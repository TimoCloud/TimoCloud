package cloud.timo.TimoCloud.api.objects.properties;

import java.security.PublicKey;

public class CordProperties {

    private String id;
    private String name;
    private PublicKey publicKey;

    private CordProperties() {
    }

    public CordProperties(String id, String name, PublicKey publicKey) {
        this();
        this.id = id;
        this.name = name;
        this.publicKey = publicKey;
    }



    public String getId() {
        return id;
    }

    public CordProperties setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public CordProperties setName(String name) {
        this.name = name;
        return this;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public CordProperties setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
        return this;
    }




}
