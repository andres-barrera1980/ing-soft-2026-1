# Plantilla de entrega — Parcial 2



## Pregunta [04]: [P04_patrones_descuento]

### Estudiante
- **Nombre completo**: [Danna Gabriela Rojas Bernal]

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **gemini** | [Claude / ChatGPT / Gemini / Copilot / DeepSeek / Qwen / Mistral / Otro / **Sin IA — respuesta propia**] |
| **gemini flash** | [Ej: Claude Opus 4.5, GPT-4o, Gemini 2.5 Pro, etc. Si respondes sin IA, escribe "N/A"] |
| **se requiere para el ejercicio** | [Justifica en 1-3 oraciones. Si respondes sin IA, explica por qué decidiste no usar LLM para esta pregunta.] |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```
[Pega aquí el prompt exacto que enviaste al LLM. 
Incluye TODO el texto, sin editar ni resumir.

Un buen prompt incluye:
- Contexto del proyecto OpenLib Market
- El código o situación específica
- Lo que esperas que el LLM haga
- Restricciones (ej: "usa Java 21", "aplica SOLID")
- Formato de salida esperado (ej: "respuesta en markdown con código Java")]
```
Actúa como un Arquitecto de Software Senior especializado en patrones de diseño GoF, SOLID y Java.

Contexto:

OpenLib Market necesita aplicar diferentes estrategias de descuento durante el año:

* Descuento por fidelidad: 10% para compradores con más de 5 compras.
* Descuento por temporada: 15% en la semana del libro.
* Descuento por volumen: 5% si el carrito supera $200.000 COP.
* Sin descuento: precio normal.

Importante:
Las estrategias pueden combinarse entre sí. Por ejemplo, un cliente puede recibir simultáneamente descuento por fidelidad y por temporada.

El precio final debe calcularse dinámicamente según:

* La fecha.
* El perfil del usuario.
* El contenido del carrito.

Tareas:

1. Identifica cuál es el patrón de diseño GoF más adecuado para resolver este problema.
2. Justifica detalladamente por qué ese patrón es la mejor opción.
3. Explica cómo permite combinar múltiples descuentos de manera flexible.
4. Diseña una solución completa en Java incluyendo:

   * Interfaces.
   * Clases concretas.
   * Clase principal u orquestador.
   * Ejemplo de uso.
5. Aplica principios SOLID cuando sea posible.
6. Explica cómo se agregaría un nuevo descuento en el futuro sin modificar el código existente.

Además:

* Analiza y descarta al menos dos patrones alternativos vistos en clase.
* Explica por qué esas alternativas son menos adecuadas para este escenario.
* Indica ventajas y desventajas de cada alternativa descartada.

Genera código Java limpio, comentado y siguiendo buenas prácticas.

