# Parcial 2 — Ingeniería de Software con LLMs
## Fundamentos de Ingeniería de Software 2026-1
### Pontificia Universidad Javeriana

---

## Objetivo del parcial

Evaluar la capacidad del estudiante para **usar un LLM como herramienta de ingeniería** —no como oráculo— en tareas reales de diseño, arquitectura, principios SOLID, patrones de diseño, pruebas, virtualización y contenerización. El parcial mide dos competencias:

1. **Calidad del prompt (30%)**: qué tan bien formula el estudiante la pregunta al LLM. Un buen prompt es específico, incluye contexto del proyecto, define restricciones claras, y solicita un formato de salida concreto.
2. **Calidad del análisis (70%)**: qué tan críticamente evalúa la respuesta del LLM. El estudiante debe identificar aciertos, errores, omisiones, y producir una síntesis propia que demuestre dominio de los conceptos de la materia.

**Opción sin IA**: El estudiante puede responder cualquier pregunta sin usar un LLM. En ese caso, omite las secciones de prompt y respuesta del LLM, y escribe directamente su análisis y respuesta. La pregunta se evalúa al 100% sobre la calidad del análisis, con un **bono del +20%** sobre la nota obtenida. Las preguntas marcadas con ⭐ son especialmente recomendadas para responder sin IA.

---

## Reglas del parcial

| Regla | Detalle |
|---|---|
| **Modalidad** | Take-home individual |
| **Puntaje total** | 50 puntos |
| **Fecha de entrega** | [FECHA] a las 23:59 |
| **LLM permitidos** | Claude (Anthropic), ChatGPT (OpenAI), Gemini (Google), Copilot (GitHub), DeepSeek, Qwen, Mistral, o cualquier otro. El estudiante debe indicar cuál usó y por qué lo eligió. También puede optar por **no usar IA** en cualquier pregunta (bono +20% en esa pregunta). |
| **Consulta** | Se permite consultar apuntes, referencias del curso, documentación oficial y foros. No se permite consultar ni compartir respuestas con compañeros. |
| **Entrega** | Rama en el repositorio del curso (`parcial2/apellido-nombre`) siguiendo la plantilla de entrega. |

---

## Formato de entrega (Git)

Cada estudiante debe:

1. Crear una rama desde `develop` con el nombre `parcial2/apellido-nombre` (ej: `parcial2/torres-diego`).
2. Completar un archivo por pregunta dentro de `parcial2/` usando la plantilla `plantilla_pregunta.md`. Si respondes sin IA, omite las secciones de prompt y respuesta del LLM.
3. Realizar **al menos 2 commits** por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis. Si respondes sin IA, aplica el mismo principio: commits que muestren la evolución de tu análisis.
4. Hacer push de la rama al repositorio remoto antes de la fecha límite.
5. El último commit antes de la fecha límite será el evaluado.

```
parcial2/
├── README.md                         # Datos del estudiante, LLM elegido y justificación
├── P01_solid_principio/
│   └── respuesta.md
├── P02_solid_refactor/
│   └── respuesta.md
├── ...
└── P13_integracion/
    └── respuesta.md
```

---

## Tabla de preguntas

| # | Tema | Puntos | Enunciado resumido |
|---|---|---|---|
| 1 ⭐ | SOLID | 3 | Identificar el principio violado en una clase y proponer refactoring |
| 2 | SOLID | 4 | Refactorizar un módulo aplicando los principios correctos |
| 3 ⭐ | SOLID | 3 | Evaluar una jerarquía de clases que viola principios de diseño |
| 4 | Patrones de diseño | 3 | Identificar y aplicar el patrón correcto para estrategias de descuento |
| 5 | Patrones de diseño | 5 | Implementar notificaciones de disponibilidad usando dos patrones combinados |
| 6 ⭐ | Clean Architecture | 4 | Analizar la estructura de capas y la regla de dependencia |
| 7 | Clean Architecture | 4 | Diseñar un módulo aplicando Clean Architecture a OpenLib Market |
| 8 | Pruebas unitarias | 5 | Generar y complementar pruebas unitarias con JUnit 5 y Mockito |
| 9 ⭐ | TDD | 4 | Aplicar TDD a una funcionalidad y evaluar el proceso |
| 10 | VM vs Containers | 5 | Comparar máquinas virtuales y contenedores: diferencias, ventajas, casos de uso |
| 11 ⭐ | Pruebas manuales vs automatizadas | 4 | Analizar ventajas, desventajas y criterios de decisión |
| 12 ⭐ | Ciclo de vida de defectos | 3 | Explicar y justificar decisiones en el ciclo de vida de un defecto |
| 13 | Integración — Refactoring | 3 | Refactorizar aplicando SOLID + patrón de diseño + pruebas |

