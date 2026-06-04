# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [04]: [Patrones ]

### Estudiante
- **Marlon Garcia**: [Tu nombre y apellido]

---


### Respuesta sin IA 

El patrón principal a proponer es Strategy, porque existen distintos tipos de descuentos y cada uno tiene una forma diferente de calcularse. Sin embargo, como en este caso no se aplica necesariamente un solo descuento por compra, sino que varios descuentos pueden combinarse, también sería adecuado usar Composite. Este patrón permitiría agrupar varias estrategias de descuento y aplicarlas en orden para obtener el precio final.

Y las opciones para descartar:

* Decorator: porque, aunque permite apilar descuentos, en este caso puede volver el diseño más complejo de lo necesario. Como los descuentos solo deben agruparse y aplicarse en secuencia, Composite resulta más claro y fácil de mantener.

* Chain of Responsibility: este patrón se usa más cuando una solicitud pasa por varios manejadores y alguno decide si la procesa o la delega. En este caso no se busca que un descuento “maneje” o “rechace” la compra, sino aplicar todas las estrategias de descuento que correspondan.



