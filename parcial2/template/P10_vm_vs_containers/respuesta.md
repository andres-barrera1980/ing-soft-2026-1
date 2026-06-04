# Pregunta P10: VM vs Containers — Comparación

### Estudiante
- **Nombre completo**: Mateo Traslaviña Moreno

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Claude |
| **Modelo específico** | Claude Sonnet 4.6 |
| **¿Por qué elegiste este LLM?** | Para preguntas conceptuales con múltiples dimensiones de comparación (rendimiento, seguridad, uso de recursos, DevOps), Claude estructura bien las respuestas en tablas y matrices de decisión. Su conocimiento de infraestructura moderna (Docker, Kubernetes, VM providers) es sólido y genera análisis de trade-offs equilibrados. |

---

### Prompt utilizado

```
Eres un arquitecto de infraestructura explicando tecnologías de virtualización al equipo de OpenLib Market, una plataforma de compra-venta de libros en Java 21 con Spring Boot 3.x que necesita decidir su estrategia de despliegue.

Necesito una comparación técnica exhaustiva entre Máquinas Virtuales (VMs) y Contenedores (Docker/Kubernetes) para el contexto de esta aplicación.

Analiza las siguientes dimensiones:
1. Arquitectura técnica: ¿cómo funcionan internamente?
2. Uso de recursos: CPU, RAM, almacenamiento, tiempo de arranque
3. Aislamiento y seguridad: ¿qué nivel de aislamiento provee cada uno?
4. Portabilidad y consistencia de entornos
5. Integración con CI/CD y pipelines de despliegue
6. Casos de uso donde cada tecnología gana
7. Cuándo usar VMs vs Contenedores para OpenLib Market específicamente

Incluye:
- Una tabla comparativa con criterios y puntuación
- Un diagrama ASCII de arquitectura de cada enfoque
- Recomendación concreta para OpenLib Market con justificación

Formato de salida: markdown con secciones nombradas, tablas, y ejemplos concretos de comandos Docker/Kubernetes relevantes para una app Spring Boot.
```

---

### Respuesta del LLM

## Arquitectura técnica

### Máquinas Virtuales

Una VM virtualiza hardware completo a través de un **hypervisor** (VMware ESXi, KVM, Hyper-V, VirtualBox). El hypervisor abstrae el hardware físico y permite que múltiples sistemas operativos invitados corran en el mismo host.

```
┌─────────────────────────────────────────────┐
│              Hardware Físico                 │
│         (CPU, RAM, Disco, Red)              │
├─────────────────────────────────────────────┤
│           Hypervisor (KVM/VMware)           │
├──────────────┬──────────────┬───────────────┤
│     VM 1     │     VM 2     │     VM 3      │
│  Guest OS    │  Guest OS    │  Guest OS     │
│  (Linux)     │  (Windows)   │  (Linux)      │
│  Libs/Deps   │  Libs/Deps   │  Libs/Deps    │
│  App Java    │  App .NET    │  App Python   │
└──────────────┴──────────────┴───────────────┘
```

Cada VM tiene su propio kernel, sistema de archivos completo, y stack de red. Es como tener computadoras físicas separadas compartiendo el mismo hardware.

### Contenedores

Los contenedores usan el **kernel del host** directamente. Docker usa `namespaces` y `cgroups` de Linux para aislar procesos, sin virtualizar hardware.

```
┌─────────────────────────────────────────────┐
│              Hardware Físico                 │
├─────────────────────────────────────────────┤
│              Sistema Operativo Host          │
│                  (Linux Kernel)             │
├─────────────────────────────────────────────┤
│            Container Runtime (Docker)       │
├──────────────┬──────────────┬───────────────┤
│ Container 1  │ Container 2  │ Container 3   │
│  Libs/Deps   │  Libs/Deps   │  Libs/Deps    │
│  App Java    │  App Node    │  PostgreSQL   │
└──────────────┴──────────────┴───────────────┘
```

Los contenedores comparten el kernel del host. El "aislamiento" es a nivel de proceso con namespaces (PID, red, filesystem) y cgroups (límites de CPU/RAM).

## Tabla comparativa

| Criterio | Máquina Virtual | Contenedor | Ganador |
|---|---|---|---|
| Tiempo de arranque | 1-5 minutos | Segundos | Contenedor |
| Overhead de RAM | Alto (SO completo: ~512MB-2GB por VM) | Bajo (MB por contenedor) | Contenedor |
| Overhead de disco | Alto (imagen completa: ~10-40GB) | Bajo (capas compartidas: ~100-500MB) | Contenedor |
| Aislamiento de seguridad | Alto (kernel separado) | Medio (kernel compartido) | VM |
| Portabilidad | Media (imagen pesada, hypervisor-dependiente) | Alta (Docker Hub, cualquier host Linux) | Contenedor |
| Densidad por host | Baja (10-30 VMs) | Alta (100s de contenedores) | Contenedor |
| Consistencia de entorno | Alta | Muy alta (imagen inmutable) | Contenedor |
| CI/CD integration | Media | Excelente | Contenedor |
| Soporte multi-OS | Total (Windows+Linux en mismo host) | Limitado (kernel compartido) | VM |
| Madurez/soporte enterprise | Alta | Alta (Docker desde 2013) | Empate |
| Coste de infraestructura | Mayor | Menor | Contenedor |

## Uso de recursos: ejemplos concretos

Para una app Spring Boot como OpenLib Market:

**VM**: Una instancia típica requiere ~2 vCPU + 2GB RAM solo para el SO guest + JVM + app. Si tienes 10 instancias para escalar: 20 vCPU + 20GB RAM mínimo.

**Contenedor**: El SO no está en la imagen. Una imagen Spring Boot con JRE 21 ocupa ~200-300MB. 10 contenedores en el mismo host: 2-4GB RAM total, arranque en <10 segundos.

## Aislamiento y seguridad

Las VMs tienen **aislamiento a nivel kernel**: si un proceso dentro de la VM hace exploit del kernel, solo afecta esa VM. Es más difícil escapar del aislamiento.

Los contenedores comparten el kernel del host: un `container escape` (vulnerabilidad en el runtime de Docker) podría comprometer el host. Sin embargo, con configuraciones como `seccomp`, `AppArmor`, usuarios no-root, y pods de Kubernetes con `securityContext`, el riesgo se reduce considerablemente.

Para OpenLib Market: dado que maneja datos de usuarios y transacciones, se recomienda nunca correr contenedores como `root`.

## Integración CI/CD para Spring Boot

Con contenedores, el pipeline es natural:

```dockerfile
# Dockerfile para OpenLib Market
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/openlib-market.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

```yaml
# docker-compose.yml para desarrollo
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/openlib
    depends_on:
      - db
  db:
    image: postgres:16
    environment:
      POSTGRES_DB: openlib
      POSTGRES_PASSWORD: pass
```

Con VMs, el pipeline requiere: provisionar VM → instalar SO → instalar JDK → copiar JAR → configurar systemd. Cada paso es manual o requiere herramientas como Ansible/Packer.

## Cuándo usar cada uno

**Usa VMs cuando:**
- Necesitas correr múltiples SOs (Windows + Linux en el mismo host)
- Tienes requisitos de seguridad/compliance muy estrictos (PCI-DSS, HIPAA)
- Tu aplicación requiere acceso directo al hardware (GPU, hardware especializado)
- Tienes aplicaciones legacy que no pueden containerizarse fácilmente

