# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [10]: [vm_vs_containers]

### Estudiante
- **Nombre completo**: Boris Nicolas Valbuena Gueirsman

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Gemini |
| **Modelo específico** | Gemini 3.5 Flash |
| **¿Por qué elegiste este LLM?** | Es la que más usé durante el curso, por lo cual es la misma que usaré en el parcial. |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```
[Actúa como un arquitecto de infraestructura y plataformas especializado en virtualización, sistemas operativos, Docker, Kubernetes y computación en la nube.

Contexto:

Estoy estudiando virtualización para una plataforma de comercio electrónico llamada OpenLib Market. En clase hemos visto dos enfoques principales:

* Máquinas Virtuales (Virtual Machines - VMs)
* Contenedores (Containers)

Necesito una comparación técnica rigurosa entre ambas tecnologías.

Antes de responder:

1. Explica cómo funciona la arquitectura interna de una máquina virtual.
2. Explica cómo funciona la arquitectura interna de un contenedor.
3. Describe el papel del hypervisor en las VMs.
4. Explica el concepto de kernel compartido en contenedores.
5. Compara ventajas y desventajas de cada enfoque.
6. Analiza rendimiento, aislamiento, seguridad, portabilidad y consumo de recursos.
7. Presenta casos de uso reales donde conviene cada tecnología.
8. Identifica errores comunes (pitfalls) que suelen cometer los equipos al adoptar VMs o contenedores.
9. Incluye consideraciones de seguridad.

Restricciones:

* Utiliza terminología técnica correcta.
* No simplifiques excesivamente los conceptos.
* Explica las diferencias arquitectónicas fundamentales.
* Incluye ejemplos relacionados con OpenLib Market.
* Menciona explícitamente implicaciones de seguridad.

Formato de salida:

# Tabla comparativa VM vs Contenedores

# Arquitectura de Máquinas Virtuales

# Arquitectura de Contenedores

# Ventajas de las Máquinas Virtuales

# Ventajas de los Contenedores

# Casos de uso recomendados

# Riesgos y consideraciones de seguridad

# Pitfalls comunes

# Conclusión

Validación:

La respuesta debe explicar claramente la diferencia entre hypervisor y kernel compartido, comparar aislamiento y rendimiento, mencionar riesgos de seguridad asociados a contenedores y presentar casos de uso realistas para cada tecnología.

```

---

### Respuesta del LLM

> ¡Saludos! Como arquitecto de infraestructura y plataformas, entiendo perfectamente el reto. Diseñar la base tecnológica para una plataforma de comercio electrónico como **OpenLib Market** requiere entender a fondo cómo se gestionan los recursos a nivel de hardware y sistema operativo. No se trata de elegir una tecnología porque esté de moda, sino de entender sus compensaciones (*trade-offs*) en rendimiento, aislamiento y mantenibilidad.

A continuación, presento el análisis técnico y riguroso de ambas arquitecturas.

---

## Tabla comparativa VM vs Contenedores

| Característica | Máquinas Virtuales (VMs) | Contenedores (Docker/Kubernetes) |
| --- | --- | --- |
| **Abstracción** | Nivel de Hardware (Virtualiza componentes físicos) | Nivel de Sistema Operativo (Aísla procesos en el espacio de usuario) |
| **Aislamiento** | Fuerte (Límite de hardware virtual por hipervisor) | Lógico (Namespaces y Cgroups, comparten el mismo Kernel) |
| **Consumo de Recursos** | Alto (Requiere CPU, RAM y almacenamiento dedicado para cada OS invitado) | Muy bajo (Solo consume lo que requiere el proceso de la aplicación) |
| **Tiempo de Arranque** | Minutos (Requiere el proceso completo de *boot* del OS) | Segundos o milisegundos (Es solo el inicio de un proceso) |
| **Portabilidad** | Limitada por el formato del hipervisor (OVA, VMDK, VHD) | Alta (Cualquier host con el mismo motor de contenedores y OS base) |
| **Almacenamiento** | Pesado (GigaBytes debido al *Guest OS*) | Ligero (MegaBytes, basado en capas de solo lectura) |

