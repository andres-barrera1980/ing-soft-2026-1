# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

---

## Pregunta [10]: [vm vs containers]

### Estudiante
- **Nombre completo**: [Juan Fernando Herrera Patiño]

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [ChatGPT] |
| **Modelo específico** | [GPT-4o ]|
| **¿Por qué elegiste este LLM?** | [mas comodo para mi] |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

Actúa como un experto en sistemas operativos, virtualización y computación en la nube. Compara detalladamente máquinas virtuales (VMs) y contenedores considerando: (a) la arquitectura y funcionamiento interno de cada tecnología, explicando el papel del hipervisor en las VMs y del kernel compartido en los contenedores; (b) las ventajas de las máquinas virtuales frente a los contenedores; (c) las ventajas de los contenedores frente a las máquinas virtuales; (d) casos de uso reales donde conviene utilizar cada tecnología; y (e) errores comunes o pitfalls al adoptar VMs o contenedores. Presenta la respuesta primero en una tabla comparativa y luego desarrolla explicaciones técnicas adicionales. Incluye aspectos de rendimiento, consumo de recursos, portabilidad, aislamiento, seguridad, escalabilidad, tiempo de arranque y administración. También menciona explícitamente los riesgos de seguridad asociados al uso de contenedores por compartir el kernel del sistema operativo anfitrión.
```

---

### Respuesta del LLM

Comparación entre Máquinas Virtuales (VMs) y Contenedores
Tabla Comparativa
Aspecto	Máquinas Virtuales (VMs)	Contenedores
Arquitectura	Virtualizan hardware completo mediante un hipervisor.	Virtualizan a nivel de sistema operativo compartiendo el kernel del host.
Componente clave	Hipervisor (Type 1 o Type 2).	Runtime de contenedores (Docker, containerd, CRI-O).
Sistema Operativo	Cada VM tiene su propio SO invitado.	Todos comparten el kernel del sistema anfitrión.
Aislamiento	Muy fuerte. Cada VM está aislada completamente.	Más ligero, pero menos aislado porque comparte kernel.
Rendimiento	Mayor sobrecarga (overhead).	Muy cercano al rendimiento nativo.
Consumo de RAM	Alto. Cada VM carga un SO completo.	Bajo. Solo ejecuta procesos y librerías necesarias.
Uso de CPU	Mayor debido a la virtualización del hardware.	Menor, ya que ejecuta procesos directamente sobre el kernel.
Tiempo de arranque	Segundos o minutos.	Milisegundos o pocos segundos.
Portabilidad	Buena, pero las imágenes suelen ser grandes.	Excelente; las imágenes son ligeras y fáciles de mover.
Escalabilidad	Más lenta. Crear nuevas VMs consume recursos.	Muy rápida. Permite escalar cientos de instancias rápidamente.
Seguridad	Más robusta por el aislamiento completo.	Menor aislamiento; un problema en el kernel puede afectar múltiples contenedores.
Tamaño típico	GBs.	MBs.
Administración	Más compleja.	Más sencilla con orquestadores como Kubernetes.
Compatibilidad de SO	Permite ejecutar distintos sistemas operativos simultáneamente.	Deben compartir el mismo tipo de kernel.
Casos ideales	Sistemas heterogéneos, aplicaciones legacy, alta seguridad.	Microservicios, DevOps, CI/CD, aplicaciones cloud-native.
(a) Arquitectura y Funcionamiento Interno
Máquinas Virtuales (VMs)

Una máquina virtual simula una computadora completa.

Arquitectura
Aplicaciones
      │
Sistema Operativo Invitado
      │
Hipervisor
      │
Hardware Físico
Papel del Hipervisor

El hipervisor es la capa que permite crear y administrar máquinas virtuales.

Tipos:

Hipervisor Tipo 1 (Bare Metal)

Se ejecuta directamente sobre el hardware.

Ejemplos:

VMware ESXi
Microsoft Hyper-V
Xen
Hardware
   ↓
Hipervisor
   ↓
VMs

Ventaja:

Mejor rendimiento.
Mayor seguridad.
Hipervisor Tipo 2

Se ejecuta sobre un sistema operativo anfitrión.

Ejemplos:

Oracle VM VirtualBox
VMware Workstation
Hardware
   ↓
Host OS
   ↓
Hipervisor
   ↓
VMs
Contenedores

Los contenedores no virtualizan hardware.

Virtualizan procesos.

Arquitectura
Aplicaciones
      │
Contenedores
      │
Runtime (Docker/containerd)
      │
Kernel Compartido
      │
Sistema Operativo Host
Kernel Compartido

Todos los contenedores utilizan el mismo kernel del sistema operativo anfitrión.

Por ejemplo:

Ubuntu Host
     │
Kernel Linux
     │
 ┌───────┬───────┬───────┐
 │Cont A │Cont B │Cont C │
 └───────┴───────┴───────┘

Los contenedores usan:

Namespaces (aislamiento)
cgroups (control de recursos)

para aparentar que son entornos independientes.

(b) Ventajas de las Máquinas Virtuales
1. Mayor aislamiento

Cada VM posee:

Kernel propio
Sistema operativo propio
Espacio de memoria independiente

Una falla en una VM normalmente no afecta a las demás.

2. Mejor seguridad

El hipervisor agrega una capa adicional de protección.

Ideal para:

Bancos
Entornos gubernamentales
Infraestructura crítica
3. Sistemas operativos diferentes

Se puede ejecutar simultáneamente:

Windows Server
Linux
FreeBSD

en el mismo hardware.

4. Compatibilidad con aplicaciones legacy

Muchas aplicaciones antiguas requieren:

Versiones específicas de Windows.
Drivers específicos.
Configuraciones incompatibles con contenedores.

Las VMs son ideales en estos casos.

5. Snapshots completos

Permiten guardar el estado exacto de toda la máquina.

(c) Ventajas de los Contenedores
1. Arranque extremadamente rápido

VM:

30 segundos - 2 minutos

Contenedor:

100 ms - 5 segundos
2. Menor consumo de recursos

No necesitan:

Kernel propio.
Sistema operativo completo.

Esto permite ejecutar muchos más contenedores que VMs en el mismo servidor.

3. Mejor densidad

Ejemplo:

Servidor:
32 GB RAM

Podría alojar:

8-15 VMs

o

100+ contenedores

dependiendo de la carga.

4. Ideal para CI/CD

Los pipelines pueden:

Construir
↓
Probar
↓
Desplegar

en segundos.

5. Cloud-Native

Funcionan perfectamente con:

Kubernetes
Docker
(d) Casos de Uso Reales
Cuándo usar Máquinas Virtuales
Centros de datos corporativos

Ejemplo:

ERP empresarial
Base de datos Oracle
Active Directory
Aplicaciones Legacy

Ejemplo:

Sistema bancario de 15 años
Windows Server 2008
Multi-OS

Ejemplo:

Windows + Linux + BSD

en un mismo servidor físico.

Alta Seguridad

Ejemplo:

Sistemas financieros
Infraestructura militar
Cuándo usar Contenedores
Microservicios

Ejemplo:

Servicio Usuarios
Servicio Pagos
Servicio Inventario
Servicio Notificaciones
DevOps

Implementaciones continuas.

Computación en la nube

Servicios desplegados en:

Amazon Web Services
Google Cloud
Microsoft Azure
Escalado dinámico

Ejemplo:

Black Friday

o

Cyber Monday

donde se necesitan cientos de instancias adicionales rápidamente.

(e) Errores Comunes (Pitfalls)
Errores con Máquinas Virtuales
1. Sobreaprovisionamiento

Crear demasiadas VMs:

CPU insuficiente
RAM insuficiente
Storage saturado
2. VM Sprawl

Acumulación descontrolada de máquinas virtuales olvidadas.

Consecuencias:

Costos elevados.
Gestión compleja.
3. Creer que más VMs siempre significa más disponibilidad

Sin una arquitectura adecuada puede generarse un único punto de fallo.

Errores con Contenedores
1. Creer que son tan seguros como una VM

No lo son.

Comparten kernel.

2. Ejecutar contenedores como root

Mala práctica frecuente.

Aumenta significativamente la superficie de ataque.

3. Imágenes excesivamente grandes

Ejemplo:

Ubuntu completo
+ herramientas innecesarias
+ paquetes de desarrollo

Generan:

Más consumo.
Más vulnerabilidades.
4. No gestionar persistencia

Los contenedores son efímeros.

Guardar datos críticos dentro del contenedor puede causar pérdida de información.

5. Ignorar límites de recursos

No configurar:

CPU Limits
Memory Limits

puede afectar todo el host.

Riesgos de Seguridad Específicos de los Contenedores

Este es uno de los puntos más importantes.

Kernel Compartido

Todos los contenedores utilizan el mismo kernel.

Kernel Linux
     │
 ┌───┼───┐
 │   │   │
C1  C2  C3

Si un atacante explota una vulnerabilidad del kernel:

Container Escape

podría:

Escapar del contenedor.
Obtener acceso al host.
Comprometer otros contenedores.
Riesgos principales
Container Escape

El atacante sale del contenedor y accede al sistema anfitrión.

Vulnerabilidades del Kernel

Una falla afecta potencialmente a todos los contenedores.

Escalada de Privilegios

Especialmente si el contenedor se ejecuta como root.

Imágenes comprometidas

Descargar imágenes no verificadas puede introducir malware.

Mitigaciones
Ejecutar contenedores sin privilegios.
Mantener el kernel actualizado.
Utilizar imágenes oficiales.
Aplicar políticas de seguridad.
Escanear imágenes continuamente.
Utilizar mecanismos como seccomp, AppArmor o SELinux.
Conclusión

Las máquinas virtuales proporcionan el mejor nivel de aislamiento, compatibilidad y seguridad, a costa de un mayor consumo de recursos y tiempos de arranque más lentos. Los contenedores, por su parte, ofrecen un rendimiento cercano al nativo, menor consumo de recursos, alta portabilidad y escalabilidad, siendo la tecnología dominante para aplicaciones modernas y arquitecturas de microservicios. Sin embargo, al compartir el kernel del sistema anfitrión, los contenedores presentan riesgos de seguridad específicos que deben gestionarse cuidadosamente mediante buenas prácticas de aislamiento, control de privilegios y actualización constante del entorno.

---

### Análisis crítico de la respuesta

El LLM explicó correctamente la diferencia arquitectónica entre máquinas virtuales y contenedores, destacando el papel del hipervisor en las VMs y el kernel compartido en los contenedores. Las ventajas mencionadas son precisas y los casos de uso propuestos son realistas para entornos empresariales y cloud-native. También abordó adecuadamente el problema de seguridad asociado a los contenedores por compartir el kernel y explicó riesgos como container escape y escalación de privilegios. Además, identificó pitfalls relevantes como el VM sprawl, el sobreaprovisionamiento y la mala gestión de recursos en contenedores. Como posible omisión, faltó profundizar más en los mecanismos de aislamiento de Linux (namespaces y cgroups), que suelen ser un concepto importante cuando se estudia cómo funcionan internamente los contenedores.

#### 1. ¿Qué hizo bien el prompt?

El prompt fue efectivo porque solicitó explícitamente una comparación entre máquinas virtuales y contenedores considerando arquitectura, ventajas, desventajas, casos de uso y errores comunes. Además, pidió el resultado en formato de tabla comparativa seguido de explicaciones, lo que permitió obtener una respuesta organizada y fácil de analizar. También fue acertado exigir que se explicara el papel del hipervisor en las VMs y del kernel compartido en los contenedores, ya que esta es la diferencia técnica fundamental entre ambas tecnologías. Finalmente, incluir aspectos como rendimiento, seguridad, portabilidad y escalabilidad ayudó a obtener una visión más completa y útil para el contexto de la materia.



#### 2. ¿Qué se puede mejorar?

El prompt pudo ser más específico al solicitar conceptos vistos en clase como los tipos de hipervisores (Tipo 1 y Tipo 2), el uso de namespaces y cgroups en contenedores, y el impacto de la virtualización en el rendimiento del sistema operativo. También habría sido útil pedir ejemplos prácticos relacionados con entornos empresariales y cloud computing para profundizar los casos de uso. Aunque la respuesta fue correcta, no profundizó demasiado en cómo Kubernetes aprovecha los contenedores ni en algunas limitaciones de compatibilidad entre sistemas operativos que suelen discutirse en clase.



#### 3. Respuesta final

Las máquinas virtuales y los contenedores proporcionan aislamiento para ejecutar aplicaciones, pero difieren en su arquitectura: las VMs virtualizan hardware mediante un hipervisor y ejecutan un sistema operativo completo por instancia, mientras que los contenedores comparten el kernel del host usando mecanismos como namespaces y cgroups. Las VMs ofrecen mayor aislamiento, seguridad y compatibilidad con distintos sistemas operativos, mientras que los contenedores destacan por su rapidez, bajo consumo de recursos y facilidad de escalamiento. Por ello, las VMs son más adecuadas para aplicaciones legacy o entornos de alta seguridad, mientras que los contenedores son ideales para microservicios y aplicaciones cloud-native. La principal limitación de los contenedores es que comparten el kernel, lo que introduce riesgos de seguridad que deben mitigarse mediante buenas prácticas de configuración y aislamiento.

