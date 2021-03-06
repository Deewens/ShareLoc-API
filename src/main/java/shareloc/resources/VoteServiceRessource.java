package shareloc.resources;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.groups.ConvertGroup;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import shareloc.model.dao.HouseshareDAO;
import shareloc.model.dao.ServiceDAO;
import shareloc.model.dao.UserDAO;
import shareloc.model.dao.VoteServiceDAO;
import shareloc.model.ejb.Houseshare;
import shareloc.model.ejb.Service;
import shareloc.model.ejb.User;
import shareloc.model.ejb.VoteService;
import shareloc.model.validation.groups.VoteServiceConstraints;
import shareloc.security.SignInNeeded;
import shareloc.utils.ErrorCode;

import java.util.List;
import java.util.Optional;

import static shareloc.utils.CustomResponse.buildErrorResponse;

@SignInNeeded
@Path("/houseshares/{houseshareId}/voteservice/{serviceId}")
public class VoteServiceRessource {

    @PathParam("houseshareId")
    private Integer houseshareId;

    @PathParam("serviceId")
    private Integer serviceId;

    // CONTEXT
    @Context
    SecurityContext securityContext;

    @Context
    UriInfo uriInfo;

    // DAO
    @Inject
    UserDAO userDAO;

    @Inject
    HouseshareDAO houseshareDAO;

    @Inject
    ServiceDAO serviceDAO;

    @Inject
    VoteServiceDAO voteServiceDAO;

    /**
     * Récupère tous les votes d'un service de la collocation d'id
     *
     * @return La liste de tous les votes de service
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getVoteServices() {
        Optional<User> loggedInUser = userDAO.findByPseudo(securityContext.getUserPrincipal().getName());
        Optional<Houseshare> houseshare = houseshareDAO.findById(this.houseshareId);
        Optional<Service> service = serviceDAO.findById((this.serviceId));

        if (loggedInUser.isPresent()) {

            if (houseshare.isEmpty())
                return buildHouseshareNotFoundErrorResponse();

            if (!houseshare.get().getUsers().contains(loggedInUser.get()))
                return buildUserNotInHouseshareErrorResponse();

            if (service.isEmpty())
                return buildHouseshareServiceNotFoundErrorResponse();

            return Response.ok(voteServiceDAO.findByService(service.get())).build();
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * Crée nouveau vote de service
     *
     * @param voteService VoteService à créer
     * @return L'entité VoteService qui a été créée
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createVoteService(@Valid @ConvertGroup(to = VoteServiceConstraints.CreateVoteServiceConstraint.class) VoteService voteService) {
        Optional<User> loggedInUser = userDAO.findByPseudo(securityContext.getUserPrincipal().getName());
        Optional<Houseshare> houseshare = houseshareDAO.findById(this.houseshareId);
        Optional<Service> service = serviceDAO.findById((this.serviceId));

        if (loggedInUser.isPresent()) {

            if (houseshare.isEmpty())
                return buildHouseshareNotFoundErrorResponse();

            if (!houseshare.get().getUsers().contains(loggedInUser.get()))
                return buildUserNotInHouseshareErrorResponse();

            if (service.isEmpty())
                return buildHouseshareServiceNotFoundErrorResponse();

            Optional<VoteService> votedService = voteServiceDAO.findByVoterAndService(loggedInUser.get(), service.get());
            if(!votedService.isEmpty())
                return buildUserAlreadyVoted();

            VoteService voteServiceCreated = voteServiceDAO.create(
                    new VoteService(loggedInUser.get(), service.get(), voteService.getVoteType(), voteService.getVote())
            );

            List<VoteService> voteList = voteServiceDAO.findByService(service.get());
            List<User> users = houseshareDAO.findById(houseshareId).get().getUsers();

            if (voteList.size() == users.size()) {
                int positiveVote = 0;
                int nevagiteVote = 0;

                for (int i = 0; i < voteList.size(); i++) {
                    if (voteList.get(i).getVote()) {
                        positiveVote++;
                    } else {
                        nevagiteVote++;
                    }
                }
                if (voteService.getVoteType() == 0) {
                    if (positiveVote > nevagiteVote) {
                        serviceDAO.update(
                                new Service(
                                        serviceId,
                                        service.get().getHouseshare(),
                                        service.get().getTitle(),
                                        service.get().getDescription(),
                                        service.get().getCost(),
                                        1
                                )
                        );
                    } else {
                        serviceDAO.delete(service.get());
                    }

                    List<VoteService> voteToDelete = voteServiceDAO.findByService(service.get());

                    for (int i = 0; i<voteToDelete.size(); i++) {
                        voteServiceDAO.delete(voteToDelete.get(i));
                    }
                } else if (voteService.getVoteType() == 1) {
                    if (positiveVote > nevagiteVote) {
                        serviceDAO.update(
                                new Service(
                                        serviceId,
                                        service.get().getHouseshare(),
                                        service.get().getTitle(),
                                        service.get().getDescription(),
                                        service.get().getCost(),
                                        0
                                )
                        );
                    } else {
                        serviceDAO.update(
                                new Service(
                                        serviceId,
                                        service.get().getHouseshare(),
                                        service.get().getTitle(),
                                        service.get().getDescription(),
                                        service.get().getCost(),
                                        1
                                )
                        );
                    }

                    List<VoteService> voteToDelete = voteServiceDAO.findByService(service.get());

                    for (int i = 0; i<voteToDelete.size(); i++) {
                        voteServiceDAO.delete(voteToDelete.get(i));
                    }
                }
            }

            return Response.created(uriInfo.getAbsolutePath()).entity(voteServiceCreated).build();
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    // TODO methode PUT

    private Response buildHouseshareNotFoundErrorResponse() {
        return buildErrorResponse(
                Response.Status.NOT_FOUND,
                ErrorCode.NOT_FOUND,
                "Houseshare not found",
                "The houseshare you are trying to access does not exist.");
    }

    private Response buildUserNotInHouseshareErrorResponse() {
        return buildErrorResponse(
                Response.Status.UNAUTHORIZED,
                ErrorCode.UNAUTHORIZED_ERROR,
                "User not in the houseshare",
                "You are not in the houseshare that you gave.");
    }

    private Response buildHouseshareServiceNotFoundErrorResponse() {
        return buildErrorResponse(
                Response.Status.NOT_FOUND,
                ErrorCode.NOT_FOUND,
                "Service not found",
                "The service you gave in id does not exist."
        );
    }

    private Response buildUserAlreadyVoted() {
        return buildErrorResponse(
                Response.Status.UNAUTHORIZED,
                ErrorCode.UNDEFINED_ERROR,
                "User voted this service",
                "You have already voted this service."
        );
    }

}