---

## Arquitectura de Máquinas Virtuales

La arquitectura de una Máquina Virtual se basa en la **emulación o virtualización del hardware físico**. Cada VM es un sistema informático completo e independiente que se ejecuta sobre un hardware anfitrión (*Host Hardware*).

### El papel crucial del Hipervisor (VMM)

El componente central aquí es el **Hipervisor** o *Virtual Machine Monitor* (VMM). Su función principal es interceptar las instrucciones de CPU, memoria e I/O de las VMs y traducirlas al hardware real. Existen dos tipos principales:

* **Tipo 1 (Bare-Metal):** Se ejecuta directamente sobre el hardware físico (ej. VMware ESXi, Proxmox VE, KVM). Ofrece el máximo rendimiento y es el estándar para la infraestructura de producción de OpenLib Market.
* **Tipo 2 (Hosted):** Se ejecuta sobre un sistema operativo anfitrión (ej. VirtualBox, VMware Workstation). Ideal para entornos de desarrollo locales.

El hipervisor garantiza un **aislamiento estricto a nivel de hardware**. Cada VM cuenta con su propio Sistema Operativo Invitado (*Guest OS*), sus propios *drivers* de almacenamiento, pila de red y kernel. Si el Kernel de la VM 1 se corrompe o sufre un *kernel panic*, la VM 2 y el Host siguen operando como si nada hubiera pasado.

---

## Arquitectura de Contenedores

A diferencia de las VMs, los contenedores no virtualizan el hardware; en su lugar, **virtualizan el Sistema Operativo**. Un contenedor es, fundamentalmente, un proceso nativo del sistema operativo anfitrión que ha sido aislado del resto mediante características específicas del Kernel de Linux.

### El concepto de Kernel Compartido

En la arquitectura de contenedores, **no existe un Guest OS**. Todos los contenedores que se ejecutan en un servidor comparten el **mismo Kernel del Host**. El motor de contenedores (como el *Docker Daemon* o *containerd*) actúa como un orquestador de procesos en el espacio de usuario (*user space*), no como un hipervisor.

Este aislamiento lógico y control de recursos se logra gracias a dos primitivas del Kernel de Linux:

* **Namespaces (Espacios de nombres):** Proveen la ilusión de exclusividad (aislamiento). Existen namespaces para la red (`net`), los procesos (`pid`), los puntos de montaje (`mnt`), y los usuarios (`user`). Un contenedor no puede ver los procesos ni las interfaces de red de otro porque están en namespaces distintos.
* **Cgroups (Control Groups):** Se encargan de la asignación y limitación de recursos físicos (CPU, memoria, I/O de disco, ancho de banda de red). Evitan que un contenedor sature el servidor.

---

## Ventajas de las Máquinas Virtuales

* **Aislamiento de seguridad robusto:** Al tener fronteras basadas en hardware gestionadas por el hipervisor, la superficie de ataque es drásticamente menor. Un atacante que comprometa el *Guest OS* sigue atrapado dentro de la VM.
* **Heterogeneidad de Sistemas Operativos:** En un mismo servidor físico con Linux, puedes ejecutar VMs con Windows Server, FreeBSD o distribuciones antiguas de Linux con kernels específicos.
* **Madurez del ecosistema de gestión:** Herramientas de respaldo, migración en vivo (*Live Migration* como vMotion) y políticas de almacenamiento a nivel de bloque están sumamente consolidadas.

---

## Ventajas de los Contenedores

* **Eficiencia extrema de recursos:** Al no emular hardware ni cargar múltiples instancias de sistemas operativos, la densidad de aplicaciones por servidor es exponencialmente mayor.
* **Velocidad de despliegue y escalado:** El arranque de un contenedor toma milisegundos. Para eventos de alta demanda en OpenLib Market (como un *Black Friday*), Kubernetes puede escalar de 10 a 500 instancias de un microservicio en cuestión de segundos.
* **Inmutabilidad y portabilidad absoluta:** El paradigma de "construye una vez, ejecuta en cualquier lado". La imagen del contenedor incluye el código, binarios y librerías exactas. Lo que funciona en la laptop del desarrollador funcionará idéntico en la nube de producción.

