## P01_solid_principio  

### Estudiante
- **Nombre completo**: Nicolás Silva García

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [**Sin IA — respuesta propia**] |
| **Modelo específico** | [N/A] |
| **¿Por qué elegiste este LLM?** | [El enunciado la marca como recomendada sin IA y me siento confiado en lo que aprendi de los principios SOLID como para poder resolverla por mi cuenta] |

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

### Pregunta 1
### P.1 ⭐ (3 puntos)

El siguiente método pertenece a la clase `GestorLibro` del módulo de administración de OpenLib Market:

```java
public class GestorLibro {
    public void publicarLibro(Libro libro, Usuario vendedor) {
        // 1. Validar que el libro tenga ISBN, título y autor
        if (libro.getIsbn() == null || libro.getTitulo() == null || libro.getAutor() == null) {
            throw new IllegalArgumentException("Datos del libro incompletos");
        }
        
        // 2. Guardar el libro en la base de datos
        Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/openlib", "admin", "pass");
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO libros (...) VALUES (...)");
        stmt.executeUpdate();
        
        // 3. Generar slug para la URL
        String slug = libro.getTitulo().toLowerCase().replace(" ", "-").replaceAll("[^a-z0-9-]", "");
        libro.setSlug(slug);
        
        // 4. Enviar correo al vendedor
        EmailService email = new EmailService("smtp.openlib.com", 587, "noreply@openlib.com", "pass123");
        email.enviar(vendedor.getEmail(), "Libro publicado", "Tu libro '" + libro.getTitulo() + "' ha sido publicado exitosamente.");
        
        // 5. Registrar en log
        Logger.getLogger("OpenLib").info("Libro publicado: " + libro.getIsbn() + " por " + vendedor.getNombre());
        
        // 6. Indexar para búsqueda
        SearchIndex index = new SearchIndex("elasticsearch.openlib.com:9200");
        index.indexar(libro);
    }
}
```

**Tarea**: Identifica qué principio SOLID se está violando (más de uno puede aplicar, pero enfócate en el principal).

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

No aplica, esta respuesta es mia propia.


#### 2. ¿Qué se puede mejorar?

No aplica, esta respuesta es mia propia.


#### 3. Respuesta final

Se está violando la S del principio de responsabilidad única, ya que la clase esta haciendo 6 cosas diferentes que deberían estar en clases separadas:
1) Validar que el libro tenga ISBN, título y autor
2) Guardar el libro en la base de datos
3) Generar slug para la URL
4) Enviar correo al vendedor
5) Registrar en log
6) Indexar para búsqueda

Esto es una mala practica, ya que si cambia la forma de validar los datos del libro, se deberia cambiar TODO el metodo publicarLibro, haciendo muy engorroso cualquier cambio futuro.

Lo propio seria que cada responsabilidad tenga su propia clase, algo como esto (lo dejo en comentario y pseudocodigo porque no tengo ni idea de como funciona publicarLibro por dentro en este contexto)

class LibroValidator {
    public boolean validar(Libro libro) {
        if (libro.getIsbn() == null || libro.getTitulo() == null || libro.getAutor() == null) {
            throw new IllegalArgumentException("Datos del libro incompletos");
        }
    }
}

class LibroRepository {
    public void guardar(Libro libro) {
        // Guardar en la BD;
        Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/openlib", "admin", "pass");
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO libros (...) VALUES (...)");
        stmt.executeUpdate();
    }
}

class SlugGenerator {
    public String generar(String titulo) {
        //esto ya estaba en el problema entonces lo dejo igual
        return titulo.toLowerCase().replace(" ", "-").replaceAll("[^a-z0-9-]", "");
    }
}

class EmailService {
    public void enviar() {
       EmailService email = new EmailService("smtp.openlib.com", 587, "noreply@openlib.com", "pass123");
        email.enviar(vendedor.getEmail(), "Libro publicado", "Tu libro '" + libro.getTitulo() + "' ha sido publicado exitosamente.");
    }
}

class LoggerService {
    public void log(String mensaje) {
        Logger.getLogger("OpenLib").info("Libro publicado: " + libro.getIsbn() + " por " + vendedor.getNombre());
    }
}

class SearchIndex {
    public void indexar(Libro libro) {
        SearchIndex index = new SearchIndex("elasticsearch.openlib.com:9200");
        index.indexar(libro);
    }
}
