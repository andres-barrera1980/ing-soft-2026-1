# Pregunta P04: Patrones — Estrategias de descuento

### Estudiante
- **Nombre completo**: Mateo Traslaviña Moreno

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Claude |
| **Modelo especifico** | Claude Sonnet 4.6 |
| **¿Por que elegiste este LLM?** | La tarea requiere tanto identificar el patron correcto como justificar la eleccion y descartar alternativas, lo que requiere razonamiento de diseño. Claude es fuerte en justificaciones arquitectonicas y en producir codigo Java idiomatico con Spring. |

---

### Prompt utilizado

```
Eres un arquitecto de software revisando el modulo de descuentos de OpenLib Market, una plataforma de e-commerce de libros en Java 21 con Spring Boot 3.x.

El sistema necesita aplicar estas estrategias de descuento:
- Descuento por fidelidad: 10% para compradores con más de 5 compras anteriores.
- Descuento por temporada: 15% en la semana del libro (fechas configurables).
- Descuento por volumen: 5% si el total del carrito supera $200,000 COP.
- Sin descuento: precio normal.

Reglas de composición criticas:
- Las estrategias pueden COMBINARSE: un cliente fiel en la semana del libro recibe AMBOS descuentos.
- El precio final se calcula dinamicamente en cada request segun fecha, perfil del usuario y contenido del carrito.
- En el futuro podrian agregarse nuevos tipos de descuento sin modificar código existente.

Necesito que:
1. Identifiques el patrón de diseño más adecuado para este escenario. Justifica tecnicamente por que ese patrón.
2. Descartes explícitamente al menos DOS alternativas de patrones, explicando por qué NO son adecuadas.
3. Propongas una implementación completa en Java 21 con:
   - Interfaces y clases principales
   - Manejo de la composición (múltiples descuentos aplicados en cadena)
   - Ejemplo de uso completo mostrando el cálculo del precio final
4. Explica cómo se agregaría un nuevo tipo de descuento "PRIMERA_COMPRA" (20% para usuarios nuevos) sin modificar clases existentes.

Formato de salida: "Patrón elegido + justificación", "Alternativas descartadas", "Implementación", "Extensión futura".
```

---

### Respuesta del LLM

## Patrón elegido: Strategy + Composite

El patrón **Strategy** define la interfaz base para cada descuento (calculable e intercambiable). El patrón **Composite** (o Decorator en variante) permite combinar múltiples estrategias. Juntos resuelven dos problemas distintos: encapsular los algoritmos de descuento (Strategy) y aplicarlos en composición (Composite).

**Justificación técnica**: Cada tipo de descuento tiene su propio algoritmo de cálculo. Strategy permite encapsular cada algoritmo en una clase separada. La composición es necesaria porque el enunciado dice explícitamente que los descuentos se acumulan.

## Alternativas descartadas

**Template Method**: Define el esqueleto de un algoritmo con pasos que subclases sobreescriben. El problema es que asume una sola jerarquía de herencia. Si queremos combinar descuentos de fidelidad + temporada, necesitaríamos crear una subclase por cada combinación posible — explosión combinatoria de clases.

**State**: El patrón State es para objetos que cambian su comportamiento según su estado interno. Los descuentos no son estados de un objeto — son reglas de negocio independientes que se pueden combinar. State no facilita la composición.

## Implementación

