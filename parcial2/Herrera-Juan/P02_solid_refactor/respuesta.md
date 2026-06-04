# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [2]: [solid refactor]

### Estudiante
- **Nombre completo**: [Juan Fernando Herrera Patiño]

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [ChatGPT] |
| **Modelo específico** | [GPT-4o ]|
| **¿Por qué elegiste este LLM?** | [mas comodo para mi] |

---

### Prompt utilizado

Analiza el siguiente código de ProcesadorPago. Identifica qué principios SOLID se están violando (mínimo dos) y explica por qué. Luego propone un refactoring completo aplicando el patrón de diseño más adecuado para este caso. Tu solución debe mostrar las interfaces o clases abstractas necesarias y las implementaciones concretas para cada método de pago (tarjeta, PSE, PayPal, criptomonedas). Finalmente, justifica cómo tu diseño permite agregar nuevos métodos de pago sin modificar el código existente y cómo mejora la mantenibilidad y extensibilidad del sistema.

java
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

---

### Respuesta del LLM

>Análisis de Principios SOLID en ProcesadorPago
1. Principios SOLID Violados

Aunque existen varias violaciones, las más importantes son:

A. SRP (Single Responsibility Principle)

Definición:

Una clase debe tener una única razón para cambiar.

Problema

La clase ProcesadorPago realiza múltiples responsabilidades:

Decide qué método de pago utilizar.
Implementa la lógica de tarjeta.
Implementa la lógica de PSE.
Implementa la lógica de PayPal.
Maneja errores de métodos no soportados.
if (tipoPago.equals("TARJETA")) {
   ...
}
else if (tipoPago.equals("PSE")) {
   ...
}
else if (tipoPago.equals("PAYPAL")) {
   ...
}

Si cambia la integración con PayPal, la clase cambia.

Si cambia PSE, la clase cambia.

Si aparece un nuevo medio de pago, la clase cambia.

Tiene múltiples razones para modificarse.

B. OCP (Open/Closed Principle)

Definición:

Las entidades de software deben estar abiertas para extensión pero cerradas para modificación.

Problema

Supongamos que OpenLib desea agregar:

Criptomonedas
Nequi
Daviplata
Apple Pay

Actualmente debemos modificar:

public ResultadoPago procesar(Pago pago)

y agregar:

else if(tipoPago.equals("CRYPTO")) {
   ...
}

Cada nuevo método obliga a modificar código existente.

Esto viola directamente OCP.

C. DIP (Dependency Inversion Principle)

Definición:

Los módulos de alto nivel no deben depender de módulos de bajo nivel; ambos deben depender de abstracciones.

Actualmente:

ProcesadorPago

depende directamente de:

Tarjeta
PSE
PayPal

porque conoce toda su implementación.

No existe una abstracción común.

2. Patrón de Diseño Adecuado

El patrón más apropiado es:

Strategy Pattern

¿Por qué?

Porque cada forma de pago representa un algoritmo diferente para realizar la misma operación:

procesarPago()

Tarjeta, PSE, PayPal y Cripto son estrategias intercambiables.

3. Refactoring Completo
Paso 1: Crear la abstracción
Interface
public interface MetodoPago {

    ResultadoPago procesar(Pago pago);

}
Paso 2: Implementación Tarjeta
public class PagoTarjeta implements MetodoPago {

    @Override
    public ResultadoPago procesar(Pago pago) {

        // Validar CVV

        // Validar expiración

        // Verificar fondos

        // Conectar Visa/Mastercard

        return new ResultadoPago(
            true,
            "Pago con tarjeta procesado"
        );
    }
}
Paso 3: Implementación PSE
public class PagoPSE implements MetodoPago {

    @Override
    public ResultadoPago procesar(Pago pago) {

        // Redirección bancaria

        // Confirmación PSE

        return new ResultadoPago(
            true,
            "Pago PSE procesado"
        );
    }
}
Paso 4: Implementación PayPal
public class PagoPayPal implements MetodoPago {

