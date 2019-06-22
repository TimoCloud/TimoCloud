package cloud.timo.TimoCloud.api.implementations.storage;

import cloud.timo.TimoCloud.api.objects.IdentifiableObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class IdentifiableObjectStorage<T extends IdentifiableObject> {

    private Map<String, Collection<T>> byName;
    private Map<String, T> byId;

    public IdentifiableObjectStorage() {
        byName = new HashMap<>();
        byId = new HashMap<>();
    }

    public T getById(String id) {
        if (id == null) return null;
        return byId.get(id);
    }

    public T getByName(String name) {
        if (name == null) return null;
        name = name.toLowerCase();
        Collection<T> identifiables = byName.get(name);
        if (identifiables == null || identifiables.size() == 0) return null;
        return identifiables.iterator().next();
    }

    /**
     * @param identifier Either id or name
     * @return Identifiable whose name or id matches the given identifier
     */
    public T getByIdentifier(String identifier) {
        if (identifier == null) return null;
        T idResult = getById(identifier);
        return idResult != null ? idResult : getByName(identifier);
    }

    public void add(T identifiable) {
        remove(identifiable);
        byId.put(identifiable.getId(), identifiable);
        byName.putIfAbsent(identifiable.getName().toLowerCase(), new LinkedHashSet<>());
        byName.get(identifiable.getName().toLowerCase()).add(identifiable);
    }

    public void remove(T identifiable) {
        byId.remove(identifiable.getId());
        if (byName.containsKey(identifiable.getName().toLowerCase())) {
            byName.get(identifiable.getName().toLowerCase()).remove(identifiable);
        }
    }

    public void update(T identifiable) { // Called when keys like name or public key changed
        remove(identifiable);
        add(identifiable);
    }

    public boolean contains(T identifiable) {
        return getById(identifiable.getId()) != null || getByName(identifiable.getName()) != null;
    }

    public Collection<T> values() {
        return byId.values();
    }

}
