# Pregunta 9

### Estudiante
- **Nombre completo**: Nicolas Silva Garcia

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [Gemini] |
| **Modelo específico** | [Gemini 3.1 Pro] |
| **¿Por qué elegiste este LLM?** | [Aparte de que es la unica cuenta en la que tengo un servicio de pago en este momento, Gemini 3.1 pro me ha demostrado que es muy efectivo a la hora de asimilar roles y contexto a lo largo de una conversación. Esto me permite guiar su respuesta al punto exacto que necesito sin tener que estarle recordando detalles basicos o limitaciones del problema] |

---

### pregunta 9
OpenLib Market necesita una funcionalidad de **control de inventario**: cuando el stock de un libro llega a cero, el sistema debe marcarlo automáticamente como "AGOTADO", notificar al vendedor, y deshabilitar el botón de "Agregar al carrito" en el frontend. Cuando el vendedor repone stock, el libro vuelve a estado "DISPONIBLE".

**Tarea**: Aplica TDD a esta funcionalidad.

1. **Prompt**: Pídele a tu LLM que demuestre el ciclo completo de TDD (Red-Green-Refactor) para la clase `ControlInventarioService` que maneje esta lógica. Debe: (a) escribir primero las pruebas unitarias (JUnit 5 + Mockito) que fallen, (b) luego la implementación mínima para que pasen, y (c) finalmente un refactoring que mejore el diseño sin romper las pruebas. El código debe ser en Java. Pega el prompt y la respuesta.
2. **Análisis**: ¿El LLM realmente hizo TDD (test primero, implementación después)? ¿O escribió implementación y luego mostró pruebas que ya pasaban? ¿Las pruebas cubren todos los escenarios relevantes? ¿El refactoring del LLM mejoró el diseño sin cambiar comportamiento? ¿Cómo verificarías que el LLM no está simulando TDD sino aplicándolo genuinamente?


### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```
Actúa como un Agile Technical Coach y experto en Java.

Necesitamos implementar la clase ControlInventarioService para OpenLib Market.
Reglas de negocio:

    Si al actualizar el stock, este llega a 0: El libro cambia a estado "AGOTADO", se notifica al vendedor, y se expone un flag para que el frontend deshabilite la compra.

    Si el stock se actualiza a un número > 0: El libro cambia a estado "DISPONIBLE".

Tu tarea:
Debes demostrar un ciclo TDD estricto y secuencial. Estructura tu respuesta EXACTAMENTE en estas tres fases, asumiendo que después de cada fase ejecutamos el compilador.

Fase 1 (RED): Escribe ÚNICAMENTE la clase de prueba ControlInventarioServiceTest (JUnit 5 + Mockito). Debe fallar o no compilar porque la implementación aún no existe. Prueba las transiciones de estado y la notificación.

Fase 2 (GREEN): Escribe la implementación MÁS BÁSICA, ingenua y "sucia" posible de ControlInventarioService que logre pasar las pruebas de la Fase 1. No apliques buenas prácticas aquí, solo haz que los tests pasen (por ejemplo, usa strings quemados y condicionales simples).

Fase 3 (REFACTOR): Refactoriza la implementación de la Fase 2 para que sea código de producción limpio (ej. usando Enums, extrayendo métodos, eliminando magic strings), justificando por qué los tests de la Fase 1 garantizan que no rompiste nada.
```

---

### Respuesta del LLM

¡Entendido! Como Agile Technical Coach, me encanta este enfoque. Vamos a ejecutar un ciclo TDD puro. El objetivo aquí no es escribir el diseño final de inmediato, sino dejar que las pruebas guíen el diseño y nos proporcionen una red de seguridad infalible.

A continuación, presento el ciclo exacto: **Red, Green, Refactor**.

---

### Fase 1 (RED): La Especificación Ejecutable

En TDD, escribimos la prueba antes de que exista el código de producción. Asumimos las firmas de los métodos y las dependencias que *desearíamos* tener. Si ejecutamos esto en el IDE ahora mismo, ni siquiera compilará, lo cual es el primer y más puro estado de "Rojo".

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
    private INotificadorVendedor notificadorMock;

    private ControlInventarioService service;
    private Libro libro;

    @BeforeEach
    void setUp() {
        // Inicializamos el servicio inyectando el mock
        service = new ControlInventarioService(notificadorMock);
        // Asumimos un constructor de Libro básico para la prueba
        libro = new Libro("ISBN-123", "VEND-001"); 
    }

    @Test
    void actualizarStock_ACero_MarcaAgotadoDeshabilitaCompraYNotifica() {
        // Actuar
        service.actualizarStock(libro, 0);

        // Afirmar
        assertEquals("AGOTADO", libro.getEstado(), "El estado debe cambiar a AGOTADO");
        assertTrue(libro.isCompraDeshabilitada(), "El flag de compra debe estar en true");
        
        // Verificamos que el contrato de notificación se cumplió exactamente una vez
        verify(notificadorMock, times(1)).notificarAgotado("VEND-001", "ISBN-123");
    }

    @Test
    void actualizarStock_MayorACero_MarcaDisponibleYHabilitaCompra() {
        // Actuar
        service.actualizarStock(libro, 10);

        // Afirmar
        assertEquals("DISPONIBLE", libro.getEstado(), "El estado debe cambiar a DISPONIBLE");
        assertFalse(libro.isCompraDeshabilitada(), "El flag de compra debe estar en false");
        
        // Verificamos que NO se disparó la notificación
        verify(notificadorMock, never()).notificarAgotado(anyString(), anyString());
    }
}

```