    @Override
    public ResultadoPago procesar(Pago pago) {

        // OAuth

        // Cargo PayPal

        // Webhook

        return new ResultadoPago(
            true,
            "Pago PayPal procesado"
        );
    }
}
Paso 5: Implementación Criptomonedas
public class PagoCripto implements MetodoPago {

    @Override
    public ResultadoPago procesar(Pago pago) {

        // Generar wallet

        // Esperar confirmaciones blockchain

        // Registrar hash transacción

        return new ResultadoPago(
            true,
            "Pago con criptomonedas procesado"
        );
    }
}
Paso 6: Nuevo ProcesadorPago

Ahora el procesador únicamente coordina.

public class ProcesadorPago {

    private MetodoPago metodoPago;

    public ProcesadorPago(
            MetodoPago metodoPago) {

        this.metodoPago = metodoPago;
    }

    public ResultadoPago procesar(
            Pago pago) {

        return metodoPago.procesar(pago);
    }
}
Uso del Sistema
Tarjeta
MetodoPago metodo =
        new PagoTarjeta();

ProcesadorPago procesador =
        new ProcesadorPago(metodo);

procesador.procesar(pago);
PSE
MetodoPago metodo =
        new PagoPSE();

ProcesadorPago procesador =
        new ProcesadorPago(metodo);

procesador.procesar(pago);
PayPal
MetodoPago metodo =
        new PagoPayPal();

ProcesadorPago procesador =
        new ProcesadorPago(metodo);

procesador.procesar(pago);
Criptomonedas
MetodoPago metodo =
        new PagoCripto();

ProcesadorPago procesador =
        new ProcesadorPago(metodo);

procesador.procesar(pago);
Diagrama Conceptual
                 +----------------+
                 |  MetodoPago    |
                 +----------------+
                 | +procesar()    |
                 +--------+-------+
                          |
      -----------------------------------------
      |               |            |          |
      v               v            v          v

+-----------+  +-----------+ +-----------+ +-----------+
| Tarjeta   |  | PSE       | | PayPal    | | Cripto    |
+-----------+  +-----------+ +-----------+ +-----------+
| procesar()|  | procesar()| | procesar()| | procesar()|
+-----------+  +-----------+ +-----------+ +-----------+

                          ^
                          |
                  +---------------+
                  |ProcesadorPago |
                  +---------------+
                  | MetodoPago    |
                  +---------------+
                  | procesar()    |
                  +---------------+
4. ¿Cómo se cumple SOLID?
SRP

Antes:

ProcesadorPago

contenía la lógica de todos los medios.

Ahora:

PagoTarjeta

solo maneja tarjetas.

PagoPSE

solo maneja PSE.

PagoPayPal

solo maneja PayPal.

Cada clase tiene una única responsabilidad.

OCP

Antes:

Agregar Cripto requería:

else if(tipoPago.equals("CRYPTO"))

modificando código existente.

Ahora:

Simplemente creamos:

public class PagoCripto
       implements MetodoPago

No se modifica:

ProcesadorPago

ni ninguna estrategia existente.

El sistema está:

✅ Cerrado para modificación

✅ Abierto para extensión

DIP

Antes:

ProcesadorPago

dependía directamente de implementaciones concretas.

Ahora depende de:

MetodoPago

que es una abstracción.

private MetodoPago metodoPago;

Cumple el Principio de Inversión de Dependencias.

5. Beneficios para Mantenibilidad y Extensibilidad
Aspecto	Diseño Original	Diseño Refactorizado
Agregar nuevo método de pago	Modificar clase existente	Crear nueva estrategia
Riesgo de romper código	Alto	Bajo
Reutilización	Baja	Alta
Test unitarios	Difíciles	Simples
Acoplamiento	Alto	Bajo
Escalabilidad	Limitada	Alta
Conclusión

