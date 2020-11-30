package shareloc.resources;

import shareloc.model.HouseshareManager;
import shareloc.model.dao.UserDAO;
import shareloc.model.ejb.User;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Path("/houseshare")
public class HouseshareRessource {
    @Inject
    HouseshareManager houseshareManager;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response createHouseshare(@QueryParam("name") String name, @DefaultValue("0") @QueryParam("user_id") int userId) {
        List<HashMap<String, String>> errorMsgs = houseshareManager.createHouseshare(name, userId);

        if (errorMsgs.isEmpty()) { // Si la liste est vide, il n'y a pas eu d'erreur
            HashMap<String, String> success = new HashMap<>();
            success.put("message", "Création de la co-location réussie.");
            return Response.ok().entity(success).build();
        } else {
            HashMap<String, List<HashMap<String, String>>> errors = new HashMap<>();
            errors.put("errors", errorMsgs);

            return Response.status(422).entity(errors).build();
        }
    }

}
