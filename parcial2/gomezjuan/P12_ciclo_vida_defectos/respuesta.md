# Pregunta P12: Ciclo de vida de defectos

### Estudiante
- **Nombre completo**: Juan Camilo Gomez

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Sin IA, aunqeu no le pedi pront, me ayude bastante de ia |
| **Modelo específico** | N/A |
| **¿Por qué elegiste este LLM?** |por el bono

---

### Análisis crítico de la respuesta

#### 3. Respuesta final

## Ciclo de vida del defecto: Botón "Agregar al carrito" con más de 5 ítems

 El boton "Agregar al carrito" no responde cuando el usuario tiene más de 5 ítems en el carrito. No hay mensaje de error ni retroalimentación visual.

---

### Fase 1: Detección

**Responsable:** Tester (QA)  
**Acción:** El tester identifica el comportamiento inesperado durante pruebas del flujo de compra con múltiples ítems.  
**Herramienta:** Entorno de testing (staging), navegador con DevTools para verificar si hay errores en consola.  
**Estado en Jira:** Nuevo 

**Nota:** El defecto es especialmente grave porque **no hay retroalimentación visual** al usuario — el botón simplemente no hace nada. Esto genera confusión sin indicar la causa.

---

### Fase 2: Reporte

**Responsable:** Tester (QA)  
**Acción:** Crear el ticket en Jira con:
- Resumen claro y específico
- Pasos para reproducir (paso a paso, reproducible por cualquier desarrollador)
- Resultado esperado vs resultado obtenido
- Evidencia: screenshot, video de pantalla, logs de consola si hay errores JavaScript
- Severidad sugerida: **Alta** (afecta flujo crítico de compra)
- Prioridad sugerida: **Alta** (impacta directamente la conversión de ventas)

**Herramienta:** Jira — ticket creado con template de bug report  
**Estado en Jira:** Abierto 

---

### Fase 3: Asignación (Triage)

**Responsable:** Tech Lead o Product Manager  
**Acción:** Revisar el ticket, confirmar la severidad/prioridad, y asignar al desarrollador responsable del módulo de carrito.  
**Herramienta:** Jira — campo "Assignee" + Sprint board  
**Estado en Jira:** Asignado (Assigned)

**Posibles causas raíz a investigar:**
- Hay un límite hardcodeado de 5 ítems en el frontend (JavaScript) que bloquea silenciosamente el evento del botón
- El backend rechaza la petición con un 4xx pero el frontend no maneja el error ni muestra mensaje
- La regla de negocio real es un máximo de 10 ítems (como en `CarritoService`) pero el frontend tiene 5 hardcodeado incorrectamente

---

### Fase 4: Diagnóstico

**Responsable:** Desarrollador asignado  
**Acción:**
1. Reproducir el defecto localmente siguiendo los pasos exactos del ticket
2. Revisar el código JavaScript del botón — buscar validaciones de cantidad de ítems
3. Revisar los logs del backend — ¿llega la petición? ¿Retorna error?
4. Comparar la regla de negocio en `CarritoService` (máximo 10) con lo que implementa el frontend
5. Documentar la causa raíz encontrada en el ticket de Jira

**Herramienta:** IDE, DevTools del navegador, logs de servidor, Jira  
**Estado en Jira:** En análisis (In Analysis)

---

### Fase 5: Corrección

**Responsable:** Desarrollador  
**Acción:**
- Si el bug está en el frontend: corregir el límite hardcodeado (de 5 a 10, o consumir el límite desde la API)
- Agregar manejo del error del backend con mensaje claro al usuario: "Tu carrito ya tiene el máximo de ítems permitidos"
- Escribir o actualizar pruebas unitarias y de integración que cubran el caso borde (carrito con 10 ítems)
- Crear PR con la corrección y referencia al ticket de Jira

**Herramienta:** Git, IDE, Jira (actualizar estado), PR review  
**Estado en Jira:** En corrección (In Progress)

---

### Fase 6: Verificación

**Responsable:** Tester (QA) — idealmente diferente al que reportó  
**Acción:**
1. Ejecutar exactamente los pasos del ticket original en el ambiente de staging con la corrección desplegada
2. Verificar que el botón ya no falla silenciosamente
3. Verificar que aparece un mensaje de error claro y accionable para el usuario
4. Ejecutar pruebas de regresión del flujo completo de carrito
5. Si pasa: aprobar. Si falla: reabrir con evidencia

**Herramienta:** Staging environment, Jira, suite de pruebas de regresión  
**Estado en Jira:** En verificacion  → aprobado: Verificado (Verified)

---

### Fase 7: Cierre

**Responsable:** Tech Lead o QA Lead  
**Acción:** Confirmar que la corrección está en producción (o en el release aprobado), documentar la causa raíz para el registro de defectos, y cerrar el ticket.  
**Herramienta:** Jira  
**Estado en Jira:** Cerrado (Closed)
