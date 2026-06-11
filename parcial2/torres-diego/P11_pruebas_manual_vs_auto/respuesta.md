# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [11]: [P11_pruebas_manual_vs_auto]

### Estudiante
- **Nombre completo**: [Diego Alejandro Torres Barragan ]

Sin uso de IA 

#### 1. Respuesta final

Pruebas manuales:
Ventajas:

Detectan problemas de usabilidad y experiencia de usuario que una prueba automatizada no puede ver
No requieren inversion inicial de tiempo para escribir scripts
Flexibles para explorar flujos no previstos (pruebas exploratorias)

Desventajas:

Lentas y no escalables, no se pueden repetir miles de veces
Propensas a error humano, dos testers pueden obtener resultados diferentes
No sirven para regresion frecuente porque consumen mucho tiempo

Pruebas automatizadas:
Ventajas:

Se pueden correr en cada commit sin esfuerzo adicional
Rapidas y consistentes, siempre ejecutan exactamente los mismos pasos
Ideales para regresion: verifican que nada se rompio con cada cambio

Desventajas:

Costo de mantenimiento alto: si cambia el codigo las pruebas hay que actualizarlas tambien
Inversion inicial de tiempo para escribirlas bien
No detectan problemas visuales ni de usabilidad

| Tipo | Manual | Automatizada |
|---|---|---|
| Exploratorias | Si | No |
| Unitarias | No | Si |
| Integracion | A veces | Si |
| Regresion | No (muy costoso) | Si |
| Usabilidad | Si | No |
| Carga y estres | No | Si |

Criterios para decidir:
Automatizar cuando:

La prueba se repite en cada release
El flujo es estable y no cambia seguido
Es una prueba de regresion critica

Hacer manual cuando:

Es una prueba exploratoria de un flujo nuevo
Involucra juicio visual o de experiencia de usuario
Es una funcionalidad que probablemente cambie pronto

Las Recomendacion para OpenLib Market:
Automatizar: pruebas unitarias de CarritoService, ControlInventarioService y RealizarPagoUseCase, pruebas de integracion del flujo de pago y pruebas de regresion del checkout.
Manual: pruebas de usabilidad del flujo de compra, pruebas exploratorias de la interfaz JavaFX y verificacion visual del carrito en diferentes resoluciones.