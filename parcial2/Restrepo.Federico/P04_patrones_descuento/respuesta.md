# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta 04: Patrones de diseño — Estrategias de descuento

### Estudiante
- **Nombre completo**: Federico Restrepo

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Claude |
| **Modelo específico** | Claude Sonnet 4.5 |
| **¿Por qué elegiste este LLM?** | Claude Sonnet 4.5 tiene buena capacidad de razonamiento sobre patrones de diseño y produce código Java limpio y bien estructurado. Además justifica sus decisiones de diseño con mayor profundidad que otros modelos, lo cual es clave para esta pregunta que exige descartar alternativas. |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```text
Actúa como un Arquitecto de Software Senior especializado en patrones de diseño (GoF).

Contexto: Estoy desarrollando OpenLib Market, un marketplace de libros en Java. Necesito implementar un sistema de descuentos con las siguientes reglas de negocio:

- Descuento por fidelidad: 10% para compradores con más de 5 compras previas.
- Descuento por temporada: 15% en la semana del libro (fecha especial anual).
- Descuento por volumen: 5% si el carrito supera $200.000 COP.
- Sin descuento: precio normal.

RESTRICCIÓN IMPORTANTE: Las estrategias pueden combinarse. Por ejemplo, un cliente fiel durante la semana del libro con carrito mayor a $200.000 podría recibir los tres descuentos aplicados sobre el precio. El precio final debe calcularse dinámicamente según la fecha, el perfil del usuario y el contenido del carrito.

Tarea:
1. Identifica qué patrón de diseño de los GoF es el más adecuado para este escenario.
2. Justifica por qué ese patrón y descarta explícitamente al menos otras DOS alternativas concretas (por ejemplo: State, Template Method, Chain of Responsibility, etc.).
3. Propón una implementación completa en Java 21 con todas las clases e interfaces necesarias, respetando los principios SOLID (especialmente OCP y SRP).
4. Incluye un ejemplo de uso (método main o test) que muestre cómo se aplican múltiples descuentos combinados.

Formato de salida: respuesta en markdown con bloques de código Java correctamente formateados.
```

---

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

*(1 iteración)*

---

#### Patrón recomendado: **Strategy + Decorator**

Para este escenario, el patrón más adecuado es una **combinación de Strategy y Decorator**:

- **Strategy** define el contrato común de cálculo de descuento.
- **Decorator** permite envolver estrategias unas sobre otras para combinar múltiples descuentos de forma dinámica y sin modificar las clases existentes.

---

#### ¿Por qué Strategy + Decorator y no otras alternativas?

**Alternativa 1 descartada: State**
El patrón State modela objetos cuyo comportamiento cambia según su estado interno, y las transiciones entre estados son parte del diseño. Aquí no hay estados del carrito que provoquen transiciones: los descuentos son reglas independientes que se evalúan simultáneamente. State no permite combinar comportamientos, que es el requisito central.

**Alternativa 2 descartada: Template Method**
Template Method define el esqueleto de un algoritmo en una superclase abstracta y deja que las subclases sobrescriban pasos específicos. El problema es que usa herencia, lo que impide combinar descuentos en tiempo de ejecución sin crear una subclase por cada combinación posible (3 descuentos → hasta 7 combinaciones). No escala.

**Alternativa 3 descartada: Chain of Responsibility**
CoR pasa la solicitud a lo largo de una cadena hasta que un manejador la procesa. Implica que solo un manejador responde. Aunque podría adaptarse para que todos los handlers procesen (variante), no es su intención original y el diseño se vuelve confuso. Decorator es más explícito para este caso: la combinación es composición, no cadena.

---

#### Implementación en Java 21

