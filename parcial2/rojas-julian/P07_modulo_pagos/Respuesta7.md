
## Pregunta [07]: [Diseñar un módulo aplicando Clean Architecture a OpenLib Market]

### Estudiante
- **Nombre completo**: Julian Felipe Rojas Almanza

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Gpt |
| **Modelo específico** | Gpt-4o |
| **¿Por qué elegiste este LLM?** | Es parte de los modelos más eficientes para modelar diagramas estructurales en formato Mermaid y aplicar de forma pragmática la separación de conceptos. Evitan mezclar anotaciones de frameworks dentro de las capas internas de entidades y casos de uso, un error conceptual crítico y muy común en otros LLMs. |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```
Actúa como un Arquitecto de Software Principal con experiencia en Clean Architecture y Diseño Guiado por el Dominio (DDD). Estoy resolviendo un ejercicio técnico académico para el sistema "OpenLib Market" y necesito que rediseñes un módulo crítico siguiendo estrictamente las reglas de Robert C. Martin.

[CONTEXTO]
El equipo de desarrollo ha acoplado todo el módulo de **gestión de pagos** en una sola clase monolítica llamada `PaymentService` dentro de un único método `realizarPago`. Este método hace todo: llama a las pasarelas de pago externas, gestiona transacciones directas a PostgreSQL, envía correos de notificación, cambia estados de órdenes y maneja la lógica de entrega. El sistema debe exponer una API REST para ser consumida por un frontend en JavaFX.

[PROBLEMA / TAREA]
Necesito que rediseñes por completo este módulo aplicando los principios de Clean Architecture para resolver el acoplamiento severo. Tu respuesta debe incluir de forma explícita:
1. La definición exacta de qué componentes, interfaces, puertos y clases van en cada una de las cuatro capas funcionales:
   - Entidades (Entities)
   - Casos de Uso (Use Cases)
   - Adaptadores de Interfaz (Interface Adapters)
   - Frameworks y Drivers
2. Un diagrama de estructura arquitectónica detallado en formato Mermaid que ilustre la organización de estas capas y demuestre visualmente cómo todas las dependencias del código fuente apuntan exclusivamente hacia adentro.
3. Una explicación clara y concisa de cómo se aplica la Inversión de Dependencias (DIP) mediante la definición de Puertos (Interfaces) y Adaptadores para inyectar las dependencias externas (Base de datos PostgreSQL, pasarelas de pago como PayPal/Tarjeta y envío de correos) sin violar los límites de la arquitectura.

[RESTRICCIONES]
- Chain of Thought: Antes de presentar el diseño, explica por qué colocar anotaciones de frameworks de persistencia (como Hibernate/JPA) o de frameworks web (como Spring Web) directamente en las clases de la capa de Entidades o Casos de Uso es una violación de Clean Architecture.
- El Caso de Uso de pagos debe ser agnóstico a si la petición viene de una API REST de JavaFX, una interfaz de consola o un test unitario.
- Los adaptadores externos (como pasarelas de pago y repositorios de persistencia) deben ser implementados hacia el exterior, consumiendo los puertos definidos por la capa de Casos de Uso.
- Todo el diseño conceptual y nombres de componentes deben estar estrictamente orientados al negocio de pagos de OpenLib Market (ej: `ProcesarPagoUseCase`, `NotificationGatewayPort`, `PaymentRestController`, etc.).

[FORMATO DE SALIDA]
Estructura tu respuesta en Markdown utilizando única y estrictamente estos cuatro encabezados para organizar la información:
- ### 1. Distribución de Componentes por Capas (Clean Architecture)
- ### 2. Diagrama Estructural de Dependencias (Mermaid)
- ### 3. Mecanismo de Inversión de Dependencias y Puertos
- ### 4. Evaluación de Testabilidad del Nuevo Diseño

[VALIDACIÓN]
Sabré que tu respuesta es excelente si el controlador REST y la base de datos PostgreSQL quedan completamente relegados a la capa más externa, si el diagrama Mermaid renderiza sin errores de sintaxis mostrando un flujo de dependencias estrictamente centrípeto (hacia adentro), y si demuestras que la lógica central de pagos puede ser testeada de forma aislada mediante dobles de prueba (mocks) sin levantar bases de datos ni servicios de red.
```

---

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

# Solución

### 1. Distribución de Componentes por Capas (Clean Architecture)

Antes del rediseño, es importante entender que colocar anotaciones como `@Entity`, `@Table`, `@RestController`, `@Autowired` o cualquier dependencia de Spring/JPA dentro de Entidades o Casos de Uso viola Clean Architecture.

¿Por qué?

Porque esas capas internas comenzarían a depender de frameworks externos. Si mañana PostgreSQL se reemplaza por MongoDB o Spring Boot por otro framework, el núcleo del negocio tendría que modificarse.

Las capas internas deben contener únicamente reglas de negocio.

---

#### Capa 1: Entidades (Entities)

Representan las reglas más estables del dominio de pagos.

