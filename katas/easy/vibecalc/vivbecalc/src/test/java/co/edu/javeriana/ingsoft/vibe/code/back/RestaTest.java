package co.edu.javeriana.ingsoft.vibe.code.back;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas para la operación Resta")
class RestaTest {

    @Test
    @DisplayName("Restar dos números positivos")
    void testRestaPositivos() {
        Resta resta = new Resta();
        assertEquals(1, resta.aplicar(5, 4));
    }

    @Test
    @DisplayName("Restar números negativos")
    void testRestaNegativos() {
        Resta resta = new Resta();
        assertEquals(-1, resta.aplicar(-2, -1));
    }

    @Test
    @DisplayName("Restar positivo menos negativo")
    void testRestaMixto() {
        Resta resta = new Resta();
        assertEquals(9, resta.aplicar(5, -4));
    }

    @Test
    @DisplayName("Restar cero")
    void testRestaCero() {
        Resta resta = new Resta();
        assertEquals(5, resta.aplicar(5, 0));
    }

    @Test
    @DisplayName("Overflow en resta")
    void testRestaOverflow() {
        Resta resta = new Resta();
        assertThrows(ArithmeticException.class, () -> {
            resta.aplicar(Integer.MIN_VALUE, 1);
        });
    }

    @Test
    @DisplayName("Underflow en resta")
    void testRestaUnderflow() {
        Resta resta = new Resta();
        assertThrows(ArithmeticException.class, () -> {
            resta.aplicar(Integer.MAX_VALUE, -1);
        });
    }

    @Test
    @DisplayName("Verificar nombre de operación")
    void testNombre() {
        Resta resta = new Resta();
        assertEquals("Resta", resta.getNombre());
    }
}
