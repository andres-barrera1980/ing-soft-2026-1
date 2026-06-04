# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta 08: Pruebas unitarias — CarritoService

### Estudiante
- **Nombre completo**: Federico Restrepo

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Claude |
| **Modelo específico** | Claude Sonnet 4.5 |
| **¿Por qué elegiste este LLM?** | Claude genera pruebas unitarias completas con JUnit 5 y Mockito con buena cobertura de casos borde, y entiende la lógica de negocio del código que se le proporciona sin inventar comportamientos que no están en el código. |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```text
Actúa como un desarrollador Java Senior con experiencia en pruebas unitarias con JUnit 5 y Mockito.

Contexto: En OpenLib Market tenemos la siguiente clase CarritoService con estas reglas de negocio:
- Un carrito puede tener máximo 10 ítems DIFERENTES (sin importar la cantidad de cada uno).
- Si se agrega un ítem que ya existe, se suma la cantidad al existente (no cuenta como ítem nuevo).
- Si se intenta agregar un ítem nuevo cuando ya hay 10 ítems únicos, lanza IllegalStateException.
- No se puede hacer checkout con el carrito vacío (lanza IllegalStateException).
- calcularTotal() lanza IllegalStateException si el carrito está vacío.

El código de CarritoService es el siguiente:

public class CarritoService {
    private final RepositorioLibro repositorioLibro;
    private final Map<Long, ItemCarrito> items = new HashMap<>();
    private static final int MAX_ITEMS = 10;

    public CarritoService(RepositorioLibro repositorioLibro) {
        this.repositorioLibro = repositorioLibro;
    }

    public void agregarItem(Long libroId, int cantidad) {
        if (cantidad <= 0) throw new IllegalArgumentException("Cantidad debe ser positiva");
        Libro libro = repositorioLibro.buscarPorId(libroId)
            .orElseThrow(() -> new NoSuchElementException("Libro no encontrado: " + libroId));
        if (libro.getStock() < cantidad) throw new IllegalStateException("Stock insuficiente");

        if (!items.containsKey(libroId) && items.size() >= MAX_ITEMS) {
            throw new IllegalStateException("El carrito no puede tener más de " + MAX_ITEMS + " ítems diferentes");
        }

        items.merge(libroId,
            new ItemCarrito(libro, cantidad, libro.getPrecio()),
            (existente, nuevo) -> {
                existente.setCantidad(existente.getCantidad() + cantidad);
                return existente;
            });
    }

    public void removerItem(Long libroId) {
        if (!items.containsKey(libroId)) throw new NoSuchElementException("Ítem no está en el carrito");
        items.remove(libroId);
    }

    public BigDecimal calcularTotal() {
        if (items.isEmpty()) throw new IllegalStateException("El carrito está vacío");
        return items.values().stream()
            .map(item -> item.getPrecioUnitario().multiply(BigDecimal.valueOf(item.getCantidad())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void vaciar() { items.clear(); }
    public int cantidadItems() { return items.values().stream().mapToInt(ItemCarrito::getCantidad).sum(); }
    public int cantidadItemsUnicos() { return items.size(); }

    public void validarParaCheckout() {
        if (items.isEmpty()) throw new IllegalStateException("El carrito debe tener al menos 1 ítem para checkout");
    }
}

Tarea: Genera pruebas unitarias completas con JUnit 5 y Mockito para CarritoService que cubran:
1. Casos normales de cada método.
2. Casos borde: carrito vacío, carrito con exactamente 10 ítems distintos, intento de agregar el ítem 11 (nuevo), checkout con carrito vacío.
3. Caso de agregar un ítem que ya existe en el carrito (debe sumar cantidad, no rechazar).
4. Todas las excepciones documentadas: IllegalArgumentException, NoSuchElementException, IllegalStateException.
5. Uso correcto de mocks para RepositorioLibro con Mockito (@Mock, when/thenReturn, verify).

Restricciones: Java 21, JUnit 5 (@Test, @BeforeEach, @ExtendWith(MockitoExtension.class)), Mockito. No uses PowerMock. Cada test debe tener un nombre descriptivo en español usando @DisplayName.
Formato de salida: una sola clase de prueba completa en un bloque de código Java.
```

---

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

*(1 iteración)*

---

