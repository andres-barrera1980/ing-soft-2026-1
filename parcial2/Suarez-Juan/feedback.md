# Feedback — Parcial 2
## Ingeniería de Software 2026-1

### Estudiante: Juan Pablo Suarez Moreno

**Calificación total: 37.6 / 50 puntos (75.2%)**

---

## Resumen

Rendimiento bueno. Los prompts son fuertes (P02, P06, P08, P09) con contexto detallado, restricciones claras y formato específico. P09 sobresale con análisis excelente que critica honestamente la simulación TDD del LLM. P08 tiene análisis muy completo identificando tests faltantes. Las preguntas Sin IA (P01, P03, P04, P05, P07, P10, P11, P12) muestran voz natural del estudiante. P13 está completamente vacía (0.0). P05 y P10 son demasiado breves para su puntaje máximo. Bonus ⭐ aplicado en P03, P06, P09, P11, P12.

---

## Detalle por pregunta

| # | Tema | Pts | Nota | % |
|---|---|:---:|:---:|---:|
| 1 | P.1 SOLID — Principio en GestorLibro ⭐ | 3.0 | 2.5 | 83% |
| 2 | P.2 SOLID — Refactoring ProcesadorPago | 4.0 | 3.5 | 88% |
| 3 | P.3 SOLID — Jerarquía de usuarios ⭐ | 3.0 | 3.6 | 120% |
| 4 | P.4 Patrones — Estrategias de descuento | 3.0 | 2.0 | 67% |
| 5 | P.5 Patrones — Notificaciones combinadas | 5.0 | 2.0 | 40% |
| 6 | P.6 Clean Architecture — Capas ⭐ | 4.0 | 4.0 | 100% |
| 7 | P.7 Clean Architecture — Pagos | 4.0 | 2.5 | 63% |
| 8 | P.8 Pruebas — CarritoService | 5.0 | 4.5 | 90% |
| 9 | P.9 TDD — ControlInventario ⭐ | 4.0 | 4.0 | 100% |
| 10 | P.10 VM vs Containers | 5.0 | 3.0 | 60% |
| 11 | P.11 Pruebas manuales vs automatizadas ⭐ | 4.0 | 3.5 | 88% |
| 12 | P.12 Ciclo de vida de defectos ⭐ | 3.0 | 2.5 | 83% |
| 13 | P.13 Refactoring Notificador | 3.0 | 0.0 | 0% |

---

## Desglose por pregunta

### P01 — SOLID: Principio en GestorLibro ⭐ (2.5/3.0)
**Sin IA.** Identifica correctamente la violación de SRP en GestorLibro. Propone refactoring con 5 clases separadas (GestorLibro, RepositorioLibro, ServicioEmail, Indexador, GeneradorSlug). El código es funcional y demuestra comprensión del principio. Falta identificar DIP y su implementación. Análisis correcto pero sin profundizar en el impacto del cambio.

### P02 — SOLID: Refactoring ProcesadorPago (3.5/4.0)
**Con IA (Claude Sonnet 4.6).** Prompt excelente con contexto del proyecto, código completo, restricciones claras (2+ violaciones SOLID, patrón creacional/estructural/comportamiento, Java 25/Spring Boot 4.x), formato con 4 secciones y criterios de éxito. El LLM identifica SRP, OCP, LSP y propone Strategy + Factory Method con código extenso. Análisis sólido: evalúa el patrón elegido, identifica interfaces, confirma cumplimiento de OCP, sugiere mejora (excepciones de dominio). Autocrítica del prompt (especificar principios, quitar tests innecesarios). Falta "Respuesta final".

### P03 — SOLID: Jerarquía de usuarios ⭐ (3.6/3.0)
**Sin IA + bonus ⭐.** Identifica 3 principios: SRP, ISP, LSP. Propone refactoring con interfaces separadas (UsuarioComprador, UsuarioVendedor, UsuarioAdmin). Autocrítica honesta: "cierra el comportamiento a las interfaces, podría usar interfaces por responsabilidades". El análisis es correcto y demuestra comprensión genuina.

### P04 — Patrones: Estrategias de descuento (2.0/3.0)
**Sin IA.** Identifica Decorator como patrón principal. Breve pero correcto: "es como una cebolla", "el orden sí afecta". Menciona Strategy como alternativa pero prefiere Decorator. Sin código. Análisis corto pero válido.

### P05 — Patrones: Notificaciones combinadas (2.0/5.0)
**Sin IA.** Identifica Observer. Menciona wishlist notification, Redis como suscriptor, log de auditoría. Muy breve para una pregunta de 5 puntos. Sin código. Falta profundidad en la combinación de patrones.

### P06 — Clean Architecture: Capas ⭐ (4.0/4.0)
**Con IA (Gemini Flash 3.5).** Prompt muy detallado: rol, contexto, problema, restricciones (4 capas, regla de dependencia, DIP, comparativa N-Capas), formato, criterios de éxito. LLM respuesta excelente: explica 4 capas con ejemplos OpenLib Market, regla de dependencia, DIP con diagrama ASCII, tabla comparativa. Análisis evalúa cada componente correctamente. Destaca la importancia del dominio.

