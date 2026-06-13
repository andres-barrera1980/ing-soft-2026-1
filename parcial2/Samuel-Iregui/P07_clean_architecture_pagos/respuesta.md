# Plantilla de entrega — Parcial 2

---

## Pregunta 7: Diseñar un módulo aplicando Clean Architecture a OpenLib Market

### Estudiante
- **Nombre completo**: Samuel Iregui

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Antigravity |
| **Modelo específico** | Gemini 3.5 Flash |
| **¿Por qué elegiste este LLM?** | Elegi este modelo por comodidad propia y debido a que tengo pago gemini pro y ya tengo todo bien organizado para los diferentes trabajos |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```text
Actúa como un arquitecto de software senior. En OpenLib Market tenemos un módulo de gestión de pagos sumamente acoplado. Toda la lógica reside en una sola clase monolítica llamada PaymentService y en su método realizarPago(), el cual realiza llamadas directas a APIs de pasarelas, persiste a PostgreSQL usando JPA y envía correos mediante librerías acopladas de SMTP.

Necesito rediseñar este módulo aplicando rigurosamente Clean Architecture.
Haz lo siguiente:
1. Explica qué clases, interfaces y componentes irán en cada una de las 4 capas de la arquitectura (Entities, Use Cases, Interface Adapters, y Frameworks & Drivers).
2. Proporciona un diagrama Mermaid que ilustre la estructura de dependencias de este módulo de pagos, asegurando que se respete la Dependency Rule (las dependencias apuntan hacia adentro).
3. Escribe el código en Java 21 para los componentes centrales, demostrando cómo se aplica el principio de Inversión de Dependencia (DIP) para desacoplar el caso de uso de la base de datos (PostgreSQL) y el envío de correos.
4. Explica detalladamente cómo funciona la inyección de dependencias en este diseño para conectar las implementaciones de infraestructura sin violar el aislamiento de las capas internas.

Formato de salida esperado: Documento Markdown con las secciones: "Estructura por Capas", "Diagrama de Arquitectura", "Implementación Java 21" y "Estrategia de Inyección".
```

---

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

### Estructura por Capas

Para eliminar el acoplamiento del monolito `PaymentService`, distribuimos el código en las siguientes capas concéntricas:

1.  **Entidades (Entities)**:
    *   `Pago` (record): Representa el estado del pago, montos, impuestos e ID de transacción. Contiene lógica del negocio independiente (como validaciones de montos no negativos).
    *   `OrdenCompra`: Entidad que relaciona los libros comprados con el comprador y el total a cobrar.
2.  **Casos de Uso (Use Cases)**:
    *   `ProcesarPagoUseCase` (clase): Contiene la lógica para orquestar la transacción: validar orden, llamar a la pasarela, registrar la transacción y notificar.
    *   **Puertos (Interfaces)**: `PasarelaPagoPort`, `TransaccionRepositoryPort`, `NotificadorEmailPort`.
3.  **Adaptadores de Interfaz (Interface Adapters)**:
    *   `PagoRestController` (Controller REST): Expone los endpoints HTTP.
    *   `PostgresTransaccionRepositoryAdapter` (implementa `TransaccionRepositoryPort` mapeando a entidades JPA).
    *   `SmtpNotificadorAdapter` (implementa `NotificadorEmailPort`).
    *   `PasarelaPagoFactory` y adaptadores individuales (`TarjetaPagoAdapter`, `PsePagoAdapter`, `PaypalPagoAdapter`).
4.  **Frameworks & Drivers**:
    *   Spring Boot (Inyección de dependencias y Spring MVC).
    *   Spring Data JPA & PostgreSQL Driver.
    *   Firebase SDK / JavaMail SMTP.

---

### Diagrama de Arquitectura

```mermaid
graph TD
    subgraph Capa_Frameworks [Frameworks & Drivers]
        SpringController[PagoRestController]
        SpringData[SpringDataJpaRepository]
        PostgresDB[(PostgreSQL Database)]
        SmtpServer[SMTP Server]
    end

    subgraph Capa_Adapters [Interface Adapters]
        PagoController[PagoControllerAdapter]
        RepoAdapter[PostgresTransaccionRepositoryAdapter]
        MailAdapter[SmtpNotificadorAdapter]
    end

    subgraph Capa_UseCases [Use Cases]
        UseCase[ProcesarPagoUseCase]
        IRepo[TransaccionRepositoryPort]
        IMail[NotificadorEmailPort]
        IPago[PasarelaPagoPort]
    end

    subgraph Capa_Entities [Entities]
        EntityPago[Pago]
    end

    %% Relaciones
    SpringController --> PagoController
    PagoController --> UseCase
    UseCase --> EntityPago
    
    %% Inversión de dependencias (DIP)
    RepoAdapter ..|> IRepo
    RepoAdapter --> SpringData
    SpringData --> PostgresDB
    
    MailAdapter ..|> IMail
    MailAdapter --> SmtpServer

    UseCase --> IRepo
    UseCase --> IMail
    UseCase --> IPago
```

