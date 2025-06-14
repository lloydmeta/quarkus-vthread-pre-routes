package com.beachape.routes.hello;

import java.security.Principal;

import org.jboss.logging.Logger;

import static com.beachape.logging.MessageUtils.formatMessageWithThread;

import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;

@Default
@ApplicationScoped
@RunOnVirtualThread
public class GreetingResource {

    private static final Logger LOGGER = Logger.getLogger(GreetingResource.class);

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        LOGGER.info(formatMessageWithThread("Handling hello within GreetingResource"));
        return "Hello from Quarkus REST";
    }

    @Authenticated
    @GET
    @Path("/say-my-name")
    @Produces(MediaType.TEXT_PLAIN)
    public String sayMyName(@Context SecurityIdentity securityIdentity) {
        LOGGER.info(formatMessageWithThread("Handling say-my-name within GreetingResource"));
        Principal principal = securityIdentity.getPrincipal();
        if (principal != null) {
            LOGGER.info(formatMessageWithThread("Authenticated user: " + principal.getName()));
            return "Hello, " + principal.getName() + "!";
        } else {
            LOGGER.warn(formatMessageWithThread("No authenticated user found in SecurityIdentity"));
            return "Hello, anonymous user!";
        }
    }
}
