package shareloc.model.ejb;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Service implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int serviceId;
    @ManyToOne(targetEntity = Houseshare.class)
    private Houseshare houseshare; // Co-location du service
    @Column(nullable = false)
    private String title;
    private String description;
    @Column(nullable = false)
    private int cost;

    public Service() {}

    public Service(int serviceId, Houseshare houseshare, String title, String description, int cost) {
        this.serviceId = serviceId;
        this.houseshare = houseshare;
        this.title = title;
        this.description = description;
        this.cost = cost;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public Houseshare getHouseshare() {
        return houseshare;
    }

    public void setHouseshare(Houseshare houseshare) {
        this.houseshare = houseshare;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }
}
