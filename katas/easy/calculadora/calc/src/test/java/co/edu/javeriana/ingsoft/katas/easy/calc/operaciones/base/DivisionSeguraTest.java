package co.edu.javeriana.ingsoft.katas.easy.calc.operaciones.base;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DivisionSeguraTest {
    private final DivisionSegura division = new DivisionSegura();
    @Test
    void divisionExacta() {
        assertEquals(2.0, division.dividir(4, 2));
    }
    @Test
    void divisionInexacta() {
        assertEquals(2.5, division.dividir(5, 2));
    }

    @Test
    void divisionPorCeroDebeLanzarExcepcion() {
        assertThrows(ArithmeticException.class, () -> {division.dividir(5, 0);
        });
    }

    @Test
    void valoresFueraDeRangoDebenLanzarExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> {
            division.dividir(Integer.MAX_VALUE + 1, 1);
        });
    }
}