```java
// Contexto del carrito necesario para evaluar las reglas
public record ContextoDescuento(
    int comprasPrevias,
    boolean esSemanadelLibro,
    java.math.BigDecimal totalCarrito
) {}

// Contrato común: Strategy
public interface EstrategiaDescuento {
    java.math.BigDecimal aplicar(java.math.BigDecimal precio, ContextoDescuento ctx);
}

// --- Estrategias concretas (Strategy) ---

public class SinDescuento implements EstrategiaDescuento {
    @Override
    public java.math.BigDecimal aplicar(java.math.BigDecimal precio, ContextoDescuento ctx) {
        return precio;
    }
}

public class DescuentoFidelidad implements EstrategiaDescuento {
    private static final java.math.BigDecimal DESCUENTO = new java.math.BigDecimal("0.10");

    @Override
    public java.math.BigDecimal aplicar(java.math.BigDecimal precio, ContextoDescuento ctx) {
        if (ctx.comprasPrevias() > 5) {
            return precio.multiply(java.math.BigDecimal.ONE.subtract(DESCUENTO));
        }
        return precio;
    }
}

public class DescuentoTemporada implements EstrategiaDescuento {
    private static final java.math.BigDecimal DESCUENTO = new java.math.BigDecimal("0.15");

    @Override
    public java.math.BigDecimal aplicar(java.math.BigDecimal precio, ContextoDescuento ctx) {
        if (ctx.esSemanadelLibro()) {
            return precio.multiply(java.math.BigDecimal.ONE.subtract(DESCUENTO));
        }
        return precio;
    }
}

public class DescuentoVolumen implements EstrategiaDescuento {
    private static final java.math.BigDecimal DESCUENTO = new java.math.BigDecimal("0.05");
    private static final java.math.BigDecimal UMBRAL = new java.math.BigDecimal("200000");

    @Override
    public java.math.BigDecimal aplicar(java.math.BigDecimal precio, ContextoDescuento ctx) {
        if (ctx.totalCarrito().compareTo(UMBRAL) > 0) {
            return precio.multiply(java.math.BigDecimal.ONE.subtract(DESCUENTO));
        }
        return precio;
    }
}

// --- Decorador base (Decorator) ---

public abstract class DescuentoDecorador implements EstrategiaDescuento {
    protected final EstrategiaDescuento envuelta;

    public DescuentoDecorador(EstrategiaDescuento envuelta) {
        this.envuelta = envuelta;
    }
}

// Los decoradores concretos delegan al envuelto primero y luego aplican su regla
public class DecoradorFidelidad extends DescuentoDecorador {
    private final EstrategiaDescuento fidelidad = new DescuentoFidelidad();

    public DecoradorFidelidad(EstrategiaDescuento envuelta) {
        super(envuelta);
    }

    @Override
    public java.math.BigDecimal aplicar(java.math.BigDecimal precio, ContextoDescuento ctx) {
        java.math.BigDecimal precioBase = envuelta.aplicar(precio, ctx);
        return fidelidad.aplicar(precioBase, ctx);
    }
}

public class DecoradorTemporada extends DescuentoDecorador {
    private final EstrategiaDescuento temporada = new DescuentoTemporada();

    public DecoradorTemporada(EstrategiaDescuento envuelta) {
        super(envuelta);
    }

    @Override
    public java.math.BigDecimal aplicar(java.math.BigDecimal precio, ContextoDescuento ctx) {
        java.math.BigDecimal precioBase = envuelta.aplicar(precio, ctx);
        return temporada.aplicar(precioBase, ctx);
    }
}

public class DecoradorVolumen extends DescuentoDecorador {
    private final EstrategiaDescuento volumen = new DescuentoVolumen();

    public DecoradorVolumen(EstrategiaDescuento envuelta) {
        super(envuelta);
    }

    @Override
    public java.math.BigDecimal aplicar(java.math.BigDecimal precio, ContextoDescuento ctx) {
        java.math.BigDecimal precioBase = envuelta.aplicar(precio, ctx);
        return volumen.aplicar(precioBase, ctx);
    }
}
```

---

#### Ejemplo de uso: múltiples descuentos combinados

```java
public class EjemploDescuentos {
    public static void main(String[] args) {
        // Cliente fiel, en semana del libro, carrito de $250.000
        ContextoDescuento ctx = new ContextoDescuento(
            8,                                         // más de 5 compras → fidelidad aplica
            true,                                      // semana del libro → temporada aplica
            new java.math.BigDecimal("250000")         // supera $200.000 → volumen aplica
        );

        // Precio original del libro: $50.000 COP
        java.math.BigDecimal precioOriginal = new java.math.BigDecimal("50000");

        // Componer los descuentos con Decorator
        EstrategiaDescuento estrategia = new DecoradorFidelidad(
            new DecoradorTemporada(
                new DecoradorVolumen(
                    new SinDescuento()
                )
            )
        );

        java.math.BigDecimal precioFinal = estrategia.aplicar(precioOriginal, ctx);

        // Precio final: 50000 * 0.95 * 0.85 * 0.90 = $36.382,50 COP
        System.out.printf("Precio original: $%s%n", precioOriginal);
        System.out.printf("Precio final:    $%.2f%n", precioFinal);
    }
}
```

**Salida esperada:**
```
Precio original: $50000
Precio final:    $36382.50
```

---

#### Ventajas de este diseño

| Principio SOLID | Cómo se cumple |
|---|---|
| **SRP** | Cada estrategia tiene una única razón de cambio (su regla de descuento) |
| **OCP** | Agregar un nuevo descuento = nueva clase, sin modificar las existentes |
| **DIP** | El código cliente depende de `EstrategiaDescuento` (abstracción), no de clases concretas |
| **LSP** | Cualquier decorador puede reemplazar a cualquier `EstrategiaDescuento` sin romper el cliente |

---

### Análisis crítico de la respuesta

