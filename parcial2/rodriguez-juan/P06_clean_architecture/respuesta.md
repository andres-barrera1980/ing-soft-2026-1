# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta 6_P.6

### Estudiante

- **Nombre completo**: Juan David Rodriguez Franco

---

### LLM utilizado

| Campo                                   | Valor  |
| --------------------------------------- | ------ |
| **Nombre del LLM**                | no IA  |
| **Modelo específico**            | N/A    |
| **¿Por qué elegiste este LLM?** | SIN IA |

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
entidades: donde se tienen las relgas de negocio
casos de uso: se explica el flujo de aplicacion del sistema
adaptadores: se conecta logica con programas exteriores
Frameworks: tipo REACT

La regla principal es que las dependencias solo pueden apuntar hacia las capas internas. Para lograrlo, se utiliza inversión de dependencia. los casos de uso dependen de interfaces, mientras que las implementaciones concretas se encuentran en las capas externas.

en clean arquitcture, las reglas de negocio no dependen de frameworks para asi tener un mejor control y mantenimiento del codigo.
```
