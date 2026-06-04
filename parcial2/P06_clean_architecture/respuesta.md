# Plantilla de entrega — Parcial 2

## Pregunta [06]: [clean architecture]


### Estudiante

* **Nombre completo**: [Marlon Garcia]

---

### LLM utilizado

| Campo                           | Valor                                                                                                                                                                                                                                                                                    |
| ------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Nombre del LLM**              | ChatGPT                                                                                                                                                                                                                                                                                  |
| **Modelo específico**           | GPT-5.5 Thinking                                                                                                                                                                                                                                                                         |
| **¿Por qué elegiste este LLM?** | Elegí este LLM porque permite explicar conceptos de arquitectura de software de forma estructurada y también aterrizarlos a ejemplos concretos en Java y en el contexto de OpenLib Market. Además, ayuda a comparar enfoques arquitectónicos y a detectar posibles errores conceptuales. |

---

### Prompt utilizado

```
Estoy trabajando en el proyecto OpenLib Market, que es un marketplace de libros, y necesito entender cómo aplicar Clean Architecture de Robert C. Martin en este contexto.

Explícame de forma clara qué es Clean Architecture, cuáles son sus capas principales: Entities, Use Cases, Interface Adapters y Frameworks/Drivers. También necesito que expliques la regla de dependencia, especialmente eso de que las dependencias solo deben apuntar hacia adentro.

Incluye también cómo se aplica el principio de inversión de dependencia entre capas. Por ejemplo, si un caso de uso necesita guardar un libro, enviar una notificación, consultar Redis o conectarse a una base de datos, ¿cómo se evita que dependa directamente de PostgreSQL, Redis, SMTP o Spring Boot?

Finalmente, compárala con una arquitectura tradicional en capas tipo presentación → lógica de negocio → datos, explicando diferencias, ventajas y riesgos. Usa ejemplos concretos de OpenLib Market para decir qué clases o componentes irían en cada capa.

Responde en Markdown, con secciones claras y ejemplos concretos.
```

---

### Respuesta del LLM

Clean Architecture es una propuesta de organización de software creada por Robert C. Martin. Su objetivo principal es separar la lógica de negocio importante de los detalles técnicos externos, como frameworks, bases de datos, interfaces gráficas o servicios de terceros.

La idea central es que el sistema se organiza en capas concéntricas. Las reglas más importantes y estables quedan en el centro, mientras que los detalles técnicos quedan hacia afuera.

La regla fundamental es:

**Las dependencias solo pueden apuntar hacia adentro.**

Esto significa que las capas externas pueden conocer y depender de las capas internas, pero las capas internas no deben depender de las externas.

---

## Capas de Clean Architecture

### 1. Entities

Las entidades están en el centro de la arquitectura. Representan las reglas de negocio más generales y estables del sistema.

En OpenLib Market, aquí podrían estar clases como:

```java
class Libro {
    private String isbn;
    private String titulo;
    private String autor;
    private boolean disponible;

    public boolean estaDisponible() {
        return disponible;
    }
}
```

Otros ejemplos de entidades serían:

```text
Usuario
Carrito
Pedido
Pago
Comentario
Descuento
```

Estas clases no deberían depender de bases de datos, frameworks, controladores, servicios externos ni librerías específicas. Por ejemplo, `Libro` no debe saber si se guarda en PostgreSQL, MongoDB o un archivo.

---

### 2. Use Cases

Los casos de uso representan las acciones específicas que el sistema puede realizar. Contienen lógica de aplicación.

En OpenLib Market, algunos casos de uso serían:

```text
PublicarLibroUseCase
ComprarLibroUseCase
AgregarLibroAWishlistUseCase
ProcesarPagoUseCase
NotificarLibroDisponibleUseCase
CalcularPrecioFinalUseCase
```

Por ejemplo, `PublicarLibroUseCase` podría validar que un libro tenga los datos mínimos, generar su slug y pedir que se guarde.

Sin embargo, el caso de uso no debería depender directamente de PostgreSQL o Spring Data. En vez de eso, depende de una interfaz:

```java
interface LibroRepository {
    void guardar(Libro libro);
}
```

Entonces el caso de uso queda así:

```java
class PublicarLibroUseCase {
    private final LibroRepository libroRepository;

    public PublicarLibroUseCase(LibroRepository libroRepository) {
        this.libroRepository = libroRepository;
    }

    public void ejecutar(Libro libro) {
        libroRepository.guardar(libro);
    }
}
```

