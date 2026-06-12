# Pregunta P06: Clean Architecture — Análisis de capas y regla de dependencia

### Estudiante
- **Nombre completo**: Juan Camilo Gomez

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Sin IA — respuesta propia |
| **Modelo específico** | N/A |
| **¿Por qué elegiste este LLM?** para ganarme el bono

---

### Análisis crítico de la respuesta

#### 3. Respuesta final

## Clean Architecture: capas, regla de dependencia y comparación con arquitectura tradicional

### Las cuatro capas

1. Entities 
Contienen las reglas de negocio empresariales mas generales. Son independientes de cualquier framework, base de datos o interfaz. En OpenLib Market: `Libro`, `Usuario`, `Carrito`, `Orden`, `Pago`. Estas clases existen incluso si el sistema no tiene base de datos ni API.

2. Use Cases 
Contienen la logica de negocio específica de la aplicacion. Orquestan el flujo de datos hacia y desde las entidades. En OpenLib Market: `PublicarLibroUseCase`, `ProcesarPagoUseCase`, `AgregarAlCarritoUseCase`. 

3. Interface Adapters 
Convierten datos entre el formato más conveniente para los casos de uso y el formato más conveniente para agentes externos . En OpenLib Market: `LibroController` , `LibroRepositorioPostgres`, `LibroPresenter` . Aquí viven los Controllers, Gateways y Presenters.

4. Frameworks & Drivers 
La capa más externa. Frameworks, bases de datos, servidores web. En OpenLib Market: Spring Boot, PostgreSQL, Elasticsearch, JavaFX, EmailService. Esta capa es la más propensa a cambiar.

---

### La Regla de Dependencia

Las dependencias en el código fuente solo pueden apuntar hacia adentro. Nada en una capa interna puede conocer algo de una capa externa.


Esto significa:
- PublicarLibroUseCase nunca importa una clase de Spring Boot ni de JDBC.
- Libro no conoce ni LibroController ni LibroRepositorioPostgres.
- LibroRepositorioPostgres depende de una interfaz RepositorioLibro definida en la capa de Use Cases, no al revés.

---

### Inversión de Dependencia en los límites entre capas

En el límite entre Use Cases e Interface Adapters, la regla de dependencia parece generar un problema: el caso de uso necesita guardar datos , pero el repositorio está en una capa externa. La solución es el Dependency Inversion Principle

El flujo de control va de afuera hacia adentro, pero la dependencia de código fuente va de afuera hacia adentro también: LibroRepositorioPostgres depende de RepositorioLibro, que está en Use Cases. Use Cases NO conoce a LibroRepositorioPostgres.

---

### Comparación con arquitectura en capas tradicional

| Aspecto | Arquitectura tradicional | Clean Architecture |

| **Capas** | Presentación → Lógica → Datos | Entities → Use Cases → Adapters → Frameworks |
| **Dirección dependencias** | Cualquier direccion | Siempre hacia adentro  |
| **Base de datos** | Centro del diseño (se diseña el schema primero) | Detalle de implementación (capa más externa) |
| **Testeabilidad** | Difícil  | Alta |
| **Cambio de BD** | Afecta multiples capas | Solo afecta la implementación del repositorio |
| **Frameworks** | Fundamentales  | Reemplazables sin tocar la logica de negocio |

