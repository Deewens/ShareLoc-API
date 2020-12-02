package shareloc.resources;

import shareloc.model.HouseshareManager;
import shareloc.model.dao.HouseshareDAO;
import shareloc.model.dao.UserDAO;
import shareloc.model.dao.UserHouseshareDAO;
import shareloc.model.ejb.Houseshare;
import shareloc.model.ejb.User;
import shareloc.model.ejb.UserHouseshare;
import shareloc.security.SignInNeeded;
import shareloc.utils.ErrorCode;
import shareloc.utils.ParamError;
import shareloc.utils.ParamErrorResponse;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@SignInNeeded
@Path("/houseshare")
public class HouseshareRessource {
    @Context
    UriInfo uriInfo;

    @Inject
    HouseshareManager houseshareManager;

    @Inject
    private HouseshareDAO houseshareDAO;

    @Inject
    private UserHouseshareDAO userHouseshareDAO;

    @Inject
    private UserDAO userDAO;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response createHouseshare(@FormParam("name") String name, @DefaultValue("0") @HeaderParam("user_id") int userId) {
        List<ParamError> errors = houseshareManager.checkCreateHouseshareFields(name, userId);

        if (errors.isEmpty()) { // Si la liste est vide, il n'y a pas eu d'erreur
            Optional<User> user = userDAO.findById(userId);
            if(user.isEmpty()) {
                errors.add(new ParamError(ErrorCode.NOT_FOUND, "user_id", Integer.toString(userId), "User not found with the given ID."));
            } else {
                Houseshare houseshare = houseshareDAO.create(new Houseshare(name));
                userHouseshareDAO.create(new UserHouseshare(user.get(), houseshare, 0, true));

                return Response.created(uriInfo.getAbsolutePath()).entity(houseshare).build();
            }

        }
        ParamErrorResponse errorResponse = new ParamErrorResponse("link", "Validation errors", errors);
        return Response.status(422).entity(errorResponse).build();
    }
}
