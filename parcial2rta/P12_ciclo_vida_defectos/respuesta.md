# Plantilla de entrega — Parcial 2

---

## Pregunta 12: Explicar y justificar decisiones en el ciclo de vida de un defecto

### Estudiante
- **Nombre completo**: Samuel Iregui

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Antigravity |
| **Modelo específico** | Gemini 3.5 Flash |
| **¿Por qué elegiste este LLM?** | Elegi este modelo por comodidad propia y debido a que tengo pago gemini pro y ya tengo todo bien organizado para los diferentes trabajos |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```text
Actúa como un QA Lead y Project Manager experto en metodologías ágiles. En el proyecto OpenLib Market, un tester acaba de registrar en Jira el siguiente defecto:
- Resumen: El botón "Agregar al carrito" no responde cuando el usuario tiene más de 5 ítems en el carrito.
- Pasos para reproducir: (1) Iniciar sesión como comprador, (2) Agregar 5 libros diferentes al carrito, (3) Intentar agregar un sexto libro desde la página de detalle del producto.
- Resultado esperado: El libro se agrega al carrito y el contador se actualiza.
- Resultado obtenido: El botón no produce ninguna acción. No hay mensaje de error ni retroalimentación visual.

Necesito que expliques el Ciclo de Vida del Defecto en Jira detalladamente. Para cada una de las siguientes 7 fases (Detección, Reporte, Asignación, Diagnóstico, Corrección, Verificación y Cierre), especifica:
- ¿Quién es el responsable?
- ¿Qué acción exacta se toma?
- ¿Qué herramienta se utiliza?
- ¿Cuál es el estado del ticket en Jira en esa fase (ej. Open, In Progress, Resolved, Closed, Reopened)?
- Identifica la posible causa raíz del defecto (analiza qué podría estar fallando en la lógica de negocio).

Formato de salida esperado: Documento Markdown con las fases detalladas.
```

---

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

A continuación se detalla el ciclo de vida de este defecto en Jira:

#### 1. Detección
*   **Responsable**: Tester / QA Engineer.
*   **Acción**: El tester ejecuta una prueba exploratoria en el catálogo de OpenLib Market y detecta que tras agregar 5 libros diferentes, el botón de compra deja de funcionar sin dar alertas.
*   **Herramientas**: Aplicación frontend JavaFX, consola de logs del cliente.
*   **Estado en Jira**: N/A (Aún no registrado).

#### 2. Reporte
*   **Responsable**: Tester / QA Engineer.
*   **Acción**: Crea un ticket en Jira detallando el resumen, pasos para reproducir, resultados esperados y obtenidos, adjuntando capturas o logs de consola. Define la severidad como **Alta** (bloquea la compra) y la prioridad como **Alta**.
*   **Herramientas**: Jira.
*   **Estado en Jira**: `NEW` / `OPEN`.

#### 3. Asignación
*   **Responsable**: QA Lead / Tech Lead / Product Owner.
*   **Acción**: Revisa el reporte del bug, descarta que sea un duplicado, y lo asigna al desarrollador responsable del módulo del carrito de compras.
*   **Herramientas**: Jira.
*   **Estado en Jira**: `ASSIGNED` / `TO DO`.

#### 4. Diagnóstico
*   **Responsable**: Desarrollador (Software Engineer).
*   **Acción**: Reproduce el error localmente. Inspecciona el código del backend (`CarritoService`) y el controlador del frontend. 
*   **Posible Causa Raíz**: 
    *   *Backend*: La constante `MAX_ITEMS` en `CarritoService` se configuró erróneamente en `5` en lugar de los `10` especificados por el negocio, o el backend lanza un `IllegalStateException` no controlado cuando se intenta agregar un 6° ítem.
    *   *Frontend*: El controlador del botón JavaFX no tiene un bloque `try-catch` para capturar la excepción lanzada por el backend. Por ende, la UI falla silenciosamente y el botón deja de responder.
