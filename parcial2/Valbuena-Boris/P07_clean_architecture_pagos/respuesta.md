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

[Evalúa tu propio prompt, no la respuesta del LLM. ¿El contexto fue suficiente? ¿Las restricciones fueron claras? ¿El formato de salida que pediste ayudó a obtener una buena respuesta? ¿Qué parte de tu prompt fue más efectiva? Sé específico: menciona fragmentos concretos de tu prompt que funcionaron bien.]


#### 2. ¿Qué se puede mejorar?

[¿Qué le faltó a tu prompt? ¿Qué harías diferente si pudieras reformularlo? ¿El LLM entendió mal algo por falta de claridad en tu prompt? ¿La respuesta tiene errores u omisiones? ¿Qué no cubrió el LLM que tú sí sabes por lo visto en clase?]


#### 3. Respuesta final

[Escribe tu respuesta definitiva a la pregunta del parcial, integrando lo que aprendiste del LLM pero yendo más allá. Corrige errores, llena omisiones, conecta con conceptos vistos en clase. Esta es tu respuesta: demuestra que tú dominas el tema.]
