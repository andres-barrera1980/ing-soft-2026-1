# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta 03: SOLID - Jerarquía de usuarios

### Estudiante
- **Nombre completo**: Federico Restrepo

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Gemini |
| **Modelo específico** | Gemini 3.1 Pro (High) |
| **¿Por qué elegiste este LLM?** | Elegí usar este LLM (ignorando el bono) para poder hacer un análisis profundo y corregir los errores que comete la IA al interpretar el Principio de Sustitución de Liskov. |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```text
Actúa como un Arquitecto de Software Senior. 
Contexto: Estamos desarrollando el sistema de roles de OpenLib Market. El equipo actual modeló la siguiente jerarquía de usuarios:

public interface Usuario {
    void comprar(Libro libro);
    void vender(Libro libro);
    void moderarComentario(Comentario comentario);
    void generarReporteVentas();
    void gestionarUsuarios();
}

public class Comprador implements Usuario {
    public void comprar(Libro libro) { /* implementación */ }
    public void vender(Libro libro) { throw new UnsupportedOperationException("Un comprador no puede vender"); }
    public void moderarComentario(Comentario c) { throw new UnsupportedOperationException("No autorizado"); }
    public void generarReporteVentas() { throw new UnsupportedOperationException("No aplica"); }
    public void gestionarUsuarios() { throw new UnsupportedOperationException("No autorizado"); }
}

public class Vendedor extends Comprador {
    @Override
    public void comprar(Libro libro) { throw new UnsupportedOperationException("Un vendedor no compra"); }
    @Override
    public void vender(Libro libro) { /* implementación */ }
}

public class Administrador extends Vendedor {
    @Override
    public void comprar(Libro libro) { /* un admin sí puede comprar */ }
    // hereda vender() de Vendedor, implementa el resto
}

Tarea: Analiza esta jerarquía señalando qué principios SOLID se están violando y propón un rediseño en Java.
```

---

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**
**Análisis de la jerarquía actual**
El código actual viola gravemente dos principios SOLID:
1. **Principio de Segregación de Interfaces (ISP)**: La interfaz `Usuario` es muy grande (*fat interface*) y obliga a las clases a implementar métodos que no usan.
2. **Principio de Sustitución de Liskov (LSP)**: Al lanzar `UnsupportedOperationException`, las clases hijas rompen el contrato de la clase padre. Un `Vendedor` no puede sustituir a un `Comprador` porque lanza una excepción al intentar `comprar()`.

**Rediseño propuesto**
Para solucionarlo, debemos dividir las responsabilidades en interfaces pequeñas (ISP) y utilizar la composición múltiple en lugar de la herencia jerárquica para evitar violar el LSP:

```java
// Interfaces segregadas por rol
public interface IComprador { void comprar(Libro libro); }
public interface IVendedor { void vender(Libro libro); void generarReporteVentas(); }
public interface IAdministrador { void moderarComentario(Comentario c); void gestionarUsuarios(); }

// Las clases concretas implementan solo lo que necesitan sin heredar unas de otras
public class ClienteBasico implements IComprador {
    public void comprar(Libro libro) { /* implementación */ }
}

public class PerfilVendedor implements IComprador, IVendedor {
    public void comprar(Libro libro) { /* implementación */ }
    public void vender(Libro libro) { /* implementación */ }
    public void generarReporteVentas() { /* implementación */ }
}

public class AdministradorSistema implements IComprador, IVendedor, IAdministrador {
    // Implementa todos los métodos sin arrastrar excepciones ni herencias frágiles
}
```

---

### Análisis crítico de la respuesta
El prompt funcionó bien porque pegué el código completo de la jerarquía y le asigné el rol de Arquitecto Senior, lo que lo orientó a dar respuestas más técnicas. Eso permitió que identificara ISP y LSP directamente.
Lo que le faltó fue más grave que lo que acertó. Primero, ignoró OCP: si mañana entra un Auditor, toca modificar la interfaz Usuario entera. Segundo, mencionó superficialmente el problema de Vendedor extends Comprador pero no explicó el impacto real: un método procesarCompra(Comprador c) puede recibir un Vendedor en tiempo de ejecución y explotar con UnsupportedOperationException sin que el compilador avise nada. Tercero, en su rediseño asumió que un vendedor también puede comprar sin cuestionarlo, cuando eso es una decisión de negocio que debió preguntar.
La solución correcta es segregar Usuario en interfaces por capacidad real, Comprador, Vendedor, Moderador, GestorUsuarios, y que cada clase implemente solo lo que necesita. Así se elimina la herencia rota, cada rol queda aislado y agregar nuevos tipos de usuario no toca el código existente.
En el prompt debí pedir explícitamente que cubriera todos los principios SOLID y que justificara las decisiones de negocio antes de asumir qué puede hacer cada rol.