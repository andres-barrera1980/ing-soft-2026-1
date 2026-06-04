# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [03]: [Solid jerarquia]

### Estudiante
- **Marlon Garcia**: [Tu nombre y apellido]

---


### Respuesta sin IA 

La clase viola principalmente el principio de segregación de interfaces+, porque la interfaz Usuario obliga a todos los tipos de usuario a implementar métodos que no necesariamente le corresponden. Por ejemplo, un comprador no debería estar obligado a tener métodos como vender, generarReporteVentas o gestionarUsuarios, ya que esas acciones pertenecen a otros roles como vendedor o administrador.

Esto provoca que algunas clases tengan que implementar métodos innecesarios y manejar esos casos lanzando excepciones como “no autorizado”. Sin embargo, esa excepción no debería ser necesaria si las responsabilidades estuvieran bien separadas en interfaces más específicas.

Además, también viola el principio de sustitución de Liskov ,porque si una clase Comprador implementa Usuario, debería poder usarse en cualquier lugar donde se espere un Usuario sin romper el comportamiento esperado. Pero si al llamar métodos como vender o gestionarUsuarios el comprador lanza una excepción, entonces no puede sustituir correctamente a la interfaz Usuario.

Una mejor solución sería dividir la interfaz en interfaces más pequeñas, por ejemplo: `Comprador`, `Vendedor` y `Administrador`, cada una con los métodos que deberia tener en vez de que usuario embeba todos.

### Refactor del codigo 
```java

public interface Usuario {
    String getNombre();
    String getEmail();
}

// ======Interfaces específicas por rol=====

// Solo los usuarios que pueden comprar implementan esta interfaz.
public interface Comprador extends Usuario {
    void comprar(Libro libro);
}

// Solo los usuarios que pueden vender implementan esta interfaz.
public interface Vendedor extends Usuario {
    void vender(Libro libro);
    // Podria ser una clase, pero los reportes pueden ser PDF, Excel, etc. [factory jeje]
    void generarReporteVentas();
}

// Solo los usuarios administradores implementan esta interfaz.
public interface Administrador extends Usuario {
    void gestionarUsuarios(Usuario usuario);
}

// Esta clase evita repetir nombre y email en todos los tipos de usuario.
public abstract class UsuarioBase implements Usuario {

    private String nombre;
    private String email;

    public UsuarioBase(String nombre, String email) {
        this.nombre = nombre;
        this.email = email;
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    @Override
    public String getEmail() {
        return email;
    }
}

```
