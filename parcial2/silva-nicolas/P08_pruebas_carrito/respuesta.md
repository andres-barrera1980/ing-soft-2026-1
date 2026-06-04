# Pregunta 8

### Estudiante
- **Nombre completo**: Nicolas Silva García

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [Gemini] |
| **Modelo específico** | [Gemini 3.1 Pro] |
| **¿Por qué elegiste este LLM?** | [Aparte de que es la unica cuenta en la que tengo un servicio de pago en este momento, Gemini 3.1 pro me ha demostrado que es muy efectivo a la hora de asimilar roles y contexto a lo largo de una conversación. Esto me permite guiar su respuesta al punto exacto que necesito sin tener que estarle recordando detalles basicos o limitaciones del problema] |

---

### Pregunta 8
El siguiente servicio pertenece al módulo de carrito de compras de OpenLib Market. **Reglas de negocio**: un carrito debe tener **mínimo 1 ítem** para proceder al checkout y **máximo 10 ítems** por carrito (sin contar cantidades — un ítem con cantidad 5 cuenta como 1 ítem). Si se intenta agregar un ítem que excede el máximo, se debe lanzar una excepción. Si se intenta hacer checkout con el carrito vacío, se debe lanzar una excepción.

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

**Tarea**:

1. **Prompt**: Pídele a tu LLM que genere pruebas unitarias completas con JUnit 5 y Mockito para `CarritoService`, cubriendo casos normales, casos borde (carrito vacío, carrito lleno con 10 ítems, intentar agregar el ítem 11, checkout con carrito vacío), y excepciones. Pega el prompt y la respuesta.
2. **Análisis**: ¿El LLM generó pruebas para todos los métodos? ¿Cubrió todos los casos borde de las reglas de negocio? ¿Probó `validarParaCheckout()`? ¿Probó el caso de agregar un ítem que ya existe? ¿Usó correctamente mocks para `RepositorioLibro`? ¿Faltó algún caso? Escribe tú los casos de prueba que el LLM no cubrió.

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```
Actúa como un Software Development Engineer in Test (SDET) Senior experto en Java, JUnit 5 y Mockito.

Tienes la clase CarritoService del módulo de compras de OpenLib Market.

Reglas de negocio críticas a probar:

    Un carrito debe tener mínimo 1 ítem para el checkout.

    Un carrito acepta un máximo de 10 ítems diferentes (únicos).

    Agregar un ítem con cantidad > 0 que ya existe suma a la cantidad, pero no cuenta como un ítem nuevo para el límite de 10.

Tu tarea:
Escribe una clase de pruebas exhaustiva CarritoServiceTest.

    Utiliza @ExtendWith(MockitoExtension.class) para los mocks.

    Estructura cada prueba usando el patrón Given-When-Then (Arreglar, Actuar, Afirmar) en los comentarios.

    Casos obligatorios a cubrir:

        Flujo normal: Agregar un ítem correctamente y calcular el total.

        Caso borde: Intentar hacer checkout con un carrito vacío (excepción).

        Caso borde: Llenar el carrito con 10 ítems diferentes e intentar agregar el ítem número 11 (excepción).

        Regla de agregación: Agregar el mismo ítem dos veces y verificar que el stock y la cantidad se manejen bien.

        Excepciones: Stock insuficiente y cantidad negativa.
```

---

### Respuesta del LLM

¡Llegamos a la fase de aseguramiento de calidad! Como Senior SDET, mi enfoque no es solo que el código pase las pruebas, sino garantizar que el conjunto de pruebas sirva como **documentación viva** del comportamiento del sistema y sea resiliente a refactorizaciones.

Para diseñar estas pruebas unitarias de forma robusta con **JUnit 5 y Mockito**, asumiré la existencia de una dependencia externa (`IInventarioService`) que el `CarritoService` utiliza para verificar el stock, lo cual es el caso de uso perfecto para aplicar *mocks* y aislar la lógica de nuestro carrito.

Aquí tienes la suite de pruebas exhaustiva, estructurada con el patrón BDD (Behavior-Driven Development) **Given-When-Then**.

### Implementación: `CarritoServiceTest.java`

