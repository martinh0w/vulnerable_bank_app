package ninja.jira.skeletonkey.app.utility;

/**
 * This class builds error/success messages to be sent.
 */
public class StatusBuilder {

    private String status;
    private String message;

    /**
     * Constructor for class StatusBuilder
     * @param status error/success depending on outcome of query
     * @param message for more elaboration of status
     */
    public StatusBuilder(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}