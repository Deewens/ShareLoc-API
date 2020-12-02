package shareloc.utils;

public class ParamError {
    private String code;
    private String field;
    private String value;
    private String message;

    public ParamError() {}

    public ParamError(String code, String field, String value, String message) {
        this.code = code;
        this.field = field;
        this.value = value;
        this.message = message;
    }

    public ParamError(String code, String field, String message) {
        this.code = code;
        this.field = field;
        this.value = "";
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
