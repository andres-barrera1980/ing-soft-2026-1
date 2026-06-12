# Plantilla de entrega — Parcial 2

---

## Pregunta P11: Pruebas Manuales vs Automatizadas

### Estudiante
- **Nombre completo**: Ivan Wright

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Gemini |
| **Modelo especifico** | Gemini 3.1 Pro |
| **¿Por que elegiste este LLM?** | Lo elegi porque es muy capaz de estructurar comparativas y listados claros, lo cual es ideal para definir estrategias conceptuales de QA y DevOps. |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```text
hola gemini, actua como un QA Manager.

[CONTEXTO]
En OpenLib Market estamos definiendo nuestra estrategia de testing para el primer release (V1.0) de la plataforma. Tenemos que decidir que probar de forma manual y que probar de forma automatizada.

[PROBLEMA]
Necesito que me hagas una comparativa completa entre pruebas manuales y automatizadas. Debes incluir exactamente lo siguiente:
1. Ventajas de cada una (minimo 3 por enfoque).
2. Desventajas de cada una (minimo 3 por enfoque).
3. Tipos de pruebas que aplican mejor a cada enfoque.
4. Criterios claros para decidir cuando automatizar y cuando hacer manual.
5. Una recomendacion concreta para OpenLib Market sobre que debemos automatizar y que dejar manual.

[RESTRICCIONES]
- Ve directo al grano, usa viñetas para que sea facil de leer.
- Las recomendaciones para OpenLib Market deben ser realistas para un primer release.

[FORMATO]
Entregame todo en markdown, separando claramente cada uno de los 5 puntos solicitados en la seccion del problema.

[VALIDACION]
Sabre que tu respuesta es util si los criterios de decision no son genericos, sino cosas practicas que un QA Manager usaria en la vida real para cuidar el presupuesto del proyecto.
```

---

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```markdown
¡Hola! Excelente decisión planear la estrategia de testing antes del primer release. Aquí tienes la comparativa directa entre pruebas manuales y automatizadas para guiar la estrategia de OpenLib Market.

### 1. Ventajas

**Pruebas Manuales:**
*   **Empatía humana:** El tester puede evaluar la estética, la usabilidad (UX) y la "sensación" de la aplicación como lo haría un usuario real, algo que una máquina no puede sentir.
*   **Flexibilidad instantánea:** Si un tester nota un comportamiento extraño fuera del guion, puede desviarse inmediatamente para investigar y cazar el bug de forma creativa.
*   **Baja inversión inicial:** No requieren configurar infraestructuras complejas, comprar licencias de herramientas ni saber programar scripts para empezar a probar hoy mismo.

**Pruebas Automatizadas:**
*   **Velocidad de ejecución:** Pueden ejecutar miles de casos de prueba complejos en minutos, siendo fundamentales para la Integración Continua (CI/CD).
*   **Consistencia absoluta:** Un script automatizado jamás se aburre, se distrae o pasa por alto un paso, eliminando el error humano por fatiga.
*   **Ejecución desatendida:** Se pueden programar para que corran a las 3:00 AM y tener los reportes de calidad listos para el equipo a primera hora de la mañana.

### 2. Desventajas

**Pruebas Manuales:**
*   **Lentitud e ineficiencia:** Son pésimas para tareas repetitivas, como llenar un formulario de registro 100 veces seguidas con datos distintos.
*   **Propensas a errores de monotonía:** La repetición constante hace que los testers se confíen y omitan detalles en regresiones largas.
*   **Inviabilidad de carga:** Es físicamente imposible simular manualmente a 10,000 usuarios comprando libros al mismo tiempo en el sistema.

**Pruebas Automatizadas:**
*   **Inversión inicial muy alta:** Requieren mucho tiempo, esfuerzo y programadores especializados (SDETs) para configurar el entorno y escribir los primeros scripts.
*   **Falta de intuición visual:** Un script no notará si el color del botón "Comprar" se camufla con el fondo, a menos que esté programado específicamente para validar ese píxel.
*   **Falsos positivos por cambios de UI:** Cambios estéticos menores en la interfaz gráfica (como mover de lugar un campo) pueden hacer que las pruebas fallen aunque la lógica de negocio esté perfecta (fragilidad).

### 3. Tipos de Pruebas que aplican a cada enfoque

*   **Enfoque Manual:** Pruebas Exploratorias, Pruebas de Usabilidad (UX/UI), Pruebas de Aceptación del Usuario (UAT) y Pruebas Ad-hoc.
*   **Enfoque Automatizado:** Pruebas Unitarias, Pruebas de Regresión masivas, Pruebas de Carga/Estrés y Pruebas de APIs.

### 4. Criterios de decisión (Cuándo usar cuál)

**Automatiza si:**
*   El caso de prueba se repite muy frecuentemente (ej. flujo de login o carrito).
*   Es tedioso y propenso a errores humanos por volumen de datos.
*   La funcionalidad es el "core" crítico del negocio y debe validarse en cada commit.
*   La funcionalidad ya es madura y no sufrirá rediseños constantes en la interfaz.

**Hazlo manual si:**
*   Es una funcionalidad nueva que apenas está iterando diseño y cambia todos los días.
*   El flujo solo se va a ejecutar una o dos veces en el ciclo de vida del proyecto.
*   Se requiere validar accesibilidad y experiencia estética humana.

### 5. Recomendación estratégica para OpenLib Market (Primer Release)

Para el primer release (V1.0), recomiendo **automatizar absolutamente todo el flujo de compras a nivel visual (End-to-End)** mediante herramientas como Selenium o Cypress. Deben crear scripts automatizados para la UI que validen todo el proceso en el navegador: buscar el libro, agregarlo al carrito, interactuar con el formulario de envío y procesar el pago, cubriendo todas las resoluciones y variaciones de dispositivos móviles. Lo único que deberían dejar como prueba manual es la revisión de que los textos legales y los colores de la marca en la cabecera estén correctos. Todo lo demás, especialmente en la UI del carrito, debe arrancar 100% automatizado.
```

