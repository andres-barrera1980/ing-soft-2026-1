# Pregunta P12: Ciclo de vida de defectos

### Estudiante
- **Nombre completo**: Mateo Traslaviña Moreno

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Claude |
| **Modelo específico** | Claude Sonnet 4.6 |
| **¿Por qué elegiste este LLM?** | El ciclo de vida de defectos es un tema con flujos de estado bien definidos que Claude representa eficientemente. Su capacidad para generar diagramas ASCII y tablas bien estructuradas hace que los flujos sean fáciles de entender, y conecta bien los conceptos teóricos con herramientas concretas como Jira. |

---

### Prompt utilizado

```
Eres un QA Lead en el equipo de OpenLib Market, plataforma de compra-venta de libros en Java 21 con Spring Boot 3.x.

Necesito una explicación completa del ciclo de vida de un defecto (bug lifecycle) aplicado a este proyecto.

Cubre:
1. Definición: qué es un defecto y cómo se diferencia de un "error" y un "fallo"
2. Los estados del ciclo de vida (con diagrama de flujo ASCII)
3. Quién tiene responsabilidad en cada estado (QA, Dev, Product Owner, etc.)
4. Severidad vs Prioridad: diferencia y ejemplos concretos de OpenLib Market
5. Un ejemplo completo de un defecto real en OpenLib Market pasando por todos los estados (desde que se reporta hasta que se cierra)
6. Métricas para medir la salud del proceso de gestión de defectos
7. Cómo se integra el bug lifecycle con un tablero Jira/GitHub Issues en un equipo ágil

Formato: markdown con diagrama de flujo, tabla de severidad/prioridad, y el ejemplo completo con el defecto pasando estado por estado.
```

---

### Respuesta del LLM

## Definición: Error, Defecto y Fallo

Es importante distinguir tres conceptos relacionados pero diferentes:

- **Error**: una acción humana equivocada que produce un resultado incorrecto. El desarrollador escribe `>` en lugar de `>=` en la validación de stock.
- **Defecto (Bug)**: la manifestación de ese error en el código/documentación. El `if (stock > cantidad)` incorrecto en `ControlInventario.java` es el defecto.
- **Fallo (Failure)**: el comportamiento incorrecto observable cuando el defecto se ejecuta. Un usuario ve "Compra exitosa" pero su pedido falla porque el sistema permitió vender el último libro dos veces.

La cadena es: Error → Defecto → Fallo. Un defecto puede existir sin producir un fallo visible (si esa rama de código nunca se ejecuta).

## Ciclo de vida del defecto

```
                    ┌─────────────┐
                    │   NUEVO     │ ← QA detecta y reporta
                    └──────┬──────┘
                           │
                    ┌──────▼──────┐
                    │  ASIGNADO   │ ← QA Lead/PM asigna al Dev
                    └──────┬──────┘
                           │
              ┌────────────▼────────────┐
              │        ANÁLISIS         │ ← Dev analiza reproducibilidad
              └──┬────────────────┬─────┘
                 │                │
        ┌────────▼────┐    ┌──────▼──────┐
        │  DUPLICADO  │    │   ABIERTO   │ ← Confirmado, en backlog
        │  /RECHAZADO │    └──────┬──────┘
        └─────────────┘          │
                          ┌──────▼──────┐
                          │  EN PROCESO │ ← Dev está corrigiendo
                          └──────┬──────┘
                                 │
                          ┌──────▼──────┐
                          │  RESUELTO   │ ← Dev dice "listo"
                          └──────┬──────┘
                                 │
                    ┌────────────▼───────────┐
                    │   VERIFICACIÓN (QA)     │ ← QA verifica el fix
                    └──┬─────────────────┬───┘
                       │                 │
              ┌────────▼────┐    ┌───────▼──────┐
              │  REABIERTO  │    │    CERRADO    │ ← Fix verificado ✓
              │ (vuelve a   │    └──────────────┘
              │  ASIGNADO)  │
              └─────────────┘
```

## Responsabilidades por estado

