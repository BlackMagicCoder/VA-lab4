package de.berlin.htw.control;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.jboss.logging.Logger;
import io.quarkus.scheduler.Scheduled;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

/**
 * @author Alexander Stanik [alexander.stanik@htw-berlin.de]
 */
@ApplicationScoped
public class ChatController {
	
	@Inject
	Logger logger;

	@Inject
	MessageGenerator generator;
	
	@Inject
	@Channel("chat-producer")
	Emitter<String> kafkaEmitter;
	
    public String getTweet() {
        final String tweet = generator.generateTweet();
        logger.info("new tweet: " + tweet);
        return tweet;
    }
    
    @Scheduled(every="10s")     
    void produceTweet() {
        final String tweet = this.getTweet();
        // sendet die Nachricht an Kafka
        kafkaEmitter.send(tweet);
    }

}