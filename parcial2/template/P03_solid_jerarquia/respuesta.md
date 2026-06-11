# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [03]: [P03_solid_jerarquia]

### Estudiante
- **Nombre completo**: [Danna Gabriela rRojas Bernal]

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Sin IA — respuesta propia** | [Claude / ChatGPT / Gemini / Copilot / DeepSeek / Qwen / Mistral / Otro / **Sin IA — respuesta propia**] |
| **N/A** | [Ej: Claude Opus 4.5, GPT-4o, Gemini 2.5 Pro, etc. Si respondes sin IA, escribe "N/A"] |
| **Por que no me parece complicada la pregunta y estoy en la capacidad de responderla** | [Justifica en 1-3 oraciones. Si respondes sin IA, explica por qué decidiste no usar LLM para esta pregunta.] |

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

**pregunta**: Analiza esta jerarquía de clases.
AL analizar la jerarquia principalmente se incumple el principio de sustitucion de liskov y el ISP. Ya que la interfaz Usuario obliga a implementar métodos que algunos usuarios realmente no necesitan, lo que lleva a lanzar excepciones como UnsupportedOperationException. También se incumple LSP porque clases como Vendedor modifican el comportamiento heredado de Comprador, impidiendo que puedan sustituirlo correctamente. Por loq ue una solucion sería crear interfaces especificas para cada rol, asi cada usuario implemente únicamente las funcionalidades que le corresponden y sea facil de mantener.

#### 1. ¿Qué hizo bien el prompt?

[Evalúa tu propio prompt, no la respuesta del LLM. ¿El contexto fue suficiente? ¿Las restricciones fueron claras? ¿El formato de salida que pediste ayudó a obtener una buena respuesta? ¿Qué parte de tu prompt fue más efectiva? Sé específico: menciona fragmentos concretos de tu prompt que funcionaron bien.]


#### 2. ¿Qué se puede mejorar?

[¿Qué le faltó a tu prompt? ¿Qué harías diferente si pudieras reformularlo? ¿El LLM entendió mal algo por falta de claridad en tu prompt? ¿La respuesta tiene errores u omisiones? ¿Qué no cubrió el LLM que tú sí sabes por lo visto en clase?]


#### 3. Respuesta final

[Escribe tu respuesta definitiva a la pregunta del parcial, integrando lo que aprendiste del LLM pero yendo más allá. Corrige errores, llena omisiones, conecta con conceptos vistos en clase. Esta es tu respuesta: demuestra que tú dominas el tema.]
