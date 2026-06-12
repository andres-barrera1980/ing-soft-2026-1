## P07_clean_architecture_pagos

### Estudiante
- **Nombre completo**: [Alejandro Macias Barrios]

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [Claude] |
| **Modelo específico** | [Claude Sonnet 4.6] |
| **¿Por qué elegiste este LLM?** | [Diseñar un módulo completo con Clean Architecture requiere generar diagramas y código coherente entre sí. Claude mantiene consistencia en los nombres de clases a lo largo de una respuesta larga, lo que facilita el análisis posterior.] |


### Prompt utilizado
Contexto: OpenLib Market, plataforma de compraventa de libros en Java 21.
El módulo de pagos actual tiene todo acoplado en una clase PaymentService con un método 
realizarPago() que: llama a la pasarela de pago, envía correo de notificación al comprador 
y vendedor, cambia el estado de la orden, y prepara la entrega o descarga del libro.

El módulo debe soportar:
- Múltiples métodos de pago: tarjeta, PSE, PayPal.
- Persistencia en PostgreSQL.
- Notificación por correo al comprador y vendedor.
- API REST consumida por un frontend JavaFX.

Tareas:
1. Rediseña este módulo aplicando Clean Architecture (Entidades, Casos de Uso, 
   Adaptadores de Interfaz, Frameworks & Drivers).
2. Para cada capa, especifica qué clases/interfaces van ahí y por qué.
3. Dibuja un diagrama ASCII o Mermaid de la estructura de carpetas y dependencias.
4. Explica cómo se inyectan las dependencias externas respetando la regla de dependencia.
5. Compara la testabilidad del diseño nuevo vs el PaymentService original.
6. Responde en Java 21, en markdown con secciones claras.]

### Respuesta del LLM

