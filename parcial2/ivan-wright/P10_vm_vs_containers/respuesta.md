# Plantilla de entrega — Parcial 2

---

## Pregunta P10: Máquinas Virtuales vs Contenedores

### Estudiante
- **Nombre completo**: Ivan Wright

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Gemini |
| **Modelo especifico** | Gemini 3.1 Pro |
| **¿Por que elegiste este LLM?** | Lo elegi porque organiza muy bien la informacion teorica y le va muy bien armando tablas comparativas para conceptos de DevOps. |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```text
hola gemini, actua como un ingeniero devops.

[CONTEXTO]
En el curso estamos discutiendo dos enfoques de virtualizacion para aislar aplicaciones: las maquinas virtuales (VMs) y los contenedores (como Docker). Necesito tener muy claro como funcionan y cuando usar cual.

[PROBLEMA]
Quiero que compares detalladamente las maquinas virtuales vs los contenedores. Tu respuesta debe cubrir exactamente estos 5 puntos: 
1. Arquitectura y como funciona cada uno.
2. Ventajas de las VMs sobre los contenedores.
3. Ventajas de los contenedores sobre las VMs.
4. Casos de uso donde conviene cada tecnologia.
5. Errores comunes (common pitfalls) al adoptar cada una.

[RESTRICCIONES]
- Ve al grano y usa un lenguaje tecnico pero facil de digerir.
- No uses rodeos ni introducciones largas.

[FORMATO]
Entregame todo en markdown. Obligatoriamente la primera parte debe ser una tabla comparativa resumiendo los puntos, y luego de la tabla pones las explicaciones detalladas de los 5 puntos solicitados.

[VALIDACION]
Sabre que tu respuesta es buena si la tabla comparativa me permite ver de un solo vistazo la diferencia entre ambas tecnologias y si las explicaciones de los errores comunes son escenarios reales de produccion.
```

---

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```markdown
¡Hola! Entendido, vamos directo al grano con la comparación técnica entre Máquinas Virtuales (VMs) y Contenedores.

### Tabla Comparativa: VMs vs Contenedores

| Característica | Máquinas Virtuales (VMs) | Contenedores (Docker, etc.) |
| :--- | :--- | :--- |
| **Arquitectura base** | Virtualiza el hardware físico (requiere un Hypervisor). | Virtualiza el Sistema Operativo (usa un motor como Docker). |
| **Sistema Operativo (OS)** | Cada VM tiene su propio OS completo (Guest OS). | Comparten el OS del host (solo empaquetan binarios y librerías). |
| **Arranque y Velocidad** | Lento (minutos), porque debe arrancar un OS completo. | Ultrarrápido (segundos), como un proceso normal del sistema. |
| **Peso y Almacenamiento** | Pesado (Gigabytes), incluye el OS y sus herramientas. | Ligero (Megabytes), solo contiene la app y sus dependencias. |
| **Aislamiento** | Alto. Fuerte separación a nivel de hardware virtual. | Medio. Los procesos están aislados lógicamente. |

---

### Explicaciones Detalladas

#### 1. Arquitectura y cómo funciona cada uno
*   **Máquinas Virtuales:** Utilizan un software llamado *Hypervisor* (como VMware, VirtualBox o KVM) que se instala sobre el hardware físico. Este hypervisor divide los recursos (CPU, RAM) y crea "computadoras virtuales". Cada computadora requiere instalar su propio sistema operativo desde cero.
*   **Contenedores:** Se ejecutan sobre el sistema operativo principal (Host OS) utilizando un motor (como Docker Engine). No virtualizan hardware, sino que crean espacios aislados a nivel de software (namespaces y cgroups) donde corre tu aplicación con sus librerías específicas.

#### 2. Ventajas de VMs sobre contenedores
*   **Aislamiento total:** Como cada VM tiene su propio OS, si un proceso interno colapsa gravemente el sistema operativo, solo afecta a esa VM, protegiendo al resto del servidor.
*   **Soporte de OS variados:** Puedes correr un entorno Windows, uno Linux y otro FreeBSD en el mismo servidor físico simultáneamente.
*   **Madurez:** Es una tecnología probada por décadas, con excelentes herramientas de backup a nivel de máquina completa.

#### 3. Ventajas de contenedores sobre VMs
*   **Eficiencia extrema:** Al no duplicar el sistema operativo invitado, puedes correr cientos de contenedores en la misma máquina física donde apenas cabrían 5 VMs.
*   **Portabilidad garantizada:** Resuelve el clásico problema "Funciona en mi máquina". El contenedor empaca todo lo necesario, garantizando que corra igual en desarrollo, testing y producción.
*   **Escalabilidad ágil:** Al arrancar en milisegundos, son la opción perfecta para arquitecturas de microservicios que necesitan escalar dinámicamente según picos de tráfico.

#### 4. Casos de uso
*   **Usa VMs para:** Aplicaciones monolíticas tradicionales (legacy), bases de datos pesadas que requieren recursos dedicados, o cuando necesitas ejecutar software en un sistema operativo diferente al de tu servidor host.
*   **Usa Contenedores para:** Arquitecturas basadas en microservicios, integración y despliegue continuo (CI/CD), y aplicaciones modernas diseñadas para la nube (Cloud Native).

#### 5. Errores Comunes (Common Pitfalls)
*   **En VMs:** Sobre-asignar recursos (*Overprovisioning*). Darle 16GB de RAM a una VM cuando la aplicación solo usa 2GB, desperdiciando recursos valiosos del servidor físico. Otro error grave es no actualizarlas, tratándolas como servidores de metal olvidados.
*   **En Contenedores:** Tratar al contenedor como si fuera una VM ("Fat Containers"). Un contenedor debe ser efímero y correr un solo proceso principal. Un error clásico de novato es meter la aplicación web, la base de datos y un servidor SSH dentro del mismo contenedor, rompiendo toda la filosofía de Docker.
```

