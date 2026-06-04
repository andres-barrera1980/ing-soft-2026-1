# Plantilla de entrega — Parcial 2
---

## Pregunta [12]: [P12_ciclo_vida_defectos]

### Estudiante
- **Nombre completo**: Samuel López Gómez

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Claude |
| **Modelo específico** |Sonnet 4.6 |
| **¿Por qué elegiste este LLM?** | La IA dijo "Te sugiero GPT-4o o Claude 3.5 Sonnet. Ambos conocen a la perfección los flujos de Jira, GitHub y el rol de QA en el desarrollo ágil." |

---

### Prompt utilizado

Actúa como un QA Lead y Scrum Master en el proyecto OpenLib Market. Un tester acaba de reportar el siguiente defecto en Jira:

Resumen: El botón "Agregar al carrito" no responde cuando el usuario tiene más de 5 ítems en el carrito.
Pasos para reproducir: (1) Iniciar sesión como comprador, (2) Agregar 5 libros diferentes al carrito, (3) Intentar agregar un sexto libro desde la página de detalle del producto.
Resultado esperado: El libro se agrega al carrito y el contador se actualiza. (Nota de negocio: El límite real es de 10 ítems distintos, por lo que el sexto debería agregarse sin problema).
Resultado obtenido: El botón no produce ninguna acción. No hay mensaje de error ni retroalimentación visual.

Necesito que gestiones este defecto aplicando el ciclo de vida de defectos clásico (Detección → Reporte → Asignación → Diagnóstico → Corrección → Verificación → Cierre).

Para cada fase, debes indicar explícitamente:
1. ¿Quién es el responsable? (Rol en el equipo).
2. ¿Qué acción exacta se toma relacionada con este bug específico? (Atrévete a sugerir una posible causa raíz en el diagnóstico, tanto en front como en back).
3. ¿Qué herramienta(s) se usa(n)?
4. ¿Cuál sería el estado exacto del ticket en Jira al finalizar esa fase?

Restricciones:
- Aterriza todo al caso de OpenLib Market, no des definiciones genéricas.
- Usa herramientas reales del stack de desarrollo (Jira, GitHub, IntelliJ, Chrome DevTools, etc.).

Formato de salida esperado: Markdown, con un subtítulo para cada fase del ciclo de vida conteniendo los 4 puntos solicitados de forma clara.

```

---

### Respuesta del LLM
# Ciclo de Vida del Defecto — OpenLib Market
## BUG-042: "Agregar al carrito" no responde con más de 5 ítems

> **Proyecto:** OpenLib Market · Java 21 / JavaFX / Spring Boot  
> **Reportado por:** QA Tester  
> **Fecha de detección:** Sprint 3 — Ciclo de regresión previo al primer release  
> **Severidad inicial:** Alta — bloquea el flujo de compra principal  
> **Prioridad:** P1 — afecta directamente la conversión del negocio

---

## Fase 1 — Detección

### 1. Responsable
**QA Tester** (rol: Analista de Calidad del equipo de OpenLib).

### 2. Acción exacta tomada
Durante la ejecución del caso de prueba **TC-CART-07** ("Agregar el sexto libro distinto al carrito"), el tester inicia sesión con el usuario `carlos@javeriana.edu.co` (rol BUYER), agrega cinco libros al carrito usando el botón **"Agregar al carrito"** de las tarjetas en `BuyerDashboardView`, y al intentar agregar un sexto libro (`Spring Boot in Action`, ID 7), el botón no produce ninguna reacción visible: sin cambio en el contador del header, sin navegación, sin mensaje de error ni feedback visual de ningún tipo. El comportamiento se reproduce de forma consistente en tres intentos consecutivos. Se verifica también desde el diálogo de detalle del libro (`showBookDetail`) con el botón **"Comprar ahora - GRATIS"**, donde ocurre el mismo silencio. El tester confirma que el límite de negocio documentado en `historia_usuario_buyer.md` establece un máximo de **10 ítems distintos**, por lo que el sexto ítem debería agregarse sin restricción.

### 3. Herramientas utilizadas
- **Aplicación OpenLib Market** en ejecución local (JavaFX desktop)
- **Caso de prueba TC-CART-07** en la suite de regresión del Sprint 3
- **Grabación de pantalla** (OBS Studio) para documentar la reproducción paso a paso
- **Jira** para la creación inicial del ticket

### 4. Estado del ticket en Jira al finalizar esta fase
```
Estado:   OPEN
Tipo:     Bug
ID:       BUG-042
Resumen:  [Cart] Botón "Agregar al carrito" no responde al intentar agregar el 6° ítem
Severidad: Alta
Prioridad: Sin asignar (pendiente de triage)
Asignado a: Sin asignar
```

---

## Fase 2 — Reporte

### 1. Responsable
**QA Tester** redacta el reporte completo. **QA Lead** lo revisa y enriquece antes de que entre al backlog.

### 2. Acción exacta tomada
El tester completa el ticket BUG-042 en Jira con toda la información necesaria para que el desarrollador pueda reproducirlo sin necesidad de intervención del tester. Se adjuntan: grabación de pantalla (22 segundos mostrando los 5 ítems en carrito y el clic sin efecto), screenshot del header con el contador en "5", y los logs de consola de la JVM capturados durante la sesión (sin excepciones visibles en `stdout`, lo que ya es un dato diagnóstico relevante). El QA Lead añade la etiqueta `regression` y vincula el ticket a la **Historia de Usuario HU-BUYER-03** ("Como comprador, quiero agregar libros al carrito para poder proceder al checkout").

**Descripción completa del ticket:**

```
ENTORNO:
  OS: Windows 11 / macOS 14
  Java: 21 (Temurin)
  Build: commit sha-f3a9c12 (rama main, Sprint 3)
  Usuario de prueba: carlos@javeriana.edu.co / buyer123

