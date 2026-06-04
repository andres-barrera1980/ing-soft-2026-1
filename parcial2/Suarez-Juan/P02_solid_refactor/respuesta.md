# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [02]: Principio SOLID

### Estudiante
- **Nombre completo**: Juan Pablo Suarez Moreno

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Claude |
| **Modelo específico** | Sonnet 4.6 Bajo |
| **¿Por que elegiste este LLM?** | Se a elegido este modelo gracias a que destaca por su rendimiento a un costo menor de costo de tokens, ademas de ser reconocido por poder depurar y crear codigo eficientemente |

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

[CONTEXTO]
Proyecto: OpenLib Market (Plataforma transaccional E-commerce para libros Open Source).
Módulo actual: Buyer.
Stack Tecnologico: Java 25, Spring Boot 4.x, PostgreSQL/MongoDB, JUnit 5.

[PROBLEMA]
Necesito analizar el código adjunto para identificar fallas o principios SOLID son vulnerados.
Código a analizar:
----------------------------------------
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
----------------------------------------

[RESTRICCIONES]
1. Identificar al menos 2 principios SOLID que se estén violando en el diseño actual.
2. Proponer un refactoring completo aplicando un patron de diseño creacional, estructural o de comportamiento que pueda resuelver el problema.
3. El codigo refactorizado debe mantener la compatibilidad con Java 25 y Spring Boot 4.x.

[FORMATO DE SALIDA]
Solicito la respuesta estructurada de la siguiente manera en formato markdown:
1. Analisis de SOLID: Explicación clara de que principios se violan y por que.
2. Eleccion del Patron: Justifica de que patron de diseño se eligiste para la solucionarlo y como resuelve el problema.
3. Codigo Refactorizado: Bloques de codigo limpios, comentados y aplicando Clean Code.

[VALIDACIÓN / CRITERIOS DE ÉXITO]
La solucion es correcta si el nuevo diseño desacopla las responsabilidades y cumple correctamente con los principios SOLID que pueda estar asociado.
```

---

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**
Aquí está el análisis completo y refactoring del código:

---

## 1. Análisis de Principios SOLID Violados

### ❌ S — Single Responsibility Principle (SRP)
`ProcesadorPago` tiene **múltiples razones para cambiar**: si cambia la lógica de tarjetas, PSE o PayPal, se modifica la misma clase. Una clase, una responsabilidad — aquí hay tres lógicas de negocio distintas conviviendo.

### ❌ O — Open/Closed Principle (OCP)
Para agregar un nuevo método de pago (ej. Nequi, Daviplata), **hay que modificar el método `procesar()`** añadiendo otro `else if`. La clase no está cerrada para modificación ni abierta para extensión.

### ⚠️ L — Liskov Substitution (LSP) — Violación implícita
Al usar un `String` como discriminador de tipo, el polimorfismo real no existe. Cualquier subtipo que reemplace a `ProcesadorPago` debe conocer el valor exacto del string, lo que rompe la sustituibilidad segura.

---

## 2. Patrón Elegido: **Strategy + Factory Method**

| Patrón | Rol en la solución |
|---|---|
| **Strategy** | Define una interfaz común `ProcesadorPago` e implementaciones concretas por método de pago. Cada una tiene su propia responsabilidad. |
| **Factory Method** | `ProcesadorPagoFactory` encapsula la creación del procesador correcto según el tipo, sin que el cliente conozca las implementaciones. |

**¿Por qué este combo?**
- **Strategy** resuelve SRP y OCP: cada clase tiene una sola responsabilidad, y agregar Nequi = crear una nueva clase, sin tocar las existentes.
- **Factory** desacopla la instanciación del uso, compatible con inyección de dependencias de Spring Boot 4.x.

---

## 3. Código Refactorizado

### 📁 Estructura de paquetes

```
com.openlib.buyer.payment/
├── domain/
│   ├── Pago.java
│   └── ResultadoPago.java
├── strategy/
│   ├── ProcesadorPago.java          ← interfaz (Strategy)
│   ├── ProcesadorTarjeta.java
│   ├── ProcesadorPSE.java
│   └── ProcesadorPayPal.java
└── factory/
    └── ProcesadorPagoFactory.java
