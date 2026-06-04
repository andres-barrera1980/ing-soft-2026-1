# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [10]: [P10_vm_vs_containers]

### Estudiante
- **Nombre completo**: [Jhoan Galeano]

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [Claude |
| **Modelo específico** | [ Claude pro 3.1|
| **¿Por qué elegiste este LLM?** | Use claude porque igual que las anteoriores, sirve para entender mejor la logica. |

---

### Prompt utilizado

Actúa como un ingeniero de software senior y experto en arquitectura de software. 

Tu tarea es realizar una comparación exhaustiva y técnica entre Máquinas Virtuales (VMs) y Contenedores. Debes estructurar tu respuesta incluyendo primero una tabla comparativa resumen y, posteriormente, explicaciones detalladas para cada uno de los puntos solicitados.

Por favor, aborda los siguientes aspectos con el máximo rigor técnico:

1. Arquitectura y Funcionamiento: Explica cómo opera cada tecnología a nivel de hardware, sistema operativo, hipervisor/motor de contenedores y gestión de recursos (especificando cómo interactúan con el kernel).
2. Ventajas de las Máquinas Virtuales sobre los Contenedores.
3. Ventajas de los Contenedores sobre las Máquinas Virtuales.
4. Casos de uso ideales: Escenarios reales y concretos de la industria donde conviene adoptar cada una.
5. Common Pitfalls (Errores comunes): Errores típicos y malentendidos que cometen los equipos de ingeniería al adoptar y desplegar cada una de estas tecnologías.



---

### Respuesta del LLM

Máquinas Virtuales
Una VM opera sobre un hipervisor que abstrae el hardware físico y presenta hardware virtualizado a cada instancia. Existen dos tipos:
Type-1 (bare-metal): el hipervisor corre directamente sobre el hardware sin OS anfitrión. Ejemplos: VMware ESXi, Microsoft Hyper-V, Xen. Es el modelo estándar en centros de datos empresariales y clouds como AWS EC2 (Nitro), GCP y Azure.
Type-2 (hosted): el hipervisor corre sobre un OS anfitrión como una aplicación más. Ejemplos: VirtualBox, VMware Workstation. Usado principalmente en desarrollo local.
Cada VM contiene:

Su propio kernel de OS
Drivers virtualizados (vNIC, vDisk, vCPU)
Stack completo de userspace

La interacción con el hardware real ocurre a través del hipervisor, que traduce las instrucciones privilegiadas del guest kernel usando técnicas como trap-and-emulate, binary translation (en x86 sin VT-x) o hardware-assisted virtualization (Intel VT-x / AMD-V), donde las instrucciones privilegiadas se ejecutan directamente en modo VMX root del CPU sin traducción.
Contenedores
Los contenedores no virtualizan hardware. Comparten el kernel del host y usan dos primitivas del kernel de Linux para aislamiento y gestión de recursos:
Namespaces — aíslan la vista del sistema para cada contenedor:

pid — árbol de procesos propio
net — interfaces de red y tablas de ruteo propias
mnt — sistema de archivos montado propio
uts — hostname propio
ipc — colas de mensajes y semáforos aislados
user — mapeo de UIDs (permite ser "root" dentro sin serlo fuera)

cgroups (Control Groups) — limitan y contabilizan el uso de recursos: CPU, RAM, I/O de disco, ancho de banda de red. Sin cgroups, un contenedor podría consumir todos los recursos del host.
El motor de contenedores (Docker Engine, containerd) orquesta la creación de namespaces, la configuración de cgroups, el montaje de capas del sistema de archivos (overlay2, devicemapper) y la ejecución del proceso inicial del contenedor.
2. Ventajas de las Máquinas Virtuales
Aislamiento de kernel completo. Cada VM tiene su propio kernel. Una vulnerabilidad en el kernel de una VM no compromete las demás ni el hipervisor (salvo exploits de VM escape, que son extremadamente raros en hipervisores maduros). Esto es crítico en entornos multitenant donde diferentes clientes comparten el mismo hardware físico, como en clouds públicos.
Soporte de sistemas operativos heterogéneos. Una VM puede correr Windows Server 2019, RHEL 7, FreeBSD y Ubuntu en el mismo host físico simultáneamente. Los contenedores solo pueden correr workloads compatibles con el kernel del host — no es posible correr un contenedor con kernel de Windows en un host Linux sin VMs de por medio.
Compatibilidad con software legacy. Aplicaciones que requieren drivers de kernel específicos, módulos del OS, o que asumen un entorno de OS completo (como SAP, Oracle DB con configuraciones de kernel particulares) funcionan en VMs sin modificaciones.
Seguridad certificable y compliance. Regulaciones como PCI-DSS nivel 1, HIPAA y FedRAMP frecuentemente exigen aislamiento de hardware demostrable. Las VMs ofrecen fronteras de seguridad auditables que los contenedores no pueden igualar sin capas adicionales como gVisor o Kata Containers.
Snapshots y rollback de estado completo. Un snapshot de VM captura el estado exacto del disco, memoria y CPU en un instante. Esto permite rollbacks de SO completo en segundos, algo imposible nativamente con contenedores.

3. Ventajas de los Contenedores
Densidad y eficiencia de recursos. Un host con 64 GB de RAM puede correr 5–10 VMs o varios cientos de contenedores. Al compartir el kernel y no cargar un OS completo por instancia, el overhead por workload se reduce drásticamente. En OpenLib Market, esto significa más réplicas del servicio de catálogo por el mismo costo de infraestructura.
Arranque en milisegundos. Un contenedor arranca en el tiempo que tarda en iniciar el proceso principal de la aplicación — típicamente menos de 1 segundo para una app Go o Java ya compilada. Una VM necesita pasar por BIOS/UEFI virtual, bootloader, inicio del kernel, servicios del OS. Esto hace a los contenedores la única opción viable para serverless y autoscaling reactivo.
Portabilidad real. La imagen de contenedor incluye el sistema de archivos, dependencias y configuración de la aplicación. "Funciona en mi máquina" se elimina porque la imagen que corre en el laptop del desarrollador es bit-a-bit la misma que corre en producción. Las VMs tienen imágenes de decenas de GBs atadas a formatos de hipervisor específicos (VMDK, VHD, QCOW2).
CI/CD nativo. Los contenedores encajan perfectamente en pipelines de integración continua: cada commit construye una imagen, la imagen se testea, la imagen se despliega. No hay que esperar el provisioning de una VM. Pipelines que antes tardaban 20 minutos ahora tardan 2.
Inmutabilidad por diseño. Una imagen de contenedor es inmutable — nunca se modifica en producción, se reemplaza. Esto elimina la categoría completa de bugs de "configuration drift" que afectan a VMs que se parchean incrementalmente durante meses o años.

4. Casos de Uso Ideales
Cuándo elegir VMs
Workloads de bases de datos críticas en producción. PostgreSQL, Oracle, SQL Server corriendo en VMs con recursos dedicados (CPU pinning, NUMA awareness) ofrecen performance predecible y aislamiento garantizado. El riesgo del "noisy neighbor" en contenedores sin cgroups bien configurados puede degradar latencias de I/O impredeciblemente.
Entornos regulados multitenant. Un proveedor de servicios financieros que procesa datos de múltiples bancos en el mismo hardware físico necesita aislamiento de kernel demostrable para auditorías. Los contenedores compartiendo kernel no pasan ciertos frameworks de compliance sin capas adicionales.
Software legacy sin containerización viable. Aplicaciones monolíticas de 15 años con dependencias de kernel, drivers propietarios o configuraciones de OS específicas que no pueden modificarse (por licenciamiento o riesgo operacional) viven en VMs indefinidamente.
Ambientes de desarrollo con SO múltiples. Un equipo que desarrolla software para Windows, macOS y Linux simultáneamente usa VMs para garantizar paridad de entorno con producción en cada SO objetivo.
Cuándo elegir Contenedores
Microservicios con escalado horizontal. El servicio de búsqueda de OpenLib Market puede tener 2 réplicas en horario nocturno y 50 en hora pico, escalando en segundos con Kubernetes HPA. Con VMs, cada réplica nueva tarda minutos en arrancar y consume GBs de RAM.
CI/CD y ambientes efímeros. Cada pull request levanta un ambiente de pruebas completo en contenedores, corre los tests y se destruye. El costo es mínimo comparado con VMs dedicadas por ambiente.
Serverless y funciones event-driven. AWS Lambda, Google Cloud Run y Azure Functions corren contenedores internamente. La capacidad de arrancar en milisegundos es fundamental para que el modelo de billing por invocación sea económicamente viable.
APIs stateless de alto throughput. Servicios REST o gRPC sin estado que necesitan escalar horizontalmente son el caso de uso canónico de contenedores orquestados con Kubernetes.

5. Common Pitfalls
Errores comunes con VMs
Sobreprovisionar por defecto. Los equipos asignan 8 vCPUs y 32 GB RAM a cada VM "por si acaso", resultando en hosts con 15% de utilización promedio. El costo se multiplica sin beneficio operacional. La práctica correcta es comenzar conservador y monitorear con herramientas como vSphere Performance Charts o CloudWatch para ajustar.
Tratar snapshots como backups. Un snapshot de VM captura el estado en un momento, pero vive en el mismo datastore que la VM. Si el datastore falla, se pierden la VM y el snapshot. Los backups reales requieren replicación a almacenamiento separado con herramientas como Veeam o AWS Backup.
Ignorar VM sprawl. Es trivial crear VMs y difícil recordar apagarlas. Organizaciones con cientos de VMs "zombie" corriendo workloads terminados desde hace meses generan costos silenciosos de miles de dólares mensuales. Requiere gobernanza activa con tagging, lifecycle policies y revisiones periódicas.
No configurar recursos de CPU correctamente. En VMware, el ratio de vCPU a pCPU puede llegar a 8:1 o más. Sin límites (CPU reservations/limits), una VM bajo carga puede robar ciclos de otras. En producción crítica, las CPU reservations deben configurarse explícitamente.
Parchear el OS dentro de la VM sin proceso. Aplicar patches directamente en VMs de producción sin snapshot previo, sin ambiente de staging y sin ventana de mantenimiento es una causa frecuente de outages. Las VMs facilitan esta mala práctica porque parecen "máquinas reales".

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

[En este caso el prompt no estuvo del todo correcto y genero preguntas que podriamos decir que estan mal redactadas.]


#### 2. ¿Qué se puede mejorar?

[tener ideas mas claras, ser mas conciso y tener una mejor resolucion de preguntas.]


#### 3. Respuesta final

[Escribe tu respuesta definitiva a la pregunta del parcial, integrando lo que aprendiste del LLM pero yendo más allá. Corrige errores, llena omisiones, conecta con conceptos vistos en clase. Esta es tu respuesta: demuestra que tú dominas el tema.]
