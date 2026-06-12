## P10_vm_vs_containers

### Estudiante
- **Nombre completo**: [Alejandro Macias Barrios]

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [Claude] |
| **Modelo específico** | [Claude Sonnet 4.6] |
| **¿Por qué elegiste este LLM?** | [Claude maneja bien las comparaciones técnicas estructuradas y genera tablas comparativas precisas. Para un tema de virtualización, donde hay mucha información técnica que organizar, produce respuestas más ordenadas que otros LLMs.] |

### Prompt utilizado
En mi curso de ingeniería de software discutimos virtualización. Necesito una comparación 
técnica completa entre Máquinas Virtuales (VMs) y Contenedores (Docker/OCI).

Cubre los siguientes puntos obligatoriamente:
(a) Arquitectura: cómo funciona internamente cada uno (hypervisor tipo 1 vs tipo 2, 
    container runtime, relación con el kernel del host).
(b) Ventajas de VMs sobre contenedores (mínimo 3 con justificación técnica).
(c) Ventajas de contenedores sobre VMs (mínimo 3 con justificación técnica).
(d) Casos de uso específicos donde cada tecnología es la opción correcta.
(e) Pitfalls comunes al adoptar cada tecnología.

Formato de salida: primero una tabla comparativa resumida, luego una sección de 
explicación detallada para cada punto. Sé técnico y preciso; no uses lenguaje de marketing.

### Respuesta del LLM

[(a) Arquitectura
Máquinas Virtuales
Una VM virtualiza hardware completo. El hypervisor puede ser:

Tipo 1 (bare-metal): corre directamente sobre el hardware, sin SO huésped (VMware ESXi, Microsoft Hyper-V, KVM). Más eficiente.
Tipo 2 (hosted): corre sobre un SO host (VirtualBox, VMware Workstation). Más lento por la capa adicional.

Cada VM incluye su propio kernel, sistema operativo completo, y procesos. El hypervisor traduce instrucciones del SO huésped al hardware real.
Contenedores
Los contenedores no virtualizan hardware. Usan características del kernel de Linux:

namespaces: aíslan PID, red, sistema de archivos, usuarios.
cgroups: limitan el uso de CPU, memoria, I/O.

Todos los contenedores comparten el mismo kernel del host. El container runtime (Docker Engine, containerd) gestiona la creación y ejecución de contenedores. Las imágenes usan capas Union File System (OverlayFS), donde cada instrucción del Dockerfile agrega una capa inmutable.

(b) Ventajas de VMs sobre contenedores

Aislamiento de kernel: cada VM tiene su propio kernel. Una vulnerabilidad de kernel en una VM no afecta otras VMs ni el host. En contenedores, una vulnerabilidad de kernel afecta a todos los contenedores del host (container escape attacks).
Soporte para sistemas operativos heterogéneos: en el mismo host físico puedes correr Windows Server, Ubuntu 20.04 y CentOS 7 simultáneamente. Los contenedores Linux solo pueden correr workloads Linux en un host Linux.
Regulación y compliance: sectores regulados (banca, salud) exigen aislamiento fuerte. Una VM con kernel propio cumple más fácilmente con PCI-DSS o HIPAA porque la superficie de ataque compartida entre tenants es mínima.


(c) Ventajas de contenedores sobre VMs

Densidad: un servidor con 32 GB RAM puede correr ~3–5 VMs con SO completo o cientos de contenedores. El overhead de memoria de un contenedor es el de la aplicación, no el de un SO completo.
Arranque inmediato: un contenedor arranca en milisegundos. Una VM necesita bootear el kernel (mínimo 30 segundos). Esto hace a los contenedores ideales para auto-scaling y funciones serverless.
Reproducibilidad en CI/CD: la imagen OCI empaqueta código + dependencias + configuración. Si la imagen corre en local, corre idéntica en staging y producción. Con VMs, configurar el entorno es manual y propenso a errores.


(d) Casos de uso
Usar VMs cuando:

Multi-tenant con tenants no confiables (hosting compartido, cloud público).
Se necesitan SOs distintos en el mismo hardware.
Compliance estricto exige aislamiento de kernel.
Cargas legacy que no pueden containerizarse (aplicaciones con drivers de kernel personalizados).

Usar contenedores cuando:

Arquitectura de microservicios con muchos servicios pequeños.
CI/CD: builds reproducibles y entornos efímeros de prueba.
Escalado horizontal rápido (Kubernetes).
Workloads en la misma organización y red de confianza.


(e) Pitfalls comunes
VMs

Overcommit de recursos: asignar más vCPUs o RAM de la que el host tiene puede degradar todas las VMs cuando todas demandan recursos simultáneamente.
VM sprawl: es fácil crear VMs y olvidarlas. Decenas de VMs inactivas consumen licencias y recursos.
Snapshots como backup: los snapshots no son backups. Guardan el estado del disco de la VM pero no son recovery point objects robustos.

Contenedores

Correr como root: por defecto, muchos contenedores corren como root dentro del contenedor. Si el aislamiento falla, el proceso tiene privilegios de root en el host.
Imagen base obsoleta: usar FROM ubuntu:latest significa que cada build puede traer una versión diferente. Las imágenes de producción deben usar tags fijos y escanearse por vulnerabilidades.
Datos en el contenedor: tratar el contenedor como persistente. Los contenedores son efímeros; los datos deben montarse en volúmenes externos.
Logs en stdout: muchos equipos conectan los logs directamente al filesystem del contenedor, que se pierde cuando el contenedor muere. Los logs deben ir a stdout/stderr y ser capturados por el runtime.]

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?
[Enumerar los puntos con letras y pedir "mínimo 3 con justificación técnica" obligó al LLM a dar razones concretas en vez de listas superficiales. Pedir explícitamente que no use "lenguaje de marketing" produjo explicaciones técnicas más precisas sobre namespaces, cgroups y hypervisors. El formato pedido (tabla primero, luego explicación) es exactamente el que se obtuvo.]

