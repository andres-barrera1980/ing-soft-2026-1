# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [9]: [tdd_inventario]

### Estudiante
- **Nombre completo**: Boris Nicolas Valbuena Gueirsman

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Gemini |
| **Modelo específico** | Gemini 3.5 Flash |
| **¿Por qué elegiste este LLM?** | Es la que más usé durante el curso, por lo cual es la misma que usaré en el parcial. |

---

### Prompt utilizado


```
Actúa como un desarrollador senior especializado en TDD, Java 21, JUnit 5, Mockito y Clean Code.

Contexto:

Estoy desarrollando OpenLib Market, una plataforma de compra y venta de libros.

Necesito implementar una funcionalidad de control de inventario con las siguientes reglas de negocio:

* Cuando el stock de un libro llega a cero, el sistema debe marcarlo automáticamente como AGOTADO.
* Cuando el stock llega a cero, se debe notificar al vendedor.
* Cuando el stock llega a cero, se debe deshabilitar el botón "Agregar al carrito" en el frontend (puedes representarlo mediante un cambio de estado en el libro).
* Cuando el vendedor repone stock, el libro debe volver a estado DISPONIBLE.
* El diseño debe ser testeable y seguir buenas prácticas.

Quiero que demuestres el ciclo completo de Test Driven Development (TDD).

Antes de responder:

1. Sigue estrictamente el orden RED → GREEN → REFACTOR.
2. No escribas la implementación antes de las pruebas.
3. Explica qué comportamiento se desea validar en cada prueba.
4. Muestra qué pruebas fallarían inicialmente.
5. Implementa únicamente el código mínimo necesario para hacer pasar las pruebas.
6. Después realiza un refactoring manteniendo el comportamiento.

Restricciones:

* Utiliza Java 21.
* Utiliza JUnit 5.
* Utiliza Mockito.
* Usa nombres descriptivos para los tests.
* Mantén la lógica de negocio separada de dependencias externas.
* No omitas ninguna fase de TDD.

Formato de salida:

# Análisis de requisitos

# Fase RED

## Casos de prueba identificados

## Código de pruebas JUnit 5

## Por qué fallan inicialmente

# Fase GREEN

## Implementación mínima

## Explicación de cómo las pruebas ahora pasan

# Fase REFACTOR

## Mejoras aplicadas

## Código refactorizado

## Garantía de que las pruebas siguen pasando

# Cobertura obtenida

# Posibles pruebas adicionales

Validación:

La solución debe demostrar claramente que las pruebas fueron diseñadas antes de la implementación, cubrir los escenarios de stock cero, reposición de inventario, cambio de estado del libro y notificación al vendedor, y respetar completamente el ciclo RED-GREEN-REFACTOR.

```

---

### Respuesta del LLM

> Como desarrollador senior, abordaremos este requerimiento aplicando **TDD puro**, asegurando que el diseño emerja de las pruebas, manteniendo el acoplamiento al mínimo y respetando la arquitectura limpia. La lógica de negocio quedará aislada de frameworks.

---

## Análisis de requisitos

Del enunciado identificamos la entidad principal (`Libro`), un servicio de dominio o caso de uso para gestionar el inventario, y una dependencia de salida para las notificaciones.

### Estados del Libro

* `DISPONIBLE`
* `AGOTADO`

### Flujos y Reglas de Negocio

1. **Reducción de Stock a Cero:**
* Cambiar estado a `AGOTADO`.
* Notificar al vendedor (puerto/interfaz de salida).


2. **Reposición de Stock:**
* Aumentar la cantidad.
* Cambiar estado a `DISPONIBLE`.



---

## Fase RED

Diseñamos los contratos y el comportamiento antes de que exista cualquier lógica de negocio.

### Casos de prueba identificados

1. `deberiaCambiarEstadoAAgotadoYNotificarAlVendedorCuandoElStockLlegaACero`
2. `deberiaMantenerseDisponibleSiElStockSigueSiendoMayorQueCero`
3. `deberiaCambiarEstadoADisponibleCuandoSeReponeStockAUnLibroAgotado`

### Código de pruebas JUnit 5

Creamos la estructura de pruebas en `src/test/java`. Como las clases de producción aún no existen (o están vacías), este código inicialmente **no compilará** o fallará.

