# Plantilla de entrega — Parcial 2

---

## Pregunta P09: TDD — Control de Inventario

### Estudiante
- **Nombre completo**: Ivan Wright

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Gemini |
| **Modelo especifico** | Gemini 3.1 Pro |
| **¿Por que elegiste este LLM?** | Porque al pedirle ejemplos de ciclos TDD (Test-Driven Development) suele organizar muy bien las tres fases del desarrollo de manera didactica, aunque a veces tiende a saltarse pasos que luego debo corregir. |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```text
hola gemini, actua como un desarrollador senior experto en java y TDD.

[CONTEXTO]
En OpenLib Market (Java 21), necesito crear un `ControlInventarioService`. La logica es: si el stock de un libro llega a cero, se debe marcar su estado automaticamente como "AGOTADO", avisarle al vendedor por correo y asegurar que no se pueda agregar al carrito. Si despues el vendedor repone el stock, el estado del libro debe cambiar a "DISPONIBLE".

[PROBLEMA]
Quiero que apliques TDD (Test-Driven Development) paso a paso para construir esta funcionalidad. Tienes que mostrarme exactamente el ciclo Red-Green-Refactor.

[RESTRICCIONES]
- Primero tienes que darme el codigo de la prueba en rojo (que falle) usando JUnit 5 y Mockito.
- Luego dame el codigo en verde (la implementacion minima y fea para que la prueba pase).
- Finalmente dame el codigo refactorizado (mas limpio) sin romper las pruebas.

[FORMATO]
Entregame todo en markdown. Separa claramente las fases "1. Red", "2. Green", y "3. Refactor" con bloques de codigo Java para cada una.

[VALIDACION]
Sabre que aplicaste TDD de verdad si la prueba inicial falla por no tener la logica implementada, y si el refactoring mejora el diseño en lugar de simplemente escribir el codigo final desde el principio.
```

---

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```markdown
¡Hola! Entendido. Vamos a aplicar la estricta disciplina de **TDD (Test-Driven Development)** para construir tu `ControlInventarioService` siguiendo el ciclo Red-Green-Refactor.

### 1. Fase RED (Escribir la prueba que falla)

Primero, creamos la clase de prueba para la lógica principal: agotar el inventario. En TDD, diseñamos la API desde la prueba antes de que exista el código de producción.

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ControlInventarioServiceTest {

    @Mock
    private NotificadorVendedor notificador;

    @Mock
    private LibroRepository repository;

    @InjectMocks
    private ControlInventarioService service;

    @Test
    void reducirStock_CuandoLlegaAZero_DebeMarcarAgotadoYNotificar() {
        // Arrange
        Libro libro = new Libro(1L, "Java TDD", 1, "DISPONIBLE"); // Stock inicial de 1
        when(repository.findById(1L)).thenReturn(libro);

        // Act
        service.reducirStock(1L, 1);

        // Assert
        assertEquals("AGOTADO", libro.getEstado());
        assertEquals(0, libro.getStock());
        verify(notificador, times(1)).enviarAlertaAgotado(libro.getVendedorId(), libro.getTitulo());
        verify(repository, times(1)).save(libro);
    }
}
```
*Si corremos esto en nuestro IDE, la prueba **falla (RED)** porque la clase `ControlInventarioService` y su método `reducirStock` aún no están implementados.*

### 2. Fase GREEN (Implementación mínima para pasar)

Ahora escribimos la implementación directa para que la barra se ponga verde lo antes posible.

```java
public class ControlInventarioService {
    
    private LibroRepository repository;
    private NotificadorVendedor notificador;

    public ControlInventarioService(LibroRepository repository, NotificadorVendedor notificador) {
        this.repository = repository;
        this.notificador = notificador;
    }

    public void reducirStock(Long libroId, int cantidad) {
        Libro libro = repository.findById(libroId);
        libro.setStock(libro.getStock() - cantidad);
        
        if (libro.getStock() == 0) {
            libro.setEstado("AGOTADO");
            notificador.enviarAlertaAgotado(libro.getVendedorId(), libro.getTitulo());
        }
        
        repository.save(libro);
    }
}
```
*Si corremos la prueba ahora, **pasa exitosamente (GREEN)**.*

