package shareloc.model;

import shareloc.model.dao.HouseshareDAO;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HouseshareManager {
    @Inject
    private HouseshareDAO houseshareDAO = new HouseshareDAO();

    public HashMap<String, List<String>> createHouseshare(String name) {
        if (name == null || name.isBlank()) {
            List<String> errorMsgs = new ArrayList<>();
            errorMsgs.add("Vous devez mettre");
        }

        return null;
    }
}
