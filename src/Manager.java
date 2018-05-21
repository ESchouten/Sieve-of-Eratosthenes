import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Manager {

    private int threads;
    private List<Integer> primes;
    private List<Integer> sievingPrimes;

    private int until;

    private boolean output;

    private ExecutorService executor;

    private Date start;
    private Date end;

    public Manager(int until, int threads, boolean output) {
        this.primes = new ArrayList<>();
        this.sievingPrimes = new ArrayList<>();
        this.until = until;
        this.output = output;
        this.threads = threads;
    }


    public void start() throws InterruptedException, ExecutionException {
        this.start = new Date();
        int sievingPrimesMax = (int) Math.ceil(Math.sqrt(until));
        sievingPrimes = new Worker("0", 0, sievingPrimesMax).call();
        sievingPrimes = sievingPrimes.subList(2, sievingPrimes.size());
        this.executor = Executors.newFixedThreadPool(threads);
        Collection<Callable<Object>> workers = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            int from = sievingPrimesMax + (until - sievingPrimesMax) / threads * i + 1;
            int to = sievingPrimesMax + (until - sievingPrimesMax) / threads * (i + 1);
            workers.add(new Worker(Integer.toString(i), sievingPrimes, from, to));
        }
        primes.addAll(sievingPrimes);
        List<Future<Object>> futures = this.executor.invokeAll(workers);
        this.executor.shutdown();
        for (Future<Object> list : futures) {
            primes.addAll((List<Integer>) list.get());
        }

        this.end = new Date();
        this.print();
    }

    private void print() {
        System.out.println(this.end.getTime() - this.start.getTime() + " ms");

        if (output) {
            StringJoiner sj = new StringJoiner(", ");
            for (Integer prime : primes) {
                sj.add(prime.toString());
            }
            String primeList = sj.toString();
            String check = "2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101, 103, 107, 109, 113, 127, 131, 137, 139, 149, 151, 157, 163, 167, 173, 179, 181, 191, 193, 197, 199, 211, 223, 227, 229, 233, 239, 241, 251, 257, 263, 269, 271, 277, 281, 283, 293, 307, 311, 313, 317, 331, 337, 347, 349, 353, 359, 367, 373, 379, 383, 389, 397, 401, 409, 419, 421, 431, 433, 439, 443, 449, 457, 461, 463, 467, 479, 487, 491, 499, 503, 509, 521, 523, 541, 547, 557, 563, 569, 571, 577, 587, 593, 599, 601, 607, 613, 617, 619, 631, 641, 643, 647, 653, 659, 661, 673, 677, 683, 691, 701, 709, 719, 727, 733, 739, 743, 751, 757, 761, 769, 773, 787, 797, 809, 811, 821, 823, 827, 829, 839, 853, 857, 859, 863, 877, 881, 883, 887, 907, 911, 919, 929, 937, 941, 947, 953, 967, 971, 977, 983, 991, 997";
            System.out.println(primeList);
            System.out.println(check);
            System.out.println(primeList.equals("2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101, 103, 107, 109, 113, 127, 131, 137, 139, 149, 151, 157, 163, 167, 173, 179, 181, 191, 193, 197, 199, 211, 223, 227, 229, 233, 239, 241, 251, 257, 263, 269, 271, 277, 281, 283, 293, 307, 311, 313, 317, 331, 337, 347, 349, 353, 359, 367, 373, 379, 383, 389, 397, 401, 409, 419, 421, 431, 433, 439, 443, 449, 457, 461, 463, 467, 479, 487, 491, 499, 503, 509, 521, 523, 541, 547, 557, 563, 569, 571, 577, 587, 593, 599, 601, 607, 613, 617, 619, 631, 641, 643, 647, 653, 659, 661, 673, 677, 683, 691, 701, 709, 719, 727, 733, 739, 743, 751, 757, 761, 769, 773, 787, 797, 809, 811, 821, 823, 827, 829, 839, 853, 857, 859, 863, 877, 881, 883, 887, 907, 911, 919, 929, 937, 941, 947, 953, 967, 971, 977, 983, 991, 997"));
        }
    }
}
