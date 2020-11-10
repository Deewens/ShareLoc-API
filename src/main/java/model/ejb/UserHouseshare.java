package model.ejb;

import javax.persistence.*;

@Entity
@Table(name = "user_houseshare")
@IdClass(UserHouseshareId.class)
public class UserHouseshare {
    @Id
    @Column(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;
    @Id
    @Column(name = "houseshare_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "houseshare_id", referencedColumnName = "houseshare_id")
    private Houseshare houseshareId;
    private int points;

    public UserHouseshare() {}

    public UserHouseshare(User user, Houseshare houseshareId, int points) {
        this.user = user;
        this.houseshareId = houseshareId;
        this.points = points;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Houseshare getHouseshareId() {
        return houseshareId;
    }

    public void setHouseshareId(Houseshare houseshareId) {
        this.houseshareId = houseshareId;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
