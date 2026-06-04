# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

# Pregunta P04: Patrón de diseño para estrategias de descuento

### Estudiante
- **Nombre completo**: Juan Camilo Gomez

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Claude |
| **Modelo específico** | Claude Sonnet 4.6 |
| **¿Por qué elegiste este LLM?** | La pregunta requiere justificar la elección de un patrón y descartar alternativas, lo que exige razonamiento comparativo.

---

### Prompt utilizado

Soy estudiante de Ingeniería de Software en la Pontificia Universidad Javeriana.
Proyecto: OpenLib Market, plataforma de venta de libros digitales en Colombia.

OpenLib Market necesita aplicar diferentes estrategias de descuento:
- Descuento por fidelidad: 10% para compradores con más de 5 compras
- Descuento por temporada: 15% en la semana del libro
- Descuento por volumen: 5% si el carrito supera $200,000 COP
- Sin descuento: precio normal
- IMPORTANTE: las estrategias pueden combinarse (ej: cliente fiel en semana del libro = 10% + 15%)

El precio final se calcula dinámicamente según la fecha, el perfil del usuario y el contenido del carrito.

Tareas:
1. Identifica qué patrón de diseño de los vistos en clase (Singleton, Factory, Observer, Strategy, Decorator, Command, Template Method) es el más adecuado para este escenario.
2. Justifica por qué ese patrón.
3. Descarta explícitamente al menos otras dos alternativas, explicando por qué no son adecuadas.
4. Propón una implementación completa en Java 21 con: interfaces, clases concretas, y un ejemplo de uso que muestre descuentos combinados.

Restricciones:
- Java 21
- El diseño debe permitir combinar múltiples descuentos
- El diseño debe permitir agregar nuevas estrategias sin modificar código existente
- Responde en markdown con bloques de código Java
```

---

### Respuesta del LLM

## Patrón para estrategias de descuento en OpenLib Market

### Patrón elegido: Strategy + Composite

El escenario requiere dos capacidades:
1. Encapsular algoritmos de descuento intercambiables → **Strategy**
2. Combinar múltiples descuentos como si fueran uno solo → **Composite** (o una lista de strategies aplicadas en cadena)

### Alternativas descartadas

**Template Method**: Define el esqueleto de un algoritmo en una clase base y permite que las subclases sobreescriban pasos específicos. El problema es que usa herencia, no composición, lo que dificulta combinar descuentos en tiempo de ejecución. No se puede "mezclar" un `DescuentoFidelidad` con un `DescuentoTemporada` fácilmente.

**Decorator**: Parecería viable porque permite agregar comportamiento en capas, pero su propósito es agregar responsabilidades a un objeto individual, no aplicar algoritmos alternativos sobre el mismo dato. Además, el orden en que se aplican los decoradores afecta el resultado de formas inesperadas.

---

### Implementación

```java
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

// Abstracción Strategy
public interface EstrategiaDescuento {
    BigDecimal calcularDescuento(BigDecimal precioBase, Carrito carrito, Usuario usuario);
    String getNombre();
}
```

```java
// Estrategia 1: Descuento por fidelidad
public class DescuentoFidelidad implements EstrategiaDescuento {
    private static final BigDecimal PORCENTAJE = new BigDecimal("0.10");
    private static final int MIN_COMPRAS = 5;

