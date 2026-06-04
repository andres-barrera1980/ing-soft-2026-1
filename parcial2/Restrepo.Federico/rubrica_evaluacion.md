# Rúbrica de evaluación — Parcial 2
## Ingeniería de Software con LLMs — 50 puntos

---

## Componente 1: Calidad del prompt (30% del puntaje de cada pregunta)

Evalúa qué tan bien formula el estudiante la pregunta al LLM. Un prompt de calidad obtiene respuestas más precisas y útiles.

| Nivel | Puntaje (%) | Descriptor |
|---|---|---|
| **Excelente** | 90-100% | El prompt incluye: contexto completo del proyecto OpenLib Market, la pregunta o problema específico con código o escenario detallado, restricciones explícitas (versión de Java, principios a aplicar, frameworks), formato de salida solicitado, y ejemplos concretos. El prompt demuestra que el estudiante entendió el problema antes de preguntar. |
| **Bueno** | 70-89% | El prompt incluye la mayoría de los elementos anteriores pero le falta alguno — por ejemplo: tiene contexto pero no restricciones, o tiene restricciones pero no pidió formato de salida. El prompt es claro pero podría ser más específico. |
| **Regular** | 50-69% | El prompt es genérico o incompleto. Plantea la pregunta pero sin suficiente contexto del proyecto ni restricciones técnicas. El LLM tendría que adivinar detalles importantes para dar una respuesta útil. |
| **Insuficiente** | 0-49% | El prompt es una copia literal del enunciado sin elaboración, o es tan vago que cualquier LLM daría una respuesta igualmente vaga. No hay evidencia de que el estudiante reflexionó sobre cómo preguntar. |

### Criterios específicos para evaluar el prompt

| Criterio | Qué buscar |
|---|---|
| **Contexto del proyecto** | ¿Menciona OpenLib Market? ¿Describe el módulo o funcionalidad relevante? |
| **Especificidad técnica** | ¿Indica versiones (Java 21, Spring Boot 3.x, JUnit 5)? ¿Menciona el stack? |
| **Restricciones** | ¿Pide aplicar principios SOLID o patrones específicos? ¿Define límites? |
| **Formato de salida** | ¿Solicita un formato concreto (código Java, diagrama Mermaid, tabla, markdown)? |
| **Ejemplos o casos** | ¿Proporciona ejemplos de entrada/salida esperados? ¿Casos de prueba? |
| **Claridad general** | ¿El prompt es comprensible sin ambigüedades? ¿Está bien estructurado? |

---

## Componente 2: Calidad del análisis (70% del puntaje de cada pregunta)

Evalúa la capacidad del estudiante de evaluar críticamente la respuesta del LLM y producir una síntesis propia.

| Nivel | Puntaje (%) | Descriptor |
|---|---|---|
| **Excelente** | 90-100% | El estudiante identifica con precisión los aciertos y errores de la respuesta, incluyendo omisiones sutiles (casos borde, implicaciones de diseño, trade-offs). La síntesis propia demuestra dominio profundo de los conceptos de la materia: corrige errores del LLM, agrega lo que faltó, y conecta con conceptos vistos en clase (referencias a presentaciones, talleres, principios estudiados). El análisis es específico — cita fragmentos concretos de la respuesta del LLM, no generalidades. |
| **Bueno** | 70-89% | El estudiante identifica la mayoría de los aciertos y errores evidentes, pero puede pasar por alto omisiones más sutiles o no profundizar en el "por qué" de los errores. La síntesis propia es correcta pero no necesariamente supera a la respuesta del LLM de forma significativa. Hay conexión con conceptos del curso pero de forma más general. |
| **Regular** | 50-69% | El análisis es superficial. Identifica algunos aciertos o errores obvios pero no profundiza. La síntesis propia es esencialmente un resumen de lo que dijo el LLM sin agregar valor sustancial. La conexión con conceptos del curso es genérica ("esto se relaciona con SOLID") sin explicar cómo. |
| **Insuficiente** | 0-49% | No hay análisis crítico real: el estudiante solo describe lo que dijo el LLM ("el LLM respondió que...") sin evaluarlo. O peor: afirma que la respuesta es perfecta sin examinarla. No hay síntesis propia. No conecta con conceptos de la materia. |

### Criterios específicos para evaluar el análisis

