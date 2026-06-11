## Pregunta [11]: [manual vs auto]

### Estudiante

* **Nombre completo**: [Marlon Garcia]

---

### LLM utilizado

| Campo                           | Valor                                                                                                                                                                                                                                   |
| ------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Nombre del LLM**              | ChatGPT                                                                                                                                                                                                                                 |
| **Modelo específico**           | GPT-5.5 Thinking                                                                                                                                                                                                                        |
| **¿Por qué elegiste este LLM?** | Elegi chatgpt porque ya tiene todo el contexto del parcial, ya que lo he utilizado para varias preguntas, por lo que ya entiende mejor la dinámica de este y los outputs que necesito. |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```text
Estoy trabajando en OpenLib Market, un marketplace de libros, y necesito definir una estrategia de testing para el primer release. Quiero que compares pruebas manuales y pruebas automatizadas.

Incluye mínimo 3 ventajas y 3 desventajas de cada enfoque, tipos de pruebas que aplican a cada uno, criterios prácticos para decidir cuándo automatizar una prueba y cuándo hacerla manual, y una recomendación concreta para OpenLib Market diciendo cuáles pruebas deberían hacerse manualmente y cuáles automatizadas.

Organiza la respuesta en markdown, primero con una tabla comparativa y después con explicaciones breves.
```

---

### Respuesta del LLM

> Iteraciones realizadas: 1.

## Comparación entre pruebas manuales y pruebas automatizadas

| Criterio        | Pruebas manuales                                                            | Pruebas automatizadas                                                           |
| --------------- | --------------------------------------------------------------------------- | ------------------------------------------------------------------------------- |
| Definición      | Son ejecutadas por una persona que interactúa directamente con el sistema.  | Son ejecutadas por scripts o herramientas sin intervención humana directa.      |
| Velocidad       | Más lentas, porque dependen del tiempo del tester.                          | Más rápidas, especialmente cuando se ejecutan muchas veces.                     |
| Costo inicial   | Menor costo inicial, porque no requieren crear scripts.                     | Mayor costo inicial, porque hay que diseñar, programar y mantener pruebas.      |
| Mantenimiento   | Menor mantenimiento técnico, pero requieren esfuerzo humano constante.      | Requieren mantenimiento cuando cambia el código, la UI o las reglas de negocio. |
| Exploración     | Muy buenas para encontrar errores inesperados o problemas de usabilidad.    | Menos flexibles para descubrir problemas no previstos.                          |
| Repetibilidad   | Pueden variar según la persona que las ejecute.                             | Son repetibles y consistentes.                                                  |
| Uso recomendado | Usabilidad, exploración, validación visual, pruebas nuevas o poco estables. | Regresión, pruebas unitarias, integración, APIs, flujos críticos repetitivos.   |

---

## Pruebas manuales

Las pruebas manuales son aquellas que realiza una persona interactuando directamente con la aplicación. En OpenLib Market, por ejemplo, un tester podría entrar al sistema, buscar un libro, agregarlo al carrito, simular una compra y revisar si la experiencia tiene sentido.

### Ventajas de las pruebas manuales

1. **Permiten evaluar experiencia de usuario**

Son útiles para revisar si la interfaz es clara, si los botones están bien ubicados, si los mensajes se entienden y si el flujo de compra resulta natural.

2. **Ayudan a encontrar errores inesperados**

Una persona puede explorar el sistema de maneras que no estaban previstas en un caso de prueba automatizado.

3. **Tienen menor costo inicial**

Para funcionalidades nuevas o inestables, puede ser más rápido probar manualmente que invertir tiempo creando pruebas automatizadas que tal vez cambien pronto.

4. **Son útiles para validación visual**

Sirven para revisar diseño, colores, alineación, textos, navegación y consistencia visual.

### Desventajas de las pruebas manuales

1. **Son lentas**

Cada vez que se quiere validar algo, una persona debe repetir el proceso.

2. **Son propensas a errores humanos**

El tester puede olvidar un paso, interpretar mal el resultado o no repetir exactamente el mismo escenario.

3. **No escalan bien**

Cuando el sistema crece, probar manualmente todos los flujos se vuelve costoso y difícil.

4. **Son menos convenientes para regresión**

