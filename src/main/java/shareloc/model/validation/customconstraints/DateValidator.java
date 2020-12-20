package shareloc.model.validation.customconstraints;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateValidator implements ConstraintValidator<Date, String> {

    @Override
    public void initialize(Date constraintAnnotation) {

    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null)
            return false;

        if (s.length() == 0)
            return false;

        try {
            new SimpleDateFormat("yyyy-MM-dd").parse(s);
        } catch (ParseException e) {
            return false;
        }

        return true;
    }
}
