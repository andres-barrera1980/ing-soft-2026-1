# Plantilla de entrega — Parcial 2

---

## Pregunta P09: TDD — ControlInventario

### Estudiante

- **Nombre completo**: Juan David Rodriguez Franco

---

### LLM utilizado

| Campo                                   | Valor                                                                                                          |
| --------------------------------------- | -------------------------------------------------------------------------------------------------------------- |
| **Nombre del LLM**                | Gemini |
| **Modelo específico**            | Gemini 3.1 Pro |
| **¿Por qué elegiste este LLM?** | Gemini 3.1 Pro es capaz de simular correctamente el ciclo TDD Red-Green-Refactor mostrando código en tres etapas progresivas y coherentes entre sí. |

---

### Prompt utilizado

```text
Actúa como un ingeniero de software experto en TDD (Test-Driven Development) con Java.

En OpenLib Market necesitamos la clase `ControlInventarioService`. Sus reglas son:
- Cuando el stock de un libro llega a 0, el sistema debe marcarlo como "AGOTADO" y notificar al vendedor.
- Cuando el vendedor repone stock, el libro vuelve al estado "DISPONIBLE".

Por favor, demuestra el ciclo completo de TDD (Red → Green → Refactor) para implementar esta funcionalidad:

**FASE RED**: Escribe primero las pruebas unitarias con JUnit 5 y Mockito que fallen porque la implementación aún no existe.
**FASE GREEN**: Escribe la implementación MÍNIMA de `ControlInventarioService` para que las pruebas pasen.
**FASE REFACTOR**: Mejora el diseño sin romper las pruebas.

El código debe ser en Java. Usa interfaces para las dependencias externas (repositorio y notificador).
```

---

### Respuesta del LLM

```markdown
## TDD para ControlInventarioService — Ciclo Completo

---

### 🔴 FASE RED — Las pruebas que deben FALLAR (aún no hay implementación)

```java
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ControlInventarioServiceTest {

    @Mock
    private LibroRepository libroRepository;

    @Mock
    private VendedorNotificador vendedorNotificador;

    private ControlInventarioService service;

    @BeforeEach
    void setUp() {
        // En esta fase, esta línea FALLA con ClassNotFoundException
        service = new ControlInventarioService(libroRepository, vendedorNotificador);
    }

    @Test
    void reducirStock_cuandoStockLlegaACero_debeMarcarComoAgotadoYNotificar() {
        // Arrange
        Libro libro = new Libro("ISBN-001", "Clean Code", 1, "DISPONIBLE");
        when(libroRepository.buscarPorIsbn("ISBN-001")).thenReturn(libro);

        // Act
        service.reducirStock("ISBN-001", 1);

        // Assert — FALLA porque la clase no existe
        assertEquals("AGOTADO", libro.getEstado());
        verify(vendedorNotificador, times(1)).notificarStockAgotado(libro);
    }

    @Test
    void reponerStock_debeMarcarLibroComoDisponible() {
        // Arrange
        Libro libro = new Libro("ISBN-001", "Clean Code", 0, "AGOTADO");
        when(libroRepository.buscarPorIsbn("ISBN-001")).thenReturn(libro);

        // Act
        service.reponerStock("ISBN-001", 10);

        // Assert — FALLA porque la clase no existe
        assertEquals("DISPONIBLE", libro.getEstado());
        assertEquals(10, libro.getStock());
    }

    @Test
    void reducirStock_cuandoStockQuedaPositivo_noDebeCambiarEstadoNiNotificar() {
        // Arrange
        Libro libro = new Libro("ISBN-002", "Refactoring", 5, "DISPONIBLE");
        when(libroRepository.buscarPorIsbn("ISBN-002")).thenReturn(libro);

        // Act
        service.reducirStock("ISBN-002", 2);

        // Assert — Stock queda en 3, sigue DISPONIBLE
        assertEquals("DISPONIBLE", libro.getEstado());
        assertEquals(3, libro.getStock());
        verify(vendedorNotificador, never()).notificarStockAgotado(any());
    }
}
```

---

### 🟢 FASE GREEN — Implementación MÍNIMA para pasar las pruebas

Primero las interfaces (dependencias externas):

```java
public interface LibroRepository {
    Libro buscarPorIsbn(String isbn);
    void guardar(Libro libro);
}

