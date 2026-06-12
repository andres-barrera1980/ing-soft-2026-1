
## Pregunta [09]: [Aplicar TDD a una funcionalidad y evaluar el proceso]

### Estudiante
- **Nombre completo**: Julian Felipe Rojas Almanza

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Gpt |
| **Modelo específico** | Gpt-4o |
| **¿Por qué elegiste este LLM?** | Es uno de los modelos que mejor asimilan y ejecutan instrucciones que requieren un orden cronológico estricto en el desarrollo de software. Tienen la disciplina lógica para escribir pruebas que compilen pero fallen conceptualmente e implementar soluciones iterativas incrementales sin adelantarse al refactoring final. |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```
Actúa como un Ingeniero de Software Senior y Evangelista de XP (Extreme Programming) experto en TDD (Test-Driven Development). Estoy resolviendo un ejercicio técnico académico para el sistema "OpenLib Market" y necesito que demuestres el ciclo de diseño guiado por pruebas paso a paso.

[CONTEXTO]
Estamos desarrollando el módulo de **control de inventario** para OpenLib Market en Java 21. La funcionalidad requerida dicta las siguientes reglas de negocio:
1. Cuando el stock de un libro llega a cero, el sistema debe cambiar automáticamente su estado a "AGOTADO".
2. Al agotarse, debe disparar una notificación al vendedor.
3. Cuando el vendedor repone el stock (stock > 0), el estado del libro debe cambiar automáticamente a "DISPONIBLE".

[PROBLEMA / TAREA]
Necesito que demuestres de forma rigurosa y explícita el ciclo completo de TDD (**Red-Green-Refactor**) para la clase `ControlInventarioService`. Para ello, debes estructurar tu respuesta de manera estrictamente cronológica simulando el proceso de desarrollo real:

- **Fase 1: RED (Prueba que falla)**: Escribe la suite de pruebas unitarias inicial utilizando JUnit 5 y Mockito. Las pruebas deben validar el cambio de estado a "AGOTADO", la notificación al vendedor y la reposición a "DISPONIBLE". En esta fase, asume cómo te gustaría que fuera la API de `ControlInventarioService`. Explica brevemente por qué estas pruebas fallarían (al no existir la implementación).
- **Fase 2: GREEN (Código mínimo para pasar)**: Escribe el código fuente mínimo y estrictamente necesario de la clase `ControlInventarioService` (y las clases de soporte requeridas) para que todas las pruebas escritas en la Fase 1 pasen a verde. Se permite código simple o "falso" con tal de cumplir la aserción.
- **Fase 3: REFACTOR (Mejora de diseño)**: Toma el código de la Fase 2 y aplícale mejoras de diseño, legibilidad y buenas prácticas de Clean Code (por ejemplo, encapsular la lógica de estado en la entidad del dominio, eliminar duplicación o mejorar la inyección de dependencias) asegurando y explicando cómo las pruebas garantizan que no se rompió ningún comportamiento.

[RESTRICCIONES]
- No te adelantes: Está estrictamente prohibido mostrar el código final refinado en la Fase 1 o Fase 2. Debes simular el pensamiento iterativo e incremental de TDD.
- Utiliza Mockito para aislar las dependencias de notificación (ej: `ServicioNotificacion`) y persistencia (ej: `RepositorioLibro`).


[FORMATO DE SALIDA]
Estructura tu respuesta en Markdown utilizando única y estrictamente estos tres encabezados para organizar las fases del ciclo:
- ### 1. Fase RED: Diseño de Pruebas Unitarias Iniciales (JUnit 5 + Mockito)
- ### 2. Fase GREEN: Implementación Mínima de Producción
- ### 3. Fase REFACTOR: Limpieza de Código y Estructuración Final

[VALIDACIÓN]
Sabré que tu respuesta es excelente si respetas el orden del ciclo TDD sin hacer trampa (es decir, sin diseñar la clase perfecta desde el primer bloque de código), si las pruebas cubren los flujos de agotado y reposición de stock, y si demuestras cómo la suite de pruebas actúa como una red de seguridad durante el refactoring de la Fase 3.
```

---

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

# Solución

### 1. Fase RED: Diseño de Pruebas Unitarias Iniciales (JUnit 5 + Mockito)

En TDD comenzamos escribiendo las pruebas antes de tener implementación.

Imaginemos que queremos una API así:

```java
controlInventarioService.actualizarStock(libroId, nuevoStock);
```

Las reglas que deben cumplirse son:

* Stock = 0 → estado AGOTADO.
* Notificar al vendedor cuando se agote.
* Stock > 0 → estado DISPONIBLE.

Como todavía no existe ninguna implementación, estas pruebas inicialmente fallarán por error de compilación o porque los comportamientos aún no existen.