> ⭐ = Especialmente recomendada para responder **sin usar IA** (bono +20% en esa pregunta).

---

## Preguntas

### P.1 ⭐ (3 puntos)

El siguiente método pertenece a la clase `GestorLibro` del módulo de administración de OpenLib Market:

```java
public class GestorLibro {
    public void publicarLibro(Libro libro, Usuario vendedor) {
        // 1. Validar que el libro tenga ISBN, título y autor
        if (libro.getIsbn() == null || libro.getTitulo() == null || libro.getAutor() == null) {
            throw new IllegalArgumentException("Datos del libro incompletos");
        }
        
        // 2. Guardar el libro en la base de datos
        Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/openlib", "admin", "pass");
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO libros (...) VALUES (...)");
        stmt.executeUpdate();
        
        // 3. Generar slug para la URL
        String slug = libro.getTitulo().toLowerCase().replace(" ", "-").replaceAll("[^a-z0-9-]", "");
        libro.setSlug(slug);
        
        // 4. Enviar correo al vendedor
        EmailService email = new EmailService("smtp.openlib.com", 587, "noreply@openlib.com", "pass123");
        email.enviar(vendedor.getEmail(), "Libro publicado", "Tu libro '" + libro.getTitulo() + "' ha sido publicado exitosamente.");
        
        // 5. Registrar en log
        Logger.getLogger("OpenLib").info("Libro publicado: " + libro.getIsbn() + " por " + vendedor.getNombre());
        
        // 6. Indexar para búsqueda
        SearchIndex index = new SearchIndex("elasticsearch.openlib.com:9200");
        index.indexar(libro);
    }
}
```

**Tarea**: Identifica qué principio SOLID se está violando (más de uno puede aplicar, pero enfócate en el principal). Luego:

1. **Prompt**: Formula un prompt para tu LLM pidiéndole que analice esta clase, identifique el principio violado, y proponga un refactoring. Pega el prompt completo y la respuesta del LLM.
2. **Análisis**: Evalúa la respuesta del LLM. ¿Identificó correctamente el principio? ¿Su refactoring es correcto y completo? ¿Hay algo que el LLM omitió o sugirió mal? ¿Qué mejorarías de la respuesta?

---

### P.2 (4 puntos)

OpenLib Market necesita soportar múltiples métodos de pago (tarjeta de crédito, PSE, PayPal, criptomonedas). El equipo junior escribió este código:

```java
public class ProcesadorPago {
    private String tipoPago; // "TARJETA", "PSE", "PAYPAL"
    
    public ProcesadorPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }
    
    public ResultadoPago procesar(Pago pago) {
        if (tipoPago.equals("TARJETA")) {
            // Validar CVV, fecha expiración, fondos
            // Conectar con API de franquicia (Visa/Mastercard)
            // Procesar cargo
            return new ResultadoPago(true, "Pago con tarjeta procesado");
        } else if (tipoPago.equals("PSE")) {
            // Redirigir a portal bancario
            // Validar retorno de PSE
            // Confirmar débito
            return new ResultadoPago(true, "Pago PSE procesado");
        } else if (tipoPago.equals("PAYPAL")) {
            // Autenticar con OAuth PayPal
            // Ejecutar cargo
            // Registrar webhook de confirmación
            return new ResultadoPago(true, "Pago PayPal procesado");
        }
        throw new IllegalArgumentException("Método de pago no soportado");
    }
}
```

