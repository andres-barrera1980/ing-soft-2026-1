# Plantilla de entrega — Parcial 2

## Pregunta [XX]: [Título resumido]

### Estudiante
- **Nombre completo**: Nicolas Silva Garcia

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Sin IA — respuesta propia |
| **Modelo específico** | N/A |
| **¿Por qué elegiste este LLM?** | No use ningun LLM, esta respuesta es mia  |

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

### Pregunta 3
### P.3 ⭐ (3 puntos)

OpenLib Market tiene tres tipos de usuarios. El equipo modeló esta jerarquía:

```java
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
```

**Tarea**: Analiza esta jerarquía de clases, señalando qué principios SOLID se violan y proponga un rediseño completo.

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

No aplica, esta respuesta es mia propia.



#### 2. ¿Qué se puede mejorar?

No aplica, esta respuesta es mia propia.



#### 3. Respuesta final

Se estan violando la I y la L de Solid

La I (interface segregation principle): la interfaz de Usuario tiene metodos que dependen de la clase de usuario que la implemente (comprar, vender, moderar, generar reportes, gestionar usuarios). Lo propio seria separar esta interfaz en varias interfaces mas pequenas y especificas para cada funcionalidad del usuario.

La L (Liskov Substitution principle): Se esta haciendo Vendedor extends Comprador, pero un Vendedor no puede comprar. Por tanto no se puede usar un Vendedor en lugar de un Comprador. Tambien se esta haciendo Administrador extends Vendedor, pero un Administrador no puede vender. Por tanto no se puede usar un Administrador en lugar de un Vendedor. Hay que asignarle los modulos de interfaz apropiados a cada uno de los roles

El rediseño que propongo divide la interfaz gorda del usuario en modulos pequeños para todas sus 
tareas a la vez que corrige la violacion de Liskov, corrigiendo ambas violaciones a los principios SOLID.

```java
public interface IComprador {
    void comprar(Libro libro);
}

public interface IVendedor {
    void vender(Libro libro);
}

public interface IModerador {
    void moderarComentario(Comentario comentario);
}

public interface IGestorAdministrativo {
    void generarReporteVentas();
    void gestionarUsuarios();
}
public class ClienteComprador implements IComprador {
    @Override
    public void comprar(Libro libro) {
    }
}

public class ClienteVendedor implements IVendedor {
    @Override
    public void vender(Libro libro) {
    }
}

//le mandamos al admin unicamente los comportamientos deseados en vez de todos los comportamientos disponibles del vendedor y comprador, ademas de los comportamientos exclusivos del administrador.
public class AdministradorSistema implements IComprador, IVendedor, IModerador, IGestorAdministrativo {
    
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