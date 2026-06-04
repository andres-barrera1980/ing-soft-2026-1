# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [XX]: [Título resumido]

### Estudiante
- **Nombre completo**: [Tu nombre y apellido]

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [Claude / ChatGPT / Gemini / Copilot / DeepSeek / Qwen / Mistral / Otro / **Sin IA — respuesta propia**] |
| **Modelo específico** | [Ej: Claude Opus 4.5, GPT-4o, Gemini 2.5 Pro, etc. Si respondes sin IA, escribe "N/A"] |
| **¿Por qué elegiste este LLM?** | [Justifica en 1-3 oraciones. Si respondes sin IA, explica por qué decidiste no usar LLM para esta pregunta.] |

---

### Prompt utilizado

Eres un arquitecto de software experto en patrones de diseño (GoF), principios SOLID y desarrollo orientado a objetos en Java.

Analiza cuidadosamente el siguiente caso:

OpenLib Market necesita aplicar diferentes estrategias de descuento durante el año:

Descuento por fidelidad: 10% para compradores con más de 5 compras.
Descuento por temporada: 15% durante la semana del libro.
Descuento por volumen: 5% si el valor del carrito supera los $200.000 COP.
Sin descuento: se mantiene el precio original.

Requisitos importantes:

Los descuentos pueden combinarse entre sí.
Un cliente puede recibir uno, varios o todos los descuentos simultáneamente.
El cálculo debe realizarse dinámicamente en tiempo de ejecución.
Debe ser posible agregar nuevos tipos de descuento en el futuro sin modificar el código existente.
Debes identificar cuál de los patrones de diseño vistos en clase es el MÁS adecuado para resolver este problema.
Justifica detalladamente tu elección considerando:
Flexibilidad.
Escalabilidad.
Mantenibilidad.
Posibilidad de combinar descuentos.
Cumplimiento de los principios SOLID.
Descarta explícitamente al menos dos patrones de diseño alternativos, explicando por qué NO son la mejor opción para este escenario.
Por ejemplo, analiza si Strategy, Factory Method, Template Method, Chain of Responsibility o cualquier otro patrón sería adecuado o no.
Diseña una solución completa en Java que incluya:
Interfaces.
Clases concretas.
Relaciones entre clases.
Explicación de cada componente.
Código completamente funcional y compilable.
Incluye un diagrama textual de la estructura de clases.
Muestra un ejemplo de ejecución donde:
El carrito tenga un valor de $300.000 COP.
El usuario tenga más de 5 compras.
Sea la semana del libro.
Se apliquen todos los descuentos posibles.
Explica paso a paso cómo se calcula el precio final.
Analiza qué principios SOLID se cumplen en la solución y cómo contribuyen a la calidad del diseño.
Finalmente, indica si existe alguna limitación de la solución propuesta y cómo podría mejorarse en una versión futura.

