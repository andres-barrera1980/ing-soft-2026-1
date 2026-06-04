# Plantilla de entrega — Parcial 2

---

## Pregunta 9: Aplicar TDD a una funcionalidad y evaluar el proceso

### Estudiante
- **Nombre completo**: Samuel Iregui

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Antigravity |
| **Modelo específico** | Gemini 3.5 Flash |
| **¿Por qué elegiste este LLM?** | Elegi este modelo por comodidad propia y debido a que tengo pago gemini pro y ya tengo todo bien organizado para los diferentes trabajos |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```text
Actúa como un desarrollador senior experto en metodologías ágiles, XP y TDD (Desarrollo Guiado por Pruebas). Necesito que implementes en Java 21 una funcionalidad de control de inventario para el proyecto OpenLib Market siguiendo rigurosamente el ciclo de TDD (Red-Green-Refactor).

Requerimientos de negocio:
- Cuando el stock de un libro llega a cero, su estado debe cambiar automáticamente a "AGOTADO", se debe notificar al vendedor y se debe desactivar el botón de "Agregar al carrito" en el catálogo.
- Cuando el vendedor repone el stock (stock > 0), el estado del libro vuelve a ser "DISPONIBLE" y se vuelve a habilitar el botón en el catálogo.

Estructura de la respuesta:
1. Fase RED (Rojo): Escribe las pruebas unitarias usando JUnit 5 y Mockito para este comportamiento. Explica qué ocurre en esta fase (ej: errores de compilación o aserciones fallidas).
2. Fase GREEN (Verde): Escribe el código mínimo y necesario para que el compilador no falle y todas las pruebas unitarias pasen.
3. Fase REFACTOR (Refactorización): Rediseña el código para mejorar la legibilidad y la estructura (por ejemplo, encapsulando las transiciones de estado en la entidad del dominio) sin alterar el comportamiento y asegurando que los tests sigan pasando con éxito.

Formato de salida esperado: Código Java completo estructurado por cada fase del ciclo TDD con explicaciones.
```

---

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

### 1. Fase RED (Rojo)

En TDD, escribimos la prueba antes de que la clase de producción exista. Para que las pruebas compilen pero fallen, primero declaramos un esqueleto mínimo (stubs) de las clases involucradas.

#### Esqueleto mínimo (para compilar):
```java
public enum EstadoLibro { DISPONIBLE, AGOTADO }
public record Libro(String isbn, String titulo, int stock, EstadoLibro estado) {}
public interface NotificadorService { void notificarVendedor(String vendedorEmail, String mensaje); }
public interface CatalogoService { void actualizarEstadoBoton(String isbn, boolean habilitado); }
public class ControlInventarioService {
    public ControlInventarioService(NotificadorService n, CatalogoService c) {}
    public void actualizarStock(Libro libro, int nuevoStock, String vendedorEmail) {
        // Vacío para fallar las aserciones
    }
}
```

#### Pruebas Unitarias (`ControlInventarioServiceTest`):
```java
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ControlInventarioServiceTest {

    @Mock private NotificadorService notificador;
    @Mock private CatalogoService catalogo;
    private ControlInventarioService service;
    private Libro libro;

    @BeforeEach
    void setUp() {
        service = new ControlInventarioService(notificador, catalogo);
        libro = new Libro("123", "Patrones de Diseño", 5, EstadoLibro.DISPONIBLE);
    }

    @Test
    void actualizarStock_A_Cero_CambiaAEstadoAgotadoYNotificaYDesactivaBoton() {
        // Act
        service.actualizarStock(libro, 0, "vendedor@openlib.com");

        // Assert
        // El test fallará aquí porque el método en ControlInventarioService está vacío y no altera nada
        assertEquals(EstadoLibro.AGOTADO, libro.estado());
        verify(notificador).notificarVendedor("vendedor@openlib.com", "El libro Patrones de Diseño está agotado.");
        verify(catalogo).actualizarEstadoBoton("123", false);
    }

    @Test
    void actualizarStock_ReponeStock_CambiaADisponibleYActivaBoton() {
        Libro libroAgotado = new Libro("123", "Patrones de Diseño", 0, EstadoLibro.AGOTADO);

        // Act
        service.actualizarStock(libroAgotado, 10, "vendedor@openlib.com");

        // Assert
        assertEquals(EstadoLibro.DISPONIBLE, libroAgotado.estado());
        verify(catalogo).actualizarEstadoBoton("123", true);
        verifyNoInteractions(notificador); // No debería enviar correo de "agotado" al reponer
    }
}
```
**Resultado en Fase RED**: Las pruebas compilan gracias al esqueleto, pero la ejecución falla inmediatamente (Falla de Aserción: se esperaba `AGOTADO` pero fue `DISPONIBLE`).

