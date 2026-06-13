## Pregunta 11

### Estudiante
- **Nombre completo**: Nicolas Silva García

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [**Sin IA — respuesta propia**] |
| **Modelo específico** | [N/A] |
| **¿Por qué elegiste este LLM?** | [Decidi responder sin IA porque es un tema bastante manejable y no requeria de una IA] |

---

### Pregunta 11

En el proceso de desarrollo de software, existen dos enfoques principales para las pruebas: **pruebas manuales** y **pruebas automatizadas**. Cada uno tiene ventajas, desventajas y contextos donde es más apropiado. OpenLib Market está definiendo su estrategia de testing para el primer release.

**Tarea**:

1. **Prompt**: Pídele a tu LLM que compare pruebas manuales y automatizadas considerando: ventajas de cada una (mínimo 3), desventajas de cada una (mínimo 3), tipos de pruebas que aplican a cada enfoque, criterios para decidir cuándo automatizar una prueba y cuándo hacerla manual, y una recomendación concreta de qué pruebas deberían ser manuales y cuáles automatizadas en OpenLib Market. Pega el prompt y la respuesta.
2. **Análisis**: ¿El LLM capturó correctamente las ventajas y desventajas de cada enfoque? ¿Mencionó el costo de mantenimiento de las pruebas automatizadas? ¿Los criterios de decisión son prácticos o genéricos? ¿La recomendación para OpenLib Market es realista para el contexto del proyecto? ¿Qué ventaja o desventaja importante omitió el LLM?

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

---

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**
[Pega aquí la respuesta COMPLETA del LLM, sin editar, sin resumir.
Incluye TODO el texto, código, explicaciones que generó el LLM.

Si el LLM generó código, asegúrate de que esté correctamente formateado.
Si tuviste que hacer varias iteraciones, pega la MEJOR respuesta obtenida,
pero menciona cuántas iteraciones hiciste.]


---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

No aplica, esta respuesta es mia propia

#### 2. ¿Qué se puede mejorar?

No aplica, esta respuesta es mia propia

#### 3. Respuesta final

Pruebas Manuales

ventajas
- Permite descubrir errores visuales, de usabilidad (UX) y flujos ilógicos que un script no puede detectar.
- No requiere programar código ni configurar infraestructura compleja para empezar a probar.
- Ideal para características que cambian constantemente, donde mantener scripts automatizados sería una pérdida de tiempo.

desventajas
- Son extremadamente lentas
- Susceptibles a subjetividad y error humano
- Dificiles de escalar

Pruebas Automatizadas

ventajas
- Se pueden ejecutar miles de veces en minutos (o segundos), garantizando consistencia absoluta.s
- Permiten validar cada commit o Pull Request instantáneamente, previniendo regresiones.
- Facilitan las pruebas basadas en datos, probando un mismo algoritmo con resto de entradas diferentes.

desventajas
- Requieren inversión inicial en tiempo y herramientas
- No detectan problemas de UX/UI
- Requieren mantenimiento constante

Tipos de Pruebas Manuales:

    Pruebas Exploratorias: El tester navega por el programa intentando encontrar errores, basandose en su experiencia.

    Pruebas de Usabilidad (UX/UI): Evaluar si el diseño es intuitivo y estéticamente correcto.

    User Acceptance Testing (UAT): Pruebas de aceptación realizadas por el cliente final o Product Owner para validar si el software cumple las expectativas del negocio.

Tipos de Pruebas Automatizadas:

    Pruebas Unitarias: Validar el comportamiento aislado de funciones o clases.

    Pruebas de Integración: Verificar que diferentes módulos se comuniquen correctamente.

Cuándo Automatizar:

    La tarea es repetitiva y predecible 
    Es un Core Business Path donde un fallo significa pérdida de dinero inmediata 
    Es necesario ejecutar la prueba en múltiples navegadores o dispositivos sistemáticamente.
    Se requiere probar combinaciones masivas de datos 

Cuándo hacer Pruebas Manuales:

    La funcionalidad es un MVP (Producto Mínimo Viable) y su flujo cambiará drásticamente la próxima semana.
    Se requiere validar la "sensación" de la aplicación (¿El botón de checkout invita a hacer clic? ¿El color rojo de "Agotado" se ve bien?).
    Casos de uso de una sola vez cuyo esfuerzo de programación es mayor que el beneficio.
