
## Pregunta [04]: [Identificar y aplicar el patrón correcto para estrategias de descuento]

### Estudiante
- **Nombre completo**: Julian Felipe Rojas Almanza

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Gpt |
| **Modelo específico** | Gpt-4o |
| **¿Por qué elegiste este LLM?** | Es un modelo maduro para resolver problemas de diseño avanzados donde los patrones se cruzan o combinan. Tienen la capacidad analítica necesaria para descartar con fundamentos técnicos otras alternativas del catálogo GoF. |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```
Actúa como un Arquitecto de Software Senior y experto en Patrones de Diseño de GoF (Gang of Four). Estoy resolviendo un ejercicio técnico académico sobre el proyecto "OpenLib Market" y necesito que respondas exactamente a las tareas y justificaciones solicitadas sobre el sistema de descuentos que te presentaré.

[CONTEXTO]
La plataforma "OpenLib Market" necesita aplicar diferentes estrategias de descuento durante el año que pueden combinarse entre sí (por ejemplo, un cliente fiel puede recibir descuento de temporada + fidelidad acumulados). El precio final debe calcularse dinámicamente según la fecha, el perfil del usuario y el contenido del carrito. Las reglas iniciales son:
- Descuento por fidelidad: 10% para compradores con más de 5 compras.
- Descuento por temporada: 15% en la semana del libro.
- Descuento por volumen: 5% si el carrito supera $200,000 COP.
- Sin descuento: precio normal.

[PROBLEMA / TAREA]
Necesito que resuelvas y respondas de forma directa y explícita a los siguientes puntos requeridos por mi enunciado:
1. Identifica qué patrón de diseño (o combinación de patrones) es el más adecuado para resolver este escenario, permitiendo tanto la intercambiabilidad de los descuentos como su composición/combinación dinámica.
2. Justifica detalladamente por qué elegiste ese patrón de diseño frente a la necesidad de combinar reglas y descarta explícitamente al menos otras dos alternativas de patrones de diseño explicando por qué no serían óptimas para este caso.
3. Propón una implementación completa que incluya las interfaces, las clases de las estrategias de descuento, el contexto que las administra y un ejemplo de uso (método main o test conceptual) donde se demuestre cómo se aplican y combinan los descuentos para un carrito de compras.

[RESTRICCIONES]
- Chain of Thought: Antes de escribir el código, explica detalladamente cómo tu solución abordará el requerimiento clave de que "estas estrategias pueden combinarse" sin caer en estructuras rígidas o acoplamiento mutuo entre los descuentos.
- Evita el uso de condicionales anidados (if/else) para decidir la combinación de descuentos en el cliente.
- El código resultante debe ser limpio, idiomático y fácil de testear.

[FORMATO DE SALIDA]
Estructura tu respuesta en Markdown utilizando única y estrictamente estos cuatro encabezados para organizar la información:
- ### 1. Identificación del Patrón de Diseño Adecuado
- ### 2. Justificación Técnica y Descarte de Alternativas
- ### 3. Estrategia para la Combinación de Descuentos
- ### 4. Implementación del Código

[VALIDACIÓN]
Sabré que tu respuesta es excelente si el patrón seleccionado resuelve de forma elegante la composición de los descuentos, si los descartes de las otras dos alternativas demuestran un conocimiento profundo de los trade-offs de diseño, y si el código de ejemplo compila conceptualmente y muestra la reducción dinámica del precio del carrito al combinar más de un descuento.
```

---

### Respuesta del LLM

# Solución

### 1. Identificación del Patrón de Diseño Adecuado

**Patrón principal: Strategy**

Cada descuento representa una forma distinta de calcular una reducción sobre el precio, por lo que encaja perfectamente con el patrón **Strategy**.

Sin embargo, el requisito más importante es que **los descuentos puedan combinarse dinámicamente**.

Por ello, la mejor solución es utilizar:

* **Strategy** para representar cada descuento.
* **Composite (o composición de estrategias)** para agrupar varios descuentos y aplicarlos en conjunto.

De esta forma cada descuento es independiente y se pueden agregar o quitar descuentos sin modificar el resto del sistema.

---

### 2. Justificación Técnica y Descarte de Alternativas

#### ¿Por qué Strategy + Composite?

Porque permite:

* Intercambiar descuentos fácilmente.
* Agregar nuevos descuentos sin modificar código existente.
* Combinar múltiples descuentos de forma dinámica.
* Cumplir el principio Abierto/Cerrado (OCP).

Cada descuento conoce únicamente su propia regla.

---

#### Alternativa descartada: Factory Method

Una fábrica sirve para crear objetos, pero no resuelve el problema de combinar descuentos.

Aunque podría crear estrategias, seguiríamos necesitando otra solución para aplicarlas juntas.

Por sí sola no es suficiente.

---

#### Alternativa descartada: Template Method

Template Method define una secuencia fija de pasos.

El problema es que aquí las combinaciones cambian constantemente:

* Fidelidad.
* Temporada.
* Volumen.
* Futuras promociones.

Una plantilla fija volvería el sistema rígido y difícil de extender.

