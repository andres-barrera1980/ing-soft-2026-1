# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [01]: [P01_solid_principio]

### Estudiante
- **Nombre completo**: [Juan Pablo Suarez Moreno]

---

### Análisis crítico de la respuesta

#### 1. Conclusion

El codigo presentado muestra una la clase llamada "GestorLibro", con una funcion de publicarLibro que tiene varios pasos con comentarios para hacerlo.

El problema que presenta la clase es que esta violando evidentemente el principio SOLID (Single Responsability), que dicta que una clase deberia tener solo un motivo para cambiar, esta clase no solo accede al apartado de persistencia en el punto 2, tambien se encarga de mandar notificaciones en el punto 4 nombrando los apartados evidentes. Ya solo con tener en cuenta estos 2 se puede saber que esta clase se cree una super clase.

#### 2. Refactorizacion
```

