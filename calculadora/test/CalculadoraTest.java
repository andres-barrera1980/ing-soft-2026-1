import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class CalculadoraTest {

    @Test
    public void logaritmoDe100() {
        assertEquals(2, Calculadora.log10(100));
    }

}
