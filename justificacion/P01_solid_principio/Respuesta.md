
## Pregunta [1]: [Identificar el principio violado en una clase y proponer refactoring]

### Estudiante
- **Nombre completo**: Julian Felipe Rojas Almanza

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Claude |
| **Modelo específico** |  Claude sonnet 4.6 |
| **¿Por qué elegiste este LLM?** | Es uno de los modelos con mejor razonamiento conceptual y abstracto para el análisis de código, la detección de violaciones de principios de diseño (SOLID) y la reestructuración de arquitectura limpia en Java.|

---

### Prompt utilizado


```

Actúa como un Arquitecto de Software Senior y revisor de código experimentado. Estoy resolviendo un ejercicio técnico académico basado en el proyecto "OpenLib Market" (módulo de administración) y necesito que respondas exactamente a las tareas solicitadas.

[CONTEXTO]
El siguiente método pertenece a la clase `GestorLibro`:

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

[PROBLEMA / TAREA]
Necesito que resuelvas y respondas de forma directa y explícita a los siguientes dos puntos requeridos por mi enunciado:
1. Identifica qué principio SOLID se está violando principalmente en esta clase y justifica detalladamente por qué.
2. Propón un refactoring completo paso a paso que aplique ese principio para solucionar el problema.

[RESTRICCIONES]
- Chain of Thought: Antes de escribir el código final, explica tu razonamiento conceptual detallando qué responsabilidades de infraestructura deben ser separadas.
- El refactoring debe remover el acoplamiento rígido, eliminando el uso de 'new' para servicios externos y evitando credenciales quemadas (hardcoded) en el código.


[FORMATO DE SALIDA]
Estructura tu respuesta en Markdown utilizando única y estrictamente estos tres encabezados para organizar la información:
- ### 1. Identificación del Principio SOLID Violado
- ### 2. Propuesta de Refactoring Paso a Paso
- ### 3. Código Java Refactorizado

[VALIDACIÓN]
Sabré que tu respuesta es excelente si respondes con precisión a las dos preguntas del enunciado, si el principio identificado coincide con la acumulación de múltiples responsabilidades del método original, y si el código final delega las tareas en componentes independientes listos para pruebas unitarias.

```

---

### Respuesta del LLM

# Refactoring SOLID — OpenLib Market
**Arquitecto de Software Senior · Revisión de código**
*Java 21 · Spring Boot 3.x · PostgreSQL 16*

---

## 1. Identificación del Principio SOLID Violado

**El principio violado es SRP — Single Responsibility Principle (Principio de Responsabilidad Única).**

> *"Una clase debe tener una, y solo una, razón para cambiar."* — Robert C. Martin

### Diagnóstico detallado

El método `publicarLibro` en `GestorLibro` tiene **seis responsabilidades distintas**, cada una con su propia razón de cambio independiente:

| # | Responsabilidad | Razón de cambio independiente |
|---|---|---|
| 1 | Validación de datos del libro | Cambia si las reglas de negocio de validación evolucionan |
| 2 | Persistencia en base de datos | Cambia si migras de JDBC puro a JPA, o cambias el esquema |
| 3 | Generación de slug para URL | Cambia si se adopta otra convención de URLs o se internacionaliza |
| 4 | Envío de correo electrónico | Cambia si se cambia el proveedor SMTP o la plantilla del correo |
| 5 | Registro en log | Cambia si se adopta otra estrategia de observabilidad |
| 6 | Indexación para búsqueda | Cambia si se migra de Elasticsearch a OpenSearch o Solr |

### Agravantes adicionales que rompen también OCP, DIP y testabilidad

- **Acoplamiento rígido con `new`:** `EmailService`, `SearchIndex` y `Connection` se instancian directamente dentro del método. No se pueden sustituir por dobles de prueba (mocks), haciendo el método completamente no testeable de forma unitaria.
- **Credenciales hardcoded:** La URL de la base de datos, el host SMTP con su contraseña y la URL de Elasticsearch están quemadas en el código fuente. Cualquier cambio de entorno obliga a recompilar.
- **Violación de DIP:** La clase de alto nivel (`GestorLibro`) depende directamente de implementaciones concretas de bajo nivel (`DriverManager`, `EmailService`, `SearchIndex`), en lugar de depender de abstracciones.

