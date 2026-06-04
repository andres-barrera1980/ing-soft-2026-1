
## Pregunta [08]: [Generar y complementar pruebas unitarias con JUnit 5 y Mockito]

### Estudiante
- **Nombre completo**: Julian Felipe Rojas Almanza

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** |Gpt |
| **Modelo específico** | Gpt-4o |
| **¿Por qué elegiste este LLM?** | Según la tabla de la guía, es uno de los mejores modelos para la "Generación de pruebas" y cobertura de casos borde. Entienden a la perfección cómo orquestar el ciclo de vida de JUnit 5  y cómo simular el comportamiento de dependencias mediante Mockito sin generar código inconsistente. |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```
Actúa como un Ingeniero de Software Senior especialista en QA y Pruebas Unitarias Automatizadas. Estoy resolviendo un ejercicio técnico académico para el sistema "OpenLib Market" y necesito que generes una suite de pruebas unitarias robusta y exhaustiva para un componente crítico.

[CONTEXTO]
Tenemos la clase `CarritoService` encargada de gestionar el flujo del carrito de compras en OpenLib Market. Sus reglas de negocio estipulan que un carrito debe tener un mínimo de 1 ítem para proceder al checkout y un máximo de 10 ítems únicos diferentes (sin importar la cantidad de ejemplares por ítem). Adjunto el código:

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

[PROBLEMA / TAREA]
Necesito que escribas la clase completa de pruebas unitarias en Java utilizando **JUnit 5** y **Mockito**. La suite debe cubrir de manera obligatoria y explícita los siguientes escenarios:
1. **Casos normales**: Agregar ítems válidos, calcular el total correctamente y remover ítems existentes.
2. **Casos límite (Boundary Testing)**: Llenar el carrito con exactamente 10 ítems únicos diferentes (límite superior permitido).
3. **Casos de error y Excepciones**:
   - Intentar agregar el ítem único número 11 (debe lanzar `IllegalStateException`).
   - Intentar agregar un ítem con cantidad cero o negativa (`IllegalArgumentException`).
   - Intentar agregar un libro inexistente en el repositorio (`NoSuchElementException`).
   - Intentar agregar un libro cuyo stock sea menor a la cantidad solicitada (`IllegalStateException`).
   - Ejecutar `calcularTotal()` o `validarParaCheckout()` en un carrito completamente vacío (`IllegalStateException`).
4. **Caso de mutación**: Agregar un ítem que ya existe en el carrito para verificar que se incrementa su cantidad acumulada en lugar de crear un nuevo ítem único.

[RESTRICCIONES]
- Estructura interna: Diseña cada prueba siguiendo el patrón **AAA** (Arrange, Act, Assert) comentando claramente cada sección dentro del método.
- Aislamiento: Utiliza la extensión de Mockito (`@ExtendWith(MockitoExtension.class)`) para mockear de forma limpia el componente `RepositorioLibro`. No utilices bases de datos reales ni dependencias externas.
- El código resultante debe ser válido, utilizando las aserciones modernas de JUnit 5 (`assertThrows`, `assertEquals`, etc.).

[FORMATO DE SALIDA]
Devuelve tu respuesta estructurada en Markdown utilizando única y estrictamente estos dos encabezados:
- ### 1. Estrategia de Cobertura y Casos de Prueba Diseñados
- ### 2. Código de la Clase de Pruebas Unitarias 

[VALIDACIÓN]
Consideraré que tu respuesta es excelente si la suite compila conceptualmente, si mockea de forma correcta los retornos de `Optional<Libro>` del repositorio, y si no pasa por alto ninguna de las excepciones definidas en las reglas de negocio del componente.
```

---

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**
# Solución

### 1. Estrategia de Cobertura y Casos de Prueba Diseñados

La suite cubre los siguientes escenarios:

#### Casos normales

* Agregar un ítem válido al carrito.
* Calcular correctamente el total del carrito.
* Remover un ítem existente.

#### Casos límite (Boundary Testing)

* Permitir exactamente 10 ítems únicos diferentes.
* Rechazar el ítem único número 11.

#### Casos de error y excepciones

* Cantidad igual a cero.
* Cantidad negativa.
* Libro inexistente.
* Stock insuficiente.
* Calcular total con carrito vacío.
* Validar checkout con carrito vacío.
* Remover un ítem inexistente.

#### Caso de mutación

* Agregar dos veces el mismo libro y verificar que:

  * Se mantiene un único ítem.
  * La cantidad acumulada aumenta correctamente.

---

### 2. Código de la Clase de Pruebas Unitarias (Java 21 + JUnit 5)