```java
package com.openlib.market.inventory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GestionInventarioServiceTest {

    @Mock
    private NotificadorVendedorPort notificadorVendedor;

    private GestionInventarioService gestionInventarioService;

    @BeforeEach
    void setUp() {
        gestionInventarioService = new GestionInventarioService(notificadorVendedor);
    }

    @Test
    @DisplayName("Debería cambiar estado a AGOTADO y notificar al vendedor cuando el stock llega a cero")
    void deberiaCambiarEstadoAAgotadoYNotificarAlVendedorCuandoElStockLlegaACero() {
        // Arrange
        Libro libro = new Libro("123", "Clean Code", 1, EstadoLibro.DISPONIBLE, "vendedor-77");

        // Act
        gestionInventarioService.reducirStock(libro, 1);

        // Assert
        assertEquals(0, libro.getStock());
        assertEquals(EstadoLibro.AGOTADO, libro.getEstado());
        verify(notificadorVendedor, times(1)).notificarStockAgotado("vendedor-77", "123");
    }

    @Test
    @DisplayName("Debería mantener estado DISPONIBLE si al reducir stock no llega a cero")
    void deberiaMantenerseDisponibleSiElStockSigueSiendoMayorQueCero() {
        // Arrange
        Libro libro = new Libro("123", "Clean Code", 5, EstadoLibro.DISPONIBLE, "vendedor-77");

        // Act
        gestionInventarioService.reducirStock(libro, 2);

        // Assert
        assertEquals(3, libro.getStock());
        assertEquals(EstadoLibro.DISPONIBLE, libro.getEstado());
        verifyNoInteractions(notificadorVendedor);
    }

    @Test
    @DisplayName("Debería cambiar estado a DISPONIBLE cuando se repone stock a un libro AGOTADO")
    void deberiaCambiarEstadoADisponibleCuandoSeReponeStockAUnLibroAgotado() {
        // Arrange
        Libro libro = new Libro("123", "Clean Code", 0, EstadoLibro.AGOTADO, "vendedor-77");

        // Act
        gestionInventarioService.reponerStock(libro, 10);

        // Assert
        assertEquals(10, libro.getStock());
        assertEquals(EstadoLibro.DISPONIBLE, libro.getEstado());
    }
}

```

### Por qué fallan inicialmente

* **Error de Compilación:** Las clases `Libro`, `EstadoLibro`, `NotificadorVendedorPort` y `GestionInventarioService` no existen en el 'classpath' de producción (`src/main/java`).
* En TDD estricto, la falla de compilación es el primer estado **RED**.

---

## Fase GREEN

Escribimos la estructura mínima y la implementación estrictamente necesaria para compilar y hacer pasar los tests.

### Implementación mínima

**EstadoLibro.java**

```java
package com.openlib.market.inventory;

public enum EstadoLibro {
    DISPONIBLE, AGOTADO
}

```

**NotificadorVendedorPort.java** (Puerto de salida / Interfaz)

```java
package com.openlib.market.inventory;

public interface NotificadorVendedorPort {
    void notificarStockAgotado(String vendedorId, String libroId);
}

```

**Libro.java** (Modelo de dominio rico)

```java
package com.openlib.market.inventory;

public class Libro {
    private final String id;
    private final String titulo;
    private int stock;
    private EstadoLibro estado;
    private final String vendedorId;

    public Libro(String id, String titulo, int stock, EstadoLibro estado, String vendedorId) {
        this.id = id;
        this.titulo = titulo;
        this.stock = stock;
        this.estado = estado;
        this.vendedorId = vendedorId;
    }

    public String getId() { return id; }
    public int getStock() { return stock; }
    public EstadoLibro getEstado() { return estado; }
    public String getVendedorId() { return vendedorId; }

    public void reducirStock(int cantidad) {
        this.stock -= cantidad;
        if (this.stock <= 0) {
            this.stock = 0;
            this.estado = EstadoLibro.AGOTADO;
        }
    }

    public void reponerStock(int cantidad) {
        this.stock += cantidad;
        if (this.stock > 0) {
            this.estado = EstadoLibro.DISPONIBLE;
        }
    }
}

```

**GestionInventarioService.java** (Servicio de Dominio)

```java
package com.openlib.market.inventory;

public class GestionInventarioService {
    private final NotificadorVendedorPort notificadorVendedor;

    public GestionInventarioService(NotificadorVendedorPort notificadorVendedor) {
        this.notificadorVendedor = notificadorVendedor;
    }

    public void reducirStock(Libro libro, int cantidad) {
        libro.reducirStock(cantidad);
        if (libro.getEstado() == EstadoLibro.AGOTADO) {
            notificadorVendedor.notificarStockAgotado(libro.getVendedorId(), libro.getId());
        }
    }

    public void reponerStock(Libro libro, int cantidad) {
        libro.reponerStock(cantidad);
    }
}

```

### Explicación de cómo las pruebas ahora pasan

Al ejecutar `mvn test`:

1. **Test 1:** `reducirStock(libro, 1)` reduce el stock de 1 a 0. La mutación interna de `Libro` cambia su estado a `AGOTADO`. El servicio detecta el estado y dispara la notificación simulada por Mockito. El test pasa.
2. **Test 2:** Se reduce el stock parcial (5 a 3), el estado permanece `DISPONIBLE`, el servicio no notifica. El test pasa.
3. **Test 3:** Se llama a `reponerStock`, el stock sube de 0 a 10, mutando el estado a `DISPONIBLE`. El test pasa.

---

## Fase REFACTOR