```java
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias de CarritoService")
class CarritoServiceTest {

    @Mock
    private RepositorioLibro repositorioLibro;

    private CarritoService carrito;

    // Helpers para crear libros de prueba
    private Libro libroConStock(Long id, int stock, BigDecimal precio) {
        Libro libro = mock(Libro.class);
        when(libro.getStock()).thenReturn(stock);
        when(libro.getPrecio()).thenReturn(precio);
        return libro;
    }

    @BeforeEach
    void setUp() {
        carrito = new CarritoService(repositorioLibro);
    }

    // ─── agregarItem ────────────────────────────────────────────────────────

    @Test
    @DisplayName("agregarItem: agrega un ítem correctamente cuando hay stock suficiente")
    void agregarItem_casoNormal() {
        Libro libro = libroConStock(1L, 5, new BigDecimal("20000"));
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro));

        carrito.agregarItem(1L, 2);

        assertEquals(1, carrito.cantidadItemsUnicos());
        assertEquals(2, carrito.cantidadItems());
    }

    @Test
    @DisplayName("agregarItem: lanza IllegalArgumentException si la cantidad es cero")
    void agregarItem_cantidadCero_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> carrito.agregarItem(1L, 0));
    }

    @Test
    @DisplayName("agregarItem: lanza IllegalArgumentException si la cantidad es negativa")
    void agregarItem_cantidadNegativa_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> carrito.agregarItem(1L, -3));
    }

    @Test
    @DisplayName("agregarItem: lanza NoSuchElementException si el libro no existe")
    void agregarItem_libroNoEncontrado_lanzaExcepcion() {
        when(repositorioLibro.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> carrito.agregarItem(99L, 1));
    }

    @Test
    @DisplayName("agregarItem: lanza IllegalStateException si el stock es insuficiente")
    void agregarItem_stockInsuficiente_lanzaExcepcion() {
        Libro libro = libroConStock(1L, 2, new BigDecimal("10000"));
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro));

        assertThrows(IllegalStateException.class, () -> carrito.agregarItem(1L, 5));
    }

    @Test
    @DisplayName("agregarItem: suma la cantidad si el ítem ya existe en el carrito")
    void agregarItem_itemYaExiste_sumaCantidad() {
        Libro libro = libroConStock(1L, 10, new BigDecimal("15000"));
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro));

        carrito.agregarItem(1L, 3);
        carrito.agregarItem(1L, 4); // mismo libroId — no debe rechazar

        assertEquals(1, carrito.cantidadItemsUnicos()); // sigue siendo 1 ítem único
        assertEquals(7, carrito.cantidadItems());        // pero con cantidad 3+4=7
    }

    @Test
    @DisplayName("agregarItem: permite agregar exactamente 10 ítems diferentes")
    void agregarItem_exactamenteDiezItems_sinExcepcion() {
        for (long id = 1; id <= 10; id++) {
            Libro libro = libroConStock(id, 5, new BigDecimal("10000"));
            when(repositorioLibro.buscarPorId(id)).thenReturn(Optional.of(libro));
            carrito.agregarItem(id, 1);
        }

        assertEquals(10, carrito.cantidadItemsUnicos());
    }

    @Test
    @DisplayName("agregarItem: lanza IllegalStateException al intentar agregar el ítem 11 (nuevo)")
    void agregarItem_carritoLleno_intentoDecimoprimer_lanzaExcepcion() {
        // Llenar con 10 ítems únicos
        for (long id = 1; id <= 10; id++) {
            Libro libro = libroConStock(id, 5, new BigDecimal("10000"));
            when(repositorioLibro.buscarPorId(id)).thenReturn(Optional.of(libro));
            carrito.agregarItem(id, 1);
        }

        // Ítem 11 — nuevo libroId
        Libro libro11 = libroConStock(11L, 5, new BigDecimal("10000"));
        when(repositorioLibro.buscarPorId(11L)).thenReturn(Optional.of(libro11));

        assertThrows(IllegalStateException.class, () -> carrito.agregarItem(11L, 1));
    }

    @Test
    @DisplayName("agregarItem: con carrito lleno, sí permite actualizar cantidad de ítem existente")
    void agregarItem_carritoLleno_actualizarExistente_sinExcepcion() {
        for (long id = 1; id <= 10; id++) {
            Libro libro = libroConStock(id, 10, new BigDecimal("10000"));
            when(repositorioLibro.buscarPorId(id)).thenReturn(Optional.of(libro));
            carrito.agregarItem(id, 1);
        }

        // Agregar más cantidad al ítem 1 ya existente — no debe lanzar excepción
        Libro libro1 = libroConStock(1L, 10, new BigDecimal("10000"));
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro1));

        assertDoesNotThrow(() -> carrito.agregarItem(1L, 3));
        assertEquals(10, carrito.cantidadItemsUnicos()); // sigue siendo 10 únicos
    }

    // ─── removerItem ────────────────────────────────────────────────────────

    @Test
    @DisplayName("removerItem: elimina un ítem correctamente")
    void removerItem_casoNormal() {
        Libro libro = libroConStock(1L, 5, new BigDecimal("10000"));
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro));
        carrito.agregarItem(1L, 2);

        carrito.removerItem(1L);

        assertEquals(0, carrito.cantidadItemsUnicos());
    }

    @Test
    @DisplayName("removerItem: lanza NoSuchElementException si el ítem no está en el carrito")
    void removerItem_itemNoExiste_lanzaExcepcion() {
        assertThrows(NoSuchElementException.class, () -> carrito.removerItem(99L));
    }

    // ─── calcularTotal ──────────────────────────────────────────────────────

    @Test
    @DisplayName("calcularTotal: retorna el total correcto con múltiples ítems")
    void calcularTotal_casoNormal() {
        Libro libro1 = libroConStock(1L, 5, new BigDecimal("20000"));
        Libro libro2 = libroConStock(2L, 5, new BigDecimal("15000"));
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro1));
        when(repositorioLibro.buscarPorId(2L)).thenReturn(Optional.of(libro2));

        carrito.agregarItem(1L, 2); // 2 × 20.000 = 40.000
        carrito.agregarItem(2L, 3); // 3 × 15.000 = 45.000

        BigDecimal total = carrito.calcularTotal();

        assertEquals(new BigDecimal("85000"), total);
    }

    @Test
    @DisplayName("calcularTotal: lanza IllegalStateException si el carrito está vacío")
    void calcularTotal_carritoVacio_lanzaExcepcion() {
        assertThrows(IllegalStateException.class, () -> carrito.calcularTotal());
    }

    // ─── vaciar ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("vaciar: elimina todos los ítems del carrito")
    void vaciar_casoNormal() {
        Libro libro = libroConStock(1L, 5, new BigDecimal("10000"));
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro));
        carrito.agregarItem(1L, 1);

        carrito.vaciar();

        assertEquals(0, carrito.cantidadItemsUnicos());
        assertEquals(0, carrito.cantidadItems());
    }

    // ─── cantidadItems / cantidadItemsUnicos ────────────────────────────────

    @Test
    @DisplayName("cantidadItems: retorna la suma total de unidades, no el número de ítems únicos")
    void cantidadItems_sumaUnidades() {
        Libro libro1 = libroConStock(1L, 10, new BigDecimal("10000"));
        Libro libro2 = libroConStock(2L, 10, new BigDecimal("10000"));
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro1));
        when(repositorioLibro.buscarPorId(2L)).thenReturn(Optional.of(libro2));

        carrito.agregarItem(1L, 4);
        carrito.agregarItem(2L, 6);

        assertEquals(10, carrito.cantidadItems());
        assertEquals(2,  carrito.cantidadItemsUnicos());
    }

    // ─── validarParaCheckout ────────────────────────────────────────────────

    @Test
    @DisplayName("validarParaCheckout: lanza IllegalStateException si el carrito está vacío")
    void validarParaCheckout_carritoVacio_lanzaExcepcion() {
        assertThrows(IllegalStateException.class, () -> carrito.validarParaCheckout());
    }

    @Test
    @DisplayName("validarParaCheckout: no lanza excepción si hay al menos un ítem")
    void validarParaCheckout_carritoConItems_sinExcepcion() {
        Libro libro = libroConStock(1L, 5, new BigDecimal("10000"));
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro));
        carrito.agregarItem(1L, 1);

        assertDoesNotThrow(() -> carrito.validarParaCheckout());
    }

    // ─── verificación de mocks ──────────────────────────────────────────────

    @Test
    @DisplayName("agregarItem: llama exactamente una vez a repositorioLibro.buscarPorId")
    void agregarItem_verificaInteraccionConRepositorio() {
        Libro libro = libroConStock(1L, 5, new BigDecimal("10000"));
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro));

        carrito.agregarItem(1L, 1);

        verify(repositorioLibro, times(1)).buscarPorId(1L);
    }
}
```

---

### Análisis crítico de la respuesta
Pegar el código completo de CarritoService fue lo que más ayudó, el LLM no tuvo que asumir nada y los tests con @DisplayName en español quedaron descriptivos. El caso que más me gustó fue agregarItem_carritoLleno_actualizarExistente_sinExcepcion, un caso borde que yo no hubiera pensado: con el carrito lleno igual se puede actualizar un ítem existente.

Lo que falló: el helper libroConStock crea un mock nuevo cada vez que se llama, y en el test del carrito lleno se llama dos veces para el mismo libro, generando dos instancias distintas que podrían causar problemas si ItemCarrito compara referencias. También faltó un test básico de calcularTotal con un solo ítem, y ninguno verifica que después de vaciar() se puedan agregar ítems de nuevo.
Para la próxima le pediría que reutilice instancias en los helpers, agregue ese test de sanidad en calcularTotal e incluya verificaciones de interacción en los tests de flujo normal, no solo en uno aparte al final.