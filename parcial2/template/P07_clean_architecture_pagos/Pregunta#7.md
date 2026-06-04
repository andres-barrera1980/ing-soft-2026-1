# Plantilla de entrega — Parcial 2
---

## Pregunta [07]: [P07_clean_architecture_pagos]

### Estudiante
- **Nombre completo**: Samuel López Gómez

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Chat GPT|
| **Modelo específico** | GPT-5.5|
| **¿Por qué elegiste este LLM?** |Es excepcional para dibujar diagramas de arquitectura en Mermaid y estructurar proyectos complejos separando puertos y adaptadores.  |

---

### Prompt utilizado

Actúa como un arquitecto de software senior rediseñando el módulo de pagos del proyecto OpenLib Market (entorno Java 21). 

Actualmente, el equipo tiene una clase fuertemente acoplada llamada `PaymentService` con un método `realizarPago` que hace todo:
- Procesa el pago llamando a pasarelas externas (Tarjeta, PSE, PayPal).
- Registra la transacción directamente en PostgreSQL.
- Envía notificaciones por correo al comprador y vendedor.
- Está acoplada a las peticiones web porque devuelve directamente respuestas HTTP al frontend JavaFX.

Necesito que rediseñes este módulo aplicando estrictamente Clean Architecture. Debes:
1. Definir exactamente qué componentes, interfaces y clases van en cada una de las 4 capas (Entities, Use Cases, Interface Adapters, Frameworks & Drivers).
2. Proporcionar un diagrama de arquitectura en formato Mermaid que muestre los componentes y la dirección de las flechas de dependencia (demostrando que se respeta la Regla de Dependencia).
3. Explicar cómo se inyectan las dependencias externas (Base de datos, Email, Pasarelas) sin violar la arquitectura (usando Inversión de Dependencias).

Restricciones:
- No escribas todo el código Java, solo enfócate en el diseño estructural, los nombres de las clases/interfaces y las explicaciones.
- Los adaptadores web y de base de datos deben estar completamente aislados de la lógica de negocio.

Formato de salida esperado: Markdown con las secciones "Mapeo de Capas", "Diagrama de Arquitectura (Mermaid)", y "Estrategia de Inyección de Dependencias".

