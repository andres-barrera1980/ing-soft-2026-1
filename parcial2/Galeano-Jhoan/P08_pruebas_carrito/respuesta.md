# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [8]: [P08_pruebas_carrito]

### Estudiante
- **Nombre completo**: [Jhoan Galeano]

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [Claude  |
| **Modelo específico** | [ Claude 3.1 pro |
| **¿Por qué elegiste este LLM?** | Esta IA me ayuda a razonar cuando son preguntas que exigen un pensamiento mas a fondo |

---

### Prompt utilizado

Actúa como un ingeniero de software senior y experto en QA automation en el contexto de OpenLib Market. Necesito que generes un set de pruebas unitarias exhaustivo y completo utilizando JUnit 5 y Mockito para la clase `CarritoService` que te proporciono abajo.

Reglas de negocio a cubrir obligatoriamente:
- Un carrito debe tener mínimo 1 ítem para proceder al checkout. Si se intenta validar para checkout con el carrito vacío, debe lanzar `IllegalStateException`.
- Máximo 10 ítems únicos por carrito (sin contar cantidades). Si se intenta agregar un ítem número 11 que sea diferente, debe lanzar `IllegalStateException`.
- Si se agrega una cantidad menor o igual a cero, debe lanzar `IllegalArgumentException`.
- Si el libro no existe en el repositorio, debe lanzar `NoSuchElementException`.
- Si la cantidad solicitada excede el stock del libro, debe lanzar `IllegalStateException`.

Escribe el código de producción de prueba completo, sin omitir métodos ni usar comentarios de marcador de posición (placeholder). Asegúrate de incluir:
1. Configuración de los mocks con `@Mock` para `RepositorioLibro` e inicialización en `@BeforeEach`.
2. Casos felices/normales: `agregarItem` (libro nuevo), `agregarItem` (incrementar cantidad de libro existente), `removerItem`, `calcularTotal`, `vaciar`, `cantidadItems` y `cantidadItemsUnicos`.
3. Casos borde y excepciones:
   - Intentar agregar el ítem único número 11.
   - Validar el comportamiento de `items.merge` cuando se suma cantidad a un ítem existente estando ya en el límite de 10 ítems únicos (debería permitirlo).
   - `removerItem` de un libro que no está en el carrito (`NoSuchElementException`).
   - `calcularTotal` con carrito vacío (`IllegalStateException`).
   - `validarParaCheckout` con carrito vacío (`IllegalStateException`) y con ítems (caso feliz).

Aquí está el código de la clase a probar:

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

---

### Respuesta del LLM

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CarritoService — Suite completa")
class CarritoServiceTest {

    @Mock
    private RepositorioLibro repositorioLibro;

    private CarritoService carritoService;

    // ─── Helpers ──────────────────────────────────────────────────────────

    private Libro libroConStock(Long id, int stock, BigDecimal precio) {
        Libro libro = mock(Libro.class);
        when(libro.getId()).thenReturn(id);
        when(libro.getStock()).thenReturn(stock);
        when(libro.getPrecio()).thenReturn(precio);
        return libro;
    }

    private void mockLibro(Long id, int stock, BigDecimal precio) {
        Libro libro = libroConStock(id, stock, precio);
        when(repositorioLibro.buscarPorId(id)).thenReturn(Optional.of(libro));
    }

    private void llenarCarritoConNLibros(int n) {
        for (long id = 1; id <= n; id++) {
            mockLibro(id, 10, BigDecimal.valueOf(10_000));
            carritoService.agregarItem(id, 1);
        }
    }

    // ─── Setup ────────────────────────────────────────────────────────────

    @BeforeEach
    void setUp() {
        carritoService = new CarritoService(repositorioLibro);
    }

    // ══════════════════════════════════════════════════════════════════════
    // CASOS FELICES
    // ══════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("agregarItem — casos normales")
    class AgregarItemNormal {

        @Test
        @DisplayName("Agrega un libro nuevo al carrito vacío")
        void agregaLibroNuevo() {
            mockLibro(1L, 5, BigDecimal.valueOf(50_000));

            carritoService.agregarItem(1L, 2);

            assertEquals(1, carritoService.cantidadItemsUnicos());
            assertEquals(2, carritoService.cantidadItems());
        }

        @Test
        @DisplayName("Incrementa la cantidad si el libro ya existe en el carrito")
        void incrementaCantidadLibroExistente() {
            mockLibro(1L, 10, BigDecimal.valueOf(30_000));
            carritoService.agregarItem(1L, 2);

            carritoService.agregarItem(1L, 3);

            assertEquals(1, carritoService.cantidadItemsUnicos());
            assertEquals(5, carritoService.cantidadItems());
        }

        @Test
        @DisplayName("Agrega dos libros distintos y cuenta ambos")
        void agregaDosLibrosDistintos() {
            mockLibro(1L, 5, BigDecimal.valueOf(20_000));
            mockLibro(2L, 5, BigDecimal.valueOf(40_000));

            carritoService.agregarItem(1L, 1);
            carritoService.agregarItem(2L, 1);

            assertEquals(2, carritoService.cantidadItemsUnicos());
            assertEquals(2, carritoService.cantidadItems());
        }
    }

    @Nested
    @DisplayName("removerItem — casos normales")
    class RemoverItemNormal {

        @Test
        @DisplayName("Remueve un ítem existente correctamente")
        void removeItemExistente() {
            mockLibro(1L, 5, BigDecimal.valueOf(25_000));
            carritoService.agregarItem(1L, 1);

            carritoService.removerItem(1L);

            assertEquals(0, carritoService.cantidadItemsUnicos());
            assertEquals(0, carritoService.cantidadItems());
        }

        @Test
        @DisplayName("Remueve solo el libro indicado sin afectar los demás")
        void removeSoloLibroIndicado() {
            mockLibro(1L, 5, BigDecimal.valueOf(10_000));
            mockLibro(2L, 5, BigDecimal.valueOf(10_000));
            carritoService.agregarItem(1L, 1);
            carritoService.agregarItem(2L, 1);

            carritoService.removerItem(1L);

            assertEquals(1, carritoService.cantidadItemsUnicos());
        }
    }

    @Nested
    @DisplayName("calcularTotal — casos normales")
    class CalcularTotalNormal {

        @Test
        @DisplayName("Calcula el total con un solo ítem")
        void totalConUnItem() {
            mockLibro(1L, 10, BigDecimal.valueOf(50_000));
            carritoService.agregarItem(1L, 3);

            BigDecimal total = carritoService.calcularTotal();

            assertEquals(new BigDecimal("150000"), total);
        }

        @Test
        @DisplayName("Calcula el total con múltiples ítems distintos")
        void totalConMultiplesItems() {
            mockLibro(1L, 10, BigDecimal.valueOf(30_000));
            mockLibro(2L, 10, BigDecimal.valueOf(20_000));
            carritoService.agregarItem(1L, 2);
            carritoService.agregarItem(2L, 1);

            BigDecimal total = carritoService.calcularTotal();

            assertEquals(new BigDecimal("80000"), total);
        }

        @Test
        @DisplayName("Calcula el total acumulado tras incrementar cantidad de un ítem")
        void totalTrasIncrementarCantidad() {
            mockLibro(1L, 10, BigDecimal.valueOf(10_000));
            carritoService.agregarItem(1L, 2);
            carritoService.agregarItem(1L, 3);

            BigDecimal total = carritoService.calcularTotal();

            assertEquals(new BigDecimal("50000"), total);
        }
    }

    @Nested
    @DisplayName("vaciar — casos normales")
    class VaciarNormal {

        @Test
        @DisplayName("Vaciar deja el carrito sin ítems")
        void vaciarDejaCarritoVacio() {
            mockLibro(1L, 5, BigDecimal.valueOf(15_000));
            carritoService.agregarItem(1L, 2);

            carritoService.vaciar();

            assertEquals(0, carritoService.cantidadItemsUnicos());
            assertEquals(0, carritoService.cantidadItems());
        }

        @Test
        @DisplayName("Vaciar un carrito ya vacío no lanza excepción")
        void vaciarCarritoYaVacioEsSeguro() {
            assertDoesNotThrow(() -> carritoService.vaciar());
        }
    }

    @Nested
    @DisplayName("cantidadItems y cantidadItemsUnicos")
    class ContadoresNormal {

        @Test
        @DisplayName("cantidadItems suma todas las unidades de todos los ítems")
        void cantidadItemsSumaUnidades() {
            mockLibro(1L, 10, BigDecimal.valueOf(10_000));
            mockLibro(2L, 10, BigDecimal.valueOf(10_000));
            carritoService.agregarItem(1L, 3);
            carritoService.agregarItem(2L, 4);

            assertEquals(7, carritoService.cantidadItems());
        }

        @Test
        @DisplayName("cantidadItemsUnicos cuenta tipos distintos sin importar cantidad")
        void cantidadItemsUnicosContaTipos() {
            mockLibro(1L, 10, BigDecimal.valueOf(10_000));
            mockLibro(2L, 10, BigDecimal.valueOf(10_000));
            carritoService.agregarItem(1L, 5);
            carritoService.agregarItem(2L, 5);

            assertEquals(2, carritoService.cantidadItemsUnicos());
        }

        @Test
        @DisplayName("cantidadItems retorna 0 en carrito vacío")
        void cantidadItemsEnCarritoVacioEsCero() {
            assertEquals(0, carritoService.cantidadItems());
        }
    }

    @Nested
    @DisplayName("validarParaCheckout — caso feliz")
    class ValidarCheckoutNormal {

        @Test
        @DisplayName("No lanza excepción si el carrito tiene al menos un ítem")
        void noLanzaConItemsPresentes() {
            mockLibro(1L, 5, BigDecimal.valueOf(10_000));
            carritoService.agregarItem(1L, 1);

            assertDoesNotThrow(() -> carritoService.validarParaCheckout());
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    // CASOS BORDE Y EXCEPCIONES
    // ══════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("agregarItem — validaciones y excepciones")
    class AgregarItemExcepciones {

        @Test
        @DisplayName("Lanza IllegalArgumentException si cantidad es cero")
        void lanzaExcepcionCantidadCero() {
            IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> carritoService.agregarItem(1L, 0)
            );
            assertEquals("Cantidad debe ser positiva", ex.getMessage());
            verifyNoInteractions(repositorioLibro);
        }

        @Test
        @DisplayName("Lanza IllegalArgumentException si cantidad es negativa")
        void lanzaExcepcionCantidadNegativa() {
            assertThrows(
                IllegalArgumentException.class,
                () -> carritoService.agregarItem(1L, -5)
            );
            verifyNoInteractions(repositorioLibro);
        }

        @Test
        @DisplayName("Lanza NoSuchElementException si el libro no existe en repositorio")
        void lanzaExcepcionLibroNoEncontrado() {
            when(repositorioLibro.buscarPorId(99L)).thenReturn(Optional.empty());

            NoSuchElementException ex = assertThrows(
                NoSuchElementException.class,
                () -> carritoService.agregarItem(99L, 1)
            );
            assertTrue(ex.getMessage().contains("99"));
        }

        @Test
        @DisplayName("Lanza IllegalStateException si el stock es insuficiente")
        void lanzaExcepcionStockInsuficiente() {
            mockLibro(1L, 2, BigDecimal.valueOf(10_000));

            IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> carritoService.agregarItem(1L, 5)
            );
            assertEquals("Stock insuficiente", ex.getMessage());
        }

        @Test
        @DisplayName("Lanza IllegalStateException al agregar ítem número 11 distinto")
        void lanzaExcepcionAlSuperarMaximoDiezItemsUnicos() {
            llenarCarritoConNLibros(10);
            mockLibro(11L, 5, BigDecimal.valueOf(10_000));

            IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> carritoService.agregarItem(11L, 1)
            );
            assertTrue(ex.getMessage().contains("10"));
            assertEquals(10, carritoService.cantidadItemsUnicos());
        }

        @Test
        @DisplayName("Permite incrementar cantidad de ítem existente aunque ya haya 10 ítems únicos")
        void permiteIncrementarCantidadConCarritoLleno() {
            llenarCarritoConNLibros(10);

            // El libro 1 ya existe — no es un ítem nuevo, debe permitirse
            mockLibro(1L, 20, BigDecimal.valueOf(10_000));

            assertDoesNotThrow(() -> carritoService.agregarItem(1L, 5));
            assertEquals(10, carritoService.cantidadItemsUnicos());
            assertEquals(15, carritoService.cantidadItems()); // 9×1 + 1×(1+5)
        }
    }

    @Nested
    @DisplayName("removerItem — excepciones")
    class RemoverItemExcepciones {

        @Test
        @DisplayName("Lanza NoSuchElementException al remover ítem que no está en el carrito")
        void lanzaExcepcionAlRemoverItemInexistente() {
            NoSuchElementException ex = assertThrows(
                NoSuchElementException.class,
                () -> carritoService.removerItem(99L)
            );
            assertEquals("Ítem no está en el carrito", ex.getMessage());
        }

        @Test
        @DisplayName("Lanza NoSuchElementException al remover de carrito vacío")
        void lanzaExcepcionAlRemoverDeCarritoVacio() {
            assertThrows(
                NoSuchElementException.class,
                () -> carritoService.removerItem(1L)
            );
        }
    }

    @Nested
    @DisplayName("calcularTotal — excepciones")
    class CalcularTotalExcepciones {

        @Test
        @DisplayName("Lanza IllegalStateException si el carrito está vacío")
        void lanzaExcepcionCarritoVacio() {
            IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> carritoService.calcularTotal()
            );
            assertEquals("El carrito está vacío", ex.getMessage());
        }

        @Test
        @DisplayName("Lanza IllegalStateException tras vaciar un carrito con ítems")
        void lanzaExcepcionTrasVaciar() {
            mockLibro(1L, 5, BigDecimal.valueOf(10_000));
            carritoService.agregarItem(1L, 1);
            carritoService.vaciar();

            assertThrows(
                IllegalStateException.class,
                () -> carritoService.calcularTotal()
            );
        }
    }

    @Nested
    @DisplayName("validarParaCheckout — excepciones")
    class ValidarCheckoutExcepciones {

        @Test
        @DisplayName("Lanza IllegalStateException si el carrito está vacío")
        void lanzaExcepcionCheckoutCarritoVacio() {
            IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> carritoService.validarParaCheckout()
            );
            assertEquals(
                "El carrito debe tener al menos 1 ítem para checkout",
                ex.getMessage()
            );
        }

        @Test
        @DisplayName("Lanza IllegalStateException tras remover el último ítem")
        void lanzaExcepcionTrasRemoverUltimoItem() {
            mockLibro(1L, 5, BigDecimal.valueOf(10_000));
            carritoService.agregarItem(1L, 1);
            carritoService.removerItem(1L);

            assertThrows(
                IllegalStateException.class,
                () -> carritoService.validarParaCheckout()
            );
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    // CASOS DE INTEGRACIÓN INTERNA
    // ══════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Flujos combinados")
    class FlujosCombinados {

        @Test
        @DisplayName("Flujo completo: agregar, remover, calcular total")
        void flujoCompletoAgregarRemoverTotal() {
            mockLibro(1L, 10, BigDecimal.valueOf(20_000));
            mockLibro(2L, 10, BigDecimal.valueOf(30_000));

            carritoService.agregarItem(1L, 2);
            carritoService.agregarItem(2L, 1);
            carritoService.removerItem(1L);

            assertEquals(1, carritoService.cantidadItemsUnicos());
            assertEquals(new BigDecimal("30000"), carritoService.calcularTotal());
            assertDoesNotThrow(() -> carritoService.validarParaCheckout());
        }

        @Test
        @DisplayName("Vaciar permite reutilizar el carrito desde cero")
        void vaciarPermiteReutilizarCarrito() {
            mockLibro(1L, 10, BigDecimal.valueOf(10_000));
            carritoService.agregarItem(1L, 3);
            carritoService.vaciar();

            mockLibro(2L, 10, BigDecimal.valueOf(50_000));
            carritoService.agregarItem(2L, 1);

            assertEquals(1, carritoService.cantidadItemsUnicos());
            assertEquals(new BigDecimal("50000"), carritoService.calcularTotal());
        }

        @Test
        @DisplayName("El repositorio se consulta exactamente una vez por llamada a agregarItem")
        void repositorioConsultadoUnaVezPorAgregado() {
            mockLibro(1L, 10, BigDecimal.valueOf(10_000));

            carritoService.agregarItem(1L, 1);
            carritoService.agregarItem(1L, 2);

            verify(repositorioLibro, times(2)).buscarPorId(1L);
        }
    }
}

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

[Evaluo los casos que se deben tomar en cuenta para realizar las operaciones necesarias.]


#### 2. ¿Qué se puede mejorar?

[Un prompt mas efectivo a la hora de pedir cosas exactas. y que sean directas para crear casos o pruebas unitarias.]


#### 3. Respuesta final

[¿El LLM generó pruebas para todos los métodos?Sí. Cubrió explícitamente todos los métodos expuestos en la clase de producción: agregarItem() entre otras.
 ¿Cubrió todos los casos borde de las reglas de negocio? Casi todos. Validó los límites de 10 ítems máximos, las cantidades negativas/cero, el carrito vacío, y el stock insuficiente.
 ¿Probó `validarParaCheckout()`? ¿Probó el caso de agregar un ítem que ya existe? Sí. Creó la clase anidada ValidarCheckoutNormal (para el caso feliz) y ValidarCheckoutExcepciones (para comprobar que lance IllegalStateException si está vacío o si se vacía tras remover ítems)
 ¿Usó correctamente mocks para `RepositorioLibro`? si, lo hiizo en dos pruebas, en incrementaCantidadLibroExistente() y en permiteIncrementarCantidadConCarritoLleno()
]