    @Override
    public BigDecimal calcularDescuento(BigDecimal precioBase, Carrito carrito, Usuario usuario) {
        if (usuario.getTotalCompras() > MIN_COMPRAS) {
            return precioBase.multiply(PORCENTAJE).setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    @Override
    public String getNombre() { return "Descuento por fidelidad (10%)"; }
}
```

```java
// Estrategia 2: Descuento por temporada
public class DescuentoTemporada implements EstrategiaDescuento {
    private static final BigDecimal PORCENTAJE = new BigDecimal("0.15");
    private final LocalDate inicioSemanaLibro;
    private final LocalDate finSemanaLibro;

    public DescuentoTemporada(LocalDate inicio, LocalDate fin) {
        this.inicioSemanaLibro = inicio;
        this.finSemanaLibro = fin;
    }

    @Override
    public BigDecimal calcularDescuento(BigDecimal precioBase, Carrito carrito, Usuario usuario) {
        LocalDate hoy = LocalDate.now();
        if (!hoy.isBefore(inicioSemanaLibro) && !hoy.isAfter(finSemanaLibro)) {
            return precioBase.multiply(PORCENTAJE).setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    @Override
    public String getNombre() { return "Descuento por temporada (15%)"; }
}
```

```java
// Estrategia 3: Descuento por volumen
public class DescuentoVolumen implements EstrategiaDescuento {
    private static final BigDecimal PORCENTAJE = new BigDecimal("0.05");
    private static final BigDecimal MINIMO_CARRITO = new BigDecimal("200000");

    @Override
    public BigDecimal calcularDescuento(BigDecimal precioBase, Carrito carrito, Usuario usuario) {
        if (carrito.getTotal().compareTo(MINIMO_CARRITO) > 0) {
            return precioBase.multiply(PORCENTAJE).setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    @Override
    public String getNombre() { return "Descuento por volumen (5%)"; }
}
```

```java
// CalculadorDescuentos: aplica todas las estrategias activas y las suma
public class CalculadorDescuentos {
    private final List<EstrategiaDescuento> estrategias;

    public CalculadorDescuentos(List<EstrategiaDescuento> estrategias) {
        this.estrategias = estrategias;
    }

    public BigDecimal calcularPrecioFinal(BigDecimal precioBase, Carrito carrito, Usuario usuario) {
        BigDecimal totalDescuento = estrategias.stream()
            .map(e -> e.calcularDescuento(precioBase, carrito, usuario))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // El descuento total no puede superar el precio base
        if (totalDescuento.compareTo(precioBase) > 0) totalDescuento = precioBase;

        return precioBase.subtract(totalDescuento).setScale(2, RoundingMode.HALF_UP);
    }
}
```

```java
// Ejemplo de uso con descuentos combinados
public class EjemploUso {
    public static void main(String[] args) {
        List<EstrategiaDescuento> estrategias = List.of(
            new DescuentoFidelidad(),
            new DescuentoTemporada(LocalDate.of(2026, 4, 20), LocalDate.of(2026, 4, 27)),
            new DescuentoVolumen()
        );

        CalculadorDescuentos calculador = new CalculadorDescuentos(estrategias);
        BigDecimal precioFinal = calculador.calcularPrecioFinal(
            new BigDecimal("250000"), carrito, usuarioFiel
        );
        // Un cliente fiel con carrito > $200k en semana del libro:
        // Descuento = 10% + 15% + 5% = 30% → precio final: $175,000 COP
    }
}
```


---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

mejoro mucho que definiera que las estrategias "pueden combinarse", lo que guio al LLM hacia una solución mas profunda. El prompt tambien señalo los patrones vistos en clase, lo que evito respuestas con patrones fuera de lo estipulado y por ultimo la restriccion de "descarta explicitamente al menos dos alternativas" hizo que hiciera el analisis comparativo que se necesitaba


#### 2. ¿Qué se puede mejorar?

no pidio que el LLM considerara el caso de descuentos con tope y no se pregunto por el orden de aplicación de los descuentos. por lo que la respuesta aplica todos los descuentos sobre el precio base, no de forma encadenada, lo que hace diferencia

#### 3. Respuesta final

El patron strategy con una lista de estrategias es la solución correcta para este escenario. lo que hace que,
- Encapsular cada regla de descuento en su propia clase 
- Agregar nuevas estrategias sin modificar calcular descuentos
- Combinar estrategias en tiempo de ejecución según el contexto

La respuesta del LLM es buena pero se salta el tope de descuento. En un sistema de produccion, el descuento acumulado no deberia poder superar el precio del producto ni generar precios negativos
