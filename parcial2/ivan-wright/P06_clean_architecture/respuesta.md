# Plantilla de entrega — Parcial 2

---

## Pregunta P06: Clean Architecture

### Estudiante
- **Nombre completo**: Ivan Wright

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Sin IA — respuesta propia |
| **Modelo especifico** | N/A |
| **¿Por que elegiste este LLM?** | Al igual que en otras preguntas recomendadas, decidi no utilizar IA para demostrar mi dominio teorico y practico sobre arquitectura de software y asegurar el bono del +20%. |

---

### Analisis critico de la respuesta

#### 1. ¿Que hizo bien el prompt?
N/A (Respuesta elaborada sin asistencia de IA).

#### 2. ¿Que se puede mejorar?
N/A (Respuesta elaborada sin asistencia de IA).

#### 3. Respuesta final

Primeramente hay que entender que Clean Architecture se basa en organizar todo en capas que van de adentro hacia afuera, donde la regla de oro es que las dependencias siempre deben apuntar hacia adentro. Esto significa que el centro de la aplicacion no tiene idea de lo que pasa afuera.

Basicamente hay cuatro capas. Las Entities son el nucleo puro donde van las reglas del negocio, por ejemplo una clase `Libro` donde tenemos la logica de si se le puede aplicar un descuento o no a su precio independientemente de la base de datos. Seguidamente estan los Use Cases que controlan el flujo de la aplicacion, como un `PublicarLibroUseCase` que recibe el libro y orquesta el proceso. Luego vienen los Interface Adapters como los controladores REST que transforman lo que llega de la web a algo que los Use Cases entiendan, y finalmente la capa mas externa de Frameworks and Drivers donde estaria la conexion dura a PostgreSQL o Spring Boot.

Para entender el principio de inversion de dependencia (DIP) en los limites de estas capas, imaginemos que el caso de uso necesita guardar el libro. Si el caso de uso llama directo a la base de datos estaria apuntando hacia afuera y rompiendo la regla. Para arreglarlo, el caso de uso crea una interfaz como `RepositorioLibro` en su misma capa interna. Asi, la capa externa implementa esa interfaz (por ejemplo con `PostgresLibroRepository`). En tiempo de ejecucion la accion va hacia afuera, pero la dependencia del codigo apunta hacia adentro, lo cual es la clave de todo.

Si comparamos esto con la arquitectura tradicional en capas que casi siempre es presentacion, logica y luego datos, vemos un problema grave. Ahi la logica depende de los datos, asi que si pasamos de MySQL a MongoDB se nos cae todo el sistema. En Clean architecture la logica y las entidades estan aisladas en el centro y no dependen de si usamos Postgres o Firebase.

Aca pongo un ejemplo muy rapido de como se verian las capas en codigo para OpenLib Market:

```java
// Capa 1: Entidad (sin dependencias)
public class Libro {
    private double precio;
    public boolean aplicarDescuento(double porcentaje) {
        return true; // logica pura de negocio
    }
}

// Capa 2: Interfaz DIP y Caso de Uso
public interface RepositorioLibro {
    void guardar(Libro libro);
}

public class PublicarLibroUseCase {
    private final RepositorioLibro repo; // Inyectado por interfaz
    public PublicarLibroUseCase(RepositorioLibro repo) { this.repo = repo; }
    public void ejecutar(Libro libro) { repo.guardar(libro); }
}

// Capa 3: Adaptador Controlador
@RestController
public class LibroController {
    private final PublicarLibroUseCase useCase;
    public LibroController(PublicarLibroUseCase useCase) { this.useCase = useCase; }
    @PostMapping("/libros")
    public String publicar() { /* llama al useCase */ return "ok"; }
}

// Capa 4: Frameworks / DB
@Repository
public class PostgresLibroRepository implements RepositorioLibro {
    public void guardar(Libro libro) {
        // guardado real en postgres
    }
}
```

Asi demostramos que si cambiamos el framework web o la base de datos la logica no se entera para nada.
