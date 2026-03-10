package co.edu.javeriana.ingsoft.kata.fizzbuzz;

import java.io.PrintStream;


public class FizzBuzz {

    public static final int MIN_VALUE = 1;
    public static final int MAX_VALUE = 100;
    public static final int FIZZ_NUMBER = 3;
    public static final int BUZZ_NUMBER = 5;
    public static final String FIZZ = "Fizz";
    public static final String BUZZ = "Buzz";

    public String calculate(int number) {
        if(number < MIN_VALUE || number > MAX_VALUE){
            throw new IllegalArgumentException("El número debe ser mayor o igual a " + MIN_VALUE + " y menor o igual a " + MAX_VALUE);
        }
        String result = "";
        if(number % FIZZ_NUMBER != 0 && number % BUZZ_NUMBER != 0)
            return Integer.toString(number);

        if(number % FIZZ_NUMBER == 0)
            result = FIZZ;

        if(number % BUZZ_NUMBER == 0)
            result = result.concat(BUZZ);

        return result;

    }

    public void print() {
        print(System.out);
    }

    public void print(PrintStream output) {
        for(int i = MIN_VALUE; i <= MAX_VALUE; i++) {
            output.println(calculate(i));
        }
    }
}
