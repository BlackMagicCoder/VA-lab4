package de.berlin.htw.boundary;

import de.berlin.htw.boundary.dto.Message;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

/**
 * @author Alexander Stanik [alexander.stanik@htw-berlin.de]
 * @author Cedrik Tailleur [cedrik.tailleur@htw-berlin.de]
 */
// Definiert diese Klasse als eine von Quarkus verwaltete Bean mit Anwendungs-Gültigkeitsbereich.
@ApplicationScoped
public class MessageProducer {

    // Injiziert einen Emitter, der mit dem Kanal 'chat-producer' verbunden ist.
    // Ein Emitter ist eine SmallRye-spezifische API zum Senden von Nachrichten an einen Kanal.
    @Inject
    @Channel("chat-producer") // Verknüpft den Emitter mit dem in application.properties definierten Kanal.
    Emitter<String> emitter;

    /**
     * Sendet eine gegebene Textnachricht an den Kafka-Topic 'chat'.
     * @param message Der zu sendende Text.
     */
    public void sendMessage(final String message) {
        // Die send-Methode des Emitters leitet die Nachricht an den konfigurierten Kafka-Producer weiter.
        emitter.send(message);
    }

    /**
     * Extrahiert den Inhalt aus einem Message-Objekt und sendet ihn an Kafka.
     * @param message Das Message-Objekt.
     */
    public void sendTweet(final Message message) {
        emitter.send(message.getContent());
    }

    /**
     * Sendet den Inhalt einer Nachricht ebenfalls an Kafka. Die Unterscheidung zu sendTweet ist hier nicht relevant,
     * da der Emitter standardmäßig asynchron arbeitet.
     * @param message Das Message-Objekt.
     */
    public void sendTweetAsync(final Message message) {
        emitter.send(message.getContent());
    }
}
