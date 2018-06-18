import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Manager {

    private int threads;
    private boolean[] notPrimes;
    private int next = 1;
    private int until;

    private boolean output;

    private ExecutorService executor;

    private Date start;
    private Date end;

    public Manager(int limit, int threads, boolean output) {
        this.notPrimes = new boolean[limit + 1];
        this.until = (int) Math.ceil(Math.sqrt(limit));
        this.output = output;
        this.threads = threads;
    }


    public List<Integer> start() throws InterruptedException {
        this.start = new Date();
        if (threads > 0) {
            this.executor = Executors.newFixedThreadPool(threads);
            Collection<Callable<Object>> workers = new ArrayList<>();
            for (int i = 0; i < threads; i++) {
                workers.add(new Worker(Integer.toString(i), this, notPrimes));
            }
            this.executor.invokeAll(workers);
            this.executor.shutdown();
        } else {
            new Worker("0", this, notPrimes).call();
        }

        this.end = new Date();
        if (output) this.print();

        List<Integer> primes = new ArrayList<>();
        if (until >= 2) primes.add(2);
        for (int i = 3; i < notPrimes.length; i = i + 2) {
            if(!notPrimes[i]) primes.add(i);
        }

        return primes;
    }

    public synchronized int getNext(String worker) {
        while (next < until) {
            next++;
            if (!notPrimes[next]) {
                if (output) System.out.println(worker + ": " + next);
                return next;
            }
        }
        return -1;
    }

    private void print() {
        System.out.println(this.end.getTime() - this.start.getTime() + " ms");
    }
}