La respuesta debe ser técnica, detallada, argumentada y enfocada en justificar la selección del patrón más apropiado.
```

---

### Respuesta del LLM

Análisis del Problema

OpenLib Market necesita aplicar descuentos que:

Se calculan dinámicamente.
Pueden combinarse.
Pueden agregarse nuevos descuentos sin modificar código existente.
Deben respetar SOLID.
Deben ser fáciles de mantener y extender.

Los descuentos disponibles son:

Descuento	Condición
Fidelidad	Más de 5 compras
Temporada	Semana del libro
Volumen	Carrito > $200.000
Sin descuento	Caso base
Selección del Patrón de Diseño
Patrón Elegido: Decorator (Decorador)

El patrón Decorator es el más adecuado porque permite agregar comportamientos (descuentos) de manera dinámica y combinable sobre un objeto base.

¿Por qué Decorator?

Cada descuento modifica el precio calculado por el descuento anterior.

Ejemplo:

Precio Base
    ↓
Descuento Fidelidad
    ↓
Descuento Temporada
    ↓
Descuento Volumen
    ↓
Precio Final

Los descuentos se pueden apilar dinámicamente.

Esto coincide exactamente con el propósito del patrón Decorator.

Justificación
1. Flexibilidad

Permite aplicar:

Fidelidad

o

Fidelidad + Temporada

o

Fidelidad + Temporada + Volumen

sin crear clases específicas para cada combinación.

2. Escalabilidad

Si mañana aparece:

Descuento por cumpleaños

simplemente creamos:

DescuentoCumpleanios

sin tocar ninguna clase existente.

3. Mantenibilidad

Cada descuento tiene su propia clase.

No existe una clase gigante llena de:

if(...)
else if(...)
else if(...)
4. Cumplimiento OCP

El sistema queda:

Cerrado para modificación.
Abierto para extensión.
Patrones Descartados
Strategy

Podría parecer una opción válida.

Ejemplo:

interface EstrategiaDescuento

Problema:

Una estrategia normalmente reemplaza otra.

Tarjeta
O
PayPal
O
PSE

Pero aquí necesitamos:

Fidelidad + Temporada + Volumen

simultáneamente.

Strategy no modela naturalmente la combinación de múltiples descuentos.

Factory Method

Factory sirve para crear objetos.

Ejemplo:

DescuentoFactory

Puede ayudar a construir descuentos.

Pero no resuelve:

Aplicar múltiples descuentos acumulados

Por tanto no es el patrón principal.

Template Method

Template Method define pasos fijos de un algoritmo.

Aquí los descuentos:

Son opcionales.
Son combinables.
Cambian dinámicamente.

No existe una secuencia fija.

Por tanto no es apropiado.

Chain of Responsibility

Puede encadenar descuentos.

Sin embargo:

Está pensado para delegar solicitudes.
No para decorar progresivamente un resultado.

Decorator representa mejor la acumulación de descuentos.

Diseño Propuesto
Clase Contexto
public class Cliente {

    private int comprasRealizadas;

    public Cliente(int comprasRealizadas) {
        this.comprasRealizadas = comprasRealizadas;
    }

    public int getComprasRealizadas() {
        return comprasRealizadas;
    }
}
Carrito
public class Carrito {

    private double valor;

    public Carrito(double valor) {
        this.valor = valor;
    }

    public double getValor() {
        return valor;
    }
}
Componente Base
public interface Descuento {

    double calcularPrecio();
}
Implementación Sin Descuento
public class PrecioBase implements Descuento {

    private double precio;

    public PrecioBase(double precio) {
        this.precio = precio;
    }

    @Override
    public double calcularPrecio() {
        return precio;
    }
}
Decorador Abstracto
public abstract class DescuentoDecorator
        implements Descuento {

    protected Descuento descuento;

    public DescuentoDecorator(
            Descuento descuento) {

        this.descuento = descuento;
    }
}
Descuento Fidelidad
public class DescuentoFidelidad
        extends DescuentoDecorator {

    public DescuentoFidelidad(
            Descuento descuento) {

        super(descuento);
    }

    @Override
    public double calcularPrecio() {

        return descuento.calcularPrecio()
                * 0.90;
    }
}
Descuento Temporada
public class DescuentoTemporada
        extends DescuentoDecorator {

    public DescuentoTemporada(
            Descuento descuento) {

        super(descuento);
    }

    @Override
    public double calcularPrecio() {

        return descuento.calcularPrecio()
                * 0.85;
    }
}
Descuento Volumen
public class DescuentoVolumen
        extends DescuentoDecorator {

    public DescuentoVolumen(
            Descuento descuento) {

        super(descuento);
    }

    @Override
    public double calcularPrecio() {

        return descuento.calcularPrecio()
                * 0.95;
    }
}
Diagrama de Clases
                    +------------------+
                    |    Descuento     |
                    +------------------+
                    | calcularPrecio() |
                    +---------+--------+
                              ^
                              |
                 +------------+------------+
                 |                         |
                 |                         |
      +-------------------+    +----------------------+
      |    PrecioBase     |    | DescuentoDecorator   |
      +-------------------+    +----------------------+
      | calcularPrecio()  |    | descuento            |
      +-------------------+    +----------+-----------+
                                          ^
                    ----------------------------------------
                    |                |                    |
                    |                |                    |
      +------------------+ +----------------+ +----------------+
      | Fidelidad        | | Temporada      | | Volumen        |
      +------------------+ +----------------+ +----------------+
      | calcularPrecio() | | calcularPrecio()| | calcularPrecio()|
      +------------------+ +----------------+ +----------------+
Ejemplo de Ejecución
Datos
Valor carrito = $300.000
Compras realizadas = 8
Semana del libro = Sí

Aplican:

✅ Fidelidad 10%

✅ Temporada 15%

✅ Volumen 5%

Construcción dinámica
public class Main {

    public static void main(String[] args) {

        Descuento descuento =
            new PrecioBase(300000);

        descuento =
            new DescuentoFidelidad(
                descuento);

        descuento =
            new DescuentoTemporada(
                descuento);

        descuento =
            new DescuentoVolumen(
                descuento);

        System.out.println(
            descuento.calcularPrecio()
        );
    }
}
Cálculo Paso a Paso
Precio inicial
300.000
Fidelidad (-10%)
300.000 × 0.90

270.000
Temporada (-15%)
270.000 × 0.85

229.500
Volumen (-5%)
229.500 × 0.95

218.025
Precio Final
$218.025 COP
Principios SOLID Cumplidos
SRP

Cada clase tiene una única responsabilidad.

DescuentoFidelidad

solo aplica fidelidad.

DescuentoVolumen

solo aplica volumen.

OCP

Agregar un descuento nuevo:

class DescuentoCumpleanios

No modifica ninguna clase existente.

LSP

Todos los decoradores pueden reemplazar:

Descuento

sin romper comportamiento.

ISP

La interfaz es pequeña:

double calcularPrecio();

No obliga a implementar métodos innecesarios.

DIP

El código depende de:

Descuento

(abstracción)

y no de clases concretas.

Limitaciones de la Solución
1. Orden de Aplicación

Actualmente:

Fidelidad
→ Temporada
→ Volumen

produce un resultado.

Otro orden podría producir un valor distinto.

Ejemplo:

Volumen
→ Fidelidad
→ Temporada
Mejora Futura

Crear un:

MotorDescuentos

que defina explícitamente el orden de ejecución.

2. Demasiados Decoradores

Si el negocio llega a tener:

20 descuentos

la cadena podría crecer mucho.

Mejora Futura

Combinar Decorator con:

Factory
Configuración externa
Motor de reglas (Rule Engine)

para construir automáticamente la cadena.

Conclusión

El patrón Decorator es la mejor solución para OpenLib Market porque permite combinar descuentos dinámicamente, agregar nuevos descuentos sin modificar código existente, cumplir los principios SOLID y mantener una arquitectura extensible y mantenible. Aunque Strategy y Chain of Responsibility podrían utilizarse parcialmente, Decorator representa de forma más natural el problema de aplicar múltiples descuentos acumulativos sobre un mismo precio, convirtiéndose en la alternativa más adecuada para este escenario.
```

