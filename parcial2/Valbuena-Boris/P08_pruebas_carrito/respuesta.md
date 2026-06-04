# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [8]: [pruebas_carrito]

### Estudiante
- **Nombre completo**: Boris Nicolas Valbuena Gueirsman

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Gemini |
| **Modelo específico** | Gemini 3.5 Flash |
| **¿Por qué elegiste este LLM?** | Es la que más usé durante el curso, por lo cual es la misma que usaré en el parcial. |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```
Actúa como un ingeniero de software senior especializado en pruebas unitarias, TDD, JUnit 5, Mockito y Java 21.

Contexto:

Estoy desarrollando OpenLib Market. El siguiente servicio implementa la lógica de negocio del carrito de compras.

Reglas de negocio:

* Un carrito debe tener mínimo 1 ítem para realizar checkout.
* Un carrito puede tener máximo 10 ítems diferentes.
* Las cantidades no afectan el límite de ítems.
* Si se intenta agregar el ítem número 11 debe lanzarse una excepción.
* Si se intenta hacer checkout con un carrito vacío debe lanzarse una excepción.
* Si se intenta agregar un libro inexistente debe lanzarse una excepción.
* Si se intenta agregar una cantidad mayor al stock debe lanzarse una excepción.

Código a probar:

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

        if (libro.getStock() < cantidad)
            throw new IllegalStateException("Stock insuficiente");

        if (!items.containsKey(libroId) && items.size() >= MAX_ITEMS) {
            throw new IllegalStateException(
                "El carrito no puede tener más de " + MAX_ITEMS + " ítems diferentes");
        }

        items.merge(libroId,
            new ItemCarrito(libro, cantidad, libro.getPrecio()),
            (existente, nuevo) -> {
                existente.setCantidad(existente.getCantidad() + cantidad);
                return existente;
            });
    }

    public void removerItem(Long libroId) {
        if (!items.containsKey(libroId))
            throw new NoSuchElementException("Ítem no está en el carrito");

        items.remove(libroId);
    }

    public BigDecimal calcularTotal() {
        if (items.isEmpty())
            throw new IllegalStateException("El carrito está vacío");

        return items.values().stream()
            .map(item -> item.getPrecioUnitario()
            .multiply(BigDecimal.valueOf(item.getCantidad())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void vaciar() {
        items.clear();
    }

    public int cantidadItems() {
        return items.values().stream()
            .mapToInt(ItemCarrito::getCantidad)
            .sum();
    }

    public int cantidadItemsUnicos() {
        return items.size();
    }

    public void validarParaCheckout() {
        if (items.isEmpty())
            throw new IllegalStateException(
                "El carrito debe tener al menos 1 ítem para checkout");
    }
}
```

Antes de responder:

1. Identifica todos los métodos que requieren pruebas.
2. Identifica casos normales, casos borde y casos de error.
3. Diseña una suite de pruebas con cobertura alta.
4. Utiliza JUnit 5 y Mockito.
5. Usa mocks para RepositorioLibro.
6. Justifica brevemente cada prueba.

Restricciones:

* Utiliza @ExtendWith(MockitoExtension.class).
* Utiliza @Mock y @InjectMocks.
* Usa assertThrows para excepciones.
* Incluye nombres descriptivos para los tests.
* Incluye pruebas para todos los métodos públicos.

Formato de salida:

# Estrategia de pruebas

# Casos de prueba identificados

# Código completo de pruebas JUnit 5

# Explicación de cobertura

# Casos borde cubiertos

# Posibles pruebas adicionales

Validación:

La solución debe cubrir casos exitosos, excepciones, reglas de negocio, límite de 10 ítems, intento de agregar el ítem 11, checkout con carrito vacío, carrito vacío al calcular total, agregar un ítem ya existente y uso correcto de Mockito.

