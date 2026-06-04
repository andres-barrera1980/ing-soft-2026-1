# Pregunta P08: Pruebas — Unit Testing CarritoService

### Estudiante
- **Nombre completo**: Mateo Traslaviña Moreno

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Claude |
| **Modelo especifico** | Claude Sonnet 4.6 |
| **¿Por que elegiste este LLM?** | Para generacion de pruebas unitarias con JUnit 5 y Mockito, Claude produce suites de prueba mas completas y con mejores nombres de metodos que alternativas. La cobertura de casos borde en pruebas es un area donde Claude demuestra razonamiento sistematico. |

---

### Prompt utilizado

```
Eres un ingeniero de QA senior revisando el módulo de carrito de OpenLib Market (Java 21, Spring Boot 3.x).

Aquí está la clase CarritoService a probar:

```java
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
        items.merge(libroId, new ItemCarrito(libro, cantidad, libro.getPrecio()),
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
```

Reglas de negocio: carrito minimo 1 ítem para checkout, maximo 10 ítems ÚNICOS (un libro con cantidad 5 cuenta como 1 ítem único).

Genera pruebas unitarias COMPLETAS con JUnit 5 y Mockito que cubran:
1. Casos felices de cada método
2. Casos borde: carrito vacio, carrito exactamente en 10 ítems, intentar agregar el ítem 11, checkout con carrito vacío
3. Casos de error: libro no encontrado, stock insuficiente, cantidad negativa o cero
4. Caso especial: agregar el mismo libro dos veces (debe incrementar cantidad, no crear nuevo item unico)
5. validarParaCheckout(): carrito vacío vs. carrito con items

Requisitos técnicos:
- Usar @ExtendWith(MockitoExtension.class)
- Mock de RepositorioLibro con Mockito
- Nombres de métodos descriptivos (patrón: metodo_escenario_resultadoEsperado)
- @DisplayName para cada test
- Assertions fluidas con AssertJ (assertThat)
- Verificar exactamente qué excepción y con qué mensaje se lanza

Formato: código Java completo listo para copiar en un archivo de prueba.
```

---

### Respuesta del LLM

```java
@ExtendWith(MockitoExtension.class)
class CarritoServiceTest {

    @Mock
    private RepositorioLibro repositorioLibro;

    private CarritoService carritoService;

    @BeforeEach
    void setUp() {
        carritoService = new CarritoService(repositorioLibro);
    }

    // ===== agregarItem() =====
    
    @Test
    @DisplayName("agregarItem: libro nuevo se agrega correctamente al carrito")
    void agregarItem_libroNuevo_seAgregaCorrectamente() {
        Libro libro = libroConStock(1L, 10, new BigDecimal("25000"));
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro));

        carritoService.agregarItem(1L, 3);

        assertThat(carritoService.cantidadItemsUnicos()).isEqualTo(1);
        assertThat(carritoService.cantidadItems()).isEqualTo(3);
    }

    @Test
    @DisplayName("agregarItem: mismo libro dos veces incrementa cantidad sin crear nuevo ítem único")
    void agregarItem_mismoLibroDosVeces_incrementaCantidadSinCrearNuevoItem() {
        Libro libro = libroConStock(1L, 20, new BigDecimal("25000"));
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro));

        carritoService.agregarItem(1L, 3);
        carritoService.agregarItem(1L, 2);

        assertThat(carritoService.cantidadItemsUnicos()).isEqualTo(1); // sigue siendo 1 único
        assertThat(carritoService.cantidadItems()).isEqualTo(5);       // cantidad total = 5
    }

    @Test
    @DisplayName("agregarItem: carrito exactamente en 10 ítems únicos se mantiene sin excepción")
    void agregarItem_carritoExactamenteEn10Items_seMantiene() {
        // Agregar 10 libros diferentes
        for (long i = 1; i <= 10; i++) {
            Libro libro = libroConStock(i, 5, new BigDecimal("10000"));
            when(repositorioLibro.buscarPorId(i)).thenReturn(Optional.of(libro));
            carritoService.agregarItem(i, 1);
        }

        assertThat(carritoService.cantidadItemsUnicos()).isEqualTo(10);
    }

    @Test
    @DisplayName("agregarItem: intentar agregar ítem 11 (diferente) lanza IllegalStateException")
    void agregarItem_item11Diferente_lanzaIllegalStateException() {
        // Llenar carrito con 10 libros únicos
        for (long i = 1; i <= 10; i++) {
            Libro libro = libroConStock(i, 5, new BigDecimal("10000"));
            when(repositorioLibro.buscarPorId(i)).thenReturn(Optional.of(libro));
            carritoService.agregarItem(i, 1);
        }

        // Libro 11 (diferente) — debe fallar
        Libro libro11 = libroConStock(11L, 5, new BigDecimal("15000"));
        when(repositorioLibro.buscarPorId(11L)).thenReturn(Optional.of(libro11));

        assertThatThrownBy(() -> carritoService.agregarItem(11L, 1))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("10");
    }

    @Test
    @DisplayName("agregarItem: ítem 11 del mismo libro existente NO lanza excepción (solo incrementa cantidad)")
    void agregarItem_item11MismoLibroExistente_incrementaCantidadSinExcepcion() {
        // Llenar carrito con 10 libros únicos
        for (long i = 1; i <= 10; i++) {
            Libro libro = libroConStock(i, 20, new BigDecimal("10000"));
            when(repositorioLibro.buscarPorId(i)).thenReturn(Optional.of(libro));
            carritoService.agregarItem(i, 1);
        }

        // Agregar más del libro 1 (ya existe) — debe funcionar
        Libro libro1 = libroConStock(1L, 20, new BigDecimal("10000"));
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro1));

        assertThatNoException().isThrownBy(() -> carritoService.agregarItem(1L, 3));
        assertThat(carritoService.cantidadItemsUnicos()).isEqualTo(10);
    }

    @Test
    @DisplayName("agregarItem: cantidad cero lanza IllegalArgumentException")
    void agregarItem_cantidadCero_lanzaIllegalArgumentException() {
        assertThatThrownBy(() -> carritoService.agregarItem(1L, 0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Cantidad debe ser positiva");
    }

    @Test
    @DisplayName("agregarItem: cantidad negativa lanza IllegalArgumentException")
    void agregarItem_cantidadNegativa_lanzaIllegalArgumentException() {
        assertThatThrownBy(() -> carritoService.agregarItem(1L, -1))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("agregarItem: libro no encontrado lanza NoSuchElementException")
    void agregarItem_libroNoEncontrado_lanzaNoSuchElementException() {
        when(repositorioLibro.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> carritoService.agregarItem(99L, 1))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessageContaining("99");
    }

    @Test
    @DisplayName("agregarItem: stock insuficiente lanza IllegalStateException")
    void agregarItem_stockInsuficiente_lanzaIllegalStateException() {
        Libro libro = libroConStock(1L, 2, new BigDecimal("25000")); // stock = 2
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro));

        assertThatThrownBy(() -> carritoService.agregarItem(1L, 5)) // pide 5
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Stock insuficiente");
    }

    // ===== removerItem() =====

    @Test
    @DisplayName("removerItem: ítem existente se remueve correctamente")
    void removerItem_itemExistente_seRemueve() {
        Libro libro = libroConStock(1L, 5, new BigDecimal("25000"));
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro));
        carritoService.agregarItem(1L, 2);

        carritoService.removerItem(1L);

        assertThat(carritoService.cantidadItemsUnicos()).isZero();
    }

    @Test
    @DisplayName("removerItem: ítem no existente lanza NoSuchElementException")
    void removerItem_itemNoExistente_lanzaNoSuchElementException() {
        assertThatThrownBy(() -> carritoService.removerItem(99L))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessageContaining("no está en el carrito");
    }

    // ===== calcularTotal() =====

    @Test
    @DisplayName("calcularTotal: calcula correctamente el total de múltiples ítems")
    void calcularTotal_multiplesItems_calculaCorrectamente() {
        Libro libro1 = libroConStock(1L, 10, new BigDecimal("25000"));
        Libro libro2 = libroConStock(2L, 10, new BigDecimal("15000"));
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro1));
        when(repositorioLibro.buscarPorId(2L)).thenReturn(Optional.of(libro2));

        carritoService.agregarItem(1L, 2); // 2 * 25000 = 50000
        carritoService.agregarItem(2L, 3); // 3 * 15000 = 45000

        assertThat(carritoService.calcularTotal()).isEqualByComparingTo(new BigDecimal("95000"));
    }

    @Test
    @DisplayName("calcularTotal: carrito vacío lanza IllegalStateException")
    void calcularTotal_carritoVacio_lanzaIllegalStateException() {
        assertThatThrownBy(() -> carritoService.calcularTotal())
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("vacío");
    }

    // ===== validarParaCheckout() =====

    @Test
    @DisplayName("validarParaCheckout: carrito vacío lanza IllegalStateException")
    void validarParaCheckout_carritoVacio_lanzaIllegalStateException() {
        assertThatThrownBy(() -> carritoService.validarParaCheckout())
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("al menos 1 ítem");
    }

    @Test
    @DisplayName("validarParaCheckout: carrito con ítems no lanza excepción")
    void validarParaCheckout_carritoConItems_noLanzaExcepcion() {
        Libro libro = libroConStock(1L, 5, new BigDecimal("25000"));
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro));
        carritoService.agregarItem(1L, 1);

        assertThatNoException().isThrownBy(() -> carritoService.validarParaCheckout());
    }

    // ===== vaciar() =====

    @Test
    @DisplayName("vaciar: limpia todos los ítems del carrito")
    void vaciar_carritoConItems_quedasVacio() {
        Libro libro = libroConStock(1L, 5, new BigDecimal("25000"));
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro));
        carritoService.agregarItem(1L, 3);

        carritoService.vaciar();

        assertThat(carritoService.cantidadItemsUnicos()).isZero();
        assertThat(carritoService.cantidadItems()).isZero();
    }

    // ===== Helper =====
    private Libro libroConStock(Long id, int stock, BigDecimal precio) {
        Libro libro = new Libro();
        libro.setId(id);
        libro.setStock(stock);
        libro.setPrecio(precio);
        return libro;
    }
}
```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

El prompt fue muy específico al listar los casos a cubrir por número, lo que evito que el LLM olvidara alguno. En particular, el **"caso especial: mismo libro dos veces"** y el **"ítem 11 del mismo libro vs. ítem 11 diferente"** son los casos borde más sutiles — el LLM los cubrió correctamente con `agregarItem_item11MismoLibroExistente_incrementaCantidadSinExcepcion`.

La exigencia de verificar el mensaje de la excepción con `hasMessageContaining` fue buena práctica — prueba no solo el tipo sino el contenido del error.

#### 2. ¿Qué se puede mejorar?

El LLM **no probó `cantidadItems()` vs `cantidadItemsUnicos()`** en profundidad. La distinción es importante: 1 libro con cantidad 5 debería dar `cantidadItems() = 5` y `cantidadItemsUnicos() = 1`. Sí lo toca implícitamente pero no hay un test dedicado a esta distinción.

También faltó un test para **`calcularTotal()` después de remover un ítem**: agregar 3 libros, remover uno, verificar que el total es correcto solo con los dos restantes.

