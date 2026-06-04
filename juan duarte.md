# parcial 2 juan duarte

## Pregunta 1: Identificación de principios SOLID

### Estudiante

* **Nombre completo**: Juan Duarte

---

### LLM utilizado

| Campo                           | Valor                                                                             |
| ------------------------------- | --------------------------------------------------------------------------------- |
| **Nombre del LLM**              | Gemini                                                                            |
| **Modelo específico**           | N/A                                                                               |
| **¿Por qué elegiste este LLM?** | Se utilizó Gemini para analizar las violaciones de principios SOLID en el código. |

---

### Prompt utilizado

```
Tengo el siguiente codigo y se tiene la idea de que se esta violando un principio SOLID, ayudame a identificar que principio solid se esta violando, puede haber mas de 1
```

---

### Respuesta del LLM

```
Si debemos señalar una "falla principal" de entre todas las violaciones cometidas en tu código, la respuesta es, sin duda, el Principio de Responsabilidad Única (SRP).

### ¿Por qué el SRP es la falla principal?

El SRP es el cimiento de los demás principios SOLID. Cuando una clase viola el SRP (como tu GestorLibro), genera un efecto dominó que activa automáticamente las otras fallas:

1. Causa el acoplamiento excesivo: Al encargarse de la base de datos, el correo y la indexación, la clase está obligada a conocer los detalles de implementación de servicios externos, violando el DIP (Inversión de Dependencias).

2. Impide la extensibilidad: Si intentas seguir el OCP (Abierto/Cerrado), te verás bloqueado, porque cada vez que quieras añadir una nueva funcionalidad (por ejemplo, guardar una copia en un servicio de almacenamiento de archivos), tendrás que abrir y modificar este mismo archivo, lo cual es exactamente lo que el OCP intenta evitar.

### El problema del "Efecto Cascada"

Cuando una clase hace muchas cosas, se convierte en un punto único de fallo.

* Si cambia el servidor de correos: Debes modificar GestorLibro.
* Si cambias la tecnología de base de datos: Debes modificar GestorLibro.
* Si cambias la lógica de indexación: Debes modificar GestorLibro.

La falla principal es de Diseño: Has mezclado la Lógica de Negocio (publicar un libro) con la Infraestructura (cómo conectarse a una DB, cómo enviar un SMTP, cómo conectarse a ElasticSearch).

¿Te gustaría que te muestre un ejemplo de cómo quedaría la estructura de clases si aplicamos el SRP para separar la lógica de negocio de la infraestructura?
```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

El contexto fue suficiente para que el LLM identificara más de un principio SOLID y explicara el problema principal.

#### 2. ¿Qué se puede mejorar?

Lo que se mejoraria seria en ser mas especifico con lo que esta pidiendo el enunciado.

#### 3. Respuesta final

el LLM que se esta usando mes el de gemini y efectivamente dice que si hay una clase que intenta hacer muchas cosas en una sola (personalmente yo pienso que es un problema de acoplamiento); el LLM dice que esto es un problema de principio de responsabilidad unica, donde una sola clase esta acopladaq a hacer mas de una cosa a la vez (muchas funciones) como las siguientes:

* validacion de reglas de negocio
* persistencvia de datos: conexion y ejecucion de la base de datos SQL
* Logica de formato: generacion de slugs
* integracion con servicios mexternos: envio de correos ey conexionm a elasticsearch
* manejo de infraestructura: logging

de igual manera principio de abierto o cerrado, principio de inversion de dependencias

Pero sin duda alguna la principal es el del principio de responsabilidad unica, porque es el que genera un problema en cascada

El LLM identifico correctamente el principio (mas de uno)

El LLM no fue muy especifico en cual fue el Principio SOLID mprincipal que se esta violando

```

---

## Pregunta 2: Refactoring usando un patrón de diseño

### Estudiante
- **Nombre completo**: Juan Duarte

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Gemini |
| **Modelo específico** | N/A |
| **¿Por qué elegiste este LLM?** | Se utilizó Gemini para analizar las violaciones de principios SOLID y proponer un refactoring. |

---

### Prompt utilizado

```

