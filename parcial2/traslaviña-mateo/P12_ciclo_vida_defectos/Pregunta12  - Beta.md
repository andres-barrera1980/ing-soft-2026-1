# Pregunta P12: Ciclo de vida de defectos

### Estudiante
- **Nombre completo**: Mateo Traslaviña Moreno

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Claude |
| **Modelo específico** | Claude Sonnet 4.6 |
| **¿Por qué elegiste este LLM?** | El ciclo de vida de defectos es un tema con flujos de estado bien definidos que Claude representa eficientemente. Su capacidad para generar diagramas ASCII y tablas bien estructuradas hace que los flujos sean fáciles de entender, y conecta bien los conceptos teóricos con herramientas concretas como Jira. |

---

### Prompt utilizado

```
Eres un QA Lead en el equipo de OpenLib Market, plataforma de compra-venta de libros en Java 21 con Spring Boot 3.x.

Necesito una explicación completa del ciclo de vida de un defecto (bug lifecycle) aplicado a este proyecto.

Cubre:
1. Definición: qué es un defecto y cómo se diferencia de un "error" y un "fallo"
2. Los estados del ciclo de vida (con diagrama de flujo ASCII)
3. Quién tiene responsabilidad en cada estado (QA, Dev, Product Owner, etc.)
4. Severidad vs Prioridad: diferencia y ejemplos concretos de OpenLib Market
5. Un ejemplo completo de un defecto real en OpenLib Market pasando por todos los estados (desde que se reporta hasta que se cierra)
6. Métricas para medir la salud del proceso de gestión de defectos
7. Cómo se integra el bug lifecycle con un tablero Jira/GitHub Issues en un equipo ágil

Formato: markdown con diagrama de flujo, tabla de severidad/prioridad, y el ejemplo completo con el defecto pasando estado por estado.
```

---

