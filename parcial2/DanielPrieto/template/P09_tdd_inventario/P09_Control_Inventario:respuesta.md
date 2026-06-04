# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [09]: [P09 Control Inventario]

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

[Ciclo Red-Green-Refactor:

RED (tests primero, fallan):

@Test void cuandoStockLlegaACero_estadoCambiaAAgotado()
@Test void cuandoStockLlegaACero_notificaAlVendedor()
@Test void cuandoVendedorReponesStock_estadoVuelveADisponible()
@Test void reponerStock_conCantidadNegativa_lanzaException()
@Test void libroYaAgotado_alReponerStock_soloNotificaSiEstadoCambia()


GREEN (implementación mínima):

public class ControlInventarioService {
    private final LibroRepository libroRepository;
    private final VendedorNotificacionService notificacionService;
    
    public void reducirStock(Long libroId, int cantidad) {
        Libro libro = libroRepository.buscarPorId(libroId).orElseThrow();
        libro.setStock(libro.getStock() - cantidad);
        if (libro.getStock() <= 0) {
            libro.setEstado(EstadoLibro.AGOTADO);
            notificacionService.notificarAgotado(libro.getVendedor(), libro);
        }
        libroRepository.guardar(libro);
    }
    
    public void reponerStock(Long libroId, int cantidad) {
        if (cantidad <= 0) throw new IllegalArgumentException("Cantidad debe ser positiva");
        Libro libro = libroRepository.buscarPorId(libroId).orElseThrow();
        libro.setStock(libro.getStock() + cantidad);
        if (libro.getEstado() == EstadoLibro.AGOTADO) {
            libro.setEstado(EstadoLibro.DISPONIBLE);
        }
        libroRepository.guardar(libro);
    }
}


REFACTOR: extraer la lógica de cambio de estado a un método privado actualizarEstado(Libro) para no duplicar el condicional si más adelante hay más transiciones de estado.

La señal de que un LLM no hizo TDD real: muestra la implementación completa y luego los tests que “curiosamente” todos pasan. En TDD genuino, los tests se escriben contra una interfaz que aún no existe.]
