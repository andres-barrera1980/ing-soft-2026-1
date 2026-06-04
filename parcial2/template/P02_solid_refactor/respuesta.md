# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [02]: [P02_solid_refactor]

### Estudiante
- **Nombre completo**: Danna Gabriela Rojas Bernal

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Claude** | [Claude / ChatGPT / Gemini / Copilot / DeepSeek / Qwen / Mistral / Otro / **Sin IA — respuesta propia**] |
| **Sonnet 4.6** | [Ej: Claude Opus 4.5, GPT-4o, Gemini 2.5 Pro, etc. Si respondes sin IA, escribe "N/A"] |
| **Porque me dio curiosidad conocer la respuesta y mejroar la mia** | [Justifica en 1-3 oraciones. Si respondes sin IA, explica por qué decidiste no usar LLM para esta pregunta.] |

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
Eres un ingeniero de software senior revisando código del proyecto OpenLib Market,
una plataforma de compra-venta de libros digitales (Java 21, Spring Boot 3.x, PostgreSQL 16).
Te comparto la clase `ProcesadorPago` del módulo de pagos, escrita por un desarrollador junior:
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
Antes de escribir cualquier código, explica tu razonamiento:
¿qué principios SOLID se están violando, por qué, y cuál será tu estrategia de refactoring?
Luego necesito que:
1. Identifiques TODOS los principios SOLID violados con explicación concreta por cada uno.
2. Refactorices el módulo aplicando los principios correctos, soportando:
   tarjeta de crédito, PSE, PayPal y criptomonedas.
3. El nuevo diseño debe permitir agregar un nuevo método de pago en el futuro
   sin modificar código existente.
