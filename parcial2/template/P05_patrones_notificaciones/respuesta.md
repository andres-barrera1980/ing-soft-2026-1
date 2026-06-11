# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [05]: [P05_patrones_notificaciones]

### Estudiante
- **Nombre completo**: [Danna Gabriela Rojas Bernal]

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Sin IA — respuesta propia** | [Claude / ChatGPT / Gemini / Copilot / DeepSeek / Qwen / Mistral / Otro / **Sin IA — respuesta propia**] |
| **N/A** | [Ej: Claude Opus 4.5, GPT-4o, Gemini 2.5 Pro, etc. Si respondes sin IA, escribe "N/A"] |
| ** No es necesario** | [Justifica en 1-3 oraciones. Si respondes sin IA, explica por qué decidiste no usar LLM para esta pregunta.] |

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
**Pregunta **: Diseña e implementa este sistema usando **dos patrones de diseño de los vistos en clase** que trabajen en conjunto.

Para este problema utilizaría una combinacion de los patrones Observer y Strategy, pues el patron Observer permite que cuando un libro vuelva a estar disponible, todos los componentes interesados sean notificados automaticamente como el servicio de correos, las notificaciones push o el sistema de auditoria. De esta forma, el libro no necesita conocer los detalles de cada accion que se ejecuta despues del cambio de estado. Y el patrOn Strategy permite que cada tipo de notificación implemente su propia logica de manera independiente, por ejemplo, enviar un correo, una notificación móvil o registrar información en un log.

Para observer:
public interface ObservadorDisponibilidad {
    void actualizar(Libro libro);
}
public class NotificadorCorreo implements ObservadorDisponibilidad {

    @Override
    public void actualizar(Libro libro) {
        System.out.println(
            "Enviando correo a usuarios con el libro "
            + libro.getTitulo()
            + " en su wishlist."
        );
    }
}
public class NotificadorPush implements ObservadorDisponibilidad {

    @Override
    public void actualizar(Libro libro) {
        System.out.println(
            "Enviando notificación push a usuarios favoritos de "
            + libro.getTitulo()
        );
    }
}
public class ActualizadorRedis implements ObservadorDisponibilidad {

    @Override
    public void actualizar(Libro libro) {
        System.out.println(
            "Actualizando caché Redis para "
            + libro.getTitulo()
        );
    }
}
public class AuditoriaLog implements ObservadorDisponibilidad {

    @Override
    public void actualizar(Libro libro) {
        System.out.println(
            "Registrando evento de auditoría para "
            + libro.getTitulo()
        );
    }
}
Strategy
public interface EstrategiaNotificacion {
    void ejecutar(Libro libro);
}
public class CorreoStrategy implements EstrategiaNotificacion {

    @Override
    public void ejecutar(Libro libro) {
        System.out.println("Enviando correo para " + libro.getTitulo());
    }
}
public class RedisStrategy implements EstrategiaNotificacion {

    @Override
    public void ejecutar(Libro libro) {
        System.out.println("Actualizando Redis para " + libro.getTitulo());
    }
}
