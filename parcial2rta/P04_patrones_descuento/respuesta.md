# Plantilla de entrega — Parcial 2

---

## Pregunta 4: Identificar y aplicar el patrón correcto para estrategias de descuento

### Estudiante
- **Nombre completo**: Samuel Iregui

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Antigravity |
| **Modelo específico** | Gemini 3.5 Flash |
| **¿Por qué elegiste este LLM?** | Elegi este modelo por comodidad propia y debido a que tengo pago gemini pro y ya tengo todo bien organizado para los diferentes trabajos |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```
Actúa como un arquitecto de software senior. En el proyecto OpenLib Market necesitamos implementar un sistema para aplicar diferentes estrategias de descuento sobre el precio final del carrito de compras:
- Descuento por fidelidad: 10% para compradores con más de 5 compras.
- Descuento por temporada: 15% en la semana del libro (supongamos del 23 al 30 de abril).
- Descuento por volumen: 5% si el carrito supera $200,000 COP.
- Sin descuento: precio normal.

Estas estrategias pueden combinarse en tiempo de ejecución (ej. un cliente fiel recibe descuento de temporada + fidelidad). El precio final debe calcularse dinámicamente según la fecha del carrito, el perfil del usuario (número de compras) y el contenido/valor del carrito.

Necesito que:
1. Identifiques qué patrón de diseño es el más adecuado para representar las estrategias individuales y cómo podemos combinarlas de forma flexible sin acoplar el código.
2. Justifiques la elección del patrón y descartes al menos otras dos alternativas (por ejemplo, Chain of Responsibility y Template Method), explicando por qué no se adaptan tan bien a la combinación dinámica de descuentos.
3. Propongas una implementación funcional en Java 21 utilizando records y características modernas, incluyendo la interfaz de descuento, las estrategias concretas, la estrategia compuesta para combinarlos y un ejemplo de uso con un carrito de compras.

Formato de salida esperado: Markdown estructurado con secciones de "Patrón de diseño propuesto", "Justificación y comparación", y "Código Java 21 refactorizado".
```

---

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

### Patrón de diseño propuesto

Para resolver este problema de manera limpia y extensible, se propone utilizar una combinación de dos patrones de diseño:
1. **Patrón Strategy (Estrategia)**: Para definir cada algoritmo de descuento (fidelidad, temporada, volumen) de forma independiente y encapsulada.
2. **Patrón Composite (Compuesto)**: Para modelar la combinación de múltiples estrategias de descuento. Una estrategia compuesta contiene una lista de otras estrategias y se comporta como si fuera una sola, ejecutándolas en secuencia.

---

### Justificación y comparación

#### ¿Por qué Strategy + Composite?
El cálculo del precio de un carrito requiere aplicar descuentos que varían según factores externos (fechas, usuario, montos). Al usar **Strategy**, podemos agregar o modificar cualquier regla de descuento de forma independiente sin tocar la clase `Carrito` ni las otras estrategias (respetando el Principio de Abierto/Cerrado). Al añadir **Composite**, logramos combinar cualquier cantidad de descuentos de forma dinámica en tiempo de ejecución simplemente pasando una lista de estrategias a un contenedor.

#### Alternativas descartadas:
1. **Template Method (Método Plantilla)**:
   - *Por qué se descarta*: Este patrón se basa en la herencia para que las subclases redefinan partes de un algoritmo. Si bien permitiría especializar un cálculo base, no permite combinar dinámicamente comportamientos en tiempo de ejecución. Tendríamos que crear clases como `DescuentoFidelidadYTemporada` o `DescuentoFidelidadYVolumen`, resultando en una explosión de clases rígida e inmantenible.
2. **Chain of Responsibility (Cadena de Responsabilidad)**:
   - *Por qué se descarta*: En Chain of Responsibility, una petición pasa a través de una cadena de manejadores donde comúnmente un único manejador procesa la petición y detiene el flujo, o cada uno hace una acción excluyente. Aunque se puede modificar para que pase por todos los eslabones, el patrón no está conceptualmente diseñado para la acumulación y cálculo matemático coordinado de un precio, haciendo que el paso de parámetros y la acumulación del estado del descuento se vuelvan complejos y propensos a acoplamiento.

---

### Código Java 21 refactorizado

Aquí tienes la implementación completa aprovechando `java.time` y `records` de Java 21:

