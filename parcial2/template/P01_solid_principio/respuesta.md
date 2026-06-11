# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [01]: [P01_solid_srp]

### Estudiante
- **Danna Gabriela Rojas Bernal**
---
NO utilice AI
### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [Claude / ChatGPT / Gemini / Copilot / DeepSeek / Qwen / Mistral / Otro / **Sin IA — respuesta propia**] |
| **Modelo específico** | [Ej: Claude Opus 4.5, GPT-4o, Gemini 2.5 Pro, etc. Si respondes sin IA, escribe "N/A"] |
| **¿Por qué elegiste este LLM?** | [Justifica en 1-3 oraciones. Si respondes sin IA, explica por qué decidiste no usar LLM para esta pregunta.] |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**


### Análisis crítico de la respuesta

**Pregunta**: Identifica qué principio SOLID se está violando (más de uno puede aplicar, pero enfócate en el principal).

El principal principio de solid que se esta violando es el principio de responsabililidad unica ya que en el metodo de publicarlibro() contiene varias responsabilidades dentro de una misma clase, en lugar de encargarse de su logica, tmabien realiza la persistencia en la base de datos, el envio de correos electrónicos, etc; por lo que implica que la clase tiene al menos seis razones diferentes para cambiar, lo que contradice el SRP.



