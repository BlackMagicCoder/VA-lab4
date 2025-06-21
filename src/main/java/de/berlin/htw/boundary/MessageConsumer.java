package de.berlin.htw.boundary;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import jakarta.inject.Singleton;

import de.berlin.htw.boundary.dto.Message;

/**
 * @author Alexander Stanik [alexander.stanik@htw-berlin.de]
 */
// Definiert diese Klasse als Singleton-Bean, es wird also nur eine Instanz davon geben.
@Singleton
public class MessageConsumer {

    // Eine thread-sichere Warteschlange, um die von Kafka empfangenen Nachrichten zwischenzuspeichern.
    // Die Kapazität ist auf 50 Nachrichten begrenzt.
    BlockingQueue<Message> queue = new LinkedBlockingQueue<>(50);

    /**
     * Diese Methode wird automatisch aufgerufen, wenn eine neue Nachricht im Kafka-Topic 'chat' ankommt.
     * @param messageContent Der Inhalt der Nachricht aus Kafka.
     */
    // Die @Incoming-Annotation verbindet diese Methode mit dem 'chat-consumer'-Kanal aus den application.properties.
    @Incoming("chat-consumer")
    public void consume(String messageContent) {
        try {
            // Erstellt ein neues Message-Objekt für die interne Weiterverarbeitung.
            Message message = new Message();
            message.setContent(messageContent);
            // Fügt die Nachricht in die Warteschlange ein. Blockiert, falls die Queue voll ist.
            queue.put(message);
        } catch (InterruptedException e) {
            // Stellt den Interrupted-Status des Threads wieder her, falls er unterbrochen wurde.
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }

    /**
     * Holt die älteste Nachricht aus der Warteschlange.
     * Diese Methode wird vom ChatResource aufgerufen, um Nachrichten an den Client zu senden.
     * @return Der Inhalt der Nachricht.
     * @throws InterruptedException wenn der Thread während des Wartens unterbrochen wird.
     */
    public String get() throws InterruptedException {
        // Entnimmt die nächste Nachricht. Blockiert, falls die Queue leer ist.
        return queue.take().getContent();
    }
}