public interface VendedorNotificador {
    void notificarStockAgotado(Libro libro);
}
```

La implementación mínima de `ControlInventarioService`:

```java
public class ControlInventarioService {

    private final LibroRepository libroRepository;
    private final VendedorNotificador vendedorNotificador;

    public ControlInventarioService(LibroRepository libroRepository,
                                    VendedorNotificador vendedorNotificador) {
        this.libroRepository = libroRepository;
        this.vendedorNotificador = vendedorNotificador;
    }

    public void reducirStock(String isbn, int cantidad) {
        Libro libro = libroRepository.buscarPorIsbn(isbn);
        libro.setStock(libro.getStock() - cantidad);

        if (libro.getStock() <= 0) {
            libro.setStock(0);
            libro.setEstado("AGOTADO");
            vendedorNotificador.notificarStockAgotado(libro);
        }
    }

    public void reponerStock(String isbn, int cantidad) {
        Libro libro = libroRepository.buscarPorIsbn(isbn);
        libro.setStock(cantidad);
        libro.setEstado("DISPONIBLE");
    }
}
```
✅ Con esta implementación, las 3 pruebas pasan (GREEN).

---

### 🔵 FASE REFACTOR — Mejorar el diseño sin romper las pruebas

Problemas detectados en la fase GREEN:
1. El método `reducirStock` no valida que la cantidad a reducir sea positiva.
2. No se persiste el libro después de los cambios (falta llamar a `libroRepository.guardar()`).
3. El estado "AGOTADO"/"DISPONIBLE" debería ser un `enum` para evitar strings mágicos.

Versión refactorizada:

```java
public enum EstadoLibro {
    DISPONIBLE, AGOTADO
}

public class ControlInventarioService {

    private final LibroRepository libroRepository;
    private final VendedorNotificador vendedorNotificador;

    public ControlInventarioService(LibroRepository libroRepository,
                                    VendedorNotificador vendedorNotificador) {
        this.libroRepository = libroRepository;
        this.vendedorNotificador = vendedorNotificador;
    }

    public void reducirStock(String isbn, int cantidad) {
        if (cantidad <= 0) throw new IllegalArgumentException("La cantidad a reducir debe ser positiva");
        
        Libro libro = libroRepository.buscarPorIsbn(isbn);
        int nuevoStock = Math.max(0, libro.getStock() - cantidad);
        libro.setStock(nuevoStock);

        if (nuevoStock == 0) {
            libro.setEstado(EstadoLibro.AGOTADO.name());
            vendedorNotificador.notificarStockAgotado(libro);
        }

        libroRepository.guardar(libro); // Persistir los cambios
    }

    public void reponerStock(String isbn, int cantidad) {
        if (cantidad <= 0) throw new IllegalArgumentException("La cantidad a reponer debe ser positiva");

        Libro libro = libroRepository.buscarPorIsbn(isbn);
        libro.setStock(cantidad);
        libro.setEstado(EstadoLibro.DISPONIBLE.name());

        libroRepository.guardar(libro); // Persistir los cambios
    }
}
```
✅ Las 3 pruebas originales siguen pasando. El diseño mejoró sin cambiar el comportamiento.
```
**FASE GREEN**: Escribe la implementación MÍNIMA de `ControlInventarioService` para que las pruebas pasen.
**FASE REFACTOR**: Mejora el diseño sin romper las pruebas.

El código debe ser en Java. Usa interfaces para las dependencias externas (repositorio y notificador).
```

---

### Respuesta del LLM

```markdown
## TDD para ControlInventarioService — Ciclo Completo

