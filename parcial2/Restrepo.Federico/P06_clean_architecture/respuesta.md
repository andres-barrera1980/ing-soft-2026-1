# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta 06: Clean Architecture — Capas y regla de dependencia

### Estudiante
- **Nombre completo**: Federico Restrepo

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Claude |
| **Modelo específico** | Claude Sonnet 4.5 |
| **¿Por qué elegiste este LLM?** | Claude tiene buena comprensión de conceptos arquitectónicos como Clean Architecture y explica la regla de dependencia con claridad, además de generar ejemplos concretos del dominio sin inventar cosas fuera de contexto. |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```text
Actúa como un Arquitecto de Software Senior con experiencia en Clean Architecture (libro de Robert C. Martin).

Contexto: Estoy desarrollando OpenLib Market, un marketplace de libros en Java. Necesito entender cómo aplicar Clean Architecture a este proyecto.

Explícame lo siguiente:
1. Las cuatro capas de Clean Architecture (Entities, Use Cases, Interface Adapters, Frameworks & Drivers) y qué tipo de código va en cada una.
2. La regla de dependencia: qué significa exactamente, en qué dirección apuntan las dependencias y por qué.
3. Cómo se aplica el Principio de Inversión de Dependencia (DIP) en los límites entre capas — especialmente entre Use Cases e Interface Adapters. Usa un ejemplo con interfaces y clases concretas.
4. Cómo se diferencia Clean Architecture de una arquitectura en capas tradicional (presentación → lógica de negocio → datos). ¿Cuál es la diferencia fundamental?
5. Para cada capa, dame ejemplos concretos de qué clases o componentes irían ahí en OpenLib Market (libros, usuarios, pagos, búsqueda, etc.).

Formato de salida: respuesta en markdown, con una tabla resumen de capas al final y ejemplos de código Java donde sea necesario para ilustrar el DIP en los límites entre capas.
```

---

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

*(1 iteración)*

---

### Clean Architecture en OpenLib Market

#### 1. Las cuatro capas

Clean Architecture organiza el sistema en círculos concéntricos. El principio fundamental es que el código fuente solo puede depender de capas más internas, nunca de capas externas.

```
[ Frameworks & Drivers        ]  ← capa más externa
  [ Interface Adapters        ]
    [ Use Cases               ]
      [ Entities              ]  ← capa más interna
```

---

**Capa 1 — Entities (Entidades)**

Contiene las reglas de negocio empresariales: objetos que representan conceptos del dominio con sus invariantes. No dependen de ninguna otra capa ni de ningún framework.

En OpenLib Market:
- `Libro` (con validaciones de ISBN, precio mínimo, estado)
- `Usuario` (con roles y reglas de acceso)
- `Orden` (con reglas de transición de estados: pendiente → pagada → entregada)
- `Carrito` (con regla de máximo 10 ítems)

```java
// Entidad pura — sin anotaciones JPA, sin Spring, sin nada externo
public class Libro {
    private final String isbn;
    private final String titulo;
    private BigDecimal precio;
    private EstadoLibro estado;

    public Libro(String isbn, String titulo, BigDecimal precio) {
        if (isbn == null || isbn.isBlank()) throw new IllegalArgumentException("ISBN requerido");
        if (precio.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Precio debe ser positivo");
        this.isbn = isbn;
        this.titulo = titulo;
        this.precio = precio;
        this.estado = EstadoLibro.DISPONIBLE;
    }

    public void marcarAgotado() { this.estado = EstadoLibro.AGOTADO; }
    public boolean estaDisponible() { return estado == EstadoLibro.DISPONIBLE; }
    // getters...
}
```

---

**Capa 2 — Use Cases (Casos de uso)**

Contiene las reglas de negocio de la aplicación: orquesta el flujo entre entidades para cumplir un objetivo concreto. Define interfaces (puertos) para todo lo que necesita del exterior (repositorios, servicios de email, etc.) — nunca instancia implementaciones concretas.

En OpenLib Market:
- `ComprarLibroUseCase`
- `PublicarLibroUseCase`
- `ProcesarPagoUseCase`
- `BuscarLibrosUseCase`

```java
// Puerto de salida (output port) — definido en Use Cases, implementado en Interface Adapters
public interface RepositorioLibro {
    Optional<Libro> buscarPorIsbn(String isbn);
    void guardar(Libro libro);
}

public interface ServicioEmail {
    void enviar(String destinatario, String asunto, String cuerpo);
}

// Caso de uso
public class ComprarLibroUseCase {
    private final RepositorioLibro repositorioLibro;
    private final ServicioEmail servicioEmail;

    public ComprarLibroUseCase(RepositorioLibro repositorioLibro, ServicioEmail servicioEmail) {
        this.repositorioLibro = repositorioLibro;
        this.servicioEmail = servicioEmail;
    }

    public void ejecutar(String isbn, String emailComprador) {
        Libro libro = repositorioLibro.buscarPorIsbn(isbn)
            .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado"));
        if (!libro.estaDisponible()) throw new IllegalStateException("Libro no disponible");
        // lógica de compra...
        servicioEmail.enviar(emailComprador, "Compra confirmada", "Tu compra de " + libro.getTitulo() + " fue exitosa.");
    }
}
```

---

**Capa 3 — Interface Adapters (Adaptadores de interfaz)**

