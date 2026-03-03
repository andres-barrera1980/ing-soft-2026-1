package co.edu.javeriana.ingsoft.katas.easy.calc.operaciones.base;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SumaTest {

    private final Suma suma = new Suma();

    @Test
    void realizarOperacionSumaExitosa() {

        assertEquals(5, suma.realizarOperacion(2, 3));
    }

    @Test
    void realizarOperacionNum1Nulo() {
        assertThrows(IllegalArgumentException.class, () -> suma.realizarOperacion(null, 5));
    }

    @Test
    void realizarOperacionNum2Nulo() {
        assertThrows(IllegalArgumentException.class, () -> suma.realizarOperacion(5, null));
    }

    @Test
    void realizarOperacionOverflowMaximo() {
        assertThrows(ArithmeticException.class, () -> suma.realizarOperacion(Integer.MAX_VALUE, 1));
    }

    @Test
    void realizarOperacionOverflowMinimo() {
        assertThrows(ArithmeticException.class, () -> suma.realizarOperacion(Integer.MIN_VALUE, -1));
    }
}