**Tarea**: Identifica qué principios SOLID se están violando y refactoriza el diseño para corregirlo.

1. **Prompt**: Pídele a tu LLM que analice este código, identifique los principios SOLID violados (al menos dos), y proponga un refactoring completo usando el patrón de diseño que considere apropiado. Pega el prompt y la respuesta.
2. **Análisis**: ¿El LLM identificó correctamente los principios violados? ¿El patrón que eligió es el adecuado? ¿Qué interfaces o clases abstractas introdujo? ¿Qué pasaría si mañana hay que agregar un nuevo método de pago — el diseño propuesto lo permite sin modificar código existente? ¿Qué mejorarías del diseño?

---

### P.3 ⭐ (3 puntos)

OpenLib Market tiene tres tipos de usuarios. El equipo modeló esta jerarquía:

```java
public interface Usuario {
    void comprar(Libro libro);
    void vender(Libro libro);
    void moderarComentario(Comentario comentario);
    void generarReporteVentas();
    void gestionarUsuarios();
}

public class Comprador implements Usuario {
    public void comprar(Libro libro) { /* implementación */ }
    public void vender(Libro libro) { throw new UnsupportedOperationException("Un comprador no puede vender"); }
    public void moderarComentario(Comentario c) { throw new UnsupportedOperationException("No autorizado"); }
    public void generarReporteVentas() { throw new UnsupportedOperationException("No aplica"); }
    public void gestionarUsuarios() { throw new UnsupportedOperationException("No autorizado"); }
}

public class Vendedor extends Comprador {
    @Override
    public void comprar(Libro libro) { throw new UnsupportedOperationException("Un vendedor no compra"); }
    @Override
    public void vender(Libro libro) { /* implementación */ }
}

public class Administrador extends Vendedor {
    @Override
    public void comprar(Libro libro) { /* un admin sí puede comprar */ }
    // hereda vender() de Vendedor, implementa el resto
}
```

**Tarea**: Analiza esta jerarquía de clases.

1. **Prompt**: Pídele a tu LLM que analice esta jerarquía señalando qué principios SOLID se violan y proponga un rediseño completo. Pega el prompt y la respuesta.
2. **Análisis**: ¿El LLM identificó los principios violados? ¿Detectó los problemas en la jerarquía de clases? ¿Notó la interfaz con demasiados métodos? ¿El rediseño propuesto es correcto y aplicable a OpenLib Market? ¿Hay algo que el LLM pasó por alto sobre las consecuencias de estas violaciones en tiempo de ejecución?

---

### P.4 (3 puntos)

OpenLib Market necesita aplicar diferentes estrategias de descuento durante el año:

- **Descuento por fidelidad**: 10% para compradores con más de 5 compras.
- **Descuento por temporada**: 15% en la semana del libro.
- **Descuento por volumen**: 5% si el carrito supera $200,000 COP.
- **Sin descuento**: precio normal.

Estas estrategias pueden combinarse (ej: un cliente fiel puede recibir descuento de temporada + fidelidad). El precio final debe calcularse dinámicamente según la fecha, el perfil del usuario, y el contenido del carrito.

**Tarea**:

1. **Prompt**: Pídele a tu LLM que identifique qué patrón de diseño de los vistos en clase es el más adecuado para este escenario, y que proponga una implementación en Java con clases, interfaces y un ejemplo de uso. Exígele que justifique por qué ese patrón y descarte al menos otras dos alternativas. Pega el prompt y la respuesta.
2. **Análisis**: ¿El LLM identificó el patrón adecuado? ¿Consideró la posibilidad de composición de estrategias? ¿Qué alternativas mencionó y fueron correctamente descartadas? ¿La implementación es correcta y funcional? ¿Qué le faltó?

