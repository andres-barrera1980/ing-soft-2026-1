# Plantilla de entrega — Parcial 2
---

## Pregunta [XX]: [Título resumido]

### Estudiante
- **Nombre completo**: Samuel López Gómez

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Claude |
| **Modelo específico** |Sonnet 4.6 |
| **¿Por qué elegiste este LLM?** |  |

---

### Prompt utilizado

Actúa como un arquitecto de software senior diseñando el módulo de precios para el proyecto OpenLib Market (entorno Java 21). 

Tenemos el siguiente requerimiento: El sistema debe aplicar diferentes descuentos al carrito de compras:
- Descuento por fidelidad: 10% (clientes con > 5 compras).
- Descuento por temporada: 15% (semana del libro).
- Descuento por volumen: 5% (carrito > $200,000 COP).
- Sin descuento: precio normal.
REGLA DE NEGOCIO CLAVE: Las estrategias pueden combinarse dinámicamente (ej: un cliente fiel en la semana del libro recibe ambos descuentos aplicados al precio base o en cascada, tú decides la lógica pero debe soportar la combinación).

Necesito que:
1. Identifiques qué patrón de diseño (o combinación de patrones) de los clásicos del GoF es el más adecuado para este escenario.
2. Propongas una implementación en Java 21 con clases, interfaces y un ejemplo de uso en un `main` o test.
3. Justifiques detalladamente por qué elegiste ese/esos patrón(es).
4. Descartes explícitamente al menos otras dos alternativas de patrones, explicando por qué fallarían o serían subóptimas aquí.

Restricciones:
- Código en POO pura, sin frameworks.
- El diseño debe permitir agregar nuevas reglas de descuento en el futuro sin modificar el código existente (cumplir OCP).

Formato de salida esperado: Markdown con las secciones "Patrón Seleccionado", "Alternativas Descartadas", "Justificación" y "Código Java".

