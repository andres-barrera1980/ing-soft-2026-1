# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [6]: [clean architecture]

### Estudiante
- **Nombre completo**: [Juan Fernando Herrera Patiño]

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [ChatGPT] |
| **Modelo específico** | [GPT-4o ]|
| **¿Por qué elegiste este LLM?** | [mas comodo para mi] |

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [Claude / ChatGPT / Gemini / Copilot / DeepSeek / Qwen / Mistral / Otro / **Sin IA — respuesta propia**] |
| **Modelo específico** | [Ej: Claude Opus 4.5, GPT-4o, Gemini 2.5 Pro, etc. Si respondes sin IA, escribe "N/A"] |
| **¿Por qué elegiste este LLM?** | [Justifica en 1-3 oraciones. Si respondes sin IA, explica por qué decidiste no usar LLM para esta pregunta.] |

---

### Prompt utilizado

Eres un arquitecto de software experto en Clean Architecture, principios SOLID y diseño de sistemas empresariales.

Explica detalladamente la arquitectura Clean Architecture propuesta por Robert C. Martin (Uncle Bob).

Tu respuesta debe incluir obligatoriamente:

1. Explicación de las cuatro capas:

   * Entities
   * Use Cases
   * Interface Adapters
   * Frameworks & Drivers

2. Explicación clara de la regla de dependencia (Dependency Rule):

   * Qué significa que las dependencias solo pueden apuntar hacia adentro.
   * Por qué esta regla es importante.
   * Qué problemas evita.

3. Explicación de cómo se aplica el principio de Inversión de Dependencias (DIP) entre capas:

   * Qué capa define las interfaces.
   * Qué capa implementa las interfaces.
   * Cómo se logra el desacoplamiento.

4. Comparación con una arquitectura tradicional en capas:

   * Presentación
   * Lógica de negocio
   * Acceso a datos

   Indica ventajas y desventajas de cada enfoque.

5. Aplica todos los conceptos al caso de OpenLib Market:

   * Indica qué clases o componentes pertenecerían a cada capa.
   * Explica por qué pertenecen a esa capa.

6. Incluye un diagrama textual de la arquitectura.

7. Explica los beneficios de Clean Architecture en términos de mantenibilidad, pruebas, escalabilidad y desacoplamiento.

8. Menciona errores comunes al implementar Clean Architecture.

La respuesta debe ser técnica, detallada y utilizar ejemplos concretos relacionados con OpenLib Market.


---

### Respuesta del LLM

Clean Architecture de Robert C. Martin (Uncle Bob)
1. ¿Qué es Clean Architecture?

Clean Architecture es un estilo arquitectónico propuesto por Robert C. Martin cuyo objetivo principal es construir sistemas:

Independientes de frameworks.
Independientes de bases de datos.
Independientes de interfaces de usuario.
Fáciles de probar.
Altamente mantenibles.
Desacoplados de tecnologías externas.

La idea central es que las reglas de negocio del sistema deben estar protegidas de los detalles técnicos.

Por ejemplo, en OpenLib Market, la lógica para publicar libros, procesar pagos o aplicar descuentos no debería depender de:

Spring Boot
PostgreSQL
Redis
Elasticsearch
APIs de terceros

Si mañana cambia una tecnología, la lógica de negocio debería permanecer intacta.

2. Las Cuatro Capas de Clean Architecture

La arquitectura se organiza en círculos concéntricos.

+----------------------------------+
|      Frameworks & Drivers        |
+----------------------------------+
|       Interface Adapters         |
+----------------------------------+
|           Use Cases              |
+----------------------------------+
|            Entities              |
+----------------------------------+

Mientras más al centro está una capa:

Más importante es para el negocio.
Menos debe conocer sobre tecnologías externas.
A. Entities (Entidades)
¿Qué son?

Representan las reglas de negocio más puras y estables.

Son objetos del dominio que existen independientemente de:

Base de datos
Framework
API
Interfaz gráfica
Ejemplos en OpenLib Market
public class Libro {

    private String isbn;
    private String titulo;
    private double precio;

}
public class Usuario {

    private String nombre;
    private String email;

}
public class Carrito {

    private List<Libro> libros;

}
Responsabilidad

Contener:

Estado del negocio.
Reglas fundamentales.

Ejemplo:

public class Libro {

    public boolean tieneISBNValido() {
        return isbn != null && !isbn.isBlank();
    }

}
B. Use Cases
¿Qué son?

Representan los casos de uso de la aplicación.

Implementan procesos de negocio específicos.

Ejemplos en OpenLib Market
Publicar Libro
public class PublicarLibroUseCase {
}
Comprar Libro
public class ComprarLibroUseCase {
}
Procesar Pago
public class ProcesarPagoUseCase {
}
Aplicar Descuentos
public class AplicarDescuentoUseCase {
}
Responsabilidad

Orquestar entidades y reglas.

Ejemplo:

public class ComprarLibroUseCase {

