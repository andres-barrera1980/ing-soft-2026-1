# Pregunta P11: Pruebas manuales vs pruebas automatizadas

### Estudiante
- **Nombre completo**: Juan Camilo Gomez

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Sin IA — respuesta propia |
| **Modelo específico** | N/A |
| **¿Por qué elegiste este LLM?** | ganar bono

---

### Análisis crítico de la respuesta

#### 3. Respuesta final

## Pruebas manuales vs automatizadas para OpenLib Market

### Ventajas de las pruebas manuales

1. un tester humano nota si un boton es confuso, si el flujo de compra es frustrante, o si el mensaje de error es incomprensible. Ningun script automatizado puede detectar que el diseño de la pagina de pago genera desconfianza en el usuario.

2. cuando no hay especificaciones completas, un tester puede explorar el sistema de forma creativa, encontrando bugs que nunca se habrian anticipado en un script. En el primer release de OpenLib Market, muchos flujos aún no están completamente especificados.

3.  para una funcionalidad que cambia constantemente, mantener pruebas automatizadas cuesta mas que simplemente probarlas manualmente cada iteración.

### Desventajas de las pruebas manuales

1. dos ejecuciones manuales de la misma prueba pueden diferir en pasos, datos de entrada o condiciones del entorno. Un bug intermitente puede no reproducirse.

2. probar OpenLib Market en 50 navegadores y resoluciones de pantalla diferentes manualmente es impracticable. La regresión completa después de cada release tomaría días.

3. después de ejecutar 200 casos de prueba seguidos, la atención del tester baja. Pasos críticos pueden saltarse inconscientemente.

### Ventajas de las pruebas automatizadas

1. *una suite de pruebas unitarias de 500 tests corre en segundos. Se puede ejecutar en cada commit del pipeline CI/CD sin costo adicional.

2. cada nuevo feature se prueba automáticamente contra todos los comportamientos existentes. Si `ProcesarPagoUseCase` rompe algo en `CarritoService`, el pipeline lo detecta antes del merge.


### Desventajas de las pruebas automatizadas


1. tener 90% de cobertura de codigo no significa que el sistema este correctamente testeado. Una suite de pruebas que prueba implementacion en lugar de comportamiento puede pasar al 100% y aún tener bugs críticos de logica de negocio.

2. escribir pruebas de calidad, configurar el pipeline CI, los mocks y los ambientes de prueba requiere tiempo significativo antes de ver beneficios.



### Criterios para decidir cuándo automatizar

Automatizar cuando:
- La prueba se ejecuta frecuentemente 
- El comportamiento es estable y no cambia seguido
- La prueba es repetible y deterministica 
- La prueba es dificil de ejecutar manualmente 

Dejar manual cuando:
- La funcionalidad cambia rapidamente en iteraciones tempranas
- La prueba requiere juicio humano 
- El costo de automatizar supera el beneficio 
- Es una prueba exploratoria por primera vez

### Recomendación para OpenLib Market (primer release)

Automatizar:
- Todas las pruebas unitarias de dominio (`CarritoService`, `ControlInventarioService`, `GestorLibro`)
- Pruebas de integracion de la API REST 
- Pruebas de regresion del flujo critico: agregar al carrito → checkout → pago

Dejar manual:
- Flujo completo de onboarding de vendedores 
- UI del frontend JavaFX 
- Pruebas de aceptación con usuarios reales antes del lanzamiento