```

---

### Respuesta del LLM
# Módulo de Precios con Descuentos Combinables
> OpenLib Market · Diseño de Patrones GoF · Java 21

---

## Patrón Seleccionado

### Combinación: **Decorator + Strategy**

La solución óptima requiere **dos patrones GoF trabajando en sinergia**:

| Patrón | Rol en este problema |
|---|---|
| **Decorator** | Envuelve descuentos unos sobre otros permitiendo combinaciones dinámicas sin explosión de subclases |
| **Strategy** | Encapsula la *condición de elegibilidad* de cada descuento como algoritmo intercambiable |

La separación es precisa:
- **Decorator** responde a *¿cómo se apilan y calculan los descuentos?*
- **Strategy** responde a *¿cuándo aplica cada descuento?*

Sin Strategy, el Decorator necesita conocer la lógica de negocio de cada condición
(cuántas compras tiene el cliente, si es semana del libro, etc.), violando SRP.
Sin Decorator, Strategy no tiene mecanismo de composición: cada combinación
de reglas requeriría una clase distinta.

---

## Alternativas Descartadas

### ❌ Alternativa 1: Chain of Responsibility

**Por qué parece atractiva:** cada handler de la cadena puede decidir si aplica
su descuento y pasar al siguiente, encadenando reglas dinámicamente.

**Por qué falla aquí:**

El propósito semántico de Chain of Responsibility es que **un solo manejador
procese la solicitud** (o la pase). Es el patrón de los middlewares y filtros
de autorización, donde una vez que alguien maneja la petición los demás ya no
lo hacen (o no deberían).

En nuestro caso **todos los descuentos aplicables deben actuar simultáneamente**
sobre el precio. Forzar Chain of Responsibility implica hackear su semántica:
cada nodo debe recordar si ya aplicó descuento y pasarlo acumulado, convirtiendo
la cadena en un acumulador con efectos secundarios. El resultado es código que
usa el patrón incorrecto y es más difícil de razonar que un `if-else` plano.

```java
// Así quedaría: antipatrón disfrazado de CoR
handler.handle(carrito); // ¿cuántos handlers actuaron? ¿en qué orden? opaco.
```

Además, agregar un nuevo descuento requiere insertar un nodo en la cadena en
la posición correcta — orden que importa y no está expresado en ningún contrato.

---

### ❌ Alternativa 2: Strategy puro (sin Decorator)

**Por qué parece atractiva:** encapsula cada regla de descuento como una
estrategia intercambiable. Limpio, OCP-compliant.

**Por qué falla aquí:**

Strategy puro resuelve el problema de *un algoritmo a la vez*. Para combinaciones
de N descuentos necesitaríamos una estrategia por cada combinación posible:

```
FidelidadStrategy
TemporadaStrategy
VolumenStrategy
FidelidadYTemporadaStrategy        ← clase nueva
FidelidadYVolumenStrategy          ← clase nueva
TemporadaYVolumenStrategy          ← clase nueva
FidelidadYTemporadaYVolumenStrategy ← clase nueva
```

Con 3 reglas hay 7 combinaciones (2³ - 1). Con 5 reglas: 31. Con 8: 255.
Esta es la **explosión de subclases** que Decorator existe precisamente para evitar.

Alternativamente se puede pasar una `List<Strategy>` e iterar, pero eso significa
que el código cliente decide el orden de aplicación y acumula el resultado manualmente,
lo que saca la lógica de composición fuera del dominio del precio.

---

### ❌ Alternativa 3: Template Method

**Por qué parece atractiva:** define un esqueleto del algoritmo de descuento
con pasos a sobreescribir por subclases.

**Por qué falla aquí:**

Template Method exige **herencia** para cada variación. La composición dinámica
en runtime (habilitar o deshabilitar descuentos según el contexto del cliente)
es fundamentalmente incompatible con la herencia estática. No se puede en runtime
decidir "quiero aplicar fidelidad pero no volumen" sin instanciar clases distintas,
volviendo al problema de explosión de subclases de Strategy puro.

---

## Justificación

### ¿Por qué Decorator es el núcleo?

El patrón Decorator permite **añadir comportamiento a un objeto envolviéndolo**,
sin modificar su clase. En términos de precios:

```
PrecioBase
  └── DecoradorFidelidad (envuelve PrecioBase)
        └── DecoradorTemporada (envuelve DecoradorFidelidad)
              └── DecoradorVolumen (envuelve DecoradorTemporada)
```

Cada decorador llama al componente que envuelve para obtener el precio acumulado
y le aplica su propio descuento. El orden puede invertirse o cambiarse en runtime
simplemente construyendo la cadena de otra manera.

La lógica de descuento en cascada (aplicar sobre precio ya descontado) emerge
naturalmente de la estructura del patrón, sin código especial.

### ¿Por qué agregar Strategy para las condiciones?

La condición de elegibilidad (`¿aplica este descuento?`) es un algoritmo que
varía independientemente de cómo se calcula el descuento. Encapsularla como
Strategy permite:

1. **Testear condiciones aisladamente** sin instanciar decoradores.
2. **Reutilizar condiciones** en distintos contextos (reportes, UI, etc.).
3. **Cambiar reglas de negocio** (¿qué es "cliente fiel"? ¿>5 o >10 compras?)
   sin tocar las clases de descuento.

### Cumplimiento de OCP

Agregar "Descuento por suscripción premium: 20%" en el futuro:
1. Crear `CondicionSuscripcionPremium implements CondicionDescuento` ← nueva clase.
2. Crear `DecoradorSuscripcion extends DecoradorDescuento` ← nueva clase.
3. Agregar al builder: `.conDescuento(new DecoradorSuscripcion(...))` ← nueva línea.

**Cero clases existentes modificadas. OCP cumplido.**

---

## Código Java

### Modelo de Carrito (alineado con el proyecto)

```java
package com.openlib.pricing.model;

