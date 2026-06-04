# Plantilla de entrega — Parcial 2
---

## Pregunta [02]: P02_solid_refactor

### Estudiante
- **Nombre completo**: Samuel López Gómez

---

### LLM utilizado

| Campo | Valor | 
|---|---|
| **Nombre del LLM** | Claude |
| **Modelo específico** | sonnet 4.6 |
| **¿Por qué elegiste este LLM?** |  |

---

### Prompt utilizado

Actúa como un arquitecto de software senior revisando el módulo de pagos del proyecto OpenLib Market (entorno Java 21). Adjunto el código inicial de la clase `ProcesadorPago` escrita por un desarrollador junior.

Necesito que:
1. Identifiques y expliques detalladamente al menos dos principios SOLID que se están violando en esta implementación.
2. Propongas un refactoring completo utilizando el patrón de diseño más apropiado para este caso (explica por qué eliges ese patrón).
3. Escribas el código Java resultante, mostrando las interfaces/clases abstractas necesarias y cómo quedaría la clase `ProcesadorPago` final.

Restricciones:
- El diseño propuesto debe permitir que el día de mañana agreguemos un nuevo método de pago (ej. Criptomonedas) sin modificar el código de la clase `ProcesadorPago`.
- El código debe usar características modernas de Java 21 donde aplique.
- No uses librerías externas para la lógica central, solo POO pura.

Formato de salida esperado: Markdown con las secciones: "Principios Violados", "Patrón Seleccionado y Justificación", y "Código Refactorizado".

Código a analizar:
public class ProcesadorPago {
    private String tipoPago; // "TARJETA", "PSE", "PAYPAL"
    
    public ProcesadorPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }
    
    public ResultadoPago procesar(Pago pago) {
        if (tipoPago.equals("TARJETA")) {
            // Validar CVV, fecha expiración, fondos
            // Conectar con API de franquicia (Visa/Mastercard)
            // Procesar cargo
            return new ResultadoPago(true, "Pago con tarjeta procesado");
        } else if (tipoPago.equals("PSE")) {
            // Redirigir a portal bancario
            // Validar retorno de PSE
            // Confirmar débito
            return new ResultadoPago(true, "Pago PSE procesado");
        } else if (tipoPago.equals("PAYPAL")) {
            // Autenticar con OAuth PayPal
            // Ejecutar cargo
            // Registrar webhook de confirmación
            return new ResultadoPago(true, "Pago PayPal procesado");
        }
        throw new IllegalArgumentException("Método de pago no soportado");
    }
}

```

---

### Respuesta del LLM

# Análisis SOLID — `ProcesadorPago`
> OpenLib Market · Módulo de Pagos · Revisión arquitectónica · Java 21

---

## Principios Violados

### 1. Single Responsibility Principle (SRP)

La clase `ProcesadorPago` acumula **tres responsabilidades de dominio completamente distintas** dentro de un único método:

| Rama | Responsabilidad | Actor que la cambia |
|---|---|---|
| `"TARJETA"` | Validar CVV/expiración + conectar API Visa/Mastercard | Equipo de integraciones bancarias |
| `"PSE"` | Redirigir al portal + confirmar débito bancario | Equipo de pasarelas colombianas |
| `"PAYPAL"` | OAuth + webhook de confirmación | Equipo de integraciones internacionales |

Cada una de estas ramas tiene su propio ciclo de vida, sus propias credenciales, sus propias reglas de reintento y sus propios errores posibles. Encerrarlas en la misma clase garantiza que **un cambio en PayPal obligue a recompilar y re-desplegar el mismo artefacto que maneja PSE**, aumentando el riesgo innecesariamente.

**Síntoma concreto:** si mañana Visa cambia el campo `cvv2` a `securityCode` en su API, el desarrollador tiene que abrir `ProcesadorPago`, navegar por el `if-else` y rezar para no romper PSE de paso.

---

### 2. Open/Closed Principle (OCP)

El principio establece que una clase debe estar **abierta para extensión pero cerrada para modificación**.

En la implementación actual, agregar Criptomonedas como método de pago implica **obligatoriamente abrir `ProcesadorPago`** y añadir un nuevo `else if`:

```java
// Para agregar cripto HAY que modificar esta clase:
} else if (tipoPago.equals("CRIPTO")) {
    // ...
}
```

Esto viola OCP porque la clase no está diseñada para ser extendida; está diseñada para ser modificada. Con cada nuevo método de pago crece el riesgo de romper los ya existentes, y la clase nunca queda "cerrada".

---

### 3. Dependency Inversion Principle (DIP) — violación implícita

`ProcesadorPago` decide **en tiempo de ejecución** qué lógica concreta ejecutar, dependiendo de un `String` frágil. No existe ninguna abstracción entre el orquestador y las implementaciones de pago. Cualquier consumidor que use `ProcesadorPago` está acoplado indirectamente a los tres mecanismos de pago a la vez, aunque solo necesite uno.

---

## Patrón Seleccionado y Justificación

### Patrón: **Strategy** (con Factory Method de apoyo)

#### ¿Por qué Strategy?

El patrón Strategy define una **familia de algoritmos intercambiables** encapsulados detrás de una interfaz común. Es el candidato natural porque:

| Criterio | Situación actual | Con Strategy |
|---|---|---|
| Agregar nuevo método de pago | Modificar `ProcesadorPago` | Crear nueva clase, cero cambios en `ProcesadorPago` |
| Testear PSE de forma aislada | Imposible sin instanciar todo | Mock de `EstrategiaPago` |
| Desplegar solo el módulo PayPal | Imposible | Sí, cada estrategia es independiente |
| Legibilidad del código | `if-else` de 60+ líneas | Clase de ~20 líneas por estrategia |

#### ¿Por qué no otros patrones?

- **Template Method:** sirve cuando los pasos son los mismos y solo varían partes. Aquí el flujo de tarjeta (validar CVV → API franquicia) es estructuralmente distinto al de PSE (redirección → callback). No hay esqueleto común real.
- **Chain of Responsibility:** apropiado cuando varios manejadores pueden procesar la misma solicitud. Aquí un pago tiene exactamente un método; no hay cadena.
- **State:** modela transiciones de estado de un objeto. El pago no "transiciona" entre métodos; simplemente tiene uno.

#### Factory Method de apoyo

Se añade un `PagoStrategyFactory` para desacoplar la selección de estrategia de su construcción, respetando DIP: `ProcesadorPago` depende de la interfaz `EstrategiaPago`, nunca de las clases concretas.

---

## Código Refactorizado

### Modelo base (sin cambios respecto al proyecto)

```java
package com.openlib.model.pago;