| Estado | Responsable | Acción |
|---|---|---|
| **Nuevo** | QA Tester | Reporta con pasos de reproducción, capturas, entorno |
| **Asignado** | QA Lead / Product Owner | Prioriza, asigna al desarrollador adecuado |
| **Análisis** | Desarrollador | Reproduce, confirma, investiga causa raíz |
| **Duplicado/Rechazado** | Desarrollador | Marca si ya existe o si no es un defecto real |
| **Abierto** | Desarrollador (backlog) | En cola para ser corregido según prioridad |
| **En Proceso** | Desarrollador | Activamente corrigiendo |
| **Resuelto** | Desarrollador | Fix aplicado, listo para QA |
| **Verificación** | QA Tester | Reproduce los pasos originales para confirmar fix |
| **Cerrado** | QA Tester / QA Lead | Fix confirmado, bug cerrado |
| **Reabierto** | QA Tester | El fix no funcionó o creó regresión |

## Severidad vs Prioridad

Son dos dimensiones ortogonales que frecuentemente se confunden:

- **Severidad**: impacto técnico del defecto en el sistema (¿qué tan grave es el problema?)
- **Prioridad**: urgencia de corregirlo desde la perspectiva del negocio (¿qué tan pronto necesitamos el fix?)

| | Alta Prioridad | Baja Prioridad |
|---|---|---|
| **Alta Severidad** | Fix AHORA: falla en procesamiento de pagos | Fix esta semana: error en reporte interno que solo ve el admin |
| **Baja Severidad** | Fix rápido: botón de carrito mal posicionado en homepage | Fix cuando haya tiempo: typo en email de bienvenida |

**Ejemplos concretos de OpenLib Market:**

| Defecto | Severidad | Prioridad | Razonamiento |
|---|---|---|---|
| El pago con tarjeta Visa falla con error 500 | Crítica | Urgente | Bloquea ingresos directamente |
| La búsqueda de libros en español retorna resultados en inglés | Alta | Alta | Afecta la experiencia de todos los usuarios |
| El precio en el carrito no se actualiza si cambias la cantidad sin hacer click | Media | Media | Workaround existe (recargar página) |
| El logo se muestra pixelado en pantallas 4K | Baja | Baja | Cosmético, no afecta funcionalidad |
| Typo "Pubilcar libro" en botón del dashboard de admin | Baja | Urgente | El CEO verá el demo mañana — alta prioridad de negocio a pesar de baja severidad técnica |

## Ejemplo completo: defecto en OpenLib Market

**Bug**: "El sistema permite comprar libros sin stock disponible"

**Estado 1 — NUEVO** (reportado por QA, martes 9am):
```
Título: Se puede comprar libro sin stock
Entorno: Staging, build #247
Pasos de reproducción:
  1. Navegar a libro "Clean Code" (ISBN 978-0132350884)
  2. Reducir stock a 0 desde admin
  3. Intentar comprar desde cuenta de comprador
Resultado actual: Compra exitosa, orden creada
Resultado esperado: Error "No hay stock disponible"
Severidad: Crítica
Adjunto: screenshot_stock_cero.png, video_reproduccion.mp4
```

**Estado 2 — ASIGNADO** (QA Lead, martes 9:30am):
QA Lead asigna a María (dev de inventario), prioridad Urgente.

**Estado 3 — ANÁLISIS** (María, martes 10am):
María reproduce el bug. Encuentra que `CarritoService.agregarItem()` no llama a `ControlInventario.hayDisponibilidad()` antes de crear la orden.

**Estado 4 — ABIERTO** → **EN PROCESO** (martes 10:30am):
María confirma el defecto, mueve a En Proceso, crea branch `fix/stock-validation-on-purchase`.

**Estado 5 — RESUELTO** (martes 2pm):
María agrega la validación, escribe tests de regresión, hace PR. Review aprobado, mergeo a develop.
```
Nota del desarrollador: Agregada validación en CarritoService.agregarItem() 
antes de proceder. Tests añadidos: CarritoServiceTest.agregar_sinStock_lanzaExcepcion()
Commit: abc1234
```

**Estado 6 — VERIFICACIÓN** (QA, martes 4pm):
QA ejecuta los pasos de reproducción originales en staging con el fix. El error "No hay stock disponible" aparece correctamente. QA también verifica el caso de borde: 1 unidad disponible, 2 intentos de compra simultáneos (uno debe fallar).

**Estado 7 — CERRADO** (martes 4:30pm):
Verificación exitosa. Bug cerrado. Tiempo total: 7.5 horas.

## Métricas de gestión de defectos

