package shareloc.model.validation.customconstraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateValidator.class)
@Documented
public @interface Date {
    String message() default "must be a date in the correct format (e.g.: YYYY-MM-DD)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
