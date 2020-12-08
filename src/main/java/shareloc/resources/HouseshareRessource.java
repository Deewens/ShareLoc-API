package shareloc.resources;

import shareloc.model.dao.HouseshareDAO;
import shareloc.model.dao.ServiceDAO;
import shareloc.model.dao.UserDAO;
import shareloc.model.ejb.Houseshare;
import shareloc.model.ejb.Service;
import shareloc.model.ejb.User;
import shareloc.security.SignInNeeded;
import shareloc.utils.*;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
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

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateHouseshare(@PathParam("id") int id, @NotNull Houseshare obj) {
        Optional<User> user = userDAO.findByPseudo(securityContext.getUserPrincipal().getName());
        Optional<Houseshare> houseshare = houseshareDAO.findById(id);

        if (user.isPresent() && houseshare.isPresent()) {
            List<ParamError> errors = new ArrayList<>();
            if (obj.getName() == null || obj.getName().isBlank()) {
                errors.add(new ParamError(ErrorCode.PARAM_EMPTY, "name", "Name must not be empty."));
            } else {
                houseshare.get().setName(obj.getName());
            }

            if (obj.getManager() == null) {
                errors.add(new ParamError(ErrorCode.PARAM_EMPTY, "manager", "The manager must be provided."));
            } else {
                Optional<User> manager = userDAO.findById(obj.getManager().getUserId());
                if (manager.isEmpty()) {
                    errors.add(new ParamError(ErrorCode.PARAM_EMPTY, "manager", "The given manager does not exist."));
                } else {
                    houseshare.get().setManager(manager.get());
                }
            }

            if (obj.getUsers().isEmpty()) {
                errors.add(new ParamError(ErrorCode.BAD_FORMAT, "users", "Users must be a json array of user."));
            } else {
                List<User> userList = new ArrayList<>();
                for (User value : obj.getUsers()) {
                    Optional<User> us = userDAO.findById(value.getUserId());
                    if (us.isEmpty()) {
                        errors.add(new ParamError(ErrorCode.ENTITY_NOT_FOUND, "users", "One of the user given does not exist."));
                    } else {
                        userList.add(us.get());
                    }
                }
                houseshare.get().setUsers(userList);
            }

            if (obj.getHouseshareServices().isEmpty()) {
                errors.add(new ParamError(ErrorCode.BAD_FORMAT, "houseshareServices", "HouseshareServices must be a json array of service."));
            } else {
                List<Service> serviceList = new ArrayList<>();
                for (Service value : obj.getHouseshareServices()) {
                    Optional<Service> service = serviceDAO.findById(value.getServiceId());
                    if (service.isEmpty()) {
                        errors.add(new ParamError(ErrorCode.ENTITY_NOT_FOUND, "houseshareServices", "One of the service given does not exist."));
                    } else {
                        serviceList.add(service.get());
                    }
                }
                houseshare.get().setHouseshareServices(serviceList);
            }

            if (!errors.isEmpty()) {
                ParamErrorResponse errorResponse = new ParamErrorResponse("link", "Validation errors", errors);
                return Response.status(422).entity(errorResponse).build();
            } else if (UserRight.isUserManager(user.get(), houseshare.get())) {
                Houseshare response = houseshareDAO.update(obj);
                if (response == null) {
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
                }
                return Response.ok(houseshare).build();
            }
        }
        return Response.status(Response.Status.NOT_FOUND).build(); // Renvoie NOT_FOUND par sécurité, de sorte à ce que l'utilisateur ne sache pas si l'houseshare existe alors qu'il n'a pas les droits dessus
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createHouseshare(@NotNull Houseshare obj) {
        String name = obj.getName();
        List<ParamError> errors = new ArrayList<>();
        if (name != null && !name.isBlank()) {
            if (houseshareDAO.findByName(name).isPresent()) {
                errors.add(new ParamError(ErrorCode.ALREADY_EXIST, "name", name, "Name already exist."));
            }
        } else {
            errors.add(new ParamError(ErrorCode.PARAM_EMPTY, "name", name, "Name must not be empty."));
        }

        if(errors.isEmpty()) {
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

        ParamErrorResponse errorResponse = new ParamErrorResponse("link", "Validation errors", errors);
        return Response.status(422).entity(errorResponse).build();
    }
}
