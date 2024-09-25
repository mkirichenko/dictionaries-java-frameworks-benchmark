package ru.tbank.dictionaries.ktor.plugins

import java.security.KeyFactory
import java.security.NoSuchAlgorithmException
import java.security.interfaces.RSAPublicKey
import java.security.spec.X509EncodedKeySpec
import java.util.*

private const val DASHES = "-----"
private const val X509_PEM_HEADER = DASHES + "BEGIN PUBLIC KEY" + DASHES
private const val X509_PEM_FOOTER = DASHES + "END PUBLIC KEY" + DASHES

fun convertStringToRsaPublicKey(source: String): RSAPublicKey {
    val keyFactory = rsaFactory()

    val lines = source.lines().toList()
    require(lines.isNotEmpty() && lines[0].startsWith(X509_PEM_HEADER)) {
        ("Key is not in PEM-encoded X.509 format, please check that the header begins "
                + "with " + X509_PEM_HEADER)
    }

    val base64Encoded = StringBuilder()
    for (line in lines) {
        if (isNotX509Wrapper(line)) {
            base64Encoded.append(line)
        }
    }
    val x509 = Base64.getDecoder().decode(base64Encoded.toString())
    try {
        return keyFactory.generatePublic(
            X509EncodedKeySpec(x509)
        ) as RSAPublicKey
    } catch (e: Exception) {
        throw IllegalArgumentException(e)
    }
}

private fun rsaFactory(): KeyFactory {
    try {
        return KeyFactory.getInstance("RSA")
    } catch (e: NoSuchAlgorithmException) {
        throw IllegalStateException(e)
    }
}

private fun isNotX509Wrapper(line: String): Boolean {
    return X509_PEM_HEADER != line && X509_PEM_FOOTER != line
}
