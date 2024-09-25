package ru.tbank.dictionaries.kora;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.Nullable;
import java.security.interfaces.RSAPublicKey;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import ru.tinkoff.kora.common.Context;
import ru.tinkoff.kora.config.common.annotation.ConfigSource;
import ru.tinkoff.kora.http.server.common.HttpServerInterceptor;
import ru.tinkoff.kora.http.server.common.HttpServerRequest;
import ru.tinkoff.kora.http.server.common.HttpServerResponse;
import ru.tinkoff.kora.http.server.common.HttpServerResponseException;

public final class JwtVerifierHttpServerInterceptor implements HttpServerInterceptor {

    public static final Context.Key<DecodedJWT> KEY = new Context.KeyImmutable<DecodedJWT>() {
    };

    private final JWTVerifier jwtVerifier;

    public JwtVerifierHttpServerInterceptor(JwtPublicKeyConfig config) {
        String publicKeyValue = config.publicKey();
        if (publicKeyValue == null) {
            this.jwtVerifier = null;
        } else {
            RSAPublicKey rsaPublicKey = RsaPublicKeyConverter.convertStringToRsaPublicKey(publicKeyValue);
            this.jwtVerifier = JWT
                    .require(Algorithm.RSA256(rsaPublicKey))
                    .build();
        }
    }

    @Override
    public CompletionStage<HttpServerResponse> intercept(Context context, HttpServerRequest request,
                                                         InterceptChain chain) throws Exception {

        if (jwtVerifier == null) {
            return chain.process(context, request);
        }

        boolean verified = verify(context, request);
        if (verified) {
            return chain.process(context, request);
        } else {
            return CompletableFuture.failedStage(HttpServerResponseException.of(401, "jwt missing or invalid"));
        }
    }

    private boolean verify(Context context, HttpServerRequest request) {
        String authorization = request.headers().getFirst("Authorization");
        if (authorization == null) {
            return false;
        }

        if (!authorization.startsWith("Bearer ")) {
            return false;
        }

        String token = authorization.substring(7);
        try {
            DecodedJWT decodedJwt = jwtVerifier.verify(token);
            context.set(KEY, decodedJwt);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @ConfigSource("jwt")
    public interface JwtPublicKeyConfig {

        @Nullable
        String publicKey();
    }
}
