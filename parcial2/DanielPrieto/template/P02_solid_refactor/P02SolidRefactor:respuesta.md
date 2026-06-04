# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [02]: [P02_solid_Refactor]

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

[Principios violados:

	•	OCP: agregar criptomonedas requiere modificar ProcesadorPago (abrir el if/else).
	•	SRP: una sola clase contiene la lógica de tres métodos de pago completamente diferentes.
	•	LSP (marginal): el throw final implica un contrato roto si se pasa un tipo desconocido.

Patrón adecuado: Strategy

public interface EstrategiaPago {
    ResultadoPago procesar(Pago pago);
}

public class PagoTarjeta implements EstrategiaPago {
    public ResultadoPago procesar(Pago pago) { /* lógica Visa/MC */ }
}
public class PagoPSE implements EstrategiaPago {
    public ResultadoPago procesar(Pago pago) { /* lógica PSE */ }
}
public class PagoPayPal implements EstrategiaPago {
    public ResultadoPago procesar(Pago pago) { /* OAuth + webhook */ }
}

public class ProcesadorPago {
    private final EstrategiaPago estrategia;
    public ProcesadorPago(EstrategiaPago estrategia) { this.estrategia = estrategia; }
    public ResultadoPago procesar(Pago pago) { return estrategia.procesar(pago); }
}


Agregar criptomonedas = nueva clase PagoCripto implements EstrategiaPago. Cero modificaciones al código existente → OCP cumplido.]
