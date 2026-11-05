package rickwporter.prettytable.example.pets;

public final class PetstoreException extends Exception {
    private static final long serialVersionUID = 1L;

    private final int status;
    private final Object body;

    PetstoreException(int statusCode, Object responseBody) {
        super("Petstore returned HTTP " + statusCode);
        this.status = statusCode;
        this.body = responseBody;
    }

    public int getStatus() {
        return this.status;
    }

    public Object getBody() {
        return this.body;
    }
}