| Criterio | Qué buscar |
|---|---|
| **Identificación de aciertos** | ¿Señala qué partes de la respuesta son correctas y por qué? ¿Es específico? |
| **Identificación de errores** | ¿Detecta errores técnicos (código que no compila, patrón mal aplicado, concepto erróneo)? |
| **Identificación de omisiones** | ¿Nota lo que el LLM NO dijo? Casos borde, trade-offs, alternativas, riesgos. |
| **Profundidad del análisis** | ¿Va más allá de lo obvio? ¿Explica el "por qué" de cada hallazgo? |
| **Calidad de la síntesis** | ¿Su respuesta final es mejor que la del LLM? ¿Corrige, completa, mejora? |
| **Conexión con el curso** | ¿Referencia conceptos, presentaciones, talleres o principios vistos en clase? |
| **Pensamiento crítico** | ¿Cuestiona la respuesta del LLM en lugar de aceptarla? ¿Propone alternativas? |

---

## Ponderación final por pregunta

Para cada pregunta, la nota se calcula como:

```
Nota_pregunta = (Max_puntos_pregunta) × (0.30 × %Calidad_Prompt + 0.70 × %Calidad_Análisis)
```

Donde `%Calidad_Prompt` y `%Calidad_Análisis` son porcentajes entre 0 y 1 según la rúbrica anterior.

**Ejemplo para una pregunta de 4 puntos:**
- Prompt: Bueno (80%) → 0.30 × 0.80 = 0.24
- Análisis: Excelente (95%) → 0.70 × 0.95 = 0.665
- Total: 4 × (0.24 + 0.665) = 4 × 0.905 = 3.62 → 3.6 puntos

---

## Criterios transversales

Estos aplican a todas las preguntas y pueden sumar o restar hasta un 5% de la nota final.

### Bonificaciones (+)

| Criterio | Bonus |
|---|---|
| El estudiante respondió una pregunta **sin usar IA** (marcada ⭐). La pregunta se evalúa al 100% sobre calidad del análisis. | **+20%** sobre la nota de esa pregunta |
| El estudiante respondió una pregunta **sin usar IA** (no marcada ⭐). | **+10%** sobre la nota de esa pregunta |
| El estudiante iteró sobre el prompt (varios intentos documentados en commits) y mejoró progresivamente la respuesta. | +0.5 pts sobre la nota final |
| El estudiante probó el código generado por el LLM (lo compiló, ejecutó, o testeó) y documentó los resultados. | +0.5 pts sobre la nota final |
| El estudiante usó más de un LLM para comparar respuestas y eligió la mejor, documentando la comparación. | +0.5 pts sobre la nota final |

### Penalizaciones (−)

| Criterio | Penalización |
|---|---|
| No se identifica qué LLM se usó o no se justifica la elección. | −0.5 pts sobre la nota final |
| La respuesta del LLM está editada, resumida o incompleta (se requiere transparencia total). | −1.0 pt sobre la nota final |
| El análisis es genérico y repetitivo entre preguntas (copy-paste de frases como "el LLM respondió bien"). | −1.0 pt sobre la nota final |
| No hay evidencia de commits separados para prompt y análisis (un solo commit masivo al final). | −0.5 pts sobre la nota final |
| El estudiante usó IA en una pregunta pero no incluyó el prompt completo ni la respuesta del LLM. | −1.0 pt sobre la nota final |
| El estudiante marcó "Sin IA" pero hay evidencia de uso de LLM en la respuesta. | −2.0 pts sobre la nota final (anulación de la pregunta) |

---

## Tabla resumen de evaluación

| # | Tema | Puntos | % Prompt | % Análisis | Puntaje |
|---|---|---|---|---|---|
| 1 | SOLID — Principio violado en GestorLibro | 3 | | | |
| 2 | SOLID — Refactoring ProcesadorPago | 4 | | | |
| 3 | SOLID — Jerarquía de usuarios | 3 | | | |
| 4 | Patrones — Estrategias de descuento | 3 | | | |
| 5 | Patrones — Notificaciones combinadas | 5 | | | |
| 6 | Clean Architecture — Capas y dependencia | 4 | | | |
| 7 | Clean Architecture — Aplicación a pagos | 4 | | | |
| 8 | Pruebas — Unit Testing CarritoService | 5 | | | |
| 9 | Pruebas — TDD ControlInventario | 4 | | | |
| 10 | VM vs Containers — Comparación | 5 | | | |
| 11 | Pruebas manuales vs automatizadas | 4 | | | |
| 12 | Ciclo de vida de defectos | 3 | | | |
| 13 | Integración — Refactoring Notificador | 3 | | | |
| | **TOTAL** | **50** | | | |
| | Bonificaciones | | | | |
| | Penalizaciones | | | | |
| | **NOTA FINAL** | | | | |
