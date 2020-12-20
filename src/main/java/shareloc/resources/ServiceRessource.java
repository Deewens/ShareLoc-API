package shareloc.resources;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.groups.ConvertGroup;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import shareloc.model.dao.HouseshareDAO;
import shareloc.model.dao.ServiceDAO;
import shareloc.model.dao.UserDAO;
import shareloc.model.ejb.Houseshare;
import shareloc.model.ejb.Service;
import shareloc.model.ejb.User;
import shareloc.model.validation.groups.ServiceConstraints;
import shareloc.security.SignInNeeded;
import shareloc.utils.ErrorCode;

import java.util.Optional;

import static shareloc.utils.CustomResponse.buildErrorResponse;

@SignInNeeded
@Path("/houseshares/{houseshareId}/services")
public class ServiceRessource {
    @PathParam("houseshareId")
    private Integer houseshareId;

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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getServices() {
        Optional<User> loggedInUser = userDAO.findByPseudo(securityContext.getUserPrincipal().getName());
        Optional<Houseshare> houseshare = houseshareDAO.findById(this.houseshareId);

        if (loggedInUser.isPresent()) {
            if (houseshare.isEmpty()) {
                return buildHouseshareNotFoundErrorResponse();
            }

            if (!houseshare.get().getUsers().contains(loggedInUser.get())) {
                return buildUserNotInHouseshareErrorResponse();
            }

            return Response.ok(serviceDAO.findByHouseshare(houseshare.get())).build();
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    @GET
    @Path("{serviceId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getService(@NotNull @PathParam("serviceId") Integer serviceId) {
        Optional<User> loggedInUser = userDAO.findByPseudo(securityContext.getUserPrincipal().getName());
        Optional<Houseshare> houseshare = houseshareDAO.findById(this.houseshareId);

        if (loggedInUser.isPresent()) {
            if (houseshare.isEmpty()) {
                return buildHouseshareNotFoundErrorResponse();
            }

            if (!houseshare.get().getUsers().contains(loggedInUser.get())) {
                return buildUserNotInHouseshareErrorResponse();
            }

            Optional<Service> service = serviceDAO.findById(serviceId);
            if (service.isPresent()) {
                return Response.ok(service.get()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createService(@Valid @ConvertGroup(to = ServiceConstraints.CreateServiceConstraint.class) Service service) {
        Optional<User> loggedInUser = userDAO.findByPseudo(securityContext.getUserPrincipal().getName());
        Optional<Houseshare> houseshare = houseshareDAO.findById(this.houseshareId);

        if (loggedInUser.isPresent()) {
            if (houseshare.isEmpty()) {
                return buildHouseshareNotFoundErrorResponse();
            }

            if (!houseshare.get().getUsers().contains(loggedInUser.get())) {
                return buildUserNotInHouseshareErrorResponse();
            }

            Service serviceCreated = serviceDAO.create(new Service(houseshare.get(), service.getTitle(), service.getDescription(), service.getCost()));

            return Response.created(uriInfo.getAbsolutePath()).entity(serviceCreated).build();
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }


    @PUT
    @Path("{serviceId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateService(@NotNull @PathParam("serviceId") Integer serviceId,
                                  @Valid @ConvertGroup(to = ServiceConstraints.UpdateServiceConstraint.class) Service service) {
        Optional<User> loggedInUser = userDAO.findByPseudo(securityContext.getUserPrincipal().getName());
        if (loggedInUser.isPresent()) {
            Optional<Houseshare> houseshare = houseshareDAO.findById(this.houseshareId);
            if (houseshare.isEmpty()) {
                return buildHouseshareNotFoundErrorResponse();
            }

            if (!houseshare.get().getUsers().contains(loggedInUser.get())) {
                return buildUserNotInHouseshareErrorResponse();
            }

            Optional<Service> serviceToUpdate = serviceDAO.findById(serviceId);
            if (serviceToUpdate.isEmpty()) {
                return buildHouseshareServiceNotFoundErrorResponse();
            }

            Service serviceUpdated = serviceDAO.update(
                    new Service(
                            serviceToUpdate.get().getHouseshare(),
                            service.getTitle(),
                            service.getDescription(),
                            service.getCost()
                    )
            );

            return Response.ok(serviceUpdated).build();
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    @DELETE
    @Path("{serviceId}")
    public Response deleteService(@NotNull @PathParam("serviceId") Integer serviceId) {
        Optional<User> loggedInUser = userDAO.findByPseudo(securityContext.getUserPrincipal().getName());
        if (loggedInUser.isPresent()) {
            Optional<Houseshare> houseshare = houseshareDAO.findById(this.houseshareId);
            if (houseshare.isEmpty()) {
                return buildHouseshareNotFoundErrorResponse();
            }

            if (!houseshare.get().getUsers().contains(loggedInUser.get())) {
                return buildUserNotInHouseshareErrorResponse();
            }

            Optional<Service> serviceToUpdate = serviceDAO.findById(serviceId);
            if (serviceToUpdate.isEmpty()) {
                return buildHouseshareServiceNotFoundErrorResponse();
            }

            return Response.status(Response.Status.NO_CONTENT).build();
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

    private Response buildHouseshareServiceNotFoundErrorResponse() {
        return buildErrorResponse(
                Response.Status.NOT_FOUND,
                ErrorCode.NOT_FOUND,
                "Service not found",
                "The service you gave in id does not exist."
        );
    }
}
