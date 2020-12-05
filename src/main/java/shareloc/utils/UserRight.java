package shareloc.utils;

import shareloc.model.ejb.Houseshare;
import shareloc.model.ejb.User;

public class UserRight {
    public static boolean isUserManager(User user, Houseshare houseshare) {
        return houseshare.getManager().getUserId() == user.getUserId();
    }

    public static boolean isUserIntoHouseshare(User user, Houseshare houseshare) {
        for (User value : houseshare.getUsers()) {
            if (value.getUserId() == user.getUserId()) {
                return true;
            }
        }

        return false;
    }
}
