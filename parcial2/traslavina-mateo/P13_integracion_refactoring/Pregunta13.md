# Pregunta P13: Integración — Refactoring Notificador

### Estudiante
- **Nombre completo**: Mateo Traslaviña Moreno

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Claude |
| **Modelo específico** | Claude Sonnet 4.6 |
| **¿Por qué elegiste este LLM?** | Claude tiene excelente razonamiento para identificar problemas de integración y acoplamiento en código Java. Para refactorings que involucran patrones de diseño (Observer, Strategy, Facade), Claude propone soluciones estructuradas y genera código Spring Boot idiomático con inyección de dependencias correcta. |

---

### Prompt utilizado

```
Eres un ingeniero de software senior revisando código del proyecto OpenLib Market, plataforma de compra-venta de libros en Java 21 con Spring Boot 3.x.

Analiza el siguiente servicio de notificaciones del módulo de integración:

```java
@Service
public class NotificadorPedido {
    
    public void notificarPedidoCreado(Pedido pedido) {
        // Notificar por email
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.openlib.com");
        props.put("mail.smtp.port", "587");
        Session session = Session.getInstance(props);
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress("noreply@openlib.com"));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(pedido.getComprador().getEmail()));
        message.setSubject("Tu pedido #" + pedido.getId() + " fue creado");
        message.setText("Hola " + pedido.getComprador().getNombre() + ", tu pedido está en camino.");
        Transport.send(message);
        
        // Notificar por SMS
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.smsprovider.com/send"))
            .POST(HttpRequest.BodyPublishers.ofString(
                "{\"to\":\"" + pedido.getComprador().getTelefono() + "\",\"text\":\"Pedido #" + pedido.getId() + " creado\"}"))
            .header("Authorization", "Bearer hardcoded_sms_token_12345")
            .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        
        // Notificar al vendedor por email
        MimeMessage vendedorMsg = new MimeMessage(session);
        vendedorMsg.setFrom(new InternetAddress("noreply@openlib.com"));
        vendedorMsg.addRecipient(Message.RecipientType.TO, new InternetAddress(pedido.getVendedor().getEmail()));
        vendedorMsg.setSubject("Nuevo pedido de tu libro");
        vendedorMsg.setText("Tienes un nuevo pedido #" + pedido.getId());
        Transport.send(vendedorMsg);
        
        // Registrar en sistema externo de analytics
        HttpClient analyticsClient = HttpClient.newHttpClient();
        HttpRequest analyticsRequest = HttpRequest.newBuilder()
            .uri(URI.create("https://analytics.openlib.com/events"))
            .POST(HttpRequest.BodyPublishers.ofString("{\"event\":\"order_created\",\"orderId\":\"" + pedido.getId() + "\"}"))
            .build();
        analyticsClient.send(analyticsRequest, HttpResponse.BodyHandlers.ofString());
    }
}
```

Necesito que:
1. Identifiques todos los problemas de este código (no solo SOLID — incluye problemas de integración, seguridad, testabilidad)
2. Propongas un refactoring usando el patrón Observer/Event-Driven apropiado para Spring Boot
3. Generes el código refactorizado completo con: eventos de dominio, listeners, interfaces para cada canal de notificación
4. Expliques cómo este refactoring mejora la testabilidad (incluye ejemplo de test con mocks)

Restricciones: Java 21, Spring Boot 3.x, @EventListener de Spring, inyección de dependencias. No uses frameworks externos de mensajería (sin Kafka/RabbitMQ para este ejemplo).

Formato: markdown con secciones "Diagnóstico", "Problemas identificados", "Refactoring propuesto", "Código final", "Tests".
```

---

### Respuesta del LLM

## Diagnóstico

El `NotificadorPedido` es un ejemplo clásico de código que viola múltiples principios simultáneamente. Un solo método de ~40 líneas está acoplado a 4 sistemas externos diferentes con credenciales hardcodeadas, sin manejo de errores, y sin posibilidad de pruebas unitarias.

## Problemas identificados

**1. SRP — Responsabilidad múltiple**
El método hace 4 cosas distintas: email al comprador, SMS al comprador, email al vendedor, registro en analytics. Cada una es una razón independiente para cambiar.

**2. DIP — Dependencias concretas**
Depende directamente de `Session`, `MimeMessage`, `HttpClient` — implementaciones concretas. Imposible mockear para tests.

**3. OCP — Cerrado para extensión**
Si mañana necesitan agregar notificación push o WhatsApp, deben modificar este método.

**4. Seguridad crítica — Credenciales hardcodeadas**
`"Bearer hardcoded_sms_token_12345"` está en el código fuente. Si este código se sube a GitHub, el token queda expuesto para siempre en el historial.

**5. Manejo de errores ausente**
Si el servidor SMTP está caído, el SMS y analytics tampoco se envían aunque estén disponibles. Un fallo en cualquier canal cancela todos los siguientes.

