# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [07]: [P07_clean_architecture_pagos]

### Estudiante
- **Nombre completo**: [Diego Alejandro Torres Barrgan]

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [Claude] |
| **Modelo específico** | [Ej: Claude Sonnet 4.6/bajo]|
| **¿Por qué elegiste este LLM?** | [Es el modelo que actualmente mas uso y en el que estoy confiando] |

---


---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```
[Actúa como un Arquitecto de Software Senior. Estamos trabajando en OpenLib Market y el equipo de pagos hizo un desastre: metieron toda la lógica (pasarelas de pago, correos, base de datos y lógica de negocio) dentro de una sola clase gigante llamada PaymentService.
Necesito que me ayudes a rediseñar este módulo de pagos aplicando Clean Architecture para desacoplarlo. El sistema debe soportar múltiples métodos de pago (Tarjeta, PSE, PayPal), guardar en PostgreSQL y notificar a los usuarios.
Por favor, entrégame lo siguiente:
Definición de Capas: Explica qué clases o interfaces van en cada nivel: Entities, Use Cases, Interface Adapters y Frameworks & Drivers.
Regla de Dependencia: Explica brevemente cómo se asegura que las dependencias solo vayan hacia adentro (hacia el core).
Diagrama de Estructura: Crea un diagrama en Mermaid que muestre la organización de estas capas y cómo fluye la comunicación.
Implementación en Java: Muestra el código de las clases principales (especialmente el Caso de Uso y las Interfaces/Ports) para ver cómo se inyectan las dependencias externas (como el repositorio o el servicio de correo) sin ensuciar la lógica de negocio.
Inyección de Dependencias: Explica cómo se conectan los detalles externos (PostgreSQL, APIs de terceros) al core al final.]
```

---

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**
Rediseno del modulo de pagos con Clean Architecture
1. Definicion de capas
Entities:
Las clases de negocio puras, sin dependencias externas.

Orden — tiene el estado, el total y el comprador
Pago — tiene el monto y el metodo de pago
ResultadoPago — encapsula si el pago fue exitoso o no

Use Cases:
La logica de la aplicacion y las interfaces (ports) que necesita.

RealizarPagoUseCase — orquesta todo el flujo
RepositorioOrden (interfaz) — para persistir ordenes
PasarelaPago (interfaz) — para procesar el cobro
ServicioNotificacion (interfaz) — para notificar al usuario

Interface Adapters:
Traducen entre el mundo externo y el core.

PagoController — recibe la peticion REST
RepositorioOrdenPostgresImpl — implementa RepositorioOrden con JPA
PasarelaPaypalImpl, PasarelaTarjetaImpl, PasarelaPSEImpl — implementan PasarelaPago
EmailNotificacionImpl — implementa ServicioNotificacion

Frameworks & Drivers:
Spring Boot, JPA/Hibernate, driver PostgreSQL, SDKs de PayPal y PSE. Solo configuracion, cero logica.

2. Regla de dependencia
Cada capa solo puede conocer a la que tiene mas adentro. PagoController conoce al Use Case, el Use Case conoce las interfaces, y las implementaciones concretas (Postgres, PayPal) implementan esas interfaces pero el Use Case nunca las importa directamente. Si se viola esto, el negocio queda acoplado a la tecnologia y probar sin servicios externos se vuelve imposible.

3. Diagrama 
```
Frameworks & Drivers
└── PostgreSQL, PayPal SDK, SMTP

Interface Adapters
├── PagoController
├── RepositorioOrdenPostgresImpl  →  implementa RepositorioOrden
├── PasarelaPaypalImpl            →  implementa PasarelaPago
└── EmailNotificacionImpl         →  implementa ServicioNotificacion

Use Cases
├── RealizarPagoUseCase
├── RepositorioOrden (interfaz)
├── PasarelaPago (interfaz)
└── ServicioNotificacion (interfaz)

Entities
├── Orden
└── Pago

Flujo de llamadas:
PagoController → RealizarPagoUseCase → interfaces → implementaciones → servicios externos
```