---

### P.5 (5 puntos)

**Contexto**: En OpenLib Market, cuando un libro agotado vuelve a estar disponible, varios componentes del sistema necesitan reaccionar:

- Enviar notificación por correo a los usuarios que lo agregaron a su wishlist.
- Enviar notificación push a los usuarios que lo marcaron como favorito en la app móvil.
- Actualizar la caché de Redis para que aparezca en búsquedas.
- Registrar el evento en un log de auditoría.

**Tarea**: Diseña e implementa este sistema usando **dos patrones de diseño de los vistos en clase** que trabajen en conjunto.

1. **Prompt**: Pídele a tu LLM que diseñe e implemente este sistema de notificaciones usando dos patrones de diseño combinados de los vistos en clase. Debe incluir: diagrama de clases (en Mermaid o texto), código Java de las clases principales, y explicación de por qué eligió esos dos patrones. Pega el prompt y la respuesta.
2. **Análisis**: ¿El LLM eligió patrones adecuados para este escenario? ¿Los aplicó correctamente? ¿La combinación de patrones tiene sentido o están forzados? ¿El diseño permite registrar y desregistrar dinámicamente los componentes interesados? ¿Cómo manejaría el caso de que una notificación falle? ¿El acoplamiento entre los componentes es el adecuado?

---

### P.6 ⭐ (4 puntos)

Clean Architecture (Robert C. Martin) propone una organización en capas concéntricas con una regla fundamental: **las dependencias solo pueden apuntar hacia adentro**. Las capas son: Entidades (Entities), Casos de Uso (Use Cases), Adaptadores de Interfaz (Interface Adapters), y Frameworks y Drivers.

**Tarea**:

1. **Prompt**: Pídele a tu LLM que explique Clean Architecture: sus capas, la regla de dependencia, el principio de inversión de dependencia aplicado en los límites entre capas, y cómo se compara con una arquitectura en capas tradicional (presentación → lógica → datos). Pide ejemplos concretos de qué va en cada capa para el contexto de OpenLib Market. Pega el prompt y la respuesta.
2. **Análisis**: ¿El LLM explicó correctamente la regla de dependencia? ¿Explicó cómo funciona la inversión de dependencia en los límites entre capas? ¿La comparación con arquitectura en capas tradicional fue precisa? ¿Los ejemplos de OpenLib Market son correctos para cada capa? ¿El LLM cometió algún error conceptual?

---

### P.7 (4 puntos)

El equipo de OpenLib Market está diseñando el módulo de **gestión de pagos**. Debe soportar:

- Procesar un pago con múltiples métodos (tarjeta, PSE, PayPal).
- Registrar la transacción en base de datos PostgreSQL.
- Notificar al comprador y al vendedor por correo.
- Exponer una API REST para que el frontend JavaFX la consuma.

Actualmente el equipo acopló todo en una sola clase `PaymentService` con un único método `realizarPago` que cubre todas las funcionalidades: llamar a la pasarela de pago, enviar el correo de notificación, cambiar el estado de la orden y preparar la entrega o descarga.

**Tarea**: Rediseña este módulo aplicando Clean Architecture.

1. **Prompt**: Pídele a tu LLM que rediseñe el módulo de pagos de OpenLib Market aplicando Clean Architecture. Debe: definir qué va en cada capa (Entities, Use Cases, Interface Adapters, Frameworks & Drivers), dibujar un diagrama de la estructura (en Mermaid o ASCII), mostrar cómo se respeta la regla de dependencia, y explicar cómo se inyectan las dependencias externas sin violar la arquitectura. Pega el prompt y la respuesta.
2. **Análisis**: ¿El LLM ubicó correctamente cada componente en su capa correspondiente? ¿El controlador REST está bien aislado de la lógica de negocio? ¿La regla de dependencia se respeta en todo el diseño? ¿Hay dependencias que cruzan capas en la dirección equivocada? ¿Qué tan testeable es el diseño resultante comparado con la versión acoplada original?

