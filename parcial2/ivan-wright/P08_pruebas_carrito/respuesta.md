# Plantilla de entrega — Parcial 2

---

## Pregunta P08: Pruebas Unitarias y Mockito — Carrito

### Estudiante
- **Nombre completo**: Ivan Wright

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Gemini |
| **Modelo especifico** | Gemini 3.1 Pro |
| **¿Por que elegiste este LLM?** | Lo elegi porque es muy bueno escribiendo pruebas unitarias con JUnit 5, estructura muy bien el codigo usando el patron Arrange-Act-Assert y configura correctamente los Mocks. |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```text
hola gemini, actua como un QA engineer experto en java.

[CONTEXTO]
En OpenLib Market tenemos la clase `CarritoService` que maneja la logica del carrito de compras y depende de una interfaz externa `RepositorioLibro`. Hay dos reglas de negocio importantisimas: el carrito debe tener minimo 1 item para hacer checkout, y solo puede tener como maximo 10 items diferentes (las cantidades de cada item no importan para este limite).

[PROBLEMA]
Necesito que escribas pruebas unitarias completas para esta clase. Cubre casos normales (agregar, remover, calcular total), pero sobre todo concentrate en los casos borde que te mencione: carrito vacio, carrito lleno con 10 items, intentar agregar el item numero 11, y tratar de hacer checkout cuando esta vacio.

[RESTRICCIONES]
- Usa JUnit 5 y Mockito.
- Aplica el patron Arrange-Act-Assert (Given-When-Then) en cada prueba.
- Mokea correctamente el `RepositorioLibro` para aislar las pruebas.

[FORMATO]
Entregame todo estructurado en markdown con un bloque de codigo en Java que contenga la clase de prueba completa.

[VALIDACION]
Sabre que tu respuesta es util si las pruebas cubren los limites maximos y minimos que te pase y verifican que se lancen las excepciones correctas sin depender de una base de datos real.
```

---

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```markdown
¡Hola! Entendido. Para asegurar la calidad de `CarritoService` y validar tus reglas de negocio estrictas, preparé un conjunto de pruebas unitarias usando **JUnit 5** y **Mockito**. 

He estructurado cada prueba siguiendo el patrón **Arrange-Act-Assert (AAA)** para que sean fáciles de leer, y he mockeado la dependencia `RepositorioLibro` para que el entorno esté totalmente aislado.

### 1. Implementación de Pruebas Unitarias

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

    private Libro libroPrueba;

    @BeforeEach
    void setUp() {
        // Arrange general
        libroPrueba = new Libro(1L, "Clean Code", new BigDecimal("100000"), 50); // id, titulo, precio, stock
    }

    @Test
    void agregarItem_EscenarioNormal_DebeAgregarYSumarCantidad() {
        // Arrange
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libroPrueba));

        // Act
        carritoService.agregarItem(1L, 2);

        // Assert
        assertEquals(2, carritoService.cantidadItems());
        assertEquals(1, carritoService.cantidadItemsUnicos());
        assertEquals(new BigDecimal("200000"), carritoService.calcularTotal());
    }

    @Test
    void agregarItem_SinStock_DebeLanzarExcepcion() {
        // Arrange
        libroPrueba.setStock(1);
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libroPrueba));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            carritoService.agregarItem(1L, 2);
        });
    }

    @Test
    void agregarItem_ExcederMaximo10ItemsDiferentes_DebeLanzarExcepcion() {
        // Arrange: Llenar el carrito con 10 ítems distintos
        for (long i = 1; i <= 10; i++) {
            Libro libro = new Libro(i, "Libro " + i, new BigDecimal("100"), 10);
            when(repositorioLibro.buscarPorId(i)).thenReturn(Optional.of(libro));
            carritoService.agregarItem(i, 1);
        }

        // El ítem 11
        Libro libro11 = new Libro(11L, "Libro 11", new BigDecimal("100"), 10);
        when(repositorioLibro.buscarPorId(11L)).thenReturn(Optional.of(libro11));

        // Act & Assert
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            carritoService.agregarItem(11L, 1);
        });
        assertTrue(exception.getMessage().contains("no puede tener más de 10 ítems"));
    }
    
    @Test
    void agregarItem_ItemYaExisteCuandoHay10Items_NoDebeLanzarExcepcion() {
        // Arrange: Llenar el carrito con 10 ítems distintos
        for (long i = 1; i <= 10; i++) {
            Libro libro = new Libro(i, "Libro " + i, new BigDecimal("100"), 10);
            when(repositorioLibro.buscarPorId(i)).thenReturn(Optional.of(libro));
            carritoService.agregarItem(i, 1);
        }

        // Act: Agregar más cantidad a un ítem que ya está en el carrito (el ID 1)
        carritoService.agregarItem(1L, 2);

        // Assert
        assertEquals(10, carritoService.cantidadItemsUnicos());
        assertEquals(12, carritoService.cantidadItems()); // 10 originales + 2 adicionales
    }

    @Test
    void validarParaCheckout_CarritoVacio_DebeLanzarExcepcion() {
        // Arrange: CarritoService recién instanciado está vacío

        // Act & Assert
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            carritoService.validarParaCheckout();
        });
        assertEquals("El carrito debe tener al menos 1 ítem para checkout", exception.getMessage());
    }

    @Test
    void removerItem_ItemExistente_DebeRemoverlo() {
        // Arrange
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libroPrueba));
        carritoService.agregarItem(1L, 1);

        // Act
        carritoService.removerItem(1L);

        // Assert
        assertEquals(0, carritoService.cantidadItemsUnicos());
        assertThrows(IllegalStateException.class, () -> carritoService.calcularTotal());
    }
}
```

