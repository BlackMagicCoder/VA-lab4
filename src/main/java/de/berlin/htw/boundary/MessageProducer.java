package de.berlin.htw.boundary;

import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import de.berlin.htw.boundary.dto.Message;

/**
 * @author Alexander Stanik [alexander.stanik@htw-berlin.de]
 */
@Singleton

public class MessageProducer {

    @Inject
    private Event<Message> twitterChannel;

    public void sendMessage(final String message) {
        final Message tweet = new Message();
        tweet.setContent(message);
        sendTweet(tweet);
    }
    
    public void sendTweet(final Message message) {
        twitterChannel.fire(message);
    }
    
    public void sendTweetAsync(final Message message) {
        twitterChannel.fireAsync(message);
    }
    
}