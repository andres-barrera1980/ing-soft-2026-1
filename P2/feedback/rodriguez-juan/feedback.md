# Parcial 2 — Juan David Rodriguez Franco

## Información General

| Campo | Detalle |
|---|---|
| **Estudiante** | Juan David Rodriguez Franco |
| **Rama** | `parcial2/rodriguez-juan` |
| **LLM principal** | Gemini 3.1 Pro |
| **Respondió SIN IA** | P01, P03, P06 |

## Resultados por Pregunta

| # | Pregunta | Máx | Nota | Observación |
|---|---|---|---|---|
| P01 | SOLID — Principio GestorLibro | 3.0 | **3.0** | SIN IA. Identifica SRP y OCP correctamente. Sin código de ejemplo ni casos concretos, pero análisis correcto. ⭐ +20% |
| P02 | SOLID — Refactoring ProcesadorPago | 4.0 | **4.0** | Prompt excelente con contexto completo. Análisis muy fuerte: identifica switch residual en CheckoutUseCase que el LLM dejó, propone PagoFactory con registro dinámico. |
| P03 | SOLID — Jerarquía de usuarios | 3.0 | **1.8** | SIN IA. Identifica ISP y SRP, menciona UnsupportedOperationException. Sin referencia a LSP, sin código. Análisis breve. ⭐ +20% |
| P04 | Patrones — Estrategias de descuento | 3.0 | **3.0** | Prompt excelente pidiendo descartar ≥2 alternativas. Análisis detecta que el LLM asumió descuentos compuestos vs aditivos. Correcta selección de Strategy. |
| P05 | Patrones — Notificaciones | 5.0 | **4.5** | Observer + Adapter con diagrama Mermaid. Análisis identifica cascada de fallo síncrono (si SMTP falla, Redis y Auditoría no se ejecutan). Propone ExecutorService. |
| P06 | Clean Architecture — Capas | 4.0 | **1.8** | SIN IA. Respuesta muy superficial: solo 4 bullets sin profundidad, sin ejemplos, sin código. ⭐ +20% |
| P07 | Clean Architecture — Pagos | 4.0 | **3.8** | Prompt sólido pidiendo capas + Mermaid + DIP. Análisis excelente con tabla comparativa de testeabilidad. Identifica que faltó pedir código del Use Case. |
| P08 | Pruebas — CarritoService | 5.0 | **5.0** | Prompt excelente listando 8 casos concretos. LLM generó suite completa. Análisis identifica 2 casos faltantes (carrito vacío + stock insuficiente) y provee código correctivo. |
| P09 | TDD — ControlInventario | 4.0 | **4.0** | Prompt que exige ciclo Red→Green→Refactor. LLM demostró TDD correctamente. Análisis crítico: las validaciones en Refactor debieron partir de nuevas pruebas RED. |
| P10 | VMs vs Contenedores | 5.0 | **4.5** | Prompt directo acotando 5 aspectos. Análisis identifica omisión crítica: seguridad del kernel compartido y complejidad operacional de orquestación. |
| P11 | Pruebas manuales vs auto | 4.0 | **3.5** | Prompt estructurado con escenarios concretos. Análisis nota que falta discusión sobre ROI y costo de mantenimiento de automatización. |
| P12 | Ciclo de vida defecto | 3.0 | **3.0** | Reporte de defecto profesional con pasos detallados (Charles Proxy para simular timeout). Análisis identifica que faltan estados BLOCKED/Rejected. |
| P13 | Refactoring Strategy + tests | 3.0 | **3.0** | Antes/después completo con código y pruebas de regresión. Análisis señala "class explosion" como trade-off no mencionado por el LLM. |
| **TOTAL** | | **50.0** | **44.9** | **89.8%** |

## Retroalimentación General

### Fortalezas

- **Prompting de alta calidad:** Los prompts son consistentemente específicos, con contexto del proyecto OpenLib Market, restricciones técnicas claras (Java, JUnit 5, Mockito) y formato de salida esperado. Destacan P04 (pedir descartar alternativas), P08 (8 casos de prueba listados) y P13 (exigir before/after).
- **Análisis crítico genuino:** A diferencia de muchos compañeros, Juan no se limita a resumir la respuesta del LLM. En P02 detecta un switch residual que el LLM dejó, en P05 identifica la cascada de fallo síncrono, en P08 completa la suite con 2 tests faltantes, y en P10 señala omisiones de seguridad. Esto demuestra comprensión real de los conceptos.
- **Propuestas con código:** Cuando identifica una omisión del LLM, no solo la menciona — escribe el código correctivo (P02: PagoFactory, P08: tests faltantes).
- **Uso de diagramas:** Incluye diagramas Mermaid donde aportan valor (P05, P07).

### Áreas de Mejora

- **Profundidad en respuestas SIN IA (P06):** La pregunta de Clean Architecture es respondida con solo 4 bullets genéricos. Para ser una ⭐, esperaría al menos un diagrama simple y ejemplos concretos del proyecto.
- **Jerarquía de principios SOLID (P03):** Responde SIN IA pero omite LSP y no provee ejemplos de código. Identificar LSP en una jerarquía de usuarios es precisamente el núcleo de la pregunta.
- **README.md en blanco:** El archivo de metadatos del estudiante no fue diligenciado (nombre, fecha, LLM, justificación).

### Recomendaciones

- Invertir más tiempo en las preguntas ⭐ SIN IA, donde se espera demostrar dominio conceptual sin apoyo del LLM.
- Incluir ejemplos de código incluso en respuestas SIN IA para sustentar el análisis.
- Diligenciar siempre el README.md con la metadata del estudiante.
- El nivel general de prompting y análisis crítico es excelente (89.8%) — mantener este nivel de exigencia en el proyecto final.

## Nota Final

**44.9 / 50.0 = 89.8%**
