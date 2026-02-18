package com.beachape;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;

@QuarkusTest
@TestProfile(RunBlockingDisabledProfile.class)
public class VirtualThreadAuthWithoutRunBlockingIT extends AbstractVirtualThreadAuthIT {
}