PASOS PARA REPRODUCIR:
  1. Iniciar sesión como BUYER (carlos@javeriana.edu.co)
  2. Desde BuyerDashboardView, agregar los libros:
       - Clean Code (ID 1)
       - Design Patterns (ID 2)
       - The Pragmatic Programmer (ID 3)
       - Introduction to Algorithms (ID 4)
       - You Don't Know JS (ID 5)
     → Contador del header muestra: 🛒 5
  3. Intentar agregar "Spring Boot in Action" (ID 7)
     desde la tarjeta del catálogo (botón "Agregar al carrito")
     y también desde el diálogo de detalle (botón "Comprar ahora - GRATIS")

RESULTADO OBTENIDO:
  El botón no produce ninguna acción observable.
  No hay cambio en el contador del header.
  No hay mensaje de error, toast, ni alerta.
  No hay excepción en la consola de la JVM.
  El carrito permanece con 5 ítems.

RESULTADO ESPERADO:
  El libro "Spring Boot in Action" se agrega al carrito.
  El contador del header se actualiza a 6.
  (Regla de negocio: límite = 10 ítems distintos, según HU-BUYER-03)

EVIDENCIA:
  [adjunto] reproduccion_bug042.mp4
  [adjunto] screenshot_carrito_5items.png
  [adjunto] console_log_session.txt

NOTAS ADICIONALES:
  No se lanza ninguna excepción. El comportamiento silencioso sugiere
  una condición de guarda (if/guard clause) que bloquea la ejecución
  antes de llegar a DataStore.addToCart(). Posible hardcode del límite
  en 5 en lugar de 10.