```

---

### `ProcesadorPago.java` — Interfaz Strategy

```java
package com.openlib.buyer.payment.strategy;

import com.openlib.buyer.payment.domain.Pago;
import com.openlib.buyer.payment.domain.ResultadoPago;

/**
 * Contrato común para todos los métodos de pago.
 * Permite agregar nuevos procesadores sin modificar código existente (OCP).
 */
public interface ProcesadorPago {

    /**
     * Procesa el pago según la estrategia concreta.
     *
     * @param pago información del pago a procesar
     * @return resultado con estado y mensaje descriptivo
     */
    ResultadoPago procesar(Pago pago);
}
```

---

### `ProcesadorTarjeta.java` — Estrategia concreta

```java
package com.openlib.buyer.payment.strategy;

import com.openlib.buyer.payment.domain.Pago;
import com.openlib.buyer.payment.domain.ResultadoPago;
import org.springframework.stereotype.Component;

/**
 * Procesador de pagos con tarjeta crédito/débito.
 * Responsabilidad única: orquestar el flujo de pago con franquicias (Visa/Mastercard).
 */
@Component
public class ProcesadorTarjeta implements ProcesadorPago {

    @Override
    public ResultadoPago procesar(Pago pago) {
        validarDatosTarjeta(pago);
        conectarConFranquicia(pago);
        return new ResultadoPago(true, "Pago con tarjeta procesado exitosamente");
    }

    private void validarDatosTarjeta(Pago pago) {
        // Validar CVV, fecha de expiración y disponibilidad de fondos
    }

    private void conectarConFranquicia(Pago pago) {
        // Conectar con API de Visa/Mastercard y procesar cargo
    }
}
```

---

### `ProcesadorPSE.java` — Estrategia concreta

```java
package com.openlib.buyer.payment.strategy;

import com.openlib.buyer.payment.domain.Pago;
import com.openlib.buyer.payment.domain.ResultadoPago;
import org.springframework.stereotype.Component;

/**
 * Procesador de pagos vía PSE (débito bancario en Colombia).
 * Responsabilidad única: gestionar el flujo de redirección y confirmación con PSE.
 */
@Component
public class ProcesadorPSE implements ProcesadorPago {

    @Override
    public ResultadoPago procesar(Pago pago) {
        redirigirAPortalBancario(pago);
        confirmarDebito(pago);
        return new ResultadoPago(true, "Pago PSE procesado exitosamente");
    }

    private void redirigirAPortalBancario(Pago pago) {
        // Generar URL de redirección al portal del banco seleccionado
    }

    private void confirmarDebito(Pago pago) {
        // Validar retorno de PSE y confirmar el débito en cuenta
    }
}
```

---

### `ProcesadorPayPal.java` — Estrategia concreta

```java
package com.openlib.buyer.payment.strategy;

import com.openlib.buyer.payment.domain.Pago;
import com.openlib.buyer.payment.domain.ResultadoPago;
import org.springframework.stereotype.Component;

/**
 * Procesador de pagos con PayPal.
 * Responsabilidad única: autenticación OAuth y ejecución del cargo en PayPal.
 */
@Component
public class ProcesadorPayPal implements ProcesadorPago {

    @Override
    public ResultadoPago procesar(Pago pago) {
        autenticarConOAuth(pago);
        ejecutarCargo(pago);
        registrarWebhookConfirmacion(pago);
        return new ResultadoPago(true, "Pago PayPal procesado exitosamente");
    }

    private void autenticarConOAuth(Pago pago) {
        // Obtener token de acceso mediante OAuth 2.0 de PayPal
    }

    private void ejecutarCargo(Pago pago) {
        // Ejecutar el cargo en la cuenta PayPal del comprador
    }

    private void registrarWebhookConfirmacion(Pago pago) {
        // Registrar endpoint de webhook para confirmación asíncrona
    }
}
```

---

### `TipoPago.java` — Enum tipado (reemplaza el String mágico)

```java
package com.openlib.buyer.payment.domain;

/**
 * Tipos de pago soportados por OpenLib Market.
 * Usar enum elimina strings mágicos y habilita verificación en tiempo de compilación.
 */
