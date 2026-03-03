package co.edu.javeriana.ingsoft.vibe.code.back;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas para la operación Fibonacci")
class FibonacciTest {

    private Fibonacci fibonacci;

    @BeforeEach
    void setUp() {
        fibonacci = new Fibonacci();
    }

    @Test
    @DisplayName("Fibonacci de 0")
    void testFibonacciCero() {
        assertEquals(0, fibonacci.aplicar(0));
    }

    @Test
    @DisplayName("Fibonacci de 1")
    void testFibonacciUno() {
        assertEquals(1, fibonacci.aplicar(1));
    }

    @Test
    @DisplayName("Fibonacci de 2")
    void testFibonacciDos() {
        assertEquals(1, fibonacci.aplicar(2));
    }

    @Test
    @DisplayName("Fibonacci de 5")
    void testFibonacciCinco() {
        assertEquals(5, fibonacci.aplicar(5));
    }

    @Test
    @DisplayName("Fibonacci de 10")
    void testFibonacciDiez() {
        assertEquals(55, fibonacci.aplicar(10));
    }

    @Test
    @DisplayName("Fibonacci de número negativo lanza excepción")
    void testFibonacciNegativo() {
        assertThrows(ArithmeticException.class, () -> {
            fibonacci.aplicar(-5);
        });
    }

    @Test
    @DisplayName("Fibonacci utiliza cache eficientemente")
    void testFibonacciCache() {
        // Primera llamada
        long inicio1 = System.nanoTime();
        fibonacci.aplicar(30);
        long tiempo1 = System.nanoTime() - inicio1;

        // Segunda llamada (debe ser más rápida debido al cache)
        long inicio2 = System.nanoTime();
        fibonacci.aplicar(30);
        long tiempo2 = System.nanoTime() - inicio2;

        // El tiempo2 debería ser significativamente menor
        assertTrue(tiempo2 < tiempo1, "Cache no está funcionando correctamente");
    }

    @Test
    @DisplayName("Fibonacci overflow")
    void testFibonacciOverflow() {
        assertThrows(ArithmeticException.class, () -> {
            fibonacci.aplicar(50); // fib(50) > Integer.MAX_VALUE
        });
    }

    @Test
    @DisplayName("Verificar nombre de operación")
    void testNombre() {
        assertEquals("Fibonacci", fibonacci.getNombre());
    }
}
