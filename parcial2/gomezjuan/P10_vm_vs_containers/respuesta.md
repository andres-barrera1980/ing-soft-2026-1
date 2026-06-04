# Pregunta P10: Máquinas Virtuales vs Contenedores

### Estudiante
- **Nombre completo**: Juan Camilo Gomez

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Claude |
| **Modelo específico** | Claude Sonnet 4.6 |
| **¿Por qué elegiste este LLM?** | Para comparaciones técnicas con tabla y explicaciones profundas

---

### Prompt utilizado

```
Soy estudiante de Ingeniería de Software en la Pontificia Universidad Javeriana.
Curso: Fundamentos de Ingeniería de Software. Tema: virtualización y contenerización.

Compara Máquinas Virtuales (VMs) y Contenedores considerando todos estos aspectos:

a) Arquitectura: cómo funciona internamente cada uno, qué es un hypervisor, 
   qué es un container runtime, por qué los contenedores son más livianos
b) Ventajas de VMs sobre contenedores (mínimo 3 ventajas reales)
c) Ventajas de contenedores sobre VMs (mínimo 3 ventajas reales)  
d) Casos de uso concretos donde conviene cada uno (da ejemplos específicos, no genéricos)
e) Common pitfalls: errores comunes al adoptar VMs y errores comunes al adoptar contenedores
f) Seguridad: específicamente el problema de compartir kernel en contenedores vs aislamiento de VMs

Formato de salida:
1. Primero una tabla comparativa con las dimensiones clave
2. Luego secciones de explicación profunda para cada punto

Restricciones:
- Menciona explícitamente el problema de seguridad del kernel compartido en contenedores
- Incluye ejemplos concretos de herramientas (Docker, VMware, KVM, Podman, etc.)
- Responde en markdown con tabla y secciones
```

---

### Respuesta del LLM

## Máquinas Virtuales vs Contenedores

### Tabla comparativa

| Dimensión | Máquinas Virtuales | Contenedores |
|---|---|---|
| **Aislamiento** | Completo (kernel separado) | Parcial (comparten kernel del host) |
| **Arranque** | Minutos | Segundos o milisegundos |
| **Tamaño** | GBs (incluye SO completo) | MBs (solo capas de la app) |
| **Rendimiento** | Overhead por hypervisor (~5-15%) | Casi nativo (1-3% overhead) |
| **Portabilidad** | Alta, pero imágenes pesadas | Muy alta, imágenes livianas |
| **Seguridad** | Muy alta — kernel separado | Media — vulnerabilidad en kernel afecta todos |
| **Gestión** | VMware, VirtualBox, KVM, Hyper-V | Docker, Podman, containerd |
| **Orquestación** | VMware vSphere, OpenStack | Kubernetes, Docker Swarm |
| **Costo infraestructura** | Mayor (más recursos por VM) | Menor (más densidad) |

---

### a) Arquitectura

**Máquinas Virtuales:**
Una VM incluye un Sistema Operativo completo (kernel + userspace) sobre una capa llamada **hypervisor**. El hypervisor puede ser:
- **Tipo 1 (bare-metal)**: corre directamente sobre el hardware. Ejemplos: VMware ESXi, Microsoft Hyper-V, KVM.
- **Tipo 2 (hosted)**: corre sobre un SO host. Ejemplos: VirtualBox, VMware Workstation.

Cada VM tiene su propio kernel, lo que significa aislamiento completo pero también overhead: cada VM reserva RAM y CPU para su propio SO.

**Contenedores:**
Los contenedores comparten el kernel del SO host. Usan características del kernel Linux (**cgroups** para limitar recursos, **namespaces** para aislar procesos, redes y sistemas de archivos). El **container runtime** (Docker Engine, containerd) gestiona el ciclo de vida de los contenedores.

Por eso los contenedores son más livianos: no hay SO completo dentro del contenedor, solo los binarios y librerías que la aplicación necesita.

---

### b) Ventajas de VMs sobre contenedores

1. **Aislamiento de seguridad superior**: Cada VM tiene su propio kernel. Una vulnerabilidad en el kernel no afecta otras VMs. Crítico para entornos multi-tenant donde los inquilinos son mutuamente desconfiables.

2. **Soporte para múltiples sistemas operativos**: Una VM puede correr Windows sobre un host Linux, o macOS (con limitaciones). Los contenedores solo pueden correr el mismo tipo de kernel que el host.

3. **Compatibilidad con software legado**: Aplicaciones que requieren versiones específicas del kernel o drivers propietarios pueden correr en una VM con exactamente el SO correcto, sin modificar el host.

---

