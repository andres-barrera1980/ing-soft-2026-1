# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta 10: Máquinas Virtuales vs Contenedores

### Estudiante
- **Nombre completo**: Federico Restrepo

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Gemini |
| **Modelo específico** | Gemini 3.1 Pro |
| **¿Por qué elegiste este LLM?** | Usé Gemini 3.1 Pro en antigravity, ya que ofrece relativamente una buena cantidad de tokens y ofrece un manejo visual de los archivos que me gusta. |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```text
Actúa como un experto en DevOps y Arquitectura de Sistemas de una empresa de tecnología top tier.

Contexto: Necesito entender a fondo las diferencias entre Máquinas Virtuales (VMs) y Contenedores para tomar decisiones de arquitectura.

Tarea: Elabora una comparación técnica detallada entre Máquinas Virtuales y Contenedores. Debes abordar los siguientes puntos exactamente en este orden:
1. Arquitectura y cómo funciona cada uno a nivel de sistema operativo y kernel.
2. Ventajas técnicas de las Máquinas Virtuales sobre los Contenedores.
3. Ventajas técnicas de los Contenedores sobre las Máquinas Virtuales.
4. Casos de uso concretos donde conviene usar VMs y dónde conviene usar Contenedores (ejemplos realistas).
5. Common pitfalls (errores comunes) o riesgos de seguridad y operativos al adoptar cada tecnología.

Formato de salida: 
- Inicia con una tabla comparativa clara y directa que resuma las diferencias clave (Aislamiento, Sistema Operativo, Tiempo de arranque, Rendimiento, Seguridad, etc.).
- Luego, desarrolla cada uno de los 5 puntos solicitados usando encabezados en markdown y un lenguaje técnico pero claro.
```

---

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

*(1 iteración)*

---

### Comparación: Máquinas Virtuales vs Contenedores

| Característica | Máquinas Virtuales (VMs) | Contenedores |
| :--- | :--- | :--- |
| **Aislamiento** | Completo a nivel de hardware/hypervisor | A nivel de SO (aislamiento de procesos y recursos) |
| **Sistema Operativo** | Cada VM tiene su propio OS completo (Guest OS) | Todos comparten el kernel del Host OS |
| **Tamaño (Huella)** | Pesado (Gigabytes) | Ligero (Megabytes) |
| **Tiempo de arranque** | Lento (Minutos, requiere boot del OS) | Muy rápido (Milisegundos/Segundos) |
| **Rendimiento** | Sobrecarga por la virtualización del hardware | Rendimiento casi nativo (procesos nativos del host) |
| **Seguridad** | Muy Alta (difícil escapar del hypervisor) | Media-Alta (depende de la configuración del kernel host) |
| **Portabilidad** | Limitada (acoplado a hipervisores/formatos) | Muy Alta ("Build once, run anywhere") |

---

#### 1. Arquitectura y Funcionamiento

**Máquinas Virtuales (VMs):**
La virtualización tradicional utiliza una capa de software llamada **Hipervisor** (como VMware ESXi, KVM o Hyper-V) que se asienta sobre el hardware físico. El hipervisor abstrae la CPU, memoria, almacenamiento y red, y los asigna a entornos aislados. 
Lo fundamental de esta arquitectura es que **cada VM ejecuta una copia completa de un Sistema Operativo Invitado (Guest OS)**, además de los binarios, librerías y la aplicación. El hipervisor debe traducir las llamadas del hardware virtual al hardware físico, lo que genera una sobrecarga (overhead).

**Contenedores:**
Los contenedores (como Docker, containerd o Podman) utilizan virtualización a nivel de Sistema Operativo. En lugar de un hipervisor, utilizan un **Motor de Contenedores** que se ejecuta directamente sobre el Host OS. 
En lugar de virtualizar hardware, el motor aprovecha capacidades nativas del kernel de Linux: **Namespaces** (para aislar lo que el contenedor puede ver: red, procesos, montajes) y **Cgroups** (para limitar lo que el contenedor puede usar: CPU, memoria). **Todos los contenedores comparten el mismo kernel del Host OS**, lo que elimina la necesidad de múltiples Guest OS y reduce drásticamente el overhead.

#### 2. Ventajas de las Máquinas Virtuales sobre los Contenedores

*   **Aislamiento Rígido y Seguridad Fuerte:** Dado que cada VM tiene su propio kernel y está separada por el hipervisor, una vulnerabilidad que cause un *kernel panic* en una VM no afecta a las demás. Es el estándar de oro para aislar entornos *multi-tenant* donde no se confía en los vecinos.
*   **Independencia del Sistema Operativo:** Puedes ejecutar servidores Windows, distribuciones Linux dispares (CentOS, Ubuntu) y FreeBSD simultáneamente en el mismo hipervisor físico. Los contenedores están atados al kernel del Host (un host Linux solo puede ejecutar contenedores Linux de forma nativa).
*   **Compatibilidad con Software Legacy:** Las aplicaciones antiguas diseñadas para arquitecturas monolíticas o que requieren acceso a bajo nivel a componentes del SO funcionan perfectamente en VMs sin requerir refactorización.

