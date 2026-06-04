# Plantilla de entrega — Parcial 2

---

## Pregunta P04: P.4

### Estudiante

- **Nombre completo**: Juan David Rodriguez Franco

---

### LLM utilizado

| Campo                                   | Valor                                                                                                                                                                                            |
| --------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **Nombre del LLM**                | Gemini                                                                                                                                                                                           |
| **Modelo específico**            | Gemini 3.1 Pro                                                                                                                                                                                   |
| **¿Por qué elegiste este LLM?** | Gemini 3.1 Pro tiene excelente razonamiento arquitectónico para comparar patrones de diseño, justificar decisiones y proporcionar implementaciones complejas como la composición de patrones. |

---

### Prompt utilizado

```text
Actúa como un arquitecto de software experto en Java.

En OpenLib Market necesitamos un sistema para calcular descuentos en el carrito de compras. Existen diferentes tipos:
- Descuento por fidelidad (10% si tiene > 5 compras)
- Descuento por temporada (15% en semana del libro)
- Descuento por volumen (5% si el total supera $200,000 COP)
- Sin descuento (precio normal)

IMPORTANTE: Estas estrategias pueden combinarse. Un cliente fiel podría recibir descuento de temporada + fidelidad al mismo tiempo. El precio final se calcula dinámicamente.

Por favor:
1. Identifica qué patrón de diseño es el más adecuado para resolver esto y permite combinar las reglas.
2. Descarta al menos otras dos alternativas (patrones o enfoques) explicando por qué no sirven o por qué son inferiores para este caso particular.
3. Propón una implementación en Java usando interfaces y clases, e incluye un ejemplo de uso donde se vea cómo se combinan los descuentos.
```

---

### Respuesta del LLM

```markdown
Para resolver este problema donde los descuentos se pueden **combinar dinámicamente**, el patrón de diseño más adecuado es una combinación del patrón **Strategy** (para definir cada regla) junto con un **Composite** (o una cadena evaluadora) que permita agrupar y aplicar múltiples estrategias de manera secuencial o acumulativa. 

En este caso particular, la mejor aproximación moderna es inyectar una **Lista de Estrategias (List<DescuentoStrategy>)** en una clase calculadora, iterando sobre ellas. 

### Alternativas Descartes

1. **Patrón Decorator**:
   * *Por qué se descarta*: Aunque el patrón Decorator es excelente para "envolver" objetos y añadir responsabilidades (como un descuento sobre otro), puede volverse engorroso crear la cadena de decoradores dinámicamente en tiempo de ejecución. Tendríamos instanciaciones profundamente anidadas `new DescuentoFidelidad(new DescuentoTemporada(new PrecioBase()))`, lo cual dificulta evaluar independientemente qué descuentos aplicaron o remover uno del carrito fácilmente.
2. **Uso de Herencia Múltiple o Subclases (Ej: `CarritoConFidelidadYTemporada`)**:
   * *Por qué se descarta*: Nos llevaría a una "explosión de clases". Tendríamos que crear una subclase para cada combinación posible de descuentos (Temporada, Fidelidad, Volumen, Temporada+Fidelidad, Temporada+Volumen, etc.), lo cual es insostenible y viola el principio de favorecer la composición sobre la herencia.
3. **Condicionales (if-else anidados)**:
   * *Por qué se descarta*: Violaría el principio OCP (Open/Closed). Agregar una nueva campaña publicitaria requeriría modificar el código principal de cálculo.

### Implementación en Java

#### 1. Interfaz de Estrategia
```java
import java.math.BigDecimal;

public interface DescuentoStrategy {
    // Retorna el monto a descontar basado en el contexto
    BigDecimal calcularDescuento(CarritoContext contexto, BigDecimal precioActual);
  
    // Indica si el descuento aplica o no
    boolean esAplicable(CarritoContext contexto);
}
```

#### 2. Objeto de Contexto (Datos necesarios para evaluar)

```java
public class CarritoContext {
    private int comprasPreviasCliente;
    private boolean esSemanaDelLibro;
    private BigDecimal totalCarrito;

    // Constructores, Getters y Setters omitidos por brevedad
    public void setComprasPreviasCliente(int n) { this.comprasPreviasCliente = n; }
    public int getComprasPreviasCliente() { return this.comprasPreviasCliente; }
    public void setEsSemanaDelLibro(boolean b) { this.esSemanaDelLibro = b; }
    public boolean isEsSemanaDelLibro() { return this.esSemanaDelLibro; }
    public void setTotalCarrito(BigDecimal t) { this.totalCarrito = t; }
    public BigDecimal getTotalCarrito() { return this.totalCarrito; }
}
```

#### 3. Estrategias Concretas

```java
public class FidelidadDescuento implements DescuentoStrategy {
    @Override
    public boolean esAplicable(CarritoContext contexto) {
        return contexto.getComprasPreviasCliente() > 5;
    }

    @Override
    public BigDecimal calcularDescuento(CarritoContext ctx, BigDecimal precioActual) {
        return precioActual.multiply(new BigDecimal("0.10")); // 10%
    }
}

public class TemporadaDescuento implements DescuentoStrategy {
    @Override
    public boolean esAplicable(CarritoContext contexto) {
        return contexto.isEsSemanaDelLibro();
    }

