## P11_pruebas_manual_vs_auto

### Estudiante
- **Nombre completo**: [Alejandro Macias Barrios]

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [Gemini] |
| **Modelo específico** | [Gemini Flash] |
| **¿Por qué elegiste este LLM?** | [Aunque la LLM que mas me gusta es Claude pues ya se me acabaron los creditos y entre todas las demas decidí usar esta ya que experimentalmente también es de las que mas me ha funcionado, además es la que usa Antigravity y la hemos usado bastante en clase] |

### Prompt utilizado
[Imagina que eres un estudiante de Ingeniería de sistemas de la Pontificia Universidad Javeriana cursando la materia de Fundamentos de Ingeniería de Software. El profesor te ha hecho la siguiente pregunta y debes responderla de la mejor manera posible.

En el proceso de desarrollo de software, existen dos enfoques principales para las pruebas: **pruebas manuales** y **pruebas automatizadas**. Cada uno tiene ventajas, desventajas y contextos donde es más apropiado. OpenLib Market está definiendo su estrategia de testing para el primer release.

Compare pruebas manuales y automatizadas considerando: ventajas de cada una (mínimo 3), desventajas de cada una (mínimo 3), tipos de pruebas que aplican a cada enfoque, criterios para decidir cuándo automatizar una prueba y cuándo hacerla manual, y una recomendación concreta de qué pruebas deberían ser manuales y cuáles automatizadas en OpenLib Market.]

### Respuesta del LLM
[
1. Cuadro Comparativo: Pruebas Manuales vs. Automatizadas
Pruebas Manuales
El factor humano inspeccionando el software directamente.

Ventajas (Mínimo 3):
Flexibilidad y Exploración: Permiten el exploratory testing, donde el tester sigue su intuición y encuentra "bichos" (bugs) en flujos no previstos en un guion estricto.
Validación de UX/UI Real: El ojo humano es el único capaz de notar si un botón quedó visualmente incómodo, si un texto está desalineado o si la experiencia de usuario se siente "pesada".
Bajo Costo Inicial: No requieren de tiempo para programar scripts de prueba ni de configurar complejas herramientas de integración continua antes de empezar a probar.

Desventajas (Mínimo 3):
Propensas al Error Humano: El cansancio o la distracción del tester pueden hacer que pase por alto fallas evidentes tras repetir el mismo flujo varias veces.
No son Escalables: Si el sistema crece, validar todo manualmente en cada sprint se vuelve una pesadilla logística y consume demasiado tiempo.
Lentas para Pruebas Masivas: Es imposible simular manualmente a 1,000 usuarios comprando un libro al mismo tiempo.
Tipos de Pruebas que Aplican: Pruebas Usabilidad (UX), Pruebas Exploratorias, Pruebas de Aceptación de Usuario (UAT), Pruebas de Humo (Smoke testing inicial en etapas tempranas).
Pruebas Automatizadas
Uso de herramientas y scripts de código para ejecutar casos de prueba predefinidos.

Ventajas (Mínimo 3):
Velocidad y Consistencia: Se ejecutan en segundos o minutos, exactamente de la misma manera cada vez, sin saltarse ningún paso.
Reutilización y Regresión: Un script escrito hoy sirve para validar el sistema durante los próximos dos años. Son ideales para asegurar que lo que ya funcionaba no se rompa con código nuevo.
Soporte para Carga y Rendimiento: Permiten simular escenarios de estrés de hardware o tráfico concurrente que un equipo humano jamás podría replicar.

Desventajas (Mínimo 3):
Alto Costo de Mantenimiento: Si la interfaz o el flujo cambian (lo cual pasa mucho en un primer release), los scripts se rompen y toca gastar tiempo reescribiéndolos.
Inversión Inicial Elevada: Requieren tiempo de desarrollo (los scripts son código), licencias o configuración de frameworks (como Selenium, Cypress o Jest), y personal capacitado.
Falsa Sensación de Seguridad: La automatización solo busca lo que le dijiste que buscara. Si hay un error crítico al lado del botón que está probando, el script lo ignorará si no está programado para verificarlo.
Tipos de Pruebas que Aplican: Pruebas Unitarias (Unit Tests), Pruebas de Integración, Pruebas de Regresión, Pruebas de Carga/Rendimiento/Estrés, API testing.

Recomendación Concreta para el Primer Release de OpenLib MarketConsiderando que estamos en el primer release, el software va a ser inestable en su UI y la prioridad es salir a producción rápido pero seguros. Mi propuesta es un enfoque híbrido optimizado:¿Qué automatizar en OpenLib Market?Pruebas Unitarias del Backend (100% Automatizado): Toda la lógica del carrito de compras, cálculo de impuestos, validación de stock de libros y pasarela de pagos. Esto debe blindarse con JUnit/Jest para que ningún desarrollador rompa la lógica del negocio.Pruebas de API (Automatizado con Postman o RestAssured): Los endpoints esenciales (Autenticación, Búsqueda de Libros, Crear Orden). Es rápido de automatizar y no cambia tanto como la interfaz web.Un Flujo de Regresión Crítico (E2E básico): Solo el Happy Path principal de la aplicación: Iniciar sesión $\rightarrow$ Buscar libro $\rightarrow$ Agregar al carrito $\rightarrow$ Comprar.¿Qué mantener Manual en OpenLib Market?Pruebas de Interfaz (UI) y Usabilidad: Como es el primer lanzamiento, la interfaz va a cambiar según el feedback de los usuarios. Automatizar la UI ahorita sería botar plata en mantenimiento de scripts. El equipo debe validar a mano que el responsive en celulares funcione bien y se vea estético.Pruebas de Aceptación (UAT): Validar con los stakeholders o usuarios beta que el flujo de subasta o venta de libros usados realmente cumpla con la expectativa del negocio.Pruebas Exploratorias de Seguridad Básica: Intentar "romper" los formularios de registro e inicio de sesión metiendo datos basura o caracteres extraños a mano para ver cómo reacciona el sistema en caliente.]

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?
[Restricciones y mínimos explícitos: Establecer un mínimo de 3 ventajas y 3 desventajas para cada enfoque evitó que la IA fuera perezosa o vaga. Al darle métricas claras, el output se alineó exactamente con la rúbrica de una evaluación real.]