// Representa una solicitud de pago dentro de OpenLib Market
public record Pago(
    String metodoPago,   // "TARJETA", "PSE", "PAYPAL", "CRIPTO"
    double monto,
    String moneda,
    String referencia
) {}
```

```java
package com.openlib.model.pago;

// Resultado inmutable de cualquier operación de pago
public record ResultadoPago(
    boolean exitoso,
    String mensaje,
    String codigoTransaccion
) {
    // Factory methods expresivos
    public static ResultadoPago exitoso(String mensaje, String codigo) {
        return new ResultadoPago(true, mensaje, codigo);
    }

    public static ResultadoPago fallido(String razon) {
        return new ResultadoPago(false, razon, null);
    }
}
```

---

### Interfaz central: `EstrategiaPago`

```java
package com.openlib.service.pago;

import com.openlib.model.pago.Pago;
import com.openlib.model.pago.ResultadoPago;

/**
 * Contrato único que deben cumplir todos los métodos de pago.
 *
 * Agregar Criptomonedas = implementar esta interfaz.
 * ProcesadorPago no se toca.
 */
public interface EstrategiaPago {

    /**
     * Identificador del método de pago que maneja esta estrategia.
     * Usado por el factory para el registro automático.
     */
    String metodoPago();

    /**
     * Ejecuta el proceso de pago completo.
     *
     * @param pago datos de la transacción
     * @return resultado inmutable de la operación
     */
    ResultadoPago procesar(Pago pago);

    /**
     * Valida que el pago tenga los datos mínimos requeridos
     * por este método específico antes de intentar procesar.
     *
     * Default implementado para no forzar a estrategias simples
     * a sobreescribir si no tienen validaciones extra.
     */
    default void validar(Pago pago) {
        if (pago == null) {
            throw new IllegalArgumentException("El pago no puede ser nulo");
        }
        if (pago.monto() <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a cero");
        }
    }
}
```

---

### Estrategia 1: `TarjetaEstrategia`

```java
package com.openlib.service.pago.estrategia;

import com.openlib.model.pago.Pago;
import com.openlib.model.pago.ResultadoPago;
import com.openlib.service.pago.EstrategiaPago;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Responsabilidad única: procesar pagos con tarjeta crédito/débito.
 * Conecta con la API de la franquicia (Visa/Mastercard).
 */
@Component
public class TarjetaEstrategia implements EstrategiaPago {

    @Override
    public String metodoPago() {
        return "TARJETA";
    }

    @Override
    public void validar(Pago pago) {
        EstrategiaPago.super.validar(pago);
        // Validaciones específicas de tarjeta: CVV, fecha expiración, etc.
        // En producción se verificarían campos adicionales del objeto Pago
    }

