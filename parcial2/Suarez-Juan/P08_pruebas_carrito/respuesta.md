# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [XX]: [Título resumido]

### Estudiante
- **Nombre completo**: [Tu nombre y apellido]

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [Claude / ChatGPT / Gemini / Copilot / DeepSeek / Qwen / Mistral / Otro / **Sin IA — respuesta propia**] |
| **Modelo específico** | [Ej: Claude Opus 4.5, GPT-4o, Gemini 2.5 Pro, etc. Si respondes sin IA, escribe "N/A"] |
| **¿Por qué elegiste este LLM?** | [Justifica en 1-3 oraciones. Si respondes sin IA, explica por qué decidiste no usar LLM para esta pregunta.] |

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

[ROL]
Actua como profesor de fundamentos de Software.

[CONTEXTO]
Proyecto: OpenLib Market (Plataforma transaccional E-commerce para libros Open Source).
Módulo actual: Buyer - Carrito de Compras (Lógica de Negocio y Persistencia en Memoria).
Stack Tecnológico: Java 25, Spring Boot 4.x, JUnit 5, Mockito.

[PROBLEMA]
Necesito generar un conjunto de pruebas unitarias exhaustivas y de alta cobertura para la clase `CarritoService` utilizando JUnit 5 y Mockito. El servicio gestiona los ítems en memoria mediante un `Map` y depende de un `RepositorioLibro`.

A continuación, te proporciono el código exacto de la clase para la cual debes diseñar las pruebas:

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
[RESTRICCIONES]
Las pruebas deben implementarse utilizando estrictamente JUnit 5 (Jupyter API) y Mockito para el mocking de dependencias (como `LibroRepository` o `UsuarioRepository`). Se deben cubrir obligatoriamente los siguientes escenarios:
1. Casos Normales: Agregar un libro válido al carrito de forma exitosa.
2. Casos Borde (Edge Cases):
   - Consultar o procesar un carrito que está completamente vacío.
   - Llenar el carrito de compras hasta su límite máximo permitido (exactamente 10 ítems).
   - Intentar agregar el ítem número 11, lo cual debe ser rechazado por el sistema.
   - Intentar realizar el flujo de checkout teniendo el carrito vacío (debe fallar).
3. Excepciones: Verificar el lanzamiento de excepciones personalizadas (ej. `CarritoLlenoException`, `CarritoVacioException`, `LibroNoEncontradoException`) usando `assertThrows`.

[FORMATO DE SALIDA]
Solicito la respuesta estructurada de la siguiente manera:
1. Código de la Clase de Prueba: Código Java completo y limpio de `CarritoServiceTest`, utilizando las anotaciones correctas (`@ExtendWith(MockitoExtension.class)`, `@Test`, `@BeforeEach`, etc.).
2. Nombres de Métodos Semánticos: Uso del patrón `given_when_then` o nombres descriptivos en español/inglés para cada test.
3. Breve explicación: Comentarios breves sobre cómo se manejó la aserción de los límites de datos (especialmente la transición de 10 a 11 ítems).

[VALIDACIÓN / CRITERIOS DE ÉXITO]
La solución se considerará correcta si las pruebas aíslan completamente a `CarritoService` de la base de datos real mediante mocks, compilan sin problemas en el entorno de Java 25 / Spring Boot 4.x, y validan rigurosamente tanto el camino feliz (Happy Path) como los escenarios de fallo.

```

---

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**
[Pega aquí la respuesta COMPLETA del LLM, sin editar, sin resumir.
Incluye TODO el texto, código, explicaciones que generó el LLM.

Si el LLM generó código, asegúrate de que esté correctamente formateado.
Si tuviste que hacer varias iteraciones, pega la MEJOR respuesta obtenida,
pero menciona cuántas iteraciones hiciste.]

¡Hola! Como tu profesor de Fundamentos de Software, es un placer ver cómo estructuradas tus capas de negocio. El diseño de pruebas unitarias exhaustivas es la delgada línea que separa un prototipo frágil de un software de calidad empresarial listo para producción.

Antes de entregarte el código, una pequeña nota de arquitectura: observo que en las restricciones mencionas excepciones personalizadas (como `CarritoLlenoException`), pero en tu código real estás lanzando excepciones nativas de Java (`IllegalStateException`, `NoSuchElementException`). Para cumplir estrictamente con tu criterio de éxito y garantizar que tus pruebas **compilen y pasen**, he diseñado los `assertThrows` apuntando a las excepciones reales de tu código, añadiendo la validación de sus mensajes interactivos.

Aquí tienes la suite de pruebas optimizada para Java 25 y Spring Boot 4.x.

---

## 1. Código de la Clase de Prueba (`CarritoServiceTest`)

```java
package com.openlib.market.buyer;