```

### 3. Herramientas utilizadas
- **Jira** (creación y enriquecimiento del ticket BUG-042)
- **Confluence** (referencia cruzada a HU-BUYER-03 y reglas de negocio del carrito)
- **OBS Studio** (grabación de pantalla adjunta)
- **Terminal / JVM console** (captura de logs)

### 4. Estado del ticket en Jira al finalizar esta fase
```
Estado:   OPEN
Tipo:     Bug
ID:       BUG-042
Labels:   regression, cart, blocker-release
Sprint:   Sprint 3
Vinculado a: HU-BUYER-03
Evidencia adjunta: ✓ (video + screenshot + logs)
Asignado a: Sin asignar (pendiente de triage)
```

---

## Fase 3 — Asignación

### 1. Responsable
**Scrum Master** facilita el triage en la Daily Standup o en una sesión de bug triage de 15 minutos. La decisión de asignación la toma el **Tech Lead** del equipo de desarrollo de OpenLib.

### 2. Acción exacta tomada
En la Daily Standup del día siguiente a la detección, el QA Lead presenta BUG-042. Dado que el botón afecta el flujo de compra principal y el primer release está en 5 días, el Tech Lead clasifica el defecto como **P1 / blocker de release** y lo asigna al desarrollador que owns las vistas de comprador y la lógica del carrito (`BuyerDashboardView`, `ViewHelper`, `DataStore`). El ticket se mueve al top del Sprint Backlog y se añade al tablero Kanban como carril de alta prioridad. El Scrum Master actualiza el **burndown** del sprint para reflejar el trabajo no planeado y notifica al Product Owner que el defecto puede impactar la fecha de release si no se resuelve en las próximas 24 horas.

### 3. Herramientas utilizadas
- **Jira** (asignación, cambio de estado, ajuste de sprint)
- **Slack** (canal `#openlib-dev`, mención directa al developer asignado con link al ticket)
- **Google Meet** (sesión de triage de 15 min si no hay Daily inmediata)

### 4. Estado del ticket en Jira al finalizar esta fase
```
Estado:   IN PROGRESS
Tipo:     Bug
ID:       BUG-042
Prioridad: P1 — Blocker
Asignado a: [Developer — owner de cart/BuyerDashboardView]
Sprint:   Sprint 3 (top of backlog)
Estimación: 2h (diagnóstico + fix + unit test)
```

---

## Fase 4 — Diagnóstico

### 1. Responsable
**Developer** asignado, con soporte del **QA Lead** si se necesita reproducir condiciones específicas.

### 2. Acción exacta tomada y causa raíz probable

El desarrollador abre IntelliJ IDEA, reproduce el bug localmente con el usuario `a@test.co` (credenciales de prueba rápida del `DataStore`) y sigue el flujo de ejecución con el debugger.

**Ruta de código bajo inspección:**

```
ViewHelper.productCard()
  └─► btnAdd.setOnAction(e -> onAdd.accept(book))         ← lambda del botón
        └─► BuyerDashboardView.renderCurrentPage()
              └─► b -> { controller.addToCart(b); ... }    ← lambda pasada a productCard
                    └─► BuyerController.addToCart(book)
                          └─► DataStore.addToCart(book)
```

**Causa raíz — Hipótesis A (más probable): Condición de guarda hardcodeada en la lambda de `renderCurrentPage`**

Al revisar el código de `BuyerDashboardView.renderCurrentPage()`, el desarrollador encuentra (o introdujo en un commit reciente) una guarda como la siguiente que nunca fue comunicada al equipo de QA ni al PO:

```java
// BuyerDashboardView.java — renderCurrentPage()
VBox card = ViewHelper.productCard(
    book,
    b -> {
        if (controller.getCartCount() >= 5) {   // ← BUG: debería ser >= 10
            return;                              // ← sin feedback al usuario
        }
        controller.addToCart(b);
        SceneManager.getInstance().showBuyerDashboard();
    },
    () -> showBookDetail(book)
);
```

El límite está hardcodeado en `5` en lugar de `10`, y el bloqueo es silencioso: no hay `Alert`, no hay toast, no hay cambio visual en el botón. Esta condición también explica por qué no hay excepciones en la consola: el `return` corta la ejecución limpiamente antes de llamar a `DataStore`.

**Causa raíz — Hipótesis B (secundaria): Límite incorrecto también en `DataStore.addToCart()`**

```java
// DataStore.java — addToCart()
public void addToCart(Book book) {
    if (cart.size() >= 5) return;   // ← BUG: debería ser >= 10, y sin feedback
    Optional<CartItem> existing = cart.stream()
            .filter(c -> c.getBook().getId().equals(book.getId()))
            .findFirst();
    if (existing.isEmpty()) {
        cart.add(new CartItem(book));
    }
}
```

Es posible que el límite esté en `DataStore` directamente, en la vista, o en ambos. El debugger con un breakpoint en `DataStore.addToCart()` confirmará cuál de los dos niveles intercepta primero.

