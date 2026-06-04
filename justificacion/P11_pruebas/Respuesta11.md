
## Pregunta [11]: [Analizar ventajas, desventajas y criterios de decisión]

### Estudiante
- **Nombre completo**: Julian Felipe Rojas Almanza

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Gpt |
| **Modelo específico** | Gpt-4o |
| **¿Por qué elegiste este LLM?** | Es uno de los modelos con mejor visión estratégica y de gestión de proyectos. No se limitan a dar respuestas genéricas, sino que entienden los conceptos de deuda técnica en código de pruebas y el costo por hora del esfuerzo humano, entregando recomendaciones sumamente realistas. |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```
Actúa como un QA Lead y Arquitecto de Estrategias de Testing con años de experiencia en automatización de pruebas y aseguramiento de la calidad. Estoy resolviendo un ejercicio técnico académico para la plataforma "OpenLib Market" y necesito una comparación estratégica rigurosa.

[CONTEXTO]
La plataforma "OpenLib Market" está definiendo la estrategia de testing para su primer release oficial. El equipo técnico está debatiendo el presupuesto, el tiempo y el esfuerzo que se debe asignar a las pruebas manuales frente a las pruebas automatizadas para garantizar un lanzamiento estable sin agotar los recursos.

[PROBLEMA / TAREA]
Necesito que realices un análisis comparativo profundo entre Pruebas Manuales y Pruebas Automatizadas. Tu respuesta debe resolver de forma directa y explícita los siguientes puntos requeridos por mi enunciado:
1. Ventajas de cada enfoque: Detalla un mínimo de 3 ventajas claras de las pruebas manuales y un mínimo de 3 ventajas de las pruebas automatizadas.
2. Desventajas de cada enfoque: Detalla un mínimo de 3 desventajas claras de las pruebas manuales (ej: factor humano, fatiga) y un mínimo de 3 desventajas de las pruebas automatizadas (enfocándote fuertemente en el costo de mantenimiento y falsos positivos).
3. Tipos de pruebas aplicables: Identifica qué tipos de pruebas se alinean mejor con el enfoque manual (ej: UX, Exploratory) y cuáles con el automatizado (ej: Regresión, Carga, Unitarias).
4. Criterios de Decisión: Establece un conjunto de reglas o criterios prácticos (frecuencia, estabilidad de la función, criticidad, ROI) para determinar cuándo una prueba DEBE ser manual y cuándo DEBE automatizarse.
5. Recomendación para OpenLib Market: Brinda una propuesta concreta y aterrizada para el primer release del proyecto, especificando qué flujos exactos (ej: login, pasarela de pagos, carrito de compras, feedback visual) deberían probarse de forma manual y cuáles automatizarse.

[RESTRICCIONES]
- Costo de Mantenimiento: Es obligatorio que analices el impacto del costo de mantenimiento en las pruebas automatizadas cuando la interfaz o los requisitos cambian constantemente.
- El análisis no debe ser puramente teórico; debe conectar los conceptos con el contexto de un primer release de una plataforma web/móvil donde el tiempo de salida al mercado (*Time to Market*) es crítico.

[FORMATO DE SALIDA]
Estructura tu respuesta en Markdown utilizando única y estrictamente el siguiente orden y encabezados:
- ### 1. Análisis Comparativo: Pruebas Manuales vs. Automatizadas
  (Presenta una tabla que resuma: Ventajas (mínimo 3), Desventajas (mínimo 3) y Tipos de Pruebas aplicables para ambos enfoques).
- ### 2. Criterios de Ingeniería para la Automatización (Matriz de Decisión)
- ### 3. Estrategia y Recomendación Concreta para OpenLib Market (Primer Release)

[VALIDACIÓN]
Sabré que tu respuesta es excelente si cumples con el mínimo de 3 ventajas/desventajas por enfoque, si la recomendación para OpenLib Market es pragmática para un primer lanzamiento (evitando la utopía de automatizar el 100%), y si explicas con claridad cómo las pruebas manuales exploratorias aportan un valor que la automatización no puede replicar.
```

---

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**
# Análisis Comparativo: Pruebas Manuales vs Automatizadas - OpenLib Market

### 1. Análisis Comparativo: Pruebas Manuales vs. Automatizadas

| Aspecto | Pruebas Manuales | Pruebas Automatizadas |
|----------|----------|----------|
| Ventaja 1 | Detectan problemas de UX y usabilidad | Ejecución rápida y repetible |
| Ventaja 2 | Permiten pruebas exploratorias | Reducen errores humanos |
| Ventaja 3 | No requieren desarrollo de scripts | Ideales para regresión continua |
| Ventaja 4 | Flexibles ante cambios frecuentes | Integración con CI/CD |
| Desventaja 1 | Fatiga y errores humanos | Alto costo inicial de implementación |
| Desventaja 2 | Lentas en ejecuciones repetitivas | Requieren mantenimiento constante |
| Desventaja 3 | Difíciles de escalar | Pueden generar falsos positivos |
| Desventaja 4 | Costosas para regresiones grandes | Menos efectivas para evaluar UX |
| Tipos de pruebas recomendadas | Exploratorias, UX, accesibilidad, validación visual, pruebas ad-hoc | Unitarias, integración, regresión, API, rendimiento y carga |

