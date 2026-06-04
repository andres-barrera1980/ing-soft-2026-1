# Plantilla de entrega — Parcial 2
---

## Pregunta [10]: [P10_vm_vs_containers]

### Estudiante
- **Nombre completo**: Samuel López Gómez

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Claude |
| **Modelo específico** |Sonnet 4.6 |
| **¿Por qué elegiste este LLM?** | Para temas de DevOps e infraestructura se usa Claude sonnet 4.6 suele organizar las tablas comparativas de manera muy clara y no escatiman en detalles técnicos  |

---

### Prompt utilizado

Actúa como un Arquitecto Cloud / DevOps Senior definiendo la estrategia de despliegue para el proyecto OpenLib Market (una plataforma e-commerce de libros). El equipo está debatiendo si desplegar usando Máquinas Virtuales (VMs) o Contenedores (ej. Docker).

Necesito que compares Máquinas Virtuales y Contenedores abordando exactamente los siguientes puntos:
1. Arquitectura: ¿Cómo funciona cada uno a bajo nivel?
2. Ventajas específicas de las VMs sobre los contenedores.
3. Ventajas específicas de los contenedores sobre las VMs.
4. Casos de uso ideales: ¿En qué escenario de OpenLib Market usarías VMs y en cuál Contenedores?
5. Common pitfalls: ¿Cuáles son los errores más comunes al adoptar cada una de estas tecnologías?

Restricciones:
- Sé preciso técnicamente sobre cómo se maneja el Kernel del Sistema Operativo en cada enfoque.
- Incluye aspectos de seguridad, aislamiento y consumo de recursos.

Formato de salida esperado: Markdown. Debes comenzar obligatoriamente con una "Tabla Comparativa" de alto nivel y luego desarrollar las explicaciones detalladas para cada uno de los 5 puntos solicitados.