**Hallazgo adicional de diseño:** independientemente del límite correcto (10), la lógica de límite no debería vivir en la vista (`BuyerDashboardView`) ni en el store. Debería estar en `BuyerController.addToCart()` o en un servicio dedicado, y debe retornar un resultado (`boolean` o `enum`) que permita a la vista mostrar feedback al usuario.

### 3. Herramientas utilizadas
- **IntelliJ IDEA** — debugger con breakpoints en `DataStore.addToCart()`, `BuyerController.addToCart()`, y la lambda de `renderCurrentPage()`
- **Git / GitHub** — `git log --oneline src/main/java/com/openlib/util/DataStore.java` y `git blame` para identificar el commit que introdujo la condición de guarda
- **Jira** — comentario técnico en BUG-042 documentando la causa raíz encontrada

### 4. Estado del ticket en Jira al finalizar esta fase
```
Estado:   IN PROGRESS
ID:       BUG-042
Comentario añadido: "Causa raíz identificada: guarda hardcodeada con límite 5
                     en BuyerDashboardView.renderCurrentPage() (y posiblemente
                     también en DataStore.addToCart()). Fix en progreso.
                     Commit culpable: sha-b2d1e09 (mensaje: 'add cart limit guard')."
Root cause tag: hardcoded-limit, missing-user-feedback
```

---

## Fase 5 — Corrección

### 1. Responsable
**Developer** asignado. El **Tech Lead** hace code review antes del merge.

### 2. Acción exacta tomada

El developer crea la rama `fix/BUG-042-cart-limit-correction` desde `main` y realiza los siguientes cambios:

**Corrección 1 — `DataStore.addToCart()`: corregir límite y retornar resultado**

```java
// DataStore.java
private static final int MAX_CART_ITEMS = 10; // ← constante nombrada, no magic number

public AddToCartResult addToCart(Book book) {
    if (cart.size() >= MAX_CART_ITEMS) {
        return AddToCartResult.LIMIT_REACHED;
    }
    boolean alreadyInCart = cart.stream()
            .anyMatch(c -> c.getBook().getId().equals(book.getId()));
    if (alreadyInCart) {
        return AddToCartResult.ALREADY_IN_CART;
    }
    cart.add(new CartItem(book));
    return AddToCartResult.SUCCESS;
}

public enum AddToCartResult {
    SUCCESS, LIMIT_REACHED, ALREADY_IN_CART
}
```

**Corrección 2 — `BuyerController.addToCart()`: propagar el resultado**

```java
// BuyerController.java
public DataStore.AddToCartResult addToCart(Book book) {
    return store.addToCart(book);
}
```

**Corrección 3 — `BuyerDashboardView.renderCurrentPage()`: eliminar guarda incorrecta, mostrar feedback**

```java
// BuyerDashboardView.java — renderCurrentPage()
VBox card = ViewHelper.productCard(
    book,
    b -> {
        DataStore.AddToCartResult result = controller.addToCart(b);
        switch (result) {
            case SUCCESS ->
                SceneManager.getInstance().showBuyerDashboard();
            case LIMIT_REACHED ->
                ViewHelper.showAlert(Alert.AlertType.WARNING,
                    "Límite del carrito",
                    "Puedes agregar hasta 10 libros distintos por orden.");
            case ALREADY_IN_CART ->
                ViewHelper.showAlert(Alert.AlertType.INFORMATION,
                    "Ya en el carrito",
                    "Este libro ya está en tu carrito.");
        }
    },
    () -> showBookDetail(book)
);
```

**Corrección 4 — `BuyerDashboardView.showBookDetail()`: mismo feedback en el diálogo de detalle**

```java
// Mismo patrón switch aplicado al botón "Comprar ahora - GRATIS"
addBtn.setOnAction(e -> {
    DataStore.AddToCartResult result = controller.addToCart(book);
    dialog.close();
    switch (result) {
        case SUCCESS ->
            SceneManager.getInstance().showBuyerDashboard();
        case LIMIT_REACHED ->
            ViewHelper.showAlert(Alert.AlertType.WARNING,
                "Límite del carrito",
                "Puedes agregar hasta 10 libros distintos por orden.");
        case ALREADY_IN_CART ->
            ViewHelper.showAlert(Alert.AlertType.INFORMATION,
                "Ya en el carrito",
                "Este libro ya está en tu carrito.");
    }
});
```

