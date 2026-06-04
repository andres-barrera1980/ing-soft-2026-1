# Plantilla de entrega — Parcial 2

---

## Pregunta 6: Analizar la estructura de capas y la regla de dependencia

### Estudiante
- **Nombre completo**: Samuel Iregui

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Antigravity |
| **Modelo específico** | Gemini 3.5 Flash |
| **¿Por qué elegiste este LLM?** | Elegi este modelo por comodidad propia y debido a que tengo pago gemini pro y ya tengo todo bien organizado para los diferentes trabajos |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```text
Actúa como un consultor senior de arquitectura de software. Explica Clean Architecture de Robert C. Martin de forma clara y profunda. En tu explicación debes incluir:
1. Las 4 capas concéntricas (Entities, Use Cases, Interface Adapters, Frameworks & Drivers) y qué lógica o código reside en cada una.
2. La Regla de Dependencia ("Dependency Rule") y cómo se aplica en la práctica en un proyecto.
3. Cómo se aplica el Principio de Inversión de Dependencias (DIP) en los límites de las capas, especialmente para interactuar con la base de datos o APIs externas sin violar la arquitectura.
4. Una comparación directa contra una arquitectura en capas tradicional (Presentación -> Lógica -> Datos) y la diferencia en el sentido de su acoplamiento.
5. Ejemplos de código o clases del dominio del proyecto "OpenLib Market" (una plataforma de venta de libros) para cada capa.

Formato de salida esperado: Explicación estructurada en Markdown con subsecciones claras y ejemplos aplicados.
```

---

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

### 1. Las 4 capas de Clean Architecture

Clean Architecture organiza un sistema de software dividiendo el código en capas concéntricas:

*   **Entidades (Entities)**: Encapsulan las reglas de negocio globales y generales de la empresa. Son las más estables y no cambian cuando cambian los detalles de la interfaz o bases de datos. En Java, se implementan como POJOs (Plain Old Java Objects) o `records` limpios.
*   **Casos de Uso (Use Cases)**: Contienen las reglas de negocio específicas de la aplicación. Orquestan el flujo de datos desde y hacia las entidades. Cambios en esta capa no deberían afectar las entidades, pero sí están aislados de bases de datos, frameworks o UI.
*   **Adaptadores de Interfaz (Interface Adapters)**: Convierten los datos del formato más conveniente para los casos de uso y entidades, al formato más conveniente para los agentes externos (como bases de datos o la web). Aquí residen los Controladores REST, Presentadores y las implementaciones de los repositorios.
*   **Frameworks y Drivers**: Es la capa más externa y está compuesta por herramientas y tecnologías como la base de datos (PostgreSQL), frameworks web (Spring Boot), librerías de UI o servicios en la nube. Esta capa solo contiene código de pegamento (glue code).

---

### 2. La Regla de Dependencia (Dependency Rule)

La regla fundamental de esta arquitectura es que **las dependencias de código solo pueden apuntar hacia adentro**. 
Esto significa que:
*   Nada en un círculo interno puede saber algo sobre algo en un círculo externo.
*   Las clases de la capa interna no pueden importar o hacer referencia a clases de las capas externas (por ejemplo, un Caso de Uso no puede importar un controlador de Spring o un modelo JPA).

---

### 3. Inversión de Dependencias (DIP) en los límites de capas

Para cumplir con la Regla de Dependencia, necesitamos comunicar capas internas con externas. Por ejemplo, un caso de uso necesita guardar datos en una base de datos PostgreSQL (externa).

Para hacer esto sin que el caso de uso dependa de la base de datos, aplicamos **Inversión de Dependencia (DIP)**:
1. En la capa de **Casos de Uso**, definimos una interfaz (puerto) llamada `LibroRepository`.
2. El caso de uso interactúa únicamente con esta interfaz `LibroRepository`.
3. En la capa de **Adaptadores**, implementamos esta interfaz mediante una clase concreta, por ejemplo, `PostgresLibroRepositoryAdapter`, la cual usa Spring Data o JPA para comunicarse con la base de datos.
4. En tiempo de ejecución, Spring inyecta la implementación (adaptador) en el caso de uso. De esta forma, el flujo de control va hacia afuera (Caso de Uso -> Base de datos), pero la dependencia de código va hacia adentro (Adaptador -> Interfaz del Caso de Uso).

---

### 4. Comparación: Tradicional vs Clean Architecture