```

---

### Respuesta del LLM
# VMs vs Contenedores — Estrategia de Despliegue OpenLib Market

> **Rol:** Arquitecto Cloud / DevOps Senior  
> **Proyecto:** OpenLib Market · Plataforma e-commerce de libros  
> **Objetivo:** Definir la estrategia de despliegue óptima comparando Máquinas Virtuales y Contenedores

---

## Tabla Comparativa

| Dimensión | Máquinas Virtuales (VMs) | Contenedores (Docker / OCI) |
|---|---|---|
| **Unidad de abstracción** | Hardware virtualizado completo | Proceso aislado sobre el kernel del host |
| **Kernel del SO** | Cada VM tiene su propio kernel | Comparten el kernel del host |
| **Hypervisor** | Tipo 1 (bare-metal) o Tipo 2 (hosted) | No aplica; usa namespaces + cgroups |
| **Tiempo de arranque** | 30 seg – varios minutos | Milisegundos a segundos |
| **Tamaño de imagen** | Gigabytes (incluye SO completo) | Megabytes (solo dependencias de la app) |
| **Densidad por host** | Decenas de VMs | Cientos a miles de contenedores |
| **Aislamiento** | Fuerte (hardware virtualizado) | Moderado (namespaces de kernel) |
| **Overhead de recursos** | Alto (CPU/RAM para cada SO invitado) | Bajo (overhead de proceso) |
| **Portabilidad** | Parcial (ligada al hypervisor) | Alta (cualquier runtime OCI-compatible) |
| **Seguridad de superficie** | Menor superficie de ataque compartida | Kernel compartido amplía superficie |
| **Gestión de estado** | Stateful por naturaleza | Stateless por convención (volúmenes externos) |
| **Costo estimado** | Mayor (más recursos por unidad) | Menor (mejor utilización del hardware) |
| **Orquestación** | VMware vSphere, AWS EC2 Auto Scaling | Kubernetes, Docker Swarm, ECS |
| **Ideal para OpenLib** | BD, servicios legacy, aislamiento PCI-DSS | API, microservicios, workers asíncronos |

---

## 1. Arquitectura: funcionamiento a bajo nivel

### Máquinas Virtuales

Una VM es una **emulación completa de hardware** administrada por un **hypervisor**. Existen dos tipos:

- **Tipo 1 (bare-metal):** El hypervisor corre directamente sobre el hardware físico, sin SO anfitrión. Ejemplos: VMware ESXi, Microsoft Hyper-V, KVM (Linux). Es el modelo que usan AWS EC2, Azure VMs y GCP Compute Engine a nivel de infraestructura.
- **Tipo 2 (hosted):** El hypervisor es una aplicación sobre un SO anfitrión. Ejemplos: VirtualBox, VMware Workstation. Menos usado en producción.

**Stack completo (Tipo 1):**

```
┌──────────────────────────────────────────────────────────┐
│  VM 1                    │  VM 2                         │
│  ┌────────────────────┐  │  ┌────────────────────┐       │
│  │  App (OpenLib API) │  │  │  PostgreSQL         │       │
│  ├────────────────────┤  │  ├────────────────────┤       │
│  │  Libs / Runtime    │  │  │  Libs               │       │
│  ├────────────────────┤  │  ├────────────────────┤       │
│  │  SO Invitado       │  │  │  SO Invitado        │       │
│  │  (kernel propio)   │  │  │  (kernel propio)    │       │
│  └────────────────────┘  │  └────────────────────┘       │
├──────────────────────────────────────────────────────────┤
│  Hypervisor (KVM / ESXi / Hyper-V)                       │
├──────────────────────────────────────────────────────────┤
│  Hardware físico (CPU, RAM, NIC, Disco)                  │
└──────────────────────────────────────────────────────────┘
```

**Manejo del kernel:** cada VM ejecuta su propio kernel (Linux 5.x, Windows Server, etc.). El hypervisor intercepta las instrucciones privilegiadas mediante **trap-and-emulate** o extensiones de virtualización por hardware (Intel VT-x / AMD-V). Esto garantiza que ningún kernel invitado puede acceder directamente al hardware real.

---

### Contenedores

Un contenedor **no virtualiza hardware**. Es un proceso del SO anfitrión al que se le aplican restricciones y una vista aislada del sistema usando dos primitivas del kernel Linux:

- **Namespaces:** proporcionan aislamiento de visibilidad. Tipos relevantes:
  - `pid` — árbol de procesos propio
  - `net` — interfaz de red propia (veth pair + bridge)
  - `mnt` — sistema de archivos propio (UnionFS / OverlayFS)
  - `uts` — hostname propio
  - `ipc` — colas de mensajes y semáforos aislados
  - `user` — mapeo de UIDs (rootless containers)

- **cgroups (Control Groups):** imponen límites de consumo de recursos: CPU, RAM, disco I/O, red. Son el mecanismo que evita que un contenedor monopolice el host.

**Stack completo:**

```
┌────────────────────┐  ┌────────────────────┐  ┌──────────────────┐
│ Contenedor 1       │  │ Contenedor 2        │  │ Contenedor 3     │
│ OpenLib API (Java) │  │ Redis Cache         │  │ Audit Worker     │
│ + JRE 21           │  │                     │  │ + JRE 21         │
│ + libs propias     │  │                     │  │                  │
└────────────────────┘  └────────────────────┘  └──────────────────┘
         │ namespaces + cgroups │ namespaces + cgroups │
