# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [08]: [P08_pruebas_carrito]

### Estudiante
- **Nombre completo**: [Danna Gabriela Rojas Bernal]

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **claude** | [Claude / ChatGPT / Gemini / Copilot / DeepSeek / Qwen / Mistral / Otro / **Sin IA — respuesta propia**] |
| **sonnet 4.6** | [Ej: Claude Opus 4.5, GPT-4o, Gemini 2.5 Pro, etc. Si respondes sin IA, escribe "N/A"] |
| **EJERCICIO me  lo pide** | [Justifica en 1-3 oraciones. Si respondes sin IA, explica por qué decidiste no usar LLM para esta pregunta.] |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```
[Pega aquí el prompt exacto que enviaste al LLM. 
Incluye TODO el texto, sin editar ni resumir.

Un buen prompt incluye:
- Contexto del proyecto OpenLib Market
- El código o situación específica
- Lo que esperas que el LLM haga
- Restricciones (ej: "usa Java 21", "aplica SOLID")
- Formato de salida esperado (ej: "respuesta en markdown con código Java")]
```
Actúa como un Ingeniero de QA Senior especializado en Java, JUnit 5, Mockito y TDD. Analiza  la siguiente clase CarritoService y sus reglas de negocio. Necesito que generes una suite completa de pruebas unitarias utilizando JUnit 5 y Mockito. REGLAS DE NEGOCIO: - Un carrito debe tener mínimo 1 ítem para proceder al checkout. - Un carrito puede tener máximo 10 ítems diferentes. - Las cantidades no cuentan como ítems únicos. - Si se intenta agregar un ítem nuevo cuando ya existen 10 ítems diferentes, debe lanzarse una excepción. - Si se intenta hacer checkout con el carrito vacío, debe lanzarse una excepción. - Si la cantidad es menor o igual a cero, debe lanzarse una excepción. - Si el libro no existe, debe lanzarse una excepción. - Si no hay stock suficiente, debe lanzarse una excepción. CÓDIGO A PROBAR:
java
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
TAREAS: 1. Genera pruebas unitarias completas usando JUnit 5 y Mockito. 2. Usa @ExtendWith(MockitoExtension.class). 3. Usa @Mock para RepositorioLibro. 4. Usa @InjectMocks para CarritoService. 5. Incluye nombres descriptivos para cada prueba. 6. Organiza las pruebas utilizando el patrón Given-When-Then. 7. Explica brevemente qué valida cada prueba. Debes cubrir obligatoriamente TODOS los siguientes escenarios: Método agregarItem(): - agregar un ítem válido. - agregar un ítem existente y aumentar cantidad. - cantidad igual a cero. - cantidad negativa. - libro inexistente. - stock insuficiente. - carrito con exactamente 10 ítems diferentes. - intento de agregar el ítem número 11. Método removerItem(): - remover un ítem existente. - remover un ítem inexistente. Método calcularTotal(): - calcular total correctamente con varios ítems. - calcular total con un solo ítem. - carrito vacío. Método validarParaCheckout(): - checkout válido. - checkout con carrito vacío. Métodos auxiliares: - vaciar(). - cantidadItems(). - cantidadItemsUnicos(). IMPORTANTE: Antes de terminar, crea una sección llamada "Cobertura de Casos" donde indiques explícitamente qué método y qué regla de negocio cubre cada prueba. Finalmente, indica si existe algún caso borde adicional que debería probarse para alcanzar una cobertura cercana al 100%.
---

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**
[Pega aquí la respuesta COMPLETA del LLM, sin editar, sin resumir.
Incluye TODO el texto, código, explicaciones que generó el LLM.

Si el LLM generó código, asegúrate de que esté correctamente formateado.
Si tuviste que hacer varias iteraciones, pega la MEJOR respuesta obtenida,
pero menciona cuántas iteraciones hiciste.]
```
Código de Pruebas
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CarritoServiceTest {

    @Mock
    private RepositorioLibro repositorioLibro;

    @InjectMocks
    private CarritoService carritoService;

    private Libro libro1;
    private Libro libro2;

    @BeforeEach
    void setUp() {
        libro1 = new Libro(
                1L,
                "Clean Code",
                new BigDecimal("50000"),
                20
        );

        libro2 = new Libro(
                2L,
                "Design Patterns",
                new BigDecimal("70000"),
                15
        );
    }

    // =========================================================
    // agregarItem()
    // =========================================================

    @Test
    @DisplayName("Debe agregar un ítem válido al carrito")
    void debeAgregarItemValido() {

        // Given
        when(repositorioLibro.buscarPorId(1L))
                .thenReturn(Optional.of(libro1));

        // When
        carritoService.agregarItem(1L, 2);

        // Then
        assertEquals(2, carritoService.cantidadItems());
        assertEquals(1, carritoService.cantidadItemsUnicos());
    }

    @Test
    @DisplayName("Debe aumentar la cantidad cuando el ítem ya existe")
    void debeAumentarCantidadCuandoItemYaExiste() {

        // Given
        when(repositorioLibro.buscarPorId(1L))
                .thenReturn(Optional.of(libro1));

        carritoService.agregarItem(1L, 2);

        // When
        carritoService.agregarItem(1L, 3);

        // Then
        assertEquals(5, carritoService.cantidadItems());
        assertEquals(1, carritoService.cantidadItemsUnicos());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando cantidad es cero")
    void debeLanzarExcepcionCuandoCantidadEsCero() {

        // Given
        int cantidad = 0;

        // When - Then
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> carritoService.agregarItem(1L, cantidad)
                );

        assertEquals(
                "Cantidad debe ser positiva",
                exception.getMessage()
        );
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando cantidad es negativa")
    void debeLanzarExcepcionCuandoCantidadEsNegativa() {

        // Given
        int cantidad = -5;

        // When - Then
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> carritoService.agregarItem(1L, cantidad)
                );

        assertEquals(
                "Cantidad debe ser positiva",
                exception.getMessage()
        );
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el libro no existe")
    void debeLanzarExcepcionCuandoLibroNoExiste() {

        // Given
        when(repositorioLibro.buscarPorId(99L))
                .thenReturn(Optional.empty());

        // When - Then
        assertThrows(
                NoSuchElementException.class,
                () -> carritoService.agregarItem(99L, 1)
        );
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando no hay stock suficiente")
    void debeLanzarExcepcionCuandoStockInsuficiente() {

        // Given
        when(repositorioLibro.buscarPorId(1L))
                .thenReturn(Optional.of(libro1));

        // When - Then
        assertThrows(
                IllegalStateException.class,
                () -> carritoService.agregarItem(1L, 100)
        );
    }

    @Test
    @DisplayName("Debe permitir exactamente 10 ítems diferentes")
    void debePermitirExactamenteDiezItemsDiferentes() {

        // Given
        for (long i = 1; i <= 10; i++) {

            Libro libro = new Libro(
                    i,
                    "Libro " + i,
                    BigDecimal.TEN,
                    50
            );

            when(repositorioLibro.buscarPorId(i))
                    .thenReturn(Optional.of(libro));
        }

        // When
        for (long i = 1; i <= 10; i++) {
            carritoService.agregarItem(i, 1);
        }

        // Then
        assertEquals(10, carritoService.cantidadItemsUnicos());
    }

    @Test
    @DisplayName("Debe lanzar excepción al intentar agregar el ítem número 11")
    void debeLanzarExcepcionAlAgregarItemNumeroOnce() {

        // Given
        for (long i = 1; i <= 11; i++) {

            Libro libro = new Libro(
                    i,
                    "Libro " + i,
                    BigDecimal.TEN,
                    50
            );

            when(repositorioLibro.buscarPorId(i))
                    .thenReturn(Optional.of(libro));
        }

        for (long i = 1; i <= 10; i++) {
            carritoService.agregarItem(i, 1);
        }

        // When - Then
        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> carritoService.agregarItem(11L, 1)
                );

        assertTrue(
                exception.getMessage().contains("más de 10 ítems")
        );
    }

    // =========================================================
    // removerItem()
    // =========================================================

    @Test
    @DisplayName("Debe remover un ítem existente")
    void debeRemoverItemExistente() {

        // Given
        when(repositorioLibro.buscarPorId(1L))
                .thenReturn(Optional.of(libro1));

        carritoService.agregarItem(1L, 2);

        // When
        carritoService.removerItem(1L);

        // Then
        assertEquals(0, carritoService.cantidadItemsUnicos());
    }

    @Test
    @DisplayName("Debe lanzar excepción al remover un ítem inexistente")
    void debeLanzarExcepcionAlRemoverItemInexistente() {

        // Given - When - Then
        assertThrows(
                NoSuchElementException.class,
                () -> carritoService.removerItem(999L)
        );
    }

    // =========================================================
    // calcularTotal()
    // =========================================================

    @Test
    @DisplayName("Debe calcular correctamente el total con varios ítems")
    void debeCalcularTotalConVariosItems() {

        // Given
        when(repositorioLibro.buscarPorId(1L))
                .thenReturn(Optional.of(libro1));

        when(repositorioLibro.buscarPorId(2L))
                .thenReturn(Optional.of(libro2));

        carritoService.agregarItem(1L, 2);
        carritoService.agregarItem(2L, 1);

        // When
        BigDecimal total = carritoService.calcularTotal();

        // Then
        assertEquals(
                new BigDecimal("170000"),
                total
        );
    }

    @Test
    @DisplayName("Debe calcular correctamente el total con un solo ítem")
    void debeCalcularTotalConUnSoloItem() {

        // Given
        when(repositorioLibro.buscarPorId(1L))
                .thenReturn(Optional.of(libro1));

        carritoService.agregarItem(1L, 3);

        // When
        BigDecimal total = carritoService.calcularTotal();

        // Then
        assertEquals(
                new BigDecimal("150000"),
                total
        );
    }

    @Test
    @DisplayName("Debe lanzar excepción al calcular total con carrito vacío")
    void debeLanzarExcepcionAlCalcularTotalConCarritoVacio() {

        // Given - When - Then
        assertThrows(
                IllegalStateException.class,
                () -> carritoService.calcularTotal()
        );
    }

    // =========================================================
    // validarParaCheckout()
    // =========================================================

    @Test
    @DisplayName("Debe permitir checkout cuando existe al menos un ítem")
    void debePermitirCheckoutValido() {

        // Given
        when(repositorioLibro.buscarPorId(1L))
                .thenReturn(Optional.of(libro1));

        carritoService.agregarItem(1L, 1);

        // When - Then
        assertDoesNotThrow(
                () -> carritoService.validarParaCheckout()
        );
    }

    @Test
    @DisplayName("Debe lanzar excepción al hacer checkout con carrito vacío")
    void debeLanzarExcepcionCheckoutCarritoVacio() {

        // Given - When - Then
        assertThrows(
                IllegalStateException.class,
                () -> carritoService.validarParaCheckout()
        );
    }

    // =========================================================
    // Métodos auxiliares
    // =========================================================

    @Test
    @DisplayName("vaciar debe eliminar todos los ítems del carrito")
    void debeVaciarCarrito() {

        // Given
        when(repositorioLibro.buscarPorId(1L))
                .thenReturn(Optional.of(libro1));

        carritoService.agregarItem(1L, 5);

        // When
        carritoService.vaciar();

        // Then
        assertEquals(0, carritoService.cantidadItems());
        assertEquals(0, carritoService.cantidadItemsUnicos());
    }

    @Test
    @DisplayName("cantidadItems debe retornar la suma total de cantidades")
    void debeRetornarCantidadItems() {

        // Given
        when(repositorioLibro.buscarPorId(1L))
                .thenReturn(Optional.of(libro1));

        when(repositorioLibro.buscarPorId(2L))
                .thenReturn(Optional.of(libro2));

        carritoService.agregarItem(1L, 2);
        carritoService.agregarItem(2L, 3);

        // When
        int cantidad = carritoService.cantidadItems();

        // Then
        assertEquals(5, cantidad);
    }

    @Test
    @DisplayName("cantidadItemsUnicos debe retornar únicamente los ítems distintos")
    void debeRetornarCantidadItemsUnicos() {

        // Given
        when(repositorioLibro.buscarPorId(1L))
                .thenReturn(Optional.of(libro1));

        carritoService.agregarItem(1L, 2);
        carritoService.agregarItem(1L, 5);

        // When
        int cantidad = carritoService.cantidadItemsUnicos();

        // Then
        assertEquals(1, cantidad);
    }
}
Explicación de cada prueba
Prueba	Qué valida
debeAgregarItemValido	Agrega correctamente un libro existente con cantidad válida
debeAumentarCantidadCuandoItemYaExiste	Si el libro ya existe en el carrito, incrementa la cantidad sin crear un nuevo ítem
debeLanzarExcepcionCuandoCantidadEsCero	No permite cantidades iguales a cero
debeLanzarExcepcionCuandoCantidadEsNegativa	No permite cantidades negativas
debeLanzarExcepcionCuandoLibroNoExiste	No permite agregar libros inexistentes
debeLanzarExcepcionCuandoStockInsuficiente	No permite agregar más unidades que el stock disponible
debePermitirExactamenteDiezItemsDiferentes	Valida el límite permitido de ítems únicos
debeLanzarExcepcionAlAgregarItemNumeroOnce	Impide superar el máximo de 10 ítems diferentes
debeRemoverItemExistente	Elimina correctamente un ítem presente
debeLanzarExcepcionAlRemoverItemInexistente	Lanza excepción al eliminar algo inexistente
debeCalcularTotalConVariosItems	Calcula correctamente la suma de varios productos
debeCalcularTotalConUnSoloItem	Calcula correctamente el total de un único producto
debeLanzarExcepcionAlCalcularTotalConCarritoVacio	No permite calcular total de un carrito vacío
debePermitirCheckoutValido	Permite checkout con al menos un ítem
debeLanzarExcepcionCheckoutCarritoVacio	Impide checkout sin productos
debeVaciarCarrito	Limpia completamente el carrito
debeRetornarCantidadItems	Cuenta correctamente las cantidades totales
debeRetornarCantidadItemsUnicos	Cuenta correctamente los ítems distintos
Cobertura de Casos
Método agregarItem()
Prueba	Regla cubierta
debeAgregarItemValido	Agregar ítem válido
debeAumentarCantidadCuandoItemYaExiste	Las cantidades no cuentan como ítems únicos
debeLanzarExcepcionCuandoCantidadEsCero	Cantidad ≤ 0
debeLanzarExcepcionCuandoCantidadEsNegativa	Cantidad ≤ 0
debeLanzarExcepcionCuandoLibroNoExiste	Libro inexistente
debeLanzarExcepcionCuandoStockInsuficiente	Stock insuficiente
debePermitirExactamenteDiezItemsDiferentes	Máximo 10 ítems
debeLanzarExcepcionAlAgregarItemNumeroOnce	Intento de agregar el ítem 11
Método removerItem()
Prueba	Regla cubierta
debeRemoverItemExistente	Remoción válida
debeLanzarExcepcionAlRemoverItemInexistente	Ítem inexistente
Método calcularTotal()
Prueba	Regla cubierta
debeCalcularTotalConVariosItems	Suma correcta
debeCalcularTotalConUnSoloItem	Total correcto con un ítem
debeLanzarExcepcionAlCalcularTotalConCarritoVacio	Carrito vacío
Método validarParaCheckout()
Prueba	Regla cubierta
debePermitirCheckoutValido	Mínimo 1 ítem para checkout
debeLanzarExcepcionCheckoutCarritoVacio	Checkout con carrito vacío
Métodos auxiliares
Prueba	Método cubierto
debeVaciarCarrito	vaciar()
debeRetornarCantidadItems	cantidadItems()
debeRetornarCantidadItemsUnicos	cantidadItemsUnicos()
Casos borde adicionales recomendados (para acercarse al 100%)

Aunque los escenarios obligatorios ya están cubiertos, un QA Senior normalmente agregaría:

1. Agregar el mismo libro cuando el carrito ya tiene 10 ítems únicos

Valida que:

items.size() == 10

pero el libro ya existe.

Debe permitir aumentar cantidad sin excepción.

2. Stock exactamente igual a la cantidad solicitada

Ejemplo:

stock = 5
cantidad = 5

Debe ser válido.

3. Calcular total después de remover un ítem

Verifica consistencia del estado interno.

4. Vaciar un carrito ya vacío

Debe ejecutarse sin errores.

5. Checkout inmediatamente después de vaciar

Secuencia:

agregarItem()
vaciar()
validarParaCheckout()

Debe lanzar excepción.

6. Verificar interacción con Mockito

Por ejemplo:

verify(repositorioLibro).buscarPorId(1L);

Esto aumenta la cobertura de comportamiento y colaboración entre objetos.

Con estos casos adicionales se lograría una cobertura muy cercana al 100% de ramas (branch coverage) y 100% de reglas de negocio del servicio.
---

### Análisis crítico de la respuesta