Por favor analiza el codigo, identifica los principios solid que se estan violando en el codigo y por favor proponga un refactoring completo usando un patron de diseÑo que considere adecuado

```

---

### Respuesta del LLM

```

Este código presenta un problema clásico de diseño que se encuentra frecuentemente cuando se escala una aplicación.

Principio de Abierto/Cerrado (OCP): Es la violación más grave.

Principio de Responsabilidad Única (SRP): La clase ProcesadorPago tiene demasiadas razones para cambiar.

Propuesta de Refactoring: Patrón Strategy

1. Definir la Interfaz (Estrategia)

public interface EstrategiaPago {
ResultadoPago procesar(Pago pago);
}

2. Implementar Estrategias Concretas

public class PagoTarjeta implements EstrategiaPago {
public ResultadoPago procesar(Pago pago) {
return new ResultadoPago(true, "Pago con tarjeta procesado");
}
}

public class PagoPSE implements EstrategiaPago {
public ResultadoPago procesar(Pago pago) {
return new ResultadoPago(true, "Pago PSE procesado");
}
}

3. Uso en el Contexto

public class ProcesadorPago {
private EstrategiaPago estrategia;

```
public ProcesadorPago(EstrategiaPago estrategia) {
    this.estrategia = estrategia;
}

public ResultadoPago ejecutar(Pago pago) {
    return estrategia.procesar(pago);
}
```

}

Cumple con OCP y SRP y mejora la testeabilidad.

```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

Permitió identificar correctamente los principios violados y obtener una propuesta de refactorización completa.

#### 2. ¿Qué se puede mejorar?

Podría haber sido más específico respecto a la explicación del patrón elegido.

#### 3. Respuesta final

Se identificcaron correctamente los principios violados y el patron elige los principio adecuado:

- definir interfaz (estrategia)

introdujo la interfaz de estrategia pago

introdujo:

EstrategiaPago

ProcesarPago

la propuesto de diseño cambia algo de codigo encapsulando su propia logica

yo dejaria el codigo asi como esta, ya que esta funcionando como deberia

---

## Pregunta 3: Violaciones ISP y LSP

### Estudiante
- **Nombre completo**: Juan Duarte

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Gemini |
| **Modelo específico** | N/A |
| **¿Por qué elegiste este LLM?** | Se utilizó para identificar violaciones de SOLID y proponer un rediseño. |

---

### Prompt utilizado

```

Este código presenta un problema clásico de diseño que se encuentra frecuentemente cuando se escala una aplicación. A continuación, analizo las violaciones y propongo una solución completa.

```

---

### Respuesta del LLM

```

Principio de Segregación de Interfaz (ISP): Esta es la violación principal.

Principio de Sustitución de Liskov (LSP): Es la violación más grave en cuanto a comportamiento.

Principio de Responsabilidad Única (SRP): La interfaz Usuario intenta definir los comportamientos de todo el sistema.

Propuesta de Refactoring

public interface Comprador {
void comprar(Libro libro);
}

public interface Vendedor {
void vender(Libro libro);
}

public interface Moderador {
void moderarComentario(Comentario comentario);
}

public interface Administrador extends Moderador {
void generarReporteVentas();
void gestionarUsuarios();
}

public class Cliente implements Comprador {
public void comprar(Libro libro) { }
}

public class VendedorImpl implements Vendedor {
public void vender(Libro libro) { }
}

public class AdministradorImpl implements Comprador, Vendedor, Administrador {
public void comprar(Libro libro) { }
public void vender(Libro libro) { }
public void moderarComentario(Comentario c) { }
public void generarReporteVentas() { }
public void gestionarUsuarios() { }
}

```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

Permitió identificar varios principios violados y obtener una propuesta de refactorización basada en interfaces más pequeñas.

#### 2. ¿Qué se puede mejorar?

Podría haber pedido una justificación más detallada del diseño propuesto.

#### 3. Respuesta final

el llm identifica sin problema los Principios violados y detecta que hay mas de lo necesario implementado, lo que hace es reducir la complejidad del codigo, el diseño es correcto usando las clases necesarias para funcionar.
```