**Corrección 5 — Test unitario nuevo en `DataStore` (o `BuyerController`)**

```java
// Nuevo test: CartLimitTest.java
@Test
void addToCart_shouldReturnLimitReached_whenCartHasTenItems() {
    DataStore store = DataStore.getInstance();
    store.logout(); // reset cart
    store.setCurrentUser(/* test user */);

    List<Book> books = store.getAllBooks(); // 8 libros en seed
    // Agregar los 8 disponibles
    books.forEach(store::addToCart);
    // Crear 2 libros extra artificiales para llegar a 10
    store.addToCart(new Book(99L, "Extra Book A", ...));
    store.addToCart(new Book(100L, "Extra Book B", ...));

    assertEquals(10, store.getCart().size());

    // El undécimo debe retornar LIMIT_REACHED
    DataStore.AddToCartResult result =
        store.addToCart(new Book(101L, "Extra Book C", ...));
    assertEquals(DataStore.AddToCartResult.LIMIT_REACHED, result);
    assertEquals(10, store.getCart().size()); // tamaño no cambió
}

@Test
void addToCart_shouldAllowSixthItem_whenLimitIsTen() {
    DataStore store = DataStore.getInstance();
    store.logout();
    List<Book> books = store.getAllBooks().subList(0, 6);
    books.forEach(b -> assertEquals(
        DataStore.AddToCartResult.SUCCESS, store.addToCart(b)));
    assertEquals(6, store.getCart().size()); // el 6° se agregó correctamente
}
```

El developer hace commit con el mensaje `fix(cart): correct cart limit from 5 to 10, add AddToCartResult enum, add user feedback [BUG-042]`, abre un **Pull Request** en GitHub con el link al ticket BUG-042, y solicita revisión del Tech Lead.

### 3. Herramientas utilizadas
- **IntelliJ IDEA** — edición, refactor, ejecución de tests
- **Git / GitHub** — rama `fix/BUG-042-cart-limit-correction`, Pull Request con descripción del fix y link al ticket Jira
- **JUnit 5** — nuevos tests unitarios `CartLimitTest`
- **Jira** — transición del ticket a "In Review", comentario con link al PR

### 4. Estado del ticket en Jira al finalizar esta fase
```
Estado:   IN REVIEW
ID:       BUG-042
PR:       github.com/openlib/proyecto/pull/87
          "[fix] BUG-042 — Cart limit corrected to 10, AddToCartResult enum,
           user feedback on limit/duplicate"
Fix verificado localmente por el developer: ✓
Esperando: Code review del Tech Lead + merge a main
```

---

## Fase 6 — Verificación

### 1. Responsable
**QA Tester** que reportó el defecto (para garantizar independencia). El **QA Lead** supervisa y da el visto bueno final.

### 2. Acción exacta tomada

Una vez que el PR es aprobado y mergeado a `main`, el pipeline de CI ejecuta automáticamente los tests (incluido el nuevo `CartLimitTest`). Tras el build verde, QA descarga la nueva build y ejecuta el protocolo de verificación:

**Casos de verificación ejecutados sobre BUG-042:**

| # | Escenario | Resultado esperado | Resultado obtenido |
|---|---|---|---|
| V-1 | Agregar el 6° libro distinto (fix principal) | Libro agregado, contador = 6 | ✅ PASS |
| V-2 | Agregar el 10° libro distinto | Libro agregado, contador = 10 | ✅ PASS |
| V-3 | Intentar agregar el 11° libro | Alert "Límite del carrito (10)" visible | ✅ PASS |
| V-4 | Botón "Comprar ahora" en diálogo de detalle con 10 ítems | Alert visible, diálogo se cierra | ✅ PASS |
| V-5 | Agregar libro ya presente en el carrito | Alert "Ya en el carrito" visible | ✅ PASS |
| V-6 | Happy path completo: agregar 1 libro, ir a checkout | Sin regresión, flujo completo OK | ✅ PASS |
| V-7 | Carrito vacío → agregar primer libro | Sin regresión | ✅ PASS |

El QA Tester deja constancia en el ticket de los 7 casos ejecutados, los resultados y una captura de pantalla del alert correcto al intentar agregar el ítem 11.

