# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [6]: [clean_architecture]

### Estudiante
- **Nombre completo**: Boris Nicolas Valbuena Gueirsman

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Gemini |
| **Modelo específico** | Gemini 3.5 Flash |
| **¿Por qué elegiste este LLM?** | Es la que más usé durante el curso, por lo cual es la misma que usaré en el parcial. |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```
Actúa como un arquitecto de software senior especializado en Clean Architecture, SOLID, Java 21 y Spring Boot 3.x.
Contexto:
Estoy estudiando la arquitectura de OpenLib Market, una plataforma de compra y venta de libros.
Necesito comprender en profundidad Clean Architecture propuesta por Robert C. Martin (Uncle Bob) y cómo aplicarla en este contexto.
Antes de responder:

Explica qué es Clean Architecture y cuáles son sus objetivos.
Describe detalladamente cada una de sus capas.
Explica la regla de dependencia (Dependency Rule).
Explica cómo se aplica el Dependency Inversion Principle (DIP) entre capas.
Muestra ejemplos concretos de OpenLib Market para cada capa.
Compara Clean Architecture con una arquitectura tradicional de tres capas (Presentación → Lógica de Negocio → Datos).
Analiza ventajas, desventajas y casos de uso apropiados.
Restricciones:

Utiliza ejemplos relacionados con OpenLib Market.
Explica los conceptos desde la perspectiva de ingeniería de software.
Evita definiciones superficiales.
Incluye ejemplos de clases o componentes para cada capa.
Justifica cada decisión arquitectónica.
Formato de salida:

¿Qué es Clean Architecture?
Capas de Clean Architecture
Entities
Use Cases
Interface Adapters
Frameworks & Drivers
Regla de Dependencia
Aplicación del Dependency Inversion Principle
Ejemplos en OpenLib Market
Comparación con Arquitectura en Capas Tradicional
Ventajas
Desventajas
Conclusión
Validación:
La respuesta debe explicar claramente la dirección correcta de las dependencias, demostrar cómo se aplica DIP entre capas y mostrar ejemplos coherentes de OpenLib Market para cada nivel de la arquitectura.
```

---

### Respuesta del LLM

> ¡Hola! Excelente iniciativa. Estudiar Clean Architecture (Arquitectura Limpia) sobre un dominio claro como **OpenLib Market** es la mejor forma de pasar de la teoría a la práctica de ingeniería de software avanzada.

Como arquitecto senior, te guiaré a través de este análisis profundo, asegurando que cada concepto se alinee con las capacidades modernas de **Java 21** (como *Records*, *Pattern Matching* y *Sealed Classes*) y el ecosistema de **Spring Boot 3.x**.

---

## ¿Qué es Clean Architecture?

Clean Architecture es un patrón de diseño arquitectónico promovido por Robert C. Martin ("Uncle Bob") cuyo objetivo principal es la **separación de conceptos (Separation of Concerns)** mediante la división del software en capas concéntricas.

### Objetivos Principales:

* **Independencia de Frameworks:** Las herramientas (como Spring Boot) son detalles de implementación. La arquitectura no debe depender de la existencia de una librería externa.
* **Testabilidad:** La lógica de negocio central (reglas de negocio) se puede probar en aislamiento, sin bases de datos, servidores web o cualquier otro elemento externo.
* **Independencia de la Interfaz de Usuario (UI):** La UI puede cambiar de una API REST a una interfaz de línea de comandos (CLI) o GraphQL sin alterar una sola línea de la lógica de negocio.
* **Independencia de la Base de Datos:** Los motores de persistencia (PostgreSQL, MongoDB) están acoplados en los bordes, permitiendo swaps tecnológicos sin dolor.

---

## Capas de Clean Architecture

### Entities (Entidades)

Representan las **reglas de negocio globales y empresariales**. Encapsulan las estructuras de datos y el comportamiento más general y de alto nivel de la aplicación. No conocen absolutamente nada del mundo exterior (ni de persistencia, ni de transporte web). En Java 21, podemos modelar el estado inmutable usando `records` o clases con comportamiento encapsulado rico (Rich Domain Model).

### Use Cases (Casos de Uso)

Contienen las **reglas de negocio específicas de la aplicación**. Esta capa coordina el flujo de datos hacia y desde las entidades. Cambios en esta capa no afectan a las entidades, pero cambios en las reglas de negocio del proceso de la aplicación (ej. *"cómo se procesa una venta de un libro"*) sí impactarán aquí. Se implementan mediante interactores (`Interactors`).

### Interface Adapters (Adaptadores de Interfaz)

Esta capa traduce los datos desde el formato más conveniente para los casos de uso y entidades, al formato más conveniente para componentes externos como la Base de Datos o la Web. Aquí residen los **Controladores REST**, los **Presenters**, y las **Interfaces de los Repositorios** (los puertos).

### Frameworks & Drivers (Marcos de Trabajo y Controladores)

Es la capa más externa y está compuesta por herramientas y tecnologías como **Spring Boot**, motores de bases de datos (Hibernate/JPA), servidores web (Tomcat), y sistemas de mensajería (Kafka). Aquí es donde el código que escribimos se conecta con el "mundo real".

