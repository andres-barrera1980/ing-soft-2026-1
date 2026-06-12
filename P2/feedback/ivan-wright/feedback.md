# Feedback — Parcial 2: Prompting

**Estudiante:** Ivan Wright
**LLM utilizado:** Gemini 3.1 Pro (preguntas con IA); Sin IA en P01, P03, P06
**Fecha de revisión:** 2026-06-11

---

## Resumen ejecutivo

**Nota final: 39.9 / 50 (79.8%)**

Buen desempeño general. Los prompts están bien estructurados siguiendo el patrón CONTEXTO → PROBLEMA → RESTRICCIONES → FORMATO → VALIDACIÓN, lo cual garantiza respuestas de calidad del LLM. El análisis es sólido en la mayoría de preguntas: el estudiante identifica omisiones del LLM y propone mejoras concretas. Las respuestas "Sin IA" (P01, P03, P06) demuestran comprensión genuina de los conceptos. Las áreas de mejora están en la profundidad del análisis en algunas preguntas teóricas (P10, P12) y en la evaluación más crítica de las respuestas del LLM cuando comete errores conceptuales.

---

## Evaluación por pregunta

### P01 — SOLID — Principio violado en GestorLibro (⭐ Sin IA) — 2.4/3

**Análisis del estudiante:** Excelente trabajo identificando las violaciones SOLID línea por línea. Correctamente señala SRP (validación, slug, logging, indexación), DIP (instanciación directa de PostgreSQL, EmailService, SearchIndex) y OCP (EmailService, SearchIndex). La propuesta de refactoring con interfaces (`LibroRepository`, `NotaificacionService`, `SearchIndexer`), inyección de dependencias por constructor, y movimiento de validación/slug a la entidad `Libro` es correcta y bien ejecutada.

**Puntos fuertes:** Análisis línea a línea del código original. La decisión de mover `validar()` y `generarSlug()` a la clase `Libro` es una mejora sobre la respuesta típica del LLM.

**Área de mejora:** El logging queda como `LOGGER.info()` directo en el servicio. Podría haber propuesto un aspecto o decorador para logging cross-cutting, mencionando el patrón Decorator o AOP.

**Nota:** 2.4/3 (80% × 3.0 = 2.4, Sin IA bono +20% ya aplicado en el %)

---

### P02 — SOLID — Refactoring ProcesadorPago — 3.2/4

**Prompt:** Bueno. Proporciona contexto de OpenLib Market con Java 21 y Spring Boot, pide identificar al menos dos principios SOLID y refactoring con patrón de diseño. El formato markdown y las restricciones técnicas están bien definidos.

**Respuesta del LLM:** Correcta. Identifica SRP y OCP, propone Strategy pattern con Spring Boot. La implementación con `List<EstrategiaPago>` inyectada y selección por stream es elegante.

**Análisis del estudiante:** Bueno. Identifica que el LLM no generó pruebas ni manejó excepciones de dominio. Propone `MetodoPagoNoSoportadoException` personalizada, lo cual mejora la solución. Menciona que Strategy facilita testing con Mockito.

**Observación de calificación:** El LLM identifica SRP como violado, pero como se discutió en clase, las variantes de pago (tarjeta, PSE, PayPal) son implementaciones de la *misma* responsabilidad ("procesar pago"), no responsabilidades distintas. La violación principal es OCP. El estudiante acepta el análisis del LLM sin cuestionar esta distinción.

**Nota:** 3.2/4

---

### P03 — SOLID — Jerarquía de usuarios (⭐ Sin IA) — 2.4/3

**Análisis del estudiante:** Correcto. Identifica ISP (interfaz `Usuario` con 5 métodos que obliga a `Comprador` a lanzar `UnsupportedOperationException`) y LSP (`Vendedor extends Comprador` bloquea `comprar()` con excepción, rompiendo sustitución). La observación sobre `Administrador extends Vendedor` violando LSP al reactivar `comprar()` es perspicaz.

**Propuesta de rediseño:** Sólida. Segrega en `CompradorLibros`, `VendedorLibros`, `AdministradorSistema`. Cada clase implementa solo las interfaces de su rol. `Administrador` implementa las tres. La nota sobre RBAC como alternativa más avanzada demuestra visión más allá de lo básico.

