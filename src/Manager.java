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


    public List<Integer> start() throws InterruptedException, ExecutionException {
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
        if (output) this.print();
        return primes;
    }

    private void print() {
        System.out.println(this.end.getTime() - this.start.getTime() + " ms");
    }
}