import com.openlib.model.Book;
import com.openlib.model.CartItem;
import com.openlib.model.User;

import java.util.List;

/**
 * Contexto de precio: contiene toda la información necesaria para que
 * los decoradores y las condiciones tomen decisiones.
 *
 * Record inmutable — Java 16+. Alineado con CartItem y User del proyecto.
 */
public record ContextoPrecio(
    User comprador,
    List<CartItem> items,
    int totalComprasHistoricas,  // para fidelidad
    boolean esSemanaDellibro     // para temporada
) {
    /** Suma total del carrito a precio base, sin descuentos. */
    public double totalBase() {
        return items.stream()
                    .mapToDouble(CartItem::getSubtotal)
                    .sum();
    }
}
```

---

### Interfaz Strategy: condición de elegibilidad

```java
package com.openlib.pricing.condition;

import com.openlib.pricing.model.ContextoPrecio;

/**
 * Strategy: encapsula la lógica de "¿este descuento aplica para este contexto?".
 * Cada regla de negocio es un algoritmo intercambiable.
 *
 * @see CondicionFidelidad
 * @see CondicionTemporada
 * @see CondicionVolumen
 */
@FunctionalInterface
public interface CondicionDescuento {
    boolean aplica(ContextoPrecio contexto);
}
```

---

### Implementaciones de Strategy (condiciones)

```java
package com.openlib.pricing.condition;

import com.openlib.pricing.model.ContextoPrecio;

/** Aplica si el cliente tiene más de 5 compras históricas. */
public class CondicionFidelidad implements CondicionDescuento {

    private static final int UMBRAL_COMPRAS = 5;

    @Override
    public boolean aplica(ContextoPrecio contexto) {
        return contexto.totalComprasHistoricas() > UMBRAL_COMPRAS;
    }
}
```

```java
package com.openlib.pricing.condition;

import com.openlib.pricing.model.ContextoPrecio;

/** Aplica si la compra ocurre durante la semana del libro. */
public class CondicionTemporada implements CondicionDescuento {

    @Override
    public boolean aplica(ContextoPrecio contexto) {
        return contexto.esSemanaDellibro();
    }
}
```

```java
package com.openlib.pricing.condition;

import com.openlib.pricing.model.ContextoPrecio;

/** Aplica si el total base del carrito supera $200,000 COP. */
public class CondicionVolumen implements CondicionDescuento {

    private static final double UMBRAL_COP = 200_000.0;

    @Override
    public boolean aplica(ContextoPrecio contexto) {
        return contexto.totalBase() > UMBRAL_COP;
    }
}
```

---

### Interfaz Decorator: componente de precio

```java
package com.openlib.pricing.decorator;

import com.openlib.pricing.model.ContextoPrecio;

/**
 * Componente base del Decorator.
 * Todas las variantes de precio (base y decoradas) cumplen este contrato.
 */
public interface ComponentePrecio {

    /**
     * Calcula el precio final para el contexto dado.
     *
     * @param contexto datos del carrito y del comprador
     * @return precio en COP después de aplicar las reglas de esta instancia
     */
    double calcularPrecio(ContextoPrecio contexto);

    /**
     * Descripción legible de las reglas aplicadas en esta cadena.
     * Útil para el ticket de compra y para debugging.
     */
    String descripcion();
}
```

---

### Componente concreto: precio base

```java
package com.openlib.pricing.decorator;

import com.openlib.pricing.model.ContextoPrecio;

/**
 * Precio base sin ningún descuento.
 * Es el componente más interno de cualquier cadena de Decorators.
 */
public class PrecioBase implements ComponentePrecio {

    @Override
    public double calcularPrecio(ContextoPrecio contexto) {
        return contexto.totalBase();
    }

    @Override
    public String descripcion() {
        return "Precio base";
    }
}
```

---

### Decorador abstracto

```java
package com.openlib.pricing.decorator;

