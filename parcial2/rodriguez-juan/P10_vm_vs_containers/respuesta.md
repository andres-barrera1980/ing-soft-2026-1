# Plantilla de entrega — Parcial 2

---

## Pregunta P10: Infraestructura — VMs vs Contenedores

### Estudiante

- **Nombre completo**: Juan David Rodriguez Franco

---

### LLM utilizado

| Campo                                   | Valor                                                                                                          |
| --------------------------------------- | -------------------------------------------------------------------------------------------------------------- |
| **Nombre del LLM**                | Gemini |
| **Modelo específico**            | Gemini 3.1 Pro |
| **¿Por qué elegiste este LLM?** | Gemini 3.1 Pro tiene excelentes capacidades para formatear cuadros comparativos y sintetizar diferencias arquitectónicas entre tecnologías de infraestructura y nube. |

---

### Prompt utilizado

```text
Actúa como un Arquitecto Cloud experto.

En nuestro proyecto "OpenLib Market" (cuyo backend usa Spring Boot y base de datos PostgreSQL), el equipo debate cómo desplegar la aplicación en producción. Una mitad quiere usar Máquinas Virtuales (VMs) tradicionales en AWS EC2, y la otra mitad quiere usar Contenedores (Docker) en AWS ECS o EKS.

Por favor, genera un cuadro comparativo detallado entre desplegar en VMs vs desplegar en Docker. El cuadro debe incluir estrictamente los siguientes aspectos:
1. Aislamiento.
2. Tiempo de arranque.
3. Consumo de recursos.
4. Portabilidad.
5. Facilidad de integración continua (CI/CD).

Después del cuadro comparativo, emite una recomendación arquitectónica definitiva sobre cuál de las dos tecnologías deberíamos elegir para OpenLib Market y por qué.
```

---

### Respuesta del LLM

```markdown
Como Arquitecto Cloud, entiendo perfectamente la disyuntiva del equipo. Ambas tecnologías han sido pilares fundamentales en la evolución del despliegue de software, pero tienen enfoques arquitectónicos (Hypervisor vs. Container Engine) muy distintos.

A continuación, te presento el cuadro comparativo para tu stack (Spring Boot + PostgreSQL):

### Cuadro Comparativo: Máquinas Virtuales (VMs) vs Contenedores (Docker)

| Aspecto | Máquinas Virtuales (VMs) | Contenedores (Docker) |
| :--- | :--- | :--- |
| **Arquitectura base** | Requiere un **Hypervisor** que virtualiza el hardware subyacente. Cada VM tiene su propio Sistema Operativo (Guest OS) completo. | Requiere un **Container Engine** (como Docker Daemon) que virtualiza a nivel del Sistema Operativo. Comparten el mismo kernel (Host OS). |
| **1. Aislamiento** | **Alto/Total**: Aislamiento a nivel de hardware virtualizado. Una VM comprometida o saturada difícilmente afectará a las demás. | **Medio/Alto**: Aislamiento a nivel de procesos (namespaces y cgroups de Linux). Es seguro, pero comparten el kernel, lo que implica una superficie de ataque teóricamente mayor. |
| **2. Tiempo de arranque** | **Lento (Minutos)**: Arrancar una VM implica encender un Guest OS completo (booting), inicializar servicios del sistema y luego tu app Spring Boot. | **Rápido (Segundos o milisegundos)**: No hay SO que arrancar. Iniciar un contenedor es tan rápido como iniciar el proceso de Java en la máquina host. |
| **3. Consumo de recursos** | **Alto (Pesado)**: Cada VM reserva RAM y CPU fijas, sumado al "overhead" (peso extra) de tener un SO completo corriendo por cada instancia (GBs de almacenamiento). | **Bajo (Ligero)**: No tienen Guest OS. Solo consumen la RAM y CPU que el proceso de Java y Postgres necesitan (MBs de almacenamiento). Alta densidad en los servidores. |
| **4. Portabilidad** | **Baja**: Mover una VM de on-premise a AWS, o entre nubes, puede causar conflictos por dependencias del hypervisor o drivers del hardware emulado. | **Muy Alta**: "Build once, run anywhere". La imagen Docker contiene el JRE, tu .jar y el OS base mínimo. Si corre en la laptop del desarrollador, corre idéntico en AWS ECS. |
| **5. Facilidad en CI/CD** | **Compleja**: Requiere herramientas de gestión de configuración (Ansible, Chef) o hornear imágenes pesadas (AMIs). Los despliegues toman más tiempo y son propensos a mutaciones. | **Excelente**: Se integra nativamente. El pipeline compila el .jar, construye la imagen Docker y la empuja a un Registry (ECR). El despliegue es inmutable y predecible. |

---

### Recomendación Arquitectónica Definitiva

Para **OpenLib Market** (Spring Boot + PostgreSQL), **mi recomendación definitiva es elegir Contenedores (Docker) sobre AWS ECS o EKS.**

**¿Por qué?**
1. **Microservicios e Inmutabilidad**: Spring Boot está diseñado para ser ágil y empaquetarse en contenedores (incluso provee herramientas como Buildpacks y soporte nativo con GraalVM). Usar Docker garantiza que el entorno de desarrollo sea idéntico al de producción, erradicando el clásico *"en mi máquina sí funciona"*.
2. **Escalabilidad y Costos**: Al tener tiempos de arranque en segundos y un menor consumo de recursos, puedes escalar horizontalmente mucho más rápido ante picos de tráfico en tu e-commerce, aprovechando mejor cada dólar gastado en infraestructura cloud.
3. **Pipeline CI/CD Moderno**: La construcción de imágenes Docker y su despliegue automatizado es hoy en día el estándar de la industria.

*Nota sobre la Base de Datos*: Aunque la aplicación Spring Boot debe ir en contenedores, te sugiero que la base de datos PostgreSQL **no** se gestione manualmente ni en VMs ni en contenedores propios en producción. La mejor práctica es externalizar el estado a un servicio gestionado (PaaS) como **AWS RDS for PostgreSQL**, dejando los contenedores exclusivamente para la capa de cómputo (Spring Boot) sin estado (Stateless).
# Plantilla de entrega — Parcial 2

---

## Pregunta P10: Infraestructura — VMs vs Contenedores

### Estudiante

- **Nombre completo**: Juan David Rodriguez Franco

---

### LLM utilizado

| Campo                                   | Valor                                                                                                          |
| --------------------------------------- | -------------------------------------------------------------------------------------------------------------- |
| **Nombre del LLM**                | Gemini |
| **Modelo específico**            | Gemini 3.1 Pro |
| **¿Por qué elegiste este LLM?** | Gemini 3.1 Pro tiene excelentes capacidades para formatear cuadros comparativos y sintetizar diferencias arquitectónicas entre tecnologías de infraestructura y nube. |

---

### Prompt utilizado

```text
Actúa como un Arquitecto Cloud experto.

