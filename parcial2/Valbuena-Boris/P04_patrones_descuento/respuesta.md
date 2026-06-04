# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [4]: [patrones_descuento]

### Estudiante
- **Nombre completo**: Boris Nicolas Valbuena Gueirsman

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Gemini |
| **Modelo específico** | Gemini 3.5 Flash |
| **¿Por qué elegiste este LLM?** | Es la que más usé durante el curso, por lo cual es la misma que usaré en el parcial. |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```
Actúa como un arquitecto de software senior especializado en Java 21, patrones de diseño GoF, principios SOLID y diseño orientado a objetos.
Contexto:
Estoy trabajando en OpenLib Market, una plataforma de compra y venta de libros. El sistema debe aplicar descuentos dinámicos sobre el precio de compra.
Requisitos del negocio:

Descuento por fidelidad: 10% para compradores con más de 5 compras.
Descuento por temporada: 15% durante la semana del libro.
Descuento por volumen: 5% para compras superiores a $200.000 COP.
Sin descuento: precio normal.
Importante:
Las estrategias de descuento pueden combinarse entre sí. Por ejemplo, un cliente puede recibir simultáneamente descuento por fidelidad y descuento por temporada.
El precio final debe calcularse dinámicamente según:

Perfil del usuario.
Fecha de compra.
Contenido y valor del carrito.
Necesito que identifiques cuál es el patrón de diseño más adecuado para este escenario.
Antes de responder:

Analiza los requisitos paso a paso.
Identifica el patrón de diseño más apropiado.
Justifica técnicamente tu elección.
Explica cómo se soporta la combinación de múltiples descuentos.
Descarta explícitamente al menos dos patrones alternativos y explica por qué son menos adecuados.
Evalúa la solución desde la perspectiva de SOLID.
Restricciones:

Utiliza Java 21.
Sigue buenas prácticas de Clean Code.
Diseña una solución extensible para futuros descuentos.
Evita condicionales complejos.
Permite agregar nuevos descuentos sin modificar código existente.
Formato de salida:

Análisis del problema
Patrón de diseño recomendado
Justificación
Patrones alternativos descartados
Diseño de clases
Código Java completo
Ejemplo de uso
Beneficios de la solución
Cumplimiento de SOLID
Validación:
La solución debe permitir combinar múltiples descuentos simultáneamente, ser extensible para futuros descuentos y minimizar el acoplamiento entre componentes.
```

---

### Respuesta del LLM

¡Hola! Excelente desafío arquitectónico. Como Arquitecto de Software Senior, he analizado en detalle los requisitos de **OpenLib Market**. La necesidad de aplicar reglas dinámicas, cumulativas y extensibles sin caer en un infierno de condicionales anidados (`if-else`) requiere una estructura limpia y desacoplada.

A continuación, presento el análisis detallado y la propuesta de arquitectura utilizando **Java 21**.

---

## Análisis del problema

El núcleo del problema radica en tres factores críticos:

1. **Volatilidad de las Reglas:** Los descuentos cambian por factores externos (fechas, volumen, datos del usuario).
2. **Combinabilidad (Cumulativos):** Las reglas no son mutuamente excluyentes; deben poder aplicarse en cadena sobre el precio.
3. **Extensibilidad:** El negocio querrá añadir nuevos descuentos en el futuro (ej. "Descuento de Cumpleaños", "Cyberlunes") sin romper lo que ya funciona.

Si usáramos un enfoque tradicional con `if-else` secuenciales, violaríamos el principio de Responsabilidad Única y el código se volvería inmantenible a medida que crezcan las reglas comerciales.

---

## Patrón de diseño recomendado

El patrón de diseño GoF más adecuado para resolver este escenario es el **Patrón Decorator (Decorador)**, idealmente orquestado o combinado con una variante moderna del patrón **Strategy (Estrategia)** basada en interfaces funcionales o *Pipelines* de ejecución.

Para mantener la solución limpia, idiomática en Java 21 y altamente extensible, utilizaremos una arquitectura basada en **Cadenas de Funciones / Pipeline de Estrategias Decoradoras**.

---

## Justificación

El patrón **Decorator** permite añadir responsabilidades a un objeto de forma dinámica. En lugar de que una sola clase intente calcular todas las combinaciones posibles, cada tipo de descuento se convierte en un "decorador" que envuelve el cálculo del precio base.

* **Soporte para combinaciones:** Un descuento decora a otro. Por ejemplo, el `FidelityDiscount` envuelve al `SeasonalDiscount`, que a su vez envuelve al cálculo del `BasePrice`. Cada capa aplica su descuento sobre el resultado de la capa anterior o sobre el precio base original, según la regla de negocio.
* **Flexibilidad en tiempo de ejecución:** Podemos evaluar las condiciones en tiempo de ejecución y construir la cadena de descuentos activas dinámicamente según el contexto (usuario, fecha, carrito).

---

## Patrones alternativos descartados

### 1. Strategy (Estrategia) Puro

