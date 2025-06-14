package com.beachape.filters;

import java.util.UUID;

import org.jboss.logging.Logger;

import static com.beachape.logging.MessageUtils.formatMessageWithThread;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;

@Provider
public final class ReqIdRequestFilter implements ContainerRequestFilter {

    public static final String PROPERTY_NAME = "request-id";

    private static final Logger LOGGER = Logger.getLogger(ReqIdRequestFilter.class);

    @Override
    public void filter(ContainerRequestContext requestContext) {
        LOGGER.info(formatMessageWithThread("Processing request in ReqIdRequestFilter"));
        var reqId = UUID.randomUUID().toString();
        requestContext.setProperty(PROPERTY_NAME, reqId);
    }

}
