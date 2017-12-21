package com.rupoland.step_definitions;

import com.codeborne.selenide.Selenide;
import com.rupoland.BaseTest;
import cucumber.api.java.en.Given;

public class BaseStepDefs extends BaseTest{

    public BaseStepDefs() {
    }

    @Given("^user opens lobby$")
    public void openGamesPage() {
        Selenide.open("http://pr-cy.ru/");
    }
}