## Pregunta 4: Selección de un patrón GOF

### Estudiante

* **Nombre completo**: Juan Duarte

---

### LLM utilizado

| Campo                           | Valor                                                                            |
| ------------------------------- | -------------------------------------------------------------------------------- |
| **Nombre del LLM**              | Gemini                                                                           |
| **Modelo específico**           | N/A                                                                              |
| **¿Por qué elegiste este LLM?** | Se utilizó para identificar el patrón GOF más adecuado y justificar su elección. |

---

### Prompt utilizado

```text
Basándote en lo anterior identifica que patrón de diseño de la gof es el más adecuado y propone una implementación en Java con clases, interfaces y ejemplos de uso. Debes justificar porqué elegiste ese patrón y descartar al menos 2 patrones que no sean compatibles
```

---

### Respuesta del LLM

```text
Para este escenario, el patrón de diseño más adecuado es el Patrón Decorator (Decorador).

¿Por qué elegir el Patrón Decorator?

A diferencia de la Estrategia (Strategy), que normalmente se usa para intercambiar algoritmos (uno u otro), el Decorator permite envolver un objeto base con comportamientos adicionales de manera acumulativa.

Implementación en Java

1. Definir la interfaz base

public interface CalculadorPrecio {
    double calcular(double precioBase);
}

2. Componente concreto (Precio base)

public class PrecioBase implements CalculadorPrecio {
    @Override
    public double calcular(double precioBase) {
        return precioBase;
    }
}

3. Decoradores base y concretos

public abstract class DescuentoDecorator implements CalculadorPrecio {
    protected CalculadorPrecio componente;

    public DescuentoDecorator(CalculadorPrecio componente) {
        this.componente = componente;
    }
}

public class DescuentoFidelidad extends DescuentoDecorator {
    public DescuentoFidelidad(CalculadorPrecio c) { super(c); }
    public double calcular(double p) { return componente.calcular(p) * 0.90; }
}

public class DescuentoTemporada extends DescuentoDecorator {
    public DescuentoTemporada(CalculadorPrecio c) { super(c); }
    public double calcular(double p) { return componente.calcular(p) * 0.85; }
}

public class DescuentoVolumen extends DescuentoDecorator {
    public DescuentoVolumen(CalculadorPrecio c) { super(c); }
    public double calcular(double p) { return componente.calcular(p) * 0.95; }
}

Strategy (Estrategia): Se descartó porque el patrón Strategy está diseñado para seleccionar un solo algoritmo a la vez.

Chain of Responsibility (Cadena de Responsabilidad): Aunque parece una opción, este patrón está pensado para pasar una petición a través de una cadena hasta que un objeto la procese o la detenga.
```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

Permitió obtener una justificación clara del patrón seleccionado y la comparación con otros patrones.

#### 2. ¿Qué se puede mejorar?

Podría haberse pedido una implementación más extensa.

#### 3. Respuesta final

el llm identifica el patron decorator, el cual considero personalmente el adecuado; descartó el patron de strategy y la cadena de responsabilidad. Y la implementacion es correcta usando precio base como componente concreto y dio correctos los ejemplos de uso

---

## Pregunta 5: Sistema de notificaciones usando dos patrones GOF

### Estudiante

* **Nombre completo**: Juan Duarte

---

### LLM utilizado

| Campo                           | Valor                                                                             |
| ------------------------------- | --------------------------------------------------------------------------------- |
| **Nombre del LLM**              | Gemini                                                                            |
| **Modelo específico**           | N/A                                                                               |
| **¿Por qué elegiste este LLM?** | Se utilizó para combinar dos patrones GOF y diseñar el sistema de notificaciones. |

---

### Prompt utilizado

```text
Podrías diseñar e implementar este sistema de notificaciones usando dos patrones de diseños de la gof combinados. Una vez lo hayas hecho, por favor haz un diagrama de clases (puede ser en texto), código en Java de las clases principales y explícame por qué elegiste esos 2 patrones
```

