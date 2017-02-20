package com.emc.internal;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static com.emc.internal.TestConstants.TEST_PROFILE;

@ActiveProfiles(TEST_PROFILE)
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    @SuppressWarnings({"JUnitTestMethodWithNoAssertions", "EmptyMethod"})
    public void testContextLoads() {

    }

    @Configuration
    static class ContextConfig {

    }
}