La consecuencia práctica es que si el equipo de infraestructura cambia el servidor de correo, un desarrollador de negocio tiene que tocar la clase `GestorLibro`, lo cual es exactamente la situación que SRP prohíbe.

---

## 2. Propuesta de Refactoring Paso a Paso

### Razonamiento conceptual: qué responsabilidades deben separarse

Antes de escribir código, hay que identificar las **fronteras naturales de responsabilidad** del sistema:

**Frontera 1 — Lógica de dominio / negocio**
La validación y la generación del slug son reglas de negocio puras. No dependen de ninguna infraestructura. Deben vivir cerca del dominio, en un servicio de aplicación o en el propio objeto `Libro`.

**Frontera 2 — Infraestructura de persistencia**
La escritura en base de datos es responsabilidad exclusiva de un repositorio. En Spring Boot 3.x, esto se delega a una interfaz que extiende `JpaRepository` o `CrudRepository`. La clase de negocio no debe saber nada de JDBC, SQL ni conexiones.

**Frontera 3 — Infraestructura de mensajería (correo)**
El envío de correo es un efecto secundario de infraestructura. Debe encapsularse en un puerto (interfaz) que el servicio de negocio consuma, y cuya implementación concreta (`SmtpNotificacionService`) sea inyectada por Spring.

**Frontera 4 — Infraestructura de búsqueda**
La indexación es otra responsabilidad de infraestructura, análoga al correo. Mismo patrón: interfaz + implementación concreta inyectada.

**Frontera 5 — Logging**
El logging transversal se gestiona mediante la anotación `@Slf4j` de Lombok (o un `Logger` estático estándar). No requiere una clase separada, pero sí debe salir del cuerpo del método como instanciación manual.

### Pasos del refactoring

**Paso 1:** Crear la interfaz `LibroRepository` y dejar que Spring Data JPA la implemente.

**Paso 2:** Crear la interfaz `NotificacionService` con un método `notificarPublicacion`, y su implementación `SmtpNotificacionService`. Las credenciales SMTP se inyectan desde `application.properties` con `@Value`.

**Paso 3:** Crear la interfaz `IndiceLibroService` con un método `indexar`, y su implementación `ElasticsearchIndiceService`. El host se inyecta desde configuración.

**Paso 4:** Extraer la lógica de slug a un método privado puro dentro de `GestorLibro` (es lógica de negocio liviana que puede vivir ahí) o a una clase utilitaria `SlugUtils`.

**Paso 5:** Reescribir `GestorLibro` como `@Service` de Spring, recibiendo todas las dependencias por constructor. El método `publicarLibro` queda reducido a orquestar el flujo de negocio.

**Paso 6:** Verificar que cada clase resultante tiene exactamente una razón para cambiar y puede ser probada de forma unitaria con Mockito.

---

## 3. Código Java Refactorizado

### `Libro.java` — Entidad de dominio

```java
package com.openlib.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "libros")
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String isbn;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String autor;

    @Column(unique = true)
    private String slug;

    // Getters y setters omitidos por brevedad (usar Lombok @Data en proyecto real)
    public String getIsbn()   { return isbn; }
    public String getTitulo() { return titulo; }
    public String getAutor()  { return autor; }
    public void   setSlug(String slug) { this.slug = slug; }
}
```

### `Usuario.java` — Entidad de dominio

```java
package com.openlib.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String email;

    public String getNombre() { return nombre; }
    public String getEmail()  { return email; }
}
```

### `LibroRepository.java` — Puerto de persistencia

```java
package com.openlib.domain.port;

import com.openlib.domain.Libro;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Contrato de persistencia para Libro.
 * Spring Data JPA genera la implementación en tiempo de ejecución.
 * GestorLibro depende de esta interfaz, nunca de JDBC directamente.
 */
public interface LibroRepository extends JpaRepository<Libro, Long> {

    boolean existsByIsbn(String isbn);
}
```

### `NotificacionService.java` — Puerto de notificaciones

```java
package com.openlib.domain.port;

import com.openlib.domain.Libro;
import com.openlib.domain.Usuario;

/**
 * Abstracción de notificaciones.
 * GestorLibro depende de este contrato, ignorando si el canal
 * es SMTP, SendGrid, SES u otro.
 */
public interface NotificacionService {

    void notificarPublicacion(Usuario vendedor, Libro libro);
}
```

### `IndiceLibroService.java` — Puerto de indexación