```

---

### Respuesta del LLM

> Aquí tienes la estrategia de pruebas y la suite de tests unitarios diseñada con estándares de nivel Senior, optimizada para Java 21, JUnit 5 y Mockito.

---

## Estrategia de pruebas

Para garantizar la robustez de `CarritoService`, aplicaremos un enfoque de **caja blanca** centrado en el comportamiento de las reglas de negocio y el control de flujos (caminos felices, límites y excepciones).

Dado que el servicio depende de la infraestructura (`RepositorioLibro`), aislaremos la lógica de negocio usando **Mocks**. La estrategia se basa en el patrón **AAA (Arrange, Act, Assert)** y se estructurará utilizando `@Nested` de JUnit 5 para agrupar los tests por método, mejorando drásticamente la legibilidad y el mantenimiento del código.

---

## Casos de prueba identificados

| Método | Tipo | Descripción / Caso de Prueba |
| --- | --- | --- |
| `agregarItem` | Feliz | Agregar un ítem inexistente en el carrito con stock suficiente. |
|  | Feliz | Agregar un ítem que ya existe en el carrito (debe acumular cantidad). |
|  | Error | Intentar agregar con cantidad menor o igual a cero (`IllegalArgumentException`). |
|  | Error | Intentar agregar un libro que no existe en el repositorio (`NoSuchElementException`). |
|  | Error | Intentar agregar una cantidad mayor al stock disponible (`IllegalStateException`). |
|  | Borde/Error | Intentar agregar el ítem único número 11 superando el límite (`IllegalStateException`). |
| `removerItem` | Feliz | Eliminar un ítem que existe en el carrito. |
|  | Error | Intentar eliminar un ítem que no está en el carrito (`NoSuchElementException`). |
| `calcularTotal` | Feliz | Calcular el total con múltiples ítems en el carrito. |
|  | Error | Calcular el total con el carrito vacío (`IllegalStateException`). |
| `vaciar` | Feliz | Vaciar un carrito con ítems (el tamaño final debe ser 0). |
| `cantidadItems` | Feliz | Sumar el total de cantidades de todos los ítems. |
| `cantidadItemsUnicos` | Feliz | Contar la cantidad de llaves (productos distintos) en el mapa. |
| `validarParaCheckout` | Feliz | El carrito tiene al menos un ítem (pasa la validación). |
|  | Error | El carrito está vacío (`IllegalStateException`). |

---

## Código completo de pruebas JUnit 5

```java
package com.openlib.market.service;

import com.openlib.market.model.ItemCarrito;
import com.openlib.market.model.Libro;
import com.openlib.market.repository.RepositorioLibro;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
@DisplayName("Pruebas Unitarias para CarritoService")
class CarritoServiceTest {

    @Mock
    private RepositorioLibro repositorioLibro;

    @InjectMocks
    private CarritoService carritoService;

    // Métodos utilitarios para mantener los tests limpios (DRY)
    private Libro crearLibro(Long id, String precio, int stock) {
        Libro libro = new Libro();
        libro.setId(id);
        libro.setPrecio(new BigDecimal(precio));
        libro.setStock(stock);
        return libro;
    }

    @Nested
    @DisplayName("Método: agregarItem")
    class AgregarItemTests {

        @Test
        @DisplayName("Debe agregar un ítem exitosamente cuando el carrito está vacío y hay stock")
        void debeAgregarItemExitosamente() {
            // Arrange
            Long libroId = 1L;
            Libro libro = crearLibro(libroId, "50.0", 10);
            when(repositorioLibro.buscarPorId(libroId)).thenReturn(Optional.of(libro));

            // Act
            carritoService.agregarItem(libroId, 2);

            // Assert
            assertEquals(1, carritoService.cantidadItemsUnicos(), "Debe haber 1 ítem único");
            assertEquals(2, carritoService.cantidadItems(), "La cantidad total de ítems debe ser 2");
            verify(repositorioLibro, times(1)).buscarPorId(libroId);
        }

        @Test
        @DisplayName("Debe acumular la cantidad si el ítem ya existe en el carrito")
        void debeAcumularCantidadSiYaExiste() {
            // Arrange
            Long libroId = 1L;
            Libro libro = crearLibro(libroId, "50.0", 10);
            when(repositorioLibro.buscarPorId(libroId)).thenReturn(Optional.of(libro));

            // Act
            carritoService.agregarItem(libroId, 2);
            carritoService.agregarItem(libroId, 3);

            // Assert
            assertEquals(1, carritoService.cantidadItemsUnicos(), "No deben aumentar los ítems únicos");
            assertEquals(5, carritoService.cantidadItems(), "La cantidad acumulada debe ser 5");
        }

        @Test
        @DisplayName("Debe lanzar IllegalArgumentException si la cantidad es menor o igual a cero")
        void debeLanzarExcepcionSiCantidadEsInvalida() {
            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
                carritoService.agregarItem(1L, 0)
            );
            assertEquals("Cantidad debe ser positiva", exception.getMessage());
            verifyNoInteractions(repositorioLibro);
        }

