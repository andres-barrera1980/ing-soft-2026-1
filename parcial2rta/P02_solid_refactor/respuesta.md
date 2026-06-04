# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta 2: Refactorizar un módulo aplicando los principios correctos

### Estudiante
- **Nombre completo**: Samuel Iregui

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Antigravity (Gemini 3.1 Pro High) |
| **Modelo específico** | Gemini 3.1 Pro (High) |
| **¿Por qué elegiste este LLM?** | Elegi este modelo por comodidad propia y debido a que tengo pago gemini pro y ya tengo todo bien organizado para los diferentes trabajos |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```text
Actúa como un arquitecto de software senior revisando el código del proyecto OpenLib Market (Java 21, Spring Boot 3.x). El equipo junior implementó el siguiente código para manejar múltiples métodos de pago:

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

Necesito que:
1. Identifiques qué principios SOLID se están violando (detalla al menos dos y explica por qué).
2. Propongas un refactoring completo utilizando el patrón de diseño más apropiado para eliminar los condicionales `if/else` y hacer el sistema extensible.
3. Generes el código Java refactorizado (incluye la interfaz, las implementaciones concretas y la clase de contexto/fábrica).

Restricciones:
- El código debe aprovechar las características de Java 21.
- El diseño debe permitir agregar un nuevo método de pago (por ejemplo, "CRIPTOMONEDAS") en el futuro SIN tener que modificar las clases existentes.

Formato de salida esperado: Markdown estructurado con secciones para "Diagnóstico de principios violados", "Patrón de diseño elegido" y "Código refactorizado".
```

---

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**
### Diagnóstico de principios violados

1. **Principio de Responsabilidad Única (SRP - Single Responsibility Principle):**
   La clase `ProcesadorPago` tiene múltiples responsabilidades. No solo determina qué flujo seguir dependiendo del método de pago, sino que conoce la lógica específica de cada uno (validar CVV, conectar a OAuth, redirigir a PSE, etc.). Cualquier cambio en la integración de PayPal o en las reglas de tarjeta de crédito obligaría a modificar esta clase.

2. **Principio de Abierto/Cerrado (OCP - Open/Closed Principle):**
   La clase no está abierta a la extensión y cerrada a la modificación. Para agregar un nuevo método de pago (por ejemplo, criptomonedas), es estrictamente necesario entrar a la clase `ProcesadorPago`, añadir un nuevo `else if` y escribir la lógica correspondiente, lo que incrementa el riesgo de introducir bugs en el código que ya funcionaba.

### Patrón de diseño elegido

El patrón ideal para resolver este problema es el **Patrón Strategy (Estrategia)**. Este patrón de comportamiento permite definir una familia de algoritmos (los distintos métodos de pago), encapsular cada uno en una clase separada (las estrategias) y hacerlos intercambiables en tiempo de ejecución. 

Adicionalmente, usamos el principio de inyección de dependencias para delegar la creación y selección de la estrategia correcta, logrando que el contexto se acople únicamente a la interfaz y no a las implementaciones concretas.

### Código refactorizado

```java
// 1. Definir la interfaz Strategy
public interface EstrategiaPago {
    ResultadoPago procesar(Pago pago);
    boolean soporta(String tipoPago);
}

// 2. Implementar estrategias concretas
public class EstrategiaPagoTarjeta implements EstrategiaPago {
    @Override
    public ResultadoPago procesar(Pago pago) {
        // Lógica específica de tarjeta: Validar CVV, fecha expiración, fondos...
        return new ResultadoPago(true, "Pago con tarjeta procesado");
    }

    @Override
    public boolean soporta(String tipoPago) {
        return "TARJETA".equalsIgnoreCase(tipoPago);
    }
}

public class EstrategiaPagoPSE implements EstrategiaPago {
    @Override
    public ResultadoPago procesar(Pago pago) {
        // Lógica específica de PSE: Redirigir a portal bancario...
        return new ResultadoPago(true, "Pago PSE procesado");
    }

    @Override
    public boolean soporta(String tipoPago) {
        return "PSE".equalsIgnoreCase(tipoPago);
    }
}

public class EstrategiaPagoPayPal implements EstrategiaPago {
    @Override
    public ResultadoPago procesar(Pago pago) {
        // Lógica específica de PayPal: Autenticar con OAuth...
        return new ResultadoPago(true, "Pago PayPal procesado");
    }

    @Override
    public boolean soporta(String tipoPago) {
        return "PAYPAL".equalsIgnoreCase(tipoPago);
    }
}

// 3. Crear el Contexto (usando Strategy Inyectadas)
public class ProcesadorPago {
    private final List<EstrategiaPago> estrategias;

    // En Spring Boot, esta lista se inyecta automáticamente con todas las implementaciones de la interfaz
    public ProcesadorPago(List<EstrategiaPago> estrategias) {
        this.estrategias = estrategias;
    }

    public ResultadoPago procesar(Pago pago, String tipoPago) {
        EstrategiaPago estrategiaSeleccionada = estrategias.stream()
                .filter(estrategia -> estrategia.soporta(tipoPago))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Método de pago no soportado: " + tipoPago));

        return estrategiaSeleccionada.procesar(pago);
    }
}
```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

[Evalúa tu propio prompt, no la respuesta del LLM. ¿El contexto fue suficiente? ¿Las restricciones fueron claras? ¿El formato de salida que pediste ayudó a obtener una buena respuesta? ¿Qué parte de tu prompt fue más efectiva? Sé específico: menciona fragmentos concretos de tu prompt que funcionaron bien.]
El promt tiene un correcto uso de contexto y el comportamiento que debe utilizar, las restricciones se dejaron correctamente al final del promt para asegurar que el modelo no omitiera estas restricciones y la parte mas efectiva de mi promt fue el paso a paso de lo que necesitaba que realizara. Como por ejemplo: "Necesito que:
1. Identifiques qué principios SOLID se están violando (detalla al menos dos y explica por qué).
2. Propongas un refactoring completo utilizando el patrón de diseño más apropiado para eliminar los condicionales `if/else` y hacer el sistema extensible.
3. Generes el código Java refactorizado (incluye la interfaz, las implementaciones concretas y la clase de contexto/fábrica)."


#### 2. ¿Qué se puede mejorar?

Se puede mejorar el contexto dado al modelo para que tenga mas informacion de donde salen las cosas ademas de detallar mas tanto las restricciones como el codigo en si que debe modificar y lo necesario que debe permanecer.


#### 3. Respuesta final

En conclusión, el modelo me ayudó a comprender con más detalle varios aspectos que posiblemente habría pasado por alto. Sin embargo, la falta de contexto provocó algunas omisiones en el código, especialmente en relación con otros componentes del sistema que podrían haberse tenido en cuenta para lograr una implementación más completa.
