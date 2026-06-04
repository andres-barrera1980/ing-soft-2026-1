# Pregunta P08: Pruebas unitarias para CarritoService

### Estudiante
- **Nombre completo**: Juan Camilo Gomez

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Claude |
| **Modelo específico** | Claude Sonnet 4.6 |
| **¿Por qué elegiste este LLM?** | La generación de pruebas unitarias completas con JUnit 5 y Mockito es una tarea donde Claude produce código Java correcto 

---

### Prompt utilizado

```
Soy estudiante de Ingeniería de Software en la Pontificia Universidad Javeriana.
Proyecto: OpenLib Market. Tengo este servicio de carrito de compras con las siguientes 
reglas de negocio:
- Un carrito puede tener MÍNIMO 1 ítem para proceder al checkout
- MÁXIMO 10 ítems DIFERENTES (un ítem con cantidad 5 cuenta como 1 ítem único)
- Si se intenta agregar un ítem que excede el máximo, se lanza IllegalStateException
- Si se intenta hacer checkout con carrito vacío, se lanza IllegalStateException

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

Genera pruebas unitarias COMPLETAS con JUnit 5 y Mockito cubriendo:
1. Casos normales: agregar un ítem, agregar el mismo ítem dos veces (debe acumular cantidad), remover ítem, calcular total, vaciar carrito
2. Casos borde: carrito vacío, carrito con exactamente 10 ítems, intentar agregar el ítem 11, checkout con carrito vacío
3. Excepciones: cantidad negativa, libro no encontrado, stock insuficiente, ítem no en carrito al remover
4. Mocks: usa Mockito correctamente para RepositorioLibro

Restricciones:
- JUnit 5 con @Test, @BeforeEach, assertThrows
- Mockito con mock(), when(), verify()
- Nombres de pruebas descriptivos en español
- Un @Test por caso de prueba
- Responde en markdown con un único bloque de código Java
```

---

### Respuesta del LLM

```java
import org.junit.jupiter.api.*;
import org.mockito.*;
import java.math.BigDecimal;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CarritoServiceTest {

    private RepositorioLibro repositorioLibro;
    private CarritoService carrito;

    @BeforeEach
    void setUp() {
        repositorioLibro = mock(RepositorioLibro.class);
        carrito = new CarritoService(repositorioLibro);
    }

    // ---- Helper ----
    private Libro crearLibro(Long id, int stock, BigDecimal precio) {
        Libro libro = new Libro();
        libro.setId(id);
        libro.setStock(stock);
        libro.setPrecio(precio);
        return libro;
    }

    // ---- Casos normales ----

    @Test
    void debeAgregarUnItemAlCarrito() {
        Libro libro = crearLibro(1L, 10, new BigDecimal("50000"));
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro));

        carrito.agregarItem(1L, 2);

        assertEquals(1, carrito.cantidadItemsUnicos());
        assertEquals(2, carrito.cantidadItems());
    }

    @Test
    void debeAcumularCantidadAlAgregarMismoItemDosVeces() {
        Libro libro = crearLibro(1L, 10, new BigDecimal("50000"));
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro));

        carrito.agregarItem(1L, 2);
        carrito.agregarItem(1L, 3);

        assertEquals(1, carrito.cantidadItemsUnicos()); // sigue siendo 1 ítem único
        assertEquals(5, carrito.cantidadItems());        // cantidad total = 5
    }

    @Test
    void debeRemoverItemExistente() {
        Libro libro = crearLibro(1L, 10, new BigDecimal("50000"));
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro));
        carrito.agregarItem(1L, 1);

        carrito.removerItem(1L);

        assertEquals(0, carrito.cantidadItemsUnicos());
    }

    @Test
    void debeCalcularTotalCorrectamente() {
        Libro libro1 = crearLibro(1L, 10, new BigDecimal("50000"));
        Libro libro2 = crearLibro(2L, 10, new BigDecimal("30000"));
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro1));
        when(repositorioLibro.buscarPorId(2L)).thenReturn(Optional.of(libro2));

        carrito.agregarItem(1L, 2); // 2 x 50000 = 100000
        carrito.agregarItem(2L, 1); // 1 x 30000 = 30000

        assertEquals(new BigDecimal("130000"), carrito.calcularTotal());
    }

    @Test
    void debeVaciarElCarrito() {
        Libro libro = crearLibro(1L, 10, new BigDecimal("50000"));
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro));
        carrito.agregarItem(1L, 1);

        carrito.vaciar();

        assertEquals(0, carrito.cantidadItemsUnicos());
        assertEquals(0, carrito.cantidadItems());
    }

    // ---- Casos borde ----

    @Test
    void debePermitirAgregarExactamente10Items() {
        for (long i = 1; i <= 10; i++) {
            Libro libro = crearLibro(i, 5, new BigDecimal("10000"));
            when(repositorioLibro.buscarPorId(i)).thenReturn(Optional.of(libro));
            carrito.agregarItem(i, 1);
        }

        assertEquals(10, carrito.cantidadItemsUnicos());
    }

    @Test
    void debeLanzarExcepcionAlAgregarItem11Diferente() {
        for (long i = 1; i <= 10; i++) {
            Libro libro = crearLibro(i, 5, new BigDecimal("10000"));
            when(repositorioLibro.buscarPorId(i)).thenReturn(Optional.of(libro));
            carrito.agregarItem(i, 1);
        }
        Libro libroExtra = crearLibro(11L, 5, new BigDecimal("10000"));
        when(repositorioLibro.buscarPorId(11L)).thenReturn(Optional.of(libroExtra));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
            () -> carrito.agregarItem(11L, 1));
        assertTrue(ex.getMessage().contains("10"));
    }

    @Test
    void debePermitirAgregarMasCantidadDeItemExistenteConCarritoLleno() {
        // Con 10 ítems llenos, aún se puede agregar más cantidad de uno existente
        for (long i = 1; i <= 10; i++) {
            Libro libro = crearLibro(i, 10, new BigDecimal("10000"));
            when(repositorioLibro.buscarPorId(i)).thenReturn(Optional.of(libro));
            carrito.agregarItem(i, 1);
        }
        // Agregar más del ítem 1 (ya existe) — debe funcionar
        assertDoesNotThrow(() -> carrito.agregarItem(1L, 2));
        assertEquals(10, carrito.cantidadItemsUnicos()); // sigue siendo 10 únicos
    }

    @Test
    void debeLanzarExcepcionAlValidarCheckoutConCarritoVacio() {
        IllegalStateException ex = assertThrows(IllegalStateException.class,
            () -> carrito.validarParaCheckout());
        assertTrue(ex.getMessage().contains("al menos 1"));
    }

    @Test
    void debePermitirCheckoutConAlMenosUnItem() {
        Libro libro = crearLibro(1L, 5, new BigDecimal("20000"));
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro));
        carrito.agregarItem(1L, 1);

        assertDoesNotThrow(() -> carrito.validarParaCheckout());
    }

    // ---- Excepciones ----

    @Test
    void debeLanzarExcepcionPorCantidadNegativa() {
        assertThrows(IllegalArgumentException.class,
            () -> carrito.agregarItem(1L, -1));
    }

    @Test
    void debeLanzarExcepcionPorCantidadCero() {
        assertThrows(IllegalArgumentException.class,
            () -> carrito.agregarItem(1L, 0));
    }

    @Test
    void debeLanzarExcepcionCuandoLibroNoExiste() {
        when(repositorioLibro.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
            () -> carrito.agregarItem(99L, 1));
    }

    @Test
    void debeLanzarExcepcionPorStockInsuficiente() {
        Libro libro = crearLibro(1L, 2, new BigDecimal("50000")); // stock = 2
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro));

        assertThrows(IllegalStateException.class,
            () -> carrito.agregarItem(1L, 5)); // pide 5, solo hay 2
    }

    @Test
    void debeLanzarExcepcionAlRemoverItemQueNoEstaEnCarrito() {
        assertThrows(NoSuchElementException.class,
            () -> carrito.removerItem(99L));
    }

    @Test
    void debeLanzarExcepcionAlCalcularTotalConCarritoVacio() {
        assertThrows(IllegalStateException.class,
            () -> carrito.calcularTotal());
    }

    // ---- Verificación de interacciones con mock ----

    @Test
    void debeConsultarRepositorioUnaVezPorAgregado() {
        Libro libro = crearLibro(1L, 10, new BigDecimal("50000"));
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro));

        carrito.agregarItem(1L, 1);

        verify(repositorioLibro, times(1)).buscarPorId(1L);
    }
}
```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

