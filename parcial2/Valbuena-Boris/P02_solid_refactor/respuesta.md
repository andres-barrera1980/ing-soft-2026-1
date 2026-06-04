# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [2]: [solid_refactor]

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
Actúa como un arquitecto de software senior especializado en Java 21, Spring Boot 3.x, patrones de diseño y principios SOLID.

Contexto:

Estoy trabajando en OpenLib Market, una plataforma de compra y venta de libros. El sistema debe soportar múltiples métodos de pago como tarjeta de crédito, PSE, PayPal y próximamente criptomonedas.

El equipo junior implementó la siguiente solución:

```java id="vjlwmc"
public class ProcesadorPago {
    private String tipoPago; // "TARJETA", "PSE", "PAYPAL"

    public ProcesadorPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }

    public ResultadoPago procesar(Pago pago) {
        if (tipoPago.equals("TARJETA")) {
            return new ResultadoPago(true, "Pago con tarjeta procesado");
        } else if (tipoPago.equals("PSE")) {
            return new ResultadoPago(true, "Pago PSE procesado");
        } else if (tipoPago.equals("PAYPAL")) {
            return new ResultadoPago(true, "Pago PayPal procesado");
        }
        throw new IllegalArgumentException("Método de pago no soportado");
    }
}
```

Necesito que analices este diseño desde la perspectiva de SOLID y patrones de diseño.

Antes de responder:

1. Analiza paso a paso las responsabilidades de la clase.
2. Identifica al menos dos principios SOLID que se estén violando.
3. Explica por qué esos principios están siendo afectados.
4. Evalúa si el diseño soporta correctamente la incorporación de nuevos métodos de pago.
5. Determina qué patrón de diseño sería el más adecuado para resolver el problema.
6. Justifica técnicamente tu elección.

Restricciones:

* Utiliza Java 21.
* Sigue buenas prácticas de Clean Code.
* Aplica principios SOLID.
* Diseña una solución extensible para futuros métodos de pago.
* Considera inyección de dependencias.
* Evita condicionales extensos basados en Strings.

Formato de salida:

# Diagnóstico

# Principios SOLID violados

# Patrón de diseño recomendado

# Justificación de la solución

# Diagrama conceptual de clases

# Código refactorizado

# Beneficios del nuevo diseño

# Cómo agregar un nuevo método de pago (Criptomonedas)

Validación:

La solución debe permitir agregar nuevos métodos de pago sin modificar el código existente del procesador principal, reducir el acoplamiento y aumentar la extensibilidad y mantenibilidad del sistema.

```

---

### Respuesta del LLM
¡Hola! Tomo el rol. Como Arquitecto de Software Senior, entiendo perfectamente el dolor de cabeza que representan este tipo de implementaciones "anti-patrón" cuando el negocio empieza a escalar.

El código que presentó el equipo junior es un clásico ejemplo de deuda técnica temprana. En plataformas de e-commerce como *OpenLib Market*, los métodos de pago cambian, evolucionan y se añaden constantemente (como la integración de criptomonedas que mencionas). Si dejamos el diseño actual, el sistema colapsará bajo su propio peso de mantenimiento.

A continuación, presento el análisis arquitectónico, el diagnóstico y la solución refactorizada utilizando las capacidades modernas de **Java 21**.

---

## 1. Diagnóstico

La clase `ProcesadorPago` actualmente asume **múltiples responsabilidades**: conocer la lógica de negocio de cada pasarela, validar el tipo de pago y controlar el flujo de ejecución.

El diseño **no soporta la incorporación de nuevos métodos de pago** de forma segura. Cada vez que queramos agregar uno (como criptomonedas), estamos obligados a entrar a esta clase, modificar un método existente y añadir más bloques `if-else`. Esto introduce un alto riesgo de romper los métodos de pago que ya funcionan de manera estable en producción.

---

## 2. Principios SOLID Violados

* **Single Responsibility Principle (SRP - Principio de Responsabilidad Única):** La clase tiene más de una razón para cambiar. Debería cambiar solo si se modifica el flujo general de procesamiento, pero actualmente cambiaría si se modifica la lógica de tarjetas, de PSE o de PayPal.
* **Open/Closed Principle (OCP - Principio de Abierto/Cerrado):** El código no está cerrado a la modificación ni abierto a la extensión. Para extender el comportamiento (añadir Cripto), es mandatorio *modificar* el código fuente del método `procesar`.

---

## 3. Patrón de Diseño Recomendado

El patrón de diseño ideal aquí es una combinación de **Strategy (Estrategia)** respaldado por el uso de **Dependency Injection (DI)** con Spring Boot.

### Justificación Técnica

El patrón *Strategy* permite definir una familia de algoritmos (métodos de pago), encapsular cada uno de ellos en una clase independiente y hacer que sus objetos sean intercambiables. Al combinarlo con Spring Boot, podemos inyectar dinámicamente todas las estrategias disponibles en un `Map<String, Strategy>`, eliminando por completo los condicionales `if-else` o `switch` basados en cadenas de texto.

---

## 4. Diagrama Conceptual de Clases

El flujo desacoplado sigue la siguiente estructura:

```
                  ┌──────────────────────┐
                  │   PaymentProcessor   │
                  └──────────┬───────────┘
                             │ (Usa un Map de estrategias)
                             ▼
                ┌──────────────────────────┐
                │     PaymentStrategy      │ <<interface>>
                └────────────┬─────────────┘
                             │
       ┌─────────────────────┼─────────────────────┐
       ▼                     ▼                     ▼
