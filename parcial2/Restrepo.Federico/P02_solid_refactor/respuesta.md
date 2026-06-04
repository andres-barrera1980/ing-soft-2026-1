# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta 02: SOLID - Refactoring ProcesadorPago

### Estudiante
- **Nombre completo**: Federico Restrepo

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Gemini |
| **Modelo específico** | Gemini 3.1 Pro (High) |
| **¿Por qué elegiste este LLM?** | Gemini 3.1 Pro es excelente para refactorizar código y aplicar patrones de diseño como el Strategy. |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```text
Actúa como un Arquitecto de Software Senior. 
Contexto: En el proyecto OpenLib Market, tenemos una clase `ProcesadorPago` que actualmente maneja múltiples métodos de pago usando un bloque if/else muy grande en su método `procesar(Pago pago)`.

Código a analizar:
public class ProcesadorPago {
    private String tipoPago; // "TARJETA", "PSE", "PAYPAL"
    
    public ProcesadorPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }
    
    public ResultadoPago procesar(Pago pago) {
        if (tipoPago.equals("TARJETA")) {
            return new ResultadoPago(true, "Pago con tarjeta procesado");
        } else if (tipoPago.equals("PSE")) {
            return new ResultadoPago(true, "Pago PSE procesado");
        } else if (tipoPago.equals("PAYPAL")) {
            return new ResultadoPago(true, "Pago PayPal procesado");
        }
        throw new IllegalArgumentException("Método de pago no soportado");
    }
}

Tarea: Identifica qué principios SOLID se están violando en este código (al menos dos) y propón un refactoring completo usando el patrón de diseño que consideres más apropiado. Escribe el código en Java 21, separa todo en interfaces y clases, y explica cómo tu diseño permite agregar nuevos métodos de pago a futuro sin modificar el código existente.
```

---

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**
**Principios SOLID violados:**
1. **Open/Closed Principle (OCP)**: La clase no está cerrada a la modificación. Cada vez que se quiera agregar un nuevo método de pago, hay que modificar esta clase y agregar otro `else if`.
2. **Single Responsibility Principle (SRP)**: La clase asume la responsabilidad de procesar tarjetas, redirigir a PSE y autenticar con PayPal. Son responsabilidades distintas.

**Patrón de Diseño aplicado: Strategy (Estrategia)**
Usamos el patrón Strategy para definir una familia de algoritmos (métodos de pago), encapsular cada uno en su propia clase, y hacerlos intercambiables.

```java
// 1. Interfaz común (Estrategia)
public interface EstrategiaPago {
    ResultadoPago procesar(Pago pago);
}

// 2. Estrategias concretas
public class PagoTarjeta implements EstrategiaPago {
    @Override
    public ResultadoPago procesar(Pago pago) {
        return new ResultadoPago(true, "Pago con tarjeta procesado");
    }
}

public class PagoPSE implements EstrategiaPago {
    @Override
    public ResultadoPago procesar(Pago pago) {
        return new ResultadoPago(true, "Pago PSE procesado");
    }
}

public class PagoPayPal implements EstrategiaPago {
    @Override
    public ResultadoPago procesar(Pago pago) {
        return new ResultadoPago(true, "Pago PayPal procesado");
    }
}

// 3. El contexto (ProcesadorPago refactorizado)
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
*Con este diseño, si mañana agregamos Bitcoin, solo creamos la clase `PagoBitcoin implements EstrategiaPago` sin tocar las demás clases (cumpliendo OCP a la perfección).*

---

### Análisis crítico de la respuesta

El prompt funcionó bien porque le pasé el código exacto y le exigí Java 21 con separación en interfaces, lo que lo obligó a dar una solución concreta en vez de algo genérico.
La IA identificó bien OCP y SRP y aplicó Strategy correctamente, pero omitió algo que no caí en el momento: el código original recibía un String tipoPago y ella inyectó EstrategiaPago directo sin explicar cómo instanciarla desde ese String. Nos faltó pedirle que resolviera también la creación de objetos.
La solución completa necesita combinar Strategy con un Map<String, EstrategiaPago> preconfigurado. Si no, los if-else simplemente se mueven a otra clase y el problema de fondo sigue ahí.