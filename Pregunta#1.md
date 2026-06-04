# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [01]: GestorLibro y SOLID

### Estudiante
- **Nombre completo**: Samuel López Gómez

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Claude|
| **Modelo específico** |Sonnet 4.6 (bajo) |
| **¿Por qué elegiste este LLM?** | [Justifica en 1-3 oraciones. Si respondes sin IA, explica por qué decidiste no usar LLM para esta pregunta.] |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```
[Pega aquí el prompt exacto que enviaste al LLM. 
Incluye TODO el texto, sin editar ni resumir.

Actúa como un arquitecto de software senior revisando código del proyecto OpenLib Market, una plataforma de compra-venta de libros (asume un entorno Java 21). Adjunto el método `publicarLibro` de la clase `GestorLibro` del módulo de administración.

Necesito que:
1. Identifiques qué principio SOLID principal se está violando en este código y expliques detalladamente por qué.
2. Propongas un refactoring paso a paso aplicando ese principio para corregir el diseño de la clase.
3. Generes el código Java refactorizado (creando las clases e interfaces necesarias).

Restricciones:
- Aplica inyección de dependencias mediante constructores.
- El código resultante debe compilar conceptualmente en Java 21.
- Asegúrate de que las nuevas clases no violen el principio de inversión de dependencias.

Formato de salida esperado: Markdown estructurado con tres secciones exactas: "Diagnóstico", "Estrategia de Refactoring" y "Código Final".

Código a analizar:
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
```

---

### Respuesta del LLM

# Análisis SOLID — `GestorLibro.publicarLibro`
> OpenLib Market · Revisión arquitectónica · Java 21

---

## Diagnóstico

### Principio violado: **Single Responsibility Principle (SRP)** — el más flagrante

El SRP establece que *una clase debe tener una única razón para cambiar*. `GestorLibro.publicarLibro` tiene **seis razones independientes para cambiar**:

| # | Responsabilidad | ¿Razón de cambio? |
|---|---|---|
| 1 | Validar datos del libro | Cambian las reglas de negocio |
| 2 | Persistir en base de datos | Cambia el motor de BD o el ORM |
| 3 | Generar el slug de URL | Cambia el algoritmo de slugificación |
| 4 | Enviar correo al vendedor | Cambia el proveedor SMTP |
| 5 | Registrar en log | Cambia la estrategia de logging |
| 6 | Indexar para búsqueda | Cambia el motor de búsqueda |

**Adicionalmente**, hay tres violaciones secundarias encadenadas:

- **DIP (Dependency Inversion Principle):** `GestorLibro` depende de implementaciones concretas (`EmailService`, `SearchIndex`, `DriverManager`), no de abstracciones.
- **OCP (Open/Closed Principle):** Agregar un nuevo paso al publicar (e.g. notificar a Slack) obliga a modificar `GestorLibro` directamente.
- **Acoplamiento duro de infraestructura:** credenciales y URLs hardcodeadas dentro de la lógica de negocio.

---

## Estrategia de Refactoring

**Paso 1 — Extraer interfaces** para cada colaborador externo, respetando DIP.

**Paso 2 — Extraer la lógica de slug** a un servicio de utilidad sin estado (`SlugGenerator`).

**Paso 3 — Extraer la validación** a un componente dedicado (`LibroValidator`), alineado con el `Book` real del proyecto (que usa `title`, `author`, `isbn`).

**Paso 4 — Refactorizar `GestorLibro`** para recibir todas las dependencias por constructor (inyección de dependencias) y delegar cada responsabilidad a su colaborador.

**Paso 5 — Implementaciones concretas** de las interfaces, desacopladas de `GestorLibro`.

El resultado es que agregar un nuevo comportamiento (e.g. publicar en redes sociales) sólo requiere implementar una nueva clase, nunca tocar `GestorLibro`.

---

## Código Final

### `LibroValidator.java`

```java
package com.openlib.service.publicacion;

import com.openlib.model.Book;

/**
 * Responsabilidad única: validar que un Book tenga los datos mínimos
 * requeridos para ser publicado.
 */
public class LibroValidator {

    public void validar(Book libro) {
        if (libro.getIsbn() == null || libro.getIsbn().isBlank()) {
            throw new IllegalArgumentException("El ISBN del libro es obligatorio");
        }
        if (libro.getTitle() == null || libro.getTitle().isBlank()) {
            throw new IllegalArgumentException("El título del libro es obligatorio");
        }
        if (libro.getAuthor() == null || libro.getAuthor().isBlank()) {
            throw new IllegalArgumentException("El autor del libro es obligatorio");
        }
    }
}
```

