# Guía de prompting para ingeniería de software

> **Objetivo**: Esta guía te ayudará a formular prompts efectivos para el Parcial 2. Recuerda: en este parcial evaluamos tanto la calidad de tu prompt (30%) como tu capacidad de análisis crítico (70%). Un buen prompt es el primer paso para una buena respuesta.

---

## 1. Anatomía de un buen prompt técnico

Un prompt efectivo para tareas de ingeniería de software tiene **5 componentes**:

```
[CONTEXTO]  → ¿Qué proyecto es? ¿Qué stack usa? ¿Qué módulo?
[PROBLEMA]  → ¿Qué necesitas resolver? Sé específico.
[RESTRICCIONES] → ¿Qué debe y NO debe hacer? Versiones, frameworks, principios.
[FORMATO]   → ¿Cómo esperas la respuesta? Código, diagrama, tabla, explicación.
[VALIDACIÓN] → ¿Cómo sabrás si la respuesta es buena? Criterios de éxito.
```

### Ejemplo de prompt débil vs. prompt fuerte

**Débil** ❌:
> Refactoriza esta clase para que cumpla con SOLID.
> ```java
> public class GestorLibro { ... }
> ```

**Fuerte** ✅:
> Eres un ingeniero de software senior revisando código del proyecto OpenLib Market, una plataforma de compra-venta de libros (Java 21, Spring Boot 3.x, PostgreSQL). Adjunto la clase `GestorLibro` del módulo de administración.
>
> Necesito que:
> 1. Identifiques qué principio SOLID se está violando principalmente.
> 2. Propongas un refactoring paso a paso aplicando ese principio.
> 3. Generes el código Java refactorizado (clases separadas, interfaces, pruebas unitarias con JUnit 5).
>
> Restricciones:
> - Usa inyección de dependencias (Spring).
> - El código debe compilar en Java 21.
> - Cada clase debe tener una sola responsabilidad.
>
> Formato de salida: markdown estructurado con secciones "Diagnóstico", "Refactoring propuesto", "Código final", y "Pruebas".
>
> ```java
> public class GestorLibro { ... }
> ```

---

## 2. Técnicas que mejoran la respuesta del LLM

### Chain of Thought (Razonamiento paso a paso)
Pide al LLM que explique su razonamiento antes de dar la respuesta final. Esto reduce errores en tareas complejas.

> "Antes de escribir el código, explica tu razonamiento: ¿qué principio se viola, por qué, y cuál será tu estrategia de refactoring?"

### Role prompting
Asigna un rol al LLM. Esto activa patrones de respuesta más específicos.

> "Actúa como un arquitecto de software revisando un diseño para una plataforma de e-commerce."

### Few-shot prompting
Da ejemplos de lo que esperas como entrada y salida.

> "Aquí hay un ejemplo del formato que espero:
> **Entrada**: clase con múltiples responsabilidades
> **Salida**: diagnóstico + refactoring + código + pruebas"

### Self-consistency
Para decisiones importantes, pide al LLM que genere múltiples alternativas y las compare.

> "Propón 3 diseños alternativos para este módulo y compáralos en una tabla (criterios: acoplamiento, cohesión, testabilidad, complejidad)."

---

## 3. Errores comunes al formular prompts técnicos

| Error | Por qué es un problema | Cómo evitarlo |
|---|---|---|
| Preguntar sin contexto | El LLM asume un proyecto genérico y la respuesta no aplica a OpenLib Market. | Siempre menciona: proyecto, stack, módulo, y el objetivo del sistema. |
| No especificar versiones | El LLM puede generar código para Java 8 o usar APIs deprecadas. | Indica: Java 21, Spring Boot 3.x, JUnit 5, PostgreSQL 16. |
| No pedir formato de salida | El LLM decide si te da código, explicación, diagrama o todo mezclado. | Sé explícito: "Respuesta en markdown con código en bloques ```java". |
| Pregunta demasiado abierta | "¿Cómo mejoro este código?" es vago. El LLM puede ignorar cosas importantes. | Sé específico: "Identifica violaciones de SOLID, problemas de seguridad, y código duplicado." |
| No validar la respuesta | Aceptar la primera respuesta sin cuestionar. Los LLMs se equivocan, alucinan APIs inexistentes, o pasan por alto errores sutiles. | Lee el código generado línea por línea. ¿Compila? ¿Usa imports correctos? ¿Los tipos son consistentes? |

---

## 4. Estrategia de iteración

No te conformes con la primera respuesta. El proceso recomendado es:

```
Paso 1: Prompt inicial (contexto + problema + restricciones + formato)
          ↓
Paso 2: Evaluar respuesta → ¿Errores? ¿Omisiones?
          ↓
Paso 3: Prompt de refinamiento ("En tu respuesta anterior faltó X. También necesito Y.")
          ↓
Paso 4: Comparar respuestas → ¿La segunda es mejor?
          ↓
Paso 5: Documentar el proceso en Git (commits separados)
```

Cada iteración debe ser un commit diferente. Esto demuestra tu proceso de pensamiento y puede sumar puntos de bonificación.

---

## 5. Señales de que el LLM puede estar equivocado

Los LLMs no son perfectos. Desconfía si ves:

- **Código que usa APIs que no existen**: Spring Boot no tiene `@MagicAutowired`, JUnit 5 no tiene `@Test(expected=...)`.
- **Explicaciones demasiado genéricas**: "Este código viola buenas prácticas" sin decir cuáles.
- **Confianza excesiva en respuestas incorrectas**: el LLM rara vez dice "no sé" o "podría estar equivocado".
- **Inconsistencias**: en una parte dice que uses Strategy y en otra implementa Factory sin notarlo.
- **Código que no maneja casos borde**: `null`, listas vacías, valores negativos, concurrencia.

**Tu trabajo es ser el revisor, no el copista.**

---

## 6. Cómo elegir el LLM adecuado para cada pregunta

| Tipo de tarea | LLMs recomendados | Por qué |
|---|---|---|
| Análisis de código y refactoring | Claude, GPT-4o | Mejor razonamiento sobre código |
| Generación de pruebas | Claude, Copilot | Mejor cobertura de casos borde |
| Docker y DevOps | GPT-4o, Claude | Buen conocimiento de sintaxis y mejores prácticas |
| Arquitectura y diseño | Claude Opus, Gemini | Mejor en razonamiento abstracto y trade-offs |
| Código boilerplate | Copilot, Qwen | Más rápido para código repetitivo |

No uses un solo LLM para todo. La elección del LLM hace parte de tu criterio como ingeniero.