---

### 3. Interface Adapters

Los adaptadores de interfaz traducen información entre el mundo externo y los casos de uso.

Aquí estarían componentes como:

```text
LibroController
PagoController
PedidoController
LibroDTO
UsuarioDTO
PostgresLibroRepository
EmailNotificationAdapter
RedisCacheAdapter
```

Un controlador recibe una petición HTTP y la adapta para llamar al caso de uso:

```java
class LibroController {
    private final PublicarLibroUseCase publicarLibroUseCase;

    public LibroController(PublicarLibroUseCase publicarLibroUseCase) {
        this.publicarLibroUseCase = publicarLibroUseCase;
    }

    public void publicarLibro(LibroRequest request) {
        Libro libro = new Libro(request.getIsbn(), request.getTitulo(), request.getAutor());
        publicarLibroUseCase.ejecutar(libro);
    }
}
```

También están los adaptadores que implementan las interfaces requeridas por los casos de uso:

```java
class PostgresLibroRepository implements LibroRepository {
    @Override
    public void guardar(Libro libro) {
        // Código concreto para guardar en PostgreSQL
    }
}
```

---

### 4. Frameworks and Drivers

Esta es la capa más externa. Contiene detalles técnicos y herramientas concretas.

En OpenLib Market, aquí estarían:

```text
Spring Boot
PostgreSQL
Redis
Elasticsearch
SMTP
PayPal SDK
PSE API
React
Aplicación móvil
Servidor web
```

Estos elementos son necesarios para que el sistema funcione, pero no deberían dominar la lógica central.

La lógica de negocio no debería estar escrita pensando directamente en Spring Boot, PostgreSQL o Redis. Esos son detalles reemplazables.

---

## Regla de dependencia

La regla de dependencia dice que las dependencias del código fuente deben ir hacia adentro.

Una forma de verlo sería:

```text
Frameworks/Drivers → Interface Adapters → Use Cases → Entities
```

Esto quiere decir:

* Los controladores pueden depender de los casos de uso.
* Los casos de uso pueden depender de las entidades.
* Las entidades no dependen de nadie.
* Los casos de uso no deben depender directamente de frameworks ni bases de datos.
* Las capas externas se adaptan a las internas, no al revés.

Por ejemplo, no sería correcto que una entidad `Libro` tuviera anotaciones o lógica dependiente directamente de una base de datos externa. Tampoco sería ideal que un caso de uso dependiera directamente de `PostgresLibroRepository`.

---

## Inversión de dependencia entre capas

La inversión de dependencia permite que una capa interna necesite una operación externa sin depender directamente de la tecnología concreta.

Por ejemplo, si un caso de uso necesita enviar un correo cuando un libro vuelve a estar disponible, no debería depender de una clase concreta como `SmtpEmailService`.

Incorrecto:

```java
class NotificarLibroDisponibleUseCase {
    private SmtpEmailService emailService;
}
```

Correcto:

```java
interface NotificadorUsuarios {
    void notificarLibroDisponible(Libro libro);
}
```

El caso de uso depende de la abstracción:

```java
class NotificarLibroDisponibleUseCase {
    private final NotificadorUsuarios notificadorUsuarios;

    public NotificarLibroDisponibleUseCase(NotificadorUsuarios notificadorUsuarios) {
        this.notificadorUsuarios = notificadorUsuarios;
    }

    public void ejecutar(Libro libro) {
        notificadorUsuarios.notificarLibroDisponible(libro);
    }
}
```

Y la implementación concreta queda afuera:

```java
class SmtpNotificadorUsuarios implements NotificadorUsuarios {
    @Override
    public void notificarLibroDisponible(Libro libro) {
        // Enviar correo usando SMTP
    }
}
```

Así, el caso de uso no depende de SMTP. SMTP depende de la interfaz que necesita el caso de uso.

Eso es inversión de dependencia aplicada en los límites de la arquitectura.

---

## Comparación con arquitectura tradicional por capas

Una arquitectura tradicional suele organizarse así:

```text
Presentación → Lógica de negocio → Datos
```

Por ejemplo:

```text
Controller → Service → Repository → Database
```