### 3. Herramientas utilizadas
- **OpenLib Market** — build generada desde commit post-merge (`sha-c9f4a21`)
- **Jira** — actualización del ticket con checklist de verificación y evidencia adjunta
- **GitHub Actions** — confirmación de build verde con todos los tests pasando (incluyendo `CartLimitTest`)
- **Screenshot** del alert funcional adjunto al ticket

### 4. Estado del ticket en Jira al finalizar esta fase
```
Estado:   RESOLVED
ID:       BUG-042
Resolución: Fixed
Verificado por: [QA Tester]
Build de verificación: sha-c9f4a21
Tests de regresión: ✓ 7/7 PASS
Evidencia de verificación adjunta: ✓
Pendiente: Aprobación de cierre formal por QA Lead
```

---

## Fase 7 — Cierre

### 1. Responsable
**QA Lead** realiza el cierre formal. El **Product Owner** es notificado para transparencia.

### 2. Acción exacta tomada

El QA Lead revisa el checklist de cierre del defecto:

- ✅ El defecto original no se reproduce en la build `sha-c9f4a21`
- ✅ Los 7 casos de verificación pasan
- ✅ No se introdujeron regresiones en flujos adyacentes (checkout, carrito vacío, detalle de libro)
- ✅ El nuevo test `CartLimitTest` está en el repositorio y corre en CI
- ✅ La constante `MAX_CART_ITEMS = 10` reemplaza el magic number en `DataStore`
- ✅ El feedback visual al usuario (Alert) existe para los tres casos: éxito, límite, duplicado
- ✅ La regla de negocio (límite de 10) ahora está en el nivel correcto (`DataStore`), no dispersa en la vista

El QA Lead documenta en el ticket una **lección aprendida**: la regla de límite de carrito no existía en ningún test antes de este defecto. Se añade como acción de mejora para el equipo: cualquier regla de negocio con un valor numérico (límites, umbrales, constantes) debe tener un test unitario explícito que valide ese valor exacto. Esta acción se crea como tarea técnica **TECH-019** en el backlog.

El **Product Owner** es notificado por Slack que BUG-042 está cerrado y el flujo de carrito funciona correctamente hasta el límite de 10 ítems con feedback apropiado al usuario.

### 3. Herramientas utilizadas
- **Jira** — transición final del ticket a estado `CLOSED`
- **Confluence** — adición de la lección aprendida en la página "Retrospectiva Sprint 3 — Defectos"
- **Slack** (canal `#openlib-product`) — notificación al PO del cierre
- **Jira** — creación de tarea técnica **TECH-019**: "Agregar tests unitarios para todas las reglas de negocio con valores numéricos (límites, umbrales)"

### 4. Estado final del ticket en Jira
```
Estado:   CLOSED
ID:       BUG-042
Resolución: Fixed
Cerrado por: [QA Lead]
Fecha de cierre: [fecha del sprint]
Fix commit: sha-c9f4a21
Lección aprendida: documentada en Confluence (Retro Sprint 3)
Tarea de mejora generada: TECH-019
Tiempo total del ciclo: ~18 horas (detección → cierre)
```

---

## Resumen del Ciclo Completo

```
FASE              RESPONSABLE        DURACIÓN EST.   ESTADO JIRA RESULTANTE
──────────────────────────────────────────────────────────────────────────
1. Detección      QA Tester          30 min          OPEN
2. Reporte        QA Tester / Lead   45 min          OPEN (con evidencia)
3. Asignación     Scrum Master/TL    15 min (Daily)  IN PROGRESS
4. Diagnóstico    Developer          1 h             IN PROGRESS (root cause)
5. Corrección     Developer / TL     3 h (fix+PR)    IN REVIEW
6. Verificación   QA Tester          1 h             RESOLVED
7. Cierre         QA Lead            20 min          CLOSED
──────────────────────────────────────────────────────────────────────────
TOTAL                                ~6 h activas / ~18 h calendario
```

---

## Lecciones Aprendidas del Equipo OpenLib