* **Por qué se evaluó:** Permite aislar cada algoritmo de descuento en su propia clase.
* **Por qué se descartó:** El patrón Strategy puro está diseñado para que elijas **una sola** estrategia de entre varias opciones excluyentes. No maneja de forma nativa la composición o acumulación de múltiples estrategias sobre un mismo objeto de forma secuencial sin introducir una lógica de orquestación compleja que termine emulando, precisamente, al Decorator o a una Chain of Responsibility.

### 2. Chain of Responsibility (Cadena de Responsabilidad)

* **Por qué se evaluó:** Permite pasar una petición a lo largo de una cadena de manejadores.
* **Por qué se descartó:** Típicamente, en Chain of Responsibility, un eslabón de la cadena procesa la solicitud y **detiene** la propagación (como un filtro de seguridad). Aunque se puede modificar para que todos procesen la solicitud, el enfoque semántico de transformar un valor numérico de forma acumulativa y matemática se modela de manera más natural y limpia mediante la envoltura de comportamientos (Decorator) o la reducción de funciones.

---

## Diseño de clases

Aprovechando **Java 21**, utilizaremos interfaces funcionales, `records` para la inmutabilidad de los datos, y un motor de evaluación que construirá la tubería (*pipeline*) de descuentos dinámicamente.

* **`CartContext` (Record):** Contiene toda la información necesaria para evaluar los descuentos (Usuario, Carrito, Fecha).
* **`DiscountOperation` (Interface Funcional):** Representa la abstracción del cálculo del precio ($BigDecimal \to BigDecimal$).
* **`DiscountPolicy` (Interface):** Define el contrato para cada regla de descuento. Evalúa si aplica y devuelve el decorador/transformador.

---

## Código Java completo

```java
package com.openlib.market.discount;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

// ==========================================
// 1. CONTEXTO DE DOMINIO (Inmutable)
// ==========================================

record UserProfile(String id, int completedPurchases, LocalDate birthDate) {}

record Book(String id, String title, BigDecimal price) {}

record Cart(List<Book> items) {
    public BigDecimal getTotal() {
        return items.stream()
                .map(Book::price)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

record CartContext(UserProfile user, Cart cart, LocalDate purchaseDate) {}

// ==========================================
// 2. ABSTRACCIÓN DEL DECORADOR / COMPONENT
// ==========================================

@FunctionalInterface
interface DiscountOperation {
    BigDecimal apply(BigDecimal currentPrice);
}

// ==========================================
// 3. ESTRATEGIAS DE POLÍTICAS DE DESCUENTO
// ==========================================

interface DiscountPolicy {
    boolean isApplicable(CartContext context);
    DiscountOperation createDecorator(CartContext context);
}

// Descuento 1: Fidelidad (10% si compras > 5)
class FidelityDiscountPolicy implements DiscountPolicy {
    @Override
    public boolean isApplicable(CartContext context) {
        return context.user().completedPurchases() > 5;
    }

    @Override
    public DiscountOperation createDecorator(CartContext context) {
        return currentPrice -> currentPrice.multiply(BigDecimal.valueOf(0.90)); // -10%
    }
}

// Descuento 2: Temporada (15% Semana del Libro - Simulación de fechas)
class SeasonalDiscountPolicy implements DiscountPolicy {
    private final LocalDate startOfWeek;
    private final LocalDate endOfWeek;

    public SeasonalDiscountPolicy(LocalDate startOfWeek, LocalDate endOfWeek) {
        this.startOfWeek = startOfWeek;
        this.endOfWeek = endOfWeek;
    }

    @Override
    public boolean isApplicable(CartContext context) {
        LocalDate date = context.purchaseDate();
        return (!date.isBefore(startOfWeek)) && (!date.isAfter(endOfWeek));
    }

    @Override
    public DiscountOperation createDecorator(CartContext context) {
        return currentPrice -> currentPrice.multiply(BigDecimal.valueOf(0.85)); // -15%
    }
}

// Descuento 3: Volumen (5% para compras superiores a $200.000 COP)
class VolumeDiscountPolicy implements DiscountPolicy {
    private static final BigDecimal THRESHOLD = new BigDecimal("200000");

    @Override
    public boolean isApplicable(CartContext context) {
        return context.cart().getTotal().compareTo(THRESHOLD) > 0;
    }

    @Override
    public DiscountOperation createDecorator(CartContext context) {
        return currentPrice -> currentPrice.multiply(BigDecimal.valueOf(0.95)); // -5%
    }
}

// ==========================================
// 4. MOTOR DE EVALUACIÓN (Orquestador)
// ==========================================

class DiscountEngine {
    private final List<DiscountPolicy> policies;

    public DiscountEngine(List<DiscountPolicy> policies) {
        this.policies = List.copyOf(policies);
    }

    public BigDecimal calculateFinalPrice(CartContext context) {
        BigDecimal basePrice = context.cart().getTotal();

        // Evaluamos qué políticas aplican y acumulamos sus transformaciones (Decoradores)
        return policies.stream()
                .filter(policy -> policy.isApplicable(context))
                .map(policy -> policy.createDecorator(context))
                // Reducimos las funciones aplicando una tras otra de forma secuencial (Pipeline / Decorator)
                .reduce(DiscountOperation::andThen)
                .orElse(price -> price) // Si no hay descuentos, retorna el precio original
                .apply(basePrice);
    }
}

```

