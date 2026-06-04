# Plantilla de entrega — Parcial 2
---

## Pregunta [11]: P11_pruebas_manual_vs_auto]

### Estudiante
- **Nombre completo**: Samuel López Gómez

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Claude |
| **Modelo específico** |Sonnet 4.6 |
| **¿Por qué elegiste este LLM?** | La Ia dijo "Te sugiero usar GPT-4o o Claude 3.5 Sonnet. Ambos son excelentes generando tablas comparativas y argumentando recomendaciones técnicas (como decidir qué se automatiza y qué no)". |

---

### Prompt utilizado

Actúa como un QA Lead (Líder de Calidad) definiendo la estrategia de pruebas para el primer release del proyecto OpenLib Market (una plataforma e-commerce de libros en Java/Spring Boot).

Necesito que compares exhaustivamente las pruebas manuales y las pruebas automatizadas. Tu respuesta debe incluir exactamente lo siguiente:
1. Al menos 3 ventajas y 3 desventajas de las pruebas manuales.
2. Al menos 3 ventajas y 3 desventajas de las pruebas automatizadas.
3. Qué tipos de pruebas específicas se adaptan mejor a cada enfoque (ej. exploratorias, regresión, carga, usabilidad).
4. Criterios de decisión claros: ¿Cuándo el equipo de OpenLib Market debe automatizar una prueba y cuándo debe dejarla manual?
5. Una recomendación concreta y realista sobre qué flujos exactos de OpenLib Market deberían ser manuales y cuáles automatizados para este primer release.

Restricciones:
- No des respuestas de libro de texto genéricas. Justifica tus recomendaciones basándote en que somos un e-commerce.

Formato de salida esperado: Markdown estructurado con secciones para "Ventajas y Desventajas", "Tipos de Pruebas", "Criterios de Decisión" y "Recomendación para OpenLib Market".

