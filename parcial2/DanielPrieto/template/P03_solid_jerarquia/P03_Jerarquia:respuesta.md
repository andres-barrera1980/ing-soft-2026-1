# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [03]: [P03_Jearaquia]

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

[Principios violados:

LSP (Liskov Substitution): Vendedor extends Comprador y lanza UnsupportedOperationException en comprar(). Si se sustituye un Comprador por un Vendedor, el programa se rompe en tiempo de ejecución. Esto es la definición textual de violación de LSP.

ISP (Interface Segregation): La interfaz Usuario tiene 5 métodos que ningún tipo de usuario implementa completo. Comprador lanza excepciones en 4 de 5 métodos. Una interfaz gorda que obliga a implementar lo que no se usa.

Rediseño correcto:

public interface Comprador { void comprar(Libro libro); }
public interface Vendedor { void vender(Libro libro); void generarReporteVentas(); }
public interface Moderador { void moderarComentario(Comentario c); }
public interface Administrador extends Comprador, Moderador { void gestionarUsuarios(); }

public class UsuarioComprador implements Comprador { ... }
public class UsuarioVendedor implements Vendedor { ... }
public class UsuarioAdministrador implements Administrador, Vendedor { ... }


Sin herencia forzada, sin UnsupportedOperationException, cada rol implementa solo lo que le corresponde.]
