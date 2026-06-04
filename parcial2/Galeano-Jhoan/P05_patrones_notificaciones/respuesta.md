# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [5]: [P05_patrones_notificaciones]

### Estudiante
- **Nombre completo**: [Jhoan Galeano]

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [Claude ] |
| **Modelo específico** | [Ej: Claude 3.1 pro |
| **¿Por qué elegiste este LLM?** | [Escogi esta IA porque puede pensar mas a fondo y esto se necesita para poder obtener la respuesta mas certera a la pregunta] |

---

### Prompt utilizado

Actúa como un ingeniero de software senior y arquitecto de soluciones. Analiza el siguiente requerimiento para el sistema de eventos de OpenLib Market:

Cuando un libro agotado vuelve a estar disponible, múltiples componentes independientes deben reaccionar de forma inmediata:
1. Enviar un correo electrónico a los usuarios con el libro en su wishlist.
2. Enviar una notificación push a la app móvil de los usuarios que lo marcaron como favorito.
3. Actualizar la caché de Redis para que el libro vuelva a aparecer en las búsquedas.
4. Registrar el evento en un log de auditoría.

Tarea:
Diseña e implementa una solución desacoplada y limpia utilizando de forma combinada DOS patrones de diseño de software. Genera una respuesta con la siguiente estructura:

1. Selección y Justificación: Explica detalladamente qué dos patrones elegiste, por qué hacen sentido juntos y cómo reducen el acoplamiento entre el libro y los componentes que reaccionan.
2. Diagrama de Clases: Presenta un diagrama de clases claro utilizando sintaxis de Mermaid.
3. Implementación en Java: Escribe el código funcional en Java que muestre la interfaz del evento, los componentes interesados (que puedan registrarse y desregistrarse dinámicamente) y un método main simulando el cambio de estado del libro. Incluye un mecanismo básico de manejo de errores por si uno de los componentes falla.
```

---

### Respuesta del LLM

1. Selección y Justificación
Observer + Command es la combinación correcta para este requerimiento.
Observer resuelve el desacoplamiento: Libro no conoce a ninguno de los cuatro componentes. Solo notifica que algo ocurrió, y quien esté suscrito reacciona. Los componentes se registran y desregistran en tiempo de ejecución sin que el libro sepa cuántos hay ni qué hacen.
Command resuelve la encapsulación de la acción: cada notificación se convierte en un objeto independiente con su propia lógica, su propio manejo de error y su posibilidad de reintento o rollback. Sin Command, los observers tendrían lógica compleja directamente en actualizar(), mezclando responsabilidades.
Juntos forman un pipeline limpio: Observer detecta quién debe reaccionar, Command encapsula qué debe ejecutarse. Un observer simplemente toma su Command y lo ejecuta, sin saber nada de los demás.
Por qué no solo Observer: los observers terminarían con lógica de negocio pesada (enviar email, conectar Redis, escribir log) directamente en su método actualizar(), violando SRP.
Por qué no solo Command: necesitaríamos que Libro conozca y llame explícitamente a cada comando cuando cambia de estado, creando acoplamiento directo con todos los componentes.
import java.util.ArrayList;
import java.util.List;

// ─── Evento ───────────────────────────────────────────────────────────────

class EventoLibro {
    final String isbn;
    final String titulo;
    final int stock;

    EventoLibro(String isbn, String titulo, int stock) {
        this.isbn = isbn;
        this.titulo = titulo;
        this.stock = stock;
    }
}

// ─── Interfaces Observer ──────────────────────────────────────────────────

interface ObservadorLibro {
    void actualizar(EventoLibro evento);
    String nombre();
}

interface PublicadorLibro {
    void suscribir(ObservadorLibro observador);
    void desuscribir(ObservadorLibro observador);
    void notificar(EventoLibro evento);
}

// ─── Interfaz Command ─────────────────────────────────────────────────────

interface ComandoNotificacion {
    void ejecutar(EventoLibro evento);
}

// ─── Implementaciones Command ─────────────────────────────────────────────

class ComandoEmail implements ComandoNotificacion {
    public void ejecutar(EventoLibro e) {
        System.out.println("  [Email]     Enviando correo a wishlist de: " + e.titulo);
    }
}

class ComandoPush implements ComandoNotificacion {
    public void ejecutar(EventoLibro e) {
        System.out.println("  [Push]      Notificación móvil enviada para: " + e.titulo);
    }
}

class ComandoCache implements ComandoNotificacion {
    public void ejecutar(EventoLibro e) {
        System.out.println("  [Redis]     Caché actualizada — isbn: " + e.isbn);
        // Simulamos fallo para demostrar manejo de errores
        if (e.isbn.equals("ERR-001"))
            throw new RuntimeException("Redis no disponible");
    }
}

class ComandoAuditoria implements ComandoNotificacion {
    public void ejecutar(EventoLibro e) {
        System.out.println("  [Auditoría] Evento registrado — " + e.titulo
            + " | stock: " + e.stock);
    }
}

// ─── Observer base abstracto (combina Observer + Command) ─────────────────

abstract class ObservadorBase implements ObservadorLibro {
    private final ComandoNotificacion comando;

    ObservadorBase(ComandoNotificacion comando) {
        this.comando = comando;
    }

    @Override
    public void actualizar(EventoLibro evento) {
        try {
            comando.ejecutar(evento);
        } catch (Exception ex) {
            System.err.println("  [ERROR en " + nombre() + "] "
                + ex.getMessage() + " — continuando con los demás...");
        }
    }
}

// ─── Observers concretos ──────────────────────────────────────────────────

class NotificadorEmail extends ObservadorBase {
    NotificadorEmail() { super(new ComandoEmail()); }
    public String nombre() { return "NotificadorEmail"; }
}

class NotificadorPush extends ObservadorBase {
    NotificadorPush() { super(new ComandoPush()); }
    public String nombre() { return "NotificadorPush"; }
}

class ActualizadorCache extends ObservadorBase {
    ActualizadorCache() { super(new ComandoCache()); }
    public String nombre() { return "ActualizadorCache"; }
}

class RegistradorAuditoria extends ObservadorBase {
    RegistradorAuditoria() { super(new ComandoAuditoria()); }
    public String nombre() { return "RegistradorAuditoria"; }
}

// ─── Libro (Publicador) ───────────────────────────────────────────────────

class Libro implements PublicadorLibro {
    private final String isbn;
    private final String titulo;
    private int stock;
    private final List<ObservadorLibro> observadores = new ArrayList<>();

    Libro(String isbn, String titulo) {
        this.isbn = isbn;
        this.titulo = titulo;
        this.stock = 0;
    }

    public void suscribir(ObservadorLibro o)   { observadores.add(o); }
    public void desuscribir(ObservadorLibro o) { observadores.remove(o); }

    public void notificar(EventoLibro evento) {
        System.out.println("\n Notificando a " + observadores.size()
            + " componentes sobre: " + evento.titulo);
        observadores.forEach(o -> o.actualizar(evento));
    }

    public void setStock(int nuevoStock) {
        boolean estabaAgotado = this.stock == 0;
        this.stock = nuevoStock;
        if (estabaAgotado && nuevoStock > 0) {
            notificar(new EventoLibro(isbn, titulo, nuevoStock));
        }
    }
}

// ─── Main ─────────────────────────────────────────────────────────────────

public class Main {
    public static void main(String[] args) {

        // Libro inicialmente agotado
        Libro libro = new Libro("978-001", "Clean Code");

        // Registro dinámico de observadores
        ObservadorLibro email     = new NotificadorEmail();
        ObservadorLibro push      = new NotificadorPush();
        ObservadorLibro cache     = new ActualizadorCache();
        ObservadorLibro auditoria = new RegistradorAuditoria();

        libro.suscribir(email);
        libro.suscribir(push);
        libro.suscribir(cache);
        libro.suscribir(auditoria);

        // Caso 1: libro vuelve a estar disponible → dispara todos
        System.out.println("══════════════════════════════════════");
        System.out.println(" Caso 1: libro vuelve a tener stock");
        System.out.println("══════════════════════════════════════");
        libro.setStock(10);

        // Caso 2: desuscribir push en tiempo de ejecución
        System.out.println("\n══════════════════════════════════════");
        System.out.println(" Caso 2: sin notificación push");
        System.out.println("══════════════════════════════════════");
        libro.desuscribir(push);
        libro.setStock(0);
        libro.setStock(5);

        // Caso 3: simular fallo en Redis — los demás siguen ejecutando
        System.out.println("\n══════════════════════════════════════");
        System.out.println(" Caso 3: fallo en Redis (isbn ERR-001)");
        System.out.println("══════════════════════════════════════");
        Libro libroConError = new Libro("ERR-001", "Refactoring");
        libroConError.suscribir(email);
        libroConError.suscribir(cache);
        libroConError.suscribir(auditoria);
        libroConError.setStock(3);
    }
}
![alt text](image.png)


---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

[El prompt fue suficiente pero para este caso fue un poco ambiguo a la hora de explicar que se necesitaba el diagrmaa de clases, debia enfatizar un poc mas directo.]


#### 2. ¿Qué se puede mejorar?

[Usar mejores palabras para que el prompt pueda ser mas claro y con ello para este caso, obtener una mejor respuesta, mas concisa yq ue el diagrama de clases tenga mas sentido y sea mas general sobre lo que se pide?]


#### 3. Respuesta final

[Escribe tu respuesta definitiva a la pregunta del parcial, integrando lo que aprendiste del LLM pero yendo más allá. Corrige errores, llena omisiones, conecta con conceptos vistos en clase. Esta es tu respuesta: demuestra que tú dominas el tema.]
