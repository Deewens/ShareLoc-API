package shareloc.resources;

import org.json.JSONException;
import org.json.JSONObject;
import shareloc.model.AuthManager;
import shareloc.model.dao.UserDAO;
import shareloc.model.ejb.User;
import shareloc.security.JWTokenUtility;
import shareloc.security.SignInNeeded;
import shareloc.utils.ErrorCode;
import shareloc.utils.ParamError;
import shareloc.utils.ParamErrorResponse;

import javax.inject.Inject;
import javax.json.JsonObject;
import javax.json.stream.JsonParsingException;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Path("/")
public class AuthRessource {
    @Context
    UriInfo uriInfo;
    @Inject
    private AuthManager authManager;
    @Inject
    private UserDAO userDAO;

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@NotNull User user) {
        String email = user.getEmail();
        String password = user.getPassword();

        List<ParamError> errors = authManager.checkLoginFields(email, password);

        if (errors.isEmpty()) {
            Optional<User> userConnected = userDAO.findByEmail(email);
            if (userConnected.isEmpty()) {
                errors.add(new ParamError(ErrorCode.NOT_FOUND, "email", email, "This email address does not exist."));
            } else {
                if (!password.equals(userConnected.get().getPassword())) {
                    errors.add(new ParamError(ErrorCode.NOT_MATCH, "password", "The provided password don't match."));
                } else {
                    HashMap<String, Object> data = new HashMap<>();
                    data.put("token", JWTokenUtility.buildJWT(userConnected.get().getPseudo()));
                    data.put("user", userConnected.get());

                    GenericEntity<HashMap<String, Object>> entity = new GenericEntity<>(data) {};
                    return Response.ok(entity).build();
                }
            }

            ParamErrorResponse paramErrorResponse = new ParamErrorResponse("link", "Unauthorized error", errors);
            return Response.status(Response.Status.UNAUTHORIZED).entity(paramErrorResponse).build();
        }

        ParamErrorResponse paramErrorResponse = new ParamErrorResponse("link", "Validation errors", errors);
        return Response.status(422).entity(paramErrorResponse).build();
    }

    @POST
    @Path("/signup")
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(@NotNull User user) {
        String email = user.getEmail();
        String pseudo = user.getPseudo();
        String password = user.getPassword();
        String firstname = user.getFirstname();
        String lastname = user.getLastname();

        List<ParamError> errors = authManager.checkSignupFields(email, pseudo, password, firstname, lastname);

        if (errors.isEmpty()) { // Si la liste est vide, il n'y a pas eu d'erreur
            User userCreated = userDAO.create(new User(pseudo, email, password, firstname, lastname));
            return Response.created(uriInfo.getAbsolutePath()).entity(userCreated).build();
        } else {
            ParamErrorResponse paramErrorResponse = new ParamErrorResponse("link", "Validation errors", errors);

            return Response.status(422).entity(paramErrorResponse).build();
        }
    }

    @GET
    @Path("/whoami")
    @Produces(MediaType.APPLICATION_JSON)
    @SignInNeeded
    public Response whoami(@Context SecurityContext security) {
        Optional<User> userOptional = userDAO.findByPseudo(security.getUserPrincipal().getName());

        if (userOptional.isPresent()) {
            return Response.ok().entity(userOptional.get()).build();
        }

        return Response.status((Response.Status.NOT_FOUND)).build();
    }
}