    public void ejecutar(...) {

        validarStock();

        procesarPago();

        registrarCompra();

    }
}
C. Interface Adapters
¿Qué son?

Adaptan el mundo externo al modelo interno.

Transforman datos entre capas.

Componentes típicos
Controllers
LibroController
PagoController
Presenters
LibroPresenter
CompraPresenter
Gateways
LibroRepositoryImpl
UsuarioRepositoryImpl
Ejemplo

El controlador recibe:

{
  "isbn":"123"
}

y lo transforma en:

ComprarLibroRequest

que entiende el caso de uso.

D. Frameworks & Drivers
¿Qué son?

Son detalles externos.

Tecnologías que pueden cambiar.

Ejemplos en OpenLib
Framework
Spring Boot
Base de datos
PostgreSQL
Caché
Redis
Buscador
Elasticsearch
Correo
SMTP
API de pagos
PayPal API
Stripe API
PSE API
Característica importante

Esta capa conoce todo.

Las capas internas no conocen nada de ella.

3. Dependency Rule (Regla de Dependencia)
Principio Fundamental

Las dependencias de código fuente solo pueden apuntar hacia adentro.

Correcto
Frameworks
     ↓
Adapters
     ↓
Use Cases
     ↓
Entities
Incorrecto
Entities
     ↓
Spring

o

UseCase
     ↓
PostgreSQL
¿Qué significa?

Una entidad no debería saber que existe:

@Repository
@Entity
@RestController

porque son detalles técnicos.

¿Por qué es importante?

Permite cambiar:

PostgreSQL → MongoDB

sin modificar:

ComprarLibroUseCase
Problemas que evita
Alto acoplamiento

Malo:

public class ComprarLibroUseCase {

   private PostgreSQLRepository repo;

}
Dependencia tecnológica

Si desaparece PostgreSQL:

Todo el sistema se rompe.
Baja testabilidad

Necesitaríamos:

Base de datos real

para probar la lógica.

4. Aplicación de DIP (Dependency Inversion Principle)
Regla

Las capas internas definen contratos.

Las capas externas los implementan.

Quién define interfaces
Use Cases
public interface LibroRepository {

    void guardar(Libro libro);

}
Quién implementa
Interface Adapters
public class PostgreSQLLibroRepository
        implements LibroRepository {

}
Flujo
Use Case
      ↓
LibroRepository (interfaz)
      ↑
PostgreSQLLibroRepository
Resultado

El caso de uso no conoce:

PostgreSQL
MySQL
MongoDB

Solo conoce:

LibroRepository
5. Comparación con Arquitectura Tradicional
Arquitectura Tradicional
Presentación
      ↓
Negocio
      ↓
Datos
Ejemplo
Controller
    ↓
Service
    ↓
Repository
Ventajas
Fácil de entender.
Rápida de implementar.
Menos clases.
Desventajas

La lógica termina dependiendo de:

Spring
JPA
Hibernate

Generando acoplamiento.

Clean Architecture
Frameworks
    ↓
Adapters
    ↓
Use Cases
    ↓
Entities
Ventajas
Más desacoplada.
Más testeable.
Más mantenible.
Independiente de tecnologías.
Desventajas
Más clases.
Mayor complejidad inicial.
Curva de aprendizaje.
6. Aplicación a OpenLib Market
Entities
Libro
Usuario
Compra
Pago
Carrito
Comentario

Porque representan el dominio central.

Use Cases
PublicarLibroUseCase
ComprarLibroUseCase
ProcesarPagoUseCase
AgregarWishlistUseCase
AplicarDescuentosUseCase

Porque contienen reglas de negocio.

Interface Adapters
LibroController
PagoController

LibroRepositoryImpl
UsuarioRepositoryImpl

LibroPresenter

Porque adaptan entrada y salida.

Frameworks & Drivers
Spring Boot

PostgreSQL

Redis

Elasticsearch

SMTP

PayPal API

PSE API

Porque son detalles tecnológicos.

7. Diagrama Textual Completo
┌─────────────────────────────────────┐
│       Frameworks & Drivers          │
│-------------------------------------│
│ Spring Boot                         │
│ PostgreSQL                          │
│ Redis                               │
│ Elasticsearch                       │
│ SMTP                                │
│ PayPal API                          │
└─────────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────┐
│        Interface Adapters           │
│-------------------------------------│
│ LibroController                     │
│ PagoController                      │
│ LibroRepositoryImpl                 │
│ UsuarioRepositoryImpl               │
│ Presenters                          │
└─────────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────┐
│            Use Cases                │
│-------------------------------------│
│ ComprarLibroUseCase                 │
│ PublicarLibroUseCase                │
│ ProcesarPagoUseCase                 │
│ AplicarDescuentoUseCase             │
└─────────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────┐
│             Entities                │
│-------------------------------------│
│ Libro                               │
│ Usuario                             │
│ Compra                              │
│ Pago                                │
│ Carrito                             │
└─────────────────────────────────────┘
8. Beneficios de Clean Architecture
Mantenibilidad

