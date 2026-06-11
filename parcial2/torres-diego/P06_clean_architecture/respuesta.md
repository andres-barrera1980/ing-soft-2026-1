# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [06]: [P06_clean_architecture]

### Estudiante
- **Nombre completo**: [Diego Alejandro Torres Barragan]

--- Sin uso de IA



#### 1. Respuesta final

[Las capas
Clean Architecture organiza el sistema en capas concentricas donde el centro tiene la logica de negocio y afuera estan los detalles tecnicos. De adentro hacia afuera:

Entities: los objetos de negocio puros. En OpenLib Market serian Libro, Usuario, Orden. No dependen de nada.
Use Cases: la logica de la aplicacion. Por ejemplo PublicarLibroUseCase o ProcesarPagoUseCase. Solo conocen las entidades.
Interface Adapters: traducen datos entre capas. Aqui van los controladores REST y las implementaciones de repositorios.
Frameworks & Drivers: los detalles tecnicos como Spring Boot, PostgreSQL, JavaFX. La capa mas externa.

La regla de dependencia
Las dependencias solo pueden apuntar hacia adentro. Un Use Case puede usar una Entity, pero una Entity nunca puede saber que existe un repositorio o un framework. Esto hace que el nucleo del negocio sea independiente de la tecnologia.
Inversion de dependencia en los limites
El punto clave es que si un Use Case necesita guardar datos, no puede importar directamente RepositorioLibroPostgresImpl porque eso romperia la regla. La solucion es que el Use Case define una interfaz (RepositorioLibro) y la implementacion concreta vive en la capa externa. Asi el flujo de control va hacia afuera pero la dependencia del codigo apunta hacia adentro.
Diferencia con arquitectura tradicional
En la arquitectura tradicional (presentacion → logica → datos) la logica de negocio termina dependiendo directamente de JPA o JDBC, lo que la acopla a la tecnologia y hace dificil hacer pruebas. Con Clean Architecture eso no pasa porque los Use Cases solo conocen interfaces, no implementaciones concretas.]

Entities — solo logica de negocio, sin imports de frameworks:
```java
javapublic class Libro {
    private Long id;
    private String titulo;
    private String isbn;
    private int stock;

    public boolean estaDisponible() {
        return stock > 0;
    }
}
```
Use Case — define la interfaz que necesita, no la implementacion:
```java
javapublic interface RepositorioLibro {
    Optional<Libro> buscarPorId(Long id);
    void guardar(Libro libro);
}

public class PublicarLibroUseCase {
    private final RepositorioLibro repositorio;

    public PublicarLibroUseCase(RepositorioLibro repositorio) {
        this.repositorio = repositorio;
    }

    public void ejecutar(Libro libro) {
        if (libro.getIsbn() == null) throw new IllegalArgumentException("ISBN requerido");
        repositorio.guardar(libro);
    }
}
```
Este Use Case no sabe nada de PostgreSQL ni de Spring. Solo conoce la interfaz.

Interface Adapter — el controlador REST y la implementacion del repositorio:
```java
java// Controlador REST (capa de adaptadores)
@RestController
public class LibroController {
    private final PublicarLibroUseCase useCase;

    public LibroController(PublicarLibroUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping("/libros")
    public ResponseEntity<?> publicar(@RequestBody LibroDTO dto) {
        Libro libro = new Libro(dto.getTitulo(), dto.getIsbn());
        useCase.ejecutar(libro);
        return ResponseEntity.ok().build();
    }
}

// Implementacion del repositorio (tambien capa de adaptadores)
public class RepositorioLibroPostgresImpl implements RepositorioLibro {
    // aqui si va JPA, EntityManager, etc.
    public void guardar(Libro libro) { /* logica con JPA */ }
    public Optional<Libro> buscarPorId(Long id) { /* query a postgres */ }
}
```

Como se ve la regla de dependencia en este ejemplo
```
LibroController  →  PublicarLibroUseCase  →  RepositorioLibro (interfaz)
                                                      ↑
                                        RepositorioLibroPostgresImpl
```
El controller depende del use case, el use case depende de la interfaz, y la implementacion concreta implementa esa interfaz. En ningun momento el use case sabe que existe Postgres. Eso es la inversion de dependencia aplicada en el limite entre capas.