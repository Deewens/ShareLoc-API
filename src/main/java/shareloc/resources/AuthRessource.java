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
import shareloc.model.validation.groups.UserConstraints;
import shareloc.security.PasswordUtils;
import shareloc.security.JWTokenUtility;
import shareloc.security.SignInNeeded;
import shareloc.utils.ErrorCode;
import static shareloc.utils.CustomResponse.*;

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

    /**
     * Connexion d'un utlisateur
     *
     * @param user L'utilisateur avec l'identifiant et le mot de passe
     * @return L'entité User connecté
     */
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

    /**
     * Inscription d'un utlisateur
     *
     * @param user L'utilisateur avec ses informations d'inscription
     * @return L'entité User qui a été créée
     */
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

    /**
     * Récupération du user grâce au token
     *
     * @return L'entité User connecté
     */
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

    /**
     * Modification de l'USER
     *
     * @return L'entité User modifié
     */
    @PUT
    @Path("/user/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    @SignInNeeded
    public Response updateUser(@Context SecurityContext security,
                               @PathParam("userId") int userId,
                               @Valid @ConvertGroup(to = UserConstraints.PutUserConstraint.class) User user) {
        Optional<User> loggedInUser = userDAO.findByPseudo(security.getUserPrincipal().getName());

        Optional<User> userToUpdate = userDAO.findById(userId);

        if (loggedInUser.isPresent()) {
            if (userToUpdate.isEmpty()) {
                return buildUserNotExistResponse();
            }

            if (loggedInUser.get().getUserId() != userToUpdate.get().getUserId()) {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }

            userToUpdate.get().setFirstname(user.getFirstname());
            userToUpdate.get().setLastname(user.getLastname());
            userToUpdate.get().setPseudo(user.getPseudo());
            userToUpdate.get().setEmail(user.getEmail());

            return Response.ok(userDAO.update(userToUpdate.get())).build();
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
}
