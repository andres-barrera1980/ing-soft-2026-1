# Plantilla de entrega — Parcial 2

## Pregunta [XX]: [Título resumido]

### Estudiante
- **Nombre completo**: [Tu nombre y apellido]

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [Gemini] |
| **Modelo específico** | [Gemini 3.1 Pro] |
| **¿Por qué elegiste este LLM?** | [Aparte de que es la unica cuenta en la que tengo un servicio de pago en este momento, Gemini 3.1 pro me ha demostrado que es muy efectivo a la hora de asimilar roles y contexto a lo largo de una conversación. Esto me permite guiar su respuesta al punto exacto que necesito sin tener que estarle recordando detalles basicos o limitaciones del problema] |

---

### Pregunta 4
OpenLib Market necesita aplicar diferentes estrategias de descuento durante el año:

- **Descuento por fidelidad**: 10% para compradores con más de 5 compras.
- **Descuento por temporada**: 15% en la semana del libro.
- **Descuento por volumen**: 5% si el carrito supera $200,000 COP.
- **Sin descuento**: precio normal.

Estas estrategias pueden combinarse (ej: un cliente fiel puede recibir descuento de temporada + fidelidad). El precio final debe calcularse dinámicamente según la fecha, el perfil del usuario, y el contenido del carrito.

**Tarea**:

1. **Prompt**: Pídele a tu LLM que identifique qué patrón de diseño de los vistos en clase es el más adecuado para este escenario, y que proponga una implementación en Java con clases, interfaces y un ejemplo de uso. Exígele que justifique por qué ese patrón y descarte al menos otras dos alternativas. Pega el prompt y la respuesta.
2. **Análisis**: ¿El LLM identificó el patrón adecuado? ¿Consideró la posibilidad de composición de estrategias? ¿Qué alternativas mencionó y fueron correctamente descartadas? ¿La implementación es correcta y funcional? ¿Qué le faltó?

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```
Actúa como un Arquitecto de Software Senior en java 25 y profesor universitario de Patrones de Diseño.
Tenemos el siguiente escenario de negocio en "OpenLib Market":
Necesitamos aplicar diferentes estrategias de descuento durante el año:

- **Descuento por fidelidad**: 10% para compradores con más de 5 compras.
- **Descuento por temporada**: 15% en la semana del libro.
Descuento por volumen: 5% si el carrito supera $200,000 COP.
Sin descuento: precio normal.
Regla de negocio crítica: Estas estrategias pueden combinarse (ej: un cliente fiel puede recibir descuento de temporada + fidelidad). El precio final debe calcularse dinámicamente según la fecha, el perfil del usuario, y el contenido del carrito.
Tu tarea:

Identifica qué patrón de diseño (de la familia GoF) es el más adecuado para resolver el problema de la combinación de descuentos.
Justifica detalladamente tu elección.
Descarta explícitamente al menos otras dos alternativas de patrones (ej. Strategy clásico, Chain of Responsibility, etc.) explicando por qué fallan o se quedan cortos para este caso específico.
Proporciona una implementación limpia en Java (versión 25) que incluya las interfaces, las clases y una clase Main con un ejemplo de uso combinando dos descuentos.

---

### Respuesta del LLM

¡Excelente escenario de arquitectura! Has tocado una de las fronteras clásicas donde la elección del patrón define si el sistema escala de forma limpia o si se convierte en un nido de dependencias.

Como arquitecto, analizo tu requerimiento y la palabra clave que dicta la solución es **"combinarse"**. Cuando necesitamos acumular comportamientos de forma dinámica en tiempo de ejecución, la estructura procedimental debe cambiar.

Aquí tienes la resolución estructurada para este desafío.

### 1. El Patrón Elegido: Decorator (Decorador)

El patrón estructural **Decorator** de la familia GoF es la solución procedimental exacta para este problema.

**Justificación lógica:**
El patrón Decorador permite añadir responsabilidades o comportamientos adicionales a un objeto dinámicamente. En lugar de heredar o elegir una única estrategia, el Decorador "envuelve" (wraps) al componente original.
En el caso de OpenLib Market, empezamos con un `PrecioBase`. Si el usuario aplica para temporada, envolvemos ese precio con el `DescuentoTemporada`. Si además es fiel, envolvemos ese resultado con `DescuentoFidelidad`. Al invocar el cálculo, la llamada viaja desde la capa más externa hacia el centro, y los resultados matemáticos se calculan en cascada al regresar. Esto nos da combinaciones infinitas sin crear nuevas clases.

---

### 2. Descarte de Alternativas

Es crucial entender por qué otros patrones populares fallan estructuralmente aquí:

* ❌ **Patrón Strategy (Estrategia):**
* **Por qué falla:** Strategy está diseñado para seleccionar *un único* algoritmo de una familia y hacerlo intercambiable (como lo vimos con los métodos de pago). Si usas Strategy aquí, tendrías que instanciar la `EstrategiaTemporada`. ¿Pero qué pasa si quieres temporada + fidelidad? Te verías obligado a crear una clase `EstrategiaTemporadaYFidelidad`. Esto genera una **explosión combinatoria de clases** (tendrías que programar una clase por cada combinación posible de descuentos), violando el principio de diseño de mantener el código cerrado a modificaciones.


* ❌ **Patrón Chain of Responsibility (Cadena de Responsabilidad):**
* **Por qué falla:** La Cadena de Responsabilidad está pensada para pasar una petición a lo largo de una cadena de manejadores hasta que *uno de ellos* la procesa y detiene el flujo (ej. filtros de autenticación o manejo de excepciones). Aunque se puede forzar para acumular datos, su intención semántica original no es transformar el estado estructural de un cálculo en cascada, sino decidir "quién se hace cargo". El Decorador está intrínsecamente diseñado para la agregación estructural de resultados.



---

### 3. Implementación en Java 25

Para demostrar un nivel *Senior*, utilizaremos `BigDecimal` (mandatorio para transacciones financieras, nunca usar `double` por pérdida de precisión flotante) y `records` (introducidos para DTOs inmutables).

```java
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

