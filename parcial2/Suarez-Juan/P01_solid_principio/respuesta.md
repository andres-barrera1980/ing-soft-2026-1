# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [01]: [P01_solid_principio]

### Estudiante
- **Nombre completo**: [Juan Pablo Suarez Moreno]

---

### Análisis crítico de la respuesta

#### 1. Conclusion

El codigo presentado muestra una la clase llamada "GestorLibro", con una funcion de publicarLibro que tiene varios pasos con comentarios para hacerlo.

El problema que presenta la clase es que esta violando evidentemente el principio SOLID (Single Responsability), que dicta que una clase deberia tener solo un motivo para cambiar, esta clase no solo accede al apartado de persistencia en el punto 2, tambien se encarga de mandar notificaciones en el punto 4 nombrando los apartados evidentes. Ya solo con tener en cuenta estos 2 se puede saber que esta clase se cree una super clase.

#### 2. Refactorizacion
```
//Clase grande
class GestorLibro {
    private RepositorioLibro repositorio = new RepositorioLibro();
    private ServicioEmail servicioEmail = new ServicioEmail();
    private Indexador indexador = new Indexador();
    private GeneradorSlug generadorSlug = new GeneradorSlug();

    public void publicarLibro(Libro libro, Usuario vendedor) {
        if (libro.getIsbn() == null || libro.getTitulo() == null || libro.getAutor() == null) {
            throw new IllegalArgumentException("Datos del libro incompletos");
        }
        //Asignacion o reparticion de responsabilidades, llama a las clases
        generadorSlug.asignar(libro);
        repositorio.guardar(libro);
        servicioEmail.enviarCorreo(vendedor.getEmail(), libro.getTitulo());
        Logger.getLogger("OpenLib").info("Libro publicado: " + libro.getIsbn() + " por " + vendedor.getNombre());
        indexador.ejecutar(libro);
    }
}

class RepositorioLibro {
    private String url = "jdbc:postgresql://localhost:5432/openlib";
    private String user = "admin";
    private String pass = "pass";

    public void guardar(Libro libro) {
        try {
            Connection conn = DriverManager.getConnection(url, user, pass);
            String sql = "INSERT INTO libros VALUES ('" + libro.getIsbn() + "', '" + libro.getTitulo() + "', '" + libro.getAutor() + "', '" + libro.getSlug() + "')";
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

class ServicioEmail {
    private EmailService clienteConfigurado = new EmailService("smtp.openlib.com", 587, "noreply@openlib.com", "pass123");

    public void enviarCorreo(String email, String titulo) {
        clienteConfigurado.enviar(email, "Libro publicado", "Tu libro '" + titulo + "' ha sido publicado.");
    }
}

class Indexador {
    public void ejecutar(Libro libro) {
        SearchIndex index = new SearchIndex("elasticsearch.openlib.com:9200");
        index.indexar(libro);
    }
}

class GeneradorSlug {
    public void asignar(Libro libro) {
        String slug = libro.getTitulo().toLowerCase().replace(" ", "-").replaceAll("[^a-z0-9-]", "");
        libro.setSlug(slug);
    }
}