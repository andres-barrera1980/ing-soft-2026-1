
## Pregunta [02]: [Refactorizar un módulo aplicando los principios correctos]

### Estudiante
- **Nombre completo**: Julian Felipe Rojas Almanza

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Gpt |
| **Modelo específico** | Gpt-4o |
| **¿Por qué elegiste este LLM?** | líder en reconocimiento de patrones de diseño orientados a objetos (Design Patterns), ideal para evaluar si el diseño permite la extensión sin modificación. |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```
Actúa como un Arquitecto de Software Senior y experto en Patrones de Diseño. Estoy resolviendo un ejercicio técnico académico basado en el proyecto "OpenLib Market" y necesito que respondas exactamente a las tareas solicitadas sobre el código que te presentaré.

[CONTEXTO]
La plataforma "OpenLib Market" necesita soportar múltiples métodos de pago (tarjeta de crédito, PSE, PayPal, criptomonedas). Actualmente contamos con una implementación inicial.

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

[PROBLEMA / TAREA]
Necesito que resuelvas y respondas de forma directa y explícita a los siguientes dos puntos requeridos por mi enunciado:
1. Identifica qué principios SOLID se están violando en esta clase (deben ser al menos dos) y justifica detalladamente por qué.
2. Propón un refactoring completo que corrija el diseño utilizando el patrón de diseño que consideres más apropiado para este escenario. Explica qué interfaces o clases abstractas introduces y demuestra cómo el nuevo diseño permitiría agregar un método de pago como "Criptomonedas" sin modificar el código existente.

[RESTRICCIONES]
- Chain of Thought: Antes de escribir el código final, explica conceptualmente la elección del patrón de diseño seleccionado y por qué es el adecuado para resolver las violaciones detectadas.
- El refactoring debe eliminar los bloques condicionales (if/else) basados en strings o tipos de pago.
- Las clases e interfaces resultantes deben estar preparadas para cumplir con el principio de Abierto/Cerrado (OCP).


[FORMATO DE SALIDA]
Estructura tu respuesta en Markdown utilizando única y estrictamente estos tres encabezados para organizar la información:
- ### 1. Identificación de los Principios SOLID Violados
- ### 2. Patrón de Diseño Elegido y Estrategia de Refactoring
- ### 3. Código Java Refactorizado (Incluyendo la extensión para Criptomonedas)

[VALIDACIÓN]
Sabré que tu respuesta es excelente si identificas correctamente al menos dos principios SOLID vulnerados por la estructura de control if/else, si seleccionas un patrón de diseño estándar de la industria para resolver este problema, y si demuestras con código cómo se añade el nuevo método de pago de criptomonedas de forma 100% extensible.
```

---

### Respuesta del LLM

 # Solución

### 1. Identificación de los Principios SOLID Violados

**1. Principio de Responsabilidad Única (SRP)**

La clase `ProcesadorPago` tiene varias responsabilidades al mismo tiempo:

* Decide qué método de pago utilizar.
* Procesa pagos con tarjeta.
* Procesa pagos con PSE.
* Procesa pagos con PayPal.

Si cambia la lógica de cualquiera de estos métodos, la clase debe modificarse, por lo que tiene múltiples razones para cambiar.

**2. Principio Abierto/Cerrado (OCP)**

La clase no está preparada para extenderse sin modificaciones.

Cada vez que se necesite agregar un nuevo método de pago, por ejemplo **Criptomonedas**, será necesario modificar el método `procesar()` agregando otro bloque `if/else`.

Esto aumenta el acoplamiento y el riesgo de introducir errores en código que ya funciona correctamente.

---

### 2. Patrón de Diseño Elegido y Estrategia de Refactoring

**Patrón seleccionado: Strategy**

El patrón Strategy es apropiado porque todos los métodos de pago realizan la misma acción general: **procesar un pago**, pero cada uno tiene una implementación diferente.

La estrategia consiste en:

1. Crear una interfaz común llamada `MetodoPago`.
2. Crear una clase independiente para cada método de pago.
3. Eliminar completamente los bloques `if/else`.
4. Hacer que `ProcesadorPago` trabaje únicamente con la interfaz.

**Ventajas**

* Cumple con el principio de Responsabilidad Única (SRP).
* Cumple con el principio Abierto/Cerrado (OCP).
* Facilita el mantenimiento y la extensión del sistema.
* Permite agregar nuevos métodos de pago sin modificar código existente.

---

### 3. Código Java Refactorizado (Incluyendo la extensión para Criptomonedas)

#### Interfaz común

```java
public interface MetodoPago {
    ResultadoPago procesar(Pago pago);
}
```

#### Implementación para Tarjeta

