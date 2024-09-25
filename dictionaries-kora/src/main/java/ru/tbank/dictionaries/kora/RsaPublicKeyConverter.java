package ru.tbank.dictionaries.kora;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RsaPublicKeyConverter {

    private static final String DASHES = "-----";
    private static final String X509_PEM_HEADER = DASHES + "BEGIN PUBLIC KEY" + DASHES;
    private static final String X509_PEM_FOOTER = DASHES + "END PUBLIC KEY" + DASHES;

    public static RSAPublicKey convertStringToRsaPublicKey(String source) {
        var keyFactory = rsaFactory();

        var lines = source.lines().toList();
        if (lines.isEmpty() || !lines.getFirst().startsWith(X509_PEM_HEADER)) {
            throw new IllegalArgumentException(
                    "Key is not in PEM-encoded X.509 format, please check that the header begins with " + X509_PEM_HEADER);
        }

        var base64Encoded = new StringBuilder();
        for (String line : lines) {
            if (isNotX509Wrapper(line)) {
                base64Encoded.append(line);
            }
        }
        var x509 = Base64.getDecoder().decode(base64Encoded.toString());
        try {
            return (RSAPublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(x509));
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static KeyFactory rsaFactory() {
        try {
            return KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private static boolean isNotX509Wrapper(String line) {
        return !X509_PEM_HEADER.equals(line) && !X509_PEM_FOOTER.equals(line);
    }
}
