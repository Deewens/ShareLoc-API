package shareloc.model.ejb;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import shareloc.model.validation.groups.AchievedServiceConstraints;

import java.io.Serializable;
import java.util.Date;

@Entity
public class AchievedService {
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

    @Column()
    private String picture;

    @NotNull(groups = {AchievedServiceConstraints.UpdateAchievedServiceConstraint.class})
    @Column(nullable = false)
    private Boolean valid;

    public AchievedService() {}

    public AchievedService(Service service, Houseshare houseshare, User from, User to, Date date, String picture, boolean valid) {
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

    @Override
    public String toString() {
        return "AchievedService{" +
                "achievedServiceId=" + achievedServiceId +
                ", service=" + service +
                ", houseshare=" + houseshare +
                ", from=" + from +
                ", to=" + to +
                ", date=" + date +
                ", picture='" + picture + '\'' +
                ", valid=" + valid +
                '}';
    }
}
