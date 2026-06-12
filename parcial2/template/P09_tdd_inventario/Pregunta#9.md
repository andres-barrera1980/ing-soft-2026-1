# Plantilla de entrega — Parcial 2
---

## Pregunta [09]: [P09_tdd_inventario]

### Estudiante
- **Nombre completo**: Samuel López Gómez

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Chat GPT |
| **Modelo específico** |GPT 5.5|
| **¿Por qué elegiste este LLM?** |  GPT 5.5 es muy bueno simulando el flujo conversacional de TDD paso a paso.|

---

### Prompt utilizado

Actúa como un Agile Coach y desarrollador Senior enseñando la técnica TDD (Test-Driven Development) a un junior del proyecto OpenLib Market (Java 21).

Tenemos la funcionalidad de "Control de Inventario": 
1. Cuando el stock de un libro llega a 0, el sistema debe cambiar su estado a "AGOTADO".
2. Debe notificar al vendedor.
3. Si el vendedor repone stock (cantidad > 0), el libro vuelve a "DISPONIBLE".

Necesito que demuestres el ciclo estricto de TDD (Red -> Green -> Refactor) para la clase `ControlInventarioService`. 
Debes mostrar el código progresivamente:
1. PASO RED: Escribe SOLO la primera prueba unitaria (JUnit 5 + Mockito) que debe fallar. No escribas la implementación de la clase todavía.
2. PASO GREEN: Escribe la implementación mínima, fea y más básica de `ControlInventarioService` solo para que esa prueba pase.
3. Repite Red y Green para la funcionalidad de reponer stock.
4. PASO REFACTOR: Muestra el código final refactorizado de la clase, mejorando el diseño, extrayendo métodos o mejorando legibilidad sin romper las pruebas.

Restricciones:
- No escribas todo el código de golpe. Separa claramente los bloques de código por cada fase (Red, Green, Refactor).
- Usa POO pura.

Formato de salida: Markdown estructurado como una clase interactiva, con los pasos claramente delimitados por títulos.

```

---

### Respuesta del LLM
Clase Interactiva: TDD para ControlInventarioService (Java 21)
Objetivo
Implementar la funcionalidad de control de inventario siguiendo estrictamente el ciclo:

Red → Green → Refactor

Reglas de negocio:

Si el stock llega a 0, el libro pasa a estado AGOTADO.
Se notifica al vendedor.
Si el vendedor repone stock (> 0), el libro vuelve a DISPONIBLE.
Iteración 1: Agotar Inventario
🔴 RED
Primero escribimos una prueba que falle.

@ExtendWith(MockitoExtension.class)
class ControlInventarioServiceTest {

    @Mock
    private NotificacionService notificacionService;

    @Test
    void debeMarcarLibroComoAgotadoYNotificarCuandoStockLlegaACero() {

        Libro libro = new Libro();
        libro.setStock(0);
        libro.setEstado(EstadoLibro.DISPONIBLE);

        ControlInventarioService service =
                new ControlInventarioService(notificacionService);

        service.actualizarEstado(libro);

        assertEquals(EstadoLibro.AGOTADO, libro.getEstado());

        verify(notificacionService)
                .notificarVendedor(libro);
    }
}
Resultado esperado:

❌ COMPILA MAL
ControlInventarioService no existe.
Perfecto. Estamos en RED.

🟢 GREEN
Implementación mínima para pasar la prueba.

public class ControlInventarioService {

    private final NotificacionService notificacionService;

    public ControlInventarioService(
            NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    public void actualizarEstado(Libro libro) {

        if (libro.getStock() == 0) {
            libro.setEstado(EstadoLibro.AGOTADO);
            notificacionService.notificarVendedor(libro);
        }
    }
}
Resultado:

✅ TEST PASA
No agregamos nada más.

Iteración 2: Reposición de Stock
🔴 RED
Nueva regla de negocio.

@Test
void debeCambiarADisponibleCuandoSeReponeStock() {

    Libro libro = new Libro();
    libro.setStock(5);
    libro.setEstado(EstadoLibro.AGOTADO);

    ControlInventarioService service =
            new ControlInventarioService(notificacionService);

    service.actualizarEstado(libro);

    assertEquals(
            EstadoLibro.DISPONIBLE,
            libro.getEstado());
}
Resultado esperado:

❌ TEST FALLA
Expected DISPONIBLE
Actual AGOTADO
Excelente.

🟢 GREEN
Implementación mínima para pasar ambas pruebas.

public class ControlInventarioService {

    private final NotificacionService notificacionService;

    public ControlInventarioService(
            NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    public void actualizarEstado(Libro libro) {

        if (libro.getStock() == 0) {

            libro.setEstado(EstadoLibro.AGOTADO);
            notificacionService.notificarVendedor(libro);

        } else {

            libro.setEstado(EstadoLibro.DISPONIBLE);
        }
    }
}
Resultado:

✅ TEST 1 PASA
✅ TEST 2 PASA
🔵 REFACTOR
Ahora mejoramos el diseño sin cambiar comportamiento.

Problemas detectados
Método con dos responsabilidades.
Lógica de estado mezclada con notificaciones.
Baja legibilidad.
Código Refactorizado
public class ControlInventarioService {

    private final NotificacionService notificacionService;

    public ControlInventarioService(
            NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    public void actualizarEstado(Libro libro) {

        if (sinStock(libro)) {
            marcarComoAgotado(libro);
            notificarVendedor(libro);
            return;
        }

        marcarComoDisponible(libro);
    }

    private boolean sinStock(Libro libro) {
        return libro.getStock() == 0;
    }

    private void marcarComoAgotado(Libro libro) {
        libro.setEstado(EstadoLibro.AGOTADO);
    }

    private void marcarComoDisponible(Libro libro) {
        libro.setEstado(EstadoLibro.DISPONIBLE);
    }

    private void notificarVendedor(Libro libro) {
        notificacionService.notificarVendedor(libro);
    }
}
Resultado Final
Tests construidos mediante TDD
Iteración 1
debeMarcarLibroComoAgotadoYNotificarCuandoStockLlegaACero
Iteración 2
debeCambiarADisponibleCuandoSeReponeStock
Aprendizajes
RED
Escribir primero una prueba que falle.

GREEN
Implementar únicamente el código mínimo necesario para que pase.

REFACTOR
Mejorar diseño, legibilidad y mantenibilidad sin romper los tests.

Esta disciplina garantiza que la implementación esté guiada por requisitos verificables y reduce significativamente la probabilidad de introducir defectos en OpenLib Market.
```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

