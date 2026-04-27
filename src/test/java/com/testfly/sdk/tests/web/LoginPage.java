package com.testfly.sdk.tests.web;

import com.microsoft.playwright.Locator;
import com.testfly.sdk.web.base.BasePage;

public class LoginPage extends BasePage {

    private final String userField = "#user-name";
    private final String passField = "#password";
    private final String loginBtn  = "#login-button";
    private final String title     = ".title";
    private final String error     = "[data-test='error']";

    public void enterUsername(String user) { type(userField, user); }
    public void enterPassword(String pass) { type(passField, pass); }
    public void clickLogin()               { click(loginBtn); }

    public Locator getUsernameInput()      { return getPage().locator(userField); }
    public Locator getPasswordInput()      { return getPage().locator(passField); }
    public Locator getLoginButton()        { return getPage().locator(loginBtn); }
    public Locator getInventoryTitle()     { return getPage().locator(title); }
    public Locator getErrorMessageElement(){ return getPage().locator(error); }
}
