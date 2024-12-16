package exception;

public class EntityNotFoundException extends LibraryException {
    private final String entityName;
    private final String identifier;

    public EntityNotFoundException(String entityName, String identifier) {
        super(String.format("%s not found with identifier: %s", entityName, identifier));
        this.entityName = entityName;
        this.identifier = identifier;
    }

    public String getEntityName() {
        return entityName;
    }

    public String getIdentifier() {
        return identifier;
    }
}