---

### Respuesta del LLM

```text
Para resolver este requerimiento donde un evento (libro disponible) debe desencadenar múltiples acciones independientes sin que el objeto "Libro" necesite conocer a cada uno de sus suscriptores, la combinación ideal es Observer y Strategy.

Observer (Observador): Es el patrón principal.

Strategy (Estrategia): Lo utilizaremos dentro de los observadores.

Diagrama:

+-----------------+          +-----------------------+
| LibroSujeto     |<>------->| Notificador (I)       |
+-----------------+          +-----------------------+
| - observadores  |          | + ejecutar(Libro)     |
+-----------------+          +-----------^-----------+
                                         |
         ________________________________|_______________________________
        |                |               |               |
+----------------+ +--------------+ +--------------+ +--------------+
| EmailObserver  | | PushObserver | | CacheObserver| | AuditObserver|
+----------------+ +--------------+ +--------------+ +--------------+

Implementación en Java:

interface Notificador {
    void ejecutar(String nombreLibro);
}

class Libro {
    private List<Notificador> suscriptores = new ArrayList<>();

    public void agregarSuscriptor(Notificador n) {
        suscriptores.add(n);
    }

    public void setDisponible() {
        notificar();
    }

    private void notificar() {
        for (Notificador n : suscriptores) {
            n.ejecutar(nombre);
        }
    }
}
```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

El prompt permitió obtener un diseño completo con diagrama y código.

#### 2. ¿Qué se puede mejorar?

Podría haberse solicitado un diagrama más detallado.

#### 3. Respuesta final

se eligio el patron de observer y el strategy, con su respectivo diagrama de clases; sin embargo, el diagrama quedó incompleto, pues se ve la ausensia de un observer, la implementacion es correcta y la justificacion de la combinacion también, diceindo que en observer podemos añadir funciones nuevas sin tener que modificar el codigo y que cada clase de observer es una estrategia que contiene su propia logica, esto hace que no tengamos condicionales tan largos dentro de las clases

---

## Pregunta 6: Clean Architecture

### Estudiante

* **Nombre completo**: Juan Duarte

---

### LLM utilizado

| Campo                           | Valor                                                                                                    |
| ------------------------------- | -------------------------------------------------------------------------------------------------------- |
| **Nombre del LLM**              | Gemini                                                                                                   |
| **Modelo específico**           | N/A                                                                                                      |
| **¿Por qué elegiste este LLM?** | Se utilizó para explicar las capas de Clean Architecture y compararlas con una arquitectura tradicional. |

---

### Prompt utilizado

```text
Podrías de clean arquitectura: sus capas, la regla de dependencia, el principio de inversión de dependencia aplicado en los límites entre capas y como se compara con una arquitectura en capas tradicional (presentación -> lógica -> datos). Dame ejemplos concretos de cada capa basándote en el contexto de libmarket
```

---

### Respuesta del LLM

```text
La Clean Architecture de Robert C. Martin busca la independencia de frameworks, base de datos e interfaz de usuario.

Entities:
Libro, Usuario, Carrito.

Use Cases:
PublicarLibroUseCase, CalcularDescuentoUseCase.

Interface Adapters:
ControladorLibro, RepositorioLibroSQL, DTOs.

Frameworks & Drivers:
Spring Boot, PostgreSQL, MongoDB, SMTP.

El Principio de Inversión de Dependencia se aplica definiendo interfaces en capas internas y sus implementaciones en las capas externas.

Comparativa:

Arquitectura Tradicional:
UI → Lógica → Datos

Clean Architecture:
Centralizado en el negocio y con dependencias hacia adentro.
```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

Permitió obtener una explicación general de las capas y una comparación con una arquitectura tradicional.

#### 2. ¿Qué se puede mejorar?

Se podría pedir una comparación más detallada y ejemplos más específicos de LibMarket.

#### 3. Respuesta final

