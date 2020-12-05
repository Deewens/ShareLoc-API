package shareloc.resources;

import shareloc.model.dao.HouseshareDAO;
import shareloc.model.dao.UserDAO;
import shareloc.model.ejb.Houseshare;
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

    @PATCH
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateHouseshare(@PathParam("id") int id, @NotNull Houseshare obj) {
        Optional<User> user = userDAO.findByPseudo(securityContext.getUserPrincipal().getName());
        Optional<Houseshare> houseshare = houseshareDAO.findById(id);

        String name = obj.getName();
        if (user.isPresent() && houseshare.isPresent()) {
            if (UserRight.isUserManager(user.get(), houseshare.get())) {
                List<ParamError> errors = new ArrayList<>();

                if (name == null || name.isBlank()) {
                    errors.add(new ParamError(ErrorCode.PARAM_EMPTY, "name", "Name must not be empty."));
                    ParamErrorResponse errorResponse = new ParamErrorResponse("link", "Validation errors", errors);
                    return Response.status(422).entity(errorResponse).build();
                } else {
                    houseshare.get().setName(obj.getName());
                    Houseshare response = houseshareDAO.update(houseshare.get());
                    if (response == null) {
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
                    }
                    return Response.ok(houseshare).build();
                }
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
