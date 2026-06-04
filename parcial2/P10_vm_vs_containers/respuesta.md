# Plantilla de entrega — Parcial 2

## Pregunta [09]: [vm vs containers]

### Estudiante

* **Nombre completo**: Marlon Garcia

---

### LLM utilizado

| Campo                           | Valor                                                                                                                                                                                                                                                                                      |
| ------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **Nombre del LLM**              | ChatGPT                                                                                                                                                                                                                                                                                    |
| **Modelo específico**           | GPT-5.5 Thinking                                                                                                                                                                                                                                                                           |
| **¿Por qué elegiste este LLM?** | Elegí este LLM porque permite estructurar comparaciones técnicas de forma clara, separar conceptos por categorías y generar una respuesta organizada en formato de tabla y análisis. Además, ayuda a identificar ventajas, casos de uso y errores comunes de forma resumida pero completa. |

---

### Prompt utilizado

```
Explícame la diferencia entre máquinas virtuales y contenedores como si fuera para una respuesta de parcial. Necesito que compares ambos enfoques teniendo en cuenta su arquitectura, cómo funciona cada uno, ventajas de las VMs frente a los contenedores, ventajas de los contenedores frente a las VMs, casos de uso recomendados y errores comunes al adoptar cada tecnología.

Por favor organiza primero la respuesta en una tabla comparativa y después agrega una explicación breve de los puntos más importantes, especialmente sobre seguridad, rendimiento, aislamiento y facilidad de despliegue.
```

---

### Respuesta del LLM

> Iteraciones realizadas: 1.

## Tabla comparativa: máquinas virtuales vs contenedores

| Aspecto               | Máquinas virtuales                                                                | Contenedores                                                                                      |
| --------------------- | --------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------- |
| Arquitectura          | Cada VM ejecuta un sistema operativo completo sobre un hipervisor.                | Cada contenedor comparte el kernel del sistema operativo anfitrión.                               |
| Unidad de aislamiento | Sistema operativo completo virtualizado.                                          | Proceso o conjunto de procesos aislados.                                                          |
| Sistema operativo     | Cada VM tiene su propio sistema operativo invitado.                               | Los contenedores no tienen un sistema operativo completo propio, comparten el kernel del host.    |
| Peso                  | Más pesadas, porque incluyen sistema operativo completo.                          | Más livianos, porque empaquetan aplicación y dependencias, pero no un sistema operativo completo. |
| Arranque              | Más lento, porque debe iniciar un sistema operativo completo.                     | Más rápido, porque inicia como un proceso aislado.                                                |
| Consumo de recursos   | Mayor consumo de CPU, RAM y almacenamiento.                                       | Menor consumo de recursos.                                                                        |
| Aislamiento           | Aislamiento más fuerte, porque cada VM tiene su propio sistema operativo.         | Aislamiento más ligero, porque comparten el kernel del host.                                      |
| Seguridad             | Generalmente más seguras para cargas críticas o no confiables.                    | Requieren buena configuración de seguridad porque comparten kernel.                               |
| Portabilidad          | Portables, pero las imágenes suelen ser grandes.                                  | Muy portables y prácticas para despliegue continuo.                                               |
| Escalabilidad         | Escalan más lento por su peso.                                                    | Escalan rápido, ideales para microservicios.                                                      |
| Casos de uso          | Ejecutar sistemas operativos distintos, aislar cargas críticas, ambientes legacy. | Microservicios, CI/CD, despliegues rápidos, desarrollo local reproducible.                        |

---

## Arquitectura y funcionamiento

Una máquina virtual funciona creando una capa de virtualización sobre el hardware físico. Esta capa se llama hipervisor. Encima del hipervisor se ejecutan varias máquinas virtuales, y cada una tiene su propio sistema operativo, sus propias librerías y sus propias aplicaciones.

Ejemplo:

```text
Hardware físico
→ Hipervisor
→ VM 1: Linux + App
→ VM 2: Windows + App
→ VM 3: Ubuntu + App
```

En cambio, un contenedor no virtualiza un sistema operativo completo. El contenedor empaqueta la aplicación con sus dependencias, pero comparte el kernel del sistema operativo anfitrión.

Ejemplo:

```text
Hardware físico
→ Sistema operativo host
→ Motor de contenedores
→ Contenedor 1: App + dependencias
→ Contenedor 2: App + dependencias
```

La diferencia principal es que las VMs virtualizan hardware y sistema operativo completo, mientras que los contenedores aíslan procesos compartiendo el mismo kernel.

---

## Ventajas de las VMs sobre los contenedores