**Puntos fuertes:** Identifica correctamente ambos principios (ISP + LSP) y explica *por qué* se violan, no solo *qué* se viola. El rediseño elimina todas las excepciones `UnsupportedOperationException`.

**Área de mejora:** Podría haber mencionado que un `CompradorVendedor` (usuario que compra y vende) necesitaría implementar ambas interfaces, mostrando la flexibilidad del diseño propuesto.

**Nota:** 2.4/3 (80% × 3.0 = 2.4, Sin IA bono +20% ya aplicado)

---

### P04 — Patrones — Estrategias de descuento — 2.5/3

**Prompt:** Excelente. Estructura CONTEXTO-PROBLEMA-RESTRICCIONES-FORMATO-VALIDACIÓN. La restricción de "no crear clases mezcladas como DescuentoFidelidadYTemporada" es muy inteligente. La validación final sobre OCP es un buen criterio de calidad.

**Respuesta del LLM:** Correcta. Elige Decorator, descarta Strategy (mutuamente excluyente) y State (no son estados del ciclo de vida). La implementación con `CalculadorPrecio`, `DescuentoDecorator` abstracto y decoradores concretos es funcional.

**Análisis del estudiante:** Bueno. Identifica omisión crítica: el orden de apilamiento de decoradores afecta el resultado final con porcentajes. Propone prioridad o Builder para ordenar descuentos de mayor a menor.

**Observación de calificación:** La crítica al descarte de Strategy es parcialmente válida — Strategy con una *lista* de estrategias aplicables secuencialmente (como Composite) también funcionaría. El estudiante no cuestiona esto. Sin embargo, Decorator es efectivamente una buena elección para este caso.

**Nota:** 2.5/3

---

### P05 — Patrones — Notificaciones combinadas — 3.5/5

**Prompt:** Muy bueno. Define claramente los 4 eventos que deben ocurrir, pide exactamente dos patrones combinados con diagrama Mermaid. La restricción de "no modificar la clase original del inventario" es clave. La validación sobre fallos de red es excelente.

**Respuesta del LLM:** Buena. Elige Observer + Command. Observer para pub/sub dinámico, Command para encapsular acciones y permitir reintentos. Diagrama Mermaid correcto. Implementación funcional con `GestorInventario` como sujeto y observers que internamente crean comandos.

**Análisis del estudiante:** Identifica correctamente que la instanciación con `new` dentro de los observers impide la inyección de dependencias de Spring. Propone Factory Method como solución, lo cual es una mejora válida.

**Área de mejora:** El estudiante propone agregar un tercer patrón (Factory), pero la pregunta pide exactamente dos. La observación sobre DI es correcta pero la solución con Factory añade complejidad innecesaria — Spring puede inyectar beans en los observers directamente si se usan `@Component`.

**Nota:** 3.5/5

---

### P06 — Clean Architecture — Conceptos (⭐ Sin IA) — 3.2/4

**Análisis del estudiante:** Sólido. Explica correctamente las 4 capas (Entities, Use Cases, Interface Adapters, Frameworks & Drivers), la regla de dependencia (hacia adentro), y el DIP en los límites entre capas. La analogía con la arquitectura tradicional (presentación → lógica → datos) y por qué cambiar de MySQL a MongoDB "nos cae todo el sistema" es clara y correcta.

**Puntos fuertes:** El ejemplo de código con `RepositorioLibro` como interfaz en la capa interna y `PostgresLibroRepository` implementándola en la capa externa demuestra comprensión real del DIP. Los 4 fragmentos de código (entidad, caso de uso, controlador, repositorio) están correctamente ubicados en sus capas.

**Área de mejora:** No menciona los "casos de uso" como orquestadores que coordinan las entidades — solo muestra un caso de uso que delega directamente al repositorio. Un caso de uso real coordinaría múltiples entidades y servicios.

**Nota:** 3.2/4 (80% × 4.0 = 3.2, Sin IA bono +20% ya aplicado)

---

### P07 — Clean Architecture — Módulo de Pagos — 3.5/4

**Prompt:** Excelente. Contexto claro del módulo acoplado, pide diagrama Mermaid, código Java con comentarios de capa, y validación de cambio de base de datos sin tocar lógica central.