---

### P.8 (5 puntos)

El siguiente servicio pertenece al módulo de carrito de compras de OpenLib Market. **Reglas de negocio**: un carrito debe tener **mínimo 1 ítem** para proceder al checkout y **máximo 10 ítems** por carrito (sin contar cantidades — un ítem con cantidad 5 cuenta como 1 ítem). Si se intenta agregar un ítem que excede el máximo, se debe lanzar una excepción. Si se intenta hacer checkout con el carrito vacío, se debe lanzar una excepción.

```java
public class CarritoService {
    private final RepositorioLibro repositorioLibro;
    private final Map<Long, ItemCarrito> items = new HashMap<>();
    private static final int MAX_ITEMS = 10;
    
    public CarritoService(RepositorioLibro repositorioLibro) {
        this.repositorioLibro = repositorioLibro;
    }
    
    public void agregarItem(Long libroId, int cantidad) {
        if (cantidad <= 0) throw new IllegalArgumentException("Cantidad debe ser positiva");
        Libro libro = repositorioLibro.buscarPorId(libroId)
            .orElseThrow(() -> new NoSuchElementException("Libro no encontrado: " + libroId));
        if (libro.getStock() < cantidad) throw new IllegalStateException("Stock insuficiente");
        
        // Validar máximo de ítems (solo si es un ítem nuevo, no si ya existe)
        if (!items.containsKey(libroId) && items.size() >= MAX_ITEMS) {
            throw new IllegalStateException("El carrito no puede tener más de " + MAX_ITEMS + " ítems diferentes");
        }
        
        items.merge(libroId, 
            new ItemCarrito(libro, cantidad, libro.getPrecio()),
            (existente, nuevo) -> {
                existente.setCantidad(existente.getCantidad() + cantidad);
                return existente;
            });
    }
    
    public void removerItem(Long libroId) {
        if (!items.containsKey(libroId)) throw new NoSuchElementException("Ítem no está en el carrito");
        items.remove(libroId);
    }
    
    public BigDecimal calcularTotal() {
        if (items.isEmpty()) throw new IllegalStateException("El carrito está vacío");
        return items.values().stream()
            .map(item -> item.getPrecioUnitario().multiply(BigDecimal.valueOf(item.getCantidad())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public void vaciar() { items.clear(); }
    public int cantidadItems() { return items.values().stream().mapToInt(ItemCarrito::getCantidad).sum(); }
    public int cantidadItemsUnicos() { return items.size(); }
    
    public void validarParaCheckout() {
        if (items.isEmpty()) throw new IllegalStateException("El carrito debe tener al menos 1 ítem para checkout");
    }
}
```

**Tarea**:

1. **Prompt**: Pídele a tu LLM que genere pruebas unitarias completas con JUnit 5 y Mockito para `CarritoService`, cubriendo casos normales, casos borde (carrito vacío, carrito lleno con 10 ítems, intentar agregar el ítem 11, checkout con carrito vacío), y excepciones. Pega el prompt y la respuesta.
2. **Análisis**: ¿El LLM generó pruebas para todos los métodos? ¿Cubrió todos los casos borde de las reglas de negocio? ¿Probó `validarParaCheckout()`? ¿Probó el caso de agregar un ítem que ya existe? ¿Usó correctamente mocks para `RepositorioLibro`? ¿Faltó algún caso? Escribe tú los casos de prueba que el LLM no cubrió.

---

### P.9 ⭐ (4 puntos)

OpenLib Market necesita una funcionalidad de **control de inventario**: cuando el stock de un libro llega a cero, el sistema debe marcarlo automáticamente como "AGOTADO", notificar al vendedor, y deshabilitar el botón de "Agregar al carrito" en el frontend. Cuando el vendedor repone stock, el libro vuelve a estado "DISPONIBLE".

**Tarea**: Aplica TDD a esta funcionalidad.