**6. Acoplamiento al dominio**
`NotificadorPedido` conoce la estructura interna de `Pedido`, `Comprador`, y `Vendedor`. Si cambia algún getter, se rompe el notificador.

**7. No testeable**
No hay forma de ejecutar un test unitario sin conectarse realmente a SMTP, SMS API y analytics. Esto hace que el test sea lento, dependiente de red, y no determinístico.

## Refactoring propuesto

**Patrón: Spring Application Events (Observer desacoplado)**

Spring tiene un mecanismo de eventos integrado: `ApplicationEventPublisher` publica, `@EventListener` escucha. Los listeners son independientes, cada uno maneja su canal. Si un listener falla, los demás siguen ejecutándose.

```
PedidoService
    └── publica PedidoCreadoEvent
            ├── EmailCompradorListener → EmailNotificacionService
            ├── SmsCompradorListener   → SmsNotificacionService
            ├── EmailVendedorListener  → EmailNotificacionService
            └── AnalyticsListener      → AnalyticsService
```

## Código final

### Evento de dominio

```java
// Evento inmutable — solo datos, sin lógica
public record PedidoCreadoEvent(
    String pedidoId,
    String compradorEmail,
    String compradorNombre,
    String compradorTelefono,
    String vendedorEmail
) {}
```

### Interfaces de canal de notificación

```java
public interface EmailNotificacionService {
    void enviarEmail(String destinatario, String asunto, String cuerpo);
}

public interface SmsNotificacionService {
    void enviarSms(String telefono, String mensaje);
}

public interface AnalyticsService {
    void registrarEvento(String nombreEvento, String entidadId);
}
```

### Implementaciones (con configuración externalizada)

```java
@Service
public class SmtpEmailNotificacionService implements EmailNotificacionService {

    @Value("${mail.smtp.host}")
    private String smtpHost;

    @Value("${mail.smtp.port}")
    private int smtpPort;

    @Value("${mail.from}")
    private String from;

    @Override
    public void enviarEmail(String destinatario, String asunto, String cuerpo) {
        // Implementación real usando JavaMailSender de Spring
        // (se omite por brevedad)
    }
}

@Service
public class HttpSmsNotificacionService implements SmsNotificacionService {

    @Value("${sms.api.url}")
    private String smsApiUrl;

    @Value("${sms.api.token}")  // ← viene de application.properties o variable de entorno
    private String smsToken;

    @Override
    public void enviarSms(String telefono, String mensaje) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(smsApiUrl))
            .POST(HttpRequest.BodyPublishers.ofString(
                String.format("{\"to\":\"%s\",\"text\":\"%s\"}", telefono, mensaje)))
            .header("Authorization", "Bearer " + smsToken)
            .build();
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            // Log y continúa — no bloquea otros canales
            log.warn("Fallo al enviar SMS a {}: {}", telefono, e.getMessage());
        }
    }
}
```

### Listeners independientes

```java
@Component
public class EmailCompradorListener {

    private final EmailNotificacionService emailService;

    public EmailCompradorListener(EmailNotificacionService emailService) {
        this.emailService = emailService;
    }

    @EventListener
    public void onPedidoCreado(PedidoCreadoEvent event) {
        emailService.enviarEmail(
            event.compradorEmail(),
            "Tu pedido #" + event.pedidoId() + " fue creado",
            "Hola " + event.compradorNombre() + ", tu pedido está en camino."
        );
    }
}

@Component
public class SmsCompradorListener {

    private final SmsNotificacionService smsService;

    public SmsCompradorListener(SmsNotificacionService smsService) {
        this.smsService = smsService;
    }

    @EventListener
    public void onPedidoCreado(PedidoCreadoEvent event) {
        smsService.enviarSms(
            event.compradorTelefono(),
            "Pedido #" + event.pedidoId() + " creado exitosamente"
        );
    }
}

@Component
public class EmailVendedorListener {

    private final EmailNotificacionService emailService;

    public EmailVendedorListener(EmailNotificacionService emailService) {
        this.emailService = emailService;
    }

    @EventListener
    public void onPedidoCreado(PedidoCreadoEvent event) {
        emailService.enviarEmail(
            event.vendedorEmail(),
            "Nuevo pedido de tu libro",
            "Tienes un nuevo pedido #" + event.pedidoId()
        );
    }
}

@Component
public class AnalyticsListener {

    private final AnalyticsService analyticsService;

    public AnalyticsListener(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @EventListener
    public void onPedidoCreado(PedidoCreadoEvent event) {
        analyticsService.registrarEvento("order_created", event.pedidoId());
    }
}
```

### PedidoService que publica el evento