import com.openlib.pricing.condition.CondicionDescuento;
import com.openlib.pricing.model.ContextoPrecio;

/**
 * Decorador abstracto: envuelve un ComponentePrecio y delega el cálculo.
 * Las subclases definen el porcentaje y la condición de su descuento.
 *
 * La condición de elegibilidad es una Strategy inyectada por constructor,
 * cumpliendo DIP: el decorador depende de la abstracción, no de la lógica concreta.
 */
public abstract class DecoradorDescuento implements ComponentePrecio {

    protected final ComponentePrecio envuelto;
    protected final CondicionDescuento condicion;

    protected DecoradorDescuento(ComponentePrecio envuelto, CondicionDescuento condicion) {
        this.envuelto  = envuelto;
        this.condicion = condicion;
    }

    /**
     * Porcentaje de descuento expresado como fracción (ej: 0.10 para 10%).
     */
    protected abstract double porcentajeDescuento();

    /**
     * Nombre del descuento para el ticket de compra.
     */
    protected abstract String nombreDescuento();

    @Override
    public double calcularPrecio(ContextoPrecio contexto) {
        double precioAnterior = envuelto.calcularPrecio(contexto);

        if (condicion.aplica(contexto)) {
            // Descuento en cascada: se aplica sobre el precio ya descontado
            return precioAnterior * (1.0 - porcentajeDescuento());
        }

        return precioAnterior; // la condición no aplica: precio sin cambio
    }

    @Override
    public String descripcion() {
        return envuelto.descripcion() + " → " + nombreDescuento();
    }
}
```

---

### Decoradores concretos

```java
package com.openlib.pricing.decorator;

import com.openlib.pricing.condition.CondicionDescuento;

/** Aplica 10% de descuento por fidelidad del cliente. */
public class DecoradorFidelidad extends DecoradorDescuento {

    public DecoradorFidelidad(ComponentePrecio envuelto, CondicionDescuento condicion) {
        super(envuelto, condicion);
    }

    @Override
    protected double porcentajeDescuento() { return 0.10; }

    @Override
    protected String nombreDescuento() { return "Descuento fidelidad (-10%)"; }
}
```

```java
package com.openlib.pricing.decorator;

import com.openlib.pricing.condition.CondicionDescuento;

/** Aplica 15% de descuento durante la semana del libro. */
public class DecoradorTemporada extends DecoradorDescuento {

    public DecoradorTemporada(ComponentePrecio envuelto, CondicionDescuento condicion) {
        super(envuelto, condicion);
    }

    @Override
    protected double porcentajeDescuento() { return 0.15; }

    @Override
    protected String nombreDescuento() { return "Descuento semana del libro (-15%)"; }
}
```

```java
package com.openlib.pricing.decorator;

import com.openlib.pricing.condition.CondicionDescuento;

/** Aplica 5% de descuento por volumen (carrito > $200,000 COP). */
public class DecoradorVolumen extends DecoradorDescuento {

    public DecoradorVolumen(ComponentePrecio envuelto, CondicionDescuento condicion) {
        super(envuelto, condicion);
    }

    @Override
    protected double porcentajeDescuento() { return 0.05; }

    @Override
    protected String nombreDescuento() { return "Descuento por volumen (-5%)"; }
}
```

---

### Builder: construcción fluida de la cadena

```java
package com.openlib.pricing;

import com.openlib.pricing.condition.*;
import com.openlib.pricing.decorator.*;
import com.openlib.pricing.model.ContextoPrecio;

/**
 * Builder fluido para construir la cadena de decoradores.
 *
 * Permite expresar la política de precios de forma declarativa:
 *
 *   CalculadorPrecio.para(contexto)
 *       .conDescuentoFidelidad()
 *       .conDescuentoTemporada()
 *       .conDescuentoVolumen()
 *       .calcular();
 *
 * Agregar un nuevo descuento = agregar un método .conDescuentoX() aquí
 * y crear su decorador y condición. Nada más cambia.
 */
