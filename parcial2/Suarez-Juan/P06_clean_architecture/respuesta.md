# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [06]: Explicacion de Arquitectura por Capas

### Estudiante
- **Nombre completo**: Juan Pablo Suarez Moreno

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Gemini |
| **Modelo específico** | Flash 3.5 |
| **¿Por qué elegiste este LLM?** | Se usa este modelo por la capacidad de grandes textos, ademas de la capacidad de conectarse con Google directamente para exter informacion relecante |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```
[Pega aquí el prompt exacto que enviaste al LLM. 
Incluye TODO el texto, sin editar ni resumir.

Un buen prompt incluye:
- Contexto del proyecto OpenLib Market
- El código o situación específica
- Lo que esperas que el LLM haga
- Restricciones (ej: "usa Java 21", "aplica SOLID")
- Formato de salida esperado (ej: "respuesta en markdown con código Java")]

[ROL]

Eres un profesor de desarrollo de software que esta explicando a los estudiantes fundamentos del Software

[CONTEXTO]

Proyecto: OpenLib Market (Plataforma transaccional E-commerce para libros Open Source).

Stack Tecnologico Principal: Java 25, Spring Boot 4.x, PostgreSQL/MongoDB.

Enfoque Arquitectonico: Clean Architecture (Robert C. Martin).


[PROBLEMA]

Necesito una explicación teorica, profunda y metodologica de Clean Architecture, detallando como estructurar los componentes de OpenLib Market en Clean Architecture y como se diferencia radicalmente de una arquitectura tradicional de N-Capas.

[RESTRICCIONES]

La explicación debe abordar obligatoriamente los siguientes puntos:

1. Las 4 capas concentricas: Definicion y responsabilidades de Entidades (Entities), Casos de Uso (Use Cases), Adaptadores de Interfaz (Interface Adapters), y Frameworks/Drivers.

2. La Regla de Dependencia: Explicar que es y por que las dependencias solo pueden apuntar hacia adentro.

3. Inversión de Dependencias (DIP): Explicar detalladamente como se aplica el Principio de Inversion de Dependencias en los limites (boundaries) de las capas para evitar que el nucleo del negocio dependa de la base de datos o frameworks.

4. Comparativa: Contraste directo con una Arquitectura Tradicional en Capas (Presentación → Lógica de Negocio → Datos), exponiendo las ventajas de Clean Architecture frente al acoplamiento a la base de datos.

[FORMATO DE SALIDA]

Solicito la respuesta estructurada de la siguiente manera:

1. Explicacion Conceptual: Desarrollo de los 4 puntos de las restricciones usando Markdown claro, negritas y viñetas.

2. Un cuadro comparativo entre arquitecturas n capas y clena arquitecture

[VALIDACION / CRITERIOS DE EXITO]

La respuesta se considerara excelente si:

- Explica de forma clara como el núcleo (Use Cases/Entities) se mantiene agnostico a Spring Boot y PostgreSQL.

- EL lenguaje ejemplado es facil de entender si no conozco del tema.

- El texto es conciso pero tiene el contenido claro y bien estructurado para la comprension
```

---

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**
[Pega aquí la respuesta COMPLETA del LLM, sin editar, sin resumir.
Incluye TODO el texto, código, explicaciones que generó el LLM.


Si el LLM generó código, asegúrate de que esté correctamente formateado.
Si tuviste que hacer varias iteraciones, pega la MEJOR respuesta obtenida,
pero menciona cuántas iteraciones hiciste.]

¡Hola a todos! Bienvenidos a la clase de hoy. Tomen asiento.

Hoy vamos a hablar de cómo estructurar el software para que no se convierta en una pesadilla mantenible con el paso de los años. Cuando construimos una plataforma como **OpenLib Market** (nuestro e-commerce de libros Open Source), es fácil caer en la tentación de programar rápido y dejar que el framework (Spring Boot) o la base de datos (PostgreSQL o MongoDB) dominen todo nuestro diseño.

Para evitar esto, estudiaremos **Clean Architecture** (Arquitectura Limpia), propuesta por Robert C. Martin ("Uncle Bob"). El objetivo supremo de este enfoque es simple: **el código que contiene las reglas de tu negocio debe ser inmune a los cambios de la tecnología.**

---

## 1. Explicación Conceptual

### Las 4 Capas Concéntricas

Imaginemos Clean Architecture como una cebolla con cuatro capas concéntricas. El software se organiza desde lo más abstracto y vital (en el centro) hasta lo más detallado y cambiante (en el exterior).

Para **OpenLib Market**, las capas se distribuyen y responsabilizan de la siguiente manera:

