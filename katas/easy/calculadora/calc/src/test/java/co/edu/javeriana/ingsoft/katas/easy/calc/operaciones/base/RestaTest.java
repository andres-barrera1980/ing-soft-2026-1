package co.edu.javeriana.ingsoft.katas.easy.calc.operaciones.base;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RestaTest {

    @Test
    void testRestaBasica() {
        Resta r = new Resta();
        Double resultado = r.realizarOperacion(5.0, 3.0);
        assertEquals(2.0, resultado);
    }

}