Las máquinas virtuales tienen mejor aislamiento porque cada VM tiene su propio sistema operativo. Esto las hace convenientes cuando se ejecutan aplicaciones críticas, cargas no confiables o sistemas que requieren separación fuerte.

También permiten ejecutar sistemas operativos diferentes sobre el mismo hardware. Por ejemplo, se puede tener una VM con Windows y otra con Linux en el mismo servidor físico.

Además, son útiles para sistemas legacy que necesitan una configuración específica del sistema operativo.

Ejemplos donde convienen VMs:

```text
- Ejecutar Windows y Linux en el mismo servidor.
- Aislar aplicaciones críticas de seguridad.
- Probar sistemas operativos completos.
- Mantener aplicaciones antiguas que dependen de un entorno específico.
```

---

## Ventajas de los contenedores sobre las VMs

Los contenedores son más livianos y rápidos. Como no incluyen un sistema operativo completo, consumen menos recursos y pueden arrancar en segundos o incluso menos.

También facilitan la portabilidad, porque la aplicación se empaqueta con sus dependencias. Esto ayuda a evitar el problema de “en mi máquina sí funciona”.

Son muy usados en arquitecturas de microservicios y pipelines de CI/CD, porque permiten desplegar, escalar y reemplazar servicios de forma rápida.

Ejemplos donde convienen contenedores:

```text
- Microservicios.
- Despliegues frecuentes.
- Ambientes de desarrollo reproducibles.
- Automatización con CI/CD.
- Escalamiento rápido de servicios.
```

---

## Seguridad

Una diferencia importante es que los contenedores comparten el kernel del sistema operativo anfitrión. Esto significa que, si hay una vulnerabilidad grave en el kernel o una mala configuración de permisos, el riesgo puede afectar a otros contenedores o al host.

Por eso, en contenedores se deben cuidar aspectos como:

```text
- No ejecutar contenedores como root.
- No usar imágenes desconocidas o sin actualizar.
- Limitar permisos y capacidades.
- Escanear vulnerabilidades en imágenes.
- No montar volúmenes sensibles sin necesidad.
```

Las VMs suelen tener una barrera de aislamiento más fuerte porque cada una ejecuta su propio sistema operativo. Sin embargo, también deben mantenerse actualizadas y bien configuradas.

---

## Common pitfalls o errores comunes

### Errores comunes al usar VMs

```text
- Crear demasiadas VMs y desperdiciar recursos.
- No actualizar los sistemas operativos invitados.
- Usar snapshots como si fueran backups permanentes.
- No controlar el consumo de CPU, RAM y almacenamiento.
- Configurar mal la red entre VMs.
```

### Errores comunes al usar contenedores

```text
- Pensar que un contenedor es igual de aislado que una VM.
- Ejecutar contenedores como root.
- Usar imágenes pesadas o inseguras.
- No definir límites de CPU y memoria.
- Guardar datos importantes dentro del contenedor en vez de usar volúmenes.
- No actualizar imágenes base.
- Meter demasiadas responsabilidades en un solo contenedor.
```

---

## Conclusión

Las máquinas virtuales y los contenedores sirven para aislar aplicaciones, pero no funcionan igual.

Las VMs son más pesadas, pero ofrecen mayor aislamiento y permiten ejecutar sistemas operativos completos. Son adecuadas para cargas críticas, sistemas legacy y escenarios donde se necesita una separación fuerte.

Los contenedores son más livianos, rápidos y portables. Son ideales para microservicios, despliegues frecuentes y ambientes de desarrollo o producción donde se busca escalar rápido.

En general, no se trata de que uno sea siempre mejor que el otro, sino de elegir según la necesidad del sistema.

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

El prompt fue claro porque especificó exactamente los criterios de comparación que debía cubrir el LLM: arquitectura, ventajas, casos de uso y errores comunes. Esto ayudó a que la respuesta no se quedara solo en una definición general de máquinas virtuales y contenedores.

También fue útil pedir explícitamente el formato de salida: “tabla comparativa y después explicación breve”. Esto hizo que la respuesta quedara ordenada y fácil de revisar.

La parte más efectiva del prompt fue pedir énfasis en “seguridad, rendimiento, aislamiento y facilidad de despliegue”, porque esos son puntos clave para diferenciar VMs y contenedores en un contexto práctico.

#### 2. ¿Qué se puede mejorar?

El prompt pudo ser más específico pidiendo que incluyera conceptos técnicos, como hipervisor tipo 1 y tipo 2, namespaces y cgroups. La respuesta explicó bien la idea general, pero no profundizó tanto en esos mecanismos internos.

También se pudo pedir que la comparación incluyera una columna sobre “nivel de aislamiento” y otra sobre “arranque y consumo de recursos”, aunque estos temas sí aparecieron en la tabla.