┌──────────────────────────────────────────────────────────┐
│  Container Runtime  (containerd / runc / podman)         │
├──────────────────────────────────────────────────────────┤
│  Kernel del Host  ← COMPARTIDO por todos los contenedores│
├──────────────────────────────────────────────────────────┤
│  Hardware físico                                         │
└──────────────────────────────────────────────────────────┘
```

**Manejo del kernel:** todos los contenedores comparten el kernel del host. Las syscalls de cada contenedor llegan directamente al kernel, filtradas por **seccomp** (filtro de syscalls peligrosas) y **AppArmor / SELinux** (políticas de acceso mandatorio). No hay trap-and-emulate; por eso el overhead es mínimo.

> **Implicación crítica:** si el kernel del host tiene una vulnerabilidad de escalada de privilegios (ej. `Dirty COW`, `runc CVE-2019-5736`), un contenedor comprometido puede afectar al host. Con VMs, esa vulnerabilidad quedaría confinada al SO invitado.

---

## 2. Ventajas específicas de las VMs

### Aislamiento de seguridad de nivel hardware

El hypervisor actúa como barrera física entre los SO invitados. Un exploit que comprometa el kernel de una VM no tiene vector directo hacia el host ni hacia otras VMs. Esto es fundamental para **OpenLib Market** si procesa pagos: el estándar **PCI-DSS** recomienda explícitamente aislamiento fuerte para los componentes del cardholder data environment (CDE).

### Kernel personalizado y heterogeneidad de SO

Cada VM puede correr un SO completamente distinto. Es posible ejecutar Windows Server 2022 junto con Ubuntu 22.04 en el mismo host físico. Esto permite correr dependencias legacy (ej. una librería de pagos que solo tiene binario para Windows) sin modificar el sistema base.

### Snapshots y rollback de estado completo

Los hypervisor modernos soportan snapshots atómicos de disco + memoria RAM. Un rollback a un estado anterior es cuestión de segundos, incluyendo el estado del sistema de archivos, configuraciones de red y memoria de la aplicación. En contenedores, el equivalente requiere combinar imágenes versionadas + backups externos de volúmenes.

### Cumplimiento regulatorio y auditoría

Los marcos de cumplimiento como **SOC 2 Type II**, **ISO 27001** y **PCI-DSS** tienen controles bien establecidos para VMs. El aislamiento de red entre VMs (via VLANs, VMware NSX, etc.) es auditable y demostrable frente a auditores externos con tooling maduro.

### Predictibilidad de rendimiento (noisy neighbor controlado)

Con cgroups bien configurados, los contenedores también limitan recursos, pero comparten el scheduler del kernel. Las VMs con **CPU pinning** (asignación de núcleos físicos dedicados) garantizan latencia predecible incluso bajo carga extrema del host. Crítico para la base de datos transaccional de OpenLib.

---

## 3. Ventajas específicas de los Contenedores

### Arranque instantáneo y escalado horizontal rápido

Un contenedor nuevo del API de OpenLib está operativo en menos de 2 segundos. Una VM equivalente tarda entre 45 segundos y 3 minutos (boot del kernel, init del SO, arranque de la JVM). En escenarios de Black Friday o picos de tráfico, Kubernetes puede escalar de 3 a 30 réplicas del API en segundos, algo imposible con VMs en el mismo tiempo.

### Densidad y eficiencia de recursos

Un host con 32 GB de RAM puede correr cómodamente 200+ contenedores de microservicios livianos. El mismo host con VMs sería capaz de correr unas 10–15 VMs (dado el overhead de 1–2 GB de RAM por SO invitado). Para OpenLib, esto se traduce directamente en **menor costo mensual de infraestructura cloud**.

### Inmutabilidad e infraestructura como código

Una imagen Docker es un artefacto inmutable y versionado. El mismo artefacto que pasó por el pipeline de CI/CD es exactamente el que corre en producción. Esto elimina la categoría entera de bugs de "funciona en mi máquina" y simplifica las auditorías de seguridad: si la imagen firmada es la misma en staging y prod, hay trazabilidad completa.

### Ecosistema DevOps y velocidad de entrega

La integración entre contenedores y herramientas CI/CD (GitHub Actions, GitLab CI, ArgoCD) es nativa. Un ciclo completo de build → test → push → deploy para OpenLib puede ocurrir en menos de 5 minutos. Con VMs, el equivalente requiere templates de VM, scripts de provisioning (Packer, Ansible) y tiempos de boot que alargan el ciclo.

### Gestión de dependencias aislada por servicio

Cada contenedor de OpenLib puede tener su propio JRE (el API usa Java 21, el worker legacy usa Java 11) sin conflictos. En VMs, este escenario requeriría VMs separadas o gestión manual de versiones de runtime con herramientas como SDKMAN.

### Portabilidad entre clouds y on-premise

Una imagen OCI (`docker.io/openlib/api:1.4.2`) corre sin modificación en AWS ECS, Google Cloud Run, Azure AKS, o en un servidor on-premise. Migrar de AWS a GCP es cambiar URLs del registry, no re-provisionar infraestructura.

---

## 4. Casos de uso ideales para OpenLib Market

### Cuándo usar VMs en OpenLib Market

**Base de datos PostgreSQL (OLTP)**

La base de datos transaccional de OpenLib (órdenes, inventario, usuarios) debe correr en una VM dedicada o en un servicio managed (RDS/Cloud SQL, que internamente usa VMs). Las razones son:

- **I/O predecible:** CPU pinning y discos NVMe dedicados garantizan latencia P99 estable.
- **Aislamiento de datos PCI-DSS:** si se almacenan datos de pago, el CDE debe estar en una zona de aislamiento fuerte que las VMs proveen más naturalmente.
- **Snapshots transaccionales:** los backups point-in-time de PostgreSQL combinados con snapshots de VM son el gold standard de recuperabilidad.

**Servidor de archivos / CDN de portadas de libros**

Las imágenes de portadas son assets estáticos de alto volumen. Un servidor de almacenamiento (MinIO, NFS) en una VM con almacenamiento attached es predecible en IOPS y más fácil de auditar que volúmenes de contenedores.

**Componentes con requerimientos de seguridad estrictos (pasarela de pagos)**

El módulo que integra con Stripe/PayU debe cumplir PCI-DSS. Desplegarlo en una VM en una VLAN aislada, con firewall dedicado y auditoría de accesos, satisface más fácilmente los controles requeridos que un pod de Kubernetes en un cluster compartido.

---

### Cuándo usar Contenedores en OpenLib Market

**API REST (BookController, OrderController, AuthController)**

El API de OpenLib es el componente con mayor variabilidad de carga. Kubernetes + HPA (Horizontal Pod Autoscaler) puede escalar réplicas en segundos basándose en métricas de CPU o de requests/seg. El código ya estructura el backend como un monolito Spring Boot, perfectamente empaquetable en una imagen Docker.

```dockerfile
# Dockerfile optimizado para OpenLib API (Java 21)
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app
COPY --from=build /app/target/openlib-backend.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-XX:+UseZGC", "-jar", "app.jar"]
```

**Workers asíncronos (notificaciones, auditoría)**

Los observadores diseñados (`EmailWishlistObserver`, `RedisCacheObserver`, `AuditLogObserver`) son candidatos ideales para contenedores independientes y stateless. Kubernetes puede escalar o reiniciar workers fallidos sin intervención humana.

**Redis (caché de búsquedas)**

Redis corre excelentemente en contenedor para entornos no críticos. Su estado es efímero por diseño (caché invalidable). En producción, puede usarse ElastiCache (managed) o Redis en contenedor con un PersistentVolume.

**Entornos de desarrollo y QA**

`docker-compose.yml` permite levantar toda la plataforma OpenLib (API + BD + Redis) en una sola máquina de desarrollo en segundos, eliminando la necesidad de VMs locales por desarrollador.

---

### Arquitectura híbrida recomendada para OpenLib Market

```
                        Internet
                           │
                    ┌──────▼───────┐
                    │  Load Balancer│
                    │  (Nginx/ALB)  │
                    └──────┬───────┘
                           │
          ┌────────────────▼────────────────┐
          │     Kubernetes Cluster           │
          │  ┌──────────┐  ┌─────────────┐  │
          │  │ API Pods │  │Worker Pods  │  │   ← CONTENEDORES
          │  │ (x3-10)  │  │Email/Push/  │  │
          │  └──────────┘  │Audit (x2-5) │  │
          │                └─────────────┘  │
          │  ┌──────────────────────────┐   │
          │  │  Redis (StatefulSet)     │   │
          └──┤──────────────────────────┤───┘
             │                          │
   ┌─────────▼──────────┐  ┌────────────▼────────────┐
   │  VM: PostgreSQL     │  │  VM: Pasarela de Pagos  │  ← VMs
   │  (RDS / dedicated)  │  │  (zona PCI-DSS aislada) │
   └─────────────────────┘  └─────────────────────────┘
