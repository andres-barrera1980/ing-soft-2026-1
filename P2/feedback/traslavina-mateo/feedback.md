# Feedback — Parcial 2: Prompting

**Estudiante:** Mateo Traslaviña Moreno
**LLM utilizado:** Claude Sonnet 4.6 (todas las preguntas)
**Fecha de revisión:** 2026-06-10

---

## Resumen ejecutivo

**Nota final: 37.4 / 53 (70.6%)**

Buen desemgeneral. Los prompts están bien estructurados y el código generado por el LLM refleja la calidad de las entradas. El análisis muestra evidencia de uso de IA en varias respuestas, particularmente en P03, P06, P12 y P13. Las respuestas de código (P01, P05, P08, P09, P10) son las más fuertes. Las áreas de mejora están en los análisis manuales donde la comprensión de conceptos se ve limitada.

---

## Evaluación por pregunta

### P01 — Principios SOLID en Código (⭐) — 2.5/3

**Prompt:** Excelente estructura, proporciona código concreto del proyecto OpenLib Market, solicita identificación de violaciones SOLID y refactorización con ejemplos específicos.

**Respuesta del LLM:** El código resultante refleja bien la entrada del prompt. La refactorización es clara y aplica correctamente los principios SOLID.

**Análisis del estudiante:** Bueno. Identifica los principios relevantes (OCP, LSP, DIP). El código funciona bien para preguntas de código.

**Nota:** 2.5/3

---

### P02 — Diseño de Interfaces y Polimorfismo — 3.0/3

**Prompt:** Muy buen prompt con excelente estructura. El código resultante refleja la entrada.

**Respuesta del LLM:** Implementa correctamente el patrón Strategy con interfaces para medios de pago.

**Análisis del estudiante:** Identifica correctamente los principios más relevantes (OCP, LSP, DIP). Nota que falta LSP — un medio de pago puede reemplazar al otro, lo cual es una observación válida.

**Nota:** 3.0/3

---

### P03 — Testing y TDD (⭐) — 0.5/3

**Prompt:** Aceptable pero con problemas de comprensión.

**Respuesta del LLM:** Respuesta generada con IA evidente. El análisis muestra falta de comprensión profunda de los conceptos de TDD.

**Análisis del estudiante:** Evidencia clara de uso de IA. Los textos como "También omitió la transaccionalidad..." muestran patrones de escritura generados por IA.

**Nota:** 0.5/3

---

### P04 — Patrones de Diseño — 2.0/3

**Prompt:** Aceptable.

**Respuesta del LLM:** Las alternativas propuestas no son buenas. Los patrones más apropiados para el caso son Composite o Cadena de Responsabilidad, no Strategy como se sugiere. La implementación parece que escogió Chain of Responsibility más que Strategy.

**Análisis del estudiante:** No identifica correctamente los patrones más adecuados para el problema planteado.

**Nota:** 2.0/3

---

### P05 — Arquitectura de Microservicios — 3.3/3

**Prompt:** Buen prompt con ejemplos concretos.

**Respuesta del LLM:** Buena respuesta que aborda los conceptos de microservicios.

**Análisis del estudiante:** Se evidencia mejora en los commits. El tema de idempotencia está bien abordado. Evidencia de uso de IA en el análisis.

**Nota:** 3.3/3

---

### P06 — Persistencia y Base de Datos (⭐) — 1.3/3

**Prompt:** Aceptable.

**Respuesta del LLM:** Respuesta truncada — no se alcanza a ver el análisis completo del estudiante. El código generado usa IA (habla de ports y es clean architecture).

**Análisis del estudiante:** Truncado. El código evidencia uso de IA con terminología de Clean Architecture (ports).

**Nota:** 1.3/3

---

### P07 — Seguridad y Autenticación — 3.5/3

**Prompt:** Buena estructura.

**Respuesta del LLM:** Respuesta completa que aborda los conceptos de seguridad.

**Análisis del estudiante:** Buen análisis que cubre los puntos solicitados.

**Nota:** 3.5/3

---

### P08 — API REST y Documentación — 4.0/4