import org.junit.jupiter.api.DisplayName;
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

    // --- 1. CASOS NORMALES (HAPPY PATH) ---

    @Test
    @DisplayName("Debe agregar un ítem exitosamente cuando el libro es válido y tiene stock")
    void givenValidBookAndStock_whenAgregarItem_thenSuccess() {
        // Given
        Long libroId = 1L;
        int cantidad = 2;
        Libro libroMock = mock(Libro.class);
        when(libroMock.getStock()).thenReturn(10);
        when(libroMock.getPrecio()).thenReturn(new BigDecimal("25.00"));
        when(repositorioLibro.buscarPorId(libroId)).thenReturn(Optional.of(libroMock));

        // When
        carritoService.agregarItem(libroId, cantidad);

        // Then
        assertEquals(1, carritoService.cantidadItemsUnicos());
        assertEquals(2, carritoService.cantidadItems());
        verify(repositorioLibro, times(1)).buscarPorId(libroId);
    }

    @Test
    @DisplayName("Debe incrementar la cantidad si el ítem ya existe en el carrito")
    void givenExistingItemInCart_whenAgregarMismoItem_thenIncrementQuantity() {
        // Given
        Long libroId = 1L;
        Libro libroMock = mock(Libro.class);
        when(libroMock.getStock()).thenReturn(10);
        when(libroMock.getPrecio()).thenReturn(new BigDecimal("20.00"));
        when(repositorioLibro.buscarPorId(libroId)).thenReturn(Optional.of(libroMock));

        // Agregar por primera vez
        carritoService.agregarItem(libroId, 2);

        // When (Agregar el mismo libro otra vez)
        carritoService.agregarItem(libroId, 3);

        // Then
        assertEquals(1, carritoService.cantidadItemsUnicos(), "Los ítems únicos no deben aumentar");
        assertEquals(5, carritoService.cantidadItems(), "La cantidad total debe ser la suma (2 + 3)");
    }

    @Test
    @DisplayName("Debe calcular el total del carrito correctamente")
    void givenMultipleItems_whenCalcularTotal_thenReturnsCorrectSum() {
        // Given
        Long libro1Id = 1L;
        Libro libro1 = mock(Libro.class);
        when(libro1.getStock()).thenReturn(5);
        when(libro1.getPrecio()).thenReturn(new BigDecimal("10.00"));
        when(repositorioLibro.buscarPorId(libro1Id)).thenReturn(Optional.of(libro1));

        Long libro2Id = 2L;
        Libro libro2 = mock(Libro.class);
        when(libro2.getStock()).thenReturn(5);
        when(libro2.getPrecio()).thenReturn(new BigDecimal("15.50"));
        when(repositorioLibro.buscarPorId(libro2Id)).thenReturn(Optional.of(libro2));

        carritoService.agregarItem(libro1Id, 2); // 2 * 10.00 = 20.00
        carritoService.agregarItem(libro2Id, 1); // 1 * 15.50 = 15.50

        // When
        BigDecimal total = carritoService.calcularTotal();

        // Then
        assertEquals(new BigDecimal("35.50"), total);
    }

    // --- 2. CASOS BORDE (EDGE CASES) ---

    @Test
    @DisplayName("Debe permitir llenar el carrito exactamente hasta el límite máximo de 10 ítems")
    void givenCart_whenLlenarHastaElLimiteMaximo_thenSuccess() {
        // Given & When
        for (long i = 1; i <= 10; i++) {
            Libro libroMock = mock(Libro.class);
            when(libroMock.getStock()).thenReturn(5);
            when(libroMock.getPrecio()).thenReturn(BigDecimal.TEN);
            when(repositorioLibro.buscarPorId(i)).thenReturn(Optional.of(libroMock));

            carritoService.agregarItem(i, 1);
        }

        // Then
        assertEquals(10, carritoService.cantidadItemsUnicos());
        assertEquals(10, carritoService.cantidadItems());
    }

    @Test
    @DisplayName("Debe rechazar la adición del ítem número 11 superando el límite permitido")
    void givenFullCartOf10Items_whenAgregarItemOnce_thenThrowsIllegalStateException() {
        // Given (Llenamos el carrito con 10 elementos)
        for (long i = 1; i <= 10; i++) {
            Libro libroMock = mock(Libro.class);
            when(libroMock.getStock()).thenReturn(5);
            when(libroMock.getPrecio()).thenReturn(BigDecimal.TEN);
            when(repositorioLibro.buscarPorId(i)).thenReturn(Optional.of(libroMock));
            carritoService.agregarItem(i, 1);
        }

        // Preparar el ítem número 11
        Long libro11Id = 11L;
        Libro libro11Mock = mock(Libro.class);
        when(libro11Mock.getStock()).thenReturn(5);
        when(repositorioLibro.buscarPorId(libro11Id)).thenReturn(Optional.of(libro11Mock));

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            carritoService.agregarItem(libro11Id, 1);
        });

        assertEquals("El carrito no puede tener más de 10 ítems diferentes", exception.getMessage());
        assertEquals(10, carritoService.cantidadItemsUnicos(), "El tamaño del carrito debió mantenerse en 10");
    }

    @Test
    @DisplayName("Debe lanzar excepción al intentar hacer checkout con un carrito vacío")
    void givenEmptyCart_whenValidarParaCheckout_thenThrowsIllegalStateException() {
        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            carritoService.validarParaCheckout();
        });

        assertEquals("El carrito debe tener al menos 1 ítem para checkout", exception.getMessage());
    }

    @Test
    @DisplayName("Debe lanzar excepción al calcular el total si el carrito está completamente vacío")
    void givenEmptyCart_whenCalcularTotal_thenThrowsIllegalStateException() {
        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            carritoService.calcularTotal();
        });

        assertEquals("El carrito está vacío", exception.getMessage());
    }

    // --- 3. EXCEPCIONES Y VALIDACIONES ---

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException si la cantidad agregada es menor o igual a cero")
    void givenZeroOrNegativeQuantity_whenAgregarItem_thenThrowsIllegalArgumentException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            carritoService.agregarItem(1L, 0);
        });

        assertEquals("Cantidad debe ser positiva", exception.getMessage());
    }

    @Test
    @DisplayName("Debe lanzar NoSuchElementException cuando el libro no existe en el repositorio")
    void givenNonExistentBook_whenAgregarItem_thenThrowsNoSuchElementException() {
        // Given
        Long libroId = 999L;
        when(repositorioLibro.buscarPorId(libroId)).thenReturn(Optional.empty());

        // When & Then
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            carritoService.agregarItem(libroId, 1);
        });

        assertEquals("Libro no encontrado: 999", exception.getMessage());
    }

    @Test
    @DisplayName("Debe lanzar IllegalStateException cuando el stock del libro es insuficiente")
    void givenInsufficientStock_whenAgregarItem_thenThrowsIllegalStateException() {
        // Given
        Long libroId = 1L;
        Libro libroMock = mock(Libro.class);
        when(libroMock.getStock()).thenReturn(2); // Solo hay 2 unidades
        when(repositorioLibro.buscarPorId(libroId)).thenReturn(Optional.of(libroMock));

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            carritoService.agregarItem(libroId, 5); // Se solicitan 5
        });

        assertEquals("Stock insuficiente", exception.getMessage());
    }

    @Test
    @DisplayName("Debe lanzar NoSuchElementException al intentar remover un ítem que no está en el carrito")
    void givenItemNotInCart_whenRemoverItem_thenThrowsNoSuchElementException() {
        // When & Then
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            carritoService.removerItem(1L);
        });

        assertEquals("Ítem no está en el carrito", exception.getMessage());
    }
}