todo estuvo bien, solo un pequeño detalle al momento de hacer la comparasion y es que no fue demasiado preciso al momento de comparar, solo fue un cuadro bastante comprimido y los ejemplos de libmarket no son muy precisos, da ejemp,os muy generales en cuanto al contexto

```
```

## Pregunta 7: Rediseño del módulo de pagos con Clean Architecture

### Estudiante

* **Nombre completo**: Juan Duarte

---

### LLM utilizado

| Campo                           | Valor                                                                       |
| ------------------------------- | --------------------------------------------------------------------------- |
| **Nombre del LLM**              | Gemini                                                                      |
| **Modelo específico**           | N/A                                                                         |
| **¿Por qué elegiste este LLM?** | Se utilizó para rediseñar el módulo de pagos utilizando Clean Architecture. |

---

### Prompt utilizado

```text id="w8rg7y"
por favor podrias rediseñar el modulo de pagos de openlibmarket aplicando la clean arquitecture. esto debe incluir una definicion de que va en cada capa (Entities, Use Cases, Interface Adapters, Frameworks & Drivers); tambien dibuja un diagrama de la estructura en ascii y mostrarme como se respeta la regla de dependencia y explicarme como se inyectan las dependencias externas sin afectar la arquitectura
```

---

### Respuesta del LLM

```text id="nh2dwm"
Para rediseñar el módulo de pagos de OpenLib Market bajo los principios de Clean Architecture, debemos separar la lógica de negocio pura de los detalles de implementación.

Entities:
Pago (monto, estado, método de pago).
EstadoPago (ENUM: PENDIENTE, PROCESADO, FALLIDO).

Use Cases:
ProcesarPagoUseCase.
Puertos: PaymentGateway, EmailService, PagoRepository.

Interface Adapters:
PaymentController.
PaymentRepositoryImpl.
EmailAdapter.

Frameworks & Drivers:
Spring Boot/Jakarta REST, driver JDBC, SDK de PayPal.

Diagrama ASCII:

[ Frameworks & Drivers ]
            |
[ Interface Adapters ]
            |
[ Use Cases ]
            |
[ Entities ]

La regla se respeta porque las flechas de dependencia siempre apuntan al centro.

Caso de uso:

public class ProcesarPagoUseCase {
    private final PaymentGateway gateway;
    private final EmailService email;

    public ProcesarPagoUseCase(PaymentGateway gateway, EmailService email) {
        this.gateway = gateway;
        this.email = email;
    }

    public void ejecutar(Pago pago) {
        if (gateway.cobrar(pago)) {
            email.enviarConfirmacion(pago.getComprador());
        }
    }
}

Implementación concreta:

public class PostgresPagoRepository implements PagoRepository {
    public void guardar(Pago pago) { }
}
```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

Permitió obtener la distribución de las capas y la aplicación de la regla de dependencia.

#### 2. ¿Qué se puede mejorar?

Se podría pedir una representación más completa de los adaptadores y controladores.

#### 3. Respuesta final

se ubicaron correctamente los componentres donde deben ir, todo como se muestra en el ascii

no logro ver el controlador rest, la Ia falló, sin embargo, la regla de dependencia si está bien aplicada, el diseño propuesto es mas testeable por su desacomplamiento

---

## Pregunta 8: Pruebas unitarias con JUnit 5 y Mockito

### Estudiante

* **Nombre completo**: Juan Duarte

---

### LLM utilizado

| Campo                           | Valor                                                          |
| ------------------------------- | -------------------------------------------------------------- |
| **Nombre del LLM**              | Gemini                                                         |
| **Modelo específico**           | N/A                                                            |
| **¿Por qué elegiste este LLM?** | Se utilizó para generar pruebas unitarias para CarritoService. |

---

### Prompt utilizado

```text id="bgf1zx"
podrias generar pruebas unitarias junit 5 y mockito para "carritosService", no olvides cubrir casos normales, casos borde (carrito vacío, carrito lleno con 10 ítems, intentar agregar el ítem 11, checkout con carrito vacío) y el manejo de excepciones posibles completos
```

---

