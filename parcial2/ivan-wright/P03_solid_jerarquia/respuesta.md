# Plantilla de entrega — Parcial 2

---

## Pregunta P03: SOLID — Jerarquia de usuarios

### Estudiante
- **Nombre completo**: Ivan Wright

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Sin IA — respuesta propia |
| **Modelo especifico** | N/A |
| **¿Por que elegiste este LLM?** | Decidi no utilizar IA en esta pregunta para evaluar directamente mis conocimientos sobre principios SOLID y ganar el bono del +20% por responder de manera autonoma. |

---

### Analisis critico de la respuesta

#### 1. ¿Que hizo bien el prompt?
N/A (Respuesta elaborada sin asistencia de IA).

#### 2. ¿Que se puede mejorar?
N/A (Respuesta elaborada sin asistencia de IA).

#### 3. Respuesta final

Primeramente en `public interface Usuario { ... }` se viola el Interface Segregation Principle (ISP), ya que agrupa responsabilidades muy distintas obligando a cualquier clase a cargar con todos esos metodos, lo que deberia ser separado en interfaces mas pequeñas.

En `public class Comprador implements Usuario { public void vender(Libro libro) { throw new UnsupportedOperationException(...); } ... }` se puede ver el efecto de la violacion del Interface Segregation Principle (ISP) ya que la clase se ve forzada a implementar metodos que no necesita y debe lanzar excepciones diciendo que no estan soportados.

En `public class Vendedor extends Comprador { public void comprar(Libro libro) { throw new UnsupportedOperationException(...); } }` se viola el Liskov Substitution Principle (LSP) al hacer que un vendedor herede de un comprador pero alterando el comportamiento original del padre al bloquear la compra con una excepcion. Si el sistema intenta usar al vendedor asumiendo que es un comprador la aplicacion fallara en tiempo de ejecucion.

En `public class Administrador extends Vendedor { public void comprar(Libro libro) { ... } }` se vuelve a violar el Liskov Substitution Principle (LSP). Esto es porque hereda de vendedor solo para reutilizar la funcion de vender, pero vuelve a activar la funcion comprar que el padre habia bloqueado. Usar la herencia de esta manera no representa una relacion "es-un" logica sino que se usa a la fuerza para no escribir mas codigo.

---

### Propuesta de Rediseño (Java 21)

Para solucionar esto, debemos separar la interfaz gigante en interfaces mas pequeñas y especificas (roles). Luego, cada clase implementara unicamente las interfaces de las acciones que realmente puede realizar, eliminando por completo la necesidad de lanzar excepciones de operacion no soportada.

#### 1. Segregacion de Interfaces (ISP)

```java
public interface CompradorLibros {
    void comprar(Libro libro);
}

public interface VendedorLibros {
    void vender(Libro libro);
    void generarReporteVentas();
}

public interface AdministradorSistema {
    void moderarComentario(Comentario comentario);
    void gestionarUsuarios();
}
```

#### 2. Implementacion de Clases (LSP)

Ahora cada tipo de usuario solo se compromete a implementar los contratos (interfaces) que de verdad le corresponden a su rol, evitando herencias forzadas:

```java
public class Comprador implements CompradorLibros {
    @Override
    public void comprar(Libro libro) {
        // Logica de compra de libros
    }
}

public class Vendedor implements VendedorLibros {
    @Override
    public void vender(Libro libro) {
        // Logica para publicar y vender
    }

    @Override
    public void generarReporteVentas() {
        // Logica para generar reportes
    }
}

// Un administrador puede hacer de todo en el sistema,
// por lo que implementa todas las interfaces de roles.
public class Administrador implements CompradorLibros, VendedorLibros, AdministradorSistema {
    @Override
    public void comprar(Libro libro) {
        // Logica de compra de libros
    }

    @Override
    public void vender(Libro libro) {
        // Logica para publicar y vender
    }

    @Override
    public void generarReporteVentas() {
        // Logica para generar reportes
    }

    @Override
    public void moderarComentario(Comentario comentario) {
        // Logica para eliminar o aprobar comentarios
    }

    @Override
    public void gestionarUsuarios() {
        // Logica para suspender o crear usuarios
    }
}
```

*Nota: En un caso mas complejo y real para OpenLib Market, quizas seria aun mejor usar Composicion sobre Herencia utilizando un patron basado en Roles (RBAC), donde un objeto `Usuario` unico contiene una lista de `Roles` dinamicos que le otorgan permisos. Sin embargo, segregar las interfaces en roles es la solucion arquitectonica directa y correcta para arreglar las violaciones de SOLID en el codigo presentado.*
