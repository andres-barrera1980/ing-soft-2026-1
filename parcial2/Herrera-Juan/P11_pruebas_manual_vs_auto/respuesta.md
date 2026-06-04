# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [XX]: [Título resumido]

### Estudiante
- **Nombre completo**: [Tu nombre y apellido]

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [Claude / ChatGPT / Gemini / Copilot / DeepSeek / Qwen / Mistral / Otro / **Sin IA — respuesta propia**] |
| **Modelo específico** | [Ej: Claude Opus 4.5, GPT-4o, Gemini 2.5 Pro, etc. Si respondes sin IA, escribe "N/A"] |
| **¿Por qué elegiste este LLM?** | [Justifica en 1-3 oraciones. Si respondes sin IA, explica por qué decidiste no usar LLM para esta pregunta.] |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

Actúa como un experto en aseguramiento de calidad de software (QA), testing y desarrollo ágil. Compara detalladamente las pruebas manuales y las pruebas automatizadas indicando al menos tres ventajas y tres desventajas de cada enfoque. Explica qué tipos de pruebas son más apropiadas para cada uno, cuáles son los criterios prácticos para decidir cuándo automatizar una prueba y cuándo realizarla manualmente, considerando frecuencia de ejecución, costo, riesgo, estabilidad de requisitos y retorno de inversión. Finalmente, propone una estrategia de testing para OpenLib Market indicando específicamente qué pruebas deberían automatizarse y cuáles deberían ejecutarse manualmente para el primer release del proyecto. Presenta la respuesta en una tabla comparativa seguida de una explicación detallada.


---

### Respuesta del LLM

Comparación entre Pruebas Manuales y Pruebas Automatizadas
Tabla Comparativa
Aspecto	Pruebas Manuales	Pruebas Automatizadas
Ejecución	Realizadas por un tester de forma manual.	Ejecutadas por herramientas o scripts.
Velocidad	Más lentas.	Muy rápidas.
Costo inicial	Bajo.	Alto (desarrollo y mantenimiento de scripts).
Costo a largo plazo	Alto por repetición constante.	Bajo cuando se ejecutan muchas veces.
Cobertura	Limitada por tiempo y recursos humanos.	Alta, pueden ejecutarse cientos o miles de casos.
Detección de problemas visuales	Excelente.	Limitada.
Consistencia	Puede variar entre ejecuciones.	Siempre ejecutan los mismos pasos.
Escalabilidad	Difícil de escalar.	Muy escalable.
Adecuadas para	Exploratorias, usabilidad, UX.	Regresión, integración, unitarias.
Participación humana	Requiere intervención constante.	Requiere intervención mínima.
Integración CI/CD	Difícil.	Ideal para pipelines CI/CD.
ROI	Menor en pruebas repetitivas.	Alto en pruebas frecuentes.
1. Pruebas Manuales
¿Qué son?

Son pruebas ejecutadas directamente por una persona que interactúa con el sistema como lo haría un usuario real.

Ejemplo:

Ingresar al sistema
Buscar un libro
Agregar al carrito
Realizar una compra

y verificar visualmente que todo funcione correctamente.

Ventajas de las Pruebas Manuales
1. Detectan problemas de experiencia de usuario

Permiten identificar:

Interfaces confusas.
Flujo de navegación poco intuitivo.
Mensajes poco claros.
2. Ideales para pruebas exploratorias

El tester puede investigar comportamientos inesperados.

Ejemplo:

¿Qué ocurre si el usuario abre varias pestañas?
3. Bajo costo inicial

No es necesario:

Frameworks.
Scripts.
Infraestructura de automatización.
Desventajas de las Pruebas Manuales
1. Son lentas

Ejecutar el mismo flujo cien veces consume mucho tiempo.

2. Son propensas a errores humanos

Un tester puede:

Omitir pasos.
Interpretar incorrectamente resultados.
3. Difíciles de repetir consistentemente

Cada ejecución puede variar ligeramente.