Si una funcionalidad debe probarse en cada release, hacerlo manualmente puede consumir mucho tiempo.

### Tipos de pruebas que aplican bien manualmente

```text
- Pruebas exploratorias.
- Pruebas de usabilidad.
- Pruebas visuales.
- Pruebas de aceptación temprana.
- Validación de textos y experiencia de usuario.
```

---

## Pruebas automatizadas

Las pruebas automatizadas son pruebas que se ejecutan mediante código o herramientas. Por ejemplo, se pueden crear pruebas unitarias para validar el cálculo del total del carrito o pruebas de integración para verificar que una API de pagos responda correctamente.

### Ventajas de las pruebas automatizadas

1. **Son rápidas y repetibles**

Una vez creadas, se pueden ejecutar muchas veces de forma consistente.

2. **Son buenas para regresión**

Permiten verificar que cambios nuevos no dañen funcionalidades existentes.

3. **Reducen errores humanos**

El script ejecuta siempre los mismos pasos y valida los mismos resultados.

4. **Se integran con CI/CD**

Pueden ejecutarse automáticamente cada vez que se sube código al repositorio.

### Desventajas de las pruebas automatizadas

1. **Tienen alto costo inicial**

Requieren tiempo para diseñar, programar y configurar herramientas.

2. **Tienen costo de mantenimiento**

Si cambia la interfaz, las APIs o las reglas de negocio, las pruebas también deben actualizarse.

3. **No reemplazan completamente la revisión humana**

Pueden validar que algo funcione técnicamente, pero no siempre detectan problemas de experiencia de usuario.

4. **Pueden dar falsos positivos o falsos negativos**

Una prueba mal diseñada puede fallar aunque el sistema esté bien, o pasar aunque exista un error real.

### Tipos de pruebas que aplican bien automatizadas

```text
- Pruebas unitarias.
- Pruebas de integración.
- Pruebas de API.
- Pruebas de regresión.
- Pruebas de humo.
- Pruebas end-to-end para flujos críticos.
```

---

## Criterios para decidir cuándo automatizar

Conviene automatizar una prueba cuando:

```text
- Se ejecuta muchas veces.
- Valida una regla de negocio importante.
- Es parte de un flujo crítico.
- Tiene resultado esperado claro.
- No cambia constantemente.
- Su ejecución manual sería lenta o repetitiva.
```

Ejemplos en OpenLib Market:

```text
- Calcular total del carrito.
- Validar máximo 10 ítems por carrito.
- Validar que no se pueda pagar un carrito vacío.
- Verificar cambio de estado de una orden después del pago.
- Probar endpoints REST de libros, carrito y pagos.
```

---

## Criterios para hacer una prueba manual

Conviene hacer una prueba manual cuando:

```text
- La funcionalidad todavía está cambiando mucho.
- Se quiere evaluar la experiencia del usuario.
- Se quiere explorar comportamientos inesperados.
- El resultado depende de percepción humana.
- La automatización costaría más que el beneficio.
```

Ejemplos en OpenLib Market:

```text
- Revisar si el flujo de compra es intuitivo.
- Evaluar si la pantalla del carrito es clara.
- Validar mensajes de error para usuarios.
- Revisar diseño visual del frontend JavaFX.
- Probar manualmente una integración externa inestable en una primera etapa.
```

---

## Recomendación para OpenLib Market

Para el primer release de OpenLib Market, lo más recomendable es usar una estrategia mixta.

### Pruebas que deberían automatizarse

```text
- Pruebas unitarias de reglas de negocio:
  - Carrito con mínimo 1 ítem para checkout.
  - Máximo 10 ítems diferentes.
  - Cálculo del total.
  - Aplicación de descuentos.
  - Control de stock.

- Pruebas de integración:
  - Guardar una orden.
  - Registrar una transacción.
  - Consultar libros disponibles.
  - Actualizar estado de un pedido.

- Pruebas de API:
  - Crear carrito.
  - Agregar libro al carrito.
  - Realizar pago.
  - Consultar historial de compras.

- Pruebas de regresión:
  - Flujo básico de compra.
  - Flujo de publicación de libro.
  - Flujo de pago exitoso.
```

### Pruebas que deberían hacerse manualmente