#### 2. ¿Qué se puede mejorar?
[El prompt no mencionó explícitamente el problema de seguridad del kernel compartido, que es el concepto de clase más importante sobre contenedores. El LLM lo tocó brevemente en "ventajas de VMs" y en pitfalls ("correr como root"), pero no desarrolló el concepto de container escape attacks con suficiente profundidad. Debería haberse pedido explícitamente "explica el riesgo de seguridad por compartir kernel".]

#### 3. Respuesta final
[Las máquinas virtuales y los contenedores son tecnologías de virtualización que buscan ejecutar aplicaciones de manera aislada, pero lo hacen de formas diferentes. Las VMs virtualizan el hardware completo mediante un hypervisor y ejecutan un sistema operativo independiente con su propio kernel, mientras que los contenedores comparten el kernel del host y utilizan mecanismos como namespaces y cgroups para proporcionar aislamiento y control de recursos.
Las VMs ofrecen un mayor nivel de seguridad y aislamiento, siendo ideales para entornos con requisitos estrictos de compliance, ejecución de múltiples sistemas operativos o escenarios multi-tenant donde los usuarios no son confiables. Sin embargo, consumen más recursos y tienen tiempos de arranque más lentos debido a que cada instancia incluye un sistema operativo completo. En cambio, los contenedores destacan por su ligereza, rapidez y eficiencia. Permiten ejecutar una gran cantidad de aplicaciones en el mismo hardware, facilitan la integración continua y el despliegue continuo (CI/CD), y son la base de arquitecturas modernas de microservicios y plataformas de orquestación como Kubernetes. Su principal desventaja es que comparten el kernel del host, por lo que una vulnerabilidad a ese nivel puede comprometer todos los contenedores.

La elección entre VMs y contenedores depende del contexto. Si la prioridad es el aislamiento, la seguridad o la compatibilidad con diferentes sistemas operativos, las VMs son la mejor opción. Si se busca eficiencia, escalabilidad y despliegues rápidos, los contenedores resultan más adecuados. Todo parece muy bueno pero también debemos tener en cuenta que  que ambas tecnologías presentan riesgos operativos que deben gestionarse correctamente.]