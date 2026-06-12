# Plantilla de entrega — Parcial 2
---

## Pregunta [06]: [P06_clean_architecture]

### Estudiante
- **Nombre completo**: Samuel López Gómez

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Gemini |
| **Modelo específico** |3.1 pro advance |
| **¿Por qué elegiste este LLM?** | La IA considera que Gemini Advanced es fantástico para esta pregunta. Sabenexplicar muy bien la diferencia entre capas tradicionales y Clean Architecture |

---

### Prompt utilizado

Actúa como un arquitecto de software experto en Clean Architecture (Robert C. Martin). El equipo del proyecto OpenLib Market (plataforma de compra-venta de libros en Java) necesita entender este paradigma antes de rediseñar el sistema.

Necesito que expliques de forma clara, técnica y estructurada lo siguiente:
1. Las 4 capas principales de Clean Architecture y la "Regla de Dependencia" fundamental.
2. Cómo se aplica el Principio de Inversión de Dependencia (DIP) específicamente en los límites entre capas (boundary crossing) para no violar la Regla de Dependencia (ej. cómo el Use Case llama a la base de datos).
3. Una comparación directa: ¿En qué se diferencia Clean Architecture de la Arquitectura en Capas tradicional (Presentación → Lógica → Datos)?
4. Ejemplos concretos: Asigna al menos dos componentes reales de OpenLib Market a cada una de las 4 capas para que el equipo lo entienda.

Restricciones:
- No des respuestas genéricas; todo debe estar aterrizado al contexto de e-commerce de libros.
- Usa lenguaje técnico preciso (puertos, adaptadores, abstracciones).

Formato de salida esperado: Markdown con las secciones "Capas y Regla de Dependencia", "Inversión de Dependencias en los Límites", "Comparación con Arquitectura Tradicional" y "Ejemplos en OpenLib Market".

```

---

### Respuesta del LLM
Aquí tienes la explicación detallada de Clean Architecture, aterrizada al contexto de OpenLib Market, diseñada para el equipo de desarrollo.

Capas y Regla de Dependencia
Clean Architecture divide el software en capas concéntricas, aislando las políticas de negocio de los detalles técnicos. Las 4 capas principales (de adentro hacia afuera) son:

Entities (Reglas de Negocio Empresariales): Contienen los objetos de dominio, modelos y reglas fundamentales del negocio que no cambian sin importar si la aplicación es web, móvil o de consola. Son los conceptos más abstractos y puros del sistema.
Use Cases (Reglas de Negocio de la Aplicación): Orquestan el flujo de datos hacia y desde las entidades. Aquí se definen las operaciones específicas del sistema (lo que la aplicación "hace"). Dirigen las entidades para aplicar sus reglas críticas a fin de lograr los objetivos del caso de uso.
Interface Adapters (Adaptadores de Interfaz): Actúan como traductores. Convierten los datos desde el formato más conveniente para los Casos de Uso y Entidades, hacia el formato más conveniente para agentes externos (como la Base de Datos, la Web o la UI), y viceversa. Aquí viven los Controladores, Presentadores y Gateways.
Frameworks & Drivers (Infraestructura): La capa más externa. Está compuesta por herramientas, bases de datos, frameworks web (ej. Spring Boot) y librerías de terceros. Es donde residen los "detalles" de implementación.
La Regla de Dependencia: El principio rector que mantiene unida esta arquitectura dicta que las dependencias del código fuente solo pueden apuntar hacia adentro. Nada en un círculo interno puede saber algo en absoluto sobre algo en un círculo externo. Variables, funciones, clases, bases de datos o frameworks de las capas externas no pueden ser mencionados por las capas internas.

Inversión de Dependencias en los Límites
Cuando ejecutamos un caso de uso, la lógica dicta que el flujo de control se mueve de adentro hacia afuera: el caso de uso necesita leer o guardar datos en la base de datos. Sin embargo, si el Caso de Uso (capa 2) llama directamente a la capa de Base de Datos (capa 4), violaría la Regla de Dependencia.

Aquí es donde aplicamos el Principio de Inversión de Dependencia (DIP) a través del patrón de Puertos y Adaptadores (Ports and Adapters):

El Puerto (Abstracción en la Capa Interna): En la capa de Use Cases, OpenLib Market define una interfaz (el "Puerto"). Por ejemplo, BookRepository. El caso de uso depende y llama a los métodos de esta interfaz genérica para buscar o guardar un libro. Como la interfaz está en su misma capa, la Regla de Dependencia está intacta.
El Adaptador (Implementación en la Capa Externa): En la capa de Interface Adapters, creamos una clase concreta (ej. PostgresBookAdapter) que implementa la interfaz BookRepository. Este adaptador inyecta dependencias de la capa de Infraestructura (Hibernate/SQL) para realizar la operación real.
El resultado: En tiempo de compilación, el código fuente del Adaptador (externo) depende de la Interfaz (interno), apuntando hacia el centro. Pero en tiempo de ejecución (mediante Inyección de Dependencias), el flujo de control viaja del Caso de Uso hacia el Adaptador. Hemos cruzado el límite arquitectónico (boundary crossing) invirtiendo la dependencia.

Comparación con Arquitectura Tradicional
En una Arquitectura en Capas tradicional (N-Tier), el diseño suele ser de arriba hacia abajo: Presentación (UI) → Lógica de Negocio (Service) → Acceso a Datos (DAO) → Base de Datos.

El problema tradicional: La capa de Lógica de Negocio depende directamente de la capa de Acceso a Datos. Si cambiamos la base de datos (de MySQL a MongoDB) o el ORM, la lógica de negocio se ve afectada. El núcleo del e-commerce termina acoplado a SQL o a librerías de persistencia.
La solución Clean Architecture: Pone a la Lógica de Negocio en el centro absoluto. El acceso a datos (Infraestructura) y la Presentación (Web) son plugins que apuntan hacia el centro. La base de datos se convierte en un detalle intercambiable. La lógica de negocio de OpenLib Market puede probarse íntegramente de forma unitaria sin necesidad de levantar una base de datos ni un servidor Tomcat, usando Mocks para las interfaces (Puertos).
Ejemplos en OpenLib Market
Para que el equipo visualice cómo se mapea el e-commerce de libros en Java a este paradigma, aquí están los componentes reales ubicados en sus capas respectivas:

1. Entities (Reglas Empresariales)

Book (Clase Java): Contiene el estado del libro y reglas de invariantes propias del dominio (ej. un método applyDiscount(percentage) o validaciones intrínsecas de que un ISBN debe tener el formato correcto).
Order (Clase Java): Entidad que agrupa múltiples libros y calcula el total a pagar, aplicando reglas de negocio globales de e-commerce (ej. calcular el subtotal sin impuestos).
2. Use Cases (Reglas de la Aplicación)

PublishBookUseCase (Clase Java): Interactor que recibe un DTO con datos de un vendedor, instancia un Book, valida las políticas de publicación de la plataforma y utiliza la interfaz BookRepository para persistirlo.
ProcessPurchaseUseCase (Clase Java): Orquesta el flujo completo de una compra. Llama a interfaces como InventoryGateway para reservar el stock, PaymentGateway para cobrar, y genera la entidad Order.
3. Interface Adapters (Adaptadores)

BookController (Clase REST): Recibe peticiones HTTP, extrae los parámetros (ej. GET /api/books?author=Tolkien), los convierte en un objeto de petición (Request Model) y se lo pasa al Caso de Uso correspondiente. También toma la respuesta del Caso de Uso y la formatea en JSON.
JpaBookRepositoryAdapter (Clase Repositorio): Implementa la interfaz BookRepository del Caso de Uso. Recibe la entidad pura Book, la mapea a un objeto @Entity de JPA (BookJpaEntity) y utiliza los métodos del framework para guardarla en base de datos.
4. Frameworks & Drivers (Infraestructura)

Spring Web MVC / Tomcat: El framework que maneja el enrutamiento real de los endpoints HTTP, la deserialización del JSON y la gestión de la red.
PostgreSQL / Spring Data JPA: El motor de base de datos relacional físico donde se almacena el catálogo de libros, y las librerías de Hibernate que generan las sentencias SQL reales.
```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