### Respuesta del LLM

```text id="z9d4l9"
@ExtendWith(MockitoExtension.class)
class CarritoServiceTest {

    @Mock
    private RepositorioLibro repositorioLibro;

    private CarritoService carritoService;

    @BeforeEach
    void setUp() {
        carritoService = new CarritoService(repositorioLibro);
    }

    @Test
    void agregarItemExitoso() { }

    @Test
    void excederMaximoItems() { }

    @Test
    void checkoutVacio() { }

    @Test
    void stockInsuficiente() { }

    @Test
    void calcularTotal() { }

    @Test
    void removerItemInexistente() { }
}

Consideraciones:

@ExtendWith(MockitoExtension.class): Inicializa automáticamente los mocks.

@BeforeEach: Garantiza que el carrito esté limpio antes de cada prueba.

Se validan las excepciones y los casos borde.
```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

Permitió solicitar los casos normales, los casos borde y el manejo de excepciones.

#### 2. ¿Qué se puede mejorar?

Se podría pedir una cobertura más amplia para elementos repetidos dentro del carrito.

#### 3. Respuesta final

en efecto realizo los test completos y uso las excepciones completas esperadas, validó el checkout, no valido si un objeto ya existia y si uso correctamente el repositorio libro, el caso que hizo falta fue el de si un objeto ya estaba en el carrito

---

## Pregunta 9: Aplicación del ciclo TDD

### Estudiante

* **Nombre completo**: Juan Duarte

---

### LLM utilizado

| Campo                           | Valor                                                          |
| ------------------------------- | -------------------------------------------------------------- |
| **Nombre del LLM**              | Gemini                                                         |
| **Modelo específico**           | N/A                                                            |
| **¿Por qué elegiste este LLM?** | Se utilizó para demostrar el ciclo Red-Green-Refactor con TDD. |

---

### Prompt utilizado

```text id="x71f83"
podrias demostrar el ciclo completo de TDD (red-green-factor) para la clase de "controlinventarioservice", tambien que maneje esta logica, debe: (a) escribir primero las pruebas unitarias (JUnit 5 + Mockito) que fallen, (b) luego la implementación mínima para que pasen, y (c) hacer un refactorin que mejore el diseño sin romper las pruebas, el codigo debe ser extrictamente en java
```

---

### Respuesta del LLM

```text id="v4f4vf"
Fase RED:

@ExtendWith(MockitoExtension.class)
class ControlInventarioServiceTest {

    @Mock private LibroRepository repo;

    @Test
    void debeMarcarComoAgotadoSiStockLlegaACero() { }
}

Fase GREEN:

public class ControlInventarioService {

    private final LibroRepository repo;

    public void reducirStock(Long id, int cantidad) {
        Libro libro = repo.findById(id).orElseThrow();
        libro.setStock(libro.getStock() - cantidad);

        if (libro.getStock() == 0) {
            libro.setEstado(EstadoLibro.AGOTADO);
        }

        repo.save(libro);
    }
}

Fase REFACTOR:

public class Libro {

    private int stock;
    private EstadoLibro estado;

    public void reducirStock(int cantidad) {
        this.stock -= cantidad;
        actualizarEstado();
    }

    private void actualizarEstado() {
        this.estado = (this.stock <= 0)
                ? EstadoLibro.AGOTADO
                : EstadoLibro.DISPONIBLE;
    }
}

El servicio queda más limpio y se elimina el anemic domain model.
```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

Permitió seguir explícitamente las fases Red-Green-Refactor del TDD.

#### 2. ¿Qué se puede mejorar?

Se podrían solicitar más escenarios de prueba y una mayor complejidad.

#### 3. Respuesta final

se hizo test primero e implemntacion despues, las pruebas cubren pocos escenarios, no es tan complejom al momento de hacer pruebas, no cambió nada y simplemente hizo el codigo menos complejo eliminando el anemic domain model y está aplicando realmente el LLM, esto se hace por el orden en el que el me pide que lo implemente y como hace el codigo basandose en las pruebas

```
```