### 3. Fase REFACTOR (Mejorar el diseño)

La implementación ya funciona, pero en la fase de Refactor podemos mejorar la legibilidad y encapsulamiento. Extraeremos la lógica de verificación de agotado a un método privado y haremos las dependencias `final`.

```java
public class ControlInventarioService {
    
    private final LibroRepository repository;
    private final NotificadorVendedor notificador;

    public ControlInventarioService(LibroRepository repository, NotificadorVendedor notificador) {
        this.repository = repository;
        this.notificador = notificador;
    }

    public void reducirStock(Long libroId, int cantidad) {
        Libro libro = repository.findById(libroId);
        libro.setStock(libro.getStock() - cantidad);
        
        procesarEstadoInventario(libro);
        
        repository.save(libro);
    }

    // Refactoring: Extracción a método privado para mayor claridad y modularidad
    private void procesarEstadoInventario(Libro libro) {
        if (libro.getStock() == 0) {
            libro.setEstado("AGOTADO");
            notificador.enviarAlertaAgotado(libro.getVendedorId(), libro.getTitulo());
        }
    }
}
```
*Al volver a correr las pruebas, siguen pasando y no hemos roto nada. Hemos completado el ciclo TDD con éxito.*
```

---

### Analisis critico de la respuesta

#### 1. ¿Que hizo bien el prompt?

El prompt fue directo al pedirle que mostrara las fases divididas (Red, Green, Refactor). Exigirle que me diera el codigo en tres bloques de markdown separados fue una muy buena idea porque me permitio analizar que es lo que la IA considera realmente "TDD". Tambien pedirle explicitamente que usara JUnit 5 y Mockito garantizo que el codigo generado estuviera listo para ejecutarse sin dependencias obsoletas.


#### 2. ¿Que se puede mejorar?

El LLM entendio mal el concepto de la fase Green. *Como sabemos por lo discutido en clase, la fase Green de TDD consiste en escribir el codigo mas tonto y minimo posible (como un valor quemado) solo para que la barra pase a verde*. El LLM no hizo eso, sino que de inmediato escribio toda la logica final en la fase Green. Ademas, omitio por completo probar el segundo escenario que le pedi en el prompt: que pasaba cuando el vendedor reponia el stock y debia volver a estado DISPONIBLE.


#### 3. Respuesta final

Analizando a fondo, el LLM no esta aplicando TDD genuinamente, sino que esta simulando TDD. Escribio una prueba, y luego de inmediato salto a escribir la implementacion final completa con la logica de base de datos y correos. *Para verificar que alguien o algo aplica TDD genuinamente, uno debe ver pasos intermedios diminutos (baby steps) donde la implementacion apenas hace lo minimo necesario para pasar la prueba actual antes de siquiera pensar en la logica final*.

Adicionalmente, las pruebas generadas por la IA no cubren todos los escenarios relevantes. Solo hizo la parte de agotar inventario, pero falto la parte de reponer. Aca incluyo la prueba unitaria que el LLM omitio para lograr la cobertura completa:

```java
    @Test
    void reponerStock_CuandoLlegaInventario_DebeMarcarDisponible() {
        // Arrange
        Libro libro = new Libro(1L, "Java TDD", 0, "AGOTADO"); 
        when(repository.findById(1L)).thenReturn(libro);

        // Act
        service.reponerStock(1L, 10);

        // Assert
        assertEquals("DISPONIBLE", libro.getEstado());
        assertEquals(10, libro.getStock());
        verify(repository, times(1)).save(libro); // Verifica que se guarda el nuevo estado
    }
```

En cuanto al refactoring propuesto (fase 3), la extraccion de la logica al metodo privado `procesarEstadoInventario` que hizo la IA fue bastante acertada. Cumplio con el objetivo principal del ciclo TDD: mejorar la legibilidad y encapsulamiento del codigo existente sin alterar ni romper el comportamiento que ya garantizaban las pruebas.
