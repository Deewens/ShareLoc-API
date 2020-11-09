package model.ejb;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "achieved_service")
public class AchievedService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "achieved_service_id")
    private int achievedServiceId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", referencedColumnName = "service_id")
    private Service service;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "houseshare_id", referencedColumnName = "houseshare_id")
    private Houseshare houseshare;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_user_id", referencedColumnName = "user_id")
    private User from;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_user_id", referencedColumnName = "user_id")
    private User to;
    private Date date;
    private String picture;
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
