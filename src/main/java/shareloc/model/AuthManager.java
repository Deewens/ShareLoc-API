package shareloc.model;

import shareloc.model.dao.UserDAO;
import shareloc.model.ejb.User;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class AuthManager {
    private static final String CODE_FIELD_ALREADY_EXIST = "FIELD_ALREADY_EXIST";
    private static final String CODE_BAD_FORMAT = "BAD_FORMAT";
    private static final String CODE_EMPTY_FIELD = "EMPTY_FIELD";
    private static final String CODE_TOO_LONG_FIELD = "TOO_LONG_FIELD";
    private static final String CODE_TOO_SHORT_FIELD = "TOO_SHORT_FIELD";

    @SuppressWarnings("FieldMayBeFinal")
    @Inject
    private UserDAO userDAO = new UserDAO();

    public Optional<User> login(String email, String password) {
        Optional<User> user = userDAO.findByEmail(email);

        if(user.isPresent() && password.equals(user.get().getPassword())) {
            return user;
        }

        return Optional.empty();
    }

    public List<HashMap<String, String>> register(String email, String pseudo, String password, String firstname, String lastname) {
        List<HashMap<String, String>> errors = new ArrayList<>();

        errors.addAll(checkEmail(email));
        errors.addAll(checkPseudo(pseudo));
        errors.addAll(checkPassword(password));
        errors.addAll(checkFirstname(firstname));
        errors.addAll(checkLastname(lastname));

        if (errors.isEmpty()) {
            userDAO.create(new User(pseudo, email, password, firstname, lastname));
        }

        return errors;
    }

    private List<HashMap<String, String>> checkEmail(String email) {
        List<HashMap<String, String>> errors = new ArrayList<>();

        if (email != null && !email.isEmpty()) {
            if (email.matches("([^.@]+)(\\.[^.@]+)*@([^.@]+\\.)+([^.@]+)")) {
                Optional<User> user = userDAO.findByEmail(email);
                if(user.isPresent()) {
                    HashMap<String, String> error = new HashMap<>();
                    error.put("code", CODE_FIELD_ALREADY_EXIST);
                    error.put("field", "email");
                    error.put("title", "L'adresse email existe déjà dans la base de données.");
                    error.put("message", "Essayez de choisir une autre adresse email qui n'est pas déjà utilisé.");

                    errors.add(error);
                }
            } else {
                HashMap<String, String> error = new HashMap<>();
                error.put("code", CODE_BAD_FORMAT);
                error.put("field", "email");
                error.put("title", "L'adresse email n'est pas au bon format.");
                error.put("message", "Vous devez entrer un email au format valide (exemple@domaine.fr)");
                errors.add(error);
            }
        } else {
            HashMap<String, String> error = new HashMap<>();
            error.put("code", CODE_EMPTY_FIELD);
            error.put("field", "email");
            error.put("title", "Le champ email est vide.");
            error.put("message", "L'adresse email est obligatoire.");
            errors.add(error);
        }

        return errors;
    }

    private List<HashMap<String, String>> checkPseudo(String pseudo) {
        List<HashMap<String, String>> errors = new ArrayList<>();

        if (pseudo != null && !pseudo.isEmpty()) {
            if (pseudo.length() > 50) {
                HashMap<String, String> error = new HashMap<>();
                error.put("code", CODE_TOO_LONG_FIELD);
                error.put("field", "pseudo");
                error.put("title", "Le pseudo est trop long.");
                error.put("message", "Votre pseudo ne peut pas faire plus de 50 caractères.");

                errors.add(error);
            } else {
                if (userDAO.findByPseudo(pseudo).isPresent()) {
                    HashMap<String, String> error = new HashMap<>();
                    error.put("code", CODE_FIELD_ALREADY_EXIST);
                    error.put("field", "pseudo");
                    error.put("title", "Le pseudo " + pseudo + " existe déjà dans la base de données.");
                    error.put("message", "Essayez de choisir un pseudo qui n'est pas déjà utilisé.");

                    errors.add(error);
                }
            }
        } else {
            HashMap<String, String> error = new HashMap<>();
            error.put("code", CODE_EMPTY_FIELD);
            error.put("field", "pseudo");
            error.put("title", "Le champ pseudo est vide");
            error.put("message", "Le pseudo est obligatoire.");
            errors.add(error);
        }

        return errors;
    }

    private List<HashMap<String, String>> checkPassword(String password) {
        List<HashMap<String, String>> errors = new ArrayList<>();

        if (password != null && !password.isEmpty()) {
            if (password.length() < 8) {
                HashMap<String, String> error = new HashMap<>();
                error.put("code", CODE_TOO_SHORT_FIELD);
                error.put("field", "password");
                error.put("title", "Le mot de passe est trop court.");
                error.put("message", "Votre mot de passe doit faire au minimum 8 caractères.");

                errors.add(error);
            }
        } else {
            HashMap<String, String> error = new HashMap<>();
            error.put("code", CODE_EMPTY_FIELD);
            error.put("field", "password");
            error.put("title", "Le champ est vide");
            error.put("message", "Le mot de passe est obligatoire.");

            errors.add(error);
        }

        return errors;
    }

    private List<HashMap<String, String>> checkFirstname(String firstname) {
        List<HashMap<String, String>> errors = new ArrayList<>();

        if (firstname != null && !firstname.isEmpty()) {
            if (firstname.length() > 50) {
                HashMap<String, String> error = new HashMap<>();
                error.put("code", CODE_TOO_LONG_FIELD);
                error.put("field", "firstname");
                error.put("title", "Le prénom est trop long");
                error.put("message", "Votre prénom doit avoir un maximum de 50 caractères.");

                errors.add(error);
            }
        } else {
            HashMap<String, String> error = new HashMap<>();
            error.put("code", CODE_EMPTY_FIELD);
            error.put("field", "firstname");
            error.put("title", "Le champ est vide");
            error.put("message", "Le prénom est obligatoire.");

            errors.add(error);
        }

        return errors;
    }

    private List<HashMap<String, String>> checkLastname(String lastname) {
        List<HashMap<String, String>> errors = new ArrayList<>();

        if (lastname != null && !lastname.isEmpty()) {
            if (lastname.length() > 50) {
                HashMap<String, String> error = new HashMap<>();
                error.put("code", CODE_TOO_LONG_FIELD);
                error.put("field", "lastname");
                error.put("title", "Le nom est trop long");
                error.put("message", "Votre nom doit avoir un maximum de 50 caractères.");

                errors.add(error);
            }
        } else {
            HashMap<String, String> error = new HashMap<>();
            error.put("code", CODE_EMPTY_FIELD);
            error.put("field", "lastname");
            error.put("title", "Le champ est vide");
            error.put("message", "Le nom est obligatoire.");

            errors.add(error);
        }

        return errors;
    }
}
