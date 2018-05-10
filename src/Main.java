public class Main {

    public static void main(String[] args) throws InterruptedException {
//        Manager manager = new Manager(2000000000, 3, false);
        Manager manager = new Manager(1000, 2, true);
        manager.start();
    }
}