#### Impacto del mantenimiento en la automatización

Uno de los mayores costos de la automatización es el mantenimiento. Cuando la interfaz cambia constantemente (nombres de botones, flujos, componentes visuales o reglas de negocio), los scripts automatizados deben actualizarse.

Consecuencias:

- Tiempo adicional de desarrollo.
- Incremento del costo operativo.
- Falsos positivos por selectores rotos.
- Pérdida temporal de confianza en la suite.

Por esta razón, automatizar funcionalidades todavía inestables suele generar poco retorno de inversión (ROI).

---

### 2. Criterios de Ingeniería para la Automatización (Matriz de Decisión)

| Criterio | Manual | Automatizada |
|-----------|-----------|-----------|
| Funcionalidad cambia constantemente | ✓ | |
| Funcionalidad estable | | ✓ |
| Se ejecuta pocas veces | ✓ | |
| Se ejecuta en cada release | | ✓ |
| Validación visual o experiencia de usuario | ✓ | |
| Validación lógica repetitiva | | ✓ |
| Prueba exploratoria | ✓ | |
| Prueba de regresión | | ✓ |
| Proceso crítico para el negocio | | ✓ |
| Alto ROI por repetición | | ✓ |

#### Regla práctica

Automatizar cuando:

- La prueba se ejecuta frecuentemente.
- El flujo es estable.
- El riesgo de fallo es alto.
- El costo de automatización se recupera mediante reutilización.

Mantener manual cuando:

- El requisito cambia constantemente.
- Se necesita criterio humano.
- La validación es visual o subjetiva.
- La prueba se ejecutará pocas veces.

---

### 3. Estrategia y Recomendación Concreta para OpenLib Market (Primer Release)

Dado que OpenLib Market se encuentra en su primer release y el Time to Market es importante, no es recomendable intentar automatizar el 100% del sistema.

#### Automatizar

**Alta prioridad:**

- Login y autenticación.
- Registro de usuarios.
- Recuperación de contraseña.
- APIs de catálogo de libros.
- Carrito de compras.
- Cálculo de totales.
- Checkout.
- Integración con pasarela de pagos.
- Gestión de inventario.
- Casos de regresión críticos.

**Nivel técnico:**

- Pruebas unitarias.
- Pruebas de integración.
- Pruebas de API.
- Smoke tests automáticos.

#### Mantener Manual

**Alta prioridad:**

- Experiencia de usuario.
- Diseño visual.
- Navegación entre pantallas.
- Flujo de compra completo desde la perspectiva del cliente.
- Compatibilidad en diferentes navegadores.
- Validación de mensajes, textos y feedback visual.

**Pruebas exploratorias:**

Los testers deben recorrer la aplicación buscando comportamientos inesperados, inconsistencias y problemas de usabilidad que una herramienta automatizada no puede identificar por sí sola.

#### Distribución recomendada para el primer release

- 70% Automatización.
- 30% Pruebas manuales.

La automatización debe concentrarse en los procesos críticos y repetitivos del negocio, mientras que las pruebas manuales deben enfocarse en UX, exploración y validaciones visuales.

#### Conclusión

Las pruebas manuales y automatizadas no compiten; se complementan. Para OpenLib Market, la mejor estrategia para el primer lanzamiento es automatizar los flujos críticos de negocio (login, pagos, carrito e inventario) y reservar las pruebas manuales para experiencia de usuario, exploración y validaciones visuales. Esto maximiza la calidad sin comprometer el tiempo de salida al mercado.

```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

[Evalúa tu propio prompt, no la respuesta del LLM. ¿El contexto fue suficiente? ¿Las restricciones fueron claras? ¿El formato de salida que pediste ayudó a obtener una buena respuesta? ¿Qué parte de tu prompt fue más efectiva? Sé específico: menciona fragmentos concretos de tu prompt que funcionaron bien.]


#### 2. ¿Qué se puede mejorar?

[¿Qué le faltó a tu prompt? ¿Qué harías diferente si pudieras reformularlo? ¿El LLM entendió mal algo por falta de claridad en tu prompt? ¿La respuesta tiene errores u omisiones? ¿Qué no cubrió el LLM que tú sí sabes por lo visto en clase?]


#### 3. Respuesta final

[Escribe tu respuesta definitiva a la pregunta del parcial, integrando lo que aprendiste del LLM pero yendo más allá. Corrige errores, llena omisiones, conecta con conceptos vistos en clase. Esta es tu respuesta: demuestra que tú dominas el tema.]