```

---

### Respuesta del LLM
# Estrategia de Pruebas — OpenLib Market · Primer Release

> **Rol:** QA Lead  
> **Proyecto:** OpenLib Market · E-commerce de libros · Java 21 / Spring Boot  
> **Alcance:** Definición de estrategia de pruebas manuales vs. automatizadas para el primer release productivo

---

## Contexto del proyecto

OpenLib Market es un e-commerce con dos roles definidos (BUYER y ADMIN), flujos transaccionales críticos (carrito → checkout → orden), un catálogo de libros con aprobación administrativa, y un módulo de notificaciones. El equipo ya cuenta con pruebas unitarias básicas (`BookControllerTest`, `OrderStateTest`, `SortStrategyTest`) y un patrón Observer para eventos de órdenes. El primer release implica un equipo con tiempo y presupuesto acotados, lo que hace que la priorización de qué automatizar sea una decisión arquitectural, no solo técnica.

---

## Ventajas y Desventajas

### Pruebas Manuales

#### Ventajas

**1. Detección de problemas de experiencia de usuario que ningún script puede ver**  
Un tester humano que navega el catálogo de libros nota si una portada se carga lenta, si el botón "Agregar al carrito" está demasiado cerca del borde en móvil, o si el mensaje de error de stock agotado es confuso. Ningún assertion de Selenium valida "esto se siente raro". En e-commerce, donde la conversión depende de micro-fricciones, esta capacidad es irreemplazable.

**2. Exploración libre sin guión predefinido (testing exploratorio)**  
Un tester experimentado puede descubrir, en 30 minutos de exploración libre del checkout de OpenLib, combinaciones de estados que ningún caso de prueba documentado anticipó: ¿qué pasa si se aplica un cupón, se vacía el carrito, se vuelve a llenar y se intenta completar la orden? Los bugs más costosos en e-commerce suelen vivir en estas intersecciones de estados.

**3. Costo de entrada prácticamente cero para el primer release**  
Configurar un framework de automatización de UI (Selenium, Playwright) para un proyecto que aún está cambiando su estructura de vistas consume días de setup. Las pruebas manuales permiten validar los flujos críticos desde el primer día sin inversión en infraestructura de testing, lo que es determinante cuando el equipo de OpenLib tiene un deadline de primer release.

#### Desventajas

**1. No escalan con el crecimiento del catálogo y los flujos**  
Hoy OpenLib puede tener 50 libros en catálogo. Si crece a 5.000 y se agregan categorías, filtros y estados de inventario, la regresión manual de "verificar que el catálogo muestra correctamente los libros aprobados" pasa de 10 minutos a 2 horas. El costo de la regresión manual crece linealmente con el producto.

**2. Son inherentemente no repetibles con precisión**  
Dos testers ejecutando el mismo caso de prueba del checkout de OpenLib pueden llegar a resultados distintos según su interpretación del criterio de aceptación, su velocidad al completar los pasos, o si uno olvidó limpiar el carrito de una sesión anterior. Esta variabilidad introduce falsos negativos que erosionan la confianza del equipo en los resultados.

**3. Son incompatibles con integración continua**  
Cada vez que un desarrollador hace push a la rama principal del proyecto, no hay forma de disparar automáticamente una batería de pruebas manuales. Esto significa que defectos introducidos en `OrderService` o `BookService` solo se detectan cuando alguien los ejecuta manualmente, potencialmente horas o días después del commit que los causó.

---

### Pruebas Automatizadas

#### Ventajas

**1. Regresión continua sin costo marginal por ejecución**  
Una vez escrito el test que verifica que una orden en estado `PENDING` no puede transicionar directamente a `DELIVERED` (la máquina de estados ya existe en el proyecto), ese test corre en cada build, en cada rama, durante años, sin costo adicional. Para un e-commerce con lógica de estados de orden compleja, esto es capital.

**2. Velocidad de feedback para el desarrollador**  
Un ciclo de prueba unitaria en OpenLib (correr `mvn test`) tarda segundos. Un desarrollador que modifica `OrderService.processOrder()` sabe antes de hacer commit si rompió algún contrato. Comparado con esperar a que un tester manual ejecute el flujo de checkout, el feedback llega 100x más rápido, cuando el contexto del cambio todavía está fresco.

**3. Documentación viva del comportamiento esperado**  
Los tests de `OrderStateTest` ya existentes en el proyecto son la especificación ejecutable del comportamiento de la máquina de estados. Cualquier desarrollador nuevo en OpenLib puede leer esos tests y entender exactamente qué transiciones son válidas, sin depender de documentación que puede quedar desactualizada.

#### Desventajas

**1. Alto costo de mantenimiento cuando la UI cambia frecuentemente**  
Las pruebas de UI automatizadas (Selenium/Playwright sobre las vistas JavaFX o web de OpenLib) son frágiles. Si el equipo renombra un botón, cambia el ID de un campo del formulario de checkout, o reorganiza el layout de `BuyerDashboardView`, los tests de UI se rompen. En un primer release donde las vistas todavía están evolucionando, automatizar UI agresivamente genera más trabajo de mantenimiento que valor.

**2. No detectan problemas de usabilidad ni bugs visuales**  
Un test automatizado puede confirmar que el flujo de registro de `RegisterView` completa sin excepciones y que el usuario queda en la BD. No puede detectar que el campo de contraseña tiene el placeholder en un color casi invisible sobre fondo blanco, o que el botón de registro está debajo del fold en pantallas de 768px. En e-commerce, estos problemas impactan directamente la tasa de conversión.

**3. Inversión inicial significativa que compite con features del primer release**  
Configurar un pipeline de CI/CD con cobertura de integración, mocks de servicios externos (gateway de pagos, servicio de correo), y datos de prueba reproducibles para OpenLib puede tomar entre una y dos semanas de un desarrollador senior. En el contexto de un primer release con alcance fijo, esta inversión debe justificarse con claridad.

---

## Tipos de Pruebas: ¿Manual o Automatizado?

### Pruebas que pertenecen al mundo manual

**Testing exploratorio**  
Sin script, con tiempo limitado y un objetivo definido (ej. "intenta hacer una compra fallida de todas las formas posibles"). Es la técnica más efectiva para descubrir bugs en intersecciones de estados que nadie anticipó. Imposible de automatizar por definición.

**Pruebas de usabilidad**  
¿El flujo de checkout de OpenLib es intuitivo para alguien que nunca usó la plataforma? ¿El mensaje "Tu orden ha sido procesada" genera confianza? Estas preguntas requieren observar a usuarios reales o testers actuando como usuarios, no assertions en código.

**Pruebas de compatibilidad visual (smoke visual)**  
Verificar que la vista del catálogo se ve correctamente en Chrome, Firefox, Safari y en resoluciones de 1920px, 1366px y 375px (móvil). Aunque existen herramientas de regresión visual (Percy, Chromatic), para un primer release el ojo humano es más rápido y confiable dado el tiempo disponible.

**Pruebas de flujos de pago con terceros**  
La integración con pasarelas de pago (Stripe, PayU) involucra sandboxes con comportamiento no completamente determinista, flujos 3DS que abren ventanas del banco, y casos edge que los mocks no reproducen fielmente. Estas pruebas se ejecutan manualmente antes de cada release en un ambiente staging conectado al sandbox real.

**Pruebas de aceptación con el Product Owner**  
El PO debe revisar manualmente que las historias de usuario de `historia_usuario_buyer.md` y `historia_usuario_admin.md` están implementadas según lo acordado. Ninguna herramienta reemplaza la validación del stakeholder.

---

### Pruebas que pertenecen al mundo automatizado

**Pruebas unitarias de lógica de negocio**  
La máquina de estados de órdenes (`PendingState`, `PaidState`, `ShippedState`, etc.), el cálculo de totales del carrito, las reglas de validación de `Book.status`. Son deterministas, rápidas y el valor de tenerlas en CI es inmediato. El proyecto ya tiene base con `OrderStateTest`.

**Pruebas de integración de la API REST**  
Verificar que `POST /orders` retorna 201 con el body correcto, que `GET /books?status=APROBADO` no devuelve libros pendientes, que `PATCH /admin/books/{id}` requiere rol ADMIN. Estas pruebas con `MockMvc` o `RestAssured` son el contrato de la API y deben correr en cada build.

**Pruebas de regresión de flujos críticos (E2E)**  
Una vez que los flujos de registro, login y checkout estén estables (no antes), automatizar el happy path con Playwright: un usuario se registra, busca un libro, lo agrega al carrito, hace checkout, y la orden aparece en su historial. Este test corre como smoke test antes de cada deployment.

**Pruebas de carga y performance**  
Simular 500 usuarios concurrentes navegando el catálogo de OpenLib con k6 o Gatling. Ningún tester humano puede generar esa carga de forma reproducible. Estas pruebas deben correr al menos una vez antes del primer release para identificar cuellos de botella en `BookService.getPublicCatalog()` bajo carga real.

**Pruebas de seguridad básicas (DAST)**  
Herramientas como OWASP ZAP pueden escanear automáticamente los endpoints de OpenLib buscando vulnerabilidades comunes (SQL injection, XSS, headers de seguridad faltantes). Automatizar este scan en el pipeline de staging elimina una categoría entera de vulnerabilidades sin esfuerzo manual.

---

## Criterios de Decisión

Antes de decidir si una prueba debe automatizarse, el equipo de OpenLib debe evaluar los siguientes criterios:

### Automatizar cuando...

| Criterio | Indicador concreto en OpenLib |
|---|---|
| **Frecuencia de ejecución alta** | El caso se ejecutaría más de 10 veces antes del release (ej. verificar login en cada PR) |
| **Determinismo alto** | El resultado esperado es siempre el mismo dado el mismo input (ej. transición de estados de orden) |
| **Riesgo de regresión alto** | El componente es tocado frecuentemente y un bug costaría dinero real (ej. cálculo del total del carrito) |
| **Lógica de negocio compleja** | Múltiples reglas condicionales que un humano puede omitir por error (ej. descuentos + stock + estado del libro) |
| **Flujo crítico de negocio** | Es el happy path que genera ingresos: registro → catálogo → carrito → checkout → confirmación |
| **Verificación de contrato de API** | Cualquier endpoint público de la API que otros sistemas o el frontend consuman |

### Dejar manual cuando...

| Criterio | Indicador concreto en OpenLib |
|---|---|
| **UI en evolución activa** | Las vistas de `AdminBooksView` o `BuyerDashboardView` cambian cada sprint |
| **Involucra juicio subjetivo** | "¿El mensaje de error es claro para el usuario?" |
| **Requiere terceros no deterministos** | Flujos 3DS de pago, callbacks de pasarela, emails reales |
| **Se ejecuta raramente** | Pruebas de flujos que solo ocurren en eventos excepcionales (ej. devolución de orden entregada) |
| **Costo de automatización > costo de ejecución manual en el release** | Si automatizar toma 2 días y el release es en 3 días, ejecutar manualmente 1 vez es la decisión correcta |
| **Es una prueba exploratoria por naturaleza** | El valor está en la creatividad del tester, no en la repetición |

### La regla del ROI de automatización

```
Automatizar SI:  (Tiempo_ejecución_manual × Frecuencia_ejecuciones) > Tiempo_automatización + Tiempo_mantenimiento

