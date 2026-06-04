# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [7]: [clean_architecture_pagos]

### Estudiante
- **Nombre completo**: Boris Nicolas Valbuena Gueirsman

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Gemini |
| **Modelo específico** | Gemini 3.5 Flash |
| **¿Por qué elegiste este LLM?** | Es la que más usé durante el curso, por lo cual es la misma que usaré en el parcial. |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```
Actúa como un arquitecto de software senior especializado en Clean Architecture, Java 21, Spring Boot 3.x, PostgreSQL y diseño orientado a dominios.
Contexto:
Estoy trabajando en OpenLib Market, una plataforma de compra y venta de libros.
El módulo de pagos debe soportar:

Procesar pagos mediante tarjeta de crédito.
Procesar pagos mediante PSE.
Procesar pagos mediante PayPal.
Registrar transacciones en PostgreSQL.
Notificar por correo al comprador.
Notificar por correo al vendedor.
Exponer una API REST consumida por un frontend JavaFX.
Actualmente existe una clase PaymentService con un único método realizarPago() que:

Invoca la pasarela de pago.
Actualiza la orden.
Guarda la transacción.
Envía correos.
Prepara la entrega o descarga.
Necesito rediseñar completamente el módulo utilizando Clean Architecture.
Antes de responder:

Analiza los problemas de la implementación actual.
Explica qué componentes pertenecen a cada capa.
Justifica la ubicación de cada componente.
Explica cómo se respeta la regla de dependencia.
Explica cómo se realiza la inyección de dependencias sin violar la arquitectura.
Evalúa la testabilidad de la solución.
Restricciones:

Utiliza Java 21.
Sigue principios SOLID.
Sigue estrictamente Clean Architecture.
No permitas dependencias desde capas internas hacia frameworks externos.
El dominio no debe depender de Spring Boot.
El dominio no debe depender de PostgreSQL.
El dominio no debe depender de proveedores de pago.
Formato de salida:

Problemas del diseño actual
Entities
Use Cases
Interface Adapters
Frameworks & Drivers
Diagrama de arquitectura (Mermaid)
Flujo de procesamiento de pago
Regla de dependencia
Inyección de dependencias
Ventajas frente al diseño original
Ejemplo de estructura de paquetes
Validación:
La solución debe respetar completamente la regla de dependencia de Clean Architecture y permitir cambiar la base de datos, el proveedor de pagos o el framework web sin modificar las capas internas.
```

---

### Respuesta del LLM

> Actúa como un arquitecto de software senior especializado en Clean Architecture, Java 21, Spring Boot 3.x, PostgreSQL y diseño orientado a dominios.
Contexto:
Estoy trabajando en OpenLib Market, una plataforma de compra y venta de libros.
El módulo de pagos debe soportar:

Procesar pagos mediante tarjeta de crédito.
Procesar pagos mediante PSE.
Procesar pagos mediante PayPal.
Registrar transacciones en PostgreSQL.
Notificar por correo al comprador.
Notificar por correo al vendedor.
Exponer una API REST consumida por un frontend JavaFX.
Actualmente existe una clase PaymentService con un único método realizarPago() que:

Invoca la pasarela de pago.
Actualiza la orden.
Guarda la transacción.
Envía correos.
Prepara la entrega o descarga.
Necesito rediseñar completamente el módulo utilizando Clean Architecture.
Antes de responder:

Analiza los problemas de la implementación actual.
Explica qué componentes pertenecen a cada capa.
Justifica la ubicación de cada componente.
Explica cómo se respeta la regla de dependencia.
Explica cómo se realiza la inyección de dependencias sin violar la arquitectura.
Evalúa la testabilidad de la solución.
Restricciones:

Utiliza Java 21.
Sigue principios SOLID.
Sigue estrictamente Clean Architecture.
No permitas dependencias desde capas internas hacia frameworks externos.
El dominio no debe depender de Spring Boot.
El dominio no debe depender de PostgreSQL.
El dominio no debe depender de proveedores de pago.
Formato de salida:

Problemas del diseño actual
Entities
Use Cases
Interface Adapters
Frameworks & Drivers
Diagrama de arquitectura (Mermaid)
Flujo de procesamiento de pago
Regla de dependencia
Inyección de dependencias
Ventajas frente al diseño original
Ejemplo de estructura de paquetes
Validación:
La solución debe respetar completamente la regla de dependencia de Clean Architecture y permitir cambiar la base de datos, el proveedor de pagos o el framework web sin modificar las capas internas.
```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

El prompt da muy buen contexto sobre el problema, ya que explica de forma clara que funcionalidades debe tener el modulo de pagos y detalla las responsabilidades que hoy estan concentradas en `PaymentService`. Ademas, pide directo usar Clean Architecture y solicita describir cada capa (Entities, Use Cases, Interface Adapters y Frameworks & Drivers), lo que ayuda a organizar bien la respuesta.

Tambien sirvieron bastante restricciones como que el dominio no dependa de Spring Boot, que no dependa de PostgreSQL y que la solucion respete del todo la regla de dependencia, porque obligan a que sea una arquitectura de verdad desacoplada.

Otra cosa efectiva fue pedir el diagrama de arquitectura y la explicacion de como inyectar dependencias. Asi la respuesta no se quedo solo en teoria, sino que mostro como se haria en la practica en un proyecto real.

#### 2. ¿Qué se puede mejorar?

Aunque el prompt esta completo, podria pedir de frente ejemplos de codigo para cada capa importante. La respuesta explica la arquitectura, pero algunos componentes se quedan muy por encima y no se ve con detalle como implementar los casos de uso o las entidades del dominio.

Tambien habria sido util pedirle que maneje los multiples metodos de pago con algun patron de diseño especifico, como Strategy, porque el sistema debe soportar tarjeta, PSE y PayPal. El LLM lo menciona por encima con adaptadores y gateways, pero no entra en detalle en esa parte.

Otra mejora seria pedirle ejemplos de pruebas unitarias para ver la ventaja real de esta arquitectura. El modelo habla de testabilidad, pero no muestra como hacer los tests.

#### 3. Respuesta final

La implementacion original tiene mucho acoplamiento porque una sola clase `PaymentService` junta la logica de negocio, la persistencia, la integracion con pasarelas de pago, el envio de correos y la preparacion de las entregas. Esto rompe el principio de responsabilidad unica y hace que mantener y probar el codigo sea muy dificil.

Si aplicamos Clean Architecture, dividimos el sistema en cuatro capas:

* **Entities:** Tienen las reglas de negocio mas importantes y los objetos de dominio como `Payment`, `Transaction`, `Money` y `Order`.
* **Use Cases:** Coordinan el flujo de pago con casos de uso como `ProcessPaymentUseCase` y definen interfaces como `PaymentRepository`, `PaymentGateway` y `NotificationService`.
* **Interface Adapters:** Aca van los controladores REST, los adaptadores de los repositorios y los adaptadores para las pasarelas y servicios de mail.
* **Frameworks & Drivers:** Contienen Spring Boot, PostgreSQL, los SDK de PayPal o PSE, JavaMail y cualquier tecnologia externa.

La regla de dependencia se cumple porque las capas internas no saben nada de la infraestructura. Los casos de uso dependen solo de las interfaces, y las implementaciones reales estan en las capas mas externas.

El controlador REST queda libre de logica de negocio, solo recibe peticiones y las pasa al caso de uso. De esta manera, si a futuro cambiamos Spring Boot por otro framework o PostgreSQL por otra base de datos, el codigo interno no se toca.

Para soportar multiples metodos de pago de forma extensible, podemos meter el patron Strategy en el diseño, donde cada metodo de pago implemente una estrategia distinta (`TarjetaPaymentStrategy`, `PsePaymentStrategy`, `PayPalPaymentStrategy`). Asi, agregar nuevos metodos de pago no nos obliga a modificar los casos de uso que ya tenemos.

Comparado con lo que habia antes, este diseño es mucho mas mantenible, facil de extender y probar, porque nos deja hacer pruebas unitarias usando mocks de las interfaces sin tener que conectarnos a bases de datos o servicios externos reales.