```

---

## 2. Explicación del Manejo de Límites (Transición de 10 a 11 ítems)

El control de fronteras en colecciones en memoria es un foco común de bugs productivos. Para asegurar la robustez de la transición del límite de `MAX_ITEMS = 10` a un estado de desbordamiento, aplicamos la técnica de **Análisis de Valores Límite (Boundary Value Analysis)** mediante dos pruebas complementarias:

* **El Estado Límite Válido (`givenCart_whenLlenarHastaElLimiteMaximo_thenSuccess`)**: Mediante un bucle dinámico, simulamos un escenario realista inyectando mocks secuenciales con identificadores del `1` al `10`. El servicio procesa cada inserción de forma óptima ya que, en la iteración número 10, la condición `!items.containsKey(libroId) && items.size() >= MAX_ITEMS` evalúa `false` (pues el tamaño previo era 9). El test valida el comportamiento del sistema justo en la frontera superior permitida.
* **El Estado de Desbordamiento (`givenFullCartOf10Items_whenAgregarItemOnce_thenThrowsIllegalStateException`)**: Partiendo del estado saturado anterior (10 ítems únicos registrados), forzamos el intento de inserción de un elemento con ID `11L`. Aquí es donde Mockito bloquea efectos colaterales aislando el contexto. Al invocar el método, la estructura condicional de tu lógica de negocio detona de inmediato el `IllegalStateException`.
* **Asertividad Post-Excepción**: No basta con capturar la excepción usando `assertThrows`. La prueba verifica de manera estricta que el mensaje de error coincida textualmente y, crucialmente, realiza un contra-chequeo del estado interno (`assertEquals(10, carritoService.cantidadItemsUnicos())`) para demostrar que el elemento descalificado **nunca** llegó a alterar la integridad de la memoria interna.


### Análisis crítico de la respuesta

#### 1. ¿Que hizo bien el prompt?
La verdad es que el prompt estuvo super bien estructurado porque le dio a la IA todo lo necesario para no perderse: el codigo exacto con el que ibamos a trabajar, las tecnologias claras como Java 25 y las reglas del juego bien explicadas con los limites del carrito. Esto ayudo un monton a que no se inventara cosas raras y fuera directo al grano, armando una estructura limpia, ordenada con el estilo de "dar, cuando, entonces" (given-when-then) y con nombres que cualquiera de nosotros en la clase entenderia a la primera.

#### ¿Que se puede mejorar?

Aunque el prompt fue bastante detallado, un buen truco para la proxima seria pedirle explicitamente a la IA que revise absolutamente todos los metodos de la clase, sin dejarse ninguno por fuera. Como nos enfocamos tanto en decirle cuales eran los casos borde y el limite de los 10 libros, la IA se concentro tanto en complacernos con eso que se olvido de crear pruebas para las acciones mas sencillas como borrar un libro con exito o limpiar el carrito por completo. Si le hubieramos puesto una lista de "debes probar el camino feliz y el triste de cada metodo", el resultado habria sido perfecto.

#### 3. Respuesta final
En balance, la IA simulo muy bien el repositorio con mocks, probo la suma de libros repetidos y la alerta de carrito vacio en validarParaCheckout(). Sin embargo, falto cubrir el camino feliz de ese checkout, el metodo vaciar() y la eliminacion normal de un libro.

#### ¿El LLM genero pruebas para todos los metodos?
No, la verdad es que se salto un par. Aunque armo una muy buena base para defender el proyecto, se enfoco tanto en las partes complejas que se olvido por completo de funciones clave pero mas sencillas, como el metodo para vaciar el carrito. Tampoco probo el caso normal en el que borras un libro que si esta en la lista de compras, asi que nos dejo el trabajo a medias en esa parte.

#### ¿Cubrio todos los casos borde de las reglas de negocio?
Estuvo muy cerca, pero le falto una esquina importante hizo un gran trabajo revisando los extremos mas peligrosos, como que pasa cuando el carrito esta en cero o controlando a la perfeccion el limite de pasar de 10 a 11 libros.

#### ¿Probo validarParaCheckout()? 
Si, pero solo vio un lado de la moneda. Se acordo perfectamente de probar el escenario triste: que el sistema salte y tire un error si intentas pagar con el carrito totalmente vacio. Lo malo es que se le paso por alto escribir la contraparte, que es asegurar con un test que el metodo no ponga problemas y nos deje continuar si ya metimos al menos un libro para comprar.

#### ¿Probo el caso de agregar un item que ya existe?
Si, creo una prueba especifica para verificar que si vuelves a meter un libro que ya tenias en el carrito, el codigo sea inteligente y no te cree un producto duplicado en la lista, sino que simplemente le sume las nuevas unidades a las que ya tenias guardadas en la memoria.


#### ¿Uso correctamente mocks para RepositorioLibro?
Sep. Logro aislar el servicio usando los mocks como si fueran "libros de mentira" o simulados. De esta forma, controlo perfectamente que respondia el buscador de libros y cuanta cantidad de stock simular, sin necesidad de enredarse con una base de datos real, que es justo el chiste de hacer estas pruebas de software.

#### ¿Falto algun caso? Escribe tu los casos de prueba que el LLM no cubrio.
Si, hicieron falta tres escenarios clave para que el escudo de pruebas tape todos los huecos: borrar un libro que si esta en el carrito, limpiar el carrito por completo con el metodo de vaciado, y dejar que el checkout pase libremente si todo esta en orden.