```java
package com.openlib.domain.port;

import com.openlib.domain.Libro;

/**
 * Abstracción del motor de búsqueda.
 * La implementación concreta (Elasticsearch, OpenSearch, etc.)
 * se intercambia sin tocar la lógica de negocio.
 */
public interface IndiceLibroService {

    void indexar(Libro libro);
}
```

### `SmtpNotificacionService.java` — Adaptador SMTP

```java
package com.openlib.infrastructure.email;

import com.openlib.domain.Libro;
import com.openlib.domain.Usuario;
import com.openlib.domain.port.NotificacionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Implementación SMTP del contrato NotificacionService.
 * Las credenciales viven en application.properties / variables de entorno,
 * nunca en el código fuente.
 */
@Slf4j
@Service
public class SmtpNotificacionService implements NotificacionService {

    private final JavaMailSender mailSender;
    private final String remitente;

    /**
     * Inyección por constructor: todas las dependencias son explícitas,
     * inmutables y fácilmente sustituibles en tests con Mockito.
     *
     * @param mailSender  Bean configurado por Spring Boot a partir de
     *                    spring.mail.* en application.properties
     * @param remitente   Dirección "from" leída desde configuración
     */
    public SmtpNotificacionService(
            JavaMailSender mailSender,
            @Value("${openlib.mail.remitente}") String remitente) {
        this.mailSender = mailSender;
        this.remitente  = remitente;
    }

    @Override
    public void notificarPublicacion(Usuario vendedor, Libro libro) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(remitente);
        mensaje.setTo(vendedor.getEmail());
        mensaje.setSubject("Libro publicado");
        mensaje.setText(
            "Tu libro '" + libro.getTitulo() + "' ha sido publicado exitosamente."
        );
        mailSender.send(mensaje);
        log.info("Notificación enviada a {} para libro ISBN={}",
                 vendedor.getEmail(), libro.getIsbn());
    }
}
```

### `ElasticsearchIndiceService.java` — Adaptador de indexación

```java
package com.openlib.infrastructure.search;

import com.openlib.domain.Libro;
import com.openlib.domain.port.IndiceLibroService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/**
 * Adaptador de Elasticsearch para el contrato IndiceLibroService.
 * El host se inyecta desde configuración, sin credenciales en código.
 */
@Slf4j
@Service
public class ElasticsearchIndiceService implements IndiceLibroService {

    private final RestClient restClient;

    public ElasticsearchIndiceService(
            @Value("${openlib.search.host}") String searchHost) {
        this.restClient = RestClient.builder()
                .baseUrl("http://" + searchHost)
                .build();
    }

    @Override
    public void indexar(Libro libro) {
        // En producción se usaría el cliente oficial de Elasticsearch para Java.
        // Aquí se muestra el patrón de inyección de configuración.
        log.info("Indexando libro ISBN={} en búsqueda", libro.getIsbn());
        restClient.put()
                .uri("/libros/_doc/{isbn}", libro.getIsbn())
                .body(libro)
                .retrieve()
                .toBodilessEntity();
    }
}
```

### `SlugUtils.java` — Utilidad de dominio pura

```java
package com.openlib.domain.util;

/**
 * Lógica de generación de slug extraída a una clase utilitaria pura.
 * Sin dependencias externas: 100% testeable con JUnit sin contexto de Spring.
 */
public final class SlugUtils {

    private SlugUtils() { /* clase utilitaria, no instanciable */ }

    /**
     * Convierte un título en un slug apto para URL.
     * Ejemplo: "El Señor de los Anillos" → "el-senor-de-los-anillos"
     */
    public static String generarSlug(String titulo) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("El título no puede ser nulo o vacío");
        }
        return titulo.trim()
                     .toLowerCase()
                     .replaceAll("[áàä]", "a")
                     .replaceAll("[éèë]", "e")
                     .replaceAll("[íìï]", "i")
                     .replaceAll("[óòö]", "o")
                     .replaceAll("[úùü]", "u")
                     .replaceAll("[ñ]",   "n")
                     .replace(" ", "-")
                     .replaceAll("[^a-z0-9-]", "");
    }
}
```

### `GestorLibro.java` — Servicio de aplicación refactorizado

