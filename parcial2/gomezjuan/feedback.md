# Feedback — Parcial 2
## Ingeniería de Software 2026-1

### Estudiante: Juan Camilo Gómez

**Calificación total: 43.7 / 50 puntos (87.4%)**

---

## Resumen

Prompts consistentemente buenos. Los análisis identifican problemas clave pero rara vez profundizan en trade-offs o conexiones con el curso. Las preguntas Sin IA (P03, P06, P09, P11) demuestran buen dominio del material. Penalización de integridad en P12 (-2.0) por afirmar "Sin IA" mientras admite uso de IA. Penalizaciones por commits pendientes de discusión (P07, P08, P10, P13).

---

## Detalle por pregunta

| # | Tema | Pts | Nota | % |
|---|---|:---:|:---:|---:|
| 1 | P.1 SOLID — Principio en GestorLibro ⭐ | 3 | 2.50 | 83% |
| 2 | P.2 SOLID — Refactoring ProcesadorPago | 4 | 4.00 | 100% |
| 3 | P.3 SOLID — Jerarquía de usuarios ⭐ | 3 | 3.30 (+20% sin IA incluido) | 110% |
| 4 | P.4 Patrones — Estrategias de descuento | 3 | 2.20 | 73% |
| 5 | P.5 Patrones — Notificaciones combinadas | 5 | 4.50 | 90% |
| 6 | P.6 Clean Architecture — Capas ⭐ | 4 | 4.00 (+20% sin IA, capped) | 100% |
| 7 | P.7 Clean Architecture — Pagos | 4 | 3.30 | 83% |
| 8 | P.8 Pruebas — CarritoService | 5 | 4.2 | 84% |
| 9 | P.9 TDD — ControlInventario ⭐ | 4 | 4.00 (+20% sin IA, capped) | 100% |
| 10 | P.10 VM vs Containers | 5 | 4.2 | 84% |
| 11 | P.11 Pruebas manuales vs automatizadas ⭐ | 4 | 4.0 (+20% sin IA) | 100% |
| 12 | P.12 Ciclo de vida de defectos ⭐ | 3 | 1.0 (-2.0 integridad) | 33% |
| 13 | P.13 Integración — Refactoring Notificador | 3 | 2.5 | 83% |

---

## Desglose por pregunta

### P01 — SRP en GestorLibro (2.50/3.00) ⭐ Usó Claude

**Prompt (82%):** Buen prompt con código completo, contexto de OpenLib Market, restricción Java 21, tareas claras. Falta: no pide explícitamente identificar el principio violado (lo deja al LLM), no solicita formato de salida específico.

**Análisis:** No explica claramente la violación de SRP ni cómo la implementación refactorizada subsana el problema. Tampoco identifica otros principios SOLID aplicados en la solución (OCP, DIP). El insight transaccional es válido pero insuficiente para compensar la falta de claridad en la explicación del principio.

**Nota final:** 2.50 (ajuste del profesor)

---

### P02 — Refactoring ProcesadorPago (4.00/4.00) Usó Claude

**Prompt (85%):** Excelente. Incluye código completo, pide mínimo 2 principios SOLID con argumentos concretos, solicita justificación del patrón, y exige demostración con criptomonedas para probar OCP. Java 21 + interfaces.

**Análisis:** El estudiante trabajó genuinamente el análisis: identifica que el LLM no resolvió la selección de estrategia y añade código de un registro de estrategias con `Map<String, ProcesadorPago>`. Esto demuestra comprensión más allá de la respuesta del LLM. Aunque repite el claim de SRP sin cuestionar si "procesar pago" es realmente una sola responsabilidad, el trabajo general es sólido y muestra esfuerzo propio.

**Nota final:** 4.00 (ajuste del profesor — el estudiante trabajó la explicación)

---

### P03 — Jerarquía de usuarios: ISP & LSP (3.30/3.00) ⭐ Sin IA (+20% incluido)

