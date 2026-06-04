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

