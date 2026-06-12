## P08_pruebas_carrito

### Estudiante
- **Nombre completo**: [Alejandro Macias Barrios]

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [Claude] |
| **Modelo específico** | [Claude Sonnet 4.6] |
| **¿Por qué elegiste este LLM?** | [La generación de pruebas unitarias con JUnit 5 y Mockito requiere conocer la API correcta de estas librerías. Claude genera pruebas con nombres descriptivos y cobertura de casos borde más completa que otros LLMs en mi experiencia según lo visto en clase.] |

### Prompt utilizado
Contexto: OpenLib Market. Tengo la siguiente clase CarritoService en Java 21:

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

Reglas de negocio:
- Mínimo 1 ítem para checkout.
- Máximo 10 ítems ÚNICOS (un libro con cantidad 5 cuenta como 1 ítem único).
- Si se intenta agregar un ítem 11 único, se lanza excepción.
- Si se intenta checkout con carrito vacío, se lanza excepción.

Genera pruebas unitarias COMPLETAS con JUnit 5 y Mockito en Java 21 que cubran:
1. Casos normales: agregar un ítem nuevo, agregar un ítem existente (suma cantidades), remover ítem, calcular total, vaciar.
2. Casos borde: carrito vacío, carrito con exactamente 10 ítems únicos, intentar agregar el ítem 11.
3. Excepciones: checkout con carrito vacío, cantidad negativa, libro no encontrado, stock insuficiente, remover ítem inexistente, calcular total con carrito vacío.
4. Usa Mockito para mockear RepositorioLibro. Usa nombres de prueba descriptivos (dado_cuando_entonces). No uses @SpringBootTest.]

### Respuesta del LLM

[import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarritoServiceTest {

    @Mock
    private RepositorioLibro repositorioLibro;

    @InjectMocks
    private CarritoService carritoService;

    // --- Helpers ---
    private Libro crearLibro(Long id, int stock, BigDecimal precio) {
        Libro libro = new Libro();
        libro.setId(id);
        libro.setStock(stock);
        libro.setPrecio(precio);
        libro.setTitulo("Libro " + id);
        return libro;
    }

    // =============================================
    // CASOS NORMALES
    // =============================================

    @Test
    void dadoCarritoVacio_cuandoAgregoItemNuevo_entoncesCarritoTieneUnItem() {
        Libro libro = crearLibro(1L, 10, new BigDecimal("25000"));
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro));

        carritoService.agregarItem(1L, 2);

        assertThat(carritoService.cantidadItemsUnicos()).isEqualTo(1);
        assertThat(carritoService.cantidadItems()).isEqualTo(2);
    }

    @Test
    void dadoItemExistente_cuandoAgregoMismoItem_entoncesSumaCantidades() {
        Libro libro = crearLibro(1L, 10, new BigDecimal("25000"));
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro));

        carritoService.agregarItem(1L, 2);
        carritoService.agregarItem(1L, 3);

        assertThat(carritoService.cantidadItemsUnicos()).isEqualTo(1); // sigue siendo 1 único
        assertThat(carritoService.cantidadItems()).isEqualTo(5);       // cantidad total = 2+3
    }

    @Test
    void dadoItemEnCarrito_cuandoRemuevo_entoncesCarritoQuedaSinEseItem() {
        Libro libro = crearLibro(1L, 10, new BigDecimal("25000"));
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro));
        carritoService.agregarItem(1L, 1);

        carritoService.removerItem(1L);

        assertThat(carritoService.cantidadItemsUnicos()).isEqualTo(0);
    }

    @Test
    void dadosDosItems_cuandoCalcularTotal_entoncesDevuelveSumaCorrecta() {
        Libro libro1 = crearLibro(1L, 10, new BigDecimal("20000"));
        Libro libro2 = crearLibro(2L, 10, new BigDecimal("30000"));
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro1));
        when(repositorioLibro.buscarPorId(2L)).thenReturn(Optional.of(libro2));

        carritoService.agregarItem(1L, 2); // 40000
        carritoService.agregarItem(2L, 1); // 30000

        assertThat(carritoService.calcularTotal()).isEqualByComparingTo(new BigDecimal("70000"));
    }

    @Test
    void dadoCarritoConItems_cuandoVaciar_entoncesCarritoQuedaVacio() {
        Libro libro = crearLibro(1L, 10, new BigDecimal("25000"));
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro));
        carritoService.agregarItem(1L, 1);

        carritoService.vaciar();

        assertThat(carritoService.cantidadItemsUnicos()).isEqualTo(0);
    }

    // =============================================
    // CASOS BORDE
    // =============================================

    @Test
    void dadoCarritoVacio_cuandoConsultoCantidad_entoncesDevuelveCero() {
        assertThat(carritoService.cantidadItemsUnicos()).isEqualTo(0);
        assertThat(carritoService.cantidadItems()).isEqualTo(0);
    }

    @Test
    void dadoCarritoConDiezItemsUnicos_cuandoAgregoItemExistente_entoncesPermiteAgregarCantidad() {
        // Llenar carrito con 10 libros distintos
        for (long i = 1; i <= 10; i++) {
            Libro libro = crearLibro(i, 10, new BigDecimal("10000"));
            when(repositorioLibro.buscarPorId(i)).thenReturn(Optional.of(libro));
            carritoService.agregarItem(i, 1);
        }
        // Agregar más cantidad de un libro que ya existe (ID=1) — debe permitirse
        Libro libroExistente = crearLibro(1L, 10, new BigDecimal("10000"));
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libroExistente));

        assertThatNoException().isThrownBy(() -> carritoService.agregarItem(1L, 2));
        assertThat(carritoService.cantidadItemsUnicos()).isEqualTo(10); // sigue siendo 10 únicos
    }

    @Test
    void dadoCarritoConDiezItemsUnicos_cuandoIntentaAgregarItemNuevo_entoncesLanzaExcepcion() {
        for (long i = 1; i <= 10; i++) {
            Libro libro = crearLibro(i, 10, new BigDecimal("10000"));
            when(repositorioLibro.buscarPorId(i)).thenReturn(Optional.of(libro));
            carritoService.agregarItem(i, 1);
        }
        Libro libroNuevo = crearLibro(11L, 10, new BigDecimal("10000"));
        when(repositorioLibro.buscarPorId(11L)).thenReturn(Optional.of(libroNuevo));

        assertThatThrownBy(() -> carritoService.agregarItem(11L, 1))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("10");
    }

    // =============================================
    // EXCEPCIONES
    // =============================================

    @Test
    void dadoCarritoVacio_cuandoValidarParaCheckout_entoncesLanzaExcepcion() {
        assertThatThrownBy(() -> carritoService.validarParaCheckout())
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("vacío");
    }

    @Test
    void dadoCantidadNegativa_cuandoAgregarItem_entoncesLanzaIllegalArgument() {
        assertThatThrownBy(() -> carritoService.agregarItem(1L, -1))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("positiva");
    }

    @Test
    void dadoLibroInexistente_cuandoAgregarItem_entoncesLanzaNoSuchElement() {
        when(repositorioLibro.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> carritoService.agregarItem(99L, 1))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessageContaining("99");
    }

    @Test
    void dadoStockInsuficiente_cuandoAgregarItem_entoncesLanzaIllegalState() {
        Libro libro = crearLibro(1L, 2, new BigDecimal("25000")); // solo 2 en stock
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro));

        assertThatThrownBy(() -> carritoService.agregarItem(1L, 5)) // pide 5
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Stock");
    }

    @Test
    void dadoItemNoEnCarrito_cuandoRemoverItem_entoncesLanzaNoSuchElement() {
        assertThatThrownBy(() -> carritoService.removerItem(999L))
            .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void dadoCarritoVacio_cuandoCalcularTotal_entoncesLanzaIllegalState() {
        assertThatThrownBy(() -> carritoService.calcularTotal())
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("vacío");
    }
}]

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?
[El prompt enumeró muy bien los casos a cubrir (normales, borde, excepciones), nombró métodos específicos (validarParaCheckout, el caso de agregar cantidad adicional a ítem existente cuando el carrito ya tiene 10), y especificó el formato de nombres de prueba. Esto produjo una cobertura mucho más completa que un prompt genérico.]

