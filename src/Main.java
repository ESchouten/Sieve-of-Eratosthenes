import java.util.concurrent.ExecutionException;

public class Main {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Manager manager = new Manager(1000, 2, true);
//        Manager manager = new Manager(1000000000, 3, true);
        manager.start();
    }
}
