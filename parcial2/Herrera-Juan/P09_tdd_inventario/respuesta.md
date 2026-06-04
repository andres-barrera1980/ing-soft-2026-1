# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [9]: [tdd inventario]

### Estudiante
- **Nombre completo**: [Juan Fernando Herrera Patiño]

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [ChatGPT] |
| **Modelo específico** | [GPT-4o ]|
| **¿Por qué elegiste este LLM?** | [mas comodo para mi] |

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
Actúa como un experto en TDD (Test Driven Development), JUnit 5, Mockito y desarrollo Java. Demuestra el ciclo completo Red-Green-Refactor para implementar una clase llamada ControlInventarioService que gestione el inventario de OpenLib Market. Las reglas de negocio son: cuando el stock de un libro llega a cero debe cambiar automáticamente a estado AGOTADO, notificar al vendedor y permitir que el frontend identifique que el botón "Agregar al carrito" debe deshabilitarse; cuando el vendedor repone stock (stock mayor que cero) el libro debe volver a estado DISPONIBLE. Debes seguir estrictamente la metodología TDD mostrando primero la fase RED con las pruebas unitarias que inicialmente fallan usando JUnit 5 y Mockito, explicando por qué fallan y sin escribir todavía la implementación. Después muestra la fase GREEN implementando únicamente el código mínimo necesario para que las pruebas pasen y explicando qué pruebas quedan en verde. Finalmente realiza la fase REFACTOR mejorando el diseño sin modificar el comportamiento ni romper las pruebas existentes. Incluye código Java completo, uso de mocks para dependencias externas y pruebas para los escenarios de stock igual a cero, stock mayor que cero, reposición de stock, notificación al vendedor, cambio a estado AGOTADO, cambio a estado DISPONIBLE y validación de disponibilidad para el frontend. Concluye explicando cómo verificar que realmente se siguió TDD y no simplemente se escribió primero la implementación y luego las pruebas.

