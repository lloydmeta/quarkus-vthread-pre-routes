package com.beachape;

import java.util.Map;

import io.quarkus.test.junit.QuarkusTestProfile;

public class RunBlockingDisabledProfile implements QuarkusTestProfile {

    @Override
    public Map<String, String> getConfigOverrides() {
        return Map.of("myauth.identity-provider.use-context-run-blocking", "false");
    }
}
