
## Pregunta [09]: [TDD inventario]

### Estudiante
- **Nombre completo**: [Marlon Garcia]


## Respuestas sin IA

Para probar el servicio `CarritoService` se usan pruebas unitarias con **JUnit 5** y **Mockito**, ya que el servicio depende de `RepositorioLibro`. Esa dependencia se simula con un mock para no depender de una base de datos real.

Las pruebas cubren:

* Agregar ítems correctamente.
* Sumar cantidades cuando se agrega el mismo libro.
* Calcular el total.
* Remover ítems.
* Vaciar el carrito.
* Validar el límite de 10 ítems únicos.
* Validar que no se pueda agregar un ítem número 11.
* Validar que sí se pueda aumentar la cantidad de un ítem ya existente aunque el carrito tenga 10 ítems únicos.
* Validar excepciones por carrito vacío, cantidad inválida, libro inexistente, stock insuficiente y checkout vacío.

```java
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CarritoServiceTest {

    @Mock
    private RepositorioLibro repositorioLibro;

    private CarritoService carritoService;

    @BeforeEach
    void setUp() {
        // Antes de cada prueba se crea un carrito nuevo para evitar que una prueba afecte a otra.
        carritoService = new CarritoService(repositorioLibro);
    }

    @Test
    void agregarItem_deberiaAgregarLibroAlCarrito() {
        // Se prepara un libro existente con stock suficiente.
        Long libroId = 1L;
        Libro libro = crearLibro(10, "50000");

        when(repositorioLibro.buscarPorId(libroId)).thenReturn(Optional.of(libro));

        // Se agrega el libro al carrito.
        carritoService.agregarItem(libroId, 2);

        // Debe existir un solo ítem único, pero con cantidad 2.
        assertEquals(1, carritoService.cantidadItemsUnicos());
        assertEquals(2, carritoService.cantidadItems());
        verify(repositorioLibro).buscarPorId(libroId);
    }

    @Test
    void agregarItem_mismoLibro_deberiaSumarCantidadSinAumentarItemsUnicos() {
        // Si se agrega el mismo libro dos veces, no debe contar como otro ítem diferente.
        Long libroId = 1L;
        Libro libro = crearLibro(10, "50000");

        when(repositorioLibro.buscarPorId(libroId)).thenReturn(Optional.of(libro));

        carritoService.agregarItem(libroId, 2);
        carritoService.agregarItem(libroId, 3);

        // Sigue siendo un solo libro diferente, pero la cantidad total queda en 5.
        assertEquals(1, carritoService.cantidadItemsUnicos());
        assertEquals(5, carritoService.cantidadItems());
    }

    @Test
    void calcularTotal_deberiaRetornarLaSumaCorrecta() {
        // Se agregan dos libros con precios diferentes para validar el calculo del total.
        Libro libro1 = crearLibro(10, "50000");
        Libro libro2 = crearLibro(10, "80000");

        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro1));
        when(repositorioLibro.buscarPorId(2L)).thenReturn(Optional.of(libro2));

        carritoService.agregarItem(1L, 2); // 2 * 50000 = 100000
        carritoService.agregarItem(2L, 1); // 1 * 80000 = 80000

        BigDecimal total = carritoService.calcularTotal();

        assertEquals(new BigDecimal("180000"), total);
    }

    @Test
    void removerItem_deberiaEliminarElItemDelCarrito() {
        // Primero se agrega un libro y luego se remueve para verificar que desaparezca del carrito.
        Long libroId = 1L;
        Libro libro = crearLibro(10, "50000");

        when(repositorioLibro.buscarPorId(libroId)).thenReturn(Optional.of(libro));

        carritoService.agregarItem(libroId, 2);
        carritoService.removerItem(libroId);

        assertEquals(0, carritoService.cantidadItemsUnicos());
        assertEquals(0, carritoService.cantidadItems());
    }

    @Test
    void removerItem_queNoExiste_deberiaLanzarExcepcion() {
        // No se puede remover un ítem que nunca fue agregado.
        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> carritoService.removerItem(1L)
        );

        assertEquals("Ítem no está en el carrito", exception.getMessage());
    }

    @Test
    void vaciar_deberiaEliminarTodosLosItemsDelCarrito() {
        // Se agregan varios libros y luego se vacía el carrito completo.
        Libro libro1 = crearLibro(10, "50000");
        Libro libro2 = crearLibro(10, "80000");

        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro1));
        when(repositorioLibro.buscarPorId(2L)).thenReturn(Optional.of(libro2));

        carritoService.agregarItem(1L, 1);
        carritoService.agregarItem(2L, 1);

        carritoService.vaciar();

        assertEquals(0, carritoService.cantidadItemsUnicos());
        assertEquals(0, carritoService.cantidadItems());
    }

    @Test
    void calcularTotal_conCarritoVacio_deberiaLanzarExcepcion() {
        // Segun la regla del servicio, no se puede calcular total si no hay items.
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> carritoService.calcularTotal()
        );

        assertEquals("El carrito está vacío", exception.getMessage());
    }

    @Test
    void validarParaCheckout_conCarritoVacio_deberiaLanzarExcepcion() {
        // El checkout no debe continuar si el carrito no tiene al menos un ítem.
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> carritoService.validarParaCheckout()
        );

        assertEquals("El carrito debe tener al menos 1 ítem para checkout", exception.getMessage());
    }

    @Test
    void validarParaCheckout_conCarritoConItems_noDeberiaLanzarExcepcion() {
        // Si el carrito tiene al menos un item, la validacion para checkout debe pasar.
        Long libroId = 1L;
        Libro libro = crearLibro(10, "50000");

        when(repositorioLibro.buscarPorId(libroId)).thenReturn(Optional.of(libro));

        carritoService.agregarItem(libroId, 1);

        assertDoesNotThrow(() -> carritoService.validarParaCheckout());
    }

    @Test
    void agregarItem_conCantidadCero_deberiaLanzarExcepcion() {
        // La cantidad debe ser positiva, por eso 0 no es valido.
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> carritoService.agregarItem(1L, 0)
        );

        assertEquals("Cantidad debe ser positiva", exception.getMessage());

        // Como la cantidad es inválida, ni siquiera debería consultar el repositorio.
        verify(repositorioLibro, never()).buscarPorId(anyLong());
    }

    @Test
    void agregarItem_conCantidadNegativa_deberiaLanzarExcepcion() {
        // Tampoco se permite agregar cantidades negativas.
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> carritoService.agregarItem(1L, -2)
        );

        assertEquals("Cantidad debe ser positiva", exception.getMessage());

        // No debe buscar el libro porque la validacion de cantidad ocurre primero.
        verify(repositorioLibro, never()).buscarPorId(anyLong());
    }

    @Test
    void agregarItem_conLibroInexistente_deberiaLanzarExcepcion() {
        // Se simula que el repositorio no encuentra el libro solicitado.
        Long libroId = 99L;

        when(repositorioLibro.buscarPorId(libroId)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> carritoService.agregarItem(libroId, 1)
        );

        assertEquals("Libro no encontrado: 99", exception.getMessage());
    }

    @Test
    void agregarItem_conStockInsuficiente_deberiaLanzarExcepcion() {
        // El libro existe, pero no tiene suficiente stock para la cantidad pedida.
        Long libroId = 1L;
        Libro libro = crearLibro(2, "50000");

        when(repositorioLibro.buscarPorId(libroId)).thenReturn(Optional.of(libro));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> carritoService.agregarItem(libroId, 5)
        );

        assertEquals("Stock insuficiente", exception.getMessage());
    }

    @Test
    void carritoCon10ItemsUnicos_deberiaPermitirse() {
        // El limite máximo es de 10 libros diferentes, entonces este caso todavía debe ser válido.
        for (long i = 1; i <= 10; i++) {
            Libro libro = crearLibro(10, "10000");
            when(repositorioLibro.buscarPorId(i)).thenReturn(Optional.of(libro));

            carritoService.agregarItem(i, 1);
        }

        assertEquals(10, carritoService.cantidadItemsUnicos());
        assertEquals(10, carritoService.cantidadItems());
    }

    @Test
    void agregarItemNumero11_deberiaLanzarExcepcion() {
        // Primero se llena el carrito con 10 libros diferentes.
        for (long i = 1; i <= 10; i++) {
            Libro libro = crearLibro(10, "10000");
            when(repositorioLibro.buscarPorId(i)).thenReturn(Optional.of(libro));

            carritoService.agregarItem(i, 1);
        }

        // Luego se intenta agregar un libro diferente adicional.
        Long libroId11 = 11L;
        Libro libro11 = crearLibro(10, "10000");

        when(repositorioLibro.buscarPorId(libroId11)).thenReturn(Optional.of(libro11));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> carritoService.agregarItem(libroId11, 1)
        );

        assertEquals(
                "El carrito no puede tener más de 10 ítems diferentes",
                exception.getMessage()
        );

        // Se verifica que el carrito siga teniendo solo 10 ítems únicos.
        assertEquals(10, carritoService.cantidadItemsUnicos());
    }

    @Test
    void agregarMasCantidadDeItemExistente_conCarritoLleno_deberiaPermitirse() {
        // Se llena el carrito con 10 libros diferentes.
        for (long i = 1; i <= 10; i++) {
            Libro libro = crearLibro(20, "10000");
            when(repositorioLibro.buscarPorId(i)).thenReturn(Optional.of(libro));

            carritoService.agregarItem(i, 1);
        }

        // Aunque el carrito ya tiene 10 ítems únicos, se puede aumentar la cantidad de uno existente.
        carritoService.agregarItem(1L, 4);

        // La cantidad de ítems únicos sigue siendo 10, pero la cantidad total aumenta.
        assertEquals(10, carritoService.cantidadItemsUnicos());
        assertEquals(14, carritoService.cantidadItems());
    }

    private Libro crearLibro(int stock, String precio) {
        // Se crea un mock de Libro para controlar stock y precio sin depender de una implementación real.
        Libro libro = mock(Libro.class);

        // Se usa lenient para evitar errores de Mockito cuando alguna prueba no usa todos los métodos mockeados.
        lenient().when(libro.getStock()).thenReturn(stock);
        lenient().when(libro.getPrecio()).thenReturn(new BigDecimal(precio));

        return libro;
    }
}
```
## Justificación

Estas pruebas verifican las reglas principales del carrito. La regla del máximo se valida con `cantidadItemsUnicos()`, porque el enunciado dice que el máximo de 10 corresponde a ítems diferentes, no a la suma de cantidades.

Por ejemplo, si el carrito tiene 10 libros distintos, no se puede agregar un libro nuevo. Sin embargo, sí se puede aumentar la cantidad de un libro que ya estaba en el carrito, porque eso no aumenta la cantidad de ítems únicos.

También se prueba que no se pueda hacer checkout con el carrito vacío, ya que el carrito debe tener mínimo un ítem para continuar con la compra.

En comparación con probar manualmente, estas pruebas permiten verificar automáticamente los casos normales, los casos borde y las excepciones principales del servicio.