El código original viola principalmente SRP, OCP y DIP, debido a que concentra todas las variantes de pago en una única clase mediante condicionales. La solución más adecuada es aplicar el patrón Strategy, creando una interfaz MetodoPago y una implementación concreta para cada medio de pago (Tarjeta, PSE, PayPal y Criptomonedas). Con este diseño, el sistema se vuelve más mantenible, extensible y testeable, ya que nuevos métodos de pago pueden agregarse mediante nuevas estrategias sin modificar el código existente.
```

---

La IA señaló bien las violaciones de SRP, OCP y DIP en la clase ProcesadorPago. También eligió correctamente el patrón Strategy, porque cada forma de pago es un algoritmo distinto y se puede intercambiar. En el refactor creó la interfaz MetodoPago y las clases concretas (PagoTarjeta, PagoPSE, PagoPayPal, PagoCripto), lo que permite agregar nuevos métodos sin modificar el código existente, cumpliendo OCP y DIP. El nuevo ProcesadorPago solo coordina y depende de la abstracción, lo que mejora mantenibilidad y extensibilidad. Lo único que faltó fue un Factory para instanciar automáticamente la estrategia correcta según el tipo de pago, pero en general el diseño propuesto es sólido y cumple con lo visto en clase.

#### 1. ¿Qué hizo bien el prompt?

Lo que hizo bien el prompt fue que dio contexto suficiente y muy claro: incluiste el código completo de ProcesadorPago, lo que permitió al modelo analizar directamente las violaciones de SOLID sin tener que imaginar nada. También pediste de forma explícita “identifica al menos dos principios SOLID violados y propone un refactoring completo usando el patrón adecuado”, lo que obligó a la IA a cubrir tanto el diagnóstico como la solución. Además, la instrucción “justifica cómo tu diseño permite agregar nuevos métodos de pago sin modificar el código existente” fue muy efectiva porque guió la respuesta hacia OCP y extensibilidad, que eran puntos clave del parcial. En resumen, lo más fuerte de tu prompt fue: dar el código completo, exigir mínimo dos principios, y pedir un refactor con justificación práctica.


#### 2. ¿Qué se puede mejorar?

Lo que le faltó a tu prompt fue ser más específico en el alcance del refactor: pediste un “refactoring completo usando el patrón adecuado”, pero no aclaraste si querías también un diagrama UML, un ejemplo de uso o incluso un Factory para instanciar las estrategias, lo cual habría guiado mejor la respuesta. Tampoco pediste que el modelo explicara explícitamente cómo se cumplen cada uno de los principios SOLID después del cambio, por eso la IA lo hizo de forma general y no tan estructurada. El LLM no entendió mal, pero por falta de claridad se limitó a Strategy y omitió la parte de creación de objetos. La respuesta no tiene errores, pero sí omisiones: no cubrió el uso de un Factory ni la relación con patrones complementarios (como Abstract Factory o Dependency Injection) que en clase se mencionan para reforzar DIP. En resumen, el prompt funcionó bien, pero pudo ser más preciso al exigir justificación principio por principio y al pedir explícitamente un mecanismo de instanciación para que el diseño quedara más completo.


#### 3. Respuesta final

La clase ProcesadorPago viola varios principios SOLID. El más evidente es el SRP, porque mezcla la lógica de todos los métodos de pago en un solo lugar. También rompe el OCP, ya que cada vez que se agrega un nuevo medio de pago hay que modificar el método procesar. Además incumple el DIP, porque depende directamente de implementaciones concretas y no de una abstracción común.

La solución es aplicar el patrón Strategy, creando una interfaz MetodoPago con el método procesar(Pago pago) y luego clases concretas como PagoTarjeta, PagoPSE, PagoPayPal y PagoCripto. El nuevo ProcesadorPago solo recibe una estrategia y la ejecuta, quedando cerrado a modificaciones y abierto a extensiones.

Con este diseño, agregar un nuevo método de pago no requiere tocar el código existente, solo crear una nueva clase que implemente la interfaz. Esto mejora la mantenibilidad, reduce el acoplamiento y facilita las pruebas. Para hacerlo aún más completo, se puede usar un Factory que instancie automáticamente la estrategia correcta según el tipo de pago, evitando que el cliente tenga que conocer las clases concretas.
