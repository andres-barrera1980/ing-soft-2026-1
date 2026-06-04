# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta 09: TDD — Control de inventario

### Estudiante
- **Nombre completo**: Federico Restrepo

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Claude |
| **Modelo específico** | Claude Sonnet 4.5 |
| **¿Por qué elegiste este LLM?** | Usé Claude Sonnet 4.5 en antigravity, ya que ofrece relativamente una buena cantidad de tokens y ofrece un manejo visual de los archivos que me gusta.  |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```text
Actúa como un desarrollador Java Senior que practica TDD estrictamente.

Contexto: En OpenLib Market necesito implementar ControlInventarioService con estas reglas:
- reducirStock(Long libroId, int cantidad): reduce el stock del libro. Si el stock llega a 0, debe marcar el libro como AGOTADO y notificar al vendedor.
- reponerStock(Long libroId, int cantidad): aumenta el stock. Si el libro estaba AGOTADO, debe cambiar a DISPONIBLE.
- Ambos métodos deben lanzar IllegalArgumentException si la cantidad es <= 0.
- reducirStock debe lanzar IllegalStateException si no hay suficiente stock.

Dependencias disponibles (ya existen como interfaces):
- RepositorioLibro: tiene buscarPorId(Long) y guardar(Libro)
- NotificadorVendedor: tiene notificarAgotado(Libro)

Tarea: Demuestra el ciclo completo de TDD (Red → Green → Refactor) para esta clase.

IMPORTANTE: Sigue el orden correcto de TDD:
1. RED: escribe primero los tests con JUnit 5 + Mockito. En este punto ControlInventarioService NO existe todavía, así que los tests compilarían pero fallarían (o ni compilarían). Explica por qué cada test fallaría.
2. GREEN: escribe la implementación MÍNIMA de ControlInventarioService para que todos los tests pasen. No sobre-diseñes, solo haz que pasen.
3. REFACTOR: mejora el diseño de la implementación (extrae métodos, mejora nombres, elimina duplicación) sin modificar los tests ni cambiar el comportamiento externo.

Restricciones: Java 21, JUnit 5 (@ExtendWith(MockitoExtension.class)), Mockito. Cada test con @DisplayName descriptivo en español.
Formato: markdown con tres bloques de código Java claramente separados y etiquetados como RED, GREEN y REFACTOR.
```

---

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

*(1 iteración)*

---

### TDD para ControlInventarioService

#### Interfaces de soporte (ya existentes)

```java
public enum EstadoLibro { DISPONIBLE, AGOTADO }

public class Libro {
    private Long id;
    private int stock;
    private EstadoLibro estado;
    private Long vendedorId;

    public Libro(Long id, int stock) {
        this.id = id;
        this.stock = stock;
        this.estado = EstadoLibro.DISPONIBLE;
    }

    public Long getId()              { return id; }
    public int getStock()            { return stock; }
    public EstadoLibro getEstado()   { return estado; }
    public Long getVendedorId()      { return vendedorId; }
    public void setStock(int stock)  { this.stock = stock; }
    public void setEstado(EstadoLibro estado) { this.estado = estado; }
    public void setVendedorId(Long id)        { this.vendedorId = id; }
}

public interface RepositorioLibro {
    Optional<Libro> buscarPorId(Long id);
    void guardar(Libro libro);
}

public interface NotificadorVendedor {
    void notificarAgotado(Libro libro);
}
```

---

### 🔴 RED — Tests primero (ControlInventarioService aún NO existe)

> En este punto los tests no compilan porque `ControlInventarioService` no existe. Al crearla vacía, todos los tests fallan con `NullPointerException` o `AssertionError`. Eso es exactamente la fase Red.

