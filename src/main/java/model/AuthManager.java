package model;

import model.dao.UserDAO;
import model.ejb.User;

import java.util.HashMap;
import java.util.Optional;

public class AuthManager {
    private final static String KEY_EMAIL = "EMAIL";

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

    public static HashMap<String, String> register(String email, String pseudo, String password, String firstname, String lastname) {
        HashMap<String, String> formError = new HashMap<>();

        HashMap<String, String> emailError = checkEmail(email);
        formError.putAll(emailError);

        System.out.println(formError.toString());

        return formError;
    }

    private static HashMap<String, String> checkEmail(String emailField) {
        String email = emailField;

        HashMap<String, String> errorMsgs = new HashMap<>();

        if (email != null && !email.isEmpty()) {
            if (email.matches("([^.@]+)(\\.[^.@]+)*@([^.@]+\\.)+([^.@]+)")) {
                Optional<User> user = userDAO.findByEmail(email);
                if(user.isPresent()) {
                    errorMsgs.put(KEY_EMAIL, "Cette adresse email existe déjà, choisissez en une autre.");
                }
            } else {
                errorMsgs.put(KEY_EMAIL, "Vous devez entrer une adresse email dans un format valide.");
            }
        } else {
            errorMsgs.put(KEY_EMAIL, "Vous devez entrer une adresse email.");
        }
        return errorMsgs;
    }
}
