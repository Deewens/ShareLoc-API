package shareloc.resources;

import jakarta.inject.Inject;

import jakarta.validation.Valid;
import jakarta.validation.groups.ConvertGroup;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import shareloc.model.dao.HouseshareDAO;
import shareloc.model.dao.MessageDAO;
import shareloc.model.dao.UserDAO;
import shareloc.model.ejb.Houseshare;
import shareloc.model.ejb.Message;
import shareloc.model.ejb.User;
import shareloc.model.validation.groups.MessageConstraints;
import shareloc.security.SignInNeeded;
import shareloc.utils.ErrorCode;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static shareloc.utils.CustomResponse.buildErrorResponse;

@SignInNeeded
@Path("/houseshares/{houseshareId}/messages")
public class MessageRessource {

    @PathParam("houseshareId")
    private Integer houseshareId;

    @Context
    UriInfo uriInfo;

    @Context
    SecurityContext securityContext;

    @Inject
    UserDAO userDAO;

    @Inject
    private MessageDAO messageDAO;

    @Inject
    HouseshareDAO houseshareDAO;

    /**
     * Récupère les messages de collocation d'id
     *
     * @return La liste des messages
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMessages() {

        Optional<User> loggedInUser = userDAO.findByPseudo(securityContext.getUserPrincipal().getName());
        Optional<Houseshare> houseshare = houseshareDAO.findById(this.houseshareId);

        if (loggedInUser.isPresent()) {
            if (houseshare.isEmpty())
                return buildHouseshareNotFoundErrorResponse();

            if (!houseshare.get().getUsers().contains(loggedInUser.get()))
                return buildUserNotInHouseshareErrorResponse();


            List<Message> messages = messageDAO.findByHouseshare(houseshare.get());

            if(!messages.isEmpty()) {
                GenericEntity<List<Message>> entity = new GenericEntity<List<Message>>(messages) {};
                return Response.ok().entity(entity).build();
            }

            return Response.status(Response.Status.NO_CONTENT).build();
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * Crée nouveau message
     *
     * @param obj Message à créer
     * @return L'entité Message qui a été créée
     */
    @SignInNeeded
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createMessage(@Valid @ConvertGroup(to = MessageConstraints.PostConstraint.class) Message obj) {
        Optional<User> loggedInUser = userDAO.findByPseudo(securityContext.getUserPrincipal().getName());

        if (loggedInUser.isPresent()) {
            Optional<Houseshare> houseshare = houseshareDAO.findById(this.houseshareId);

            if (houseshare.isEmpty())
                return buildHouseshareNotFoundErrorResponse();

            String messageContent = obj.getMessageContent(); // voir comment gérer quand le contenu de message est ""
            Date messageDate = new Date(); // voir à récupérer "current_time" de JSON et de créer l'objet date

            Message messageCreated = messageDAO.create(new Message(houseshare.get(), loggedInUser.get(), messageContent, messageDate));
            return Response.created(uriInfo.getAbsolutePath()).entity(messageCreated).build();
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

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

}
