import java.io.Serializable;

public class Message implements Serializable {
    private String sender;
    private String message;
    private byte[] media; // If multimedia, store bytes here.

    public Message(String sender, String message) {
        this.sender = sender;
        this.message = message;
        this.media = null;
    }

    public Message(String sender, byte[] media) {
        this.sender = sender;
        this.media = media;
        this.message = null;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public byte[] getMedia() {
        return media;
    }
}
