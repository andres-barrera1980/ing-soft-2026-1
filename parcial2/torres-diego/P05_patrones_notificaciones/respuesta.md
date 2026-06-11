# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [05]: [P05_patrones_notificaciones]

### Estudiante
- **Nombre completo**: [Diego Alejandro Torres Barrgan]

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [Claude] |
| **Modelo específico** | [Ej: Claude Sonnet 4.6/bajo]|
| **¿Por qué elegiste este LLM?** | [Es el modelo que actualmente mas uso y en el que estoy confiando] |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```
[Eres un ingeneiro de softaware senior en el que estas haciendo una tienda online de libros , donde en estos momentos tienes que hacer que En OpenLib Market, cuando un libro agotado vuelve a estar disponible, varios componentes del sistema necesitan reaccionar:- Enviar notificación por correo a los usuarios que lo agregaron a su wishlist.- Enviar notificación push a los usuarios que lo marcaron como favorito en la app móvil.- Actualizar la caché de Redis para que aparezca en búsquedas.- Registrar el evento en un log de auditoría.

necesito que diseñes e implementes este sistema usando dos patrones de diseño de la lista que te di , que es los patrones que vimos en clase para que trabajen en conjunto, para esto tambien quiero que hagas un diagrama de clase (en maid o texto , usa el que mas se te facilite) codigo en java de las clases principales, y una breve explicacion de porque se eligio esos dos patrones. ]
```

---

### Respuesta del LLM
Patrones que aplican: Observer + Strategy
¿Por qué estos dos patrones?
Se usa Observer porque cuando un libro vuelve a estar disponible, varios componentes necesitan reaccionar de forma independiente. GestorLibro no necesita saber quiénes son ni cuántos son, simplemente notifica y cada uno hace lo suyo. Si mañana llega un nuevo canal, solo se registra un observador nuevo sin tocar nada existente.
Se usa Strategy de forma implícita porque cada observador encapsula su propia lógica de notificación. El correo, el push, el cache y el log son estrategias distintas intercambiables detrás de la misma interfaz.

```java
// Interfaz Observer — todos los observadores deben implementarla
public interface ObservadorLibro {
    void actualizar(Libro libro);
}

// Sujeto — GestorLibro mantiene la lista de observadores y notifica
public class GestorLibro {

    private final List<ObservadorLibro> observadores = new ArrayList<>();

    public void registrar(ObservadorLibro observador) {
        observadores.add(observador);
    }

    public void desregistrar(ObservadorLibro observador) {
        observadores.remove(observador);
    }

    public void marcarDisponible(Libro libro) {
        libro.setEstado("DISPONIBLE");
        notificar(libro);
    }

    private void notificar(Libro libro) {
        for (ObservadorLibro observador : observadores) {
            observador.actualizar(libro);
        }
    }
}

// Observador 1 — envía correo a usuarios de la wishlist
public class NotificadorCorreo implements ObservadorLibro {
    @Override
    public void actualizar(Libro libro) {
        // conectar a SMTP y enviar correo a usuarios de la wishlist
        System.out.println("Correo enviado: " + libro.getTitulo() + " está disponible");
    }
}

// Observador 2 — envía notificación push a favoritos
public class NotificadorPush implements ObservadorLibro {
    @Override
    public void actualizar(Libro libro) {
        // conectar a Firebase y enviar push
        System.out.println("Push enviado: " + libro.getTitulo() + " está disponible");
    }
}

// Observador 3 — actualiza la caché de Redis
public class ActualizadorCache implements ObservadorLibro {
    @Override
    public void actualizar(Libro libro) {
        // actualizar entrada en Redis
        System.out.println("Cache actualizada para: " + libro.getTitulo());
    }
}

// Observador 4 — registra el evento en el log de auditoría
public class RegistradorAuditoria implements ObservadorLibro {
    @Override
    public void actualizar(Libro libro) {
        Logger.getLogger("OpenLib").info("Libro disponible: " + libro.getIsbn());
    }
}
```


GestorLibro (sujeto)
│  + registrar(observador)
│  + desregistrar(observador)
│  + marcarDisponible(libro)
│
│ notifica
▼
ObservadorLibro (interfaz)
│  + actualizar(libro)
│
│ implementan
├──────────────────────────────────────────┐
│                    │                     │                    │
NotificadorCorreo  NotificadorPush  ActualizadorCache  RegistradorAuditoria
+ actualizar()     + actualizar()   + actualizar()     + actualizar()
(envía email)      (envía push)     (actualiza Redis)  (escribe log)

```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

[El contexto fue bastante completo. Se especificó el proyecto (OpenLib Market), se listaron exactamente los 4 componentes que debían reaccionar, se pidió diagrama + código + justificación, y se restringió a los patrones vistos en clase. Eso le dio al LLM suficiente información para no inventarse cosas]


#### 2. ¿Qué se puede mejorar?

[El prompt estuvo bastante completo, lo único que creo que le faltó fue pedir explícitamente que justificara por qué descartó otros patrones de la lista. Eso hubiera dado mas peso a la respuesta.]


#### 3. Respuesta final

[Para que OpenLib Market avise de forma independiente cuando un libro vuelve a estar disponible, usamos Observer y Strategy (los patrones que se vieron en clase).

El GestorLibro funciona como el sujeto (Observer).Cuando el libro cambia a "DISPONIBLE", les avisa a todos los que estén anotados de una,sin importarle quiénes sean.Asi cumplimos el OCP porque podemos meter mas alertas despues sin tocar el gestor.

El cómo se avisa (si por mail, push, log o caché)lo manejamos con Strategy.Cada alerta implementa la interfaz ObservadorLibro, volviendose una estrategia intercambiable.

Juntos hacen un combo genial: Observer resuelve a quién notificar y Strategy el cómo. El código queda bien limpio, desacoplado y cumpliendo el SRP.]