// 1. DTOs inmutables del dominio
record Usuario(String id, int cantidadCompras) {}
record Carrito(BigDecimal subtotal, LocalDate fechaCompra) {}

// 2. Componente Principal (La interfaz base)
interface ICotizadorPrecio {
    BigDecimal calcularPrecio(Carrito carrito, Usuario usuario);
}

// 3. Componente Concreto (El precio sin descuentos)
class CotizadorBase implements ICotizadorPrecio {
    @Override
    public BigDecimal calcularPrecio(Carrito carrito, Usuario usuario) {
        return carrito.subtotal(); // Precio normal
    }
}

// 4. Decorador Base (Clase Abstracta)
abstract class DescuentoDecorador implements ICotizadorPrecio {
    protected final ICotizadorPrecio cotizadorEnvuelto;

    public DescuentoDecorador(ICotizadorPrecio cotizadorEnvuelto) {
        this.cotizadorEnvuelto = cotizadorEnvuelto;
    }

    @Override
    public BigDecimal calcularPrecio(Carrito carrito, Usuario usuario) {
        // Delega la llamada hacia adentro de la "cebolla"
        return cotizadorEnvuelto.calcularPrecio(carrito, usuario);
    }
}

// 5. Decoradores Concretos (Las reglas de negocio)
class DescuentoFidelidad extends DescuentoDecorador {
    public DescuentoFidelidad(ICotizadorPrecio cotizador) { super(cotizador); }

    @Override
    public BigDecimal calcularPrecio(Carrito carrito, Usuario usuario) {
        BigDecimal precioActual = super.calcularPrecio(carrito, usuario);
        
        // Regla: > 5 compras = 10% de descuento
        if (usuario.cantidadCompras() > 5) {
            BigDecimal descuento = precioActual.multiply(new BigDecimal("0.10"));
            return precioActual.subtract(descuento);
        }
        return precioActual;
    }
}

class DescuentoTemporada extends DescuentoDecorador {
    private final LocalDate inicioSemanaLibro;
    private final LocalDate finSemanaLibro;

    public DescuentoTemporada(ICotizadorPrecio cotizador, LocalDate inicio, LocalDate fin) {
        super(cotizador);
        this.inicioSemanaLibro = inicio;
        this.finSemanaLibro = fin;
    }

