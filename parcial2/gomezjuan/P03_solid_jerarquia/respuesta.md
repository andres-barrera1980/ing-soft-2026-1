# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

# Pregunta P03: Evaluación de jerarquía de clases — Principios SOLID violados

### Estudiante
- **Nombre completo**: Juan Camilo Gomez

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** no use
| **Modelo específico** | [Ej: Claude Opus 4.5, GPT-4o, Gemini 2.5 Pro, etc. Si respondes sin IA, escribe "N/A"] |
| **¿Por qué elegiste este LLM?** para tratar de ganarme el bono asi no este completamente bien

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



Interface Segregation Principle 

La interfaz Usuario es una interfaz gruesa que obliga a todas las implementaciones a conocer metodos que no les corresponden:

public interface Usuario {
    void comprar(Libro libro);       // Solo Comprador
    void vender(Libro libro);        // Solo Vendedor
    void moderarComentario(...);     // Solo Administrador
    void generarReporteVentas();     // Solo Administrador/Vendedor
    void gestionarUsuarios();        // Solo Administrador


Comprador implementa Usuario pero lanza UnsupportedOperationException en cuatro de los cinco métodos. Esto viola ISP pues los clientes no deben depender de interfaces que no usan.

por otro lado :Liskov Substitution Principle

`Vendedor` hereda `comprar()` de `Comprador` y lo sobreescribe lanzando una excepción. Esto significa que un `Vendedor` no puede sustituir a un `Comprador` sin romper el comportamiento esperado.

rediseño:

public interface Comprador {
    void comprar(Libro libro);
}

public interface Vendedor {
    void vender(Libro libro);
}

public interface Moderador {
    void moderarComentario(Comentario comentario);
}

public interface Administrador {
    void gestionarUsuarios();
    void generarReporteVentas();
}

public class UsuarioComprador implements Comprador {
    @Override
    public void comprar(Libro libro) {
        // implementación
    }
}

public class UsuarioVendedor implements Comprador, Vendedor {

    @Override
    public void comprar(Libro libro) {
        // implementación
    }

    @Override
    public void vender(Libro libro) {
        // implementación
    }
}

Se aplica ISP porque cada interfaz tiene una responsabilidad especifica.
Ya no hay metodos que lancen UnsupportedOperationException.
También mejora LSP porque ninguna clase implementa comportamientos invalidos.
El diseño es mas flexible para agregar nuevos tipos de usuario.


