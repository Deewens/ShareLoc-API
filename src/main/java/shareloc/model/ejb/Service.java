package shareloc.model.ejb;

import javax.persistence.*;

@Entity
@Table(name = "service")
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_id")
    private int serviceId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "houseshare_id", referencedColumnName = "houseshare_id")
    private Houseshare houseshare;
    private String title;
    private String description;
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

    public void setServiceId(int id) {
        this.serviceId = id;
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
