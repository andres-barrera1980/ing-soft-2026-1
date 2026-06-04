# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [P04_patrones_descuento]

### Estudiante
- **Nombre completo**: [Alejadro Andres Perez Diaz]

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [Claude ] |
| **Modelo específico** | [Sonet 4.6] |
| **¿Por qué elegiste este LLM?** | [como dije antes segun yo es la mejor para analis o cosas de codigo.] |

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
[Actúa como un Ingeniero de Software Senior y experto en arquitectura. 
Considera que las estrategias de descuento pueden combinarse entre sí, por ejemplo fidelidad + temporada al mismo tiempo. Justifica si el patrón elegido soporta esa composición y descarta al menos dos patrones alternativos explicando por qué no son adecuados.

Si el LLM generó código, asegúrate de que esté correctamente formateado.
Si tuviste que hacer varias iteraciones, pega la MEJOR respuesta obtenida,
pero menciona cuántas iteraciones hiciste.]
```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

[Evalúa tu propio prompt, no la respuesta del LLM. ¿El contexto fue suficiente? ¿Las restricciones fueron claras? ¿El formato de salida que pediste ayudó a obtener una buena respuesta? ¿Qué parte de tu prompt fue más efectiva? Sé específico: menciona fragmentos concretos de tu prompt que funcionaron bien.]
-yo diria que el contexto fue suficeinte para realizar esta esta regunta y viedo la respeusta arrogaja por la ia la analise y si me parece correcta.


#### 2. ¿Qué se puede mejorar?

[¿Qué le faltó a tu prompt? ¿Qué harías diferente si pudieras reformularlo? ¿El LLM entendió mal algo por falta de claridad en tu prompt? ¿La respuesta tiene errores u omisiones? ¿Qué no cubrió el LLM que tú sí sabes por lo visto en clase?
-podria mejorar la parte de un mejor contexto porque le di contexto minimo.]


#### 3. Respuesta final

[El patrón correcto: Decorator
La razón es simple — los descuentos se combinan. Strategy no sirve porque elige uno solo. Decorator te permite apilar varios encima del precio original.
javapublic interface EstrategiaDescuento {
    double aplicar(double precio);
}

public class SinDescuento implements EstrategiaDescuento {
    public double aplicar(double precio) {
        return precio;
    }
}

public abstract class DescuentoDecorator implements EstrategiaDescuento {
    protected EstrategiaDescuento descuento;
    public DescuentoDecorator(EstrategiaDescuento descuento) {
        this.descuento = descuento;
    }
}

public class DescuentoFidelidad extends DescuentoDecorator {
    public DescuentoFidelidad(EstrategiaDescuento descuento) {
        super(descuento);
    }
    public double aplicar(double precio) {
        return descuento.aplicar(precio) * 0.90;
    }
}

public class DescuentoTemporada extends DescuentoDecorator {
    public DescuentoTemporada(EstrategiaDescuento descuento) {
        super(descuento);
    }
    public double aplicar(double precio) {
        return descuento.aplicar(precio) * 0.85;
    }
}

public class DescuentoVolumen extends DescuentoDecorator {
    public DescuentoVolumen(EstrategiaDescuento descuento) {
        super(descuento);
    }
    public double aplicar(double precio) {
        return descuento.aplicar(precio) * 0.95;
    }
}
Analisi Critico:
¿El LLM identificó el patrón adecuado?
si identifico el patron correcto  y descarto el de strategy y expico porque
¿Consideró la posibilidad de composición de estrategias?
si la descarto proque elige una sola estrategia y ejecuta esa.
¿Qué alternativas mencionó y fueron correctamente descartadas?
Se descartó correctamente Strategy porque no permite combinar descuentos, y descartó if/else 
¿La implementación es correcta y funcional? ¿Qué le faltó?
Sí, porque la interfaz EstrategiaDescuento, los decorators y el ejemplo de uso combinado funcionan correctamente y permiten agregar descuentos nuevos sin tocar código existente.

]
