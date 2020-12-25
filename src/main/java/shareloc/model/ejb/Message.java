package shareloc.model.ejb;

import jakarta.persistence.*;
import java.util.Date;

@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int messageId;

    @ManyToOne(targetEntity = Houseshare.class)
    private Houseshare houseshare;

    @ManyToOne(targetEntity = User.class)
    private User user;

    private String messageContent;

    private Date messageDate;

    public Message() {}

    public Message(Houseshare houseshare, User user, String messageContent, Date messageDate) {
        this.houseshare = houseshare;
        this.user = user;
        this.messageContent = messageContent;
        this.messageDate = messageDate;
    }

    public int getMessageId() { return messageId; }

    public void setMessageId(int messageId) { this.messageId = messageId; }

    public Houseshare getHouseshare() { return houseshare; }

    public void setHouseshare(Houseshare houseshare) { this.houseshare = houseshare; }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }

    public String getMessageContent() { return messageContent; }

    public void setMessageContent(String messageContent) { this.messageContent = messageContent; }

    public Date getMessageDate() { return messageDate; }

    public void setMessageDate(Date messageDate) { this.messageDate = messageDate; }

}

