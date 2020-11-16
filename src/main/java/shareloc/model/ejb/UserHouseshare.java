package shareloc.model.ejb;

import javax.persistence.*;

@Entity
@Table(name = "user_houseshare")
public class UserHouseshare {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_houseshare_id")
    private int user_houseshare_id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "houseshare_id", referencedColumnName = "houseshare_id")
    private Houseshare houseshare;
    private int points;

    public UserHouseshare() {}

    public UserHouseshare(int user_houseshare_id, User user, Houseshare houseshare, int points) {
        this.user_houseshare_id = user_houseshare_id;
        this.user = user;
        this.houseshare = houseshare;
        this.points = points;
    }

    public int getUser_houseshare_id() {
        return user_houseshare_id;
    }

    public void setUser_houseshare_id(int user_houseshare_id) {
        this.user_houseshare_id = user_houseshare_id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Houseshare getHouseshare() {
        return houseshare;
    }

    public void setHouseshare(Houseshare houseshare) {
        this.houseshare = houseshare;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
