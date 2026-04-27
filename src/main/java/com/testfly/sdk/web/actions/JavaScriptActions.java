package com.testfly.sdk.web.actions;

import io.qameta.allure.Step;

public interface JavaScriptActions extends PageContext {

    @Step("JavaScript çalıştırılıyor: '{script}'")
    default Object executeScript(String script) {
        getLogger().info("Executing JavaScript: {}", script);
        return getPage().evaluate(script);
    }

    @Step("JavaScript çalıştırılıyor: '{script}' -> Argüman: {arg}")
    default Object executeScript(String script, Object arg) {
        return getPage().evaluate(script, arg);
    }

    @Step("Element görünüme kaydırılıyor: '{selector}'")
    default void scrollIntoView(String selector) {
        getLogger().info("Scrolling into view: {}", selector);
        getPage().locator(selector).evaluate("el => el.scrollIntoView({behavior: 'smooth', block: 'center'})");
    }

    @Step("Local Storage'dan veri okunuyor: '{key}'")
    default String getLocalStorageItem(String key) {
        return (String) getPage().evaluate(String.format("window.localStorage.getItem('%s')", key));
    }
}