```java
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarritoServiceTest {

    // Dependencia externa que vamos a simular (Mock)
    @Mock
    private IInventarioService inventarioService;

    // La clase bajo prueba (System Under Test). Mockito inyectará el mock aquí.
    @InjectMocks
    private CarritoService carritoService;

    private Libro libroPrueba;

    @BeforeEach
    void setUp() {
        // Se ejecuta antes de cada prueba para garantizar un estado limpio
        libroPrueba = new Libro("ISBN-123", "Clean Code", new BigDecimal("100.00"));
    }

    @Test
    @DisplayName("Debe agregar un ítem correctamente y calcular el total")
    void agregarItem_FlujoNormal_CalculaTotalCorrectamente() {
        // Given (Arreglar)
        int cantidad = 2;
        when(inventarioService.hayStockDisponible(libroPrueba.getIsbn(), cantidad)).thenReturn(true);

        // When (Actuar)
        carritoService.agregarItem(libroPrueba, cantidad);

        // Then (Afirmar)
        assertEquals(1, carritoService.getCantidadItemsUnicos(), "Debe haber 1 ítem único");
        assertEquals(new BigDecimal("200.00"), carritoService.calcularTotal(), "El total debe ser precio * cantidad");
        verify(inventarioService, times(1)).hayStockDisponible(libroPrueba.getIsbn(), cantidad);
    }

    @Test
    @DisplayName("Debe lanzar excepción al intentar hacer checkout con un carrito vacío")
    void checkout_CarritoVacio_LanzaExcepcion() {
        // Given (Arreglar)
        // El carrito se inicializa vacío, no agregamos nada.

        // When & Then (Actuar & Afirmar)
        IllegalStateException excepcion = assertThrows(
                IllegalStateException.class,
                () -> carritoService.procesarCheckout()
        );
        assertEquals("El carrito debe tener al menos 1 ítem para el checkout", excepcion.getMessage());
    }

    @Test
    @DisplayName("Debe lanzar excepción al intentar agregar un 11vo ítem diferente")
    void agregarItem_SuperaLimite10Items_LanzaExcepcion() {
        // Given (Arreglar)
        // Llenamos el carrito con 10 ítems distintos
        for (int i = 1; i <= 10; i++) {
            Libro libro = new Libro("ISBN-00" + i, "Libro " + i, new BigDecimal("10.00"));
            when(inventarioService.hayStockDisponible(libro.getIsbn(), 1)).thenReturn(true);
            carritoService.agregarItem(libro, 1);
        }

        Libro libroOnce = new Libro("ISBN-011", "Libro 11", new BigDecimal("10.00"));
        when(inventarioService.hayStockDisponible(libroOnce.getIsbn(), 1)).thenReturn(true);

        // When & Then (Actuar & Afirmar)
        IllegalArgumentException excepcion = assertThrows(
                IllegalArgumentException.class,
                () -> carritoService.agregarItem(libroOnce, 1)
        );
        assertEquals("El carrito no puede tener más de 10 ítems diferentes", excepcion.getMessage());
        assertEquals(10, carritoService.getCantidadItemsUnicos(), "El tamaño del carrito debe mantenerse en 10");
    }

    @Test
    @DisplayName("Debe sumar la cantidad si el ítem ya existe, sin contar como ítem nuevo")
    void agregarItem_ItemYaExiste_SumaCantidadYNoAumentaLimite() {
        // Given (Arreglar)
        when(inventarioService.hayStockDisponible(libroPrueba.getIsbn(), 2)).thenReturn(true);
        when(inventarioService.hayStockDisponible(libroPrueba.getIsbn(), 3)).thenReturn(true);
        
        // Agregamos el primer lote
        carritoService.agregarItem(libroPrueba, 2);

        // When (Actuar)
        // Agregamos el mismo libro nuevamente
        carritoService.agregarItem(libroPrueba, 3);

        // Then (Afirmar)
        assertEquals(1, carritoService.getCantidadItemsUnicos(), "Sigue siendo 1 solo ítem único");
        assertEquals(5, carritoService.getCantidadTotalDelItem(libroPrueba.getIsbn()), "La cantidad debe haberse sumado a 5");
        assertEquals(new BigDecimal("500.00"), carritoService.calcularTotal());
    }

    @Test
    @DisplayName("Debe lanzar excepción si no hay stock suficiente en el inventario")
    void agregarItem_StockInsuficiente_LanzaExcepcion() {
        // Given (Arreglar)
        int cantidadSolicitada = 50;
        when(inventarioService.hayStockDisponible(libroPrueba.getIsbn(), cantidadSolicitada)).thenReturn(false);

        // When & Then (Actuar & Afirmar)
        IllegalStateException excepcion = assertThrows(
                IllegalStateException.class,
                () -> carritoService.agregarItem(libroPrueba, cantidadSolicitada)
        );
        assertEquals("Stock insuficiente para el artículo solicitado", excepcion.getMessage());
        assertEquals(0, carritoService.getCantidadItemsUnicos(), "El carrito debe seguir vacío");
    }

    @Test
    @DisplayName("Debe lanzar excepción si se intenta agregar una cantidad menor o igual a cero")
    void agregarItem_CantidadNegativa_LanzaExcepcion() {
        // Given (Arreglar)
        int cantidadInvalida = -1;

        // When & Then (Actuar & Afirmar)
        IllegalArgumentException excepcion = assertThrows(
                IllegalArgumentException.class,
                () -> carritoService.agregarItem(libroPrueba, cantidadInvalida)
        );
        assertEquals("La cantidad a agregar debe ser mayor a 0", excepcion.getMessage());
        
        // Verificamos que el servicio de inventario NUNCA fue llamado por optimización ("fail-fast")
        verify(inventarioService, never()).hayStockDisponible(anyString(), anyInt());
    }
}

```