    @Override
    public ResultadoPago procesar(Pago pago) {
        validar(pago);

        // 1. Validar CVV y fecha de expiración
        // 2. Conectar con API Visa/Mastercard
        // 3. Procesar cargo
        String codigoTransaccion = "TARJETA-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        return ResultadoPago.exitoso(
            "Pago con tarjeta procesado exitosamente por $" + pago.monto(),
            codigoTransaccion
        );
    }
}
```

---

### Estrategia 2: `PseEstrategia`

```java
package com.openlib.service.pago.estrategia;

import com.openlib.model.pago.Pago;
import com.openlib.model.pago.ResultadoPago;
import com.openlib.service.pago.EstrategiaPago;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Responsabilidad única: procesar pagos mediante PSE (Colombia).
 * Maneja la redirección al portal bancario y la confirmación del débito.
 */
@Component
public class PseEstrategia implements EstrategiaPago {

    @Override
    public String metodoPago() {
        return "PSE";
    }

    @Override
    public ResultadoPago procesar(Pago pago) {
        validar(pago);

        // 1. Redirigir a portal bancario PSE
        // 2. Validar retorno con token de confirmación
        // 3. Confirmar débito con el banco
        String codigoTransaccion = "PSE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        return ResultadoPago.exitoso(
            "Pago PSE procesado exitosamente por $" + pago.monto(),
            codigoTransaccion
        );
    }
}
```

---

### Estrategia 3: `PaypalEstrategia`

```java
package com.openlib.service.pago.estrategia;

import com.openlib.model.pago.Pago;
import com.openlib.model.pago.ResultadoPago;
import com.openlib.service.pago.EstrategiaPago;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Responsabilidad única: procesar pagos mediante PayPal.
 * Maneja OAuth, ejecución del cargo y registro del webhook.
 */
@Component
public class PaypalEstrategia implements EstrategiaPago {

    @Override
    public String metodoPago() {
        return "PAYPAL";
    }

    @Override
    public ResultadoPago procesar(Pago pago) {
        validar(pago);

        // 1. Autenticar con OAuth de PayPal
        // 2. Ejecutar cargo sobre la cuenta
        // 3. Registrar webhook de confirmación
        String codigoTransaccion = "PP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        return ResultadoPago.exitoso(
            "Pago PayPal procesado exitosamente por $" + pago.monto(),
            codigoTransaccion
        );
    }
}
```

---

### Estrategia 4: `CriptoEstrategia` — nueva sin tocar `ProcesadorPago`

```java
package com.openlib.service.pago.estrategia;

import com.openlib.model.pago.Pago;
import com.openlib.model.pago.ResultadoPago;
import com.openlib.service.pago.EstrategiaPago;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Nueva estrategia agregada SIN modificar ProcesadorPago.
 * Demuestra que el diseño cumple OCP.
 */
@Component
public class CriptoEstrategia implements EstrategiaPago {

    @Override
    public String metodoPago() {
        return "CRIPTO";
    }

    @Override
    public ResultadoPago procesar(Pago pago) {
        validar(pago);

        // 1. Generar dirección de wallet destino
        // 2. Esperar confirmación on-chain (N bloques)
        // 3. Registrar hash de transacción
        String codigoTransaccion = "CRIPTO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        return ResultadoPago.exitoso(
            "Pago en criptomoneda procesado por $" + pago.monto(),
            codigoTransaccion
        );
    }
}
```

---

### Factory: `PagoStrategyFactory`

```java
package com.openlib.service.pago;

import com.openlib.service.pago.estrategia.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Responsabilidad única: resolver qué EstrategiaPago corresponde
 * a un método de pago dado.
 *
 * Se auto-registra con las estrategias inyectadas por Spring,
 * por lo que agregar una nueva estrategia (@Component) es suficiente:
 * el factory la descubre automáticamente sin ningún cambio aquí.
 */
public class PagoStrategyFactory {

    private final Map<String, EstrategiaPago> estrategias;

    // Spring inyecta la lista de todos los @Component que implementen EstrategiaPago
    public PagoStrategyFactory(List<EstrategiaPago> estrategiasRegistradas) {
        this.estrategias = new HashMap<>();
        estrategiasRegistradas.forEach(e -> estrategias.put(e.metodoPago(), e));
    }

    /**
     * Resuelve la estrategia o lanza excepción descriptiva.
     *
     * Usa switch expression de Java 21 sobre el mapa para
     * un manejo expresivo del caso no encontrado.
     */
    public EstrategiaPago resolver(String metodoPago) {
        return switch (estrategias.get(metodoPago)) {
            case EstrategiaPago e -> e;
            case null -> throw new IllegalArgumentException(
                "Método de pago no soportado: '%s'. Métodos disponibles: %s"
                    .formatted(metodoPago, estrategias.keySet())
            );
        };
    }
}
```

---

### `ProcesadorPago` — clase final refactorizada

```java
package com.openlib.service.pago;

