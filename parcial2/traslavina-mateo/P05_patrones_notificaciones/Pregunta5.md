# Pregunta P05: Patrones — Notificaciones combinadas

### Estudiante
- **Nombre completo**: Mateo Traslaviña Moreno

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Claude |
| **Modelo especifico** | Claude Sonnet 4.6 |
| **¿Por que elegiste este LLM?** | Esta pregunta requiere diseñar un sistema con dos patrones combinados e implementarlos en codigo. Claude es especialmente bueno generando diagramas Mermaid y codigo Java estructurado en una sola respuesta. |

---

### Prompt utilizado
```
Eres un arquitecto de software senior diseñando el sistema de notificaciones de OpenLib Market (Java 21, Spring Boot 3.x), plataforma de compra-venta de libros.

Escenario: cuando un libro agotado vuelve a estar disponible, múltiples componentes del sistema deben reaccionar:
1. Enviar email a usuarios que agregaron el libro a su wishlist.
2. Enviar notificación push a usuarios que marcaron el libro como favorito en la app móvil.
3. Actualizar la caché de Redis para que el libro aparezca en búsquedas.
4. Registrar el evento en un log de auditoría.

Requisitos funcionales adicionales:
- Los componentes interesados deben poder registrarse y desregistrarse dinámicamente en tiempo de ejecución.
- Si la notificación push falla, no debe interrumpir el envío de email.
- El sistema debe ser extensible: agregar "notificar por SMS" no debe tocar código existente.
- El libro puede volver a estar disponible múltiples veces (el sistema debe manejar eventos repetidos).

Diseña e implementa este sistema usando DOS patrones de diseño de los vistos en clase que trabajen en conjunto. Para cada patron:
- Explica por que lo elegiste
- Muestra cómo contribuye al diseño
- Muestra cómo interactúan entre sí

Entregables:
1. Diagrama de clases en Mermaid
2. Codigo Java 21 de las clases principales (interfaces, implementaciones, el libro como sujeto)
3. Ejemplo de uso mostrando el flujo completo cuando un libro vuelve a estar disponible
4. Como manejar el fallo de una notificación sin detener las demas


---

### Respuesta del LLM

## Patrones elegidos: Observer + Strategy

**Observer (patron principal)**: El libro (`Libro`) actúa como Subject. Cuando su disponibilidad cambia a "DISPONIBLE", notifica a todos los observers registrados. Esto permite desacoplamiento total: el libro no conoce quienes estan suscritos.

**Strategy (patron secundario)**: Cada canal de notificacion (email, push, cache, log) encapsula su propia estrategia de envio. Esto permite agregar nuevos canales sin modificar el Subject ni los demas observers.

## Diagrama Mermaid

```mermaid
classDiagram
    class DisponibilidadObserver {
        <<interface>>
        +onLibroDisponible(Libro libro)
        +getNombre() String
    }
    
    class GestorDisponibilidad {
        -observers: List~DisponibilidadObserver~
        +registrar(observer)
        +desregistrar(observer)
        +notificarDisponibilidad(libro)
    }
    
    class EmailWishlistObserver {
        -emailService: EmailService
        +onLibroDisponible(Libro)
    }
    
    class PushFavoritosObserver {
        -pushService: PushService
        +onLibroDisponible(Libro)
    }
    
    class CacheRedisObserver {
        -redisTemplate: RedisTemplate
        +onLibroDisponible(Libro)
    }
    
    class AuditoriaObserver {
        -log: Logger
        +onLibroDisponible(Libro)
    }
    
    GestorDisponibilidad --> DisponibilidadObserver : notifica
    EmailWishlistObserver ..|> DisponibilidadObserver
    PushFavoritosObserver ..|> DisponibilidadObserver
    CacheRedisObserver ..|> DisponibilidadObserver
    AuditoriaObserver ..|> DisponibilidadObserver

## Código Java

