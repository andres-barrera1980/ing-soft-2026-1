# Plantilla de entrega — Parcial 2

---

## Pregunta P04: Patrones de diseño — Estrategias de descuento

### Estudiante
- **Nombre completo**: Ivan Wright

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Gemini |
| **Modelo especifico** | Gemini 3.1 Pro |
| **¿Por que elegiste este LLM?** | Lo elegi porque es muy bueno analizando patrones de diseño y me ayuda a comparar diferentes arquitecturas para tomar la mejor decision. |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```text
hola gemini, actua como un ingeniero de software senior experto en java.

[CONTEXTO]
Para el proyecto OpenLib Market (que usa Java 21 y Spring Boot 3), necesito manejar varias estrategias de descuento: 10% por fidelidad (mas de 5 compras), 15% por temporada (semana del libro) y 5% por volumen (mas de $200,000 COP). Ojo que estas se pueden combinar, por ejemplo alguien puede tener descuento de temporada y de fidelidad al mismo tiempo. El precio final se calcula dinamicamente.

[PROBLEMA]
Primeramente identifica que patron de diseño de los vistos en clase es el mas adecuado para este caso donde los descuentos se acumulan. Seguidamente, propon una implementacion con clases e interfaces y dame un ejemplo de uso. Finalmente, justifica por que elegiste este patron y descarta por lo menos otras dos alternativas de patrones que no servirian.

[RESTRICCIONES]
- El codigo debe compilar en Java 21 y seguir principios SOLID (especialmente Open/Closed Principle).
- La implementacion debe permitir apilar multiples descuentos a la vez sin crear clases mezcladas como 'DescuentoFidelidadYTemporada'.

[FORMATO]
Dame todo estructurado en markdown. Necesito el diagnostico, las alternativas descartadas, las interfaces/clases java, y el bloque del `main` para el ejemplo de uso.

[VALIDACION]
Sabre que la respuesta es buena si el codigo final respeta el principio Open/Closed y me permite agregar un nuevo descuento en el futuro sin modificar el codigo de los descuentos que ya existen.
```

---

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```markdown
¡Hola! Para este escenario en OpenLib Market, donde los descuentos deben aplicarse dinámicamente y además **pueden combinarse** entre sí, el patrón de diseño más adecuado es el **Patrón Decorator (Decorador)**.

### 1. Patrón Elegido: Decorator
El patrón Decorator permite añadir responsabilidades o comportamientos adicionales a un objeto dinámicamente. En el caso de los precios, un descuento "decora" el cálculo del precio base. Si hay múltiples descuentos, un decorador envuelve a otro decorador, aplicando los descuentos de manera apilada sin tener que crear una clase para cada combinación posible (lo que evitaría crear clases inmanejables como `DescuentoFidelidadYTemporada`).

### 2. Alternativas Descartadas
1.  **Patrón Strategy (Estrategia):** Aunque el enunciado dice "estrategias de descuento", el patrón Strategy clásico está diseñado para que los algoritmos sean **mutuamente excluyentes**. Es decir, aplicas el descuento A **o** el descuento B. Para combinarlos, tendrías que modificar el contexto para que acepte una lista de estrategias y las itere, lo cual empieza a parecerse más a una Cadena de Responsabilidad o un Composite.
2.  **Patrón State (Estado):** Se podría pensar que el carrito pasa por diferentes "estados" de descuento. Sin embargo, los descuentos no representan estados mutuamente excluyentes en un ciclo de vida, sino reglas aplicables a un cálculo concurrente. Usar State aquí sería un abuso del patrón.

### 3. Implementación en Java

Primero, definimos la interfaz base para el cálculo del precio (El Componente):

```java
public interface CalculadorPrecio {
    double calcularPrecio();
}
```

Implementamos el precio base del carrito (El Componente Concreto):

```java
public class PrecioBaseCarrito implements CalculadorPrecio {
    private double montoTotal;

    public PrecioBaseCarrito(double montoTotal) {
        this.montoTotal = montoTotal;
    }

