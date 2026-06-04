# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [12]: Tester Defecto

### Estudiante
- **Nombre completo**: Juan Pablo Suarez Moreno


### Análisis crítico de la respuesta

#### 1. Cliclo de vida de defectos

Empieza en la fase de deteccion, donde el tester es evidentemente el responsable y su accion es identificar que el boton "Agregar al carrito" no responde al intentar añadir un sexto libro. 
Luego viene el reporte, donde el mismo tester deberia documenta el defecto en Jira en el caso del proyecto con todos los detalles, resumen claro, pasos para reproducir, resultado esperado y obtenido. 
El estado pasa a "reporte".En el poyecto el manager deberia revisar el caso, evalua su prioridad y pues debido al ser error en una funcionalidad clave es de alta prioridad y se lo asignaria a un desarrollador, el estado cambia a "asginado".
Ya en diagnostico asignado se deberia revisar localmente, revisar codigo asociado y encontrar la cauza.
En ese tiempo el estado cambiaria a In porges, y luego de que el desarrollador solucione el bug mostado tiene la obligacion de subir como hotfix a Git para cmabiar todas las verciones y poner el ticket en correccion, Despues el tester lo verifica hocea pone el ticket en verificado mira que este todo bien y lo Cierra, serrando asi el ciclo de reparacion de un error

