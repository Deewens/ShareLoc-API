package shareloc.model.ejb;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.groups.Default;
import shareloc.model.validation.groups.ServiceConstraints;

@Entity
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int serviceId;

    @ManyToOne(targetEntity = Houseshare.class)
    private Houseshare houseshare; // Co-location du service

    @NotBlank(groups = { ServiceConstraints.CreateServiceConstraint.class, ServiceConstraints.UpdateServiceConstraint.class/*, Default.class*/ })
    @Column(nullable = false)
    private String title;

    @NotNull(groups = { ServiceConstraints.CreateServiceConstraint.class, ServiceConstraints.UpdateServiceConstraint.class })
    private String description;

    @NotNull(groups = { ServiceConstraints.CreateServiceConstraint.class, ServiceConstraints.UpdateServiceConstraint.class })
    @Column(nullable = false)
    private Integer cost;

    @NotNull(groups = { ServiceConstraints.UpdateServiceConstraint.class })
    @Column(nullable = false)
    private Integer status; // 0 = inactif, 1 = actif, 2 = demande d'ajout, 3 = demande de suppresion

    public Service() {}

    public Service(Houseshare houseshare, String title, String description, int cost, int status) {
        this.houseshare = houseshare;
        this.title = title;
        this.description = description;
        this.cost = cost;
        this.status = status;
    }

    public Service(Integer serviceId, Houseshare houseshare, String title, String description, int cost, int status) {
        this.serviceId = serviceId;
        this.houseshare = houseshare;
        this.title = title;
        this.description = description;
        this.cost = cost;
        this.status = status;
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

    public int getStatus() { return status; }

    public void setStatus(int status) { this.status = status; }
}
