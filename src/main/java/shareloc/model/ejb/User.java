package shareloc.model.ejb;

import org.eclipse.persistence.annotations.SerializedObject;

import javax.json.bind.annotation.JsonbAnnotation;
import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Entity
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;
    @Column(unique = true, nullable = false)
    private String pseudo;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String firstname;
    @Column(nullable = false)
    private String lastname;


    public User() {}

    public User(int userId, String pseudo, String email, String password, String firstname, String lastname) {
        this.userId = userId;
        this.pseudo = pseudo;
        this.email = email;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public User(String pseudo, String email, String password, String firstname, String lastname) {
        this.pseudo = pseudo;
        this.email = email;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
}
