package com.beachape.routes;

import com.beachape.routes.hello.GreetingResource;

import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;

@RunOnVirtualThread
@Path("/v1")
public class V1Resource {

    @Inject
    GreetingResource greetingResource;

    @Path("/hello")
    public GreetingResource getGreetingResource() {
        return greetingResource;
    }

}
