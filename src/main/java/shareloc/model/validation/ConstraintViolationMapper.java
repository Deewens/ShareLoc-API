package shareloc.model.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ElementKind;
import jakarta.validation.Path;
import jakarta.validation.constraints.*;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import shareloc.model.validation.customconstraints.Date;
import shareloc.utils.ErrorCode;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.StreamSupport;

@Provider
public class ConstraintViolationMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();

        List<ValidationErrorResponse.ValidationError> errors = new ArrayList<>();

        for (ConstraintViolation<?> violation : violations) {
            String property = getConstraintProperty(violation);

            errors.add(new ValidationErrorResponse.ValidationError(
                    getConstraintType(violation),
                    property,
                    violation.getInvalidValue(),
                    property + " " + violation.getMessage())
            );

        }

        ValidationErrorResponse response = new ValidationErrorResponse("url", "Validation error", errors);

        return Response.status(422).entity(response).build();
    }

    /**
     * @param violation the violation
     * @return name of the property which was not validated
     */
    private String getConstraintProperty(ConstraintViolation<?> violation) {
        Path propertyPath = violation.getPropertyPath();
        Optional<Path.Node> leafNodeOptional = StreamSupport.stream(propertyPath.spliterator(), false).reduce((a, b) -> b);

        if (leafNodeOptional.isPresent()) {
            Path.Node leafNode =leafNodeOptional.get();

            if (ElementKind.PROPERTY == leafNode.getKind()) {
                return leafNode.getName();
            }
        }

        return null;
    }

    /**
     * @param violation the violation
     * @return custom code of the error
     */
    private String getConstraintType(ConstraintViolation<?> violation) {
        Annotation annotation = violation.getConstraintDescriptor().getAnnotation();

        if (annotation instanceof Email) {
            return ErrorCode.BAD_FORMAT;
        }

        if (annotation instanceof NotEmpty) {
            return ErrorCode.EMPTY;
        }

        if (annotation instanceof NotBlank) {
            return ErrorCode.BLANK;
        }

        if (annotation instanceof NotNull) {
            return ErrorCode.NULL;
        }

        if (annotation instanceof Date) {
            return ErrorCode.DATE_ERROR;
        }

        if (annotation instanceof Size) {
            return ErrorCode.TOO_SHORT;
        }

        return ErrorCode.UNDEFINED_ERROR;
    }

}