```

---

## 5. Common Pitfalls

### Errores comunes al adoptar VMs

**Sprawl de VMs sin inventario**  
Es fácil aprovisionar una VM y olvidarla. En AWS, una instancia EC2 `m5.large` corriendo 24/7 sin carga activa cuesta ~70 USD/mes. Equipos sin gobernanza acaban con docenas de VMs "huérfanas". Solución: etiquetado obligatorio (`project`, `owner`, `environment`) y alertas de costo.

**Imágenes base no actualizadas (patch debt)**  
Una VM creada con Ubuntu 20.04 en 2021 y nunca actualizada acumula cientos de CVEs. A diferencia de contenedores (donde reconstruir la imagen actualiza todas las dependencias), parchear VMs requiere procesos manuales o herramientas como AWS Systems Manager Patch Manager.

**Over-provisioning de recursos**  
El patrón típico: "le doy 8 vCPUs y 32 GB por si acaso". El resultado es que el 80% del tiempo la VM usa el 10% de los recursos asignados. En cloud, esto se paga íntegramente. Con contenedores + cgroups, el ajuste fino es granular y dinámico.

**Confundir snapshots con backups**  
Los snapshots de VM capturan estado de disco pero no son backups offsite. Un snapshot en el mismo disco físico que la VM no protege contra falla de hardware. Para PostgreSQL en OpenLib, se necesita WAL archiving a S3 + snapshots como capas complementarias.

**VMs como pets, no como cattle**  
Hacer SSH a una VM de producción y modificar configuraciones manualmente genera drift de estado. Esa VM se vuelve irremplazable. Solución: tratar las VMs como inmutables usando Packer para crear imágenes base y Terraform para el ciclo de vida.

---

### Errores comunes al adoptar Contenedores

**Correr procesos como root dentro del contenedor**  
Por defecto, muchas imágenes Docker ejecutan como `root` (UID 0). Si hay una vulnerabilidad de escape de contenedor, el atacante tiene privilegios de root en el host. Para OpenLib, todos los Dockerfiles deben incluir:
```dockerfile
RUN addgroup -S openlib && adduser -S openlib -G openlib
USER openlib
```

**Almacenar estado en el filesystem del contenedor**  
Un contenedor reiniciado pierde todo lo escrito en su filesystem. Guardar logs, uploads de portadas o datos de sesión dentro del contenedor es un antipatrón. Todo estado persistente debe ir en volúmenes externos (`PersistentVolumeClaim` en Kubernetes, S3 para assets).

**Imágenes de producción con herramientas de debug**  
Incluir bash, curl, wget, o peor aún, un JDK completo en lugar de un JRE en la imagen de producción amplía la superficie de ataque innecesariamente. Usar multi-stage builds:
```dockerfile
FROM maven:3.9-eclipse-temurin-21 AS build
# ... compilación ...

