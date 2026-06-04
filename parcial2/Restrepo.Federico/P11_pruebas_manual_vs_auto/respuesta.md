# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta 11: Pruebas manuales vs automatizadas

### Estudiante
- **Nombre completo**: Federico Restrepo

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Gemini |
| **Modelo específico** | Gemini 3.1 Pro |
| **¿Por qué elegiste este LLM?** | Usé Gemini 3.1 Pro en antigravity, ya que ofrece relativamente una buena cantidad de tokens y ofrece un manejo visual de los archivos que me gusta. |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```text
Actúa como un QA Lead definiendo la estrategia de calidad.

Contexto: OpenLib Market está por lanzar su primer release. Necesito definir cuándo usar pruebas manuales y cuándo automatizadas.

Tarea: Elabora una comparación detallada entre pruebas manuales y automatizadas. Tu respuesta debe incluir estrictamente:
1. Al menos 3 ventajas y 3 desventajas de las pruebas manuales.
2. Al menos 3 ventajas y 3 desventajas de las pruebas automatizadas.
3. Tipos de pruebas específicos que se adaptan mejor a cada enfoque.
4. Criterios de decisión prácticos para decidir si un caso de prueba específico debe automatizarse o hacerse manualmente.
5. Una recomendación concreta y realista sobre qué partes de OpenLib Market (módulo de pagos, búsqueda de libros, UX del carrito, inicio de sesión) deberían ser manuales y cuáles automatizadas para este primer release.

Formato de salida: markdown, bien estructurado con viñetas y tablas si es necesario.
```

---

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

*(1 iteración)*

---

### Comparativa: Pruebas Manuales vs Automatizadas

#### 1. Pruebas Manuales

**Ventajas:**
1. **Juicio humano y empatía:** Un humano puede percibir que un botón tiene un color que confunde, que la navegación es torpe o que la interfaz está desalineada; cosas que un script automatizado ignora si el HTML es técnicamente válido.
2. **Costo inicial bajo y agilidad a corto plazo:** No requiere desarrollar scripts ni configurar frameworks. Un tester puede empezar a probar una funcionalidad apenas está desplegada. Ideal para prototipos o features muy cambiantes.
3. **Descubrimiento (Exploratory Testing):** Los testers humanos pueden salirse del "camino feliz" y explorar el sistema de formas creativas que los programadores no anticiparon, encontrando bugs en flujos no documentados.

**Desventajas:**
1. **Lentas y propensas al error humano:** Ejecutar 200 casos de prueba a mano toma días, y el tester puede olvidar pasos o cansarse, omitiendo bugs por fatiga (especialmente en pruebas de regresión).
2. **No son escalables:** A medida que OpenLib Market crezca, el tiempo necesario para probar todo antes de un release será insostenible sin contratar ejércitos de QA.
3. **Dificultad para simular carga:** Es imposible que un tester manual simule a 5,000 usuarios comprando libros simultáneamente para probar el rendimiento del servidor.

#### 2. Pruebas Automatizadas

**Ventajas:**
1. **Velocidad y repetitividad:** Un conjunto de 1,000 pruebas automatizadas puede correr en minutos de forma consistente, sin saltarse pasos. Son perfectas para Integración Continua (CI/CD).
2. **Eficiencia en pruebas de regresión:** Garantizan que el código nuevo no rompió funcionalidades antiguas. Te dan confianza inmediata después de un refactoring complejo (como cambiar el motor de base de datos).
3. **Cobertura amplia y simulación masiva:** Permiten ejecutar pruebas de carga, estrés y rendimiento inyectando tráfico masivo de forma programada, algo inviable manualmente.

**Desventajas:**
1. **Alto costo inicial y curva de aprendizaje:** Escribir un buen test automatizado (ej. Selenium, Cypress, JUnit) toma tiempo y requiere habilidades de programación.
2. **Costo de mantenimiento (Test Maintenance):** Si la interfaz gráfica cambia frecuentemente (ej. cambias el ID de un botón), las pruebas se rompen ("flaky tests") y requieren tiempo de los desarrolladores para ser reparadas, generando frustración.
3. **Miopía del script:** La automatización solo verifica lo que le programaste que verifique. Si el script verifica que se muestre un mensaje de éxito, pero el mensaje aparece detrás de una imagen que lo tapa, la prueba pasará en verde aunque el usuario no pueda verlo.

