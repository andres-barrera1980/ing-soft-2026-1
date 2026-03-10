package co.edu.javeriana.ingsoft.kata.fizzbuzz;



import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FizzBuzzTest {

    private FizzBuzz fizzBuzz;

    @BeforeEach
    void setUp() {
        fizzBuzz = new FizzBuzz();
    }

    @Test
    void testMinDomainRange(){
        //Arrange
        int minValue = -1;
        //Act && Assert
        assertThrows(IllegalArgumentException.class, ()-> fizzBuzz.calculate(minValue));
    }

    @Test
    void testMaxDomainRange(){
        //Arrange
        int minValue = 101;
        //Act && Assert
        assertThrows(IllegalArgumentException.class, ()-> fizzBuzz.calculate(minValue));
    }

    @Test
    void testFizz(){
        //Arrange
        int value = 27;
        String expected = "Fizz";
        //Act
        String result = fizzBuzz.calculate(value);
        //Assert
        assertEquals(expected, result);
    }

    @Test
    void testBuzz(){
        //Arrange
        int value = 10;
        String expected = "Buzz";
        //Act
        String result = fizzBuzz.calculate(value);
        //Assert
        assertEquals(expected, result);
    }

    @Test
    void testFizzBuzz(){
        //Arrange
        int value = 15;
        String expected = "FizzBuzz";
        //Act
        String result = fizzBuzz.calculate(value);
        //Assert
        assertEquals(expected, result);
    }

    @Test
    void testNoFizzBuzz(){
        //Arrange
        int value = 4;
        String expected = "4";
        //Act
        String result = fizzBuzz.calculate(value);
        //Assert
        assertEquals(expected, result);
    }

}