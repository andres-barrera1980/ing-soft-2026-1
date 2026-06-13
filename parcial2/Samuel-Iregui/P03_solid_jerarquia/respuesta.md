# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta 3: Evaluar una jerarquía de clases que viola principios de diseño

### Estudiante
- **Nombre completo**: Samuel Iregui

---
**Respuesta sin IA**
La clase viola principalmente el principio de Segregación de Interfaces (ISP), ya que la interfaz Usuario obliga a implementar métodos que no corresponden a todos los roles. Por ejemplo, un comprador no debería tener funciones de vendedor o administrador.

También incumple el principio de Sustitución de Liskov (LSP), porque algunas implementaciones terminan lanzando excepciones al ejecutar métodos que no les aplican, impidiendo que puedan sustituir correctamente a un Usuario.

Una mejor solución sería dividir Usuario en interfaces más específicas, como Comprador, Vendedor y Administrador, de manera que cada una contenga únicamente las responsabilidades que le corresponden.

**Refactor**
```java
public interface Comprador {
    void comprar(Libro libro);
}

public interface Vendedor {
    void vender(Libro libro);
}

public interface Moderador {
    void moderarComentario(Comentario comentario);
}

public interface Administrador {
    void gestionarUsuarios();
    void generarReporteVentas();
}

public class UsuarioComprador implements Comprador {
    @Override
    public void comprar(Libro libro) {
        // implementación
    }
}

public class UsuarioVendedor implements Vendedor {

    @Override
    public void vender(Libro libro) {
        // implementación
    }
}

public class UsuarioAdministrador implements Comprador, Vendedor, Moderador, Administrador {
    @Override
    public void comprar(Libro libro) {
        // implementación
    }
    @Override
    public void vender(Libro libro) {
        // implementación
    }
    @Override
    public void moderarComentario(Comentario comentario) {
        // implementación
    }
    @Override
    public void gestionarUsuarios() {
        // implementación
    }
    @Override
    public void generarReporteVentas() {
        // implementación
    }
}
```
