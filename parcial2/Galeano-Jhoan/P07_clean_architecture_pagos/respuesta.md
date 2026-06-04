# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [7]: [P07_clean_architecture_pagos]

### Estudiante
- **Nombre completo**: [Jhoan Galeano]

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [Claude] |
| **Modelo específico** | [Claude 3.1 pro] |
| **¿Por qué elegiste este LLM?** | Esta IA razona de una buena manera y es necesario para obtener una respuesta mas acertada de lo que s epide] |

---

### Prompt utilizado

Actúa como un ingeniero de software senior y arquitecto de soluciones de OpenLib Market. Tenemos un problema grave de acoplamiento en el módulo de gestión de pagos: el equipo metió en una sola clase (`PaymentService`) y en un único método (`realizarPago`) la lógica de las pasarelas (tarjeta, PSE, PayPal), el registro en PostgreSQL, el envío de correos y la API REST que consume el frontend en JavaFX.

Tarea:
Rediseña por completo este módulo aplicando los principios de Clean Architecture para separar las responsabilidades. Genera una respuesta estructurada con los siguientes puntos:

1. Distribución por Capas: Detalla explícitamente qué componentes van en cada una de las 4 capas (Entities, Use Cases, Interface Adapters, Frameworks & Drivers) para resolver este problema de pagos.
2. Diagrama de Estructura: Dibuja un diagrama en formato Mermaid que muestre las capas y la dirección de las dependencias.
3. Regla de Dependencia e Inyección: Explica cómo se respeta la regla de dependencia y cómo se inyectan las herramientas externas (la base de datos, el servicio de correo y las pasarelas de pago) utilizando interfaces/inversión de dependencias para no contaminar el caso de uso central.

---

### Respuesta del LLM

 Distribución por Capas
Capa 1 — Entities (núcleo del negocio, cero dependencias externas)
Contiene las reglas de negocio que existirían aunque no hubiera base de datos ni frontend. Son los objetos más estables del sistema.

Pago — entidad central con isbn, monto, método, estado y timestamp
ResultadoPago — valor de retorno con éxito/fallo y mensaje
EstadoPago — enum: PENDIENTE, APROBADO, RECHAZADO, ERROR
MetodoPago — enum: TARJETA, PSE, PAYPAL
Excepcion de dominio: PagoInvalidoException

Capa 2 — Use Cases (orquesta el flujo, solo conoce Entities)
Contiene la lógica de aplicación. Define qué debe pasar cuando se realiza un pago, pero nunca sabe cómo se persiste ni cómo se envía el correo.

RealizarPagoUseCase — orquesta: validar → procesar pasarela → persistir → notificar
ConsultarPagoUseCase — consulta historial por usuario o isbn
Puertos de salida (interfaces que esta capa define y el exterior implementa):

PasarelaPagoPort — procesar(Pago): ResultadoPago
PagoRepositoryPort — guardar(Pago), buscarPorId(String)
NotificacionPort — enviarConfirmacion(Pago)



Capa 3 — Interface Adapters (traduce entre el mundo exterior y los use cases)
Convierte datos del exterior al formato que los use cases entienden, y viceversa.

PagoController — recibe HTTP REST del frontend JavaFX, llama al use case
PagoDTO — objeto de transferencia que evita exponer la entidad Pago al exterior
PagoMapper — convierte PagoDTO ↔ Pago
PagoRepositoryAdapter — implementa PagoRepositoryPort usando JPA/PostgreSQL
EmailNotificacionAdapter — implementa NotificacionPort usando JavaMail
TarjetaAdapter, PSEAdapter, PayPalAdapter — implementan PasarelaPagoPort

Capa 4 — Frameworks & Drivers (detalles técnicos, la capa más exterior)
Contiene todo lo que puede reemplazarse sin tocar el negocio: Spring Boot, PostgreSQL, JavaMail, SDKs de pasarelas, JavaFX.