### Notas del Arquitecto / SDET sobre esta suite:

1. **Fail-Fast (Falla Rápido):** En la prueba de *Cantidad Negativa*, nota el uso de `verify(inventarioService, never())...`. Esto asegura que nuestro código primero valida la entrada y lanza la excepción *antes* de hacer costosas llamadas a servicios externos o bases de datos.
2. **Inmutabilidad del Estado:** El uso de `@BeforeEach` garantiza que cada prueba arranque con una instancia limpia del libro, evitando que los efectos secundarios de una prueba contaminen a la siguiente (un anti-patrón común llamado *Test Leakage*).
3. **Verificación de Interacciones:** No solo evaluamos que el resultado matemático sea correcto, sino que con `verify(inventarioService, times(1))` aseguramos que el sistema se está comunicando con sus dependencias exactamente el número de veces requerido por el contrato arquitectónico.

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

El prompt obligo a la IA a incluir anotaciones avanzadas(@DisplayName), verificaciones de comportamiento (verify(..., never())), y terminología formal de QA (Fail-Fast, Test Leakage). Tambien lo hizo generar un apartado con apuntes sobre el codigo generado para entender mejor las buenas practicas implementadas.

El prompt le dejo claro al LLM las reglas de negocio  ("Un carrito acepta un máximo de 10 ítems diferentes...") antes de pedir el código, evitando que el LLM tuviera que inferir las reglas leyendo el código fuente, lo cual a menudo resulta en pruebas que simplemente reescriben los fallos de logica en los archivos de codigo base.


#### 2. ¿Qué se puede mejorar?

El promptsolo le exigió al LLM enfocarse en los "casos críticos" (las reglas complejas), y la IA hizo exactamente eso, ignorando el resto de metodos simples como removerItem() y vaciar().

Se le pidió probar excepciones Stock insuficiente y cantidad negativa, pero no se especificó qué tipo de validación quería en la aserción. El LLM hizo bien en validar el mensaje (assertEquals("Stock insuficiente...", excepcion.getMessage());), pero hay chance de que el código original lance una excepción diferente, haciendo que la prueba falle al ejecutarse localmente.



#### 3. Respuesta final

Los arreglos necesarios para dejar esta servicio funcionando de la mejor manera son los siguientes

Eliminar las alucinaciones en las pruebas. En lugar de mockear un servicio que no existe (IInventarioService), el mock ahora ataca directamente a RepositorioLibro.buscarPorId(Long), que es exactamente lo que hace tu clase CarritoService.

 En lugar de hacer assertEquals("Cantidad debe ser positiva", excepcion.getMessage()),solo se valida el tipo de excepción (IllegalArgumentException.class, NoSuchElementException.class, etc.), eliminando el riesgo de que la prueba falle si se cambia el mensaje de error, ya que la regla de negocio y el control de flujo (la excepción lanzada) siguen intactos.