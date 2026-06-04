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

[Evalua tu propio prompt, no la respuesta del LLM. ¿El contexto fue suficiente? ¿Las restricciones fueron claras? ¿El formato de salida que pediste ayudo a obtener una buena respuesta? ¿Que parte de tu prompt fue mas efectiva? Se especifico: menciona fragmentos concretos de tu prompt que funcionaron bien.]


#### 2. ¿Que se puede mejorar?

[¿Que le falto a tu prompt? ¿Que harias diferente si pudieras reformularlo? ¿El LLM entendio mal algo por falta de claridad en tu prompt? ¿La respuesta tiene errores u omisiones? ¿Que no cubrio el LLM que tu si sabes por lo visto en clase?]


#### 3. Respuesta final

[Escribe tu respuesta definitiva a la pregunta del parcial, integrando lo que aprendiste del LLM pero yendo mas alla. Corrige errores, llena omisiones, conecta con conceptos vistos en clase. Esta es tu respuesta: demuestra que tu dominas el tema.]