4. Implementacion en Java
Entities:
```java
javapublic class Orden {
    private Long id;
    private String emailComprador;
    private BigDecimal total;
    private EstadoOrden estado;

    public void marcarComoPagada() {
        this.estado = EstadoOrden.PAGADA;
    }
}

public class Pago {
    private BigDecimal monto;
    private String metodoPago; // TARJETA, PSE, PAYPAL
}
```
Interfaces del Use Case (ports):
```java
javapublic interface RepositorioOrden {
    Orden buscarPorId(Long id);
    void guardar(Orden orden);
}

public interface PasarelaPago {
    ResultadoPago procesar(Pago pago);
}

public interface ServicioNotificacion {
    void notificar(String email, String mensaje);
}
```
Use Case:
```java
javapublic class RealizarPagoUseCase {
    private final RepositorioOrden repositorioOrden;
    private final PasarelaPago pasarelaPago;
    private final ServicioNotificacion notificacion;

    public RealizarPagoUseCase(RepositorioOrden repositorioOrden,
                                PasarelaPago pasarelaPago,
                                ServicioNotificacion notificacion) {
        this.repositorioOrden = repositorioOrden;
        this.pasarelaPago = pasarelaPago;
        this.notificacion = notificacion;
    }

    public void ejecutar(Long ordenId, Pago pago) {
        Orden orden = repositorioOrden.buscarPorId(ordenId);
        ResultadoPago resultado = pasarelaPago.procesar(pago);
        if (resultado.isExitoso()) {
            orden.marcarComoPagada();
            repositorioOrden.guardar(orden);
            notificacion.notificar(orden.getEmailComprador(), "Tu pago fue procesado");
        }
    }
}
```
Controller:
```java
java@RestController
public class PagoController {
    private final RealizarPagoUseCase useCase;

    public PagoController(RealizarPagoUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping("/pagos")
    public ResponseEntity<?> pagar(@RequestBody PagoDTO dto) {
        Pago pago = new Pago(dto.getMonto(), dto.getMetodo());
        useCase.ejecutar(dto.getOrdenId(), pago);
        return ResponseEntity.ok().build();
    }
}
```
5. Inyeccion de dependencias
Spring Boot se encarga de conectar todo en la capa mas externa. El Use Case nunca instancia nada directamente, recibe todo por constructor:
```java
java@Configuration
public class PagoConfig {

    @Bean
    public RealizarPagoUseCase realizarPagoUseCase(
            RepositorioOrden repositorioOrden,
            PasarelaPago pasarelaPago,
            ServicioNotificacion notificacion) {
        return new RealizarPagoUseCase(repositorioOrden, pasarelaPago, notificacion);
    }

    @Bean
    public PasarelaPago pasarelaPago() {
        return new PasarelaPaypalImpl(); // o PSE o Tarjeta segun config
    }
}
```
Asi el core nunca sabe que existe Spring, PostgreSQL ni PayPal. Si manana cambian de PayPal a PSE, solo se cambia el @Bean, sin tocar el Use Case.

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

[Darle contexto del problema desde el principio ayudo mucho, el LLM entendio de una que el problema era el acoplamiento y no se fue por las ramas. Pedir los 5 puntos numerados tambien funciono porque la respuesta salio ordenada. La frase "sin ensuciar la logica de negocio" fue clave para que se enfocara en el desacoplamiento]


#### 2. ¿Qué se puede mejorar?

[No se pidio explicar como el sistema elige entre Tarjeta, PSE o PayPal segun lo que llegue, entonces el LLM solo mostro un ejemplo con PayPal y quedo incompleto ese punto. Tambien faltaria pedir como se testea el Use Case con mocks, que es algo importante visto en clase.]


#### 3. Respuesta final

[El problema del PaymentService original es que tenia todo junto: pasarelas, correos, base de datos y logica de negocio en un solo lugar, lo cual lo hace imposible de probar y mantener.
Con Clean Architecture se separa en capas donde las dependencias solo apuntan hacia adentro. El RealizarPagoUseCase solo conoce interfaces, nunca implementaciones concretas, entonces si manana cambian de PayPal a PSE no se toca nada del core.
Lo que el LLM no cubrio fue como seleccionar la pasarela correcta segun el metodo de pago, eso se resuelve con un mapa de estrategias:
```java
javapublic class SelectorPasarela {
    private final Map<String, PasarelaPago> pasarelas;

    public PasarelaPago seleccionar(String metodo) {
        return pasarelas.get(metodo);
    }
}
```
La ventaja principal de este diseno es que el Use Case se puede probar con mocks sin necesitar base de datos ni servicios externos, algo que con el codigo original era imposible.]