---

## Casos de uso recomendados para OpenLib Market

### Cuándo usar Máquinas Virtuales (VMs)

1. **Base de Datos Relacional Monolítica (PostgreSQL / Oracle):** Las bases de datos transaccionales principales de OpenLib Market requieren un rendimiento de I/O de disco predecible, almacenamiento persistente clásico y un aislamiento estricto para mitigar riesgos de corrupción de datos o brechas de seguridad masivas.
2. **Sistemas Legacy o de Terceros:** Si el software del proveedor de pasarela de pagos o el ERP logístico requiere una versión de kernel muy específica o corre estrictamente sobre Windows Server, la VM es la opción obligatoria.

### Cuándo usar Contenedores (Docker + Kubernetes)

1. **Arquitectura de Microservicios:** El catálogo de productos, el carrito de compras, el servicio de notificaciones y el motor de búsquedas de OpenLib Market deben ser contenedores independientes. Esto permite actualizar el carrito de compras sin tocar el resto de la plataforma.
2. **Entornos de Desarrollo e Integración Continua (CI/CD):** Levantar réplicas idénticas de la plataforma para que los desarrolladores prueben su código antes de enviarlo a producción.

---

## Riesgos y consideraciones de seguridad

El modelo de kernel compartido de los contenedores introduce vectores de ataque críticos que debes considerar en OpenLib Market:

* **Ataques de Escape de Contenedor (*Container Breakout*):** Si un atacante explota una vulnerabilidad en el Kernel del host desde dentro de un contenedor, podría obtener acceso root a nivel del servidor físico, comprometiendo **todos** los demás contenedores que residen en ese nodo.
* **Denegación de Servicio Interna (DDoS de recursos):** Si un contenedor no tiene configurados los *Cgroups* correctamente y sufre una fuga de memoria (*Memory Leak*), el Kernel del Host activará el *Out Of Memory (OOM) Killer*, lo que podría apagar contenedores vecinos legítimos.
* **Vulnerabilidades en Imágenes de Contenedores:** Al descargar imágenes públicas no verificadas, se pueden introducir librerías con vulnerabilidades críticas (CVEs) o *malware* de minería de criptomonedas en la infraestructura de la tienda.

---

## Pitfalls comunes (Errores al adoptar la tecnología)

1. **Tratar a los contenedores como "VMs pequeñas" (Antipatrón):** Intentar meter SSH, cronjobs, bases de datos y herramientas de logs dentro de un solo contenedor. Los contenedores deben seguir el principio de un único proceso por contenedor.
2. **Ignorar los límites de recursos en Kubernetes/Docker:** No definir los `limits` y `requests` de CPU y memoria. En OpenLib Market, esto causaría que un pico de tráfico en el microservicio de búsqueda tumbe el microservicio de pagos por inanición de recursos.
3. **Persistencia de datos incorrecta:** Olvidar que el almacenamiento de un contenedor es efímero por defecto. Guardar las imágenes de los productos de OpenLib Market dentro del sistema de archivos del contenedor provocará que se borren permanentemente cada vez que el contenedor se reinicie o actualice. (Se deben usar *Volumes* o almacenamiento de objetos como S3).
4. **Ejecutar contenedores como usuario `root`:** Por defecto, el usuario dentro del contenedor es `root`. Si un atacante toma el control del proceso, tendrá privilegios de administrador. Es vital forzar la directiva `USER` no-root en los Dockerfiles.

---

## Conclusión

Para la arquitectura moderna de **OpenLib Market**, la respuesta óptima no es elegir una tecnología sobre la otra, sino implementar un **enfoque híbrido**.