El prompt fue muy efectivo porque enumero explícitamente los cuatro grupos de casos de prueba. Incluir las reglas de negocio textuales fue clave para que el LLM entendiera la distincion entre `cantidadItems()` y `cantidadItemsUnicos()`. 

#### 2. ¿Qué se puede mejorar?

El LLM no cubrió el caso de agregar cantidad cero. También faltó probar que después de `vaciar()`, `validarParaCheckout()` lanza excepción, lo cual verifica que el estado interno se resetea correctamente. 

#### 3. Respuesta final

la respuesta de la LLM es correcta pero tambien identifique el siguiente caso borde que el LLM no cubrió, que escribo yo:


void debeLanzarExcepcionAlCheckoutDespuesDeVaciarCarrito() {
    Libro libro = crearLibro(1L, 5, new BigDecimal("20000"));
    when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro));
    carrito.agregarItem(1L, 1);
    carrito.vaciar(); // vaciamos el carrito

    // Ahora el checkout debe fallar porque el carrito está vacío
    assertThrows(IllegalStateException.class,
        () -> carrito.validarParaCheckout());
}

@Test
void debeMantener10ItemsUnicos_Y_PermitirIncrementoDeExistente() {
    // Llena el carrito con 10 ítems únicos
    for (long i = 1; i <= 10; i++) {
        Libro l = crearLibro(i, 20, new BigDecimal("10000"));
        when(repositorioLibro.buscarPorId(i)).thenReturn(Optional.of(l));
        carrito.agregarItem(i, 1);
    }
    // Intenta agregar más del ítem 1 — debe funcionar (ya existe)
    carrito.agregarItem(1L, 5);
    assertEquals(10, carrito.cantidadItemsUnicos()); // 10 únicos
    assertEquals(15, carrito.cantidadItems());        // 9*1 + 1*6 = 15
}
```

Estos dos casos validan comportamientos reales que un usuario podría desencadenar y que el LLM omitió.
