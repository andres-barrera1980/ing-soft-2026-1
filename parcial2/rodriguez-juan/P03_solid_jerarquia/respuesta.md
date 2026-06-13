# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta 3: P.3

### Estudiante

- **Nombre completo**: Juan David Rodriguez Franco

---

### LLM utilizado

| Campo                                   | Valor            |
| --------------------------------------- | ---------------- |
| **Nombre del LLM**                | respuesta propia |
| **Modelo específico**            | N/A              |
| **¿Por qué elegiste este LLM?** | Sin IA           |

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
> [Pega aquí la respuesta COMPLETA del LLM, sin editar, sin resumir.
> Incluye TODO el texto, código, explicaciones que generó el LLM.

Si el LLM generó código, asegúrate de que esté correctamente formateado.
Si tuviste que hacer varias iteraciones, pega la MEJOR respuesta obtenida,
pero menciona cuántas iteraciones hiciste.]

```
 
---
Los 2 pricipios SOLID violados fueron : single responsibility y segregacion de interface. El primero es porque en la interfaz se estan mezclando varios roles en una misma interface. Siento que que un usuario pueda comprar y al mimso tiempo gestionar usuarios en una misma interface no esta bien organizado. Para casos asi, es mejor delegarlas a "administrador" y "Comprador" para asi distribuir mejor esas responsabilidades. Con respecto segregacion de interface, veo problemas en la interaccion de usuario y comprador por las misma razones. se estan implemetando en la interface de usuario metodos que despues no se usan en comprador, por ejemplo. lo que provoca UnsupportedOperationException. Se deben crear interfaces específicas como PuedeComprar, PuedeVender,etc. Cada tipo de usuario implementará únicamente las capacidades que necesita.

```
