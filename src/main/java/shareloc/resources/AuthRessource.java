package shareloc.resources;

import shareloc.model.AuthManager;
import shareloc.model.ejb.User;
import shareloc.security.JWTokenUtility;

import javax.inject.Inject;
import javax.validation.constraints.NotEmpty;
import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Path("/")
public class AuthRessource {
    @Inject
    private AuthManager authManager;

    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@NotEmpty(message = "L'adresse email doit être saisi") @QueryParam("email") String email,
                          @NotEmpty(message = "Le mot de passe doit être saisi") @QueryParam("password") String password) {

        Optional<User> userOptional = authManager.login(email, password);

        if(!userOptional.isPresent()) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }

        User user = userOptional.get();
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("token", JWTokenUtility.buildJWT(user.getPseudo()));
        data.put("user",userOptional);

        return Response.ok().entity(data).build();
    }

    @POST
    @Path("register")
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(@QueryParam("email") String email, @QueryParam("pseudo") String pseudo,
                             @QueryParam("password") String password, @QueryParam("firstname") String firstname,
                             @QueryParam("lastname") String lastname) {
        HashMap<String, List<String>> errorMsgs = authManager.register(email, pseudo, password, firstname, lastname);

        if (errorMsgs.isEmpty()) {
            return Response.ok().build();
        } else {
            GenericEntity<HashMap<String, List<String>>> entity = new GenericEntity<>(errorMsgs) {};
            return Response.status(Response.Status.BAD_REQUEST).entity(entity).build();
        }
    }
}
