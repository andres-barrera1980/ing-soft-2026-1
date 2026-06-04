# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [11]: [P11 Pruebas manuales vs automatizadas]

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

[Pruebas manuales — cuándo usarlas en OpenLib Market:

	•	Pruebas exploratorias (flujos nuevos sin especificación completa)
	•	Pruebas de usabilidad/UX (experiencia del usuario real en el checkout)
	•	Casos de prueba que cambian frecuentemente (MVP en iteración rápida)

Ventajas manuales: detectan problemas de UX que scripts no capturan, no requieren mantenimiento de código, útiles cuando el comportamiento esperado es ambiguo.

Desventajas manuales: no repetibles con exactitud, lentas en regresión, dependientes del tester.

Pruebas automatizadas — cuándo usarlas:

	•	Regresión del carrito de compras, cálculo de pagos, autenticación
	•	Pruebas unitarias de CarritoService, ProcesadorPago
	•	Pipeline CI/CD antes de cada merge

Desventaja que suelen omitir: el costo de mantenimiento. Una suite de 500 tests automatizados que nadie mantiene se convierte en deuda técnica. Si el código cambia y los tests no se actualizan, dan falsa seguridad.

Criterio de decisión: automatizar cuando el caso se ejecuta frecuentemente, la lógica es estable, y el costo de automatización se amortiza en menos de 10 ejecuciones manuales equivalentes. ]
