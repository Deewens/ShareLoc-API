package shareloc.resources;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.groups.ConvertGroup;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import shareloc.model.dao.UserDAO;
import shareloc.model.ejb.User;
import shareloc.model.validation.groups.SigningConstraint;
import shareloc.model.validation.ValidationErrorResponse;
import shareloc.security.PasswordUtils;
import shareloc.security.JWTokenUtility;
import shareloc.security.SignInNeeded;
import shareloc.utils.ErrorCode;

import java.util.*;

import static shareloc.utils.CustomResponse.*;

@Path("/")
public class AuthRessource {
    @Context
    UriInfo uriInfo;
    @Inject
    private UserDAO userDAO;
    @Inject
    private PasswordUtils passwordUtils;

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@Valid @ConvertGroup(to = SigningConstraint.class) User user) {
        String email = user.getEmail();

        Optional<User> userOptional = userDAO.findByEmail(email);
        if (userOptional.isPresent()) {
            boolean passwordMatch = PasswordUtils.verifyUserPassword(user.getPassword(), userOptional.get().getPassword(), userOptional.get().getSalt());

            if (passwordMatch) {
                HashMap<String, Object> data = new HashMap<>();
                data.put("token", JWTokenUtility.buildJWT(userOptional.get().getPseudo()));
                userOptional.get().setPassword(null);
                userOptional.get().setSalt(null);
                data.put("user", userOptional.get());

                GenericEntity<HashMap<String, Object>> entity = new GenericEntity<>(data) {};
                return Response.ok(entity).build();
            }
        }

        return buildErrorResponse(
                Response.Status.UNAUTHORIZED,
                ErrorCode.UNAUTHORIZED_ERROR,
                "Credential errors",
                "Email or password does not match with an existing user.");
    }

    @POST
    @Path("/signup")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(@Valid User user) {
        String email = user.getEmail();
        String pseudo = user.getPseudo();
        String plainPassword = user.getPassword();
        String firstname = user.getFirstname();
        String lastname = user.getLastname();

        String salt = passwordUtils.getSalt(30);
        String password = passwordUtils.generateSecurePassword(plainPassword, salt);

        Optional<User> checkEmail = userDAO.findByEmail(email);
        Optional<User> checkPseudo = userDAO.findByPseudo(pseudo);

        List<ValidationErrorResponse.ValidationError> errors = new ArrayList<>();

        if (checkEmail.isPresent()) {
            errors.add(new ValidationErrorResponse.ValidationError(
                    ErrorCode.ALREADY_EXIST,
                    "email",
                    email,
                    "email already exist in database")
            );
        }

        if (checkPseudo.isPresent()) {
            errors.add(new ValidationErrorResponse.ValidationError(
                    ErrorCode.ALREADY_EXIST,
                    "pseudo",
                    pseudo,
                    "pseudo already exist in database")
            );
        }

        if (!errors.isEmpty()) {
            ValidationErrorResponse response = new ValidationErrorResponse("url", "Validation error", errors);
            return Response.status(422).entity(response).build();
        } else {
            User userCreated = userDAO.create(new User(pseudo, email, password, firstname, lastname, salt));
            userCreated.setPassword(null);
            userCreated.setSalt(null);
            return Response.created(uriInfo.getAbsolutePath()).entity(userCreated).build();
        }
    }

    @GET
    @Path("/whoami")
    @Produces(MediaType.APPLICATION_JSON)
    @SignInNeeded
    public Response whoami(@Context SecurityContext security) {
        Optional<User> userOptional = userDAO.findByPseudo(security.getUserPrincipal().getName());

        if (userOptional.isPresent()) {
            userOptional.get().setPassword(null);
            userOptional.get().setSalt(null);
            return Response.ok().entity(userOptional.get()).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
