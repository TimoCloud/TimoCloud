package cloud.timo.TimoCloud.api.internal.links;

public class LinkResolveException extends RuntimeException {

    private final IdentifiableLink link;

    public LinkResolveException(IdentifiableLink link) {
        this.link = link;
    }

    @Override
    public String getMessage() {
        return String.format("Unable to resolve link to '%s': Object not found. Please report this.", link.getId());
    }
}
