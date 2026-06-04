# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [P02_solid_refactor]

### Estudiante
- **Nombre completo**: [Alejandro Andres Perez Diaz]

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [Claude] |
| **Modelo específico** | [Sonet 4.6] |
| **¿Por qué elegiste este LLM?** | [Porque segun mi opinion esta es la mejor IA para Analisis de Codigo.] |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```
[Actúa como un Ingeniero de Software Senior y realiza lo siguiente:

1.)Identifica y explica detalladamente al menos dos principios SOLID que se estén violando en este código.

2.)Propón una solución mediante un refactoring completo utilizando el patrón de diseño creacional/comportamental más adecuado para eliminar las estructuras condicionales (if-else).

4.) Restricciones Técnicas y Arquitectura (Obligatorio)
Lenguaje: Java 21.


5. Formato de Salida Esperado
Análisis Teórico: Lista y explicación de los principios SOLID violados.

Código Refactorizado: Bloques de código en Markdown limpios, incluyendo interfaces y las implementaciones concretas..

Un buen prompt incluye:
- Contexto del proyecto OpenLib Market
- El código o situación específica
- Lo que esperas que el LLM haga
- Restricciones (ej: "usa Java 21", "aplica SOLID")
- Formato de salida esperado (ej: "respuesta en markdown con código Java")]
```

---

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**
[Pega aquí la respuesta COMPLETA del LLM, sin editar, sin resumir.
Incluye TODO el texto, código, explicaciones que generó el LLM.

Si el LLM generó código, asegúrate de que esté correctamente formateado.
Si tuviste que hacer varias iteraciones, pega la MEJOR respuesta obtenida,
pero menciona cuántas iteraciones hiciste.]
```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

[Evalúa tu propio prompt, no la respuesta del LLM. ¿El contexto fue suficiente? ¿Las restricciones fueron claras? ¿El formato de salida que pediste ayudó a obtener una buena respuesta? ¿Qué parte de tu prompt fue más efectiva? Sé específico: menciona fragmentos concretos de tu prompt que funcionaron bien.]
-Siento que fue bastante claro a la hora de reponder esta pregunta porque fue concreto por ejemplo la primera parte siento que explcia bastante bien que patrones se violan. 


#### 2. ¿Qué se puede mejorar?

[¿Qué le faltó a tu prompt? ¿Qué harías diferente si pudieras reformularlo? ¿El LLM entendió mal algo por falta de claridad en tu prompt? ¿La respuesta tiene errores u omisiones? ¿Qué no cubrió el LLM que tú sí sabes por lo visto en clase?
-Le pondria mas contexto sobre el proyecto de openlib_market  con respecto a errores o omisiones no veo que halla omitido cosas la verdad.]


#### 3. Respuesta final

[O — Open/Closed es la violación más gritada. El if/else if es la señal clásica. Cada vez que llegue un método de pago nuevo — criptomonedas ya está en el enunciado — tienes que abrir ProcesadorPago y agregar otro bloque. El código nunca está "cerrado", siempre hay que modificarlo para extenderlo.
S — Single Responsibility también se viola porque ProcesadorPago conoce la lógica interna de tres sistemas completamente distintos: el protocolo de Visa/Mastercard, el flujo de redirección de PSE, y el OAuth de PayPal. Si PayPal cambia su API, tienes que tocar esta clase. Si PSE cambia su portal, también. Son razones de cambio independientes viviendo en el mismo lugar.
D — Dependency Inversion se viola implícitamente porque la clase depende del string "TARJETA", "PSE", "PAYPAL" para decidir qué lógica ejecutar, en lugar de depender de una abstracción.
-Parte de proponer un Patron de Diseño:
La idea es crear una interfaz común y una implementación por método de pago:
javapublic interface MetodoPago {
    ResultadoPago procesar(Pago pago);
}

public class PagoTarjeta implements MetodoPago {
    public ResultadoPago procesar(Pago pago) {
        // lógica de Visa/Mastercard
    }
}

public class PagoPSE implements MetodoPago {
    public ResultadoPago procesar(Pago pago) {
        // lógica de PSE
    }
}

public class PagoPayPal implements MetodoPago {
    public ResultadoPago procesar(Pago pago) {
        // lógica de OAuth PayPal
    }
}

public class ProcesadorPago {
    private MetodoPago metodo;

    public ProcesadorPago(MetodoPago metodo) {
        this.metodo = metodo;
    }

    public ResultadoPago procesar(Pago pago) {
        return metodo.procesar(pago);
    }
}]
