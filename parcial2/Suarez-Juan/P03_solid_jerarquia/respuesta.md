# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [03]: Principios SOLID usuarios

### Estudiante
- **Nombre completo**: Juan Pablo Suarez Moreno



### Análisis crítico de la respuesta

#### 1. Conclusion

Considero que el codigo proporcionado esta rompiendo 3 de los Principios SOLID, el mas evidente es el principio de responsabilidad Unica gracias que las clases intentan manejar multiples responsabilidades y reglas de negocio al mismo tiempo a pesar de no tener acceso o permiso. Segregacion de interfazes, el ejemplo puede ser la interfaz Usuario que acumula responsabilidades distinatas en una sola cambiando la logica del negocio entre todas las clases que hereda. Por ultimo pero no menos importante la Likov sustitucion, mas que todo por que las sublases adyacentes, un ejemplo evidente, comprador que es un Usuarios, al pedirle que venda un libro como aparece explota la logica del sistema y lanza error, lo mismo con vendedor que "extends" de comprador al pedir que compre.

#### 2. Refactorizacion

```
public interface UsuarioComprador {
    void comprar(Libro libro);
}

public interface UsuarioVenedor {
    void vender(Libro libro);
}

public interface UsuarioAdmin {
    void gestionarUsuarios();
    void generarReporteVentas();
    void moderarComentario(Comentario comentario);
}

public class Comprador implements UsuarioComprador {
    @Override
    void comprar(Libro libro){ //Codigo }
}

public class Vendedor implements UsuarioVenedor {
    @Override
    void vender(Libro libro){ //Codigo }
}

public class Admin implements UsuarioAdmin {
    @Override
    void gestionarUsuarios(){ //Codigo }
    @Override
    void generarReporteVentas(){ //Codigo }
    @Override
    void moderarComentario(Comentario comentario){ //Codigo }
}
```

#### Considero que la refactorizacion puede mejorar ya que a pesar de solucionar los errores por llamadas a funciones sin logica del negocio y crear interfaces necesesarias para separar la logica del negocio esta refactorizacion cierra el comportamiento de las clases o usuarios de Market Lib unicamente a las interfaces, una posible solucion podria ser usar interfaces por responsabilidades aun que expandiria la creacion de estas.