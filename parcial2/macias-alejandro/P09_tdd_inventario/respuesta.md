## P09_tdd_inventario

### Estudiante
- **Nombre completo**: [Alejandro Macias Barrios]

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [**Sin IA — respuesta propia**] |
| **Modelo específico** | [N/A] |
| **¿Por qué elegiste este LLM?** | [N/A] |


### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?
[N/A.]

#### 2. ¿Qué se puede mejorar?
[N/A.]

#### 3. Respuesta final
[FASE 1 (RED): Pruebas que fallan (no existe implementación aún)

// ControlInventarioServiceTest.java — escrito ANTES de la implementación

@ExtendWith(MockitoExtension.class)
class ControlInventarioServiceTest {

    @Mock
    private LibroRepositorio libroRepositorio;

    @Mock
    private NotificadorVendedor notificadorVendedor;

    private ControlInventarioService service;

    @BeforeEach
    void setUp() {
        service = new ControlInventarioService(libroRepositorio, notificadorVendedor);
    }

    // --- Test 1: stock llega a cero → libro se marca AGOTADO ---
    @Test
    void dadoStockEnUno_cuandoReducoEnUno_entoncesLibroSeMarcaAgotado() {
        Libro libro = new Libro(1L, "Clean Code", 1, EstadoLibro.DISPONIBLE);
        when(libroRepositorio.buscarPorId(1L)).thenReturn(Optional.of(libro));

        service.reducirStock(1L, 1);

        assertThat(libro.getEstado()).isEqualTo(EstadoLibro.AGOTADO);
    }

    // --- Test 2: stock cero → notificar vendedor ---
    @Test
    void dadoStockLlegaACero_cuandoReducirStock_entoncesNotificaVendedor() {
        Libro libro = new Libro(1L, "Clean Code", 1, EstadoLibro.DISPONIBLE);
        libro.setVendedor(new Usuario(10L, "vendedor@openlib.com"));
        when(libroRepositorio.buscarPorId(1L)).thenReturn(Optional.of(libro));

        service.reducirStock(1L, 1);

        verify(notificadorVendedor).notificarAgotado(libro.getVendedor(), libro);
    }

    // --- Test 3: stock no llega a cero → no se marca agotado ---
    @Test
    void dadoStockEnTres_cuandoReducoEnUno_entoncesLibroSigueDISPONIBLE() {
        Libro libro = new Libro(1L, "Clean Code", 3, EstadoLibro.DISPONIBLE);
        when(libroRepositorio.buscarPorId(1L)).thenReturn(Optional.of(libro));

        service.reducirStock(1L, 1);

        assertThat(libro.getEstado()).isEqualTo(EstadoLibro.DISPONIBLE);
        assertThat(libro.getStock()).isEqualTo(2);
        verifyNoInteractions(notificadorVendedor);
    }

    // --- Test 4: reposición de stock → libro vuelve a DISPONIBLE ---
    @Test
    void dadoLibroAgotado_cuandoVendedorReponeLStock_entoncesLibroVuelveADisponible() {
        Libro libro = new Libro(1L, "Clean Code", 0, EstadoLibro.AGOTADO);
        when(libroRepositorio.buscarPorId(1L)).thenReturn(Optional.of(libro));

        service.reponerStock(1L, 5);

        assertThat(libro.getEstado()).isEqualTo(EstadoLibro.DISPONIBLE);
        assertThat(libro.getStock()).isEqualTo(5);
    }

    // --- Test 5: no se puede reducir más de lo disponible ---
    @Test
    void dadoStockInsuficiente_cuandoReducirStock_entoncesLanzaExcepcion() {
        Libro libro = new Libro(1L, "Clean Code", 2, EstadoLibro.DISPONIBLE);
        when(libroRepositorio.buscarPorId(1L)).thenReturn(Optional.of(libro));

        assertThatThrownBy(() -> service.reducirStock(1L, 5))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Stock insuficiente");
    }

    // --- Test 6: reducción con cantidad cero o negativa ---
    @Test
    void dadoCantidadNegativa_cuandoReducirStock_entoncesLanzaIllegalArgument() {
        assertThatThrownBy(() -> service.reducirStock(1L, 0))
            .isInstanceOf(IllegalArgumentException.class);
    }
}

FASE 2 (GREEN): Implementación mínima para que pasen

public class ControlInventarioService {
    private final LibroRepositorio libroRepositorio;
    private final NotificadorVendedor notificadorVendedor;

    public ControlInventarioService(
        LibroRepositorio libroRepositorio,
        NotificadorVendedor notificadorVendedor
    ) {
        this.libroRepositorio = libroRepositorio;
        this.notificadorVendedor = notificadorVendedor;
    }

    public void reducirStock(Long libroId, int cantidad) {
        if (cantidad <= 0) throw new IllegalArgumentException("Cantidad debe ser positiva");

        Libro libro = libroRepositorio.buscarPorId(libroId)
            .orElseThrow(() -> new NoSuchElementException("Libro no encontrado: " + libroId));

        if (libro.getStock() < cantidad) {
            throw new IllegalStateException("Stock insuficiente: disponible=" + libro.getStock());
        }

        libro.setStock(libro.getStock() - cantidad);

        if (libro.getStock() == 0) {
            libro.setEstado(EstadoLibro.AGOTADO);
            notificadorVendedor.notificarAgotado(libro.getVendedor(), libro);
        }

        libroRepositorio.guardar(libro);
    }

    public void reponerStock(Long libroId, int cantidad) {
        if (cantidad <= 0) throw new IllegalArgumentException("Cantidad debe ser positiva");

        Libro libro = libroRepositorio.buscarPorId(libroId)
            .orElseThrow(() -> new NoSuchElementException("Libro no encontrado: " + libroId));

        libro.setStock(libro.getStock() + cantidad);

        if (libro.getEstado() == EstadoLibro.AGOTADO && libro.getStock() > 0) {
            libro.setEstado(EstadoLibro.DISPONIBLE);
        }

        libroRepositorio.guardar(libro);
    }
}

FASE 3 (REFACTOR): Mejorar diseño sin romper pruebas

// Mover la lógica de estado a la entidad Libro (ella conoce sus propias reglas)
public class Libro {
    // ... campos ...

    public void reducirStock(int cantidad) {
        if (cantidad <= 0) throw new IllegalArgumentException("Cantidad debe ser positiva");
        if (this.stock < cantidad) throw new IllegalStateException("Stock insuficiente: disponible=" + this.stock);
        this.stock -= cantidad;
        if (this.stock == 0) this.estado = EstadoLibro.AGOTADO;
    }

    public void reponerStock(int cantidad) {
        if (cantidad <= 0) throw new IllegalArgumentException("Cantidad debe ser positiva");
        this.stock += cantidad;
        if (this.estado == EstadoLibro.AGOTADO) this.estado = EstadoLibro.DISPONIBLE;
    }
}

// El servicio queda delegando a la entidad y orquestando efectos secundarios
public class ControlInventarioService {
    private final LibroRepositorio libroRepositorio;
    private final NotificadorVendedor notificadorVendedor;

    public void reducirStock(Long libroId, int cantidad) {
        Libro libro = libroRepositorio.buscarPorId(libroId)
            .orElseThrow(() -> new NoSuchElementException("Libro no encontrado: " + libroId));

        boolean estabaDisponible = libro.getEstado() == EstadoLibro.DISPONIBLE;
        libro.reducirStock(cantidad); // lanza excepción si stock insuficiente o cantidad inválida

        if (libro.getEstado() == EstadoLibro.AGOTADO && estabaDisponible) {
            notificadorVendedor.notificarAgotado(libro.getVendedor(), libro);
        }

        libroRepositorio.guardar(libro);
    }

    public void reponerStock(Long libroId, int cantidad) {
        Libro libro = libroRepositorio.buscarPorId(libroId)
            .orElseThrow(() -> new NoSuchElementException("Libro no encontrado: " + libroId));
        libro.reponerStock(cantidad);
        libroRepositorio.guardar(libro);
    }
}]