```java
// Interfaz Observer
public interface DisponibilidadObserver {
    void onLibroDisponible(Libro libro);
    String getNombre();
}

// Subject / Gestor de disponibilidad
@Service
public class GestorDisponibilidad {
    private static final Logger log = LoggerFactory.getLogger(GestorDisponibilidad.class);
    private final List<DisponibilidadObserver> observers = new CopyOnWriteArrayList<>();
    
    public void registrar(DisponibilidadObserver observer) {
        observers.add(observer);
        log.info("Observer registrado: {}", observer.getNombre());
    }
    
    public void desregistrar(DisponibilidadObserver observer) {
        observers.remove(observer);
    }
    
    public void notificarDisponibilidad(Libro libro) {
        log.info("Notificando disponibilidad de: {}", libro.getIsbn());
        for (DisponibilidadObserver observer : observers) {
            try {
                observer.onLibroDisponible(libro);
            } catch (Exception e) {
                // Aislamiento de fallos: un observer que falla no detiene a los demás
                log.error("Error en observer {}: {}", observer.getNombre(), e.getMessage());
            }
        }
    }
}

// Observer: email a wishlist
@Service
public class EmailWishlistObserver implements DisponibilidadObserver {
    private final WishlistRepository wishlistRepo;
    private final NotificacionEmailService emailService;
    
    public EmailWishlistObserver(WishlistRepository wishlistRepo, NotificacionEmailService emailService) {
        this.wishlistRepo = wishlistRepo;
        this.emailService = emailService;
    }
    
    @Override
    public void onLibroDisponible(Libro libro) {
        List<String> emails = wishlistRepo.findEmailsByLibroId(libro.getId());
        emails.forEach(email -> emailService.enviar(email, libro));
    }
    
    @Override
    public String getNombre() { return "EMAIL_WISHLIST"; }
}

// Observer: push a favoritos
@Service
public class PushFavoritosObserver implements DisponibilidadObserver {
    private final FavoritosRepository favoritosRepo;
    private final PushNotificationService pushService;
    
    public PushFavoritosObserver(FavoritosRepository favoritosRepo, PushNotificationService pushService) {
        this.favoritosRepo = favoritosRepo;
        this.pushService = pushService;
    }
    
    @Override
    public void onLibroDisponible(Libro libro) {
        List<String> tokens = favoritosRepo.findDeviceTokensByLibroId(libro.getId());
        tokens.forEach(token -> pushService.enviar(token, libro.getTitulo(), "¡Ya disponible!"));
    }
    
    @Override
    public String getNombre() { return "PUSH_FAVORITOS"; }
}

// Observer: actualizar caché Redis
@Service
public class CacheRedisObserver implements DisponibilidadObserver {
    private final RedisTemplate<String, Libro> redisTemplate;
    
    public CacheRedisObserver(RedisTemplate<String, Libro> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    
    @Override
    public void onLibroDisponible(Libro libro) {
        redisTemplate.opsForValue().set("libro:" + libro.getId(), libro, Duration.ofHours(24));
    }
    
    @Override
    public String getNombre() { return "CACHE_REDIS"; }
}

// Observer: auditoría
@Service
public class AuditoriaObserver implements DisponibilidadObserver {
    private static final Logger log = LoggerFactory.getLogger(AuditoriaObserver.class);
    
    @Override
    public void onLibroDisponible(Libro libro) {
        log.info("[AUDITORIA] Libro disponible: ISBN={}, titulo={}, timestamp={}",
            libro.getIsbn(), libro.getTitulo(), Instant.now());
    }
    
    @Override
    public String getNombre() { return "AUDITORIA_LOG"; }
}

// Registro automático de observers via Spring
@Configuration
public class NotificacionConfig {
    @Bean
    public CommandLineRunner registrarObservers(
        GestorDisponibilidad gestor,
        List<DisponibilidadObserver> observers
    ) {
        return args -> observers.forEach(gestor::registrar);
    }
}
```

## Flujo completo de ejemplo

```java
// Cuando el vendedor repone stock:
@Service
public class InventarioService {
    private final GestorDisponibilidad gestorDisponibilidad;
    private final LibroRepository libroRepository;
    
    public void reponerStock(Long libroId, int cantidad) {
        Libro libro = libroRepository.findById(libroId).orElseThrow();
        boolean eraAgotado = libro.getEstado() == EstadoLibro.AGOTADO;
        
        libro.setStock(libro.getStock() + cantidad);
        if (libro.getStock() > 0) {
            libro.setEstado(EstadoLibro.DISPONIBLE);
        }
        libroRepository.save(libro);
        
        // Solo notificar si el libro VOLVIÓ a estar disponible (no si ya estaba)
        if (eraAgotado && libro.getEstado() == EstadoLibro.DISPONIBLE) {
            gestorDisponibilidad.notificarDisponibilidad(libro);
        }
    }
}
```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

El prompt fue muy efectivo al especificar el **requisito de aislamiento de fallos** ("si la notificación push falla, no debe interrumpir el email"). Esto llevó al LLM a implementar el `try-catch` en el loop de observers, que es la solución correcta. Sin esta restricción explícita, el LLM hubiera omitido el manejo de errores.

La solicitud de **diagrama Mermaid + código + flujo completo** forzó una respuesta integral que permite evaluar el diseño de principio a fin.

#### 2. ¿Qué se puede mejorar?

El LLM no consideró **notificaciones asíncronas**. En producción, notificar a todos los usuarios de la wishlist de forma síncrona en el mismo hilo que repone el stock es un antipatrón — si hay 10,000 usuarios en la wishlist, el método `reponerStock()` tarda minutos. La solución real usa eventos de Spring (`@EventListener` + `@Async`) o un message broker como Kafka/RabbitMQ.

También omitió **deduplicación de eventos**: si `reponerStock()` se llama dos veces seguidas (por error o concurrencia), se enviarían dos emails al mismo usuario. Falta un mecanismo de idempotencia.

El `CopyOnWriteArrayList` es correcta para thread-safety, pero el LLM no explicó por qué eligió esa estructura.

#### 3. Respuesta final

**Los patrones Observer + Strategy (implícito en los observers)** son la elección correcta para este escenario. Observer desacopla el libro de los sistemas de notificación; cada observer encapsula su estrategia de notificación (email vs. push vs. caché).