    @Override
    public BigDecimal calcularDescuento(CarritoContext ctx, BigDecimal precioActual) {
        return precioActual.multiply(new BigDecimal("0.15")); // 15%
    }
}

public class VolumenDescuento implements DescuentoStrategy {
    @Override
    public boolean esAplicable(CarritoContext contexto) {
        return contexto.getTotalCarrito().compareTo(new BigDecimal("200000")) > 0;
    }

    @Override
    public BigDecimal calcularDescuento(CarritoContext ctx, BigDecimal precioActual) {
        return precioActual.multiply(new BigDecimal("0.05")); // 5%
    }
}
```

#### 4. Calculadora Central (Uso de Lista de Estrategias)

```java
import java.util.List;

public class CalculadoraPrecio {
    private final List<DescuentoStrategy> estrategiasActivas;

    public CalculadoraPrecio(List<DescuentoStrategy> estrategiasActivas) {
        this.estrategiasActivas = estrategiasActivas;
    }

    public BigDecimal calcularPrecioFinal(BigDecimal precioBase, CarritoContext contexto) {
        BigDecimal precioFinal = precioBase;
      
        for (DescuentoStrategy estrategia : estrategiasActivas) {
            if (estrategia.esAplicable(contexto)) {
                BigDecimal descuento = estrategia.calcularDescuento(contexto, precioFinal);
                // Restamos el descuento acumulativo al precio actual
                precioFinal = precioFinal.subtract(descuento); 
            }
        }
      
        return precioFinal;
    }
}
```

#### 5. Ejemplo de uso (Cliente)

```java
import java.util.List;
import java.math.BigDecimal;

public class Main {
    public static void main(String[] args) {
        // Inicializamos las estrategias vigentes en el sistema
        List<DescuentoStrategy> descuentos = List.of(
            new FidelidadDescuento(),
            new TemporadaDescuento(),
            new VolumenDescuento()
        );
      
        CalculadoraPrecio calculadora = new CalculadoraPrecio(descuentos);
      
        // Contexto: Cliente con 6 compras, en semana del libro, compra de 100,000 COP
        CarritoContext contexto = new CarritoContext();
        contexto.setComprasPreviasCliente(6); // Aplica Fidelidad (10%)
        contexto.setEsSemanaDelLibro(true);   // Aplica Temporada (15%)
        contexto.setTotalCarrito(new BigDecimal("100000")); // NO aplica volumen (<200k)
      
        BigDecimal precioBase = new BigDecimal("100000");
      
        // 100,000 - 15% (Temporada) = 85,000
        // 85,000 - 10% (Fidelidad) = 76,500
        BigDecimal precioFinal = calculadora.calcularPrecioFinal(precioBase, contexto);
      
        System.out.println("Precio final con descuentos combinados: " + precioFinal); 
        // Salida esperada: 76500.00
    }
}
```

Con este enfoque, agregar un descuento nuevo de Black Friday solo requeriría crear su respectiva clase `DescuentoStrategy` y añadirla a la lista, cumpliendo el principio Open/Closed.

```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

El prompt fue muy efectivo al especificar explícitamente la **necesidad de combinar** los descuentos. Esta fue la clave para que el LLM no propusiera un `Strategy` simple excluyente (donde solo aplica uno), sino una **Composición de Estrategias** (iterando sobre una lista). Además, pedirle que descartara otras dos alternativas obligó a la IA a justificar por qué no usar `Decorator` (que suele ser la respuesta clásica de los libros para este problema) demostrando un análisis más profundo de las implicaciones en tiempo de ejecución.

#### 2. ¿Qué se puede mejorar?

Al prompt le faltó definir una regla de negocio crucial: **¿Cómo se acumulan matemáticamente los descuentos?** Al no especificarlo, el LLM asumió que se aplican iterativamente sobre el saldo restante (descuento sobre descuento). En el comercio real, muchas tiendas prefieren sumar los porcentajes (ej: 10% + 15% = 25% directo sobre el precio base original). Si el prompt hubiera aclarado esta regla, la implementación de la interfaz `calcularDescuento` habría sido más fiel a la realidad del negocio.

#### 3. Respuesta final

El LLM seleccionó acertadamente el patrón **Strategy** apoyado en una estructura de evaluación en cadena (una forma simplificada del patrón **Composite** o Pipeline). Esto es inmensamente superior a tener código espagueti con `if-else` o a usar herencia múltiple, asegurando que agregar nuevas reglas no modifique el motor de cálculo (cumpliendo a cabalidad **OCP**). 

Adicionalmente, el análisis de los descartes fue impecable:
* **Decorator** es útil para añadir comportamiento de forma dinámica, pero armar y desarmar el arnés de objetos en memoria según el perfil del usuario puede volverse un dolor de cabeza.
* **Herencia** fue correctamente descartada porque generar clases por cada combinación produce una "explosión de clases" insostenible.

La implementación entregada es robusta, compilable y lista para producción en "OpenLib Market". Es una gran demostración de cómo el patrón Strategy, al combinarse con inyección de listas en lugar de un único valor, permite que los algoritmos sean escalables y fácilmente testeables en aislamiento.
```
