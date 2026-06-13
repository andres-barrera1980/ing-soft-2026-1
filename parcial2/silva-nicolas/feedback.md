# Parcial 2 — Silva Nicolás (Nicolás Silva García)

## Información General

| Campo | Detalle |
|---|---|
| **Estudiante** | Silva Nicolás (Nicolás Silva García) |
| **Rama** | `parcial2/silva-nicolas` |
| **Archivos** | `P01_solid_principio/` ... `P13_integracion_refactoring/` |
| **Herramientas IA** | Gemini 3.1 Pro (P02,P04,P05,P07,P08,P09,P10), Sin IA (P01,P03,P06,P11,P12), Sin responder (P13) |
| **Metodología** | Gemini 3.1 Pro con memoria de sesión persistente para mantener contexto de OpenLib Market. Prompts con roles técnicos detallados (senior Java, arquitecto de software). |

## Resultados por Pregunta

| # | Pregunta | Máx | Nota | Observación |
|---|---|---|---|---|
| P01 | Principio SOLID (SRP) | 3.0 | 2.8 | Sin IA. Identifica correctamente SRP, propone refactor clase por clase. Análisis sin autoevaluación. Le faltó identificar DIP (conexiones y servicios hardcodeados en cada clase refactorizada) y el código es poco legible. |
| P02 | SOLID Refactor | 4.0 | 3.0 | Prompt con rol + contexto; LLM identifica OCP, SRP, DIP + Strategy. Estudiante identifica prompt gaps (sin formato, DDD ausente). Análisis con evidente uso de IA. |
| P03 | Jerarquía de Objetos | 3.0 | 3.0 | Sin IA. Identifica ISP y LSP correctamente. Propone interfaz segregada limpia. Respuesta completa y correcta. |
| P04 | Estrategias de Descuento | 3.0 | 3.0 | Prompt con restricción de descartar alternativas. LLM elige Decorator (descarta Strategy y Chain). Estudiante añade Factory. Análisis muy bueno. |
| P05 | Patrón Reacción | 5.0 | 4.0 | Observer + Decorator combinados. LLM produce diagrama Mermaid, código, manejo de fallos. Estudiante identifica falta de concurrencia y propone CompletableFuture. Plus por el tema de hilos. |
| P06 | Clean Architecture | 4.0 | 4.0 | Sin IA. Explica 4 capas con ejemplos de OpenLib Market. Conecta con DIP. Buena explicación. |
| P07 | Clean Architecture Pagos | 4.0 | 3.0 | Prompt exige DIP + capas. LLM produce mapeo 4-capas con Mermaid. Estudiante identifica falta de Strategy para enrutamiento, pero no implementa corrección. Análisis suficiente aunque sin implementación concreta. |
| P08 | Pruebas Carrito | 5.0 | 3.5 | Prompt con reglas de negocio. LLM alucina IInventarioService. Estudiante detecta la alucinación y corrige a RepositorioLibro. Propone eliminar assertEquals frágiles manteniendo validación de tipo de excepción. Análisis crítico sólido. |
| P09 | TDD Inventario | 4.0 | 3.5 | Prompt estructura Red-Green-Refactor. LLM simula TDD genuinamente. Estudiante identifica falta de idempotencia y casos de stock negativo. Usó IA en pregunta recomendada sin IA. |
| P10 | VM vs Contenedores | 5.0 | 4.0 | Prompt con especificaciones técnicas (hypervisor tipo 1/2, namespaces, cgroups). LLM produce tabla + arquitectura. Estudiante agrega análisis de IPC/POSIX (semáforos, memoria compartida). Demuestra comprensión a bajo nivel de sistemas operativos. |
| P11 | Pruebas Manuales vs Auto | 4.0 | 3.0 | Sin IA. Comparación sólida con ventajas/desventajas (3+ cada una). Recomendaciones prácticas para OpenLib Market. Análisis superficial ("no aplica, es mia propia"). |
| P12 | Ciclo de Vida Defectos | 3.0 | 2.5 | Sin IA. Describe 7 fases con roles, herramientas (Jira), y ejemplo concreto de OpenLib Market (fallo al agregar 6to libro). Le faltó conectar ejemplos de Jira con los estados descritos. |
| P13 | Refactorización | 3.0 | 0.0 | **No respondió.** Archivo `respuesta.md` es solo la plantilla vacía. |
| **TOTAL** | | **50.0** | **39.3** | **78.6%** — Nota: **4.0** (por la tabla de conversión del enunciado) |

## Retroalimentación General