2. Pruebas Automatizadas
¿Qué son?

Son pruebas ejecutadas por herramientas y scripts.

Ejemplos:

JUnit
Mockito
Selenium
Cypress
Playwright
Ventajas de las Pruebas Automatizadas
1. Ejecución rápida

Miles de pruebas pueden ejecutarse en minutos.

2. Alta repetibilidad

Siempre realizan exactamente los mismos pasos.

3. Excelente para regresión

Permiten verificar rápidamente que cambios recientes no rompieron funcionalidades existentes.

Desventajas de las Pruebas Automatizadas
1. Alto costo inicial

Se requiere:

Diseño.
Desarrollo.
Mantenimiento.
2. Mantenimiento continuo

Cuando cambia la aplicación:

Pantallas
APIs
Flujos

las pruebas deben actualizarse.

3. No detectan fácilmente problemas de usabilidad

Pueden validar:

Botón existe
Botón funciona

pero no:

Botón es intuitivo
¿Qué tipos de pruebas son más apropiadas para cada enfoque?
Pruebas Manuales
Exploratorias

El tester investiga libremente.

Usabilidad

Validación de experiencia de usuario.

Accesibilidad visual

Verificación de:

Contraste.
Tamaño de fuentes.
Navegación.
Pruebas de aceptación con usuarios

Validan expectativas reales del negocio.

Pruebas Automatizadas
Unitarias

Ejemplo:

ControlInventarioServiceTest
Integración

Ejemplo:

Pago → Base de Datos
Regresión

Verifican funcionalidades ya implementadas.

API Testing

Validación automática de endpoints REST.

Smoke Tests

Pruebas rápidas después de cada despliegue.

¿Cuándo Automatizar una Prueba?
Criterio 1: Frecuencia de ejecución
Automatizar

Si se ejecuta:

Muchas veces

Ejemplo:

Login
Registro
Checkout
Manual

Si se ejecuta:

Una sola vez

o muy ocasionalmente.

Criterio 2: Riesgo de negocio
Automatizar

Procesos críticos:

Pagos
Inventario
Compras
Facturación
Manual

Funcionalidades secundarias.

Criterio 3: Estabilidad de requisitos
Automatizar

Cuando la funcionalidad ya está estable.

Manual

Cuando los requisitos cambian constantemente.

Criterio 4: Retorno de inversión (ROI)

Automatizar cuando:

Costo Automatización
<
Costo Repetir Manualmente
Criterio 5: Complejidad visual
Manual

Cuando se requiere validar:

Diseño
Experiencia de usuario
Aspectos visuales
Estrategia de Testing para OpenLib Market (Primer Release)
Funcionalidades Principales

Supongamos que OpenLib Market incluye:

Registro de usuarios
Inicio de sesión
Catálogo de libros
Carrito de compras
Pagos
Inventario
Wishlist
Pruebas que DEBEN Automatizarse
1. Registro de usuarios

Casos:

Usuario válido
Correo duplicado
Contraseña inválida

Porque son frecuentes y críticas.

2. Login

Casos:

Credenciales válidas
Inválidas
Usuario bloqueado
3. Control de Inventario

Casos:

Stock llega a cero
Libro agotado
Reposición de stock
4. Carrito de Compras

Casos:

Agregar libro
Eliminar libro
Calcular total
5. Procesamiento de Pagos

Casos:

Tarjeta
PSE
PayPal
6. APIs REST

Endpoints:

GET /libros
POST /ordenes
POST /pagos
7. Pruebas de Regresión

Cada despliegue debe ejecutar automáticamente:

Login
Compra
Pago
Inventario
Pruebas que DEBEN Realizarse Manualmente
1. Usabilidad

Verificar:

Facilidad de compra
Claridad de mensajes
Flujo de navegación
2. Diseño Responsive

Probar:

Desktop
Tablet
Móvil
3. Accesibilidad

Validar:

Contraste
Lectores de pantalla
Navegación por teclado
4. Exploratorias