En nuestro proyecto "OpenLib Market" (cuyo backend usa Spring Boot y base de datos PostgreSQL), el equipo debate cómo desplegar la aplicación en producción. Una mitad quiere usar Máquinas Virtuales (VMs) tradicionales en AWS EC2, y la otra mitad quiere usar Contenedores (Docker) en AWS ECS o EKS.

Por favor, genera un cuadro comparativo detallado entre desplegar en VMs vs desplegar en Docker. El cuadro debe incluir estrictamente los siguientes aspectos:
1. Aislamiento.
2. Tiempo de arranque.
3. Consumo de recursos.
4. Portabilidad.
5. Facilidad de integración continua (CI/CD).

Después del cuadro comparativo, emite una recomendación arquitectónica definitiva sobre cuál de las dos tecnologías deberíamos elegir para OpenLib Market y por qué.
```

---

### Respuesta del LLM

```markdown
Como Arquitecto Cloud, entiendo perfectamente la disyuntiva del equipo. Ambas tecnologías han sido pilares fundamentales en la evolución del despliegue de software, pero tienen enfoques arquitectónicos (Hypervisor vs. Container Engine) muy distintos.

A continuación, te presento el cuadro comparativo para tu stack (Spring Boot + PostgreSQL):

### Cuadro Comparativo: Máquinas Virtuales (VMs) vs Contenedores (Docker)

| Aspecto | Máquinas Virtuales (VMs) | Contenedores (Docker) |
| :--- | :--- | :--- |
| **Arquitectura base** | Requiere un **Hypervisor** que virtualiza el hardware subyacente. Cada VM tiene su propio Sistema Operativo (Guest OS) completo. | Requiere un **Container Engine** (como Docker Daemon) que virtualiza a nivel del Sistema Operativo. Comparten el mismo kernel (Host OS). |
| **1. Aislamiento** | **Alto/Total**: Aislamiento a nivel de hardware virtualizado. Una VM comprometida o saturada difícilmente afectará a las demás. | **Medio/Alto**: Aislamiento a nivel de procesos (namespaces y cgroups de Linux). Es seguro, pero comparten el kernel, lo que implica una superficie de ataque teóricamente mayor. |
| **2. Tiempo de arranque** | **Lento (Minutos)**: Arrancar una VM implica encender un Guest OS completo (booting), inicializar servicios del sistema y luego tu app Spring Boot. | **Rápido (Segundos o milisegundos)**: No hay SO que arrancar. Iniciar un contenedor es tan rápido como iniciar el proceso de Java en la máquina host. |
| **3. Consumo de recursos** | **Alto (Pesado)**: Cada VM reserva RAM y CPU fijas, sumado al "overhead" (peso extra) de tener un SO completo corriendo por cada instancia (GBs de almacenamiento). | **Bajo (Ligero)**: No tienen Guest OS. Solo consumen la RAM y CPU que el proceso de Java y Postgres necesitan (MBs de almacenamiento). Alta densidad en los servidores. |
| **4. Portabilidad** | **Baja**: Mover una VM de on-premise a AWS, o entre nubes, puede causar conflictos por dependencias del hypervisor o drivers del hardware emulado. | **Muy Alta**: "Build once, run anywhere". La imagen Docker contiene el JRE, tu .jar y el OS base mínimo. Si corre en la laptop del desarrollador, corre idéntico en AWS ECS. |
| **5. Facilidad en CI/CD** | **Compleja**: Requiere herramientas de gestión de configuración (Ansible, Chef) o hornear imágenes pesadas (AMIs). Los despliegues toman más tiempo y son propensos a mutaciones. | **Excelente**: Se integra nativamente. El pipeline compila el .jar, construye la imagen Docker y la empuja a un Registry (ECR). El despliegue es inmutable y predecible. |

