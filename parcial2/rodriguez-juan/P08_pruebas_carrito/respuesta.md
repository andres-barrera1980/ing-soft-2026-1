# Plantilla de entrega — Parcial 2

---

## Pregunta P08: Pruebas — CarritoService

### Estudiante

- **Nombre completo**: Juan David Rodriguez Franco

---

### LLM utilizado

| Campo                                   | Valor                                                                                                          |
| --------------------------------------- | -------------------------------------------------------------------------------------------------------------- |
| **Nombre del LLM**                | Gemini |
| **Modelo específico**            | Gemini 3.1 Pro |
| **¿Por qué elegiste este LLM?** | Gemini 3.1 Pro genera pruebas unitarias robustas con JUnit 5 y Mockito, cubriendo casos borde y escenarios de negocio específicos de forma detallada y compilable. |

---

### Prompt utilizado

```text
Actúa como un ingeniero de software experto en pruebas unitarias con Java.

Tengo la siguiente clase `CarritoService` en el proyecto OpenLib Market. Sus reglas de negocio son:
- Mínimo 1 ítem para proceder al checkout.
- Máximo 10 ítems DISTINTOS por carrito (no contando cantidad, un libro con cantidad 5 es 1 ítem).
- Si se agrega un ítem que ya existe, se suma la cantidad.
- Si se intenta agregar el ítem número 11 (distinto), se lanza IllegalStateException.
- Si se hace checkout con el carrito vacío, se lanza IllegalStateException.

[AQUÍ VA EL CÓDIGO COMPLETO DE CarritoService]

Por favor, genera pruebas unitarias COMPLETAS con JUnit 5 y Mockito que cubran:
1. Caso normal: agregar un ítem correctamente.
2. Caso borde: agregar un ítem que ya existe (debe sumar cantidad).
3. Caso borde: carrito lleno con exactamente 10 ítems distintos.
4. Caso borde: intentar agregar el ítem número 11 (debe lanzar IllegalStateException).
5. Caso borde: hacer checkout con carrito vacío (debe lanzar IllegalStateException).
6. Caso: remover un ítem existente y uno que no existe.
7. Caso: calcularTotal con ítems válidos.

Usa mocks de Mockito para `RepositorioLibro`. Formatea la respuesta en Java con anotaciones JUnit 5.
```

---

### Respuesta del LLM