```text
- Revisión visual del frontend JavaFX.
- Validación de usabilidad del flujo de compra.
- Pruebas exploratorias buscando errores no previstos.
- Revisión de mensajes de error.
- Validación de experiencia al buscar libros.
- Revisión manual inicial de integraciones externas como PayPal o PSE.
```

---

## Conclusión

Las pruebas manuales y automatizadas no compiten entre sí, sino que se complementan.

Las pruebas manuales son mejores para usabilidad, exploración y validación visual. Las automatizadas son mejores para lógica de negocio, regresión, APIs y flujos críticos repetitivos.

Para OpenLib Market, lo ideal en el primer release es automatizar las reglas de negocio importantes y los flujos críticos, mientras se mantienen pruebas manuales para experiencia de usuario, diseño visual y exploración.

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

El prompt fue claro porque explicó el contexto del proyecto: “OpenLib Market, un marketplace de libros” y el objetivo específico: “definir una estrategia de testing para el primer release”. Esto ayudó a que la respuesta no fuera completamente genérica, sino aplicada al proyecto.

También funcionó bien pedir explícitamente “mínimo 3 ventajas y 3 desventajas de cada enfoque”, porque obligó al LLM a cubrir ambos lados de manera equilibrada. La instrucción de incluir “criterios prácticos para decidir cuándo automatizar una prueba y cuándo hacerla manual” también fue efectiva, porque llevó la respuesta más allá de una simple comparación teórica.

El formato solicitado, “primero con una tabla comparativa y después con explicaciones breves”, ayudó a que la respuesta fuera ordenada, fácil de leer y fácil de usar como base para el parcial.

#### 2. ¿Qué se puede mejorar?

El prompt pudo ser más específico pidiendo que se conectara la respuesta con conceptos de pirámide de pruebas, como pruebas unitarias, integración y end-to-end. Aunque el LLM mencionó esos tipos de pruebas, no explicó explícitamente la idea de que debería haber más pruebas unitarias que pruebas end-to-end.

También se pudo pedir que mencionara herramientas concretas para el contexto Java, por ejemplo JUnit, Mockito, Postman, Selenium o herramientas de CI/CD. La respuesta fue correcta, pero un poco general en la parte de herramientas.

El LLM sí mencionó el costo de mantenimiento de las pruebas automatizadas, lo cual era importante. Sin embargo, pudo profundizar más en que automatizar pruebas inestables o funcionalidades que cambian mucho puede generar pruebas frágiles y costosas.

Una omisión importante fue no hablar de datos de prueba. En OpenLib Market, las pruebas automatizadas necesitan datos controlados, por ejemplo libros disponibles, usuarios, carritos, órdenes y pagos simulados. Sin datos de prueba claros, las pruebas pueden fallar por razones externas y no por errores reales del sistema.

#### 3. Respuesta final

Las pruebas manuales y las pruebas automatizadas son dos enfoques complementarios dentro del proceso de desarrollo de software. Para el primer release de OpenLib Market no conviene escoger solo una, sino diseñar una estrategia mixta.

Las pruebas manuales son aquellas que realiza una persona interactuando directamente con el sistema. Son útiles cuando se necesita evaluar experiencia de usuario, diseño visual, claridad de mensajes o explorar comportamientos inesperados. Por ejemplo, en OpenLib Market conviene probar manualmente si el flujo de búsqueda, carrito y compra resulta entendible para un usuario real.

Las pruebas automatizadas son aquellas que se ejecutan mediante código o herramientas. Son útiles para validar reglas de negocio, endpoints, integración entre componentes y regresión. Por ejemplo, en OpenLib Market conviene automatizar que el carrito no permita checkout vacío, que no acepte más de 10 ítems diferentes y que el total se calcule correctamente.

| Criterio             | Pruebas manuales                             | Pruebas automatizadas                             |
| -------------------- | -------------------------------------------- | ------------------------------------------------- |
| Ejecución            | Las ejecuta una persona.                     | Las ejecuta un script o herramienta.              |
| Velocidad            | Más lentas.                                  | Más rápidas después de crearlas.                  |
| Repetibilidad        | Pueden variar entre testers.                 | Son repetibles y consistentes.                    |
| Costo inicial        | Bajo.                                        | Más alto.                                         |
| Mantenimiento        | Requieren esfuerzo humano constante.         | Requieren mantenimiento técnico.                  |
| Mejor uso            | Usabilidad, exploración y validación visual. | Regresión, lógica de negocio, APIs e integración. |
| Limitación principal | No escalan bien.                             | Pueden ser frágiles si el sistema cambia mucho.   |

