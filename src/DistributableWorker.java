import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class DistributableWorker {

    private static String url = "tcp://localhost:61616";
    private static String subjectFrom = "eratosthenes1";
    private static String subjectTo = "eratosthenes2";

    public static void main(String[] args) throws Exception {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        Connection connection = connectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination destination_fromQueue = session.createQueue(subjectFrom);
        MessageConsumer consumer = session.createConsumer(destination_fromQueue);
        Message message = consumer.receive();

        if (message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            String[] strings = textMessage.getText().split(";");

            String id = strings[0];
            List<Integer> providedPrimes = Arrays.stream(strings[1].split(", ")).map(Integer::parseInt).collect(Collectors.toList());
            int from = Integer.parseInt(strings[2]);
            int until = Integer.parseInt(strings[3]);
            boolean[] notPrimes = new boolean[until - from];

            for (Integer prime : providedPrimes) {
                int multiplier = (int) Math.ceil(((double) from / (double) prime));
                if (multiplier == 1) multiplier++;
                for (int i = multiplier; i < until; i++) {
                    try {
                        notPrimes[prime * i - from] = true;
                    } catch (ArrayIndexOutOfBoundsException e) {
                        break;
                    }
                }
            }

            List<Integer> primes = new ArrayList<>();
            for (int i = 0; i < notPrimes.length; i++) {
                if (!notPrimes[i]) primes.add(i + from);
            }

            Destination destination_toQueue = session.createQueue(subjectTo);
            MessageProducer producer = session.createProducer(destination_toQueue);
            StringJoiner sj = new StringJoiner(", ");
            for (Integer prime : primes) {
                sj.add(prime.toString());
            }
            TextMessage messageTo = session.createTextMessage(id + ";" + sj.toString());
            producer.send(messageTo);
        }


        connection.close();
    }
}
