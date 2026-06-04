# Plantilla de entrega — Parcial 2

---

## Pregunta P12: QA — Ciclo de Vida de Defectos

### Estudiante

- **Nombre completo**: Juan David Rodriguez Franco

---

### LLM utilizado

| Campo                                   | Valor                                                                                                          |
| --------------------------------------- | -------------------------------------------------------------------------------------------------------------- |
| **Nombre del LLM**                | Gemini |
| **Modelo específico**            | Gemini 3.1 Pro |
| **¿Por qué elegiste este LLM?** | Gemini 3.1 Pro es muy bueno estructurando documentos formales (como reportes de QA) y definiendo flujos de procesos ágiles como los estados de Jira con claridad y coherencia. |

---

### Prompt utilizado

```text
Actúa como un QA Engineer Senior.

En nuestro proyecto OpenLib Market, hemos detectado un bug crítico: "A veces, al pagar con tarjeta de crédito, el sistema descuenta el saldo del usuario pero la plataforma no registra la orden creada".

Por favor, realiza dos tareas:
1. Redacta el reporte de este defecto (Defect Report) como si lo fueras a crear en Jira. Debe incluir todos los campos estándar y profesionales (Título, Prioridad, Severidad, Descripción, Entorno, Pasos para reproducir, Resultado esperado, Resultado actual, Evidencia, etc.).
2. Describe detalladamente el ciclo de vida de este ticket (los estados o Status por los que pasará en el tablero Kanban/Scrum) desde que se reporta hasta que se cierra en producción, indicando qué rol (ej. QA, Dev, Product Owner) es responsable de pasarlo a cada estado.
```

---

### Respuesta del LLM

```markdown
Como QA Engineer Senior, este es un incidente crítico (P1/S1) que afecta directamente la facturación y la confianza del cliente, lo que podría derivar en problemas legales o contracargos. Procedo a documentar el bug y su flujo de resolución.

---

### 1. Reporte de Defecto (Jira Ticket)

*   **Ticket ID:** OLM-8452 (Autogenerado)
*   **Título:** [Pagos] - Cobro exitoso en tarjeta de crédito sin creación de orden en el sistema.
*   **Tipo de Issue:** Bug
*   **Severidad:** Crítica (Blocker) - Pérdida de datos transaccionales financieros.
*   **Prioridad:** Muy Alta (P1) - Afecta a usuarios en producción y genera pérdida monetaria.
*   **Componentes:** `PaymentGateway`, `OrderService`, `CheckoutUI`.
*   **Entorno:** Producción (App Web v2.4.1, Backend API v3.1.0).
*   **Dispositivo/Navegador:** Múltiples (reportado inicialmente en Chrome 124 / Windows 11).

**Descripción:**
Durante el proceso de checkout, si ocurre una latencia o un *timeout* en la comunicación entre el servicio de la pasarela de pagos (ej. Stripe/MercadoPago) y nuestro `OrderService`, la pasarela procesa el cobro al cliente, pero el webhook de confirmación no es procesado correctamente por OpenLib Market. El usuario ve una pantalla de error genérica ("Hubo un problema con tu orden") pero recibe el SMS/Notificación de cobro de su banco.

**Pasos para reproducir:**
*(Nota: Para replicar en ambiente Staging o QA, se debe simular un timeout en la respuesta del webhook).*
1. Iniciar sesión con un usuario de prueba válido.
2. Agregar un libro al carrito (ej. ISBN: 978-3-16-148410-0).
3. Proceder al Checkout y seleccionar "Tarjeta de Crédito" como método de pago.
4. Ingresar una tarjeta de crédito válida.
5. Usar una herramienta como Charles Proxy o interceptor de red para forzar un *timeout* (delay > 15s) en el endpoint `/api/v1/payments/webhook`.
6. Hacer clic en "Pagar".

**Resultado Actual:**
El dinero es descontado de la tarjeta del cliente (verificado en el dashboard de la pasarela de pagos). Sin embargo, la interfaz muestra un error, el carrito no se vacía y la tabla `orders` en la base de datos no refleja la transacción.

**Resultado Esperado:**
Si el pago es exitoso en la pasarela, la orden debe crearse obligatoriamente en el estado "PAGADO" en la base de datos. Si falla la creación de la orden internamente, se debe disparar un mecanismo de compensación automático (rollback/refund) para devolver el dinero a la tarjeta y notificar al usuario.

**Evidencia adjunta:**
*   `checkout_console_error.png`
*   `kibana_logs_trace_abc123.txt` (Muestra NullPointerException en OrderService.java:142)
*   `stripe_dashboard_screenshot.jpg`

---

### 2. Ciclo de Vida del Defecto en Jira (Workflow)

Para asegurar la trazabilidad y calidad en la resolución, el ticket pasará por los siguientes estados:

| Estado (Status) | Descripción | Responsable de moverlo a este estado |
| :--- | :--- | :--- |
| **1. TO DO / OPEN** | El bug es creado e ingresado al backlog. Debido a su prioridad (P1), entra inmediatamente al sprint actual. | **QA Engineer** (Quien reporta el bug) |
| **2. IN ANALYSIS** | Se asigna a un desarrollador para que investigue los logs y determine la causa raíz del *timeout* o fallo de sincronización. | **Developer / Tech Lead** |
| **3. IN PROGRESS** | El desarrollador identifica el problema y comienza a escribir el código para solucionarlo (ej. implementar colas de mensajes RabbitMQ/Kafka o mejorar las transacciones @Transactional). | **Developer** |
| **4. CODE REVIEW** | El desarrollador termina el parche y crea un Pull Request (PR). Otro ingeniero revisa el código para asegurar que no rompe nada más. | **Developer (Revisor par)** |
| **5. READY FOR QA** | El PR es aprobado, fusionado (merged) y desplegado automáticamente al ambiente de Staging/QA. | **Pipeline CI/CD (o Developer)** |
| **6. IN TESTING** | QA toma el ticket y ejecuta los "Pasos para reproducir" en Staging para validar que la corrección funciona y que los rollbacks ocurren correctamente. | **QA Engineer** |
| **7. READY FOR DEPLOY** | QA aprueba la corrección. El Product Owner autoriza el paso a Producción (dado que es un hotfix, puede ser inmediato). | **QA Engineer / Product Owner** |
| **8. DONE / CLOSED** | El parche se despliega en Producción. Se monitorean los logs por 24h para asegurar que no vuelva a ocurrir. El ticket se cierra. | **DevOps / Tech Lead** |

*(Flujo alternativo: Si en el estado IN TESTING el bug sigue ocurriendo, el QA devuelve el ticket al estado IN PROGRESS asignándolo nuevamente al Developer).*
```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

[Pendiente de análisis]

#### 2. ¿Qué se puede mejorar?

[Pendiente de análisis]

#### 3. Respuesta final

[Pendiente de análisis]