---

### Fase 2 (GREEN): Lo Mínimo Necesario

Ahora, el objetivo es poner la barra en verde lo más rápido posible. Escribiremos el código de producción más ingenuo, acoplado y "sucio" (Hardcoded strings, lógica anémica) solo para satisfacer las afirmaciones de la prueba anterior.

```java
// Contratos mínimos para que el código compile
interface INotificadorVendedor {
    void notificarAgotado(String idVendedor, String isbn);
}

class Libro {
    private String isbn;
    private String idVendedor;
    private String estado;
    private boolean compraDeshabilitada;

    public Libro(String isbn, String idVendedor) {
        this.isbn = isbn;
        this.idVendedor = idVendedor;
    }

    // Getters y setters básicos expuestos
    public String getIsbn() { return isbn; }
    public String getIdVendedor() { return idVendedor; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public boolean isCompraDeshabilitada() { return compraDeshabilitada; }
    public void setCompraDeshabilitada(boolean compraDeshabilitada) { this.compraDeshabilitada = compraDeshabilitada; }
}

// Implementación "sucia" pero funcional
class ControlInventarioService {
    private INotificadorVendedor notificador;

    public ControlInventarioService(INotificadorVendedor notificador) {
        this.notificador = notificador;
    }

    public void actualizarStock(Libro libro, int cantidad) {
        // Uso de Magic Strings y lógica filtrada en el servicio
        if (cantidad == 0) {
            libro.setEstado("AGOTADO");
            libro.setCompraDeshabilitada(true);
            notificador.notificarAgotado(libro.getIdVendedor(), libro.getIsbn());
        } else {
            libro.setEstado("DISPONIBLE");
            libro.setCompraDeshabilitada(false);
        }
    }
}

```

*Si ejecutamos JUnit en este punto: **¡Barra Verde!** Las pruebas pasan.*

---

### Fase 3 (REFACTOR): Diseño Limpio y Seguro

Con la red de seguridad de nuestras pruebas en verde, podemos refactorizar sin miedo. Vamos a eliminar los *Magic Strings* introduciendo un `Enum`, y aplicaremos el principio *Information Expert* (DDD) para trasladar la responsabilidad del cambio de estado hacia adentro de la entidad `Libro`, dejando al servicio solo como orquestador.

**Regla de oro del Refactor:** No modificamos ni una sola línea de la clase de prueba (`ControlInventarioServiceTest`).

```java
// 1. Eliminamos los Magic Strings con un Enum seguro
enum EstadoLibro {
    DISPONIBLE, AGOTADO
}

// 2. Modelo de Dominio Rico: La entidad maneja su propio estado
class Libro {
    private String isbn;
    private String idVendedor;
    private EstadoLibro estado;
    private boolean compraDeshabilitada;

    public Libro(String isbn, String idVendedor) {
        this.isbn = isbn;
        this.idVendedor = idVendedor;
        this.estado = EstadoLibro.DISPONIBLE; 
    }

    // Lógica de negocio encapsulada
    public void registrarNuevoStock(int cantidad) {
        boolean sinStock = (cantidad <= 0);
        this.estado = sinStock ? EstadoLibro.AGOTADO : EstadoLibro.DISPONIBLE;
        this.compraDeshabilitada = sinStock;
    }

    // Adaptador para mantener retrocompatibilidad exacta con el Test de la Fase 1
    public String getEstado() {
        return estado.name(); 
    }

    public boolean isCompraDeshabilitada() { return compraDeshabilitada; }
    public String getIsbn() { return isbn; }
    public String getIdVendedor() { return idVendedor; }
}

// 3. Servicio Orquestador Limpio (Clean Architecture)
class ControlInventarioService {
    private final INotificadorVendedor notificador;

    public ControlInventarioService(INotificadorVendedor notificador) {
        this.notificador = notificador;
    }

    public void actualizarStock(Libro libro, int cantidad) {
        // Delegamos la mutación al dominio
        libro.registrarNuevoStock(cantidad);
        
        // Orquestamos los efectos secundarios mediante métodos privados claros
        procesarNotificaciones(libro);
    }

    private void procesarNotificaciones(Libro libro) {
        if (libro.isCompraDeshabilitada()) {
            notificador.notificarAgotado(libro.getIdVendedor(), libro.getIsbn());
        }
    }
}

```

