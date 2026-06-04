# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [10]: [P10_vm_vs_containers]

### Estudiante
- **Nombre completo**: [Diego Alejandro Torres Barrgan]

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [Claude] |
| **Modelo específico** | [Ej: Claude Sonnet 4.6/bajo]|
| **¿Por qué elegiste este LLM?** | [Es el modelo que actualmente mas uso y en el que estoy confiando, ademas que es muy poderoso para generar codigo y para explicarlo ] |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```
[Actua como un ingeniero DevOps Senior. Estamos en el curso de Ingenieria de Software y necesito entender la diferencia entre maquinas virtuales y contenedores para el contexto de OpenLib Market.
Comparalos considerando:
(a) arquitectura y como funciona cada uno internamente
(b) ventajas de VMs sobre contenedores
(c) ventajas de contenedores sobre VMs
(d) casos de uso concretos donde conviene cada uno
(e) errores comunes al adoptar cada tecnologia
Entregame primero una tabla comparativa y luego una explicacion de cada punto. Se especifico con el tema de seguridad y el kernel.]
```

---

### Respuesta del LLM

Tabla comparativa:
| Aspecto | Maquinas Virtuales | Contenedores |
|---|---|---|
| Aislamiento | SO completo por VM | Comparten kernel del host |
| Peso | Pesadas (GBs) | Livianos (MBs) |
| Arranque | Minutos | Segundos |
| Seguridad | Mayor aislamiento | Menor, comparten kernel |
| Portabilidad | Menor | Mayor |
| Recursos | Consume mas por overhead del hypervisor | Consume menos |
| Casos de uso | Ambientes que requieren SO distinto o aislamiento fuerte | Microservicios, CI/CD, despliegues rapidos |

a) Arquitectura
Las VMs corren sobre un hypervisor (VMware, VirtualBox, KVM) que simula hardware completo. Cada VM tiene su propio sistema operativo y kernel. Esto genera overhead porque hay una capa extra de traduccion de instrucciones entre el hardware real y el SO virtualizado.
Los contenedores comparten el kernel del host y solo aíslan el proceso y sus dependencias usando namespaces y cgroups del sistema operativo. Por eso son mucho mas livianos y rapidos.
b) Ventajas de VMs

Aislamiento completo: cada VM tiene su propio kernel, si una cae no afecta las demas
Permiten correr Windows y Linux en el mismo servidor fisico
Mas seguras: un atacante que compromete un contenedor puede intentar escapar al kernel del host, en una VM eso es mucho mas dificil
Utiles para workloads que requieren garantias de recursos dedicados

c) Ventajas de contenedores

Arrancan en segundos vs minutos de una VM
Consumen muchos menos recursos de CPU y memoria
Portables: la imagen corre igual en cualquier maquina con Docker
Ideales para escalar horizontalmente en la nube
Facilitan CI/CD porque el ambiente de desarrollo es identico al de produccion

d) Casos de uso
VMs convienen para ambientes que necesitan aislamiento fuerte como bancos o sistemas de salud, o cuando se necesita correr sistemas operativos diferentes en el mismo hardware.
Contenedores convienen para microservicios como los de OpenLib Market (servicio de pagos, catalogo, usuarios separados), pipelines de CI/CD y despliegues frecuentes en la nube.
e) Errores comunes

Contenedores: correr como root dentro del contenedor, no limitar CPU ni memoria con cgroups, meter credenciales hardcodeadas en la imagen, no actualizar las imagenes base y acumular vulnerabilidades
VMs: sobreprovisionar recursos que nunca se usan, no hacer snapshots antes de cambios criticos, subestimar el overhead del hypervisor en rendimiento
```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

[Pedir explicitamente "se especifico con el tema de seguridad y el kernel" fue lo que mas ayudo, sin eso el LLM probablemente lo hubiera mencionado de pasada. Tambien ayudo pedir la tabla primero y las explicaciones despues porque la respuesta salio ordenada y facil de leer. Los cinco puntos numerados evitaron que el LLM se fuera por las ramas.]


#### 2. ¿Qué se puede mejorar?

[No se pidio que explicara los tipos de hypervisor (Type 1 y Type 2) que es algo visto en clase y quedo por fuera de la respuesta. Tampoco se pidio contexto especifico de como usaria OpenLib Market cada tecnologia, entonces los ejemplos quedaron un poco genericos.]


#### 3. Respuesta final

Las VMs y los contenedores permiten correr aplicaciones en entornos aislados pero funcionan muy diferente internamente. Las VMs usan un hypervisor que simula hardware completo y cada una tiene su propio kernel, lo que genera overhead pero da un aislamiento fuerte. Los contenedores comparten el kernel del host usando namespaces y cgroups, por eso son mucho mas livianos y arrancan en segundos.
El punto de seguridad es clave: si un contenedor es comprometido un atacante puede intentar escapar al kernel del host porque todos lo comparten, en una VM eso es mucho mas dificil porque cada una tiene el suyo. Por eso para sistemas criticos como pagos en OpenLib Market convendria evaluar bien si usar contenedores es suficiente o si se necesita el aislamiento de una VM.
Algo que no quedo claro en la respuesta es la diferencia entre hypervisor Type 1 y Type 2. El Type 1 corre directo sobre el hardware (como KVM o VMware ESXi) y es mas eficiente, el Type 2 corre sobre un SO host (como VirtualBox) y tiene mas overhead. Esa diferencia importa cuando se compara rendimiento entre VMs y contenedores porque no es lo mismo una VM sobre Type 1 que sobre Type 2.
Para OpenLib Market lo mas practico seria usar contenedores para los microservicios (pagos, catalogo, usuarios) por la facilidad de escalar y desplegar, y reservar VMs para ambientes que necesiten aislamiento fuerte o correr sistemas operativos diferentes.
