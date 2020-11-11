package model;

import model.dao.UserDAO;
import model.ejb.User;

import java.util.Optional;

public class AuthManager {
    private static UserDAO userDAO = new UserDAO();

    public static Optional<User> login(String email, String password) {
        Optional<User> user = userDAO.findByEmail(email);

        if(user.isPresent() && password.equals(user.get().getPassword())) {
            return user;
        }

        return Optional.empty();
    }

    public static Optional<User> getUser(String email) {
        Optional<User> userOptional = userDAO.findByEmail(email);
        return Optional.ofNullable(userOptional).get();
    }
}
