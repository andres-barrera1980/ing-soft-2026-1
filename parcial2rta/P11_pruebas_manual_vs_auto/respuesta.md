# Plantilla de entrega — Parcial 2

---

## Pregunta 11: Analizar ventajas, desventajas y criterios de decisión

### Estudiante
- **Nombre completo**: Samuel Iregui

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Antigravity |
| **Modelo específico** | Gemini 3.5 Flash |
| **¿Por qué elegiste este LLM?** | Elegi este modelo por comodidad propia y debido a que tengo pago gemini pro y ya tengo todo bien organizado para los diferentes trabajos |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```text
Actúa como un QA Lead y estratega de pruebas de software. Necesito definir la estrategia de testing para el primer release de OpenLib Market.

Por favor, realiza una comparación profunda entre pruebas manuales y pruebas automatizadas, detallando:
1. Ventajas de las pruebas manuales (mínimo 3).
2. Desventajas de las pruebas manuales (mínimo 3).
3. Ventajas de las pruebas automatizadas (mínimo 3).
4. Desventajas de las pruebas automatizadas (mínimo 3).
5. Criterios claros y específicos para decidir cuándo es conveniente automatizar una prueba y cuándo debe ser manual.
6. Una recomendación concreta sobre qué componentes o flujos de OpenLib Market deberían probarse de forma manual y cuáles de forma automatizada en el release 1.

Formato de salida esperado: Markdown estructurado con tablas e incisos.
```

---

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

### 1. Pruebas Manuales vs Automatizadas

#### Pruebas Manuales
Las pruebas manuales involucran a testers humanos que ejecutan casos de prueba paso a paso interactuando directamente con el software, observando la interfaz y el comportamiento de manera empírica.

*   **Ventajas**:
    1.  **Intuición Humana y Usabilidad**: Los testers humanos pueden detectar problemas de UX (experiencia de usuario) o de diseño visual que un script automatizado ignoraría por completo.
    2.  **Pruebas Exploratorias**: Permite al tester "desviarse" del guion establecido para probar escenarios inusuales o creativos sobre la marcha.
    3.  **Bajo Costo Inicial**: No requiere programar scripts ni configurar complejas suites de CI/CD, lo que agiliza las pruebas en fases muy tempranas.
*   **Desventajas**:
    1.  **Propenso al Error Humano**: Un tester puede pasar por alto fallos visuales menores debido al cansancio o la rutina.
    2.  **No Repetible a Gran Escala**: Repetir cientos de pruebas manualmente en cada release consume demasiado tiempo y retrasa el despliegue.
    3.  **Inviabilidad de Ciertas Pruebas**: Es imposible simular 10,000 usuarios concurrentes (pruebas de carga) de forma manual.

#### Pruebas Automatizadas
Las pruebas automatizadas utilizan herramientas de software (como Selenium, JUnit, Mockito) para ejecutar scripts previamente codificados que validan los resultados esperados de forma programática.

*   **Ventajas**:
    1.  **Ejecución Rápida y Repetible**: Corren en segundos o minutos y se pueden ejecutar miles de veces sin costo operacional adicional.
    2.  **Integración en CI/CD**: Permiten el "Shift-Left Testing", detectando bugs en el instante exacto en que un desarrollador sube su código.
    3.  **Precisión**: El script valida exactamente lo programado sin desviaciones, garantizando que las regresiones sean detectadas con alta efectividad.
*   **Desventajas**:
    1.  **Alto Costo de Desarrollo y Mantenimiento**: Escribir los tests toma tiempo de desarrollo. Además, si la interfaz de usuario cambia, los scripts se rompen ("flakiness") y requieren mantenimiento constante.
    2.  **Falsa Sensación de Seguridad**: Si las pruebas están mal escritas o desactualizadas, darán luz verde aunque existan fallos reales.
    3.  **Falta de Flexibilidad**: No pueden detectar errores de usabilidad fuera de las aserciones exactas del código.

---

### 2. Criterios de Decisión para Automatizar

| Automatizar si... | Probar Manualmente si... |
|---|---|
| El flujo es crítico y de alto riesgo (ej: Pagos). | La interfaz gráfica cambia frecuentemente (UI inestable). |
| La prueba se ejecutará repetidamente en cada commit. | Es una prueba de usabilidad, diseño o UX visual. |
| El comportamiento es estable y no cambia a corto plazo. | Es una prueba exploratoria para buscar nuevas fallas. |
| Se requieren pruebas de rendimiento, carga o volumen. | Es un flujo de configuración inicial que se ejecuta una sola vez. |

---

### 3. Recomendación para el Release 1 de OpenLib Market

1.  **Pruebas Automatizadas**:
    *   **Procesamiento de Pagos**: Cada método (Tarjeta, PSE, PayPal) debe validarse con unit y integration tests para asegurar que el dinero se cobre y registre correctamente.
    *   **Carrito de Compras**: Pruebas unitarias para validar las reglas de stock, el límite de 10 ítems y el cálculo exacto de totales.
    *   **Autenticación**: Scripts para registrar usuarios, login y control de accesos por roles.
2.  **Pruebas Manuales**:
    *   **Interfaz de Usuario del Catálogo**: La visualización de portadas de libros, tipografías y alineación en el frontend JavaFX.
    *   **Flujo Completo de Usuario (End-to-End)**: Navegar en el catálogo, buscar libros, agregarlos al carrito y pagar, simulando la experiencia de un cliente real.
    *   **Panel de Administración**: Pruebas exploratorias en el módulo de moderación de comentarios para validar flujos poco comunes.

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

El prompt solicita de forma clara y cuantitativa las ventajas y desventajas de ambos enfoques de testing, forzando al LLM a dar respuestas simétricas y a contextualizarlas con el caso específico de OpenLib Market.

#### 2. ¿Qué se puede mejorar?

El prompt pudo indagar en los costos de infraestructura (servidores de CI/CD, licencias de herramientas) que acompañan a la automatización, para obtener una comparación económica y financiera más profunda en lugar de quedarse solo en el aspecto técnico de la ejecución.

#### 3. Respuesta final

En conclusión, la respuesta del LLM es correcta y proporciona una base sólida para la estrategia de QA de OpenLib Market:
- **Pruebas Automatizadas:** Se deben enfocar en la lógica de negocio estable y de alto riesgo (cálculo de totales en carrito, procesamiento de pagos, pasarelas e integraciones de bases de datos) mediante Unit Tests e Integration Tests. Esto es vital para asegurar que futuros cambios no rompan flujos clave (Regresión).
- **Pruebas Manuales:** Siguen siendo irremplazables para la experiencia de usuario (UX), el diseño visual (UI), y pruebas exploratorias rápidas de nuevas funcionalidades.

**Concepto omitido / Crucial a destacar (Costo de Mantenimiento y ROI):**
El LLM no profundizó en la trampa del **Costo de Mantenimiento de la Automatización**. Automatizar no siempre ahorra dinero; si una funcionalidad está en constante cambio (como la UI de un startup), el costo de actualizar y reparar constantemente los scripts automatizados rotos ("test maintenance overhead") supera con creces el costo de realizar una prueba manual corta. La automatización se justifica cuando la funcionalidad es madura y el número de ejecuciones estimadas amortiza el costo de desarrollo del test.