#### 2. ¿Qué se puede mejorar?
[No se generó una prueba para verificar que cantidadItems() suma las cantidades de todos los ítems correctamente cuando hay múltiples ítems con cantidades distintas. Tampoco generó el caso de vaciar() y luego validar que validarParaCheckout() lanza excepción (flujo combinado). Además, usó @InjectMocks que requiere que CarritoService tenga un constructor que Mockito pueda invocar — si el constructor no es compatible, fallará silenciosamente. Hubiera sido más explícito construir el objeto manualmente en @BeforeEach.]

#### 3. Respuesta final
[Las pruebas del LLM son buenas y cubren la mayoría de los casos. Los casos que faltaron y deben agregarse:

// Primer caso omitido: cantidadItems() suma correctamente múltiples ítems con distintas cantidades
@Test
void dadosTresItemsConCantidades_cuandoCantidadItems_entoncesDevuelveSumaTotal() {
    for (long i = 1; i <= 3; i++) {
        Libro libro = crearLibro(i, 10, new BigDecimal("10000"));
        when(repositorioLibro.buscarPorId(i)).thenReturn(Optional.of(libro));
        carritoService.agregarItem(i, (int) i); // cantidades: 1, 2, 3
    }
    assertThat(carritoService.cantidadItems()).isEqualTo(6);
    assertThat(carritoService.cantidadItemsUnicos()).isEqualTo(3);
}

// Segundo caso omitido: validarParaCheckout pasa con carrito lleno
@Test
void dadoCarritoConItem_cuandoValidarParaCheckout_entoncesNoLanzaExcepcion() {
    Libro libro = crearLibro(1L, 10, new BigDecimal("25000"));
    when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro));
    carritoService.agregarItem(1L, 1);

    assertThatNoException().isThrownBy(() -> carritoService.validarParaCheckout());
}

// Tercer caso omitido: cantidad cero lanza excepción
@Test
void dadoCantidadCero_cuandoAgregarItem_entoncesLanzaExcepcion() {
    assertThatThrownBy(() -> carritoService.agregarItem(1L, 0))
        .isInstanceOf(IllegalArgumentException.class);
}]