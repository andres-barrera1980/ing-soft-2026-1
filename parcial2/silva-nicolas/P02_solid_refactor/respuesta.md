# P02_solid_refactor

## Estudiante
- **Nombre completo**: Nicolas Silva

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [Gemini] |
| **Modelo específico** | [Gemini 3.1 Pro] |
| **¿Por qué elegiste este LLM?** | [Que dias me meti a mirar y me ofrecieron un mes de pro a 8 lukas, imposible no aprovechar ese gangaso] |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

Hago uso de la modalidad de memoria de Gemini 3.1 pro, ya tengo cargado todo el contexto de OpenLib Market, por lo que unicamente le doy las instrucciones de como comportarse.

```
Eres un desarrollador senior de java 25 y un arquitecto de software experto en el desarrollo de API REST en springboot 4.x, siguiendo los principios de Clean Architecture, DDD, TDD, SOLID y patrones de diseño GoF.

OpenLib Market necesita soportar múltiples métodos de pago (tarjeta de crédito, PSE, PayPal, criptomonedas). El equipo junior escribió este código:

```java
public class ProcesadorPago {
    private String tipoPago; // "TARJETA", "PSE", "PAYPAL"
    
    public ProcesadorPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }
    
    public ResultadoPago procesar(Pago pago) {
        if (tipoPago.equals("TARJETA")) {
            // Validar CVV, fecha expiración, fondos
            // Conectar con API de franquicia (Visa/Mastercard)
            // Procesar cargo
            return new ResultadoPago(true, "Pago con tarjeta procesado");
        } else if (tipoPago.equals("PSE")) {
            // Redirigir a portal bancario
            // Validar retorno de PSE
            // Confirmar débito
            return new ResultadoPago(true, "Pago PSE procesado");
        } else if (tipoPago.equals("PAYPAL")) {
            // Autenticar con OAuth PayPal
            // Ejecutar cargo
            // Registrar webhook de confirmación
            return new ResultadoPago(true, "Pago PayPal procesado");
        }
        throw new IllegalArgumentException("Método de pago no soportado");
    }
}
```

**Tarea**: Identifica qué principios SOLID se están violando y refactoriza el diseño para corregirlo.



### Respuesta del LLM

1. Análisis de Arquitectura y Principios Violados

Desde la perspectiva de un diseño robusto y escalable, el código escrito por el equipo junior presenta deficiencias estructurales graves que impiden la mantenibilidad y rompen con las bases de la programación orientada a objetos.

Principios SOLID Violados:

    Principio Abierto/Cerrado (OCP - Open/Closed Principle): Este es el fallo arquitectónico más crítico. La clase ProcesadorPago está abierta a la modificación. Cada vez que el negocio exija un nuevo método de pago (ej. Criptomonedas), el desarrollador está obligado a modificar esta clase, agregando otro bloque else if. Esto viola el aislamiento y aumenta el riesgo de introducir bugs en flujos de pago que ya funcionaban.

    Principio de Responsabilidad Única (SRP - Single Responsibility Principle): La clase asume múltiples responsabilidades. Se encarga de enrutar la petición (decidir qué if ejecutar) y, simultáneamente, contiene el conocimiento de cómo ejecutar la lógica de negocio y la infraestructura de cada pasarela (OAuth, redirecciones, validaciones de franquicia).

    Principio de Inversión de Dependencias (DIP - Dependency Inversion Principle): Aunque sutil en este fragmento, el procesador está acoplado a implementaciones concretas en su flujo de control en lugar de depender de abstracciones.

Problemas adicionales (Clean Architecture & TDD):
Este diseño es prácticamente imposible de someter a pruebas unitarias (TDD) de forma aislada. Para probar el pago con PayPal, tendrías que instanciar el procesador completo, arrastrando las dependencias (y posibles fallos) de la lógica de tarjetas de crédito o PSE.
2. Refactoring Propuesto: Patrón Strategy + Spring Boot 4.x

Como arquitecto, la solución procedimental exacta para eliminar sentencias condicionales complejas que seleccionan algoritmos es el Patrón Strategy (GoF).

Aprovechando las características de Java 25 (como los records para inmutabilidad de DTOs) y la inyección de dependencias avanzada de Spring Boot 4.x, podemos crear un diseño limpio donde las estrategias se autodescubren y se registran dinámicamente en el contexto de la aplicación.

```Java

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

// 1. DTOs inmutables usando Records (Java 14+)
public record Pago(double monto, String moneda, String idUsuario) {}
public record ResultadoPago(boolean exito, String mensaje) {}

// 2. Abstracción del Dominio (Contrato)
public interface IEstrategiaPago {
    ResultadoPago procesar(Pago pago);
    String obtenerIdentificadorMetodo();
}

// 3. Implementaciones concretas (Capa de Infraestructura / Servicios de Dominio)
@Component
public class PagoTarjeta implements IEstrategiaPago {
    @Override
    public ResultadoPago procesar(Pago pago) {
        // Integración con API de franquicia (Visa/Mastercard)
        return new ResultadoPago(true, "Pago con tarjeta procesado");
    }

    @Override
    public String obtenerIdentificadorMetodo() {
        return "TARJETA";
    }
}

@Component
public class PagoPSE implements IEstrategiaPago {
    @Override
    public ResultadoPago procesar(Pago pago) {
        // Redirección bancaria y confirmación
        return new ResultadoPago(true, "Pago PSE procesado");
    }

