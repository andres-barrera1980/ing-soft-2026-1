# Pregunta P02: SOLID — Refactoring ProcesadorPago

### Estudiante
- **Nombre completo**: Mateo Traslaviña Moreno

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Claude |
| **Modelo específico** | Claude Sonnet 4.6 |
| **¿Por qué elegiste este LLM?** | Para refactoring de código Java con patrones de diseño, Claude produce código más limpio y bien estructurado que alternativas. La tarea requiere tanto identificar principios violados como proponer un diseño extensible, dos cosas en las que Claude demuestra razonamiento paso a paso. |

---

### Prompt utilizado

```
Eres un arquitecto de software senior revisando código del proyecto OpenLib Market (Java 21, Spring Boot 3.x, PostgreSQL). El proyecto permite compra-venta de libros digitales y físicos.

Analiza este código del módulo de pagos:

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

Tareas:
1. Identifica TODOS los principios SOLID violados (al menos dos) con justificación técnica específica citando el código.
2. Identifica el patrón de diseño más apropiado para resolver esto. Justifica por qué ese patrón y descarta al menos una alternativa.
3. Propón un refactoring completo en Java 21 con Spring Boot que:
   - Use el patrón identificado
   - Sea extensible: agregar "CRIPTO" NO debe requerir modificar clases existentes
   - Use interfaces en lugar de clases concretas para las dependencias
   - Use inyección de dependencias por constructor
4. Muestra cómo quedaría el código si mañana se agrega soporte para criptomonedas.

Restricciones:
- Sin Strings mágicos ("TARJETA", "PSE") — usar un enum
- Código Java 21 compilable
- Cada clase en su propio archivo conceptual

Formato: secciones "Análisis SOLID", "Patrón elegido", "Código refactorizado", "Extensión con cripto".
```

---

### Respuesta del LLM

## Análisis SOLID

**OCP (Open/Closed Principle) — Violación principal**  
La clase `ProcesadorPago` debe modificarse cada vez que se agrega un método de pago nuevo. Agregar "CRIPTO" implica editar el `if-else` existente, violando el principio de estar abierta para extensión pero cerrada para modificación.

**SRP (Single Responsibility Principle) — Violación secundaria**  
Una sola clase maneja la lógica de tres procesadores completamente diferentes. Cada rama del `if-else` tiene su propio conjunto de dependencias (API de tarjeta, portal PSE, OAuth PayPal). Si cambia la API de PayPal, se modifica `ProcesadorPago`.

**DIP (Dependency Inversion Principle) — Violación implícita**  
La decisión de qué procesador usar está hardcodeada en el mismo componente que ejecuta el pago, sin ninguna abstracción que separe la selección de la ejecución.

## Patrón elegido: Strategy

El patrón **Strategy** es ideal aquí porque:
- Define una familia de algoritmos (estrategias de pago) intercambiables
- Cada algoritmo se encapsula en su propia clase
- Los algoritmos son intercambiables desde el cliente

**Alternativa descartada — Factory Method**: podría resolver la creación de procesadores, pero no resuelve el problema central de las múltiples responsabilidades dentro de un mismo método. Strategy + Factory combinados es la solución completa.

## Código refactorizado

```java
// Enum de tipos de pago
public enum TipoPago {
    TARJETA, PSE, PAYPAL, CRIPTO
}

// Interfaz Strategy
public interface EstrategiaPago {
    ResultadoPago procesar(Pago pago);
    TipoPago getTipo();
}

// Estrategia para tarjeta
@Service
public class PagoTarjetaStrategy implements EstrategiaPago {
    @Override
    public ResultadoPago procesar(Pago pago) {
        // Validar CVV, fecha expiración
        // Conectar con API franquicia
        return new ResultadoPago(true, "Pago con tarjeta procesado");
    }
    
    @Override
    public TipoPago getTipo() { return TipoPago.TARJETA; }
}

// Estrategia para PSE
@Service
public class PagoPSEStrategy implements EstrategiaPago {
    @Override
    public ResultadoPago procesar(Pago pago) {
        // Redirigir portal bancario
        return new ResultadoPago(true, "Pago PSE procesado");
    }
    
    @Override
    public TipoPago getTipo() { return TipoPago.PSE; }
}

// Estrategia para PayPal
@Service
public class PagoPayPalStrategy implements EstrategiaPago {
    @Override
    public ResultadoPago procesar(Pago pago) {
        // OAuth + cargo
        return new ResultadoPago(true, "Pago PayPal procesado");
    }
    
    @Override
    public TipoPago getTipo() { return TipoPago.PAYPAL; }
}

// Factory/Registry que selecciona la estrategia correcta
@Service
public class ProcesadorPago {
    private final Map<TipoPago, EstrategiaPago> estrategias;
    
    public ProcesadorPago(List<EstrategiaPago> estrategiasDisponibles) {
        this.estrategias = estrategiasDisponibles.stream()
            .collect(Collectors.toMap(EstrategiaPago::getTipo, Function.identity()));
    }
    
    public ResultadoPago procesar(Pago pago) {
        EstrategiaPago estrategia = estrategias.get(pago.getTipoPago());
        if (estrategia == null) {
            throw new IllegalArgumentException("Método de pago no soportado: " + pago.getTipoPago());
        }
        return estrategia.procesar(pago);
    }
}
```

## Extensión con cripto

Para agregar criptomonedas, **solo se agrega una nueva clase sin tocar nada existente**:

```java
@Service
public class PagoCriptoStrategy implements EstrategiaPago {
    @Override
    public ResultadoPago procesar(Pago pago) {
        // Conectar wallet, verificar blockchain
        return new ResultadoPago(true, "Pago cripto procesado");
    }
    
    @Override
    public TipoPago getTipo() { return TipoPago.CRIPTO; }
}
```

Spring inyecta automáticamente esta nueva implementación en la lista de `estrategiasDisponibles` del `ProcesadorPago`. Cero modificaciones al código existente. OCP cumplido.

---

### Análisis crítico de la respuesta

