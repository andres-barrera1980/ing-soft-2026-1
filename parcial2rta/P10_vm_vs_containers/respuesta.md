# Plantilla de entrega — Parcial 2

---

## Pregunta 10: Comparar máquinas virtuales y contenedores: diferencias, ventajas, casos de uso

### Estudiante
- **Nombre completo**: Samuel Iregui

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Antigravity |
| **Modelo específico** | Gemini 3.5 Flash |
| **¿Por qué elegiste este LLM?** | Elegi este modelo por comodidad propia y debido a que tengo pago gemini pro y ya tengo todo bien organizado para los diferentes trabajos |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```text
Actúa como un DevOps y arquitecto de infraestructura en la nube senior. Necesito que realices una comparación técnica rigurosa entre Máquinas Virtuales (VMs) y Contenedores.

Tu respuesta debe estructurarse de la siguiente manera:
1. Una tabla comparativa resumida que evalúe criterios como Arquitectura, Aislamiento, Consumo de recursos, Tiempo de inicio, Portabilidad y Sistema Operativo.
2. Una explicación detallada del funcionamiento y arquitectura de ambos, contrastando el papel del Hipervisor frente al Container Engine (como Docker).
3. Ventajas detalladas de las Máquinas Virtuales sobre los Contenedores.
4. Ventajas detalladas de los Contenedores sobre las Máquinas Virtuales.
5. Casos de uso prácticos para cada tecnología en un entorno real de producción.
6. Errores comunes (pitfalls) en la adopción de ambas.

Formato de salida esperado: Tabla Markdown comparativa seguida de explicaciones detalladas en cada sección.
```

---

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

### 1. Tabla Comparativa

| Criterio | Máquinas Virtuales (VMs) | Contenedores (ej. Docker) |
|---|---|---|
| **Arquitectura** | Hardware $\rightarrow$ Host OS $\rightarrow$ Hipervisor $\rightarrow$ Guest OS $\rightarrow$ Apps | Hardware $\rightarrow$ Host OS $\rightarrow$ Container Engine $\rightarrow$ Apps |
| **Aislamiento** | Total (aislamiento a nivel de hardware mediante Hipervisor) | Aislado por software (Kernel compartido mediante namespaces y cgroups) |
| **Sistema Operativo** | Cada VM tiene su propio Sistema Operativo invitado (Guest OS) | Comparten el Sistema Operativo del Host (sin Guest OS) |
| **Consumo de Recursos**| Alto (cada VM reserva memoria, CPU y espacio de disco para su Guest OS) | Muy bajo (solo consumen los recursos necesarios para el proceso de la App) |
| **Tiempo de Inicio** | Minutos (debe arrancar y bootear el OS completo) | Milisegundos o segundos (inician como un proceso del Host) |
| **Portabilidad** | Limitada por el hipervisor y el formato de la VM (ej. OVA, VMDK) | Altísima (cualquier host con el motor de contenedores puede correr la imagen) |

---

### 2. Funcionamiento y Arquitectura

#### Máquinas Virtuales
Las VMs se ejecutan sobre un **Hipervisor** (ya sea de Tipo 1, sobre el hardware directo como ESXi, o Tipo 2, sobre un OS anfitrión como VirtualBox). El hipervisor engaña a la máquina virtual haciéndole creer que tiene hardware físico dedicado (CPU, memoria, discos virtuales). Cada máquina virtual requiere instalar un Sistema Operativo completo (Guest OS). Esto genera una capa de abstracción pesada pero altamente aislada.

#### Contenedores
Los contenedores no virtualizan el hardware, sino el Sistema Operativo. Corren sobre un **Container Engine** (como Docker, containerd o Podman). En lugar de simular hardware, el motor utiliza características del kernel de Linux como **Namespaces** (para aislar procesos, red y discos) y **Control Groups (cgroups)** (para limitar los recursos físicos que puede consumir cada contenedor). Los contenedores son procesos aislados dentro del mismo kernel del OS anfitrión.

---

### 3. Ventajas de las VMs sobre los Contenedores

1.  **Aislamiento y Seguridad Robustos**: Al no compartir el kernel del sistema operativo, el hipervisor actúa como una barrera rígida. Si una aplicación dentro de una VM es comprometida, es extremadamente difícil que el atacante acceda al host o a otras VMs.
2.  **Soporte Multi-OS**: Permite ejecutar sistemas operativos diferentes en el mismo host físico (por ejemplo, una VM de Windows Server corriendo sobre un servidor físico de Linux).
3.  **Recursos Garantizados**: Las VMs tienen recursos asignados de manera fija, lo que evita que otros servicios impacten su rendimiento.

---

### 4. Ventajas de los Contenedores sobre las VMs

