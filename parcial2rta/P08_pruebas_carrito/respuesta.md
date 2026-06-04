# Plantilla de entrega — Parcial 2

---

## Pregunta 8: Generar y complementar pruebas unitarias con JUnit 5 y Mockito

### Estudiante
- **Nombre completo**: Samuel Iregui

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Antigravity |
| **Modelo específico** | Gemini 3.5 Flash |
| **¿Por qué elegiste este LLM?** | Elegi este modelo por comodidad propia y debido a que tengo pago gemini pro y ya tengo todo bien organizado para los diferentes trabajos |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```text
Actúa como un ingeniero de control de calidad (QA) y desarrollador senior experto en testing. Necesito que generes una suite completa de pruebas unitarias utilizando JUnit 5 y Mockito para la clase CarritoService.

La clase CarritoService tiene las siguientes dependencias y reglas de negocio:
- Depende de RepositorioLibro para buscar libros por su ID.
- Al agregar un ítem:
  1. La cantidad debe ser mayor a cero (si no, IllegalArgumentException).
  2. El libro debe existir en la base de datos (si no, NoSuchElementException).
  3. La cantidad agregada no puede superar el stock del libro (si no, IllegalStateException).
  4. El carrito tiene una restricción de máximo 10 ítems únicos (diferentes). Si se intenta agregar el 11°, debe lanzar IllegalStateException. Pero si se aumenta la cantidad de un ítem que ya está en el carrito, no debe contar como ítem nuevo ni exceder la restricción de 10.
- Al remover un ítem:
  - El ítem debe existir en el carrito (si no, NoSuchElementException).
- Al calcular el total o hacer checkout:
  - El carrito no debe estar vacío (si no, IllegalStateException).

Por favor, genera el código Java de la clase CarritoServiceTest con toda la inicialización necesaria de Mockito, casos de prueba para el comportamiento normal, excepciones, casos borde y aserciones completas.

Formato de salida esperado: Código Java limpio y bien documentado para pruebas unitarias.
```

---

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

Aquí tienes el código completo de las pruebas unitarias para `CarritoService` usando JUnit 5 y Mockito:

```java
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        libro1 = new Libro(1L, "Libro de Test 1", new BigDecimal("50000"), 10);
        libro2 = new Libro(2L, "Libro de Test 2", new BigDecimal("30000"), 5);
    }

    // --- Pruebas de agregarItem() ---

    @Test
    void agregarItem_Exitoso() {
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro1));

        carritoService.agregarItem(1L, 2);

        assertEquals(1, carritoService.cantidadItemsUnicos());
        assertEquals(2, carritoService.cantidadItems());
        verify(repositorioLibro, times(1)).buscarPorId(1L);
    }

    @Test
    void agregarItem_CantidadMenorOIgualACero_LanzaIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> carritoService.agregarItem(1L, 0));
        assertThrows(IllegalArgumentException.class, () -> carritoService.agregarItem(1L, -5));
    }

    @Test
    void agregarItem_LibroNoEncontrado_LanzaNoSuchElementException() {
        when(repositorioLibro.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> carritoService.agregarItem(99L, 2));
    }

    @Test
    void agregarItem_StockInsuficiente_LanzaIllegalStateException() {
        when(repositorioLibro.buscarPorId(2L)).thenReturn(Optional.of(libro2));

        assertThrows(IllegalStateException.class, () -> carritoService.agregarItem(2L, 6)); // Stock es 5
    }

    @Test
    void agregarItem_IncrementarCantidadDeItemExistente() {
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro1));

        carritoService.agregarItem(1L, 2);
        carritoService.agregarItem(1L, 3); // Volver a agregar el mismo ítem

        assertEquals(1, carritoService.cantidadItemsUnicos()); // Sigue siendo 1 ítem único
        assertEquals(5, carritoService.cantidadItems()); // Cantidad acumulada es 5
    }

    @Test
    void agregarItem_SuperaMaximoItemsDiferentes_LanzaIllegalStateException() {
        // Mockear 10 libros diferentes
        for (long i = 1; i <= 10; i++) {
            Libro libro = new Libro(i, "Libro " + i, new BigDecimal("10000"), 5);
            when(repositorioLibro.buscarPorId(i)).thenReturn(Optional.of(libro));
            carritoService.agregarItem(i, 1);
        }

        // Crear el 11° libro
        Libro libro11 = new Libro(11L, "Libro 11", new BigDecimal("10000"), 5);
        when(repositorioLibro.buscarPorId(11L)).thenReturn(Optional.of(libro11));

        // Debería fallar al agregar el 11° ítem único
        assertThrows(IllegalStateException.class, () -> carritoService.agregarItem(11L, 1));
    }

    // --- Pruebas de removerItem() ---

    @Test
    void removerItem_Exitoso() {
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro1));
        carritoService.agregarItem(1L, 2);

        carritoService.removerItem(1L);

        assertEquals(0, carritoService.cantidadItemsUnicos());
        assertEquals(0, carritoService.cantidadItems());
    }

    @Test
    void removerItem_InexistenteEnCarrito_LanzaNoSuchElementException() {
        assertThrows(NoSuchElementException.class, () -> carritoService.removerItem(1L));
    }

    // --- Pruebas de calcularTotal() ---

    @Test
    void calcularTotal_Exitoso() {
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro1));
        when(repositorioLibro.buscarPorId(2L)).thenReturn(Optional.of(libro2));

        carritoService.agregarItem(1L, 2); // 2 * $50.000 = $100.000
        carritoService.agregarItem(2L, 1); // 1 * $30.000 = $30.000

        BigDecimal totalEsperado = new BigDecimal("130000");
        assertEquals(totalEsperado, carritoService.calcularTotal());
    }

    @Test
    void calcularTotal_CarritoVacio_LanzaIllegalStateException() {
        assertThrows(IllegalStateException.class, () -> carritoService.calcularTotal());
    }

    // --- Pruebas de vaciar() y validarParaCheckout() ---

    @Test
    void vaciar_DeberiaLimpiaTodoElCarrito() {
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro1));
        carritoService.agregarItem(1L, 2);

        carritoService.vaciar();

        assertEquals(0, carritoService.cantidadItemsUnicos());
        assertThrows(IllegalStateException.class, () -> carritoService.calcularTotal());
    }

    @Test
    void validarParaCheckout_Exitoso() {
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro1));
        carritoService.agregarItem(1L, 1);

        assertDoesNotThrow(() -> carritoService.validarParaCheckout());
    }

    @Test
    void validarParaCheckout_CarritoVacio_LanzaIllegalStateException() {
        assertThrows(IllegalStateException.class, () -> carritoService.validarParaCheckout());
    }
}
```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

