# Pregunta P07: Clean Architecture — Aplicación a pagos

### Estudiante
- **Nombre completo**: Mateo Traslaviña Moreno

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Claude |
| **Modelo especifico** | Claude Sonnet 4.6 |
| **¿Por que elegiste este LLM?** | El diseño de modulos con Clean Architecture requiere razonamiento sobre estructura de capas y regla de dependencia. Claude genera diagramas Mermaid correctos y codigo Java idiomatico con Spring Boot. Tras la experiencia de P06, quise comparar mi comprension del tema con como un LLM lo aplica en un caso concreto. |

---

### Prompt utilizado

```
Eres un arquitecto de software senior rediseñando el módulo de pagos de OpenLib Market (Java 21, Spring Boot 3.x, JavaFX frontend, PostgreSQL).

Contexto del problema: actualmente el equipo tiene todo acoplado en una sola clase PaymentService:
- Llamar a la pasarela de pago externa (tarjeta, PSE, PayPal)
- Enviar correo de confirmación al comprador y al vendedor
- Cambiar el estado de la orden en la base de datos
- Preparar la entrega/descarga del libro

Necesito que rediseñes este módulo aplicando Clean Architecture (Robert C. Martin):

Entregables requeridos:
1. Definir qué va en CADA capa (Entities, Use Cases, Interface Adapters, Frameworks & Drivers) con ejemplos concretos de clases para el módulo de pagos.
2. Diagrama de la estructura en Mermaid mostrando las dependencias entre capas.
3. Código Java de las clases principales: la entidad Pago, el use case ProcesarPagoUseCase, el repositorio de pagos (interfaz en Use Cases, implementación JPA en Adapters), y el controller REST.
4. Explicar cómo se inyectan las dependencias externas (pasarela de pago, email) sin violar la regla de dependencia.
5. Comparar testabilidad: ¿por qué el nuevo diseño es más testeable que el PaymentService original?

Restricciones críticas:
- Las entidades NO deben importar nada de Spring/JPA (sin @Entity en la capa de dominio)
- Los Use Cases deben depender solo de interfaces que ellos mismos definen
- El controller REST va en Interface Adapters, no en Use Cases
- La regla de dependencia debe respetarse en TODOS los límites de capa
- Usar inyección por constructor en todas partes

Formato: "Distribución por capas", "Diagrama Mermaid", "Código principal", "Inyección de dependencias", "Testabilidad".
```

---

### Respuesta del LLM

## Distribución por capas

**Entities (Dominio)**:
- `Pago` — entidad con lógica de validación (importe positivo, método no nulo)
- `Orden` — entidad con estado (PENDIENTE → PAGADA → ENTREGADA)
- `ResultadoPago` — value object inmutable con el resultado de una transacción
- `MetodoPago` — enum (TARJETA, PSE, PAYPAL)
- `EstadoOrden` — enum (PENDIENTE, PAGADA, ENTREGADA, CANCELADA)

**Use Cases (Application)**:
- `ProcesarPagoUseCase` — caso de uso principal
- `PagoRepository` — interfaz (puerto de salida)
- `PasarelaPagoPort` — interfaz para pasarela externa
- `NotificacionPort` — interfaz para emails
- `OrdenRepository` — interfaz

**Interface Adapters**:
- `PagoController` — REST endpoint `/api/v1/pagos`
- `PagoJpaGateway` implements `PagoRepository`
- `PagoJpaEntity` — entidad JPA (separada de `Pago` del dominio)
- `StripeGateway` implements `PasarelaPagoPort`
- `EmailNotificacionAdapter` implements `NotificacionPort`
- `PagoMapper` — convierte entre `Pago` y `PagoJpaEntity`

**Frameworks & Drivers**:
- Spring Boot, JPA, PostgreSQL driver
- Stripe SDK, Jakarta Mail
- `PagoJpaRepository extends JpaRepository`

## Diagrama Mermaid

```mermaid
graph TD
    subgraph "Frameworks & Drivers"
        FC[PagoJpaRepository<br/>JpaRepository]
        FS[Stripe SDK]
        FM[Jakarta Mail]
    end
    
    subgraph "Interface Adapters"
        AC[PagoController]
        AG[PagoJpaGateway]
        AS[StripeGateway]
        AE[EmailNotificacionAdapter]
        AM[PagoMapper]
    end
    
    subgraph "Use Cases"
        UC[ProcesarPagoUseCase]
        UP[PagoRepository interfaz]
        UPP[PasarelaPagoPort interfaz]
        UN[NotificacionPort interfaz]
    end
    
    subgraph "Entities"
        EP[Pago]
        EO[Orden]
        ER[ResultadoPago]
    end

    AC --> UC
    AG --> FC
    AG .->|implements| UP
    AS --> FS
    AS .->|implements| UPP
    AE --> FM
  