import com.openlib.model.pago.Pago;
import com.openlib.model.pago.ResultadoPago;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Responsabilidad única: orquestar el flujo de procesamiento de un pago.
 *
 * NO sabe nada sobre tarjetas, PSE, PayPal ni cripto.
 * Solo delega a la estrategia correcta y aplica comportamiento transversal
 * (logging, manejo de errores).
 *
 * Agregar Criptomonedas = crear CriptoEstrategia. Esta clase: intacta.
 */
@Service
@RequiredArgsConstructor
public class ProcesadorPago {

    private static final Logger log = LoggerFactory.getLogger(ProcesadorPago.class);

    private final PagoStrategyFactory factory;

    public ResultadoPago procesar(Pago pago) {
        log.info("Iniciando pago: metodo={} monto={} ref={}",
                pago.metodoPago(), pago.monto(), pago.referencia());

        EstrategiaPago estrategia = factory.resolver(pago.metodoPago());

        ResultadoPago resultado = estrategia.procesar(pago);

        if (resultado.exitoso()) {
            log.info("Pago exitoso: ref={} codigo={}",
                    pago.referencia(), resultado.codigoTransaccion());
        } else {
            log.warn("Pago fallido: ref={} razon={}",
                    pago.referencia(), resultado.mensaje());
        }

        return resultado;
    }
}
```

---

### Configuración Spring (`@Configuration`)

```java
package com.openlib.config;

import com.openlib.service.pago.EstrategiaPago;
import com.openlib.service.pago.PagoStrategyFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class PagoConfig {

    /**
     * Spring descubre automáticamente todos los @Component que implementen
     * EstrategiaPago y los inyecta aquí. No hay que registrar nada manualmente.
     */
    @Bean
    public PagoStrategyFactory pagoStrategyFactory(List<EstrategiaPago> estrategias) {
        return new PagoStrategyFactory(estrategias);
    }
}
```

---

### Estructura de paquetes resultante

```
com.openlib/
├── model/
│   └── pago/
│       ├── Pago.java                      ← record inmutable (Java 16+)
│       └── ResultadoPago.java             ← record inmutable con factory methods
├── service/
│   └── pago/
│       ├── EstrategiaPago.java            ← interfaz con default method
│       ├── PagoStrategyFactory.java       ← resuelve estrategia por nombre
│       ├── ProcesadorPago.java            ← orquestador limpio (~25 líneas)
│       └── estrategia/
│           ├── TarjetaEstrategia.java
│           ├── PseEstrategia.java
│           ├── PaypalEstrategia.java
│           └── CriptoEstrategia.java      ← agregada SIN tocar ProcesadorPago ✓
└── config/
    └── PagoConfig.java
```

---

### Test unitario — verificación del OCP en acción

```java
@Test
void procesarPago_deberiaUsarLaEstrategiaCorrecta() {
    // Arrange
    var tarjetaEstrategia = mock(EstrategiaPago.class);
    when(tarjetaEstrategia.metodoPago()).thenReturn("TARJETA");
    when(tarjetaEstrategia.procesar(any())).thenReturn(
        ResultadoPago.exitoso("OK", "TARJETA-ABC123")
    );

    var factory = new PagoStrategyFactory(List.of(tarjetaEstrategia));
    var procesador = new ProcesadorPago(factory);

    var pago = new Pago("TARJETA", 59_900.0, "COP", "REF-001");

    // Act
    ResultadoPago resultado = procesador.procesar(pago);

    // Assert
    assertTrue(resultado.exitoso());
    assertEquals("TARJETA-ABC123", resultado.codigoTransaccion());
    verify(tarjetaEstrategia).procesar(pago);
}

@Test
void procesarPago_conCripto_noCambiaClaseProcesadorPago() {
    // Demostración de OCP: CriptoEstrategia se agrega sin tocar ProcesadorPago
    var cripto = new CriptoEstrategia();
    var factory = new PagoStrategyFactory(List.of(cripto));
    var procesador = new ProcesadorPago(factory);

    var pago = new Pago("CRIPTO", 0.005, "BTC", "REF-002");
    ResultadoPago resultado = procesador.procesar(pago);

    assertTrue(resultado.exitoso());
    assertTrue(resultado.codigoTransaccion().startsWith("CRIPTO-"));
}
```
```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?



#### 2. ¿Qué se puede mejorar?



#### 3. Respuesta final