| Criterio | Arquitectura de 3 Capas Tradicional | Clean Architecture |
|---|---|---|
| **Estructura** | Presentación $\rightarrow$ Lógica de Negocio $\rightarrow$ Acceso a Datos | Capas concéntricas (Entidades en el centro) |
| **Acoplamiento** | La lógica de negocio depende directamente de la base de datos o de ORMs (como Hibernate). | La base de datos y la UI dependen de las abstracciones de la lógica de negocio. |
| **Flexibilidad** | Cambiar de base de datos o framework requiere modificar la lógica de negocio. | Cambiar la persistencia o la UI es un detalle externo que no toca el core del negocio. |
| **Testeabilidad** | Difícil de probar sin mockear la base de datos o sin levantar contextos pesados. | Altamente testeable. La lógica de negocio se puede probar con pruebas unitarias simples. |

---

### 5. Ejemplos aplicados a OpenLib Market

#### Capa de Entidades
```java
// Entidad pura sin anotaciones de JPA o persistencia
public class Libro {
    private final String isbn;
    private String titulo;
    private double precio;

    public Libro(String isbn, String titulo, double precio) {
        if (isbn == null || titulo == null) throw new IllegalArgumentException("Datos inválidos");
        this.isbn = isbn;
        this.titulo = titulo;
        this.precio = precio;
    }
    // Getters y lógica de negocio interna del libro
}
```

#### Capa de Casos de Uso
```java
// Interfaz de puerto (definida en el caso de uso)
public interface LibroRepository {
    void guardar(Libro libro);
}

// Caso de uso que orquesta la acción de publicar
public class PublicarLibroUseCase {
    private final LibroRepository repository;

    public PublicarLibroUseCase(LibroRepository repository) {
        this.repository = repository;
    }

    public void ejecutar(String isbn, String titulo, double precio) {
        Libro nuevoLibro = new Libro(isbn, titulo, precio);
        repository.guardar(nuevoLibro);
    }
}
```

#### Capa de Adaptadores de Interfaz
```java
// Adaptador de salida (Database) que implementa la interfaz del Caso de Uso
public class PostgresLibroRepositoryAdapter implements LibroRepository {
    private final SpringDataLibroRepository jpaRepository; // Depende de la capa externa

    public PostgresLibroRepositoryAdapter(SpringDataLibroRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void guardar(Libro libro) {
        LibroEntity entity = new LibroEntity(libro.getIsbn(), libro.getTitulo(), libro.getPrecio());
        jpaRepository.save(entity);
    }
}
```

#### Capa de Frameworks y Drivers
```java
// Entidad de persistencia JPA de Hibernate
@Entity
@Table(name = "libros")
public class LibroEntity {
    @Id
    private String isbn;
    private String titulo;
    private double precio;
    // Constructores, getters, setters
}
```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

El prompt solicita de forma muy estructurada los aspectos clave de Clean Architecture (capas, regla de dependencia, DIP y comparación). Lo más efectivo fue pedir ejemplos concretos en el contexto de OpenLib Market, ya que esto saca al LLM de la teoría abstracta y permite evaluar si entiende cómo mapear las capas a un proyecto real.

#### 2. ¿Qué se puede mejorar?

El prompt pudo haber preguntado acerca de cómo manejar las estructuras de datos (DTOs) y las entidades JPA que cruzan los límites de las capas, para ver si el LLM sugería acoplar las entidades de base de datos JPA con la lógica de negocio, o si aclaraba la necesidad de traductores (Mappers) entre capas (un pitfall muy común al adoptar Clean Architecture).

#### 3. Respuesta final

En conclusión, la respuesta del LLM describe correctamente las capas y el funcionamiento de Clean Architecture. Es crucial entender que la diferencia fundamental con la arquitectura tradicional de tres capas radica en la dirección del acoplamiento: **en Clean Architecture la base de datos es un detalle externo**.

En el contexto de OpenLib Market, esto se traduce en:
- **Entidades**: Clases puras de Java (POJOs) sin anotaciones de JPA (`@Entity`, `@Table`) para evitar que el motor de persistencia (Hibernate/Spring Data) dicte las reglas del negocio.
- **Casos de Uso**: Contienen la orquestación (ej. `PublicarLibroUseCase`) y definen los puertos (interfaces como `LibroRepository`).
- **Adaptadores**: Implementan los puertos (`PostgresLibroRepositoryAdapter`) mapeando las entidades del dominio puro a entidades JPA (`LibroEntity`) mediante Mappers.
- **Frameworks**: Spring Boot, Hibernate y PostgreSQL quedan completamente aislados en la periferia.

El LLM estructuró muy bien los ejemplos de código mostrando cómo se aplica la Inversión de Dependencias (DIP) mediante la separación de la interfaz `LibroRepository` (definida en Casos de Uso) de su adaptador concreto en persistencia. No obstante, para un sistema en producción, vale la pena resaltar que es necesario definir DTOs específicos para evitar que los cambios en la API web expongan las entidades directamente a los clientes, algo que el LLM omitió en su explicación.
