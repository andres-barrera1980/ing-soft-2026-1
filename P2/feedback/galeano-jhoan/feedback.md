# Feedback — Parcial 2
## Ingeniería de Software 2026-1

### Estudiante: Jhoan Galeano
### Sin IA: 6 preguntas ⭐ (P01, P03, P06, P09, P11, P12)

---

**Calificación total: 24.2 / 50 puntos (48.4%) → 2.4 / 5.0**

---

## Resumen ejecutivo

Jhoan presenta un parcial con **calidad de prompts aceptable**, pero con **análisis críticos extremadamente pobres**. La principal debilidad es que las respuestas Sin IA carecen de desarrollo concreto — no hay código, ejemplos ni justificaciones técnicas. En Con IA, el estudiante se limita a 1-2 oraciones genéricas sin agregar valor sobre lo que el LLM ya produjo.

**Puntos destacados:**
- P02 y P04: prompts bien estructurados con respuestas útiles del LLM
- P05 y P08: el LLM generó código funcional de calidad

**Áreas críticas:**
- **P02: No pegó la respuesta del LLM** — sección vacía, análisis inverificable
- **P07: No detectó que el LLM omitió el diagrama Mermaid** solicitado
- **P13: Afirma que la solución es "testeable" pero el LLM generó cero pruebas**
- **Preguntas Sin IA:** todas extremadamente breves, sin código, sin profundidad
- **P09:** Siendo TDD (requiere código), la hizo Sin IA con solo 2 oraciones metacognitivas

---

## Detalle por pregunta

### P01 — SOLID: Principio en GestorLibro ⭐ (1.8/3.0)

Identifica SRP correctamente, pero no menciona "razón de cambio" que es central para explicar SRP. No identifica DIP como segundo principio violado. No propone refactorización concreta ni muestra código. Análisis vago y genérico.

**Bono +20%:** 1.5 → 1.8

---

### P02 — SOLID: Refactoring ProcesadorPago — Gemini (2.3/4.0)

**Prompt (75%):** Bien estructurado, con contexto y código.

**⚠️ No pegó la respuesta del LLM:** La sección de respuesta del LLM está vacía. Esto hace que el análisis sea parcialmente inverificable.

**Análisis (50%):** Reconoce SRP, OCP y Strategy como patrón adecuado. Menciona mejora con `Map<String, Supplier>`. Sin embargo, al no tener la respuesta para contrastar, el análisis queda incompleto.

**Fórmula:** 4 × (0.30 × 0.75 + 0.70 × 0.50) = 2.3

---

### P03 — SOLID: Jerarquía de usuarios ⭐ (0.6/3.0)

No nombra principios SOLID específicos (no identifica LSP ni ISP como violados). No propone rediseño concreto de la jerarquía. Menos de 5 oraciones en total. Sin código.

**Bono +20%:** 0.5 → 0.6

---

### P04 — Patrones: Estrategias de descuento — Claude (1.6/3.0)

**Prompt (80%):** Buen prompt pidiendo identificación de patrón, descarte de alternativas e implementación.

**Respuesta LLM:** Excelente. Claude eligió Decorator y descartó correctamente Strategy y Chain of Responsibility.

**Análisis (40%):** Extremadamente superficial. Cada pregunta del análisis se responde con 1-2 oraciones cortas. No profundiza en por qué Decorator resuelve la composición mejor que las alternativas. No detecta omisiones del LLM.

**Fórmula:** 3 × (0.30 × 0.80 + 0.70 × 0.40) = 1.6

---

### P05 — Patrones: Notificaciones combinadas — Claude (2.3/5.0)

**Prompt (80%):** Bien estructurado con dos patrones, diagrama, código y manejo de errores.

**Respuesta LLM:** Excelente. Observer + Command con manejo de errores con try-catch y desuscripción dinámica.

**Análisis (30%):** Muy pobre. Respuestas de 1 oración ("si, tiene sentido"). No detecta que el manejo de errores ya usa try-catch para no interrumpir el ciclo. No hay crítica constructiva.

**Fórmula:** 5 × (0.30 × 0.80 + 0.70 × 0.30) = 2.3

---

### P06 — Clean Architecture: Capas ⭐ (0.6/4.0)

Solo menciona 2 de las 4 capas de Clean Architecture. No explica la Regla de Dependencia. Sin código, sin diagrama, sin ejemplo concreto de OpenLib Market. Menos de 6 oraciones.

**Bono +20%:** 0.5 → 0.6

---

### P07 — Clean Architecture: Pagos — Claude (1.9/4.0)

**Prompt (75%):** Prompt adecuado pidiendo capas, dependencias y diagrama Mermaid.

**Respuesta LLM:** El LLM generó solución con separación por capas. **Omitió el diagrama Mermaid que el prompt solicitaba explícitamente.**

**Análisis (35%):** El estudiante no detectó que el LLM omitió el diagrama solicitado. Análisis genérico sin crítica técnica sustancial.

**Fórmula:** 4 × (0.30 × 0.75 + 0.70 × 0.35) = 1.9

---

### P08 — Pruebas: CarritoService — Claude (2.3/5.0)

**Prompt (85%):** Buen prompt con JUnit 5 + Mockito y casos normal, borde y excepción.

