# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [12]: [ciclo_vida_defectos]

### Estudiante
- **Nombre completo**: [Diego Alejandro Torres Barrgan]

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [Claude] |
| **Modelo específico** | [Ej: Claude Sonnet 4.6/bajo]|
| **¿Por qué elegiste este LLM?** | [Es el modelo que actualmente mas uso y en el que estoy confiando, ademas que es muy poderoso para generar codigo y para explicarlo ] |

---

### Prompt utilizado

[Actua como un ingeniero QA Senior. En OpenLib Market un tester reporto este defecto en Jira:
Resumen: El boton "Agregar al carrito" no responde cuando el usuario tiene mas de 5 items en el carrito.
Pasos: (1) Iniciar sesion como comprador, (2) Agregar 5 libros diferentes, (3) Intentar agregar un sexto libro.
Resultado esperado: El libro se agrega y el contador se actualiza.
Resultado obtenido: El boton no hace nada, sin mensaje de error ni feedback visual.
Explica el ciclo de vida completo de este defecto: deteccion, reporte, asignacion, diagnostico, correccion, verificacion y cierre. Para cada fase indica: quien es responsable, que accion se toma, que herramienta se usa y cual seria el siguiente estado del ticket en Jira. Se especifico con la causa raiz probable y los posibles estados del defecto en Jira.]
```

---

### Respuesta del LLM

1. Deteccion

Responsable: Tester
Accion: Encuentra el bug durante pruebas manuales del flujo de compra
Herramienta: Ambiente de pruebas, JavaFX
Estado en Jira: ABIERTO

2. Reporte

Responsable: Tester
Accion: Crea el ticket en Jira con pasos para reproducir, resultado esperado y obtenido
Herramienta: Jira
Estado en Jira: ABIERTO → EN REVISION

3. Asignacion

Responsable: Tech Lead o Project Manager
Accion: Revisa el ticket, confirma que es reproducible y lo asigna al desarrollador del modulo de carrito
Herramienta: Jira
Estado en Jira: EN REVISION → ASIGNADO

4. Diagnostico

Responsable: Desarrollador
Accion: Investiga la causa raiz. El problema probable es que CarritoService lanza una excepcion cuando hay mas de 5 items pero el frontend no la maneja, entonces el boton simplemente no responde sin mostrar ningun mensaje al usuario
Herramienta: IDE, logs del sistema
Estado en Jira: ASIGNADO → EN PROGRESO

5. Correccion

Responsable: Desarrollador
Accion: Corrige el bug. Puede ser en el frontend para mostrar un mensaje de error, o en el backend para manejar correctamente el limite de items. Hace un PR con la correccion
Herramienta: IDE, Git
Estado en Jira: EN PROGRESO → EN VERIFICACION

6. Verificacion

Responsable: Tester
Accion: Reproduce los pasos originales del bug para confirmar que esta corregido. Tambien hace pruebas de regresion para verificar que nada mas se rompio
Herramienta: Ambiente de pruebas, Jira
Estado en Jira: EN VERIFICACION → RESUELTO o REABIERTO si el bug persiste

7. Cierre

Responsable: Tester o Tech Lead
Accion: Confirma que el bug esta corregido y cierra el ticket
Herramienta: Jira
Estado en Jira: RESUELTO → CERRADO
```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

Pegar el caso concreto del defecto dentro del prompt fue lo que mas ayudo porque el LLM no invento un ejemplo generico sino que aplico cada fase al bug real de OpenLib Market. Pedir explicitamente "causa raiz probable" hizo que el LLM identificara el problema entre el backend y el frontend, que es la parte mas interesante del diagnostico. Pedir el estado de Jira en cada fase tambien fue util porque la respuesta salio estructurada y facil de seguir.


#### 2. ¿Qué se puede mejorar?

El LLM no menciono que un defecto puede ser rechazado, es decir que en la fase de revision el Tech Lead puede decidir que no es un bug sino un comportamiento esperado y cerrarlo como NO ES UN BUG. Ese es un estado importante del ciclo de vida que quedo por fuera. Tambien quedo superficial la fase de verificacion, no explico que pasa si el tester no puede reproducir la correccion y tiene que reabrir el ticket con evidencia adicional.


#### 3. Respuesta final

El ciclo de vida de este defecto aplicado al caso de OpenLib Market seria:
Deteccion — el tester encuentra que el boton no responde al intentar agregar el sexto libro. Lo reporta en Jira con los pasos exactos para reproducirlo. Estado: ABIERTO
Reporte — el ticket queda documentado con resultado esperado, resultado obtenido y pasos. Sin esto el desarrollador no puede reproducirlo. Estado: EN REVISION
Asignacion — el Tech Lead revisa, confirma que es reproducible y lo asigna al desarrollador del modulo de carrito. Aca tambien podria cerrarse como NO ES UN BUG si el limite de 5 items fuera intencional, pero en este caso el resultado esperado dice que si deberia agregarse. Estado: ASIGNADO
Diagnostico — el desarrollador investiga la causa raiz. Lo mas probable es que CarritoService este lanzando una excepcion cuando supera el limite pero el frontend no la esta capturando, entonces el boton simplemente no hace nada sin mostrar ningun mensaje al usuario. Estado: EN PROGRESO
Correccion — se corrige el bug y se hace un PR. La correccion puede ser en el frontend para mostrar el error, o en el backend para manejar bien el limite. Estado: EN VERIFICACION
Verificacion — el tester reproduce los pasos originales para confirmar que esta corregido y hace pruebas de regresion. Si el bug persiste el ticket se REABRE con evidencia adicional, si esta corregido pasa a RESUELTO
Cierre — el Tech Lead o tester confirma y cierra el ticket. Estado: CERRADO
Lo que el LLM no cubrio bien fue el estado RECHAZADO, que es cuando en la fase de revision se determina que el comportamiento es intencional y no un bug. Ese estado es parte del ciclo y es importante porque no todo ticket abierto termina en correccion.
