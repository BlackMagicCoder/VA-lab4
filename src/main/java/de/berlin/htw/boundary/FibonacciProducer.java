package de.berlin.htw.boundary;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

/**
 * @author Alexander Stanik [alexander.stanik@htw-berlin.de]
 */
@ApplicationScoped
public class FibonacciProducer {

    /**
     * Injiziert einen Emitter, der mit dem Kanal 'fibonacci-producer' verbunden ist.
     * Ein Emitter ist eine SmallRye-spezifische API zum Senden von Nachrichten an einen Kanal.
     * Der Kanal selbst ist in 'application.properties' konfiguriert.
     */
    @Inject
    @Channel("fibonacci-producer")
    Emitter<String> emitter;

    /**
     * Diese Methode wird einmal beim Start der Anwendung aufgerufen (@Observes StartupEvent).
     * Sie dient dazu, die unendliche Fibonacci-Berechnung zu initialisieren.
     * @param ev Das StartupEvent, das von Quarkus beim Start ausgelöst wird.
     */
    void onStart(@Observes StartupEvent ev) {
        // Sendet das erste Fibonacci-Tupel "0,1" an den 'fibonacci'-Topic.
        // Dies ist der Startpunkt für den FibonacciConsumer, der die Kette aufnimmt,
        // die nächste Zahl berechnet und das Ergebnis wieder in denselben Topic schreibt.
        emitter.send("0,1");
    }
}
