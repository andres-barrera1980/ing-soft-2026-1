# Pregunta P01: SOLID — Principio violado en GestorLibro

### Estudiante
- **Nombre completo**: Mateo Traslaviña Moreno

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Claude |
| **Modelo específico** | Claude Sonnet 4.6 |
| **¿Por qué elegiste este LLM?** | Claude tiene excelente razonamiento sobre código Java y principios de diseño orientado a objetos. Su capacidad para identificar violaciones SOLID y proponer refactorings estructurados lo hace ideal para esta pregunta. Además, genera explicaciones claras con justificaciones técnicas precisas. |

---

### Prompt utilizado

```
Eres un ingeniero de software senior revisando código del proyecto OpenLib Market, una plataforma de compra-venta de libros desarrollada en Java 21 con Spring Boot 3.x y PostgreSQL.

Analiza la siguiente clase del módulo de administración:

```java
public class GestorLibro {
    public void publicarLibro(Libro libro, Usuario vendedor) {
        // 1. Validar que el libro tenga ISBN, título y autor
        if (libro.getIsbn() == null || libro.getTitulo() == null || libro.getAutor() == null) {
            throw new IllegalArgumentException("Datos del libro incompletos");
        }
        
        // 2. Guardar el libro en la base de datos
        Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/openlib", "admin", "pass");
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO libros (...) VALUES (...)");
        stmt.executeUpdate();
        
        // 3. Generar slug para la URL
        String slug = libro.getTitulo().toLowerCase().replace(" ", "-").replaceAll("[^a-z0-9-]", "");
        libro.setSlug(slug);
        
        // 4. Enviar correo al vendedor
        EmailService email = new EmailService("smtp.openlib.com", 587, "noreply@openlib.com", "pass123");
        email.enviar(vendedor.getEmail(), "Libro publicado", "Tu libro '" + libro.getTitulo() + "' ha sido publicado exitosamente.");
        
        // 5. Registrar en log
        Logger.getLogger("OpenLib").info("Libro publicado: " + libro.getIsbn() + " por " + vendedor.getNombre());
        
        // 6. Indexar para búsqueda
        SearchIndex index = new SearchIndex("elasticsearch.openlib.com:9200");
        index.indexar(libro);
    }
}
```

Necesito que:
1. Identifiques el principio SOLID principal que se está violando (y si se estan violando mas que me los incluyas Tambien) y expliques POR QUÉ con argumentos técnicos concretos referenciando el código.
2. Propongas un refactoring paso a paso aplicando los principios correctos, separando responsabilidades.
4. Generes el código Java refactorizado 

Necesito que el formato de salida sea markdown estructurado con secciones "Diagnóstico", "Principios violados", "Refactoring propuesto", "Código final".
```

---

### Respuesta del LLM

## Diagnóstico

La clase `GestorLibro` viola principalmente el **Principio de Responsabilidad Única (SRP — Single Responsibility Principle)**. Un método de 30 líneas está haciendo seis cosas completamente distintas:

1. **Validación de datos** del libro
2. **Persistencia** en base de datos (con JDBC crudo)
3. **Generación de slug** para URL
4. **Envío de correo** electrónico
5. **Logging** de auditoría
6. **Indexación** en Elasticsearch

El SRP establece que una clase debe tener una única razón para cambiar. `GestorLibro` tiene al menos 6 razones: si cambia el proveedor de email, si cambia el motor de búsqueda, si cambia el esquema de la DB, si cambia la lógica del slug, etc.

## Principios violados

**Principal: SRP (Single Responsibility Principle)**
La clase tiene múltiples responsabilidades acopladas en un solo método.

**Secundario: DIP (Dependency Inversion Principle)**
La clase depende directamente de implementaciones concretas (`EmailService`, `SearchIndex`, `DriverManager`) en lugar de abstracciones. Además, las credenciales están hardcodeadas, lo que viola el principio de configuración externalizada.

**Secundario: OCP (Open/Closed Principle)**
Si se necesita agregar un nuevo canal de notificación (por ejemplo, SMS), habría que modificar `GestorLibro` directamente.

## Refactoring propuesto

**Paso 1**: Extraer la lógica de persistencia a `LibroRepository` (interfaz + implementación JPA)  
**Paso 2**: Extraer la generación de slug a `SlugService`  
**Paso 3**: Extraer el envío de correo a una interfaz `NotificacionService`  
**Paso 4**: El logging se maneja con un aspecto AOP o dentro de cada servicio  
**Paso 5**: Extraer la indexación a `BuscadorService` (interfaz)  
**Paso 6**: `GestorLibro` solo coordina, inyectando los servicios por constructor

## Código final