```java
package com.openlib.application;

import com.openlib.domain.Libro;
import com.openlib.domain.Usuario;
import com.openlib.domain.port.IndiceLibroService;
import com.openlib.domain.port.LibroRepository;
import com.openlib.domain.port.NotificacionService;
import com.openlib.domain.util.SlugUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio de aplicación con responsabilidad única:
 * orquestar el flujo de publicación de un libro.
 *
 * Esta clase tiene exactamente UNA razón para cambiar:
 * si el flujo de negocio de publicación cambia.
 *
 * No sabe nada de JDBC, SMTP, Elasticsearch ni credenciales.
 */
@Slf4j
@Service
public class GestorLibro {

    private final LibroRepository     libroRepository;
    private final NotificacionService notificacionService;
    private final IndiceLibroService  indiceLibroService;

    /**
     * Inyección por constructor: obligatoria, inmutable y completamente
     * mockeable en tests sin levantar contexto de Spring.
     */
    public GestorLibro(
            LibroRepository     libroRepository,
            NotificacionService notificacionService,
            IndiceLibroService  indiceLibroService) {
        this.libroRepository     = libroRepository;
        this.notificacionService = notificacionService;
        this.indiceLibroService  = indiceLibroService;
    }

    /**
     * Publica un libro en el sistema:
     * valida, persiste, genera slug, notifica e indexa.
     *
     * @param libro    Libro a publicar; debe tener ISBN, título y autor.
     * @param vendedor Usuario propietario del libro.
     * @throws IllegalArgumentException si faltan datos obligatorios del libro.
     */
    @Transactional
    public void publicarLibro(Libro libro, Usuario vendedor) {

        // 1. Validar datos mínimos del libro
        validarLibro(libro);

        // 2. Generar slug antes de persistir
        libro.setSlug(SlugUtils.generarSlug(libro.getTitulo()));

        // 3. Persistir — GestorLibro no sabe si es JPA, JDBC u otro
        libroRepository.save(libro);

        // 4. Notificar al vendedor — GestorLibro no sabe si es SMTP, push, etc.
        notificacionService.notificarPublicacion(vendedor, libro);

        // 5. Indexar para búsqueda — GestorLibro no sabe si es Elasticsearch u otro
        indiceLibroService.indexar(libro);

        // 6. Registrar evento de negocio (logging transversal, no instanciación manual)
        log.info("Libro publicado: ISBN={} | Título='{}' | Vendedor={}",
                 libro.getIsbn(), libro.getTitulo(), vendedor.getNombre());
    }

    /**
     * Validación de invariantes del libro.
     * Método privado puro: sin efectos secundarios, 100% testeable.
     */
    private void validarLibro(Libro libro) {
        if (libro == null) {
            throw new IllegalArgumentException("El libro no puede ser nulo");
        }
        if (libro.getIsbn() == null || libro.getIsbn().isBlank()) {
            throw new IllegalArgumentException("El ISBN del libro es obligatorio");
        }
        if (libro.getTitulo() == null || libro.getTitulo().isBlank()) {
            throw new IllegalArgumentException("El título del libro es obligatorio");
        }
        if (libro.getAutor() == null || libro.getAutor().isBlank()) {
            throw new IllegalArgumentException("El autor del libro es obligatorio");
        }
    }
}
```

### `GestorLibroTest.java` — Test unitario que demuestra la testabilidad

