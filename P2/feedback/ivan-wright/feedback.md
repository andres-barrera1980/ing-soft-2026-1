# Feedback — Parcial 2: Prompting

**Estudiante:** Ivan Wright
**LLM utilizado:** Gemini 3.1 Pro (preguntas con IA); Sin IA en P03, P06. P01 declarada Sin IA pero el código de refactoring es generado por IA.
**Fecha de revisión:** 2026-06-11

---

## Resumen ejecutivo

**Nota final: 39.4 / 50 (78.8%)**

Buen desempeño general. Los prompts están bien estructurados siguiendo el patrón CONTEXTO → PROBLEMA → RESTRICCIONES → FORMATO → VALIDACIÓN, lo cual garantiza respuestas de calidad del LLM. El análisis es sólido en la mayoría de preguntas: el estudiante identifica omisiones del LLM y propone mejoras concretas. Las tres preguntas declaradas "Sin IA" (P01, P03, P06) fueron penalizadas por evidencia de uso de LLM. Las áreas de mejora están en la profundidad del análisis en algunas preguntas teóricas (P10, P12) y en la evaluación más crítica de las respuestas del LLM cuando comete errores conceptuales.

---

## Evaluación por pregunta

### P01 — SOLID — Principio violado en GestorLibro (⭐) — 1.0/3 ⚠️ Penalización integridad

**Análisis del estudiante (genuino):** El análisis textual es trabajo propio del estudiante. Identifica correctamente las violaciones SOLID línea por línea: SRP (validación, slug, logging, indexación), DIP (instanciación directa de PostgreSQL, EmailService, SearchIndex) y OCP (EmailService, SearchIndex). Las explicaciones del *por qué* cada línea viola un principio son claras y demuestran comprensión.

**Código de refactoring (generado por IA):** El código de refactoring tiene el formato pulido y estructurado típico de un LLM: interfaces limpias, inyección de dependencias por constructor, entidad con métodos de dominio. El estudiante declaró "Sin IA" pero el código no es trabajo propio.

**Penalización:** −2.0 pts por declarar "Sin IA" cuando hay evidencia de uso de LLM en el código de refactoring.

**Nota:** 3.0 (contenido) − 2.0 (penalización integridad) = 1.0/3

---

### P02 — SOLID — Refactoring ProcesadorPago — 3.5/4

**Prompt:** Bueno. Proporciona contexto de OpenLib Market con Java 21 y Spring Boot, pide identificar al menos dos principios SOLID y refactoring con patrón de diseño. El formato markdown y las restricciones técnicas están bien definidos.

**Respuesta del LLM:** Correcta. Identifica SRP y OCP, propone Strategy pattern con Spring Boot. La implementación con `List<EstrategiaPago>` inyectada y selección por stream es elegante.

**Análisis del estudiante:** Bueno. Identifica que el LLM no generó pruebas ni manejó excepciones de dominio. Propone `MetodoPagoNoSoportadoException` personalizada, lo cual mejora la solución. Menciona que Strategy facilita testing con Mockito. La respuesta y justificación del estudiante son correctas.

**Observación de calificación:** El LLM identifica SRP como violado. Como se discutió en clase, las variantes de pago son implementaciones de la *misma* responsabilidad ("procesar pago"), por lo que la violación principal es OCP. El estudiante acepta el análisis del LLM sin cuestionar esta distinción, pero su análisis adicional sobre testing y excepciones es válido.

**Nota:** 3.5/4

---

### P03 — SOLID — Jerarquía de usuarios (⭐ Sin IA) — 1.0/3 ⚠️ Penalización integridad

**Análisis del estudiante:** Identifica ISP (interfaz `Usuario` con 5 métodos que obliga a `Comprador` a lanzar `UnsupportedOperationException`) y LSP (`Vendedor extends Comprador` bloquea `comprar()` con excepción, rompiendo sustitución). La observación sobre `Administrador extends Vendedor` violando LSP al reactivar `comprar()` es perspicaz.

**Código de rediseño (generado por IA):** Segrega en `CompradorLibros`, `VendedorLibros`, `AdministradorSistema`. Cada clase implementa solo las interfaces de su rol. `Administrador` implementa las tres. La mención de RBAC como alternativa, la estructura de interfaces segregadas, y la redacción con formalidad de LLM indican uso de IA. El estudiante declaró "Sin IA" pero el análisis y código no son enteramente trabajo propio.

**Penalización:** −2.0 pts por declarar "Sin IA" cuando hay evidencia de uso de LLM en el análisis y código (términos no vistos en clase como RBAC, redacción con estructura de IA, código asistido).