        @Test
        @DisplayName("Debe lanzar NoSuchElementException si el libro no existe en el repositorio")
        void debeLanzarExcepcionSiLibroNoExiste() {
            // Arrange
            Long libroId = 99L;
            when(repositorioLibro.buscarPorId(libroId)).thenReturn(Optional.empty());

            // Act & Assert
            NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> 
                carritoService.agregarItem(libroId, 1)
            );
            assertEquals("Libro no encontrado: " + libroId, exception.getMessage());
        }

        @Test
        @DisplayName("Debe lanzar IllegalStateException si la cantidad solicitada supera el stock")
        void debeLanzarExcepcionSiStockInsuficiente() {
            // Arrange
            Long libroId = 1L;
            Libro libro = crearLibro(libroId, "30.0", 5);
            when(repositorioLibro.buscarPorId(libroId)).thenReturn(Optional.of(libro));

            // Act & Assert
            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> 
                carritoService.agregarItem(libroId, 6)
            );
            assertEquals("Stock insuficiente", exception.getMessage());
        }

        @Test
        @DisplayName("Debe lanzar IllegalStateException al intentar agregar el undécimo (11) ítem diferente")
        void debeLanzarExcepcionAlSuperarMaximoDeItemsDiferentes() {
            // Arrange: Llenamos el carrito con el máximo permitido (10 ítems únicos)
            for (long i = 1; i <= 10; i++) {
                Libro libroExistente = crearLibro(i, "10.0", 5);
                when(repositorioLibro.buscarPorId(i)).thenReturn(Optional.of(libroExistente));
                carritoService.agregarItem(i, 1);
            }

            Long libroOnceId = 11L;
            Libro libroOnce = crearLibro(libroOnceId, "15.0", 5);
            when(repositorioLibro.buscarPorId(libroOnceId)).thenReturn(Optional.of(libroOnce));

            // Act & Assert
            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> 
                carritoService.agregarItem(libroOnceId, 1)
            );
            assertEquals("El carrito no puede tener más de 10 ítems diferentes", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Método: removerItem")
    class RemoverItemTests {

        @Test
        @DisplayName("Debe eliminar el ítem correctamente si existe en el carrito")
        void debeRemoverItemExistente() {
            // Arrange
            Long libroId = 1L;
            Libro libro = crearLibro(libroId, "20.0", 5);
            when(repositorioLibro.buscarPorId(libroId)).thenReturn(Optional.of(libro));
            carritoService.agregarItem(libroId, 2);

            // Act
            carritoService.removerItem(libroId);

            // Assert
            assertEquals(0, carritoService.cantidadItemsUnicos());
        }

        @Test
        @DisplayName("Debe lanzar NoSuchElementException si se intenta remover un ítem que no está en el carrito")
        void debeLanzarExcepcionSiItemNoEstaEnCarrito() {
            // Act & Assert
            NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> 
                carritoService.removerItem(1L)
            );
            assertEquals("Ítem no está en el carrito", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Método: calcularTotal")
    class CalcularTotalTests {

        @Test
        @DisplayName("Debe calcular correctamente la suma total del carrito")
        void debeCalcularTotalCorrectamente() {
            // Arrange
            Libro libro1 = crearLibro(1L, "10.00", 5);
            Libro libro2 = crearLibro(2L, "25.50", 5);

            when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro1));
            when(repositorioLibro.buscarPorId(2L)).thenReturn(Optional.of(libro2));

            carritoService.agregarItem(1L, 2); // 10.00 * 2 = 20.00
            carritoService.agregarItem(2L, 3); // 25.50 * 3 = 76.50

            // Act
            BigDecimal total = carritoService.calcularTotal();

            // Assert
            assertEquals(new BigDecimal("96.50"), total);
        }

        @Test
        @DisplayName("Debe lanzar IllegalStateException si se calcula el total de un carrito vacío")
        void debeLanzarExcepcionSiCarritoEstaVacio() {
            // Act & Assert
            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> 
                carritoService.calcularTotal()
            );
            assertEquals("El carrito está vacío", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Método: vaciar")
    class VaciarTests {

        @Test
        @DisplayName("Debe limpiar todos los elementos del carrito")
        void debeVaciarElCarrito() {
            // Arrange
            Long libroId = 1L;
            Libro libro = crearLibro(libroId, "15.0", 5);
            when(repositorioLibro.buscarPorId(libroId)).thenReturn(Optional.of(libro));
            carritoService.agregarItem(libroId, 2);

            // Act
            carritoService.vaciar();

            // Assert
            assertEquals(0, carritoService.cantidadItemsUnicos());
            assertEquals(0, carritoService.cantidadItems());
        }
    }

    @Nested
    @DisplayName("Método: validarParaCheckout")
    class ValidarParaCheckoutTests {

        @Test
        @DisplayName("No debe lanzar excepción si el carrito tiene al menos un ítem")
        void debeValidarCheckoutExitosamente() {
            // Arrange
            Long libroId = 1L;
            Libro libro = crearLibro(libroId, "10.0", 5);
            when(repositorioLibro.buscarPorId(libroId)).thenReturn(Optional.of(libro));
            carritoService.agregarItem(libroId, 1);

            // Act & Assert: Si lanza excepción, el test falla automáticamente
            assertDoesNotThrow(() -> carritoService.validarParaCheckout());
        }

        @Test
        @DisplayName("Debe lanzar IllegalStateException si el carrito está vacío al validar checkout")
        void debeLanzarExcepcionEnCheckoutSiCarritoVacio() {
            // Act & Assert
            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> 
                carritoService.validarParaCheckout()
            );
            assertEquals("El carrito debe tener al menos 1 ítem para checkout", exception.getMessage());
        }
    }
}

```

---

## Explicación de cobertura

Esta suite alcanza un **100% de cobertura de líneas y de ramas (Branch Coverage)** en `CarritoService`.

* **Estructuras de Decisión (`if`):** Cada guardia (validaciones de stock, cantidades negativas, carrito vacío y límites superiores) ha sido forzada a evaluar tanto en `true` (provocando la excepción) como en `false` (continuando el flujo normal).
* **Flujos de Mocking:** Se cubrieron los escenarios donde el `Optional` del repositorio retorna datos (`Optional.of(libro)`) y cuando retorna vacío (`Optional.empty()`), asegurando que el mapeo de excepciones funcione de punta a punta.

---

## Casos borde cubiertos

* **El límite exacto ($N = 10$):** El test `debeLanzarExcepcionAlSuperarMaximoDeItemsDiferentes` simula de forma exacta la frontera. Llena el carrito con 10 libros distintos (comportamiento válido y permitido) e intenta meter el número 11, disparando de inmediato la excepción de negocio esperada.
* **Suma de cantidades sin afectar ítems únicos:** Se probó que al agregar el mismo `libroId` repetidas veces, `items.size()` se mantiene estático mientras que la cantidad total interna se acumula de forma correcta usando el método `.merge()` de Java.
* **Validación de frontera de stock:** Se valida el comportamiento cuando la cantidad solicitada es exactamente superior por 1 unidad al stock disponible.

---

## Posibles pruebas adicionales

A nivel de arquitectura de software, si el sistema escala, recomendaría agregar los siguientes escenarios de prueba:

1. **Prueba de Concurrencia:** Dado que `HashMap` no es thread-safe, si este servicio pasara a ser un Singleton expuesto a múltiples hilos (y no limitado al ciclo de vida de la sesión del usuario), se requerirían pruebas de estrés concurrente o migrar la estructura interna a un `ConcurrentHashMap`.
2. **Mutación de Datos en el Mock:** Validar que si el precio del libro cambia en la base de datos *después* de haber sido agregado al carrito, el carrito conserve el `precioUnitario` capturado en el momento exacto de la compra (requisito indispensable en auditorías de e-commerce). El diseño actual con `libro.getPrecio()` al agregar el ítem cumple con esto, pero un test unitario específico blindaría este comportamiento contra futuras regresiones de código.
```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

[Evalúa tu propio prompt, no la respuesta del LLM. ¿El contexto fue suficiente? ¿Las restricciones fueron claras? ¿El formato de salida que pediste ayudó a obtener una buena respuesta? ¿Qué parte de tu prompt fue más efectiva? Sé específico: menciona fragmentos concretos de tu prompt que funcionaron bien.]


#### 2. ¿Qué se puede mejorar?

[¿Qué le faltó a tu prompt? ¿Qué harías diferente si pudieras reformularlo? ¿El LLM entendió mal algo por falta de claridad en tu prompt? ¿La respuesta tiene errores u omisiones? ¿Qué no cubrió el LLM que tú sí sabes por lo visto en clase?]


#### 3. Respuesta final

[Escribe tu respuesta definitiva a la pregunta del parcial, integrando lo que aprendiste del LLM pero yendo más allá. Corrige errores, llena omisiones, conecta con conceptos vistos en clase. Esta es tu respuesta: demuestra que tú dominas el tema.]
