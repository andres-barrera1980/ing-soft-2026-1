# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [12]: [ciclo_vida_defectos]

### Estudiante
- **Nombre completo**: Boris Nicolas Valbuena Gueirsman

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Gemini |
| **Modelo específico** | Gemini 3.5 Flash |
| **¿Por qué elegiste este LLM?** | Es la que más usé durante el curso, por lo cual es la misma que usaré en el parcial. |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```
Actúa como un líder QA Senior con experiencia en Jira, gestión de defectos y procesos de aseguramiento de calidad.

Analiza el siguiente defecto reportado en OpenLib Market:

Resumen: El botón “Agregar al carrito” no responde cuando el usuario tiene más de 5 ítems en el carrito.

Pasos para reproducir:

1. Iniciar sesión como comprador.
2. Agregar 5 libros diferentes al carrito.
3. Intentar agregar un sexto libro desde la página de detalle.

Resultado esperado:
El libro se agrega al carrito y el contador se actualiza.

Resultado obtenido:
El botón no produce ninguna acción. No hay mensaje de error ni retroalimentación visual.

Explica el ciclo de vida completo del defecto utilizando las fases:

1. Detección
2. Reporte
3. Asignación
4. Diagnóstico
5. Corrección
6. Verificación
7. Cierre

Para cada fase indica:

* Responsable
* Acción realizada
* Herramienta utilizada
* Estado del defecto en Jira
* Resultado esperado de la fase

Finalmente indica una posible causa raíz y un flujo completo de estados dentro de Jira.
```

---

### Respuesta del LLM

> ¡Hola! Como Líder QA Senior de OpenLib Market, me alegra ver un reporte tan bien estructurado en sus pasos iniciales. Este es un "bug" crítico, ya que impacta directamente en la conversión y los ingresos del negocio (un stopper para que los usuarios sigan gastando dinero).

Vamos a desglosar el ciclo de vida completo de este defecto para que el equipo lo gestione de forma impecable en Jira.

---

## Ciclo de Vida del Defecto: Análisis Fase a Fase

### 1. Detección

* **Responsable:** Ingeniero de QA (o Tester).
* **Acción realizada:** Ejecución de pruebas funcionales (exploratorias o basadas en casos de prueba de regresión). Se detecta que el botón "Agregar al carrito" queda sordo/inactivo al intentar añadir el sexto ítem. Se abren las herramientas de desarrollador del navegador (DevTools) para capturar logs de consola y red.
* **Herramienta utilizada:** Navegador Web (Chrome/Firefox), DevTools (Consola y Network).
* **Estado en Jira:** No aplica (Aún no se ha creado el ticket).
* **Resultado esperado:** Evidencia sólida recolectada (capturas de pantalla, grabación de pantalla y logs de error si los hay).

### 2. Reporte

* **Responsable:** Ingeniero de QA.
* **Acción realizada:** Creación del ticket en Jira detallando el resumen, pasos para reproducir, resultado esperado, resultado obtenido, severidad (Alta/Critical) y prioridad (Alta). Se adjuntan las evidencias capturadas en la fase anterior.
* **Herramienta utilizada:** Jira Software.
* **Estado en Jira:** `Backlog` o `To Do` (en el tablero de bugs).
* **Resultado esperado:** Ticket de Jira creado (`OLM-1234`) con toda la información necesaria para que cualquier desarrollador pueda replicarlo sin ambigüedades.

### 3. Asignación

* **Responsable:** Líder de QA / Scrum Master / Product Owner (en el Daily o sesión de Triage).
* **Acción realizada:** Revisión de la severidad del bug. Dado que afecta las ventas, se prioriza para el Sprint actual y se asigna al desarrollador Backend o Frontend responsable del módulo del carrito (para este ejemplo, asumamos que se asigna a un Dev Fullstack).
* **Herramienta utilizada:** Jira Software (Tablero Kanban/Sprint).
* **Estado en Jira:** `Selected for Development` o `In Progress` (cuando el Dev lo toma).
* **Resultado esperado:** El ticket tiene un dueño ("Assignee") y una prioridad clara frente a otras tareas.

### 4. Diagnóstico

* **Responsable:** Desarrollador Asignado.
* **Acción realizada:** El desarrollador replica el error en su entorno local usando los pasos del ticket. Revisa el código del componente del botón y las peticiones a la API del carrito para entender por qué falla.
* **Herramienta utilizada:** IDE (VS Code / IntelliJ), Postman, Git.
* **Estado en Jira:** `In Progress`.
* **Resultado esperado:** Identificación exacta de la línea de código, configuración o endpoint que está causando el bloqueo.