```java
// Interfaz Strategy base
public interface EstrategiaDescuento {
    /**
     * Calcula el porcentaje de descuento (0.0 a 1.0) dado el contexto.
     * Ejemplo: 0.10 = 10% de descuento
     */
    BigDecimal calcularDescuento(ContextoDescuento contexto);
    String getNombre();
}

// Contexto con toda la información necesaria
public record ContextoDescuento(
    int comprasPrevias,
    LocalDate fechaActual,
    BigDecimal totalCarrito,
    boolean esPrimeraCompra
) {}

// Estrategia: descuento por fidelidad
@Service
public class DescuentoFidelidadStrategy implements EstrategiaDescuento {
    private static final int MIN_COMPRAS = 5;
    private static final BigDecimal DESCUENTO = new BigDecimal("0.10");
    
    @Override
    public BigDecimal calcularDescuento(ContextoDescuento ctx) {
        return ctx.comprasPrevias() > MIN_COMPRAS ? DESCUENTO : BigDecimal.ZERO;
    }
    
    @Override
    public String getNombre() { return "FIDELIDAD_10%"; }
}

// Estrategia: descuento por temporada
@Service
public class DescuentoTemporadaStrategy implements EstrategiaDescuento {
    @Value("${descuento.temporada.inicio}") private LocalDate inicio;
    @Value("${descuento.temporada.fin}") private LocalDate fin;
    private static final BigDecimal DESCUENTO = new BigDecimal("0.15");
    
    @Override
    public BigDecimal calcularDescuento(ContextoDescuento ctx) {
        LocalDate hoy = ctx.fechaActual();
        return (!hoy.isBefore(inicio) && !hoy.isAfter(fin)) ? DESCUENTO : BigDecimal.ZERO;
    }
    
    @Override
    public String getNombre() { return "TEMPORADA_15%"; }
}

// Estrategia: descuento por volumen
@Service
public class DescuentoVolumenStrategy implements EstrategiaDescuento {
    private static final BigDecimal UMBRAL = new BigDecimal("200000");
    private static final BigDecimal DESCUENTO = new BigDecimal("0.05");
    
    @Override
    public BigDecimal calcularDescuento(ContextoDescuento ctx) {
        return ctx.totalCarrito().compareTo(UMBRAL) > 0 ? DESCUENTO : BigDecimal.ZERO;
    }
    
    @Override
    public String getNombre() { return "VOLUMEN_5%"; }
}

// Compositor: aplica todas las estrategias y acumula descuentos
@Service
public class CalculadorPrecioService {
    private final List<EstrategiaDescuento> estrategias;
    
    public CalculadorPrecioService(List<EstrategiaDescuento> estrategias) {
        this.estrategias = estrategias;
    }
    
    public ResultadoDescuento calcular(BigDecimal precioBase, ContextoDescuento contexto) {
        List<String> descuentosAplicados = new ArrayList<>();
        BigDecimal totalDescuento = BigDecimal.ZERO;
        
        for (EstrategiaDescuento estrategia : estrategias) {
            BigDecimal descuento = estrategia.calcularDescuento(contexto);
            if (descuento.compareTo(BigDecimal.ZERO) > 0) {
                totalDescuento = totalDescuento.add(descuento);
                descuentosAplicados.add(estrategia.getNombre());
            }
        }
        
        // Aplicar descuento total (máximo 100%)
        BigDecimal factorDescuento = totalDescuento.min(BigDecimal.ONE);
        BigDecimal precioFinal = precioBase.multiply(BigDecimal.ONE.subtract(factorDescuento))
            .setScale(0, RoundingMode.HALF_UP);
        
        return new ResultadoDescuento(precioBase, precioFinal, factorDescuento, descuentosAplicados);
    }
}

// DTO de resultado
public record ResultadoDescuento(
    BigDecimal precioOriginal,
    BigDecimal precioFinal,
    BigDecimal descuentoTotal,
    List<String> descuentosAplicados
) {}
```

## Extensión futura

Para agregar `PRIMERA_COMPRA` (20%), **solo se agrega una clase nueva**:

```java
@Service
public class DescuentoPrimeraCompraStrategy implements EstrategiaDescuento {
    private static final BigDecimal DESCUENTO = new BigDecimal("0.20");
    
    @Override
    public BigDecimal calcularDescuento(ContextoDescuento ctx) {
        return ctx.esPrimeraCompra() ? DESCUENTO : BigDecimal.ZERO;
    }
    
    @Override
    public String getNombre() { return "PRIMERA_COMPRA_20%"; }
}
```

Spring la inyecta automáticamente en `CalculadorPrecioService`. OCP respetado.

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

El prompt fue efectivo en dos areas clave. Primero, exigir que el LLM descartara **dos alternativas explícitamente** forzó un análisis más profundo que solo decir "use Strategy". Las comparaciones con Template Method y State son tecnicamente correctas. El uso de `record` para `ContextoDescuento` y `ResultadoDescuento` muestra Java idiomático moderno (Java 16+), algo que el prompt habilitó al pedir Java 21.

#### 2. ¿Qué se puede mejorar?

El LLM aplicó descuentos de forma **aditiva** (suma los porcentajes), lo cual puede no ser el comportamiento correcto de negocio. En muchos e-commerces los descuentos son multiplicativos: 10% + 15% no es 25%, sino aplicar 10% al precio y luego 15% al resultado. Esta distinción de negocio crítica no fue abordada.

También **no hay limite máximo de descuento configurable**. El LLM usa `BigDecimal.ONE` como limite hardcodeado, pero en OpenLib Market podría haber una política de máximo 30% de descuento que debería ser configurable.

El `ContextoDescuento` como record podria volverse muy grande con más campos — necesitaria refactoring a Builder pattern con el tiempo.

#### 3. Respuesta final

**El patrón correcto es Strategy** para encapsular cada algoritmo de descuento. La composición de multiples estrategias se logra con el `CalculadorPrecioService` que itera sobre todas las estrategias disponibles — esto es en realidad el patrón **Chain of Responsibility en modo acumulativo** más que Composite, pero el resultado funcional es correcto.