**Respuesta LLM:** Suite de pruebas unitarias completa y funcional.

**Análisis (30%):** Superficial. No identifica qué casos de borde faltan, no critica la calidad de los mocks, no sugiere mejoras. No demuestra comprensión de qué hace una buena prueba.

**Fórmula:** 5 × (0.30 × 0.85 + 0.70 × 0.30) = 2.3

---

### P09 — TDD: ControlInventario ⭐ (1.0/4.0)

Pregunta que requiere código (ciclo Red-Green-Refactor con JUnit). El estudiante la hizo Sin IA y escribió solo 2 oraciones metacognitivas diciendo que "aprendió la importancia de TDD". Sin código, sin pruebas, sin demostración del ciclo.

**Bono +20%:** 0.8 → 1.0

---

### P10 — VM vs Containers — Claude (2.1/5.0)

**Prompt (80%):** Buen prompt con tabla comparativa, ventajas/desventajas.

**Respuesta LLM:** El LLM produjo una comparación VM vs contenedores completa y detallada.

**Análisis (25%):** El estudiante dice que "no todos los casos de uso son realistas" pero **no especifica cuáles ni por qué**. Crítica hueca sin sustento. Sin conexión con OpenLib Market, sin reflexión sobre cuándo aplicar cada tecnología.

**Fórmula:** 5 × (0.30 × 0.80 + 0.70 × 0.25) = 2.1

---

### P11 — Pruebas manuales vs automatizadas ⭐ (2.6/4.0)

Menciona estrategia híbrida (pirámide de automatización) pero solo cubre 1-2 de las 3+ ventajas/desventajas requeridas. Sin ejemplos concretos de OpenLib Market.

**Bono +20%:** 2.2 → 2.6

---

### P12 — Ciclo de vida de defectos ⭐ (1.4/3.0)

Solo cubre 3 de las 7 fases del ciclo de vida de defectos. La respuesta se corta abruptamente. Sin ejemplos concretos ni herramientas mencionadas.

**Bono +20%:** 1.2 → 1.4

---

### P13 — Integración: Refactoring Notificador — Claude (2.0/3.0)

**Prompt (80%):** Prompt adecuado pidiendo refactorización con SOLID + patrón + JUnit.

**Respuesta LLM:** El LLM refactorizó con DIP/Strategy pero **no generó pruebas unitarias** a pesar de solicitarlas en el prompt.

**Análisis (50%):** Reconoce aciertos del LLM. Sin embargo, afirma que la solución es "completa y testeable" cuando el LLM **no generó ni una sola prueba** — contradicción que el estudiante no detectó.

**Fórmula:** 3 × (0.30 × 0.80 + 0.70 × 0.50) = 2.0

---

## Tabla resumen

| # | Tema | Pts | % Prompt | % Análisis | Nota |
|---|------|:---:|:--------:|:----------:|:----:|
| 1 | SOLID — GestorLibro ⭐ | 3 | — | — | 1.8 |
| 2 | SOLID — Refactoring ProcesadorPago | 4 | 75% | 50% | 2.3 |
| 3 | SOLID — Jerarquía usuarios ⭐ | 3 | — | — | 0.6 |
| 4 | Patrones — Descuento | 3 | 80% | 40% | 1.6 |
| 5 | Patrones — Notificaciones | 5 | 80% | 30% | 2.3 |
| 6 | Clean Architecture — Capas ⭐ | 4 | — | — | 0.6 |
| 7 | Clean Architecture — Pagos | 4 | 75% | 35% | 1.9 |
| 8 | Pruebas — CarritoService | 5 | 85% | 30% | 2.3 |
| 9 | TDD — ControlInventario ⭐ | 4 | — | — | 1.0 |
| 10 | VM vs Containers | 5 | 80% | 25% | 2.1 |
| 11 | Pruebas manuales vs auto ⭐ | 4 | — | — | 2.6 |
| 12 | Ciclo de vida defectos ⭐ | 3 | — | — | 1.4 |
| 13 | Integración — Notificador | 3 | 80% | 50% | 2.0 |
| | **TOTAL** | **50** | | | **24.2** |

---

## Fortalezas generales

1. **Prompts funcionales:** Cuando usó IA, los prompts fueron adecuados y el LLM produjo respuestas de calidad.
2. **Conceptos básicos correctos (Con IA):** En preguntas con LLM, logró identificar los patrones y principios correctos.

## Áreas de mejora

1. **Análisis crítico insuficiente:** El análisis se limita a 1-2 oraciones por pregunta, sin profundidad técnica, sin detectar omisiones del LLM. El propósito del examen es agregar valor, no resumir lo que la IA ya dijo.
2. **Respuestas Sin IA sin código:** Las 6 preguntas Sin IA carecen de implementación concreta. El estudiante describe conceptos vagamente pero no demuestra capacidad de aplicarlos.
3. **Omisiones del LLM no detectadas:** P07 (diagrama faltante), P13 (pruebas faltantes) — el estudiante no identificó errores del LLM.
4. **P02 sin respuesta del LLM:** No pegar la respuesta del LLM es un error de procedimiento que dificulta la evaluación.

---

*Feedback generado el 13 de junio de 2026. Nota preliminar sujeta a revisión del profesor.*
