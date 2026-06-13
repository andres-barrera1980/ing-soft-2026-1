# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta 1: Identificar el principio violado en una clase y proponer refactoring

### Estudiante
- **Nombre completo**: Samuel Iregui

**Respuesta sin IA**

La clase GestorLibros incumple los principios de Responsabilidad Única e Inversión de Dependencias. Aunque su función debería ser únicamente gestionar libros, también realiza tareas como validar datos, acceder a la base de datos y enviar correos. Además, crea directamente sus dependencias, generando un alto acoplamiento. Lo adecuado sería delegar estas funciones a servicios especializados e interactuar con ellos mediante abstracciones.

```java

public class GestorLibro {

    private final ValidadorLibro validador;
    private final RepositorioLibro repositorio;
    private final GeneradorSlug generadorSlug;
    private final ServicioCorreo servicioCorreo;
    
    public GestorLibro(
            ValidadorLibro validador,
            RepositorioLibro repositorio,
            GeneradorSlug generadorSlug,
            ServicioCorreo servicioCorreo) {
        this.validador = validador;
        this.repositorio = repositorio;
        this.generadorSlug = generadorSlug;
        this.servicioCorreo = servicioCorreo;
    }
    public void publicarLibro(Libro libro, Usuario vendedor) {
        validador.validar(libro);
        libro.setSlug(
                generadorSlug.crearSlug(libro.getTitulo())
        );
        repositorio.guardar(libro);
        servicioCorreo.enviarConfirmacion(
                vendedor,
                libro
        );
    }
}
```

