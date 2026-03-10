package co.edu.javeriana.ingsoft.kata.fizzbuzz;



import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FizzBuzzTest {

    @Test
    void testMinDomainRange(){
        //Arrange
        FizzBuzz fizzBuzz = new FizzBuzz();
        int minValue = -1;
        //Act &&
        assertThrows(IllegalArgumentException.class, ()-> fizzBuzz.calculate(minValue));

    }

    @Test
    void testMaxDomainRange(){
        //Arrange
        FizzBuzz fizzBuzz = new FizzBuzz();
        int minValue = 101;
        //Act &&
        assertThrows(IllegalArgumentException.class, ()-> fizzBuzz.calculate(minValue));
    }

    @Test
    void testFizz(){
        //Arrange
        FizzBuzz fizzBuzz = new FizzBuzz();
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
        FizzBuzz fizzBuzz = new FizzBuzz();
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
        FizzBuzz fizzBuzz = new FizzBuzz();
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
        FizzBuzz fizzBuzz = new FizzBuzz();
        int value = 4;
        String expected = "4";
        //Act
        String result = fizzBuzz.calculate(value);
        //Assert
        assertEquals(expected, result);
    }



}