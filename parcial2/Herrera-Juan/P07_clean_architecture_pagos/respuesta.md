# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [7]: [clean arquitecture pagos]

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

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

Eres un arquitecto de software experto en Clean Architecture, SOLID y sistemas de pagos.

Analiza el siguiente escenario:

OpenLib Market tiene actualmente una clase PaymentService con un único método realizarPago() que se encarga de procesar el pago, comunicarse con la pasarela de pago, registrar la transacción en PostgreSQL, enviar correos al comprador y vendedor, actualizar el estado de la orden y preparar la entrega o descarga del producto.

Este diseño presenta alto acoplamiento y viola varios principios de Clean Architecture.

Rediseña completamente este módulo aplicando Clean Architecture.

Tu respuesta debe incluir:

1. Identificar qué responsabilidades están mezcladas en el diseño actual.
2. Definir qué componentes pertenecen a cada capa:

   * Entities
   * Use Cases
   * Interface Adapters
   * Frameworks & Drivers
3. Explicar cómo se aplica la Dependency Rule.
4. Mostrar cómo se aplica el principio de inversión de dependencias (DIP).
5. Diseñar las interfaces necesarias para desacoplar:

   * Pasarelas de pago (Tarjeta, PSE, PayPal)
   * Persistencia PostgreSQL
   * Servicio de correo
6. Incluir un diagrama de arquitectura en Mermaid o ASCII.
7. Mostrar ejemplos de código Java para las clases e interfaces principales.
8. Explicar cómo se inyectan las dependencias externas sin violar Clean Architecture.
9. Comparar el diseño propuesto con la versión original basada en PaymentService.
10. Analizar ventajas en mantenibilidad, escalabilidad y testabilidad.

La respuesta debe ser técnica, detallada y utilizar ejemplos concretos relacionados con OpenLib Market.


---

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**
Rediseño del Módulo de Pagos de OpenLib Market con Clean Architecture
1. Problemas del Diseño Actual

Actualmente existe una única clase:

public class PaymentService {

    public void realizarPago() {
        // Procesar pago
        // Conectar PayPal/PSE/Tarjeta
        // Guardar en PostgreSQL
        // Enviar correos
        // Actualizar orden
        // Preparar entrega
    }
}

Esta clase concentra demasiadas responsabilidades.

Responsabilidades mezcladas
Responsabilidad	Tipo
Procesar pago	Negocio
Conectarse a PayPal/PSE	Infraestructura
Guardar transacción	Persistencia
Enviar correos	Comunicación
Actualizar orden	Negocio
Preparar entrega	Logística
Manejo de errores externos	Infraestructura
Principios Violados
SRP (Single Responsibility Principle)

Tiene múltiples razones para cambiar:

Cambio en PayPal.
Cambio en PostgreSQL.
Cambio en correos.
Cambio en la lógica de órdenes.
OCP (Open/Closed Principle)

Agregar:

Nequi
Daviplata
Stripe

obliga a modificar PaymentService.

DIP (Dependency Inversion Principle)

Depende directamente de:

PayPalSDK
PostgreSQL
SMTP

en lugar de depender de abstracciones.

2. Diseño Basado en Clean Architecture
Capa 1: Entities

Representan las reglas centrales del dominio.

Pago
public class Pago {

    private String id;
    private double monto;
    private EstadoPago estado;

}
Orden
public class Orden {

    private String id;
    private EstadoOrden estado;

}
Usuario
public class Usuario {

    private String email;

}
Responsabilidad

Contener:

Estado del negocio.
Reglas esenciales.

No conocen:

PostgreSQL
Spring
SMTP
PayPal
Capa 2: Use Cases

Aquí vive la lógica de negocio.

Caso de Uso Principal
ProcesarPagoUseCase
Interfaces Definidas por el Caso de Uso
Pasarela de Pago
public interface PaymentGateway {

    ResultadoPago procesar(
        Pago pago
    );
}
Repositorio
public interface PagoRepository {

    void guardar(Pago pago);

}
Servicio de Correo
public interface NotificationService {

    void enviar(
        String destino,
        String asunto,
        String mensaje
    );
}
Servicio de Entrega
public interface DeliveryService {

    void prepararEntrega(
        Orden orden
    );
}
Caso de Uso
public class ProcesarPagoUseCase {

    private PaymentGateway gateway;

    private PagoRepository repository;

    private NotificationService notification;

    private DeliveryService delivery;

    public ProcesarPagoUseCase(
            PaymentGateway gateway,
            PagoRepository repository,
            NotificationService notification,
            DeliveryService delivery) {

        this.gateway = gateway;
        this.repository = repository;
        this.notification = notification;
        this.delivery = delivery;
    }