    @Override
    public BigDecimal calcularPrecio(Carrito carrito, Usuario usuario) {
        BigDecimal precioActual = super.calcularPrecio(carrito, usuario);
        
        // Regla: 15% en la semana del libro
        if (!carrito.fechaCompra().isBefore(inicioSemanaLibro) && !carrito.fechaCompra().isAfter(finSemanaLibro)) {
            BigDecimal descuento = precioActual.multiply(new BigDecimal("0.15"));
            return precioActual.subtract(descuento);
        }
        return precioActual;
    }
}

class DescuentoVolumen extends DescuentoDecorador {
    public DescuentoVolumen(ICotizadorPrecio cotizador) { super(cotizador); }

    @Override
    public BigDecimal calcularPrecio(Carrito carrito, Usuario usuario) {
        BigDecimal precioActual = super.calcularPrecio(carrito, usuario);
        
        // Regla: > $200,000 COP = 5% de descuento
        if (carrito.subtotal().compareTo(new BigDecimal("200000")) > 0) {
            BigDecimal descuento = precioActual.multiply(new BigDecimal("0.05"));
            return precioActual.subtract(descuento);
        }
        return precioActual;
    }
}

// 6. Ejecución (Main)
public class OpenLibDescuentosApp {
    public static void main(String[] args) {
        // Escenario: Un carrito de 250,000 COP en la semana del libro, por un cliente fiel (7 compras).
        Carrito carrito = new Carrito(new BigDecimal("250000"), LocalDate.of(2026, 4, 23));
        Usuario usuario = new Usuario("U-101", 7);

        // Construcción del pipeline de descuentos (Composición dinámica)
        ICotizadorPrecio cotizador = new CotizadorBase(); // Inicia con el precio normal
        
        // Envolvemos con Volumen
        cotizador = new DescuentoVolumen(cotizador);
        // Envolvemos con Temporada
        cotizador = new DescuentoTemporada(cotizador, LocalDate.of(2026, 4, 20), LocalDate.of(2026, 4, 27));
        // Envolvemos con Fidelidad
        cotizador = new DescuentoFidelidad(cotizador);

        // Ejecución procedimental del cálculo en cascada
        BigDecimal precioFinal = cotizador.calcularPrecio(carrito, usuario).setScale(2, RoundingMode.HALF_UP);
        
        System.out.println("Subtotal Carrito: $" + carrito.subtotal());
        System.out.println("Precio Final (Tras descuentos combinados): $" + precioFinal);
        
        /* Flujo matemático interno:
         1. Base: $250,000
         2. Volumen: Evalúa sobre $250,000. Descuenta 5% ($12,500). Pasa: $237,500
         3. Temporada: Evalúa sobre $237,500. Descuenta 15% ($35,625). Pasa: $201,875
         4. Fidelidad: Evalúa sobre $201,875. Descuenta 10% ($20,187.5). Final: $181,687.50
        */
    }
}

```

---



### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

Con la linea "Regla de negocio crítica: Estas estrategias pueden combinarse...". A obligue al LLM a pensar en la composición estructural del sistema de descuentos, y crear la logica de su caracter combinable, ademas de generar el codigo para lograrlo.

Obligue al LLM a realizar un proceso de descarte de patrones alternativos como el strategy, para que el mismo analizara pros y contras y optara por el decorator, que resulta ser el mas eficiente para este problema.

Obligue al LLM a crear un main donde simulara dos descuentos en ejecucion, demostrando que su propuesta es efectiva y no se quedo solo en la teoria del proceso de desarrollo.

#### 2. ¿Qué se puede mejorar?

- El LLM resolvió muy bien cómo se combinan los descuentos (con new Descuento(new OtroDescuento())), pero mi prompt no le exigió resolver quién los combina, por lo que todas las clases se armaron a mano en el main


#### 3. Respuesta final

Aplicando el feedback sobre lo que genero el LLM, propongo el siguiente codigo, utilizando un Decorator para que durante el runtime se puedan combinar los descuentos de temporada y fidelidad al mismo tiempo. Ademas, se usa un factory para para instanciar la amalgama de descuentos utilizando las condiciones del problema, para que el cliente no tenga que instanciar los decorators a mano

```java
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

record Usuario(String id, int cantidadCompras) {}
record Carrito(BigDecimal subtotal, LocalDate fechaCompra) {}

interface ICotizadorPrecio {
    BigDecimal calcular(Carrito carrito, Usuario usuario);
}