---

### Analisis critico de la respuesta

#### 1. ¿Que hizo bien el prompt?

Primeramente el prompt fue directo al exigir cinco puntos super concretos, incluyendo los criterios de decision y la recomendacion final aplicada a OpenLib Market. Ahi le quite el modo enciclopedia a la IA y la obligue a darme respuestas aplicadas al contexto de nuestro proyecto. Basicamente restringir la recomendacion a un "primer release" fue la jugada clave en el prompt para poder evaluar la verdadera capacidad estrategica del LLM.


#### 2. ¿Que se puede mejorar?

Seguidamente veo que a mi prompt le falto pedirle que considerara explicitamente el presupuesto y el ciclo de vida a largo plazo del proyecto. Al no hacer eso, la IA solo se enfoco en el costo inicial de infraestructura y cayo en la trampa comun de pensar que la automatizacion grafica (UI) lo soluciona todo sin consecuencias.


#### 3. Respuesta final

Basicamente el LLM capturo bien las ventajas base y los criterios teoricos para decidir que automatizar. Sus definiciones de pruebas exploratorias y regresion son muy acertadas. Sin embargo, su recomendacion final para OpenLib Market es completamente irrealista y fragil.

El LLM recomendo automatizar todo el flujo E2E desde la UI para un primer release. Aca hay un problema grave, porque *como vimos en clase con la Piramide de Pruebas, las pruebas de UI son las mas lentas, fragiles y caras de automatizar*. En una V1.0 la interfaz cambia constantemente. Si automatizamos toda la UI hoy, los scripts se van a romper mañana simplemente porque un diseñador movio un boton o cambio el ID de un campo. 

Ahi es donde entra la desventaja critica que el LLM omitio por completo: **el altisimo costo de mantenimiento a largo plazo de los scripts automatizados**. El LLM hablo de inversion inicial, pero ignoro que las pruebas automatizadas son codigo, y el codigo se oxida si no se mantiene.

Para OpenLib Market mi recomendacion real seria invertir la piramide. Primeramente para la V1.0 debemos automatizar intensivamente la logica de negocio por debajo usando Pruebas Unitarias (Carrito) y Pruebas de API (Pagos), ya que son super rapidas y baratas. Seguidamente usaremos pruebas manuales y exploratorias para revisar la UI. La automatizacion pesada E2E con Selenium en la vista grafica solo deberia hacerse mas adelante cuando la UI este fija.
