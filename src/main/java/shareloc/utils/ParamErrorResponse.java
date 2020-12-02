package shareloc.utils;

import java.util.List;

public class ParamErrorResponse {
    private String type;
    private String title;
    private List<ParamError> errors;

    public ParamErrorResponse(String type, String title, List<ParamError> errors) {
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

    public List<ParamError> getErrors() {
        return errors;
    }

    public void setErrors(List<ParamError> errors) {
        this.errors = errors;
    }
}
