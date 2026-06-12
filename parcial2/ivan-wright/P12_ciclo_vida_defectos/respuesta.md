# Plantilla de entrega — Parcial 2

---

## Pregunta P12: Ciclo de vida de defectos

### Estudiante
- **Nombre completo**: Ivan Wright

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Gemini |
| **Modelo especifico** | Gemini 3.1 Pro |
| **¿Por que elegiste este LLM?** | Lo elegi porque organiza muy bien los pasos secuenciales y conoce perfectamente la nomenclatura de estados que se maneja en metodologias agiles y Jira. |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```text
hola gemini, actua como un QA Lead en Jira.

[CONTEXTO]
Basicamente en OpenLib Market tenemos este bug reportado por un tester en Jira:
> Resumen: El boton "Agregar al carrito" no responde cuando el usuario tiene mas de 5 items en el carrito.
> Pasos para reproducir: (1) Iniciar sesion como comprador, (2) Agregar 5 libros diferentes al carrito, (3) Intentar agregar un sexto libro desde la pagina de detalle del producto.
> Resultado esperado: El libro se agrega al carrito y el contador se actualiza.
> Resultado obtenido: El boton no produce ninguna accion. No hay mensaje de error ni retroalimentacion visual.

[PROBLEMA]
Primeramente necesito que me expliques el ciclo de vida de este defecto (deteccion, reporte, asignacion, diagnostico, correccion, verificacion, cierre) aplicado a este caso concreto.

[RESTRICCIONES]
Seguidamente para cada fase tienes que indicar obligatoriamente:
- Quien es el responsable.
- Que accion concreta se toma.
- Que herramienta se usa.
- Cual es el estado del defecto en Jira en ese momento.

[FORMATO]
Aca entregame todo en markdown, listando paso a paso cada fase.

[VALIDACION]
Sabre que tu respuesta es util si asignas los roles correctamente y propones una posible causa raiz.
```

---

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```markdown
¡Hola! Como QA Lead, entiendo perfectamente la situación. Aquí te detallo el ciclo de vida de este defecto aplicado al problema del botón "Agregar al carrito" en OpenLib Market.

### 1. Fase de Detección
*   **Responsable:** Tester (QA Analyst).
*   **Acción concreta:** Durante la ejecución de pruebas exploratorias en la UI, el tester nota que al intentar agregar un sexto libro, la aplicación no responde.
*   **Herramienta:** Navegador web (Chrome/Firefox) y la plataforma de OpenLib Market.
*   **Estado en Jira:** Aún no existe en Jira (No creado).

### 2. Fase de Reporte
*   **Responsable:** Tester (QA Analyst).
*   **Acción concreta:** Se redacta el ticket detallando el entorno, los pasos para reproducir, y se adjuntan las capturas de pantalla y los logs de la consola del navegador.
*   **Herramienta:** Jira Software.
*   **Estado en Jira:** `NEW` (Nuevo) o `TO DO` (Por Hacer).

### 3. Fase de Asignación
*   **Responsable:** QA Lead o Scrum Master.
*   **Acción concreta:** Se revisa la prioridad del bug (en este caso Alta, ya que afecta el flujo de compras) y se asigna a un desarrollador Frontend del sprint actual.
*   **Herramienta:** Jira Software.
*   **Estado en Jira:** `ASSIGNED` (Asignado) o `OPEN` (Abierto).

### 4. Fase de Corrección (y Diagnóstico implícito)
*   **Responsable:** Desarrollador Frontend.
*   **Acción concreta:** El desarrollador toma el ticket y se da cuenta de que la variable que controla el límite del carrito en el Frontend está configurada en un máximo de 5 ítems. Modifica el código para aumentar ese límite, hace commit de la solución y envía el código al entorno de pruebas.
*   **Herramienta:** IDE (VS Code), Git y Jira.
*   **Estado en Jira:** Pasa a `IN PROGRESS` (En Progreso) y luego a `RESOLVED` (Resuelto) o `READY FOR TEST` (Listo para probar).

### 5. Fase de Verificación
*   **Responsable:** Tester (QA Analyst).
*   **Acción concreta:** El tester original toma el ticket resuelto. Entra al entorno de pruebas, agrega 5 libros y luego intenta agregar el sexto libro. Verifica que el sistema sí lo permite y el contador se actualiza a 6.
*   **Herramienta:** Navegador web y entorno de Staging/QA.
*   **Estado en Jira:** `IN TESTING` (En Pruebas).

### 6. Fase de Cierre
*   **Responsable:** Tester (QA Analyst) o QA Lead.
*   **Acción concreta:** Como la prueba fue exitosa, el QA aprueba el ticket y marca el defecto como solucionado oficialmente.
*   **Herramienta:** Jira Software.
*   **Estado en Jira:** `CLOSED` (Cerrado) o `DONE` (Terminado).
```

---

### Analisis critico de la respuesta

#### 1. ¿Que hizo bien el prompt?

Primeramente el prompt fue super claro al darle todo el formato del bug de Jira (resumen, pasos, resultados). Ahi logre que la IA no me diera teoria generica sino que lo aplicara directo a OpenLib Market. Seguidamente exigirle las restricciones de responsables, acciones y estados en Jira hizo que la respuesta fuera una guia perfecta paso a paso y muy facil de leer.


#### 2. ¿Que se puede mejorar?

Basicamente le di mucho contexto visual del bug pero no le aclare cual era la verdadera regla de negocio del sistema. Aca el LLM asumio equivocadamente que el limite maximo del carrito era 5 y se invento una causa raiz basada en esa mentira, en lugar de saber que el limite real es 10. Tambien me falto exigirle que separara estrictamente el Diagnostico de la Correccion.


#### 3. Respuesta final

Primeramente el LLM asigno muy bien los responsables y el flujo basico de estados en Jira (`NEW` -> `ASSIGNED` -> `IN PROGRESS` -> `RESOLVED` -> `CLOSED`). Las diferencias entre estados y herramientas usadas quedaron claras. 

Sin embargo, el LLM fallo terriblemente en dos conceptos que vimos en clase. Seguidamente, el LLM trato de manera super superficial la fase de **Diagnostico**, fusionandola con la fase de Correccion. *Como sabemos, el diagnostico es la etapa mas critica y demorada donde el desarrollador aisla el error leyendo logs o usando el debugger, antes de escribir una sola linea de codigo para solucionarlo. Saltarse el diagnostico es un error grave de concepto.*

Ahi es donde entra el segundo error: la causa raiz. El LLM dijo que el desarrollador "se dio cuenta que el limite estaba en 5". Aca el LLM se equivoco feo. *Segun las reglas de negocio de OpenLib Market (vista en la Pregunta 8), el carrito tiene un limite maximo real de 10 items. Si el boton dejo de responder al item numero 6, la verdadera causa raiz es un defecto en el Frontend que no esta manejando correctamente la respuesta asincrona del Backend, o un error de validacion prematura en el Javascript de la vista, no un limite quemado de 5*.

Basicamente, el ciclo real debio tener un paso dedicado a diagnosticar por que la UI fallo silenciosamente al llegar al item 6, luego corregir esa logica de JS para que muestre el error adecuado, y pasar a `READY FOR TEST` para que el tester lo valide agregando items hasta llegar a 10 (no a 6) para comprobar que todo funciona.