Esta arquitectura es sencilla y común, pero puede generar un problema: la lógica de negocio suele terminar dependiendo directamente de la capa de datos o del framework.

Ejemplo:

```java
class LibroService {
    private PostgresLibroRepository repository;
}
```

En este caso, si se cambia PostgreSQL por otra tecnología, probablemente se tenga que tocar la lógica de negocio.

En Clean Architecture, la diferencia es que el caso de uso no depende del repositorio concreto. Depende de una interfaz. La implementación concreta queda en una capa externa.

La comparación sería:

```text
Arquitectura tradicional:
La lógica de negocio puede depender directamente de la capa de datos.

Clean Architecture:
La capa de datos depende de las interfaces definidas por la lógica de negocio.
```

Por eso Clean Architecture protege mejor el núcleo del sistema.

---

## Ejemplos aplicados a OpenLib Market

### Entities

```text
Libro
Usuario
Carrito
Pedido
Pago
Comentario
Descuento
```

Aquí van las reglas principales del dominio.

---

### Use Cases

```text
PublicarLibroUseCase
ComprarLibroUseCase
ProcesarPagoUseCase
AgregarLibroAWishlistUseCase
NotificarLibroDisponibleUseCase
CalcularPrecioFinalUseCase
ModerarComentarioUseCase
```

Aquí van acciones específicas que el sistema ejecuta.

---

### Interface Adapters

```text
LibroController
PedidoController
PagoController
LibroDTO
UsuarioDTO
PostgresLibroRepository
RedisCacheAdapter
EmailNotificationAdapter
PayPalPaymentAdapter
```

Aquí se adaptan datos externos hacia los casos de uso y se implementan interfaces necesarias para comunicarse con el exterior.

---

### Frameworks and Drivers

```text
Spring Boot
PostgreSQL
Redis
Elasticsearch
SMTP
PayPal SDK
PSE API
React
Aplicación móvil
```

Aquí van los detalles técnicos externos.

---

## Conclusión

Clean Architecture permite que OpenLib Market tenga una lógica de negocio independiente de los detalles técnicos. Las entidades y casos de uso quedan protegidos de cambios en frameworks, bases de datos o servicios externos.

La regla más importante es que las dependencias apuntan hacia adentro. Esto se logra aplicando inversión de dependencia: las capas internas definen interfaces y las capas externas implementan esas interfaces.

Esto hace que el sistema sea más fácil de mantener, probar y modificar.

Iteraciones realizadas: 1.

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

El prompt dio suficiente contexto porque especificó que el sistema era **OpenLib Market**, un marketplace de libros. Eso ayudó a que el LLM no respondiera de forma genérica, sino con ejemplos como `Libro`, `Carrito`, `Pedido`, `PublicarLibroUseCase` y `ProcesarPagoUseCase`.

También fue útil pedir explícitamente las capas de Clean Architecture: **Entities, Use Cases, Interface Adapters y Frameworks/Drivers**. Esa parte evitó que la respuesta omitiera alguna capa importante.

Otro punto efectivo fue pedir que explicara la regla de dependencia y la inversión de dependencia con ejemplos concretos como PostgreSQL, Redis, SMTP y Spring Boot. Ese fragmento del prompt funcionó bien porque obligó al LLM a mostrar cómo una capa interna puede necesitar una operación externa sin depender directamente de una tecnología concreta.

Finalmente, pedir la comparación con una arquitectura tradicional **presentación → lógica de negocio → datos** ayudó a que la respuesta conectara Clean Architecture con un modelo más conocido.

---

#### 2. ¿Qué se puede mejorar?

El prompt pudo ser más específico en pedir un ejemplo de estructura de paquetes, por ejemplo:

```text
domain/
application/
infrastructure/
interfaces/
```

Eso habría ayudado a visualizar mejor cómo organizar el código en un proyecto real.

También pude pedir que diferenciara con más claridad entre **entidades de dominio** y **entidades de persistencia**, porque en proyectos con frameworks como JPA es común confundir una entidad del dominio con una clase anotada con `@Entity`.

La respuesta fue correcta en general, pero se podría profundizar más en que las interfaces como `LibroRepository` deberían estar del lado interno, cerca de los casos de uso, y que las implementaciones concretas como `PostgresLibroRepository` pertenecen a una capa externa.

