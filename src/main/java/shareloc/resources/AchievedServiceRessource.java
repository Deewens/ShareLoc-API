package shareloc.resources;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.groups.ConvertGroup;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import shareloc.model.dao.AchievedServiceDAO;
import shareloc.model.dao.HouseshareDAO;
import shareloc.model.dao.ServiceDAO;
import shareloc.model.dao.UserDAO;
import shareloc.model.ejb.AchievedService;
import shareloc.model.ejb.Houseshare;
import shareloc.model.ejb.Service;
import shareloc.model.ejb.User;
import shareloc.model.ejb.json.AchievedServiceJson;
import shareloc.model.validation.groups.AchievedServiceConstraints;
import shareloc.security.SignInNeeded;
import shareloc.utils.ErrorCode;

import java.io.*;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static shareloc.utils.CustomResponse.buildErrorResponse;
import static shareloc.utils.CustomResponse.*;

@SignInNeeded
@Path("/houseshares/{houseshareId}/achieved-services")
public class AchievedServiceRessource {
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

    @Inject
    AchievedServiceDAO achievedServiceDAO;

    /**
     * Récupère la liste de tous les services rendus
     *
     * @return liste des services rendus
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAchievedServices() {
        Optional<User> loggedInUser = userDAO.findByPseudo(securityContext.getUserPrincipal().getName());
        Optional<Houseshare> houseshare = houseshareDAO.findById(this.houseshareId);

        if (loggedInUser.isPresent()) {
            if (houseshare.isEmpty()) {
                return buildHouseshareNotFoundErrorResponse();
            }

            if (!houseshare.get().getUsers().contains(loggedInUser.get())) {
                return buildUserNotInHouseshareErrorResponse();
            }

            List<AchievedService> achievedServices = achievedServiceDAO.findByHouseshare(houseshare.get());

            List<AchievedService> achievedServiceWithImageUrl = new ArrayList<>();
            for (AchievedService achievedService : achievedServices) {
                String fileName = achievedService.getPicture();

                if (!fileName.isBlank()) {
                    String imageLocation = uriInfo.getBaseUri().toString() + "houseshares/" + houseshareId + "/achieved-services/" + achievedService.getAchievedServiceId() + "/download-image";
                    achievedService.setPicture(imageLocation);
                    achievedServiceWithImageUrl.add(achievedService);
                } else {
                    achievedServiceWithImageUrl.add(achievedService);
                }
            }

            return Response.ok(achievedServiceWithImageUrl).build();
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * Récupère le service rendu d'id
     *
     * @param achievedServiceId
     * @return liste des services rendus
     */
    @GET
    @Path("{achievedServiceId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getService(@NotNull @PathParam("achievedServiceId") Integer achievedServiceId) {
        Optional<User> loggedInUser = userDAO.findByPseudo(securityContext.getUserPrincipal().getName());
        Optional<Houseshare> houseshare = houseshareDAO.findById(this.houseshareId);

        if (loggedInUser.isPresent()) {
            if (houseshare.isEmpty()) {
                return buildHouseshareNotFoundErrorResponse();
            }

            if (!houseshare.get().getUsers().contains(loggedInUser.get())) {
                return buildUserNotInHouseshareErrorResponse();
            }

            Optional<AchievedService> achievedService = achievedServiceDAO.findById(achievedServiceId);
            if (achievedService.isPresent()) {
                String fileName = achievedService.get().getPicture();

                if (!fileName.isBlank()) {
                    String imageLocation = uriInfo.getBaseUri().toString() + "houseshares/" + houseshareId + "/achieved-services/" + achievedService.get().getAchievedServiceId() + "/download-image";
                    achievedService.get().setPicture(imageLocation);

                    return Response.ok(achievedService.get()).build();
                }

                return Response.ok(achievedService.get()).build();

            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * Créer le service rendu
     *
     * @param achievedService Le service rendu à créer
     * @return L'entité AchievedService qui a été créé
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createAchievedService(@Valid @ConvertGroup(to = AchievedServiceConstraints.CreateAchievedServiceConstraint.class)
                                          AchievedServiceJson achievedService) {
        Optional<User> loggedInUser = userDAO.findByPseudo(securityContext.getUserPrincipal().getName());
        if (loggedInUser.isPresent()) {
            Optional<Houseshare> houseshare = houseshareDAO.findById(this.houseshareId);
            if (houseshare.isEmpty()) {
                return buildHouseshareNotFoundErrorResponse();
            }

            if (!houseshare.get().getUsers().contains(loggedInUser.get())) {
                return buildUserNotInHouseshareErrorResponse();
            }

            Optional<Service> service = serviceDAO.findById(achievedService.getServiceId());
            if (service.isEmpty()) {
                return buildErrorResponse(
                        Response.Status.NOT_FOUND,
                        ErrorCode.NOT_FOUND,
                        "Service not found",
                        "You can't create an achieved service with a service which does not exist.");
            }

            if (service.get().getStatus() == 0 || service.get().getStatus() == 2) {
                return buildErrorResponse(
                        Response.Status.UNAUTHORIZED,
                        ErrorCode.UNAUTHORIZED_ERROR,
                        "Service with bad status",
                        "You can't use a service with the status 0 or 2."
                );
            }

            Optional<User> beneficiaryUser = userDAO.findById(achievedService.getBeneficiaryId());
            if (beneficiaryUser.isEmpty()) {
                return buildErrorResponse(
                        Response.Status.NOT_FOUND,
                        ErrorCode.NOT_FOUND,
                        "User not found",
                        "The user Id you specified as service beneficiary does not exist.");
            }

            if (!houseshare.get().getUsers().contains(beneficiaryUser.get())) {
                return buildErrorResponse(
                        Response.Status.NOT_FOUND,
                        ErrorCode.NOT_FOUND,
                        "User not found",
                        "The user Id you specified as service beneficiary does not exist in the houseshare.");
            }

            Date date = null;
            try {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(achievedService.getDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            AchievedService achievedServiceCreated = achievedServiceDAO.create(
                    new AchievedService(
                            service.get(),
                            houseshare.get(),
                            loggedInUser.get(),
                            beneficiaryUser.get(),
                            date,
                            "",
                            false
                    )
            );

            return Response.created(uriInfo.getAbsolutePath()).entity(achievedServiceCreated).build();
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * Upload d'une image vers le serveur
     *
     * Image représentant une preuve du service achevé.
     *
     * @param achievedServiceId ID of the achieved service
     * @param enabled
     * @param inputStream Image reçu
     * @param fileDetail metadata de l'image
     * @return Message de confirmation
     */
    @POST
    @Path("/{achievedServiceId}/upload-image")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadImage(
            @PathParam("achievedServiceId") int achievedServiceId,
            @DefaultValue("true") @FormDataParam("enabled") boolean enabled,
            @FormDataParam("file") InputStream inputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail) {

        Optional<User> loggedInUser = userDAO.findByPseudo(securityContext.getUserPrincipal().getName());
        Optional<Houseshare> houseshare = houseshareDAO.findById(this.houseshareId);

        if (loggedInUser.isPresent()) {
            if (houseshare.isEmpty()) {
                return buildHouseshareNotFoundErrorResponse();
            }

            if (!houseshare.get().getUsers().contains(loggedInUser.get())) {
                return buildUserNotInHouseshareErrorResponse();
            }

            Optional<AchievedService> achievedService = achievedServiceDAO.findById(achievedServiceId);
            if (achievedService.isPresent()) {
                String fileNameGiven = fileDetail.getFileName().toLowerCase();
                String sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS_").format(new Date());

                String uploadedFileLocation = "../uploadedImages/" + sdf + fileNameGiven;
                File file = new File(uploadedFileLocation);

                while (file.exists()) {
                    sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS_").format(new Date());
                    uploadedFileLocation = "../uploadedImages/" + sdf + fileNameGiven;
                    file = new File(uploadedFileLocation);
                }


                boolean isFileSaved = saveToFile(inputStream, uploadedFileLocation);

                if (isFileSaved) {
                    String fileName = sdf + fileNameGiven;
                    String imageLocation = uriInfo.getBaseUri().toString() + "houseshares/" + houseshareId + "/achieved-services/" + achievedServiceId + "/download-image";
                    achievedService.get().setPicture(fileName);
                    AchievedService achievedServiceWithImage = achievedServiceDAO.update(achievedService.get());
                    achievedServiceWithImage.setPicture(imageLocation);
                    return Response.status(Response.Status.CREATED).entity(achievedServiceWithImage).build();
                } else {
                    return buildFileUploadErrorResponse();
                }
            } else {
                return buildAchievedServiceNotFoundResponse();
            }
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * Sauvegarde d'un fichier sur le serveur
     *
     * @param uploadedInputStream Fichier à sauvegarder
     * @param uploadedFileLocation Emplacement de la sauvegarde
     */
    private boolean saveToFile(InputStream uploadedInputStream,
                            String uploadedFileLocation) {
        try {
            OutputStream out = null;
            int read = 0;
            byte[] bytes = new byte[1024];

            out = new FileOutputStream(new File(uploadedFileLocation));
            while ((read = uploadedInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * Recupère l'image du service rendu
     *
     * @param achievedServiceId
     * return L'image
     */
    @GET
    @Path("/{achievedServiceId}/download-image")
    @Produces({"image/png", "image/jpeg"})
    public Response getPngImage(@PathParam("achievedServiceId") int achievedServiceId) {
        Optional<User> loggedInUser = userDAO.findByPseudo(securityContext.getUserPrincipal().getName());
        Optional<Houseshare> houseshare = houseshareDAO.findById(this.houseshareId);

        if (loggedInUser.isPresent()) {
            if (houseshare.isEmpty()) {
                return buildImageNotFoundErrorResponse();
            }

            if (!houseshare.get().getUsers().contains(loggedInUser.get())) {
                return buildImageNotFoundErrorResponse();
            }

            Optional<AchievedService> achievedService = achievedServiceDAO.findById(achievedServiceId);
            if (achievedService.isPresent()) {
                String fileName = achievedService.get().getPicture();

                if (fileName.isBlank()) {
                    return buildImageNotFoundErrorResponse();
                }

                String imagePath = "../uploadedImages/" + fileName;

                System.out.println(imagePath);
                File file = new File(imagePath);
                if (!file.exists()) {
                    return buildImageNotFoundErrorResponse();
                }

                return Response.ok(file).build();
            } else {
                return buildImageNotFoundErrorResponse();
            }
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * Met à jour la validation du service achevé
     *
     * @param achievedServiceId ID du service à mettre à jour
     * @param achievedServiceUpdated Service à mettre à jour
     * @return Service achevé mis à jour
     */
    @PATCH
    @Path("{achievedServiceId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateAchievedService(@PathParam("achievedServiceId") int achievedServiceId,
                                  @Valid @ConvertGroup(to = AchievedServiceConstraints.UpdateAchievedServiceConstraint.class) AchievedService achievedServiceUpdated) {
        Optional<User> loggedInUser = userDAO.findByPseudo(securityContext.getUserPrincipal().getName());
        Optional<Houseshare> houseshare = houseshareDAO.findById(this.houseshareId);

        if (loggedInUser.isPresent()) {
            if (houseshare.isEmpty()) {
                return buildHouseshareNotFoundErrorResponse();
            }

            if (!houseshare.get().getUsers().contains(loggedInUser.get())) {
                return buildUserNotInHouseshareErrorResponse();
            }

            Optional<AchievedService> achievedService = achievedServiceDAO.findById(achievedServiceId);
            if (achievedService.isPresent()) {

                achievedService.get().setValid(achievedServiceUpdated.isValid());

                return Response.ok(achievedServiceDAO.update(achievedService.get())).build();

            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    /*@DELETE
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
                return buildErrorResponse(
                        Response.Status.NOT_FOUND,
                        ErrorCode.NOT_FOUND,
                        "Service not found",
                        "The service you are trying to delete does not exist");
            }

            return Response.status(Response.Status.NO_CONTENT).build();
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }*/

}
