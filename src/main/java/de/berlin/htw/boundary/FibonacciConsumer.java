package de.berlin.htw.boundary;

import java.math.BigInteger;

import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import org.jboss.logging.Logger;



/**
 * @author Alexander Stanik [alexander.stanik@htw-berlin.de]
 */
// Definiert diese Klasse als eine von Quarkus verwaltete Bean mit Anwendungs-Gültigkeitsbereich.
@ApplicationScoped
public class FibonacciConsumer {

    @Inject
    Logger logger;
    
    /**
     * Diese Methode ist das Herzstück der Fibonacci-Job-Queue.
     * Sie empfängt eine Nachricht, verarbeitet sie und gibt das Ergebnis für die nächste Stufe zurück.
     * @param message Die eingehende Nachricht aus dem 'fibonacci'-Topic, z.B. "0,1".
     * @return Die nächste Nachricht für den 'fibonacci'-Topic, z.B. "1,1".
     */
    // @Incoming verbindet die Methode mit dem 'fibonacci-consumer'-Kanal (liest aus dem Topic).
    @Incoming("fibonacci-consumer")
    // @Outgoing verbindet den Rückgabewert der Methode mit dem 'fibonacci-producer'-Kanal (schreibt in das Topic).
    @Outgoing("fibonacci-producer")
    public String consume(String message) {
        try {
            // Zerlegt die eingehende Nachricht (z.B. "0,1") in zwei Teile.
            String[] parts = message.split(",");
            
            // Überprüft das Nachrichtenformat
            if (parts.length != 2) {
                logger.errorf("Ungültiges Nachrichtenformat: %s. Erwartet wird 'zahl1,zahl2'", message);
                return null; // Verwirft die Nachricht
            }
            
            // Wandelt die Text-Teile in sehr große Zahlen um, um einen Überlauf zu vermeiden.
            BigInteger n0 = new BigInteger(parts[0].trim());
            BigInteger n1 = new BigInteger(parts[1].trim());

            // Berechnet die nächste Fibonacci-Zahl.
            BigInteger next = n0.add(n1);
            
            // Protokolliert die Berechnung für Debugging-Zwecke
            logger.infof("Berechne: %s + %s = %s", n0, n1, next);

            // Gibt das nächste Zahlenpaar als String zurück, das dann automatisch an Kafka gesendet wird.
            return n1 + "," + next;
            
        } catch (NumberFormatException e) {
            logger.errorf("Fehler beim Parsen der Fibonacci-Zahlen aus Nachricht: %s. Fehler: %s", message, e.getMessage());
            return null; // Verwirft die ungültige Nachricht
        } catch (Exception e) {
            logger.error("Unerwarteter Fehler bei der Verarbeitung der Fibonacci-Nachricht", e);
            return null; // Verwirft die Nachricht bei unerwarteten Fehlern
        }
    }
}
