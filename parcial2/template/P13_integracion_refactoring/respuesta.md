# Plantilla de entrega — Parcial 2


## Pregunta [13]: [P13_integracion_refactoring]

### Estudiante
- **Nombre completo**: [Danna Gabriela Rojas BBernal]

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Sin IA — respuesta propia** | [Claude / ChatGPT / Gemini / Copilot / DeepSeek / Qwen / Mistral / Otro / **Sin IA — respuesta propia**] |
| **N/A** | [Ej: Claude Opus 4.5, GPT-4o, Gemini 2.5 Pro, etc. Si respondes sin IA, escribe "N/A"] |
| **s epuede hacer sin AI** | [Justifica en 1-3 oraciones. Si respondes sin IA, explica por qué decidiste no usar LLM para esta pregunta.] |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```
[Pega aquí el prompt exacto que enviaste al LLM. 
Incluye TODO el texto, sin editar ni resumir.

Un buen prompt incluye:
- Contexto del proyecto OpenLib Market
- El código o situación específica
- Lo que esperas que el LLM haga
- Restricciones (ej: "usa Java 21", "aplica SOLID")
- Formato de salida esperado (ej: "respuesta en markdown con código Java")]
```

---

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**
[Pega aquí la respuesta COMPLETA del LLM, sin editar, sin resumir.
Incluye TODO el texto, código, explicaciones que generó el LLM.

Si el LLM generó código, asegúrate de que esté correctamente formateado.
Si tuviste que hacer varias iteraciones, pega la MEJOR respuesta obtenida,
pero menciona cuántas iteraciones hiciste.]
```

---

### Análisis crítico de la respuesta
La clase Notificador presenta problemas de diseño porque concentra varias responsabilidades en un solo lugar. Por un lado, se encarga de enviar distintos tipos de notificaciones (Email, SMS y Push) y, por otro, también realiza el registro de logs. Esto viola el principio de Responsabilidad Única . Además, el uso de múltiples if-else provoca que cada vez que se agregue o modifique un tipo de notificación sea necesario cambiar la clase, incumpliendo el principio Abierto/Cerrado (OCP).

Para refactorizarla, se puede aplicar el patrón Strategy creando una interfaz Notificacion con un método enviar(), y luego implementar clases específicas como EmailNotificacion, SMSNotificacion y PushNotificacion. De igual forma, el registro de logs debería moverse a una clase independiente encargada únicamente de esa tarea.Seria ago asi para que se eleiminan las condicionales y cada clase tenga su unica responsabilidad:
Interface Notificacion
    enviar(destinatario, mensaje)

EmailNotificacion implementa Notificacion

SMSNotificacion implementa Notificacion

PushNotificacion implementa Notificacion
LoggerService
    registrar(...)

Notificador

    Notificacion estrategia
    LoggerService logger

    enviar(destinatario, mensaje)
        estrategia.enviar(destinatario, mensaje)
        logger.registrar(...)


#### 1. ¿Qué hizo bien el prompt?

[Evalúa tu propio prompt, no la respuesta del LLM. ¿El contexto fue suficiente? ¿Las restricciones fueron claras? ¿El formato de salida que pediste ayudó a obtener una buena respuesta? ¿Qué parte de tu prompt fue más efectiva? Sé específico: menciona fragmentos concretos de tu prompt que funcionaron bien.]


#### 2. ¿Qué se puede mejorar?

[¿Qué le faltó a tu prompt? ¿Qué harías diferente si pudieras reformularlo? ¿El LLM entendió mal algo por falta de claridad en tu prompt? ¿La respuesta tiene errores u omisiones? ¿Qué no cubrió el LLM que tú sí sabes por lo visto en clase?]


#### 3. Respuesta final

[Escribe tu respuesta definitiva a la pregunta del parcial, integrando lo que aprendiste del LLM pero yendo más allá. Corrige errores, llena omisiones, conecta con conceptos vistos en clase. Esta es tu respuesta: demuestra que tú dominas el tema.]