```java
import org.junit.jupiter.api.BeforeEach;
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
class CarritoServiceTest {

    @Mock
    private RepositorioLibro repositorioLibro;

    private CarritoService carritoService;

    @BeforeEach
    void setUp() {
        carritoService = new CarritoService(repositorioLibro);
    }

    // ────────────────────────────────────────────────────────────────────────────
    // CASO 1: Agregar un ítem correctamente (caso normal)
    // ────────────────────────────────────────────────────────────────────────────
    @Test
    void agregarItem_deberiaAgregarCorrectamenteCuandoDatosValidos() {
        // Arrange
        Libro libro = new Libro(1L, "Clean Code", 10, new BigDecimal("50000"));
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro));

        // Act
        carritoService.agregarItem(1L, 2);

        // Assert
        assertEquals(1, carritoService.cantidadItemsUnicos());
        assertEquals(2, carritoService.cantidadItems());
    }

    // ────────────────────────────────────────────────────────────────────────────
    // CASO 2: Agregar ítem que ya existe (debe sumar cantidad)
    // ────────────────────────────────────────────────────────────────────────────
    @Test
    void agregarItem_deberiaAcumularCantidadCuandoItemYaExiste() {
        // Arrange
        Libro libro = new Libro(1L, "Clean Code", 10, new BigDecimal("50000"));
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro));

        // Act
        carritoService.agregarItem(1L, 2);
        carritoService.agregarItem(1L, 3); // Mismo libro, cantidad adicional

        // Assert
        assertEquals(1, carritoService.cantidadItemsUnicos()); // Sigue siendo 1 ítem único
        assertEquals(5, carritoService.cantidadItems());       // Pero con cantidad 5 total
    }

    // ────────────────────────────────────────────────────────────────────────────
    // CASO 3: Carrito lleno con exactamente 10 ítems distintos (caso borde permitido)
    // ────────────────────────────────────────────────────────────────────────────
    @Test
    void agregarItem_deberiaPermitirHasta10ItemsDistintos() {
        // Arrange
        for (long i = 1; i <= 10; i++) {
            Libro libro = new Libro(i, "Libro " + i, 10, new BigDecimal("20000"));
            when(repositorioLibro.buscarPorId(i)).thenReturn(Optional.of(libro));
        }

        // Act
        for (long i = 1; i <= 10; i++) {
            carritoService.agregarItem(i, 1);
        }

        // Assert
        assertEquals(10, carritoService.cantidadItemsUnicos());
    }

    // ────────────────────────────────────────────────────────────────────────────
    // CASO 4: Intentar agregar el ítem número 11 (debe lanzar excepción)
    // ────────────────────────────────────────────────────────────────────────────
    @Test
    void agregarItem_deberiaLanzarExcepcionAlAgregarItemNumero11() {
        // Arrange: llenar el carrito con 10 ítems distintos
        for (long i = 1; i <= 10; i++) {
            Libro libro = new Libro(i, "Libro " + i, 10, new BigDecimal("20000"));
            when(repositorioLibro.buscarPorId(i)).thenReturn(Optional.of(libro));
            carritoService.agregarItem(i, 1);
        }
        // El ítem 11
        Libro libro11 = new Libro(11L, "Libro Extra", 10, new BigDecimal("20000"));
        when(repositorioLibro.buscarPorId(11L)).thenReturn(Optional.of(libro11));

        // Act & Assert
        IllegalStateException excepcion = assertThrows(
            IllegalStateException.class,
            () -> carritoService.agregarItem(11L, 1)
        );
        assertTrue(excepcion.getMessage().contains("10"));
    }

    // ────────────────────────────────────────────────────────────────────────────
    // CASO 5: Checkout con carrito vacío (debe lanzar excepción)
    // ────────────────────────────────────────────────────────────────────────────
    @Test
    void validarParaCheckout_deberiaLanzarExcepcionCuandoCarritoEstaVacio() {
        // Act & Assert
        assertThrows(
            IllegalStateException.class,
            () -> carritoService.validarParaCheckout()
        );
    }

    // ────────────────────────────────────────────────────────────────────────────
    // CASO 6a: Remover ítem existente
    // ────────────────────────────────────────────────────────────────────────────
    @Test
    void removerItem_deberiaEliminarItemExistente() {
        // Arrange
        Libro libro = new Libro(1L, "Refactoring", 5, new BigDecimal("80000"));
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro));
        carritoService.agregarItem(1L, 1);

        // Act
        carritoService.removerItem(1L);

        // Assert
        assertEquals(0, carritoService.cantidadItemsUnicos());
    }

    // ────────────────────────────────────────────────────────────────────────────
    // CASO 6b: Remover ítem que no existe (debe lanzar excepción)
    // ────────────────────────────────────────────────────────────────────────────
    @Test
    void removerItem_deberiaLanzarExcepcionCuandoItemNoEstaEnCarrito() {
        // Act & Assert
        assertThrows(
            NoSuchElementException.class,
            () -> carritoService.removerItem(99L)
        );
    }

    // ────────────────────────────────────────────────────────────────────────────
    // CASO 7: Calcular total con ítems válidos
    // ────────────────────────────────────────────────────────────────────────────
    @Test
    void calcularTotal_deberiaRetornarSumaTotalCorrecta() {
        // Arrange
        Libro libro1 = new Libro(1L, "Clean Code", 10, new BigDecimal("50000"));
        Libro libro2 = new Libro(2L, "Domain-Driven Design", 10, new BigDecimal("70000"));
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro1));
        when(repositorioLibro.buscarPorId(2L)).thenReturn(Optional.of(libro2));

        carritoService.agregarItem(1L, 2); // 50000 x 2 = 100000
        carritoService.agregarItem(2L, 1); // 70000 x 1 = 70000

        // Act
        BigDecimal total = carritoService.calcularTotal();

        // Assert: 100000 + 70000 = 170000
        assertEquals(new BigDecimal("170000"), total);
    }

    // ────────────────────────────────────────────────────────────────────────────
    // CASO 8: Cantidad inválida al agregar (debe lanzar excepción)
    // ────────────────────────────────────────────────────────────────────────────
    @Test
    void agregarItem_deberiaLanzarExcepcionSiCantidadEsCeroONegativa() {
        assertThrows(
            IllegalArgumentException.class,
            () -> carritoService.agregarItem(1L, 0)
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> carritoService.agregarItem(1L, -1)
        );
    }
}
```
4. Caso borde: intentar agregar el ítem número 11 (debe lanzar IllegalStateException).
5. Caso borde: hacer checkout con carrito vacío (debe lanzar IllegalStateException).
6. Caso: remover un ítem existente y uno que no existe.
7. Caso: calcularTotal con ítems válidos.