```java
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
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
        carritoService = new CarritoService(repositorioLibro);
    }

    private Libro crearLibro(
            Long id,
            String titulo,
            double precio,
            int stock) {

        Libro libro = mock(Libro.class);

        when(libro.getId()).thenReturn(id);
        when(libro.getTitulo()).thenReturn(titulo);
        when(libro.getPrecio())
                .thenReturn(BigDecimal.valueOf(precio));
        when(libro.getStock()).thenReturn(stock);

        return libro;
    }

    @Test
    void debeAgregarItemValidoAlCarrito() {

        // Arrange
        Libro libro = crearLibro(
                1L,
                "Clean Architecture",
                50000,
                20);

        when(repositorioLibro.buscarPorId(1L))
                .thenReturn(Optional.of(libro));

        // Act
        carritoService.agregarItem(1L, 2);

        // Assert
        assertEquals(
                1,
                carritoService.cantidadItemsUnicos());

        assertEquals(
                2,
                carritoService.cantidadItems());
    }

    @Test
    void debeCalcularTotalCorrectamente() {

        // Arrange
        Libro libro = crearLibro(
                1L,
                "DDD",
                30000,
                10);

        when(repositorioLibro.buscarPorId(1L))
                .thenReturn(Optional.of(libro));

        carritoService.agregarItem(1L, 3);

        // Act
        BigDecimal total =
                carritoService.calcularTotal();

        // Assert
        assertEquals(
                BigDecimal.valueOf(90000),
                total);
    }

    @Test
    void debeRemoverItemExistente() {

        // Arrange
        Libro libro = crearLibro(
                1L,
                "Refactoring",
                40000,
                10);

        when(repositorioLibro.buscarPorId(1L))
                .thenReturn(Optional.of(libro));

        carritoService.agregarItem(1L, 1);

        // Act
        carritoService.removerItem(1L);

        // Assert
        assertEquals(
                0,
                carritoService.cantidadItemsUnicos());
    }

    @Test
    void debePermitirExactamenteDiezItemsUnicos() {

        // Arrange
        for (long i = 1; i <= 10; i++) {

            Libro libro = crearLibro(
                    i,
                    "Libro " + i,
                    10000,
                    10);

            when(repositorioLibro.buscarPorId(i))
                    .thenReturn(Optional.of(libro));
        }

        // Act
        for (long i = 1; i <= 10; i++) {
            carritoService.agregarItem(i, 1);
        }

        // Assert
        assertEquals(
                10,
                carritoService.cantidadItemsUnicos());
    }

    @Test
    void debeLanzarExcepcionAlAgregarItemOnce() {

        // Arrange
        for (long i = 1; i <= 11; i++) {

            Libro libro = crearLibro(
                    i,
                    "Libro " + i,
                    10000,
                    10);

            when(repositorioLibro.buscarPorId(i))
                    .thenReturn(Optional.of(libro));
        }

        for (long i = 1; i <= 10; i++) {
            carritoService.agregarItem(i, 1);
        }

        // Act + Assert
        assertThrows(
                IllegalStateException.class,
                () -> carritoService.agregarItem(11L, 1));
    }

    @Test
    void debeLanzarExcepcionCuandoCantidadEsCero() {

        // Arrange + Act + Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> carritoService.agregarItem(1L, 0));
    }

    @Test
    void debeLanzarExcepcionCuandoCantidadEsNegativa() {

        // Arrange + Act + Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> carritoService.agregarItem(1L, -5));
    }

    @Test
    void debeLanzarExcepcionCuandoLibroNoExiste() {

        // Arrange
        when(repositorioLibro.buscarPorId(1L))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(
                NoSuchElementException.class,
                () -> carritoService.agregarItem(1L, 1));
    }

    @Test
    void debeLanzarExcepcionCuandoStockEsInsuficiente() {

        // Arrange
        Libro libro = crearLibro(
                1L,
                "Testing",
                20000,
                2);

        when(repositorioLibro.buscarPorId(1L))
                .thenReturn(Optional.of(libro));

        // Act + Assert
        assertThrows(
                IllegalStateException.class,
                () -> carritoService.agregarItem(1L, 5));
    }

    @Test
    void debeLanzarExcepcionAlCalcularTotalConCarritoVacio() {

        // Arrange

        // Act + Assert
        assertThrows(
                IllegalStateException.class,
                () -> carritoService.calcularTotal());
    }

    @Test
    void debeLanzarExcepcionAlValidarCheckoutConCarritoVacio() {

        // Arrange

        // Act + Assert
        assertThrows(
                IllegalStateException.class,
                () -> carritoService.validarParaCheckout());
    }

    @Test
    void debeLanzarExcepcionAlRemoverItemInexistente() {

        // Arrange

        // Act + Assert
        assertThrows(
                NoSuchElementException.class,
                () -> carritoService.removerItem(999L));
    }

    @Test
    void debeAcumularCantidadCuandoItemYaExiste() {

        // Arrange
        Libro libro = crearLibro(
                1L,
                "Effective Java",
                60000,
                20);

        when(repositorioLibro.buscarPorId(1L))
                .thenReturn(Optional.of(libro));

        // Act
        carritoService.agregarItem(1L, 2);
        carritoService.agregarItem(1L, 3);

        // Assert
        assertEquals(
                1,
                carritoService.cantidadItemsUnicos());

        assertEquals(
                5,
                carritoService.cantidadItems());
    }
}
```

```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

[Evalúa tu propio prompt, no la respuesta del LLM. ¿El contexto fue suficiente? ¿Las restricciones fueron claras? ¿El formato de salida que pediste ayudó a obtener una buena respuesta? ¿Qué parte de tu prompt fue más efectiva? Sé específico: menciona fragmentos concretos de tu prompt que funcionaron bien.]


#### 2. ¿Qué se puede mejorar?

[¿Qué le faltó a tu prompt? ¿Qué harías diferente si pudieras reformularlo? ¿El LLM entendió mal algo por falta de claridad en tu prompt? ¿La respuesta tiene errores u omisiones? ¿Qué no cubrió el LLM que tú sí sabes por lo visto en clase?]


#### 3. Respuesta final

[Escribe tu respuesta definitiva a la pregunta del parcial, integrando lo que aprendiste del LLM pero yendo más allá. Corrige errores, llena omisiones, conecta con conceptos vistos en clase. Esta es tu respuesta: demuestra que tú dominas el tema.]