La respuesta del LLM fue correcta en general, pero pudo complementar mejor la explicación de contenedores mencionando que usan mecanismos del sistema operativo como namespaces para aislar procesos y cgroups para limitar recursos. También pudo explicar que las VMs dependen de un hipervisor, que puede ejecutarse directamente sobre el hardware o sobre un sistema operativo anfitrión.

#### 3. Respuesta final

Las máquinas virtuales y los contenedores son dos formas de ejecutar aplicaciones en entornos aislados, pero funcionan de manera distinta.

Una máquina virtual virtualiza hardware completo mediante un hipervisor. Sobre ese hipervisor se ejecuta una VM con su propio sistema operativo invitado, sus librerías y sus aplicaciones. Por eso una VM puede tener, por ejemplo, Linux, Windows o Ubuntu de manera independiente sobre el mismo servidor físico.

Un contenedor, en cambio, no incluye un sistema operativo completo. Ejecuta la aplicación y sus dependencias como procesos aislados, pero comparte el kernel del sistema operativo anfitrión. Por eso los contenedores son más livianos, arrancan más rápido y consumen menos recursos.

| Criterio        | Máquinas virtuales                                                      | Contenedores                                                         |
| --------------- | ----------------------------------------------------------------------- | -------------------------------------------------------------------- |
| Arquitectura    | Hardware físico → hipervisor → sistema operativo invitado → aplicación. | Sistema operativo host → motor de contenedores → aplicación aislada. |
| Aislamiento     | Más fuerte, porque cada VM tiene su propio sistema operativo.           | Más ligero, porque los contenedores comparten kernel.                |
| Peso            | Más pesadas.                                                            | Más livianos.                                                        |
| Arranque        | Más lento.                                                              | Más rápido.                                                          |
| Recursos        | Consumen más RAM, CPU y almacenamiento.                                 | Consumen menos recursos.                                             |
| Portabilidad    | Portables, pero las imágenes suelen ser grandes.                        | Muy portables y fáciles de mover entre ambientes.                    |
| Seguridad       | Mejor aislamiento para cargas críticas.                                 | Requiere cuidado porque comparte kernel con el host.                 |
| Uso recomendado | Sistemas legacy, cargas críticas, distintos sistemas operativos.        | Microservicios, CI/CD, despliegues rápidos, desarrollo reproducible. |

Las VMs tienen como ventaja principal el aislamiento. Como cada una tiene su propio sistema operativo, son más adecuadas cuando se necesita ejecutar cargas críticas, aplicaciones no confiables o sistemas operativos distintos. También son útiles para software legacy que necesita una configuración específica del sistema.

Los contenedores tienen como ventaja principal la eficiencia. Como no cargan un sistema operativo completo, arrancan rápido, pesan menos y permiten escalar aplicaciones con facilidad. Por eso se usan mucho en microservicios, DevOps, pipelines de CI/CD y despliegues en la nube.

En seguridad, un punto clave es que los contenedores comparten el kernel del sistema operativo anfitrión. Esto significa que no deben asumirse como equivalentes a una VM en aislamiento. Si un contenedor está mal configurado, se ejecuta como root o usa imágenes inseguras, puede representar un riesgo para el host y otros contenedores.

Errores comunes al usar VMs:

* Crear demasiadas VMs y desperdiciar recursos.
* No actualizar los sistemas operativos invitados.
* Usar snapshots como si fueran backups permanentes.
* No planear correctamente CPU, RAM, almacenamiento y red.
* Mantener VMs innecesarias encendidas.

Errores comunes al usar contenedores:

* Creer que un contenedor es igual de aislado que una VM.
* Ejecutar contenedores como root.
* Usar imágenes base inseguras o desactualizadas.
* Guardar datos persistentes dentro del contenedor en vez de usar volúmenes.
* No definir límites de CPU y memoria.
* Construir contenedores demasiado grandes.
* Meter demasiadas responsabilidades en un solo contenedor.

Además, es importante mencionar dos conceptos técnicos: en máquinas virtuales, el hipervisor puede ser tipo 1, si corre directamente sobre el hardware, o tipo 2, si corre sobre un sistema operativo anfitrión. En contenedores, el aislamiento se logra mediante mecanismos como namespaces, que separan procesos, red y usuarios, y cgroups, que limitan recursos como CPU y memoria.

En conclusión, las VMs convienen cuando se necesita mayor aislamiento o sistemas operativos completos. Los contenedores convienen cuando se busca velocidad, portabilidad, escalabilidad y despliegue eficiente. No son tecnologías equivalentes: cada una responde mejor a necesidades diferentes.
