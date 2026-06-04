# Pregunta P13: Integración — Refactoring Notificador

### Estudiante
- **Nombre completo**: Mateo Traslaviña Moreno

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Claude |
| **Modelo específico** | Claude Sonnet 4.6 |
| **¿Por qué elegiste este LLM?** | Claude tiene excelente razonamiento para identificar problemas de integración y acoplamiento en código Java. Para refactorings que involucran patrones de diseño (Observer, Strategy, Facade), Claude propone soluciones estructuradas y genera código Spring Boot idiomático con inyección de dependencias correcta. |

---

### Prompt utilizado

```
Eres un ingeniero de software senior revisando código del proyecto OpenLib Market, plataforma de compra-venta de libros en Java 21 con Spring Boot 3.x.

Analiza el siguiente servicio de notificaciones del módulo de integración:

```java
@Service
public class NotificadorPedido {
    
    public void notificarPedidoCreado(Pedido pedido) {
        // Notificar por email
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.openlib.com");
        props.put("mail.smtp.port", "587");
        Session session = Session.getInstance(props);
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress("noreply@openlib.com"));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(pedido.getComprador().getEmail()));
        message.setSubject("Tu pedido #" + pedido.getId() + " fue creado");
        message.setText("Hola " + pedido.getComprador().getNombre() + ", tu pedido está en camino.");
        Transport.send(message);
        
        // Notificar por SMS
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.smsprovider.com/send"))
            .POST(HttpRequest.BodyPublishers.ofString(
                "{\"to\":\"" + pedido.getComprador().getTelefono() + "\",\"text\":\"Pedido #" + pedido.getId() + " creado\"}"))
            .header("Authorization", "Bearer hardcoded_sms_token_12345")
            .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        
        // Notificar al vendedor por email
        MimeMessage vendedorMsg = new MimeMessage(session);
        vendedorMsg.setFrom(new InternetAddress("noreply@openlib.com"));
        vendedorMsg.addRecipient(Message.RecipientType.TO, new InternetAddress(pedido.getVendedor().getEmail()));
        vendedorMsg.setSubject("Nuevo pedido de tu libro");
        vendedorMsg.setText("Tienes un nuevo pedido #" + pedido.getId());
        Transport.send(vendedorMsg);
        
        // Registrar en sistema externo de analytics
        HttpClient analyticsClient = HttpClient.newHttpClient();
        HttpRequest analyticsRequest = HttpRequest.newBuilder()
            .uri(URI.create("https://analytics.openlib.com/events"))
            .POST(HttpRequest.BodyPublishers.ofString("{\"event\":\"order_created\",\"orderId\":\"" + pedido.getId() + "\"}"))
            .build();
        analyticsClient.send(analyticsRequest, HttpResponse.BodyHandlers.ofString());
    }
}
```

Necesito que:
1. Identifiques todos los problemas de este código (no solo SOLID — incluye problemas de integración, seguridad, testabilidad)
2. Propongas un refactoring usando el patrón Observer/Event-Driven apropiado para Spring Boot
3. Generes el código refactorizado completo con: eventos de dominio, listeners, interfaces para cada canal de notificación
4. Expliques cómo este refactoring mejora la testabilidad (incluye ejemplo de test con mocks)

Restricciones: Java 21, Spring Boot 3.x, @EventListener de Spring, inyección de dependencias. No uses frameworks externos de mensajería (sin Kafka/RabbitMQ para este ejemplo).

Formato: markdown con secciones "Diagnóstico", "Problemas identificados", "Refactoring propuesto", "Código final", "Tests".
```

---

