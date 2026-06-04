# Plantilla de entrega — Parcial 2

---

## Pregunta P02: SOLID — Refactoring ProcesadorPago

### Estudiante
- **Nombre completo**: Ivan Wright

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Gemini |
| **Modelo especifico** | Gemini 3.1 Pro |
| **¿Por que elegiste este LLM?** | Lo escogi porque tiene una gran capacidad analitica para analizar codigo en Java y ayuda mucho a estructurar refactorizaciones complejas usando patrones de diseño. |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```text
hola gemini, para el proyecto OpenLib Market que usa Java 21 y Spring Boot, necesito que analices este codigo de ProcesadorPago.

Primeramente identifica cuales principios SOLID se estan violando en este codigo (deben ser al menos dos). Seguidamente propon un refactoring completo para arreglarlo usando el patron de diseño que consideres mas adecuado para manejar multiples metodos de pago.

Ten de restriccion que debes usar Java 21 y cumplir con todos los principios SOLID. Dame la salida en markdown con el codigo java estructurado.

public class ProcesadorPago {
    private String tipoPago; // "TARJETA", "PSE", "PAYPAL"
    
    public ProcesadorPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }
    
    public ResultadoPago procesar(Pago pago) {
        if (tipoPago.equals("TARJETA")) {
            // Validar CVV, fecha expiracion, fondos
            // Conectar con API de franquicia (Visa/Mastercard)
            // Procesar cargo
            return new ResultadoPago(true, "Pago con tarjeta procesado");
        } else if (tipoPago.equals("PSE")) {
            // Redirigir a portal bancario
            // Validar retorno de PSE
            // Confirmar debito
            return new ResultadoPago(true, "Pago PSE procesado");
        } else if (tipoPago.equals("PAYPAL")) {
            // Autenticar con OAuth PayPal
            // Ejecutar cargo
            // Registrar webhook de confirmacion
            return new ResultadoPago(true, "Pago PayPal procesado");
        }
        throw new IllegalArgumentException("Metodo de pago no soportado");
    }
}
```

---

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```markdown
¡Hola! Con gusto te ayudo a analizar y refactorizar el código para OpenLib Market.

### 1. Principios SOLID Violados

En el código actual de la clase `ProcesadorPago`, se están violando principalmente dos principios SOLID:

1.  **Principio de Responsabilidad Única (SRP - Single Responsibility Principle):**
    La clase `ProcesadorPago` tiene múltiples responsabilidades. Se encarga de procesar pagos con tarjeta de crédito (validaciones, conexión con Visa/Mastercard), procesar pagos por PSE (redirecciones al portal bancario) y procesar pagos con PayPal (OAuth, webhooks). Toda esta lógica disonante está acumulada en un solo lugar. Si cambian las reglas de validación de tarjetas o la API de PayPal, esta clase debe ser modificada.

2.  **Principio Abierto/Cerrado (OCP - Open/Closed Principle):**
    El código no está cerrado a la modificación ni abierto a la extensión. Debido a la estructura de control rígida de `if/else`, si OpenLib Market decide soportar un nuevo método de pago en el futuro (por ejemplo, Criptomonedas o Apple Pay), el desarrollador se verá forzado a abrir el código de la clase `ProcesadorPago` y agregar un nuevo bloque `else if`.

### 2. Refactoring Propuesto: Patrón Strategy

El patrón de diseño ideal para solucionar múltiples variaciones de comportamiento es el **Patrón Estrategia (Strategy)**. Este patrón permite definir una familia de algoritmos (los métodos de pago), encapsular cada uno de ellos y hacerlos intercambiables dinámicamente sin alterar la clase que los consume.

A continuación te presento la estructura refactorizada en Java 21, aprovechando la inyección de dependencias de Spring Boot:

#### Paso 1: Crear la interfaz de la Estrategia

```java
public interface EstrategiaPago {
    ResultadoPago procesar(Pago pago);
    boolean soporta(String tipoPago);
}
```

#### Paso 2: Implementar las Estrategias Concretas

**Pago con Tarjeta:**
```java
import org.springframework.stereotype.Component;