**Causa estructural del defecto:** la regla de negocio "máximo N ítems en carrito" estaba implícita en el documento `historia_usuario_buyer.md` pero nunca fue traducida a un test automatizado. Cuando un developer introdujo la guarda de seguridad, usó el número `5` basándose en su criterio, sin referencia a la especificación, y sin feedback al usuario.

**Acciones de mejora generadas:**

1. **TECH-019** — Agregar tests unitarios explícitos para todas las constantes de negocio (límites de carrito, estados válidos de orden, roles de usuario).
2. **TECH-020** — Definir una política de "no magic numbers" en el código de negocio; todas las constantes deben declararse en un archivo `BusinessRules.java` o en las clases que las poseen.
3. **PROCESS-005** — Durante el refinamiento de historias de usuario, el QA Lead debe identificar y documentar explícitamente los valores límite como criterios de aceptación en el ticket de Jira (no solo en documentos de texto).

---

*Gestionado bajo el ciclo de vida de defectos clásico · OpenLib Market · Sprint 3 · Java 21 / JavaFX / Spring Boot*
```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

Proporcionar el reporte del bug estructurado de antemano (resumen, pasos a reproducir, resultado esperado vs. resultado obtenido) fue una buena decisión analítica. Impidió que el modelo inventara contexto y lo forzó a aplicar el ciclo de vida del defecto de forma procedimental. Darle el rol combinado de "QA Lead y Scrum Master" fue clave: obligó a la IA a incluir dinámicas de metodologías ágiles que aplicamos constantemente en los proyectos de la universidad, como la revisión del ticket en la Daily Standup, el impacto en el burndown chart del sprint y la etapa de Code Review.

#### 2. ¿Qué se puede mejorar?

Aunque la estructura narrativa es buena, al revisar el detalle operativo noté que el modelo cometió varias fallas técnicas y de gestión:

Primero, aplicó mal las transiciones de Jira. En la fase de asignación, la IA pasa el ticket directamente a estado IN PROGRESS. En el flujo real de la industria, asignar un ticket solo lo mueve a la columna TO DO o SELECTED FOR DEVELOPMENT. El defecto solo debe pasar a IN PROGRESS cuando el desarrollador realmente inicia el trabajo de diagnóstico.
Segundo, demostró una desconexión arquitectónica grave. El modelo diagnosticó y arregló el fallo asumiendo que toda la lógica de negocio vive estáticamente en el frontend (usando un DataStore.java local en JavaFX). Pero el proyecto tiene una arquitectura distribuida cliente-servidor con Spring Boot. El primer paso formal en el diagnóstico debió ser revisar los logs de red para confirmar el origen de la falla: o el backend (Spring) está rechazando la petición y el frontend simplemente no está capturando la excepción HTTP, o el frontend tiene el límite de 5 ítems "quemado" (hardcodeado) en la vista y ni siquiera manda la solicitud al servidor.

Tercero, propuso un diseño de pruebas unitarias sucio. En la fase de validación, la IA propuso llenar un DataStore (que actúa como Singleton global) con objetos falsos. En la bibliografía técnica sobre testing, alterar el estado global en memoria para correr un test es un anti-patrón enorme. Debió reiniciar el contexto de ejecución o usar mocks formales (como Mockito) para garantizar que la prueba sea independiente y aislada.

#### 3. Respuesta final
A nivel teórico, la IA estructuró bien el flujo del bug. Separar las responsabilidades (el tester documenta, el Scrum Master prioriza, el desarrollador repara y QA valida) demuestra que comprende el estándar base de aseguramiento de calidad. La decisión de documentar "Deuda Técnica" paralela al arreglo del defecto fue un detalle de gestión muy maduro.

Sin embargo, para presentar esta solución en una entrega formal de ingeniería, yo modificaría drásticamente la fase de diagnóstico. Le exigiría al desarrollador aislar la falla primero: auditar las trazas de red para determinar objetivamente si el problema recae en el controlador REST del backend o en la validación de vista de JavaFX. Adicionalmente, alinearía el tablero Kanban a las prácticas formales: el ticket se asigna en TO DO, se depura en IN PROGRESS, se bloquea en IN REVIEW durante el Pull Request, pasa a IN QA (o READY FOR TEST) para la verificación del equipo de pruebas, y únicamente entonces puede marcarse como DONE.