## P02_solid_refactor

### Estudiante
- **Nombre completo**: [Alejandro Macias Barrios]

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [Claude] |
| **Modelo específico** | [Claude Sonnet 4.6] |
| **¿Por qué elegiste este LLM?** | [Claude ha demostrado tener un buen rendimiento en tareas de diseño y refactorización de código, así como también en la aplicación de principios de ingeniería de software. Además, ha sido uno de los que mejor hemos usado en clase, y considero que es uno de los modelos más avanzados disponibles.] |
---

### Prompt utilizado
```
[Imagina que eres un estudiante de Ingenieria de sistemas de la Pontificia Universidad Javeriana cursando la materia de Fundamentos de Ingeniería de Software. El profesor te ha hecho la siguiente pregunta y debes responderla de la mejor manera posible.
OpenLib Market necesita soportar múltiples métodos de pago (tarjeta de crédito, PSE, PayPal, criptomonedas). El equipo junior escribió este código:
```java
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
Analiza este código, identifica los principios SOLID violados, y proponga un refactoring completo usando el patrón de diseño que considere apropiado para corregirlo.]

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**
[Principios SOLID violados
1. Single Responsibility Principle (SRP)
ProcesadorPago hace demasiado: valida, conecta con APIs externas, procesa cargos y gestiona webhooks — todo en una sola clase.
2. Open/Closed Principle (OCP) — el más crítico
Para agregar criptomonedas hay que modificar la clase existente añadiendo otro else if. La clase no está cerrada para modificación.
3. Liskov Substitution Principle (LSP)
No hay jerarquía de tipos real — un String determina el comportamiento, lo que hace imposible sustituir implementaciones polimórficamente.
4. Dependency Inversion Principle (DIP)
El código de alto nivel (ProcesadorPago) depende de detalles concretos embebidos en condicionales, no de abstracciones.

Patrón apropiado: Strategy + Factory

Strategy: define una familia de algoritmos de pago intercambiables
Factory: desacopla la creación de estrategias del resto del sistema

// ============================================================
// 1. CONTRATO (Strategy Interface) — abierto a extensión
// ============================================================
public interface EstrategiaPago {
    boolean soporta(TipoPago tipo);
    ResultadoPago procesar(Pago pago);
}

public enum TipoPago {
    TARJETA, PSE, PAYPAL, CRIPTO
}

// ============================================================
// 2. ESTRATEGIAS CONCRETAS — cada una con responsabilidad única
// ============================================================
public class PagoTarjeta implements EstrategiaPago {

    private final ValidadorTarjeta validador;
    private final Apifranquicia apiFranquicia;

    public PagoTarjeta(ValidadorTarjeta validador, Apifranquicia apiFranquicia) {
        this.validador = validador;
        this.apiFranquicia = apiFranquicia;
    }

    @Override
    public boolean soporta(TipoPago tipo) {
        return tipo == TipoPago.TARJETA;
    }

    @Override
    public ResultadoPago procesar(Pago pago) {
        validador.validarCVV(pago);
        validador.validarExpiracion(pago);
        validador.validarFondos(pago);
        return apiFranquicia.ejecutarCargo(pago);
    }
}

public class PagoPSE implements EstrategiaPago {

    private final PortalPSE portalPSE;

    public PagoPSE(PortalPSE portalPSE) {
        this.portalPSE = portalPSE;
    }

    @Override
    public boolean soporta(TipoPago tipo) {
        return tipo == TipoPago.PSE;
    }

    @Override
    public ResultadoPago procesar(Pago pago) {
        String urlRetorno = portalPSE.redirigir(pago);
        portalPSE.validarRetorno(urlRetorno);
        return portalPSE.confirmarDebito(pago);
    }
}

public class PagoPayPal implements EstrategiaPago {

    private final ClientePayPal clientePayPal;
    private final GestorWebhooks webhooks;

    public PagoPayPal(ClientePayPal clientePayPal, GestorWebhooks webhooks) {
        this.clientePayPal = clientePayPal;
        this.webhooks = webhooks;
    }

    @Override
    public boolean soporta(TipoPago tipo) {
        return tipo == TipoPago.PAYPAL;
    }

    @Override
    public ResultadoPago procesar(Pago pago) {
        clientePayPal.autenticarOAuth(pago);
        ResultadoPago resultado = clientePayPal.ejecutarCargo(pago);
        webhooks.registrarConfirmacion(resultado);
        return resultado;
    }
}

// Nueva estrategia: CERO modificaciones al código existente ✓
public class PagoCripto implements EstrategiaPago {

    private final ClienteBlockchain blockchain;

    public PagoCripto(ClienteBlockchain blockchain) {
        this.blockchain = blockchain;
    }

    @Override
    public boolean soporta(TipoPago tipo) {
        return tipo == TipoPago.CRIPTO;
    }

    @Override
    public ResultadoPago procesar(Pago pago) {
        blockchain.validarWallet(pago);
        return blockchain.emitirTransaccion(pago);
    }
}

// ============================================================
// 3. FACTORY — desacopla la creación, centraliza el registro
// ============================================================
public class FabricaEstrategiasPago {

    private final List<EstrategiaPago> estrategias;

    // Inyección de dependencias (Spring, Guice, o manual)
    public FabricaEstrategiasPago(List<EstrategiaPago> estrategias) {
        this.estrategias = estrategias;
    }

    public EstrategiaPago obtener(TipoPago tipo) {
        return estrategias.stream()
                .filter(e -> e.soporta(tipo))
                .findFirst()
                .orElseThrow(() -> new TipoPagoNoSoportadoException(tipo));
    }
}

// ============================================================
// 4. PROCESADOR — ahora es un orquestador delgado (SRP limpio)
// ============================================================
public class ProcesadorPago {

    private final FabricaEstrategiasPago fabrica;
    private final RegistroTransacciones registro;

    public ProcesadorPago(FabricaEstrategiasPago fabrica,
                          RegistroTransacciones registro) {
        this.fabrica = fabrica;
        this.registro = registro;
    }

    public ResultadoPago procesar(Pago pago) {
        EstrategiaPago estrategia = fabrica.obtener(pago.getTipo());
        ResultadoPago resultado = estrategia.procesar(pago);
        registro.guardar(pago, resultado);
        return resultado;
    }
}

Cómo agregar criptomonedas en producción
// Solo esto. Sin tocar ninguna clase existente.
@Bean
public PagoCripto pagoCripto(ClienteBlockchain blockchain) {
    return new PagoCripto(blockchain);
}
// Spring/el contenedor DI la inyecta automáticamente en la lista

El principio clave que guía todo el diseño es el OCP: el sistema ahora está abierto a extensión (nuevas estrategias) y cerrado a modificación (el orquestador no cambia jamás).]

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

