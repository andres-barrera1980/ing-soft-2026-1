package co.edu.javeriana.ingsoft.katas.easy.calc.operaciones.base;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ValorAbsolutoTest {

    private final OperacionValorAbsoluto valorAbsoluto = new OperacionValorAbsoluto();

    @Test
    void realizarOperacionExitosaPositivo() {
        assertEquals(5, valorAbsoluto.realizarOperacion(5));
    }

    @Test
    void realizarOperacionExitosaNegativo() {
        assertEquals(5, valorAbsoluto.realizarOperacion(-5));
    }

    @Test
    void realizarOperacionCero() {
        assertEquals(0, valorAbsoluto.realizarOperacion(0));
    }

    @Test
    void realizarOperacionNumeroNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> valorAbsoluto.realizarOperacion(null));
    }

    @Test
    void realizarOperacionOverflowMinimo() {
        assertThrows(ArithmeticException.class,
                () -> valorAbsoluto.realizarOperacion(Integer.MIN_VALUE));
    }
}