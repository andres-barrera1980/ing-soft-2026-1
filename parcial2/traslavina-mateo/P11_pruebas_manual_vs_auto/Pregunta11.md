# Pregunta P11: Pruebas manuales vs automatizadas

### Estudiante
- **Nombre completo**: Mateo Traslaviña Moreno

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Claude |
| **Modelo específico** | Claude Sonnet 4.6 |
| **¿Por qué elegiste este LLM?** | Las preguntas sobre estrategia de pruebas requieren comprender trade-offs entre costo, velocidad y cobertura. Claude maneja bien el razonamiento sobre decisiones de ingeniería con múltiples variables, y conecta los conceptos teóricos con situaciones concretas del proyecto. |

---

### Prompt utilizado

```
Eres un QA Lead explicando estrategia de pruebas al equipo de OpenLib Market, plataforma de compra-venta de libros en Java 21 con Spring Boot 3.x.

El equipo necesita decidir qué pruebas automatizar y qué mantener manual. Actualmente tienen:
- Módulo de búsqueda de libros (ElasticSearch)
- Módulo de carrito de compras
- Módulo de pagos (integración con pasarela externa)
- Módulo de gestión de inventario
- Módulo de reseñas de libros
- Dashboard de administración (UI compleja)
- API REST expuesta a terceros

Necesito que compares pruebas manuales vs automatizadas cubriendo:
1. Qué son y cómo funcionan cada una
2. Cuándo usar cada tipo (criterios de decisión)
3. Ventajas y desventajas de cada enfoque
4. La pirámide de pruebas y cómo aplica a OpenLib Market
5. Para cada módulo listado, recomienda qué automatizar y qué mantener manual
6. Cómo calcular el ROI de automatizar una prueba (fórmula simple)

Incluye ejemplos concretos del contexto de OpenLib Market. Formato markdown con tabla comparativa y sección de recomendaciones por módulo.
```

---

### Respuesta del LLM

## Qué son y cómo funcionan

### Pruebas manuales

Un tester humano ejecuta casos de prueba siguiendo un guión o explorando la aplicación. El tester verifica comportamiento, reporta defectos, y usa su criterio y experiencia para detectar problemas inesperados.

**Tipos principales:**
- **Exploratorias**: el tester navega libremente buscando problemas no anticipados
- **De usabilidad**: ¿es la interfaz intuitiva? ¿fluye bien la experiencia?
- **Ad-hoc**: pruebas sin planificación, basadas en intuición
- **Regresión manual**: re-ejecutar casos de prueba después de un cambio

### Pruebas automatizadas

Scripts o programas que ejecutan casos de prueba y verifican resultados sin intervención humana. Pueden correr en CI/CD después de cada commit.

**Tipos por nivel (pirámide):**
- **Unitarias** (base): prueban una clase/método en aislamiento (JUnit 5 + Mockito)
- **Integración**: prueban la interacción entre componentes (Spring Boot Test + TestContainers)
- **End-to-End / UI**: prueban flujos completos de usuario (Selenium, Playwright)

## La pirámide de pruebas para OpenLib Market

```
           /\
          /  \
         / E2E\   ← Pocas, lentas, caras — solo flujos críticos
        /  (5%) \
       /──────────\
      / Integración \  ← Moderadas — APIs, DB, servicios externos
     /    (25%)      \
    /──────────────────\
   /      Unitarias     \  ← Muchas, rápidas, baratas
  /        (70%)         \
 /──────────────────────── \
```

Para OpenLib Market: ~70% unitarias, ~25% integración, ~5% E2E. Invertir la pirámide (muchos E2E) es el antipatrón más costoso — las pruebas E2E son 10-100x más lentas y frágiles que las unitarias.

## Tabla comparativa

| Criterio | Pruebas Manuales | Pruebas Automatizadas |
|---|---|---|
| Costo inicial | Bajo | Alto (requiere desarrollo) |
| Costo por ejecución | Alto (tiempo de QA) | Casi cero (CPU) |
| Velocidad | Lenta (horas) | Rápida (segundos-minutos) |
| Frecuencia de ejecución | Baja (esporádica) | Alta (cada commit) |
| Detección temprana de bugs | Baja | Alta |
| Pruebas exploratorias | Excelente | No aplicable |
| Detección de UX problems | Excelente | Limitada |
| Pruebas de regresión | Costosas | Ideal |
| Mantenimiento | Bajo | Requiere actualizar scripts |
| Casos de prueba repetitivos | Tediosos, propensos a errores | Perfectos |

## Cuándo usar cada tipo

**Automatizar cuando:**
- El caso de prueba se ejecuta frecuentemente (regresión en cada release)
- El comportamiento es determinístico y verificable programáticamente
- El caso de prueba tiene muchas variaciones de datos (data-driven testing)
- El flujo es crítico para el negocio (pago, autenticación)

**Mantener manual cuando:**
- La prueba requiere juicio humano (¿se ve bien? ¿es intuitivo?)
- El flujo cambia frecuentemente (mantener el script costaría más que ejecutar manual)
- Es una prueba exploratoria buscando bugs no anticipados
- Costo de automatizar > beneficio de automatizar

## ROI de automatización

```
ROI = (Tiempo_manual × Costo_hora × Frecuencia_año - Costo_desarrollo_script) / Costo_desarrollo_script

Donde:
- Tiempo_manual: horas que toma ejecutar el caso manualmente
- Costo_hora: costo/hora del tester (ej: $20/h)
- Frecuencia_año: veces que se ejecuta al año (ej: 52 si es semanal)
- Costo_desarrollo_script: horas de desarrollo × costo/hora desarrollador
```

