package co.edu.javeriana.ingsoft.kata.fizzbuzz;


public class FizzBuzz {

    public static final Integer MIN_VALUE = 1;
    public static final Integer MAX_VALUE = 100;
    public static final Integer FIZZ_NUMBER = 3;
    public static final Integer BUZZ_NUMBER = 5;
    public static final String FIZZ = "Fizz";

    public String calculate(int number) {
        if(number < MIN_VALUE || number > MAX_VALUE){
            throw new IllegalArgumentException("El número debe ser mayor o igual a cero y menor o igual a 100");
        }
        String result = "";
        if(number % FIZZ_NUMBER != 0 && number % BUZZ_NUMBER != 0)
            return Integer.toString(number);

        //fizz
        if(number % FIZZ_NUMBER == 0)
            result = FIZZ;
        //buzz
        if(number % BUZZ_NUMBER ==0)
            result = result.concat("Buzz");

        return result;

    }

    public void print() {
        for(int i = MIN_VALUE; i <= MAX_VALUE; i++) {
            System.out.println(calculate(i));
        }
    }
}