### c) Ventajas de contenedores sobre VMs

1. **Velocidad de arranque y densidad**: Un contenedor arranca en milisegundos vs minutos para una VM. En el mismo hardware pueden correr decenas de contenedores vs pocas VMs, porque no hay SO por instancia.

2. **Consistencia entre ambientes**: La imagen de Docker es idéntica en desarrollo, staging y producción. "Funciona en mi máquina" desaparece como excusa.

3. **CI/CD y microservicios**: Los contenedores se construyen, despliegan y escalan en segundos. Son ideales para pipelines de integración continua y arquitecturas de microservicios donde cada servicio se despliega de forma independiente.

---

### d) Casos de uso concretos

**Usar VMs cuando:**
- Proveedores cloud (AWS EC2, GCP Compute Engine): cada cliente tiene su VM con kernel separado por seguridad.
- Correr Windows y Linux en el mismo servidor físico simultáneamente.
- Software legado que requiere un SO específico (ej: aplicación que solo corre en RedHat 6).
- Entornos de escritorio virtual (VDI).

**Usar contenedores cuando:**
- Microservicios en Kubernetes: el servicio de pagos de OpenLib Market como contenedor Docker escalado por K8s.
- CI/CD pipelines: cada build de GitHub Actions corre en un contenedor limpio y efímero.
- Aplicaciones web stateless que necesitan escalar rápidamente según demanda.

---

### e) Common pitfalls

**Pitfalls con VMs:**
- **VM sprawl**: crear VMs sin seguimiento ni gobernanza lleva a tener cientos de VMs "huérfanas" consumiendo recursos.
- **Sobredimensionamiento**: asignar demasiada RAM y CPU a cada VM por precaución, desaprovechando el hardware.
- **Snapshots como backup**: los snapshots de VMs no son backups — si el datastore falla, el snapshot también se pierde.

**Pitfalls con contenedores:**
- **Correr como root dentro del contenedor**: si el proceso del contenedor corre como root y hay una escape vulnerability, el atacante tiene acceso root al host.
- **Imágenes con vulnerabilidades conocidas**: usar `FROM ubuntu:latest` sin escanear la imagen con herramientas como Trivy o Snyk.
- **Estado en el contenedor**: guardar datos dentro del contenedor en lugar de en volúmenes persistentes — al reiniciar el contenedor, los datos se pierden.
- **No limitar recursos**: un contenedor sin `--memory` y `--cpus` puede consumir todos los recursos del host y afectar a los demás.

---

### f) Seguridad: el problema del kernel compartido

Este es el punto más crítico y frecuentemente subestimado. En contenedores, todos comparten el kernel del host. Si una vulnerabilidad del kernel (ej: una falla en la syscall `ptrace`) es explotada desde dentro de un contenedor, el atacante puede:
- Escapar el aislamiento del contenedor (container escape)
- Comprometer el host completo
- Afectar todos los demás contenedores del mismo host

Ejemplos históricos reales: **CVE-2019-5736** (runc vulnerability) permitía a un contenedor malicioso sobreescribir el binario `runc` del host.

Las VMs no tienen este problema: cada VM tiene su propio kernel completamente separado. Una vulnerabilidad en el kernel de una VM no afecta las otras VMs ni el hypervisor (salvo vulnerabilidades del propio hypervisor, que son más raras y muy bien auditadas).

**Mitigaciones para contenedores:**
- Usar namespaces de usuario (rootless containers con Podman).
- Aplicar seccomp profiles para limitar las syscalls disponibles.
- Usar gVisor (sandbox de kernel de Google) para contenedores que necesitan mayor aislamiento.

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

El prompt fue muy específico, enumerar los seis aspectos que debía cubrir evitó que el LLM respondiera de forma genérica. Pedir explícitamente "el problema de seguridad del kernel compartido" fue la restricción más valiosa, ya que es el concepto más frecuentemente omitido.

#### 2. ¿Qué se puede mejorar?

Podría haberse pedido un ejemplo específico para OpenLib Market.

#### 3. Respuesta final

La respuesta del LLM es completa. El concepto que podria haber omitido y que es critico desde la perspectiva del curso es la diferencia fundamental de filosofía de diseño: las VMs virtualizan hardware, los contenedores virtualizan el sistema operativo (la abstracción es un proceso aislado).

Para OpenLib Market en produccion, la arquitectura recomendada sería híbrida: el cluster de Kubernetes (contenedores) correria sobre VMs en AWS EC2, aprovechando el aislamiento del hypervisor para seguridad y la ligereza de los contenedores para velocidad y densidad. Esta es la arquitectura estándar en la industria.