# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [08]: [P08 Pruebas Carrito de Compras ]

### Estudiante
- **Nombre completo**: [Daniel Prieto]

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [**Sin IA — respuesta propia**] |
| **Modelo específico** | [N/A] |
| **¿Por qué elegiste este LLM?** | [porque decidi retarme a resolver el sin ia, ya que confio en mis conocimientos adquiridos en clase y considero que tengo dominio del tema] |

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

#### 1. ¿Qué hizo bien el prompt?

[Evalúa tu propio prompt, no la respuesta del LLM. ¿El contexto fue suficiente? ¿Las restricciones fueron claras? ¿El formato de salida que pediste ayudó a obtener una buena respuesta? ¿Qué parte de tu prompt fue más efectiva? Sé específico: menciona fragmentos concretos de tu prompt que funcionaron bien.]


#### 2. ¿Qué se puede mejorar?

[¿Qué le faltó a tu prompt? ¿Qué harías diferente si pudieras reformularlo? ¿El LLM entendió mal algo por falta de claridad en tu prompt? ¿La respuesta tiene errores u omisiones? ¿Qué no cubrió el LLM que tú sí sabes por lo visto en clase?]


#### 3. Respuesta final

[Casos que deben cubrirse:

// Caso normal
@Test void agregarItemNuevo_exitoso()
@Test void agregarItemExistente_incrementaCantidad()
@Test void removerItem_exitoso()
@Test void calcularTotal_correcto()

// Bordes de reglas de negocio
@Test void agregarItem_cantidad0_lanzaException()
@Test void agregarItem_stockInsuficiente_lanzaException()
@Test void agregarItem_libroNoExiste_lanzaException()
@Test void agregarItem_carritoLlenoConItemNuevo_lanzaException() // 10 items → intento 11
@Test void agregarItem_itemYaExiste_noContaComoNuevoSlot() // no debe lanzar si ya está
@Test void removerItem_noExiste_lanzaException()
@Test void calcularTotal_carritoVacio_lanzaException()
@Test void validarParaCheckout_carritoVacio_lanzaException()
@Test void validarParaCheckout_carritoConItems_noLanzaException()
@Test void vaciar_limpiaTodo()
@Test void cantidadItems_sumaTodasLasCantidades()
@Test void cantidadItemsUnicos_cuentaSlotsDiferentes()


Mock esencial:

@Mock RepositorioLibro repositorioLibro;
// when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libroConStock));
// when(repositorioLibro.buscarPorId(99L)).thenReturn(Optional.empty());


El caso más propenso a omisión: agregar el ítem 11 siendo nuevo vs actualizar cantidad de ítem 10 existente — ambos deben funcionar diferente.]