---

## Regla de Dependencia

> **The Dependency Rule:** Las dependencias de código fuente solo pueden apuntar **hacia adentro**.

Nada en un círculo interno puede saber absolutamente nada sobre algo en un círculo externo. Los nombres de las cosas declaradas en un círculo externo no deben ser mencionados por el código en un círculo interno (clases, funciones, variables, o cualquier otra entidad de software).

```
[Frameworks & Drivers] ──> [Interface Adapters] ──> [Use Cases] ──> [Entities]

```

Si violas esta regla (por ejemplo, importando una entidad JPA de Spring en tu caso de uso), habrás acoplado tu núcleo de negocio a la base de datos, destruyendo el propósito de la arquitectura.

---

## Aplicación del Dependency Inversion Principle (DIP)

Para que la regla de dependencia funcione, a menudo el flujo de control va en dirección opuesta a la dependencia de código fuente. Por ejemplo, un Caso de Uso necesita guardar un libro en la base de datos. El flujo de control va del *Caso de Uso* (dentro) a la *Base de Datos* (fuera).

Aquí es donde aplicamos **DIP (Principio de Inversión de Dependencia)**:

1. El Caso de Uso define una interfaz (un **Puerto de Salida** u *Output Port*) en su propio nivel jerárquico.
2. El adaptador de la base de datos (capa externa) implementa esa interfaz.
3. En tiempo de ejecución, mediante la inyección de dependencias de Spring, se provee la implementación real, pero el caso de uso solo conoce la abstracción pura.

---

## Ejemplos en OpenLib Market

Analicemos cómo estructurar el caso de uso: **"Publicar un Libro para la Venta"**.

### 1. Capa: Entities

Modelamos el libro y su precio con lógica de negocio pura.

```java
package com.openlib.market.domain.entity;

import java.math.BigDecimal;
import java.util.UUID;

// Usamos Java 21 Records para inmutabilidad y concisión
public record Book(UUID id, String title, String isbn, Price price, String sellerId) {
    
    public Book {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("El título es requerido");
        if (isbn == null || !isValidISBN(isbn)) throw new IllegalArgumentException("ISBN inválido");
    }

    private static boolean isValidISBN(String isbn) {
        return isbn != null && (isbn.length() == 10 || isbn.length() == 13);
    }
}

record Price(BigDecimal amount, String currency) {
    public Price {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a cero");
        }
    }
}

```

### 2. Capa: Use Cases

Definimos los puertos (interfaces) y el interactor que ejecuta la acción.

```java
package com.openlib.market.application.port.out;
import com.openlib.market.domain.entity.Book;

// Puerto de Salida (Output Port) para Persistencia
public interface BookRepositoryPort {
    Book save(Book book);
    boolean existsByIsbn(String isbn);
}

```

```java
package com.openlib.market.application.usecase;

import com.openlib.market.application.port.out.BookRepositoryPort;
import com.openlib.market.domain.entity.Book;
import java.util.UUID;

// Interactor puro, libre de anotaciones de Spring como @Service
public class PublishBookUseCase {
    
    private final BookRepositoryPort bookRepository;

    public PublishBookUseCase(BookRepositoryPort bookRepository) {
        this.bookRepository = bookRepository;
    }

    // Input DTO (Record de Java 21)
    public record Command(String title, String isbn, java.math.BigDecimal price, String sellerId) {}

    public Book execute(Command command) {
        if (bookRepository.existsByIsbn(command.isbn())) {
            throw new IllegalStateException("El libro con este ISBN ya está publicado");
        }

        Book newBook = new Book(
            UUID.randomUUID(),
            command.title(),
            command.isbn(),
            new com.openlib.market.domain.entity.Price(command.price(), "USD"),
            command.sellerId()
        );

        return bookRepository.save(newBook);
    }
}

```

### 3. Capa: Interface Adapters

Aquí se traduce HTTP a los comandos del caso de uso, y se implementa la interfaz del repositorio.

```java
package com.openlib.market.infrastructure.adapter.in.web;

import com.openlib.market.application.usecase.PublishBookUseCase;
import com.openlib.market.domain.entity.Book;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/books")
public class BookController {

    private final PublishBookUseCase publishBookUseCase;

    public BookController(PublishBookUseCase publishBookUseCase) {
        this.publishBookUseCase = publishBookUseCase;
    }

    @PostMapping
    public ResponseEntity<Book> publishBook(@RequestBody BookRequest request) {
        // Adaptación de entrada
        PublishBookUseCase.Command command = new PublishBookUseCase.Command(
            request.title(), request.isbn(), request.price(), request.sellerId()
        );
        
        Book publishedBook = publishBookUseCase.execute(command);
        return ResponseEntity.ok(publishedBook);
    }
}

record BookRequest(String title, String isbn, java.math.BigDecimal price, String sellerId) {}

```

### 4. Capa: Frameworks & Drivers

