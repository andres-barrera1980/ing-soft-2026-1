# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [XX]: [Título resumido]

### Estudiante
- **Nombre completo**: [Tu nombre y apellido]

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [Claude / ChatGPT / Gemini / Copilot / DeepSeek / Qwen / Mistral / Otro / **Sin IA — respuesta propia**] |
| **Modelo específico** | [Ej: Claude Opus 4.5, GPT-4o, Gemini 2.5 Pro, etc. Si respondes sin IA, escribe "N/A"] |
| **¿Por qué elegiste este LLM?** | [Justifica en 1-3 oraciones. Si respondes sin IA, explica por qué decidiste no usar LLM para esta pregunta.] |

---

### Prompt utilizado
Explica el ciclo de vida de un defecto aplicado al caso del botón “Agregar al carrito” en OpenLib Market. Para cada fase (detección → reporte → asignación → diagnóstico → corrección → verificación → cierre) indica: ¿quién es responsable?, ¿qué acción se toma?, ¿qué herramienta se usa?, ¿cuál sería el siguiente estado del defecto en Jira?

---

### Respuesta del LLM

A continuación tienes el ciclo de vida de un defecto aplicado al caso real de OpenLib Market (botón “Agregar al carrito” no responde al intentar agregar un sexto libro), con enfoque práctico de QA y Jira.

🧩 Ciclo de Vida del Defecto
🧪 Caso

El usuario intenta agregar un sexto libro al carrito, pero:

El botón no responde
No hay mensaje de error
No se agrega el libro
1. Detección
👤 Responsable

Tester QA

⚙️ Acción
Ejecuta casos de prueba funcionales del carrito
Detecta comportamiento incorrecto en el flujo de compra
🛠️ Herramienta
Ambiente QA / Staging
Navegador web
Plan de pruebas
📌 Estado en Jira

(No creado aún)

Luego de detectar el bug:

➡️ Se crea el ticket en Jira

2. Reporte
👤 Responsable

Tester QA

⚙️ Acción
Registra el defecto en Jira con:
Pasos para reproducir
Resultado esperado vs actual
Evidencia (capturas / video)
Severidad y prioridad
🛠️ Herramienta

Jira

📌 Estado en Jira

OPEN / NEW

3. Asignación
👤 Responsable

Scrum Master / Tech Lead / QA Lead

⚙️ Acción
Evalúa impacto del bug
Asigna al desarrollador responsable del módulo de carrito
Define prioridad (ej: Alta por impacto en ventas)
🛠️ Herramienta

Jira (tablero Scrum/Kanban)

📌 Estado en Jira

ASSIGNED / TO DO

4. Diagnóstico
👤 Responsable

Desarrollador

⚙️ Acción
Reproduce el error localmente
Revisa logs del frontend y backend
Inspecciona lógica del carrito
Posible causa raíz típica:
if (carrito.size() >= 5) {
    return; // bloquea el sexto libro incorrectamente
}

o error frontend:

if (items.length === 5) disableButton();
🛠️ Herramientas
IDE (IntelliJ / VS Code)
Debugger
Logs de aplicación
Browser DevTools
Jira
📌 Estado en Jira

IN PROGRESS

5. Corrección
👤 Responsable

Desarrollador

⚙️ Acción
Corrige la lógica del carrito
Ajusta validación incorrecta
Ejecuta pruebas unitarias y manuales locales
Ejemplo de fix:
// eliminar límite incorrecto o corregir regla de negocio
if (carrito.size() > MAX_ITEMS) {
    throw new BusinessException();
}
🛠️ Herramientas
IDE
Git
Tests unitarios (JUnit)
CI local
📌 Estado en Jira

RESOLVED / READY FOR TEST

6. Verificación
👤 Responsable

Tester QA

⚙️ Acción
Repite el escenario original:
Agregar 6 libros al carrito
Ejecuta pruebas de regresión:
agregar 1–10 libros
eliminar libros
checkout
🛠️ Herramientas
Jira
Ambiente QA
Suite de pruebas manuales / automatizadas
📌 Estado en Jira

TESTING / VERIFYING

7. Cierre
👤 Responsable

QA Lead / Tester QA

⚙️ Acción
Confirma que el bug no ocurre más
Verifica que no haya regresiones
Aprueba el ticket como finalizado
🛠️ Herramientas
Jira
Evidencia de pruebas
📌 Estado en Jira

CLOSED

🔁 Resumen del flujo en Jira
OPEN
  ↓
ASSIGNED
  ↓
IN PROGRESS
  ↓
RESOLVED
  ↓
TESTING
  ↓
CLOSED
⚠️ Estados adicionales posibles
Estado	Cuándo ocurre
REOPENED	El bug reaparece tras la corrección
REJECTED	No es un bug (comportamiento esperado)
DUPLICATE	Ya existe otro ticket igual
DEFERRED	Se pospone para otra versión
🧠 Conclusión

El ciclo de vida del defecto en OpenLib Market asegura trazabilidad completa desde la detección hasta el cierre. Cada fase tiene un responsable claro, una herramienta asociada (principalmente Jira), y un estado definido que permite controlar el progreso del error. Este flujo garantiza calidad del software, comunicación entre equipos y validación correcta antes de liberar nuevas versiones del sistema.
```

---

### Análisis crítico de la respuesta
La respuesta del LLM aplicó correctamente el ciclo de vida del defecto al caso de OpenLib Market, mostrando un flujo claro desde la detección hasta el cierre, con responsables definidos, acciones concretas y estados en Jira bien descritos; además, incluyó estados adicionales como Reopened o Duplicate, lo que demuestra exhaustividad, y conectó la teoría con la práctica al dar un ejemplo de causa raíz en la validación del carrito.
#### 1. ¿Qué hizo bien el prompt?

Lo más acertado fue que cubrió todas las fases sin omitir ninguna, asignó responsables adecuados (tester, QA lead, desarrollador), vinculó cada fase con herramientas específicas (Jira, IDE, DevTools), y dio ejemplos de código que ilustran la causa raíz, lo que aporta realismo y aplicabilidad; también destacó la importancia de la trazabilidad en Jira y la validación en QA antes del cierre.


#### 2. ¿Qué se puede mejorar?

La fase de diagnóstico fue tratada de manera superficial, ya que no profundizó en cómo se documenta la causa raíz ni en la diferencia entre un bug funcional y uno de interfaz; además, podría haber enfatizado más la comunicación entre QA y desarrollo durante la verificación y detallado cómo se asegura la trazabilidad completa en Jira, lo que haría la respuesta más sólida y cercana a las prácticas profesionales de gestión de defectos.


#### 3. Respuesta final

el defecto del botón “Agregar al carrito” sigue el ciclo de vida estándar: primero es detectado por el tester al ejecutar pruebas funcionales, luego reportado en Jira con pasos, evidencia y severidad, después asignado por el QA Lead o Scrum Master al desarrollador responsable, quien en la fase de diagnóstico reproduce el error y encuentra la causa raíz en una validación incorrecta que bloquea el sexto ítem; en la fase de corrección el desarrollador ajusta la lógica y valida con pruebas unitarias, luego el tester realiza la verificación en ambiente QA confirmando que el carrito acepta más de cinco libros y que el contador se actualiza, y finalmente el defecto se cierra en Jira tras comprobar que no hay regresiones; este flujo asegura trazabilidad, responsables claros y estados definidos (Nuevo → Asignado → En progreso → Resuelto → Verificado → Cerrado), y demuestra cómo la gestión disciplinada de defectos garantiza calidad y comunicación efectiva en OpenLib Market.
