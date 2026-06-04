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

    @Test
    @DisplayName("Agregar cantidad negativa debe lanzar IllegalArgumentException")
    void agregarStock_cantidadNegativa_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
            () -> inventario.agregarStock("978-0-13-468599-1", -5));
    }

    @Test
    @DisplayName("Agregar stock con isbn null debe lanzar IllegalArgumentException")
    void agregarStock_isbnNull_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
            () -> inventario.agregarStock(null, 10));
    }

    // --- reducirStock ---

    @Test
    @DisplayName("Reducir stock con suficiente cantidad debe actualizar el stock")
    void reducirStock_suficienteStock_actualiza() {
        inventario.agregarStock("978-0-13-468599-1", 10);
        inventario.reducirStock("978-0-13-468599-1", 3);
        assertEquals(7, inventario.obtenerStock("978-0-13-468599-1"));
    }

    @Test
    @DisplayName("Reducir stock dejando en cero debe ser válido")
    void reducirStock_hastaAgotarStock_dejaEnCero() {
        inventario.agregarStock("978-0-13-468599-1", 5);
        inventario.reducirStock("978-0-13-468599-1", 5);
        assertEquals(0, inventario.obtenerStock("978-0-13-468599-1"));
    }

    @Test
    @DisplayName("Reducir más stock del disponible debe lanzar StockInsuficienteException")
    void reducirStock_stockInsuficiente_lanzaExcepcion() {
        inventario.agregarStock("978-0-13-468599-1", 3);
        assertThrows(StockInsuficienteException.class,
            () -> inventario.reducirStock("978-0-13-468599-1", 5));
    }

    @Test
    @DisplayName("Reducir stock de isbn inexistente debe lanzar IsbnNoEncontradoException")
    void reducirStock_isbnInexistente_lanzaExcepcion() {
        assertThrows(IsbnNoEncontradoException.class,
            () -> inventario.reducirStock("isbn-inexistente", 1));
    }

    @Test
    @DisplayName("Reducir cantidad cero debe lanzar IllegalArgumentException")
    void reducirStock_cantidadCero_lanzaExcepcion() {
        inventario.agregarStock("978-0-13-468599-1", 10);
        assertThrows(IllegalArgumentException.class,
            () -> inventario.reducirStock("978-0-13-468599-1", 0));
    }

    // --- obtenerStock ---

    @Test
    @DisplayName("Obtener stock de isbn inexistente debe retornar cero")
    void obtenerStock_isbnInexistente_retornaCero() {
        assertEquals(0, inventario.obtenerStock("isbn-que-no-existe"));
    }

    // --- hayDisponibilidad ---

    @Test
    @DisplayName("Hay disponibilidad cuando stock es mayor o igual a cantidad requerida")
    void hayDisponibilidad_stockSuficiente_retornaTrue() {
        inventario.agregarStock("978-0-13-468599-1", 10);
        assertTrue(inventario.hayDisponibilidad("978-0-13-468599-1", 10));
        assertTrue(inventario.hayDisponibilidad("978-0-13-468599-1", 5));
    }

    @Test
    @DisplayName("No hay disponibilidad cuando stock es insuficiente")
    void hayDisponibilidad_stockInsuficiente_retornaFalse() {
        inventario.agregarStock("978-0-13-468599-1", 3);
        assertFalse(inventario.hayDisponibilidad("978-0-13-468599-1", 5));
    }

    @Test
    @DisplayName("No hay disponibilidad para isbn inexistente")
    void hayDisponibilidad_isbnInexistente_retornaFalse() {
        assertFalse(inventario.hayDisponibilidad("isbn-inexistente", 1));
    }
}
```

## Fase GREEN (Implementación mínima)

La implementación mínima que hace pasar todos los tests. Sin optimizaciones, sin elegancia extra — solo lo suficiente para que el compilador y los tests estén felices.

```java
import java.util.HashMap;
import java.util.Map;

public class ControlInventario {

    private final Map<String, Integer> stock = new HashMap<>();

    public void agregarStock(String isbn, int cantidad) {
        if (isbn == null) throw new IllegalArgumentException("ISBN no puede ser null");
        if (cantidad <= 0) throw new IllegalArgumentException("Cantidad debe ser mayor a cero");
        stock.merge(isbn, cantidad, Integer::sum);
    }

    public void reducirStock(String isbn, int cantidad) {
        if (cantidad <= 0) throw new IllegalArgumentException("Cantidad debe ser mayor a cero");
        if (!stock.containsKey(isbn)) throw new IsbnNoEncontradoException(isbn);
        int stockActual = stock.get(isbn);
        if (stockActual < cantidad) throw new StockInsuficienteException(isbn, stockActual, cantidad);
        stock.put(isbn, stockActual - cantidad);
    }

    public int obtenerStock(String isbn) {
        return stock.getOrDefault(isbn, 0);
    }

    public boolean hayDisponibilidad(String isbn, int cantidadRequerida) {
        return obtenerStock(isbn) >= cantidadRequerida;
    }
}
```

## Fase REFACTOR (Código final limpio)

```java
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Gestiona el inventario de libros en OpenLib Market.
 * Clase de dominio puro — sin dependencias de infraestructura.
 */