---

### `SlugGenerator.java`

```java
package com.openlib.service.publicacion;

/**
 * Responsabilidad única: generar un slug URL-friendly a partir de un texto.
 * Sin dependencias externas; pura lógica de transformación de strings.
 */
public class SlugGenerator {

    public String generar(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException("El texto para generar slug no puede ser nulo o vacío");
        }
        return texto.toLowerCase()
                    .replace(" ", "-")
                    .replaceAll("[^a-z0-9-]", "");
    }
}
```

---

### `LibroRepository.java` (interfaz)

```java
package com.openlib.service.publicacion.port;

import com.openlib.model.Book;

/**
 * Puerto (abstracción) para persistencia de libros.
 * GestorLibro depende de esta interfaz, NO de JPA ni JDBC directamente.
 * La implementación concreta (JPA, H2, etc.) se inyecta desde afuera.
 */
public interface LibroRepository {
    Book guardar(Book libro);
}
```

---

### `NotificadorVendedor.java` (interfaz)

```java
package com.openlib.service.publicacion.port;

import com.openlib.model.Book;
import com.openlib.model.User;

/**
 * Puerto para notificaciones al vendedor.
 * Puede implementarse con SMTP, SendGrid, WhatsApp, etc.,
 * sin que GestorLibro sepa cuál se usa.
 */
public interface NotificadorVendedor {
    void notificarPublicacion(User vendedor, Book libro);
}
```

---

### `IndiceLibros.java` (interfaz)

```java
package com.openlib.service.publicacion.port;

import com.openlib.model.Book;

/**
 * Puerto para indexación de libros en un motor de búsqueda.
 * Desacopla GestorLibro de Elasticsearch, Lucene, o cualquier otro motor.
 */
public interface IndiceLibros {
    void indexar(Book libro);
}
```

---

### `GestorLibro.java` (refactorizado)

```java
package com.openlib.service.publicacion;

import com.openlib.model.Book;
import com.openlib.model.User;
import com.openlib.service.publicacion.port.IndiceLibros;
import com.openlib.service.publicacion.port.LibroRepository;
import com.openlib.service.publicacion.port.NotificadorVendedor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Responsabilidad única: orquestar el flujo de publicación de un libro.
 *
 * No sabe CÓMO se persiste, CÓMO se envía el correo ni CÓMO se indexa.
 * Solo sabe QUÉ pasos ocurren y en qué orden.
 *
 * Todas las dependencias se inyectan por constructor → testeable,
 * intercambiable y alineado con DIP.
 */
public class GestorLibro {

    private static final Logger log = LoggerFactory.getLogger(GestorLibro.class);

    private final LibroValidator      validator;
    private final SlugGenerator       slugGenerator;
    private final LibroRepository     libroRepository;
    private final NotificadorVendedor notificador;
    private final IndiceLibros        indice;

    // Inyección por constructor: sin 'new' de implementaciones concretas
    public GestorLibro(
            LibroValidator validator,
            SlugGenerator slugGenerator,
            LibroRepository libroRepository,
            NotificadorVendedor notificador,
            IndiceLibros indice) {

        this.validator       = validator;
        this.slugGenerator   = slugGenerator;
        this.libroRepository = libroRepository;
        this.notificador     = notificador;
        this.indice          = indice;
    }

    public Book publicarLibro(Book libro, User vendedor) {

        // 1. Validar datos mínimos del libro
        validator.validar(libro);

        // 2. Generar slug para la URL
        String slug = slugGenerator.generar(libro.getTitle());
        libro.setStatus("APROBADO"); // integrado con el campo real del modelo

        // 3. Persistir en la base de datos
        Book libroGuardado = libroRepository.guardar(libro);

        // 4. Notificar al vendedor (SMTP, SendGrid, etc. — no importa aquí)
        notificador.notificarPublicacion(vendedor, libroGuardado);

        // 5. Registrar en log (SLF4J, sin acoplamiento a java.util.Logger)
        log.info("Libro publicado: isbn={} titulo='{}' vendedor='{}'",
                libroGuardado.getIsbn(), libroGuardado.getTitle(), vendedor.getName());

        // 6. Indexar para búsqueda
        indice.indexar(libroGuardado);

        return libroGuardado;
    }
}
```

---

### Implementaciones concretas de ejemplo