Si cambia:

PayPal → Stripe

solo cambia la implementación externa.

Los casos de uso permanecen iguales.

Pruebas

Podemos probar:

ComprarLibroUseCase

sin:

PostgreSQL
Redis
SMTP

usando mocks.

Escalabilidad

Agregar:

Nueva API de pagos

no afecta el núcleo.

Desacoplamiento

La lógica de negocio queda protegida de:

Frameworks.
Bases de datos.
Servicios externos.
9. Errores Comunes al Implementar Clean Architecture
Error 1

Poner lógica de negocio en Controllers

@RestController
public class LibroController {
    // lógica de descuentos aquí
}

Incorrecto.

Debe ir en Use Cases.

Error 2

Use Cases dependientes de JPA

public class ComprarLibroUseCase {

    private JpaRepository repo;

}

Incorrecto.

Debe depender de interfaces.

Error 3

Entities con anotaciones de Framework

@Entity
@Table(...)
public class Libro

Esto acopla el dominio a JPA.

Error 4

Saltarse capas

Controller
      ↓
Base de datos

sin pasar por Use Cases.

Error 5

Confundir Clean Architecture con exceso de clases

No se trata de crear más clases, sino de separar correctamente las responsabilidades y proteger las reglas de negocio.

Conclusión

Clean Architecture organiza el sistema en Entities, Use Cases, Interface Adapters y Frameworks & Drivers, aplicando la Dependency Rule para que todas las dependencias apunten hacia el núcleo del negocio. En OpenLib Market esto permite que funcionalidades como compra de libros, pagos, descuentos, wishlists o publicaciones permanezcan independientes de tecnologías como Spring Boot, PostgreSQL, Redis o PayPal. El resultado es un sistema más mantenible, testeable, escalable y desacoplado, alineado con los principios SOLID, especialmente SRP, OCP y DIP.
```

---

### Análisis crítico de la respuesta
La respuesta explica correctamente las cuatro capas de Clean Architecture y describe adecuadamente la regla de dependencia, indicando que las dependencias deben apuntar hacia las capas internas para proteger las reglas de negocio de detalles tecnológicos. También explica de forma correcta la aplicación del principio de inversión de dependencias, mostrando cómo los casos de uso dependen de interfaces y las implementaciones concretas se encuentran en capas externas. La comparación con la arquitectura tradicional es precisa, destacando que Clean Architecture ofrece mayor desacoplamiento y testabilidad a costa de una mayor complejidad inicial. Los ejemplos propuestos para OpenLib Market son adecuados y están correctamente ubicados dentro de cada capa. Como mejora, la respuesta podría haber profundizado más en el uso de DTOs y en el flujo completo de comunicación entre capas, pero conceptualmente no presenta errores importantes y refleja correctamente los principios de Clean Architecture.

#### 1. ¿Qué hizo bien el prompt?

El prompt fue efectivo porque solicitó explícitamente la explicación de las capas de Clean Architecture, la regla de dependencia, la aplicación del principio de inversión de dependencias y una comparación con la arquitectura tradicional. Además, pedir ejemplos concretos para OpenLib Market ayudó a contextualizar los conceptos teóricos y obtener una respuesta más práctica. La parte más efectiva fue exigir la explicación de la Dependency Rule y del DIP, ya que son los conceptos fundamentales de Clean Architecture y permitieron obtener una respuesta completa y bien estructurada.



#### 2. ¿Qué se puede mejorar?

El prompt podría mejorarse solicitando ejemplos más detallados de las interfaces que definen los casos de uso y de sus implementaciones concretas en los adaptadores. También habría sido útil pedir un ejemplo completo del flujo de una operación, por ejemplo una compra de libro desde el controlador hasta la persistencia. Aunque la respuesta fue correcta, no profundizó demasiado en cómo se comunican las capas mediante DTOs o modelos de entrada y salida, elementos que suelen ser importantes en una implementación real de Clean Architecture.


#### 3. Respuesta final

Clean Architecture organiza el sistema en cuatro capas: Entities, Use Cases, Interface Adapters y Frameworks & Drivers. Su regla fundamental establece que las dependencias solo pueden apuntar hacia adentro, evitando que las reglas de negocio dependan de tecnologías externas. Esto se logra aplicando el principio de inversión de dependencias, donde las capas internas definen interfaces y las capas externas las implementan. A diferencia de una arquitectura tradicional en capas, Clean Architecture ofrece mayor desacoplamiento, mantenibilidad y facilidad de pruebas. En OpenLib Market, entidades como Libro, Usuario y Carrito pertenecen a la capa de Entities; procesos como ComprarLibro o AplicarDescuento corresponden a Use Cases; controladores y repositorios implementados pertenecen a Interface Adapters; mientras que Spring Boot, PostgreSQL, Redis y APIs externas forman parte de Frameworks & Drivers. Este enfoque protege el núcleo del negocio frente a cambios tecnológicos y facilita la evolución del sistema.

