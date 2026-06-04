# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [07]: Clean Arquitecture

### Estudiante
- **Nombre completo**: Juan Pablo Suarez Moreno

### Análisis crítico de la respuesta

#### 1. Definicionde Capas

##### Entidades (Core - Reglas de Negocio)
- Pego: Entidad que tiene el estado de pago puede ser pendiente aprobado o rechazado, igual que otros atributos adicionales como el metodo, monto, fecha.
- OrdenCompra: Entidad tiene los libros digitales que se ecnuentran en el carrito que tiene buyer.

##### Casos de Uso (Reglas de Negocio)
- Como se comento tiene que haber ProcesarPagoUseCase: Clase encargada de coordinar la logica que recibiria la orden y podria llamar la validacion de alguna apsarela o disparar alguna notificacion.
- Interfaces de Salida: Caso de uso de contraros para que pueda salir donde puede haber (PagoGateway, TransaccionGateway, o NotificationGateway).

##### Adaptadores de interfaz
- Lo mas importante considero que es los Adaptadores de pasarelas que implementan PagoGateway. Que podrian traducir el rquerimiento de pado a APIs especificas
- Notificacion, que usaria Notifiationgateway, que redactaria y enviaria correos.

##### Framework - Drivers
- Obligatoriamente esta Spring Boot 4.x y Java 25.
- PostgreSQL como la base de datos.
- JavaFX como interfaz grafica por defecto que consimiria API REST.
