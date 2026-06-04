# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [11]: [pruebas_manual_vs_auto]

### Estudiante
- **Nombre completo**: Boris Nicolas Valbuena Gueirsman

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Gemini |
| **Modelo específico** | Gemini 3.5 Flash |
| **¿Por qué elegiste este LLM?** | Es la que más usé durante el curso, por lo cual es la misma que usaré en el parcial. |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```
Actúa como un QA Lead y Software Test Architect con experiencia en pruebas manuales, pruebas automatizadas, CI/CD, testing ágil y aseguramiento de calidad.
Contexto:
Estoy trabajando en OpenLib Market, una plataforma de compra y venta de libros que incluye:

Registro e inicio de sesión de usuarios.
Catálogo y búsqueda de libros.
Carrito de compras.
Procesamiento de pagos.
Gestión de inventario.
Notificaciones por correo.
Panel administrativo.
Necesito comparar pruebas manuales y pruebas automatizadas para definir una estrategia de testing para el primer release del proyecto.
Antes de responder:

Define qué son las pruebas manuales.
Define qué son las pruebas automatizadas.
Enumera al menos 3 ventajas y 3 desventajas de cada enfoque.
Explica qué tipos de pruebas suelen realizarse manualmente.
Explica qué tipos de pruebas suelen automatizarse.
Propón criterios concretos para decidir cuándo automatizar una prueba y cuándo ejecutarla manualmente.
Aplica esos criterios al contexto de OpenLib Market.
Propón una estrategia de testing para el primer release.
Restricciones:

Utiliza ejemplos concretos de OpenLib Market.
Evita respuestas genéricas.
Considera tiempo, costo, mantenimiento y cobertura.
Incluye una recomendación práctica y realista.
Formato de salida:

Definición de Pruebas Manuales
Definición de Pruebas Automatizadas
Tabla Comparativa
Ventajas de las Pruebas Manuales
Desventajas de las Pruebas Manuales
Ventajas de las Pruebas Automatizadas
Desventajas de las Pruebas Automatizadas
Tipos de pruebas apropiadas para cada enfoque
Criterios para decidir entre manual y automatizado
Estrategia recomendada para OpenLib Market
Conclusión
Validación:
La respuesta debe incluir ventajas y desventajas de ambos enfoques, considerar el costo de mantenimiento de pruebas automatizadas y proponer una estrategia realista para OpenLib Market.
```

---

### Respuesta del LLM

> Como QA Lead y Software Test Architect, entiendo perfectamente el reto. Cuando nos enfrentamos al **primer release** de una plataforma de e-commerce como OpenLib Market, el tiempo corre en nuestra contra y el presupuesto suele ser ajustado. No podemos automatizarlo todo, ni podemos depender exclusivamente de las manos humanas si queremos escalar de forma segura.

A continuación, presento el análisis estratégico y la propuesta técnica para asegurar la calidad de nuestro MVP.

---

## Definición de Pruebas Manuales

Las pruebas manuales son el proceso en el cual los ingenieros de QA ejecutan casos de prueba paso a paso de forma humana, interactuando directamente con la aplicación sin el uso de herramientas de automatización o scripts. El tester asume el rol del usuario final para evaluar el comportamiento, la usabilidad y la consistencia visual del sistema, identificando desviaciones frente a los requisitos esperados.

## Definición de Pruebas Automatizadas

Las pruebas automatizadas consisten en el uso de herramientas de software y scripts (escribiendo código en frameworks como Playwright, Cypress o Selenium) para ejecutar de forma preprogramada los casos de prueba, verificar los resultados esperados y comparar los datos de salida. Se enfocan en la eficiencia, la repetitividad y la velocidad de ejecución a gran escala.

---

## Tabla Comparativa

| Factor | Pruebas Manuales | Pruebas Automatizadas |
| --- | --- | --- |
| **Tiempo de Ejecución** | Lento (depende de la velocidad humana). | Ultra rápido (minutos u horas en paralelo). |
| **Costo Inicial** | Bajo (solo horas de diseño de casos). | Alto (tiempo de desarrollo de frameworks y scripts). |
| **Costo a Largo Plazo** | Lineal y repetitivo en cada regresión. | Bajo (ahorro en ejecución, pero requiere mantenimiento). |
| **Mantenimiento** | Bajo (se actualiza el documento de texto). | Alto (los cambios en UI pueden romper los localizadores). |
| **Cobertura de Datos** | Limitada por el factor tiempo. | Alta (permite *Data-Driven Testing* con miles de registros). |

### Ventajas de las Pruebas Manuales

* **Flexibilidad y Adaptabilidad:** Si el flujo de pago de OpenLib Market cambia de diseño de un día para otro, el tester manual se adapta al instante sin bloquear el pipeline.
* **Pensamiento Crítico y Exploratorio:** Un humano puede notar que, aunque el botón "Añadir al carrito" funciona, el contraste del color es molesto o confuso para el usuario.
* **Bajo Costo Inicial:** No requiere configurar infraestructura de CI/CD ni frameworks complejos para empezar a reportar bugs en el registro de usuarios.

