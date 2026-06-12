## P05_patrones_notificaciones

### Estudiante
- **Nombre completo**: [Alejandro Macias Barrios]

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [Claude] |
| **Modelo específico** | [Claude Sonnet 4.6] |
| **¿Por qué elegiste este LLM?** | [Claude ha demostrado tener un buen rendimiento en tareas de diseño y refactorización de código, así como también en la aplicación de principios de ingeniería de software. Además, ha sido uno de los que mejor hemos usado en clase, y considero que es uno de los modelos más avanzados disponibles.] |

### Prompt utilizado

[Imagina que eres un estudiante de Ingeniería de sistemas de la Pontificia Universidad Javeriana cursando la materia de Fundamentos de Ingeniería de Software. El profesor te ha hecho la siguiente pregunta y debes responderla de la mejor manera posible.
En OpenLib Market, cuando un libro agotado vuelve a estar disponible, varios componentes del sistema necesitan reaccionar:

- Enviar notificación por correo a los usuarios que lo agregaron a su wishlist.
- Enviar notificación push a los usuarios que lo marcaron como favorito en la app móvil.
- Actualizar la caché de Redis para que aparezca en búsquedas.
- Registrar el evento en un log de auditoría.

Diseñe e implemente este sistema de notificaciones usando dos patrones de diseño combinados de los vistos en clase. Debe incluir: diagrama de clases (en Mermaid o texto), código Java de las clases principales, y explicación de por qué eligió esos dos patrones. Pega el prompt y la respuesta.]

### Respuesta del LLM