---

### Analisis critico de la respuesta

#### 1. ¿Que hizo bien el prompt?

El prompt fue muy especifico al pedir una tabla comparativa y obligar al LLM a separar los 5 puntos exactos que se discutian en clase. Esto evito que la IA generara una redaccion interminable y logro que la respuesta fuera super facil de leer de un solo vistazo. Lo mas util de mi prompt fue pedirle explicitamente los errores comunes (pitfalls), porque obligo a la IA a entregar escenarios de la vida real como el error de meter una base de datos y la app web en un solo contenedor.


#### 2. ¿Que se puede mejorar?

A mi prompt le falto exigirle a la IA que profundizara en los problemas de seguridad al comparar estas dos tecnologias. Al no pedirselo, el LLM me dio una explicacion muy superficial de "Aislamiento Medio" pero se comio por completo el concepto mas critico y peligroso que vimos en clase sobre como los contenedores interactuan con el sistema operativo anfitrion.


#### 3. Respuesta final

El LLM explico muy bien la diferencia de arquitectura (virtualizar hardware vs virtualizar OS) y las ventajas de cada uno. Los casos de uso que dio son super precisos y realistas, como usar VMs para bases de datos pesadas legacy y contenedores para microservicios agiles.

Sin embargo, el LLM omitio el problema de seguridad mas grave que discutimos en clase: *los contenedores comparten el mismo Kernel del sistema operativo anfitrion*. En una maquina virtual, si hay una vulnerabilidad en su propio Kernel, el atacante se queda encerrado en esa VM (gracias al fuerte aislamiento de hardware del Hypervisor). *Pero en los contenedores, si un proceso malicioso logra explotar una vulnerabilidad o causar un "Kernel Panic" en el OS base, tumba todos y cada uno de los demas contenedores que esten corriendo en esa misma maquina porque no hay un aislamiento real a ese nivel*.

Por otro lado, la explicacion de los pitfalls fue excelente. El "overprovisioning" en VMs (desperdiciar RAM) y los "fat containers" (tratar contenedores como si fueran VMs instalando multiples servicios en uno solo) son antipatrones clasicos que ocurren muchisimo en las migraciones de empresas a la nube.