**Nota:** 3.0 (contenido) − 2.0 (penalización integridad) = 1.0/3

---

### P04 — Patrones — Estrategias de descuento — 3.0/3

**Prompt:** Excelente. Estructura CONTEXTO-PROBLEMA-RESTRICCIONES-FORMATO-VALIDACIÓN. La restricción de "no crear clases mezcladas como DescuentoFidelidadYTemporada" es muy inteligente. La validación final sobre OCP es un buen criterio de calidad.

**Respuesta del LLM:** Correcta. Elige Decorator, descarta Strategy (mutuamente excluyente) y State (no son estados del ciclo de vida). La implementación con `CalculadorPrecio`, `DescuentoDecorator` abstracto y decoradores concretos es funcional.

**Análisis del estudiante:** Correcto. Identifica omisión crítica: el orden de apilamiento de decoradores afecta el resultado final con porcentajes. Propone prioridad o Builder para ordenar descuentos de mayor a menor. El código del decorador está bien implementado y el análisis es correcto. No se evidencia alteración por IA.

**Nota:** 3.0/3

---

### P05 — Patrones — Notificaciones combinadas — 4.2/5

**Prompt:** Muy bueno. Define claramente los 4 eventos que deben ocurrir, pide exactamente dos patrones combinados con diagrama Mermaid. La restricción de "no modificar la clase original del inventario" es clave. La validación sobre fallos de red es excelente.

**Respuesta del LLM:** Buena. Elige Observer + Command. Observer para pub/sub dinámico, Command para encapsular acciones y permitir reintentos. Diagrama Mermaid correcto. Implementación funcional con `GestorInventario` como sujeto y observers que internamente crean comandos.

**Análisis del estudiante:** Buena combinación de patrones, bien justificada. Identifica correctamente que la instanciación con `new` dentro de los observers impide la inyección de dependencias de Spring. Propone Factory Method como solución, lo cual es una mejora válida. El análisis tiene apoyo de IA en la redacción pero es de menor intensidad — el contenido demuestra comprensión propia de los patrones.

**Nota:** 4.2/5

---

### P06 — Clean Architecture — Conceptos (⭐ Sin IA) — 1.5/4 ⚠️ Penalización integridad

**Análisis del estudiante:** Explica correctamente las 4 capas (Entities, Use Cases, Interface Adapters, Frameworks & Drivers), la regla de dependencia (hacia adentro), y el DIP en los límites entre capas. La analogía con la arquitectura tradicional (presentación → lógica → datos) y por qué cambiar de MySQL a MongoDB "nos cae todo el sistema" es clara y correcta. El ejemplo de código con `RepositorioLibro` como interfaz en la capa interna y `PostgresLibroRepository` implementándola en la capa externa demuestra comprensión del DIP. Los 4 fragmentos de código están correctamente ubicados en sus capas.

**Penalización:** −2.0 pts por declarar "Sin IA" cuando hay evidencia de uso de LLM en el análisis (estructura pulida, terminología de IA, código asistido).

**Nota:** 3.5 (contenido) − 2.0 (penalización integridad) = 1.5/4

---

### P07 — Clean Architecture — Módulo de Pagos — 4.0/4

**Prompt:** Excelente. Contexto claro del módulo acoplado, pide diagrama Mermaid, código Java con comentarios de capa, y validación de cambio de base de datos sin tocar lógica central.

**Respuesta del LLM:** Muy buena. Define correctamente las 4 capas con el módulo de pagos. Interfaces `RepositorioPago`, `ServicioNotificacion`, `PasarelaPago` en la capa de Use Cases. Implementaciones en la capa externa. Diagrama Mermaid claro.

**Análisis del estudiante:** Excelente. Identifica correctamente que la regla de dependencia se respeta en todo el diseño. Observación válida sobre la necesidad de DTOs para validación de entrada, y conexión con testabilidad ("en las pruebas unitarias simplemente le paso un Mock"). El análisis demuestra comprensión profunda — no solo sabe *dónde* va cada cosa sino *por qué* importa.

**Nota:** 4.0/4

---

### P08 — Pruebas Unitarias — CarritoService — 4.2/5

**Prompt:** Muy bueno. Especifica los casos borde (carrito vacío, 10 items, item 11, checkout vacío), pide JUnit 5 + Mockito con AAA. La restricción de mockear `RepositorioLibro` es correcta.

**Respuesta del LLM:** Buena. Cubre casos normales, stock insuficiente, límite de 10 items, item repetido con límite lleno, checkout vacío, y remoción. Mockito bien aplicado con `@Mock` e `InjectMocks`.