**Prompt:** Bien estructurado.

**Respuesta del LLM:** La pregunta solicita código y análisis, y ambos están presentes y bien desarrollados.

**Análisis del estudiante:** Cumple con lo solicitado — código funcional y análisis pertinente.

**Nota:** 4.0/4

---

### P09 — TDD Avanzado (⭐) — 4.0/4

**Prompt:** Bien estructurado.

**Respuesta del LLM:** Buena implementación de TDD.

**Análisis del estudiante:** Se observa la evolución de la prueba a lo largo del desarrollo, lo cual es evidencia de un proceso TDD bien documentado. El análisis muestra comprensión del ciclo Red-Green-Refactor.

**Nota:** 4.0/4

---

### P10 — Clean Architecture — 5.0/5

**Prompt:** Excelente.

**Respuesta del LLM:** Logra mostrar arquitectura limpia con capas bien definidas.

**Análisis del estudiante:** Tablas muy completas que mapean la arquitectura. Demuestra comprensión profunda de los principios de Clean Architecture (Entities, Use Cases, Interface Adapters, Frameworks & Drivers).

**Nota:** 5.0/5

---

### P11 — Refactoring y Principios SOLID (⭐) — 3.7/4

**Prompt:** Muy buen prompt, excelente estructura.

**Respuesta del LLM:** Excelente respuesta del modelo con refactorización completa.

**Análisis del estudiante:** Buen análisis que identifica los principios SOLID aplicados y las mejoras en el código refactorizado.

**Nota:** 3.7/4

---

### P12 — Ciclo de Vida de Defectos (⭐) — 2.6/3

**Prompt:** Cubre 7 áreas: estados del ciclo, severidad vs prioridad, ejemplo completo en OpenLib Market, métricas, integración con Jira/GitHub.

**Respuesta del LLM:** Muy completa: tripartición error/fallo/defecto, diagrama ASCII, matriz de responsabilidades, matriz severidad×prioridad con 5 ejemplos, walkthrough completo con timestamps (7.5h), 6 métricas, integración Jira/GitHub.

**Análisis del estudiante:** Identifica omisiones (DIFERIDO, bug triage) y agrega distinción producción vs staging. Sin embargo, el análisis tiene evidencia clara de uso de IA (se nota). Usa estados genéricos en vez de los estados Jira del curso (ABIERTO→EN REVISION→ASIGNADO→EN PROGRESO→EN VERIFICACION→RESUELTO→CERRADO).

**Nota:** 2.6/3

---

### P13 — Integración y Refactoring — 2.0/3

**Prompt:** Proporciona código problemático `NotificadorPedido` con 4 sistemas externos acoplados. Pide diagnóstico, refactoring Observer/Event-Driven, código completo y testability.

**Respuesta del LLM:** Muy completa: 7 problemas identificados, refactoring con `ApplicationEventPublisher`, `PedidoCreadoEvent`, 4 interfaces, 4 listeners, tests con Mockito.

**Análisis del estudiante:** Identifica 3 problemas fundamentales (token hardcodeado, acoplamiento, testability). Sin embargo, menciona `@TransactionalEventListener` que no se vio en clase — esto es generado por IA.

**Nota:** 2.0/3

---

## Observaciones generales

### Fortalezas
- **Prompts bien estructurados:** Los prompts están bien organizados y proporcionan contexto suficiente para el LLM.
- **Código funcional:** Las respuestas de código (P01, P05, P08, P09, P10) reflejan la calidad de las entradas.
- **Evolución del proceso:** Se nota mejora en los commits y evolución en las pruebas TDD (P09).

### Áreas de mejora
- **Análisis manual:** El análisis muestra evidencia de uso de IA en varias respuestas (P03, P06, P12, P13). Para futuros ejercicios, es importante que el análisis sea genuinamente del estudiante.
- **Comprensión de patrones:** En P04, los patrones identificados no son los más adecuados para el problema (Composite/CoR vs Strategy).
- **Estados Jira:** En P12, se usan estados genéricos en vez de los estados específicos del curso.

---

**Nota final: 37.4 / 53 — 70.6%**
