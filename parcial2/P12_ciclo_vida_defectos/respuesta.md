
## Pregunta [12]: [Ciclo de vida defectos]

### Estudiante
- **Nombre completo**: [Marlon Garcia]

---

### Respuesta sin IA

# P.12 — Ciclo de vida de defecto aplicado a OpenLib Market

El defecto reportado indica que el botón “Agregar al carrito” no responde cuando el usuario ya tiene 5 ítems en el carrito e intenta agregar un sexto libro. Según las reglas del sistema, el carrito puede tener hasta 10 ítems diferentes, por lo tanto el sexto libro debería agregarse correctamente. El comportamiento actual es incorrecto porque no se agrega el producto y tampoco se muestra un mensaje de error.

| Fase         | Responsable                                        | Acción tomada                                                                                                                                                                                                                                   | Herramienta usada                                             | Estado en Jira            |
| ------------ | -------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------- | ------------------------- |
| Detección    | Tester / QA                                        | El tester encuentra el error al ejecutar pruebas funcionales del carrito. Detecta que al tener 5 libros diferentes, el botón no responde al intentar agregar el sexto.                                                                          | Navegador, ambiente de pruebas, checklist o casos de prueba   | Detectado                 |
| Reporte      | Tester / QA                                        | Se crea el defecto en Jira con resumen, pasos para reproducir, resultado esperado, resultado obtenido, severidad y evidencia si aplica.                                                                                                         | Jira                                                          | Reportado / Open          |
| Asignación   | Líder técnico, Scrum Master o encargado del equipo | Se revisa el defecto, se valida que sea reproducible y se asigna al desarrollador responsable del módulo de carrito o frontend.                                                                                                                 | Jira, tablero del sprint                                      | Asignado                  |
| Diagnóstico  | Desarrollador                                      | El desarrollador reproduce el error y revisa si el problema está en frontend, backend o validación de reglas. Puede encontrar, por ejemplo, una condición incorrecta que bloquea el botón después de 5 ítems, aunque el máximo real sea 10.     | IDE, logs, consola del navegador, debugger, pruebas unitarias | En análisis / In Progress |
| Corrección   | Desarrollador                                      | Se corrige la lógica del botón o del servicio de carrito para permitir agregar hasta 10 ítems diferentes. También se puede agregar retroalimentación visual en caso de error. Se actualizan o crean pruebas para cubrir el caso del sexto ítem. | IDE, Git, pruebas unitarias, pruebas de integración           | Corregido / Ready for QA  |
| Verificación | Tester / QA                                        | El tester vuelve a ejecutar los pasos originales. Verifica que al tener 5 libros, el sexto se agregue correctamente y el contador se actualice. También prueba casos borde: carrito con 10 ítems e intento de agregar el ítem 11.               | Jira, navegador, ambiente QA, casos de prueba                 | Verificado                |
| Cierre       | Tester / QA o líder del equipo                     | Si el defecto fue corregido correctamente y no aparecen errores relacionados, se cierra el ticket. Si el error persiste, se devuelve al desarrollador.                                                                                          | Jira                                                          | Cerrado                   |

## Aplicación concreta del defecto

El defecto inicia cuando QA detecta que el botón no responde al intentar agregar el sexto libro. Como la regla permite hasta 10 ítems diferentes, el comportamiento esperado es que el sexto libro se agregue normalmente.

Durante el diagnóstico, el desarrollador debería revisar principalmente:

* La validación del límite de ítems en el frontend.
* La validación del límite de ítems en el backend.
* La comunicación entre el botón y el servicio de carrito.
* La consola del navegador para ver si hay errores JavaScript.
* La respuesta de la API al intentar agregar el sexto libro.

Una causa probable podría ser que en algún punto del código se haya dejado una condición incorrecta como:

```java
if (items.size() >= 5) {
    bloquearBoton();
}
```

cuando realmente debería respetarse el máximo de 10 ítems diferentes.

La corrección debe asegurar que:

* El sexto libro se pueda agregar.
* El contador del carrito se actualice.
* El botón siga funcionando mientras haya menos de 10 ítems diferentes.
* Al llegar a 10 ítems, el sistema bloquee correctamente el ítem 11 o muestre un mensaje claro.
* No se deje al usuario sin retroalimentación visual.

## Conclusión

El ciclo de vida del defecto permite gestionar el error de forma ordenada desde que se detecta hasta que se cierra. En este caso, el defecto debe pasar por detección, reporte, asignación, diagnóstico, corrección, verificación y cierre.

La solución no solo debe corregir que el sexto libro se pueda agregar, sino también garantizar que la regla real del negocio se cumpla: mínimo 1 ítem para checkout y máximo 10 ítems diferentes por carrito.
