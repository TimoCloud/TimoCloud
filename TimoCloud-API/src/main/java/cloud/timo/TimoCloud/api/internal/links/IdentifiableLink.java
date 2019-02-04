package cloud.timo.TimoCloud.api.internal.links;

import cloud.timo.TimoCloud.api.objects.IdentifiableObject;


/**
 * Links are used for references between API objects in order to prevent cycles in serialization
 */
public abstract class IdentifiableLink<T extends IdentifiableObject> implements IdentifiableObject {

    private String id;
    private String name;

    public IdentifiableLink(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public T resolve() {
        T target = findTarget();
        if (target == null) throw new LinkResolveException(this);
        return target;
    }

    /**
     * Looks for the target object internally and returns null if not found
     */
    abstract T findTarget();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IdentifiableLink<?> that = (IdentifiableLink<?>) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