Convierte datos entre el formato que usan los casos de uso y el formato que usan los frameworks externos. Aquí van los controladores REST, repositorios JPA, implementaciones de los puertos definidos en Use Cases, y los DTOs.

En OpenLib Market:
- `LibroControllerREST` (convierte HTTP → llamada al use case)
- `LibroRepositorioJPA` (implementa `RepositorioLibro` usando Hibernate)
- `EmailServiceSMTP` (implementa `ServicioEmail` usando JavaMail)
- `LibroDTO` (objeto de transferencia para la API)

```java
// Implementación concreta del puerto — vive en Interface Adapters
public class LibroRepositorioJPA implements RepositorioLibro {
    private final LibroJpaRepository jpaRepository; // Spring Data JPA

    public LibroRepositorioJPA(LibroJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Libro> buscarPorIsbn(String isbn) {
        return jpaRepository.findByIsbn(isbn).map(LibroMapper::toDomain);
    }

    @Override
    public void guardar(Libro libro) {
        jpaRepository.save(LibroMapper.toEntity(libro));
    }
}
```

---

**Capa 4 — Frameworks & Drivers (Frameworks y controladores)**

La capa más externa. Aquí viven los frameworks, bases de datos, servidores web, UI. Son detalles de implementación que pueden reemplazarse sin tocar el núcleo.

En OpenLib Market:
- Spring Boot (configuración, arranque)
- PostgreSQL / Hibernate (base de datos)
- JavaFX (frontend de escritorio)
- Redis (caché)
- ElasticSearch (motor de búsqueda)

---

#### 2. La regla de dependencia

> *"El código fuente solo puede apuntar hacia adentro. Nada en un círculo interior puede saber nada sobre algo en un círculo exterior."* — Robert C. Martin

Las dependencias en tiempo de compilación apuntan siempre hacia el centro:

```
Frameworks → Interface Adapters → Use Cases → Entities
```

Entities no importa nada de Use Cases. Use Cases no importa nada de Spring ni de JPA. Esto garantiza que el núcleo del negocio puede testearse sin levantar ningún framework.

---

#### 3. DIP en los límites entre capas

El problema: Use Cases necesita guardar datos (función de Interface Adapters), pero no puede depender de Interface Adapters. La solución es el DIP: Use Cases define una **interfaz** (puerto de salida) y Interface Adapters la implementa.

```
Use Cases define:       RepositorioLibro (interface)
Interface Adapters:     LibroRepositorioJPA implements RepositorioLibro
Frameworks inyecta:     new ComprarLibroUseCase(new LibroRepositorioJPA(...))
```

La dependencia en código fuente va `LibroRepositorioJPA → RepositorioLibro`, que es de afuera hacia adentro. El flujo de control va `UseCase → LibroRepositorioJPA`, que es de adentro hacia afuera. El DIP desacopla ambas direcciones.

---

#### 4. Diferencia con arquitectura en capas tradicional

| Aspecto | Arquitectura tradicional | Clean Architecture |
|---|---|---|
| **Dirección de dependencias** | Todas apuntan hacia abajo (presentación → lógica → datos) | Todas apuntan hacia el centro (fuera → adentro) |
| **Base de datos** | Es la capa base sobre la que todo depende | Es un detalle externo, reemplazable |
| **Testabilidad** | Difícil sin BD real | Use Cases testeables con mocks |
| **Acoplamiento** | La lógica de negocio conoce JPA, SQL, etc. | La lógica de negocio no conoce ningún framework |
| **Cambio de BD** | Afecta múltiples capas | Solo afecta Interface Adapters |

En la arquitectura tradicional, la lógica de negocio depende de la base de datos. En Clean Architecture ocurre lo opuesto: la base de datos depende de las interfaces definidas por la lógica de negocio.

---

#### 5. Tabla resumen — ejemplos de OpenLib Market por capa

| Capa | Qué contiene | Ejemplos en OpenLib Market |
|---|---|---|
| **Entities** | Objetos de dominio con reglas de negocio | `Libro`, `Usuario`, `Orden`, `Carrito`, `ItemCarrito` |
| **Use Cases** | Orquestación de flujos + puertos (interfaces) | `ComprarLibroUseCase`, `PublicarLibroUseCase`, `RepositorioLibro` (interfaz), `ServicioEmail` (interfaz) |
| **Interface Adapters** | Controladores, repositorios JPA, DTOs, mappers | `LibroControllerREST`, `LibroRepositorioJPA`, `EmailServiceSMTP`, `LibroDTO` |
| **Frameworks & Drivers** | Infraestructura y frameworks | Spring Boot, PostgreSQL, Redis, ElasticSearch, JavaFX |

---

### Análisis crítico de la respuesta

El prompt funcionó bien porque el contexto de OpenLib Market hizo que usara clases concretas como Libro y Orden en vez de ejemplos genéricos. La parte de DIP fue la más útil, el diagrama de flechas dejó claro que flujo de control y dependencias van en direcciones opuestas.

Lo que faltó fue mostrar cómo se conecta todo en Spring Boot. Mencionó que "Frameworks inyecta" pero sin un ejemplo de @Bean eso queda en el aire. También hubiera ayudado un contraejemplo de código malo para entender por qué importa y no solo verlo como cuestión de estilo.

Para la próxima le pediría el cableado real en Spring Boot y un ejemplo de lo que no se debe hacer.
