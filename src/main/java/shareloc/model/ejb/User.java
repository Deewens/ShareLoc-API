package shareloc.model.ejb;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import jakarta.validation.groups.Default;
import jakarta.xml.bind.annotation.XmlRootElement;
import shareloc.model.validation.groups.HouseshareConstraints;
import shareloc.model.validation.groups.SigningConstraint;
import shareloc.model.validation.groups.UserConstraints;

import java.util.Objects;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @NotBlank(groups = {UserConstraints.PutUserConstraint.class, Default.class})
    @Column(unique = true, nullable = false)
    private String pseudo;

    @NotBlank(groups = { UserConstraints.PutUserConstraint.class, SigningConstraint.class, HouseshareConstraints.PostUsersConstraint.class, Default.class })
    @Email(groups = { UserConstraints.PutUserConstraint.class, SigningConstraint.class, HouseshareConstraints.PostUsersConstraint.class, Default.class })
    @Column(unique = true, nullable = false)
    private String email;

    @Size(min = 8)
    @NotBlank(groups = { SigningConstraint.class, Default.class })
    @Column(nullable = false)
    private String password;

    @NotEmpty(groups = {UserConstraints.PutUserConstraint.class, Default.class})
    @Column(nullable = false)
    private String firstname;

    @NotEmpty(groups = {UserConstraints.PutUserConstraint.class, Default.class})
    @Column(nullable = false)
    private String lastname;

    @Column(nullable = false)
    private String salt;

    public User() {}

    public User(int userId, String pseudo, String email, String password, String firstname, String lastname, String salt) {
        this.userId = userId;
        this.pseudo = pseudo;
        this.email = email;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.salt = salt;
    }

    public User(String pseudo, String email, String password, String firstname, String lastname, String salt) {
        this.pseudo = pseudo;
        this.email = email;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.salt = salt;
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

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
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

    public String getSalt() { return this.salt; }

    public void setSalt(String salt) { this.salt = salt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userId.equals(user.userId) && pseudo.equals(user.pseudo) && email.equals(user.email) && password.equals(user.password) && firstname.equals(user.firstname) && lastname.equals(user.lastname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, pseudo, email, password, firstname, lastname, salt);
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", pseudo='" + pseudo + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                '}';
    }
}
