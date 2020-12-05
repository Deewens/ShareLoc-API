package shareloc.model.ejb;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
public class AchievedService implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int achievedServiceId;
    @ManyToOne
    @JoinColumn(nullable = false)
    private Service service; // Il peut y avoir un seul service par ligne
    @ManyToOne
    @JoinColumn(nullable = false)
    private Houseshare houseshare; // Il peut y avoir un seul houeshare par ligne
    @ManyToOne
    @JoinColumn(nullable = false)
    private User from;
    @ManyToOne
    @JoinColumn(nullable = false)
    private User to;

    @Column(nullable = false)
    private Date date;
    @Column(nullable = false)
    private String picture;
    @Column(nullable = false)
    private boolean valid;

    public AchievedService() {}

    public AchievedService(int achievedServiceId, Service service, Houseshare houseshare, User from, User to, Date date, String picture, boolean valid) {
        this.achievedServiceId = achievedServiceId;
        this.service = service;
        this.houseshare = houseshare;
        this.from = from;
        this.to = to;
        this.date = date;
        this.picture = picture;
        this.valid = valid;
    }

    public int getAchievedServiceId() {
        return achievedServiceId;
    }

    public void setAchievedServiceId(int achievedServiceId) {
        this.achievedServiceId = achievedServiceId;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public Houseshare getHouseshare() {
        return houseshare;
    }

    public void setHouseshare(Houseshare houseshare) {
        this.houseshare = houseshare;
    }

    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public User getTo() {
        return to;
    }

    public void setTo(User to) {
        this.to = to;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
