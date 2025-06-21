package de.berlin.htw.control;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;

import java.util.Collections;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * @author Cedrik Tailleur [cedrik.tailleur@htw-berlin.de]
 */
@ApplicationScoped
public class KafkaTopicConfig {

    // Definiert den Namen des Chat-Topics
    public static final String CHAT_TOPIC = "chat";
    // Definiert den Namen des Fibonacci-Topics
    public static final String FIBONACCI_TOPIC = "fibonacci";

    // Erstellt die Kafka-Topics beim Start der Anwendung
    void onStart(@Observes StartupEvent ev) throws ExecutionException, InterruptedException {
        try (AdminClient adminClient = AdminClient.create(kafkaProps())) {
            // Überprüft, ob die Topics bereits existieren
            Set<String> existingTopics = adminClient.listTopics().names().get();
            if (!existingTopics.contains(CHAT_TOPIC)) {
                // Erstellt das Chat-Topic mit 2 Partitionen und einer Replication von 1
                adminClient.createTopics(Collections.singletonList(new NewTopic(CHAT_TOPIC, 2, (short) 1))).all().get();
            }
            if (!existingTopics.contains(FIBONACCI_TOPIC)) {
                // Erstellt das Fibonacci-Topic mit 1 Partition und einer Replication von 1
                adminClient.createTopics(Collections.singletonList(new NewTopic(FIBONACCI_TOPIC, 1, (short) 1))).all().get();
            }
        }
    }

    // Konfiguriert die Eigenschaften für den Kafka AdminClient
    private Properties kafkaProps() {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        return props;
    }
}
