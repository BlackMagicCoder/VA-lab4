package de.berlin.htw.boundary.dto;

/**
 * @author Alexander Stanik [alexander.stanik@htw-berlin.de]
 */
public class Message {

    private String author;
    
    private String content;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