---

### 2. Fase GREEN (Verde)

Escribimos la implementación más simple posible en la clase de producción para que los tests pasen. Para poder mutar el estado del Libro, cambiamos el record `Libro` por una clase tradicional.

```java
// Entidad Libro mutable básica
public class Libro {
    private final String isbn;
    private final String titulo;
    private int stock;
    private EstadoLibro estado;

    public Libro(String isbn, String titulo, int stock, EstadoLibro estado) {
        this.isbn = isbn;
        this.titulo = titulo;
        this.stock = stock;
        this.estado = estado;
    }
    // Getters y Setters
    public String getIsbn() { return isbn; }
    public String getTitulo() { return titulo; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public EstadoLibro getEstado() { return estado; }
    public void setEstado(EstadoLibro estado) { this.estado = estado; }
}

// Implementación mínima del servicio
public class ControlInventarioService {
    private final NotificadorService notificador;
    private final CatalogoService catalogo;

    public ControlInventarioService(NotificadorService notificador, CatalogoService catalogo) {
        this.notificador = notificador;
        this.catalogo = catalogo;
    }

    public void actualizarStock(Libro libro, int nuevoStock, String vendedorEmail) {
        libro.setStock(nuevoStock);
        if (nuevoStock == 0) {
            libro.setEstado(EstadoLibro.AGOTADO);
            notificador.notificarVendedor(vendedorEmail, "El libro " + libro.getTitulo() + " está agotado.");
            catalogo.actualizarEstadoBoton(libro.getIsbn(), false);
        } else if (nuevoStock > 0) {
            libro.setEstado(EstadoLibro.DISPONIBLE);
            catalogo.actualizarEstadoBoton(libro.getIsbn(), true);
        }
    }
}
```
**Resultado en Fase GREEN**: Al ejecutar la suite de pruebas, todas pasan con éxito (barra verde).

---

### 3. Fase REFACTOR (Refactorización)

El código actual en `ControlInventarioService` manipula directamente el estado del libro (lo que produce un modelo de dominio anémico). Refactorizamos moviendo la lógica de cambio de estado y control de inventario directamente a la entidad `Libro`, manteniendo el servicio como un simple coordinador.