FROM eclipse-temurin:21-jre-alpine AS runtime  # Solo JRE, Alpine minimal
COPY --from=build /app/target/*.jar app.jar
```

**No configurar resource limits en Kubernetes**  
Sin `requests` y `limits` de CPU/memoria en el pod spec, un contenedor con memory leak puede agotar la RAM del nodo entero y tumbar todos los pods co-localizados. Ejemplo para el API de OpenLib:
```yaml
resources:
  requests:
    memory: "512Mi"
    cpu: "250m"
  limits:
    memory: "1Gi"
    cpu: "1000m"
```

**Usar `latest` como tag de imagen en producción**  
El tag `latest` es mutable: `openlib/api:latest` hoy puede ser la versión 1.4.2 y mañana la 1.5.0 con breaking changes. Siempre usar tags inmutables basados en SHA del commit (`openlib/api:sha-a3f8c12`) o versión semántica (`openlib/api:1.4.2`).

**Ignorar el contexto de seguridad del cluster**  
Un cluster Kubernetes sin `PodSecurityAdmission`, sin Network Policies y con service accounts con permisos excesivos es una superficie de ataque enorme. Para OpenLib, definir Network Policies que solo permitan al pod del API comunicarse con el pod de Redis y la BD, y nada más.

**Logs a stdout sin estructura**  
Escribir logs como strings libres a stdout dificulta el parsing en herramientas como Datadog o ELK. Configurar Logback en OpenLib para emitir JSON estructurado desde el inicio:
```xml
<!-- logback-spring.xml -->
<appender name="JSON" class="ch.qos.logback.core.ConsoleAppender">
  <encoder class="net.logstash.logback.encoder.LogstashEncoder"/>
</appender>
```

---

## Recomendación final

Para OpenLib Market en su estado actual, la recomendación es una **arquitectura híbrida pragmática**:

1. **Contenedores (Kubernetes) para todo el plano de aplicación:** API, workers de notificaciones, Redis, reverse proxy. Velocidad de entrega, escalado automático y reducción de costos son el diferenciador competitivo.

2. **VMs o servicios managed (RDS, Cloud SQL) para el plano de datos:** PostgreSQL y cualquier componente que toque datos de pago o requiera cumplimiento regulatorio. El costo extra se justifica por la predictibilidad, el aislamiento y la auditoría.

3. **Infraestructura como código desde el día 1:** Terraform para VMs y cluster EKS/GKE, Helm charts para manifiestos de Kubernetes. Ninguna configuración manual en producción.

Esta combinación aprovecha lo mejor de cada tecnología y establece una base sobre la que OpenLib puede escalar sin reescribir su arquitectura de despliegue.

---

*Elaborado para OpenLib Market · Arquitectura Cloud/DevOps Senior · Java 21 · Spring Boot*
```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?



#### 2. ¿Qué se puede mejorar?



#### 3. Respuesta final