* **1. Entidades (Entities - El Núcleo):** Es la capa más interna. Contiene las reglas de negocio globales y los objetos de dominio que no cambian cuando cambia la aplicación. Son clases Java puras (POJOs), sin anotaciones de Spring ni de persistencia.
* *Ejemplo en OpenLib Market:* La clase `Libro` (con atributos como `id`, `titulo`, `licenciaOpenSource`) o la clase `CarritoCompra` con la regla matemática para calcular los impuestos de una transacción.


* **2. Casos de Uso (Use Cases / Aplicación):** Rodean a las entidades. Contienen las reglas de negocio específicas de la aplicación; es decir, describen *qué hace* el sistema ante una acción del usuario. Orquestan el flujo de datos hacia y desde las entidades.
* *Ejemplo en OpenLib Market:* El caso de uso `DescargarLibroGratuito` o `ProcesarPagoCarrito`. Esta capa sabe que para comprar un libro se debe validar el stock, aplicar el descuento y generar una orden, pero no sabe *cómo* se guarda en la base de datos.


* **3. Adaptadores de Interfaz (Interface Adapters):** Esta capa traduce los datos en el formato más conveniente para los casos de uso y las entidades, y viceversa. Aquí residen los controladores (Controllers) de nuestras APIs, los Presenters y las interfaces de los repositorios.
* *Ejemplo en OpenLib Market:* Un `LibroController` (REST) que recibe un JSON, lo convierte en un objeto entendible por el caso de uso, y los `Gateways/Repositories` que preparan los datos para ser guardados.


* **4. Frameworks y Drivers (La Capa Externa):** Es la capa más externa y cambiante. Aquí están las herramientas, tecnologías y detalles de infraestructura. El software de esta capa solo se comunica con la capa inmediatamente interna.
* *Ejemplo en OpenLib Market:* Aquí vive **Spring Boot 4.x**, el driver de **PostgreSQL** o **MongoDB**, y las librerías de terceros para pasarelas de pago.



---

### La Regla de Dependencia

Esta es la ley de oro de Clean Architecture: **Las dependencias de código solo pueden apuntar hacia adentro.**

¿Qué significa esto? Significa que una capa interna no sabe absolutamente nada de una capa externa. El código de la capa de *Casos de Uso* o *Entidades* no puede mencionar el nombre de una clase, función o variable declarada en la capa de *Adaptadores* o *Frameworks*.

* **¿Por qué es así?** Porque queremos proteger el núcleo de tu negocio. Si **Spring Boot** se actualiza o si decides migrar la base de datos de **PostgreSQL** a **MongoDB**, el código de la capa externa cambiará, pero tus *Casos de Uso* y *Entidades* (el centro) ni se enterarán. El flujo de control puede ir hacia afuera, pero la dependencia del código fuente va estrictamente hacia adentro.

---

### Inversión de Dependencias (DIP) en los Límites (Boundaries)

Aquí surge una duda lógica: *Si el Caso de Uso (capa interna) necesita guardar un libro en PostgreSQL (capa externa), ¿cómo lo hace sin romper la Regla de Dependencia?*

La respuesta es el **Principio de Inversión de Dependencias (DIP)**. En lugar de que el Caso de Uso llame directamente a la base de datos, el Caso de Uso define una "aduana" o **Interfaz (Boundary)** en su propia capa.

* **Paso 1:** En la capa de *Casos de Uso*, creamos una interfaz llamada `LibroRepositoryPort` (con un método `guardar(Libro l)`).
* **Paso 2:** En la capa de *Adaptadores de Interfaz* (más externa), creamos una clase llamada `LibroPostgresAdapter` que **implementa** esa interfaz y tiene el código real para conectarse a PostgreSQL.

```
[ Capa Casos de Uso (Interna) ]          |    [ Capa Adaptadores (Externa) ]
   Interface: LibroRepositoryPort   <|--------   Clase: LibroPostgresAdapter

```

Gracias al polimorfismo, en tiempo de ejecución el Caso de Uso usará el adaptador de Postgres, pero a nivel de código fuente, **Postgres depende del Caso de Uso**, y no al revés. Hemos invertido la flecha de dependencia. El núcleo se mantiene 100% agnóstico a la tecnología.

---

### Comparativa con la Arquitectura Tradicional de N-Capas

En la **Arquitectura Tradicional en Capas**, el flujo es lineal: la Presentación depende de la Lógica de Negocio, y la Lógica de Negocio **depende directamente de la Capa de Datos** (la Base de Datos es la reina del sistema).

