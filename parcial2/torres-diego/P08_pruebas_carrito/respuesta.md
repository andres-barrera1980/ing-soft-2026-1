# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [08]: [P08_pruebas_carrito]

### Estudiante
- **Nombre completo**: [Diego Alejandro Torres Barrgan]

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [Claude] |
| **Modelo específico** | [Ej: Claude Sonnet 4.6/bajo]|
| **¿Por qué elegiste este LLM?** | [Es el modelo que actualmente mas uso y en el que estoy confiando] |


---

### Prompt utilizado

```
[Actúa como un Ingeniero de QA / Desarrollador Backend Senior. En nuestra tienda OpenLib Market, tenemos la clase CarritoService que maneja las reglas de negocio del carrito de compras (mínimo 1 ítem para checkout, máximo 10 ítems únicos, control de stock, etc.).
Necesito que generes las pruebas unitarias completas usando JUnit 5 y Mockito para esta clase.
Por favor, asegúrate de cubrir los siguientes escenarios:

Casos Felices (Normales): Agregar un ítem correctamente, calcular el total de la compra y remover un ítem.
Casos Borde de Reglas de Negocio: * Agregar ítems hasta llegar exactamente al límite de 10 ítems únicos.
Intentar agregar el ítem único número 11 y verificar que lance la excepción correcta.
Agregar un ítem que ya existe en el carrito (verificar que sume la cantidad en vez de contar como un ítem único nuevo).
Validaciones y Excepciones:
Intentar hacer el checkout (validarParaCheckout()) con el carrito vacío.
Intentar agregar un libro que no existe en el RepositorioLibro.
Intentar agregar un libro cuando el stock disponible es insuficiente. Entrega el código de la clase de prueba de Java bien estructurado, usando @Mock, @InjectMocks, y los métodos de Assertions de JUnit 5.]
```

---

### Respuesta del LLM
```java
@ExtendWith(MockitoExtension.class)
class CarritoServiceTest {

    @Mock
    private RepositorioLibro repositorioLibro;

    @InjectMocks
    private CarritoService carritoService;

    private Libro libroDisponible;

    @BeforeEach
    void setUp() {
        libroDisponible = new Libro(1L, "Clean Code", "123", 10, new BigDecimal("50000"));
    }

    // --- Casos felices ---

    @Test
    void agregarItem_deberiaAgregarCorrectamente() {
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libroDisponible));

        carritoService.agregarItem(1L, 2);

        assertEquals(1, carritoService.cantidadItemsUnicos());
    }

    @Test
    void removerItem_deberiaEliminarDelCarrito() {
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libroDisponible));
        carritoService.agregarItem(1L, 1);

        carritoService.removerItem(1L);

        assertEquals(0, carritoService.cantidadItemsUnicos());
    }

    @Test
    void calcularTotal_deberiaRetornarSumacorrecta() {
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libroDisponible));
        carritoService.agregarItem(1L, 2);

        BigDecimal total = carritoService.calcularTotal();

        assertEquals(new BigDecimal("100000"), total);
    }

    // --- Casos borde ---

    @Test
    void agregarItem_hastaLimiteDe10_deberiaFuncionar() {
        for (long i = 1; i <= 10; i++) {
            Libro libro = new Libro(i, "Libro " + i, "isbn" + i, 5, new BigDecimal("10000"));
            when(repositorioLibro.buscarPorId(i)).thenReturn(Optional.of(libro));
            carritoService.agregarItem(i, 1);
        }

        assertEquals(10, carritoService.cantidadItemsUnicos());
    }

    @Test
    void agregarItem_item11_deberiaLanzarExcepcion() {
        for (long i = 1; i <= 10; i++) {
            Libro libro = new Libro(i, "Libro " + i, "isbn" + i, 5, new BigDecimal("10000"));
            when(repositorioLibro.buscarPorId(i)).thenReturn(Optional.of(libro));
            carritoService.agregarItem(i, 1);
        }

        Libro libro11 = new Libro(11L, "Libro 11", "isbn11", 5, new BigDecimal("10000"));
        when(repositorioLibro.buscarPorId(11L)).thenReturn(Optional.of(libro11));

        assertThrows(IllegalStateException.class, () -> carritoService.agregarItem(11L, 1));
    }

    @Test
    void agregarItem_itemYaExistente_deberiaSumarCantidad() {
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libroDisponible));
        carritoService.agregarItem(1L, 2);
        carritoService.agregarItem(1L, 3);

        assertEquals(1, carritoService.cantidadItemsUnicos());
        assertEquals(5, carritoService.cantidadItems());
    }

    // --- Validaciones y excepciones ---

    @Test
    void validarParaCheckout_carritoVacio_deberiaLanzarExcepcion() {
        assertThrows(IllegalStateException.class, () -> carritoService.validarParaCheckout());
    }

    @Test
    void agregarItem_libroNoExiste_deberiaLanzarExcepcion() {
        when(repositorioLibro.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> carritoService.agregarItem(99L, 1));
    }

    @Test
    void agregarItem_stockInsuficiente_deberiaLanzarExcepcion() {
        Libro libroSinStock = new Libro(2L, "Libro raro", "456", 1, new BigDecimal("30000"));
        when(repositorioLibro.buscarPorId(2L)).thenReturn(Optional.of(libroSinStock));

        assertThrows(IllegalStateException.class, () -> carritoService.agregarItem(2L, 5));
    }
}
```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

[Listar los escenarios numerados fue lo que mas ayudo, el LLM no se invento casos ni omitio ninguno porque estaban explicitos. Tambien ayudo mencionar @Mock, @InjectMocks y JUnit 5 directamente porque la respuesta salio con la estructura correcta sin tener que pedirlo dos veces.]


#### 2. ¿Qué se puede mejorar?

[No se pidio el caso de remover un item que no existe en el carrito, que tambien lanza excepcion y es un caso importante. Tampoco se pidio probar calcularTotal() con carrito vacio. Si reformulara el prompt agregaria esos dos casos en la lista de excepciones.]


#### 3. Respuesta final

La clase CarritoService maneja las reglas de negocio del carrito y necesita pruebas que cubran todos los flujos posibles. Las pruebas generadas cubren bien los casos del enunciado pero le faltaron dos casos importantes: remover un item que no existe y calcular el total con carrito vacio. Aqui van esos dos casos adicionales:
```java
java@Test
void removerItem_noExiste_deberiaLanzarExcepcion() {
    assertThrows(NoSuchElementException.class, () -> carritoService.removerItem(99L));
}

@Test
void calcularTotal_carritoVacio_deberiaLanzarExcepcion() {
    assertThrows(IllegalStateException.class, () -> carritoService.calcularTotal());
}
```
La ventaja de usar mocks para RepositorioLibro es que las pruebas corren sin base de datos real, lo que las hace rapidas y confiables. Si el repositorio fallara en produccion eso es responsabilidad de sus propias pruebas de integracion, no de estas.
