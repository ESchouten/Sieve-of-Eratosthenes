import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

public class Worker implements Callable<Object> {
    private String id;
    private int from;
    private int until;
    private boolean[] notPrimes;


    public Worker(String id, int from, int until) {
        this.id = id;
        this.from = from;
        this.until = until;
        this.notPrimes = new boolean[until - from];
    }

    @Override
    public List<Integer> call() {
            int prime = 2;
            int untilSqrt = (int) Math.ceil(Math.sqrt(until));
            while (prime <= untilSqrt) {
                int multiplier = 2;
                while (true) {
                    try {
                        notPrimes[prime * multiplier] = true;
                    } catch (ArrayIndexOutOfBoundsException e) {
                        break;
                    }
                    multiplier++;
                }
                for (int i = prime + 1; i <= untilSqrt; i++) {
                    if (!notPrimes[i]) {
                        prime = i;
                        break;
                    }
                    else if (i == untilSqrt) prime = untilSqrt + 1;
                }
            }
        List<Integer> primes = new ArrayList<>();
        for (int i = 0; i < notPrimes.length; i++) {
            if (!notPrimes[i]) primes.add(i + from);
        }
        return primes;
    }
}
