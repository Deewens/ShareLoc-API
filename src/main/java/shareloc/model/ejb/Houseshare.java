package shareloc.model.ejb;

import javax.persistence.*;

@Entity
@Table(name = "houseshare")
public class Houseshare {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "houseshare_id")
    private int houseshareId;
    private String name;

    public Houseshare() {}

    public Houseshare(String name) {
        this.name = name;
    }

    public Houseshare(int houseshareId, String name) {
        this.houseshareId = houseshareId;
        this.name = name;
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
}
