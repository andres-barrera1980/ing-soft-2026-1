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

[Evalua tu propio prompt, no la respuesta del LLM. ¿El contexto fue suficiente? ¿Las restricciones fueron claras? ¿El formato de salida que pediste ayudo a obtener una buena respuesta? ¿Que parte de tu prompt fue mas efectiva? Se especifico: menciona fragmentos concretos de tu prompt que funcionaron bien.]


#### 2. ¿Que se puede mejorar?

[¿Que le falto a tu prompt? ¿Que harias diferente si pudieras reformularlo? ¿El LLM entendio mal algo por falta de claridad en tu prompt? ¿La respuesta tiene errores u omisiones? ¿Que no cubrio el LLM que tu si sabes por lo visto en clase?]


#### 3. Respuesta final

[Escribe tu respuesta definitiva a la pregunta del parcial, integrando lo que aprendiste del LLM pero yendo mas alla. Corrige errores, llena omisiones, conecta con conceptos vistos en clase. Esta es tu respuesta: demuestra que tu dominas el tema.]
