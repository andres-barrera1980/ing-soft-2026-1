## P12_ciclo_vida_defectos

### Estudiante
- **Nombre completo**: [Alejandro Macias Barrios]

### LLM utilizado
| Campo | Valor |
|---|---|
| **Nombre del LLM** | [Gemini] |
| **Modelo específico** | [Gemini Flash] |
| **¿Por qué elegiste este LLM?** | [Aunque la LLM que mas me gusta es Claude pues ya se me acabaron los creditos y entre todas las demas decidí usar esta ya que experimentalmente también es de las que mas me ha funcionado, además es la que usa Antigravity y la hemos usado bastante en clase] |

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**
[Imagina que eres un estudiante de Ingeniería de sistemas de la Pontificia Universidad Javeriana cursando la materia de Fundamentos de Ingeniería de Software. El profesor te ha hecho la siguiente pregunta y debes responderla de la mejor manera posible.
Durante las pruebas de OpenLib Market, un tester reporta el siguiente defecto en Jira:
El botón "Agregar al carrito" no responde cuando el usuario tiene más de 5 ítems en el carrito.
**Pasos para reproducir**: (1) Iniciar sesión como comprador, (2) Agregar 5 libros diferentes al carrito, (3) Intentar agregar un sexto libro desde la página de detalle del producto.
> **Resultado esperado**: El libro se agrega al carrito y el contador se actualiza.
> **Resultado obtenido**: El botón no produce ninguna acción. No hay mensaje de error ni retroalimentación visual.
**Tarea**: Gestiona este defecto aplicando el ciclo de vida de defectos visto en clase.]

### Respuesta del LLM
[1. Fase de Registro e Inicialización (Estado: NEW / ABIERTO)
El tester ya hizo la mitad del trabajo al documentarlo con el formato correcto en Jira. Como Product Owner / Líder Técnico del equipo de OpenLib Market, lo primero que hago es revisar el reporte y asignarle los metadatos críticos para el Sprint:
Severidad: ALTA. El defecto bloquea una funcionalidad core del negocio (la compra de más de 5 artículos). No se cae el servidor, pero afecta directamente el flujo de conversión.
Prioridad: ALTA. Debemos resolverlo en el Sprint actual porque frena las pruebas de volumen y la validación del carrito en el primer release.
Asignación: Se lo asigno a un Developer del equipo de Backend o Frontend (dependiendo de dónde estimemos que esté el fallo) para que inicie la investigación. El estado en Jira cambia a ASSIGNED / EN DESARROLLO.

2. Fase de Análisis y Diagnóstico (Estado: EN INVESTIGACIÓN)
El desarrollador asignado toma el caso e intenta reproducir el defecto siguiendo los pasos exactos que dejó el tester. Abre la consola del navegador y los logs del servidor para ver qué está pasando por debajo:
Hipótesis 1 (Frontend): Puede ser que el botón tenga una validación quemada en código (hardcoded) que deshabilite el evento de click cuando el contador de la interfaz llega a 5.
Hipótesis 2 (Backend/BD): El endpoint POST /cart/items está arrojando un error interno (quizás un 400 Bad Request o 500 Internal Server Error) debido a una restricción en la base de datos o en la entidad del carrito que limita la colección a 5 ítems, y el Frontend no está manejando la excepción (por eso no hay mensaje de error).

3. Fase de Resolución (Estado: RESUELTO / FIXED)
Una vez identificado el origen del problema (supongamos que era una regla de negocio mal implementada en el backend que limitaba el arreglo del carrito a un máximo de 5 elementos), el desarrollador realiza los siguientes pasos:
Modifica el código para permitir que la estructura soporte más elementos o maneje el límite según los requerimientos del negocio.
Agrega un manejo de excepciones adecuado para que, si ocurriese otro error, el usuario reciba retroalimentación visual (una alerta o un mensaje).
Despliega el cambio en el entorno de pruebas (Staging/QA).
En Jira, el desarrollador cambia el estado a RESOLVED y deja un comentario técnico: "Se removió la restricción de tamaño en la colección del carrito en el backend y se añadió manejo de errores en el frontend. Listo para pruebas".

4. Fase de Verificación (Estado: EN RE-TESTING)
El ticket regresa al tester original o al equipo de QA. Aquí se ejecutan dos acciones fundamentales:
Re-testing: El tester repite exactamente los mismos 3 pasos del reporte inicial con un usuario que ya tiene 5 libros e intenta agregar el sexto. Verifica si el resultado esperado se cumple (el libro se agrega y el contador sube a 6).
Pruebas de Regresión: El tester verifica que este arreglo no haya roto otras cosas relacionadas. Por ejemplo, valida que todavía se puedan eliminar libros del carrito, que el precio total se calcule bien con 6 o más libros y que el botón de "Proceder al pago" siga funcionando.

5. Cierre del Defecto (Estado: CERRADO / CLOSED)
Dependiendo del resultado del paso anterior, el ciclo toma uno de dos caminos:
Camino A (Fracaso): Si el botón sigue sin responder o si ahora se rompe el cálculo del precio, el defecto se REAPERTURA (REOPENED), se documenta el nuevo comportamiento y regresa al desarrollador.
Camino B (Éxito): Si todo funciona perfectamente y pasó la regresión, el tester cambia el estado en Jira a CLOSED (CERRADO). El bicho está oficialmente muerto y la funcionalidad queda lista para ser incluida en el paquete del release.]

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?
[El hecho de detallar que "El botón no produce ninguna acción. No hay mensaje de error ni retroalimentación visual" fue un gran acierto. Esto obligó a la IA a no solo hablar de "arreglar el código", sino a diagnosticar un problema de arquitectura común: la falta de comunicación/manejo de excepciones entre el Frontend y el Backend.]

#### 2. ¿Qué se puede mejorar?
[Exigir métricas de priorización formal: En lugar de dejar la prioridad a la interpretación de la IA, el prompt pudo haber exigido el uso de una matriz formal de Severidad vs. Prioridad. Por ejemplo: "Clasifica el defecto usando una matriz de impacto y justifica si bloquea o no el despliegue".]

#### 3. Respuesta final
[El ciclo de vida de un defecto en OpenLib Market sigue un proceso estructurado que garantiza que los errores sean identificados, corregidos, verificados y cerrados de manera controlada. Todo comienza con el registro del incidente por parte del tester, quien documenta el problema y permite que el equipo evalúe su severidad, prioridad e impacto sobre el negocio.Después, el desarrollador analiza y reproduce el defecto para identificar su causa raíz, ya sea en el frontend, backend o base de datos. Una vez encontrado el origen del problema, se implementa la corrección, se realizan los ajustes necesarios en el código y se despliega la solución en un entorno de pruebas. El defecto pasa entonces al estado de resuelto. La siguiente etapa corresponde a la verificación por parte del equipo de QA, donde se ejecutan pruebas de re-testing para comprobar que el error original desapareció y pruebas de regresión para asegurar que la corrección no haya generado nuevos problemas en funcionalidades relacionadas. Este paso es fundamental porque una solución incorrecta puede introducir defectos adicionales en el sistema.
Por último, si todas las validaciones son exitosas, el defecto se cierra y la funcionalidad queda lista para formar parte del release. En caso contrario, el ticket se reabre y vuelve al equipo de desarrollo para una nueva corrección.]