public class CalculadorPrecio {

    private ComponentePrecio cadena;

    private CalculadorPrecio() {
        this.cadena = new PrecioBase();
    }

    public static CalculadorPrecio nuevo() {
        return new CalculadorPrecio();
    }

    public CalculadorPrecio conDescuentoFidelidad() {
        cadena = new DecoradorFidelidad(cadena, new CondicionFidelidad());
        return this;
    }

    public CalculadorPrecio conDescuentoTemporada() {
        cadena = new DecoradorTemporada(cadena, new CondicionTemporada());
        return this;
    }

    public CalculadorPrecio conDescuentoVolumen() {
        cadena = new DecoradorVolumen(cadena, new CondicionVolumen());
        return this;
    }

    /**
     * Punto de extensión OCP: agrega cualquier decorador personalizado.
     *
     * Ejemplo futuro:
     *   .conDescuento(new DecoradorSuscripcion(new CondicionSuscripcionPremium()))
     */
    public CalculadorPrecio conDescuento(DecoradorDescuento decorador) {
        // Se reconstruye la cadena pasando el decorador con la cadena actual
        cadena = decorador;
        return this;
    }

    public ResultadoPrecio calcular(ContextoPrecio contexto) {
        double precioFinal = cadena.calcularPrecio(contexto);
        double precioBase  = contexto.totalBase();
        double ahorro      = precioBase - precioFinal;
        return new ResultadoPrecio(precioBase, precioFinal, ahorro, cadena.descripcion());
    }
}
```

---

### Record de resultado

```java
package com.openlib.pricing;

/**
 * Resultado inmutable del cálculo de precio.
 * Contiene toda la información para mostrar en el ticket de compra.
 */
public record ResultadoPrecio(
    double precioBase,
    double precioFinal,
    double ahorro,
    String descripcionReglas
) {
    public double porcentajeAhorroTotal() {
        if (precioBase == 0) return 0;
        return (ahorro / precioBase) * 100.0;
    }

    @Override
    public String toString() {
        return """
            ┌─────────────────────────────────────────┐
            │         RESUMEN DE PRECIO                │
            ├─────────────────────────────────────────┤
            │ Precio base:   $%,.0f COP
            │ Precio final:  $%,.0f COP
            │ Ahorro:        $%,.0f COP (%.1f%%)
            ├─────────────────────────────────────────┤
            │ Reglas: %s
            └─────────────────────────────────────────┘
            """.formatted(precioBase, precioFinal, ahorro,
                          porcentajeAhorroTotal(), descripcionReglas);
    }
}
```

---

### `main` — demostración completa de combinaciones

```java
package com.openlib.pricing;

import com.openlib.model.*;
import com.openlib.pricing.model.ContextoPrecio;

import java.util.List;

public class DemoPrecios {

