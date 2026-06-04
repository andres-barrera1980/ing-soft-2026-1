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
    