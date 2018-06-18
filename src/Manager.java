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


    public List<Integer> start() throws Exception {
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
        if (output) this.print();

        return primes;
    }

    private void print() {
        System.out.println(this.end.getTime() - this.start.getTime() + " ms");
    }
}
