# Pregunta P03: SOLID — Jerarquía de usuarios ⭐

### Estudiante
- **Nombre completo**: Mateo Traslaviña Moreno

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Sin IA — respuesta propia |
| **Modelo especifico** | N/A |
| **¿Por que elegiste este LLM?** | Esta pregunta es marcada con ⭐ como recomendada sin IA. Ademas, los principios ISP y LSP en jerarquias de herencia son temas que estudie directamente en clase y tengo criterio propio para identificar las violaciones sin necesitar asistencia de un modelo de lenguaje. Responder sin IA me otorga el bono del +20%. |

---

### Analisis crítico de la respuesta

## Principios SOLID violados en esta jerarquia

**1. ISP — Interface Segregation Principle (violacion principal)**

La interfaz `Usuario` obliga a TODOS los tipos de usuario a declarar metodos que no les corresponden:

```java
public interface Usuario {
    void comprar(Libro libro);       // No aplica a Administrador puro
    void vender(Libro libro);        // No aplica a Comprador
    void moderarComentario(...);     // Solo Administrador
    void generarReporteVentas();     // Solo Administrador
    void gestionarUsuarios();        // Solo Administrador
}
```

`Comprador` implementa `vender()`, `moderarComentario()`, `generarReporteVentas()` y `gestionarUsuarios()` lanzando `UnsupportedOperationException`. Esto es exactamente lo que ISP prohíbe: una clase nunca debería verse forzada a implementar métodos que no usa.

**2. LSP — Liskov Substitution Principle (violación grave en tiempo de ejecución)**

Vendedor extends Comprador y sobrescribe comprar() lanzando UnsupportedOperationException. Esto viola LSP brutalmente: si tengo una referencia de tipo Comprador y le paso un Vendedor, llamar comprar() lanza una excepcion en tiempo de ejecucion que el codigo cliente no esperaba. Un Vendedor NO es sustituible por un Comprador — la herencia esta invertida conceptualmente.

```java
Comprador c = new Vendedor(); // Compila sin problema
c.comprar(unLibro);           // ¡Explota en runtime! LSP violado
```

**3. SRP secundario**: Administrador hereda de Vendedor (que hereda de Comprador), acoplando tres roles distintos en una jerarquia de herencia profunda. Si el rol de Vendedor cambia, Administrador se ve afectado automaticamente.

**Rediseño correcto aplicando ISP y composición sobre herencia:**

```java
// Interfaces segregadas por capacidad
public interface Comprador {
    void comprar(Libro libro);
}

public interface Vendedor {
    void vender(Libro libro);
    void generarReporteVentas();
}

public interface Moderador {
    void moderarComentario(Comentario comentario);
}

public interface AdministradorSistema {
    void gestionarUsuarios();
}

// Implementaciones con exactamente las capacidades que necesitan
public class UsuarioComprador implements Comprador {
    @Override
    public void comprar(Libro libro) { /* implementación real */ }
}

public class UsuarioVendedor implements Vendedor {
    @Override
    public void vender(Libro libro) { /* implementación real */ }
    
    @Override
    public void generarReporteVentas() { /* implementación real */ }
}

