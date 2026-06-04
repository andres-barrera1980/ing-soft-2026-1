# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [1]: [solid principio]

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

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**
Eres un experto en principios SOLID y buenas prácticas de diseño de software orientado a objetos. Analiza el siguiente método de la clase `GestorLibro` en Java:

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

Tarea:
1. Identifica claramente qué principio SOLID se está violando principalmente (aunque puedan aplicarse varios, enfócate en el más crítico).
2. Explica en detalle por qué se viola ese principio, usando ejemplos concretos del código.
3. Propón una refactorización paso a paso que solucione el problema, separando responsabilidades en clases o servicios adecuados.
4. Justifica cómo tu propuesta respeta el principio SOLID identificado y mejora la mantenibilidad, escalabilidad y testabilidad del sistema.
5. Presenta tu respuesta en formato académico claro y estructurado, con títulos, listas y ejemplos de código refactorizado.

No omitas nada: tu respuesta debe ser exhaustiva, crítica y práctica, mostrando tanto el análisis como la solución.

---

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**
Análisis de Violación de Principios SOLID en GestorLibro
1. Principio SOLID principalmente violado

El principio más claramente violado es el SRP (Single Responsibility Principle - Principio de Responsabilidad Única).

Definición

Este principio establece que:

"Una clase debe tener una única razón para cambiar."

Es decir, una clase debe encargarse de una sola responsabilidad dentro del sistema.

2. ¿Por qué se viola el SRP?

La clase GestorLibro concentra múltiples responsabilidades completamente diferentes dentro de un único método.

public void publicarLibro(Libro libro, Usuario vendedor)

Analizando el método:

Acción	Responsabilidad
Validar datos del libro	Validación
Guardar en base de datos	Persistencia
Generar slug	Transformación de datos
Enviar correo	Notificaciones
Registrar log	Auditoría
Indexar búsqueda	Integración con motor de búsqueda

La clase tiene seis razones distintas para cambiar.

Ejemplo 1: Cambio en validaciones

Actualmente:

if (libro.getIsbn() == null ||
    libro.getTitulo() == null ||
    libro.getAutor() == null)

Si mañana OpenLib exige:

Año de publicación obligatorio.
Editorial obligatoria.
Validar formato ISBN.

Debemos modificar GestorLibro.

Ejemplo 2: Cambio de base de datos

Actualmente:

Connection conn = DriverManager.getConnection(...)

Si mañana se cambia:

PostgreSQL → MySQL
JDBC → Hibernate
Base local → AWS RDS

También debemos modificar GestorLibro.

Ejemplo 3: Cambio de correo

Actualmente:

EmailService email = new EmailService(...)

Si mañana se usa:

Gmail SMTP
SendGrid
Amazon SES

Debemos modificar nuevamente la misma clase.

Ejemplo 4: Cambio de motor de búsqueda

Actualmente:

SearchIndex index =
    new SearchIndex("elasticsearch.openlib.com:9200");

Si se migra a:

Solr
OpenSearch
Algolia

Otra vez cambia GestorLibro.

Conclusión

La clase tiene demasiadas responsabilidades y demasiadas razones para cambiar.

Por lo tanto:

✅ Se viola claramente el Principio de Responsabilidad Única (SRP).

3. Refactorización Propuesta
Paso 1: Crear un servicio de validación
Antes
if (libro.getIsbn() == null ...)
Después
public class LibroValidator {

    public void validar(Libro libro) {
        if (libro.getIsbn() == null ||
            libro.getTitulo() == null ||
            libro.getAutor() == null) {

            throw new IllegalArgumentException(
                "Datos del libro incompletos");
        }
    }
}
Paso 2: Crear repositorio de persistencia
public class LibroRepository {

    public void guardar(Libro libro) {

        Connection conn =
            DriverManager.getConnection(...);

        PreparedStatement stmt =
            conn.prepareStatement(
                "INSERT INTO libros (...) VALUES (...)");

        stmt.executeUpdate();
    }
}

Responsabilidad:

Acceso a datos.
Paso 3: Crear generador de Slug
public class SlugGenerator {

    public String generar(String titulo) {

        return titulo.toLowerCase()
                     .replace(" ", "-")
                     .replaceAll("[^a-z0-9-]", "");
    }
}

Responsabilidad:

Construcción de URLs amigables.
Paso 4: Servicio de notificaciones
public class NotificationService {

    public void enviarLibroPublicado(
            Usuario vendedor,
            Libro libro) {

        EmailService email =
            new EmailService(...);

        email.enviar(
            vendedor.getEmail(),
            "Libro publicado",
            "Tu libro '" +
            libro.getTitulo() +
            "' ha sido publicado exitosamente."
        );
    }
}

Responsabilidad:

Comunicación con usuarios.
Paso 5: Servicio de logging
public class AuditService {

    public void registrarPublicacion(
            Libro libro,
            Usuario vendedor) {

        Logger.getLogger("OpenLib")
              .info(
                "Libro publicado: "
                + libro.getIsbn()
                + " por "
                + vendedor.getNombre()
              );
    }
}

Responsabilidad:

Auditoría y trazabilidad.
Paso 6: Servicio de indexación
public class SearchIndexer {

    public void indexar(Libro libro) {

        SearchIndex index =
            new SearchIndex(
                "elasticsearch.openlib.com:9200"
            );

        index.indexar(libro);
    }
}

Responsabilidad:

Integración con buscador.
Paso 7: Simplificar GestorLibro

Ahora la clase solo coordina el proceso.

public class GestorLibro {

    private LibroValidator validator;
    private LibroRepository repository;
    private SlugGenerator slugGenerator;
    private NotificationService notificationService;
    private AuditService auditService;
    private SearchIndexer searchIndexer;

    public GestorLibro(
            LibroValidator validator,
            LibroRepository repository,
            SlugGenerator slugGenerator,
            NotificationService notificationService,
            AuditService auditService,
            SearchIndexer searchIndexer) {

        this.validator = validator;
        this.repository = repository;
        this.slugGenerator = slugGenerator;
        this.notificationService = notificationService;
        this.auditService = auditService;
        this.searchIndexer = searchIndexer;
    }

    public void publicarLibro(
            Libro libro,
            Usuario vendedor) {

        validator.validar(libro);

        libro.setSlug(
            slugGenerator.generar(
                libro.getTitulo()
            )
        );

        repository.guardar(libro);

        notificationService
            .enviarLibroPublicado(
                vendedor,
                libro
            );

        auditService
            .registrarPublicacion(
                libro,
                vendedor
            );

        searchIndexer.indexar(libro);
    }
}
4. Justificación de la Solución
Cumplimiento del SRP

Ahora cada clase tiene una única responsabilidad:

Clase	Responsabilidad
LibroValidator	Validar libros
LibroRepository	Persistencia
SlugGenerator	Generar slug
NotificationService	Enviar notificaciones
AuditService	Registrar logs
SearchIndexer	Indexar búsqueda
GestorLibro	Orquestar el flujo

Cada una posee una sola razón para cambiar.

Mejora de mantenibilidad

Antes:

GestorLibro

contenía toda la lógica.

Después:

GestorLibro

solo coordina servicios especializados.

Un cambio en correo ya no afecta persistencia ni validación.

Mejora de escalabilidad

Si mañana OpenLib agrega:

SMS
WhatsApp
Push Notifications

Solo se modifica:

NotificationService

El resto permanece intacto.

Mejora de testabilidad

Antes era difícil probar el método porque:

Accedía a BD.
Enviaba correos.
Escribía logs.
Conectaba Elasticsearch.

Todo en una sola prueba.

Ahora cada componente puede probarse individualmente.

Ejemplo:

@Test
void debeGenerarSlugCorrectamente() {

    SlugGenerator generator =
        new SlugGenerator();

    String slug =
        generator.generar(
            "Java Avanzado"
        );

    assertEquals(
        "java-avanzado",
        slug
    );
}

Las pruebas son más simples, rápidas y aisladas.

5. Conclusión

La violación más importante del código es el Principio de Responsabilidad Única (SRP), ya que la clase GestorLibro realiza validación, persistencia, transformación de datos, notificaciones, auditoría e indexación dentro de un mismo método. La refactorización propuesta divide estas responsabilidades en servicios especializados, logrando una arquitectura más limpia, modular y fácil de mantener. Como resultado, el sistema mejora significativamente en mantenibilidad, escalabilidad, reutilización y testabilidad, alineándose con las buenas prácticas de diseño orientado a objetos y los principios SOLID.

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

Lo que hizo bien el prompt fue que definió claramente el rol de la IA como experta en SOLID, estructuró la tarea en pasos concretos para que no se omitiera nada y exigió una respuesta exhaustiva con ejemplos, código y justificación académica. Esa combinación de contexto, instrucciones precisas y formato esperado garantizó que la IA produjera un análisis completo, organizado y sin lagunas.

#### 2. ¿Qué se puede mejorar?

Lo que le faltó a tu prompt fue pedir explícitamente que el análisis no se limitara solo al SRP, sino que también evaluara si se rompían otros principios SOLID como OCP y DIP. Si lo reformularas, incluirías esa instrucción para que la IA ampliara el diagnóstico. El LLM no entendió mal, pero por falta de claridad se enfocó únicamente en SRP. La respuesta no tiene errores, pero sí omisiones: no cubrió esos otros principios ni conectó la refactorización con patrones de diseño, que es algo que en clase se suele remarcar para mostrar cómo SOLID se aplica en la práctica con soluciones arquitectónicas más completas.


#### 3. Respuesta final

La clase GestorLibro rompe sobre todo el SRP porque mezcla validación, guardado en base de datos, generación de slug, envío de correos, logging e indexación en un solo método. También incumple el OCP porque cada nueva regla obliga a modificar la clase y el DIP porque depende directamente de implementaciones concretas. La solución es separar esas tareas en servicios distintos y dejar que GestorLibro solo coordine, aplicando patrones como Repository, Strategy u Observer. Así se logra un código más limpio, fácil de mantener y probar, alineado con los principios SOLID.