# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

# Pregunta P02: Refactoring SOLID en ProcesadorPago

### Estudiante
 **Nombre completo**: Juan Camilo Gomez

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Claude |
| **Modelo específico** | Claude Sonnet 4.6 |
| **¿Por qué elegiste este LLM?** | Claude demuestra buen razonamiento sobre patrones de diseño y principios SOLID. Para una pregunta que pide refactoring con patrón de diseño adecuado, su capacidad de justificar decisiones de diseño es muy útil. |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```
Soy estudiante de Ingeniería de Software en la Pontificia Universidad Javeriana.
El proyecto es OpenLib Market, una plataforma de venta de libros digitales que necesita 
soportar múltiples métodos de pago.

El equipo junior escribió este código:

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

Tareas:
1. Identifica qué principios SOLID se están violando (mínimo dos) con argumentos concretos sobre este código.
2. Propón un refactoring completo en Java 21 usando el patrón de diseño que consideres más apropiado.
3. Justifica la elección del patrón.
4. Demuestra que el diseño resultante permite agregar criptomonedas como nuevo método de pago SIN modificar código existente.

Restricciones:
- Java 21
- Aplica SOLID correctamente
- Usa interfaces y clases separadas

```

---

### Respuesta del LLM

## Análisis de ProcesadorPago

### Principios SOLID violados

**1. OCP — Open/Closed Principle (principal)**
La clase está abierta a modificación y cerrada a extensión, exactamente al revés de lo que el principio exige. Para agregar criptomonedas como método de pago, hay que entrar a modificar el método `procesar` añadiendo otro `else if`. En un sistema con 10 métodos de pago, esta cadena se vuelve insostenible.

**2. SRP — Single Responsibility Principle**
`ProcesadorPago` conoce la lógica de procesamiento de tarjeta, PSE Y PayPal dentro de la misma clase. Cada método de pago tiene sus propias reglas (OAuth para PayPal, portal bancario para PSE), lo que significa que la clase tiene múltiples razones para cambiar.

**3. DIP — Dependency Inversion Principle**
La clase depende de implementaciones concretas implícitas (la lógica está hardcodeada), en lugar de depender de una abstracción del concepto de "método de pago".

---

### Patrón elegido: Strategy

El patrón **Strategy** es el más adecuado porque:
- Define una familia de algoritmos (uno por método de pago)
- Los encapsula en clases independientes
- Los hace intercambiables sin modificar el cliente

Alternativas descartadas:
- **Factory Method**: solo resolvería la creación, no el comportamiento polimórfico.
- **Template Method**: requeriría herencia, lo que acopla las implementaciones a una clase base común que puede no tener sentido para todos los métodos de pago.

---

### Implementación refactorizada

```java
// Abstracción — la interfaz Strategy
public interface EstrategiaPago {
    ResultadoPago procesar(Pago pago);
    String getTipo();
}
```

```java
// Estrategia concreta: Tarjeta
public class PagoTarjeta implements EstrategiaPago {
    @Override
    public ResultadoPago procesar(Pago pago) {
        // Validar CVV, fecha expiración, fondos
        // Conectar con API de franquicia (Visa/Mastercard)
        // Procesar cargo
        return new ResultadoPago(true, "Pago con tarjeta procesado");
    }

    @Override
    public String getTipo() { return "TARJETA"; }
}
```

```java
// Estrategia concreta: PSE
public class PagoPSE implements EstrategiaPago {
    @Override
    public ResultadoPago procesar(Pago pago) {
        // Redirigir a portal bancario
        // Validar retorno de PSE
        // Confirmar débito
        return new ResultadoPago(true, "Pago PSE procesado");
    }

    @Override
    public String getTipo() { return "PSE"; }
}
```

```java
// Estrategia concreta: PayPal
public class PagoPayPal implements EstrategiaPago {
    @Override
    public ResultadoPago procesar(Pago pago) {
        // Autenticar con OAuth PayPal
        // Ejecutar cargo
        // Registrar webhook de confirmación
        return new ResultadoPago(true, "Pago PayPal procesado");
    }

    @Override
    public String getTipo() { return "PAYPAL"; }
}
```

```java
// Contexto — reemplaza a ProcesadorPago
public class ProcesadorPago {
    private final EstrategiaPago estrategia;

    public ProcesadorPago(EstrategiaPago estrategia) {
        this.estrategia = estrategia;
    }

    public ResultadoPago procesar(Pago pago) {
        return estrategia.procesar(pago);
    }
}
```

```java
// Agregar criptomonedas: CERO cambios en código existente
public class PagoCripto implements EstrategiaPago {
    @Override
    public ResultadoPago procesar(Pago pago) {
        // Conectar con wallet
        // Verificar transacción en blockchain
        return new ResultadoPago(true, "Pago en criptomoneda procesado");
    }

    @Override
    public String getTipo() { return "CRIPTO"; }
}
```

```java
// Uso en el sistema
public class EjemploUso {
    public static void main(String[] args) {
        Pago pago = new Pago(/* datos */);

        // Pago con tarjeta
        ProcesadorPago procesador = new ProcesadorPago(new PagoTarjeta());
        ResultadoPago resultado = procesador.procesar(pago);

        // Pago con cripto — sin modificar ProcesadorPago
        procesador = new ProcesadorPago(new PagoCripto());
        resultado = procesador.procesar(pago);
    }
}
```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

[Evalúa tu propio prompt, no la respuesta del LLM. ¿El contexto fue suficiente? ¿Las restricciones fueron claras? ¿El formato de salida que pediste ayudó a obtener una buena respuesta? ¿Qué parte de tu prompt fue más efectiva? Sé específico: menciona fragmentos concretos de tu prompt que funcionaron bien.]


#### 2. ¿Qué se puede mejorar?

[¿Qué le faltó a tu prompt? ¿Qué harías diferente si pudieras reformularlo? ¿El LLM entendió mal algo por falta de claridad en tu prompt? ¿La respuesta tiene errores u omisiones? ¿Qué no cubrió el LLM que tú sí sabes por lo visto en clase?]


#### 3. Respuesta final

[Escribe tu respuesta definitiva a la pregunta del parcial, integrando lo que aprendiste del LLM pero yendo más allá. Corrige errores, llena omisiones, conecta con conceptos vistos en clase. Esta es tu respuesta: demuestra que tú dominas el tema.]