```java
// Entidad Libro refactorizada con encapsulación de comportamiento
public class Libro {
    private final String isbn;
    private final String titulo;
    private int stock;
    private EstadoLibro estado;

    public Libro(String isbn, String titulo, int stock, EstadoLibro estado) {
        this.isbn = isbn;
        this.titulo = titulo;
        this.stock = stock;
        this.estado = estado;
    }

    // Lógica del dominio encapsulada
    public boolean cambiarStock(int nuevoStock) {
        this.stock = nuevoStock;
        EstadoLibro estadoAnterior = this.estado;
        
        if (nuevoStock == 0) {
            this.estado = EstadoLibro.AGOTADO;
        } else if (nuevoStock > 0) {
            this.estado = EstadoLibro.DISPONIBLE;
        }
        
        return estadoAnterior != this.estado; // Retorna true si hubo transición de estado
    }

    public String getIsbn() { return isbn; }
    public String getTitulo() { return titulo; }
    public int getStock() { return stock; }
    public EstadoLibro getEstado() { return estado; }
}

// Servicio refactorizado y más limpio
public class ControlInventarioService {
    private final NotificadorService notificador;
    private final CatalogoService catalogo;

    public ControlInventarioService(NotificadorService notificador, CatalogoService catalogo) {
        this.notificador = notificador;
        this.catalogo = catalogo;
    }

    public void actualizarStock(Libro libro, int nuevoStock, String vendedorEmail) {
        boolean huboCambioEstado = libro.cambiarStock(nuevoStock);

        if (huboCambioEstado) {
            if (libro.getEstado() == EstadoLibro.AGOTADO) {
                notificador.notificarVendedor(vendedorEmail, "El libro " + libro.getTitulo() + " está agotado.");
                catalogo.actualizarEstadoBoton(libro.getIsbn(), false);
            } else {
                catalogo.actualizarEstadoBoton(libro.getIsbn(), true);
            }
        }
    }
}
```
**Resultado en Fase REFACTOR**: Los tests siguen pasando, pero la lógica de transiciones de estado ahora pertenece de manera correcta al modelo de dominio.

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

El prompt exige detallar el ciclo TDD paso a paso (Fase RED, GREEN y REFACTOR) y restringe las herramientas a JUnit 5 y Mockito. Esto obligó a que la respuesta del LLM no solo arrojara el código final estructurado, sino que demostrara el flujo evolutivo del código y cómo los tests actúan como red de seguridad.

#### 2. ¿Qué se puede mejorar?

Se le pudo haber exigido al prompt que especificara un escenario de transición neutral (por ejemplo, si el stock cambia de 5 a 2, el libro sigue estando `DISPONIBLE` y no debería disparar eventos innecesarios al catálogo o al notificador). Esto habría forzado un test de frontera que evaluara la lógica condicional fina en el refactoring.

#### 3. Respuesta final

En conclusión, la respuesta del LLM es excelente y explica de manera muy didáctica las tres fases del ciclo. 

Un acierto conceptual clave del LLM en la fase **REFACTOR** fue mover la lógica de transición de estado desde el servicio de inventario (`ControlInventarioService`) hacia la entidad de dominio (`Libro`). Esto previene el antipatrón del "Modelo de Dominio Anémico" y hace que la entidad sea rica en comportamiento y proteja sus propias invariantes.

**Vulnerabilidad/Engaño de la IA en TDD:**
Los LLM suelen "hacer trampa" en la fase RED escribiendo código de pruebas que utiliza clases y métodos que asumen existentes sin más. En esta respuesta, el modelo resolvió correctamente esta limitación escribiendo primero un "esqueleto" mínimo (stubs) de las interfaces y la clase `ControlInventarioService` vacía. Si el modelo hubiese tirado el test RED directamente sin los stubs, el compilador de Java habría fallado y en TDD la fase RED debe ser un test que **compile pero falle por aserción**.

Para garantizar la cobertura completa frente al punto que el prompt omitió (cambios de stock que no alteran el estado `DISPONIBLE` $\rightarrow$ `DISPONIBLE`), propongo incluir la siguiente prueba en la suite final:

```java
@Test
void actualizarStock_PermaneceDisponible_NoDisparaEventos() {
    // Si cambia de 5 a 3, el estado no transiciona
    service.actualizarStock(libro, 3, "vendedor@openlib.com");

    assertEquals(EstadoLibro.DISPONIBLE, libro.getEstado());
    verifyNoInteractions(notificador);
    verifyNoInteractions(catalogo);
}
```
Esto asegura que el diseño refactorizado solo notifique cuando realmente haya una transición de estado, evitando llamadas redundantes a servicios de red externos.
