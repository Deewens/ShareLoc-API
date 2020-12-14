package shareloc.model.validation;

import java.util.List;

public class ValidationErrorResponse {
    private String type;
    private String title;
    private List<ValidationError> errors;

    public ValidationErrorResponse() {}

    public ValidationErrorResponse(String type, String title, List<ValidationError> errors) {
        this.type = type;
        this.title = title;
        this.errors = errors;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ValidationError> getErrors() {
        return errors;
    }

    public void setErrors(List<ValidationError> errors) {
        this.errors = errors;
    }

    public static class ValidationError {
        private String code;
        private String field;
        private Object value;
        private String message;

        public ValidationError() {}

        public ValidationError(String code, String field, Object value, String message) {
            this.code = code;
            this.field = field;
            this.value = value;
            this.message = message;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