| Métrica | Descripción | Objetivo OpenLib Market |
|---|---|---|
| **Defect Density** | Bugs por KLOC (1000 líneas de código) | < 5 bugs/KLOC |
| **Mean Time to Detect (MTTD)** | Tiempo promedio desde introducción hasta detección | < 24h con CI automatizado |
| **Mean Time to Resolve (MTTR)** | Tiempo promedio desde reporte hasta cierre | < 48h para críticos |
| **Defect Escape Rate** | % de bugs que llegan a producción vs encontrados en QA | < 5% |
| **Reopen Rate** | % de bugs que se reabren después de "resuelto" | < 10% |
| **Bug Backlog Aging** | Bugs sin resolver > 30 días | 0 críticos, < 5 medios |

## Integración con Jira/GitHub Issues en equipo ágil

En un sprint de 2 semanas para OpenLib Market:

**Con Jira**: Los defectos se crean como issues tipo "Bug". El workflow de estados mapea directamente (To Do → In Progress → In Review → Done). Los bugs críticos se agregan al sprint activo; los medios/bajos van al backlog de bugs para el próximo sprint planning. Los filtros `priority = Critical AND status != Closed` son el primer tablero del QA Lead cada mañana.

**Con GitHub Issues**: Los labels son la clave — `severity:critical`, `severity:high`, `priority:urgent`. Los bugs se linkean a los PRs que los corrigen. Los milestones representan sprints. La acción de GitHub `close #123` en el commit cierra automáticamente el issue.

**En el sprint ceremony**: La tasa de bugs introducidos vs resueltos en el sprint se revisa en la retrospectiva. Si se abren más bugs de los que se cierran, el equipo necesita invertir más tiempo en calidad (más tests, code review más riguroso).

---

### Analisis crítico de la respuesta

#### 1. ¿Que hizo bien el prompt?

El prompt fue efectivo al pedir un **ejemplo completo de un defecto pasando por todos los estados**. Sin este requerimiento, la respuesta habría sido puramente teórica. Ver el bug "compra sin stock" transitar desde NUEVO hasta CERRADO con tiempos reales y notas de desarrollador hace el ciclo de vida concreto y recordable. También fue útil pedir la distinción **Severidad vs Prioridad con ejemplos de OpenLib Market** — es uno de los conceptos más confundidos en QA, y el ejemplo del "typo que el CEO verá mañana" (alta prioridad, baja severidad) lo ilustra mejor que cualquier definición abstracta.

#### 2. ¿Qué se puede mejorar?

El LLM no mencionó el estado **DIFERIDO (DEFERRED)** — un estado importante cuando el equipo decide conscientemente no corregir un bug porque el costo > beneficio, o porque es un comportamiento conocido en una versión específica. También faltó el concepto de **bug triage**: la reunión periódica donde el equipo revisa el backlog de bugs y decide prioridades. En un proyecto real de OpenLib Market, el triage semanal con Dev Lead, QA Lead y Product Owner es donde se toman las decisiones de severidad/prioridad. Finalmente, el LLM no habló de **bugs de producción vs bugs de staging** — los bugs encontrados en producción tienen un flujo diferente (hotfix branch, deploy urgente) del flujo normal de sprint.

#### 3. Respuesta final

El ciclo de vida de un defecto es el proceso formal que lleva un bug desde su descubrimiento hasta su resolución confirmada. La distinción conceptual más importante es: **error** (causa humana) → **defecto** (manifestación en el código) → **fallo** (comportamiento incorrecto observable).

Los estados clave son: Nuevo → Asignado → Análisis → Abierto → En Proceso → Resuelto → Verificación → Cerrado, con ramificaciones hacia Duplicado/Rechazado y Reabierto.

El concepto más mal aplicado en equipos es la confusión entre **severidad y prioridad**. Severidad es técnica ("el sistema explota"), prioridad es de negocio ("¿cuándo necesitamos el fix?"). Un typo en el botón que el CEO ve en 2 horas tiene baja severidad pero máxima prioridad. Un bug en un módulo de reportes internos puede tener alta severidad técnica pero baja prioridad si nadie usa esos reportes.

Para OpenLib Market en un equipo ágil, el ciclo de vida debe integrarse con el sprint: los bugs críticos entran directamente al sprint activo (interrumpen la planificación si es necesario), los medios y bajos van al backlog y se priorizan en el siguiente sprint planning. La métrica más valiosa para el equipo es el **Defect Escape Rate**: si muchos bugs llegan a producción, la estrategia de pruebas en staging es insuficiente.