El prompt detalla exhaustivamente las reglas de negocio del carrito de compras y las excepciones esperadas, mapeando los requisitos a métodos específicos de `CarritoService`. Esto permitió que el LLM generara la estructura exacta con JUnit 5 y Mockito y cubriera ordenadamente todos los flujos principales (éxitos y excepciones).

#### 2. ¿Qué se puede mejorar?

El prompt omitió retar al modelo a encontrar vulnerabilidades lógicas en la implementación del carrito. Al limitarse a pedir pruebas sobre lo que dice el papel, el LLM generó una suite estándar pero no detectó un bug lógico sumamente grave en el código de producción que hace que el control de stock sea inútil al realizar múltiples adiciones.

#### 3. Respuesta final

En conclusión, la suite de pruebas del LLM es estructuralmente correcta, cubre todas las interfaces del servicio y mockea adecuadamente el repositorio de libros. Sin embargo, **omitió un escenario crítico debido a un bug oculto en la implementación del código de producción**.

##### El Bug del Stock Acumulado
En el código de `CarritoService.agregarItem`:
```java
if (libro.getStock() < cantidad) throw new IllegalStateException("Stock insuficiente");
```
El sistema solo valida la cantidad *que se está agregando en la llamada actual* contra el stock disponible. No tiene en cuenta la cantidad *que el usuario ya tiene acumulada en el carrito*. 

Si el `libro1` tiene un stock total de **5** unidades:
1. Primera llamada: `carritoService.agregarItem(1L, 3)` (Pasa la validación, pues 3 < 5).
2. Segunda llamada: `carritoService.agregarItem(1L, 3)` (Pasa la validación de nuevo, pues 3 < 5).
3. Resultado: El carrito termina con **6** unidades del libro, superando el stock real del almacén de **5**.

El LLM **no generó una prueba que expusiera este fallo**. Para corregir esta omisión y hacer la suite verdaderamente robusta, se debe escribir la siguiente prueba unitaria adicional:

```java
@Test
void agregarItem_AcumuladoSuperaStock_DeberiaLanzarIllegalStateException() {
    // Escenario: Stock del libro es 5
    Libro libroStockLimitado = new Libro(1L, "Libro Stock 5", new BigDecimal("10000"), 5);
    when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libroStockLimitado));

    // Agregar 3 unidades (éxito)
    carritoService.agregarItem(1L, 3);

    // Intentar agregar 3 unidades más (debería lanzar excepción porque 3 + 3 = 6 > 5)
    assertThrows(IllegalStateException.class, () -> carritoService.agregarItem(1L, 3));
}
```

Esta prueba fallaría con la implementación actual del carrito, demostrando que hacía falta analizar el comportamiento acumulado de los ítems ya presentes en la sesión.
