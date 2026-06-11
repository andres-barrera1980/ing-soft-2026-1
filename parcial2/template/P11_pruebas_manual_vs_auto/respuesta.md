# Plantilla de entrega — Parcial 2


---

## Pregunta [11]: [P11_pruebas_manual_vs_auto]

### Estudiante
- **Nombre completo**: [Danna Gabriela ROjas Bernal

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **GEMINI** | [Claude / ChatGPT / Gemini / Copilot / DeepSeek / Qwen / Mistral / Otro / **Sin IA — respuesta propia**] |
| **Modelo específico** | [Ej: Claude Opus 4.5, GPT-4o, Gemini 2.5 Pro, etc. Si respondes sin IA, escribe "N/A"] |
| **la pregunta me lo pide** | [Justifica en 1-3 oraciones. Si respondes sin IA, explica por qué decidiste no usar LLM para esta pregunta.] |

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
Eres un Arquitecto de Software Senior revisando el módulo de checkout de "OpenLib Market", una plataforma de e-commerce para compra-venta de libros universitarios. El stack tecnológico está basado en Java 21, Spring Boot 3.x y PostgreSQL 16. Actualmente, la clase 'ProcesadorPago' utiliza condicionales 'if-else' o 'switch' acoplados a strings ("TARJETA", "PSE", "PAYPAL") para ejecutar las transacciones. Esto genera código espagueti y rompe principios de diseño. Necesitamos rediseñar este componente para eliminar los condicionales por completo. 1. Identifica explícitamente al menos DOS principios SOLID violados en el diseño original. 2. Aplica el patrón de diseño GoF de comportamiento más adecuado para resolver el acoplamiento. 3. El diseño debe permitir que en el futuro se agregue el método "Criptomonedas" sin necesidad de modificar las clases existentes (Open/Closed Principle). 4. Usa inyección de dependencias nativa de Spring Boot para resolver la estrategia en tiempo de ejecución. Entrega la respuesta en formato Markdown estructurado con las siguientes secciones: - "Diagnóstico SOLID": Explicación teórica de las violaciones encontradas. - "Estrategia de Refactoring": Patrón elegido y justificación. - "Código Java 21": Código limpio que incluya interfaces, clases concretas por pasarela y la clase orquestadora. Antes de entregar el código, aplica Chain of Thought para verificar internamente: ¿El diseño elimina los if-else en el orquestador? ¿Qué pasa si el cliente envía un tipo de pago inválido? Muestra esta validación de casos borde.
---

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**
[Pega aquí la respuesta COMPLETA del LLM, sin editar, sin resumir.
Incluye TODO el texto, código, explicaciones que generó el LLM.

Si el LLM generó código, asegúrate de que esté correctamente formateado.
Si tuviste que hacer varias iteraciones, pega la MEJOR respuesta obtenida,
pero menciona cuántas iteraciones hiciste.]
```
1. Pruebas Manuales

Las pruebas manuales son ejecutadas directamente por un tester sin utilizar scripts automatizados.

Ventajas
Ventaja	Descripción
Flexibilidad	Permiten adaptarse rápidamente a cambios funcionales o visuales sin modificar scripts.
Evaluación humana	Facilitan detectar problemas de usabilidad, experiencia de usuario (UX) y comportamiento inesperado.
Menor inversión inicial	No requieren infraestructura de automatización ni desarrollo de frameworks de prueba.
Exploración libre	Permiten descubrir defectos no contemplados en casos de prueba predefinidos.
Desventajas
Desventaja	Impacto
Alto consumo de tiempo	Repetir pruebas en cada versión puede ser extremadamente lento.
Costos operativos crecientes	A medida que el sistema crece, aumenta la necesidad de más recursos humanos.
Mayor probabilidad de error humano	Los testers pueden omitir pasos o interpretar resultados de forma inconsistente.
Escalabilidad limitada	Es difícil ejecutar cientos o miles de casos de prueba repetidamente.
2. Pruebas Automatizadas

Las pruebas automatizadas utilizan scripts y herramientas para ejecutar validaciones sin intervención humana.

Ventajas
Ventaja	Descripción
Ejecución rápida	Miles de pruebas pueden ejecutarse en minutos.
Repetibilidad	Siempre ejecutan exactamente los mismos pasos.
Integración con CI/CD	Pueden ejecutarse automáticamente en cada commit o despliegue.
Cobertura continua	Detectan regresiones inmediatamente después de cambios en el código.
Desventajas
Desventaja	Impacto
Alto costo inicial	Requieren diseño, desarrollo y configuración de infraestructura.
Mantenimiento constante	Los cambios funcionales pueden romper scripts existentes.
Poco efectivas para UX	No reemplazan la percepción humana sobre experiencia de usuario.
Retorno tardío	La inversión suele recuperarse después de múltiples ejecuciones.
3. Comparación General
Aspecto	Manual	Automatizada
Velocidad de ejecución	Baja	Alta
Costo inicial	Bajo	Alto
Costo a largo plazo	Alto	Bajo-Medio
Escalabilidad	Baja	Muy alta
Detección de problemas UX	Excelente	Limitada
Detección de regresiones	Limitada	Excelente
Adaptación a cambios frecuentes	Alta	Baja-Media
Precisión	Variable	Alta
4. ¿Qué pruebas funcionan mejor en cada modalidad?
Más adecuadas para automatización
Tipo de prueba	Justificación
Unitarias	Son repetitivas, determinísticas y rápidas.
Integración	Validan contratos entre componentes y servicios.
Regresión	Deben ejecutarse continuamente después de cambios.
API Testing	No dependen de la interfaz gráfica y son estables.
Smoke Tests	Verifican rápidamente que el sistema sigue operativo.
Ejemplos en OpenLib Market
Validación de cálculo de precios.
Aplicación de descuentos.
Creación de órdenes.
Procesamiento de pagos.
Persistencia en PostgreSQL.
Comunicación entre microservicios.
Más adecuadas para ejecución manual
Tipo de prueba	Justificación
UX	Requieren percepción humana.
Exploratorias	Buscan comportamientos inesperados.
Pruebas visuales	Evaluación de diseño y consistencia visual.
Validación de accesibilidad subjetiva	Necesitan criterio humano.
Pruebas de aceptación de negocio	Requieren interacción con stakeholders.
Ejemplos en OpenLib Market
Facilidad para encontrar un libro.
Claridad del flujo de checkout.
Comprensión de mensajes de error.
Diseño responsive.
Experiencia general de compra.
5. Criterios de Ingeniería para decidir qué automatizar
Automatizar cuando:
1. La prueba se ejecuta frecuentemente

Ejemplo:

Login
Registro
Checkout
Búsqueda de libros

Si una prueba se ejecutará en cada sprint o release, es candidata clara a automatización.

2. El flujo es estable

Ejemplo:

Cálculo de impuestos
Validación de stock
Generación de órdenes

Las reglas de negocio suelen cambiar poco.

3. El fallo tiene alto impacto

Ejemplo:

Pagos
Facturación
Gestión de inventario

Los defectos en estas áreas generan pérdidas económicas directas.

4. Existe lógica repetitiva

Ejemplo:

Validaciones de formularios
Autenticación
Integración con bases de datos
Mantener manual cuando:
1. La funcionalidad cambia constantemente

Ejemplo:

Pantallas nuevas
Prototipos
Rediseños de interfaz
2. Se evalúa percepción humana

Ejemplo:

Experiencia de compra
Diseño visual
Navegación
3. La ejecución es poco frecuente

Ejemplo:

Funcionalidades usadas una vez al año.
Casos excepcionales de negocio.
Evaluación de Viabilidad Económica para el Primer Release

Antes de recomendar una estrategia, es importante analizar la viabilidad de automatizar todo.

¿Es viable automatizar el 100% para la versión 1.0?

No.

Existen tres razones principales:

1. Alto costo inicial

Construir una suite completa implica:

Framework de automatización.
Integración con CI/CD.
Gestión de datos de prueba.
Desarrollo de scripts.
Mantenimiento.

La inversión inicial es significativa.

2. La interfaz cambia constantemente

Durante las primeras versiones de un producto:

Cambian formularios.
Cambian flujos.
Cambian componentes visuales.

Cada cambio rompe pruebas UI automatizadas y genera costos de mantenimiento elevados.

3. Bajo retorno de inversión temprano

Si la funcionalidad cambia semanalmente, los scripts deberán reescribirse constantemente, reduciendo el beneficio esperado de la automatización.

Por tanto, desde una perspectiva de ingeniería y costo-beneficio, automatizar todo en un primer lanzamiento no es económicamente eficiente.

Recomendación para OpenLib Market v1.0.0
Estrategia recomendada: Automatización Selectiva
Automatizar desde el inicio

Nivel Unitario (80–90% de cobertura de lógica de negocio)

Cálculo de precios.
Reglas de descuentos.
Validaciones.
Servicios de negocio.

Pruebas de Integración

Spring Boot + PostgreSQL.
Repositorios JPA.
Integración con pasarelas de pago simuladas.

Pruebas de API

Login.
Catálogo.
Carrito.
Checkout.
Órdenes.

Smoke Tests automatizados

Flujo básico de compra extremo a extremo.
Mantener manual inicialmente

UX y usabilidad

Navegación.
Diseño responsive.
Flujo de compra.

Pruebas exploratorias

Búsqueda de defectos inesperados.

Pruebas visuales

Consistencia de interfaces.

Aceptación funcional

Validación por Product Owner y stakeholders.
---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

[Evalúa tu propio prompt, no la respuesta del LLM. ¿El contexto fue suficiente? ¿Las restricciones fueron claras? ¿El formato de salida que pediste ayudó a obtener una buena respuesta? ¿Qué parte de tu prompt fue más efectiva? Sé específico: menciona fragmentos concretos de tu prompt que funcionaron bien.]
El prompt fue efectivo porque pidió comparar las pruebas manuales y automatizadas desde diferentes perspectivas, no solo describiendo sus características sino también analizando ventajas, desventajas y casos de uso. Además, al incluir el contexto de OpenLib Market, permitió relacionar la respuesta con situaciones reales del sistema, haciendo que los ejemplos fueran más fáciles de entender y aplicar. También fue útil solicitar criterios para decidir qué pruebas automatizar, ya que esto llevó a una respuesta más completa y orientada a la toma de decisiones.


#### 2. ¿Qué se puede mejorar?

[¿Qué le faltó a tu prompt? ¿Qué harías diferente si pudieras reformularlo? ¿El LLM entendió mal algo por falta de claridad en tu prompt? ¿La respuesta tiene errores u omisiones? ¿Qué no cubrió el LLM que tú sí sabes por lo visto en clase?]

ambién habría sido útil pedir una justificación más profunda sobre el impacto económico y el tiempo de desarrollo que implica automatizar pruebas. Aunque la respuesta fue completa, algunos apartados resultaron demasiado extensos y repetitivos para responder una pregunta de parcial.
#### 3. Respuesta final

[Escribe tu respuesta definitiva a la pregunta del parcial, integrando lo que aprendiste del LLM pero yendo más allá. Corrige errores, llena omisiones, conecta con conceptos vistos en clase. Esta es tu respuesta: demuestra que tú dominas el tema.]
Las pruebas manuales y automatizadas se complementan entre sí. Las manuales son mejores para evaluar la experiencia del usuario y detectar problemas visuales o de usabilidad, mientras que las automatizadas son ideales para tareas repetitivas como validar pagos, inventario o reglas de negocio. Para OpenLib Market no sería conveniente automatizar todo desde el inicio, ya que implica un costo alto y muchas funcionalidades aún pueden cambiar. Lo más recomendable es automatizar los procesos críticos y mantener manuales las pruebas relacionadas con la experiencia de usuario, logrando así un buen equilibrio entre calidad, tiempo y costos.