---

### Implementación Java 21

#### 1. Capa de Entidades
```java
public record Pago(String id, String ordenId, BigDecimal monto, String metodoPago) {
    public Pago {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser positivo");
        }
    }
}
```

#### 2. Capa de Casos de Uso (Puertos y Casos de Uso)
```java
// Puertos (Interfaces)
public interface TransaccionRepositoryPort {
    void guardar(Pago pago);
}

public interface NotificadorEmailPort {
    void enviarConfirmacion(Pago pago, String emailComprador, String emailVendedor);
}

public interface PasarelaPagoPort {
    boolean procesar(Pago pago);
    boolean soporta(String metodo);
}

// Caso de Uso
public class ProcesarPagoUseCase {
    private final TransaccionRepositoryPort repository;
    private final NotificadorEmailPort notificador;
    private final List<PasarelaPagoPort> pasarelas;

    public ProcesarPagoUseCase(TransaccionRepositoryPort repository, 
                               NotificadorEmailPort notificador, 
                               List<PasarelaPagoPort> pasarelas) {
        this.repository = repository;
        this.notificador = notificador;
        this.pasarelas = pasarelas;
    }

    public void ejecutar(Pago pago, String emailComprador, String emailVendedor) {
        PasarelaPagoPort pasarelaSeleccionada = pasarelas.stream()
                .filter(p -> p.soporta(pago.metodoPago()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Método no soportado: " + pago.metodoPago()));

        boolean exito = pasarelaSeleccionada.procesar(pago);
        if (!exito) {
            throw new IllegalStateException("El cobro en la pasarela falló");
        }

        repository.guardar(pago);
        notificador.enviarConfirmacion(pago, emailComprador, emailVendedor);
    }
}
```

#### 3. Capa de Adaptadores (Implementaciones concretas)
```java
// Adaptador de Persistencia
public class PostgresTransaccionRepositoryAdapter implements TransaccionRepositoryPort {
    private final JpaTransaccionRepository jpaRepository;

    public PostgresTransaccionRepositoryAdapter(JpaTransaccionRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void guardar(Pago pago) {
        TransaccionEntity entity = new TransaccionEntity(pago.id(), pago.ordenId(), pago.monto(), pago.metodoPago());
        jpaRepository.save(entity);
    }
}
```

---

### Estrategia de Inyección

Para mantener la capa de Casos de Uso pura y libre de librerías externas de frameworks, la configuración de la inyección de dependencias se realiza en la capa más externa mediante una clase de configuración de Spring Boot:

```java
@Configuration
public class PagoConfig {

    @Bean
    public ProcesarPagoUseCase procesarPagoUseCase(
            TransaccionRepositoryPort repository,
            NotificadorEmailPort notificador,
            List<PasarelaPagoPort> pasarelas) {
        return new ProcesarPagoUseCase(repository, notificador, pasarelas);
    }
}
```

Spring Boot resuelve de manera transparente:
1. Recolectar todas las pasarelas que implementen `PasarelaPagoPort` e inyectarlas como lista al caso de uso.
2. Inyectar los adaptadores de persistencia y correos mediante el constructor del caso de uso.

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

El prompt define de manera clara los requerimientos funcionales del módulo de pagos (múltiples métodos de pago, base de datos PostgreSQL, notificaciones por correo y API REST) y reta al modelo a descomponer el método monolítico `realizarPago()` siguiendo Clean Architecture. Pedir explícitamente cómo inyectar las dependencias sin violar la arquitectura fue la clave para evaluar la separación técnica y la inversión de dependencias.

#### 2. ¿Qué se puede mejorar?

Faltó especificar si queríamos que el modelo considerara el patrón Factory o Strategy para seleccionar la pasarela de pago correcta dentro del caso de uso. El LLM resolvió esto inyectando una lista de implementaciones de `PasarelaPagoPort` y filtrando en base al método `soporta()`, lo cual es una solución elegante, pero debió considerarse una restricción del diseño del puerto desde el prompt.

#### 3. Respuesta final

En conclusión, el rediseño planteado por el LLM aplica correctamente los principios de Clean Architecture y la inversión de dependencias (DIP). La lógica de pago queda separada de la infraestructura, distribuyendo las responsabilidades en capas bien definidas y evitando dependencias directas con frameworks o tecnologías específicas.

El caso de uso trabaja únicamente con interfaces, mientras que la base de datos, el correo y otros servicios se implementan mediante adaptadores externos. Además, la configuración de dependencias se mantiene fuera de la lógica de negocio, lo que facilita el mantenimiento y la escalabilidad.

Gracias a esta separación, el proceso de pago puede probarse de forma aislada utilizando mocks, sin necesidad de conectarse a una base de datos real ni a servicios externos durante las pruebas.