### Desventajas de las Pruebas Manuales

* **Propensión al Error Humano:** El cansancio tras probar el buscador de libros por quinta vez consecutiva con 20 filtros diferentes puede hacer que se escape un defecto.
* **No Escalable para Regresión:** Ejecutar manualmente toda la suite de pruebas antes de liberar el primer release consume días enteros de trabajo.
* **Limitación en Pruebas de Carga:** Es imposible simular manualmente a 5,000 usuarios comprando un libro al mismo tiempo en la plataforma.

### Ventajas de las Pruebas Automatizadas

* **Velocidad y Consistencia:** Un script siempre ejecutará los pasos de inicio de sesión de la misma manera exacta y en segundos.
* **Feedback Inmediato en CI/CD:** Permite ejecutar *Smoke Tests* automáticos cada vez que un desarrollador sube código al panel administrativo, detectando fallos al instante.
* **Reutilización:** Una vez escrito el flujo de procesamiento de pagos, se puede ejecutar miles de veces en diferentes entornos (Staging, Producción).

### Desventajas de las Pruebas Automatizadas

* **Alto Costo de Mantenimiento:** Si la estructura HTML del carrito de compras cambia, el script fallará (falso positivo) y requerirá tiempo de desarrollo para corregir los selectores.
* **Falsa Sensación de Seguridad:** El script solo busca lo que programaste que buscara. Si hay un error visual crítico al lado del botón de compra, el script lo ignorará si no está explícitamente codificado.
* **Retorno de Inversión (ROI) Tardío:** Automatizar flujos inestables que cambian constantemente en el MVP genera pérdidas de tiempo y dinero.

---

## Tipos de pruebas apropiadas para cada enfoque

### Pruebas Manuales

* **Pruebas Exploratorias:** Investigar la plataforma OpenLib Market sin un guion estricto para encontrar comportamientos inesperados (ej. jugar con combinaciones raras en el carrito).
* **Pruebas de Usabilidad (UX):** Validar si el proceso de búsqueda de libros y filtrado por género/autor es intuitivo para un usuario real.
* **Pruebas Ad-Hoc / Smoke Inicial:** Validaciones rápidas de nuevas funcionalidades recién entregadas por desarrollo que aún no son estables.

### Pruebas Automatizadas

* **Pruebas de Regresión:** Validar que los cambios nuevos no rompan lo que ya funcionaba (ej. asegurar que al tocar el panel administrativo no se rompa el inicio de sesión).
* **Pruebas de API y Contratos:** Verificar las respuestas, códigos de estado ($200\text{ OK}$, $400\text{ Bad Request}$) y tiempos de respuesta de los endpoints de la pasarela de pagos y el catálogo.
* **Pruebas de Carga y Rendimiento:** Simular picos de tráfico concurrentes en el procesamiento de pagos.
* **Pruebas de Integración Continua (CI):** Pruebas críticas cortas (*Smoke Tests*) integradas en GitHub Actions o GitLab CI.

---

## Criterios para decidir entre manual y automatizado

Para OpenLib Market, utilizaremos una matriz de decisión basada en 4 criterios clave:

1. **Estabilidad de la funcionalidad:** ¿La UI o el flujo lógico van a cambiar en el corto plazo? Si la respuesta es sí, se queda **Manual**. Si está congelado, califica para **Automatizado**.
2. **Frecuencia de ejecución:** ¿Es un flujo que se debe probar en cada commit o release? (Ej: Login, Checkout). Si se ejecuta mucho, se **Automatiza**.
3. **Complejidad de datos / Repetitividad:** ¿Requiere probar el mismo flujo con 50 tipos de tarjetas de crédito o 100 usuarios diferentes? Se **Automatiza**.
4. **Costo vs. Tiempo de ejecución:** Si automatizar el flujo toma 3 días y ejecutarlo manualmente toma 2 minutos una vez al mes, se queda **Manual**.

### Aplicación de criterios en OpenLib Market:

* **Registro e Inicio de Sesión:** **Automatizado**. Es un flujo crítico, altamente estable y que se ejecutará en cada despliegue.
* **Procesamiento de Pagos (Flujo feliz):** **Automatizado**. El checkout es el corazón del negocio; necesitamos asegurar que la API de pagos responda correctamente siempre.
* **Búsqueda y Filtros del Catálogo:** **Manual / Automatizado Mixto**. Automatizaremos la búsqueda básica por API, pero los filtros avanzados de la UI se probarán manualmente debido a los constantes ajustes de diseño del frontend en el MVP.
* **Panel Administrativo (Gestión de Inventario):** **Manual**. Para el primer release, estas pantallas internas sufren cambios constantes según los requerimientos de operación. Automatizarlas ahora dispararía el costo de mantenimiento.
* **Notificaciones por correo:** **Manual** (Para validar diseño visual/spam) + **Automatizado por API** (Para verificar que el servicio *trigger* se dispara).

