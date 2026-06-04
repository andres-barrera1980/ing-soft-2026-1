# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [2: [P02_solid_refactor]

### Estudiante
- **Nombre completo**: [Jhoan Galeano]

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [Gemini] |
| **Modelo específico** | [Gemini 1.5 Pro] |
| **¿Por qué elegiste este LLM?** | [Lo elegí porque maneja muy bien el análisis de código en Java y estructura las refactorizaciones de software utilizando patrones de diseño de manera clara] |

---

### Prompt utilizado

Actúa como un ingeniero de software senior y experto en arquitectura de software. Analiza el siguiente método `procesar` de la clase `ProcesadorPago` perteneciente al módulo de compras de OpenLib Market. 

Identifica de forma detallada al menos dos principios SOLID que el equipo junior está violando en este diseño y justifica técnicamente tu respuesta. 

Posteriormente, propón una refactorización completa en Java aplicando el patrón de diseño que consideres más adecuado para resolver el problema de extensibilidad (OpenLib Market necesita soportar múltiples métodos de pago y permitir agregar nuevos en el futuro sin modificar el código existente). Entrega la solución utilizando interfaces y buenas prácticas de Clean Code.

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**
[Pega aquí la respuesta COMPLETA del LLM, sin editar, sin resumir.
Incluye TODO el texto, código, explicaciones que generó el LLM.

Si el LLM generó código, asegúrate de que esté correctamente formateado.
Si tuviste que hacer varias iteraciones, pega la MEJOR respuesta obtenida,
pero menciona cuántas iteraciones hiciste.]
```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

[El prompt fue directo al grano al pedirle explícitamente identificar "al menos dos principios SOLID", lo que obligó al modelo a no quedarse solo con el error más obvio. También, al exigirle una "refactorización completa usando un patrón de diseño", el formato de salida del código fue limpio y estructurado desde el primer intento]


#### 2. ¿Qué se puede mejorar?

[digamos que se puede mejorar que le falto exigir restricciones de arquitectura para hacer un manejo correcto de la creacion de los objetos, ya que omitio diferentes cosas del dodigo. tambien le daria mas contexto de openlibmarket para que el sepa mas y pueda darme una respuesta mas acertada.]


#### 3. Respuesta final

[Escribe tu respuesta definitiva a la pregunta del parcial, integrando lo que aprendiste del LLM pero yendo más allá. Corrige errores, llena omisiones, conecta con conceptos vistos en clase. Esta es tu respuesta: demuestra que tú dominas el tema.]
