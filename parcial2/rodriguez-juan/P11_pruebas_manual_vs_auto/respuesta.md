# Plantilla de entrega — Parcial 2

---

## Pregunta P11: P11

### Estudiante

- **Nombre completo**: Juan David Rodriguez Franco

---

### LLM utilizado

| Campo                                   | Valor                                                                                                                                                                                       |
| --------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Nombre del LLM**                | Gemini                                                                                                                                                                                      |
| **Modelo específico**            | Gemini 3.1 Pro                                                                                                                                                                              |
| **¿Por qué elegiste este LLM?** | Gemini 3.1 Pro contextualiza muy bien los escenarios de prueba en base a arquitecturas de software específicas, y recomienda herramientas alineadas con los stacks tecnológicos actuales. |

---

### Prompt utilizado

```text
Actúa como un Lead QA Engineer.

En OpenLib Market, nuestro equipo de QA está abrumado probando cada nueva versión manualmente. Nuestro stack es: Backend en Java (Spring Boot), un Frontend Web (HTML/JS o React) y una app de escritorio en JavaFX. Queremos empezar a automatizar.

Genera una comparación detallada entre pruebas manuales vs automatizadas aplicadas a nuestro contexto. Específicamente debes incluir:
1. DOS (2) escenarios reales dentro de OpenLib Market donde las pruebas manuales sigan siendo MEJORES y no deban automatizarse.
2. TRES (3) escenarios reales dentro de OpenLib Market donde las pruebas automatizadas sean INDISPENSABLES.
3. Qué herramientas exactas de automatización recomiendas para nuestro backend (Java), frontend web y frontend de escritorio (JavaFX).
```

---

### Respuesta del LLM

```markdown
Como Lead QA Engineer, entiendo perfectamente la situación. El agotamiento del equipo de QA (burnout) por tareas repetitivas es la principal razón por la que debemos transicionar hacia una estrategia híbrida, priorizando la automatización donde aporte valor, pero conservando las pruebas manuales donde el juicio humano es irremplazable.

Aquí tienes la comparación estructurada para el contexto de OpenLib Market:

### 1. Escenarios donde las Pruebas Manuales son MEJORES (No automatizar)

1. **Pruebas de Experiencia de Usuario (UX) y Usabilidad en el Frontend Web:**
   * *Escenario:* Validar si el nuevo diseño de la "Página de Checkout" inspira confianza, si los colores contrastan bien para un usuario real, o si el tamaño del botón de "Pagar" es cómodo en pantallas táctiles pequeñas.
   * *Por qué es mejor manual:* Los scripts automatizados no tienen empatía ni percepción estética. Solo verifican que el botón *exista* en el DOM, no si resulta confuso o frustrante para un humano.
2. **Pruebas Exploratorias (Exploratory Testing) para descubrir casos borde (Edge cases):**
   * *Escenario:* Un tester de QA usando la app de escritorio (JavaFX) intentando simular el comportamiento errático de un usuario real: haciendo clic múltiples veces en "Añadir al carrito", minimizando la ventana de golpe, o desconectando el WiFi justo al hacer el pago.
   * *Por qué es mejor manual:* La automatización solo sigue caminos pre-programados ("Happy paths" o flujos tristes conocidos). El testing exploratorio depende de la intuición y curiosidad destructiva del humano para encontrar bugs imprevistos.

### 2. Escenarios donde las Pruebas Automatizadas son INDISPENSABLES

1. **Pruebas de Regresión en el Módulo de Pagos (Backend):**
   * *Escenario:* Cada vez que un desarrollador hace un commit en el `PaymentService` o `CarritoService`, debemos asegurar que no rompió la lógica de cálculo de descuentos ni la integración con la pasarela.
   * *Por qué es indispensable:* Probar manualmente el carrito con 20 combinaciones de descuentos cada semana tomaría horas y es propenso al error humano. Automatizarlo toma milisegundos y garantiza que nada viejo se rompa con código nuevo.
2. **Pruebas de Carga y Estrés (Backend API):**
   * *Escenario:* Queremos saber si el servidor (Spring Boot + PostgreSQL) soporta 5,000 usuarios buscando libros simultáneamente durante la "Semana del Libro" sin caerse.
   * *Por qué es indispensable:* Es humanamente imposible coordinar a 5,000 personas reales para que hagan clic exactamente al mismo tiempo. 
3. **Smoke Tests Multi-Navegador y Multi-Resolución (Frontend Web):**
   * *Escenario:* Validar que el flujo básico de "Buscar libro -> Añadir a Wishlist -> Login" funcione en Chrome, Firefox, Safari y Edge, en resoluciones móviles y de escritorio.
   * *Por qué es indispensable:* Repetir el mismo flujo manual en 12 combinaciones diferentes de navegadores/dispositivos cada vez que hay una release es una de las mayores causas de fatiga en QA. Un script lo hace en minutos en paralelo.

### 3. Recomendación de Herramientas (Toolchain QA)

Dado tu stack tecnológico en OpenLib Market, la arquitectura de testing debería ser:

* **Para el Backend (Java / Spring Boot):**
  * **JUnit 5 + Mockito:** Para las pruebas unitarias de los servicios (`CarritoService`).
  * **REST Assured:** Para las pruebas de integración y contratos de la API REST (verificar que los endpoints devuelvan los JSON correctos y códigos HTTP 200/400).
  * **JMeter o Gatling:** Para las pruebas de carga y estrés.
* **Para el Frontend Web (React / HTML-JS):**
  * **Cypress o Playwright:** Excelentes para pruebas End-to-End (E2E) simulando usuarios reales en el navegador. Recomiendo Playwright por su velocidad y soporte multi-navegador.
* **Para el Frontend de Escritorio (JavaFX):**
  * **TestFX:** Es el estándar *de facto* para automatizar interfaces gráficas en JavaFX. Permite simular clics, escribir en text fields y arrastrar componentes (drag & drop) programáticamente desde JUnit.
```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

[Pendiente de análisis]

#### 2. ¿Qué se puede mejorar?

[Pendiente de análisis]

#### 3. Respuesta final

[Pendiente de análisis]