```java
public class PagoTarjeta implements MetodoPago {

    @Override
    public ResultadoPago procesar(Pago pago) {
        return new ResultadoPago(
                true,
                "Pago con tarjeta procesado"
        );
    }
}
```

#### Implementación para PSE

```java
public class PagoPSE implements MetodoPago {

    @Override
    public ResultadoPago procesar(Pago pago) {
        return new ResultadoPago(
                true,
                "Pago PSE procesado"
        );
    }
}
```

#### Implementación para PayPal

```java
public class PagoPayPal implements MetodoPago {

    @Override
    public ResultadoPago procesar(Pago pago) {
        return new ResultadoPago(
                true,
                "Pago PayPal procesado"
        );
    }
}
```

#### Clase ProcesadorPago

```java
public class ProcesadorPago {

    private final MetodoPago metodoPago;

    public ProcesadorPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public ResultadoPago procesar(Pago pago) {
        return metodoPago.procesar(pago);
    }
}
```

#### Nueva implementación para Criptomonedas

```java
public class PagoCriptomonedas implements MetodoPago {

    @Override
    public ResultadoPago procesar(Pago pago) {
        return new ResultadoPago(
                true,
                "Pago con criptomonedas procesado"
        );
    }
}
```

#### Ejemplo de uso

```java
MetodoPago metodoPago = new PagoCriptomonedas();

ProcesadorPago procesador =
        new ProcesadorPago(metodoPago);

ResultadoPago resultado =
        procesador.procesar(pago);
```

### Conclusión

La solución utiliza el patrón **Strategy** para eliminar los bloques `if/else` y separar la lógica de cada método de pago en clases independientes. De esta forma, si en el futuro se desea agregar otro método de pago, solo es necesario crear una nueva clase que implemente `MetodoPago`, sin modificar el código existente y cumpliendo el principio **Abierto/Cerrado (OCP)**.


---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

El prompt estubo bastante bueno y mejor que el anterior, siendo este mas claro. se definieron los segmentos y se definio lo pedido.


#### 2. ¿Qué se puede mejorar?

Considero que el prompt a pesar de no ser perfecto, esta bastante bien estructurado cumpliendo con los reglamientos de la guia.


#### 3. Respuesta final

 se identificó cómo los bloques condicionales si o de lo contrario rompían el principio de responsabilidad única al mezclar el control del camino del programa con las reglas de cada forma de pago, y se demostró que eso saboteaba el principio abierto cerrado al obligar a modificar el código principal con cada nuevo requerimiento como el de las criptomonedas. la separación de las opciones en clases independientes que implementan la interfaz metodopago es la forma adecuada de resolver este problema, logrando que procesadorpago dependa de una idea general y no de un código específico.

sin embargo, el análisis omitió un vacío gigante. en el ejemplo de uso, se crea el objeto a mano usando el comando para crear cajas de código nuevas justo antes de pasárselo al procesador. aunque esto funciona para el ejercicio, pues en la vida real meter una creación manual dentro del flujo de ejecución vuelve a amarrar el código de forma rígida y rompe la inversión de control, que es dejar que el sistema maneje las piezas por nosotros. para que la solución fuera perfecta, faltó mencionar que la elección de la forma de pago se debería delegar a un selector inteligente o resolverlo de forma automática usando la herramienta principal de spring, guardando las opciones por nombre para que el cliente no tenga que enterarse de qué clase exacta se está usando.

el código original estaba mal diseñado porque procesadorpago se cargaba con muchas tareas al mismo tiempo al decidir la ruta y procesar la lógica de la tarjeta, la transferencia bancaria y paypal, violando el principio de responsabilidad única. además, rompía el principio abierto cerrado porque agregar opciones nos obligaba a dañar lo que ya funcionaba metiendo más condiciones. para solucionarlo bien, se aplicó el patrón de diseño de estrategia junto al enfoque de puertos y adaptadores, definiendo la interfaz metodopago como el puente o contrato del negocio, lo que permite cumplir con el principio de inversión de dependencias al hacer que el núcleo dependa de una idea abstracta y no de detalles técnicos. las clases independientes como pagotarjeta o pagocriptomonedas actúan como los conectores externos. el procesadorpago final queda limpio y con una sola razón para cambiar, recibiendo la forma de pago desde afuera y ordenando su ejecución, pues para evitar el amarre de código en el cliente, la opción elegida no se crea de forma manual, sino que se busca automáticamente usando el motor de spring mediante una fábrica de estrategias basada en un mapa de componentes. de esta manera, el sistema no solo borra las condiciones, sino que se puede probar cada pieza por separado en experimentos de mentiras con mockito, quedando fácil de mantener, seguro y listo para recibir cualquier otra forma de pago en el futuro.