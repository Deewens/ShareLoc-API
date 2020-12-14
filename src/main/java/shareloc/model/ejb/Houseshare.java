package shareloc.model.ejb;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.groups.Default;
import shareloc.model.validation.groups.HouseshareConstraints;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Houseshare {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int houseshareId;

    @NotBlank(groups = { HouseshareConstraints.PostConstraint.class, Default.class })
    @Column(nullable = false)
    private String name;

    @NotNull
    @ManyToOne
    @JoinColumn(nullable = false)
    private User manager; // Utilisateur qui est admin de cette co-location

    @NotEmpty
    @ManyToMany(targetEntity = User.class, fetch = FetchType.EAGER)
    private List<User> users = new ArrayList<User>(); // Liste des users membre de la colocation

    @OneToMany(targetEntity = Service.class)
    private List<Service> houseshareServices = new ArrayList<>(); // Liste des services d'une co-location

    public Houseshare() {}

    public Houseshare(int houseshareId, String name, User manager, List<User> users, List<Service> houseshareServices) {
        this.houseshareId = houseshareId;
        this.name = name;
        this.manager = manager;
        this.users = users;
        this.houseshareServices = houseshareServices;
    }

    public Houseshare(String name, User manager, List<User> users, List<Service> houseshareServices) {
        this.name = name;
        this.manager = manager;
        this.users = users;
        this.houseshareServices = houseshareServices;
    }

    public Houseshare(String name, User manager, List<User> users) {
        this.name = name;
        this.manager = manager;
        this.users = users;
    }

    public int getHouseshareId() {
        return houseshareId;
    }

    public void setHouseshareId(int houseshareId) {
        this.houseshareId = houseshareId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getManager() {
        return manager;
    }

    public void setManager(User manager) {
        this.manager = manager;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Service> getHouseshareServices() {
        return houseshareServices;
    }

    public void setHouseshareServices(List<Service> houseshareServices) {
        this.houseshareServices = houseshareServices;
    }


    @Override
    public String toString() {
        return "Houseshare{" +
                "houseshareId=" + houseshareId +
                ", name='" + name + '\'' +
                ", manager=" + manager +
                ", users=" + users +
                ", houseshareServices=" + houseshareServices +
                '}';
    }
}
