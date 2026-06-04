# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [P09_tdd_invetario]

### Estudiante
- **Nombre completo**: [Alejandro Andres Perez Diaz]

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [**Sin IA — respuesta propia**] |
| **Modelo específico** | ["N/A"] |
| **¿Por qué elegiste este LLM?** | [Porue es implemetnar algo de codigo y anasis de lo que se hiso.] |

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
parte de codigo:
public class ControlInventarioService {
    private final LibroRepository repositorio;
    private final NotificadorVendedor notificador;

    public ControlInventarioService(LibroRepository repositorio,
                                     NotificadorVendedor notificador) {
        this.repositorio = repositorio;
        this.notificador = notificador;
    }

    public void actualizarStock(Libro libro, int nuevoStock) {
        if (nuevoStock < 0)
            throw new IllegalArgumentException("Stock no puede ser negativo");

        libro.setStock(nuevoStock);

        if (nuevoStock == 0) {
            libro.setEstado(EstadoLibro.AGOTADO);
            notificador.notificarAgotado(libro.getVendedor());
        } else {
            libro.setEstado(EstadoLibro.DISPONIBLE);
        }

        repositorio.guardar(libro);
    }
}
[El TDD es genuino cuando las pruebas se escriben antes de que exista la clase. La diferencia entre Green y Refactor es que en Green el código es feo pero funciona, y en Refactor se extrae lógica a métodos privados como validarStock y actualizarEstado sin cambiar el comportamiento — las pruebas siguen pasando exactamente igual.]
