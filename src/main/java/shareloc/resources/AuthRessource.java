package shareloc.resources;

import shareloc.model.AuthManager;
import shareloc.model.dao.UserDAO;
import shareloc.model.ejb.User;
import shareloc.security.JWTokenUtility;
import shareloc.security.SignInNeeded;
import shareloc.utils.ErrorCode;
import shareloc.utils.ParamError;
import shareloc.utils.ParamErrorResponse;

import javax.inject.Inject;
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
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@FormParam("email") String email,
                          @FormParam("password") String password) {

        List<ParamError> errors = authManager.checkLoginFields(email, password);

        if (errors.isEmpty()) {
            Optional<User> user = userDAO.findByEmail(email);
            if (user.isEmpty()) {
                errors.add(new ParamError(ErrorCode.NOT_FOUND, "email", email, "This email address does not exist."));
            } else {
                if (!password.equals(user.get().getPassword())) {
                    errors.add(new ParamError(ErrorCode.NOT_MATCH, "password", "The provided password don't match."));
                } else {
                    HashMap<String, Object> data = new HashMap<>();
                    data.put("token", JWTokenUtility.buildJWT(user.get().getPseudo()));
                    data.put("user", user.get());

                    return Response.ok().entity(data).build();
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
    public Response register(@FormParam("email") String email,
                             @FormParam("pseudo") String pseudo,
                             @FormParam("password") String password,
                             @FormParam("firstname") String firstname,
                             @FormParam("lastname") String lastname) {

        List<ParamError> errors = authManager.checkSignupFields(email, pseudo, password, firstname, lastname);

        if (errors.isEmpty()) { // Si la liste est vide, il n'y a pas eu d'erreur
            User user = userDAO.create(new User(pseudo, email, password, firstname, lastname));
            return Response.created(uriInfo.getAbsolutePath()).entity(user).build();
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

        return Response.status((Response.Status.NO_CONTENT)).build();
    }
}
