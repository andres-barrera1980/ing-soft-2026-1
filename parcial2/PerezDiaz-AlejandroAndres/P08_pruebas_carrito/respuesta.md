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

[Evalúa tu propio prompt, no la respuesta del LLM. ¿El contexto fue suficiente?
-Yo diria que si 
 ¿Las restricciones fueron claras?
 -si la verdad e este pormt siento que si fue espesifico con lo que queria 
  ¿El formato de salida que pediste ayudó a obtener una buena respuesta? 
  -si
  ¿Qué parte de tu prompt fue más efectiva?
  -La parte más efectiva fue listar explícitamente los casos borde como carrito vacío, carrito lleno con 10 ítems. 
   Sé específico: menciona fragmentos concretos de tu prompt que funcionaron bien.]


#### 2. ¿Qué se puede mejorar?

[¿Qué le faltó a tu prompt?
-Depronto mas contexto ni ida proque se que no quedo perfecto
 ¿Qué harías diferente si pudieras reformularlo?
 -añadirme mas contexto  o nose porque la respuesta que me dio que analise esta bien 
  ¿El LLM entendió mal algo por falta de claridad en tu prompt? 
  -si porque se nota al moemnte de debolver la respuesta 
  ¿La respuesta tiene errores u omisiones? 
  -no
  ¿Qué no cubrió el LLM que tú sí sabes por lo visto en clase?
  -segun todo lo cubriocasi todo a excepcion de pronto no cumpla con algunos patrones ]


#### 3. Respuesta final

[Las pruebas unitarias de CarritoService cubren todos los métodos y casos para simular RepositorioLibro sin necesitar infraestructura real. El caso más importante que la IA no cubrió es agregar un ítem que ya existe cuando el carrito está lleno — ese caso no debe lanzar excepción porque la regla aplica solo para ítems nuevos.]
