package shareloc.utils;

import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.Response;
import shareloc.model.ejb.User;
import shareloc.model.validation.ValidationErrorResponse;

import java.io.File;
import java.util.*;

public final class CustomResponse {
    /**
     * Build a Generic Response error Object
     *
     * @param httpCode HTTP Request Status code (integer or Response.Status object)
     * @param code Custom code using ErrorCode class
     * @param title Title of the error
     * @param message detailled message of the error
     * @return Constructed response
     */
    public static Response buildErrorResponse(Response.Status httpCode, String code, String title, String message) {
        Map<String, String> error = new HashMap<>();
        error.put("type", "link");
        error.put("title", title);
        error.put("code", code);
        error.put("message", message);

        GenericEntity<Map<String, String>> entity = new GenericEntity<>(error) {};

        return Response.status(httpCode).entity(entity).build();
    }

    /**
     * Build a generic Response error Object
     *
     * @param httpCode HTTP Request Status code (integer or Response.Status object)
     * @param code Custom code using ErrorCode class
     * @param title Title of the error
     * @param message detailled message of the error
     * @return Constructed response
     */
    public static Response buildErrorResponse(int httpCode, String code, String title, String message) {
        Map<String, String> error = new HashMap<>();
        error.put("type", "link");
        error.put("title", title);
        error.put("code", code);
        error.put("message", message);

        GenericEntity<Map<String, String>> entity = new GenericEntity<>(error) {};

        return Response.status(httpCode).entity(entity).build();
    }

    public static Response buildValidationErrorResponse(int httpCode, String title, ValidationErrorResponse.ValidationError ...validationErrors) {
        List<ValidationErrorResponse.ValidationError> errors = new ArrayList<>();
        errors = Arrays.asList(validationErrors.clone());

        ValidationErrorResponse errorResponse = new ValidationErrorResponse("link", title, errors);
        GenericEntity<ValidationErrorResponse> entity = new GenericEntity<>(errorResponse) {};

        return Response.status(httpCode).entity(entity).build();
    }

    /**
     * @return Houseshare not found error response
     */
    public static Response buildHouseshareNotFoundErrorResponse() {
        return buildErrorResponse(
                Response.Status.NOT_FOUND,
                ErrorCode.NOT_FOUND,
                "Houseshare not found",
                "The houseshare you are trying to access does not exist.");
    }

    /**
     * @return User not in houseshare error response
     */
    public static Response buildUserNotInHouseshareErrorResponse() {
        return buildErrorResponse(
                Response.Status.UNAUTHORIZED,
                ErrorCode.UNAUTHORIZED_ERROR,
                "User not in the houseshare",
                "You are not in the houseshare that you gave in ID.");
    }

    /**
     * @return File upload error response
     */
    public static Response buildFileUploadErrorResponse() {
        return buildErrorResponse(
                Response.Status.BAD_REQUEST,
                ErrorCode.FILE_UPLOAD_FAILED,
                "File upload failed",
                "The file you have sent failed the upload"
        );
    }

    public static Response buildAchievedServiceNotFoundResponse() {
        return buildErrorResponse(
                Response.Status.NOT_FOUND,
                ErrorCode.NOT_FOUND,
                "Achieved service not found",
                "The achieved service you gave does not exist"
        );
    }

    public static Response buildImageNotFoundErrorResponse() {
        File imageNotFound = new File("../uploadedImages/imageNotFound.jpg");
        if (!imageNotFound.exists()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.NOT_FOUND).entity(imageNotFound).build();
    }
}
