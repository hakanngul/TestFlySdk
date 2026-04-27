package com.testfly.sdk.web.actions;

import com.microsoft.playwright.Locator;
import io.qameta.allure.Step;

public interface NavigationActions extends PageContext {

    @Step("Mevcut URL alınıyor")
    default String getCurrentUrl() {
        return getPage().url();
    }

    @Step("Sayfa başlığı alınıyor")
    default String getTitle() {
        return getPage().title();
    }

    @Step("Sayfaya gidiliyor: {path}")
    default void navigateTo(String url) {

        getLogger().info("Navigating to: " + url);
        getPage().navigate(url);
    }

    @Step("Sayfa yenileniyor")
    default void refresh() {
        getLogger().info("Refreshing page");
        getPage().reload();
    }

    @Step("Geriye gidiliyor")
    default void back() {
        getLogger().info("Navigating back");
        getPage().goBack();
    }

    @Step("İleriye gidiliyor")
    default void forward() {
        getLogger().info("Navigating forward");
        getPage().goForward();
    }

    @Step("Metin alınıyor: '{selector}'")
    default String getText(String selector) {
        getLogger().info("Getting text from: {}", selector);
        return getPage().locator(selector).textContent().trim();
    }

    @Step("Metin alınıyor (Locator)")
    default String getText(Locator locator) {
        getLogger().info("Getting text from Locator");
        return locator.textContent().trim();
    }

}
