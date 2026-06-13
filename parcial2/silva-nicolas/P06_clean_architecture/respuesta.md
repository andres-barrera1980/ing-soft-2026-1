## Pregunta 6

### Estudiante
- **Nombre completo**: Nicolas Silva García

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | **Sin IA — respuesta propia** |
| **Modelo específico** | [N/A] |
| **¿Por qué elegiste este LLM?** | [No use LLM para esta pregunta porque siento confianza en lo que aprendi del tema] |

---

### P.6 ⭐ (4 puntos)

Clean Architecture (Robert C. Martin) propone una organización en capas concéntricas con una regla fundamental: **las dependencias solo pueden apuntar hacia adentro**. Las capas son: Entidades (Entities), Casos de Uso (Use Cases), Adaptadores de Interfaz (Interface Adapters), y Frameworks y Drivers.



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

---

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**
[Pega aquí la respuesta COMPLETA del LLM, sin editar, sin resumir.
Incluye TODO el texto, código, explicaciones que generó el LLM.

Si el LLM generó código, asegúrate de que esté correctamente formateado.
Si tuviste que hacer varias iteraciones, pega la MEJOR respuesta obtenida,
pero menciona cuántas iteraciones hiciste.]


---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

No aplica, esta respuesta es mia propia.



#### 2. ¿Qué se puede mejorar?

No aplica, esta respuesta es mia propia.



#### 3. Respuesta final

Clean Architecture se basa en el principio de que las dependencias deben ir hacia adentro del codigo, lo que implica que una capa de adentro no conoce nada de las clases de afuera. Por ejemplo, un objeto de dominio no sabe que existe una base de datos, ni un controlador REST, ni un framework Spring Boot. Esta regla garantiza que si se cambia algun factor grande del exterior del codigo, como por ejemplo, la base de datos de PostgreSQL a MongoDB, la logica de negocio no sufre ni una sola modificación.

Dentro de OpenLib Market funciona de la siguiente manera

Entidades y reglas de negocio

Aca se define la logica interna del sistema, y las reglas de cada una de las clases, que siguen vigentes sin importar como se maneje el sistema desde afuera. Su responsabilidad es contener el estado y las reglas invariables del negocio.

Ejemplo en OpenLib: La clase Libro valida que un ISBN sea unico, o la clase Usuario contiene la lógica matemática para saber si es un cliente "Fiel" según su historial. No tienen anotaciones de base de datos ni de JSON.

Casos de Uso
Aca se organizan los flujos de los datos hacia y desde las entidades, definiendo lo que hace el sistema. Su responsabilidad es ejecutar los pasos lógicos específicos de la aplicacion. 

Ejemplo en OpenLib: La clase PublicarLibroUseCase valida los datos del libro, genera un slug (regla de negocio), pide que se guarde el libro y pide que se notifique al vendedor.

Adaptadores de Interfaz
Funcionan como traductores que agarran los datos en el formato de los casos de uso y los transforman al formato conveniente para los agentes externos y viceversa.

Ejemplo en OpenLib: LibroController recibe un JSON (HTTP), lo convierte en un DTO y se lo pasa al Caso de Uso.

Frameworks y Drivers (Detalles)
Es la capa mas externa. Aca se implementan las herramienta de acceso al sistema. Su responsabilidad es proveer la infraestructura física o de librerías.

Ejemplo en OpenLib: El motor de la BD o el framework Spring Boot,

Tambien es importante mencionar el Principio de Inversion de Dependencias (DIP)

El principio de Inversion de Dependencias es fundamental para que la arquitectura funcione correctamente. Este principio establece que los módulos de alto nivel no deben depender de los módulos de bajo nivel. Ambos deben depender de abstracciones. Las abstracciones no deben depender de los detalles. Los detalles deben depender de las abstracciones.


Por poner un ejemplo, si las dependencias apuntan hacia adentro, como hace el Caso de Uso (PublicarLibroUseCase - capa interior) para guardar el libro en la BD sin depender de ella?

El procedimiento mediante DIP:

En el use case se define una interfaz (ej: ILibroRepository con un método guardar(Libro libro)).

El Caso de Uso llama a ILibroRepository.guardar().

En la capa de adaptadores se implementa la interfaz (ej: PostgresLibroRepository implements ILibroRepository).

El Caso de Uso logra escribir en la base de datos sin saber que es PostgreSQL.