**Análisis:** Identifica correctamente ISP (interfaz gruesa fuerza `UnsupportedOperationException`) y LSP (Vendedor sobreescribe `comprar` con excepción). La explicación de ISP es buena — referencia que los clientes no deben depender de interfaces que no usan. Rediseño con interfaces separadas (`Comprador`, `Vendedor`, `Moderador`, `Administrador`) es correcto. `UsuarioVendedor implements Comprador, Vendedor` demuestra comprensión de interfaces múltiples. Sin embargo: la explicación de LSP es breve — no explora escenarios de sustitución ni por qué lanzar excepciones en overrides rompe el polimorfismo.

**Nota final:** 3.30 (ajuste del profesor, bono sin IA incluido)

---

### P04 — Estrategias de descuento (2.20/3.00) Usó Claude

**Prompt (88%):** Muy bueno. Define tipos de descuento con porcentajes, establece explícitamente el requisito de combinación ("cliente fiel en semana del libro = 10% + 15%"), lista patrones vistos en clase, exige comparación con ≥2 alternativas. Java 21 + formato markdown.

**Análisis:** Insight clave: el LLM aplica todos los descuentos sobre el precio base (aditivos), no encadenados — y el estudiante nota que esto hace diferencia. También identifica que faltan topes de descuento. Sin embargo, el LLM afirma usar "Strategy+Composite" cuando solo implementa una lista de estrategias — Composite no aplica aquí. El estudiante no cuestiona esta afirmación incorrecta del patrón.

**Nota final:** 2.20 (ajuste del profesor — Composite no aplica, análisis no cuestiona el LLM)

---

### P05 — Notificaciones con 2 patrones (4.50/5.00) Usó Claude

**Prompt (88%):** Buen prompt con escenario específico (libro agotado vuelve a estar disponible), 4 canales de notificación, pide 2 patrones de la lista del curso, diagrama Mermaid, manejo de errores, registro dinámico. 5 requisitos claros.

**Análisis:** Dos patrones bien combinados (Observer + Strategy) y buen análisis del estudiante. Identifica elementos faltantes: estrategia de reintentos con backoff exponencial, y logging estructurado del resultado de cada observer. El análisis demuestra comprensión de la interacción entre los patrones y cómo mejorar la solución propuesta por el LLM.

**Nota final:** 4.50 (ajuste del profesor — buena respuesta, patrones bien combinados)

---

### P06 — Clean Architecture: Capas (4.00/4.00) ⭐ Sin IA (+20%, capped)

**Análisis (82%):** Explica las 4 capas con ejemplos concretos de OpenLib Market (`Libro`, `PublicarLibroUseCase`, `LibroController`, Spring Boot). La explicación de la regla de dependencia es clara y correcta — usa `LibroRepositorioPostgres` dependiendo de `RepositorioLibro` como ejemplo. DIP en límites de capas está bien explicado. Tabla comparativa con arquitectura tradicional es un buen toque (cubre: dirección de dependencias, rol de BD, testeabilidad, reemplazabilidad de frameworks). El estilo de escritura es menos pulido que la salida típica de LLM — parece genuino.

**Fórmula:** 4 × (0.30 × 1.00 + 0.70 × 0.82) = 3.50 → +20% = 4.19 → capped 4.00

---

### P07 — Módulo de pagos con Clean Architecture (3.30/4.00) Usó Claude

**Prompt (88%):** Muy detallado. Especifica múltiples métodos de pago, PostgreSQL, REST API, diagrama Mermaid, 5 tareas específicas (capas, diagrama, código, regla de dependencia, mecanismo DI).

**Análisis (80%):** Valor agregado clave: insight de testeabilidad con ejemplo concreto de prueba mock mostrando que `ProcesarPagoUseCase` puede probarse con mocks de `RepositorioPago` y `PasarelaPago`. Esto no sería posible con el `PaymentService` monolítico original. También identifica falta de sección de configuración DI.

