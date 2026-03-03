package co.edu.javeriana.ingsoft.katas.easy.calc.operaciones.base;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ModuloTest {

    private final Modulo modulo = new Modulo();

    @Test
    void realizarOperacionSumaExitosa() {
        assertEquals(5, modulo.realizarOperacion(2, 3));
    }

    @Test
    void realizarOperacionNum1Nulo() {
        assertThrows(IllegalArgumentException.class, () -> modulo.realizarOperacion(null, 5));
    }

    @Test
    void realizarOperacionNum2Nulo() {
        assertThrows(IllegalArgumentException.class, () -> modulo.realizarOperacion(5, null));
    }

    @Test
    void realizarOperacionOverflowMaximo() {
        assertThrows(ArithmeticException.class, () -> modulo.realizarOperacion(Integer.MAX_VALUE, 1));
    }

    @Test
    void realizarOperacionOverflowMinimo() {
        assertThrows(ArithmeticException.class, () -> modulo.realizarOperacion(Integer.MIN_VALUE, -1));
    }
}