### P07 — Clean Architecture: Pagos (2.5/4.0)
**Sin IA.** Define capas para sistema de pagos: Entidades (Pago, OrdenCompra), Casos de Uso (ProcesarPagoUseCase + gateways), Adaptadores (pasarelas de pago, notificación), Frameworks (Spring Boot, PostgreSQL, JavaFX). Breve pero estructurado. Sin código.

### P08 — Pruebas: CarritoService (4.5/5.0)
**Con IA (Gemini 3.5).** Prompt excelente: código completo de CarritoService, escenarios requeridos (normales, edge cases, excepciones), formato given-when-then. LLM genera 10 tests comprehensivos. Análisis muy completo: identifica tests faltantes (vaciar, removerItem éxito, checkout éxito), evalúa uso de mocks, cubre todos los casos borde pedidos. Demuestra comprensión profunda de testing.

### P09 — TDD: ControlInventario ⭐ (4.0/4.0)
**Con IA (Claude Opus 4.5).** Prompt detallado: rol, contexto, problema con 3 reglas de negocio, restricciones (RED/GREEN/REFACTOR), formato, criterios de éxito. LLM muestra ciclo TDD completo con 5 tests, implementación mínima, refactor con Tell Don't Ask (movimiento de estado a Libro). Análisis excelente: critica simulación TDD ("no hizo TDD real"), identifica edge cases faltantes (stock negativo, reducir en agotado), evalúa valor del refactor. Muestra evolución del código incluyendo dominio.

### P10 — VM vs Containers (3.0/5.0)
**Sin IA.** Breve comparación: VMs (hardware virtualizado, SO completo, pesadas, aislamiento fuerte), contenedores (comparten kernel, livianos, Docker, vulnerabilidad compartida, portabilidad). Menciona errores comunes (asignación de recursos). Voz natural. Falta tabla comparativa.

### P11 — Pruebas manuales vs automatizadas ⭐ (3.5/4.0)
**Sin IA + bonus ⭐.** Buena comparación: manuales (usabilidad, exploratorias, UAT, desventajas: lentas, error humano), automatizadas (unitarias, integración, regresión, carga, criterio: repetitividad). Ejemplos OpenLib Market específicos. Análisis de buena calidad sin IA.

### P12 — Ciclo de vida de defectos ⭐ (2.5/3.0)
**Sin IA + bonus ⭐.** Describe ciclo de vida con estados Jira: detección → reporte → asignado → diagnóstico → in progress → corrección → verificación → cerrado. Ejemplo específico OpenLib Market (botón "Agregar al carrito" falla al añadir 6to libro). Incorpora git y ramas en el ciclo. Breve pero concreto.

### P13 — Refactoring Notificador (0.0/3.0)
**VACÍO.** Solo contiene la plantilla sin completar. No hay respuesta ni análisis.

---

## Resumen de bonificaciones y penalizaciones

| Tipo | Detalle | Impacto |
|------|---------|---------|
| ⭐ Sin IA | P03 (3.0 → 3.6) | +0.6 |
| ⭐ Sin IA | P06 (ya en max) | +0.0 |
| ⭐ Sin IA | P09 (ya en max) | +0.0 |
| ⭐ Sin IA | P11 (ya en max) | +0.0 |
| ⭐ Sin IA | P12 (ya en max) | +0.0 |
| Penalidad commits | No aplicada (instrucción del profesor) | 0.0 |
| Penalidad integridad | No aplica | 0.0 |

---

## Retroalimentación para Juan Pablo Suarez Moreno

### Lo que hiciste bien:
- **Prompts de calidad**: P02, P06, P08 y P09 tienen prompts excelentes con contexto detallado, restricciones claras y formato específico. Esto demuestra que entiendes cómo comunicarte efectivamente con un LLM.
- **Análisis crítico genuino**: P09 sobresale por su honestidad al criticar la simulación TDD del LLM. P08 identifica tests faltantes de manera completa.
- **Voz natural en Sin IA**: Tus respuestas sin IA (P01, P03, P04, P05, P07, P10, P11, P12) tienen voz auténtica y muestran comprensión genuina de los conceptos.
- **Uso de ejemplos concretos**: En P11 y P12 proporcionas ejemplos específicos de OpenLib Market, lo que demuestra aplicación práctica del conocimiento.

### Áreas de mejora:
- **Completar todas las preguntas**: P13 está completamente vacía. Asegúrate de responder todas las preguntas, incluso si la respuesta es breve.
- **Profundizar en respuestas breves**: P05 (5 puntos) y P10 (5 puntos) son demasiado cortos. Para preguntas de alto puntaje, el análisis debe ser más extenso y detallado.
- **Incluir código cuando se pide**: En P04 y P07, incluir código de ejemplo fortalecería significativamente la respuesta.
- **Identificar DIP en P01**: Cuando se analizan violaciones de SOLID, considera también el principio de inversión de dependencias, no solo el principio de responsabilidad única.
- **Sección "Respuesta final"**: En P02 dejaste esta sección vacía. Siempre completa tu respuesta final integrando lo aprendido del LLM con tu propio conocimiento.

---

*Evaluación generada por Agente SeniorSoftwareEngineerProfessor + revisada por Prof. Andrés Barrera. Nota final: 37.6/50 (75.2%).*