    public static void main(String[] args) {

        // ── Datos de prueba alineados con los modelos reales del proyecto ──────

        User comprador = User.builder()
                .name("María López")
                .email("maria@openlib.com")
                .role("BUYER")
                .build();

        Book libro1 = Book.builder()
                .title("Clean Code").author("Martin").price(85_000.0).build();

        Book libro2 = Book.builder()
                .title("Diseño de Patrones GoF").author("Gamma et al.").price(120_000.0).build();

        Book libro3 = Book.builder()
                .title("Domain-Driven Design").author("Evans").price(95_000.0).build();

        List<CartItem> items = List.of(
                new CartItem(libro1),
                new CartItem(libro2),
                new CartItem(libro3)
        );

        // Total base: $300,000 COP → elegible para volumen

        // ── Escenario 1: Cliente nuevo, sin semana del libro ──────────────────
        ContextoPrecio ctx1 = new ContextoPrecio(comprador, items, 2, false);

        ResultadoPrecio resultado1 = CalculadorPrecio.nuevo()
                .conDescuentoFidelidad()   // no aplica: solo 2 compras
                .conDescuentoTemporada()   // no aplica: no es semana del libro
                .conDescuentoVolumen()     // APLICA: $300k > $200k
                .calcular(ctx1);

        System.out.println("═══ Escenario 1: Solo descuento por volumen ═══");
        System.out.println(resultado1);
        // Precio final: $300,000 × 0.95 = $285,000 COP

        // ── Escenario 2: Cliente fiel en semana del libro ─────────────────────
        ContextoPrecio ctx2 = new ContextoPrecio(comprador, items, 8, true);

        ResultadoPrecio resultado2 = CalculadorPrecio.nuevo()
                .conDescuentoFidelidad()   // APLICA: 8 > 5 compras
                .conDescuentoTemporada()   // APLICA: es semana del libro
                .conDescuentoVolumen()     // APLICA: $300k > $200k
                .calcular(ctx2);

        System.out.println("═══ Escenario 2: Triple descuento (cascada) ═══");
        System.out.println(resultado2);
        // Precio final en cascada:
        //   $300,000 → fidelidad (-10%)  = $270,000
        //   $270,000 → temporada (-15%)  = $229,500
        //   $229,500 → volumen (-5%)     = $218,025 COP

        // ── Escenario 3: Sin descuentos ───────────────────────────────────────
        ContextoPrecio ctx3 = new ContextoPrecio(comprador,
                List.of(new CartItem(libro1)), // solo $85,000 — no supera umbral
                1, false);

        ResultadoPrecio resultado3 = CalculadorPrecio.nuevo()
                .conDescuentoFidelidad()
                .conDescuentoTemporada()
                .conDescuentoVolumen()
                .calcular(ctx3);

        System.out.println("═══ Escenario 3: Sin descuentos aplicables ═══");
        System.out.println(resultado3);
        // Precio final: $85,000 COP (igual al base)
    }
}
```

---

### Test unitario por escenario

```java
package com.openlib.pricing;

