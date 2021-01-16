package shareloc.utils;

public class DatabaseError {
    private String code;
    private String entity;
    private String message;

    public DatabaseError() {}

    public DatabaseError(String code, String entity, String message) {
        this.code = code;
        this.entity = entity;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
