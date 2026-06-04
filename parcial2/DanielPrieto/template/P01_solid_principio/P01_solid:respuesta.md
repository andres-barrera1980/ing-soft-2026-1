# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [XX]: [P01_solid_srcp]

### Estudiante
- **Nombre completo**: [DanielPrieto]

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [ **Sin IA — respuesta propia**] |
| **Modelo específico** | [N/A] |
| **¿Por qué elegiste este LLM?** | [Porque queria retarme a realizar los ejercicios usando unicamente mi conocimiento, ya que considero que tengo dominio de los temas que aparecen en el parcial] |

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

[Principio principal violado: Single Responsibility Principle (SRP)

GestorLibro.publicarLibro hace 6 cosas distintas: validar datos, persistir en BD, generar slug, enviar correo, registrar log e indexar en búsqueda. Una clase/método debe tener una sola razón para cambiar; aquí hay seis.

Secundariamente también viola DIP: instancia directamente EmailService y SearchIndex con new, creando dependencias concretas en lugar de depender de abstracciones.

Refactoring:

public class GestorLibro {
    private final LibroRepository libroRepository;
    private final SlugGenerator slugGenerator;
    private final NotificacionService notificacionService;
    private final SearchIndexService searchIndexService;

    public GestorLibro(LibroRepository libroRepository, SlugGenerator slugGenerator,
                       NotificacionService notificacionService, SearchIndexService searchIndexService) {
        this.libroRepository = libroRepository;
        this.slugGenerator = slugGenerator;
        this.notificacionService = notificacionService;
        this.searchIndexService = searchIndexService;
    }

    public void publicarLibro(Libro libro, Usuario vendedor) {
        validar(libro);
        libro.setSlug(slugGenerator.generar(libro.getTitulo()));
        libroRepository.guardar(libro);
        notificacionService.notificarPublicacion(vendedor, libro);
        searchIndexService.indexar(libro);
    }

    private void validar(Libro libro) {
        if (libro.getIsbn() == null || libro.getTitulo() == null || libro.getAutor() == null)
            throw new IllegalArgumentException("Datos del libro incompletos");
    }
}


Cada colaborador tiene su propia responsabilidad. El logging se maneja dentro de cada servicio o con AOP.
]
