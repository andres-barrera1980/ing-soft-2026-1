# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [06]: [P06_clean_architecture]

### Estudiante
- **Nombre completo**: [Danna Gabriela RoJAS Bernal]

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **chatgpt** | [Claude / ChatGPT / Gemini / Copilot / DeepSeek / Qwen / Mistral / Otro / **Sin IA — respuesta propia**] |
| **Modelo específico** | [Ej: Claude Opus 4.5, GPT-4o, Gemini 2.5 Pro, etc. Si respondes sin IA, escribe "N/A"] |
| **me lo piden** | [Justifica en 1-3 oraciones. Si respondes sin IA, explica por qué decidiste no usar LLM para esta pregunta.] |

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
Actúa como un Arquitecto de Software Senior especializado en Clean Architecture, SOLID y diseño de sistemas empresariales con Java y Spring Boot. Necesito que expliques de forma detallada y académica el concepto de Clean Architecture propuesto por Robert C. Martin (Uncle Bob). Contexto: Estamos desarrollando "OpenLib Market", una plataforma de compra y venta de libros universitarios construida con Java 21, Spring Boot y PostgreSQL. Tu explicación debe incluir obligatoriamente: 1. Definición general de Clean Architecture. 2. Explicación de las cuatro capas: - Entidades (Entities) - Casos de Uso (Use Cases) - Adaptadores de Interfaz (Interface Adapters) - Frameworks y Drivers 3. Explicación detallada de la regla fundamental de dependencia: - ¿Qué significa que las dependencias solo pueden apuntar hacia adentro? - ¿Por qué existe esta regla? - ¿Qué problemas evita? 4. Explicación del Principio de Inversión de Dependencias (DIP) dentro de Clean Architecture: - Cómo se aplica entre capas. - Qué papel juegan las interfaces. - Cómo se desacopla la lógica de negocio de bases de datos, APIs y frameworks. 5. Comparación entre Clean Architecture y una arquitectura tradicional por capas: - Presentación - Lógica de negocio - Acceso a datos Explica ventajas, desventajas y diferencias principales. 6. Ejemplos específicos para OpenLib Market: Para cada capa indica qué componentes podrían ubicarse allí. Ejemplo: - Entidades: Libro, Carrito, Usuario, Orden. - Casos de Uso: ComprarLibro, AgregarAlCarrito, ProcesarPago. - Adaptadores: Controladores REST, DTOs, Repositorios. - Frameworks y Drivers: Spring Boot, PostgreSQL, SMTP, Redis. 7. Incluye un diagrama textual sencillo que muestre la dirección correcta de las dependencias entre capas. 8. Finaliza con una sección llamada: "Errores comunes al implementar Clean Architecture" donde menciones al menos 5 errores frecuentes. IMPORTANTE: - Sé preciso desde el punto de vista arquitectónico. - Explica los conceptos antes de mostrar ejemplos. - Utiliza OpenLib Market en todos los ejemplos. - No omitas la explicación de la regla de dependencia ni del Principio de Inversión de Dependencias.
---

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**
[Pega aquí la respuesta COMPLETA del LLM, sin editar, sin resumir.
Incluye TODO el texto, código, explicaciones que generó el LLM.

Si el LLM generó código, asegúrate de que esté correctamente formateado.
Si tuviste que hacer varias iteraciones, pega la MEJOR respuesta obtenida,
pero menciona cuántas iteraciones hiciste.]
```
. ¿Qué es Clean Architecture?

Clean Architecture es un modelo arquitectónico propuesto por Robert C. Martin cuyo objetivo principal es construir sistemas que sean:

Independientes de frameworks.
Independientes de bases de datos.
Independientes de interfaces de usuario.
Altamente mantenibles.
Fácilmente testeables.
Resistentes al cambio tecnológico.

La idea central es que las reglas de negocio de la aplicación deben estar protegidas de detalles externos como Spring Boot, PostgreSQL, APIs REST o cualquier tecnología específica.

En otras palabras:

El negocio debe poder existir aunque mañana se cambie Spring Boot por otro framework o PostgreSQL por MongoDB.

Para OpenLib Market, las reglas como:

Un carrito no puede tener más de 10 ítems distintos.
No se puede comprar un libro sin stock.
Una orden debe tener al menos un producto.

deben permanecer intactas independientemente de la tecnología utilizada.

2. Las cuatro capas de Clean Architecture

Clean Architecture se representa mediante círculos concéntricos.

Mientras más cerca del centro esté una capa, más importante es para el negocio.

Capa 1: Entidades (Entities)

Es el núcleo del sistema.

Contiene las reglas de negocio más fundamentales y estables.

Las entidades representan conceptos del dominio que existirán incluso si cambia completamente la tecnología.

Características
No conocen Spring.
No conocen bases de datos.
No conocen APIs REST.
No conocen JSON.
Contienen reglas del negocio.
Ejemplo en OpenLib Market
public class Libro {
    private Long id;
    private String titulo;
    private BigDecimal precio;
    private int stock;

    public boolean tieneStock(int cantidad) {
        return stock >= cantidad;
    }
}
Posibles entidades
Libro
Usuario
Carrito
ItemCarrito
Orden
Pago
Capa 2: Casos de Uso (Use Cases)

Aquí vive la lógica de aplicación.

Un caso de uso representa una acción que un usuario puede realizar dentro del sistema.

Los casos de uso orquestan entidades y reglas de negocio.

Características
Implementan procesos de negocio.
No dependen de frameworks.
No dependen de controladores REST.
No dependen de bases de datos concretas.
Ejemplo conceptual

Caso de uso:

AgregarLibroAlCarrito

Proceso:

Buscar libro.
Verificar stock.
Validar límite de 10 ítems.
Agregar al carrito.
Guardar cambios.
Casos de uso de OpenLib Market
AgregarAlCarrito
RemoverDelCarrito
ComprarLibro
CrearOrden
ProcesarPago
RegistrarUsuario
ConsultarCatalogo
BuscarLibro
Capa 3: Adaptadores de Interfaz (Interface Adapters)

Transforman datos entre el mundo externo y el interno.

Actúan como traductores.

Responsabilidades
Convertir JSON → Objetos de negocio.
Convertir entidades → DTOs.
Adaptar repositorios.
Adaptar APIs externas.
Ejemplo

Un controlador REST recibe:

{
  "libroId": 5,
  "cantidad": 2
}

El adaptador convierte esto en:

AgregarAlCarritoCommand

que entiende el caso de uso.

Componentes típicos
Controladores REST
@RestController
public class CarritoController {
}
DTOs
public record AgregarLibroRequest(
    Long libroId,
    int cantidad
) {}
Implementaciones de repositorios
public class LibroRepositoryAdapter
        implements LibroRepositoryPort {
}
Capa 4: Frameworks y Drivers

Es la capa más externa.

Contiene los detalles tecnológicos.

Ejemplos
Spring Boot
PostgreSQL
Redis
RabbitMQ
SMTP
Docker
Kubernetes

Esta capa es considerada reemplazable.

Ejemplo

Hoy:

Spring Boot + PostgreSQL

Mañana:

Quarkus + MongoDB

Las entidades y casos de uso no deberían cambiar.

3. Regla Fundamental de Dependencia

Esta es la regla más importante de Clean Architecture.

Definición

Las dependencias del código solo pueden apuntar hacia adentro.

Es decir:

Frameworks
    ↓
Adaptadores
    ↓
Casos de Uso
    ↓
Entidades

Nunca al contrario.

¿Qué significa realmente?

Las capas internas no deben conocer las externas.

Por ejemplo:

✅ Correcto

CarritoController
      ↓
AgregarAlCarritoUseCase

❌ Incorrecto

AgregarAlCarritoUseCase
      ↓
CarritoController
¿Por qué existe esta regla?

Porque las reglas de negocio son más importantes y más estables que la tecnología.

Las tecnologías cambian constantemente:

Frameworks
Bases de datos
APIs
Librerías

Mientras que el negocio suele mantenerse.

Problemas que evita
Acoplamiento tecnológico

Evita:

@Service
@Transactional
@Entity

dentro de las reglas de negocio.

Dependencia de base de datos

Evita:

EntityManager
JdbcTemplate

en entidades o casos de uso.

Dificultad para pruebas

Si la lógica depende directamente de Spring o PostgreSQL:

No se puede probar aisladamente.

Con Clean Architecture:

Se prueba con mocks fácilmente.
4. Principio de Inversión de Dependencias (DIP)

El DIP es uno de los principios SOLID y es fundamental en Clean Architecture.

Definición

Los módulos de alto nivel no deben depender de módulos de bajo nivel. Ambos deben depender de abstracciones.

Aplicación entre capas

Supongamos el caso de uso:

ComprarLibroUseCase

Necesita guardar una orden.

Incorrecto:

public class ComprarLibroUseCase {

    private OrdenJpaRepository repository;
}

Aquí el caso de uso depende de una implementación concreta.

Correcto
public interface OrdenRepository {
    void guardar(Orden orden);
}

Caso de uso:

public class ComprarLibroUseCase {

    private final OrdenRepository repository;
}

Ahora depende de una abstracción.

Papel de las interfaces

Las interfaces funcionan como contratos.

Ejemplo:

public interface LibroRepository {
    Optional<Libro> buscarPorId(Long id);
}

Los casos de uso conocen únicamente este contrato.

No conocen:

PostgreSQL
JPA
Hibernate
Spring Data
Desacoplamiento de tecnologías
Base de datos
LibroRepository

↓

LibroRepositoryJpa

↓

PostgreSQL
API de pagos
PagoGateway

↓

StripeAdapter

↓

Stripe API
Email
NotificacionService

↓

EmailAdapter

↓

SMTP
5. Clean Architecture vs Arquitectura Tradicional por Capas
Arquitectura Tradicional
Presentación
      ↓
Negocio
      ↓
Persistencia
      ↓
Base de Datos
Problema típico

Muchas veces la lógica termina dependiendo de:

Spring
JPA
Hibernate

Lo que genera acoplamiento.

Comparación
Aspecto	Arquitectura Tradicional	Clean Architecture
Dependencia de Frameworks	Alta	Baja
Testabilidad	Media	Alta
Mantenibilidad	Media	Alta
Acoplamiento	Alto	Bajo
Cambio de tecnología	Costoso	Más sencillo
Complejidad inicial	Baja	Más alta
Ventajas de Clean Architecture
Alta independencia tecnológica.
Fácil de probar.
Menor acoplamiento.
Mayor reutilización.
Mejor mantenibilidad.
Desventajas
Mayor número de clases.
Curva de aprendizaje más alta.
Más abstracciones.
Puede parecer excesiva para proyectos pequeños.
6. OpenLib Market: Ubicación de Componentes
Entidades

Representan el dominio.

Libro
Usuario
Carrito
ItemCarrito
Orden
Pago
Categoria
Casos de Uso

Procesos de negocio.

AgregarAlCarrito
RemoverDelCarrito
ComprarLibro
CrearOrden
ProcesarPago
RegistrarUsuario
ActualizarStock
BuscarLibro
Adaptadores de Interfaz

Traducen entre capas.

LibroController
CarritoController
UsuarioController

LibroDTO
OrdenDTO
UsuarioDTO

LibroRepositoryAdapter
PagoGatewayAdapter
EmailAdapter
Frameworks y Drivers

Infraestructura.

Spring Boot
Spring MVC
Spring Security
PostgreSQL
Hibernate
JPA
Redis
SMTP
Docker
7. Diagrama Textual de Dependencias
+--------------------------------------+
|      Frameworks y Drivers            |
| Spring Boot, PostgreSQL, Redis       |
+-------------------↓------------------+

+--------------------------------------+
|      Interface Adapters              |
| Controllers, DTOs, Adapters          |
+-------------------↓------------------+

+--------------------------------------+
|         Use Cases                    |
| ComprarLibro, AgregarAlCarrito       |
+-------------------↓------------------+

+--------------------------------------+
|          Entities                    |
| Libro, Carrito, Orden, Usuario       |
+--------------------------------------+

Dirección permitida:

EXTERIOR  --->  INTERIOR

Dirección prohibida:

INTERIOR  --->  EXTERIOR
Errores comunes al implementar Clean Architecture
1. Colocar anotaciones de Spring en entidades

Incorrecto:

@Entity
@Service
@Component

dentro del dominio.

Esto acopla la capa de negocio al framework.

2. Inyectar repositorios JPA directamente en los casos de uso

Incorrecto:

private LibroJpaRepository repository;

Debe depender de una interfaz.

3. Colocar lógica de negocio en los controladores

Incorrecto:

@RestController
public class CarritoController {
    // validaciones de negocio aquí
}

La lógica debe vivir en los casos de uso.

4. Hacer que las entidades conozcan la base de datos

Incorrecto:

Libro {
   JdbcTemplate jdbcTemplate;
}

Las entidades no deben conocer infraestructura.

5. Convertir los casos de uso en simples intermediarios

Un error frecuente es mover toda la lógica a servicios externos y dejar los casos de uso como:

controller -> service -> repository

sin reglas de negocio reales.

Los casos de uso deben contener la orquestación y aplicación de las reglas del negocio.

6. Crear interfaces para todo sin necesidad

No toda clase requiere una interfaz. Las abstracciones deben aparecer en los puntos donde existe una dependencia entre capas o una necesidad real de sustitución.

7. Confundir Clean Architecture con estructura de carpetas

Tener carpetas llamadas:

domain
application
infrastructure

no significa que el sistema siga Clean Architecture. Lo importante es respetar la regla de dependencia, mantener el dominio independiente y aplicar correctamente el Principio de Inversión de Dependencias (DIP).
---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

[Evalúa tu propio prompt, no la respuesta del LLM. ¿El contexto fue suficiente? ¿Las restricciones fueron claras? ¿El formato de salida que pediste ayudó a obtener una buena respuesta? ¿Qué parte de tu prompt fue más efectiva? Sé específico: menciona fragmentos concretos de tu prompt que funcionaron bien.]


#### 2. ¿Qué se puede mejorar?

[¿Qué le faltó a tu prompt? ¿Qué harías diferente si pudieras reformularlo? ¿El LLM entendió mal algo por falta de claridad en tu prompt? ¿La respuesta tiene errores u omisiones? ¿Qué no cubrió el LLM que tú sí sabes por lo visto en clase?]


#### 3. Respuesta final

[Escribe tu respuesta definitiva a la pregunta del parcial, integrando lo que aprendiste del LLM pero yendo más allá. Corrige errores, llena omisiones, conecta con conceptos vistos en clase. Esta es tu respuesta: demuestra que tú dominas el tema.]
