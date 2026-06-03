# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta 01: SOLID - Principio en GestorLibro

### Estudiante
- **Nombre completo**: Federico Restrepo

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Gemini |
| **Modelo específico** | Gemini 3.1 Pro (High) |
| **¿Por qué elegiste este LLM?** | Gemini 3.1 Pro es excelente analizando arquitectura de software y detectando violaciones a los principios SOLID en código Java. |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```text
Actúa como un Arquitecto de Software Senior. 
Contexto: Estoy trabajando en el módulo de administración de un proyecto llamado OpenLib Market (escrito en Java). Tenemos una clase `GestorLibro` con un método `publicarLibro(Libro libro, Usuario vendedor)` que realiza múltiples tareas a la vez: validación, guardado en BD, generación de slug, envío de correo, logging e indexación.

Código a analizar:
(Se incluye el código de la clase GestorLibro del enunciado)

Tarea:
1. Identifica qué principio SOLID principal se está violando en esta clase y explica brevemente por qué.
2. Propón un refactoring completo del código para corregir esta violación, separando las responsabilidades correctamente.

Restricciones: Usa Java 21, aplica inyección de dependencias mediante constructores, no asumas el uso de frameworks como Spring (hazlo con Java puro), y mantén un acoplamiento bajo mediante el uso de interfaces.

Formato de salida: Estructura tu respuesta en Markdown, entregando primero tu diagnóstico teórico y luego los fragmentos de código refactorizados.
```

---

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**
**Diagnóstico**
El principio principal que se está violando de forma evidente es el **Single Responsibility Principle (SRP - Principio de Responsabilidad Única)**. Este principio dicta que una clase debe tener una sola razón para cambiar. 
Actualmente, `GestorLibro` cambiará si cambian las reglas de validación, el motor de base de datos, el algoritmo del slug, el servidor de correo, la estrategia de logs o el motor de búsqueda.

**Refactoring propuesto**
Para solucionarlo, debemos delegar estas responsabilidades en abstracciones separadas (interfaces) e inyectarlas en un caso de uso orquestador (`PublicadorDeLibros`), respetando también la **Inversión de Dependencias (DIP)**.

```java
// Abstracciones (Interfaces)
public interface LibroRepository { void guardar(Libro libro); }
public interface GeneradorSlug { String generarPara(String texto); }
public interface Notificador { void notificarPublicacion(Usuario usuario, Libro libro); }
public interface BuscadorIndex { void indexar(Libro libro); }
public interface LoggerService { void info(String mensaje); }

// Orquestador (El nuevo GestorLibro)
public class PublicadorDeLibros {
    private final LibroRepository repository;
    private final GeneradorSlug generadorSlug;
    private final Notificador notificador;
    private final BuscadorIndex buscadorIndex;
    private final LoggerService logger;

    public PublicadorDeLibros(LibroRepository repository, GeneradorSlug generadorSlug,
                              Notificador notificador, BuscadorIndex buscadorIndex, 
                              LoggerService logger) {
        this.repository = repository;
        this.generadorSlug = generadorSlug;
        this.notificador = notificador;
        this.buscadorIndex = buscadorIndex;
        this.logger = logger;
    }

    public void publicarLibro(Libro libro, Usuario vendedor) {
        validarLibro(libro);
        String slug = generadorSlug.generarPara(libro.getTitulo());
        libro.setSlug(slug);
        repository.guardar(libro);
        buscadorIndex.indexar(libro);
        notificador.notificarPublicacion(vendedor, libro);
        logger.info("Libro publicado: " + libro.getIsbn() + " por " + vendedor.getNombre());
    }

    private void validarLibro(Libro libro) {
        if (libro.getIsbn() == null || libro.getTitulo() == null || libro.getAutor() == null) {
            throw new IllegalArgumentException("Datos del libro incompletos");
        }
    }
}
```

---

### Análisis crítico de la respuesta

El prompt fue efectivo porque no solo copió y pegó el código, sino que brindó el contexto del proyecto (OpenLib Market) e impuso restricciones técnicas arquitectónicas muy claras (Java 21, sin frameworks externos y exigiendo inyección de dependencias). Esto obligó al LLM a entregar una solución de "Java puro" orientada a interfaces, lo cual evita que la IA genere un código acoplado con anotaciones de Spring Boot que ocultarían el verdadero rediseño.


Aunque el LLM identificó correctamente la violación del Principio de Responsabilidad Única (SRP) y separó la base de datos, correos y logs en interfaces, cometió un pequeño error de diseño en su respuesta: dejó la lógica de validación (`validarLibro`) anidada dentro de la nueva clase orquestadora `PublicadorDeLibros`. Esto significa que si mañana cambian las reglas de negocio (ej. el libro requiere un precio obligatorio), la clase `PublicadorDeLibros` volverá a cambiar, violando de nuevo el SRP. En mi prompt debí ser más estricto exigiendo aislar TODAS las responsabilidades, incluidas las validaciones.


El principio principal que se viola es el **SRP (Single Responsibility Principle)**, ya que la clase original tiene infraestructura (JDBC), notificaciones (Email) y lógica de negocio (Slugs/Validación) fuertemente acopladas. 

Para que el refactoring sea 100% correcto y respete el SRP, no basta con lo que propuso la IA; debemos extraer también la validación a un componente independiente (`LibroValidator`). De este modo, `PublicadorDeLibros` queda exclusivamente como un caso de uso orquestador (fachada) y cumple estrictamente con SRP y DIP (Dependency Inversion Principle).

```java
public interface LibroValidator { void validar(Libro libro); }

public class PublicadorDeLibros {
    private final LibroValidator validator;
    private final LibroRepository repository;
    private final GeneradorSlug generadorSlug;
    private final Notificador notificador;
    private final BuscadorIndex buscadorIndex;
    private final LoggerService logger;

    public PublicadorDeLibros(LibroValidator validator, LibroRepository repository, GeneradorSlug generadorSlug,
                              Notificador notificador, BuscadorIndex buscadorIndex, LoggerService logger) {
        this.validator = validator;
        this.repository = repository;
        this.generadorSlug = generadorSlug;
        this.notificador = notificador;
        this.buscadorIndex = buscadorIndex;
        this.logger = logger;
    }

    public void publicarLibro(Libro libro, Usuario vendedor) {
        validator.validar(libro);
        libro.setSlug(generadorSlug.generarPara(libro.getTitulo()));
        repository.guardar(libro);
        buscadorIndex.indexar(libro);
        notificador.notificarPublicacion(vendedor, libro);
        logger.info("Libro publicado: " + libro.getIsbn());
    }
}
```
