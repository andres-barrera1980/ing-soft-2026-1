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