[Diseño del sistema de notificaciones
Patrones elegidos: Observer + Command
¿Por qué Observer?
El enunciado describe exactamente el problema que Observer resuelve: un evento (LibroDisponible) que múltiples componentes desacoplados necesitan atender. El libro no debería saber quién lo escucha ni cuántos oyentes existen.
¿Por qué Command?
Cada reacción (enviar correo, enviar push, actualizar caché, auditar) es una acción independiente que puede fallar, reintentarse o encolarse. Command encapsula cada operación como un objeto, lo que permite manejo de errores por acción sin que una falla detenga las demás.
La sinergia: Observer decide quién es notificado. Command decide cómo se ejecuta cada notificación de forma robusta e independiente.

Diagrama de clases
(No pude pegar la imagen que generó la IA)

Implementación Java
// ============================================================
// 1. EVENTO — el objeto que viaja por la cadena
// ============================================================
public class EventoLibroDisponible {
    private final Libro libro;
    private final LocalDateTime timestamp;

    public EventoLibroDisponible(Libro libro) {
        this.libro = libro;
        this.timestamp = LocalDateTime.now();
    }

    public Libro getLibro() { return libro; }
    public LocalDateTime getTimestamp() { return timestamp; }
}

// ============================================================
// 2. OBSERVER — contrato de suscripción
// ============================================================
public interface ObservadorLibro {
    void actualizar(EventoLibroDisponible evento);
}

// ============================================================
// 3. COMMAND — contrato de ejecución
// ============================================================
public interface ComandoNotificacion {
    void ejecutar() throws ExcepcionNotificacion;
    String getNombre(); // para logs de error
}

// ============================================================
// 4. EJECUTOR — maneja errores y reintentos por comando
//    Una falla en Email NO detiene Push ni Caché
// ============================================================
public class EjecutorComandos {

    private static final Logger log =
        LoggerFactory.getLogger(EjecutorComandos.class);
    private final int maxReintentos;

    public EjecutorComandos(int maxReintentos) {
        this.maxReintentos = maxReintentos;
    }

    public void ejecutar(ComandoNotificacion comando) {
        int intentos = 0;
        while (intentos < maxReintentos) {
            try {
                comando.ejecutar();
                log.info("[OK] Comando ejecutado: {}", comando.getNombre());
                return;
            } catch (ExcepcionNotificacion e) {
                intentos++;
                log.warn("[REINTENTO {}/{}] Fallo en {}: {}",
                    intentos, maxReintentos, comando.getNombre(), e.getMessage());
            }
        }
        log.error("[FALLO DEFINITIVO] Comando no ejecutado: {}",
            comando.getNombre());
    }
}

// ============================================================
// 5. PUBLISHER — núcleo del patrón Observer
// ============================================================
public class PublicadorLibro {

    private final List<ObservadorLibro> observadores = new ArrayList<>();

    public void suscribir(ObservadorLibro observador) {
        observadores.add(observador);
    }

    public void desuscribir(ObservadorLibro observador) {
        observadores.remove(observador);
    }

    // Llamado cuando un libro vuelve a tener stock
    public void notificarDisponibilidad(Libro libro) {
        EventoLibroDisponible evento = new EventoLibroDisponible(libro);
        // El libro no sabe nada de sus oyentes
        observadores.forEach(obs -> obs.actualizar(evento));
    }
}

// ============================================================
// 6. COMANDOS CONCRETOS — una responsabilidad por clase
// ============================================================

public class ComandoEnviarEmail implements ComandoNotificacion {

    private final ServicioEmail servicioEmail;
    private final List<Usuario> destinatarios;
    private final Libro libro;

    public ComandoEnviarEmail(ServicioEmail servicioEmail,
                               List<Usuario> destinatarios,
                               Libro libro) {
        this.servicioEmail  = servicioEmail;
        this.destinatarios  = destinatarios;
        this.libro          = libro;
    }

    @Override
    public void ejecutar() throws ExcepcionNotificacion {
        try {
            String asunto  = "¡" + libro.getTitulo() + " ya está disponible!";
            String cuerpo  = "El libro que agregaste a tu wishlist volvió al stock.";
            servicioEmail.enviarMasivo(destinatarios, asunto, cuerpo);
        } catch (Exception e) {
            throw new ExcepcionNotificacion("Error enviando emails", e);
        }
    }

    @Override
    public String getNombre() { return "EnviarEmail[" + libro.getTitulo() + "]"; }
}

public class ComandoEnviarPush implements ComandoNotificacion {

    private final ServicioPush servicioPush;
    private final List<Usuario> destinatarios;
    private final Libro libro;

    public ComandoEnviarPush(ServicioPush servicioPush,
                              List<Usuario> destinatarios,
                              Libro libro) {
        this.servicioPush  = servicioPush;
        this.destinatarios = destinatarios;
        this.libro         = libro;
    }

    @Override
    public void ejecutar() throws ExcepcionNotificacion {
        try {
            Notificacion notif = new Notificacion(
                "Stock disponible",
                libro.getTitulo() + " que marcaste favorito ya tiene stock"
            );
            servicioPush.enviarATodos(destinatarios, notif);
        } catch (Exception e) {
            throw new ExcepcionNotificacion("Error enviando push", e);
        }
    }

    @Override
    public String getNombre() { return "EnviarPush[" + libro.getTitulo() + "]"; }
}

public class ComandoActualizarCache implements ComandoNotificacion {

    private final ClienteRedis redis;
    private final Libro libro;

    public ComandoActualizarCache(ClienteRedis redis, Libro libro) {
        this.redis = redis;
        this.libro = libro;
    }

    @Override
    public void ejecutar() throws ExcepcionNotificacion {
        try {
            redis.set("libro:" + libro.getId(), libro.toJson());
            redis.invalidarBusquedas(libro.getCategoria());
        } catch (Exception e) {
            throw new ExcepcionNotificacion("Error actualizando caché", e);
        }
    }

    @Override
    public String getNombre() { return "ActualizarCache[" + libro.getId() + "]"; }
}

public class ComandoRegistrarAuditoria implements ComandoNotificacion {

    private final ServicioAuditoria auditoria;
    private final EventoLibroDisponible evento;

    public ComandoRegistrarAuditoria(ServicioAuditoria auditoria,
                                      EventoLibroDisponible evento) {
        this.auditoria = auditoria;
        this.evento    = evento;
    }

    @Override
    public void ejecutar() throws ExcepcionNotificacion {
        try {
            auditoria.registrar(
                "LIBRO_DISPONIBLE",
                evento.getLibro().getId(),
                evento.getTimestamp()
            );
        } catch (Exception e) {
            throw new ExcepcionNotificacion("Error en auditoría", e);
        }
    }

    @Override
    public String getNombre() {
        return "Auditoria[" + evento.getLibro().getId() + "]";
    }
}

// ============================================================
// 7. OBSERVADORES CONCRETOS — unen Observer con Command
// ============================================================

public class ObservadorNotificacionEmail implements ObservadorLibro {

    private final ServicioEmail servicioEmail;
    private final RepositorioWishlist wishlist;
    private final EjecutorComandos ejecutor;

    public ObservadorNotificacionEmail(ServicioEmail servicioEmail,
                                        RepositorioWishlist wishlist,
                                        EjecutorComandos ejecutor) {
        this.servicioEmail = servicioEmail;
        this.wishlist      = wishlist;
        this.ejecutor      = ejecutor;
    }

    @Override
    public void actualizar(EventoLibroDisponible evento) {
        List<Usuario> interesados =
            wishlist.usuariosConLibro(evento.getLibro().getId());

        ComandoNotificacion comando =
            new ComandoEnviarEmail(servicioEmail, interesados, evento.getLibro());

        ejecutor.ejecutar(comando);
    }
}

public class ObservadorNotificacionPush implements ObservadorLibro {

    private final ServicioPush servicioPush;
    private final RepositorioFavoritos favoritos;
    private final EjecutorComandos ejecutor;

    public ObservadorNotificacionPush(ServicioPush servicioPush,
                                       RepositorioFavoritos favoritos,
                                       EjecutorComandos ejecutor) {
        this.servicioPush = servicioPush;
        this.favoritos    = favoritos;
        this.ejecutor     = ejecutor;
    }

    @Override
    public void actualizar(EventoLibroDisponible evento) {
        List<Usuario> interesados =
            favoritos.usuariosConFavorito(evento.getLibro().getId());

        ComandoNotificacion comando =
            new ComandoEnviarPush(servicioPush, interesados, evento.getLibro());

        ejecutor.ejecutar(comando);
    }
}

// ObservadorActualizacionCache y ObservadorAuditoria siguen
// el mismo patrón — omitidos por brevedad

// ============================================================
// 8. ENSAMBLAJE — configuración del sistema completo
// ============================================================
public class ConfiguracionNotificaciones {

    public PublicadorLibro configurar(/* dependencias inyectadas */) {

        EjecutorComandos ejecutor = new EjecutorComandos(3); // 3 reintentos

        PublicadorLibro publicador = new PublicadorLibro();

        publicador.suscribir(
            new ObservadorNotificacionEmail(servicioEmail, repoWishlist, ejecutor));
        publicador.suscribir(
            new ObservadorNotificacionPush(servicioPush, repoFavoritos, ejecutor));
        publicador.suscribir(
            new ObservadorActualizacionCache(clienteRedis, ejecutor));
        publicador.suscribir(
            new ObservadorAuditoria(servicioAuditoria, ejecutor));

        return publicador;
    }
}

// ============================================================
// 9. EJEMPLO DE USO — cuando el libro vuelve a tener stock
// ============================================================
public class ServicioInventario {

    private final PublicadorLibro publicador;
    private final RepositorioLibros repositorio;

    public void actualizarStock(String libroId, int cantidad) {
        Libro libro = repositorio.buscarPorId(libroId);
        boolean estabaAgotado = libro.getStock() == 0;

        libro.setStock(cantidad);
        repositorio.guardar(libro);

        // Solo dispara el evento si realmente volvió de agotado
        if (estabaAgotado && cantidad > 0) {
            publicador.notificarDisponibilidad(libro);
        }
    }
}

Flujo completo de una notificación
ServicioInventario.actualizarStock("clean-code", 5)
        │
        ▼
PublicadorLibro.notificarDisponibilidad(libro)
        │
        ├──▶ ObservadorEmail.actualizar(evento)
        │         └──▶ EjecutorComandos.ejecutar(ComandoEnviarEmail)
        │                   └──▶ servicioEmail.enviarMasivo(...)  ✓ / reintento
        │
        ├──▶ ObservadorPush.actualizar(evento)
        │         └──▶ EjecutorComandos.ejecutar(ComandoEnviarPush)
        │                   └──▶ servicioPush.enviarATodos(...)   ✓ / reintento
        │
        ├──▶ ObservadorCache.actualizar(evento)
        │         └──▶ EjecutorComandos.ejecutar(ComandoActualizarCache)
        │                   └──▶ redis.set(...)                   ✓ / reintento
        │
        └──▶ ObservadorAuditoria.actualizar(evento)
                  └──▶ EjecutorComandos.ejecutar(ComandoAuditoria)
                            └──▶ auditoria.registrar(...)         ✓ / reintento
]


### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?
[El prompt estableció un rol de estudiante de ingeniería de software y proporcionó el contexto del problema. Dio un ejemplo claro de cómo estaba implementado inicialmente y pidió explícitamente que se señalaran los principios violados y un rediseño completo. Además proporcionó una imagen con una explicación clara de los principios.]

#### 2. ¿Qué se puede mejorar?
[Yo creo que el prompt podria haber sido un poco mas especifico en cuanto a lo que queria, por ejemplo, podria haber pedido que se señalaran los principios violados en orden de importancia, o que se proporcionara un ejemplo de como se podria implementar el rediseño. Por otro lado, no pidio que se explicara por que se debia cambiar el diseño, solo pidio que se mostrara un rediseño. Adicionalmente, el prompt no estableció un formato de salida, lo que hace que sea mas dificil entender lo que se queria.]

#### 3. Respuesta final
[Para resolver el problema de las notificaciones cuando un libro vuelve a estar disponible, la mejor solución es combinar los patrones Observer y Command, ya que cada uno aborda una necesidad diferente del sistema y juntos proporcionan una arquitectura flexible, desacoplada y escalable.

El patrón Observer se utiliza para modelar la relación entre el evento y los componentes interesados. Cuando un libro pasa de estar agotado a disponible, se genera un evento que es publicado por el sistema. Los observadores suscritos (correo electrónico, notificaciones push, actualización de caché y auditoría) reciben automáticamente la notificación sin que el libro o el inventario conozcan los detalles de cada uno de ellos. Esto reduce el acoplamiento y facilita agregar nuevos tipos de notificaciones en el futuro. Por otro ladoel patrón Command encapsula cada acción específica dentro de un objeto independiente. En lugar de ejecutar directamente tareas como enviar correos o actualizar Redis, cada operación se representa mediante un comando que puede ejecutarse, registrarse, reintentarse o manejar errores de forma aislada. Gracias a esto, si una notificación falla, las demás continúan ejecutándose normalmente, aumentando la robustez del sistema.
La combinación de ambos patrones genera una arquitectura basada en eventos donde:
El sistema detecta que un libro vuelve a tener stock, el publicador genera un evento de disponibilidad, todos los observadores registrados reciben el evento, cada observador crea y ejecuta el comando correspondiente y las acciones se ejecutan de manera independiente y segura.

Desde el punto de vista de los principios SOLID, esta solución favorece:
Single Responsibility Principle ya que cada clase tiene una única responsabilidad.
Open/Closed Principle ya que se pueden agregar nuevos observadores o comandos sin modificar el código existente.
Dependency Inversion Principle ya que los módulos dependen de abstracciones (ObservadorLibro, ComandoNotificacion) y no de implementaciones concretas.]
