package com.beachape.filters;

import org.jboss.logging.Logger;

import jakarta.ws.rs.ext.Provider;

@Provider
public class ReqIdResponseFilter implements jakarta.ws.rs.container.ContainerResponseFilter {

    public static final String HEADER_NAME = "X-Request-ID";

    private static final Logger LOGGER = Logger.getLogger(ReqIdResponseFilter.class);

    @Override
    public void filter(jakarta.ws.rs.container.ContainerRequestContext requestContext,
            jakarta.ws.rs.container.ContainerResponseContext responseContext) {
        LOGGER.info("Processing response in ReqIdResponseFilter");
        var currentReqId = requestContext.getProperty(ReqIdRequestFilter.PROPERTY_NAME);
        if (currentReqId != null && currentReqId instanceof String) {
            LOGGER.info("Using existing request ID: " + currentReqId);
            responseContext.getHeaders().add(HEADER_NAME, currentReqId);
        } else {
            LOGGER.info("No existing request ID found, generating a new one");
            String newReqId = java.util.UUID.randomUUID().toString();
            responseContext.getHeaders().add(HEADER_NAME, newReqId);
        }
    }

}