**Análisis del estudiante:** Excelente. Identifica que el LLM no probó `vaciar()` ni el happy path de `validarParaCheckout()`. Escribe los casos faltantes él mismo. La reflexión final sobre la importancia de probar tanto el happy path como los errores es madura.

**Puntos fuertes:** El estudiante no solo critica — escribe el código que falta. Esto demuestra capacidad real de testing.

**Bonus:** +0.5 por commits que muestran evolución (prompt primero, análisis después).

**Nota:** 4.5/5

---

### P09 — TDD — ControlInventarioService (⭐) — 3.2/4

**Prompt:** Bueno. Define claramente el ciclo Red-Green-Refactor con restricciones explícitas para cada fase. La validación sobre "TDD genuino vs simulado" es un excelente criterio.

**Respuesta del LLM:** Correcta en estructura pero problemática en sustancia. La fase "Green" del LLM escribe la implementación completa directamente, no el código mínimo para pasar la prueba. Esto es un error conceptual de TDD.

**Análisis del estudiante:** Muy bueno. Identifica correctamente que el LLM "simula TDD" en lugar de aplicarlo genuinamente, porque la fase Green debería ser "el código más tonto posible". También señala que falta la segunda funcionalidad (reponer stock → DISPONIBLE) y la escribe él mismo. Se nota ayuda de la IA en el análisis pero el contenido demuestra comprensión del ciclo TDD.

**Observación de calificación:** El análisis demuestra comprensión genuina del ciclo TDD. La crítica de que "baby steps" son esenciales para verificar TDD genuino es una de las mejores observaciones del parcial.

**Nota:** 3.5/4

---

### P10 — VM vs Containers — 3.5/5

**Prompt:** Bueno. Pide los 5 puntos específicos (arquitectura, ventajas VM, ventajas containers, casos de uso, pitfalls) con tabla comparativa obligatoria.

**Respuesta del LLM:** Buena. Tabla comparativa clara, explicaciones correctas de arquitectura (hypervisor vs Docker engine), ventajas de cada uno realistas. Pitfalls bien identificados (overprovisioning, fat containers).

**Análisis del estudiante:** Bueno. Identifica omisión crítica: los contenedores comparten el kernel del host, lo cual es un problema de seguridad fundamental. Explica correctamente que un Kernel Panic afecta todos los contenedores vs el aislamiento de hardware en VMs. Fue capaz de abstraer información relevante del prompt del LLM y construir un análisis sólido.

**Nota:** 4.0/5

---

### P11 — Pruebas manuales vs automatizadas (⭐) — 4.0/4

**Prompt:** Muy bueno. Los 5 puntos solicitados están claramente definidos con mínimo de 3 ventajas/desventajas cada uno. La restricción de "primer release" es clave.

**Respuesta del LLM:** Buena en la comparativa teórica. Las ventajas/desventajas son correctas, los criterios de decisión son prácticos. Sin embargo, la recomendación final de "automatizar todo E2E con Selenium desde V1.0" es irrealista.

**Análisis del estudiante:** Excelente. Identifica que la recomendación del LLM contradice la Pirámide de Pruebas vista en clase. Propone invertir la pirámide: automatizar unitarias y API tests, usar manuales para UI en V1.0. Menciona correctamente el alto costo de mantenimiento de pruebas E2E automatizadas cuando la UI cambia constantemente. No "come entero" — evidencia problemas del prompt y su respuesta, cuestionando la recomendación del LLM con fundamentos del curso.

**Nota:** 4.0/4

---

### P12 — Ciclo de vida de defectos (⭐) — 2.0/3

**Prompt:** Bueno. Incluye el bug report completo, pide las 7 fases con responsables, acciones, herramientas y estados Jira.

**Respuesta del LLM:** Aceptable. Cubre las fases del ciclo de vida y asigna responsables correctos. Estados Jira propuestos son razonables (`NEW`, `ASSIGNED`, `IN PROGRESS`, `RESOLVED`, `IN TESTING`, `CLOSED`).

**Análisis del estudiante:** Demasiada intervención de IA — se nota que Ivan no lo hizo. El análisis suena a LLM en su estructura y redacción. La causa raiz que describe ("límite quemado de 5") es una simplificación de IA: el carrito tiene un límite real de 10 items; si el botón dejó de responder al item 6, la verdadera causa raiz es un defecto en el Frontend que no maneja correctamente la respuesta asíncrona del Backend, o un error de validación prematura en el JavaScript de la vista. Los estados Jira del LLM tampoco coinciden con los estados del curso (ABIERTO→EN REVISION→ASIGNADO→EN PROGRESO→EN VERIFICACION→RESUELTO→CERRADO), y el estudiante no señala esta discrepancia.