#### 2. ¿Qué se puede mejorar?
[Falta de profundidad metodológica: El prompt pide "tipos de pruebas", pero en la ingeniería de software el testing se divide formalmente en Niveles (Unitario, Integración, Sistema, Aceptación) y Tipos (Funcionales y No Funcionales). Si el prompt hubiera pedido explícitamente clasificar las pruebas bajo estos estándares (como la norma ISO/IEC 25010), la respuesta de la IA habría sido técnicamente más rigurosa y menos genérica.]

#### 3. Respuesta final
[Las pruebas manuales y las pruebas automatizadas no deben verse como alternativas excluyentes, sino como enfoques complementarios dentro de una estrategia de aseguramiento de calidad. Las pruebas manuales destacan por su flexibilidad, capacidad para evaluar la experiencia de usuario y detectar comportamientos inesperados, mientras que las pruebas automatizadas ofrecen velocidad, consistencia y la posibilidad de ejecutar validaciones repetitivas y masivas de manera eficiente. Las pruebas manuales son especialmente valiosas para evaluar aspectos subjetivos como la usabilidad, el diseño visual y los escenarios exploratorios, aunque presentan limitaciones en escalabilidad y son más propensas a errores humanos. Por su parte, las pruebas automatizadas son ideales para validar lógica de negocio, APIs, regresiones y rendimiento, pero requieren una inversión inicial considerable y un mantenimiento continuo de los scripts.

Para el primer release de OpenLib Market, la mejor decisión es adoptar un enfoque híbrido. Se recomienda automatizar las pruebas que aportan mayor valor y estabilidad, como las pruebas unitarias del backend, las pruebas de API y un flujo crítico de compra de extremo a extremo. Al mismo tiempo, conviene mantener manuales las pruebas de interfaz, usabilidad, aceptación por parte de los usuarios y pruebas exploratorias, ya que la aplicación aún se encuentra en una etapa temprana y es probable que la interfaz cambie con frecuencia.]