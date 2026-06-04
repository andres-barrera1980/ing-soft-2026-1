# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta 12: Ciclo de vida de defectos

### Estudiante
- **Nombre completo**: Federico Restrepo

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Gemini |
| **Modelo específico** | Gemini 3.1 Pro |
| **¿Por qué elegiste este LLM?** | Usé Gemini 3.1 Pro en antigravity, ya que ofrece relativamente una buena cantidad de tokens y ofrece un manejo visual de los archivos que me gusta. |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```text
Actúa como un QA Manager y Scrum Master.

Contexto: En OpenLib Market, un tester reportó este defecto en Jira:
"Resumen: El botón 'Agregar al carrito' no responde cuando el usuario tiene más de 5 ítems en el carrito.
Pasos: (1) Iniciar sesión, (2) Agregar 5 libros diferentes, (3) Intentar agregar un sexto libro.
Resultado esperado: El libro se agrega y el contador se actualiza.
Resultado obtenido: El botón no hace nada. No hay mensaje de error ni feedback."

Tarea: Explica cómo se gestionaría este defecto aplicando el ciclo de vida de un defecto estándar. Para CADA UNA de las siguientes fases (Detección, Reporte, Asignación, Diagnóstico, Corrección, Verificación, Cierre), debes indicar claramente:
1. ¿Quién es el responsable de la fase?
2. ¿Qué acción concreta se toma basada en este caso específico de OpenLib Market?
3. ¿Qué herramienta se utiliza?
4. ¿Cuál es el estado del ticket en Jira durante/después de esta fase?

Formato de salida: markdown, usando subtítulos para cada fase. No agregues conclusiones largas.
```

---

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

*(1 iteración)*

---

### Ciclo de vida del defecto: Bug en botón "Agregar al carrito"

#### 1. Detección (Detection)
*   **Responsable:** Tester (QA Analyst).
*   **Acción concreta:** Durante una prueba exploratoria o ejecución de un caso de prueba manual sobre la funcionalidad de compras, el tester realiza los pasos (agrega 5 libros y luego intenta el sexto) y nota que la interfaz de usuario no responde y no cumple con el resultado esperado.
*   **Herramienta usada:** Navegador web (Chrome/Firefox) interactuando con el frontend de OpenLib Market.
*   **Estado en Jira:** Aún no existe el ticket.

#### 2. Reporte (Reporting)
*   **Responsable:** Tester (QA Analyst).
*   **Acción concreta:** El tester documenta formalmente el hallazgo. Redacta el resumen, los pasos para reproducirlo, el comportamiento esperado vs obtenido, y adjunta evidencia (un video grabando la pantalla o un pantallazo de la consola del navegador mostrando posibles errores de JavaScript). Clasifica la severidad (ej. Alta, porque bloquea ventas).
*   **Herramienta usada:** Jira Software (creación de issue tipo "Bug") y herramientas de captura (Snagit/Loom).
*   **Estado en Jira:** `NUEVO` (New) o `ABIERTO` (Open).

#### 3. Asignación (Triage / Assignment)
*   **Responsable:** Product Owner, Scrum Master o QA Lead (en sesión de Triage).
*   **Acción concreta:** Se revisa el backlog de bugs. Se confirma que es un bug válido (no un comportamiento esperado de una regla de negocio mal entendida). Al ver que afecta el frontend pero podría tener origen en el backend (ej. una API fallando silenciosamente), se le asigna a un desarrollador Fullstack del equipo del carrito.
*   **Herramienta usada:** Tablero Kanban o Scrum de Jira.
*   **Estado en Jira:** `ASIGNADO` (Assigned) o `POR HACER` (To Do).

#### 4. Diagnóstico (Diagnosis / Analysis)
*   **Responsable:** Desarrollador Asignado.
*   **Acción concreta:** El desarrollador lee los pasos y los repite localmente. Abre las DevTools del navegador y ve que la llamada a la API POST `/api/carrito` devuelve un error HTTP 500 o 400 por superar un límite de ítems, pero el Frontend (JavaFX/React) no tiene un bloque `catch` que muestre un popup o toast con el mensaje de error al usuario.
*   **Herramienta usada:** IDE (IntelliJ/VSCode), DevTools del navegador, Postman (para probar la API aislada), Logs del servidor (Kibana/Consola).
*   **Estado en Jira:** `EN PROGRESO` (In Progress).

#### 5. Corrección (Fixing / Resolution)
*   **Responsable:** Desarrollador Asignado.
*   **Acción concreta:** Modifica el código del frontend para atrapar correctamente la excepción de la API y mostrar un modal de advertencia ("No puedes tener más de 5 ítems diferentes"). También escribe o actualiza una prueba unitaria/de integración para asegurar que el error se maneje en el futuro. Hace commit y push del código con el ID del bug (ej. `git commit -m "Fix OPENLIB-142: Añadido manejo de error en límite de carrito"`).
*   **Herramienta usada:** Git, IDE (IntelliJ), GitLab/GitHub para la creación del Pull Request.
*   **Estado en Jira:** `RESUELTO` (Resolved) o `LISTO PARA QA` (Ready for QA) [Una vez el código se despliega en el entorno de pruebas].

#### 6. Verificación (Verification / Retesting)
*   **Responsable:** Tester original (QA Analyst).
*   **Acción concreta:** El tester ingresa al entorno de pruebas (Staging/QA). Vuelve a ejecutar los pasos exactos del reporte original: inicia sesión, agrega 5 libros e intenta agregar el sexto. Ahora verifica que el sistema arroje un mensaje de error claro en pantalla en lugar de ignorar el clic.
*   **Herramienta usada:** Navegador web (entorno QA de OpenLib Market), Jira (para actualizar el ticket).
*   **Estado en Jira:** `EN PRUEBAS` (In Testing). Si falla vuelve a `EN PROGRESO`; si pasa, avanza.

#### 7. Cierre (Closure)
*   **Responsable:** Tester (QA Analyst) o QA Manager.
*   **Acción concreta:** Tras confirmar que el defecto está solucionado y que no introdujo regresiones (no rompió otra cosa en el carrito), el tester aprueba el ticket de forma definitiva. El código corregido queda listo para salir en el próximo release a producción.
*   **Herramienta usada:** Jira Software.
*   **Estado en Jira:** `CERRADO` (Closed) o `TERMINADO` (Done).

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?
Pedir los cuatro campos por fase, responsable, acción, herramienta y estado en Jira, forzó respuestas concretas. Lo mejor fue la fase de Diagnóstico: en vez de "el desarrollador analiza el bug" describió exactamente abrir DevTools, ver el HTTP 500 y revisar que el frontend no tiene catch. Eso es útil de verdad.
#### 2. ¿Qué se puede mejorar?
El enunciado dice que el botón falla con más de 5 ítems, pero la regla de OpenLib Market es que el límite son 10 únicos. La respuesta nunca cuestionó eso, un buen QA debería haber preguntado si el bug es real o si el tester tenía mal entendida la regla. También la fase de Cierre quedó muy corta, solo dice que se aprueba el ticket sin mencionar si se actualizó documentación, si el caso se agregó al suite de regresión o si se notificó al Product Owner.
#### 3. Respuesta final
La respuesta sirve bien para entender el ciclo de vida en la práctica. Para la próxima le pediría que detecte la inconsistencia del límite de ítems y que desarrolle más el Cierre con lo que pasa después de cerrar el ticket.