    public void ejecutar(
            Pago pago,
            Orden orden,
            Usuario comprador,
            Usuario vendedor) {

        ResultadoPago resultado =
                gateway.procesar(pago);

        if (!resultado.esExitoso()) {
            throw new RuntimeException(
                "Pago rechazado");
        }

        repository.guardar(pago);

        orden.marcarPagada();

        notification.enviar(
                comprador.getEmail(),
                "Compra realizada",
                "Tu compra fue exitosa"
        );

        notification.enviar(
                vendedor.getEmail(),
                "Venta realizada",
                "Has vendido un libro"
        );

        delivery.prepararEntrega(orden);
    }
}
Capa 3: Interface Adapters

Implementan los contratos definidos por los casos de uso.

PostgreSQL
public class PostgreSQLPagoRepository
        implements PagoRepository {

    @Override
    public void guardar(Pago pago) {

        // JDBC o JPA

    }
}
PayPal
public class PayPalGateway
        implements PaymentGateway {

    @Override
    public ResultadoPago procesar(
            Pago pago) {

        // API PayPal

        return new ResultadoPago(true);
    }
}
Tarjeta
public class TarjetaGateway
        implements PaymentGateway {

    @Override
    public ResultadoPago procesar(
            Pago pago) {

        // Visa / Mastercard

        return new ResultadoPago(true);
    }
}
PSE
public class PSEGateway
        implements PaymentGateway {

    @Override
    public ResultadoPago procesar(
            Pago pago) {

        // Integración PSE

        return new ResultadoPago(true);
    }
}
Correo SMTP
public class EmailServiceSMTP
        implements NotificationService {

    @Override
    public void enviar(
            String destino,
            String asunto,
            String mensaje) {

        // SMTP
    }
}
Entrega Física / Digital
public class OpenLibDeliveryService
        implements DeliveryService {

    @Override
    public void prepararEntrega(
            Orden orden) {

        // Logística
    }
}
Capa 4: Frameworks & Drivers

Aquí viven los detalles tecnológicos.

Framework
Spring Boot
Persistencia
PostgreSQL
Hibernate
Mensajería
SMTP
Pagos
PayPal SDK
PSE API
Visa API
3. Dependency Rule
Regla Fundamental

Las dependencias siempre apuntan hacia el centro.

Frameworks
      ↓
Adapters
      ↓
Use Cases
      ↓
Entities
Incorrecto
public class ProcesarPagoUseCase {

    private PostgreSQLRepository repo;

}

Porque Use Cases depende de infraestructura.

Correcto
private PagoRepository repository;

Depende de una abstracción.

4. Aplicación de DIP
Las interfaces las define Use Cases
PaymentGateway
PagoRepository
NotificationService
DeliveryService
Las implementaciones viven afuera
PayPalGateway

PSEGateway

PostgreSQLPagoRepository

EmailServiceSMTP
Flujo
Use Case
      ↓
PaymentGateway (interface)
      ↑
PayPalGateway
5. Diagrama de Arquitectura
┌──────────────────────────────────────────┐
│        Frameworks & Drivers              │
│------------------------------------------│
│ Spring Boot                              │
│ PostgreSQL                               │
│ SMTP                                     │
│ PayPal SDK                               │
│ PSE API                                  │
└──────────────────────────────────────────┘
                    │
                    ▼
┌──────────────────────────────────────────┐
│         Interface Adapters               │
│------------------------------------------│
│ PayPalGateway                            │
│ PSEGateway                               │
│ TarjetaGateway                           │
│ PostgreSQLPagoRepository                 │
│ EmailServiceSMTP                         │
│ OpenLibDeliveryService                   │
└──────────────────────────────────────────┘
                    │
                    ▼
┌──────────────────────────────────────────┐
│              Use Cases                   │
│------------------------------------------│
│ ProcesarPagoUseCase                      │
│ PaymentGateway                           │
│ PagoRepository                           │
│ NotificationService                      │
│ DeliveryService                          │
└──────────────────────────────────────────┘
                    │
                    ▼
┌──────────────────────────────────────────┐
│                Entities                  │
│------------------------------------------│
│ Pago                                     │
│ Orden                                    │
│ Usuario                                  │
│ ResultadoPago                            │
└──────────────────────────────────────────┘
6. Inyección de Dependencias

El ensamblaje ocurre en la capa externa.

Ejemplo con Spring:

@Configuration
public class PaymentConfig {

    @Bean
    public ProcesarPagoUseCase useCase() {

        return new ProcesarPagoUseCase(
                new PayPalGateway(),
                new PostgreSQLPagoRepository(),
                new EmailServiceSMTP(),
                new OpenLibDeliveryService()
        );
    }
}