**Respuesta del LLM:** Muy buena. Define correctamente las 4 capas con el módulo de pagos. Interfaces `RepositorioPago`, `ServicioNotificacion`, `PasarelaPago` en la capa de Use Cases. Implementaciones en la capa externa. Diagrama Mermaid claro.

**Análisis del estudiante:** Identifica correctamente que la regla de dependencia se respeta en todo el diseño. Observación válida sobre la necesidad de DTOs para validación de entrada, y excelente conexión con testabilidad ("en las pruebas unitarias simplemente le paso un Mock").

**Puntos fuertes:** La conexión explícita entre Clean Architecture y testabilidad demuestra comprensión profunda — no solo sabe *dónde* va cada cosa sino *por qué* importa.

**Nota:** 3.5/4

---

### P08 — Pruebas Unitarias — CarritoService — 4.2/5

**Prompt:** Muy bueno. Especifica los casos borde (carrito vacío, 10 items, item 11, checkout vacío), pide JUnit 5 + Mockito con AAA. La restricción de mockear `RepositorioLibro` es correcta.

**Respuesta del LLM:** Buena. Cubre casos normales, stock insuficiente, límite de 10 items, item repetido con límite lleno, checkout vacío, y remoción. Mockito bien aplicado con `@Mock` e `InjectMocks`.

**Análisis del estudiante:** Excelente. Identifica que el LLM no probó `vaciar()` ni el happy path de `validarParaCheckout()`. Escribe los casos faltantes él mismo. La reflexión final sobre la importancia de probar tanto el happy path como los errores es madura.

**Puntos fuertes:** El estudiante no solo critica — escribe el código que falta. Esto demuestra capacidad real de testing.

**Bonus:** +0.5 por commits que muestran evolución (prompt primero, análisis después).

**Nota:** 4.2/5

---

### P09 — TDD — ControlInventarioService (⭐) — 3.2/4

**Prompt:** Bueno. Define claramente el ciclo Red-Green-Refactor con restricciones explícitas para cada fase. La validación sobre "TDD genuino vs simulado" es un excelente criterio.

**Respuesta del LLM:** Correcta en estructura pero problemática en sustancia. La fase "Green" del LLM escribe la implementación completa directamente, no el código mínimo para pasar la prueba. Esto es un error conceptual de TDD.

**Análisis del estudiante:** Muy bueno. Identifica correctamente que el LLM "simula TDD" en lugar de aplicarlo genuinamente, porque la fase Green debería ser "el código más tonto posible". También señala que falta la segunda funcionalidad (reponer stock → DISPONIBLE) y la escribe él mismo.

**Observación de calificación:** El análisis demuestra comprensión genuina del ciclo TDD. La crítica de que "baby steps" son esenciales para verificar TDD genuino es una de las mejores observaciones del parcial.

**Nota:** 3.2/4

---

### P10 — VM vs Containers — 3.5/5

**Prompt:** Bueno. Pide los 5 puntos específicos (arquitectura, ventajas VM, ventajas containers, casos de uso, pitfalls) con tabla comparativa obligatoria.

**Respuesta del LLM:** Buena. Tabla comparativa clara, explicaciones correctas de arquitectura (hypervisor vs Docker engine), ventajas de cada uno realistas. Pitfalls bien identificados (overprovisioning, fat containers).

**Análisis del estudiante:** Identifica omisión crítica: los contenedores comparten el kernel del host, lo cual es un problema de seguridad fundamental. Explica correctamente que un Kernel Panic afecta todos los contenedores vs el aislamiento de hardware en VMs.

**Área de mejora:** El análisis es correcto pero breve. Podría haber profundizado más en los pitfalls o haber conectado con conceptos de orquestación (Kubernetes) que complementan la discusión de containers.

**Nota:** 3.5/5

---

### P11 — Pruebas manuales vs automatizadas (⭐) — 3.2/4

**Prompt:** Muy bueno. Los 5 puntos solicitados están claramente definidos con mínimo de 3 ventajas/desventajas cada uno. La restricción de "primer release" es clave.

**Respuesta del LLM:** Buena en la comparativa teórica. Las ventajas/desventajas son correctas, los criterios de decisión son prácticos. Sin embargo, la recomendación final de "automatizar todo E2E con Selenium desde V1.0" es irrealista.

