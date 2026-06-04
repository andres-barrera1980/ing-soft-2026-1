# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [2: [P02_solid_refactor]

### Estudiante
- **Nombre completo**: [Jhoan Galeano]

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [Gemini] |
| **Modelo específico** | [Gemini 1.5 Pro] |
| **¿Por qué elegiste este LLM?** | [Lo elegí porque maneja muy bien el análisis de código en Java y estructura las refactorizaciones de software utilizando patrones de diseño de manera clara] |

---

### Prompt utilizado

Actúa como un ingeniero de software senior y experto en arquitectura de software. Analiza el siguiente método `procesar` de la clase `ProcesadorPago` perteneciente al módulo de compras de OpenLib Market. 

Identifica de forma detallada al menos dos principios SOLID que el equipo junior está violando en este diseño y justifica técnicamente tu respuesta. 

Posteriormente, propón una refactorización completa en Java aplicando el patrón de diseño que consideres más adecuado para resolver el problema de extensibilidad (OpenLib Market necesita soportar múltiples métodos de pago y permitir agregar nuevos en el futuro sin modificar el código existente). Entrega la solución utilizando interfaces y buenas prácticas de Clean Code.

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

#### 1. ¿Qué hizo bien el prompt?

[El prompt fue directo al grano al pedirle explícitamente identificar "al menos dos principios SOLID", lo que obligó al modelo a no quedarse solo con el error más obvio. También, al exigirle una "refactorización completa usando un patrón de diseño", el formato de salida del código fue limpio y estructurado desde el primer intento]


#### 2. ¿Qué se puede mejorar?

[digamos que se puede mejorar que le falto exigir restricciones de arquitectura para hacer un manejo correcto de la creacion de los objetos, ya que omitio diferentes cosas del dodigo. tambien le daria mas contexto de openlibmarket para que el sepa mas y pueda darme una respuesta mas acertada.]


#### 3. Respuesta final

¿Identificó correctamente los principios violados?
Sí. SRP porque una sola clase concentraba lógicas de negocio completamente distintas, y OCP porque agregar cualquier método nuevo exigía modificar código existente y probado.
¿El patrón elegido es adecuado?
Sí. Strategy es el patrón correcto cuando el comportamiento varía según el contexto pero el contrato es siempre el mismo (procesar(Pago)). Cada algoritmo queda encapsulado e intercambiable.
¿Qué interfaces o clases introdujo?
Una interfaz EstrategiaPago como contrato central, y una clase concreta por cada método de pago que la implementa. ProcesadorPago pasa a ser el contexto que delega sin conocer los detalles.
¿Permite agregar un nuevo método sin modificar código existente?
Casi completamente. Agregar Nequi requiere solo crear PagoNequi implements EstrategiaPago y añadir un case en el switch del constructor — ninguna clase existente se modifica, ningún test existente se rompe.
¿Qué mejoraría del diseño?
El switch dentro del constructor de ProcesadorPago es el único punto que aún viola OCP estrictamente. La mejora sería extraerlo a un Map<String, Supplier<EstrategiaPago>> para que agregar un método nuevo sea registrar una entrada, sin tocar ninguna lógica:

// Registro de estrategias sin switch
private static final Map<String, Supplier<EstrategiaPago>> REGISTRO = Map.of(
    "TARJETA", PagoTarjeta::new,
    "PSE",     PagoPSE::new,
    "PAYPAL",  PagoPayPal::new,
    "CRIPTO",  PagoCripto::new
);

ProcesadorPago(String tipo) {
    this.estrategia = Optional.ofNullable(REGISTRO.get(tipo.toUpperCase()))
        .orElseThrow(() -> new IllegalArgumentException("Método no soportado: " + tipo))
        .get();
}