---

### 3. Estrategia para la Combinación de Descuentos

La combinación se realiza mediante una colección de estrategias.

El contexto mantiene una lista de descuentos activos y los aplica secuencialmente.

Ejemplo:

1. Precio inicial: $300.000
2. Fidelidad: -10%
3. Temporada: -15%
4. Volumen: -5%

Cada estrategia se ejecuta sin conocer a las demás.

Esto evita:

* Condicionales gigantes.
* Acoplamiento entre descuentos.
* Modificaciones al contexto cuando aparezcan nuevas promociones.

---

### 4. Implementación del Código en Java 21

#### Modelo básico

```java
public record Cliente(int comprasRealizadas) {
}
```

```java
import java.time.LocalDate;

public record Carrito(
        double total,
        Cliente cliente,
        LocalDate fecha
) {
}
```

#### Interfaz Strategy

```java
public interface Descuento {

    double aplicar(double precioActual, Carrito carrito);
}
```

#### Sin descuento

```java
public class SinDescuento implements Descuento {

    @Override
    public double aplicar(double precioActual, Carrito carrito) {
        return precioActual;
    }
}
```

#### Descuento por fidelidad

```java
public class DescuentoFidelidad implements Descuento {

    @Override
    public double aplicar(double precioActual, Carrito carrito) {

        if (carrito.cliente().comprasRealizadas() > 5) {
            return precioActual * 0.90;
        }

        return precioActual;
    }
}
```

#### Descuento por temporada

```java
import java.time.Month;

public class DescuentoTemporada implements Descuento {

    @Override
    public double aplicar(double precioActual, Carrito carrito) {

        if (carrito.fecha().getMonth() == Month.APRIL) {
            return precioActual * 0.85;
        }

        return precioActual;
    }
}
```

#### Descuento por volumen

```java
public class DescuentoVolumen implements Descuento {

    @Override
    public double aplicar(double precioActual, Carrito carrito) {

        if (carrito.total() > 200000) {
            return precioActual * 0.95;
        }

        return precioActual;
    }
}
```

#### Composite de descuentos

```java
import java.util.ArrayList;
import java.util.List;

public class DescuentosCombinados implements Descuento {

    private final List<Descuento> descuentos =
            new ArrayList<>();

    public void agregar(Descuento descuento) {
        descuentos.add(descuento);
    }

    @Override
    public double aplicar(double precioActual,
                          Carrito carrito) {

        double resultado = precioActual;

        for (Descuento descuento : descuentos) {
            resultado =
                    descuento.aplicar(resultado,
                                      carrito);
        }

        return resultado;
    }
}
```

#### Contexto

```java
public class CalculadorPrecio {

    private final Descuento descuento;

    public CalculadorPrecio(Descuento descuento) {
        this.descuento = descuento;
    }

    public double calcular(Carrito carrito) {
        return descuento.aplicar(
                carrito.total(),
                carrito
        );
    }
}
```

#### Ejemplo de uso

```java
import java.time.LocalDate;

public class Main {

    public static void main(String[] args) {

        Cliente cliente = new Cliente(10);

        Carrito carrito = new Carrito(
                300000,
                cliente,
                LocalDate.of(2026, 4, 20)
        );

        DescuentosCombinados descuentos =
                new DescuentosCombinados();

        descuentos.agregar(
                new DescuentoFidelidad());

        descuentos.agregar(
                new DescuentoTemporada());

        descuentos.agregar(
                new DescuentoVolumen());

        CalculadorPrecio calculador =
                new CalculadorPrecio(descuentos);

        double precioFinal =
                calculador.calcular(carrito);

        System.out.println(
                "Precio final: $" + precioFinal);
    }
}
```

### Resultado esperado

Precio inicial: **$300.000**

* Fidelidad → 10%
* Temporada → 15%
* Volumen → 5%

Precio final aproximado:

```text
$218.025
```

Se pueden agregar nuevos descuentos simplemente creando una nueva clase que implemente `Descuento` y agregándola al `DescuentosCombinados`, sin modificar el código existente.

```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

Se ha logrado un prompt de alta calidad al seguir la segmentación detallada en la guía de prompting. El proceso incluyó una mejora asistida por una Gema de Gemini y una intervención final de mi parte para corregirlo y pulirlo. Al contar con rol, contexto e instrucciones claras, la estructura de salida es simple y cumple rigurosamente con lo pedido.


#### 2. ¿Qué se puede mejorar?

El prompt actual es robusto y está perfectamente balanceado gracias a la revisión final. Al haber seguido la guía .md y el proceso de filtrado con Gema, los cambios necesarios son prácticamente inexistentes. Lo único que se podría hacer es experimentar con el formato de salida si las necesidades del proyecto cambian, pero técnicamente el prompt ya está al 100%.


#### 3. Respuesta final

[Escribe tu respuesta definitiva a la pregunta del parcial, integrando lo que aprendiste del LLM pero yendo más allá. Corrige errores, llena omisiones, conecta con conceptos vistos en clase. Esta es tu respuesta: demuestra que tú dominas el tema.]
