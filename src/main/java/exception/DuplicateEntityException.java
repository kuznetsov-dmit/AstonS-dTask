package exception;

public class DuplicateEntityException extends LibraryException {
    private final String entityName;
    private final String fieldName;
    private final String value;

    public DuplicateEntityException(String entityName, String fieldName, String value) {
        super(String.format("%s with %s '%s' already exists", entityName, fieldName, value));
        this.entityName = entityName;
        this.fieldName = fieldName;
        this.value = value;
    }

    public String getEntityName() {
        return entityName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getValue() {
        return value;
    }
}