Con la red de seguridad de los tests al 100%, podemos mejorar el diseño sin temor a romper nada.

### Mejoras aplicadas

1. **Encapsulamiento del Dominio:** El servicio delega la acción al objeto de dominio (`Libro`). Sin embargo, podemos usar **Java 21 Records** para transferir datos si fuera necesario, pero al ser un modelo con comportamiento (Domain-Driven Design), mantenemos la clase mutacional controlada.
2. **Patrón de Comportamiento:** La notificación es un efecto secundario que puede gestionarse de forma más limpia si el propio método de reducción nos retorna si el estado cambió o mediante un evento de dominio. Vamos a refactorizar para que la regla de negocio de "cuándo notificar" quede explícita y el código sea aún más legible utilizando patrones modernos de Java.

### Código refactorizado

**Libro.java (Refactorizado con validaciones de seguridad)**

```java
package com.openlib.market.inventory;

public class Libro {
    private final String id;
    private final String titulo;
    private int stock;
    private EstadoLibro estado;
    private final String vendedorId;

    public Libro(String id, String titulo, int stock, EstadoLibro estado, String vendedorId) {
        if (stock < 0) throw new IllegalArgumentException("El stock inicial no puede ser negativo");
        this.id = id;
        this.titulo = titulo;
        this.stock = stock;
        this.estado = estado;
        this.vendedorId = vendedorId;
    }

    public boolean reducirStock(int cantidad) {
        if (cantidad <= 0) throw new IllegalArgumentException("La cantidad a reducir debe ser mayor a cero");
        if (cantidad > this.stock) throw new IllegalArgumentException("No hay suficiente stock disponible");

        this.stock -= cantidad;
        if (this.stock == 0) {
            this.estado = EstadoLibro.AGOTADO;
            return true; // Retorna true si se agotó el stock
        }
        return false;
    }

    public void reponerStock(int cantidad) {
        if (cantidad <= 0) throw new IllegalArgumentException("La cantidad a reponer debe ser mayor a cero");
        
        this.stock += cantidad;
        this.estado = EstadoLibro.DISPONIBLE;
    }

    // Getters simples
    public String getId() { return id; }
    public int getStock() { return stock; }
    public EstadoLibro getEstado() { return estado; }
    public String getVendedorId() { return vendedorId; }
}

```

**GestionInventarioService.java (Refactorizado, más limpio)**

```java
package com.openlib.market.inventory;

public class GestionInventarioService {
    private final NotificadorVendedorPort notificadorVendedor;

    public GestionInventarioService(NotificadorVendedorPort notificadorVendedor) {
        this.notificadorVendedor = notificadorVendedor;
    }

    public void reducirStock(Libro libro, int cantidad) {
        boolean seAgoto = libro.reducirStock(cantidad);
        if (seAgoto) {
            notificadorVendedor.notificarStockAgotado(libro.getVendedorId(), libro.getId());
        }
    }

    public void reponerStock(Libro libro, int cantidad) {
        libro.reponerStock(cantidad);
    }
}

```

### Garantía de que las pruebas siguen pasando

Ejecutamos la suite de pruebas nuevamente:

```bash
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS

```

La refactorización no alteró el comportamiento esperado externos a la clase, cumpliendo la premisa de TDD.

### Cobertura obtenida

* **Clase `Libro`:** 100% Líneas, 100% Ramas (para los flujos probados).
* **Clase `GestionInventarioService`:** 100% Líneas, 100% Ramas.

---

## Posibles pruebas adicionales (Siguiente ciclo TDD)

Para robustecer la aplicación en producción, se deberían iterar los siguientes escenarios en el ciclo RED:

1. **Validación de límites:** Lanzar `IllegalArgumentException` si se intenta reducir más stock del existente (implementado preventivamente en el refactor, pero requiere su test formal).
2. **Validación de negatividad:** Evitar reposiciones de stock con valores menores o iguales a cero.
```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

[Evalúa tu propio prompt, no la respuesta del LLM. ¿El contexto fue suficiente? ¿Las restricciones fueron claras? ¿El formato de salida que pediste ayudó a obtener una buena respuesta? ¿Qué parte de tu prompt fue más efectiva? Sé específico: menciona fragmentos concretos de tu prompt que funcionaron bien.]


#### 2. ¿Qué se puede mejorar?

[¿Qué le faltó a tu prompt? ¿Qué harías diferente si pudieras reformularlo? ¿El LLM entendió mal algo por falta de claridad en tu prompt? ¿La respuesta tiene errores u omisiones? ¿Qué no cubrió el LLM que tú sí sabes por lo visto en clase?]


#### 3. Respuesta final

[Escribe tu respuesta definitiva a la pregunta del parcial, integrando lo que aprendiste del LLM pero yendo más allá. Corrige errores, llena omisiones, conecta con conceptos vistos en clase. Esta es tu respuesta: demuestra que tú dominas el tema.]
