# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [04]: [P04_Patron de Diseño]

### Estudiante
- **Nombre completo**: [Daniel Prieto]

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [**Sin IA — respuesta propia**] |
| **Modelo específico** | [N/A] |
| **¿Por qué elegiste este LLM?** | [porque decidi retarme a resolver el sin ia, ya que confio en mis conocimientos adquiridos en clase y considero que tengo dominio del tema] |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```
[Pega aquí el prompt exacto que enviaste al LLM. 
Incluye TODO el texto, sin editar ni resumir.

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


#### 2. ¿Qué se puede mejorar?

[¿Qué le faltó a tu prompt? ¿Qué harías diferente si pudieras reformularlo? ¿El LLM entendió mal algo por falta de claridad en tu prompt? ¿La respuesta tiene errores u omisiones? ¿Qué no cubrió el LLM que tú sí sabes por lo visto en clase?]


#### 3. Respuesta final

[Patrón: Strategy (con composición de estrategias)

Alternativas descartadas:

	•	Template Method: no permite combinar descuentos en tiempo de ejecución, la combinación está hardcodeada en herencia.
	•	Decorator: puede funcionar pero está pensado para añadir comportamiento a objetos individuales, no para selección dinámica de reglas de negocio.

public interface EstrategiaDescuento {
    BigDecimal calcularDescuento(Carrito carrito, Usuario usuario);
}

public class DescuentoFidelidad implements EstrategiaDescuento {
    public BigDecimal calcularDescuento(Carrito carrito, Usuario usuario) {
        return usuario.getComprasHistoricas() > 5
            ? carrito.getTotal().multiply(BigDecimal.valueOf(0.10))
            : BigDecimal.ZERO;
    }
}

public class DescuentoTemporada implements EstrategiaDescuento {
    public BigDecimal calcularDescuento(Carrito carrito, Usuario usuario) {
        return esSemanaDelLibro() ? carrito.getTotal().multiply(BigDecimal.valueOf(0.15)) : BigDecimal.ZERO;
    }
}

public class DescuentoVolumen implements EstrategiaDescuento {
    public BigDecimal calcularDescuento(Carrito carrito, Usuario usuario) {
        return carrito.getTotal().compareTo(new BigDecimal("200000")) > 0
            ? carrito.getTotal().multiply(BigDecimal.valueOf(0.05)) : BigDecimal.ZERO;
    }
}

public class CalculadorDescuentos {
    private final List<EstrategiaDescuento> estrategias;
    public CalculadorDescuentos(List<EstrategiaDescuento> estrategias) { this.estrategias = estrategias; }
    
    public BigDecimal calcularTotal(Carrito carrito, Usuario usuario) {
        BigDecimal totalDescuento = estrategias.stream()
            .map(e -> e.calcularDescuento(carrito, usuario))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        return carrito.getTotal().subtract(totalDescuento);
    }
}]