---

### 🔴 FASE RED — Las pruebas que deben FALLAR (aún no hay implementación)

```java
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ControlInventarioServiceTest {

    @Mock
    private LibroRepository libroRepository;

    @Mock
    private VendedorNotificador vendedorNotificador;

    private ControlInventarioService service;

    @BeforeEach
    void setUp() {
        // En esta fase, esta línea FALLA con ClassNotFoundException
        service = new ControlInventarioService(libroRepository, vendedorNotificador);
    }

    @Test
    void reducirStock_cuandoStockLlegaACero_debeMarcarComoAgotadoYNotificar() {
        // Arrange
        Libro libro = new Libro("ISBN-001", "Clean Code", 1, "DISPONIBLE");
        when(libroRepository.buscarPorIsbn("ISBN-001")).thenReturn(libro);

        // Act
        service.reducirStock("ISBN-001", 1);

        // Assert — FALLA porque la clase no existe
        assertEquals("AGOTADO", libro.getEstado());
        verify(vendedorNotificador, times(1)).notificarStockAgotado(libro);
    }

    @Test
    void reponerStock_debeMarcarLibroComoDisponible() {
        // Arrange
        Libro libro = new Libro("ISBN-001", "Clean Code", 0, "AGOTADO");
        when(libroRepository.buscarPorIsbn("ISBN-001")).thenReturn(libro);

        // Act
        service.reponerStock("ISBN-001", 10);

        // Assert — FALLA porque la clase no existe
        assertEquals("DISPONIBLE", libro.getEstado());
        assertEquals(10, libro.getStock());
    }

    @Test
    void reducirStock_cuandoStockQuedaPositivo_noDebeCambiarEstadoNiNotificar() {
        // Arrange
        Libro libro = new Libro("ISBN-002", "Refactoring", 5, "DISPONIBLE");
        when(libroRepository.buscarPorIsbn("ISBN-002")).thenReturn(libro);

        // Act
        service.reducirStock("ISBN-002", 2);

        // Assert — Stock queda en 3, sigue DISPONIBLE
        assertEquals("DISPONIBLE", libro.getEstado());
        assertEquals(3, libro.getStock());
        verify(vendedorNotificador, never()).notificarStockAgotado(any());
    }
}
```

---

### 🟢 FASE GREEN — Implementación MÍNIMA para pasar las pruebas

Primero las interfaces (dependencias externas):

```java
public interface LibroRepository {
    Libro buscarPorIsbn(String isbn);
    void guardar(Libro libro);
}

public interface VendedorNotificador {
    void notificarStockAgotado(Libro libro);
}
```

La implementación mínima de `ControlInventarioService`:

```java
public class ControlInventarioService {

    private final LibroRepository libroRepository;
    private final VendedorNotificador vendedorNotificador;

    public ControlInventarioService(LibroRepository libroRepository,
                                    VendedorNotificador vendedorNotificador) {
        this.libroRepository = libroRepository;
        this.vendedorNotificador = vendedorNotificador;
    }

    public void reducirStock(String isbn, int cantidad) {
        Libro libro = libroRepository.buscarPorIsbn(isbn);
        libro.setStock(libro.getStock() - cantidad);

        if (libro.getStock() <= 0) {
            libro.setStock(0);
            libro.setEstado("AGOTADO");
            vendedorNotificador.notificarStockAgotado(libro);
        }
    }

    public void reponerStock(String isbn, int cantidad) {
        Libro libro = libroRepository.buscarPorIsbn(isbn);
        libro.setStock(cantidad);
        libro.setEstado("DISPONIBLE");
    }
}
```
✅ Con esta implementación, las 3 pruebas pasan (GREEN).

---

### 🔵 FASE REFACTOR — Mejorar el diseño sin romper las pruebas

Problemas detectados en la fase GREEN:
1. El método `reducirStock` no valida que la cantidad a reducir sea positiva.
2. No se persiste el libro después de los cambios (falta llamar a `libroRepository.guardar()`).
3. El estado "AGOTADO"/"DISPONIBLE" debería ser un `enum` para evitar strings mágicos.