class CotizadorBase implements ICotizadorPrecio {
    @Override
    public BigDecimal calcular(Carrito carrito, Usuario usuario) {
        return carrito.subtotal();
    }
}

abstract class DescuentoDecorador implements ICotizadorPrecio {
    protected final ICotizadorPrecio cotizadorEnvuelto;

    public DescuentoDecorador(ICotizadorPrecio cotizadorEnvuelto) {
        this.cotizadorEnvuelto = cotizadorEnvuelto;
    }

    @Override
    public BigDecimal calcular(Carrito carrito, Usuario usuario) {
        return cotizadorEnvuelto.calcular(carrito, usuario);
    }
}

class DescuentoTemporada extends DescuentoDecorador {
    public DescuentoTemporada(ICotizadorPrecio cotizador) { super(cotizador); }

    @Override
    public BigDecimal calcular(Carrito carrito, Usuario usuario) {
        BigDecimal precioActual = super.calcular(carrito, usuario);
        LocalDate inicio = LocalDate.of(2026, 4, 20);
        LocalDate fin = LocalDate.of(2026, 4, 27);
        
        if (!carrito.fechaCompra().isBefore(inicio) && !carrito.fechaCompra().isAfter(fin)) {
            return precioActual.multiply(new BigDecimal("0.85")); // -15%
        }
        return precioActual;
    }
}

class DescuentoVolumen extends DescuentoDecorador {
    public DescuentoVolumen(ICotizadorPrecio cotizador) { super(cotizador); }

    @Override
    public BigDecimal calcular(Carrito carrito, Usuario usuario) {
        BigDecimal precioActual = super.calcular(carrito, usuario);
        if (carrito.subtotal().compareTo(new BigDecimal("200000")) > 0) {
            return precioActual.multiply(new BigDecimal("0.95")); // -5%
        }
        return precioActual;
    }
}

class DescuentoFidelidad extends DescuentoDecorador {
    public DescuentoFidelidad(ICotizadorPrecio cotizador) { super(cotizador); }

    @Override
    public BigDecimal calcular(Carrito carrito, Usuario usuario) {
        BigDecimal precioActual = super.calcular(carrito, usuario);
        if (usuario.cantidadCompras() > 5) {
            return precioActual.multiply(new BigDecimal("0.90")); // -10%
        }
        return precioActual;
    }
}

class CotizadorFactory {
    public static ICotizadorPrecio construirCotizador(Carrito carrito, Usuario usuario) {
        ICotizadorPrecio cotizador = new CotizadorBase();
        LocalDate inicio = LocalDate.of(2026, 4, 20);
        LocalDate fin = LocalDate.of(2026, 4, 27);
        if (!carrito.fechaCompra().isBefore(inicio) && !carrito.fechaCompra().isAfter(fin)) {
            cotizador = new DescuentoTemporada(cotizador);
        }

        if (carrito.subtotal().compareTo(new BigDecimal("200000")) > 0) {
            cotizador = new DescuentoVolumen(cotizador);
        }

        if (usuario.cantidadCompras() > 5) {
            cotizador = new DescuentoFidelidad(cotizador);
        }

        return cotizador;
    }
}

public class Main {
    public static void main(String[] args) {
        Carrito carrito = new Carrito(new BigDecimal("250000"), LocalDate.of(2026, 4, 23));
        Usuario usuario = new Usuario("U-101", 7);

        ICotizadorPrecio cotizador = CotizadorFactory.construirCotizador(carrito, usuario);
        
        BigDecimal precioFinal = cotizador.calcular(carrito, usuario).setScale(2, RoundingMode.HALF_UP);
        
        System.out.println("Subtotal Base: $" + carrito.subtotal());
        System.out.println("Precio Final (Descuentos en cascada): $" + precioFinal);
        
        /* * Flujo de ejecución del Decorador:
         * 1. CotizadorBase retorna $250,000.
         * 2. DescuentoTemporada recibe $250,000 -> Aplica 15% -> Pasa $212,500.
         * 3. DescuentoVolumen evalúa subtotal base > 200k. Recibe $212,500 -> Aplica 5% -> Pasa $201,875.
         * 4. DescuentoFidelidad recibe $201,875 -> Aplica 10% -> Resultado: $181,687.50.
         */
    }
}