```java
@Service
public class PedidoService {

    private final ApplicationEventPublisher eventPublisher;
    private final PedidoRepository pedidoRepository;

    public PedidoService(ApplicationEventPublisher eventPublisher, PedidoRepository pedidoRepository) {
        this.eventPublisher = eventPublisher;
        this.pedidoRepository = pedidoRepository;
    }

    @Transactional
    public Pedido crearPedido(CrearPedidoRequest request) {
        Pedido pedido = pedidoRepository.save(new Pedido(request));

        // Publicar evento — los listeners se ejecutan en el mismo hilo por defecto
        eventPublisher.publishEvent(new PedidoCreadoEvent(
            pedido.getId(),
            pedido.getComprador().getEmail(),
            pedido.getComprador().getNombre(),
            pedido.getComprador().getTelefono(),
            pedido.getVendedor().getEmail()
        ));

        return pedido;
    }
}
```

## Tests

```java
@ExtendWith(MockitoExtension.class)
class EmailCompradorListenerTest {

    @Mock
    private EmailNotificacionService emailService;

    @InjectMocks
    private EmailCompradorListener listener;

    @Test
    void onPedidoCreado_enviaEmailAlComprador() {
        PedidoCreadoEvent event = new PedidoCreadoEvent(
            "pedido-123",
            "comprador@test.com",
            "Juan",
            "+573001234567",
            "vendedor@test.com"
        );

        listener.onPedidoCreado(event);

        verify(emailService).enviarEmail(
            eq("comprador@test.com"),
            contains("pedido-123"),
            contains("Juan")
        );
    }
}

@ExtendWith(MockitoExtension.class)
class SmsCompradorListenerTest {

    @Mock
    private SmsNotificacionService smsService;

    @InjectMocks
    private SmsCompradorListener listener;

    @Test
    void onPedidoCreado_enviaSmsAlComprador() {
        PedidoCreadoEvent event = new PedidoCreadoEvent(
            "pedido-456", "email@test.com", "Ana", "+573009876543", "vendedor@test.com"
        );

        listener.onPedidoCreado(event);

        verify(smsService).enviarSms(eq("+573009876543"), contains("pedido-456"));
    }
}
```

---

### Analisis crítico de la respuesta

#### 1. ¿Que hizo bien el prompt?

El prompt fue efectivo al pedir **"no solo SOLID — incluye problemas de integración, seguridad, testabilidad"**. Sin esta instrucción, el LLM habría se enfocado solo en SRP/DIP y habría pasado por alto el problema de seguridad más crítico: las credenciales hardcodeadas en el código fuente. También fue clave especificar **"usa @EventListener de Spring, no Kafka/RabbitMQ"**, lo que resultó en una solución con las herramientas del propio framework en lugar de añadir complejidad de infraestructura para lo que es un caso sencillo. Pedir un **test con Mockito** en la misma pregunta demostró inmediatamente el beneficio del refactoring: ahora cada listener es testeable de forma completamente aislada.

#### 2. ¿Qué se puede mejorar?

El LLM usó `@EventListener` síncrono, lo que significa que si el servidor SMTP se cuelga, el thread de `crearPedido` queda bloqueado esperando. En producción, las notificaciones deberían ser **asíncronas** usando `@Async` o, idealmente, `@TransactionalEventListener` con `phase = AFTER_COMMIT` para garantizar que el evento solo se publica si la transacción de base de datos fue exitosa. El LLM no mencionó este último punto: si `@EventListener` se usa sin `@TransactionalEventListener`, el email se envía incluso si la transacción falla y el pedido hace rollback — lo que resultaría en emails de confirmación de pedidos que nunca existieron.

Otro punto ausente: el LLM no mostró cómo manejar **fallos parciales** de forma robusta. Si el email al comprador falla, ¿el SMS también se cancela? Con `@EventListener`, cada listener es independiente por defecto, pero si uno lanza una excepción no capturada puede afectar los demás. Un patrón más robusto sería cada listener con su propio try-catch + logging.

#### 3. Respuesta final

El `NotificadorPedido` original tiene tres problemas fundamentales que van más allá de las violaciones SOLID:

El **problema de seguridad más grave** es el token hardcodeado `"Bearer hardcoded_sms_token_12345"`. Una vez que este código llega a Git, el token queda en el historial para siempre, accesible a cualquier persona con acceso al repositorio. La solución es externalizar a `application.properties` y usar variables de entorno en producción.

El **problema de integración** es el acoplamiento directo a 4 sistemas externos en un solo método. Si el SMTP está caído, el SMS no se intenta. El refactoring con `ApplicationEventPublisher` desacopla el dominio del `PedidoService` de los canales de notificación: el servicio solo publica un evento `PedidoCreadoEvent`, y cada listener es responsable de su canal independientemente.

El **problema de testabilidad** hace que sea imposible escribir un test unitario sin conectarse a servidores reales. Con el refactoring, cada `EmailCompradorListener` se puede testear con un mock de `EmailNotificacionService` — 0 conexiones de red, ejecución en milisegundos.

El punto crítico que el LLM omitió: usar `@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)` en lugar de `@EventListener` simple. Esto garantiza que las notificaciones solo se envían si la transacción de base de datos fue exitosa. De lo contrario, se pueden enviar emails de confirmación de pedidos que nunca llegaron a guardarse en la base de datos — un bug silencioso y difícil de debuggear.