SpringBootApp — punto de entrada y configuración de beans
PagoJpaEntity — entidad JPA anotada con @Entity (diferente a Pago de dominio)
PagoJpaRepository — interface de Spring Data
JavaMailSender — configuración SMTP
VisaApiClient, PSEApiClient, PayPalApiClient — clientes HTTP externos
JavaFXClient — frontend que consume la API REST
 Regla de Dependencia e Inyección
La regla de dependencia dice: el código fuente solo puede apuntar hacia adentro. Entities no conoce a nadie. Use Cases solo conoce Entities. Adapters conocen Use Cases. Frameworks conocen todo, pero nada los conoce a ellos.
El problema concreto es este: RealizarPagoUseCase necesita guardar en base de datos, enviar email y cobrar por una pasarela — pero si importa PostgreSQL, JavaMail o el SDK de Visa directamente, viola la regla porque el núcleo dependería de un detalle externo.
// ─── Entities ─────────────────────────────────────────────────────────────

enum EstadoPago { PENDIENTE, APROBADO, RECHAZADO }
enum MetodoPago { TARJETA, PSE, PAYPAL }

class Pago {
    private final String id;
    private final String isbn;
    private final double monto;
    private final MetodoPago metodo;
    private EstadoPago estado;

    Pago(String id, String isbn, double monto, MetodoPago metodo) {
        this.id = id;
        this.isbn = isbn;
        this.monto = monto;
        this.metodo = metodo;
        this.estado = EstadoPago.PENDIENTE;
    }

    void aprobar() { this.estado = EstadoPago.APROBADO; }
    void rechazar() { this.estado = EstadoPago.RECHAZADO; }

    String getId()       { return id; }
    String getIsbn()     { return isbn; }
    double getMonto()    { return monto; }
    MetodoPago getMetodo() { return metodo; }
    EstadoPago getEstado() { return estado; }

    @Override
    public String toString() {
        return "Pago{id=" + id + ", isbn=" + isbn
            + ", monto=$" + monto + ", estado=" + estado + "}";
    }
}

class ResultadoPago {
    final boolean exitoso;
    final String mensaje;

    ResultadoPago(boolean exitoso, String mensaje) {
        this.exitoso = exitoso;
        this.mensaje = mensaje;
    }
}

// ─── Puertos (definidos en Use Cases, implementados en Adapters) ───────────

interface PasarelaPagoPort {
    ResultadoPago procesar(Pago pago);
}

interface PagoRepositoryPort {
    void guardar(Pago pago);
    Pago buscarPorId(String id);
}

interface NotificacionPort {
    void enviarConfirmacion(Pago pago);
}

// ─── Use Case ─────────────────────────────────────────────────────────────
// Solo conoce Entities y sus propios puertos. Cero imports de Spring/JPA/Mail.

class RealizarPagoUseCase {
    private final PasarelaPagoPort pasarela;
    private final PagoRepositoryPort repositorio;
    private final NotificacionPort notificacion;

    // Las dependencias se inyectan desde afuera — el use case no las construye
    RealizarPagoUseCase(PasarelaPagoPort pasarela,
                        PagoRepositoryPort repositorio,
                        NotificacionPort notificacion) {
        this.pasarela    = pasarela;
        this.repositorio = repositorio;
        this.notificacion = notificacion;
    }

    Pago ejecutar(String id, String isbn, double monto, MetodoPago metodo) {
        Pago pago = new Pago(id, isbn, monto, metodo);

        ResultadoPago resultado = pasarela.procesar(pago);

        if (resultado.exitoso) {
            pago.aprobar();
        } else {
            pago.rechazar();
        }

        repositorio.guardar(pago);    // persiste sin saber que es PostgreSQL
        notificacion.enviarConfirmacion(pago); // notifica sin saber que es email

        return pago;
    }
}

// ─── Adapters (implementan los puertos con tecnología concreta) ────────────

class TarjetaAdapter implements PasarelaPagoPort {
    public ResultadoPago procesar(Pago pago) {
        System.out.println("  [Visa/MC]  Procesando tarjeta por $" + pago.getMonto());
        return new ResultadoPago(true, "Cargo aprobado por franquicia");
    }
}