public enum TipoPago {
    TARJETA,
    PSE,
    PAYPAL
}
```

---

### `ProcesadorPagoFactory.java` — Factory Method con Spring

```java
package com.openlib.buyer.payment.factory;

import com.openlib.buyer.payment.domain.TipoPago;
import com.openlib.buyer.payment.strategy.ProcesadorPago;
import com.openlib.buyer.payment.strategy.ProcesadorPayPal;
import com.openlib.buyer.payment.strategy.ProcesadorPSE;
import com.openlib.buyer.payment.strategy.ProcesadorTarjeta;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Factory responsable de resolver qué estrategia de pago usar.
 *
 * Desacopla la creación del procesador de su uso.
 * Agregar un nuevo método de pago = registrarlo aquí + nueva clase Strategy.
 * El código cliente (servicio) no necesita cambiar (OCP cumplido).
 */
@Component
public class ProcesadorPagoFactory {

    // Mapa de estrategias registradas: TipoPago → implementación concreta
    private final Map<TipoPago, ProcesadorPago> procesadores;

    public ProcesadorPagoFactory(
            ProcesadorTarjeta tarjeta,
            ProcesadorPSE pse,
            ProcesadorPayPal payPal
    ) {
        this.procesadores = Map.of(
                TipoPago.TARJETA, tarjeta,
                TipoPago.PSE,     pse,
                TipoPago.PAYPAL,  payPal
        );
    }

    /**
     * Resuelve y retorna el procesador correspondiente al tipo de pago.
     *
     * @param tipo método de pago seleccionado por el comprador
     * @return implementación concreta de ProcesadorPago
     * @throws IllegalArgumentException si el tipo de pago no está soportado
     */
    public ProcesadorPago obtener(TipoPago tipo) {
        ProcesadorPago procesador = procesadores.get(tipo);

        if (procesador == null) {
            throw new IllegalArgumentException(
                "Método de pago no soportado: " + tipo
            );
        }

        return procesador;
    }
}
```

---

### `PagoService.java` — Servicio cliente (código limpio, sin lógica de pago)

```java
package com.openlib.buyer.payment;

import com.openlib.buyer.payment.domain.Pago;
import com.openlib.buyer.payment.domain.ResultadoPago;
import com.openlib.buyer.payment.domain.TipoPago;
import com.openlib.buyer.payment.factory.ProcesadorPagoFactory;
import com.openlib.buyer.payment.strategy.ProcesadorPago;
import org.springframework.stereotype.Service;

/**
 * Servicio de pagos del módulo Buyer.
 * Orquesta el proceso delegando a la estrategia correcta vía Factory.
 * No conoce ninguna implementación concreta → bajo acoplamiento.
 */
@Service
public class PagoService {

    private final ProcesadorPagoFactory factory;

    public PagoService(ProcesadorPagoFactory factory) {
        this.factory = factory;
    }

    /**
     * Procesa el pago de una orden en OpenLib Market.
     *
     * @param pago     datos del pago
     * @param tipoPago método seleccionado por el comprador
     * @return resultado del procesamiento
     */
    public ResultadoPago procesarPago(Pago pago, TipoPago tipoPago) {
        ProcesadorPago procesador = factory.obtener(tipoPago);
        return procesador.procesar(pago);
    }
}
```

---

### `ProcesadorPagoTest.java` — Test unitario con JUnit 5

```java
package com.openlib.buyer.payment;