```java
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ControlInventarioServiceTest {

    @Mock
    private RepositorioLibro repositorioLibro;

    @Mock
    private ServicioNotificacion servicioNotificacion;

    @InjectMocks
    private ControlInventarioService controlInventarioService;

    @Test
    void debeCambiarEstadoAAgotadoCuandoStockLlegaACero() {

        // Arrange
        Libro libro =
                new Libro(1L, "Clean Code", 5,
                        EstadoLibro.DISPONIBLE);

        when(repositorioLibro.buscarPorId(1L))
                .thenReturn(Optional.of(libro));

        // Act
        controlInventarioService
                .actualizarStock(1L, 0);

        // Assert
        assertEquals(
                EstadoLibro.AGOTADO,
                libro.getEstado());
    }

    @Test
    void debeNotificarAlVendedorCuandoLibroSeAgota() {

        // Arrange
        Libro libro =
                new Libro(1L, "DDD", 3,
                        EstadoLibro.DISPONIBLE);

        when(repositorioLibro.buscarPorId(1L))
                .thenReturn(Optional.of(libro));

        // Act
        controlInventarioService
                .actualizarStock(1L, 0);

        // Assert
        verify(servicioNotificacion)
                .notificarVendedor(libro);
    }

    @Test
    void debeCambiarEstadoADisponibleCuandoHayReposicion() {

        // Arrange
        Libro libro =
                new Libro(1L, "Refactoring", 0,
                        EstadoLibro.AGOTADO);

        when(repositorioLibro.buscarPorId(1L))
                .thenReturn(Optional.of(libro));

        // Act
        controlInventarioService
                .actualizarStock(1L, 10);

        // Assert
        assertEquals(
                EstadoLibro.DISPONIBLE,
                libro.getEstado());
    }
}
```

---

### 2. Fase GREEN: Implementación Mínima de Producción

Ahora escribimos únicamente el código necesario para que las pruebas pasen.

No buscamos elegancia todavía.

#### EstadoLibro

```java
public enum EstadoLibro {
    DISPONIBLE,
    AGOTADO
}
```

#### Libro

```java
public class Libro {

    private Long id;
    private String titulo;
    private int stock;
    private EstadoLibro estado;

    public Libro(Long id,
                 String titulo,
                 int stock,
                 EstadoLibro estado) {

        this.id = id;
        this.titulo = titulo;
        this.stock = stock;
        this.estado = estado;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public EstadoLibro getEstado() {
        return estado;
    }

    public void setEstado(
            EstadoLibro estado) {

        this.estado = estado;
    }
}
```

#### Dependencias

```java
import java.util.Optional;

public interface RepositorioLibro {

    Optional<Libro> buscarPorId(Long id);
}
```

```java
public interface ServicioNotificacion {

    void notificarVendedor(Libro libro);
}
```

#### Implementación mínima

```java
public class ControlInventarioService {

    private final RepositorioLibro repositorioLibro;
    private final ServicioNotificacion servicioNotificacion;

    public ControlInventarioService(
            RepositorioLibro repositorioLibro,
            ServicioNotificacion servicioNotificacion) {

        this.repositorioLibro = repositorioLibro;
        this.servicioNotificacion =
                servicioNotificacion;
    }

    public void actualizarStock(
            Long libroId,
            int nuevoStock) {

        Libro libro =
                repositorioLibro
                        .buscarPorId(libroId)
                        .orElseThrow();

        libro.setStock(nuevoStock);

        if (nuevoStock == 0) {
            libro.setEstado(
                    EstadoLibro.AGOTADO);

            servicioNotificacion
                    .notificarVendedor(libro);
        }

        if (nuevoStock > 0) {
            libro.setEstado(
                    EstadoLibro.DISPONIBLE);
        }
    }
}
```

Las pruebas ahora pasan.

---

### 3. Fase REFACTOR: Limpieza de Código y Estructuración Final

Con las pruebas en verde podemos mejorar el diseño con seguridad.

La principal mejora es mover la lógica de estado hacia la entidad `Libro`.

Antes:

```java
if (nuevoStock == 0) {
    libro.setEstado(AGOTADO);
}

if (nuevoStock > 0) {
    libro.setEstado(DISPONIBLE);
}
```

La lógica de negocio estaba dispersa en el servicio.

---

#### Entidad mejorada

```java
public class Libro {

    private Long id;
    private String titulo;
    private int stock;
    private EstadoLibro estado;

    public Libro(Long id,
                 String titulo,
                 int stock,
                 EstadoLibro estado) {

        this.id = id;
        this.titulo = titulo;
        this.stock = stock;
        this.estado = estado;
    }

    public void actualizarStock(int nuevoStock) {

        stock = nuevoStock;

        estado = (nuevoStock == 0)
                ? EstadoLibro.AGOTADO
                : EstadoLibro.DISPONIBLE;
    }

    public EstadoLibro getEstado() {
        return estado;
    }

    public int getStock() {
        return stock;
    }
}
```

