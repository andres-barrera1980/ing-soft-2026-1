# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

# Pregunta P01: Principio SOLID violado en GestorLibro

### Estudiante
- **Nombre completo**: [Juan Camilo Gomez]

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Claude  |
| **Modelo específico** 
| **¿Por qué elegiste este LLM?** tenia certeza de la primera parte del principio violado, por lo cual lo hice yo, mas lo de refactoring si utilice LLM y use claude porqeu Claude tiene buen manejo de código Java y principios de ingeniería de software

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```
Soy estudiante de Ingeniería de Software en la Pontificia Universidad Javeriana. 
Estoy trabajando en el proyecto OpenLib Market, una plataforma de venta de libros digitales.

Analiza la siguiente clase Java del módulo de administración:

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

Tareas:
1. Propón un refactoring en Java 21 que corrija la violación. Usa interfaces y clases separadas donde sea necesario.
2. Muestra cómo quedaría la clase GestorLibro después del refactoring.

Restricciones:
- Usa Java 21
- Aplica principios SOLID correctamente
- El código debe ser funcional y bien estructurado

```

---

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**
### Refactoring propuesto

#### 1. Interfaz para el repositorio de libros

```java
public interface RepositorioLibro {
    void guardar(Libro libro);
}
```

#### 2. Implementación con PostgreSQL

```java
public class RepositorioLibroPostgres implements RepositorioLibro {
    @Override
    public void guardar(Libro libro) {
        try {
            Connection conn = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/openlib", "admin", "pass");
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO libros (...) VALUES (...)");
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar el libro", e);
        }
    }
}
```

#### 3. Servicio de validación

```java
public class ValidadorLibro {
    public void validar(Libro libro) {
        if (libro.getIsbn() == null || libro.getTitulo() == null || libro.getAutor() == null) {
            throw new IllegalArgumentException("Datos del libro incompletos");
        }
    }
}
```

#### 4. Servicio de generación de slug

```java
public class GeneradorSlug {
    public String generar(String titulo) {
        return titulo.toLowerCase()
                     .replace(" ", "-")
                     .replaceAll("[^a-z0-9-]", "");
    }
}
```

#### 5. Interfaz para notificaciones

```java
public interface ServicioNotificacion {
    void notificarPublicacion(Libro libro, Usuario vendedor);
}
```

#### 6. Implementación de notificación por correo

```java
public class NotificacionEmail implements ServicioNotificacion {
    private final String smtpHost;
    private final int smtpPort;

    public NotificacionEmail(String smtpHost, int smtpPort) {
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
    }

    @Override
    public void notificarPublicacion(Libro libro, Usuario vendedor) {
        EmailService email = new EmailService(smtpHost, smtpPort, "noreply@openlib.com", "pass123");
        email.enviar(vendedor.getEmail(), "Libro publicado",
            "Tu libro '" + libro.getTitulo() + "' ha sido publicado exitosamente.");
    }
}
```

#### 7. Interfaz para indexación

```java
public interface IndicesBusqueda {
    void indexar(Libro libro);
}
```

#### 8. Implementación con Elasticsearch

```java
public class IndiceElasticsearch implements IndicesBusqueda {
    private final String url;

    public IndiceElasticsearch(String url) {
        this.url = url;
    }

    @Override
    public void indexar(Libro libro) {
        SearchIndex index = new SearchIndex(url);
        index.indexar(libro);
    }
}
```

#### 9. GestorLibro refactorizado

```java
public class GestorLibro {
    private final ValidadorLibro validador;
    private final RepositorioLibro repositorio;
    private final GeneradorSlug generadorSlug;
    private final ServicioNotificacion notificacion;
    private final IndicesBusqueda indice;
    private final Logger logger = Logger.getLogger("OpenLib");

    public GestorLibro(
            ValidadorLibro validador,
            RepositorioLibro repositorio,
            GeneradorSlug generadorSlug,
            ServicioNotificacion notificacion,
            IndicesBusqueda indice) {
        this.validador = validador;
        this.repositorio = repositorio;
        this.generadorSlug = generadorSlug;
        this.notificacion = notificacion;
        this.indice = indice;
    }

    public void publicarLibro(Libro libro, Usuario vendedor) {
        validador.validar(libro);
        repositorio.guardar(libro);
        libro.setSlug(generadorSlug.generar(libro.getTitulo()));
        notificacion.notificarPublicacion(libro, vendedor);
        logger.info("Libro publicado: " + libro.getIsbn() + " por " + vendedor.getNombre());
        indice.indexar(libro);
    }
}
```

Con este diseño, cada clase tiene **una sola responsabilidad** y el `GestorLibro` solo **coordina** el flujo, sin implementar ningún detalle. Cada colaborador puede cambiarse de forma independiente.


---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

El prompt fue efectivo porque incluia el codigo completo sin resumenes, establecía el contexto del proyecto, pedia analisis del principio violado con argumentos concretos sobre el código específico, y definía restricciones claras. 


#### 2. ¿Qué se puede mejorar?

Podria haberse pedido que incluyera pruebas unitaria para demostrar que el refactoring es testeable. Tambien de pronto omitio el manejo de la conexion a base de datos.

#### 3. Respuesta final
parte sin LLM
En la clase gestionar libro, se viola el principio de responsabilidad unica, ya que una clase siempre tiene una unica responsabilidad, en este ejemplo en especifico hay distintas razones, por ejemplo la de validacion o notificacion, esto hace que un cambio en cualquiera de esas areas obliga a modificar gestorlibro, aumentando el riesgo de bugs.

parte con LLM

El refactoring correcto es:
- Extraer cada responsabilidad a su propia clase con una interfaz que la abstraiga.
- Inyectar las dependencias a través del constructor (Constructor Injection).
- `GestorLibro` pasa a ser un **coordinador** que solo orquesta el flujo, sin conocer los detalles de implementación.

Un aspecto que el LLM no cubrió y es crítico: el **manejo de transaccionalidad**. Si `notificacion.notificarPublicacion()` falla después de que el libro ya se guardó en base de datos, el sistema queda en estado inconsistente. En producción se debería usar un patrón de **compensación** o manejar la transacción con `@Transactional` y notificación asíncrona (por ejemplo, via cola de mensajes).