El caso de uso nunca crea:

new PayPalGateway()

por sí mismo.

7. Comparación con el Diseño Original
Antes
PaymentService
 ├─ PayPal
 ├─ PostgreSQL
 ├─ SMTP
 ├─ Orden
 └─ Entrega

Todo acoplado.

Después
ProcesarPagoUseCase
        │
        ├── PaymentGateway
        ├── PagoRepository
        ├── NotificationService
        └── DeliveryService

Todo desacoplado mediante interfaces.

8. Ventajas
Mantenibilidad

Cambiar:

PayPal → Stripe

solo requiere:

StripeGateway
Escalabilidad

Agregar:

Nequi
Daviplata
Apple Pay

requiere nuevas implementaciones de:

PaymentGateway

sin modificar el caso de uso.

Testabilidad

Podemos probar:

ProcesarPagoUseCase

usando mocks:

FakePaymentGateway
FakeRepository
FakeEmailService

sin:

PostgreSQL
SMTP
PayPal
Bajo Acoplamiento

Las reglas de negocio no conocen:

Spring
JPA
PostgreSQL
SMTP
PayPal
Cumplimiento SOLID
SRP

Cada clase tiene una única responsabilidad.

OCP

Nuevos medios de pago se agregan por extensión.

LSP

Cualquier implementación de PaymentGateway puede sustituirse.

ISP

Interfaces pequeñas y específicas.

DIP

Los casos de uso dependen de abstracciones.

Conclusión

El rediseño transforma un PaymentService monolítico y altamente acoplado en una solución alineada con Clean Architecture, donde las entidades contienen el dominio, los casos de uso orquestan la lógica de negocio y las dependencias externas (PayPal, PostgreSQL, SMTP) quedan aisladas detrás de interfaces. Gracias a la Dependency Rule y al Dependency Inversion Principle, OpenLib Market obtiene un módulo de pagos mucho más mantenible, escalable, testeable y preparado para evolucionar sin afectar el núcleo del negocio.

---

### Análisis crítico de la respuesta

La respuesta ubica correctamente los componentes dentro de las capas de Clean Architecture. Las entidades contienen únicamente información del dominio, los casos de uso concentran la lógica de negocio y las implementaciones concretas de pago, persistencia y correo se encuentran en capas externas. La regla de dependencia se respeta porque el caso de uso depende de interfaces y no de tecnologías concretas como PostgreSQL o PayPal. Sin embargo, la respuesta omite mostrar explícitamente el controlador REST mencionado en el enunciado, que debería ubicarse en Interface Adapters. Aun así, el diseño es mucho más testeable que la versión original, ya que permite utilizar mocks para probar la lógica de negocio sin depender de bases de datos, servicios de correo o pasarelas de pago reales. En general, la propuesta aplica correctamente Clean Architecture y reduce significativamente el acoplamiento del sistema.


#### 1. ¿Qué hizo bien el prompt?

El prompt fue efectivo porque pidió explícitamente identificar las responsabilidades mezcladas en la clase PaymentService y redistribuirlas según las capas de Clean Architecture. Además, solicitó la explicación de la Dependency Rule, el uso del principio de inversión de dependencias, ejemplos de código y un diagrama de arquitectura, lo que permitió obtener una propuesta completa y bien estructurada. La parte más útil fue exigir la ubicación de cada componente en su capa correspondiente y la explicación de cómo inyectar dependencias externas sin violar la arquitectura.



#### 2. ¿Qué se puede mejorar?

El prompt podría mejorarse solicitando explícitamente la inclusión del controlador REST dentro de la arquitectura y mostrando el flujo completo desde la petición del frontend JavaFX hasta el caso de uso. También habría sido útil pedir DTOs de entrada y salida para ilustrar mejor la comunicación entre capas. Aunque la respuesta fue correcta, no profundizó en aspectos como manejo de transacciones, eventos de dominio o procesamiento asíncrono de notificaciones.



#### 3. Respuesta final

El módulo de pagos debe dividirse siguiendo las capas de Clean Architecture. Las entidades incluyen objetos del dominio como Pago, Orden y Usuario; los casos de uso contienen la lógica de negocio, por ejemplo ProcesarPagoUseCase; los adaptadores implementan interfaces como PaymentGateway, PagoRepository y NotificationService; y la capa externa contiene tecnologías como Spring Boot, PostgreSQL, SMTP y las APIs de PayPal o PSE. La regla de dependencia se cumple porque los casos de uso dependen únicamente de abstracciones y no de implementaciones concretas. Gracias a este diseño, el sistema es más mantenible, extensible y testeable que la versión original basada en una única clase PaymentService altamente acoplada.