Estas clases **no pertenecen al dominio** — van en la capa de infraestructura:

```java
// Adaptador JPA — usa el BookRepository real del proyecto
package com.openlib.infrastructure.persistence;

import com.openlib.model.Book;
import com.openlib.repository.BookRepository;
import com.openlib.service.publicacion.port.LibroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JpaLibroRepository implements LibroRepository {

    private final BookRepository jpaRepository; // el repo JPA ya existente

    @Override
    public Book guardar(Book libro) {
        return jpaRepository.save(libro);
    }
}
```

```java
// Adaptador SMTP para notificaciones
package com.openlib.infrastructure.email;

import com.openlib.model.Book;
import com.openlib.model.User;
import com.openlib.service.publicacion.port.NotificadorVendedor;
import org.springframework.stereotype.Component;

@Component
public class SmtpNotificadorVendedor implements NotificadorVendedor {

    @Override
    public void notificarPublicacion(User vendedor, Book libro) {
        // Aquí va la lógica real de envío SMTP/SendGrid
        // Las credenciales vienen de application.properties, no hardcodeadas
        System.out.printf("📧 Correo enviado a %s: Tu libro '%s' fue publicado.%n",
                vendedor.getEmail(), libro.getTitle());
    }
}
```

```java
// Adaptador Elasticsearch para indexación
package com.openlib.infrastructure.search;

import com.openlib.model.Book;
import com.openlib.service.publicacion.port.IndiceLibros;
import org.springframework.stereotype.Component;

@Component
public class ElasticsearchIndiceLibros implements IndiceLibros {

    @Override
    public void indexar(Book libro) {
        // Lógica real de indexación con el cliente de ES
        System.out.printf("🔍 Indexando libro isbn=%s en ElasticSearch%n", libro.getIsbn());
    }
}
```

---

### Estructura de paquetes resultante

```
com.openlib/
├── model/
│   ├── Book.java                        ← sin cambios
│   └── User.java                        ← sin cambios
├── service/
│   └── publicacion/
│       ├── GestorLibro.java             ← orquestador limpio
│       ├── LibroValidator.java          ← valida datos
│       ├── SlugGenerator.java           ← genera slug
│       └── port/                        ← interfaces (contratos)
│           ├── LibroRepository.java
│           ├── NotificadorVendedor.java
│           └── IndiceLibros.java
└── infrastructure/
    ├── persistence/
    │   └── JpaLibroRepository.java
    ├── email/
    │   └── SmtpNotificadorVendedor.java
    └── search/
        └── ElasticsearchIndiceLibros.java
```

---

### Beneficio tangible: test unitario sin Spring ni BD

```java
@Test
void publicarLibro_deberiaOrquestarTodosLosPasos() {
    var validator   = new LibroValidator();
    var slugGen     = new SlugGenerator();
    var repo        = mock(LibroRepository.class);
    var notificador = mock(NotificadorVendedor.class);
    var indice      = mock(IndiceLibros.class);

    var gestor = new GestorLibro(validator, slugGen, repo, notificador, indice);

    var libro    = Book.builder().isbn("978-3-16").title("Clean Code").author("Martin").build();
    var vendedor = User.builder().name("Carlos").email("carlos@test.com").build();

    when(repo.guardar(any())).thenReturn(libro);

    gestor.publicarLibro(libro, vendedor);

    verify(repo).guardar(libro);
    verify(notificador).notificarPublicacion(vendedor, libro);
    verify(indice).indexar(libro);
}
```

```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

[Evalúa tu propio prompt, no la respuesta del LLM. ¿El contexto fue suficiente? ¿Las restricciones fueron claras? ¿El formato de salida que pediste ayudó a obtener una buena respuesta? ¿Qué parte de tu prompt fue más efectiva? Sé específico: menciona fragmentos concretos de tu prompt que funcionaron bien.]


#### 2. ¿Qué se puede mejorar?

[¿Qué le faltó a tu prompt? ¿Qué harías diferente si pudieras reformularlo? ¿El LLM entendió mal algo por falta de claridad en tu prompt? ¿La respuesta tiene errores u omisiones? ¿Qué no cubrió el LLM que tú sí sabes por lo visto en clase?]


#### 3. Respuesta final

[Escribe tu respuesta definitiva a la pregunta del parcial, integrando lo que aprendiste del LLM pero yendo más allá. Corrige errores, llena omisiones, conecta con conceptos vistos en clase. Esta es tu respuesta: demuestra que tú dominas el tema.]
