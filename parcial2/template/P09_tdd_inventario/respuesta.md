# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [09]: [P09_tdd_inventario]

### Estudiante
- **Nombre completo**: [Danna Gabriela Rojas Bernal]

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Sin IA — respuesta propia** | [Claude / ChatGPT / Gemini / Copilot / DeepSeek / Qwen / Mistral / Otro / **Sin IA — respuesta propia**] |
| **N/A"** | [Ej: Claude Opus 4.5, GPT-4o, Gemini 2.5 Pro, etc. Si respondes sin IA, escribe "N/A"] |
| **no es necesario** | [Justifica en 1-3 oraciones. Si respondes sin IA, explica por qué decidiste no usar LLM para esta pregunta.] |

---


### Análisis crítico de la respuesta
 **pregunta** Aplica TDD a esta funcionalidad.

Fase red: se escriben pruebas en el que falle la clase princiapal y esta no exista. Las pruebas deben cubrir que al reducir stock a cero el libro quede en estado AGOTADO que se notifique al vendedor en ese momento, que si el stock sigue positivo no cambie nada, que al reponer stock el libro vuelva a DISPONIBLE y que intentar reducir más stock del disponible lance una excepción.
Fase green: se escribe la implementación mínima y suficiente para que todas esas pruebas pasen. Nada más. Si las pruebas no lo exigen, no se implementa.
Fase refactor: mejorar l diseño sin cambiar el comportamiento. Pra este caso Al refactorizar, lo que se busca es que cada parte del código sea responsable de lo que le corresponde; la regla de que un libro se agota cuando su stock llega a cero es una regla de negocio, y por eso tiene más sentido que viva dentro de la entidad Libro y no en el servicio. El servicio termina siendo mucho más limpio porque solo se encarga de coordinar, le dice que reduzca su stock, revisa si quedó agotado, notifica y guarda.