---

## Estrategia recomendada para OpenLib Market (Primer Release)

Para el primer release de OpenLib Market, aplicaremos la clásica **Pirámide de Automatización** adaptada a un enfoque ágil y realista, priorizando el ROI y protegiendo el flujo de ingresos.

```
       /\
      /  \     Manuales: UX, Panel Admin, Exploratorias (Alta prioridad)
     /----\
    /  UI  \   Automatizadas (End-to-End): Login, Flujo Feliz de Checkout (Pocas)
   /--------\
  /   API    \  Automatizadas: Catálogo, Carrito, Respuestas de Pagos (Core)
 /------------\

```

### 1. Foco en el Core Business (Automatización de API)

* **Acción:** No automatizaremos flujos complejos por la interfaz gráfica (UI) en componentes inestables. En su lugar, crearemos una suite de pruebas de API para el **Catálogo, Carrito y Pagos**.
* **Herramienta:** Postman/Newman o RestAssured.
* **Razón:** Son rápidas de crear, no se rompen si cambia el diseño visual de la página y aseguran que la lógica del negocio funciona.

### 2. Suite de Humo E2E Crítica (Automatización de UI)

* **Acción:** Desarrollar únicamente **3 flujos End-to-End (E2E)** automatizados con **Playwright** o **Cypress**:
1. Login exitoso de usuario.
2. Búsqueda de un libro $\rightarrow$ Añadir al carrito $\rightarrow$ Checkout con pago exitoso.
3. Flujo de registro de usuario nuevo.


* **Integración:** Estos scripts correrán en el pipeline de CI/CD de manera obligatoria antes de cada despliegue a Staging/Producción. Si uno falla, el release se detiene.

### 3. Fase de Certificación Manual (Mitigación de riesgos)

* **Acción:** El equipo de QA se enfocará al 100% de manera manual en:
* El **Panel Administrativo** (Gestión de inventario y visualización de ventas).
* Pruebas de **Usabilidad y Responsive** (Asegurar que OpenLib Market se vea bien en celulares y navegadores web principales).
* Validar visualmente las **Notificaciones por correo** (Formato, enlaces caídos).
* Pruebas exploratorias basadas en riesgos sobre el procesamiento de pagos (Simular caídas de internet del cliente a mitad de la transacción).



### 4. Gestión del Costo de Mantenimiento

Para evitar el "infierno del mantenimiento", se implementará el patrón de diseño **Page Object Model (POM)** en la automatización de UI desde el día uno. Así, si el botón del carrito cambia de ID, solo lo modificamos en un único archivo del proyecto de pruebas y no en los 20 scripts donde se utiliza.

---

## Conclusión

Para el primer release de OpenLib Market, **una estrategia 100% manual es un riesgo operativo, y una estrategia 100% automatizada es un suicidio financiero.** La recomendación práctica es implementar un **enfoque híbrido inteligente (70% Manual - 30% Automatizado en términos de esfuerzo de UI)**. Blindamos el flujo de dinero (Checkout y Login) con scripts automatizados de API y UI críticas embebidos en el CI/CD, garantizando que el mantenimiento sea mínimo. Al mismo tiempo, liberamos tiempo valioso para que el equipo técnico ejecute pruebas manuales de alto valor (UX, panel administrativo y flujos exploratorios) donde el ojo humano es insustituible. Esto nos garantiza salir a producción a tiempo, controlando los costos y asegurando una experiencia sin fricciones para los primeros lectores de la plataforma.
```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

El prompt esta bien porque define claramente el rol del LLM como QA Lead y da un contexto concreto con OpenLib Market. Eso ayuda a que las respuestas no sean genericas.

Tambien es efectivo porque pide ventajas, desventajas, tipos de pruebas, criterios y una estrategia final. Eso obliga a cubrir todo el tema de forma completa.

#### 2. ¿Qué se puede mejorar?

Aunque la respuesta es correcta, varias ideas se explican de forma muy general. Falto profundizar mas en el costo de mantenimiento de las pruebas automatizadas, que es clave en proyectos reales.

Tambien los criterios para decidir manual vs automatizado son algo abstractos y podrian estar mas ligados al impacto en el negocio.

En manuales, falto mencionar con mas fuerza la falta de repetibilidad exacta como desventaja importante.


#### 3. Respuesta final

Las pruebas manuales las hacen personas y permiten flexibilidad y exploracion, pero son lentas, poco escalables y menos consistentes.

Las pruebas automatizadas usan scripts, son rapidas y repetibles, pero tienen alto costo de mantenimiento y solo validan lo programado.

En OpenLib Market, lo manual sirve para UX, panel admin y exploracion. Lo automatizado para login, checkout, carrito y APIs criticas.

Lo mejor es un enfoque hibrido: automatizar lo critico del negocio y dejar lo exploratorio y visual a pruebas manuales.
