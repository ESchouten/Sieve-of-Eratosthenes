public class Main {

    public static void main(String[] args) throws InterruptedException {
        Manager manager = new Manager(1000, 2, true);
//        Manager manager = new Manager(1000000000, 2, false);
        manager.start();
    }
}
