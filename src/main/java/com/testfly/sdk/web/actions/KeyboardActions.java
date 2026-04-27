package com.testfly.sdk.web.actions;

import com.microsoft.playwright.Locator;
import io.qameta.allure.Step;

public interface KeyboardActions extends PageContext {

    @Step("Metin yazılıyor: '{text}' -> Hedef: {selector}")
    default void type(String selector, String text) {
        getLogger().info("Typing '{}' into: {}", text, selector);
        getPage().locator(selector).fill(text);
    }

    @Step("Metin yazılıyor (Locator): '{text}'")
    default void type(Locator locator, String text) {
        getLogger().info("Typing into Locator");
        locator.fill(text);
    }

    @Step("Tuşa basılıyor: '{key}' -> Hedef: {selector}")
    default void pressKey(String selector, String key) {
        getLogger().info("Pressing key '{}' on: {}", key, selector);
        getPage().locator(selector).press(key);
    }

    @Step("Element temizleniyor: '{selector}'")
    default void clear(String selector) {
        getLogger().info("Clearing element: {}", selector);
        getPage().locator(selector).clear();
    }

}
