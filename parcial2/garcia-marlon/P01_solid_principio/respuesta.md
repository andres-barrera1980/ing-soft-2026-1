## Pregunta [01]: [Solid principio]

### Estudiante
* **Nombre completo**: [Marlon Garcia]

---


### Respuesta sin IA 

Viola los principios de single responsability e inversión de dependencias. La clase se llama GestorLibros, por lo que debería encargarse únicamente de gestionar libros. El problema es que tiene un método que publica libros y, dentro de ese mismo método, también valida información, se conecta a PostgreSQL, envía correos, entre otras tareas.

Idealmente, GestorLibros debería limitarse a gestionar libros y delegar esas responsabilidades a otras clases o servicios especializados. Además, la clase instancia directamente otras clases, lo cual aumenta el acoplamiento. En lugar de hacer todas las operaciones por sí misma, debería solicitar o delegar esas acciones a dependencias externas.


### Refactor del codigo 
```java
  public class GestorLibro {

    private final LibroValidator validator;
    private final LibroRepository repository;
    private final SlugGenerator slugGenerator;
    private final Notificador notificador;
    private final PublicacionLogger logger;
    private final IndexadorBusqueda indexador;

    public GestorLibro(
            LibroValidator validator,
            LibroRepository repository,
            SlugGenerator slugGenerator,
            Notificador notificador,
            PublicacionLogger logger,
            IndexadorBusqueda indexador
    ) {
        this.validator = validator;
        this.repository = repository;
        this.slugGenerator = slugGenerator;
        this.notificador = notificador;
        this.logger = logger;
        this.indexador = indexador;
    }

    public void publicarLibro(Libro libro, Usuario vendedor) {
        validator.validar(libro);

        String slug = slugGenerator.generar(libro.getTitulo());
        libro.setSlug(slug);

        repository.guardar(libro);

        notificador.notificarLibroPublicado(vendedor, libro);

        logger.registrarPublicacion(libro, vendedor);

        indexador.indexar(libro);
    }
}
```
