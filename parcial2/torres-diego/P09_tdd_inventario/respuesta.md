# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [09]: [P09_tdd_inventario]

### Estudiante
- **Nombre completo**: [Diego Alejandro Torres Barrgan]

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [Claude] |
| **Modelo específico** | [Ej: Claude Sonnet 4.6/bajo]|
| **¿Por qué elegiste este LLM?** | [Es el modelo que actualmente mas uso y en el que estoy confiando, ademas que es muy poderoso para generar codigo y para explicarlo ] |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```
[Actua como un desarrollador que aplica TDD estrictamente. Necesito implementar ControlInventarioService para OpenLib Market. Esta clase debe: marcar un libro como AGOTADO cuando el stock llega a cero, notificar al vendedor cuando eso pasa, y marcarlo como DISPONIBLE cuando el vendedor repone stock.
Aplica el ciclo completo Red-Green-Refactor:

Primero escribe las pruebas con JUnit 5 y Mockito que fallen porque la implementacion no existe
Luego escribe la implementacion minima para que pasen
Finalmente refactoriza sin romper las pruebas

Es importante que el orden sea ese: pruebas primero, implementacion despues.]
```

---

### Respuesta del LLM

RED — pruebas que fallan (no existe implementacion aun):
```java
java@ExtendWith(MockitoExtension.class)
class ControlInventarioServiceTest {

    @Mock
    private RepositorioLibro repositorioLibro;

    @Mock
    private ServicioNotificacion notificacion;

    @InjectMocks
    private ControlInventarioService inventarioService;

    @Test
    void cuandoStockLlegaACero_debeMarcarComoAgotado() {
        Libro libro = new Libro(1L, "Clean Code", 1, EstadoLibro.DISPONIBLE);
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro));

        inventarioService.reducirStock(1L, 1);

        assertEquals(EstadoLibro.AGOTADO, libro.getEstado());
        verify(repositorioLibro).guardar(libro);
    }

    @Test
    void cuandoStockLlegaACero_debeNotificarAlVendedor() {
        Libro libro = new Libro(1L, "Clean Code", 1, EstadoLibro.DISPONIBLE);
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro));

        inventarioService.reducirStock(1L, 1);

        verify(notificacion).notificar(libro.getEmailVendedor(), "Tu libro esta agotado");
    }

    @Test
    void cuandoVendedorReponaStock_debeMarcarComoDisponible() {
        Libro libro = new Libro(1L, "Clean Code", 0, EstadoLibro.AGOTADO);
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro));

        inventarioService.reponerStock(1L, 5);

        assertEquals(EstadoLibro.DISPONIBLE, libro.getEstado());
        verify(repositorioLibro).guardar(libro);
    }

    @Test
    void cuandoStockNoLlegaACero_noDebeNotificar() {
        Libro libro = new Libro(1L, "Clean Code", 5, EstadoLibro.DISPONIBLE);
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro));

        inventarioService.reducirStock(1L, 2);

        verify(notificacion, never()).notificar(any(), any());
    }
}
```

GREEN — implementacion minima para que pasen:
```java
javapublic class ControlInventarioService {

    private final RepositorioLibro repositorioLibro;
    private final ServicioNotificacion notificacion;

    public ControlInventarioService(RepositorioLibro repositorioLibro,
                                     ServicioNotificacion notificacion) {
        this.repositorioLibro = repositorioLibro;
        this.notificacion = notificacion;
    }

    public void reducirStock(Long libroId, int cantidad) {
        Libro libro = repositorioLibro.buscarPorId(libroId)
            .orElseThrow(() -> new NoSuchElementException("Libro no encontrado"));

        libro.setStock(libro.getStock() - cantidad);

        if (libro.getStock() == 0) {
            libro.setEstado(EstadoLibro.AGOTADO);
            notificacion.notificar(libro.getEmailVendedor(), "Tu libro esta agotado");
        }

        repositorioLibro.guardar(libro);
    }

    public void reponerStock(Long libroId, int cantidad) {
        Libro libro = repositorioLibro.buscarPorId(libroId)
            .orElseThrow(() -> new NoSuchElementException("Libro no encontrado"));

        libro.setStock(libro.getStock() + cantidad);
        libro.setEstado(EstadoLibro.DISPONIBLE);
        repositorioLibro.guardar(libro);
    }
}
```
REFACTOR — mejorar sin romper pruebas:
```java
javapublic void reducirStock(Long libroId, int cantidad) {
    Libro libro = buscarLibro(libroId);
    libro.setStock(libro.getStock() - cantidad);
    if (libro.estaAgotado()) {
        libro.setEstado(EstadoLibro.AGOTADO);
        notificacion.notificar(libro.getEmailVendedor(), "Tu libro esta agotado");
    }
    repositorioLibro.guardar(libro);
}

private Libro buscarLibro(Long libroId) {
    return repositorioLibro.buscarPorId(libroId)
        .orElseThrow(() -> new NoSuchElementException("Libro no encontrado"));
}
```
Se mueve la logica de stock == 0 a un metodo estaAgotado() dentro de Libro, que es donde debe vivir esa regla de negocio.
```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

[Pedir explicitamente el orden Red-Green-Refactor fue lo que mas ayudo porque el LLM no se salto ninguna fase. Tambien ayudo aclarar "pruebas primero, implementacion despues" porque sin eso el LLM tiende a escribir todo junto sin respetar el ciclo]


#### 2. ¿Qué se puede mejorar?

[No se pidio cubrir casos borde como stock negativo o reponer stock en un libro que ya estaba disponible, entonces el LLM no los incluyo. Tambien faltaria pedir que explicara como verificar que las pruebas realmente fallan antes de la implementacion, que es la parte mas importante del TDD y quedo un poco superficial.]


#### 3. Respuesta final

El ciclo TDD se aplico correctamente: pruebas primero, implementacion minima despues, y luego refactoring. Lo importante del Red es que las pruebas deben fallar antes de escribir cualquier codigo, eso garantiza que no estan hechas a la medida de una implementacion que ya existia.

Lo que le falto a la respuesta fueron dos casos borde: reducir el stock a un valor negativo y reponer stock en un libro que ya estaba disponible. Estos son bugs potenciales en produccion y deberian tener su prueba. Los agregaria asi:
```java
@Test
void reducirStock_masDelDisponible_deberiaLanzarExcepcion() {
    Libro libro = new Libro(1L, "Clean Code", 2, EstadoLibro.DISPONIBLE);
    when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro));

    assertThrows(IllegalStateException.class, 
        () -> inventarioService.reducirStock(1L, 5));
}

@Test
void reponerStock_libroYaDisponible_deberiaSeguirDisponible() {
    Libro libro = new Libro(1L, "Clean Code", 3, EstadoLibro.DISPONIBLE);
    when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro));

    inventarioService.reponerStock(1L, 2);

    assertEquals(EstadoLibro.DISPONIBLE, libro.getEstado());
}
```
El refactoring fue pequeño pero correcto, extraer buscarLibro() evita repetir el mismo bloque en los dos metodos y hace el codigo mas legible.