```text
Pago
Orden
Cliente
MetodoPago
TransaccionPago
EstadoPago
```

Ejemplos:

```text
Pago
Orden
TransaccionPago
```

Responsabilidades:

* Validar reglas del negocio.
* Gestionar estados de pago.
* Calcular montos finales.

No conocen:

* PostgreSQL
* Spring Boot
* REST
* PayPal
* SMTP

---

#### Capa 2: Casos de Uso (Use Cases)

Contienen la lógica de aplicación.

Caso principal:

```text
ProcesarPagoUseCase
```

Puertos de salida:

```text
PaymentGatewayPort
PaymentRepositoryPort
OrderRepositoryPort
NotificationGatewayPort
DeliveryGatewayPort
```

DTOs internos:

```text
ProcesarPagoCommand
ProcesarPagoResponse
```

Responsabilidades:

* Validar la orden.
* Solicitar el cobro.
* Actualizar estado del pago.
* Actualizar estado de la orden.
* Solicitar notificación.
* Solicitar entrega.

Todo esto mediante interfaces.

---

#### Capa 3: Adaptadores de Interfaz (Interface Adapters)

Adaptan información entre el exterior y los casos de uso.

Controladores:

```text
PaymentRestController
```

Mappers:

```text
PaymentRequestMapper
PaymentResponseMapper
```

DTOs REST:

```text
PaymentRequest
PaymentResponse
```

Implementaciones de puertos:

```text
PostgreSQLPaymentRepositoryAdapter
PostgreSQLOrderRepositoryAdapter
PayPalAdapter
CreditCardAdapter
EmailNotificationAdapter
DeliveryServiceAdapter
```

---

#### Capa 4: Frameworks y Drivers

Tecnologías concretas.

```text
Spring Boot
Spring MVC
Spring Data JPA
PostgreSQL
Hibernate
JavaMail
PayPal SDK
Stripe SDK
Docker
```

Ejemplos:

```text
JpaPaymentRepository
JpaOrderRepository
SpringSecurityConfig
PostgreSQLDatasourceConfig
```

Esta capa puede reemplazarse sin modificar el negocio.

---

### 2. Diagrama Estructural de Dependencias (Mermaid)

```mermaid
flowchart TB

subgraph F["Frameworks y Drivers"]
    SPRING["Spring Boot"]
    POSTGRES["PostgreSQL"]
    PAYPALSDK["PayPal SDK"]
    SMTP["Java Mail"]
end

subgraph A["Interface Adapters"]
    REST["PaymentRestController"]

    PAYPALAD["PayPalAdapter"]
    EMAILAD["EmailNotificationAdapter"]

    PAYREPO["PostgreSQLPaymentRepositoryAdapter"]
    ORDERREPO["PostgreSQLOrderRepositoryAdapter"]
end

subgraph U["Use Cases"]
    PROCESS["ProcesarPagoUseCase"]

    PGPORT["PaymentGatewayPort"]
    NPORT["NotificationGatewayPort"]

    PREPOPORT["PaymentRepositoryPort"]
    OREPOPORT["OrderRepositoryPort"]
end

subgraph E["Entities"]
    PAGO["Pago"]
    ORDEN["Orden"]
    TRANS["TransaccionPago"]
end

REST --> PROCESS

PAYPALAD --> PGPORT
EMAILAD --> NPORT

PAYREPO --> PREPOPORT
ORDERREPO --> OREPOPORT

PROCESS --> PGPORT
PROCESS --> NPORT
PROCESS --> PREPOPORT
PROCESS --> OREPOPORT

PROCESS --> PAGO
PROCESS --> ORDEN
PROCESS --> TRANS

SPRING --> REST
POSTGRES --> PAYREPO
POSTGRES --> ORDERREPO
PAYPALSDK --> PAYPALAD
SMTP --> EMAILAD
```

---

### 3. Mecanismo de Inversión de Dependencias y Puertos

La clave de Clean Architecture es que los Casos de Uso no conocen implementaciones concretas.

Por ejemplo, el caso de uso define el puerto:

```java id="3ibd4k"
public interface PaymentGatewayPort {

    PaymentResult cobrar(Pago pago);
}
```

El caso de uso depende únicamente de esta interfaz:

```java id="5mqk3s"
public class ProcesarPagoUseCase {

    private final PaymentGatewayPort gateway;

    public ProcesarPagoUseCase(
            PaymentGatewayPort gateway) {

        this.gateway = gateway;
    }
}
```

La implementación concreta vive fuera:

```java id="iynm96"
public class PayPalAdapter
        implements PaymentGatewayPort {

    @Override
    public PaymentResult cobrar(
            Pago pago) {

        // SDK de PayPal
    }
}
```

Lo mismo ocurre para:

```text
NotificationGatewayPort
    ↓
EmailNotificationAdapter

PaymentRepositoryPort
    ↓
PostgreSQLPaymentRepositoryAdapter

OrderRepositoryPort
    ↓
PostgreSQLOrderRepositoryAdapter
```

De esta manera:

* El caso de uso no sabe si usa PayPal o Stripe.
* No sabe si la base de datos es PostgreSQL o MongoDB.
* No sabe si la notificación es Email o SMS.

Solo conoce contratos.

---

### 4. Evaluación de Testabilidad del Nuevo Diseño

La principal ventaja del rediseño es que el núcleo del negocio puede probarse completamente aislado.

Ejemplo:

```java id="s4f73v"
PaymentGatewayPort fakeGateway =
        new FakePaymentGateway();

PaymentRepositoryPort fakeRepo =
        new InMemoryPaymentRepository();

NotificationGatewayPort fakeNotification =
        new FakeNotificationGateway();

ProcesarPagoUseCase useCase =
        new ProcesarPagoUseCase(
                fakeGateway,
                fakeRepo,
                fakeNotification
        );
```

Durante la prueba:

```text
No se levanta Spring Boot.
No se conecta PostgreSQL.
No se llama PayPal.
No se envían correos.
```

Se valida únicamente la lógica de negocio:

```text
✓ Pago aprobado
✓ Orden actualizada
✓ Transacción registrada
✓ Notificación solicitada
```

Beneficios obtenidos:

* Pruebas unitarias rápidas.
* Bajo acoplamiento.
* Infraestructura reemplazable.
* Cumplimiento de DIP.
* Cumplimiento de la Regla de Dependencia.
* Casos de uso reutilizables desde REST, JavaFX, consola o pruebas automatizadas.

En consecuencia, `ProcesarPagoUseCase` permanece completamente independiente de PostgreSQL, Spring Boot, PayPal y cualquier tecnología externa, que es precisamente el objetivo principal de Clean Architecture.


---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

El prompt cumple con el objetivo gracias a que se implementó la segmentación descrita en el manual guia_prompting.md. La optimización combinó el trabajo de una Gema de Gemini en la fase inicial y mi posterior curación y corrección humana. El resultado es un prompt con rol, contexto e instrucciones precisas bajo un esquema de respuesta simple.


#### 2. ¿Qué se puede mejorar?

El prompt está tan bien hecho y los cambios necesarios son casi invisibles. Operativamente ya da el resultado exacto que se busca.


#### 3. Respuesta final

el análisis de la inteligencia artificial ubicó correctamente cada componente en su capa correspondiente, pues acomodó con éxito las entidades, los casos de uso, los adaptadores y las herramientas tecnológicas en las cuatro divisiones de la arquitectura limpia. además, se demostró que el controlador rest está bien aislado de la lógica de negocio al quedar ubicado en la capa de adaptadores de interfaz, cuidando que la regla de dependencia se respete en todo el diseño porque las flechas de código fuente apuntan hacia adentro y no hay dependencias que cruzan capas en la dirección equivocada. la evaluación de la testabilidad del nuevo diseño fue muy precisa, pues mostró que es infinitamente más fácil probar las piezas por separado en experimentos de mentiras sin levantar bases de datos ni servidores web reales en comparación con la versión acoplada original.

sin embargo, el análisis omitió un vacío gigante sobre las dependencias ocultas que meten los marcos de trabajo. en la explicación, la inteligencia artificial pasó por alto que si las entidades como pago u orden usan anotaciones automáticas de persistencia o herramientas de mapeo externas, se estarían cruzando las dependencias de forma invisible de afuera hacia adentro, lo cual rompe la arquitectura limpia aunque no se dibuje en el diagrama. para que la solución fuera perfecta, faltó mencionar que para aislar de verdad el controlador rest y el negocio, la opción elegida debe obligar a usar objetos de transferencia de datos separados para la web y para las tablas, pues de lo contrario las modificaciones de la base de datos terminarían dañando la interfaz del usuario. tampoco se especificó cómo el motor de spring inyecta los componentes en los puertos en tiempo de ejecución, lo cual es vital para entender cómo se conectan las piezas sin usar el comando de creación manual.

el código original estaba mal diseñado porque mezclaba el controlador rest con el acceso a la base de datos y las pasarelas externas en un solo bloque amarrado, violando la regla de dependencia. además, tener todo pegado obligaba a dañar el negocio con cada cambio técnico. para solucionarlo bien, se aplicó la arquitectura limpia separando el sistema en capas concéntricas y usando el principio de inversión de dependencias como el puente del negocio. las interfaces de los puertos se quedan en el centro junto a los casos de uso, mientras que las clases independientes de la infraestructura actúan como los adaptadores externos que se acoplan por fuera. el procesador final queda limpio y protegido, recibiendo las formas de pago y persistencia desde afuera y ordenando su ejecución, pues para evitar el cruce de capas incorrecto, la opción elegida no expone las entidades puras a la web, sino que se busca automáticamente usar el motor de spring para conectar las piezas mediante inyección por constructor. de esta manera, el sistema no solo borra las condiciones y gana una testabilidad unitaria espectacular con mockito, sino que asegura que todo el flujo quede fácil de mantener, seguro y listo para recibir cambios en el futuro.