Versión refactorizada:

```java
public enum EstadoLibro {
    DISPONIBLE, AGOTADO
}

public class ControlInventarioService {

    private final LibroRepository libroRepository;
    private final VendedorNotificador vendedorNotificador;

    public ControlInventarioService(LibroRepository libroRepository,
                                    VendedorNotificador vendedorNotificador) {
        this.libroRepository = libroRepository;
        this.vendedorNotificador = vendedorNotificador;
    }

    public void reducirStock(String isbn, int cantidad) {
        if (cantidad <= 0) throw new IllegalArgumentException("La cantidad a reducir debe ser positiva");
        
        Libro libro = libroRepository.buscarPorIsbn(isbn);
        int nuevoStock = Math.max(0, libro.getStock() - cantidad);
        libro.setStock(nuevoStock);

        if (nuevoStock == 0) {
            libro.setEstado(EstadoLibro.AGOTADO.name());
            vendedorNotificador.notificarStockAgotado(libro);
        }

        libroRepository.guardar(libro); // Persistir los cambios
    }

    public void reponerStock(String isbn, int cantidad) {
        if (cantidad <= 0) throw new IllegalArgumentException("La cantidad a reponer debe ser positiva");

        Libro libro = libroRepository.buscarPorIsbn(isbn);
        libro.setStock(cantidad);
        libro.setEstado(EstadoLibro.DISPONIBLE.name());

        libroRepository.guardar(libro); // Persistir los cambios
    }
}
```
✅ Las 3 pruebas originales siguen pasando. El diseño mejoró sin cambiar el comportamiento.
```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

El prompt fue muy efectivo al requerir explícitamente que el LLM demostrara el ciclo TDD en sus tres fases distintas (**Red, Green, Refactor**). Esto obligó al modelo a no saltar directamente a la solución final (como suele hacer), sino a mostrar primero las pruebas fallidas, luego la implementación más sencilla posible, y finalmente las mejoras de diseño. También fue un acierto pedir el uso de JUnit 5, Mockito e interfaces para aislar la lógica de negocio.

#### 2. ¿Qué se puede mejorar?

El prompt podría haber sido más específico exigiendo que se evaluaran casos borde desde el inicio (por ejemplo, intentar reducir una cantidad negativa o mayor al stock actual). Debido a esto, en la fase *Green* la implementación fue quizás demasiado ingenua, y en la fase *Refactor* el LLM introdujo validaciones de negocio adicionales (como rechazar cantidades negativas) que, estrictamente hablando en TDD, deberían haber surgido a partir de escribir primero una nueva prueba que fallara para esos casos borde, en lugar de agregarlo directamente en el refactor.

#### 3. Respuesta final

La respuesta final del LLM ilustra de manera excelente el flujo de trabajo de **Test-Driven Development (TDD)**:
1. **Fase RED:** Escribió el comportamiento esperado mediante pruebas, definiendo la API pública de la clase (`reducirStock`, `reponerStock`) antes de que existiera.
2. **Fase GREEN:** Creó una implementación mínima y rápida. Simplemente pasaba los tests, dejando de lado buenas prácticas como guardar en base de datos.
3. **Fase REFACTOR:** Mejoró el código sin miedo a romper la funcionalidad, porque las pruebas de la fase RED servían como red de seguridad. Aquí introdujo el `enum EstadoLibro`, las validaciones de entrada (`IllegalArgumentException`), y la persistencia (`guardar`).

Esta técnica garantiza una alta cobertura de código desde el principio y fomenta un diseño más modular, ya que nos obliga a pensar primero en cómo se usará nuestra clase (como cliente) antes de pensar en cómo implementarla internamente. La solución propuesta es limpia, robusta y refleja fielmente los principios de desarrollo ágil y diseño guiado por pruebas.
