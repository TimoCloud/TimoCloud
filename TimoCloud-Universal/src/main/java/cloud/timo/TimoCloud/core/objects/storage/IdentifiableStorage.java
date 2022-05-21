package cloud.timo.TimoCloud.core.objects.storage;

import cloud.timo.TimoCloud.core.objects.Identifiable;
import cloud.timo.TimoCloud.core.objects.PublicKeyIdentifiable;

import java.security.PublicKey;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class IdentifiableStorage<T extends Identifiable> {

    private final Map<String, Collection<T>> byName = new HashMap<>();
    private final Map<String, T> byId = new HashMap<>();
    private final Map<PublicKey, T> byPublicKey = new HashMap<>();

    public T getById(String id) {
        if (id == null) return null;
        return byId.get(id);
    }

    public T getByName(String name) {
        if (name == null) return null;
        name = name.toLowerCase(); // Case-insensitive
        Collection<T> identifiables = byName.get(name);
        if (identifiables == null || identifiables.size() == 0) return null;
        return identifiables.iterator().next();
    }

    public T getByPublicKey(PublicKey publicKey) {
        if (publicKey == null) return null;
        return byPublicKey.get(publicKey);
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
        if (identifiable instanceof PublicKeyIdentifiable)
            byPublicKey.put(((PublicKeyIdentifiable) identifiable).getPublicKey(), identifiable);
        byName.putIfAbsent(identifiable.getName().toLowerCase(), new LinkedHashSet<>());
        byName.get(identifiable.getName().toLowerCase()).add(identifiable);
    }

    public void remove(T identifiable) {
        byId.remove(identifiable.getId());
        if (identifiable instanceof PublicKeyIdentifiable)
            byPublicKey.remove(((PublicKeyIdentifiable) identifiable).getPublicKey());
        if (byName.containsKey(identifiable.getName().toLowerCase())) {
            byName.get(identifiable.getName().toLowerCase()).remove(identifiable);
        }
    }

    public void update(T identifiable) { // Called when keys like name or public key changed
        remove(identifiable);
        add(identifiable);
    }

    public boolean contains(T identifiable) {
        return getById(identifiable.getId()) != null || getByName(identifiable.getName()) != null || identifiable instanceof PublicKeyIdentifiable && getByPublicKey(((PublicKeyIdentifiable) identifiable).getPublicKey()) != null;
    }

    public Collection<T> values() {
        return byId.values();
    }

    public void clear() {
        byName.clear();
        byId.clear();
        byPublicKey.clear();
    }

}