1.  **Máxima Eficiencia**: Al eliminar el Guest OS, se pueden ejecutar cientos de contenedores en el mismo hardware donde solo cabrían una decena de VMs.
2.  **Velocidad de Arranque**: Permite realizar escalados horizontales casi instantáneos ante picos de tráfico.
3.  **Ciclo de Vida DevOps**: Las imágenes de contenedor garantizan que la aplicación corra exactamente igual en la máquina del desarrollador, en pruebas y en producción.

---

### 5. Casos de Uso

*   **Usar VMs si**:
    *   Necesitas hospedar bases de datos críticas de alto rendimiento y aislamiento (ej: Oracle DB en entornos legacy).
    *   Necesitas ejecutar sistemas operativos diferentes al del host (ej: probar apps de Windows en Linux).
    *   Ofreces infraestructura en la nube multi-inquilino (Multitenancy) donde la seguridad de aislamiento de hardware es fundamental (ej: AWS EC2).
*   **Usar Contenedores si**:
    *   Implementas una arquitectura de microservicios.
    *   Creas entornos temporales de CI/CD (construcción, ejecución de pruebas y destrucción automática de contenedores).
    *   Necesitas alta densidad y escalabilidad horizontal elástica de APIs web sin estado.

---

### 6. Errores Comunes (Pitfalls)

*   **En VMs**: "VM Bloat" o desperdicio de recursos por levantar máquinas virtuales pesadas con sistemas operativos completos solo para correr microservicios ligeros.
*   **En Contenedores**:
    *   Tratar a los contenedores como "mini VMs" (por ejemplo, instalando SSH, bases de datos y servidores web dentro del mismo contenedor).
    *   No definir límites de recursos (`limits` en Kubernetes/Docker), lo que permite que un contenedor con fuga de memoria congele a todo el servidor host.
    *   Ignorar la seguridad del kernel anfitrión compartida.

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

El prompt solicita estructuradamente los criterios más importantes para comparar ambas tecnologías (arquitectura, ventajas mutuas, casos de uso y pitfalls) y le exige al LLM un formato de salida específico (tabla comparativa seguida de explicaciones), lo cual facilita una comparación directa y estructurada.

#### 2. ¿Qué se puede mejorar?

Se pudo añadir una pregunta específica sobre cómo influye la virtualización en el rendimiento de I/O (entrada/salida de disco y red), ya que las máquinas virtuales suelen tener una penalización mayor de rendimiento de I/O debido al hipervisor en comparación con los contenedores que acceden de forma casi nativa.

#### 3. Respuesta final

En conclusión, la respuesta del LLM es técnicamente muy precisa. Logra explicar con claridad cómo las VMs virtualizan el hardware a través de un hipervisor (incluyendo un sistema operativo invitado completo), mientras que los contenedores virtualizan a nivel de sistema operativo (compartiendo el kernel del host mediante namespaces y cgroups de Linux).

**Aspectos cruciales a destacar en el análisis crítico:**
1.  **La Superficie de Ataque y Seguridad del Kernel (Concepto clave de seguridad)**: Aunque el LLM mencionó que los contenedores comparten el kernel, no profundizó en la consecuencia directa: si un atacante explota una vulnerabilidad en el kernel del host desde un contenedor comprometido (vulnerabilidades tipo *Dirty COW* o *Container Escape*), el atacante toma control de **toda la máquina física** y de todos los contenedores adyacentes. En las VMs, romper el hipervisor (hiper-escape) es un ataque teóricamente viable pero extremadamente raro y difícil de ejecutar.
2.  **Noisy Neighbors (Vecinos Ruidosos)**: En un contenedor, si no se limitan los recursos mediante cgroups (`docker run -m` o los límites en K8s), un proceso mal programado puede agotar los recursos de la máquina física anfitriona y tumbar aplicaciones vecinas. En cambio, en una VM, el hipervisor segmenta y reserva los recursos de forma rígida a nivel de hardware virtualizado.
3.  **Portabilidad Real vs. Virtual**: Los contenedores son altamente portables, pero con límites. Una imagen compilada para arquitectura Linux x86_64 no correrá nativamente sobre un servidor ARM (como AWS Graviton o Apple Silicon) sin emulación pesada (como QEMU), lo cual merma el rendimiento. Las VMs encapsulan su arquitectura completa, aislando incluso este detalle.
4.  **Pérdida en Rendimiento de I/O**: Las VMs agregan una capa de traducción para la red y el disco mediante controladores virtualizados. Los contenedores, al usar el kernel del host, ejecutan instrucciones a nivel de I/O casi a velocidad nativa, lo que los hace ideales para procesos intensivos en red y disco si el aislamiento estricto no es una prioridad.