1. **Prompt**: Pídele a tu LLM que demuestre el ciclo completo de TDD (Red-Green-Refactor) para la clase `ControlInventarioService` que maneje esta lógica. Debe: (a) escribir primero las pruebas unitarias (JUnit 5 + Mockito) que fallen, (b) luego la implementación mínima para que pasen, y (c) finalmente un refactoring que mejore el diseño sin romper las pruebas. El código debe ser en Java. Pega el prompt y la respuesta.
2. **Análisis**: ¿El LLM realmente hizo TDD (test primero, implementación después)? ¿O escribió implementación y luego mostró pruebas que ya pasaban? ¿Las pruebas cubren todos los escenarios relevantes? ¿El refactoring del LLM mejoró el diseño sin cambiar comportamiento? ¿Cómo verificarías que el LLM no está simulando TDD sino aplicándolo genuinamente?

---

### P.10 (5 puntos)

En clase discutimos dos enfoques de virtualización: **máquinas virtuales (VMs)** y **contenedores**. Ambos permiten ejecutar aplicaciones en entornos aislados, pero funcionan de manera fundamentalmente diferente.

**Tarea**:

1. **Prompt**: Pídele a tu LLM que compare máquinas virtuales y contenedores considerando: (a) arquitectura y cómo funciona cada uno, (b) ventajas de VMs sobre contenedores, (c) ventajas de contenedores sobre VMs, (d) casos de uso donde conviene cada uno, y (e) common pitfalls o errores comunes al adoptar cada tecnología. Pide el formato de salida como una tabla comparativa seguida de explicaciones. Pega el prompt y la respuesta.
2. **Análisis**: ¿El LLM explicó correctamente la diferencia de arquitectura? ¿Las ventajas que mencionó son precisas? ¿Los casos de uso son realistas? ¿Mencionó el problema de seguridad en contenedores por compartir kernel? ¿Identificó los pitfalls comunes al adoptar cada tecnología? ¿Qué concepto de los vistos en clase omitió el LLM?

---

### P.11 ⭐ (4 puntos)

En el proceso de desarrollo de software, existen dos enfoques principales para las pruebas: **pruebas manuales** y **pruebas automatizadas**. Cada uno tiene ventajas, desventajas y contextos donde es más apropiado. OpenLib Market está definiendo su estrategia de testing para el primer release.

**Tarea**:

1. **Prompt**: Pídele a tu LLM que compare pruebas manuales y automatizadas considerando: ventajas de cada una (mínimo 3), desventajas de cada una (mínimo 3), tipos de pruebas que aplican a cada enfoque, criterios para decidir cuándo automatizar una prueba y cuándo hacerla manual, y una recomendación concreta de qué pruebas deberían ser manuales y cuáles automatizadas en OpenLib Market. Pega el prompt y la respuesta.
2. **Análisis**: ¿El LLM capturó correctamente las ventajas y desventajas de cada enfoque? ¿Mencionó el costo de mantenimiento de las pruebas automatizadas? ¿Los criterios de decisión son prácticos o genéricos? ¿La recomendación para OpenLib Market es realista para el contexto del proyecto? ¿Qué ventaja o desventaja importante omitió el LLM?

---

### P.12 ⭐ (3 puntos)

Durante las pruebas de OpenLib Market, un tester reporta el siguiente defecto en Jira:

> **Resumen**: El botón "Agregar al carrito" no responde cuando el usuario tiene más de 5 ítems en el carrito.
>
> **Pasos para reproducir**: (1) Iniciar sesión como comprador, (2) Agregar 5 libros diferentes al carrito, (3) Intentar agregar un sexto libro desde la página de detalle del producto.
>
> **Resultado esperado**: El libro se agrega al carrito y el contador se actualiza.
>
> **Resultado obtenido**: El botón no produce ninguna acción. No hay mensaje de error ni retroalimentación visual.

**Tarea**: Gestiona este defecto aplicando el ciclo de vida de defectos visto en clase.