Si construyéramos OpenLib Market a la vieja usanza, mapearíamos las entidades de negocio con anotaciones `@Entity` de JPA/Hibernate acoplándolas a las tablas de PostgreSQL. Si el día de mañana queremos cambiar a MongoDB porque los libros Open Source se manejan mejor como documentos JSON, tendríamos que reescribir la lógica de negocio completa porque estaba amarrada a la estructura de la base de datos relacional.

Clean Architecture rompe radicalmente este acoplamiento situando a la base de datos en la periferia: es un simple detalle de infraestructura.

---

## 2. Cuadro Comparativo: N-Capas vs. Clean Architecture

| Criterio | Arquitectura Tradicional (N-Capas) | Clean Architecture |
| --- | --- | --- |
| **Centro del Universo** | La **Base de Datos**. Todo el diseño del software se construye a partir de las tablas y el modelo relacional. | El **Dominio / Reglas de Negocio**. El software se modela pensando en lo que hace el negocio, sin importar cómo se almacena. |
| **Dirección de Dependencias** | De arriba hacia abajo: `Presentación → Negocio → Datos`. | Siempre **hacia el centro**: `Frameworks → Adaptadores → Casos de Uso ← Entidades`. |
| **Acoplamiento al Framework (Spring)** | **Alto**. Las anotaciones del framework e infraestructura invaden las clases de lógica de negocio. | **Nulo en el núcleo**. Spring Boot vive únicamente en la capa externa; el núcleo es Java puro. |
| **Facilidad de Pruebas (Testing)** | **Complejo**. Para probar la lógica de negocio, sueles necesitar levantar una base de datos en memoria (H2) o mocks pesados. | **Muy Alta**. Los casos de uso y entidades se prueban con pruebas unitarias simples y ultra rápidas, sin encender bases de datos. |
| **Impacto de Cambios Tecnológicos** | **Catastrófico**. Cambiar de base de datos o de framework web implica modificar casi todas las capas del sistema. | **Mínimo y Aislado**. Cambiar PostgreSQL por MongoDB solo afecta a la capa externa de adaptadores; el núcleo no cambia una sola línea. |

---

Como pueden ver, estudiantes, Clean Architecture requiere escribir un poco más de código al principio (interfaces y traductores de datos), pero le otorga a **OpenLib Market** una flexibilidad y una vida útil incomparables.

¿Queda clara la diferencia entre poner la base de datos en el centro versus poner las reglas de tu negocio en el centro?


---

### Preguntas del texto

#### ¿El LLM explicó correctamente la regla de dependencia?

Si, la explicacion fue bstante clara, define con precision el flujo de las dependencias del codigo fuente, debe dirigirse solo hacia las capas interiores agurando que los componentes de OpenLib Market se mantengan intactos a pesar de hacer cambios en la capa exterior.

#### ¿Explicó cómo funciona la inversión de dependencia en los límites entre capas?

Correcto, abordo claramente la inversion de dependencias, explico como el caso de uso define un limite a traves de una interfaz abstracta con la intension de que la infraestructura externa implemente esa interfax. para invertir la felcha de acomplamiento y proteger el nucleo.

#### ¿La comparación con arquitectura en capas tradicional fue precisa?

Si quedo claro y facil identificar la diferencia, especialmente gracias a que N-Capas situa su persistencia en el fondo como cimiento, provocando que cualquier modificacion en el modelo de datos tenga repercuciones grandes en el codigo

#### ¿Los ejemplos de OpenLib Market son correctos para cada capa?

Si, los ejemplos practivos son coherentes con lo que me esta explicando en la arquitectura Clean. En entidades se encuentran los libros, categorias, etc. en los casos de uso se orquesta las reglas de negocio donde esta la DescargaLibroGratuito y los controladores REST y Spring Boot junto con Drivers en la infraestructra o en la parte expetiror msotrando las separacion de rsponsabilidades del proyecto.

#### ¿El LLM cometió algún error conceptual?

No cometio ningun error conceptual, aun que si le falto profundidad en el proyecto ya que a pesar de tener un buen prompt el contexto no es suficiente para que entienda el modelo de negocio a totalidad.

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

Explico de manera clara edutiva con un lenguaje claro que es Clean Arquitecture, usando ejemplos generales desde un perfil profesional.


#### 2. ¿Qué se puede mejorar?

Puede mejorar el entendimiento o mejor modelo del razonamiento ya que a pesar de tener un contexto bastante completo no entendio la idea y los ejemplos era muy generales.


#### 3. Respuesta final

Clean Arquitecture es una chimba, es muy util y practica para hacer cambios sin afectar a todo el modelo o logica del negocio.
