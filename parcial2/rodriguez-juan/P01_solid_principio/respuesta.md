# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta 1: P1

### Estudiante

- **Nombre completo**: Juan David Rodriguez Franco

---

### LLM utilizado

| Campo                                   | Valor            |
| --------------------------------------- | ---------------- |
| **Nombre del LLM**                | respuesta propia |
| **Modelo específico**            | N/A              |
| **¿Por qué elegiste este LLM?** | SIN IA           |

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
> [Pega aquí la respuesta COMPLETA del LLM, sin editar, sin resumir.
> Incluye TODO el texto, código, explicaciones que generó el LLM.

Si el LLM generó código, asegúrate de que esté correctamente formateado.
Si tuviste que hacer varias iteraciones, pega la MEJOR respuesta obtenida,
pero menciona cuántas iteraciones hiciste.]

```
Los principios que identifique que se violan son el principio de open/closed y el de single responsability, que yo consiero que es el mas afectado.Esto se debe a que, en el caso de open closed, es imposible querer agregar cosas nuevas sin tener que modificar lo presente. Por esa misma razon, creo que tambien se viola el de single responsibility. Esto se debe a que se guardan libros, se validan , se genera url, etc. Hay demasidas cosas que tienen funciones diferentes dentro de una misma funcion, lo que va directamente en contra de lo que es el principio, donde se delegan las funciones a centrarse en solo una funcion.
```
