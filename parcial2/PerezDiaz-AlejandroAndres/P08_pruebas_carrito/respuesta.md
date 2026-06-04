# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [P08_pruebas_carrito]

### Estudiante
- **Nombre completo**: [Alejandro Andres Perez Diaz]

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [Claude ] |
| **Modelo específico** | [Sonet 4.6] |
| **¿Por qué elegiste este LLM?** | [Mejor IA] |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```
[Actúa como Ingeniero de Software Senior experto en testing. Genera pruebas unitarias completas con JUnit 5 y Mockito para la clase CarritoService de OpenLib Market. Cubre todos los métodos, casos normales, casos borde como carrito vacío, carrito lleno con 10 ítems, intentar agregar el ítem 11, checkout con carrito vacío, agregar un ítem que ya existe, y todas las excepciones posibles. Usa mocks para RepositorioLibro.

Un buen prompt incluye:
- Contexto del proyecto OpenLib Market
- El código o situación específica
- Lo que esperas que el LLM haga
- Restricciones (ej: "usa Java 21", "aplica SOLID")
- Formato de salida esperado (ej: "respuesta en markdown con código Java")]
```

---

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**
[Respuesta:
java@ExtendWith(MockitoExtension.class)
class CarritoServiceTest {

    @Mock
    private RepositorioLibro repositorioLibro;

    @InjectMocks
    private CarritoService carritoService;

    private Libro libroMock;

    @BeforeEach
    void setUp() {
        libroMock = new Libro(1L, "Clean Code", 50000, 10);
    }

    @Test
    void agregarItem_exitoso() {
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libroMock));
        carritoService.agregarItem(1L, 2);
        assertEquals(1, carritoService.cantidadItemsUnicos());
    }

    @Test
    void agregarItem_cantidadNegativa_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
            () -> carritoService.agregarItem(1L, -1));
    }

    @Test
    void agregarItem_libroNoExiste_lanzaExcepcion() {
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class,
            () -> carritoService.agregarItem(1L, 1));
    }

    @Test
    void agregarItem_stockInsuficiente_lanzaExcepcion() {
        libroMock.setStock(1);
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libroMock));
        assertThrows(IllegalStateException.class,
            () -> carritoService.agregarItem(1L, 5));
    }

    @Test
    void agregarItem_carritoLleno_lanzaExcepcion() {
        for (long i = 1; i <= 10; i++) {
            Libro libro = new Libro(i, "Libro " + i, 10000, 10);
            when(repositorioLibro.buscarPorId(i)).thenReturn(Optional.of(libro));
            carritoService.agregarItem(i, 1);
        }
        Libro libro11 = new Libro(11L, "Libro 11", 10000, 10);
        when(repositorioLibro.buscarPorId(11L)).thenReturn(Optional.of(libro11));
        assertThrows(IllegalStateException.class,
            () -> carritoService.agregarItem(11L, 1));
    }

    @Test
    void agregarItem_itemYaExiste_actualizaCantidad() {
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libroMock));
        carritoService.agregarItem(1L, 2);
        carritoService.agregarItem(1L, 3);
        assertEquals(1, carritoService.cantidadItemsUnicos());
        assertEquals(5, carritoService.cantidadItems());
    }

    @Test
    void removerItem_exitoso() {
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libroMock));
        carritoService.agregarItem(1L, 1);
        carritoService.removerItem(1L);
        assertEquals(0, carritoService.cantidadItemsUnicos());
    }

    @Test
    void removerItem_noExiste_lanzaExcepcion() {
        assertThrows(NoSuchElementException.class,
            () -> carritoService.removerItem(99L));
    }

    @Test
    void calcularTotal_exitoso() {
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libroMock));
        carritoService.agregarItem(1L, 2);
        assertEquals(new BigDecimal("100000"), carritoService.calcularTotal());
    }

    @Test
    void calcularTotal_carritoVacio_lanzaExcepcion() {
        assertThrows(IllegalStateException.class,
            () -> carritoService.calcularTotal());
    }

    @Test
    void validarParaCheckout_carritoVacio_lanzaExcepcion() {
        assertThrows(IllegalStateException.class,
            () -> carritoService.validarParaCheckout());
    }

    @Test
    void validarParaCheckout_conItems_noLanzaExcepcion() {
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libroMock));
        carritoService.agregarItem(1L, 1);
        assertDoesNotThrow(() -> carritoService.validarParaCheckout());
    }

    @Test
    void vaciar_limpiaCarrito() {
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libroMock));
        carritoService.agregarItem(1L, 1);
        carritoService.vaciar();
        assertEquals(0, carritoService.cantidadItemsUnicos());
    }
}
Si el LLM generó código, asegúrate de que esté correctamente formateado.
Si tuviste que hacer varias iteraciones, pega la MEJOR respuesta obtenida,
pero menciona cuántas iteraciones hiciste.]
```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

[Evalúa tu propio prompt, no la respuesta del LLM. ¿El contexto fue suficiente? ¿Las restricciones fueron claras? ¿El formato de salida que pediste ayudó a obtener una buena respuesta? ¿Qué parte de tu prompt fue más efectiva? Sé específico: menciona fragmentos concretos de tu prompt que funcionaron bien.]


#### 2. ¿Qué se puede mejorar?

[¿Qué le faltó a tu prompt? ¿Qué harías diferente si pudieras reformularlo? ¿El LLM entendió mal algo por falta de claridad en tu prompt? ¿La respuesta tiene errores u omisiones? ¿Qué no cubrió el LLM que tú sí sabes por lo visto en clase?]


#### 3. Respuesta final

[Escribe tu respuesta definitiva a la pregunta del parcial, integrando lo que aprendiste del LLM pero yendo más allá. Corrige errores, llena omisiones, conecta con conceptos vistos en clase. Esta es tu respuesta: demuestra que tú dominas el tema.]
