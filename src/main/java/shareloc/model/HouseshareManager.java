package shareloc.model;

import shareloc.model.dao.HouseshareDAO;
import shareloc.model.dao.UserDAO;
import shareloc.model.dao.UserHouseshareDAO;
import shareloc.model.ejb.User;
import shareloc.utils.ErrorCode;
import shareloc.utils.ParamError;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HouseshareManager {
    @Inject
    private HouseshareDAO houseshareDAO;

    public List<ParamError> checkCreateHouseshareFields(String name, int userId) {
        List<ParamError> errors = new ArrayList<>();

        if (name != null && !name.isBlank()) {
            if (houseshareDAO.findByName(name).isPresent()) {
                errors.add(new ParamError(ErrorCode.ALREADY_EXIST, "name", name, "Name already exist."));
            }
        } else {
            errors.add(new ParamError(ErrorCode.PARAM_EMPTY, "name", name, "Name must not be empty."));
        }

        if (userId == 0) {
            errors.add(new ParamError(ErrorCode.PARAM_EMPTY, "user_id", Integer.toString(userId), "User must be given."));
        }

        return errors;
    }
}