    @Override
    public String obtenerIdentificadorMetodo() {
        return "PSE";
    }
}

@Component
public class PagoPayPal implements IEstrategiaPago {
    @Override
    public ResultadoPago procesar(Pago pago) {
        // Autenticación OAuth y Webhooks
        return new ResultadoPago(true, "Pago PayPal procesado");
    }

    @Override
    public String obtenerIdentificadorMetodo() {
        return "PAYPAL";
    }
}

// 4. Servicio de Aplicación (El Contexto)
@Service
public class ProcesadorPagoService {
    
    private final Map<String, IEstrategiaPago> estrategiasMap;

    // Spring Boot inyecta automáticamente todas las clases que implementen IEstrategiaPago
    public ProcesadorPagoService(List<IEstrategiaPago> estrategias) {
        // Construimos un mapa O(1) para enrutar el pago basado en el identificador
        this.estrategiasMap = estrategias.stream()
            .collect(Collectors.toMap(IEstrategiaPago::obtenerIdentificadorMetodo, Function.identity()));
    }

    public ResultadoPago procesar(String tipoPago, Pago pago) {
        IEstrategiaPago estrategia = estrategiasMap.get(tipoPago.toUpperCase());
        
        if (estrategia == null) {
            throw new IllegalArgumentException("Método de pago no soportado: " + tipoPago);
        }
        
        // Delegación polimórfica
        return estrategia.procesar(pago);
    }
}
```

3. Análisis del Diseño

    Identificación de principios: El refactoring elimina por completo las violaciones a SRP y OCP. ProcesadorPagoService ahora tiene una única responsabilidad: enrutar la solicitud. Las estrategias tienen la responsabilidad de ejecutar el cobro.

    Idoneidad del Patrón: El patrón Strategy es la elección perfecta. Además, al combinarlo con la inyección de dependencias de Spring (inyectando una List<IEstrategiaPago>), implementamos un Patrón Registry/Factory implícito. El contenedor de Spring se encarga de instanciar y agrupar las estrategias en tiempo de arranque.

    Interfaces introducidas: La interfaz IEstrategiaPago establece el contrato. Según los principios de Clean Architecture, esta interfaz reside en la capa de casos de uso (Aplicación) o Dominio, mientras que las implementaciones como PagoPayPal residirían en la capa de Infraestructura, garantizando que el dominio no se acople a detalles tecnológicos externos.

    Escalabilidad ante nuevos métodos: Si mañana se requiere agregar "CRIPTO", el diseño lo soporta sin tocar código existente. Solo se debe crear una nueva clase PagoCripto implements IEstrategiaPago y anotarla con @Component. Spring la detectará, la agregará a la lista, el mapa del constructor la indexará automáticamente, y el sistema estará listo para recibir "CRIPTO" como parámetro. Esto es el OCP en su máxima expresión.

    Alineación TDD: Ahora es trivial escribir pruebas unitarias. Se puede instanciar el ProcesadorPagoService inyectando un Mock de la interfaz para verificar el enrutamiento, y se puede probar cada clase de pago de manera completamente independiente.


---

### Pregunta 2
OpenLib Market necesita soportar múltiples métodos de pago (tarjeta de crédito, PSE, PayPal, criptomonedas). El equipo junior escribió este código:

```java
public class ProcesadorPago {
    private String tipoPago; // "TARJETA", "PSE", "PAYPAL"
    
    public ProcesadorPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }
    
    public ResultadoPago procesar(Pago pago) {
        if (tipoPago.equals("TARJETA")) {
            // Validar CVV, fecha expiración, fondos
            // Conectar con API de franquicia (Visa/Mastercard)
            // Procesar cargo
            return new ResultadoPago(true, "Pago con tarjeta procesado");
        } else if (tipoPago.equals("PSE")) {
            // Redirigir a portal bancario
            // Validar retorno de PSE
            // Confirmar débito
            return new ResultadoPago(true, "Pago PSE procesado");
        } else if (tipoPago.equals("PAYPAL")) {
            // Autenticar con OAuth PayPal
            // Ejecutar cargo
            // Registrar webhook de confirmación
            return new ResultadoPago(true, "Pago PayPal procesado");
        }
        throw new IllegalArgumentException("Método de pago no soportado");
    }
}
```

**Tarea**: Identifica qué principios SOLID se están violando y refactoriza el diseño para corregirlo.


### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

[Evalúa tu propio prompt, no la respuesta del LLM. ¿El contexto fue suficiente? ¿Las restricciones fueron claras? ¿El formato de salida que pediste ayudó a obtener una buena respuesta? ¿Qué parte de tu prompt fue más efectiva? Sé específico: menciona fragmentos concretos de tu prompt que funcionaron bien.]


#### 2. ¿Qué se puede mejorar?

[¿Qué le faltó a tu prompt? ¿Qué harías diferente si pudieras reformularlo? ¿El LLM entendió mal algo por falta de claridad en tu prompt? ¿La respuesta tiene errores u omisiones? ¿Qué no cubrió el LLM que tú sí sabes por lo visto en clase?]


#### 3. Respuesta final

[Escribe tu respuesta definitiva a la pregunta del parcial, integrando lo que aprendiste del LLM pero yendo más allá. Corrige errores, llena omisiones, conecta con conceptos vistos en clase. Esta es tu respuesta: demuestra que tú dominas el tema.]
