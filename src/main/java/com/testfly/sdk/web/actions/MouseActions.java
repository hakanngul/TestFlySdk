package com.testfly.sdk.web.actions;


import com.microsoft.playwright.Locator;
import io.qameta.allure.Step;

public interface MouseActions extends PageContext {

    @Step("Tıklanıyor: '{selector}'")
    default void click(String selector) {
        getLogger().info("Clicking element: " + selector);
        getPage().locator(selector).click();
    }

    @Step("Tıklanıyor (Locator)")
    default void click(Locator locator) {
        getLogger().info("Clicking element (Locator)");
        locator.click();
    }

    @Step("Çift tıklanıyor: '{selector}'")
    default void doubleClick(String selector) {
        getLogger().info("Double clicking: " + selector);
        getPage().locator(selector).dblclick();
    }

    @Step("Üzerine geliniyor: '{selector}'")
    default void hover(String selector) {
        getLogger().info("Hovering over: " + selector);
        getPage().locator(selector).hover();
    }
}
