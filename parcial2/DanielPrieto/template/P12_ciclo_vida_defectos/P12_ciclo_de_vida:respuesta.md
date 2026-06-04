# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [12]: [P12 ciclo de vida]

### Estudiante
- **Nombre completo**: [Daniel Prieto]

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [**Sin IA — respuesta propia**] |
| **Modelo específico** | [N/A] |
| **¿Por qué elegiste este LLM?** | [porque decidi retarme a resolver el sin ia, ya que confio en mis conocimientos adquiridos en clase y considero que tengo dominio del tema] |

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

---

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

[Evalúa tu propio prompt, no la respuesta del LLM. ¿El contexto fue suficiente? ¿Las restricciones fueron claras? ¿El formato de salida que pediste ayudó a obtener una buena respuesta? ¿Qué parte de tu prompt fue más efectiva? Sé específico: menciona fragmentos concretos de tu prompt que funcionaron bien.]


#### 2. ¿Qué se puede mejorar?

[¿Qué le faltó a tu prompt? ¿Qué harías diferente si pudieras reformularlo? ¿El LLM entendió mal algo por falta de claridad en tu prompt? ¿La respuesta tiene errores u omisiones? ¿Qué no cubrió el LLM que tú sí sabes por lo visto en clase?]


#### 3. Respuesta final

[Defecto: botón “Agregar al carrito” no responde con más de 5 ítems (el límite real del sistema es 10, entonces esto es un bug real — el frontend bloquea en 5 cuando debería en 10, o el backend lanza excepción sin retroalimentación visual).



|Fase        |Responsable   |Acción                                                                                            |Estado en Jira |
|------------|--------------|--------------------------------------------------------------------------------------------------|---------------|
|Detección   |Tester        |Reproduce y documenta con pasos                                                                   |—              |
|Reporte     |Tester        |Crea issue en Jira con severidad, pasos, resultado esperado vs obtenido                           |*Open*       |
|Asignación  |Tech Lead / PM|Asigna al desarrollador responsable del módulo de carrito                                         |*Assigned*   |
|Diagnóstico |Desarrollador |Revisa código del frontend (¿límite hardcodeado en 5?) y del backend (¿excepción sin catch en UI?)|*In Progress*|
|Corrección  |Desarrollador |Fix + PR con prueba de regresión                                                                  |*In Review*  |
|Verificación|Tester        |Reproduce los pasos originales en el build con el fix                                             |*In Testing* |
|Cierre      |Tester / PM   |Confirma corrección, cierra el ticket                                                             |*Closed*     |

Causa raíz probable: el frontend tiene el límite hardcodeado en 5 (en lugar de consumirlo de la API), o la excepción del backend no retorna un mensaje de error que el frontend maneje, causando silencio total en la UI. ]
