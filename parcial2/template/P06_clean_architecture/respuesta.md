# Pregunta P06: Clean Architecture — Capas y dependencia ⭐

### Estudiante
- **Nombre completo**: Mateo Traslaviña Moreno

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Sin IA — respuesta propia |
| **Modelo especifico** | N/A |
| **¿Por que elegiste este LLM?** | Esta pregunta es conceptual y la estudie directamente en clase a traves de las presentaciones sobre Clean Architecture de Robert C. Martin. Tengo criterio propio para explicar las capas, la regla de dependencia y la inversion de dependencia aplicada. El bono del +20% por responder sin IA hace que valga la pena responder desde mi conocimiento. |

---

## Clean Architecture: Capas, Regla de Dependencia y Aplicacion a OpenLib Market

### Las cuatro capas concentricas

Clean Architecture organiza el sistema en capas concentricas, donde la capa mas interna es la mas estable y la mas externa es la mas volatil:

**1. Entities (nucleo — mas estable)**  
Contienen las reglas de negocio empresariales — logica que existiria incluso si no hubiera software. Son POJOs puros sin dependencias de frameworks.  
*En OpenLib Market*: `Libro`, `Usuario`, `Orden`, `CarritoDeCompras`, `Pago`. Estas clases contienen invariantes de negocio (un libro no puede tener precio negativo, una orden no puede tener 0 items).

**2. Use Cases (Application Layer)**  
Contienen las reglas de negocio de la aplicacion — los casos de uso especificos que el sistema ejecuta. Orquestan las entidades.  
*En OpenLib Market*: `PublicarLibroUseCase`, `ProcesarPagoUseCase`, `AgregarItemAlCarritoUseCase`, `BuscarLibroUseCase`. Cada use case tiene una unica responsabilidad y se expresa como una interfaz o clase con un solo metodo `execute()`.

**3. Interface Adapters (Adaptadores de Interfaz)**  
Convierten datos del formato conveniente para use cases/entities al formato conveniente para frameworks externos (DB, web, etc.) y viceversa. Aqui viven Controllers, Presenters, y Gateways.  
*En OpenLib Market*: `LibroController` (REST), `LibroJpaGateway` (implementa `LibroRepository` de Use Cases), `LibroMapper` (convierte entre `LibroEntity` JPA y `Libro` del dominio), `PagoResponseMapper`.

**4. Frameworks & Drivers (mas volatil — exterior)**  
Detalles: Spring Boot, JPA/Hibernate, PostgreSQL, JavaFX, Elasticsearch. Son herramientas que usamos, no parte del negocio.  
*En OpenLib Market*: Configuracion de Spring, `application.yml`, la clase `main`, drivers JDBC, clientes HTTP de APIs de pago externas.

### La Regla de Dependencia

**Las dependencias en el codigo fuente solo pueden apuntar hacia adentro.** Nada en una capa interna puede conocer nada de una capa externa.

```
Frameworks → Interface Adapters → Use Cases → Entities
(la flecha indica "depende de")
```

Concretamente:
- `LibroController` (Adapter) puede depender de `PublicarLibroUseCase` (Use Case) ✅
- `PublicarLibroUseCase` NO puede importar `LibroController` ❌
- `Libro` (Entity) NO puede importar nada de Spring (`@Entity`, `@Component`) ❌
- `LibroJpaGateway` (Adapter) puede depender de `LibroRepository` (interfaz en Use Cases) ✅

### Inversión de Dependencia en los límites entre capas

El problema: un Use Case necesita guardar un libro en base de datos, pero no puede depender de JPA (capa externa). Solución: **el Use Case define una interfaz** (puerto), y la capa externa la implementa (adaptador).

```java
// En la capa Use Cases (interna):
public interface LibroRepository {  // Puerto de salida
    void guardar(Libro libro);
    Optional<Libro> buscarPorIsbn(String isbn);
}

// Caso de uso no sabe si es JPA, MongoDB o en memoria:
public class PublicarLibroUseCase {
    private final LibroRepository libroRepository; // depende de interfaz, no implementación
    
    public void execute(PublicarLibroCommand cmd) {
        Libro libro = new Libro(cmd.isbn(), cmd.titulo(), cmd.autor());
        libroRepository.guardar(libro); // llama a la interfaz
    }
}

// En la capa Interface Adapters (externa):
@Repository
public class LibroJpaGateway implements LibroRepository { // implementa el puerto
    @Autowired private LibroJpaRepository jpaRepo;
    @Autowired private LibroMapper mapper;
    
    @Override
    public void guardar(Libro libro) {
        LibroEntity entity = mapper.toEntity(libro);
        jpaRepo.save(entity);
    }
}
```

El Use Case apunta hacia adentro (a la interfaz que él mismo define). La implementación JPA apunta hacia adentro (implementa esa interfaz). La flecha de dependencia apunta al revés de lo esperado — de ahí el nombre "inversión".

### Comparación con Arquitectura en Capas Tradicional

| Aspecto | Arquitectura en Capas Tradicional | Clean Architecture |
|---|---|---|
| Flujo de dependencias | Presentation → Business → Data | Todas apuntan hacia Entities |
| Acoplamiento | Business Layer depende de Data Layer | Use Cases dependen de interfaces propias |
| Testabilidad | Difícil sin DB real | Use Cases testables con mocks de repositorio |
| Cambio de BD | Requiere modificar Business Layer | Solo cambia la implementación del Gateway |
| Frameworks | Permean todas las capas | Confinados a la capa exterior |

**Diferencia clave**: En arquitectura en capas tradicional, la lógica de negocio está en el medio y depende de la capa de datos. Esto crea acoplamiento inevitable: cambiar de PostgreSQL a MongoDB requiere modificar la capa de negocio. En Clean Architecture, la lógica de negocio (Use Cases + Entities) está completamente aislada — cambiar la BD es un detalle de implementación que solo afecta la capa de adaptadores.

### Error conceptual común

Mucha gente confunde Clean Architecture con "tener paquetes separados". Tener `com.openlib.controller`, `com.openlib.service` y `com.openlib.repository` NO es Clean Architecture si el `service` importa clases de JPA (`@Entity`, `@Column`). La separación debe ser de **dependencias en el código fuente**, no solo de paquetes.