1. **Prompt**: Pídele a tu LLM que explique el ciclo de vida de un defecto (detección → reporte → asignación → diagnóstico → corrección → verificación → cierre) aplicado a este caso concreto de OpenLib Market. Para cada fase, debe indicar: ¿quién es responsable?, ¿qué acción se toma?, ¿qué herramienta se usa?, ¿cuál sería el siguiente estado del defecto en Jira? Pega el prompt y la respuesta.
2. **Análisis**: ¿El LLM explicó correctamente todas las fases del ciclo de vida? ¿Asignó los responsables adecuados a cada fase? ¿Mencionó las diferencias entre los posibles estados del defecto? ¿Identificó correctamente la posible causa raíz? ¿El flujo de estados que propuso es correcto? ¿Qué fase del ciclo omitió o trató superficialmente el LLM?

---

### P.13 (3 puntos)

Un desarrollador del equipo escribió esta clase para manejar las notificaciones del sistema. El código compila y funciona, pero tiene problemas de diseño:

```java
public class Notificador {
    public void enviar(String tipo, String destinatario, String mensaje) {
        if (tipo.equals("EMAIL")) {
            // Conectar a SMTP
            // Enviar correo
            System.out.println("Enviando EMAIL a " + destinatario + ": " + mensaje);
        } else if (tipo.equals("SMS")) {
            // Conectar a API de Twilio
            // Enviar SMS
            System.out.println("Enviando SMS a " + destinatario + ": " + mensaje);
        } else if (tipo.equals("PUSH")) {
            // Conectar a Firebase
            // Enviar notificación push
            System.out.println("Enviando PUSH a " + destinatario + ": " + mensaje);
        }
        // Registrar en archivo de log
        try {
            Files.write(Paths.get("/var/log/openlib/notificaciones.log"),
                (tipo + "|" + destinatario + "|" + mensaje + "\n").getBytes(),
                StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

**Tarea**: Refactoriza esta clase integralmente.

1. **Prompt**: Pídele a tu LLM que refactorice esta clase aplicando al menos un principio SOLID, al menos un patrón de diseño de los vistos en clase, y que incluya pruebas unitarias con JUnit 5. Pega el prompt y la respuesta.
2. **Análisis**: ¿El LLM identificó correctamente los principios SOLID violados? ¿Qué patrón aplicó y fue el más adecuado? ¿La solución con pruebas es completa y testeable? ¿El logging quedó correctamente separado de la lógica de notificación? ¿La nueva versión permitiría agregar un canal de notificación sin modificar código existente? Si tuvieras que implementar esta clase en producción para OpenLib Market, ¿usarías exactamente la solución del LLM o la modificarías? ¿En qué?

---

## Resumen de puntos por tema

| Tema | Preguntas | Puntos |
|---|---|---|
| Principios SOLID | 1, 2, 3 | 10 |
| Patrones de diseño | 4, 5 | 8 |
| Clean Architecture | 6, 7 | 8 |
| Pruebas de software | 8, 9 | 9 |
| VM vs Containers | 10 | 5 |
| Estrategia de testing y defectos | 11, 12 | 7 |
| Integración — Refactoring | 13 | 3 |
| **TOTAL** | **13 preguntas** | **50 puntos** |

---

## Notas para la calificación

1. **Calidad del prompt (30%)**: se evalúa especificidad, inclusión de contexto del proyecto, definición de restricciones, solicitud de formato de salida, y si el prompt incluye ejemplos o criterios de éxito.
2. **Calidad del análisis (70%)**: se evalúa capacidad de identificar aciertos, errores y omisiones del LLM, profundidad del análisis comparativo con conceptos vistos en clase, y capacidad de síntesis para producir una respuesta propia mejorada.
3. **Bonus potencial**: si el estudiante itera sobre el prompt (múltiples intentos documentados en commits) mostrando mejora progresiva en la calidad de las respuestas.
4. **Penalización**: entregas sin análisis crítico (solo prompt + copy-paste de respuesta del LLM), o uso de frases genéricas tipo "el LLM respondió bien" sin profundizar.