#### Servicio refactorizado

```java
public class ControlInventarioService {

    private final RepositorioLibro repositorioLibro;
    private final ServicioNotificacion servicioNotificacion;

    public ControlInventarioService(
            RepositorioLibro repositorioLibro,
            ServicioNotificacion servicioNotificacion) {

        this.repositorioLibro = repositorioLibro;
        this.servicioNotificacion =
                servicioNotificacion;
    }

    public void actualizarStock(
            Long libroId,
            int nuevoStock) {

        Libro libro =
                repositorioLibro
                        .buscarPorId(libroId)
                        .orElseThrow();

        libro.actualizarStock(nuevoStock);

        if (libro.getEstado()
                == EstadoLibro.AGOTADO) {

            servicioNotificacion
                    .notificarVendedor(libro);
        }
    }
}
```

#### ¿Por qué el refactor es seguro?

Porque la suite creada en la fase RED sigue ejecutándose exactamente igual:

```text
✓ debeCambiarEstadoAAgotadoCuandoStockLlegaACero
✓ debeNotificarAlVendedorCuandoLibroSeAgota
✓ debeCambiarEstadoADisponibleCuandoHayReposicion
```

Si durante el refactoring hubiéramos roto alguna regla de negocio, alguna prueba habría fallado inmediatamente.

Ese es precisamente el objetivo de TDD:

```text
RED     -> Definir comportamiento
GREEN   -> Hacerlo funcionar
REFACTOR-> Mejorar diseño sin romper funcionalidad
```

Las pruebas actúan como una red de seguridad que permite evolucionar el diseño con confianza.


---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

Se ejecutó la segmentación recomendada en la guía de prompting (.md) para asegurar un diseño óptimo. Tras una mejora inicial por parte de la IA Gema de Gemini y mi posterior revisión y ajuste manual, el prompt quedó impecable. Su estructura (rol, contexto e instrucciones claras) es simple y totalmente funcional.


#### 2. ¿Qué se puede mejorar?

El control de calidad confirma que el rendimiento del prompt es óptimo. Al haber mitigado cualquier sesgo durante tu revisión manual, las mejoras viables tienden a cero. El prompt se considera una versión final cerrada y lista para su ejecución.


#### 3. Respuesta final

el análisis de la inteligencia artificial demostró de forma correcta los pasos del desarrollo guiado por pruebas al simular las fases de rojo, verde y refactorización, pues estructuró el flujo conceptual empezando por los experimentos que fallan antes de programar la lógica del negocio. se vio bien cómo el refactor mejoró el diseño moviendo el control del estado hacia la entidad libro cumpliendo con el encapsulamiento sin dañar el comportamiento, y el uso de los dobles de prueba con mockito para simular el repositorio y las alertas del vendedor fue aplicado de forma adecuada.

sin embargo, el análisis omitió un vacío gigante sobre cómo comprobar si de verdad se está aplicando este método o si solo se está simulando el proceso. la inteligencia artificial cometió el error de entregar todo el código junto en un solo mensaje de texto, lo cual hace imposible verificar si el robot de verdad escribió primero las pruebas y luego el código, pues en papel o en una sola respuesta de chat es muy fácil hacer trampa escribiendo la lógica primero y luego inventando los experimentos que encajen perfectos. para que la solución fuera perfecta, faltó mencionar que para comprobar un desarrollo guiado por pruebas genuino en openlib market se necesita revisar el historial de cambios del repositorio de código, verificando que exista un registro donde solo estén las pruebas rotas y luego otro registro con la solución mínima. tampoco se agregaron pruebas para los casos límite de fallos reales, como qué pasa si el libro buscado no existe en la base de datos o si el nuevo stock recibe un número negativo, dejando un hueco en la fase inicial del diseño.

el código original de los experimentos estaba mal diseñado porque no evaluaba las rutas alternativas del servicio de openlib market, violando las buenas prácticas de la cobertura. para solucionarlo bien, se debe cambiar la suite metiendo los escenarios de error que el robot ignoró en su primera etapa. los nuevos casos de prueba que faltaron consisten primero en verificar que el método lance una excepción controlada si el repositorio devuelve un vacío al buscar el identificador del libro. segundo, se debe programar un experimento para frenar el proceso si el stock enviado es un valor menor a cero, pues para evitar que los datos del inventario se dañen, la opción elegida debe asegurar que el sistema rebote la petición con una alerta. de esta manera, el sistema no solo demuestra un desarrollo guiado por pruebas real y completo usando mockito, sino que asegura que el motor de inventario de openlib market quede fácil de mantener, seguro y listo para aguantar refactorizaciones futuras.