Restricciones:
- Cada clase debe tener una sola responsabilidad
- Java 21 con Spring Boot 3.x
- Usa constructor injection (no @Autowired en campo)
Formato de salida en markdown con estas secciones:
1. "Diagnóstico": principios SOLID violados y por qué
2. "Estrategia de refactoring": explicación antes del código
3. "Código refactorizado": clases completas con imports
4. "Ejemplo de uso": cómo se invocaría desde un cliente
---

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**
[Pega aquí la respuesta COMPLETA del LLM, sin editar, sin resumir.
Incluye TODO el texto, código, explicaciones que generó el LLM.

Si el LLM generó código, asegúrate de que esté correctamente formateado.
Si tuviste que hacer varias iteraciones, pega la MEJOR respuesta obtenida,
pero menciona cuántas iteraciones hiciste.]
```
1. Diagnóstico
Principios SOLID violados
S — Single Responsibility Principle (SRP) ❌
ProcesadorPago tiene tres responsabilidades distintas comprimidas en un solo método: procesar pagos con tarjeta (validar CVV, conectar con Visa/Mastercard), procesar pagos PSE (redirigir portal bancario, validar retorno) y procesar pagos PayPal (OAuth, webhooks). Cada uno de estos flujos es un dominio completamente diferente. Si cambia la API de PayPal, toca modificar la misma clase que maneja PSE, aunque PSE no cambió en absoluto. Una clase, una razón para cambiar — esto tiene tres.
O — Open/Closed Principle (OCP) ❌
Este es el más crítico. Para agregar criptomonedas hay que abrir ProcesadorPago y agregar un nuevo else if. La clase no está cerrada a modificación. Todo sistema que crece con if/else encadenados sobre un tipo string es un OCP muerto.
L — Liskov Substitution Principle (LSP) ⚠️ Parcial
No hay jerarquía de tipos aquí, lo cual es el problema: el diseño debería tener una abstracción ProcesadorPago que sus subtipos implementen de forma sustituible. Al no existir esa abstracción, LSP es irrelevante porque nunca se aplica — señal de que el diseño está incompleto.
D — Dependency Inversion Principle (DIP) ❌
El cliente que construye new ProcesadorPago("PAYPAL") depende directamente de una clase concreta que sabe cómo conectarse a PayPal. Los módulos de alto nivel (lógica de negocio de OpenLib) deberían depender de abstracciones, no de implementaciones concretas de pasarelas de pago.

ISP (Interface Segregation) no aplica directamente aquí porque no hay interfaces, pero el diseño propuesto lo respetará naturalmente.


2. Estrategia de refactoring
El patrón correcto es Strategy + Registry, combinado con inyección por Spring:

Extraer interfaz ProcesadorPago — abstracción que define el contrato: procesar(Pago) y soporta(String).
Una clase por método de pago — ProcesadorTarjeta, ProcesadorPSE, ProcesadorPayPal, ProcesadorCripto. Cada una tiene una sola razón para cambiar.
RegistroProcesadores — recibe todos los procesadores via constructor injection y resuelve cuál usar en tiempo de ejecución. Cero if/else. Agregar criptomonedas = crear una clase nueva + registrarla como @Component. No se toca nada más.
ServicioPago — orquestador de alto nivel que depende solo de la abstracción, nunca de las implementaciones concretas.


3. Código refactorizado
java// ─── 1. ABSTRACCIÓN (contrato del Strategy) ───────────────────────────────

package com.openlib.pagos;

/**
 * Contrato que deben implementar todos los métodos de pago.
 * Nuevo método de pago = nueva clase que implemente esta interfaz.
 * No se modifica ningún código existente (OCP ✅).
 */
public interface ProcesadorPago {

    /**
     * Procesa el pago y retorna el resultado.
     */
    ResultadoPago procesar(Pago pago);

    /**
     * Indica si este procesador maneja el tipo de pago dado.
     * Permite al registro seleccionar el procesador correcto sin if/else.
     */
    boolean soporta(TipoPago tipoPago);
}
java// ─── 2. ENUMERACIÓN DE TIPOS (evita strings mágicos) ──────────────────────

package com.openlib.pagos;

public enum TipoPago {
    TARJETA,
    PSE,
    PAYPAL,
    CRIPTO
}
java// ─── 3. MODELO DE DOMINIO ─────────────────────────────────────────────────

package com.openlib.pagos;

import java.math.BigDecimal;

public record Pago(
    String idTransaccion,
    BigDecimal monto,
    String moneda,
    TipoPago tipoPago,
    String referenciaCliente
) {}
java// ─── 4. MODELO DE RESULTADO ───────────────────────────────────────────────

package com.openlib.pagos;

public record ResultadoPago(
    boolean exitoso,
    String mensaje,
    String codigoAutorizacion
) {
    /** Factory para resultados exitosos */
    public static ResultadoPago exitoso(String mensaje, String codigoAutorizacion) {
        return new ResultadoPago(true, mensaje, codigoAutorizacion);
    }

    /** Factory para resultados fallidos */
    public static ResultadoPago fallido(String razon) {
        return new ResultadoPago(false, razon, null);
    }
}
java// ─── 5. IMPLEMENTACIÓN: TARJETA DE CRÉDITO ───────────────────────────────

package com.openlib.pagos.impl;

import com.openlib.pagos.Pago;
import com.openlib.pagos.ProcesadorPago;
import com.openlib.pagos.ResultadoPago;
import com.openlib.pagos.TipoPago;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * SRP ✅ — solo sabe procesar pagos con tarjeta (Visa/Mastercard).
 * Para cambiar la integración con la franquicia, solo esta clase cambia.
 */
@Component
public class ProcesadorTarjeta implements ProcesadorPago {

    private static final Logger log = LoggerFactory.getLogger(ProcesadorTarjeta.class);

    @Override
    public ResultadoPago procesar(Pago pago) {
        log.info("Procesando pago con tarjeta para transacción {}", pago.idTransaccion());
        // 1. Validar CVV y fecha de expiración
        // 2. Conectar con API de franquicia (Visa/Mastercard)
        // 3. Procesar cargo
        String codigoAutorizacion = generarCodigoAutorizacion(pago);
        return ResultadoPago.exitoso("Pago con tarjeta procesado exitosamente", codigoAutorizacion);
    }

    @Override
    public boolean soporta(TipoPago tipoPago) {
        return TipoPago.TARJETA.equals(tipoPago);
    }

    private String generarCodigoAutorizacion(Pago pago) {
        // Lógica real de autorización con la franquicia
        return "VISA-" + pago.idTransaccion().toUpperCase();
    }
}
java// ─── 6. IMPLEMENTACIÓN: PSE ───────────────────────────────────────────────

package com.openlib.pagos.impl;

import com.openlib.pagos.Pago;
import com.openlib.pagos.ProcesadorPago;
import com.openlib.pagos.ResultadoPago;
import com.openlib.pagos.TipoPago;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * SRP ✅ — solo sabe interactuar con el portal bancario de PSE.
 */
@Component
public class ProcesadorPSE implements ProcesadorPago {

    private static final Logger log = LoggerFactory.getLogger(ProcesadorPSE.class);

    @Override
    public ResultadoPago procesar(Pago pago) {
        log.info("Iniciando flujo PSE para transacción {}", pago.idTransaccion());
        // 1. Generar URL de redirección al portal bancario
        // 2. Validar retorno de PSE con código CUS
        // 3. Confirmar débito con ACH Colombia
        String codigoAutorizacion = "PSE-" + pago.idTransaccion().toUpperCase();
        return ResultadoPago.exitoso("Débito PSE confirmado exitosamente", codigoAutorizacion);
    }

    @Override
    public boolean soporta(TipoPago tipoPago) {
        return TipoPago.PSE.equals(tipoPago);
    }
}
java// ─── 7. IMPLEMENTACIÓN: PAYPAL ────────────────────────────────────────────

package com.openlib.pagos.impl;

import com.openlib.pagos.Pago;
import com.openlib.pagos.ProcesadorPago;
import com.openlib.pagos.ResultadoPago;
import com.openlib.pagos.TipoPago;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * SRP ✅ — solo sabe ejecutar el flujo OAuth + cargo con PayPal.
 */
@Component
public class ProcesadorPayPal implements ProcesadorPago {

    private static final Logger log = LoggerFactory.getLogger(ProcesadorPayPal.class);

    @Override
    public ResultadoPago procesar(Pago pago) {
        log.info("Autenticando con PayPal OAuth para transacción {}", pago.idTransaccion());
        // 1. Autenticar con OAuth 2.0 de PayPal
        // 2. Ejecutar cargo via Orders API v2
        // 3. Registrar webhook de confirmación IPN
        String codigoAutorizacion = "PP-" + pago.idTransaccion().toUpperCase();
        return ResultadoPago.exitoso("Cargo PayPal ejecutado exitosamente", codigoAutorizacion);
    }

    @Override
    public boolean soporta(TipoPago tipoPago) {
        return TipoPago.PAYPAL.equals(tipoPago);
    }
}
java// ─── 8. IMPLEMENTACIÓN: CRIPTOMONEDAS (nuevo, sin tocar nada anterior) ───

package com.openlib.pagos.impl;

import com.openlib.pagos.Pago;
import com.openlib.pagos.ProcesadorPago;
import com.openlib.pagos.ResultadoPago;
import com.openlib.pagos.TipoPago;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * OCP ✅ — este procesador se AGREGÓ sin modificar ninguna clase existente.
 * Solo se añadió CRIPTO al enum TipoPago y se creó esta clase.
 */
@Component
public class ProcesadorCripto implements ProcesadorPago {

    private static final Logger log = LoggerFactory.getLogger(ProcesadorCripto.class);

    @Override
    public ResultadoPago procesar(Pago pago) {
        log.info("Iniciando transacción cripto para {}", pago.idTransaccion());
        // 1. Generar dirección de wallet temporal
        // 2. Verificar confirmaciones en blockchain (mínimo 3)
        // 3. Convertir a COP via oracle de precio
        String codigoAutorizacion = "CRIPTO-" + pago.idTransaccion().toUpperCase();
        return ResultadoPago.exitoso("Transacción cripto confirmada en blockchain", codigoAutorizacion);
    }

    @Override
    public boolean soporta(TipoPago tipoPago) {
        return TipoPago.CRIPTO.equals(tipoPago);
    }
}
java// ─── 9. REGISTRO DE PROCESADORES (el corazón del patrón Strategy) ─────────

package com.openlib.pagos;

import java.util.List;

import org.springframework.stereotype.Component;

/**
 * Recibe TODOS los ProcesadorPago disponibles via constructor injection.
 * Spring inyecta automáticamente la lista completa de @Component que
 * implementen la interfaz — sin necesidad de registrarlos manualmente.
 *
 * DIP ✅ — depende de la abstracción List<ProcesadorPago>, no de concretos.
 * OCP ✅ — agregar un procesador nuevo = Spring lo detecta solo.
 * Sin if/else ni switch en ningún lugar ✅.
 */
@Component
public class RegistroProcesadores {

    private final List<ProcesadorPago> procesadores;

    // Constructor injection ✅
    public RegistroProcesadores(List<ProcesadorPago> procesadores) {
        this.procesadores = procesadores;
    }

    /**
     * Localiza el procesador correcto usando el método soporta() de cada uno.
     * Esto reemplaza completamente el if/else original.
     */
    public ProcesadorPago obtenerPara(TipoPago tipoPago) {
        return procesadores.stream()
                .filter(p -> p.soporta(tipoPago))
                .findFirst()
                .orElseThrow(() -> new MetodoPagoNoSoportadoException(tipoPago));
    }
}
java// ─── 10. EXCEPCIÓN DE DOMINIO ─────────────────────────────────────────────

package com.openlib.pagos;

public class MetodoPagoNoSoportadoException extends RuntimeException {
    public MetodoPagoNoSoportadoException(TipoPago tipoPago) {
        super("Método de pago no soportado: " + tipoPago);
    }
}
java// ─── 11. SERVICIO DE ALTO NIVEL (orquestador) ─────────────────────────────

package com.openlib.pagos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * SRP ✅ — solo orquesta el flujo de pago. No sabe nada de tarjetas ni PSE.
 * DIP ✅ — depende de RegistroProcesadores (abstracción), no de implementaciones.
 * Constructor injection ✅.
 */
@Service
public class ServicioPago {

    private static final Logger log = LoggerFactory.getLogger(ServicioPago.class);

    private final RegistroProcesadores registro;

    public ServicioPago(RegistroProcesadores registro) {
        this.registro = registro;
    }

    @Transactional
    public ResultadoPago ejecutar(Pago pago) {
        log.info("Iniciando pago {} por tipo {}", pago.idTransaccion(), pago.tipoPago());

        ProcesadorPago procesador = registro.obtenerPara(pago.tipoPago());
        ResultadoPago resultado = procesador.procesar(pago);

        log.info("Pago {} finalizado — éxito: {}", pago.idTransaccion(), resultado.exitoso());
        return resultado;
    }
}

4. Ejemplo de uso
Desde un @RestController de Spring:
javapackage com.openlib.pagos.api;

import com.openlib.pagos.Pago;
import com.openlib.pagos.ResultadoPago;
import com.openlib.pagos.ServicioPago;
import com.openlib.pagos.TipoPago;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pagos")
public class PagoController {

    private final ServicioPago servicioPago;

    // Constructor injection ✅
    public PagoController(ServicioPago servicioPago) {
        this.servicioPago = servicioPago;
    }

    @PostMapping
    public ResponseEntity<ResultadoPago> procesarPago(@RequestBody PagoRequest request) {
        Pago pago = new Pago(
            UUID.randomUUID().toString(),
            request.monto(),
            "COP",
            request.tipoPago(),       // "TARJETA", "PSE", "PAYPAL" o "CRIPTO"
            request.idCliente()
        );

        ResultadoPago resultado = servicioPago.ejecutar(pago);
        return resultado.exitoso()
            ? ResponseEntity.ok(resultado)
            : ResponseEntity.unprocessableEntity().body(resultado);
    }

    public record PagoRequest(BigDecimal monto, TipoPago tipoPago, String idCliente) {}
}
Flujo completo en runtime:
POST /api/v1/pagos
{ "monto": 45000, "tipoPago": "CRIPTO", "idCliente": "usr-42" }

   PagoController
       └─► ServicioPago.ejecutar(pago)
               └─► RegistroProcesadores.obtenerPara(CRIPTO)
                       └─► [stream] ProcesadorTarjeta.soporta(CRIPTO) → false
                       └─► [stream] ProcesadorPSE.soporta(CRIPTO)     → false
                       └─► [stream] ProcesadorPayPal.soporta(CRIPTO)  → false
                       └─► [stream] ProcesadorCripto.soporta(CRIPTO)  → true ✅
               └─► ProcesadorCripto.procesar(pago)
                       └─► ResultadoPago.exitoso("Transacción cripto confirmada...", "CRIPTO-XXX")

Resumen de principios aplicados
PrincipioAntesDespuésS — Una responsabilidad❌ Una clase con 3 flujos distintos✅ Una clase por método de pagoO — Abierto/cerrado❌ Agregar método = modificar if/else✅ Agregar método = nueva @ComponentL — Sustitución Liskov⚠️ Sin jerarquía de tipos✅ Cualquier ProcesadorPago es sustituibleI — Segregación de interfacesN/A✅ Interfaz mínima y enfocadaD — Inversión de dependencias❌ Depende de strings mágicos y concretos✅ ServicioPago depende solo de abstraccionesSonnet 4.6 Bajo

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

[Evalúa tu propio prompt, no la respuesta del LLM. ¿El contexto fue suficiente? ¿Las restricciones fueron claras? ¿El formato de salida que pediste ayudó a obtener una buena respuesta? ¿Qué parte de tu prompt fue más efectiva? Sé específico: menciona fragmentos concretos de tu prompt que funcionaron bien.]

creo que lo que más funcionó fue asignarle el rol de ingeniero senior desde el principio por lo que dio una respuesta mas tecnica y justificada en lugar de ser tan generica, tambien ayudó mucho pedir explícitamente las secciones de salida, porque el LLM oprganizò todo de forma clara y no mezclò el diagnóstico con el código; y especificar Java 21 y spring boot 3.x fue importante también, evitò que generara codigo con anotaciones viejas

#### 2. ¿Qué se puede mejorar?

[¿Qué le faltó a tu prompt? ¿Qué harías diferente si pudieras reformularlo? ¿El LLM entendió mal algo por falta de claridad en tu prompt? ¿La respuesta tiene errores u omisiones? ¿Qué no cubrió el LLM que tú sí sabes por lo visto en clase?]
no 

#### 3. Respuesta final

[Escribe tu respuesta definitiva a la pregunta del parcial, integrando lo que aprendiste del LLM pero yendo más allá. Corrige errores, llena omisiones, conecta con conceptos vistos en clase. Esta es tu respuesta: demuestra que tú dominas el tema.]

La clase original tiene el problema de que concentra demasiadas cosas en un solo lugar cada metodo de pago es un dominio completamente distinto con su propia logica. Mezclarlos en una sola clase hace que cualquier modificacion, por pequeña que sea, ponga en riesgo el resto.
