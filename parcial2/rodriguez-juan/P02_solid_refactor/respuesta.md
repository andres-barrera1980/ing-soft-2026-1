# Plantilla de entrega — Parcial 2

---

## Pregunta P02: SOLID — Refactoring ProcesadorPago

### Estudiante

- **Nombre completo**: Juan David Rodriguez Franco

---

### LLM utilizado

| Campo                                   | Valor                                                                                                          |
| --------------------------------------- | -------------------------------------------------------------------------------------------------------------- |
| **Nombre del LLM**                | Gemini |
| **Modelo específico**            | Gemini 3.1 Pro |
| **¿Por qué elegiste este LLM?** | Gemini 3.1 Pro es excelente para analizar código acoplado, proponer patrones de diseño (como Strategy) y estructurar un refactoring claro en Java. |

---

### Prompt utilizado

```text
Actúa como un arquitecto de software experto en Java, SOLID y patrones de diseño.

En el proyecto OpenLib Market, tenemos una clase `ProcesadorPago` que procesa diferentes métodos de pago usando condicionales if-else anidados según un String (TARJETA, PSE, PAYPAL).

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

Por favor:
1. Identifica al menos dos principios SOLID que se están violando y explica por qué.
2. Propón un refactoring completo aplicando el patrón de diseño más adecuado para eliminar esos if-else.
3. Entrega el código en Java, usando interfaces, y muestra cómo un cliente (o caso de uso) instanciaría el procesador correcto.
```

---

### Respuesta del LLM

```markdown
El código actual de `ProcesadorPago` viola al menos dos principios fundamentales de SOLID:

1. **Principio Abierto/Cerrado (Open/Closed Principle - OCP)**: La clase no está cerrada a la modificación. Cada vez que OpenLib Market necesite agregar un nuevo método de pago (por ejemplo, Criptomonedas o Apple Pay), un desarrollador tendrá que modificar esta clase y agregar un nuevo bloque `else if`. 
2. **Principio de Responsabilidad Única (Single Responsibility Principle - SRP)**: La clase tiene múltiples responsabilidades. Conoce el flujo de negocio de las tarjetas, las redirecciones de PSE y el OAuth de PayPal. Si cambia la API de PayPal, esta clase debe ser modificada, afectando potencialmente a los demás métodos de pago.

### Refactoring Propuesto: Patrón Strategy

El patrón de diseño ideal para este problema es **Strategy (Estrategia)**. Nos permite encapsular cada método de pago en su propia clase, haciendo que sean intercambiables y cumpliendo OCP y SRP.

#### 1. Definir la Estrategia (Interfaz)
```java
public interface EstrategiaPago {
    ResultadoPago procesar(Pago pago);
}
```

#### 2. Implementar Estrategias Concretas
Cada clase maneja exclusivamente su lógica de integración:

```java
public class PagoTarjetaStrategy implements EstrategiaPago {
    @Override
    public ResultadoPago procesar(Pago pago) {
        // Lógica exclusiva de tarjeta: Validar CVV, franquicia, etc.
        return new ResultadoPago(true, "Pago con tarjeta procesado exitosamente");
    }
}

public class PagoPseStrategy implements EstrategiaPago {
    @Override
    public ResultadoPago procesar(Pago pago) {
        // Lógica exclusiva de PSE: Redirigir a portal, etc.
        return new ResultadoPago(true, "Pago PSE procesado exitosamente");
    }
}

public class PagoPayPalStrategy implements EstrategiaPago {
    @Override
    public ResultadoPago procesar(Pago pago) {
        // Lógica exclusiva de PayPal: OAuth, webhooks, etc.
        return new ResultadoPago(true, "Pago PayPal procesado exitosamente");
    }
}
```

#### 3. El Contexto Refactorizado (`ProcesadorPago`)
Ahora nuestra clase principal recibe la estrategia (Inyección de Dependencias) y simplemente delega la acción, sin importar qué método de pago es.

```java
public class ProcesadorPago {
    private final EstrategiaPago estrategia;
    
    // El cliente inyecta la estrategia deseada
    public ProcesadorPago(EstrategiaPago estrategia) {
        if (estrategia == null) {
            throw new IllegalArgumentException("Debe proveer una estrategia de pago");
        }
        this.estrategia = estrategia;
    }
    
    public ResultadoPago procesar(Pago pago) {
        return estrategia.procesar(pago);
    }
}
```

#### 4. Ejemplo de uso (El Cliente)
Si estamos en un controlador o caso de uso, usaríamos un *Factory* o un mapa para decidir la estrategia en tiempo de ejecución:

```java
public class CheckoutUseCase {
    public void realizarCheckout(Pago pago, String metodoElegido) {
        EstrategiaPago estrategia;
        
        switch (metodoElegido) {
            case "TARJETA" -> estrategia = new PagoTarjetaStrategy();
            case "PSE" -> estrategia = new PagoPseStrategy();
            case "PAYPAL" -> estrategia = new PagoPayPalStrategy();
            default -> throw new IllegalArgumentException("Método no soportado");
        }
        
        ProcesadorPago procesador = new ProcesadorPago(estrategia);
        ResultadoPago resultado = procesador.procesar(pago);
        System.out.println(resultado.getMensaje());
    }
}
```
*Nota: Si se usa Spring Framework, el "switch" se podría eliminar inyectando un mapa de estrategias `Map<String, EstrategiaPago>`.*
```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

[Pendiente de análisis]

#### 2. ¿Qué se puede mejorar?

[Pendiente de análisis]

#### 3. Respuesta final

[Pendiente de análisis]
