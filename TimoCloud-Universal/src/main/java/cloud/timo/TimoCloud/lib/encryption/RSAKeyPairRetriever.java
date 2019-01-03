package cloud.timo.TimoCloud.lib.encryption;

import java.io.*;
import java.security.KeyPair;

public class RSAKeyPairRetriever {

    private File directory;
    private File privateKeyFile;
    private File publicKeyFile;

    public RSAKeyPairRetriever(File directory) {
        this.directory = directory;
        privateKeyFile = new File(directory, "private.tck");
        publicKeyFile = new File(directory, "public.tck");
    }

    public boolean isValidKeyPairExisting() {
        try {
            getKeyPair();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public KeyPair getKeyPair() throws Exception {
        BufferedReader publicKeyReader = new BufferedReader(new FileReader(publicKeyFile));
        String publicKey = publicKeyReader.readLine();
        publicKeyReader.close();
        BufferedReader privateKeyReader = new BufferedReader(new FileReader(privateKeyFile));
        String privateKey = privateKeyReader.readLine();
        privateKeyReader.close();
        return new KeyPair(
                RSAKeyUtil.publicKeyFromBase64(publicKey),
                RSAKeyUtil.privateKeyFromBase64(privateKey)
        );
    }

    public KeyPair generateKeyPair() throws IOException {
        KeyPair keyPair = RSAKeyUtil.generateKeyPair();
        saveKeyPair(keyPair);
        return keyPair;
    }

    private void saveKeyPair(KeyPair keyPair) throws IOException {
        directory.mkdirs();
        if (publicKeyFile.exists()) publicKeyFile.delete();
        if (privateKeyFile.exists()) privateKeyFile.delete();
        publicKeyFile.createNewFile();
        privateKeyFile.createNewFile();
        FileWriter publicKeyWriter = new FileWriter(publicKeyFile);
        publicKeyWriter.write(RSAKeyUtil.publicKeyToBase64(keyPair.getPublic()));
        publicKeyWriter.close();
        FileWriter privateKeyWriter = new FileWriter(privateKeyFile);
        privateKeyWriter.write(RSAKeyUtil.privateKeyToBase64(keyPair.getPrivate()));
        privateKeyWriter.close();
    }

}
