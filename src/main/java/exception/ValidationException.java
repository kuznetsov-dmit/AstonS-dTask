package exception;

import java.util.HashMap;
import java.util.Map;

public class ValidationException extends LibraryException {
    private final Map<String, String> errors;

    public ValidationException(String message, Map<String, String> errors) {
        super(message);
        this.errors = new HashMap<>(errors);
    }

    public ValidationException(String field, String error) {
        super(String.format("Validation failed for field '%s': %s", field, error));
        this.errors = new HashMap<>();
        this.errors.put(field, error);
    }

    public Map<String, String> getErrors() {
        return new HashMap<>(errors);
    }
}