package de.berlin.htw.control;

import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@ApplicationScoped
public class KafkaTopicConfig {

    // Definiert den Namen des Chat-Topics
    public static final String CHAT_TOPIC = "chat";
    // Definiert den Namen des Fibonacci-Topics
    public static final String FIBONACCI_TOPIC = "fibonacci";

    @ConfigProperty(name = "quarkus.profile")
    String activeProfile;

    // Erstellt die Kafka-Topics beim Start der Anwendung
    void onStart(@Observes StartupEvent ev) throws ExecutionException, InterruptedException {
        // Bestimmt den Replikationsfaktor basierend auf dem aktiven Profil
        short replicationFactor = (short) ("test".equals(activeProfile) ? 1 : 3);

        try (AdminClient adminClient = AdminClient.create(kafkaProps())) {
            Set<String> existingTopics = adminClient.listTopics().names().get();
            List<NewTopic> newTopics = new ArrayList<>();

            if (!existingTopics.contains(CHAT_TOPIC)) {
                // Chat-Topic: 2 Partitionen, dynamischer Replikationsfaktor
                newTopics.add(new NewTopic(CHAT_TOPIC, 2, replicationFactor));
            }
            if (!existingTopics.contains(FIBONACCI_TOPIC)) {
                // Fibonacci-Topic: 1 Partition, dynamischer Replikationsfaktor
                newTopics.add(new NewTopic(FIBONACCI_TOPIC, 1, replicationFactor));
            }

            if (!newTopics.isEmpty()) {
                adminClient.createTopics(newTopics).all().get();
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