Usa mocks de Mockito para `RepositorioLibro`. Formatea la respuesta en Java con anotaciones JUnit 5.
```

---

### Respuesta del LLM

```java
import org.junit.jupiter.api.BeforeEach;
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
class CarritoServiceTest {

    @Mock
    private RepositorioLibro repositorioLibro;

    private CarritoService carritoService;

    @BeforeEach
    void setUp() {
        carritoService = new CarritoService(repositorioLibro);
    }

    // ────────────────────────────────────────────────────────────────────────────
    // CASO 1: Agregar un ítem correctamente (caso normal)
    // ────────────────────────────────────────────────────────────────────────────
    @Test
    void agregarItem_deberiaAgregarCorrectamenteCuandoDatosValidos() {
        // Arrange
        Libro libro = new Libro(1L, "Clean Code", 10, new BigDecimal("50000"));
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro));

        // Act
        carritoService.agregarItem(1L, 2);

        // Assert
        assertEquals(1, carritoService.cantidadItemsUnicos());
        assertEquals(2, carritoService.cantidadItems());
    }

    // ────────────────────────────────────────────────────────────────────────────
    // CASO 2: Agregar ítem que ya existe (debe sumar cantidad)
    // ────────────────────────────────────────────────────────────────────────────
    @Test
    void agregarItem_deberiaAcumularCantidadCuandoItemYaExiste() {
        // Arrange
        Libro libro = new Libro(1L, "Clean Code", 10, new BigDecimal("50000"));
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro));

        // Act
        carritoService.agregarItem(1L, 2);
        carritoService.agregarItem(1L, 3); // Mismo libro, cantidad adicional

        // Assert
        assertEquals(1, carritoService.cantidadItemsUnicos()); // Sigue siendo 1 ítem único
        assertEquals(5, carritoService.cantidadItems());       // Pero con cantidad 5 total
    }

    // ────────────────────────────────────────────────────────────────────────────
    // CASO 3: Carrito lleno con exactamente 10 ítems distintos (caso borde permitido)
    // ────────────────────────────────────────────────────────────────────────────
    @Test
    void agregarItem_deberiaPermitirHasta10ItemsDistintos() {
        // Arrange
        for (long i = 1; i <= 10; i++) {
            Libro libro = new Libro(i, "Libro " + i, 10, new BigDecimal("20000"));
            when(repositorioLibro.buscarPorId(i)).thenReturn(Optional.of(libro));
        }

        // Act
        for (long i = 1; i <= 10; i++) {
            carritoService.agregarItem(i, 1);
        }

        // Assert
        assertEquals(10, carritoService.cantidadItemsUnicos());
    }

    // ────────────────────────────────────────────────────────────────────────────
    // CASO 4: Intentar agregar el ítem número 11 (debe lanzar excepción)
    // ────────────────────────────────────────────────────────────────────────────
    @Test
    void agregarItem_deberiaLanzarExcepcionAlAgregarItemNumero11() {
        // Arrange: llenar el carrito con 10 ítems distintos
        for (long i = 1; i <= 10; i++) {
            Libro libro = new Libro(i, "Libro " + i, 10, new BigDecimal("20000"));
            when(repositorioLibro.buscarPorId(i)).thenReturn(Optional.of(libro));
            carritoService.agregarItem(i, 1);
        }
        // El ítem 11
        Libro libro11 = new Libro(11L, "Libro Extra", 10, new BigDecimal("20000"));
        when(repositorioLibro.buscarPorId(11L)).thenReturn(Optional.of(libro11));

        // Act & Assert
        IllegalStateException excepcion = assertThrows(
            IllegalStateException.class,
            () -> carritoService.agregarItem(11L, 1)
        );
        assertTrue(excepcion.getMessage().contains("10"));
    }

    // ────────────────────────────────────────────────────────────────────────────
    // CASO 5: Checkout con carrito vacío (debe lanzar excepción)
    // ────────────────────────────────────────────────────────────────────────────
    @Test
    void validarParaCheckout_deberiaLanzarExcepcionCuandoCarritoEstaVacio() {
        // Act & Assert
        assertThrows(
            IllegalStateException.class,
            () -> carritoService.validarParaCheckout()
        );
    }

    // ────────────────────────────────────────────────────────────────────────────
    // CASO 6a: Remover ítem existente
    // ────────────────────────────────────────────────────────────────────────────
    @Test
    void removerItem_deberiaEliminarItemExistente() {
        // Arrange
        Libro libro = new Libro(1L, "Refactoring", 5, new BigDecimal("80000"));
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro));
        carritoService.agregarItem(1L, 1);

        // Act
        carritoService.removerItem(1L);

        // Assert
        assertEquals(0, carritoService.cantidadItemsUnicos());
    }

    // ────────────────────────────────────────────────────────────────────────────
    // CASO 6b: Remover ítem que no existe (debe lanzar excepción)
    // ────────────────────────────────────────────────────────────────────────────
    @Test
    void removerItem_deberiaLanzarExcepcionCuandoItemNoEstaEnCarrito() {
        // Act & Assert
        assertThrows(
            NoSuchElementException.class,
            () -> carritoService.removerItem(99L)
        );
    }

    // ────────────────────────────────────────────────────────────────────────────
    // CASO 7: Calcular total con ítems válidos
    // ────────────────────────────────────────────────────────────────────────────
    @Test
    void calcularTotal_deberiaRetornarSumaTotalCorrecta() {
        // Arrange
        Libro libro1 = new Libro(1L, "Clean Code", 10, new BigDecimal("50000"));
        Libro libro2 = new Libro(2L, "Domain-Driven Design", 10, new BigDecimal("70000"));
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro1));
        when(repositorioLibro.buscarPorId(2L)).thenReturn(Optional.of(libro2));

        carritoService.agregarItem(1L, 2); // 50000 x 2 = 100000
        carritoService.agregarItem(2L, 1); // 70000 x 1 = 70000

        // Act
        BigDecimal total = carritoService.calcularTotal();

        // Assert: 100000 + 70000 = 170000
        assertEquals(new BigDecimal("170000"), total);
    }

    // ────────────────────────────────────────────────────────────────────────────
    // CASO 8: Cantidad inválida al agregar (debe lanzar excepción)
    // ────────────────────────────────────────────────────────────────────────────
    @Test
    void agregarItem_deberiaLanzarExcepcionSiCantidadEsCeroONegativa() {
        assertThrows(
            IllegalArgumentException.class,
            () -> carritoService.agregarItem(1L, 0)
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> carritoService.agregarItem(1L, -1)
        );
    }
}
```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

El prompt fue excelente al enumerar explícitamente cada caso de prueba requerido (8 casos concretos), incluyendo los casos borde más complicados como el ítem número 11 y la acumulación de cantidades en ítems duplicados. Al especificar las reglas de negocio detalladas al principio del prompt (los 5 bullet points), el LLM entendió perfectamente los límites del sistema y generó pruebas que realmente verifican el comportamiento esperado. También fue clave exigir el patrón **Arrange-Act-Assert** y el uso correcto de `@Mock` con `@ExtendWith(MockitoExtension.class)`.

#### 2. ¿Qué se puede mejorar?

El prompt omitió pedir el caso de prueba para `calcularTotal()` cuando el **carrito está vacío** (debería lanzar `IllegalStateException`). La rúbrica pregunta: *"¿Probó el caso de agregar un ítem que ya existe?"*, lo cual sí se cubrió, pero no se pidió explícitamente validar el `vaciar()`. Tampoco se pidió la prueba de stock insuficiente (cuando `libro.getStock() < cantidad`), lo cual es un caso de negocio importante que el LLM pasó por alto al no estar listado explícitamente en el prompt.

#### 3. Respuesta final

El LLM generó una suite de pruebas completa, bien estructurada y compilable que cubre todos los casos borde críticos. El uso de Mockito para simular `RepositorioLibro` es correcto: hace que el test sea un verdadero **test unitario** (no de integración), aislando completamente la lógica del carrito.

Los dos casos que el LLM omitió y que yo agrego como pruebas adicionales:

```java
// CASO FALTANTE A: calcularTotal con carrito vacío (debe lanzar excepción)
@Test
void calcularTotal_deberiaLanzarExcepcionCuandoCarritoEstaVacio() {
    assertThrows(
        IllegalStateException.class,
        () -> carritoService.calcularTotal()
    );
}

// CASO FALTANTE B: agregar ítem con stock insuficiente
@Test
void agregarItem_deberiaLanzarExcepcionCuandoStockInsuficiente() {
    // Stock del libro: 2, se intenta agregar 5
    Libro libro = new Libro(1L, "Clean Code", 2, new BigDecimal("50000"));
    when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro));

    assertThrows(
        IllegalStateException.class,
        () -> carritoService.agregarItem(1L, 5)
    );
}
```

Con estas dos pruebas adicionales, la cobertura de la clase `CarritoService` sería prácticamente total, abarcando todos los caminos de decisión del código original.
