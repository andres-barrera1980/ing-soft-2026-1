# Plantilla de entrega — Parcial 2

---

## Pregunta P01: SOLID — Principio violado en GestorLibro

### Estudiante
- **Nombre completo**: Ivan Wright

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Sin IA — respuesta propia |
| **Modelo especifico** | N/A |
| **¿Por que elegiste este LLM?** | Decidi no utilizar IA en esta pregunta para evaluar directamente mis conocimientos sobre principios SOLID y ganar el bono del +20% por responder de manera autonoma. |

---

### Analisis critico de la respuesta

#### 1. ¿Que hizo bien el prompt?
N/A (Respuesta elaborada sin asistencia de IA).

#### 2. ¿Que se puede mejorar?
N/A (Respuesta elaborada sin asistencia de IA).

#### 3. Respuesta final

Primeramente en `public void publicarLibro(Libro libro, Usuario vendedor)` se viola el Single Responsibility Principle (SRP), ya que GestorLibro se esta encargando de validar datos, lo que deberia ser hecho por el propio modelo de dominio (la clase Libro) o un validador dedicado.

En `Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost...", "admin", "pass");` se viola el Dependency Inversion Principle (DIP) ya que depende directamente de implementar concretamente algo en PostgreSQL en lugar de una abstraccion. Seguidamente, todo el bloque de conexion y ejecucion SQL viola el Single Responsibility Principle (SRP), ya que la clase asume responsabilidades de infraestructura (gestion de base de datos) mezcladas con la logica de negocio, lo cual deberia delegarse a un repositorio.

En `String slug = libro.getTitulo().toLowerCase().replace(" ", "-").replaceAll("[^a-z0-9-]", "");` se viola el Single Responsibility Principle (SRP), ya que esta clase se esta encargando de procesar reglas de negocio al generar el slug del libro, este trabajo deberia ser hecho por la clase Libro directamente.

En `EmailService email = new EmailService("smtp.openlib.com", 587, "noreply@openlib.com", "pass123");` se viola el Dependency Inversion Principle (DIP) al instanciar directamente una clase concreta en lugar de depender de una interfaz. Ademas tambien viola el Open/Closed Principle (OCP), en este caso la razon es que todos los cambios a la tecnologia o un parametro, obligaran a modificar esta clase por completo en vez de crear una nueva implementacion.

En `Logger.getLogger("OpenLib").info("Libro publicado: " + libro.getIsbn() + " por " + vendedor.getNombre());` se viola el Single Responsibility Principle (SRP). Esto es porque igualmente mezcla logica de negocio con infraestructura, siendo estos auditoria y logging, y esto deberia ser hecho por una clase o componente dedicado como un decorador.

En `SearchIndex index = new SearchIndex("elasticsearch.openlib.com:9200");` se viola el Dependency Inversion Principle (DIP) ya que no esta dependiendo de una abstraccion sino que esta dependiendo de una clase acoplada la cual es Elasticsearch. Lo que tambien de paso viola el Open/Closed Principle (OCP), porque cambiar de motor de busqueda o actualizar algo como puede ser la URL obliga a modificar el codigo de esa clase.

---

### Propuesta de Codigo Refactorizado (Java 21)

#### 1. Abstracciones (Interfaces de Infraestructura)

```java
public interface LibroRepository {
    void guardar(Libro libro);
}

public interface NotificacionService {
    void notificarPublicacion(Usuario usuario, Libro libro);
}

public interface SearchIndexer {
    void indexar(Libro libro);
}
```

#### 2. Entidad de Dominio (`Libro`)

```java
public class Libro {
    private String isbn;
    private String titulo;
    private String autor;
    private String slug;

    // Getters, Setters y Constructor

    public void validar() {
        if (isbn == null || titulo == null || autor == null) {
            throw new IllegalArgumentException("Datos del libro incompletos");
        }
    }

    public void generarSlug() {
        if (this.titulo != null) {
            this.slug = this.titulo.toLowerCase()
                                   .replace(" ", "-")
                                   .replaceAll("[^a-z0-9-]", "");
        }
    }

    public String getIsbn() { return isbn; }
    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
}
```

#### 3. Servicio de Aplicacion Refactorizado (`GestorLibro`)

Aplicando inyeccion de dependencias a traves del constructor, desacoplamos la logica de negocio de la infraestructura y cumplimos con los principios SOLID:

```java
import java.util.logging.Logger;

public class GestorLibro {
    private final LibroRepository repository;
    private final NotificacionService notificacionService;
    private final SearchIndexer searchIndexer;
    private static final Logger LOGGER = Logger.getLogger(GestorLibro.class.getName());

    // Inyeccion de dependencias por constructor
    public GestorLibro(LibroRepository repository, 
                        NotificacionService notificacionService, 
                        SearchIndexer searchIndexer) {
        this.repository = repository;
        this.notificacionService = notificacionService;
        this.searchIndexer = searchIndexer;
    }

    public void publicarLibro(Libro libro, Usuario vendedor) {
        // 1. Validacion de datos del dominio
        libro.validar();
        
        // 2. Logica de dominio (Slug)
        libro.generarSlug();
        
        // 3. Persistencia (Desacoplado de la tecnologia PostgreSQL)
        repository.guardar(libro);
        
        // 4. Notificacion (Desacoplado de la tecnologia SMTP / Email)
        notificacionService.notificarPublicacion(vendedor, libro);
        
        // 5. Logging
        LOGGER.info("Libro publicado: " + libro.getIsbn() + " por " + vendedor.getNombre());
        
        // 6. Indexacion (Desacoplado de la tecnologia Elasticsearch)
        searchIndexer.indexar(libro);
    }
}
```