---

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**
[Pega aquí la respuesta COMPLETA del LLM, sin editar, sin resumir.
Incluye TODO el texto, código, explicaciones que generó el LLM.

Si el LLM generó código, asegúrate de que esté correctamente formateado.
Si tuviste que hacer varias iteraciones, pega la MEJOR respuesta obtenida,
pero menciona cuántas iteraciones hiciste.]
```
Como Arquitecto de Software Senior, he analizado detalladamente el requerimiento para el subsistema de precios y descuentos de OpenLib Market. Este escenario presenta un desafío clásico de arquitectura: la necesidad de aplicar reglas de negocio dinámicas, mutables y, fundamentalmente, acumulables o combinables.

A continuación, presento el diseño arquitectónico de nivel empresarial que resuelve este problema de forma limpia, escalable y tolerante al cambio.

1. Patrón de Diseño GoF Seleccionado y Justificación
El patrón de diseño GoF más adecuado para resolver este problema es el patrón Decorator (Decorador), perteneciente a la categoría de patrones estructurales.

Justificación Detallada:
Composición Dinámica sobre Herencia: Si intentáramos resolver las combinaciones de descuentos mediante herencia tradicional, caeríamos en una explosión combinatoria de clases (ej: DescuentoFidelidadYTemporada, DescuentoVolumenYFidelidad, DescuentoTotal). El patrón Decorator nos permite añadir responsabilidades (descuentos) a un objeto de forma dinámica en tiempo de ejecución.

Transparencia en la Interfaz: Los decoradores tienen exactamente el mismo tipo que el componente que envuelven. Para el resto del sistema de OpenLib Market (como el módulo de Checkout), el objeto sigue siendo un calculador de precios común, sin importar si tiene cero, uno o tres descuentos aplicados envoltura tras envoltura.

Flexibilidad en la Combinación: Permite apilar los descuentos como capas de una cebolla. Cada capa recibe el precio calculado por la capa anterior, aplica su propio descuento sobre ese subtotal (o sobre el precio base, según la regla de negocio) y propaga el resultado.

2. Análisis y Descarte de Patrones Alternativos
Para fundamentar rigurosamente esta decisión arquitectónica, evaluamos y descartamos dos patrones comúnmente confundidos con este escenario:

Alternativa Descartada 1: Patrón Strategy (Estrategia)
Por qué es menos adecuada: El patrón Strategy es excelente para intercambiar algoritmos que son mutuamente excluyentes (como vimos en el procesador de pasarelas de pago, donde pagas con Tarjeta O pagas con PayPal, pero no con ambas al mismo tiempo). En el caso de los descuentos de OpenLib Market, las estrategias necesitan coexistir y acumularse sobre una misma transacción.

Ventajas de Strategy: Muy simple de implementar; alta cohesión; cumple perfectamente con ISP y OCP para algoritmos únicos.

Desventajas en este escenario: No tiene un mecanismo nativo para encadenar o combinar múltiples estrategias de forma transparente. Forzar a Strategy a combinarse obligaría a crear una estrategia compuesta compleja (Composite Strategy) que gestione internamente colecciones, perdiendo la elegancia del polimorfismo directo.

Alternativa Descartada 2: Patrón Chain of Responsibility (Cadena de Responsabilidad)
Por qué es menos adecuada: En la Cadena de Responsabilidad, una solicitud pasa a través de una serie de manejadores, pero un solo manejador procesa la solicitud y corta la cadena (el comportamiento típico de un middleware de autenticación o un sistema de soporte). Aunque se puede modificar para que todos los eslabones actúen, su semántica estructural está diseñada para la delegación del control de flujo, no para la transformación mutacional de un valor numérico acumulativo como el precio.

Ventajas de Chain of Responsibility: Desacopla completamente al emisor del receptor; permite reordenar los eslabones dinámicamente.

Desventajas en este escenario: Es propenso a errores de estado si un eslabón intermedio altera el objeto de forma inesperada. Además, complica el retorno de valores directos (como un BigDecimal calculado), obligando a pasar un objeto de contexto mutable a través de toda la cadena.

3. Diseño e Implementación en Java 21
A continuación, se presenta la solución de código limpio utilizando BigDecimal para evitar los errores de redondeo de punto flotante de double, tipado estricto y encapsulamiento.

A. Interfaces y Componente Base
Java
package com.openlib.market.pricing;

import java.math.BigDecimal;

// Contrato base para el cálculo de precios (Componente)
public interface CalculadorPrecio {
    BigDecimal calcularPrecioFinal();
}

// Implementación concreta base: El carrito con su precio nominal bruto
public class CarritoPrecioBase implements CalculadorPrecio {
    private final BigDecimal totalBruto;

    public CarritoPrecioBase(BigDecimal totalBruto) {
        if (totalBruto == null || totalBruto.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El total bruto no puede ser negativo ni nulo.");
        }
        this.totalBruto = totalBruto;
    }

    @Override
    public BigDecimal calcularPrecioFinal() {
        return this.totalBruto;
    }
}
B. Decorador Abstracto
Java
package com.openlib.market.pricing.decorators;

import com.openlib.market.pricing.CalculadorPrecio;
import java.util.Objects;

// Clase abstracta que mantiene la referencia al componente envuelto
public abstract class DescuentoDecorator implements CalculadorPrecio {
    protected final CalculadorPrecio calculadorEnvuelto;

    protected DescuentoDecorator(CalculadorPrecio calculadorEnvuelto) {
        this.calculadorEnvuelto = Objects.requireNonNull(calculadorEnvuelto, "El calculador base no puede ser nulo.");
    }
}
C. Decoradores Concretos (Estrategias de Descuento)
Java
package com.openlib.market.pricing.decorators.impl;

import com.openlib.market.pricing.CalculadorPrecio;
import com.openlib.market.pricing.decorators.DescuentoDecorator;
import java.math.BigDecimal;

// 1. Descuento por fidelidad (10%)
public class DescuentoFidelidadDecorator extends DescuentoDecorator {
    public DescuentoFidelidadDecorator(CalculadorPrecio calculadorEnvuelto) {
        super(calculadorEnvuelto);
    }

    @Override
    public BigDecimal calcularPrecioFinal() {
        BigDecimal precioAnterior = calculadorEnvuelto.calcularPrecioFinal();
        BigDecimal descuento = precioAnterior.multiply(new BigDecimal("0.10"));
        return precioAnterior.subtract(descuento);
    }
}

// 2. Descuento por temporada (15%)
public class DescuentoTemporadaDecorator extends DescuentoDecorator {
    public DescuentoTemporadaDecorator(CalculadorPrecio calculadorEnvuelto) {
        super(calculadorEnvuelto);
    }

    @Override
    public BigDecimal calcularPrecioFinal() {
        BigDecimal precioAnterior = calculadorEnvuelto.calcularPrecioFinal();
        BigDecimal descuento = precioAnterior.multiply(new BigDecimal("0.15"));
        return precioAnterior.subtract(descuento);
    }
}

// 3. Descuento por volumen (5%)
public class DescuentoVolumenDecorator extends DescuentoDecorator {
    public DescuentoVolumenDecorator(CalculadorPrecio calculadorEnvuelto) {
        super(calculadorEnvuelto);
    }

    @Override
    public BigDecimal calcularPrecioFinal() {
        BigDecimal precioAnterior = calculadorEnvuelto.calcularPrecioFinal();
        BigDecimal descuento = precioAnterior.multiply(new BigDecimal("0.05"));
        return precioAnterior.subtract(descuento);
    }
}
D. Orquestador Dinámico e Invocación (Ejemplo de Uso)
Java
package com.openlib.market.pricing.service;

import com.openlib.market.pricing.CalculadorPrecio;
import com.openlib.market.pricing.CarritoPrecioBase;
import com.openlib.market.pricing.decorators.impl.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class OrquestadorPrecios {

    // Simulación de evaluación de reglas de negocio en base al contexto
    public CalculadorPrecio construirFlujoPrecios(BigDecimal totalCarrito, int comprasUsuario, boolean esSemanaLibro) {
        // 1. Instanciamos el objeto base
        CalculadorPrecio calculador = new CarritoPrecioBase(totalCarrito);

        // 2. Evaluamos dinámicamente las condiciones y "envolvemos" el objeto aplicando composición
        if (comprasUsuario > 5) {
            calculador = new DescuentoFidelidadDecorator(calculador);
        }

        if (esSemanaLibro) {
            calculador = new DescuentoTemporadaDecorator(calculador);
        }

        if (totalCarrito.compareTo(new BigDecimal("200000")) > 0) {
            calculador = new DescuentoVolumenDecorator(calculador);
        }

        return calculador;
    }

    public static void main(String[] args) {
        OrquestadorPrecios orquestador = new OrquestadorPrecios();

        // CASO DE PRUEBA: Carrito de $250.000 COP, cliente fiel con 7 compras, en Semana del Libro (Aplica TODO)
        BigDecimal valorCarrito = new BigDecimal("250000");
        int comprasDelCliente = 7;
        boolean esFechaEspecial = true;

        CalculadorPrecio flujoDescuentos = orquestador.construirFlujoPrecios(valorCarrito, comprasDelCliente, esFechaEspecial);
        BigDecimal precioFinal = flujoDescuentos.calcularPrecioFinal();

        System.out.println("--- OPENLIB MARKET SYSTEM ---");
        System.out.println("Precio Bruto Base: $" + valorCarrito);
        System.out.println("Precio Final Neto Aplicando Todos los Descuentos Acumulados: $" + precioFinal);
    }
}
4. Extensibilidad Futura (Cumplimiento de SOLID)
Si el día de mañana el negocio aprueba un nuevo descuento, por ejemplo, "Descuento por Primera Compra (20%)", el impacto en el código existente es cero.

Para agregarlo, el equipo de desarrollo solo debe realizar dos pasos:

Crear una nueva clase que extienda de DescuentoDecorator (ej: DescuentoPrimeraCompraDecorator) e implementar su regla del 20% en el método calcularPrecioFinal().

Agregar la cláusula condicional de activación en el orquestador o motor de reglas (if (esPrimeraCompra) ...).

Las clases base (CarritoPrecioBase), la interfaz (CalculadorPrecio) y los decoradores de fidelidad, temporada y volumen permanecen intactos y cerrados a la modificación, cumpliendo de forma estricta con el Principio de Abierto/Cerrado (OCP).
---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

[Evalúa tu propio prompt, no la respuesta del LLM. ¿El contexto fue suficiente? ¿Las restricciones fueron claras? ¿El formato de salida que pediste ayudó a obtener una buena respuesta? ¿Qué parte de tu prompt fue más efectiva? Sé específico: menciona fragmentos concretos de tu prompt que funcionaron bien.]


#### 2. ¿Qué se puede mejorar?

[¿Qué le faltó a tu prompt? ¿Qué harías diferente si pudieras reformularlo? ¿El LLM entendió mal algo por falta de claridad en tu prompt? ¿La respuesta tiene errores u omisiones? ¿Qué no cubrió el LLM que tú sí sabes por lo visto en clase?]


#### 3. Respuesta final

[Escribe tu respuesta definitiva a la pregunta del parcial, integrando lo que aprendiste del LLM pero yendo más allá. Corrige errores, llena omisiones, conecta con conceptos vistos en clase. Esta es tu respuesta: demuestra que tú dominas el tema.]
