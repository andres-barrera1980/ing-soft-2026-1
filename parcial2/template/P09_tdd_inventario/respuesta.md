# Pregunta P09: TDD — ControlInventario

### Estudiante
- **Nombre completo**: Mateo Traslaviña Moreno

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Claude |
| **Modelo específico** | Claude Sonnet 4.6 |
| **¿Por qué elegiste este LLM?** | Claude tiene excelente comprensión del ciclo Red-Green-Refactor y genera pruebas JUnit 5 correctas con Mockito. Para TDD, donde el orden importa tanto como el código en sí, Claude proporciona justificaciones técnicas precisas sobre por qué cada test debe existir antes que la implementación. |

---

### Prompt utilizado

```
Eres un ingeniero de software senior practicando TDD en el proyecto OpenLib Market, una plataforma de compra-venta de libros desarrollada en Java 21 con Spring Boot 3.x, JUnit 5 y Mockito.

Necesito implementar la clase `ControlInventario` del módulo de inventario usando TDD estricto. El requisito es:

- `agregarStock(String isbn, int cantidad)`: agrega unidades al inventario. Si el isbn no existe, lo crea con esa cantidad. Si ya existe, suma. No acepta cantidades negativas ni cero.
- `reducirStock(String isbn, int cantidad)`: reduce unidades. Falla si el stock resultante quedaría negativo. Falla si el isbn no existe.
- `obtenerStock(String isbn)`: retorna el stock actual. Retorna 0 si el isbn no existe.
- `hayDisponibilidad(String isbn, int cantidadRequerida)`: retorna true si hay suficiente stock.

Necesito que:
1. Apliques TDD estricto: primero los tests (fase RED), luego la implementación mínima (fase GREEN), luego el refactoring.
2. Para cada test, expliques POR QUÉ ese test debe existir antes de la implementación.
3. Generes todos los tests con JUnit 5 (`@Test`, `assertThrows`, `assertEquals`, etc.) — NO uses `@Test(expected=...)` que es JUnit 4.
4. Generes la implementación final de `ControlInventario` en Java 21.
5. Cubras casos borde: isbn null, cantidad cero, cantidad negativa, stock insuficiente.

Restricciones:
- Java 21, Spring Boot 3.x, JUnit 5, Mockito si aplica
- La clase ControlInventario NO debe tener dependencias externas (es lógica pura de dominio)
- No uses anotaciones de Spring en la clase de dominio

Formato de salida: markdown con secciones "Fase RED (Tests primero)", "Fase GREEN (Implementación mínima)", "Fase REFACTOR (Código final limpio)", "Tests completos".
```

---

### Respuesta del LLM

## Fase RED (Tests primero)

Los tests se escriben ANTES que el código. En este punto, `ControlInventario` ni siquiera existe — y eso está bien. Cada test documenta un comportamiento requerido.

### ¿Por qué escribir los tests primero?

Escribir el test primero te fuerza a pensar en la API desde el punto de vista del consumidor, no del implementador. Si el test es difícil de escribir, es señal de que el diseño está mal.

```java
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ControlInventarioTest {

    private ControlInventario inventario;

    @BeforeEach
    void setUp() {
        inventario = new ControlInventario();
    }

    // --- agregarStock ---

    @Test
    @DisplayName("Agregar stock en isbn nuevo debe crear entrada con esa cantidad")
    void agregarStock_isbnNuevo_creaEntrada() {
        inventario.agregarStock("978-0-13-468599-1", 10);
        assertEquals(10, inventario.obtenerStock("978-0-13-468599-1"));
    }

    @Test
    @DisplayName("Agregar stock en isbn existente debe sumar al stock actual")
    void agregarStock_isbnExistente_acumula() {
        inventario.agregarStock("978-0-13-468599-1", 10);
        inventario.agregarStock("978-0-13-468599-1", 5);
        assertEquals(15, inventario.obtenerStock("978-0-13-468599-1"));
    }

    @Test
    @DisplayName("Agregar cantidad cero debe lanzar IllegalArgumentException")
    void agregarStock_cantidadCero_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
            () -> inventario.agregarStock("978-0-13-468599-1", 0));
    }

   
