package shareloc.utils;

import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.Response;
import shareloc.model.ejb.User;
import shareloc.model.validation.ValidationErrorResponse;

import java.util.*;

public final class CustomResponse {
    /**
     * Build a Response Object
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
     * Build a Response Object
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
}