```java
// Interfaz de repositorio
public interface LibroRepository {
    void guardar(Libro libro);
}

// Implementación JPA
@Repository
public class LibroRepositoryJpa implements LibroRepository {
    @PersistenceContext
    private EntityManager em;
    
    @Override
    public void guardar(Libro libro) {
        em.persist(libro);
    }
}

// Servicio de slug
@Service
public class SlugService {
    public String generarSlug(String titulo) {
        return titulo.toLowerCase()
            .replace(" ", "-")
            .replaceAll("[^a-z0-9-]", "");
    }
}

// Interfaz de notificación
public interface NotificacionService {
    void notificarPublicacion(Usuario vendedor, Libro libro);
}

// Implementación email
@Service
public class EmailNotificacionService implements NotificacionService {
    @Value("${mail.smtp.host}") private String smtpHost;
    @Value("${mail.smtp.port}") private int smtpPort;
    
    @Override
    public void notificarPublicacion(Usuario vendedor, Libro libro) {
        // Conectar usando configuración externalizada
        // ... lógica de envío de email
    }
}

// Interfaz de indexación
public interface BuscadorService {
    void indexar(Libro libro);
}

// Implementación Elasticsearch
@Service
public class ElasticsearchBuscadorService implements BuscadorService {
    @Value("${elasticsearch.url}") private String esUrl;
    
    @Override
    public void indexar(Libro libro) {
        // Lógica de indexación
    }
}

// Servicio de validación
@Service
public class LibroValidadorService {
    public void validar(Libro libro) {
        if (libro.getIsbn() == null || libro.getTitulo() == null || libro.getAutor() == null) {
            throw new IllegalArgumentException("Datos del libro incompletos");
        }
    }
}

// GestorLibro refactorizado — solo coordina
@Service
public class GestorLibro {
    private static final Logger log = LoggerFactory.getLogger(GestorLibro.class);
    
    private final LibroRepository libroRepository;
    private final SlugService slugService;
    private final NotificacionService notificacionService;
    private final BuscadorService buscadorService;
    private final LibroValidadorService validadorService;
    
    public GestorLibro(
        LibroRepository libroRepository,
        SlugService slugService,
        NotificacionService notificacionService,
        BuscadorService buscadorService,
        LibroValidadorService validadorService
    ) {
        this.libroRepository = libroRepository;
        this.slugService = slugService;
        this.notificacionService = notificacionService;
        this.buscadorService = buscadorService;
        this.validadorService = validadorService;
    }
    
    public void publicarLibro(Libro libro, Usuario vendedor) {
        validadorService.validar(libro);
        libro.setSlug(slugService.generarSlug(libro.getTitulo()));
        libroRepository.guardar(libro);
        notificacionService.notificarPublicacion(vendedor, libro);
        buscadorService.indexar(libro);
        log.info("Libro publicado: {} por {}", libro.getIsbn(), vendedor.getNombre());
    }
}
```

---

### Analisis crítico de la respuesta

#### 1. ¿Que hizo bien el prompt?

El prompt fue efectivo en distintos aspectos. Primero, **el contexto del proyecto** fue suficientemente especifico: mencionar Java 21, Spring Boot 3.x y PostgreSQL orientó al LLM hacia respuestas con esas tecnologías. Segundo, pedir el **formato de salida estructurado** con secciones nombradas facilitó una respuesta organizada y comparable con la rúbrica.

La parte de "justificación tecnica referenciando el codigo" fue útil: el LLM explico *por que* cada fragmento de código viola el SRP en lugar de dar una respuesta genérica.

#### 2. ¿Qué se puede mejorar?

El prompt podria haber pedido explicitamente **pruebas unitarias** para el `GestorLibro` refactorizado, lo que habría demostrado mejor la testabilidad del diseño resultante. Tambien falto pedir que el LLM considerara **transaccionalidad**: en el refactoring, si el email falla después de guardar en DB, ¿la transacción se revierte? El LLM no mencionó `@Transactional` ni el manejo de fallos parciales.

Otro punto débil: el LLM creó un `LibroValidadorService` separado, lo cual es cuestionable — la validación básica podría vivir dentro del mismo `GestorLibro` o en la entidad `Libro` misma (un enfoque de dominio rico). El LLM optó por la solución más "mecánica" de separar todo.

#### 3. Respuesta final

**Principio violado principal: SRP (Single Responsibility Principle)**

`GestorLibro.publicarLibro()` viola el SRP porque tiene *seis razones para cambiar*: cambio de proveedor de email, cambio de motor de búsqueda, cambio de esquema de BD, cambio en formato del slug, cambio en política de logging, y cambio en reglas de validación. El SRP dice que una clase debe tener una sola razón para cambiar.

**Secundariamente viola DIP**, porque depende directamente de `EmailService` (clase concreta), `SearchIndex` (clase concreta) y `DriverManager` (API de bajo nivel) en lugar de abstracciones. Esto hace imposible testear `GestorLibro` sin una BD real, un servidor SMTP real y Elasticsearch real.
