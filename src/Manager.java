import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Manager {

    private static String url = "tcp://localhost:61616";
    private static String subjectTo = "eratosthenes1";
    private static String subjectFrom = "eratosthenes2";

    private int threads;
    private List<Integer> primes;
    private List<Integer> sievingPrimes;

    private int until;

    private boolean output;

    private Date start;
    private Date end;

    public Manager(int until, int threads, boolean output) {
        this.primes = new ArrayList<>();
        this.sievingPrimes = new ArrayList<>();
        this.until = until;
        this.output = output;
        this.threads = threads;
    }


    public void start() throws Exception {
        this.start = new Date();

        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        Connection connection = connectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination destination_toQueue = session.createQueue(subjectTo);
        MessageProducer producer = session.createProducer(destination_toQueue);

        int sievingPrimesMax = (int) Math.ceil(Math.sqrt(until));
        sievingPrimes = new Worker("0", 0, sievingPrimesMax).call();
        sievingPrimes = sievingPrimes.subList(2, sievingPrimes.size());
        StringJoiner sj = new StringJoiner(", ");
        for (Integer prime : sievingPrimes) {
            sj.add(prime.toString());
        }
        String sievingPrimesString = sj.toString();
        for (int i = 0; i < threads; i++) {
            int from = sievingPrimesMax + (until - sievingPrimesMax) / threads * i + 1;
            int to = sievingPrimesMax + (until - sievingPrimesMax) / threads * (i + 1);

            String stringForConsumer = i + ";" + sievingPrimesString + ";" + from + ";" + to;
            TextMessage messageTo = session.createTextMessage(stringForConsumer);
            producer.send(messageTo);
        }
        primes.addAll(sievingPrimes);

        Destination destination = session.createQueue(subjectFrom);
        MessageConsumer consumer = session.createConsumer(destination);
        String[] receivedPrimes = new String[threads];
        for (int i = 0; i < threads; i++) {
            Message message = consumer.receive();
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                String[] strings = textMessage.getText().split(";");
                receivedPrimes[Integer.parseInt(strings[0])] = strings[1];
            }
        }
        connection.close();

        for (String p : receivedPrimes) {
            primes.addAll(Arrays.stream(p.split(", ")).map(Integer::parseInt).collect(Collectors.toList()));
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