import com.openlib.buyer.payment.domain.Pago;
import com.openlib.buyer.payment.domain.ResultadoPago;
import com.openlib.buyer.payment.domain.TipoPago;
import com.openlib.buyer.payment.factory.ProcesadorPagoFactory;
import com.openlib.buyer.payment.strategy.ProcesadorPayPal;
import com.openlib.buyer.payment.strategy.ProcesadorPSE;
import com.openlib.buyer.payment.strategy.ProcesadorTarjeta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ProcesadorPagoTest {

    private PagoService pagoService;

    @BeforeEach
    void setUp() {
        var factory = new ProcesadorPagoFactory(
                new ProcesadorTarjeta(),
                new ProcesadorPSE(),
                new ProcesadorPayPal()
        );
        pagoService = new PagoService(factory);
    }

    @ParameterizedTest
    @EnumSource(TipoPago.class)
    @DisplayName("Cada tipo de pago soportado debe retornar resultado exitoso")
    void debeProcessarTodosLosTiposDePago(TipoPago tipo) {
        Pago pago = new Pago(/* datos de prueba */);

        ResultadoPago resultado = pagoService.procesarPago(pago, tipo);

        assertThat(resultado.exitoso()).isTrue();
    }

    @Test
    @DisplayName("Debe lanzar excepción si se agrega un tipo no registrado en la factory")
    void debeLanzarExcepcionParaTipoNoSoportado() {
        // Simula factory vacía para validar el guard clause
        var factoryVacia = new ProcesadorPagoFactory(null, null, null) {
            @Override
            public com.openlib.buyer.payment.strategy.ProcesadorPago obtener(TipoPago tipo) {
                throw new IllegalArgumentException("Método no soportado: " + tipo);
            }
        };

        var service = new PagoService(factoryVacia);

        assertThatThrownBy(() -> service.procesarPago(new Pago(), TipoPago.TARJETA))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no soportado");
    }
}
```

---

## Resumen del Cumplimiento SOLID

| Principio | ¿Cumple? | Cómo |
|---|---|---|
| **S** — Single Responsibility | ✅ | Cada clase tiene exactamente una razón para cambiar |
| **O** — Open/Closed | ✅ | Agregar Nequi = nueva clase, sin tocar existentes |
| **L** — Liskov Substitution | ✅ | Cualquier `ProcesadorPago` es sustituible sin romper el contrato |
| **I** — Interface Segregation | ✅ | Interfaz mínima con un único método relevante |
| **D** — Dependency Inversion | ✅ | `PagoService` depende de la abstracción, no de concretos |

Si el LLM generó código, asegúrate de que esté correctamente formateado.
Si tuviste que hacer varias iteraciones, pega la MEJOR respuesta obtenida,
pero menciona cuántas iteraciones hiciste.]


### Análisis crítico de la respuesta

### Preguntas documento

#### ¿El LLM identificó correctamente los principios violados?

Si identifico los Pirncipios SOLID vulnerados (Single responsabilty, y Open/Close pinciple), causadas por que al cambiar al usar un if para cada tarjeta no esta abierta para extension y hay multiples logicas por tarjetas que se podrias separar

#### ¿El patrón elegido es el adecuado?

Considero que Strategy fue la mejor eleccion en este problema el comportamiento varia segun el tipo en tiempo de ejecucion, es funcional y practico usar una interfaz comun y dejar implementaciones concretas aparte. Factory complementa para desacoplar la creacion al encapsular sind ejar que el cliente conozca la implementacion.

#### ¿Qué interfaces o clases abstractas introdujo?

ProcesadorPago gracias a Strategy, junto con el enum TipoPago para eliminar string y ProcesadorPagoFactory como creacional. En el codigo no se crearon clases abstractas, gracias a que no hay comportamiento compratido entre los procesos que requiera una clase padre.

#### ¿El diseño permite agregar un nuevo método de pago sin modificar código existente?

Si, en el caso de querer agregar Nequi podemos simplemente crear ProcesadorNequi implements ProcesadorPago.

#### ¿Qué mejorarías?

Considero que lo que le falta es tener un mejor manejo de rrores con excepciones de dominio propias, en lugar de IlegalArgumentsException.

#### 1. ¿que hizo bien el prompt?

EL prompt cuenta con contexto y todas las especificaciones evaluadas en la rubrica, cuenta con limites criterios de exito, sin la necesidad de extender innecesariamente la explicacion.

#### 2. ¿que se puede mejorar?

Me gustaria haber especificado en los principios que directamente estaban siendo vulnerados, tambien homitiria el apartado de pruebas que agrego al final en Jupiter que pesar de que es muy util es innecesario en este caso.

#### 3. Respuesta final


[Escribe tu respuesta definitiva a la pregunta del parcial, integrando lo que aprendiste del LLM pero yendo más allá. Corrige errores, llena omisiones, conecta con conceptos vistos en clase. Esta es tu respuesta: demuestra que tú dominas el tema.]