### 2. Resumen de Casos Borde Cubiertos
* **Carrito lleno con 10 ítems e intentar agregar el 11**: Cubierto en la prueba `agregarItem_ExcederMaximo10ItemsDiferentes_DebeLanzarExcepcion`.
* **Checkout con carrito vacío**: Cubierto en la prueba `validarParaCheckout_CarritoVacio_DebeLanzarExcepcion`.
* **Añadir un ítem repetido cuando el límite de 10 está al máximo**: Validé que no fallara gracias a tu aclaración de que solo cuentan ítems diferentes.

Con esto tienes una base 100% aislada de base de datos usando `when().thenReturn()` de Mockito.
```

---

### Analisis critico de la respuesta

#### 1. ¿Que hizo bien el prompt?

El prompt fue muy especifico al listarle los casos borde (carrito vacio, el limite de 10 items, el intento de meter el item 11). Esto funciono muy bien porque evito que el LLM me diera pruebas genericas y lo obligo a concentrarse en la logica dificil del negocio. Pedir explicitamente el patron Arrange-Act-Assert hizo que el codigo de las pruebas generado fuera super facil de leer y entender, con cada bloque bien definido.


#### 2. ¿Que se puede mejorar?

Me falto pedirle que probara *absolutamente todos* los metodos publicos de la clase. Al concentrar el prompt tanto en los casos borde de errores, el LLM se olvido de hacer pruebas para metodos sencillos como `vaciar()` o un caso exitoso normal del metodo `validarParaCheckout()`. El LLM fue obediente pero le falto vision panoramica para lograr un 100% de cobertura.


#### 3. Respuesta final

El LLM hizo un buen trabajo aislando la prueba con Mockito usando las anotaciones `@Mock` e `@InjectMocks`, y aplico correctamente `when().thenReturn()` garantizando que la base de datos no intervenga en el test. Cubrio muy bien el caso mas confuso: agregar un item repetido cuando ya hay 10 items unicos (probando que las cantidades adicionales no rompen el limite maximo).

Sin embargo, a la respuesta del LLM le faltaron pruebas importantes. Por ejemplo, probo que el checkout falla si esta vacio, pero jamas probo que pasaba si tenia items. Aca escribo yo mismo los casos unitarios que el LLM omitio para dejar la clase completa:

```java
    // Prueba que el LLM olvido: Checkout exitoso (Happy Path)
    @Test
    void validarParaCheckout_ConItems_NoDebeLanzarExcepcion() {
        // Arrange
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libroPrueba));
        carritoService.agregarItem(1L, 1);

        // Act & Assert
        // Simplemente ejecutamos y verificamos que NO tire excepcion
        assertDoesNotThrow(() -> {
            carritoService.validarParaCheckout();
        });
    }

    // Prueba que el LLM olvido: Vaciar el carrito
    @Test
    void vaciar_DebeDejarElCarritoVacio() {
        // Arrange
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libroPrueba));
        carritoService.agregarItem(1L, 2);
        
        // Act
        carritoService.vaciar();
        
        // Assert
        assertEquals(0, carritoService.cantidadItems());
        assertEquals(0, carritoService.cantidadItemsUnicos());
    }
```

*Como ingeniero, es vital recordar que probar que algo falla (excepciones) es igual de importante que probar que algo funciona correctamente (happy path). Las pruebas unitarias no son solo para romper el codigo, sino para documentar su comportamiento esperado*.
