## P06_clean_architecture

### Estudiante
- **Nombre completo**: [Alejandro Macias Barrios]

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [**Sin IA — respuesta propia**] |
| **Modelo específico** | ["N/A"] |
| **¿Por qué elegiste este LLM?** | [Clean Architecture es un tema conceptual central del curso; respondiendo sin IA se puede demostrar dominio real y optar al bono +20%. La explicación propia permite conectar con los conceptos discutidos en clase de forma más auténtica.] |


### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?
[No usó]

#### 2. ¿Qué se puede mejorar?
[¿No usó]

#### 3. Respuesta final
[Las cuatro capas de Clean Architecture son: Entities, Use Cases, Interface Adapters y Frameworks & Drivers.
1. Entidades
Contienen las reglas de negocio empresariales: las más estables, las que menos cambian. Son objetos de dominio puro, sin dependencia alguna de frameworks, bases de datos ni HTTP.
2. Casos de Uso
Contienen las reglas de negocio específicas de la aplicación: orquestan el flujo entre entidades para cumplir un objetivo concreto del sistema.
3. Adaptadores de Interfaz
Convierten datos entre el formato que usan los casos de uso y el formato que esperan los frameworks externos. Aquí viven los controladores REST, los presentadores, y los repositorios (implementación).
4. Frameworks & Drivers
La capa más externa: bases de datos, frameworks web, librerías de UI. Son detalles que pueden cambiarse sin afectar las capas internas.

La regla de dependencia
Las dependencias del código fuente solo pueden apuntar hacia adentro. Una clase en la capa de Frameworks puede depender de Interface Adapters, pero nunca al revés.
Esto significa que PublicarLibroUseCase puede depender de LibroRepositorio (interfaz), pero nunca de LibroRepositorioPostgres (implementación). Libro (entidad) no importa nada de Spring, JPA, ni HTTP. LibroController depende de PublicarLibroUseCase, no al revés.

La gran diferencia en arquitectura tradicional, la base de datos es la capa inferior y la lógica depende de ella. En Clean Architecture, la base de datos es un detalle intercambiable, y la lógica de negocio no sabe que existe.]