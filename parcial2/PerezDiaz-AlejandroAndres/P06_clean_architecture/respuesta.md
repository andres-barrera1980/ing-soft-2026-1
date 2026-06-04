# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [P06_clean_arquitecture]

### Estudiante
- **Nombre completo**: [Alejandro Andres Perez DIaz]

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [/ **Sin IA — respuesta propia**] |
| **Modelo específico** | [ "N/A"] |
| **¿Por qué elegiste este LLM?** | [Porque me parecio sencillo ya que esta misma aquitectura es la que utilizamos nosotro en el proyecto.] |

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

[Clean Architecture organiza el código en capas  donde las dependencias solo apuntan hacia adentro, es decir las capas de afuera conocen a las de adentro pero nunca al revés.
Las capas para OpenLib Market serían:

Entidades — las clases del negocio puro como Libro, Usuario, Orden
Casos de uso — lo que el sistema puede hacer como PublicarLibro, ProcesarPago, AgregarAWishlist
Adaptadores — los controladores REST y repositorios que conectan todo
Frameworks — Spring Boot, PostgreSQL, Redis, lo más externo

La regla principal es que Libro no sabe que existe PostgreSQL ni Spring Boot. Si mañana  se quisiera cambiar la base de datos solo tocamos el adaptador, el resto del código no .
La diferencia con la arquitectura tradicional es que en la tradicional todo está conectado de presentación a lógica a datos, entonces si cambias la base de datos tienes que tocar la lógica de negocio también.]

