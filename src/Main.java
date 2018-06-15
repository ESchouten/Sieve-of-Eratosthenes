import java.util.concurrent.ExecutionException;

public class Main {

    public static void main(String[] args) throws Exception {
        int threads = 2;
        int max = 1000;
//        Manager manager = new Manager(1000000000, 3, true);
//        DistributableWorker worker = new DistributableWorker();
//        worker.main(new String[0]);
        Manager manager = new Manager(max, threads, true);
        manager.start();
    }
}