### 5. Corrección

* **Responsable:** Desarrollador Asignado.
* **Acción realizada:** Modificación del código para solucionar el problema. El desarrollador escribe pruebas unitarias para asegurar que no vuelva a ocurrir, realiza un "Code Review" (Pull Request) y despliega el arreglo en el entorno de pruebas (Staging/QA).
* **Herramienta utilizada:** Git/GitHub/Bitbucket, CI/CD pipelines (Jenkins/GitHub Actions).
* **Estado en Jira:** `In Review` / `Ready for QA`.
* **Resultado esperado:** Una nueva build/versión desplegada en el entorno de pruebas que teóricamente soluciona el fallo.

### 6. Verificación

* **Responsable:** Ingeniero de QA (idealmente el mismo que lo reportó).
* **Acción realizada:** Descarga la nueva versión en el entorno de QA. Sigue estrictamente los pasos de reproducción. Además, realiza pruebas de regresión (por ejemplo, verificar si se pueden agregar 4, 5, 6 o 10 libros, y si el precio total se calcula bien).
* **Herramienta utilizada:** Entorno de QA, Jira Software.
* **Estado en Jira:** `In QA` / `Verifying`.
* **Resultado esperado:** El sexto libro se agrega correctamente, el contador sube a "6" y no se rompió ninguna otra funcionalidad del carrito.

### 7. Cierre

* **Responsable:** Ingeniero de QA / Líder de QA.
* **Acción realizada:** Al confirmar que el bug está resuelto con éxito, se añade un comentario de cierre en Jira agradeciendo al equipo y se procede a cerrar el ticket de manera definitiva.
* **Herramienta utilizada:** Jira Software.
* **Estado en Jira:** `Done` o `Closed`.
* **Resultado esperado:** El ticket cambia a estado final. El ciclo de vida de este defecto ha concluido.

---

## Posible Causa Raíz (Root Cause Analysis)

Analizando el comportamiento ("el botón no produce ninguna acción ni da error"), la causa raíz más probable apunta a una **validación rígida en el Frontend o una restricción rígida (hardcoded) en el estado del componente**:

> **Hipótesis técnica:** El desarrollador anterior implementó un límite máximo de 5 ítems por regla de negocio antigua, o usando una estructura de control mal calculada en JavaScript (`if (cart.length >= 5) return;`). Al cumplirse la condición, el código ejecuta un `return` temprano que "congela" la acción de la UI, omitiendo deliberadamente la llamada a la API y olvidando lanzar un mensaje de alerta o "toast" al usuario que explique el límite.

---

## Flujo Completo de Estados en Jira (Workflow)

Para este flujo de trabajo estándar de QA, el mapa de estados dentro de Jira se estructuraría de la siguiente manera:

```text
[Backlog / New] 
       │
       ▼
[Selected for Development]
       │
       ▼
[In Progress] (El Dev está diagnosticando/corrigiendo)
       │
       ▼
[In Review / PR] (Revisión de código por pares)
       │
       ▼
[Ready for QA] (Desplegado en el entorno de pruebas)
       │
       ▼
[In QA / Testing] ──(Si falla la verificación)──► [Reopened] ──► (Vuelve a In Progress)
       │
       └─(Si pasa la verificación)
       │
       ▼
[Done / Closed]

```

Como líder de QA, te sugiero que mantengamos una comunicación estrecha con el equipo de producto para validar si esos "5 ítems" eran un límite real del negocio mal comunicado, o si efectivamente se trata de un bug técnico puro. ¡Buen trabajo detectándolo a tiempo!
```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

[Evalúa tu propio prompt, no la respuesta del LLM. ¿El contexto fue suficiente? ¿Las restricciones fueron claras? ¿El formato de salida que pediste ayudó a obtener una buena respuesta? ¿Qué parte de tu prompt fue más efectiva? Sé específico: menciona fragmentos concretos de tu prompt que funcionaron bien.]


#### 2. ¿Qué se puede mejorar?

[¿Qué le faltó a tu prompt? ¿Qué harías diferente si pudieras reformularlo? ¿El LLM entendió mal algo por falta de claridad en tu prompt? ¿La respuesta tiene errores u omisiones? ¿Qué no cubrió el LLM que tú sí sabes por lo visto en clase?]


#### 3. Respuesta final

[Escribe tu respuesta definitiva a la pregunta del parcial, integrando lo que aprendiste del LLM pero yendo más allá. Corrige errores, llena omisiones, conecta con conceptos vistos en clase. Esta es tu respuesta: demuestra que tú dominas el tema.]