```java
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// 1. Dominio con Records modernos
public record Usuario(String nombre, int comprasRealizadas) {}

public record ItemCarrito(String libro, BigDecimal precio, int cantidad) {
    public BigDecimal calcularSubtotal() {
        return precio.multiply(BigDecimal.valueOf(cantidad));
    }
}

public record Carrito(List<ItemCarrito> items, LocalDateTime fecha) {
    public BigDecimal calcularTotalBase() {
        return items.stream()
                .map(ItemCarrito::calcularSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

// 2. Interfaz Strategy para Descuentos
public interface EstrategiaDescuento {
    BigDecimal aplicar(BigDecimal totalActual, Carrito carrito, Usuario usuario);
}

// 3. Implementaciones concretas de la Estrategia (Strategy)

public class DescuentoFidelidad implements EstrategiaDescuento {
    @Override
    public BigDecimal aplicar(BigDecimal totalActual, Carrito carrito, Usuario usuario) {
        if (usuario.comprasRealizadas() > 5) {
            // 10% de descuento
            return totalActual.multiply(BigDecimal.valueOf(0.90));
        }
        return totalActual;
    }
}

public class DescuentoTemporada implements EstrategiaDescuento {
    @Override
    public BigDecimal aplicar(BigDecimal totalActual, Carrito carrito, Usuario usuario) {
        int mes = carrito.fecha().getMonthValue();
        int dia = carrito.fecha().getDayOfMonth();
        // Semana del libro: 23 al 30 de abril
        if (mes == 4 && dia >= 23 && dia <= 30) {
            // 15% de descuento
            return totalActual.multiply(BigDecimal.valueOf(0.85));
        }
        return totalActual;
    }
}

public class DescuentoVolumen implements EstrategiaDescuento {
    @Override
    public BigDecimal aplicar(BigDecimal totalActual, Carrito carrito, Usuario usuario) {
        // Descuento del 5% si el carrito supera $200.000 COP
        if (totalActual.compareTo(new BigDecimal("200000")) > 0) {
            return totalActual.multiply(BigDecimal.valueOf(0.95));
        }
        return totalActual;
    }
}

public class SinDescuento implements EstrategiaDescuento {
    @Override
    public BigDecimal aplicar(BigDecimal totalActual, Carrito carrito, Usuario usuario) {
        return totalActual;
    }
}

// 4. Implementación del Patrón Composite para Combinar Estrategias
public class DescuentoCompuesto implements EstrategiaDescuento {
    private final List<EstrategiaDescuento> estrategias;

    public DescuentoCompuesto(List<EstrategiaDescuento> estrategias) {
        this.estrategias = estrategias;
    }

    @Override
    public BigDecimal aplicar(BigDecimal totalActual, Carrito carrito, Usuario usuario) {
        BigDecimal totalConDescuento = totalActual;
        for (EstrategiaDescuento estrategia : estrategias) {
            totalConDescuento = estrategia.aplicar(totalConDescuento, carrito, usuario);
        }
        return totalConDescuento;
    }
}

// 5. Ejemplo de uso
public class Main {
    public static void main(String[] args) {
        Usuario clienteFiel = new Usuario("Samuel Iregui", 7); // Cliente fiel (> 5 compras)
        
        List<ItemCarrito> items = List.of(
            new ItemCarrito("Diseño de Patrones", new BigDecimal("120000"), 1),
            new ItemCarrito("Clean Code", new BigDecimal("100000"), 1)
        ); // Total base: $220.000 COP (> $200.000 COP)
        
        // Carrito creado el 25 de abril (dentro de la Semana del Libro)
        Carrito carrito = new Carrito(items, LocalDateTime.of(2026, 4, 25, 12, 0));
        
        BigDecimal totalBase = carrito.calcularTotalBase();
        System.out.println("Total Base: $" + totalBase); // $220000
        
        // Creamos la combinación de estrategias aplicables
        EstrategiaDescuento descuentoCombinado = new DescuentoCompuesto(List.of(
            new DescuentoFidelidad(),
            new DescuentoTemporada(),
            new DescuentoVolumen()
        ));
        
        BigDecimal precioFinal = descuentoCombinado.aplicar(totalBase, carrito, clienteFiel);
        System.out.println("Precio Final con todos los descuentos: $" + precioFinal);
        // Aplica 10% por fidelidad ($198000), luego 15% por temporada ($168300), y como no supera $200.000 tras los descuentos, no aplica volumen.
    }
}
```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

El prompt tiene un correcto uso del contexto de OpenLib Market y detalla de manera específica las estrategias de descuento requeridas. Las restricciones se dejaron al final para asegurar que el modelo no omitiera justificar la elección de patrones y descartar las alternativas. Lo más efectivo fue pedir explícitamente la combinación de estrategias, lo que obligó al modelo a no quedarse en un Strategy básico y proponer composición.

#### 2. ¿Qué se puede mejorar?

Se pudo detallar mejor la regla de negocio sobre cómo se deben aplicar los descuentos acumulados (si de forma sumatoria o en cascada sobre el valor restante), ya que el LLM tomó una decisión arbitraria por la falta de esta especificación. También se pudo especificar si queríamos que el carrito dependiera directamente de un motor de descuentos o de una sola interfaz.

#### 3. Respuesta final

En conclusión, para solucionar este problema de forma flexible y extensible se deben combinar dos patrones: el **Strategy** para definir cada tipo de descuento como un algoritmo independiente, y el **Composite** (o un procesador de estrategias) para permitir la combinación dinámica de estos descuentos sin acoplamiento.
  
Se descartan:
- **Template Method**: ya que depende de la herencia y se define en tiempo de compilación, impidiendo la combinación dinámica de comportamientos en tiempo de ejecución.
- **Chain of Responsibility**: pues su objetivo es que un único manejador procese la solicitud y detenga la cadena, mientras que aquí necesitamos que todas las estrategias aplicables participen del cálculo de forma acumulativa.
  
El código presentado en la respuesta del LLM es funcional, utiliza records modernos de Java 21 y resuelve de forma elegante la composición de las estrategias mediante `DescuentoCompuesto`. Un detalle de diseño a considerar es que los descuentos se aplican de manera secuencial (en cascada). Si el negocio requiriera que los descuentos se aplicaran sobre el total original (de forma aditiva), bastaría con modificar la implementación de `DescuentoCompuesto` para sumar los porcentajes antes de aplicarlos, demostrando la alta mantenibilidad y bajo acoplamiento del diseño.
