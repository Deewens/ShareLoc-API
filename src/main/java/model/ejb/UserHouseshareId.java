package model.ejb;

import java.io.Serializable;
import java.util.Objects;

public class UserHouseshareId implements Serializable {
    private User user;
    private Houseshare houseshare;

    public UserHouseshareId(User user, Houseshare houseshare) {
        this.user = user;
        this.houseshare = houseshare;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserHouseshareId that = (UserHouseshareId) o;
        return Objects.equals(user, that.user) &&
                Objects.equals(houseshare, that.houseshare);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, houseshare);
    }
}