class PSEAdapter implements PasarelaPagoPort {
    public ResultadoPago procesar(Pago pago) {
        System.out.println("  [PSE]      Débito bancario por $" + pago.getMonto());
        return new ResultadoPago(true, "Débito PSE confirmado");
    }
}

class PayPalAdapter implements PasarelaPagoPort {
    public ResultadoPago procesar(Pago pago) {
        System.out.println("  [PayPal]   OAuth + cargo por $" + pago.getMonto());
        return new ResultadoPago(true, "Cargo PayPal ejecutado");
    }
}

class PagoRepositoryAdapter implements PagoRepositoryPort {
    // En producción: inyecta JpaRepository de Spring Data
    private final java.util.Map<String, Pago> db = new java.util.HashMap<>();

    public void guardar(Pago pago) {
        db.put(pago.getId(), pago);
        System.out.println("  [PostgreSQL] Pago persistido: " + pago.getId());
    }

    public Pago buscarPorId(String id) {
        return db.get(id);
    }
}

class EmailNotificacionAdapter implements NotificacionPort {
    // En producción: inyecta JavaMailSender de Spring
    public void enviarConfirmacion(Pago pago) {
        System.out.println("  [Email]    Confirmación enviada — estado: "
            + pago.getEstado());
    }
}

// ─── Frameworks & Drivers: configuración DI (simula Spring Boot) ───────────

class AppConfig {
    static RealizarPagoUseCase buildUseCase(MetodoPago metodo) {
        // Spring haría esto con @Bean y @Autowired
        // Aquí lo hacemos manual para mostrar el flujo limpio
        PasarelaPagoPort pasarela = switch (metodo) {
            case TARJETA -> new TarjetaAdapter();
            case PSE     -> new PSEAdapter();
            case PAYPAL  -> new PayPalAdapter();
        };
        return new RealizarPagoUseCase(
            pasarela,
            new PagoRepositoryAdapter(),
            new EmailNotificacionAdapter()
        );
    }
}

// ─── Main (simula el Controller recibiendo la petición del frontend JavaFX) ─

public class Main {
    public static void main(String[] args) {

        System.out.println("══════════════════════════════════════");
        System.out.println(" Pago con Tarjeta");
        System.out.println("══════════════════════════════════════");
        Pago p1 = AppConfig.buildUseCase(MetodoPago.TARJETA)
            .ejecutar("PAY-001", "978-001", 85_000, MetodoPago.TARJETA);
        System.out.println("  Resultado: " + p1);

        System.out.println("\n══════════════════════════════════════");
        System.out.println(" Pago con PSE");
        System.out.println("══════════════════════════════════════");
        Pago p2 = AppConfig.buildUseCase(MetodoPago.PSE)
            .ejecutar("PAY-002", "978-002", 120_000, MetodoPago.PSE);
        System.out.println("  Resultado: " + p2);

        System.out.println("\n══════════════════════════════════════");
        System.out.println(" Pago con PayPal");
        System.out.println("══════════════════════════════════════");
        Pago p3 = AppConfig.buildUseCase(MetodoPago.PAYPAL)
            .ejecutar("PAY-003", "978-003", 200_000, MetodoPago.PAYPAL);
        System.out.println("  Resultado: " + p3);
    }
}

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

[El prompt ayudo a relacionar bien las entidades para poder buscar la mejor solucion.]


#### 2. ¿Qué se puede mejorar?

[Usar un prompt o realizarlo que sea mas fijo, que distribuya bien los pasos y tenga pasos mas conectados]


#### 3. Respuesta final

[Escribe tu respuesta definitiva a la pregunta del parcial, integrando lo que aprendiste del LLM pero yendo más allá. Corrige errores, llena omisiones, conecta con conceptos vistos en clase. Esta es tu respuesta: demuestra que tú dominas el tema.]
