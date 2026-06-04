# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [10]: Maquinas Virtuales vs Contenedores

### Estudiante
- **Nombre completo**: Juan Pablo Suarez Moreno

### Análisis crítico de la respuesta

Las maquinas virtuales funcionan virtualizando hardware usando por ejemplo (como VMware o VirtualBox), que se instala sobre el sistema operativo host o directamente en el hardware. Cada VM tiene un propio sistema operativo completo, su propio kernel, memoria y almacenamiento virtualizados, bueno y malo a la vez. Esto las hace muy pesadas las imagenes y lentas de arrancar, pero ofrecen un aislamiento muy fuerte porque cada una corre de forma completamente independiente excelente para pruebas de seguridad. Convienen cuando necesitas correr multiples sistemas operativos distintos en una misma maquina. Considero que los errores mas comunes son los de asignarle cantidades o muy pequenas o muy grandes de memoria o recursos del equipo base.
Los contenedores no virtualizan hardware sino que comparten el kernel del sistema operativo host hocea se ponene en la misma placa base en vez de una encima. Esto los hace extremadamente livianos, y son muy portables gracias a herramientas como Docker. El problema es que al compartir el kernel, si hay una vulnerabilidad alguno todos los contenedores quedan expuestos. Convienen para microservicios, pipelines, y cualquier app que necesite escalar rapido y desplegarse en multiples ambientes de forma consistente como dice la descripcion de docker. Permite una muy buena portabilidad ya que trae con sigo todo el entorno asiq ue es facil de poner en otro sidpositivo.