┌──────────────┐      ┌──────────────┐      ┌──────────────┐
│CreditCardStr │      │    PseStr    │      │  PayPalStr   │
└──────────────┘      └──────────────┘      └──────────────┘

```

---

## 5. Código Refactorizado (Java 21 & Spring Boot 3.x)

Usaremos **Records** de Java 21 para la inmutabilidad de los datos y patrones limpios de inyección de dependencias.

### DTOs Básicos (Java Records)

```java
package com.openlib.market.payment.dto;

import java.math.BigDecimal;

public record Pago(BigDecimal monto, String moneda, String detalles) {}

public record ResultadoPago(boolean exitoso, String mensaje) {}

```

### La Interfaz Estrategia

```java
package com.openlib.market.payment.strategy;

import com.openlib.market.payment.dto.Pago;
import com.openlib.market.payment.dto.ResultadoPago;

public interface PaymentStrategy {
    ResultadoPago procesar(Pago pago);
    String getTipoPago(); // Identificador único de la estrategia
}

```

### Implementaciones de las Estrategias

```java
package com.openlib.market.payment.strategy.impl;

import com.openlib.market.payment.dto.Pago;
import com.openlib.market.payment.dto.ResultadoPago;
import com.openlib.market.payment.strategy.PaymentStrategy;
import org.springframework.stereotype.Component;

@Component
public class TarjetaPaymentStrategy implements PaymentStrategy {
    @Override
    public ResultadoPago procesar(Pago pago) {
        // Lógica de integración con la pasarela de Tarjetas (Stripe, etc.)
        return new ResultadoPago(true, "Pago con tarjeta por " + pago.monto() + " procesado exitosamente.");
    }

    @Override
    public String getTipoPago() {
        return "TARJETA";
    }
}

@Component
public class PsePaymentStrategy implements PaymentStrategy {
    @Override
    public ResultadoPago procesar(Pago pago) {
        return new ResultadoPago(true, "Pago PSE procesado exitosamente.");
    }

    @Override
    public String getTipoPago() {
        return "PSE";
    }
}

```

### El Procesador Principal (El Contexto)

Spring Boot inyecta automáticamente todas las clases que implementen `PaymentStrategy` en una lista. Usamos el constructor para transformar esa lista en un mapa indexado de forma eficiente y limpia.

```java
package com.openlib.market.payment;

import com.openlib.market.payment.dto.Pago;
import com.openlib.market.payment.dto.ResultadoPago;
import com.openlib.market.payment.strategy.PaymentStrategy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ProcesadorPago {

    private final Map<String, PaymentStrategy> estrategias;

    // Inyección de dependencias limpia por constructor (Java 21)
    public ProcesadorPago(List<PaymentStrategy> estrategiaList) {
        this.estrategias = estrategiaList.stream()
                .collect(Collectors.toUnmodifiableMap(
                        PaymentStrategy::getTipoPago,
                        Function.identity()
                ));
    }

    public ResultadoPago procesar(String tipoPago, Pago pago) {
        return Optional.ofNullable(estrategias.get(tipoPago.toUpperCase()))
                .map(estrategia -> estrategia.procesar(pago))
                .orElseThrow(() -> new IllegalArgumentException("Método de pago no soportado: " + tipoPago));
    }
}

