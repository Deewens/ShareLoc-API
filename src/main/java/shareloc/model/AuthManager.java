package shareloc.model;

import shareloc.model.dao.UserDAO;
import shareloc.model.ejb.User;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class AuthManager {
    private final String KEY_EMAIL = "EMAIL_ERROR";
    private final String KEY_PSEUDO = "PSEUDO_ERROR";
    private final String KEY_PASSWORD = "PASSWORD_ERROR";
    private final String KEY_FIRSTNAME = "FIRSTNAME_ERROR";
    private final String KEY_LASTNAME = "LASTNAME_ERROR";

    @Inject
    private UserDAO userDAO = new UserDAO();

    public Optional<User> login(String email, String password) {
        Optional<User> user = userDAO.findByEmail(email);

        if(user.isPresent() && password.equals(user.get().getPassword())) {
            return user;
        }

        return Optional.empty();
    }

    public HashMap<String, List<String>> register(String email, String pseudo, String password, String firstname, String lastname) {
        HashMap<String, List<String>> formError = new HashMap<>();

        formError.putAll(checkEmail(email));
        formError.putAll(checkPassword(password));
        formError.putAll(checkPseudo(pseudo));
        formError.putAll(checkFirstname(firstname));
        formError.putAll(checkLastname(lastname));

        if (formError.isEmpty()) {
            userDAO.create(new User(pseudo, email, password, firstname, lastname));
        }

        return formError;
    }

    private HashMap<String, List<String>> checkEmail(String email) {
        List<String> msgs = new ArrayList<>();

        if (email != null && !email.isEmpty()) {
            if (email.matches("([^.@]+)(\\.[^.@]+)*@([^.@]+\\.)+([^.@]+)")) {
                Optional<User> user = userDAO.findByEmail(email);
                if(user.isPresent()) {
                    msgs.add("Cette adresse email existe déjà, choisissez en une autre.");
                }
            } else {
                msgs.add("Vous devez entrer une adresse email dans un format valide.");
            }
        } else {
            msgs.add("Vous devez entrer une adresse email.");
        }

        HashMap<String, List<String>> errorMsgs = new HashMap<>();

        if (!msgs.isEmpty()) {
            errorMsgs.put(KEY_EMAIL, msgs);
        }

        return errorMsgs;
    }

    private HashMap<String, List<String>> checkPseudo(String pseudo) {
        List<String> msgs = new ArrayList<>();

        if (pseudo != null && !pseudo.isEmpty()) {
            if (pseudo.length() > 50) {
                msgs.add("Votre pseudo ne peut pas faire plus de 25 caractères.");
            } else {
                if (userDAO.findByPseudo(pseudo).isPresent()) {
                    msgs.add("Ce pseudo existe déjà");
                }
            }
        } else {
            msgs.add("Le pseudo est obligatoire.");
        }

        HashMap<String, List<String>> errorMsgs = new HashMap<>();
        if (!msgs.isEmpty()) {
            errorMsgs.put(KEY_PSEUDO, msgs);
        }

        return errorMsgs;
    }

    private HashMap<String, List<String>> checkPassword(String password) {
        List<String> msgs = new ArrayList<>();

        if (password != null && !password.isEmpty()) {
            if (password.length() < 8) {
                msgs.add("Votre mot de passe doit être composé d'au moins 8 caractères.");
            }
        } else {
            msgs.add("Vous devez entrer un mot de passe.");
        }

        HashMap<String, List<String>> errorMsgs = new HashMap<>();
        if (!msgs.isEmpty()) {
            errorMsgs.put(KEY_PASSWORD, msgs);
        }

        return errorMsgs;
    }

    private HashMap<String, List<String>> checkFirstname(String firstname) {
        List<String> msgs = new ArrayList<>();

        if (firstname != null && !firstname.isEmpty()) {
            if (firstname.length() > 50) {
                msgs.add("Votre prénom ne peut pas faire plus de 50 caractères.");
            }
        } else {
            msgs.add("Vous devez mettre votre prénom.");
        }

        HashMap<String, List<String>> errorMsgs = new HashMap<>();
        if (!msgs.isEmpty()) {
            errorMsgs.put(KEY_FIRSTNAME, msgs);
        }
        return errorMsgs;
    }

    private HashMap<String, List<String>> checkLastname(String lastname) {
        List<String> msgs = new ArrayList<>();

        if (lastname != null && !lastname.isEmpty()) {
            if (lastname.length() > 50) {
                msgs.add("Votre nom ne peut pas faire plus de 50 caractères.");
            }
        } else {
            msgs.add("Vous devez mettre votre nom.");
        }

        HashMap<String, List<String>> errorMsgs = new HashMap<>();
        if (!msgs.isEmpty()) {
            errorMsgs.put(KEY_LASTNAME, msgs);
        }
        return errorMsgs;
    }
}
