package resources;

import model.AuthManager;
import model.ejb.User;

import javax.validation.constraints.NotEmpty;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

@Path("/")
public class AuthRessource {
    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@NotEmpty(message = "L\'email doit être saisit") @QueryParam("email") String email,
                          @NotEmpty(message = "Le mot de passe doit être saisi") @QueryParam("password") String password) {

        Optional<User> userOptional = AuthManager.login(email, password);

        if(!userOptional.isPresent()) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }

        User user = userOptional.get();
        GenericEntity<User> entity = new GenericEntity<>(user) {};

        return Response.ok(entity).build();
    }
}