La estrategia estándar de la industria dicta desplegar Máquinas Virtuales robustas y altamente aisladas a través de un hipervisor tipo 1 (en la nube o *on-premise*). Sobre esas VMs, se instala un clúster de Kubernetes encargado de orquestar los cientos de contenedores ligeros que componen los microservicios de la aplicación web. De este modo, obtienes lo mejor de ambos mundos: la seguridad perimetral y el aislamiento rígido de las **VMs**, junto con la agilidad, densidad y velocidad de escalado de los **contenedores**.
```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

¡Saludos! Como arquitecto de infraestructura y plataformas, entiendo perfectamente el reto. Diseñar la base tecnológica para una plataforma de comercio electrónico como **OpenLib Market** requiere entender a fondo cómo se gestionan los recursos a nivel de hardware y sistema operativo. No se trata de elegir una tecnología porque esté de moda, sino de entender sus compensaciones (*trade-offs*) en rendimiento, aislamiento y mantenibilidad.

A continuación, presento el análisis técnico y riguroso de ambas arquitecturas.

---

## Tabla comparativa VM vs Contenedores

| Característica | Máquinas Virtuales (VMs) | Contenedores (Docker/Kubernetes) |
| --- | --- | --- |
| **Abstracción** | Nivel de Hardware (Virtualiza componentes físicos) | Nivel de Sistema Operativo (Aísla procesos en el espacio de usuario) |
| **Aislamiento** | Fuerte (Límite de hardware virtual por hipervisor) | Lógico (Namespaces y Cgroups, comparten el mismo Kernel) |
| **Consumo de Recursos** | Alto (Requiere CPU, RAM y almacenamiento dedicado para cada OS invitado) | Muy bajo (Solo consume lo que requiere el proceso de la aplicación) |
| **Tiempo de Arranque** | Minutos (Requiere el proceso completo de *boot* del OS) | Segundos o milisegundos (Es solo el inicio de un proceso) |
| **Portabilidad** | Limitada por el formato del hipervisor (OVA, VMDK, VHD) | Alta (Cualquier host con el mismo motor de contenedores y OS base) |
| **Almacenamiento** | Pesado (GigaBytes debido al *Guest OS*) | Ligero (MegaBytes, basado en capas de solo lectura) |

---

## Arquitectura de Máquinas Virtuales

La arquitectura de una Máquina Virtual se basa en la **emulación o virtualización del hardware físico**. Cada VM es un sistema informático completo e independiente que se ejecuta sobre un hardware anfitrión (*Host Hardware*).

### El papel crucial del Hipervisor (VMM)

El componente central aquí es el **Hipervisor** o *Virtual Machine Monitor* (VMM). Su función principal es interceptar las instrucciones de CPU, memoria e I/O de las VMs y traducirlas al hardware real. Existen dos tipos principales:

* **Tipo 1 (Bare-Metal):** Se ejecuta directamente sobre el hardware físico (ej. VMware ESXi, Proxmox VE, KVM). Ofrece el máximo rendimiento y es el estándar para la infraestructura de producción de OpenLib Market.
* **Tipo 2 (Hosted):** Se ejecuta sobre un sistema operativo anfitrión (ej. VirtualBox, VMware Workstation). Ideal para entornos de desarrollo locales.

El hipervisor garantiza un **aislamiento estricto a nivel de hardware**. Cada VM cuenta con su propio Sistema Operativo Invitado (*Guest OS*), sus propios *drivers* de almacenamiento, pila de red y kernel. Si el Kernel de la VM 1 se corrompe o sufre un *kernel panic*, la VM 2 y el Host siguen operando como si nada hubiera pasado.

---

## Arquitectura de Contenedores

A diferencia de las VMs, los contenedores no virtualizan el hardware; en su lugar, **virtualizan el Sistema Operativo**. Un contenedor es, fundamentalmente, un proceso nativo del sistema operativo anfitrión que ha sido aislado del resto mediante características específicas del Kernel de Linux.

### El concepto de Kernel Compartido

En la arquitectura de contenedores, **no existe un Guest OS**. Todos los contenedores que se ejecutan en un servidor comparten el **mismo Kernel del Host**. El motor de contenedores (como el *Docker Daemon* o *containerd*) actúa como un orquestador de procesos en el espacio de usuario (*user space*), no como un hipervisor.

Este aislamiento lógico y control de recursos se logra gracias a dos primitivas del Kernel de Linux:

* **Namespaces (Espacios de nombres):** Proveen la ilusión de exclusividad (aislamiento). Existen namespaces para la red (`net`), los procesos (`pid`), los puntos de montaje (`mnt`), y los usuarios (`user`). Un contenedor no puede ver los procesos ni las interfaces de red de otro porque están en namespaces distintos.
* **Cgroups (Control Groups):** Se encargan de la asignación y limitación de recursos físicos (CPU, memoria, I/O de disco, ancho de banda de red). Evitan que un contenedor sature el servidor.

---

## Ventajas de las Máquinas Virtuales

* **Aislamiento de seguridad robusto:** Al tener fronteras basadas en hardware gestionadas por el hipervisor, la superficie de ataque es drásticamente menor. Un atacante que comprometa el *Guest OS* sigue atrapado dentro de la VM.
* **Heterogeneidad de Sistemas Operativos:** En un mismo servidor físico con Linux, puedes ejecutar VMs con Windows Server, FreeBSD o distribuciones antiguas de Linux con kernels específicos.
* **Madurez del ecosistema de gestión:** Herramientas de respaldo, migración en vivo (*Live Migration* como vMotion) y políticas de almacenamiento a nivel de bloque están sumamente consolidadas.

---

## Ventajas de los Contenedores

* **Eficiencia extrema de recursos:** Al no emular hardware ni cargar múltiples instancias de sistemas operativos, la densidad de aplicaciones por servidor es exponencialmente mayor.
* **Velocidad de despliegue y escalado:** El arranque de un contenedor toma milisegundos. Para eventos de alta demanda en OpenLib Market (como un *Black Friday*), Kubernetes puede escalar de 10 a 500 instancias de un microservicio en cuestión de segundos.
* **Inmutabilidad y portabilidad absoluta:** El paradigma de "construye una vez, ejecuta en cualquier lado". La imagen del contenedor incluye el código, binarios y librerías exactas. Lo que funciona en la laptop del desarrollador funcionará idéntico en la nube de producción.

---

## Casos de uso recomendados para OpenLib Market

### Cuándo usar Máquinas Virtuales (VMs)

1. **Base de Datos Relacional Monolítica (PostgreSQL / Oracle):** Las bases de datos transaccionales principales de OpenLib Market requieren un rendimiento de I/O de disco predecible, almacenamiento persistente clásico y un aislamiento estricto para mitigar riesgos de corrupción de datos o brechas de seguridad masivas.
2. **Sistemas Legacy o de Terceros:** Si el software del proveedor de pasarela de pagos o el ERP logístico requiere una versión de kernel muy específica o corre estrictamente sobre Windows Server, la VM es la opción obligatoria.

### Cuándo usar Contenedores (Docker + Kubernetes)

1. **Arquitectura de Microservicios:** El catálogo de productos, el carrito de compras, el servicio de notificaciones y el motor de búsquedas de OpenLib Market deben ser contenedores independientes. Esto permite actualizar el carrito de compras sin tocar el resto de la plataforma.
2. **Entornos de Desarrollo e Integración Continua (CI/CD):** Levantar réplicas idénticas de la plataforma para que los desarrolladores prueben su código antes de enviarlo a producción.

---

## Riesgos y consideraciones de seguridad

El modelo de kernel compartido de los contenedores introduce vectores de ataque críticos que debes considerar en OpenLib Market:

* **Ataques de Escape de Contenedor (*Container Breakout*):** Si un atacante explota una vulnerabilidad en el Kernel del host desde dentro de un contenedor, podría obtener acceso root a nivel del servidor físico, comprometiendo **todos** los demás contenedores que residen en ese nodo.
* **Denegación de Servicio Interna (DDoS de recursos):** Si un contenedor no tiene configurados los *Cgroups* correctamente y sufre una fuga de memoria (*Memory Leak*), el Kernel del Host activará el *Out Of Memory (OOM) Killer*, lo que podría apagar contenedores vecinos legítimos.
* **Vulnerabilidades en Imágenes de Contenedores:** Al descargar imágenes públicas no verificadas, se pueden introducir librerías con vulnerabilidades críticas (CVEs) o *malware* de minería de criptomonedas en la infraestructura de la tienda.

---

## Pitfalls comunes (Errores al adoptar la tecnología)

1. **Tratar a los contenedores como "VMs pequeñas" (Antipatrón):** Intentar meter SSH, cronjobs, bases de datos y herramientas de logs dentro de un solo contenedor. Los contenedores deben seguir el principio de un único proceso por contenedor.
2. **Ignorar los límites de recursos en Kubernetes/Docker:** No definir los `limits` y `requests` de CPU y memoria. En OpenLib Market, esto causaría que un pico de tráfico en el microservicio de búsqueda tumbe el microservicio de pagos por inanición de recursos.
3. **Persistencia de datos incorrecta:** Olvidar que el almacenamiento de un contenedor es efímero por defecto. Guardar las imágenes de los productos de OpenLib Market dentro del sistema de archivos del contenedor provocará que se borren permanentemente cada vez que el contenedor se reinicie o actualice. (Se deben usar *Volumes* o almacenamiento de objetos como S3).
4. **Ejecutar contenedores como usuario `root`:** Por defecto, el usuario dentro del contenedor es `root`. Si un atacante toma el control del proceso, tendrá privilegios de administrador. Es vital forzar la directiva `USER` no-root en los Dockerfiles.

---

## Conclusión

Para la arquitectura moderna de **OpenLib Market**, la respuesta óptima no es elegir una tecnología sobre la otra, sino implementar un **enfoque híbrido**.

La estrategia estándar de la industria dicta desplegar Máquinas Virtuales robustas y altamente aisladas a través de un hipervisor tipo 1 (en la nube o *on-premise*). Sobre esas VMs, se instala un clúster de Kubernetes encargado de orquestar los cientos de contenedores ligeros que componen los microservicios de la aplicación web. De este modo, obtienes lo mejor de ambos mundos: la seguridad perimetral y el aislamiento rígido de las **VMs**, junto con la agilidad, densidad y velocidad de escalado de los **contenedores**.


#### 2. ¿Qué se puede mejorar?

La respuesta es correcta en terminos generales y explica adecuadamente las diferencias arquitectonicas entre VMs y contenedores. Tambien menciona correctamente que los contenedores comparten el kernel del host, lo que implica un menor nivel de aislamiento frente a las VMs.

Sin embargo, falto profundizar un poco mas en el papel de Kubernetes como plataforma de orquestacion de contenedores y en el concepto de densidad de despliegue, que suele ser una de las razones principales para adoptar contenedores en sistemas modernos. Tambien podria haberse mencionado que las VMs permiten ejecutar sistemas operativos completamente distintos al host, mientras que los contenedores dependen del mismo kernel.

Otro punto mejorable es que algunos ejemplos estan orientados a arquitecturas modernas de microservicios, pero no se discuten escenarios donde un monolito pueda ejecutarse eficientemente dentro de contenedores.


#### 3. Respuesta final

Las VMs virtualizan el hardware mediante un hypervisor y cada una tiene su propio sistema operativo, lo que ofrece mayor aislamiento y seguridad, aunque consume mas recursos.

Los contenedores virtualizan el sistema operativo y comparten el kernel del host, por lo que son mas ligeros, rapidos y faciles de escalar, pero tienen menor aislamiento.

Para OpenLib Market, las VMs son adecuadas para bases de datos o sistemas que requieren alta seguridad, mientras que los contenedores son ideales para microservicios como carrito, catalogo y notificaciones.

Un riesgo importante de los contenedores es que comparten el kernel del host. Entre los errores mas comunes estan ejecutar contenedores como root, no limitar recursos y asumir que su almacenamiento es permanente. Muchas empresas combinan ambas tecnologias para aprovechar las ventajas de cada una.