@Component
public class PagoTarjetaStrategy implements EstrategiaPago {
    @Override
    public boolean soporta(String tipoPago) {
        return "TARJETA".equalsIgnoreCase(tipoPago);
    }

    @Override
    public ResultadoPago procesar(Pago pago) {
        // Validar CVV, fecha expiración, fondos
        // Conectar con API de franquicia (Visa/Mastercard)
        // Procesar cargo
        return new ResultadoPago(true, "Pago con tarjeta procesado");
    }
}
```

**Pago con PSE:**
```java
import org.springframework.stereotype.Component;

@Component
public class PagoPseStrategy implements EstrategiaPago {
    @Override
    public boolean soporta(String tipoPago) {
        return "PSE".equalsIgnoreCase(tipoPago);
    }

    @Override
    public ResultadoPago procesar(Pago pago) {
        // Redirigir a portal bancario
        // Validar retorno de PSE
        // Confirmar débito
        return new ResultadoPago(true, "Pago PSE procesado");
    }
}
```

**Pago con PayPal:**
```java
import org.springframework.stereotype.Component;

@Component
public class PagoPayPalStrategy implements EstrategiaPago {
    @Override
    public boolean soporta(String tipoPago) {
        return "PAYPAL".equalsIgnoreCase(tipoPago);
    }

    @Override
    public ResultadoPago procesar(Pago pago) {
        // Autenticar con OAuth PayPal
        // Ejecutar cargo
        // Registrar webhook de confirmación
        return new ResultadoPago(true, "Pago PayPal procesado");
    }
}
```

#### Paso 3: Refactorizar el Procesador de Pago (El Contexto)

Gracias a Spring Boot, podemos inyectar todas las estrategias disponibles automáticamente como una lista y seleccionar la adecuada en tiempo de ejecución.

```java
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProcesadorPago {
    
    private final List<EstrategiaPago> estrategiasPago;
    
    // Spring inyectará automáticamente todas las clases que implementen EstrategiaPago
    public ProcesadorPago(List<EstrategiaPago> estrategiasPago) {
        this.estrategiasPago = estrategiasPago;
    }
    
    public ResultadoPago procesar(String tipoPago, Pago pago) {
        EstrategiaPago estrategia = estrategiasPago.stream()
                .filter(e -> e.soporta(tipoPago))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Método de pago no soportado: " + tipoPago));
                
        return estrategia.procesar(pago);
    }
}
```

### Ventajas del diseño logrado:
* **Cumple con SRP**: Cada clase gestiona únicamente la lógica de su propio medio de pago y nada más.
* **Cumple con OCP**: Si mañana el negocio decide agregar un método "Criptomonedas", el ingeniero solo deberá crear la clase `PagoCriptomonedasStrategy`. Automáticamente el sistema lo soportará sin alterar ni una sola línea de la clase central `ProcesadorPago`.
```

---

### Analisis critico de la respuesta

#### 1. ¿Que hizo bien el prompt?

[Evalua tu propio prompt, no la respuesta del LLM. ¿El contexto fue suficiente? ¿Las restricciones fueron claras? ¿El formato de salida que pediste ayudo a obtener una buena respuesta? ¿Que parte de tu prompt fue mas efectiva? Se especifico: menciona fragmentos concretos de tu prompt que funcionaron bien.]


#### 2. ¿Que se puede mejorar?

[¿Que le falto a tu prompt? ¿Que harias diferente si pudieras reformularlo? ¿El LLM entendio mal algo por falta de claridad en tu prompt? ¿La respuesta tiene errores u omisiones? ¿Que no cubrio el LLM que tu si sabes por lo visto en clase?]


#### 3. Respuesta final

[Escribe tu respuesta definitiva a la pregunta del parcial, integrando lo que aprendiste del LLM pero yendo mas alla. Corrige errores, llena omisiones, conecta con conceptos vistos en clase. Esta es tu respuesta: demuestra que tu dominas el tema.]