[Estructura por capas
Capa 1: Entities
entities/
├── Pago.java          // Entidad con reglas: monto > 0, estado válido
├── Orden.java         // Entidad con estado: PENDIENTE, PAGADA, CANCELADA
├── EstadoPago.java    // Enum: PENDIENTE, APROBADO, RECHAZADO
└── ResultadoPago.java // Value object inmutable

public record Pago(Long id, BigDecimal monto, String moneda, EstadoPago estado) {
    public Pago {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Monto debe ser positivo");
    }
}

public enum EstadoPago { PENDIENTE, APROBADO, RECHAZADO }

public record ResultadoPago(boolean exitoso, String referencia, String mensaje) {}

Capa 2: Use Cases
usecases/
├── ProcesarPagoUseCase.java       // Orquesta el flujo completo
├── PagoRepositorio.java           // Interfaz definida aquí (no la implementación)
├── PasarelaPagoGateway.java       // Interfaz para la pasarela externa
├── NotificadorPago.java           // Interfaz para notificaciones
└── OrdenRepositorio.java          // Interfaz para órdenes

// Interfaz del repositorio — vive en Use Cases, no en Adapters
public interface PagoRepositorio {
    void guardar(Pago pago);
    Optional<Pago> buscarPorId(Long id);
}

public interface PasarelaPagoGateway {
    ResultadoPago procesar(Pago pago, String metodoPago);
}

public interface NotificadorPago {
    void notificarCompradorYVendedor(Orden orden, ResultadoPago resultado);
}

// Caso de uso
public class ProcesarPagoUseCase {
    private final PagoRepositorio pagoRepo;
    private final PasarelaPagoGateway pasarela;
    private final NotificadorPago notificador;
    private final OrdenRepositorio ordenRepo;

    public ProcesarPagoUseCase(
        PagoRepositorio pagoRepo,
        PasarelaPagoGateway pasarela,
        NotificadorPago notificador,
        OrdenRepositorio ordenRepo
    ) {
        this.pagoRepo = pagoRepo;
        this.pasarela = pasarela;
        this.notificador = notificador;
        this.ordenRepo = ordenRepo;
    }

    public ResultadoPago ejecutar(Pago pago, String metodoPago, Long ordenId) {
        ResultadoPago resultado = pasarela.procesar(pago, metodoPago);

        Pago pagoFinal = new Pago(pago.id(), pago.monto(), pago.moneda(),
            resultado.exitoso() ? EstadoPago.APROBADO : EstadoPago.RECHAZADO);
        pagoRepo.guardar(pagoFinal);

        Orden orden = ordenRepo.buscarPorId(ordenId)
            .orElseThrow(() -> new NoSuchElementException("Orden no encontrada"));

        if (resultado.exitoso()) {
            orden.marcarComoPagada();
            ordenRepo.guardar(orden);
        }

        notificador.notificarCompradorYVendedor(orden, resultado);
        return resultado;
    }
}

Capa 3: Interface Adapters
adapters/
├── PagoController.java            // REST controller
├── PagoRepositorioPostgres.java   // Implementa PagoRepositorio
├── EmailNotificadorPago.java      // Implementa NotificadorPago
├── PasarelaPagoAdapter.java       // Implementa PasarelaPagoGateway
└── PagoDTO.java                   // DTO para la API REST

// Controlador REST — solo convierte HTTP → Use Case
@RestController
@RequestMapping("/api/pagos")
public class PagoController {
    private final ProcesarPagoUseCase procesarPagoUseCase;

    public PagoController(ProcesarPagoUseCase procesarPagoUseCase) {
        this.procesarPagoUseCase = procesarPagoUseCase;
    }

    @PostMapping
    public ResponseEntity<ResultadoPagoDTO> procesar(@RequestBody PagoRequestDTO request) {
        Pago pago = new Pago(null, request.monto(), "COP", EstadoPago.PENDIENTE);
        ResultadoPago resultado = procesarPagoUseCase.ejecutar(pago, request.metodoPago(), request.ordenId());
        return ResponseEntity.ok(new ResultadoPagoDTO(resultado.exitoso(), resultado.mensaje()));
    }
}

// Implementación del repositorio
public class PagoRepositorioPostgres implements PagoRepositorio {
    private final JdbcTemplate jdbc;

    @Override
    public void guardar(Pago pago) { /* SQL aquí */ }

    @Override
    public Optional<Pago> buscarPorId(Long id) { /* SQL aquí */ return Optional.empty(); }
}

Capa 4: Frameworks & Drivers
frameworks/
├── SpringBootApp.java   // Configuración y arranque
├── DatabaseConfig.java  // Configuración PostgreSQL
└── EmailConfig.java     // Configuración SMTP

┌─────────────────────────────────────────────────┐
│  Frameworks & Drivers                           │
│  Spring Boot, PostgreSQL, SMTP, JavaFX          │
│                                                 │
│  ┌─────────────────────────────────────────┐   │
│  │  Interface Adapters                     │   │
│  │  PagoController, PagoRepositorioPostgres│   │
│  │  EmailNotificadorPago                   │   │
│  │                                         │   │
│  │  ┌───────────────────────────────────┐  │   │
│  │  │  Use Cases                        │  │   │
│  │  │  ProcesarPagoUseCase              │  │   │
│  │  │  interfaces: PagoRepositorio,     │  │   │
│  │  │  PasarelaPagoGateway,             │  │   │
│  │  │  NotificadorPago                  │  │   │
│  │  │                                   │  │   │
│  │  │  ┌─────────────────────────────┐  │  │   │
│  │  │  │  Entities                   │  │  │   │
│  │  │  │  Pago, Orden, EstadoPago    │  │  │   │
│  │  │  └─────────────────────────────┘  │  │   │
│  │  └───────────────────────────────────┘  │   │
│  └─────────────────────────────────────────┘   │
└─────────────────────────────────────────────────┘
Flechas de dependencia: todas apuntan hacia adentro ←

Inyección de dependencias
En la capa más externa (configuración Spring):
@Configuration
public class PagoConfig {
    @Bean
    public ProcesarPagoUseCase procesarPagoUseCase(
        PagoRepositorio pagoRepo,
        PasarelaPagoGateway pasarela,
        NotificadorPago notificador,
        OrdenRepositorio ordenRepo
    ) {
        return new ProcesarPagoUseCase(pagoRepo, pasarela, notificador, ordenRepo);
    }
}]

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?
[Incluir la comparación de testabilidad explícitamente en los requisitos produjo una tabla comparativa útil. Especificar los cuatro requisitos funcionales del módulo (múltiples pasarelas, PostgreSQL, correo, REST) orientó al LLM a ubicar cada componente en la capa correcta.]

#### 2. ¿Qué se puede mejorar?
[El prompt no pidió que se mostrara la estructura de paquetes/carpetas Maven o Gradle, que es lo que realmente orienta al desarrollador al implementarlo. Tampoco preguntó cómo se maneja la transaccionalidad: si la pasarela aprueba el pago pero falla el envío del correo, ¿se revierte? El LLM omitió completamente el manejo de transacciones distribuidas, que es un problema real en este módulo.]

#### 3. Respuesta final
[El diseño del LLM es correcto estructuralmente. Sin embargo, hay una dependencia problemática que no señaló: en el controlador REST usa @RestController y @RequestMapping de Spring, lo que técnicamente debería vivir en la capa de Frameworks, no solo en Interface Adapters. En Clean Architecture estricto, los adaptadores no deben tener anotaciones de framework — esas van en la configuración de la capa externa. Y ademas de todo, siento que si hay un punto más importante que se omitió y es el manejo de la transaccionalidad. Si pasarela.procesar() devuelve éxito pero ordenRepo.guardar() lanza excepción, el pago quedó procesado en la pasarela pero la orden no se actualizó. La solución correcta es el patrón Saga o al menos una compensación explícita, que en el contexto de Clean Architecture debe vivir en el Use Case, no en los adaptadores.]