Ejemplo — Prueba de login de OpenLib:
  Ejecución manual: 3 min × 50 ejecuciones en el ciclo de vida = 150 min
  Automatización: 2 horas escritura + 30 min mantenimiento = 150 min
  Resultado: punto de equilibrio en el primer release → automatizar (el valor crece exponencialmente en releases futuros)

Ejemplo — Prueba visual de portadas en modo oscuro:
  Ejecución manual: 5 min × 3 ejecuciones = 15 min
  Automatización: 4 horas setup visual regression tool = 240 min
  Resultado: no automatizar en el primer release
```

---

## Recomendación para OpenLib Market — Primer Release

### Flujos que DEBEN ser manuales (y por qué)

**1. Prueba exploratoria del checkout completo (2 sesiones × 45 min)**  
Dos testers realizan checkout de formas distintas: uno sigue el flujo feliz con datos válidos, el otro intenta romperlo (carrito vacío, stock agotado entre agregar y pagar, sesión expirada a mitad del checkout). El objetivo no es verificar lo esperado sino descubrir lo inesperado. Esta sesión es el activo de calidad más valioso del release.

**2. Validación de aceptación de historias de usuario con el PO**  
Revisar punto por punto `historia_usuario_buyer.md` y `historia_usuario_admin.md` con el Product Owner. La aceptación del PO es el criterio de "done" para el release, y ese criterio no puede delegarse a un script.

**3. Prueba de compatibilidad visual en resoluciones clave**  
Verificar manualmente la vista del catálogo (`LibraryView`), el carrito (`CartView`) y el checkout (`CheckoutView`) en Chrome/Firefox/Safari y en 1920px, 1366px y 375px. Con las vistas todavía en evolución, esta prueba manual de 2 horas aporta más que configurar un tool de regresión visual.

**4. Flujo completo con sandbox de pasarela de pago**  
Ejecutar al menos 5 escenarios contra el sandbox real: pago exitoso, tarjeta declinada, fondos insuficientes, timeout de red, y 3DS challenge. No existe mock que reproduzca estos escenarios con fidelidad completa.

**5. Prueba de usabilidad del flujo de registro para un usuario nuevo**  
Pedir a una persona que nunca vio OpenLib Market que intente registrarse y comprar un libro sin instrucciones. Observar en silencio. Los puntos donde duda o se equivoca son los bugs de UX del release.

---

### Flujos que DEBEN estar automatizados (y por qué)

**1. Pruebas unitarias de la máquina de estados de órdenes** ← ya existe base  
`OrderStateTest` debe cubrir el 100% de las transiciones definidas en `PendingState`, `PaidState`, `ShippedState`, `DeliveredState` y `CancelledState`. Si una nueva feature rompe una transición, el build falla antes de llegar a staging. Tiempo estimado para completar cobertura: 3 horas.

**2. Pruebas de integración de los endpoints críticos de la API**  
Con `MockMvc` + `@SpringBootTest`, automatizar:
- `POST /auth/register` → 201 + usuario en BD
- `POST /auth/login` → 200 + JWT válido / 401 con credenciales incorrectas
- `GET /books` → solo retorna libros con `status=APROBADO`
- `POST /orders` → crea orden, descontando stock, retornando 201
- `PATCH /admin/books/{id}/approve` → requiere rol ADMIN, retorna 403 con BUYER

Estos 5 grupos de tests son el contrato de la API. Si alguno falla, el deployment se bloquea automáticamente.

**3. Prueba de regresión de los observadores de notificaciones**  
`OrderObserverTest` existe en el proyecto. Debe extenderse para verificar que cuando un libro pasa a DISPONIBLE, los 4 observadores (`EmailWishlistObserver`, `PushFavoritesObserver`, `RedisCacheObserver`, `AuditLogObserver`) son notificados, y que el fallo de uno no interrumpe a los demás. Esto protege el módulo de notificaciones en cada refactor.

**4. Prueba de carga del catálogo público (antes del go-live, una vez)**  
Con k6, simular 300 usuarios concurrentes ejecutando `GET /books` durante 2 minutos. El criterio de aceptación: P95 de latencia < 500ms, cero errores 5xx. Si el sistema falla esta prueba, se posterga el release. Esta prueba no corre en cada build (es costosa), sino como gate del release.

**5. Smoke test E2E del happy path (Playwright)**  
Automatizar el flujo mínimo que representa una venta exitosa:
```
registro de usuario → login → búsqueda de libro → agregar al carrito → checkout → confirmación de orden
```
Este test corre en staging antes de cada deployment. Si falla, el deployment se bloquea. Es el "¿está vivo el negocio?" test. Tiempo estimado de escritura: 1 día. Valor: evita que un deployment rompa el flujo de compra sin que nadie lo note hasta que un cliente lo reporte.

---

### Mapa de cobertura recomendado para el primer release

```
CAPA              TIPO                    HERRAMIENTA          MOMENTO DE EJECUCIÓN
─────────────────────────────────────────────────────────────────────────────────
Unitaria          Lógica de negocio       JUnit 5 + Mockito    Cada commit (CI)
Unitaria          Estrategias de sort     JUnit 5              Cada commit (CI)
Integración       Endpoints API REST      MockMvc / RestAssured Cada PR (CI)
Integración       Observers / Events      JUnit 5 + Mockito    Cada PR (CI)
E2E               Happy path compra       Playwright           Pre-deployment (CD)
Performance       Carga del catálogo      k6                   Gate del release
─────────────────────────────────────────────────────────────────────────────────
MANUAL            Exploratoria checkout   Testers              1 semana antes del release
MANUAL            Aceptación PO           PO + QA Lead         3 días antes del release
MANUAL            Compatibilidad visual   QA                   2 días antes del release
MANUAL            Sandbox de pagos        QA + Dev             2 días antes del release
MANUAL            Usabilidad registro     Usuario externo      1 semana antes del release
```

---

### Deuda técnica de QA aceptada conscientemente en el release

Las siguientes pruebas se documentan como deuda de calidad conocida, priorizadas para el segundo release:

- Pruebas de regresión visual automatizadas (Percy o Chromatic) — aplazar hasta que las vistas sean estables
- Pruebas de seguridad automatizadas (OWASP ZAP en pipeline) — aplazar hasta contar con ambiente de staging dedicado
- Pruebas de accesibilidad automatizadas (axe-core) — aplazar, pero ejecutar una revisión manual básica antes del release
- Pruebas de contrato entre frontend y API (Pact) — aplazar hasta que el frontend sea independiente del backend

Documentar esta deuda explícitamente es parte de la responsabilidad del QA Lead: el equipo conoce qué riesgos está asumiendo conscientemente en el primer release, y hay un plan para cubrirlos.

---

*Elaborado para OpenLib Market · QA Lead · Primer Release · Java 21 / Spring Boot*
```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?



#### 2. ¿Qué se puede mejorar?



#### 3. Respuesta final