---

### Análisis crítico de la respuesta
La respuesta del LLM identifica correctamente el patrón Decorator como la mejor solución, ya que permite combinar descuentos de forma dinámica y agregar nuevos descuentos sin modificar el código existente, cumpliendo además varios principios SOLID como OCP, SRP y DIP. También considera adecuadamente la composición de descuentos y descarta patrones como Strategy, Factory Method y Chain of Responsibility, aunque estos podrían adaptarse al problema con mayor complejidad. La implementación propuesta es funcional y refleja correctamente la estructura del patrón Decorator; sin embargo, presenta una limitación importante: no valida las condiciones reales del negocio, ya que aplica todos los descuentos sin verificar si el cliente cumple los requisitos de fidelidad, si es la semana del libro o si el carrito supera los $200.000 COP. En general, la respuesta es correcta y bien fundamentada, pero podría mejorar incorporando dichas validaciones y automatizando la construcción de la cadena de descuentos.

#### 1. ¿Qué hizo bien el prompt?
El prompt fue efectivo porque proporcionó suficiente contexto sobre el problema y definió claramente los requisitos que debía cumplir la solución. En particular, la instrucción de que “los descuentos pueden combinarse entre sí” ayudó a que el LLM identificara la necesidad de un patrón que permitiera composición dinámica, mientras que la solicitud explícita de “descartar al menos dos patrones alternativos y justificar por qué no son adecuados” permitió obtener una respuesta más crítica y argumentada. Además, pedir una implementación completa en Java con interfaces, clases, ejemplo de uso y análisis de principios SOLID ayudó a estructurar la respuesta y evitar explicaciones superficiales. La parte más efectiva del prompt fue exigir la justificación de la elección del patrón junto con el análisis de alternativas, ya que esto obligó al modelo a razonar sobre el problema en lugar de limitarse a proponer una solución.


#### 2. ¿Qué se puede mejorar?

El prompt podría mejorarse especificando que la implementación debe validar explícitamente las condiciones de negocio para aplicar cada descuento, ya que el LLM propuso una solución donde todos los descuentos se aplican siempre. También habría sido útil solicitar que las clases Cliente y Carrito participaran directamente en el cálculo del precio final para representar mejor el escenario planteado. Aunque la respuesta fue correcta y eligió adecuadamente el patrón Decorator, no profundizó en cómo automatizar la construcción de la cadena de descuentos ni analizó con suficiente detalle alternativas como Strategy o Chain of Responsibility, que también podrían adaptarse al problema. Si reformulara el prompt, añadiría requisitos más específicos sobre la validación de condiciones y el comportamiento dinámico esperado para obtener una solución más cercana a una implementación real.



#### 3. Respuesta final

El patrón más adecuado para este escenario es Decorator, ya que permite aplicar y combinar múltiples descuentos de forma dinámica sobre un mismo precio sin crear clases para cada combinación posible. Cada descuento se implementa como un decorador que modifica el resultado anterior y valida su condición correspondiente (fidelidad, temporada o volumen). Esta solución cumple los principios SOLID, especialmente SRP y OCP, porque cada descuento tiene una única responsabilidad y se pueden agregar nuevos descuentos sin modificar el código existente. Aunque patrones como Strategy o Factory Method podrían participar en la solución, Decorator representa mejor la necesidad de acumular descuentos de manera flexible y extensible, por lo que es la alternativa más adecuada para OpenLib Market.