**Fórmula:** 4 × (0.30 × 0.88 + 0.70 × 0.80) = 3.30

---

### P08 — Pruebas unitarias CarritoService (4.2/5.0) Usó Claude

**Prompt (92%):** Excelente. Incluye código completo, reglas de negocio explícitas (mín 1, máx 10 ítems únicos, distinción `cantidadItems` vs `cantidadItemsUnicos`), 4 categorías de casos de prueba, convenciones de nombres en español, frameworks (JUnit 5 + Mockito), un @Test por caso. Uno de los mejores prompts del examen.

**Análisis (80%):** Identifica dos casos de prueba faltantes y los escribe: (1) checkout falla después de `vaciar()` — valida reset de estado, (2) acumular con carrito lleno preserva 10 ítems únicos pero permite incrementar existentes — distinción sutil entre agregar nuevo vs. incrementar. Ambos son casos borde relevantes que demuestran intuición de testing y comprensión de reglas de negocio.

**Fórmula:** 5 × (0.30 × 0.92 + 0.70 × 0.80) = 4.18 → redondeado 4.2

---

### P09 — TDD para ControlInventario (4.00/4.00) ⭐ Sin IA (+20%, capped)

**Análisis (85%):** Muestra el ciclo completo Red-Green-Refactor con 3 pruebas (stock→0 marca AGOTADO, stock→0 notifica al vendedor, reposición→DISPONIBLE). Fase Green con implementación mínima. El refactoring es el punto destacado: mueve la lógica de transición de estado del servicio a `Libro.setStock()` — una mejora de diseño significativa que demuestra SRP en la entidad. Método `recienAgotado()` muestra diseño de API reflexivo. Las pruebas siguen pasando después del refactoring.

**Fórmula:** 4 × (0.30 × 1.00 + 0.70 × 0.85) = 3.58 → +20% = 4.30 → capped 4.00

---

### P10 — VMs vs Contenedores (4.2/5.0) Usó Claude

**Prompt (90%):** Muy detallado con 6 aspectos específicos (arquitectura, ventajas de VMs, ventajas de contenedores, casos de uso, pitfalls, seguridad). Exige explícitamente discusión de seguridad del kernel, herramientas específicas, formato tabla + secciones profundas.

**Análisis (80%):** Añade la distinción filosófica: "las VMs virtualizan hardware, los contenedores virtualizan el SO" — una abstracción perspicaz. Recomienda arquitectura híbrida para OpenLib Market (Kubernetes sobre VMs EC2). Es estándar de la industria y muestra comprensión práctica.

**Fórmula:** 5 × (0.30 × 0.90 + 0.70 × 0.80) = 4.15 → redondeado 4.2

---

### P11 — Pruebas manuales vs automatizadas (3.96/4.00) ⭐ Sin IA (+20%)

**Análisis (75%):** Cubre ventajas/desventajas de ambos enfoques con puntos prácticos. La sección de criterios de decisión es útil (automatizar cuando: frecuente, estable, repetible, difícil de hacer manualmente). Recomendaciones específicas para OpenLib Market (automatizar: pruebas unitarias de dominio, integración API, regresión de flujo crítico; manual: onboarding de vendedores, UI JavaFX, aceptación con usuarios). Menos estructurado que la salida típica de LLM — parece genuino.

**Fórmula:** 4 × (0.30 × 1.00 + 0.70 × 0.75) = 3.30 → +20% = 3.96

---

### P12 — Ciclo de vida de defectos (1.0/3.0) ⭐ Sin IA (-2.0 integridad)

**Análisis:** Describe 7 fases del ciclo de vida con un escenario de bug específico (botón no responde con >5 ítems). Cada fase tiene: rol responsable, acción, herramienta, estado en Jira. El análisis de causa raíz es reflexivo (3 posibles causas). Severidad/prioridad correcta. El trabajo es genuinamente Sin IA.

