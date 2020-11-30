package shareloc.resources;

import shareloc.model.AuthManager;
import shareloc.model.dao.UserDAO;
import shareloc.model.ejb.User;
import shareloc.security.JWTokenUtility;
import shareloc.security.SignInNeeded;

import javax.inject.Inject;
import javax.validation.constraints.NotEmpty;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Path("/")
public class AuthRessource {
    @Inject
    private AuthManager authManager;
    @Inject
    private UserDAO userDAO = new UserDAO();

    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@NotEmpty(message = "L'adresse email doit être saisi") @QueryParam("email") String email,
                          @NotEmpty(message = "Le mot de passe doit être saisi") @QueryParam("password") String password) {

        Optional<User> userOptional = authManager.login(email, password);

        if(userOptional.isEmpty()) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }

        User user = userOptional.get();
        HashMap<String, Object> data = new HashMap<>();
        data.put("token", JWTokenUtility.buildJWT(user.getPseudo()));
        data.put("user", user);

        return Response.ok().entity(data).build();
    }

    @POST
    @Path("/register")
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(@QueryParam("email") String email, @QueryParam("pseudo") String pseudo,
                             @QueryParam("password") String password, @QueryParam("firstname") String firstname,
                             @QueryParam("lastname") String lastname) {
        List<HashMap<String, String>> errorMsgs = authManager.register(email, pseudo, password, firstname, lastname);

        if (errorMsgs.isEmpty()) { // Si la liste est vide, il n'y a pas eu d'erreur
            HashMap<String, String> success = new HashMap<>();
            success.put("title", "Inscription réussie");
            success.put("message", "L'utilisateur a été ajouté en base de données.");
            return Response.ok().entity(success).build();
        } else {
            HashMap<String, List<HashMap<String, String>>> errors = new HashMap<>();
            errors.put("errors", errorMsgs);

            return Response.status(422).entity(errors).build();
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