```

---

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**
Implementación de TDD para ControlInventarioService (Red → Green → Refactor)
Contexto

OpenLib Market necesita controlar automáticamente el estado de los libros según su stock:

Stock = 0 → Estado AGOTADO
Al quedar AGOTADO → Notificar vendedor
Estado AGOTADO → Frontend deshabilita "Agregar al carrito"
Stock > 0 → Estado DISPONIBLE

Seguiremos estrictamente el flujo TDD:

1. RED
2. GREEN
3. REFACTOR
FASE 1 — RED
Objetivo

Primero escribimos las pruebas.

Todavía NO existe:

ControlInventarioService

Por tanto las pruebas no compilan o fallan.

Eso es exactamente lo esperado en TDD.

Modelo de dominio mínimo para las pruebas
EstadoLibro
public enum EstadoLibro {
    DISPONIBLE,
    AGOTADO
}
Libro
public class Libro {

    private String titulo;
    private String emailVendedor;
    private int stock;
    private EstadoLibro estado;

    public Libro(String titulo,
                 String emailVendedor,
                 int stock,
                 EstadoLibro estado) {

        this.titulo = titulo;
        this.emailVendedor = emailVendedor;
        this.stock = stock;
        this.estado = estado;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getEmailVendedor() {
        return emailVendedor;
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

    public void setEstado(EstadoLibro estado) {
        this.estado = estado;
    }
}
Dependencia Externa
NotificacionService

Será simulada mediante Mockito.

public interface NotificacionService {

    void notificarVendedor(
            String email,
            String mensaje);
}
Pruebas Unitarias (RED)
ControlInventarioServiceTest
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ControlInventarioServiceTest {

    @Mock
    private NotificacionService notificacionService;

    private ControlInventarioService service;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        service =
                new ControlInventarioService(
                        notificacionService);
    }

    @Test
    void givenStockLlegaACero_whenActualizarStock_thenEstadoAgotado() {

        Libro libro =
                new Libro(
                        "Clean Code",
                        "vendor@test.com",
                        5,
                        EstadoLibro.DISPONIBLE);

        service.actualizarStock(libro, 0);

        assertEquals(
                EstadoLibro.AGOTADO,
                libro.getEstado());
    }

    @Test
    void givenStockLlegaACero_whenActualizarStock_thenNotificaVendedor() {

        Libro libro =
                new Libro(
                        "Clean Code",
                        "vendor@test.com",
                        5,
                        EstadoLibro.DISPONIBLE);

        service.actualizarStock(libro, 0);

        verify(notificacionService)
                .notificarVendedor(
                        eq("vendor@test.com"),
                        contains("agotado"));
    }

    @Test
    void givenStockMayorACero_whenActualizarStock_thenPermaneceDisponible() {

        Libro libro =
                new Libro(
                        "Clean Code",
                        "vendor@test.com",
                        10,
                        EstadoLibro.DISPONIBLE);

        service.actualizarStock(libro, 3);

        assertEquals(
                EstadoLibro.DISPONIBLE,
                libro.getEstado());
    }

    @Test
    void givenLibroAgotado_whenReponerStock_thenEstadoDisponible() {

        Libro libro =
                new Libro(
                        "Clean Code",
                        "vendor@test.com",
                        0,
                        EstadoLibro.AGOTADO);

        service.actualizarStock(libro, 8);

        assertEquals(
                EstadoLibro.DISPONIBLE,
                libro.getEstado());
    }

    @Test
    void givenLibroAgotado_whenConsultarDisponibilidad_thenRetornaFalse() {

        Libro libro =
                new Libro(
                        "Clean Code",
                        "vendor@test.com",
                        0,
                        EstadoLibro.AGOTADO);

        assertFalse(
                service.estaDisponible(libro));
    }

    @Test
    void givenLibroDisponible_whenConsultarDisponibilidad_thenRetornaTrue() {

        Libro libro =
                new Libro(
                        "Clean Code",
                        "vendor@test.com",
                        5,
                        EstadoLibro.DISPONIBLE);

        assertTrue(
                service.estaDisponible(libro));
    }
}
¿Por qué fallan?

Porque aún NO existe:

ControlInventarioService

Las pruebas definen el comportamiento esperado antes de escribir la implementación.

Esto representa la fase RED.

FASE 2 — GREEN
Objetivo

Escribir únicamente el código mínimo para que las pruebas pasen.

Implementación mínima
public class ControlInventarioService {

    private final NotificacionService
            notificacionService;

    public ControlInventarioService(
            NotificacionService notificacionService) {

        this.notificacionService =
                notificacionService;
    }

    public void actualizarStock(
            Libro libro,
            int nuevoStock) {

        libro.setStock(nuevoStock);

        if (nuevoStock == 0) {

            libro.setEstado(
                    EstadoLibro.AGOTADO);

            notificacionService
                    .notificarVendedor(
                            libro.getEmailVendedor(),
                            "El libro está agotado");

        } else {

            libro.setEstado(
                    EstadoLibro.DISPONIBLE);
        }
    }

    public boolean estaDisponible(
            Libro libro) {

        return libro.getEstado()
                == EstadoLibro.DISPONIBLE;
    }
}
Resultado

Ahora pasan:

Test 1
givenStockLlegaACero_whenActualizarStock_thenEstadoAgotado

✔ Verde

Test 2
givenStockLlegaACero_whenActualizarStock_thenNotificaVendedor

✔ Verde

Test 3
givenStockMayorACero_whenActualizarStock_thenPermaneceDisponible

✔ Verde

Test 4
givenLibroAgotado_whenReponerStock_thenEstadoDisponible

✔ Verde

Test 5
givenLibroAgotado_whenConsultarDisponibilidad_thenRetornaFalse

✔ Verde

Test 6
givenLibroDisponible_whenConsultarDisponibilidad_thenRetornaTrue

✔ Verde

FASE 3 — REFACTOR
Problema Detectado

Actualmente existe lógica de negocio dentro del servicio:

if (nuevoStock == 0)

La entidad Libro debería ser responsable de mantener su propio estado.

Aplicamos encapsulación.

Libro Refactorizado
public class Libro {

    private String titulo;
    private String emailVendedor;
    private int stock;
    private EstadoLibro estado;

    public Libro(
            String titulo,
            String emailVendedor,
            int stock,
            EstadoLibro estado) {

        this.titulo = titulo;
        this.emailVendedor = emailVendedor;
        this.stock = stock;
        this.estado = estado;
    }

    public void actualizarStock(
            int nuevoStock) {

        this.stock = nuevoStock;

        if (nuevoStock <= 0) {

            this.estado =
                    EstadoLibro.AGOTADO;

        } else {

            this.estado =
                    EstadoLibro.DISPONIBLE;
        }
    }

    public boolean estaDisponible() {

        return estado ==
                EstadoLibro.DISPONIBLE;
    }

    public String getEmailVendedor() {
        return emailVendedor;
    }

    public EstadoLibro getEstado() {
        return estado;
    }
}
Servicio Refactorizado
public class ControlInventarioService {

    private final NotificacionService
            notificacionService;

    public ControlInventarioService(
            NotificacionService notificacionService) {

        this.notificacionService =
                notificacionService;
    }

    public void actualizarStock(
            Libro libro,
            int nuevoStock) {

        EstadoLibro estadoAnterior =
                libro.getEstado();

        libro.actualizarStock(
                nuevoStock);

        if (estadoAnterior
                != EstadoLibro.AGOTADO
                &&
                libro.getEstado()
                == EstadoLibro.AGOTADO) {

            notificacionService
                    .notificarVendedor(
                            libro.getEmailVendedor(),
                            "El libro está agotado");
        }
    }

    public boolean estaDisponible(
            Libro libro) {

        return libro.estaDisponible();
    }
}
Mejoras Conseguidas
SRP

Antes:

ControlInventarioService

controlaba:

Stock
Estado
Notificaciones

Ahora:

Libro

controla:

Estado
Disponibilidad

y

ControlInventarioService

coordina el proceso.

Menor Acoplamiento

La lógica del dominio vive en la entidad.

Mayor Testabilidad

La entidad puede probarse independientemente.

¿Cómo demostrar que realmente se hizo TDD?

Un profesor puede verificarlo observando:

Evidencia 1

Las pruebas fueron escritas antes de la implementación.

Secuencia:

Test
 ↓
Falla
 ↓
Implementación mínima
 ↓
Pasa
Evidencia 2

La implementación inicial es extremadamente simple.

if (nuevoStock == 0)

No hay optimizaciones prematuras.

Evidencia 3

El refactor ocurre después de tener todas las pruebas en verde.

RED
 ↓
GREEN
 ↓
REFACTOR
Evidencia 4

Las pruebas nunca se modifican durante el refactor.

Solo cambia:

ControlInventarioService
Libro

Las pruebas siguen pasando exactamente igual.

Resultado Final

Se implementó correctamente el ciclo TDD Red → Green → Refactor para ControlInventarioService, cubriendo:

✅ Stock llega a cero
✅ Cambio a AGOTADO
✅ Notificación al vendedor (Mockito)
✅ Frontend puede consultar disponibilidad
✅ Reposición de stock
✅ Cambio a DISPONIBLE
✅ Refactor manteniendo todas las pruebas en verde

La solución queda alineada con buenas prácticas de TDD, JUnit 5, Mockito y principios SOLID (especialmente SRP y DIP).

---

### Análisis crítico de la respuesta
El LLM siguió adecuadamente la estructura Red-Green-Refactor al presentar primero las pruebas unitarias, luego una implementación mínima y finalmente un refactor. Las pruebas cubren los escenarios principales del negocio: cambio a estado AGOTADO cuando el stock llega a cero, notificación al vendedor, reposición de stock, estado DISPONIBLE y consulta de disponibilidad para el frontend. El refactor mejora el diseño al trasladar la lógica de actualización de estado a la entidad Libro, aumentando la cohesión y reduciendo responsabilidades en el servicio. Sin embargo, no existe evidencia real de ejecución de las pruebas antes de la implementación, por lo que no puede verificarse completamente que el proceso haya sido TDD genuino y no una reconstrucción posterior. Para comprobarlo sería necesario observar la evolución del código mediante commits o ejecuciones sucesivas de pruebas fallando y luego pasando.

#### 1. ¿Qué hizo bien el prompt?

El prompt fue muy efectivo porque obligó al LLM a seguir explícitamente las fases RED, GREEN y REFACTOR, evitando una explicación únicamente teórica. Además, exigió pruebas unitarias con JUnit 5 y Mockito antes de la implementación, incluyó escenarios específicos de negocio y solicitó una explicación sobre cómo demostrar que realmente se aplicó TDD. Estas restricciones ayudaron a obtener una respuesta estructurada y alineada con la metodología.



#### 2. ¿Qué se puede mejorar?

El prompt podría mejorarse exigiendo evidencia más rigurosa de la fase RED, por ejemplo mostrando errores de compilación o fallos concretos de las pruebas antes de escribir la implementación. También sería útil solicitar casos adicionales, como evitar notificaciones duplicadas cuando un libro ya está agotado o validar valores de stock negativos. Aunque la respuesta sigue correctamente el flujo TDD, parte de la secuencia es explicada y no demostrada mediante ejecuciones reales de pruebas.



#### 3. Respuesta final

La funcionalidad de control de inventario puede desarrollarse correctamente mediante TDD siguiendo el ciclo Red-Green-Refactor. Primero se definen pruebas que describen el comportamiento esperado: cambiar el estado a AGOTADO cuando el stock llega a cero, notificar al vendedor, indicar al frontend que el libro no está disponible y restaurar el estado DISPONIBLE cuando se repone inventario. Posteriormente se implementa el código mínimo necesario para satisfacer dichas pruebas y finalmente se realiza un refactor para mejorar el diseño sin modificar el comportamiento. Una mejora importante consiste en trasladar la lógica de estado a la entidad Libro y dejar que ControlInventarioService se encargue únicamente de coordinar el proceso y las notificaciones. Este enfoque garantiza que las reglas de negocio estén protegidas por pruebas automatizadas y facilita la evolución del sistema sin introducir regresiones.