---

### Recomendación Arquitectónica Definitiva

Para **OpenLib Market** (Spring Boot + PostgreSQL), **mi recomendación definitiva es elegir Contenedores (Docker) sobre AWS ECS o EKS.**

**¿Por qué?**
1. **Microservicios e Inmutabilidad**: Spring Boot está diseñado para ser ágil y empaquetarse en contenedores (incluso provee herramientas como Buildpacks y soporte nativo con GraalVM). Usar Docker garantiza que el entorno de desarrollo sea idéntico al de producción, erradicando el clásico *"en mi máquina sí funciona"*.
2. **Escalabilidad y Costos**: Al tener tiempos de arranque en segundos y un menor consumo de recursos, puedes escalar horizontalmente mucho más rápido ante picos de tráfico en tu e-commerce, aprovechando mejor cada dólar gastado en infraestructura cloud.
3. **Pipeline CI/CD Moderno**: La construcción de imágenes Docker y su despliegue automatizado es hoy en día el estándar de la industria.

*Nota sobre la Base de Datos*: Aunque la aplicación Spring Boot debe ir en contenedores, te sugiero que la base de datos PostgreSQL **no** se gestione manualmente ni en VMs ni en contenedores propios en producción. La mejor práctica es externalizar el estado a un servicio gestionado (PaaS) como **AWS RDS for PostgreSQL**, dejando los contenedores exclusivamente para la capa de cómputo (Spring Boot) sin estado (Stateless).
```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

El prompt fue directo y limitó al LLM a comparar estrictamente los **5 puntos específicos** exigidos en la rúbrica. Además, al darle el contexto tecnológico (Spring Boot + PostgreSQL) e invocar el rol de "Arquitecto Cloud", forzó al modelo a dar una recomendación técnica justificada y aplicada al e-commerce, en lugar de una respuesta teórica genérica.

#### 2. ¿Qué se puede mejorar?

Al prompt le faltó pedir explícitamente que el LLM mencionara las **desventajas o casos de uso donde NO elegiría Docker**. Como resultado, la respuesta del LLM es algo parcializada hacia los contenedores. Para un análisis arquitectónico completo, siempre hay que evaluar los *trade-offs* (concesiones), y el prompt no exigió esta evaluación de riesgos.

#### 3. Respuesta final

Estoy **totalmente de acuerdo** con la recomendación del LLM para el contexto de OpenLib Market. Un e-commerce necesita elasticidad para soportar picos de tráfico (ej: rebajas del Día del Libro); el arranque en segundos y el bajo consumo de recursos de Docker (en ECS o EKS) lo hacen inmensamente superior a las VMs para aplicaciones Spring Boot sin estado. 

El cuadro comparativo es acertado respecto a la teoría: refleja claramente que el Hypervisor emula hardware para soportar múltiples OS completos (pesado), mientras que el Container Engine virtualiza el OS compartiendo el mismo kernel (ligero).

Sin embargo, **el LLM omitió una desventaja crítica de Docker: La seguridad por aislamiento del kernel**. Como todos los contenedores comparten el mismo kernel del *Host OS*, si un contenedor logra explotar una vulnerabilidad a nivel de kernel (ej. un *kernel panic*), puede comprometer o derribar a todos los demás contenedores que residan en la misma máquina física. Las VMs no sufren de esto gracias al aislamiento de hardware estricto. Además, el LLM no mencionó que la curva de aprendizaje y la complejidad operacional (redes superpuestas, orquestación, gestión de volumen persistente) es mucho mayor en Docker/Kubernetes que en EC2 tradicional.