import com.openlib.model.*;
import com.openlib.pricing.model.ContextoPrecio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CalculadorPrecioTest {

    private List<CartItem> carritoGrande;   // $300,000 base
    private List<CartItem> carritoPequeno;  // $85,000 base
    private User comprador;

    @BeforeEach
    void setUp() {
        comprador = User.builder().name("Test").email("t@t.com").build();

        Book caro1 = Book.builder().title("A").author("X").price(150_000.0).build();
        Book caro2 = Book.builder().title("B").author("Y").price(150_000.0).build();
        Book barato = Book.builder().title("C").author("Z").price(85_000.0).build();

        carritoGrande  = List.of(new CartItem(caro1), new CartItem(caro2));
        carritoPequeno = List.of(new CartItem(barato));
    }

    @Test
    void soloVolumen_cuandoCarritoSuperaUmbral() {
        var ctx = new ContextoPrecio(comprador, carritoGrande, 2, false);
        var resultado = CalculadorPrecio.nuevo()
                .conDescuentoFidelidad()
                .conDescuentoTemporada()
                .conDescuentoVolumen()
                .calcular(ctx);

        assertEquals(300_000.0, resultado.precioBase(), 0.01);
        assertEquals(285_000.0, resultado.precioFinal(), 0.01); // -5%
    }

    @Test
    void tripleDescuentoEnCascada_cuandoTodosAplican() {
        var ctx = new ContextoPrecio(comprador, carritoGrande, 8, true);
        var resultado = CalculadorPrecio.nuevo()
                .conDescuentoFidelidad()   // -10% sobre base
                .conDescuentoTemporada()   // -15% sobre resultado anterior
                .conDescuentoVolumen()     // -5% sobre resultado anterior
                .calcular(ctx);

        // 300,000 × 0.90 × 0.85 × 0.95 = 218,025
        assertEquals(218_025.0, resultado.precioFinal(), 0.01);
        assertTrue(resultado.porcentajeAhorroTotal() > 27.0);
    }

    @Test
    void sinDescuentos_cuandoNingunaCondicionAplica() {
        var ctx = new ContextoPrecio(comprador, carritoPequeno, 1, false);
        var resultado = CalculadorPrecio.nuevo()
                .conDescuentoFidelidad()
                .conDescuentoTemporada()
                .conDescuentoVolumen()
                .calcular(ctx);

        assertEquals(85_000.0, resultado.precioBase(), 0.01);
        assertEquals(85_000.0, resultado.precioFinal(), 0.01);
        assertEquals(0.0, resultado.ahorro(), 0.01);
    }

    @Test
    void descripcionRefleja_reglasAplicadas() {
        var ctx = new ContextoPrecio(comprador, carritoGrande, 8, true);
        var resultado = CalculadorPrecio.nuevo()
                .conDescuentoFidelidad()
                .conDescuentoTemporada()
                .calcular(ctx);

        assertTrue(resultado.descripcionReglas().contains("fidelidad"));
        assertTrue(resultado.descripcionReglas().contains("semana del libro"));
    }

    @Test
    void extensionOCP_nuevoDescuentoSinModificarClasesExistentes() {
        // Descuento futuro: suscripción premium 20%
        // Crear CondicionSuscripcionPremium y DecoradorSuscripcion
        // solo para demostrar que el punto de extensión funciona:
        var condicion = (com.openlib.pricing.condition.CondicionDescuento) ctx -> true;

        var decoradorCustom = new com.openlib.pricing.decorator.DecoradorDescuento(
                new com.openlib.pricing.decorator.PrecioBase(), condicion) {
            @Override protected double porcentajeDescuento() { return 0.20; }
            @Override protected String nombreDescuento() { return "Suscripción premium (-20%)"; }
        };

        var ctx = new ContextoPrecio(comprador, carritoGrande, 1, false);
        double precioFinal = decoradorCustom.calcularPrecio(ctx);

        assertEquals(240_000.0, precioFinal, 0.01); // 300k × 0.80
    }
}
```

---

### Estructura de paquetes resultante

```
com.openlib/
├── model/
│   ├── User.java                            ← sin cambios
│   ├── Book.java                            ← sin cambios
│   └── CartItem.java                        ← sin cambios
└── pricing/
    ├── CalculadorPrecio.java                ← Builder fluido
    ├── ResultadoPrecio.java                 ← record inmutable
    ├── model/
    │   └── ContextoPrecio.java              ← record con datos del carrito
    ├── condition/
    │   ├── CondicionDescuento.java          ← @FunctionalInterface (Strategy)
    │   ├── CondicionFidelidad.java
    │   ├── CondicionTemporada.java
    │   └── CondicionVolumen.java
    └── decorator/
        ├── ComponentePrecio.java            ← interfaz Decorator
        ├── PrecioBase.java                  ← componente concreto base
        ├── DecoradorDescuento.java          ← decorador abstracto
        ├── DecoradorFidelidad.java
        ├── DecoradorTemporada.java
        └── DecoradorVolumen.java
```

---

### Flujo visual del triple descuento

```
ContextoPrecio(totalBase=$300,000, compras=8, semanaLibro=true)
       │
       ▼
PrecioBase.calcularPrecio()
  → $300,000
       │
       ▼
DecoradorFidelidad (condicion.aplica=true ✓)
  → $300,000 × 0.90 = $270,000
       │
       ▼
DecoradorTemporada (condicion.aplica=true ✓)
  → $270,000 × 0.85 = $229,500
       │
       ▼
DecoradorVolumen (condicion.aplica=true ✓, 300k > 200k)
  → $229,500 × 0.95 = $218,025
       │
       ▼
ResultadoPrecio(base=$300,000, final=$218,025, ahorro=$81,975 = 27.3%)
```
```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?



#### 2. ¿Qué se puede mejorar?



#### 3. Respuesta final
