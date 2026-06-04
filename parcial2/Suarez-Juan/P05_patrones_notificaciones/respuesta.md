# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [05]: Entonctrar el Patron

### Estudiante
- **Nombre completo**: Juan Pablo Suarez Moreno

### Análisis crítico de la respuesta

#### 1. Conclusion

Easy es el patron de Observador!!!, este patron no solo nos permite tener una lista en este caso de usuarios con libros en su wishlist, si no que tambien nos permite notificar el cambio de estado, este caso del OpenLib Market por correo, tambien se puede aplicar en otras partes del negocio como cuando se acaba la disponibilidad. Respecto por ejemplo al cache de REdis es el otro lado, este es el observador o suscriptor su unica funcion es ecuchar el evento por ejemplo de disponibilidad o el log de auditoria que es un suscriptor pendiente del cambio de estado del libro, para guardar la informacion.

Las claras ventajas de este patron es el desacoplamiento que ofrece ya que por ejemplo el libro no necesita conocer nada mas que envisar cuando cambia de estado.
