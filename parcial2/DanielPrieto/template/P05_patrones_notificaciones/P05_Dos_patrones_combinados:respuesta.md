# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [05]: [P05 Dos patrones combinados]

### Estudiante
- **Nombre completo**: [Daniel Prieto]

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [**Sin IA — respuesta propia**] |
| **Modelo específico** | [N/A] |
| **¿Por qué elegiste este LLM?** | [] |

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

[Patrones: Observer + Strategy

	•	Observer: Libro es el subject; NotificadorEmail, NotificadorPush, ActualizadorCache, AuditoriaLogger son observers. Cuando el libro vuelve a estar disponible, notifica a todos.
	•	Strategy: cada observer implementa su propia estrategia de notificación, intercambiable e independiente.

// Observer
public interface ObservadorDisponibilidad {
    void onLibroDisponible(Libro libro);
}

public class Libro {
    private List<ObservadorDisponibilidad> observadores = new ArrayList<>();
    
    public void agregarObservador(ObservadorDisponibilidad o) { observadores.add(o); }
    public void removerObservador(ObservadorDisponibilidad o) { observadores.remove(o); }
    
    public void reponerStock(int cantidad) {
        this.stock += cantidad;
        if (this.stock > 0 && this.estado == EstadoLibro.AGOTADO) {
            this.estado = EstadoLibro.DISPONIBLE;
            observadores.forEach(o -> o.onLibroDisponible(this));
        }
    }
}

public class NotificadorEmailWishlist implements ObservadorDisponibilidad {
    public void onLibroDisponible(Libro libro) { /* enviar correo a wishlist */ }
}
public class NotificadorPushFavoritos implements ObservadorDisponibilidad {
    public void onLibroDisponible(Libro libro) { /* push a favoritos */ }
}
public class ActualizadorCacheRedis implements ObservadorDisponibilidad {
    public void onLibroDisponible(Libro libro) { /* invalidar/actualizar cache */ }
}
public class AuditoriaLogger implements ObservadorDisponibilidad {
    public void onLibroDisponible(Libro libro) { /* log auditoría */ }
}


Permite registrar/desregistrar observers dinámicamente. Para manejar fallos, cada observer debería tener try/catch interno o usar un mecanismo de reintentos, sin que un fallo propague al subject.]