public class ControlInventario {

    private final Map<String, Integer> stock = new HashMap<>();

    public void agregarStock(String isbn, int cantidad) {
        validarIsbn(isbn);
        validarCantidadPositiva(cantidad);
        stock.merge(isbn, cantidad, Integer::sum);
    }

    public void reducirStock(String isbn, int cantidad) {
        validarCantidadPositiva(cantidad);
        int stockActual = obtenerStockOFallar(isbn);
        if (stockActual < cantidad) {
            throw new StockInsuficienteException(isbn, stockActual, cantidad);
        }
        stock.put(isbn, stockActual - cantidad);
    }

    public int obtenerStock(String isbn) {
        return stock.getOrDefault(isbn, 0);
    }

    public boolean hayDisponibilidad(String isbn, int cantidadRequerida) {
        return obtenerStock(isbn) >= cantidadRequerida;
    }

    // --- private helpers ---

    private void validarIsbn(String isbn) {
        if (Objects.isNull(isbn)) {
            throw new IllegalArgumentException("ISBN no puede ser null");
        }
    }

    private void validarCantidadPositiva(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("Cantidad debe ser mayor a cero, se recibió: " + cantidad);
        }
    }

    private int obtenerStockOFallar(String isbn) {
        if (!stock.containsKey(isbn)) {
            throw new IsbnNoEncontradoException(isbn);
        }
        return stock.get(isbn);
    }
}
```

## Excepciones de dominio

```java
public class StockInsuficienteException extends RuntimeException {
    public StockInsuficienteException(String isbn, int disponible, int requerido) {
        super(String.format("Stock insuficiente para ISBN '%s': disponible=%d, requerido=%d",
            isbn, disponible, requerido));
    }
}

public class IsbnNoEncontradoException extends RuntimeException {
    public IsbnNoEncontradoException(String isbn) {
        super("ISBN no encontrado en inventario: " + isbn);
    }
}
```

---

### Analisis crítico de la respuesta

#### 1. ¿Que hizo bien el prompt?

El prompt fue efectivo en varios aspectos. Primero, especificar **"TDD estricto"** y pedir la explicación del "por qué de cada test antes de la implementación" obligó al LLM a razonar en el orden correcto (RED → GREEN → REFACTOR) en lugar de generar código y tests al mismo tiempo, que es el error más común. Segundo, indicar explícitamente **"NO uses `@Test(expected=...)` que es JUnit 4"** evitó que el LLM generara código desactualizado — sin esta restricción, los LLMs frecuentemente mezclan sintaxis de JUnit 4 y 5. Tercero, pedir las **excepciones de dominio** específicas (`StockInsuficienteException`, `IsbnNoEncontradoException`) en lugar de excepciones genéricas resultó en un diseño más expresivo.

#### 2. ¿Qué se puede mejorar?

El prompt no solicitó **pruebas de concurrencia**: si dos hilos intentan reducir el mismo stock simultáneamente, el `HashMap` no es thread-safe. En un sistema real de inventario esto es crítico. El LLM tampoco fue cuestionado sobre si `hayDisponibilidad` con `cantidadRequerida <= 0` debería lanzar excepción o retornar `true` — lo resolvió silenciosamente retornando `true` (porque `0 >= 0`), lo cual puede ser incorrecto semánticamente. Adicionalmente, el prompt pudo haber pedido **tests de integración** con un repositorio mockeado, para demostrar cómo `ControlInventario` se usaría en el contexto de Spring.

#### 3. Respuesta final

**TDD aplicado correctamente tiene tres fases diferenciadas:**

En la **fase RED**, se escribe un test que falla porque la clase no existe todavía. La clave de TDD no es "tener tests", sino que los tests definan el comportamiento esperado *antes* de que exista código. Cada test del `ControlInventarioTest` documenta un comportamiento de negocio: `agregarStock_cantidadCero_lanzaExcepcion` expresa la regla "no se puede agregar cero libros al inventario", independientemente de cómo se implemente.

En la **fase GREEN**, se escribe *el mínimo código posible* para hacer pasar los tests. Usar `Map.merge(isbn, cantidad, Integer::sum)` es la forma idiomática en Java 21 para "suma si existe, crea si no". No se agrega lógica extra que no esté cubierta por un test.

En la **fase REFACTOR**, se mejora el código sin cambiar su comportamiento. La extracción de métodos privados `validarIsbn`, `validarCantidadPositiva` y `obtenerStockOFallar` elimina duplicación y hace el código más legible — y los tests pasan exactamente igual porque refactoring no cambia comportamiento observable.

Un punto que el LLM no mencionó: en TDD también existe el **test de triangulación**. Para `agregarStock_isbnExistente_acumula`, se llama dos veces con diferentes valores para confirmar que realmente acumula y no simplemente sobreescribe — esto es triangulación y es la razón por la que necesitamos más de un test por comportamiento en casos no triviales.