```java
package com.openlib.application;

import com.openlib.domain.Libro;
import com.openlib.domain.Usuario;
import com.openlib.domain.port.IndiceLibroService;
import com.openlib.domain.port.LibroRepository;
import com.openlib.domain.port.NotificacionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test unitario puro: sin contexto de Spring, sin base de datos,
 * sin SMTP, sin Elasticsearch. Posible gracias a la inyección por
 * constructor y la separación de responsabilidades.
 */
@ExtendWith(MockitoExtension.class)
class GestorLibroTest {

    @Mock LibroRepository     libroRepository;
    @Mock NotificacionService notificacionService;
    @Mock IndiceLibroService  indiceLibroService;

    @InjectMocks
    GestorLibro gestorLibro;

    private Libro   libro;
    private Usuario vendedor;

    @BeforeEach
    void setUp() {
        libro = new Libro();
        libro.setIsbn("978-3-16-148410-0");
        libro.setTitulo("El Señor de los Anillos");
        libro.setAutor("J.R.R. Tolkien");

        vendedor = new Usuario();
        vendedor.setNombre("Juan Vendedor");
        vendedor.setEmail("juan@openlib.com");
    }

    @Test
    @DisplayName("publicarLibro: flujo feliz delega en todos los servicios")
    void publicarLibro_flujoCorrecto_delegaEnServiciosYPersiste() {
        gestorLibro.publicarLibro(libro, vendedor);

        verify(libroRepository,     times(1)).save(libro);
        verify(notificacionService, times(1)).notificarPublicacion(vendedor, libro);
        verify(indiceLibroService,  times(1)).indexar(libro);
    }

    @Test
    @DisplayName("publicarLibro: genera slug correctamente antes de persistir")
    void publicarLibro_generaSlugEsperado() {
        gestorLibro.publicarLibro(libro, vendedor);

        assertEquals("el-senor-de-los-anillos", libro.getSlug());
    }

    @Test
    @DisplayName("publicarLibro: lanza excepción si el ISBN es nulo")
    void publicarLibro_sinIsbn_lanzaExcepcion() {
        libro.setIsbn(null);

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> gestorLibro.publicarLibro(libro, vendedor)
        );
        assertEquals("El ISBN del libro es obligatorio", ex.getMessage());

        // Ningún servicio de infraestructura debe ser llamado si la validación falla
        verifyNoInteractions(libroRepository, notificacionService, indiceLibroService);
    }

    @Test
    @DisplayName("publicarLibro: lanza excepción si el título es nulo")
    void publicarLibro_sinTitulo_lanzaExcepcion() {
        libro.setTitulo(null);

        assertThrows(IllegalArgumentException.class,
                     () -> gestorLibro.publicarLibro(libro, vendedor));
        verifyNoInteractions(libroRepository, notificacionService, indiceLibroService);
    }
}
```

### `application.properties` — Configuración externalizada

```properties
# Base de datos — nunca en el código fuente
spring.datasource.url=jdbc:postgresql://localhost:5432/openlib
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASS}
spring.jpa.hibernate.ddl-auto=validate

# SMTP — nunca en el código fuente
spring.mail.host=${SMTP_HOST}
spring.mail.port=${SMTP_PORT}
spring.mail.username=${SMTP_USER}
spring.mail.password=${SMTP_PASS}
openlib.mail.remitente=noreply@openlib.com

# Elasticsearch — nunca en el código fuente
openlib.search.host=${SEARCH_HOST:elasticsearch.openlib.com:9200}
```

---

## Diagrama de la arquitectura resultante

```
┌────────────────────────────────────────────────────────────────┐
│                     application/                               │
│  GestorLibro  ──depende de──►  LibroRepository (interfaz)     │
│  @Service          │         NotificacionService (interfaz)    │
│  1 sola razón      │         IndiceLibroService (interfaz)     │
│  para cambiar      │                                           │
└────────────────────┼───────────────────────────────────────────┘
                     │ Spring inyecta implementaciones concretas
┌────────────────────▼───────────────────────────────────────────┐
│                  infrastructure/                               │
│  JpaLibroRepository      ←── Spring Data JPA                  │
│  SmtpNotificacionService ←── JavaMailSender + @Value          │
│  ElasticsearchIndiceService ←── RestClient + @Value           │
└────────────────────────────────────────────────────────────────┘
```

## Razón de cambio de cada clase tras el refactoring

| Clase | Única razón para cambiar |
|---|---|
| `GestorLibro` | El flujo de negocio de publicación cambia |
| `SmtpNotificacionService` | El mecanismo de notificación cambia |
| `ElasticsearchIndiceService` | El motor de búsqueda cambia |
| `LibroRepository` | El contrato de persistencia del dominio cambia |
| `SlugUtils` | La convención de generación de URLs cambia |



```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

Considero que se realizo un buen prompt de manera general, ya que se cumplieron las recomendaciones encontradas en el md guia_prompting, dando contexto, problema, tarea, restricciones, usando la IA recomendada y buscando validar.
Toca tener en cuenta que de ahora en adelante como se hize en este prompt se genero una gema (Similar a un agente pero en la inteligencia artificial de Gemini) El cual se usa para mejorar el prompt, ha este se le dio el md guia de prompting. Por utlimo se le da una ultima revision desde mi parte.

#### 2. ¿Qué se puede mejorar?

La extension de la respeuesta fue supremamente larga, se puede pedir en el pormpt que sea mas consizo, ademas se uso mucho lenguage tecnico en la respuesta. Se puede pedir que de las respuestas en un lenguage mas cotidiano y facil de entender.


#### 3. Respuesta final