```

---

## 6. Beneficios del Nuevo Diseño

* **Acoplamiento Débil (Loose Coupling):** El procesador principal no sabe *cómo* se procesa una tarjeta o un PSE, solo sabe que la estrategia cumple con el contrato.
* **Alta Extensibilidad:** Cumple al 100% con el principio OCP. El core del sistema queda sellado contra modificaciones no deseadas.
* **Mantenibilidad y Testeabilidad:** Cada método de pago se puede probar de forma aislada mediante pruebas unitarias (`JUnit 5`), abstrayendo las dependencias externas fácilmente.

---

## 7. Cómo agregar un nuevo método de pago (Criptomonedas)

Para cumplir con el requerimiento del negocio de añadir criptomonedas, el equipo junior ya **no tendrá que tocar** la clase `ProcesadorPago`. Bastará con crear una nueva clase que implemente la interfaz:

```java
package com.openlib.market.payment.strategy.impl;

import com.openlib.market.payment.dto.Pago;
import com.openlib.market.payment.dto.ResultadoPago;
import com.openlib.market.payment.strategy.PaymentStrategy;
import org.springframework.stereotype.Component;

@Component
public class CriptoPaymentStrategy implements PaymentStrategy {
    
    @Override
    public ResultadoPago procesar(Pago pago) {
        // Lógica específica para procesar Bitcoin, Ethereum, etc.
        return new ResultadoPago(true, "Pago con Criptomonedas procesado en la Blockchain.");
    }

    @Override
    public String getTipoPago() {
        return "CRIPTO";
    }
}

```

Al levantar la aplicación, Spring Boot detectará el componente `@Component`, lo añadirá a la lista de estrategias y el mapa del `ProcesadorPago` lo registrará automáticamente en tiempo de ejecución. ¡Listo para producción sin modificar código existente!
---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

El prompt da suficiente contexto sobre el problema y especifica claramente qué debe analizarse. También pide identificar varios principios SOLID, justificar el patrón elegido y verificar si el diseño permite agregar nuevos métodos de pago.

Además, las restricciones ayudan a orientar la solución hacia un diseño extensible usando buenas prácticas y evitando condicionales basados en Strings.

#### 2. ¿Qué se puede mejorar?

La respuesta identifica correctamente las violaciones de **SRP** y **OCP**, y la elección del patrón **Strategy** es adecuada para este caso.

Sin embargo, también podría haberse mencionado **DIP**, ya que el procesador debería depender de abstracciones (`PaymentStrategy`) y no de implementaciones concretas. Además, aunque el diseño es bastante bueno, seguir usando Strings como `"TARJETA"` o `"PSE"` puede generar errores y podría reemplazarse por un `enum`.

#### 3. Respuesta final

El LLM identificó correctamente que se están violando principalmente los principios **SRP** y **OCP**.

Por un lado, `ProcesadorPago` conoce la lógica de todos los métodos de pago, por lo que cualquier cambio en tarjeta, PSE o PayPal obliga a modificar la misma clase. Por otro lado, para agregar un nuevo método de pago es necesario agregar otro `if`, incumpliendo el principio de abierto/cerrado.

El patrón **Strategy** es una buena elección porque permite encapsular cada forma de pago en una clase independiente que implemente una misma interfaz, por ejemplo `PaymentStrategy`.

La solución introduce una interfaz común y clases como:

* `TarjetaPaymentStrategy`
* `PsePaymentStrategy`
* `PaypalPaymentStrategy`

De esta forma, el procesador solo delega la operación a la estrategia correspondiente y ya no necesita conocer los detalles de cada método de pago.

Si mañana se requiere agregar criptomonedas, bastaría con crear una nueva implementación como `CriptoPaymentStrategy` sin modificar el procesador principal. Esto cumple con OCP y hace el sistema más mantenible.

Como mejora adicional, reemplazaría los Strings por un `enum TipoPago` para evitar errores de escritura y aumentar la seguridad del código. En general, el diseño propuesto reduce el acoplamiento, mejora la extensibilidad y facilita las pruebas unitarias.
