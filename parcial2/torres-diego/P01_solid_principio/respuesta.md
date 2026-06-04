# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [01]: [P01_solid_srp]

### Estudiante
- **Nombre completo**: Diego Alejandro Torres Barragan 

---
Respuesta sin ayuda de la IA


### Análisis crítico de la respuesta

#### 1. Respuesta final

[Para esta pregunta el principal principio SOLID que se esta violando es la S, que es el principio de las responsabilidad unica ,  ya que la clase gestor libro esta llamando la funcion publicar libro , pero dentro de la funcion esta haciendo otras responsabilidades(Validar el libro, Guardar el libro en la base de datos, Generar el Slug de la url, Enviar el correo al vendedor, Registrar en el Log y indexar para busqueda), lo que implica que si se requiere hacer un cambio en cualquiera de esas responsabilidades afecta directamente la clase de gestor libro, lo cual hace que viole directamente este principio.

por la violacion de este principio hace que tambien se viole la O (open/close), ya que si en un futuro quiero enviar un mensaje por mensaje de texto , ademas del correo , tendria que modificar la clase directamente en lugar de extenderla , que seria lo ideal. 

lo ideal seria separar las responsabilidades que hace esa funcioalidad para que no tuviera que aboradar toda la clase

el refactoring que yo propongo es hacer que cada responsabilidad que tiene esa funcion sea una clase por separado y ya que si se requiere usar , la llame la clase GestorLibro ejm 

LibroValidator -> se encarga únicamente de validar que el libro tenga ISBN, título y autor
LibroRepositorio —> maneja la persistencia en base de datos
SlugGenerator —> genera el slug a partir del título
NotificadorVendedor —>envía el correo al vendedor
IndiceLibros —>indexa el libro en el motor de búsqueda]

// Valida que el libro tenga los datos obligatorios
public class ValidadorLibro {
    public void validar(Libro libro) {
        if (libro.getIsbn() == null || libro.getTitulo() == null || libro.getAutor() == null) {
            throw new IllegalArgumentException("Datos del libro incompletos");
        }
    }
}

// Genera el slug a partir del título
public class GeneradorSlug {
    public String generar(String titulo) {
        return titulo.toLowerCase()
                     .replace(" ", "-")
                     .replaceAll("[^a-z0-9-]", "");
    }
}

// Interfaz para no depender de una implementación concreta de base de datos
public interface RepositorioLibro {
    void guardar(Libro libro);
}

// Interfaz para no depender de un proveedor de correo específico
public interface NotificadorVendedor {
    void notificar(Usuario vendedor, Libro libro);
}

// Interfaz para no atarse a Elasticsearch u otro motor
public interface IndiceLibros {
    void indexar(Libro libro);
}

// GestorLibro ahora solo coordina, no implementa nada
public class GestorLibro {

    private final ValidadorLibro validador;
    private final GeneradorSlug generadorSlug;
    private final RepositorioLibro repositorio;
    private final NotificadorVendedor notificador;
    private final IndiceLibros indice;

    public GestorLibro(ValidadorLibro validador, GeneradorSlug generadorSlug,
                       RepositorioLibro repositorio, NotificadorVendedor notificador,
                       IndiceLibros indice) {
        this.validador = validador;
        this.generadorSlug = generadorSlug;
        this.repositorio = repositorio;
        this.notificador = notificador;
        this.indice = indice;
    }

    public void publicarLibro(Libro libro, Usuario vendedor) {
        validador.validar(libro);
        libro.setSlug(generadorSlug.generar(libro.getTitulo()));
        repositorio.guardar(libro);
        notificador.notificar(vendedor, libro);
        indice.indexar(libro);
        Logger.getLogger("OpenLib").info("Libro publicado: " + libro.getIsbn());
    }
}
