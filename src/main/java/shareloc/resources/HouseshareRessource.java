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
import shareloc.model.ejb.AchievedService;
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

    // Cherche la liste des co-locations de l'utilisateur
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHouseshare() {
        Optional<User> user = userDAO.findByPseudo(securityContext.getUserPrincipal().getName());

        if (user.isPresent()) {
            List<Houseshare> houseshare = houseshareDAO.findByUser(user.get());
            return Response.ok(houseshare).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();

    }

    // Cherche une co-location par son ID si l'utilsateur est dedans
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


    // Houseshare members
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

    @POST
    @Path("/{id}/users/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response putUserInHouseshare(@NotNull @PathParam("id") Integer id, @Valid @ConvertGroup(to = HouseshareConstraints.PostUsersConstraint.class) User userParam) {
        Optional<User> loggedInUser = userDAO.findByPseudo(securityContext.getUserPrincipal().getName());

        if (loggedInUser.isPresent()) {
            Optional<User> user = userDAO.findById(userParam.getUserId());
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
                Houseshare houseshareResult = houseshareDAO.update(houseshare.get());

                return Response.status(Response.Status.CREATED).entity(houseshareResult.getUsers()).build();
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


    @GET
    @Path("/{houseshareId}/myPoints")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMembersPoint() {
        return null;
    }

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
                    int negativePoints = achievedServiceDAO.countNegativePointsByUser(
                            houseshare.get(),
                            user.get(),
                            false); // mettre isValid à true après les tests

                    int positivePoints = achievedServiceDAO.countPositivePointsByUser(
                            houseshare.get(),
                            user.get(),
                            false); // mettre isValid à true après les tests*/

                    Map<String, Integer> result = new HashMap<>();
                    result.put("points", positivePoints-negativePoints);

                    List<AchievedService> achievedServiceList = achievedServiceDAO.findByHouseshare(houseshare.get());
                    for(AchievedService value : achievedServiceList) {

                    }
                    System.out.println("Achieved service groupped by :" + achievedServiceList.toString());


                    return Response.ok(result).build();
                }

            } else { // on check les points pour tout les users de l'houseshare
                /*List<AchievedService> achievedServiceListToUser = achievedServiceDAO.countNegativePoints(
                        houseshare.get(),
                        false); // mettre isValid à true après les tests

                List<AchievedService> achievedServiceListFromUser = achievedServiceDAO.countPositivePoints(
                        houseshare.get(),
                        false); // mettre isValid à true après les tests

                int negativePoint = 0;
                int positivePoint = 0;

                for (AchievedService achievedService : achievedServiceListToUser) {

                    negativePoint = negativePoint + achievedService.getService().getCost();
                };

                for (AchievedService achievedService : achievedServiceListFromUser) {
                    positivePoint = positivePoint + achievedService.getService().getCost();
                };

                int result = positivePoint - negativePoint;

                return Response.ok(result).build();*/
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
