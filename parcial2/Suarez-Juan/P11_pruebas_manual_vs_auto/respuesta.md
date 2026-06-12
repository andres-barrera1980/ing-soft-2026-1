# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [11]: Pruebas Manuales Automaticas

### Estudiante
- **Nombre completo**: Juan Pablo Suarez Moreno


### Análisis crítico de la respuesta

#### 1. Pruebas manuales

Tienen ventajas principales que permiten detectar problemas de usabilidad y experiencia de usuario que un script jamas captaria, son en parte flexibles para explorar flujos inesperados sin tener un caso definido de antemano, y no requieren inversion inicial en infraestructura ni conocimiento tecnico avanzado, pero entre sus desventajas son que son lentas y no escalan bien cuando el proyecto crece, son propensas a errores humanos ya sea por fatiga o hasta algun descuido, y son costosas a largo plazo. Los tipos de pruebas que aplican aqui son pruebas exploratorias, pruebas de usabilidad, pruebas de aceptacion del usuario y pruebas para medir hasta el hoc. Para el proyecto OpenLib Market puede ser bueno hacer manual todo lo relacionado con la experiencia de busqueda y navegacion del catalogo, el flujo de compra desde la perspectiva del usuario, y cualquier pantalla nueva que no haya sido probada antes para detectar problemas de diseno antes de automatizar.

#### 2. Pruebas Automatizadas

Las pruebas automatizadas por su lado tienen la ventaja de que se pueden correr rapidamente, son consistentes y no cometen errores por cansancio como los humanos, y a largo plazo pueden reducir el costo de regresion significativamente, sus desventajas son que tienen un costo alto de implementacion inicial, y no detectan bien problemas subjetivos de la expereiencia de Usuario o pues panatallas taan bien como las manuales. Aplican especialmente para pruebas unitarias, pruebas de integracion, pruebas de regresion y pruebas de carga. El criterio clave para decidir cuando automatizar es si la prueba se va a repetir muchas veces y es poco probable que cambie pronto alguna logica del negocio. Para OpenLib Market se deberian automatizar el login y autenticacion, la logica de prestamo y devolucion de libros, las APIs del catalogo, ya que son flujos usados y repetitivos que no deberian fallar en produccion.
