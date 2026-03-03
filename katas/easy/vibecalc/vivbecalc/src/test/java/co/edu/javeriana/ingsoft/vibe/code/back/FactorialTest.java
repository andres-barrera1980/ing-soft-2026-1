package co.edu.javeriana.ingsoft.vibe.code.back;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas para la operación Factorial")
class FactorialTest {

    @Test
    @DisplayName("Factorial de 0")
    void testFactorialCero() {
        Factorial factorial = new Factorial();
        assertEquals(1, factorial.aplicar(0));
    }

    @Test
    @DisplayName("Factorial de 1")
    void testFactorialUno() {
        Factorial factorial = new Factorial();
        assertEquals(1, factorial.aplicar(1));
    }

    @Test
    @DisplayName("Factorial de 5")
    void testFactorialCinco() {
        Factorial factorial = new Factorial();
        assertEquals(120, factorial.aplicar(5));
    }

    @Test
    @DisplayName("Factorial de 10")
    void testFactorialDiez() {
        Factorial factorial = new Factorial();
        assertEquals(3628800, factorial.aplicar(10));
    }

    @Test
    @DisplayName("Factorial de número negativo lanza excepción")
    void testFactorialNegativo() {
        Factorial factorial = new Factorial();
        assertThrows(ArithmeticException.class, () -> {
            factorial.aplicar(-5);
        });
    }

    @Test
    @DisplayName("Factorial overflow")
    void testFactorialOverflow() {
        Factorial factorial = new Factorial();
        assertThrows(ArithmeticException.class, () -> {
            factorial.aplicar(21); // 21! > Integer.MAX_VALUE
        });
    }

    @Test
    @DisplayName("Verificar nombre de operación")
    void testNombre() {
        Factorial factorial = new Factorial();
        assertEquals("Factorial", factorial.getNombre());
    }
}
