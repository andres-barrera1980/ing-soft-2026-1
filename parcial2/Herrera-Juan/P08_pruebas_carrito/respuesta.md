# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [8]: [pruebas carrito]

### Estudiante
- **Nombre completo**: [Juan Fernando Herrera Patiño]

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [ChatGPT] |
| **Modelo específico** | [GPT-4o ]|
| **¿Por qué elegiste este LLM?** | [mas comodo para mi] |

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [Claude / ChatGPT / Gemini / Copilot / DeepSeek / Qwen / Mistral / Otro / **Sin IA — respuesta propia**] |
| **Modelo específico** | [Ej: Claude Opus 4.5, GPT-4o, Gemini 2.5 Pro, etc. Si respondes sin IA, escribe "N/A"] |
| **¿Por qué elegiste este LLM?** | [Justifica en 1-3 oraciones. Si respondes sin IA, explica por qué decidiste no usar LLM para esta pregunta.] |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

Eres un experto en pruebas unitarias con JUnit 5, Mockito y desarrollo Java.

Analiza cuidadosamente la siguiente clase CarritoService y genera una suite completa de pruebas unitarias.

Requisitos obligatorios:

1. Utiliza JUnit 5 y Mockito.

2. Crea mocks apropiados para RepositorioLibro.

3. Prueba TODOS los métodos públicos:

   * agregarItem()
   * removerItem()
   * calcularTotal()
   * vaciar()
   * cantidadItems()
   * cantidadItemsUnicos()
   * validarParaCheckout()

4. Incluye casos normales y casos borde:

   * Agregar un ítem correctamente.
   * Agregar varias unidades de un mismo ítem.
   * Agregar un ítem que ya existe en el carrito.
   * Agregar exactamente 10 ítems diferentes.
   * Intentar agregar el ítem número 11.
   * Cantidad negativa o cero.
   * Libro inexistente.
   * Stock insuficiente.
   * Remover un ítem existente.
   * Remover un ítem inexistente.
   * Calcular total con varios productos.
   * Calcular total con carrito vacío.
   * Checkout con carrito vacío.
   * Checkout con carrito válido.
   * Vaciar carrito.
   * Verificar cantidadItems().
   * Verificar cantidadItemsUnicos().

5. Para cada prueba:

   * Explica brevemente qué valida.
   * Usa nombres descriptivos siguiendo el patrón given_when_then.

6. Indica el porcentaje aproximado de cobertura alcanzado y menciona si existe algún caso adicional que podría probarse.

Genera código Java completo y funcional.


---

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**
Para generar una suite de pruebas completa y funcional, necesito ver la implementación de la clase CarritoService (y preferiblemente las clases relacionadas como Libro, RepositorioLibro, ItemCarrito, excepciones, etc.).

En este momento solo tengo la lista de métodos que hay que probar:

agregarItem()
removerItem()
calcularTotal()
vaciar()
cantidadItems()
cantidadItemsUnicos()
validarParaCheckout()

pero no conozco:

La firma exacta de cada método.
Qué excepciones lanza.
Cómo se almacena el carrito.
Cómo interactúa con RepositorioLibro.
Cómo se valida el stock.
Cómo se calcula el total.
Qué estructura tienen Libro e ItemCarrito.

Por ejemplo, estas dos implementaciones requieren pruebas completamente diferentes:

public void agregarItem(Long libroId, int cantidad)

vs

public void agregarItem(Libro libro, int cantidad)

o

throw new LibroNoEncontradoException(...)

vs

throw new IllegalArgumentException(...)
Envíame el código de:
CarritoService

y si es posible también:

RepositorioLibro
Libro
ItemCarrito

y te generaré:

✅ 
Suite completa en JUnit 5 + Mockito
✅ 
Todos los casos solicitados
✅ 
Mocks correctamente configurados
✅ 
Explicación de cada prueba
✅ 
Cobertura estimada (%)
✅ 
Código listo para copiar y pegar en IntelliJ/Eclipse sin modificaciones.

### Análisis crítico de la respuesta
La respuesta no cumple completamente la tarea solicitada, ya que no genera ninguna prueba unitaria y se limita a pedir información adicional. Por tanto, no es posible afirmar que cubra todos los métodos, los casos borde o las reglas de negocio. No se probaron métodos como validarParaCheckout(), calcularTotal() o vaciar(), ni se verificaron escenarios importantes como agregar el ítem número 11, realizar checkout con el carrito vacío o agregar un ítem ya existente. Tampoco se mostró el uso de Mockito para simular el comportamiento de RepositorioLibro. Aunque es cierto que disponer de las clases auxiliares permitiría construir pruebas más precisas, la implementación de CarritoService proporcionada en el enunciado era suficiente para diseñar la mayoría de los casos de prueba. Por ello, la respuesta resulta incompleta y no satisface los requisitos de la actividad.


#### 1. ¿Qué hizo bien el prompt?

El prompt fue claro y específico, ya que indicó el uso obligatorio de JUnit 5 y Mockito, solicitó pruebas para todos los métodos públicos de CarritoService y enumeró explícitamente los casos normales, casos borde y excepciones que debían cubrirse. Además, exigió nombres descriptivos para las pruebas y el uso de mocks para RepositorioLibro, lo que proporcionó suficiente contexto para generar una suite de pruebas completa si el código hubiera estado disponible.



#### 2. ¿Qué se puede mejorar?

El prompt podría mejorarse incluyendo directamente las clases auxiliares o indicando que el código proporcionado es suficiente para inferir el comportamiento esperado. Aunque el enunciado sí contenía la implementación de CarritoService, el LLM consideró que faltaban dependencias como Libro, ItemCarrito y RepositorioLibro para construir pruebas completamente funcionales. También podría haberse solicitado expresamente que, en caso de faltar clases secundarias, asumiera implementaciones mínimas para continuar con la generación de las pruebas.



#### 3. Respuesta final

Para validar correctamente las reglas de negocio de CarritoService es necesario diseñar una suite de pruebas unitarias completa utilizando JUnit 5 y Mockito. Las pruebas deben cubrir tanto el comportamiento normal como los casos borde definidos por los requisitos del negocio: agregar ítems correctamente, actualizar la cantidad cuando un libro ya existe en el carrito, calcular el total, remover elementos y vaciar el carrito. Además, deben verificarse las restricciones de negocio más importantes, como permitir máximo 10 ítems diferentes, impedir agregar el ítem número 11, no permitir cantidades menores o iguales a cero, detectar libros inexistentes o con stock insuficiente y evitar el checkout cuando el carrito está vacío. Para aislar la lógica del servicio, RepositorioLibro debe simularse mediante mocks de Mockito, permitiendo controlar los distintos escenarios sin depender de una base de datos real. De esta forma se obtiene una alta cobertura de pruebas y se garantiza que las reglas de negocio del carrito funcionen correctamente ante situaciones normales y excepcionales.
