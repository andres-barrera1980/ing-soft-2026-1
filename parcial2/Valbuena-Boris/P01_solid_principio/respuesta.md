# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [1]: [solid_principio]

### Estudiante
- **Nombre completo**: Boris Nicolas Valbuena Gueirsman

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [ChatGPT ]|
| **Modelo específico** | [ GPT-5.5.] |
| **¿Por qué elegiste este LLM?** | [por que es la ia que estoy utilizando ultimamnte ] |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```
Actúa como un arquitecto de software senior especializado en Java 21, Spring Boot 3.x, PostgreSQL y principios SOLID.

Contexto:
Estoy trabajando en OpenLib Market, una plataforma de compra y venta de libros. El siguiente código pertenece al módulo de administración y se encarga de publicar libros en el sistema.

Necesito que analices la siguiente clase:

```java
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

Antes de responder:

1. Analiza paso a paso las responsabilidades de la clase.
2. Identifica qué principio SOLID se está violando principalmente.
3. Indica si existen otros principios SOLID afectados y justifica tu respuesta.
4. Explica por qué la implementación actual puede generar problemas de mantenimiento, escalabilidad y testabilidad.

Restricciones:

* Utiliza conceptos compatibles con Java 21 y Spring Boot 3.x.
* Aplica principios SOLID y buenas prácticas de Clean Code.
* Considera el uso de inyección de dependencias.
* No elimines funcionalidades existentes.
* Justifica técnicamente cada decisión de diseño.

Formato de salida:

# Diagnóstico

# Principio SOLID principal violado

# Otros principios afectados

# Problemas detectados

# Refactoring propuesto

# Beneficios de la solución

# Ejemplo de código refactorizado

Validación:

La respuesta debe explicar claramente las responsabilidades actuales de la clase, identificar correctamente el principio SOLID principal violado, justificar las decisiones de refactorización y proponer una solución que reduzca el acoplamiento y aumente la cohesión.

---

### Respuesta del LLM

# Diagnóstico

La clase `GestorLibro` concentra múltiples responsabilidades de negocio, infraestructura y comunicación externa dentro de un único método.

### Responsabilidades actuales identificadas

| Paso | Responsabilidad               | Tipo                           |
| ---- | ----------------------------- | ------------------------------ |
| 1    | Validar datos del libro       | Negocio                        |
| 2    | Persistir libro en PostgreSQL | Infraestructura                |
| 3    | Generar slug                  | Negocio                        |
| 4    | Enviar correo electrónico     | Infraestructura / Comunicación |
| 5    | Registrar logs                | Infraestructura                |
| 6    | Indexar en motor de búsqueda  | Infraestructura                |

La clase está actuando simultáneamente como:

* Validador
* Servicio de publicación
* Repositorio
* Generador de URL
* Servicio de notificaciones
* Servicio de logging
* Servicio de indexación

Esto genera una clase con demasiadas razones para cambiar.

---

# Principio SOLID principal violado

## S — Single Responsibility Principle (SRP)

> "Una clase debe tener una única razón para cambiar."

Este es el principio más claramente violado.

Actualmente `GestorLibro` podría cambiar por cualquiera de las siguientes razones:

1. Cambian las reglas de validación.
2. Cambia PostgreSQL.
3. Cambia la generación del slug.
4. Cambia el proveedor SMTP.
5. Cambia la estrategia de logging.
6. Cambia Elasticsearch.
7. Cambia el flujo de publicación.

Eso significa que la clase tiene al menos **6 responsabilidades distintas**.

### Ejemplo

Si mañana OpenLib decide:

* usar Amazon SES en lugar de SMTP,
* migrar de Elasticsearch a OpenSearch,

la clase `GestorLibro` debe modificarse aunque la lógica de publicación del libro siga siendo exactamente la misma.

Eso es una señal clara de violación de SRP.

---

# Otros principios afectados

## O — Open/Closed Principle (OCP)

> "Abierto para extensión, cerrado para modificación."

La clase depende directamente de implementaciones concretas:

```java
new EmailService(...)
new SearchIndex(...)
```

Si se desea:

* cambiar Elasticsearch por Solr,
* cambiar SMTP por SendGrid,

hay que modificar el código existente.

Ejemplo:

```java
SearchIndex index = new SearchIndex(...);
```

debería depender de una abstracción:

```java
SearchService searchService;
```

para permitir nuevas implementaciones sin modificar la lógica de negocio.

---

## D — Dependency Inversion Principle (DIP)

> "Los módulos de alto nivel no deben depender de módulos de bajo nivel."

Actualmente:

```java
DriverManager.getConnection(...)
new EmailService(...)
new SearchIndex(...)
```

La lógica de negocio depende directamente de detalles técnicos.

Debería depender de interfaces:

```java
LibroRepository
EmailNotifier
SearchIndexer
```

y Spring Boot inyectaría las implementaciones.

---

## I — Interface Segregation Principle (ISP)

No se observa una violación explícita porque no aparecen interfaces.

Sin embargo, al refactorizar sería recomendable crear interfaces específicas:

```java
EmailNotifier
SearchIndexer
SlugGenerator
```

en lugar de una interfaz gigante tipo:

```java
OpenLibService
```

---

## L — Liskov Substitution Principle (LSP)

No se puede determinar una violación con el código mostrado porque no existen jerarquías de herencia.

---

# Problemas detectados

## 1. Alto acoplamiento

La clase conoce:

* PostgreSQL
* JDBC
* SMTP
* Elasticsearch
* Formato del slug

Todo está acoplado directamente.

---

## 2. Difícil de probar (testabilidad)

Para probar:

```java
publicarLibro(...)
```

sería necesario:

* una base PostgreSQL funcionando
* un servidor SMTP activo
* Elasticsearch disponible

No es posible realizar pruebas unitarias reales.

---

## 3. Baja cohesión

Una clase cohesionada debería encargarse únicamente del proceso de publicación.

Actualmente realiza tareas que pertenecen a capas distintas:

* dominio
* persistencia
* infraestructura
* búsqueda
* mensajería

---

## 4. Problemas de mantenimiento

Ejemplo:

Si cambia la política de generación de slug:

```java
replace(" ", "-")
```

hay que modificar la misma clase donde también se manejan correos, BD y búsqueda.

---

## 5. Configuración hardcodeada

Muy problemático:

```java
"jdbc:postgresql://localhost:5432/openlib"
"admin"
"pass"

"smtp.openlib.com"
"pass123"

"elasticsearch.openlib.com:9200"
```

En Spring Boot esto debería venir desde:

```properties
application.yml
```

o

```properties
application.properties
```

---

## 6. Ausencia de manejo transaccional

Si ocurre:

```java
guardar libro ✅
correo ❌
```

el sistema queda inconsistente.

En Spring Boot debería utilizarse:

```java
@Transactional
```

para controlar la operación de negocio.

---

# Refactoring propuesto

Separar responsabilidades siguiendo una arquitectura típica Spring Boot.

## Servicio de aplicación

```java
LibroPublicationService
```

Responsable únicamente de coordinar el caso de uso.

---

## Validador

```java
LibroValidator
```

Responsable de validar reglas de negocio.

---

## Repositorio

```java
LibroRepository
```

Persistencia.

Con Spring Data JPA:

```java
JpaRepository<Libro, Long>
```

---

## Generador de Slug

```java
SlugGenerator
```

Responsable únicamente de generar URLs amigables.

---

## Servicio de correo

```java
NotificationService
```

Responsable de notificaciones.

---

## Servicio de indexación

```java
SearchIndexer
```

Responsable de indexar libros.

---

# Beneficios de la solución

### Mayor cohesión

Cada clase tiene una responsabilidad clara.

---

### Menor acoplamiento

La lógica de negocio ya no conoce SMTP, JDBC ni Elasticsearch.

---

### Mejor testabilidad

Podemos hacer:

```java
@Mock
LibroRepository

@Mock
NotificationService

@Mock
SearchIndexer
```