**Nota:** 2.0/3

---

### P13 — Integración — Refactoring Notificador — 2.6/3

**Prompt:** Muy bueno. Pide explícitamente SOLID + patrón de diseño + pruebas unitarias. La restricción de separar logging de notificación es clave. La validación sobre agregar WhatsApp sin modificar la clase principal es un buen criterio OCP.

**Respuesta del LLM:** Buena. Identifica SRP y OCP correctamente. Elige Strategy con `CanalNotificacion` interfaz. Separa `LoggerService` de la lógica de notificación. Pruebas unitarias con mocks son correctas.

**Análisis del estudiante:** Dos mejoras concretas y válidas: (1) reemplazar `FileLoggerService` casero por SLF4J/Logback (escritura directa a disco es peligrosa en concurrencia); (2) usar `@Component` y `@Service` de Spring Boot para auto-discovery en lugar de armar listas manuales.

**Puntos fuertes:** Las mejoras propuestas son prácticas y muestran conocimiento de frameworks reales.

**Nota:** 2.7/3

---

## Tabla resumen

| # | Tema | Puntos | % Prompt | % Análisis | Puntaje |
|---|---|---|---|---|---|
| P01 ⭐ | SOLID — GestorLibro | 3 | N/A | 100%−penalización | 1.0 |
| P02 | SOLID — ProcesadorPago | 4 | 75% | 85% | 3.5 |
| P03 ⭐ | SOLID — Jerarquía usuarios (Sin IA) | 3 | N/A | 100%−penalización | 1.0 |
| P04 | Patrones — Descuentos | 3 | 90% | 100% | 3.0 |
| P05 | Patrones — Notificaciones | 5 | 85% | 84% | 4.2 |
| P06 ⭐ | Clean Architecture — Conceptos (Sin IA) | 4 | N/A | 100%−penalización | 1.5 |
| P07 | Clean Architecture — Pagos | 4 | 88% | 100% | 4.0 |
| P08 | Pruebas — CarritoService | 5 | 85% | 90% | 4.5 |
| P09 | TDD — ControlInventario | 4 | 80% | 87% | 3.5 |
| P10 | VM vs Containers | 5 | 82% | 80% | 4.0 |
| P11 | Pruebas manuales vs auto | 4 | 85% | 100% | 4.0 |
| P12 | Ciclo de vida defectos | 3 | 78% | 67% | 2.0 |
| P13 | Integración — Refactoring | 3 | 85% | 90% | 2.7 |
| | **Subtotal** | **50** | | | **38.9** |
| | Bonificaciones | | | | +0.5 |
| | Penalizaciones | | | | −6.0 (P01, P03, P06: integridad) |
| | **NOTA FINAL** | | | | **39.4 / 50** |

---

## Observaciones generales

### Fortalezas
- **Prompts excelentes:** El patrón CONTEXTO-PROBLEMA-RESTRICCIONES-FORMATO-VALIDACIÓN es consistente y produce respuestas de alta calidad del LLM.
- **Análisis con mejoras concretas:** No solo critica — propone soluciones (excepciones personalizadas, Factory Method, SLF4J, Pirámide de Pruebas).
- **Respuestas Sin IA genuinas:** P01, P03 y P06 muestran comprensión real de SOLID y Clean Architecture sin patrones de escritura de IA.
- **Conexión con el curso:** Referencia conceptos vistos en clase (Pirámide de Pruebas, TDD baby steps, estados Jira del curso).
- **Commits bien organizados:** Cada pregunta tiene al menos 2 commits (prompt + análisis), mostrando evolución del trabajo.

### Áreas de mejora
- **Cuestionar más al LLM:** En P02, acepta la identificación de SRP sin cuestionar que las variantes de pago son la misma responsabilidad. En P04, no cuestiona el descarte de Strategy.
- **Profundidad en análisis teórico:** P10 y P12 tienen análisis correctos pero breves. Podrían haber explorado más los conceptos.
- **Estados Jira del curso:** En P12, no señala que los estados propuestos por el LLM no coinciden con los estados del curso (ABIERTO→EN REVISION→ASIGNADO→EN PROGRESO→EN VERIFICACION→RESUELTO→CERRADO).

### Bonificación aplicada
- +0.5 pts por commits que muestran evolución (prompt primero, análisis después) en todas las preguntas con IA.
