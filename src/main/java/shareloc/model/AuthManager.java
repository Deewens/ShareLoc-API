package shareloc.model;

import shareloc.model.dao.UserDAO;
import shareloc.model.ejb.User;
import shareloc.utils.ErrorCode;
import shareloc.utils.ParamError;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AuthManager {
    @Inject
    private UserDAO userDAO;

    public List<ParamError> checkLoginFields(String email, String password) {
        List<ParamError> errors = new ArrayList<>();

        if (email == null || email.isBlank()) {
            errors.add(new ParamError(ErrorCode.PARAM_EMPTY, "email", email, "Email field is empty."));
        } else if (!email.matches("([^.@]+)(\\.[^.@]+)*@([^.@]+\\.)+([^.@]+)")) {
            errors.add(new ParamError(ErrorCode.BAD_FORMAT, "email", email, "You must use a valid email address (example@domain.fr)"));
        }

        if (password == null || password.isBlank()) {
            errors.add(new ParamError(ErrorCode.PARAM_EMPTY, "password", "Password field is empty."));
        }

        return errors;
    }

    public List<ParamError> checkSignupFields(String email, String pseudo, String password, String firstname, String lastname) {
        List<ParamError> errors = new ArrayList<>();

        ParamError emailError = checkEmail(email);
        if (emailError != null)
            errors.add(emailError);

        ParamError pseudoError = checkPseudo(pseudo);
        if (pseudoError != null)
            errors.add(pseudoError);

        ParamError passwordError = checkPassword(password);
        if (passwordError != null)
            errors.add(passwordError);

        ParamError firstnameError = checkFirstname(firstname);
        if (firstnameError != null)
            errors.add(firstnameError);

        ParamError lastnameError = checkLastname(lastname);
        if (lastnameError != null)
            errors.add(lastnameError);

        return errors;
    }

    private ParamError checkEmail(String email) {
        ParamError error = null;

        if (email != null && !email.isEmpty()) {
            if (email.matches("([^.@]+)(\\.[^.@]+)*@([^.@]+\\.)+([^.@]+)")) {
                Optional<User> user = userDAO.findByEmail(email);
                if(user.isPresent()) {
                    error = new ParamError(ErrorCode.ALREADY_EXIST, "email", email, "Email already exist.");
                }
            } else {
                error = new ParamError(ErrorCode.BAD_FORMAT, "email", email, "You must use a valid email address (example@domain.fr)");
            }
        } else {
            error = new ParamError(ErrorCode.PARAM_EMPTY, "email", "Email address must not be empty.");
        }

        return error;
    }

    private ParamError checkPseudo(String pseudo) {
        ParamError error = null;

        if (pseudo != null && !pseudo.isEmpty()) {
            if (pseudo.length() > 50) {
                error = new ParamError(ErrorCode.PARAM_TOO_LONG, "pseudo", pseudo, "Your pseudo can't be more than 50 characters.");
            } else {
                if (userDAO.findByPseudo(pseudo).isPresent()) {
                    error = new ParamError(ErrorCode.ALREADY_EXIST, "pseudo", pseudo, "This pseudo is already used.");
                }
            }
        } else {
            error = new ParamError(ErrorCode.PARAM_EMPTY, "pseudo", "Pseudo must not be empty.");
        }

        return error;
    }

    private ParamError checkPassword(String password) {
        ParamError error = null;

        if (password != null && !password.isEmpty()) {
            if (password.length() < 8) {
                error = new ParamError(ErrorCode.PARAM_TOO_SHORT, "password", "The password must have 8 characters minimum.");
            }
        } else {

            error = new ParamError(ErrorCode.PARAM_EMPTY, "password", "The password field must not be empty.");
        }

        return error;
    }

    private ParamError checkFirstname(String firstname) {
        ParamError error = null;

        if (firstname != null && !firstname.isEmpty()) {
            if (firstname.length() > 50) {
                error = new ParamError(ErrorCode.PARAM_TOO_LONG, "firstname", firstname, "Firstname must not exceed 50 characters.");
            }
        } else {
            error = new ParamError(ErrorCode.PARAM_EMPTY, "firstname", "Firstname must not be empty.");
        }

        return error;
    }

    private ParamError checkLastname(String lastname) {
        ParamError error = null;

        if (lastname != null && !lastname.isEmpty()) {
            if (lastname.length() > 50) {
                error = new ParamError(ErrorCode.PARAM_TOO_LONG, "lastname", lastname, "Lastname must not exceed 50 characters.");

            }
        } else {
            error = new ParamError(ErrorCode.PARAM_EMPTY, "lastname", "Lastname must not be empty.");

        }

        return error;
    }
}
