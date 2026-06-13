## Pregunta 12

### Estudiante
- **Nombre completo**: Nicolas Silva García

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [**Sin IA — respuesta propia**] |
| **Modelo específico** | [N/A] |
| **¿Por qué elegiste este LLM?** | [Decidi responder sin IA porque es un tema bastante manejable y no requeria de una IA] |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```
[Pega aquí el prompt exacto que enviaste al LLM. 
Incluye TODO el texto, sin editar ni resumir.

Un buen prompt incluye:
- Contexto del proyecto OpenLib Market
- El código o situación específica
- Lo que esperas que el LLM haga
- Restricciones (ej: "usa Java 21", "aplica SOLID")
- Formato de salida esperado (ej: "respuesta en markdown con código Java")]
```

---

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**
[Pega aquí la respuesta COMPLETA del LLM, sin editar, sin resumir.
Incluye TODO el texto, código, explicaciones que generó el LLM.

Si el LLM generó código, asegúrate de que esté correctamente formateado.
Si tuviste que hacer varias iteraciones, pega la MEJOR respuesta obtenida,
pero menciona cuántas iteraciones hiciste.]


---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

No aplica, esta respuesta es mia propia

#### 2. ¿Qué se puede mejorar?

No aplica, esta respuesta es mia propia


#### 3. Respuesta final

1. Fase de Detección

    Responsable: Analista de QA o Tester manual.

    Acción: Durante la ejecución de una prueba exploratoria o un caso de prueba de regresión (evaluando el límite de compras), el tester intenta agregar el sexto ítem y observa la falla silenciosa en la interfaz.

    Herramienta: Navegador Web (Chrome/Firefox) con la consola de desarrollador abierta (DevTools) para capturar si hay un error HTTP 500 o un fallo de JavaScript en el momento del clic.

    Estado en Jira: (Aún no creado, desencadena la fase 2).

2. Fase de Reporte

    Responsable: Analista de QA.

    Acción: Documenta el defecto de forma estructurada. Crea el ticket incluyendo el título, la severidad (Alta, porque impide continuar comprando), los pasos para reproducir (los que mencionas en el enunciado), y adjunta evidencia (capturas de red o logs de la consola donde posiblemente el backend está rechazando la petición pero el frontend no sabe qué hacer).

    Herramienta: Jira Software.

    Estado en Jira: ABIERTO (Open) o TO DO (en el Backlog de defectos).

3. Fase de Asignación (Triage)

    Responsable: Product Owner (PO), Scrum Master, o QA Lead.

    Acción: En la reunión de revisión (Triage) o de planificación (Sprint Planning), se analiza el impacto del defecto. Al ser un bloqueo de ventas, se prioriza y se asigna al desarrollador Full-Stack o al equipo correspondiente para que lo solucione en el sprint actual.

    Herramienta: Tablero Kanban/Scrum en Jira.

    Estado en Jira: ASIGNADO (Assigned) o movido a la columna TO DO del Sprint activo.

4. Fase de Diagnóstico

    Responsable: Desarrollador Asignado.

    Acción: El desarrollador reproduce el error localmente. Analiza el flujo: ¿El backend (Spring Boot) está lanzando una excepción incorrecta por un límite mal configurado (5 en lugar de 10)? ¿O el frontend (React/Angular/JavaFX) está recibiendo el error pero carece de un bloque catch para pintar un Toast/Alerta roja en pantalla? Descubre la raíz del problema.

    Herramienta: IDE (IntelliJ IDEA / VSCode), Postman (para probar el endpoint de agregar directamente), y el depurador (Debugger).

    Estado en Jira: EN PROGRESO (In Progress) / DOING.

5. Fase de Corrección

    Responsable: Desarrollador Asignado.

    Acción: Se implementa la solución a nivel de código. Se corrige la regla límite en CarritoService (si ahí estaba el error de los 5 ítems) y se agrega el manejo de errores en el frontend para que, si falla en el futuro, el usuario reciba retroalimentación visual (ej. "Límite de carrito alcanzado"). Se escriben o actualizan las pruebas unitarias para cubrir este caso y se hace un Push al repositorio.

    Herramienta: IDE, Git, y plataforma de repositorios (GitHub / GitLab / Bitbucket).

    Estado en Jira: LISTO PARA PRUEBAS (Ready for QA) o EN REVISIÓN (In Review / Code Review).

6. Fase de Verificación

    Responsable: Analista de QA (Idealmente quien lo reportó).

    Acción: Se despliega la nueva versión en el entorno de pruebas (Staging/QA). El tester repite exactamente los mismos pasos del reporte original. Verifica dos cosas: primero, que ahora permita agregar el libro 6 (hasta llegar al 10); y segundo, que al intentar agregar el ítem 11, la interfaz muestre el error correspondiente.

    Herramienta: Entorno de pruebas (QA Environment), Navegador Web.

    Estado en Jira:

        Si pasa la prueba: Pasa a estado VERIFICADO (Verified).

        Si falla la prueba: El estado retrocede a REABIERTO (Reopened) o EN PROGRESO, y el ciclo regresa a la fase 4.

7. Fase de Cierre

    Responsable: Product Owner o QA Lead.

    Acción: Se confirma que el defecto ha sido solucionado satisfactoriamente, está verificado en la rama de integración y no causó otros problemas de regresión. Se aprueba para que el código pase al entorno de Producción en el próximo despliegue.

    Herramienta: Jira Software.

    Estado en Jira: CERRADO (Closed) o DONE.