```

---

### Respuesta del LLM
Rediseño del Módulo de Pagos de OpenLib Market con Clean Architecture
Mapeo de Capas
1. Entities (Enterprise Business Rules)
Contiene únicamente reglas de negocio puras e independientes de frameworks.

Entidades
Payment
Transaction
PaymentMethod
PaymentStatus
Order
User
Value Objects
PaymentId
TransactionId
Money
Reglas de Negocio
Validación de montos.
Validación del estado de la orden.
Reglas para aprobar o rechazar pagos.
Cálculo de comisiones si aplica.
2. Use Cases (Application Business Rules)
Orquesta el flujo de negocio sin depender de tecnologías externas.

Input Boundary
ProcessPaymentUseCase
DTOs
ProcessPaymentRequest
ProcessPaymentResponse
Interfaces (Puertos de Salida)
Persistencia
PaymentRepository
TransactionRepository
Integraciones Externas
PaymentGateway
NotificationService
Implementación del Caso de Uso
ProcessPaymentInteractor
Responsabilidades
Recibir la solicitud de pago.
Validar reglas de negocio.
Ejecutar el cobro mediante PaymentGateway.
Registrar Payment y Transaction mediante repositorios.
Generar eventos de negocio.
Solicitar envío de notificaciones.
3. Interface Adapters
Transforma datos entre el mundo externo y los casos de uso.

Controllers / Presenters
Controlador
PaymentController
Responsabilidad:

Recibir solicitudes desde JavaFX o API REST.
Construir ProcessPaymentRequest.
Invocar ProcessPaymentUseCase.
Presenter
PaymentPresenter
Responsabilidad:

Convertir ProcessPaymentResponse a un modelo de vista.
View Model
PaymentViewModel
Adaptadores de Persistencia
Implementan los puertos definidos en Use Cases.

PostgreSqlPaymentRepository
PostgreSqlTransactionRepository
Implementan:

PaymentRepository
TransactionRepository
Adaptadores de Pasarelas
CreditCardGatewayAdapter
PSEGatewayAdapter
PayPalGatewayAdapter
Implementan:

PaymentGateway
Adaptadores de Notificación
EmailNotificationAdapter
Implementa:

NotificationService
4. Frameworks & Drivers
Capa más externa.

Framework Web / JavaFX
Pantallas JavaFX
REST Controllers (si existen)
Persistencia
PostgreSQL
JDBC
Spring Data JPA
Servicios Externos
API Tarjeta de Crédito
API PSE
API PayPal
Servidor SMTP
Configuración
DependencyInjectionConfig
Spring Configuration Classes
Diagrama de Arquitectura (Mermaid)
Entities

Use_Cases

Interface_Adapters

Frameworks_and_Drivers

JavaFX UI

PostgreSQL

SMTP Server

PayPal API

PSE API

Credit Card API

PaymentController

PaymentPresenter

PostgreSqlPaymentRepository

PostgreSqlTransactionRepository

PayPalGatewayAdapter

PSEGatewayAdapter

CreditCardGatewayAdapter

EmailNotificationAdapter

ProcessPaymentInteractor

PaymentRepository

TransactionRepository

PaymentGateway

NotificationService

Payment

Transaction

PaymentStatus

PaymentMethod





Verificación de la Regla de Dependencia
Las dependencias siempre apuntan hacia adentro:

Frameworks & Drivers → Interface Adapters → Use Cases → Entities

Ninguna entidad conoce bases de datos, APIs, JavaFX, Spring o PostgreSQL.

Los casos de uso dependen únicamente de interfaces (puertos), nunca de implementaciones concretas.

Estrategia de Inyección de Dependencias
Problema
El diseño actual concentra responsabilidades en PaymentService, generando acoplamiento con:

PostgreSQL
Email
Pasarelas de pago
Respuestas HTTP/JavaFX
Esto viola SRP, DIP y Clean Architecture.

Solución
Los casos de uso dependen únicamente de abstracciones.

Dependencias requeridas por ProcessPaymentInteractor
PaymentRepository
TransactionRepository
PaymentGateway
NotificationService
El constructor recibe estas dependencias:

ProcessPaymentInteractor(
    PaymentRepository,
    TransactionRepository,
    PaymentGateway,
    NotificationService
)
Inyección de Repositorios
PaymentRepository
        ↑
PostgreSqlPaymentRepository
El caso de uso no conoce PostgreSQL ni JPA.

Inyección de Pasarelas
PaymentGateway
       ↑
 ├─ CreditCardGatewayAdapter
 ├─ PSEGatewayAdapter
 └─ PayPalGatewayAdapter
Una fábrica o estrategia selecciona el adaptador adecuado según el método de pago.

Inyección de Notificaciones
NotificationService
         ↑
EmailNotificationAdapter
Si en el futuro se agregan SMS o WhatsApp:

NotificationService
     ↑
 ├─ EmailNotificationAdapter
 ├─ SmsNotificationAdapter
 └─ WhatsAppNotificationAdapter
El caso de uso permanece intacto.

Composición Final
En la capa Frameworks & Drivers se construye el grafo de objetos:

JavaFX / Spring Configuration
            │
            ▼
ProcessPaymentInteractor
            │
    ├── PostgreSqlPaymentRepository
    ├── PostgreSqlTransactionRepository
    ├── PayPalGatewayAdapter
    └── EmailNotificationAdapter
De esta manera:

La lógica de negocio queda aislada.
Los adaptadores pueden cambiar sin modificar casos de uso.
La base de datos puede reemplazarse sin afectar el dominio.
Las pasarelas externas pueden sustituirse fácilmente.
JavaFX y la capa web permanecen desacopladas de la lógica de negocio.
```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

 Listar las responsabilidades originales de la clase monolítica (PaymentService) y exigir un rediseño estricto funcionó bastante bien. Pedir un diagrama en Mermaid con las direcciones de las flechas obligó a la IA a demostrar visualmente la Regla de Dependencia, un concepto central que analizamos constantemente en las clases de Arquitectura de Software. Prohibirle escribir código en Java también fue clave; de esta forma evitamos revisar cientos de líneas autogeneradas y logramos enfocarnos netamente en la estructura de puertos y adaptadores.

#### 2. ¿Qué se puede mejorar?

 Aunque el diagrama general sirve como guía inicial, noté varias fallas conceptuales respecto a la bibliografía formal del curso:

Primero, olvidó el Input Boundary. En su propuesta de inyección de dependencias, sugiere que el entorno (la configuración o JavaFX) inyecte directamente la clase ProcessPaymentInteractor. Esto rompe la regla de aislamiento: el controlador debe depender de la interfaz del caso de uso (el Input Port), jamás de su implementación concreta.

Segundo, el diagrama dejó al Presentador desconectado. La gráfica muestra al controlador llamando al caso de uso, y luego deja una flecha suelta desde el presentador hacia la interfaz gráfica. Omitió por completo el Output Port. El caso de uso debe recibir ese puerto en su ejecución para enviarle el modelo de respuesta, sin retornar los datos directamente al controlador.

Tercero, sobrecargó el caso de uso con decisiones de infraestructura. La IA sugirió que el caso de uso maneje una fábrica o estrategia para elegir la pasarela de pago. Si la selección depende del entorno (como Spring), el caso de uso se contamina. El diseño correcto requiere un único adaptador (PaymentGatewayProcessor) que resuelva internamente el enrutamiento hacia PayPal o PSE, manteniendo el caso de uso completamente limpio.

#### 3. Respuesta final
La distribución que plantea el modelo sirve como base de estudio estructural. Extrae correctamente las reglas de negocio a las Entities, delega la orquestación al Interactor y aísla la persistencia y los correos en sus respectivos adaptadores. En el diagrama se aprecia cómo las dependencias fluyen de afuera hacia adentro.

Sin embargo, para presentar un diagrama sólido en una sustentación o proyecto final, yo reestructuraría el flujo de salida. El PaymentController recibe la petición y llama a la interfaz del caso de uso. El interactor procesa el pago y, al terminar, le pasa un objeto plano de datos (DTO) a un PaymentOutputPort. Es el PaymentPresenter, implementando ese puerto, quien se encarga de moldear la respuesta final para que la consuma JavaFX o un formato JSON. Con esa corrección logramos un flujo unidireccional y eliminamos los cabos sueltos que dejó la IA.