También se pudo pedir una comparación más matizada con arquitectura tradicional, porque no toda arquitectura por capas está mal diseñada. El problema aparece cuando las capas internas quedan acopladas a detalles externos o cuando la capa de datos domina el diseño.

---

#### 3. Respuesta final

Clean Architecture es una arquitectura propuesta por Robert C. Martin que organiza el sistema en capas concéntricas. Su objetivo es proteger la lógica de negocio de los detalles externos como frameworks, bases de datos, APIs, interfaces gráficas o servicios de terceros.

La regla central es que **las dependencias solo pueden apuntar hacia adentro**. Esto significa que las capas externas pueden depender de las capas internas, pero las internas no deben conocer ni depender de las externas.

La estructura general es:

```text
Frameworks/Drivers → Interface Adapters → Use Cases → Entities
```

Las **Entities** son el núcleo del sistema. En OpenLib Market, aquí estarían clases como `Libro`, `Usuario`, `Carrito`, `Pedido`, `Pago`, `Comentario` o `Descuento`. Estas clases contienen reglas principales del dominio y no deberían depender de Spring Boot, PostgreSQL, Redis ni ningún framework.

Los **Use Cases** representan acciones concretas del sistema, como `PublicarLibroUseCase`, `ComprarLibroUseCase`, `ProcesarPagoUseCase`, `AgregarLibroAWishlistUseCase`, `NotificarLibroDisponibleUseCase` o `CalcularPrecioFinalUseCase`. En esta capa se coordina la lógica de aplicación. Por ejemplo, publicar un libro puede implicar validar datos, generar un slug, guardar el libro y notificar al vendedor.

Los **Interface Adapters** convierten la información entre el mundo externo y los casos de uso. Aquí pueden estar los controladores, DTOs, presenters y adaptadores concretos como `LibroController`, `LibroDTO`, `PostgresLibroRepository`, `EmailNotificationAdapter` o `RedisCacheAdapter`. Su trabajo es adaptar datos, no contener reglas centrales del negocio.

Los **Frameworks and Drivers** son la capa más externa. Aquí están herramientas como Spring Boot, PostgreSQL, Redis, Elasticsearch, SMTP, PayPal SDK, PSE API, React o una aplicación móvil. Son detalles técnicos necesarios, pero reemplazables.

La inversión de dependencia es clave en Clean Architecture. Por ejemplo, si `PublicarLibroUseCase` necesita guardar un libro, no debe depender directamente de `PostgresLibroRepository`. En cambio, debe depender de una interfaz:

```java
interface LibroRepository {
    void guardar(Libro libro);
}
```

El caso de uso depende de esa abstracción:

```java
class PublicarLibroUseCase {
    private final LibroRepository libroRepository;

    public PublicarLibroUseCase(LibroRepository libroRepository) {
        this.libroRepository = libroRepository;
    }

    public void ejecutar(Libro libro) {
        libroRepository.guardar(libro);
    }
}
```

Y la capa externa implementa la interfaz:

```java
class PostgresLibroRepository implements LibroRepository {
    @Override
    public void guardar(Libro libro) {
        // Guardar usando PostgreSQL
    }
}
```

Así, la lógica interna no depende de PostgreSQL. Es PostgreSQL, mediante su adaptador, el que depende de la interfaz requerida por el caso de uso.

La comparación con una arquitectura tradicional por capas es importante. En una arquitectura tradicional se suele organizar así:

```text
Presentación → Lógica de negocio → Datos
```

Esto puede funcionar, pero muchas veces la lógica de negocio termina dependiendo directamente de repositorios concretos, frameworks u ORMs. En Clean Architecture, en cambio, la lógica de negocio se mantiene independiente y las dependencias se invierten mediante interfaces.

La diferencia principal es:

```text
Arquitectura tradicional:
Controller → Service → Repository → Base de datos

Clean Architecture:
Frameworks/Drivers → Adapters → Use Cases → Entities
```

En OpenLib Market, Clean Architecture permitiría que reglas como comprar libros, publicar libros, calcular descuentos, procesar pagos o notificar disponibilidad se puedan probar y mantener sin depender directamente de Spring Boot, PostgreSQL, Redis, PayPal o SMTP.

En conclusión, Clean Architecture ayuda a construir un sistema más desacoplado, mantenible y testeable. Su valor no está solo en separar carpetas, sino en respetar la dirección de dependencias y aplicar inversión de dependencia en los límites entre capas.
