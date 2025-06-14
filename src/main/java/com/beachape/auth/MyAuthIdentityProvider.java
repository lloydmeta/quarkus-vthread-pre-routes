package com.beachape.auth;

import java.time.Duration;

import org.jboss.logging.Logger;

import static com.beachape.logging.MessageUtils.formatMessageWithThread;

import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.IdentityProvider;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MyAuthIdentityProvider implements IdentityProvider<MyAuthRequest> {

    private static final Logger LOGGER = Logger.getLogger(MyAuthIdentityProvider.class);

    @Override
    public Class<MyAuthRequest> getRequestType() {
        return MyAuthRequest.class;
    }

    @Override
    public Uni<SecurityIdentity> authenticate(MyAuthRequest request, AuthenticationRequestContext context) {
        LOGGER.info(formatMessageWithThread(
                "Handling authentication in MyAuthIdentityProvider for user: " + request.getUsername()));
        LOGGER.info(formatMessageWithThread("Simulating IO by sleeping for 5 seconds"));
        try {
            Thread.sleep(Duration.ofSeconds(5).toMillis()); // Simulate IO operation
        } catch (InterruptedException e) {
            LOGGER.error("Thread interrupted during sleep", e);
        }
        LOGGER.info(
                formatMessageWithThread("Done sleeping, creating SecurityIdentity for user: " + request.getUsername()));
        SecurityIdentity identity = QuarkusSecurityIdentity.builder()
                .setPrincipal(() -> request.getUsername())
                .build();
        return Uni.createFrom().item(identity);
    }

}