    @Override
    public double calcularPrecio() {
        return montoTotal;
    }
}
```

Creamos la clase abstracta Decorador:

```java
public abstract class DescuentoDecorator implements CalculadorPrecio {
    protected CalculadorPrecio calculadorEnvuelto;

    public DescuentoDecorator(CalculadorPrecio calculador) {
        this.calculadorEnvuelto = calculador;
    }

    @Override
    public double calcularPrecio() {
        return calculadorEnvuelto.calcularPrecio();
    }
}
```

Implementamos los decoradores concretos (los descuentos):

```java
public class DescuentoFidelidad extends DescuentoDecorator {
    public DescuentoFidelidad(CalculadorPrecio calculador) {
        super(calculador);
    }

    @Override
    public double calcularPrecio() {
        double precioActual = super.calcularPrecio();
        return precioActual - (precioActual * 0.10); // 10% de descuento
    }
}

public class DescuentoTemporada extends DescuentoDecorator {
    public DescuentoTemporada(CalculadorPrecio calculador) {
        super(calculador);
    }

    @Override
    public double calcularPrecio() {
        double precioActual = super.calcularPrecio();
        return precioActual - (precioActual * 0.15); // 15% de descuento
    }
}

public class DescuentoVolumen extends DescuentoDecorator {
    private double precioOriginal; // Necesario para saber si supera los 200,000

    public DescuentoVolumen(CalculadorPrecio calculador, double precioOriginal) {
        super(calculador);
        this.precioOriginal = precioOriginal;
    }

    @Override
    public double calcularPrecio() {
        double precioActual = super.calcularPrecio();
        if (precioOriginal > 200000) {
            return precioActual - (precioActual * 0.05); // 5% de descuento extra
        }
        return precioActual;
    }
}
```

### 4. Ejemplo de Uso

```java
public class Main {
    public static void main(String[] args) {
        double totalInicial = 250000;
        // Un cliente compra $250,000 COP en libros
        CalculadorPrecio precioCarrito = new PrecioBaseCarrito(totalInicial);
        
        System.out.println("Precio base: $" + precioCarrito.calcularPrecio());

        // Es un cliente fiel (aplica fidelidad)
        precioCarrito = new DescuentoFidelidad(precioCarrito);
        System.out.println("Con fidelidad (10%): $" + precioCarrito.calcularPrecio());

        // Estamos en la semana del libro (aplica temporada)
        // Se apila sobre el descuento anterior
        precioCarrito = new DescuentoTemporada(precioCarrito);
        System.out.println("Con temporada extra (15%): $" + precioCarrito.calcularPrecio());

        // Además, como la compra original superaba 200,000, aplica el de volumen
        precioCarrito = new DescuentoVolumen(precioCarrito, totalInicial);
        System.out.println("Con volumen extra (5%): $" + precioCarrito.calcularPrecio());
    }
}
```

De esta forma, puedes apilar infinitos descuentos dinámicamente como capas de una cebolla en tiempo de ejecución, cumpliendo con el principio Open/Closed.
```

---

### Analisis critico de la respuesta

#### 1. ¿Que hizo bien el prompt?

[Evalua tu propio prompt, no la respuesta del LLM. ¿El contexto fue suficiente? ¿Las restricciones fueron claras? ¿El formato de salida que pediste ayudo a obtener una buena respuesta? ¿Que parte de tu prompt fue mas efectiva? Se especifico: menciona fragmentos concretos de tu prompt que funcionaron bien.]


#### 2. ¿Que se puede mejorar?

[¿Que le falto a tu prompt? ¿Que harias diferente si pudieras reformularlo? ¿El LLM entendio mal algo por falta de claridad en tu prompt? ¿La respuesta tiene errores u omisiones? ¿Que no cubrio el LLM que tu si sabes por lo visto en clase?]


#### 3. Respuesta final

[Escribe tu respuesta definitiva a la pregunta del parcial, integrando lo que aprendiste del LLM pero yendo mas alla. Corrige errores, llena omisiones, conecta con conceptos vistos en clase. Esta es tu respuesta: demuestra que tu dominas el tema.]