Buscar comportamientos inesperados.

Ejemplo:

Múltiples pestañas abiertas
Sesiones simultáneas
Navegación no prevista
5. Validación de Experiencia de Usuario

Comprobar que:

La búsqueda es intuitiva
El checkout es sencillo
Los mensajes son claros
Estrategia Recomendada para el Primer Release
Distribución Aproximada
70% Automatizadas
30% Manuales
Automatizadas
Unitarias (JUnit + Mockito)
Integración
APIs
Regresión
Smoke Tests
Manuales
Exploratorias
UX
Accesibilidad
Responsive
Conclusión

Las pruebas manuales son indispensables para evaluar usabilidad, experiencia de usuario y escenarios exploratorios, pero resultan costosas y lentas para tareas repetitivas. Las pruebas automatizadas requieren una inversión inicial mayor, pero ofrecen rapidez, consistencia y un alto retorno de inversión en funcionalidades críticas y de ejecución frecuente. Para el primer release de OpenLib Market, la mejor estrategia es una combinación equilibrada: automatizar los procesos de negocio críticos (registro, login, carrito, pagos e inventario) y mantener pruebas manuales para validar experiencia de usuario, accesibilidad y comportamiento exploratorio del sistema.

### Análisis crítico de la respuesta
El LLM identificó correctamente las principales ventajas y desventajas de ambos enfoques y mencionó adecuadamente el costo de mantenimiento de las pruebas automatizadas. Los criterios para decidir cuándo automatizar fueron prácticos y realistas, considerando frecuencia de ejecución, riesgo de negocio, estabilidad de requisitos y retorno de inversión. La recomendación para OpenLib Market también es coherente, ya que automatiza funcionalidades críticas como pagos, inventario y carrito, mientras reserva las pruebas manuales para aspectos de usabilidad y experiencia de usuario. Como posible omisión, faltó profundizar en problemas comunes de la automatización como los flaky tests, la dependencia de ambientes de prueba y el esfuerzo de actualización cuando la interfaz cambia frecuentemente.

#### 1. ¿Qué hizo bien el prompt?

El prompt fue claro y específico al solicitar una comparación entre pruebas manuales y automatizadas incluyendo ventajas, desventajas, tipos de pruebas, criterios de decisión y una recomendación aplicada a OpenLib Market. Además, exigir una tabla comparativa seguida de explicaciones ayudó a obtener una respuesta organizada, completa y fácil de analizar. La parte más efectiva fue pedir criterios concretos para decidir cuándo automatizar una prueba y cuándo realizarla manualmente, ya que permitió obtener recomendaciones prácticas y no solo definiciones teóricas.



#### 2. ¿Qué se puede mejorar?

El prompt pudo solicitar ejemplos más concretos de herramientas utilizadas en cada enfoque y una discusión más profunda sobre métricas como cobertura de pruebas, tiempo de ejecución y costo de mantenimiento. También habría sido útil pedir una priorización de las pruebas automatizadas para un primer release con recursos limitados. Aunque la respuesta mencionó el mantenimiento de las pruebas automatizadas, pudo profundizar más en problemas como pruebas inestables (flaky tests) y falsos positivos.


#### 3. Respuesta final

Las pruebas manuales y automatizadas cumplen roles complementarios en una estrategia de calidad. Las manuales son ideales para evaluar usabilidad, experiencia de usuario, accesibilidad y pruebas exploratorias, mientras que las automatizadas permiten ejecutar rápidamente pruebas repetitivas, de regresión, integración y validación de funcionalidades críticas. La decisión de automatizar debe basarse en factores como frecuencia de ejecución, riesgo de negocio, estabilidad de requisitos y retorno de inversión, considerando además el costo de mantenimiento de los scripts. Para OpenLib Market, lo más adecuado es automatizar funcionalidades críticas como registro, login, carrito, inventario, pagos y APIs REST, y mantener pruebas manuales para validar experiencia de usuario, accesibilidad, diseño responsive y escenarios exploratorios.