**Justificación del Refactoring:**
Al ejecutar nuevamente las pruebas de la Fase 1 y ver que siguen en verde, podemos asegurar de manera categórica que nuestra refactorización arquitectónica no rompió el contrato del sistema. Cambiamos *cómo* se hace el trabajo (cohesión, encapsulamiento, delegación), pero *qué* hace el trabajo (transiciones y notificaciones) sigue siendo idéntico. Esto es el verdadero valor de aplicar TDD.

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

La linea "Escribe la implementación MÁS BÁSICA, ingenua y 'sucia' posible... No apliques buenas prácticas aquí" obliga al LLM a limitar la generacion de código limpio desde el inicio. Esto garantizó que la Fase 2 fuera genuinamente el paso "Green" de TDD y no una Fase 3 adelantada.

Se establece una línea de tiempo de desarrollo delimitando explícitamente (a), (b) y (c) asumiendo que "después de cada fase ejecutamos el compilador". 

Se define el estado inicial y final del dominio de manera clara (Stock a cero = AGOTADO + Notificación + Deshabilitar botón), dándole a la IA un marco de trabajo preciso para construir sus aserciones (assertEquals, assertTrue, verify).

#### 2. ¿Qué se puede mejorar?

Aunque lograste que el LLM simulara el proceso a la perfección (incluso el LLM hizo el excelente truco de devolver estado.name() en la Fase 3 para no romper el test de la Fase 1), a tu prompt le faltaron un par de candados lógicos que dejarían un bug grave en producción.

    Faltó definir la Idempotencia (El problema del correo duplicado): Tu prompt indicó: "Si al actualizar el stock, este llega a 0: El libro cambia a estado AGOTADO, se notifica...". Pero, ¿qué pasa, paso a paso, si el libro ya estaba agotado y un operario actualiza el stock nuevamente a 0 (o a un número negativo por error)? Según las reglas de tu prompt, el sistema enviará otro correo al vendedor.

        Cómo reformularlo: Debiste añadir una regla procedimental de idempotencia: "Si el libro ya se encontraba en estado AGOTADO y el stock se actualiza a 0, NO se debe volver a notificar al vendedor". Esto habría forzado a la IA a escribir un test en rojo (verify(notificador, never())) para ese escenario específico.

    La trampa del Refactor de Tests: En el TDD del mundo real, la Fase 3 (Refactor) no solo aplica al código de producción, sino también a los tests. Al no especificar esto, el LLM hizo malabares (como mantener el retorno de Strings) para no tocar el código de la Fase 1.

        Cómo reformularlo: "En la Fase 3, permite que las pruebas de la Fase 1 se actualicen a nivel sintáctico (por ejemplo, si introduces Enums), siempre y cuando la lógica de la aserción original se mantenga intacta".

    Omitiste el límite inferior (Stock Negativo): Definiste qué pasa cuando es > 0 y cuando llega a 0. En la ingeniería de software, los casos borde negativos rompen sistemas. Faltó pedirle a la IA que en sus pruebas validara si el sistema rechaza un stock de -5 con una excepción, o si lo trata como un 0.


#### 3. Respuesta final

Los cambios mas pertinentes para mejorar el TDD de esta pregunta son

en la fase RED
actualizarStock_Negativo_LanzaExcepcion: Para proteger el sistema contra límites inferiores, obligando al diseño a manejar una IllegalArgumentException antes de procesar cualquier lógica.

actualizarStock_ACeroPeroYaEstabaAgotado_NoVuelveANotificar: Para garantizar la idempotencia. Esta prueba exige mediante verify(..., never()) que el sistema no envíe spam al vendedor si el libro ya estaba en estado "AGOTADO".

en la fase GREEN
Una barrera superior: if (cantidad < 0) { throw new IllegalArgumentException(...); }.

Una barrera de estado: if (!"AGOTADO".equals(libro.getEstado())). Con esto, el notificador solo se dispara si el estado anterior era distinto a agotado, logrando la idempotencia rústica.

Refactor general

Delegación de Responsabilidad: El método registrarNuevoStock() evalua la transición de estado internamente y retorna un boolean (estabaDisponible && sinStock). El servicio simplemente recibe ese booleano y actua.

Actualización del Test: Se refactoriza el metodo para usar el Enum EstadoLibro.AGOTADO. Esto demuestra que las pruebas también se refactorizan sintácticamente para acompañar la evolución del diseño (tipado fuerte), siempre y cuando la lógica de la aserción original se mantenga intacta.