---

## Ejemplo de uso

A continuación, un escenario de prueba donde el cliente cumple **todas** las condiciones para acumular los tres descuentos.

```java
package com.openlib.market;

import com.openlib.market.discount.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class OpenLibMarketApplication {
    public static void main(String[] args) {
        // 1. Configurar las políticas del sistema (Inyección de dependencias amigable)
        LocalDate bookWeekStart = LocalDate.of(2026, 4, 15);
        LocalDate bookWeekEnd = LocalDate.of(2026, 4, 22);

        List<DiscountPolicy> registeredPolicies = List.of(
                new FidelityDiscountPolicy(),
                new SeasonalDiscountPolicy(bookWeekStart, bookWeekEnd),
                new VolumeDiscountPolicy()
        );

        DiscountEngine discountEngine = new DiscountEngine(registeredPolicies);

        // 2. Crear escenario: Usuario con 6 compras previas (Aplica Fidelidad)
        UserProfile customer = new UserProfile("usr-99", 6, LocalDate.of(2004, 8, 12));

        // Carrito con valor superior a $200.000 COP (Aplica Volumen)
        Cart cart = new Cart(List.of(
                new Book("b-01", "Clean Code", new BigDecimal("120000")),
                new Book("b-02", "Effective Java", new BigDecimal("130000"))
        ));

        // Fecha de compra dentro de la semana del libro (Aplica Temporada)
        CartContext context = new CartContext(customer, cart, LocalDate.of(2026, 4, 18));

        // 3. Calcular precio
        BigDecimal originalPrice = context.cart().getTotal();
        BigDecimal finalPrice = discountEngine.calculateFinalPrice(context);

        // 4. Mostrar resultados
        System.out.println("--- OPENLIB MARKET ---");
        System.out.println("Precio Base del Carrito: $" + originalPrice + " COP");
        System.out.println("Precio Final con Descuentos Aplicados: $" + finalPrice + " COP");
        // Operación matemática: 250.000 * 0.90 (Fidelidad) * 0.85 (Temporada) * 0.95 (Volumen)
    }
}

```

---

## Beneficios de la solución

* **Cero condicionales complejos:** Se eliminaron los bloques `if-else` mutables. El motor delega la responsabilidad de "saber si aplica" a cada clase de política independiente.
* **Inmutabilidad:** El uso de `records` asegura que el contexto del carrito no sufra efectos secundarios accidentales durante el procesamiento.
* **Composición Funcional Elegante:** Reemplazamos la necesidad de crear subclases manuales del patrón Decorador clásico mediante el uso de referencias y composición de funciones con `.reduce(DiscountOperation::andThen)`.

---

## Cumplimiento de SOLID

* **S (Single Responsibility Principle):** Cada clase `DiscountPolicy` tiene una única razón para cambiar: la regla específica de su negocio (ej. cambiar el porcentaje o las condiciones de volumen). El `DiscountEngine` solo se encarga de coordinar la ejecución.
* **O (Open/Closed Principle):** Para añadir un nuevo descuento (por ejemplo, un 10% por cumpleaños del usuario), no se modifica el `DiscountEngine` ni las políticas existentes. Simplemente se crea una clase nueva que implemente `DiscountPolicy` y se registra en la lista inicial. El sistema queda **abierto a la extensión, cerrado a la modificación**.
* **L (Liskov Substitution Principle):** Cualquier implementación de `DiscountPolicy` puede ser utilizada e intercambiada por el motor de descuentos sin alterar la corrección del programa.
* **I (Interface Segregation Principle):** Las interfaces creadas (`DiscountPolicy`, `DiscountOperation`) son sumamente cohesivas y minimalistas. No obligan a implementar métodos innecesarios.
* **D (Dependency Inversion Principle):** El `DiscountEngine` no depende de implementaciones concretas de descuentos; depende exclusivamente de la abstracción `DiscountPolicy`. Las estrategias se inyectan en su constructor.

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

[Evalúa tu propio prompt, no la respuesta del LLM. ¿El contexto fue suficiente? ¿Las restricciones fueron claras? ¿El formato de salida que pediste ayudó a obtener una buena respuesta? ¿Qué parte de tu prompt fue más efectiva? Sé específico: menciona fragmentos concretos de tu prompt que funcionaron bien.]


#### 2. ¿Qué se puede mejorar?

[¿Qué le faltó a tu prompt? ¿Qué harías diferente si pudieras reformularlo? ¿El LLM entendió mal algo por falta de claridad en tu prompt? ¿La respuesta tiene errores u omisiones? ¿Qué no cubrió el LLM que tú sí sabes por lo visto en clase?]


#### 3. Respuesta final

[Escribe tu respuesta definitiva a la pregunta del parcial, integrando lo que aprendiste del LLM pero yendo más allá. Corrige errores, llena omisiones, conecta con conceptos vistos en clase. Esta es tu respuesta: demuestra que tú dominas el tema.]