Ventajas de las pruebas manuales:

* Permiten evaluar experiencia de usuario y diseño visual.
* Ayudan a encontrar errores inesperados mediante exploración.
* Son rápidas de aplicar al inicio, cuando la funcionalidad todavía cambia mucho.
* Sirven para validar textos, mensajes de error y claridad del flujo.

Desventajas de las pruebas manuales:

* Son lentas cuando deben repetirse muchas veces.
* Dependen del criterio y atención del tester.
* No escalan bien a medida que crece el sistema.
* Son menos confiables para regresión continua.

Ventajas de las pruebas automatizadas:

* Son rápidas y repetibles.
* Son ideales para pruebas de regresión.
* Se pueden ejecutar en un pipeline de integración continua.
* Reducen errores humanos en validaciones repetitivas.
* Permiten detectar rápidamente si un cambio rompió una regla de negocio.

Desventajas de las pruebas automatizadas:

* Tienen mayor costo inicial.
* Requieren mantenimiento cuando cambia el código, la interfaz o las reglas de negocio.
* No reemplazan completamente la evaluación humana.
* Pueden generar falsos positivos o falsos negativos si están mal diseñadas.
* Automatizar funcionalidades inestables puede producir pruebas frágiles.

Tipos de pruebas que aplican mejor manualmente:

* Pruebas exploratorias.
* Pruebas de usabilidad.
* Pruebas visuales.
* Validación de mensajes de error.
* Pruebas de aceptación temprana.

Tipos de pruebas que aplican mejor automatizadas:

* Pruebas unitarias.
* Pruebas de integración.
* Pruebas de API.
* Pruebas de regresión.
* Pruebas de humo.
* Algunas pruebas end-to-end para flujos críticos.

Para decidir si una prueba debe automatizarse, se pueden usar estos criterios:

* Si se ejecuta muchas veces, conviene automatizarla.
* Si valida una regla crítica del negocio, conviene automatizarla.
* Si tiene entradas y salidas claras, conviene automatizarla.
* Si es lenta o repetitiva manualmente, conviene automatizarla.
* Si cambia constantemente o depende de percepción visual, conviene dejarla manual.
* Si requiere juicio humano, exploración o evaluación de experiencia, conviene hacerla manual.

Para OpenLib Market, deberían automatizarse las pruebas relacionadas con reglas de negocio y flujos críticos:

* El carrito debe tener mínimo 1 ítem para checkout.
* El carrito no debe superar 10 ítems diferentes.
* El total del carrito debe calcularse correctamente.
* No se debe permitir comprar libros sin stock.
* Una orden debe cambiar de estado después de un pago exitoso.
* El sistema debe registrar una transacción.
* Los endpoints REST de libros, carrito, pagos y órdenes deben responder correctamente.
* El flujo básico de compra debería tener al menos una prueba end-to-end automatizada.

En cambio, deberían hacerse manualmente:

* La revisión visual del frontend JavaFX.
* La validación de usabilidad del flujo de compra.
* La claridad de los mensajes de error.
* La navegación entre pantallas.
* La experiencia de búsqueda y filtrado de libros.
* Pruebas exploratorias antes del release.
* Validación inicial de integraciones externas como PayPal o PSE, especialmente si todavía están cambiando.

También es importante aplicar la idea de pirámide de pruebas: tener muchas pruebas unitarias, una cantidad moderada de pruebas de integración y pocas pruebas end-to-end, porque estas últimas suelen ser más lentas y frágiles.

En conclusión, para el primer release de OpenLib Market la mejor estrategia es combinar ambos enfoques. Las pruebas automatizadas deben cubrir lo repetitivo, crítico y medible. Las pruebas manuales deben cubrir experiencia de usuario, exploración y validación visual. De esta manera se logra una estrategia realista, mantenible y adecuada para un proyecto que todavía está evolucionando.
