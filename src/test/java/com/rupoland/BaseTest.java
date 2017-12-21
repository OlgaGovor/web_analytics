package com.rupoland;

import com.codeborne.selenide.Configuration;

public class BaseTest {

    private static final long FAIL_TEST_TIMEOUT = 5000;

    public static void setup() throws Exception {
        Configuration.baseUrl = "http://pr-cy.ru/";
        Configuration.timeout = FAIL_TEST_TIMEOUT;
        Configuration.startMaximized = true;
        Configuration.browser = "chrome";
        Configuration.reportsFolder = "target/reports";
    }

}