**⚠️ Penalización de integridad: -2.0** — El estudiante declara "Sin IA" pero admite "me ayudé bastante de IA" en el mismo campo. La bonificación está anulada a pesar de que el trabajo parece genuino.

**Fórmula:** 3 × (0.30 × 1.00 + 0.70 × 0.80) = 2.58 → redondeado 3.0 → -2.0 = 1.0

---

### P13 — Refactoring Notificador (2.5/3.0) Usó Claude

**Prompt (90%):** Excelente. Incluye código completo, 6 tareas específicas (identificar violaciones SOLID, aplicar ≥1 principio, aplicar ≥1 patrón con justificación, separar logging, probar extensibilidad, incluir pruebas unitarias). Bien estructurado con restricciones claras.

**Análisis (82%):** Dos mejoras de producción significativas: (1) las pruebas deben hacer mock de la interfaz `CanalNotificacion`, no de la clase concreta `CanalEmail` — una práctica de testing sutil pero importante; (2) el logger no debe fallar silenciosamente — debería lanzar `RuntimeException` para fallos críticos de auditoría. Ambas muestran madurez de producción.

**Fórmula:** 3 × (0.30 × 0.90 + 0.70 × 0.82) = 2.53 → redondeado 2.5

---

## Resumen de bonificaciones y penalizaciones

| Tipo | Detalle | Impacto |
|---|---|---|
| +20% Sin IA ⭐ | P03 (2.41→3.30) | +0.89 |
| +20% Sin IA ⭐ | P06 (3.50→4.00, capped) | +0.50 |
| +20% Sin IA ⭐ | P09 (3.58→4.00, capped) | +0.42 |
| +20% Sin IA ⭐ | P11 (3.30→4.0) | +0.7 |
| -2.0 integridad | P12 (falsa declaración "Sin IA", trabajo parece genuino) | -2.0 |

**Impacto neto:** +2.5 bonos - 2.0 penalizaciones = **+0.5**

---

## Retroalimentación para Juan Camilo

### Lo que hiciste bien:
- Tus prompts están entre los mejores del curso — bien estructurados, específicos, con restricciones y ejemplos claros
- Tu trabajo Sin IA (P06, P09) demuestra comprensión genuina de Clean Architecture y TDD
- Consistentemente agregas valor más allá de la respuesta del LLM (gaps transaccionales, patrones de registro, descuentos aditivos vs encadenados, mejoras de pruebas con mocks)
- El refactoring TDD (P09) moviendo la lógica de estado a la entidad es una decisión de diseño genuinamente buena

### Áreas de mejora:
1. **Análisis más profundo:** Identificas problemas pero frecuentemente te detienes en el "qué" sin explorar el "por qué". Por ejemplo, en P05 mencionas estrategia de reintentos pero no discutes qué tipo (backoff exponencial? circuit breaker?) ni por qué importa para la confiabilidad de notificaciones.
2. **Conexiones con el curso:** Tus análisis rara vez referencian materiales específicos del curso, presentaciones o talleres. Conecta tus observaciones con lo que estudiamos (ej: "esto se relaciona con el patrón Observer que discutimos en la semana 10, pero la implementación del LLM difiere de la estructura clásica del GoF porque...").
3. **Disciplina de commits:** Cuando usas un LLM, DEBES hacer 2 commits separados: uno para el prompt+respuesta, otro para tu análisis. Esto no es opcional — es cómo verificamos tu proceso.
4. **Integridad:** La admisión en P12 de usar IA mientras afirmas "Sin IA" te costó 2 puntos. Si usas IA, sé honesto al respecto — el examen recompensa la transparencia, no la deshonestidad.
5. **Cuestionar elecciones de patrones:** Cuando el LLM elige un patrón de diseño, no lo aceptes sin más. Pregunta: ¿era la mejor opción? ¿Qué alternativas existen? ¿Cuáles son los trade-offs? Aquí es donde los análisis de mayor puntaje se diferencian.

---

*Generado el 12 de junio de 2026. Nota preliminar sujeta a revisión del profesor.*
