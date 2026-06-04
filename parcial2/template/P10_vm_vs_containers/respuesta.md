# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [10]: [P10_vm_vs_containers]

### Estudiante
- **Nombre completo**: [Danna Gabriela Rojas Bernal]

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **cklaude** | [Claude / ChatGPT / Gemini / Copilot / DeepSeek / Qwen / Mistral / Otro / **Sin IA — respuesta propia**] |
| **sonnet 4.6** | [Ej: Claude Opus 4.5, GPT-4o, Gemini 2.5 Pro, etc. Si respondes sin IA, escribe "N/A"] |
| **ejercicio me lo pide** | [Justifica en 1-3 oraciones. Si respondes sin IA, explica por qué decidiste no usar LLM para esta pregunta.] |

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
Eres un ingeniero de infraestructura senior trabajando en OpenLib Market, una plataforma 
de compra-venta de libros digitales (Java 21, Spring Boot 3.x). El equipo está evaluando 
si usar máquinas virtuales (VMs) o contenedores para desplegar los servicios de la plataforma.

Necesito que compares ambas tecnologías considerando estos cinco aspectos:
a) Arquitectura: cómo funciona cada una internamente, qué componentes involucra
b) Ventajas de VMs sobre contenedores (mínimo 3)
c) Ventajas de contenedores sobre VMs (mínimo 3)
d) Casos de uso concretos donde conviene cada una
e) Pitfalls o errores comunes al adoptar cada tecnología (mínimo 2 por cada una)

Restricciones:
- Los casos de uso deben ser realistas para una plataforma de e-commerce como OpenLib Market
- Menciona explícitamente las implicaciones de seguridad de compartir kernel en contenedores
- No omitas el tema de aislamiento entre procesos

Formato de salida:
1. Una tabla comparativa con las dimensiones más importantes
2. Explicación detallada de cada sección (a, b, c, d, e)
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
<img width="1120" height="819" alt="image" src="https://github.com/user-attachments/assets/b6214f88-5012-4e3a-a550-a78421f750af" />


### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

[Evalúa tu propio prompt, no la respuesta del LLM. ¿El contexto fue suficiente? ¿Las restricciones fueron claras? ¿El formato de salida que pediste ayudó a obtener una buena respuesta? ¿Qué parte de tu prompt fue más efectiva? Sé específico: menciona fragmentos concretos de tu prompt que funcionaron bien.]
El prompt proporcionó suficiente contexto al solicitar una comparación entre máquinas virtuales y contenedores, permitiendo identificar claramente las diferencias en arquitectura, aislamiento y consumo de recursos. Además, al enfocarse en aspectos técnicos como el uso de hipervisores, sistemas operativos invitados y el kernel compartido, facilitó que la respuesta abordara los conceptos más importantes vistos en clase. La indicación de explicar ventajas, desventajas y casos de uso también ayudó a obtener una respuesta estructurada y fácil de entender.

#### 2. ¿Qué se puede mejorar?

[¿Qué le faltó a tu prompt? ¿Qué harías diferente si pudieras reformularlo? ¿El LLM entendió mal algo por falta de claridad en tu prompt? ¿La respuesta tiene errores u omisiones? ¿Qué no cubrió el LLM que tú sí sabes por lo visto en clase?]

Considero que el prompt pudo ser más específico al pedir una explicación sobre cuándo es más conveniente usar cada tecnología en situaciones reales. También habría sido útil solicitar una comparación más detallada sobre seguridad y rendimiento. Aunque la respuesta fue correcta, se enfocó más en las características generales y no profundizó tanto en algunas ventajas y limitaciones que vimos en clase.

#### 3. Respuesta final

[Escribe tu respuesta definitiva a la pregunta del parcial, integrando lo que aprendiste del LLM pero yendo más allá. Corrige errores, llena omisiones, conecta con conceptos vistos en clase. Esta es tu respuesta: demuestra que tú dominas el tema.]
Las máquinas virtuales y los contenedores buscan resolver un problema similar: ejecutar aplicaciones de forma independiente. Sin embargo, las máquinas virtuales son más pesadas porque cada una funciona como si fuera una computadora completa, mientras que los contenedores aprovechan mejor los recursos porque comparten parte del sistema donde se ejecutan. Esto hace que los contenedores sean más rápidos de crear y consumir menos memoria y almacenamiento. Por otro lado, las máquinas virtuales ofrecen un aislamiento mayor, lo que puede ser útil cuando se necesita más seguridad o separar completamente distintos entornos. En general, los contenedores suelen ser la mejor opción para aplicaciones modernas que requieren rapidez y escalabilidad, mientras que las máquinas virtuales son útiles cuando se necesita un entorno más independiente y aislado.