---

#### 3. Tipos de pruebas según el enfoque

| Enfoque | Tipos de prueba ideales |
| :--- | :--- |
| **Manuales** | Pruebas Exploratorias, Pruebas de Usabilidad (UX), Pruebas de Accesibilidad, Pruebas de Aceptación del Usuario (UAT), Pruebas Ad-hoc. |
| **Automatizadas** | Pruebas Unitarias, Pruebas de Regresión, Pruebas de Carga/Rendimiento (JMeter), Pruebas de API (Postman/RestAssured), Pruebas de Integración continuas. |

---

#### 4. Criterios de decisión: ¿Cuándo automatizar?

Usa estos criterios prácticos (checklist) para decidir:

**Automatiza si la prueba es:**
*   **Repetitiva:** Se va a ejecutar en cada release (Regresión).
*   **Crítica para el negocio:** Si falla, la empresa pierde dinero (ej. pagos, login).
*   **Determinista:** Siempre da el mismo resultado con los mismos datos de entrada.
*   **Difícil o aburrida de hacer a mano:** Inserción de 500 registros para probar paginación.

**Hazla Manual si la prueba es:**
*   **De alta volatilidad:** La funcionalidad está en constante cambio de diseño.
*   **Subjetiva:** Evalúa qué tan "bonito" o "intuitivo" es algo.
*   **De un solo uso (One-off):** Solo se probará una vez para una campaña específica.
*   **Involucra hardware físico complejo:** Probar lector de huellas o escaner de código de barras físico.

---

#### 5. Recomendación para el primer release de OpenLib Market

Para este primer release (donde los recursos son limitados y hay cosas que aún van a cambiar), la estrategia recomendada es **híbrida (Pirámide de Pruebas + Exploratorio)**:

1.  **Módulo de Pagos y Transacciones (100% Automatizado):**
    *   *Por qué:* Es crítico para el negocio. Un fallo aquí significa pérdida directa de dinero o problemas legales. Los cálculos (ej. el descuento del carrito) son matemáticos y deterministas.
    *   *Acción:* Pruebas unitarias exhaustivas y pruebas de integración de API para la pasarela de pagos.
2.  **Inicio de Sesión y Seguridad (Altamente Automatizado):**
    *   *Por qué:* Es repetitivo y estable. Si el usuario no puede entrar, no hay negocio.
    *   *Acción:* Pruebas End-to-End (Cypress/Selenium) para el flujo crítico de login y registro.
3.  **Búsqueda de Libros (Mixto):**
    *   *Automatizado:* Las APIs que devuelven los resultados de búsqueda (comprobar que al buscar "Harry Potter" el JSON traiga el libro).
    *   *Manual:* Verificar que la cuadrícula de resultados se vea bien en diferentes resoluciones de pantalla (Mobile/Desktop).
4.  **UX del Carrito y Diseño (100% Manual):**
    *   *Por qué:* En un primer release, el frontend va a sufrir muchos ajustes estéticos. Automatizar la interfaz gráfica del carrito (botones de sumar/restar, colores, animaciones) generará altos costos de mantenimiento por tests rotos.
    *   *Acción:* Pruebas exploratorias manuales para garantizar que la experiencia de compra se sienta fluida y agradable para un humano.

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?
Darle los cuatro módulos concretos de OpenLib Market fue lo que más ayudó, la sección de recomendaciones habló directamente de pagos, login, búsqueda y carrito en vez de ejemplos genéricos. El checklist también quedó útil, especialmente la distinción entre "repetitiva y determinista" para automatizar versus "subjetiva y volátil" para hacerlo manual.

#### 2. ¿Qué se puede mejorar?
Decir que pagos es "100% automatizado" me pareció muy absoluto. La UI del flujo de pago, pantallas de confirmación y mensajes de error, necesita al menos una pasada manual. También faltó mencionar que las pruebas E2E de login en Cypress suelen ser las más frágiles, y para un primer release con recursos limitados pueden volverse una carga. Y la pirámide de pruebas aparece en el título pero nunca se explica, si es el concepto central debería describirse aunque sea brevemente.

#### 3. Respuesta final
La respuesta sirve bien como guía para el primer release. Para la próxima le pediría que matice el "100% automatizado" en pagos y que explique la pirámide de pruebas en vez de asumirla conocida.