# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [04]: Entontrar el Patron asociado

### Estudiante
- **Nombre completo**: Juan Pablo Suarez Moreno

### Análisis crítico de la respuesta

#### 1. Conclusion

Es Decorator!!! me toco exponerlo jajaja.
En este caso que necesitamos a una clase agregarle caracteristicas o responsabilidades sin cambiar la estructura, esto nos permite agregarle descuentos como una cebolla a nuestro querido cliente, teniendo en cuenta que el orden si afecta, eso hay que tenerlo en cuenta a la hora de asignar los descuentos.
Este patron evita la creacion innecesaria especificas para cada caso de descuento


#### 2. Alternativas

Un alternativa puede ser el patron Strategy a pesar de que creo que es mejor Decorator, Strategy permite aislar  la regla de cada descuento en su propia clase, y poder modificar y eliminar los descuentos de manera mas sencialla.