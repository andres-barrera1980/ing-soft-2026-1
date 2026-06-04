# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [03]: [TP03_solid_jerarquia]

### Estudiante
- **Nombre completo**: [Diego Alejandro Torres Barragan]

-Respuesta hecha sin IA 

#### 1. Respuesta final

[En este punto el principio que se esta violando es la I ( Interface Segregation Principle) ya que la interfaz usuario tiene 5 metodos (comprar, vender, moderarComentario, GenerarReporteVenta y gestionarUsuarios), lo cual afecta bastante ya que ningun tipo de usuario necesita todos los metodos que tiene, comprador puede implementar la interfaz pero 4 de sus 5 metodos lanzarar un error ya que se le esta obligando a implementar cosas que no le corresponde. 

Otro principio que se esta violando es la L(Liskov Substitution principle)en la jerarquia Vendedor extiende de  Comprador pero sobrescribe Comprar lanzando una execepcion, eso hace que se rompa LSP porque se tiene un comprador y lo remplazo por un vendedor , el programa ahi se romperia, ya que una subclase no deberia romper el comportamiento de su padre. ]

- El rediseñor que yo propongo es el siguiente 

separar la interfaz grande en interfaces pequeñas , una por cada capacidad 

 -Comprable con metodo comprar
 -Vendible con metodo vender
 -Moderador con el metodo moderarCometario
 -reportable con el metodo generarReporteVentas
 -gestorUsuarios con el metodo gestionarUsuarios

 Cada clase solo implementaria las interfaces que le corresponden.Asi nadie esta obligado a tener metodos que no usa. 
 
```java
// Interfaces separadas por capacidad
public interface Comprable {
    void comprar(Libro libro);
}

public interface Vendible {
    void vender(Libro libro);
}

public interface Moderador {
    void moderarComentario(Comentario comentario);
}

public interface Reportable {
    void generarReporteVentas();
}

public interface GestorUsuarios {
    void gestionarUsuarios();
}

// Comprador solo puede comprar
public class Comprador implements Comprable {
    @Override
    public void comprar(Libro libro) {
       
    }
}

// Vendedor solo puede vender
public class Vendedor implements Vendible {
    @Override
    public void vender(Libro libro) {
        
    }
}

// Administrador tiene todas las capacidades
public class Administrador implements Comprable, Vendible, Moderador, Reportable, GestorUsuarios {
    @Override
    public void comprar(Libro libro) {
       
    }

    @Override
    public void vender(Libro libro) {
        
    }

    @Override
    public void moderarComentario(Comentario comentario) {
        
    }

    @Override
    public void generarReporteVentas() {
        
    }

    @Override
    public void gestionarUsuarios() {
        
    }
}
```