```java
// ControlInventarioServiceTest.java — FASE RED
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TDD - ControlInventarioService")
class ControlInventarioServiceTest {

    @Mock RepositorioLibro repositorioLibro;
    @Mock NotificadorVendedor notificadorVendedor;

    private ControlInventarioService service;

    @BeforeEach
    void setUp() {
        // FALLA: ControlInventarioService no existe → no compila
        service = new ControlInventarioService(repositorioLibro, notificadorVendedor);
    }

    // ── reducirStock ────────────────────────────────────────────────────────

    @Test
    @DisplayName("reducirStock: reduce el stock correctamente")
    void reducirStock_casoNormal() {
        Libro libro = new Libro(1L, 10);
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro));

        service.reducirStock(1L, 3);

        // FALLA: método no existe
        assertEquals(7, libro.getStock());
        verify(repositorioLibro).guardar(libro);
    }

    @Test
    @DisplayName("reducirStock: marca AGOTADO y notifica cuando el stock llega a 0")
    void reducirStock_llegaACero_marcaAgotadoYNotifica() {
        Libro libro = new Libro(1L, 5);
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro));

        service.reducirStock(1L, 5);

        // FALLA: método no existe
        assertEquals(EstadoLibro.AGOTADO, libro.getEstado());
        verify(notificadorVendedor).notificarAgotado(libro);
        verify(repositorioLibro).guardar(libro);
    }

    @Test
    @DisplayName("reducirStock: NO notifica si el stock queda mayor a 0")
    void reducirStock_stockMayorACero_noNotifica() {
        Libro libro = new Libro(1L, 10);
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro));

        service.reducirStock(1L, 4);

        // FALLA: método no existe
        assertEquals(EstadoLibro.DISPONIBLE, libro.getEstado());
        verify(notificadorVendedor, never()).notificarAgotado(any());
    }

    @Test
    @DisplayName("reducirStock: lanza IllegalArgumentException si la cantidad es 0 o negativa")
    void reducirStock_cantidadInvalida_lanzaExcepcion() {
        // FALLA: método no existe
        assertThrows(IllegalArgumentException.class, () -> service.reducirStock(1L, 0));
        assertThrows(IllegalArgumentException.class, () -> service.reducirStock(1L, -2));
    }

    @Test
    @DisplayName("reducirStock: lanza IllegalStateException si el stock es insuficiente")
    void reducirStock_stockInsuficiente_lanzaExcepcion() {
        Libro libro = new Libro(1L, 3);
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro));

        // FALLA: método no existe
        assertThrows(IllegalStateException.class, () -> service.reducirStock(1L, 5));
    }

    // ── reponerStock ────────────────────────────────────────────────────────

    @Test
    @DisplayName("reponerStock: aumenta el stock correctamente")
    void reponerStock_casoNormal() {
        Libro libro = new Libro(1L, 5);
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro));

        service.reponerStock(1L, 10);

        // FALLA: método no existe
        assertEquals(15, libro.getStock());
        verify(repositorioLibro).guardar(libro);
    }

    @Test
    @DisplayName("reponerStock: cambia estado a DISPONIBLE si el libro estaba AGOTADO")
    void reponerStock_libroAgotado_cambaADisponible() {
        Libro libro = new Libro(1L, 0);
        libro.setEstado(EstadoLibro.AGOTADO);
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro));

        service.reponerStock(1L, 3);

        // FALLA: método no existe
        assertEquals(EstadoLibro.DISPONIBLE, libro.getEstado());
        assertEquals(3, libro.getStock());
        verify(repositorioLibro).guardar(libro);
    }

    @Test
    @DisplayName("reponerStock: lanza IllegalArgumentException si la cantidad es 0 o negativa")
    void reponerStock_cantidadInvalida_lanzaExcepcion() {
        // FALLA: método no existe
        assertThrows(IllegalArgumentException.class, () -> service.reponerStock(1L, 0));
        assertThrows(IllegalArgumentException.class, () -> service.reponerStock(1L, -1));
    }
}
```

---

### 🟢 GREEN — Implementación mínima para pasar los tests

> El objetivo es hacer pasar todos los tests con el menor código posible. No importa si el diseño no es bonito todavía.

