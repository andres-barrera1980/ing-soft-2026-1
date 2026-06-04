# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [04]: [P04_patrones_descuento]

### Estudiante
- **Nombre completo**: [Diego Alejandro Torres Barragan ]

## Respuesta sin IA

#### 1. Respuesta final

El patron que veo mas adecuado para este caso es Strategy ya que tiene varias estrategias de descuentos que pueden cambiar y combinarse, este patron seria el ideal porque permite encapsular cada tipo descuento en su propia clase e intercambiarlos o combinarlos sin tocar codigo del carrito. 

Por que no otros patrones 

Singleton: Sirve para garantizar una unica instancia de una clase , no tiene nada que ver con el caluclo de los descuentos 
Observer: sirve para notificar a multiples objetos cuando algo cambia m pero no para encapsular logicas que requieren de hacer un calculo. 

