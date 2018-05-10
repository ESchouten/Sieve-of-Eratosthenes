import java.util.concurrent.Callable;

public class Worker implements Callable<Object> {
    private String id;
    private Manager manager;
    private boolean[] notPrimes;

    public Worker(String id, Manager manager, boolean[] notPrimes) {
        this.id = id;
        this.manager = manager;
        this.notPrimes = notPrimes;
    }

    @Override
    public Object call() {
        int prime = manager.getNext(id);
        while (prime >= 2) {
            int multiplier = 2;
            while (true) {
                try {
                    notPrimes[prime * multiplier] = true;
                } catch (ArrayIndexOutOfBoundsException e) {
                    break;
                }
                multiplier++;
            }
            prime = manager.getNext(id);
        }
        return null;
    }
}
