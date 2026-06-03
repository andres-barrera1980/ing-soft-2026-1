# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [01]: [Solid principio]

### Estudiante
- **Marlon Garcia**: [Tu nombre y apellido]

---


### Respuesta sin IA 

Viola los principios de single responsability e inversión de dependencias. La clase se llama GestorLibros, por lo que debería encargarse únicamente de gestionar libros. El problema es que tiene un método que publica libros y, dentro de ese mismo método, también valida información, se conecta a PostgreSQL, envía correos, entre otras tareas.

Idealmente, GestorLibros debería limitarse a gestionar libros y delegar esas responsabilidades a otras clases o servicios especializados. Además, la clase instancia directamente otras clases, lo cual aumenta el acoplamiento. En lugar de hacer todas las operaciones por sí misma, debería solicitar o delegar esas acciones a dependencias externas.


### Refactor del codigo 
```java
    public class GestorLibro {

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