#### 3. Ventajas de los Contenedores sobre las Máquinas Virtuales

*   **Eficiencia de Recursos Excepcional:** Al compartir el kernel y no duplicar procesos base del SO, puedes correr cientos de contenedores en el mismo servidor donde solo cabrían unas pocas VMs. Se aprovecha casi el 100% del hardware para la aplicación.
*   **Arranque Inmediato:** Iniciar un contenedor es equivalente a iniciar un nuevo proceso en Linux. No hay secuencia de boot del SO, por lo que arrancan y escalan en milisegundos.
*   **Inmutabilidad y Portabilidad Absoluta:** Un contenedor empaqueta la app y todas sus dependencias. La imagen construida en el portátil del desarrollador se ejecutará exactamente igual en los servidores de QA y Producción, eliminando el problema de "en mi máquina funciona".
*   **Sinergia con Microservicios y CI/CD:** Su ligereza permite empaquetar servicios individuales que pueden ser destruidos, desplegados y escalados independientemente a gran velocidad mediante orquestadores como Kubernetes.

#### 4. Casos de Uso Concretos

**Cuándo usar Máquinas Virtuales:**
*   **Infraestructura de Bases de Datos Core:** Sistemas de bases de datos relacionales pesados y monolíticos (Oracle, SQL Server) que requieren control absoluto sobre los recursos, I/O dedicado y un estado altamente persistente.
*   **Sistemas de Procesamiento Financiero (Core Bancario):** Entornos regulados (PCI-DSS estricto) que requieren separación por hardware entre aplicaciones para mitigar completamente el riesgo de fugas de datos laterales.
*   **Aplicaciones Monolíticas Legacy:** Un ERP antiguo en Windows Server 2012 que la empresa no tiene presupuesto para reescribir.

**Cuándo usar Contenedores:**
*   **Arquitecturas de Microservicios:** Una aplicación de comercio electrónico (como OpenLib Market) dividida en servicios de `Catálogo`, `Pagos` y `Usuarios`, gestionada por Kubernetes para escalar `Catálogo` en Black Friday sin afectar el resto.
*   **Pipelines de CI/CD Dinámicos:** Agentes de construcción (build runners) efímeros que se levantan para compilar código, correr tests, y se destruyen solos en segundos.
*   **Aplicaciones Stateless (Sin Estado):** APIs web y workers en segundo plano que pueden ser destruidos o replicados horizontalmente según la demanda de tráfico sin perder información.

#### 5. Common Pitfalls y Riesgos

**Pitfalls en VMs:**
*   **VM Sprawl (Descontrol):** La facilidad de clonar VMs lleva a una acumulación de servidores inactivos o no documentados ("zombies") que consumen recursos físicos y licencias millonarias.
*   **Pesadilla de Mantenimiento (Patching):** Si tienes 100 VMs, tienes que parchear y actualizar el SO de 100 servidores de forma individual (o mediante herramientas pesadas como Ansible/Chef), lo que consume enormes recursos operativos.

**Pitfalls en Contenedores:**
*   **Falsa Sensación de Seguridad (Container Breakout):** Asumir que un contenedor es una barrera infranqueable. Si un contenedor se ejecuta como `root` (mala práctica común) y hay una vulnerabilidad en el kernel compartido, un atacante puede escapar al Host OS y tomar control de todos los contenedores del nodo.
*   **Persistencia de Datos Mal Gestionada:** Tratar un contenedor como una VM y guardar datos de bases de datos o logs directamente en el sistema de archivos del contenedor. Cuando el contenedor muere, los datos desaparecen. Los volúmenes persistentes externos son obligatorios.
*   **Sobrecarga de Complejidad (El Efecto Kubernetes):** Adoptar Kubernetes para un proyecto simple de 2 microservicios. La complejidad operativa del orquestador termina superando la de la propia aplicación, asfixiando al equipo de desarrollo.

---

### Análisis crítico de la respuesta

Pedir los cinco puntos en orden funcionó bien, la respuesta los respetó y la tabla al inicio ayuda a entender las diferencias antes de leer todo. Lo mejor fue la sección de pitfalls con el "Efecto Kubernetes", algo que los textos técnicos normalmente ignoran porque asumen que siempre hay que usar el stack más moderno.

Lo más débil fue la sección de ventajas de VMs, los puntos son correctos pero muy básicos. Le faltó algo más técnico como las garantías de rendimiento de un hipervisor tipo-1 o el tema de NUMA y afinidad de CPU. También poner Oracle como caso de uso exclusivo de VMs quedó desactualizado, hoy Oracle en contenedores es completamente viable.

Para la próxima le pediría que profundice más en las ventajas reales de VMs y que actualice los casos de uso donde la línea entre VMs y contenedores ya se volvió difusa.