y probar el caso de uso sin infraestructura real.

---

### Más extensible

Si OpenLib migra de Elasticsearch a OpenSearch:

```java
class OpenSearchIndexer implements SearchIndexer
```

No es necesario modificar el servicio principal.

---

### Compatible con Spring Boot 3.x

Uso natural de:

* Inyección de dependencias
* Spring Data JPA
* @Transactional
* Configuración externa
* Observabilidad y logging

---

# Ejemplo de código refactorizado

```java
@Service
@RequiredArgsConstructor
@Transactional
public class LibroPublicationService {

    private final LibroValidator validator;
    private final LibroRepository libroRepository;
    private final SlugGenerator slugGenerator;
    private final NotificationService notificationService;
    private final SearchIndexer searchIndexer;

    public void publicarLibro(Libro libro, Usuario vendedor) {

        validator.validar(libro);

        libro.setSlug(
            slugGenerator.generar(libro.getTitulo())
        );

        libroRepository.save(libro);

        notificationService.notificarLibroPublicado(
            vendedor,
            libro
        );

        searchIndexer.indexar(libro);
    }
}
```

### Validador

```java
@Component
public class LibroValidator {

    public void validar(Libro libro) {

        if (libro.getIsbn() == null ||
            libro.getTitulo() == null ||
            libro.getAutor() == null) {

            throw new IllegalArgumentException(
                "Datos del libro incompletos"
            );
        }
    }
}
```

### Abstracción para correo

```java
public interface NotificationService {

    void notificarLibroPublicado(
        Usuario usuario,
        Libro libro
    );
}
```

### Implementación SMTP

```java
@Service
public class EmailNotificationService
        implements NotificationService {

    @Override
    public void notificarLibroPublicado(
            Usuario usuario,
            Libro libro) {

        // envío de correo
    }
}
```

### Abstracción para indexación

```java
public interface SearchIndexer {

    void indexar(Libro libro);
}
```

### Implementación Elasticsearch

```java
@Service
public class ElasticsearchIndexer
        implements SearchIndexer {

    @Override
    public void indexar(Libro libro) {

        // indexación
    }
}
```

En resumen, la violación principal es **SRP (Single Responsibility Principle)**, mientras que **OCP** y **DIP** también están comprometidos debido al uso de dependencias concretas y la falta de abstracciones. La refactorización mediante servicios especializados e inyección de dependencias permite una solución más mantenible, escalable, testeable y alineada con Java 21 y Spring Boot 3.x.

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

El prompt está bien planteado porque explica el contexto del proyecto y pide analizar la clase paso a paso. También especifica qué aspectos evaluar, como el principio SOLID principal, otros principios afectados y una propuesta de refactorización.

Además, pedir un formato de salida organizado ayuda a que la respuesta sea más clara y completa.

#### 2. ¿Qué se puede mejorar?

La respuesta identifica correctamente que el principal problema es el incumplimiento de SRP, ya que la clase realiza muchas tareas diferentes.

Sin embargo, algunas explicaciones son más largas de lo necesario. También podría relacionar mejor los principios OCP y DIP con ejemplos concretos del código, especialmente las dependencias creadas con new EmailService() y new SearchIndex().


#### 3. Respuesta final

El principio SOLID que más se está violando es SRP (Single Responsibility Principle) porque la clase GestorLibro tiene demasiadas responsabilidades.

En el mismo método se validan datos, se guarda información en la base de datos, se genera el slug, se envían correos, se registran logs y se indexa el libro para búsquedas. Si cualquiera de estas funcionalidades cambia, habría que modificar la misma clase.

También se ven afectados OCP y DIP, ya que la clase depende directamente de implementaciones concretas como EmailService, SearchIndex y DriverManager, lo que dificulta reemplazarlas por otras alternativas.

La mejor solución sería separar cada responsabilidad en clases diferentes, por ejemplo un validador, un repositorio, un servicio de notificaciones y un servicio de indexación. De esta forma la clase principal solo coordinaría el proceso de publicación del libro, logrando un código más fácil de mantener, probar y extender.
