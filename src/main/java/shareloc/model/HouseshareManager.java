package shareloc.model;

import shareloc.model.dao.HouseshareDAO;
import shareloc.model.dao.UserDAO;
import shareloc.model.dao.UserHouseshareDAO;
import shareloc.model.ejb.Houseshare;
import shareloc.model.ejb.User;
import shareloc.model.ejb.UserHouseshare;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class HouseshareManager {
    private static final String CODE_ENTITY_NOT_FOUND = "ENTITY_NOT_FOUND";
    private static final String CODE_EMPTY_FIELD = "EMPTY_FIELD";
    private static final String CODE_FIELD_ALREADY_EXIST = "FIELD_ALREADY_EXIST";

    @Inject
    private HouseshareDAO houseshareDAO;
    @Inject
    private UserHouseshareDAO userHouseshareDAO;
    @Inject
    private UserDAO userDAO;

    public List<HashMap<String, String>> createHouseshare(String name, int userId) {
        List<HashMap<String, String>> errors = new ArrayList<>();
        boolean hasErrors = false;

        if (name != null && !name.isBlank()) {
            if (houseshareDAO.findByName(name).isPresent()) {
                HashMap<String, String> error = new HashMap<>();
                error.put("code", CODE_FIELD_ALREADY_EXIST);
                error.put("field", "name");
                error.put("title", "Le nom " + name + " existe déjà.");
                error.put("message", "Un autre nom doit être choisi pour la colocation.");
                errors.add(error);

                hasErrors = true;
            }
        } else {
            HashMap<String, String> error = new HashMap<>();
            error.put("code", CODE_EMPTY_FIELD);
            error.put("field", "name");
            error.put("title", "Nom est vide.");
            error.put("message", "Vous devez obligatoirement mettre un nom.");
            errors.add(error);

            hasErrors = true;
        }

        Optional<User> user = Optional.empty();
        if (userId > 0) {
            user = userDAO.findById(userId);
            if(user.isEmpty()) {
                List<HashMap<String, String>> errorMsg = new ArrayList<>();
                HashMap<String, String> error = new HashMap<>();
                error.put("code", CODE_ENTITY_NOT_FOUND);
                error.put("param", "user_id");
                error.put("title", "Utilisateur introuvable");
                error.put("message", "L'utilisateur ne semble pas exister avec l'identifiant reçu.");
                errors.add(error);

                hasErrors = true;
            }
        } else {
            HashMap<String, String> error = new HashMap<>();
            error.put("code", CODE_EMPTY_FIELD);
            error.put("field", "user_id");
            error.put("title", "ID utilisateur non indiqué.");
            error.put("message", "Vous devez obligatoirement mettre l'ID de l'user.");
            errors.add(error);

            hasErrors = true;
        }


        if (!hasErrors) {
            Houseshare houseshare = houseshareDAO.create(new Houseshare(name));
            userHouseshareDAO.create(new UserHouseshare(user.get(), houseshare, 0, true));
        }

        return errors;
    }
}
