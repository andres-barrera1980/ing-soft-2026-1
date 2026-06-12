## P04_patrones_descuento

### Estudiante
- **Nombre completo**: [Alejandro Macias Barrios]

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [Claude] |
| **Modelo específico** | [Claude Sonnet 4.6] |
| **¿Por qué elegiste este LLM?** | [Claude ha demostrado tener un buen rendimiento en tareas de diseño y refactorización de código, así como también en la aplicación de principios de ingeniería de software. Además, ha sido uno de los que mejor hemos usado en clase, y considero que es uno de los modelos más avanzados disponibles.] |

### Prompt utilizado

[Imagina que eres un estudiante de Ingeniería de sistemas de la Pontificia Universidad Javeriana cursando la materia de Fundamentos de Ingeniería de Software. El profesor te ha hecho la siguiente pregunta y debes responderla de la mejor manera posible.

OpenLib Market necesita aplicar diferentes estrategias de descuento durante el año:

- **Descuento por fidelidad**: 10% para compradores con más de 5 compras.
- **Descuento por temporada**: 15% en la semana del libro.
- **Descuento por volumen**: 5% si el carrito supera $200,000 COP.
- **Sin descuento**: precio normal.

Estas estrategias pueden combinarse (ej: un cliente fiel puede recibir descuento de temporada + fidelidad). El precio final debe calcularse dinámicamente según la fecha, el perfil del usuario, y el contenido del carrito.

Identifique qué patrón de diseño de los vistos en clase es el más adecuado para este escenario, y que proponga una implementación en Java con clases, interfaces y un ejemplo de uso. Exígele que justifique por qué ese patrón y descarte al menos otras dos alternativas.]

### Respuesta del LLM

[Análisis del problema y selección del patrón
Descartando alternativas
❌ Strategy (solo)
Strategy permite intercambiar un algoritmo por otro, pero el enunciado exige que los descuentos se combinen. Con Strategy puro habría que crear una clase por cada combinación posible: FidelidadYTemporada, FidelidadYVolumen, TodosLoDescuentos... explosión combinatoria de clases.
❌ Template Method
Define un esqueleto de algoritmo fijo con pasos variables. El problema aquí no es un algoritmo con pasos ordenados, sino un conjunto abierto de reglas que se aplican o no según el contexto. Template Method no tiene mecanismo para incluir/excluir reglas dinámicamente en tiempo de ejecución.

✅ Patrón correcto: Decorator
¿Por qué Decorator?
El patrón Decorator permite añadir responsabilidades a un objeto dinámicamente envolviéndolo en capas. Cada capa agrega su comportamiento y delega al siguiente. Es exactamente lo que necesitamos:

Cada descuento es un decorador independiente
Se combinan libremente en tiempo de ejecución
Agregar un nuevo descuento es una clase nueva, sin tocar las existentes (OCP)
El orden de aplicación es explícito y controlable

// ============================================================
// 1. COMPONENTE BASE — el contrato que todos comparten
// ============================================================
public interface CalculadorPrecio {
    double calcularPrecio(Carrito carrito, Comprador comprador);
    String describir(); // para auditar qué descuentos se aplicaron
}

// ============================================================
// 2. COMPONENTE CONCRETO — precio sin ningún descuento
// ============================================================
public class PrecioBase implements CalculadorPrecio {

    @Override
    public double calcularPrecio(Carrito carrito, Comprador comprador) {
        return carrito.getTotal();
    }

    @Override
    public String describir() {
        return "Precio base";
    }
}

// ============================================================
// 3. DECORADOR ABSTRACTO — delega y habilita la cadena
// ============================================================
public abstract class DecoradorDescuento implements CalculadorPrecio {

    // Referencia al siguiente eslabón de la cadena
    protected final CalculadorPrecio calculador;

    public DecoradorDescuento(CalculadorPrecio calculador) {
        this.calculador = calculador;
    }
}

// ============================================================
// 4. DECORADORES CONCRETOS — uno por regla de negocio
// ============================================================

public class DescuentoFidelidad extends DecoradorDescuento {

    private static final double PORCENTAJE = 0.10;
    private static final int COMPRAS_MINIMAS = 5;

    public DescuentoFidelidad(CalculadorPrecio calculador) {
        super(calculador);
    }

    @Override
    public double calcularPrecio(Carrito carrito, Comprador comprador) {
        double precio = calculador.calcularPrecio(carrito, comprador);

        if (comprador.getTotalCompras() > COMPRAS_MINIMAS) {
            precio = precio * (1 - PORCENTAJE);
        }

        return precio;
    }

    @Override
    public String describir() {
        return calculador.describir() + " → Fidelidad (-10%)";
    }
}

public class DescuentoTemporada extends DecoradorDescuento {

    private static final double PORCENTAJE = 0.15;
    private final ServicioCalendario calendario;

    public DescuentoTemporada(CalculadorPrecio calculador,
                               ServicioCalendario calendario) {
        super(calculador);
        this.calendario = calendario;
    }

    @Override
    public double calcularPrecio(Carrito carrito, Comprador comprador) {
        double precio = calculador.calcularPrecio(carrito, comprador);

        if (calendario.esSemanadelLibro()) {
            precio = precio * (1 - PORCENTAJE);
        }

        return precio;
    }

    @Override
    public String describir() {
        return calculador.describir() + " → Temporada (-15%)";
    }
}

public class DescuentoVolumen extends DecoradorDescuento {

    private static final double PORCENTAJE = 0.05;
    private static final double UMBRAL_COP = 200_000;

    public DescuentoVolumen(CalculadorPrecio calculador) {
        super(calculador);
    }

    @Override
    public double calcularPrecio(Carrito carrito, Comprador comprador) {
        double precio = calculador.calcularPrecio(carrito, comprador);

        if (carrito.getTotal() > UMBRAL_COP) {
            precio = precio * (1 - PORCENTAJE);
        }

        return precio;
    }

    @Override
    public String describir() {
        return calculador.describir() + " → Volumen (-5%)";
    }
}

// ============================================================
// 5. FACTORY — construye la cadena según el contexto
// ============================================================
public class FabricaCalculadorPrecio {

    private final ServicioCalendario calendario;

    public FabricaCalculadorPrecio(ServicioCalendario calendario) {
        this.calendario = calendario;
    }

    public CalculadorPrecio construir(Carrito carrito, Comprador comprador) {
        // Siempre empieza desde el precio base
        CalculadorPrecio calculador = new PrecioBase();

        // Cada condición agrega una capa si aplica
        if (comprador.getTotalCompras() > 5) {
            calculador = new DescuentoFidelidad(calculador);
        }
        if (calendario.esSemanadelLibro()) {
            calculador = new DescuentoTemporada(calculador, calendario);
        }
        if (carrito.getTotal() > 200_000) {
            calculador = new DescuentoVolumen(calculador);
        }

        return calculador;
    }
}

// ============================================================
// 6. EJEMPLO DE USO — cliente fiel en semana del libro
// ============================================================
public class EjemploUso {
    public static void main(String[] args) {

        // Contexto: cliente fiel, semana del libro, carrito grande
        Comprador comprador = new Comprador("Ana", 8); // 8 compras previas
        Carrito carrito = new Carrito();
        carrito.agregar(new Libro("Clean Code", 120_000));
        carrito.agregar(new Libro("DDD", 95_000));
        // Total carrito: $215,000 COP

        ServicioCalendario calendario = new ServicioCalendario();
        // Simulamos que es semana del libro
        calendario.setSemanadelLibro(true);

        FabricaCalculadorPrecio fabrica =
            new FabricaCalculadorPrecio(calendario);

        CalculadorPrecio calculador =
            fabrica.construir(carrito, comprador);

        double precioFinal = calculador.calcularPrecio(carrito, comprador);

        System.out.println(calculador.describir());
        System.out.printf("Precio final: $%.0f COP%n", precioFinal);
    }
}

Salida esperada:
Precio base → Fidelidad (-10%) → Temporada (-15%) → Volumen (-5%)
Precio final: $154,878 COP
Desglose: $215,000 × 0.90 × 0.85 × 0.95 = $154,878

Agrgar Descento en el cumpleaños futuro
// Solo esto. Cero modificaciones al código existente. ✓
public class DescuentoCumpleanos extends DecoradorDescuento {
    private static final double PORCENTAJE = 0.20;

    public DescuentoCumpleanos(CalculadorPrecio calculador) {
        super(calculador);
    }

    @Override
    public double calcularPrecio(Carrito carrito, Comprador comprador) {
        double precio = calculador.calcularPrecio(carrito, comprador);
        return comprador.esSuCumpleanos() ? precio * (1 - PORCENTAJE) : precio;
    }

    @Override
    public String describir() {
        return calculador.describir() + " → Cumpleaños (-20%)";
    }
}]


### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?
[El prompt estableció un rol de estudiante de ingeniería de software y proporcionó el contexto del problema. Dio un ejemplo claro de cómo estaba implementado inicialmente y pidió explícitamente que se señalaran los principios violados y un rediseño completo. Además proporcionó una imagen con una explicación clara de los principios.]

#### 2. ¿Qué se puede mejorar?
[Yo creo que el prompt podria haber sido un poco mas especifico en cuanto a lo que queria, por ejemplo, podria haber pedido que se señalaran los principios violados en orden de importancia, o que se proporcionara un ejemplo de como se podria implementar el rediseño. Por otro lado, no pidio que se explicara por que se debia cambiar el diseño, solo pidio que se mostrara un rediseño. Adicionalmente, el prompt no estableció un formato de salida, lo que hace que sea mas dificil entender lo que se queria.]


#### 3. Respuesta final
[El patrón de diseño más adecuado para este escenario es Decorator, ya que el problema requiere aplicar múltiples descuentos de forma acumulativa y dinámica según diferentes condiciones de negocio (fidelidad, temporada y volumen). Cada descuento representa una responsabilidad independiente que puede agregarse o no en tiempo de ejecución, sin modificar el código existente.
Decorator permite construir una cadena de descuentos donde cada decorador recibe el resultado del anterior, aplica su lógica y delega el cálculo al siguiente componente. Esto evita la creación de una gran cantidad de clases para representar todas las combinaciones posibles de descuentos y mantiene el sistema flexible y extensible.
Además, la solución cumple con el principio Open/Closed (OCP) de SOLID, ya que es posible agregar nuevos descuentos, como un descuento de cumpleaños, creando una nueva clase decoradora sin alterar las ya implementadas.

Descarto usar Strategy porque está diseñado para seleccionar una estrategia entre varias alternativas, no para combinarlas simultáneamente y también descarto Template, ya que este patrón define una secuencia fija de pasos en un algoritmo, mientras que el problema requiere que las reglas se activen o no dinámicamente según el contexto.]