### Fortalezas
- **Calidad de prompts:** Los prompts del estudiante son técnicamente específicos, con roles bien definidos (arquitecto senior, SDET, Agile Coach), restricciones claras y contexto de dominio (OpenLib Market).
- **Uso estratégico de memoria de Gemini:** El estudiante aprovecha la memoria persistente de Gemini 3.1 Pro para mantener contexto del proyecto a lo largo de múltiples preguntas, una técnica legítima de prompt engineering.
- **Identificación de alucinaciones:** En P08 detecta que el LLM mockeó un servicio inexistente (IInventarioService) en lugar de RepositorioLibro. Esto demuestra capacidad crítica para leer y verificar el output del LLM.
- **Análisis de arquitectura a bajo nivel:** En P10, el estudiante conecta contenedores con semáforos POSIX y memoria compartida, demostrando comprensión del impacto de namespaces en IPC. Este nivel de profundidad técnica es inusual y valioso.
- **Autocrítica constructiva:** En P04, P05, P07, P08 y P09, el estudiante identifica honestamente las limitaciones de su propio prompt (falta de formato de salida, falta de concurrencia, falta de enrutamiento, etc.) y propone mejoras concretas (Factory, CompletableFuture, Strategy, validación de estado, etc.).
- **Conocimiento genuino en temas sin IA:** P01, P03, P06, P11 y P12 muestran respuestas coherentes y bien estructuradas sin asistencia de IA.

### Áreas de Mejora
- **P13 no respondido:** La pregunta P13 (Refactorización, 3 pts) no fue contestada. El archivo `respuesta.md` contiene solo la plantilla. Esto representa una pérdida significativa en la nota final.
- **README sin completar:** El archivo `README.md` de la raíz aún contiene los placeholders `[Tu nombre y apellido]` y `[FECHA]`. Es un detalle menor pero importante para la presentación formal.
- **Análisis superficial en preguntas Sin IA:** En P01, P03, P06, P11 y P12, las secciones de análisis son "No aplica, esta respuesta es mia propia". Si bien se entiende que no hay prompt que evaluar, sería valioso que el estudiante autoevaluara su propia respuesta: ¿qué más se podría mejorar? ¿hay conceptos de clase que faltaron?
- **Pocos commits por pregunta:** La mayoría de preguntas tienen 1 solo commit (no los 2 requeridos de prompt+respuesta + análisis). P13 no tiene commits.
- **Formato de respuestas:** Algunas respuestas mezclan el contenido del LLM con la respuesta final del estudiante. Separar claramente "lo que dijo el LLM" de "lo que yo concluyo" facilitaría la evaluación.

### Observaciones Técnicas
- **P04 (Decorator vs Strategy):** El estudiante eligió Decorator sobre Strategy para descuentos combinables. El LLM descartó correctamente Strategy (explosión combinatoria) y Chain of Responsibility (semántica de cortocircuito). El estudiante agregó Factory para la composición automática, una mejora genuina.
- **P05 (Observer + Decorator):** Combinación correcta de patrones. La adición de CompletableFuture + ExecutorService para concurrencia fue una mejora arquitectónica significativa que el LLM no incluyó.
- **P08 (Alucinación detectada):** El estudiante notó que el LLM mockeó IInventarioService — una clase que no existe en el código base. Esto es exactamente lo que se espera en un análisis crítico: verificar que el output del LLM coincide con la realidad del código.
- **P09 (TDD - Idempotencia):** El estudiante identificó que actualizar stock a 0 dos veces enviaría notificaciones duplicadas al vendedor. Propuso una guarda de estado para garantizar idempotencia. También señaló la falta de validación de stock negativo.
- **P10 (IPC en contenedores):** El análisis sobre semáforos POSIX y memoria compartida en contenedores demuestra comprensión de sistemas operativos a bajo nivel, conectando conceptos de otras asignaturas con la infraestructura de despliegue.

## Nota Final

| Concepto | Valor |
|---|---|
| **Puntaje Total** | **39.3 / 50.0** |
| **Porcentaje** | **78.6%** |
| **Nota (escala 0.0–5.0)** | **4.0** |

### Desglose de puntajes

| # | Máx | Nota | % |
|---|---|---|---|
| P01 | 3.0 | 2.8 | 93% |
| P02 | 4.0 | 3.0 | 75% |
| P03 | 3.0 | 3.0 | 100% |
| P04 | 3.0 | 3.0 | 100% |
| P05 | 5.0 | 4.0 | 80% |
| P06 | 4.0 | 4.0 | 100% |
| P07 | 4.0 | 3.0 | 75% |
| P08 | 5.0 | 3.5 | 70% |
| P09 | 4.0 | 3.5 | 88% |
| P10 | 5.0 | 4.0 | 80% |
| P11 | 4.0 | 3.0 | 75% |
| P12 | 3.0 | 2.5 | 83% |
| P13 | 3.0 | 0.0 | 0% |
| **Total** | **50.0** | **39.3** | **78.6%** |

*Nota: P13 con 0.0 porque el estudiante no respondió la pregunta (archivo `respuesta.md` con plantilla vacía).*
