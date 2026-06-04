# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [4]: [P04_patrones_descuento]

### Estudiante
- **Nombre completo**: [Jhoan Galeano]

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [Claude] |
| **Modelo específico** | [Ej: Claude 3.1 pro |
| **¿Por qué elegiste este LLM?** | [Use Claude poeque ayuda a razonar de una mejor manera y para esta pregunta es necesario un analisis mas a fondo] |

---

### Prompt utilizado

Actúa como un ingeniero de software senior y experto en arquitectura. Analiza el siguiente requerimiento para el módulo de facturación de OpenLib Market:

El sistema necesita aplicar diferentes estrategias de descuento que pueden combinarse entre sí (por ejemplo, aplicar un 10% por fidelidad más un 15% por temporada en la semana del libro, o un 5% por volumen si el carrito supera los $200,000 COP). El precio final debe calcularse dinámicamente según la fecha, el perfil del usuario y el contenido del carrito.

Por favor, genera una respuesta estructurada que cumpla con lo siguiente:
1. Identifica el patrón de diseño más adecuado. IMPORTANTE: Ten en cuenta la posibilidad de COMPOSICIÓN de estrategias (que se puedan aplicar varios descuentos acumulados a la vez).
2. Justifica técnicamente tu elección y descarta explícitamente al menos otras dos alternativas de patrones de diseño, explicando exactamente por qué no sirven para este caso combinatorio.
3. Propón una implementación completa, limpia y funcional en Java (clases e interfaces) junto con un ejemplo de uso en un método main donde se vea cómo se combinan dinámicamente dos descuentos.
---

### Respuesta del LLM
[1. Patrón elegido: Decorator
El requerimiento tiene una palabra clave: composición. Los descuentos no se eligen entre sí, se apilan. Fidelidad + Temporada + Volumen aplicados en cadena sobre el mismo precio. Eso es exactamente lo que resuelve Decorator: envolver un objeto con capas de comportamiento adicional, donde cada capa transforma el resultado de la anterior.

2. Por qué no los otros patrones
Strategy — descartado porque Strategy elige una estrategia entre varias mutuamente excluyentes. No tiene mecanismo nativo para encadenar múltiples estrategias sobre el mismo valor. Habría que crear una clase DescuentoCombinado por cada combinación posible (Fidelidad+Temporada, Fidelidad+Volumen, los tres juntos...), lo que explota en clases con cada nuevo descuento agregado.
Chain of Responsibility — descartado porque su propósito es que uno solo de los eslabones maneje la solicitud y la cadena se detenga. En facturación necesitamos que todos los descuentos aplicables actúen sobre el precio, no que el primero que coincida corte la cadena.
Decorator resuelve ambos problemas: cada descuento envuelve al anterior, todos ejecutan, y agregar uno nuevo es crear una clase sin tocar ninguna existente.
import java.time.LocalDate;
import java.util.List;

// ─── Modelos ──────────────────────────────────────────────────────────────

class Carrito {
    private final List<String> items;
    private final double subtotal;

    Carrito(List<String> items, double subtotal) {
        this.items = items;
        this.subtotal = subtotal;
    }

    double getSubtotal() { return subtotal; }
    List<String> getItems() { return items; }
}

class PerfilUsuario {
    private final String nombre;
    private final boolean esFiel;       // cliente con más de 1 año
    private final int comprasTotales;

    PerfilUsuario(String nombre, boolean esFiel, int comprasTotales) {
        this.nombre = nombre;
        this.esFiel = esFiel;
        this.comprasTotales = comprasTotales;
    }

    boolean esFiel() { return esFiel; }
    int getComprasTotales() { return comprasTotales; }
    String getNombre() { return nombre; }
}

// ─── Interfaz base (Componente Decorator) ─────────────────────────────────

interface CalculadorDescuento {
    double calcular(double precio, PerfilUsuario perfil, Carrito carrito);
    String descripcion();
}

// ─── Componente base: sin descuento ───────────────────────────────────────

class SinDescuento implements CalculadorDescuento {
    public double calcular(double precio, PerfilUsuario perfil, Carrito carrito) {
        return precio;
    }
    public String descripcion() { return "Precio base"; }
}

// ─── Decorator abstracto ──────────────────────────────────────────────────

abstract class DescuentoDecorator implements CalculadorDescuento {
    protected final CalculadorDescuento envuelto;

    DescuentoDecorator(CalculadorDescuento envuelto) {
        this.envuelto = envuelto;
    }
}

// ─── Descuento por fidelidad: 10% si el usuario es cliente fiel ───────────

class DescuentoFidelidad extends DescuentoDecorator {
    private static final double PORCENTAJE = 0.10;

    DescuentoFidelidad(CalculadorDescuento envuelto) {
        super(envuelto);
    }

    public double calcular(double precio, PerfilUsuario perfil, Carrito carrito) {
        double precioActual = envuelto.calcular(precio, perfil, carrito);
        if (perfil.esFiel()) {
            System.out.printf("  [Fidelidad]  -10%%  $%.0f → $%.0f%n",
                precioActual, precioActual * (1 - PORCENTAJE));
            return precioActual * (1 - PORCENTAJE);
        }
        return precioActual;
    }

    public String descripcion() {
        return envuelto.descripcion() + " + Fidelidad(10%)";
    }
}

// ─── Descuento por temporada: 15% en la Semana del Libro (23–30 abril) ────

class DescuentoTemporada extends DescuentoDecorator {
    private static final double PORCENTAJE = 0.15;

    DescuentoTemporada(CalculadorDescuento envuelto) {
        super(envuelto);
    }

    public double calcular(double precio, PerfilUsuario perfil, Carrito carrito) {
        double precioActual = envuelto.calcular(precio, perfil, carrito);
        LocalDate hoy = LocalDate.now();
        boolean esSemanaDeLLibro = hoy.getMonthValue() == 4
                                && hoy.getDayOfMonth() >= 23
                                && hoy.getDayOfMonth() <= 30;
        if (esSemanaDeLLibro) {
            System.out.printf("  [Temporada]  -15%%  $%.0f → $%.0f%n",
                precioActual, precioActual * (1 - PORCENTAJE));
            return precioActual * (1 - PORCENTAJE);
        }
        return precioActual;
    }

    public String descripcion() {
        return envuelto.descripcion() + " + Temporada(15%)";
    }
}

// ─── Descuento por volumen: 5% si el carrito supera $200.000 COP ──────────

class DescuentoVolumen extends DescuentoDecorator {
    private static final double PORCENTAJE = 0.05;
    private static final double UMBRAL = 200_000;

    DescuentoVolumen(CalculadorDescuento envuelto) {
        super(envuelto);
    }

    public double calcular(double precio, PerfilUsuario perfil, Carrito carrito) {
        double precioActual = envuelto.calcular(precio, perfil, carrito);
        if (carrito.getSubtotal() > UMBRAL) {
            System.out.printf("  [Volumen]     -5%%  $%.0f → $%.0f%n",
                precioActual, precioActual * (1 - PORCENTAJE));
            return precioActual * (1 - PORCENTAJE);
        }
        return precioActual;
    }

    public String descripcion() {
        return envuelto.descripcion() + " + Volumen(5%)";
    }
}

// ─── Main ─────────────────────────────────────────────────────────────────

public class Main {
    public static void main(String[] args) {
        PerfilUsuario usuario = new PerfilUsuario("Laura", true, 15);
        Carrito carrito = new Carrito(
            List.of("Clean Code", "Design Patterns", "Refactoring"),
            250_000
        );
        double precioBase = 250_000;

        // Composición dinámica: Fidelidad + Volumen apilados
        System.out.println("=== Caso 1: Fidelidad + Volumen ===");
        CalculadorDescuento caso1 =
            new DescuentoVolumen(
                new DescuentoFidelidad(
                    new SinDescuento()));

        double final1 = caso1.calcular(precioBase, usuario, carrito);
        System.out.printf("  Precio final: $%.0f%n", final1);
        System.out.println("  Cadena: " + caso1.descripcion());

        // Composición dinámica: Fidelidad + Temporada + Volumen apilados
        System.out.println("\n=== Caso 2: Fidelidad + Temporada + Volumen ===");
        CalculadorDescuento caso2 =
            new DescuentoVolumen(
                new DescuentoTemporada(
                    new DescuentoFidelidad(
                        new SinDescuento())));

        double final2 = caso2.calcular(precioBase, usuario, carrito);
        System.out.printf("  Precio final: $%.0f%n", final2);
        System.out.println("  Cadena: " + caso2.descripcion());
    }
}
CASO DE SALIDA 
=== Caso 1: Fidelidad + Volumen ===
  [Fidelidad]  -10%  $250000 → $225000
  [Volumen]     -5%  $225000 → $213750
  Precio final: $213750
  Cadena: Precio base + Fidelidad(10%) + Volumen(5%)

=== Caso 2: Fidelidad + Temporada + Volumen ===
  [Fidelidad]  -10%  $250000 → $225000
  [Volumen]     -5%  $225000 → $213750
  Precio final: $213750
  Cadena: Precio base + Fidelidad(10%) + Temporada(15%) + Volumen(5%)

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

[El contexto fue suficiente, siempre y cuando el prompt presente todo, en este caso, el prompt fue bueno pero falto un poco mas de entendimiento y de conscentimiento directo para que pueda actuar basandose en lo necesario.]


#### 2. ¿Qué se puede mejorar?

[Se puede mejorar el prompt generando preguntas para que la IA se pueda pregunatr y pueda obtener mas respeusta asi misma y con ello pueda cargar su base de datos y dar una respuesta mas certera]


#### 3. Respuesta final

[¿El LLM identificó el patrón adecuado? Para esto, si lo identifico bien, el decorator, ya que el problema requiere aplicar multiples capas de comportamiento.
 ¿Consideró la posibilidad de composición de estrategias? Sí, fue el argumento central de su elección. Explicó que los descuentos no son mutuamente excluyentes, sino que se deben apilar o encadenar 
  ¿Qué alternativas mencionó y fueron correctamente descartadas? Descarto strategy, dado que tocaria crear mas clases para cada combinacion.
   ¿La implementación es correcta y funcional? ¿Qué le faltó?]Sí, la estructura de clases e interfaces respeta la estructura del patrón Decorator.
