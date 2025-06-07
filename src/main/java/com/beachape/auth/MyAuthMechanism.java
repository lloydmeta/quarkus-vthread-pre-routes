package com.beachape.auth;

import org.jboss.logging.Logger;

import io.quarkus.security.identity.IdentityProviderManager;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.vertx.http.runtime.security.ChallengeData;
import io.quarkus.vertx.http.runtime.security.HttpAuthenticationMechanism;
import io.smallrye.mutiny.Uni;
import io.vertx.ext.web.RoutingContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class MyAuthMechanism implements HttpAuthenticationMechanism {

    private static final Logger LOGGER = Logger.getLogger(MyAuthMechanism.class);

    @Override
    public Uni<SecurityIdentity> authenticate(RoutingContext context, IdentityProviderManager identityProviderManager) {
        LOGGER.info("Handling authentication in MyAuthMechanism");
        String authorizationHeader = context.request().getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith("Basic ")) {
            return Uni.createFrom().nullItem();
        }
        String base64Credentials = authorizationHeader.substring("Basic ".length()).trim();
        String credentials = new String(java.util.Base64.getDecoder().decode(base64Credentials));
        String[] parts = credentials.split(":", 2);
        if (parts.length != 2) {
            return Uni.createFrom().nullItem();
        }

        String username = parts[0];
        String password = parts[1];
        MyAuthRequest request = new MyAuthRequest(username, password);
        return identityProviderManager.authenticate(request);
    }

    @Override
    public Uni<ChallengeData> getChallenge(RoutingContext context) {
        var challenge = new ChallengeData(Response.Status.UNAUTHORIZED.getStatusCode(), HttpHeaders.WWW_AUTHENTICATE,
                "Basic realm=\"beachape\"");
        return Uni.createFrom().item(challenge);
    }

}
