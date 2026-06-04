# Pregunta P09: TDD para control de inventario

### Estudiante
- **Nombre completo**: Juan Camilo Gomez

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Sin IA — respuesta propia |
| **Modelo específico** | N/A |
| **¿Por qué elegiste este LLM?** |para gaanarme el bono

---

### Análisis crítico de la respuesta

#### 3. Respuesta final

## TDD aplicado a ControlInventarioService

TDD sigue un ciclo Red → Green → Refactor. Lo importante es que cada prueba se escribe antes de la implementación, y se verifica que falla antes de escribir el código que la hace pasar.

---

### FASE RED — Pruebas que fallan primero

Escribimos las pruebas sin implementación. Todas deben compilar pero fallar al ejecutarse.


    // RED 1: cuando stock llega a 0, debe marcar libro como AGOTADO
    @Test
    void cuandoStockLlegaACero_debeMarcarLibroComoAgotado() {
        Libro libro = new Libro(1L, "ISBN-001", 0, EstadoLibro.DISPONIBLE);
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro));

        service.actualizarStock(1L, 0);

        assertEquals(EstadoLibro.AGOTADO, libro.getEstado());
        verify(repositorioLibro).guardar(libro);
    }

    // RED 2: cuando stock llega a 0, debe notificar al vendedor
    @Test
    void cuandoStockLlegaACero_debeNotificarAlVendedor() {
        Libro libro = new Libro(1L, "ISBN-001", 0, EstadoLibro.DISPONIBLE);
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro));

        service.actualizarStock(1L, 0);

        verify(notificador).notificarAgotado(libro);
    }

    // RED 3: cuando se repone stock, debe volver a DISPONIBLE
    @Test
    void cuandoSeReponStock_debeMarcarLibroComoDisponible() {
        Libro libro = new Libro(1L, "ISBN-001", 0, EstadoLibro.AGOTADO);
        when(repositorioLibro.buscarPorId(1L)).thenReturn(Optional.of(libro));

        service.actualizarStock(1L, 5);

        assertEquals(EstadoLibro.DISPONIBLE, libro.getEstado());
        verify(repositorioLibro).guardar(libro);
    }




### FASE GREEN — Implementacion minima para hacer pasar las pruebas

La implementación mas simple posible que haga pasar todas las pruebas:


    public ControlInventarioService(RepositorioLibro repositorioLibro,
                                     NotificadorVendedor notificador) {
        this.repositorioLibro = repositorioLibro;
        this.notificador = notificador;
    }

    public void actualizarStock(Long libroId, int nuevoStock) {
        if (nuevoStock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }

        Libro libro = repositorioLibro.buscarPorId(libroId)
            .orElseThrow(() -> new NoSuchElementException("Libro no encontrado: " + libroId));

        if (nuevoStock == 0 && libro.getEstado() != EstadoLibro.AGOTADO) {
            libro.setStock(0);
            libro.setEstado(EstadoLibro.AGOTADO);
            repositorioLibro.guardar(libro);
            notificador.notificarAgotado(libro);
        } else if (nuevoStock > 0 && libro.getEstado() == EstadoLibro.AGOTADO) {
            libro.setStock(nuevoStock);
            libro.setEstado(EstadoLibro.DISPONIBLE);
            repositorioLibro.guardar(libro);
        } else {
            libro.setStock(nuevoStock);
            repositorioLibro.guardar(libro);
        }
    }
}
```

aca todas las pruebas deben pasar (GREEN).

---

### FASE REFACTOR — Mejorar el diseño sin romper las pruebas

El `if-else` anidado en `actualizarStock` mezcla lógica de transición de estado con efectos secundarios. Lo mejoramos extrayendo la lógica de transición al propio objeto `Libro`:


// Entidad Libro con lógica de estado encapsulada
public class Libro {
    private Long id;
    private String isbn;
    private int stock;
    private EstadoLibro estado;

    public void setStock(int nuevoStock) {
        if (nuevoStock < 0) throw new IllegalArgumentException("Stock no puede ser negativo");
        this.stock = nuevoStock;
        // La entidad gestiona su propio estado
        if (nuevoStock == 0) {
            this.estado = EstadoLibro.AGOTADO;
        } else {
            this.estado = EstadoLibro.DISPONIBLE;
        }
    }

    public boolean recienAgotado() { return stock == 0 && estado == EstadoLibro.AGOTADO; }
    // getters y constructor...
}

```

Las pruebas siguen pasando sin modificarse,eso confirma que el refactoring no rompió el comportamiento.