[Después de haber hecho el Prompt creo que en la parte del contexto pude haberle dado un poco de infromación acerca del proyecto de OpenLib Market y como estaba estructurado, pero a pesar de eso el contexto que le di fue suficiente para que entendiera lo que queria. Las restricciones fueron claras. El formato de salida que pedí ayudó a obtener una buena respuesta. Yo siento que la parte mas importante del Promt fue la ultima parte en la que explicitamente le digo que es lo que tiene que hacer y lo que yo quería. "identifica los principios SOLID violados, y proponga un refactoring completo usando el patrón de diseño que considere apropiado para corregirlo.".]


#### 2. ¿Qué se puede mejorar?

[¿Depronto al Promt le faltó un poco mas de contexto en cuanto al proyecto que se esta desarrollando. Yo le explicaría que es lo que se esta haciendo y por ejemplo le comentaría un poco acerca de las tecnologías que se están utilizando. Yo siento que el LLM entendió todo bien, no encontré errores u omisiones. El LLM cubrió lo que yo quería.]


#### 3. Respuesta final

[Los principios que se están violando son: 
1. Single Responsibility Principle ya que la clase ProcesadorPago se esta encargando de hacer muchas tareas a la vez, por ejemplo: valida, conecta con APIs externas, procesa cargos y gestiona webhooks. 
2. Open/Closed Principle ya que si se quiere crear un nuevo metodo de pago "agregarCriptomonedas" hay que modificar la clase existente añadiendo otro else if. La clase no está cerrada para modificación.
3. Dependency Inversion Principle ya que el código de alto nivel (la función ProcesadorPago) depende de detalles concretos que se ecnuentran dentro de condicionales, no de abstracciones. Este último es el que mas afecta al código ya que si se quiere crear un nuevo metodo de pago toca modificar el código existente.

El patrón de diseño que se utiliza es el Strategy Pattern que consiste en definir una familia de algoritmos, encapsular cada uno de ellos y hacerlos intercambiables. El patrón Strategy permite que el algoritmo varíe independientemente de los clientes que lo utilizan. La clase ProcesadorPago ahora depende de una abstracciónStrategy (EstrategiaPago), lo que hace que el código sea más fácil de mantener y extender.]
