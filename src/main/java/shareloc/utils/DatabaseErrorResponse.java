package shareloc.utils;

import java.util.List;

/**
 * Build an error response object for database error. Contains a list of <a href="#{@link}">Database Error</a>
 *
 * @author Adrien Dudon
 */
public class DatabaseErrorResponse {
    private String type;
    private String title;
    private List<DatabaseError> errors;

    /**
     * Constructor
     *
     * @param type Documentation link of the error
     * @param errors Database error list
     */
    public DatabaseErrorResponse(String type, List<DatabaseError> errors) {
        this.type = type;
        this.title = "Database operation error";
        this.errors = errors;
    }

    /**
     * Constructor
     *
     * @param type Documentation link of the error
     * @param title (Optional) Error title (default : "Database operation error")
     * @param errors Database error list
     */
    public DatabaseErrorResponse(String type, String title, List<DatabaseError> errors) {
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

    public List<DatabaseError> getErrors() {
        return errors;
    }

    public void setErrors(List<DatabaseError> errors) {
        this.errors = errors;
    }
}