```java
// ControlInventarioService.java — FASE GREEN
import java.util.NoSuchElementException;
import java.util.Optional;

public class ControlInventarioService {

    private final RepositorioLibro repositorioLibro;
    private final NotificadorVendedor notificadorVendedor;

    public ControlInventarioService(RepositorioLibro repositorioLibro, NotificadorVendedor notificadorVendedor) {
        this.repositorioLibro = repositorioLibro;
        this.notificadorVendedor = notificadorVendedor;
    }

    public void reducirStock(Long libroId, int cantidad) {
        if (cantidad <= 0)
            throw new IllegalArgumentException("Cantidad debe ser positiva");

        Libro libro = repositorioLibro.buscarPorId(libroId)
            .orElseThrow(() -> new NoSuchElementException("Libro no encontrado: " + libroId));

        if (libro.getStock() < cantidad)
            throw new IllegalStateException("Stock insuficiente");

        libro.setStock(libro.getStock() - cantidad);

        if (libro.getStock() == 0) {
            libro.setEstado(EstadoLibro.AGOTADO);
            notificadorVendedor.notificarAgotado(libro);
        }

        repositorioLibro.guardar(libro);
    }

    public void reponerStock(Long libroId, int cantidad) {
        if (cantidad <= 0)
            throw new IllegalArgumentException("Cantidad debe ser positiva");

        Libro libro = repositorioLibro.buscarPorId(libroId)
            .orElseThrow(() -> new NoSuchElementException("Libro no encontrado: " + libroId));

        libro.setStock(libro.getStock() + cantidad);

        if (libro.getEstado() == EstadoLibro.AGOTADO) {
            libro.setEstado(EstadoLibro.DISPONIBLE);
        }

        repositorioLibro.guardar(libro);
    }
}
```

> ✅ En este punto todos los tests pasan. El código es funcional pero tiene duplicación: la validación de cantidad y la carga del libro se repite en ambos métodos.

---

### 🔵 REFACTOR — Mejorar el diseño sin romper los tests

> Extraemos la validación y la carga del libro a métodos privados para eliminar la duplicación. Los tests no cambian, solo el código interno.

```java
// ControlInventarioService.java — FASE REFACTOR
import java.util.NoSuchElementException;

public class ControlInventarioService {

    private final RepositorioLibro repositorioLibro;
    private final NotificadorVendedor notificadorVendedor;

    public ControlInventarioService(RepositorioLibro repositorioLibro, NotificadorVendedor notificadorVendedor) {
        this.repositorioLibro = repositorioLibro;
        this.notificadorVendedor = notificadorVendedor;
    }

    public void reducirStock(Long libroId, int cantidad) {
        validarCantidad(cantidad);
        Libro libro = cargarLibro(libroId);

        if (libro.getStock() < cantidad)
            throw new IllegalStateException("Stock insuficiente para el libro: " + libroId);

        libro.setStock(libro.getStock() - cantidad);
        actualizarEstadoSiAgotado(libro);
        repositorioLibro.guardar(libro);
    }

    public void reponerStock(Long libroId, int cantidad) {
        validarCantidad(cantidad);
        Libro libro = cargarLibro(libroId);

        libro.setStock(libro.getStock() + cantidad);
        if (libro.getEstado() == EstadoLibro.AGOTADO) {
            libro.setEstado(EstadoLibro.DISPONIBLE);
        }

        repositorioLibro.guardar(libro);
    }

    // ── métodos privados extraídos en el refactor ───────────────────────────

    private void validarCantidad(int cantidad) {
        if (cantidad <= 0)
            throw new IllegalArgumentException("La cantidad debe ser un valor positivo, se recibió: " + cantidad);
    }

    private Libro cargarLibro(Long libroId) {
        return repositorioLibro.buscarPorId(libroId)
            .orElseThrow(() -> new NoSuchElementException("Libro no encontrado: " + libroId));
    }

    private void actualizarEstadoSiAgotado(Libro libro) {
        if (libro.getStock() == 0) {
            libro.setEstado(EstadoLibro.AGOTADO);
            notificadorVendedor.notificarAgotado(libro);
        }
    }
}
```

> ✅ Todos los tests siguen pasando. El refactor eliminó la duplicación extrayendo `validarCantidad`, `cargarLibro` y `actualizarEstadoSiAgotado`, y mejoró los mensajes de error sin cambiar ningún comportamiento observable externamente.

---

### Análisis crítico de la respuesta