**Análisis del estudiante:** Excelente. Identifica que la recomendación del LLM contradice la Pirámide de Pruebas vista en clase. Propone invertir la pirámide: automatizar unitarias y API tests, usar manuales para UI en V1.0. Menciona correctamente el alto costo de mantenimiento de pruebas E2E automatizadas cuando la UI cambia constantemente.

**Puntos fuertes:** La referencia a la Pirámide de Pruebas y la crítica fundamentada a la recomendación del LLM demuestran comprensión real de estrategia de testing.

**Nota:** 3.2/4

---

### P12 — Ciclo de vida de defectos (⭐) — 2.4/3

**Prompt:** Bueno. Incluye el bug report completo, pide las 7 fases con responsables, acciones, herramientas y estados Jira.

**Respuesta del LLM:** Aceptable. Cubre las fases del ciclo de vida y asigna responsables correctos. Estados Jira propuestos son razonables (`NEW`, `ASSIGNED`, `IN PROGRESS`, `RESOLVED`, `IN TESTING`, `CLOSED`).

**Análisis del estudiante:** Identifica dos errores graves del LLM: (1) fusiona Diagnóstico con Corrección, cuando el diagnóstico es la fase más crítica y demorada; (2) la causa raiz propuesta por el LLM ("límite de 5 items") contradice las reglas de negocio de OpenLib Market (límite real es 10). El estudiante propone la causa raiz correcta: error en el Frontend que no maneja la respuesta asíncrona del Backend.

**Observación de calificación:** Los estados Jira del LLM no coinciden exactamente con los estados del curso (ABIERTO→EN REVISION→ASIGNADO→EN PROGRESO→EN VERIFICACION→RESUELTO→CERRADO). El estudiante no señala esta discrepancia.

**Nota:** 2.4/3

---

### P13 — Integración — Refactoring Notificador — 2.6/3

**Prompt:** Muy bueno. Pide explícitamente SOLID + patrón de diseño + pruebas unitarias. La restricción de separar logging de notificación es clave. La validación sobre agregar WhatsApp sin modificar la clase principal es un buen criterio OCP.

**Respuesta del LLM:** Buena. Identifica SRP y OCP correctamente. Elige Strategy con `CanalNotificacion` interfaz. Separa `LoggerService` de la lógica de notificación. Pruebas unitarias con mocks son correctas.

**Análisis del estudiante:** Dos mejoras concretas y válidas: (1) reemplazar `FileLoggerService` casero por SLF4J/Logback (escritura directa a disco es peligrosa en concurrencia); (2) usar `@Component` y `@Service` de Spring Boot para auto-discovery en lugar de armar listas manuales.

**Puntos fuertes:** Las mejoras propuestas son prácticas y muestran conocimiento de frameworks reales.

**Nota:** 2.6/3

---

## Tabla resumen

| # | Tema | Puntos | % Prompt | % Análisis | Puntaje |
|---|---|---|---|---|---|
| P01 ⭐ | SOLID — GestorLibro (Sin IA) | 3 | N/A | 80% | 2.4 |
| P02 | SOLID — ProcesadorPago | 4 | 75% | 78% | 3.2 |
| P03 ⭐ | SOLID — Jerarquía usuarios (Sin IA) | 3 | N/A | 80% | 2.4 |
| P04 | Patrones — Descuentos | 3 | 90% | 78% | 2.5 |
| P05 | Patrones — Notificaciones | 5 | 85% | 72% | 3.5 |
| P06 ⭐ | Clean Architecture — Conceptos (Sin IA) | 4 | N/A | 80% | 3.2 |
| P07 | Clean Architecture — Pagos | 4 | 88% | 82% | 3.5 |
| P08 | Pruebas — CarritoService | 5 | 85% | 84% | 4.2 |
| P09 | TDD — ControlInventario | 4 | 80% | 80% | 3.2 |
| P10 | VM vs Containers | 5 | 82% | 70% | 3.5 |
| P11 | Pruebas manuales vs auto | 4 | 85% | 78% | 3.2 |
| P12 | Ciclo de vida defectos | 3 | 78% | 76% | 2.4 |
| P13 | Integración — Refactoring | 3 | 85% | 82% | 2.6 |
| | **Subtotal** | **50** | | | **39.9** |
| | Bonificaciones | | | | +0.5 |
| | Penalizaciones | | | | 0.0 |
| | **NOTA FINAL** | | | | **40.4 / 50** |

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