Restringir a la IA para que "no escriba todo el código de golpe" fue la clave del éxito. Los modelos suelen procesar el problema completo internamente y entregar la clase final junto con los tests de una sola vez, lo que arruina por completo la metodología de TDD. Al asignarle el rol de "Agile Coach enseñando a un junior" y pedirle los pasos explícitos, la forzamos a mostrar en orden lógico cómo estructurar la prueba fallida (fase Red) antes de escribir la implementación funcional (fase Green).

#### 2. ¿Qué se puede mejorar?

Aunque el modelo ejecutó mecánicamente bien el ciclo de TDD, cometió un par de errores operativos que no pasarían una revisión técnica:

Primero, introdujo un fallo de idempotencia (el bug del spam). En la fase Green, la lógica indica que si el stock baja a cero, el libro pasa a estado AGOTADO y se dispara una notificación. Pero si el libro ya estaba AGOTADO y se guarda otro cambio (por ejemplo, se actualiza el precio), el sistema volverá a enviar la alerta al vendedor. La IA omitió un ciclo Red/Green crítico: validar que la notificación únicamente se envíe si el estado anterior era DISPONIBLE.

Segundo, realizó un refactoring sin valor estructural. Durante la fase Refactor, el modelo simplemente encapsuló los métodos setter nativos de Java dentro de métodos privados (por ejemplo, meter libro.setEstado(AGOTADO) en un método marcarComoAgotado()). Esto es una práctica de sobre-ingeniería que no soluciona ningún problema de diseño, solo añade una indirección innecesaria. Un rediseño correcto bajo el enfoque de dominio rico (Rich Domain Model) hubiera trasladado la lógica de cambio de estado hacia adentro de la entidad Libro.

#### 3. Respuesta final
 El documento generado por el LLM sirve como una excelente guía teórica de TDD: demuestra cómo empezar con una prueba que ni siquiera compila (Red), programar el código mínimo indispensable para pasar la validación (Green), iterar sobre nuevos requisitos, y limpiar la estructura (Refactor). Comprender esta disciplina garantiza que el código de producción nazca completamente respaldado por pruebas. Sin embargo, para un proyecto real de la carrera, yo añadiría la iteración que el modelo ignoró para frenar el envío duplicado de notificaciones