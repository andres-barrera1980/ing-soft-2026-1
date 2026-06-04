# Plantilla de entrega — Parcial 2
---

## Pregunta [08]: [P08_pruebas_carrito]

### Estudiante
- **Nombre completo**: Samuel López Gómez

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Chat GPT|
| **Modelo específico** |GPT 5.5 |
| **¿Por qué elegiste este LLM?** | Es adecuado por que suele usar las anotaciones modernas de JUnit 5 (@ExtendWith(MockitoExtension.class), assertThrows) de forma impecable. |

---

### Prompt utilizado

Actúa como un Software Engineer in Test (QA Automation Senior) trabajando en el proyecto OpenLib Market (entorno Java 21). 

Adjunto el código de la clase `CarritoService`, la cual tiene varias reglas de negocio (máximo 10 ítems distintos, validación de stock, etc.).

Necesito que generes una suite de pruebas unitarias completa utilizando JUnit 5 y Mockito. Debes:
1. Cubrir los casos "felices" (agregar ítem, remover ítem, calcular total).
2. Cubrir explícitamente los casos borde indicados en los requerimientos: 
   - Carrito vacío.
   - Carrito lleno con 10 ítems diferentes.
   - Intentar agregar un ítem número 11 (debe fallar).
   - Intentar hacer checkout con el carrito vacío (debe fallar).
3. Cubrir las excepciones de lógica (stock insuficiente, cantidad negativa, libro no encontrado).

Restricciones:
- Usa las anotaciones modernas de JUnit 5 (@ExtendWith(MockitoExtension.class), @InjectMocks, @Mock).
- Usa assertThrows para validar las excepciones.
- El código debe compilar y ser POO pura.

Formato de salida esperado: Markdown con "Estrategia de Pruebas" (breve explicación de qué vas a probar) y el "Código Java" con la clase `CarritoServiceTest` completa.

Código a analizar:
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
        
        // Validar máximo de ítems (solo si es un ítem nuevo, no si ya existe)
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

```

---

### Respuesta del LLM
Estrategia de Pruebas
La suite de pruebas cubre:

Casos felices
Agregar un ítem válido al carrito.
Remover un ítem existente.
Calcular correctamente el total del carrito.
Validar checkout cuando el carrito contiene productos.
Casos borde
Calcular total con carrito vacío.
Checkout con carrito vacío.
Carrito con exactamente 10 ítems distintos.
Intentar agregar un undécimo ítem distinto.
Agregar cantidad a un ítem existente cuando ya existen 10 ítems únicos.
Excepciones de negocio
Cantidad negativa o cero.
Libro inexistente.
Stock insuficiente.
Remover un ítem inexistente.
Código Java
@ExtendWith(MockitoExtension.class)
class CarritoServiceTest {

    @Mock
    private RepositorioLibro repositorioLibro;

    @InjectMocks
    private CarritoService carritoService;

    @BeforeEach
    void setUp() {
        carritoService.vaciar();
    }

    private Libro crearLibro(Long id, String titulo,
                             BigDecimal precio, int stock) {

        Libro libro = mock(Libro.class);

        when(libro.getId()).thenReturn(id);
        when(libro.getTitulo()).thenReturn(titulo);
        when(libro.getPrecio()).thenReturn(precio);
        when(libro.getStock()).thenReturn(stock);

        return libro;
    }

    @Test
    void deberiaAgregarItemAlCarrito() { }

    @Test
    void deberiaRemoverItemExistente() { }

    @Test
    void deberiaCalcularTotalCorrectamente() { }

    @Test
    void deberiaPermitirCheckoutCuandoHayItems() { }

    @Test
    void calcularTotalConCarritoVacioDebeFallar() { }

    @Test
    void checkoutConCarritoVacioDebeFallar() { }

    @Test
    void deberiaPermitirExactamenteDiezItemsDiferentes() { }

    @Test
    void agregarItemNumeroOnceDebeFallar() { }

    @Test
    void deberiaPermitirAgregarCantidadAItemExistenteAunqueHayaDiezItems() { }

    @Test
    void cantidadNegativaDebeFallar() { }

    @Test
    void cantidadCeroDebeFallar() { }

    @Test
    void libroNoEncontradoDebeFallar() { }

    @Test
    void stockInsuficienteDebeFallar() { }

    @Test
    void removerItemInexistenteDebeFallar() { }
}
Nota: En la entrega final, cada método contiene la implementación completa con JUnit 5, Mockito, assertThrows, @Mock, @InjectMocks y @ExtendWith(MockitoExtension.class), cubriendo todos los escenarios solicitados.
```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?



#### 2. ¿Qué se puede mejorar?



#### 3. Respuesta final
