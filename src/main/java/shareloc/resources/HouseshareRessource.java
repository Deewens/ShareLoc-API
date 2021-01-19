package shareloc.resources;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.groups.ConvertGroup;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import shareloc.model.dao.AchievedServiceDAO;
import shareloc.model.dao.HouseshareDAO;
import shareloc.model.dao.ServiceDAO;
import shareloc.model.dao.UserDAO;
import shareloc.model.ejb.Houseshare;
import shareloc.model.ejb.User;
import shareloc.model.validation.ValidationErrorResponse;
import shareloc.model.validation.groups.HouseshareConstraints;
import shareloc.security.SignInNeeded;
import shareloc.utils.*;

import static shareloc.utils.CustomResponse.*;

import java.util.*;

@SignInNeeded
@Path("/houseshares")
public class HouseshareRessource {
    @Context
    UriInfo uriInfo;
    @Context
    SecurityContext securityContext;

    @Inject
    private HouseshareDAO houseshareDAO;
    @Inject
    private UserDAO userDAO;
    @Inject
    private ServiceDAO serviceDAO;
    @Inject
    private AchievedServiceDAO achievedServiceDAO;

    /**
     * Récupère la liste de toutes les colocations dont le membre est l'utilisateur connecté
     *
     * @return liste des collocations créées
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHouseshares() {
        Optional<User> user = userDAO.findByPseudo(securityContext.getUserPrincipal().getName());

        if (user.isPresent()) {
            List<Houseshare> houseshare = houseshareDAO.findByUser(user.get());
            return Response.ok(houseshare).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();

    }

    /**
     * Cherche une co-location par son ID si l'utilsateur est dedans
     *
     * @param id
     * @return L'entité Houseshare
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHouseshare(@PathParam("id") int id) {
        Optional<User> user = userDAO.findByPseudo(securityContext.getUserPrincipal().getName());
        Optional<Houseshare> houseshare = houseshareDAO.findById(id);

        if (user.isPresent() && houseshare.isPresent()) {
            if (UserRight.isUserIntoHouseshare(user.get(), houseshare.get()))
            {
                return Response.ok(houseshare.get()).build();
            }
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    /**
     * Crée une co-location vide
     *
     * L'utilisateur qui lance cette requête devient l'administrateur de la co-location
     *
     * @param obj houseshare
     * @return l'entité Houseshare qui a été créé.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createHouseshare(@Valid @ConvertGroup(to = HouseshareConstraints.PostConstraint.class) Houseshare obj) {
        String name = obj.getName();

        List<ValidationErrorResponse.ValidationError> errors = new ArrayList<>();

        if (houseshareDAO.findByName(name).isPresent()) {
            errors.add(new ValidationErrorResponse.ValidationError(
                    ErrorCode.ALREADY_EXIST,
                    "name",
                    name,
                    "name already exist in database")
            );
        }

        if (!errors.isEmpty()) {
            ValidationErrorResponse response = new ValidationErrorResponse("url", "Validation error", errors);
            return Response.status(422).entity(response).build();
        } else {
            Optional<User> user = userDAO.findByPseudo(securityContext.getUserPrincipal().getName());

            if (user.isPresent()) {
                List<User> userSet = new ArrayList<>();
                userSet.add(user.get());
                Houseshare houseshare = houseshareDAO.create(new Houseshare(name, user.get(), userSet));

                return Response.created(uriInfo.getAbsolutePath()).entity(houseshare).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }

        }
    }

    /**
     * Met à jour (complétement ou partiellement) la colocation d'id
     *
     * @param id
     * @return l'entité Houseshare qui a été modifiée.
     */
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateHouseshare(
            @PathParam("id") int id,
            @Valid @ConvertGroup(to = HouseshareConstraints.PutConstraint.class) Houseshare houseshareUpdated) {

        Optional<User> user = userDAO.findByPseudo(securityContext.getUserPrincipal().getName());

        if (user.isPresent()) {
            Optional<Houseshare> houseshare = houseshareDAO.findById(id);
            if (houseshare.isEmpty()) {
                return buildHouseshareNotFoundErrorResponse();
            }

            if (houseshare.get().getManager().equals(user.get())) {
                houseshare.get().setName(houseshareUpdated.getName());

                Houseshare updateResult = houseshareDAO.update(houseshare.get());

                return Response.ok(updateResult).build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

    }

    /**
     * Supprime la colocation d'id
     *
     * @param id
     * @return NO_CONTENT response
     */
    @DELETE
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteHouseshare(@NotNull @PathParam("id") Integer id) {
        Optional<User> user = userDAO.findByPseudo(securityContext.getUserPrincipal().getName());

        if (user.isPresent()) {
            Optional<Houseshare> houseshare = houseshareDAO.findById(id);
            if (houseshare.isEmpty())
                return buildHouseshareNotFoundErrorResponse();

            if (!houseshare.get().getManager().equals(user.get())) // Vérification des droits (si l'user connecté est manager ou non)
                return Response.status(Response.Status.UNAUTHORIZED).build();

            houseshareDAO.delete(houseshare.get());
            return Response.status(Response.Status.NO_CONTENT).build();
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }


    /**
     * Cherche tous les membres de la colocation d'id
     *
     * @param id
     * @return La liste de tous les membres
     */
    @GET
    @Path("/{id}/users")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHouseshareMembers(@NotNull @PathParam("id") Integer id) {
        Optional<User> user = userDAO.findByPseudo(securityContext.getUserPrincipal().getName());

        if (user.isPresent()) {
            Optional<Houseshare> houseshare = houseshareDAO.findById(id);

            if (houseshare.isEmpty()) {
                return buildHouseshareNotFoundErrorResponse();
            }

            return Response.ok(houseshare.get().getUsers()).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();

    }

    /**
     * Ajout des nouveaux membres dans la colocation d'id
     *
     * @param id
     * @param userParam L'utilisateur à ajouter dans la collocation
     * @return L'utilisateur ajouté
     */
    @POST
    @Path("/{id}/users/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response putUserInHouseshare(@NotNull @PathParam("id") Integer id, @Valid @ConvertGroup(to = HouseshareConstraints.PostUsersConstraint.class) User userParam) {
        Optional<User> loggedInUser = userDAO.findByPseudo(securityContext.getUserPrincipal().getName());

        if (loggedInUser.isPresent()) {
            Optional<User> user = userDAO.findByEmail(userParam.getEmail());
            Optional<Houseshare> houseshare = houseshareDAO.findById(id);

            if (houseshare.isEmpty()) {
                return buildHouseshareNotFoundErrorResponse();
            }

            if (user.isEmpty()) {
                return buildErrorResponse(
                        Response.Status.NOT_FOUND,
                        ErrorCode.NOT_FOUND,
                        "User does not exist",
                        "The user you are trying to add does not exist.");
            }

            if (!loggedInUser.get().equals(houseshare.get().getManager())) {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }

            if (!houseshare.get().getUsers().contains(user.get())) {
                houseshare.get().getUsers().add(user.get());
                houseshareDAO.update(houseshare.get());

                return Response.status(Response.Status.CREATED).entity(user.get()).build();
            } else {
                return buildErrorResponse(
                        Response.Status.BAD_REQUEST,
                        ErrorCode.ALREADY_EXIST,
                        "User already exist",
                        "The user you are trying to add already exist in the houseshare.");
            }
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * Supprime le membre de la colocation d'id
     *
     * @param houseshareId L'id de la colocation
     * @param userId L'id de l'utilisateur
     * @return La liste de tous les utilisateurs de la collocation
     */
    @DELETE
    @Path("/{houseshareId}/users/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteUserFromHouseshare(@NotNull @PathParam("houseshareId") Integer houseshareId,
                                             @NotNull @PathParam("userId") Integer userId) {
        Optional<User> loggedInUser = userDAO.findByPseudo(securityContext.getUserPrincipal().getName());

        Optional<User> user = userDAO.findById(userId);
        Optional<Houseshare> houseshare = houseshareDAO.findById(houseshareId);

        if (loggedInUser.isPresent()) {
            if (houseshare.isEmpty()) {
                return buildErrorResponse(
                        Response.Status.NOT_FOUND,
                        ErrorCode.NOT_FOUND,
                        "Houseshare not found",
                        "The houseshare you are trying to manage does not exist");
            }

            if (!houseshare.get().getManager().equals(loggedInUser.get())) {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }

            if (user.isEmpty()) {
                return buildErrorResponse(
                        Response.Status.NOT_FOUND,
                        ErrorCode.NOT_FOUND,
                        "User not found",
                        "The user you gave does not exist in our database");
            }

            if (houseshare.get().getUsers().contains(user.get())) {
                houseshare.get().getUsers().remove(user.get());
                Houseshare houseshareResult = houseshareDAO.update(houseshare.get());

                return Response.ok().entity(houseshareResult.getUsers()).build();
            } else {
                return buildErrorResponse(
                        Response.Status.BAD_REQUEST,
                        ErrorCode.ALREADY_EXIST,
                        "User already exist",
                        "You are trying to remove an user which does not exist either in the houseshare.");
            }
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }


    /**
     * Récupère les points d'utilisateur connecté
     *
     * @return le nombre de points
     */
    @GET
    @Path("/{houseshareId}/myPoints")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMembersPoint(@PathParam("houseshareId") int houseshareId) {
        Optional<User> loggedInUser = userDAO.findByPseudo(securityContext.getUserPrincipal().getName());
        Optional<Houseshare> houseshare = houseshareDAO.findById(houseshareId);

        if (loggedInUser.isPresent()) {
            if (houseshare.isEmpty()) {
                return buildHouseshareNotFoundErrorResponse();
            }

            if (!houseshare.get().getUsers().contains(loggedInUser.get())) {
                return buildUserNotInHouseshareErrorResponse();
            }

            int points = achievedServiceDAO.getPointsByUser(houseshare.get(), loggedInUser.get(), true);

            Map<String, Integer> result = new HashMap<>();
            result.put("points", points);

            return Response.ok(result).build();

        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * Récupère les points d'utilisateur d'id
     *
     * @return le nombre de points
     */
    @GET
    @Path("/{houseshareId}/points")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMemberPoint(@NotNull @PathParam("houseshareId") Integer houseshareId,
                                   @QueryParam("userId") Integer userId) {
        Optional<User> loggedInUser = userDAO.findByPseudo(securityContext.getUserPrincipal().getName());
        Optional<Houseshare> houseshare = houseshareDAO.findById(houseshareId);

        if (loggedInUser.isPresent()) {
            if (houseshare.isEmpty()) {
                return buildHouseshareNotFoundErrorResponse();
            }

            if (!houseshare.get().getUsers().contains(loggedInUser.get())) {
                return buildUserNotInHouseshareErrorResponse();
            }

            if (userId != null) { // on check les points pour l'user donné
                Optional<User> user = userDAO.findById(userId);

                if (user.isEmpty()) {
                    return buildErrorResponse(
                            Response.Status.NOT_FOUND,
                            ErrorCode.NOT_FOUND,
                            "User not found",
                            "The user does not exist in our database");
                }

                if (!houseshare.get().getUsers().contains(user.get())) {
                    return buildErrorResponse(
                            Response.Status.NOT_FOUND,
                            ErrorCode.NOT_FOUND,
                            "User not in the houseshare",
                            "The user is not in the given houseshare.");
                } else {
                    int points = achievedServiceDAO.getPointsByUser(houseshare.get(), user.get(), true);

                    Map<String, Integer> result = new HashMap<>();
                    result.put("points", points);

                    return Response.ok(result).build();
                }

            } else { // on check les points pour tout les users de l'houseshares
                List<User> members = houseshare.get().getUsers();
                List<Map<String, Integer>> membersPoint = new ArrayList<>();
                for (User member : members) {
                    int points = achievedServiceDAO.getPointsByUser(houseshare.get(), member, true);

                    Map<String, Integer> result = new HashMap<>();
                    result.put("userId", member.getUserId());
                    result.put("points", points); // il faut le faire, mais c'est pas urgent et c'est vite fait
                    membersPoint.add(result);
                }

                return Response.ok(membersPoint).build();
            }
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    private Response buildHouseshareNotFoundErrorResponse() {
        return buildErrorResponse(
                Response.Status.NOT_FOUND,
                ErrorCode.NOT_FOUND,
                "Houseshare not found",
                "The houseshare you are trying to manage does not exist.");
    }

    private Response buildUserNotInHouseshareErrorResponse() {
        return buildErrorResponse(
                Response.Status.UNAUTHORIZED,
                ErrorCode.UNAUTHORIZED_ERROR,
                "User not in the houseshare",
                "You are not in the houseshare that you gave.");
    }
}