Aquí reside la implementación real de la persistencia usando Spring Data JPA.

```java
package com.openlib.market.infrastructure.adapter.out.persistence;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

// Entidad de Infraestructura (Spring Data / Hibernate)
@Entity
@Table(name = "books")
public class BookJpaEntity {
    @Id private UUID id;
    private String title;
    @Column(unique = true) private String isbn;
    private BigDecimal price;
    private String sellerId;
    
    // Getters, Setters, No-args constructor necesarios para Hibernate
}

```

```java
package com.openlib.market.infrastructure.adapter.out.persistence;

import com.openlib.market.application.port.out.BookRepositoryPort;
import com.openlib.market.domain.entity.Book;
import org.springframework.stereotype.Component;

@Component
public class BookPersistenceAdapter implements BookRepositoryPort {

    private final SpringDataBookRepository jpaRepository;

    public BookPersistenceAdapter(SpringDataBookRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Book save(Book book) {
        // Mapeo de Entidad de Dominio a Entidad JPA
        BookJpaEntity jpaEntity = new BookJpaEntity();
        // ... seteo de campos mapeados ...
        BookJpaEntity saved = jpaRepository.save(jpaEntity);
        
        // Retorno mapeando de vuelta al Dominio inmutable
        return new Book(saved.getId(), saved.getTitle(), saved.getIsbn(), ..., saved.getSellerId());
    }

    @Override
    public boolean existsByIsbn(String isbn) {
        return jpaRepository.existsByIsbn(isbn);
    }
}

```

> **Nota de Configuración:** Como el `PublishBookUseCase` no tiene anotaciones de Spring (para mantenerlo "limpio"), debes registrarlo como un Bean manualmente en una clase de `@Configuration` de Spring en la capa de infraestructura.

---

## Comparación con Arquitectura en Capas Tradicional

| Criterio | Arquitectura Tradicional (3 Capas) | Clean Architecture |
| --- | --- | --- |
| **Dirección de Dependencias** | Presentación $\rightarrow$ Lógica $\rightarrow$ **Datos** | Externa $\rightarrow$ [Adaptadores $\rightarrow$ Casos de Uso $\rightarrow$ **Dominio**] |
| **Centro de Gravedad** | La Base de Datos. El modelo relacional dicta el código. | El Dominio. El negocio dicta el software. |
| **Acoplamiento** | Alto. Cambiar Hibernate por Mongo destruye la capa lógica. | Muy bajo. La persistencia es un plugin. |
| **Testabilidad** | Difícil sin mocks pesados de Spring/DB o bases embebidas. | Sencilla. Pruebas unitarias puras con JUnit 5 a la velocidad del rayo. |

---

## Ventajas y Desventajas

### Ventajas

* **Evolución Sostenible:** El software no se degrada con los años. Actualizar de Spring Boot 3.x a versiones futuras o migrar a Quarkus no toca las reglas de negocio.
* **Paralelismo en el desarrollo:** Un equipo puede diseñar los Casos de Uso mientras otro configura la infraestructura de base de datos o mensajería de AWS.
* **Robustez:** Errores en infraestructura no corrompen el flujo lógico central de OpenLib Market.

### Desventajas

* **Overhead de Código:** Requiere la creación de múltiples clases y mapeadores (Mappers) para transformar datos entre capas (ej: de `BookJpaEntity` a `Book`).
* **Curva de Aprendizaje:** Requiere disciplina estricta del equipo. Es fácil romper las reglas por accidente mediante "atajos" de importaciones.
* **Complejidad Inicial:** Para aplicaciones CRUD simples (ej. un catálogo de libros estático), introduce demasiada ingeniería innecesaria.

---

## Conclusión

Para una plataforma con alto potencial de crecimiento como **OpenLib Market**, adoptar Clean Architecture no es un gasto, es una inversión en mantenibilidad. Al aislar las reglas de negocio de los detalles técnicos (como Spring Boot y las bases de datos relacionales), garantizas que el Core del negocio sea altamente testeable, agnóstico a las tendencias tecnológicas y preparado para el cambio arquitectónico.
```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

[Evalúa tu propio prompt, no la respuesta del LLM. ¿El contexto fue suficiente? ¿Las restricciones fueron claras? ¿El formato de salida que pediste ayudó a obtener una buena respuesta? ¿Qué parte de tu prompt fue más efectiva? Sé específico: menciona fragmentos concretos de tu prompt que funcionaron bien.]


#### 2. ¿Qué se puede mejorar?

[¿Qué le faltó a tu prompt? ¿Qué harías diferente si pudieras reformularlo? ¿El LLM entendió mal algo por falta de claridad en tu prompt? ¿La respuesta tiene errores u omisiones? ¿Qué no cubrió el LLM que tú sí sabes por lo visto en clase?]


#### 3. Respuesta final

[Escribe tu respuesta definitiva a la pregunta del parcial, integrando lo que aprendiste del LLM pero yendo más allá. Corrige errores, llena omisiones, conecta con conceptos vistos en clase. Esta es tu respuesta: demuestra que tú dominas el tema.]