Decirle a la IA que actúe como un "arquitecto enseñando al equipo" salvó la respuesta. Eso hizo que no escupiera código a lo loco, sino que explicara todo con plastilina. Pedirle que metiera el Principio de Inversión de Dependencias (DIP) justo en los cruces de las capas obligó al modelo a explicar Ports and Adapters en serio, en lugar de tirarnos el típico copy-paste de Wikipedia. Y compararlo con la arquitectura en capas normalita (N-Tier) me sirvió un montón para entender por qué esta vuelta es mucho mejor a la hora de hacer pruebas unitarias.

#### 2. ¿Qué se puede mejorar?

La explicación general aguanta, pero comparando con lo que leímos de Uncle Bob en clase, la IA tomó un par de atajos feos: Primero, mezcló responsabilidades. El LLM dice que el BookController agarra la respuesta del Caso de Uso y arma el JSON. Falso. En la teoría estricta, el Caso de Uso no le devuelve nada directo al Controlador. Debería llamar a un Presenter (usando un Output Port) y es ese man el que moldea los datos para la vista o la API. Segundo, los datos entre capas. Menciona por ahí los "DTOs", pero no aclara la regla de oro: lo que cruza las fronteras tienen que ser objetos planos (POJOs), nada de librerías raras. Si le pasas un HttpServletRequest del framework o un ResultSet de SQL al caso de uso, ya arruinaste toda la arquitectura.

#### 3. Respuesta final
Para estudiar para el parcial, la explicación de las 4 capas y la Regla de Dependencia sirve perfecto. Cómo usa el DIP para que la capa interna ponga la interfaz (Puerto) y la externa la implemente (Adaptador) está súper claro. Lo que más me gustó fue el ejemplo de OpenLib Market, porque separó bien la entidad de dominio pura (Book) de la tabla de la base de datos (BookJpaEntity). Eso es justo lo que siempre terminamos espaguetizando en los proyectos de la universidad. Pero si quiero sacar un 5.0, a la respuesta le falta explicar el ciclo completo con Input y Output Ports. El Controlador solo debe empujar datos hacia adentro, y el Use Case debe escupir el resultado hacia el Presentador. Solo así garantizas que tu regla de negocio sobreviva si mañana cambias de framework web.