**Ejemplo concreto — flujo de pago de OpenLib Market:**
- Tiempo manual: 30 min por ejecución
- Ejecutado: 3 veces/semana → 156 veces/año
- Costo tester: $20/h → $10 por ejecución → $1,560/año
- Costo desarrollar script de automatización: 8h × $30/h = $240
- ROI primer año: ($1,560 - $240) / $240 = **550%**

Si la prueba se ejecuta solo una vez al mes (12 veces/año): $120/año de costo manual vs $240 de automatización → ROI negativo. No automatizar.

## Recomendaciones por módulo de OpenLib Market

| Módulo | Automatizar | Manual | Justificación |
|---|---|---|---|
| **Búsqueda de libros** | Unit tests del ranking/scoring. Integration tests con ElasticSearch real (TestContainers). | Relevancia de resultados, UX de la búsqueda | Los algoritmos son automatizables; "¿los resultados son buenos?" requiere juicio humano |
| **Carrito de compras** | Toda la lógica de negocio: agregar, quitar, calcular totales, aplicar descuentos | UX del carrito (interfaz, flujo visual) | Lógica 100% determinística, excelente candidato para unit + integration tests |
| **Módulo de pagos** | Happy path y casos de error con mocks de la pasarela. Integration tests con sandbox de la pasarela | Flujo completo con tarjeta real en staging, casos edge de pasarela | Crítico: automatizar regresión, pero verificar manualmente en staging antes de cada release |
| **Control de inventario** | Todo: TDD completo (ver P09). Unitarias + integración con DB | — | Lógica pura, sin UI, 100% automatizable |
| **Reseñas de libros** | CRUD de reseñas, validaciones, paginación | Moderación de contenido, calidad de las reseñas mostradas | La moderación requiere juicio humano; el CRUD no |
| **Dashboard de administración** | APIs backend del dashboard | UI completa del dashboard: navegación, filtros, reportes visuales | UI compleja con muchos cambios visuales → scripts E2E se rompen frecuentemente → alto costo de mantenimiento |
| **API REST a terceros** | Contract tests (Pact), todos los endpoints, validación de esquemas | — | La API es un contrato: cualquier rotura es crítica, automatización obligatoria |

---

### Analisis crítico de la respuesta

#### 1. ¿Que hizo bien el prompt?

El prompt fue efectivo al listar los **7 módulos específicos de OpenLib Market** y pedir una recomendación por módulo. Esto evitó respuestas genéricas y forzó al LLM a razonar sobre cada contexto individual — por ejemplo, la diferencia entre el módulo de inventario (lógica pura, 100% automatizable) y el dashboard de administración (UI compleja con alto costo de mantenimiento de scripts). Pedir la **fórmula de ROI** con un ejemplo concreto fue especialmente valioso: la mayoría de recursos sobre testing hablan de "automatizar cuando tiene sentido" sin dar criterios cuantificables. También fue útil pedir la pirámide de pruebas con porcentajes específicos para OpenLib Market.

#### 2. ¿Qué se puede mejorar?

El LLM no mencionó **contract testing** (Pact) como parte de la estrategia para el módulo de pagos y la API REST — aunque lo nombró brevemente en la tabla, no explicó cómo funciona. Para una API expuesta a terceros, Pact (Consumer-Driven Contract Testing) es la herramienta más apropiada y habría valido la pena desarrollarla. También faltó mencionar **mutation testing** (PIT para Java) como forma de verificar la calidad de los tests unitarios — no todos los tests con 100% de cobertura son buenos tests. Otro punto ausente: la diferencia entre **test de humo** (smoke test) y **regresión** — para el módulo de pagos en producción, un smoke test post-despliegue es crítico.

#### 3. Respuesta final

La decisión entre pruebas manuales y automatizadas no es binaria — es una **estrategia de portfolio** donde cada tipo cubre lo que el otro no puede.

Las **pruebas automatizadas** ganan cuando la frecuencia de ejecución es alta: cada commit a `main` debería correr las unitarias e integración. En OpenLib Market, la lógica de carrito, inventario, y validaciones de API son perfectas para automatización — determinísticas, repetibles, y verificables programáticamente. La regla práctica: si en el último mes ejecutaste ese caso de prueba más de 5 veces, automatizarlo ya tiene ROI positivo.

Las **pruebas manuales** ganan donde el juicio humano es insustituible: ¿el flujo de checkout es confuso para un usuario nuevo? ¿las reseñas negativas moderadas afectan la reputación de vendedores legítimos? ¿el dashboard de administración es usable para un no-técnico? Estas preguntas no tienen respuesta en un `assertEquals`.

La **pirámide de pruebas** es la herramienta de balance: muchas unitarias (rápidas, baratas, mantenibles), pocas E2E (lentas, frágiles, caras). El antipatrón más común es el "cono de helado invertido": muchos E2E y pocas unitarias, resultando en pipelines de CI que tardan 45 minutos y se rompen por un cambio de clase CSS.

Para OpenLib Market, el módulo de pagos merece atención especial: automatizar el happy path y los casos de error con mocks, pero **siempre ejecutar manualmente en el sandbox real de la pasarela antes de cada release**. Los mocks no simulan perfectamente los timeouts, reintentos, y comportamientos edge de una pasarela real.