*   **Herramientas**: IDE (IntelliJ), Depurador (Debugger), Jira.
*   **Estado en Jira**: `IN PROGRESS`.

#### 5. Corrección
*   **Responsable**: Desarrollador (Software Engineer).
*   **Acción**: Corrige la constante en `CarritoService` y agrega control de excepciones en el controlador JavaFX para mostrar una ventana de alerta al usuario si el carrito se llena. Escribe una prueba unitaria de regresión, hace commit y sube el Pull Request.
*   **Herramientas**: IDE, Git, GitHub.
*   **Estado en Jira**: `IN REVIEW` / `RESOLVED`.

#### 6. Verificación
*   **Responsable**: Tester / QA Engineer (diferente al desarrollador).
*   **Acción**: Despliega la rama con el fix en el entorno de pruebas (staging), repite los pasos del reporte original y valida que ahora sí se puedan agregar hasta 10 ítems. También valida que si se excede el límite real (10), el sistema muestre un mensaje descriptivo en lugar de congelar la pantalla.
*   **Herramientas**: Entorno de pruebas (Staging), Jira.
*   **Estado en Jira**: `UNDER TEST` (si pasa $\rightarrow$ `VERIFIED`, si falla $\rightarrow$ `REOPENED` volviendo a asignarse al desarrollador).

#### 7. Cierre
*   **Responsable**: QA Lead / Product Owner.
*   **Acción**: Se confirma la solución del defecto, se fusiona el código a la rama principal (`develop`) y se cierra formalmente el ticket.
*   **Herramientas**: Jira.
*   **Estado en Jira**: `CLOSED` / `DONE`.

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

El prompt proporciona un reporte de bug estructurado con toda la información necesaria (pasos, resultado esperado y obtenido) y exige desglosar las 7 fases del ciclo definiendo responsabilidades, acciones, herramientas y estados de Jira de forma clara.

#### 2. ¿Qué se puede mejorar?

El prompt pudo haber indagado sobre el manejo de excepciones y la severidad vs prioridad en Jira, para obligar al modelo a diferenciar estos dos conceptos (por ejemplo, el bug tiene alta severidad porque rompe la funcionalidad del carrito, pero su prioridad depende de si está bloqueando el release inmediato).

#### 3. Respuesta final

En conclusión, el LLM mapea de forma correcta el ciclo de vida de un defecto. Las responsabilidades están bien asignadas y los estados del flujo de Jira son lógicos.

**Aspectos clave de la causa raíz y omisiones del LLM:**
1.  **Error en el Frontend (Silencio de Fallo)**: La razón por la que "el botón no responde y no da retroalimentación" es un fallo de diseño en la comunicación entre capas. El controlador de la UI de JavaFX del frontend llama al servicio de backend, pero **no captura la excepción lanzada** (o el servicio REST devuelve un HTTP 500 que el frontend ignora). El código del botón debería tener un bloque `try-catch` que muestre una ventana de alerta o texto informativo tipo "Has alcanzado el límite máximo de libros en el carrito", en lugar de fallar silenciosamente.
2.  **Severidad vs Prioridad en Jira**: Durante la fase de Reporte y Asignación, es crítico entender la diferencia. La **Severidad** es **Alta** porque la funcionalidad principal (comprar) está rota para ciertos casos. Sin embargo, la **Prioridad** en Jira la decide el Product Owner/Tech Lead basándose en el negocio. Si el release 1 se lanza mañana y solo el 1% de los usuarios tiene más de 5 ítems, se podría catalogar la prioridad como Media y resolverlo en un hotfix posterior para no retrasar el release.
3.  **Omisión en la fase de Verificación**: El LLM describió la verificación solo como "repetir los pasos manuales". En ingeniería de software moderna, la verificación también debe incluir la **automatización de la prueba de regresión** en la pipeline de CI/CD para asegurar que el límite de ítems no se vuelva a romper en futuros releases.
