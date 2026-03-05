package co.edu.javeriana.ingsoft.katas.easy.calc.operaciones.base;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MultiplicacionTest {
        private final Multiplicacion multiplicacion= new Multiplicacion();

        @Test
        void realizarOperacionMultiplicacionExitosa() {
            assertEquals(5, multiplicacion.realizarOperacion(2, 3));
        }
}
