package com.testfly.sdk.web.actions;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.testfly.sdk.core.ConfigManager;
import io.qameta.allure.Step;

public interface WaitActions extends PageContext {

    @Step("Element görünürlük bekleme: '{selector}'")
    default void waitForVisible(String selector) {
        waitForVisible(getPage().locator(selector));
    }

    @Step("Element görünürlük bekleme (Locator)")
    default void waitForVisible(Locator locator) {
        getLogger().info("Waiting for element to be visible...");
        locator.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(ConfigManager.get().timeout()));
    }

    @Step("Element kaybolma bekleme: '{selector}'")
    default void waitForHidden(String selector) {
        waitForHidden(getPage().locator(selector));
    }

    @Step("Element kaybolma bekleme (Locator)")
    default void waitForHidden(Locator locator) {
        getLogger().info("Waiting for element to be hidden/detached...");
        locator.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.HIDDEN)
                .setTimeout(ConfigManager.get().timeout()));
    }

    @Step("Element tıklanabilirlik bekleme: '{selector}'")
    default void waitForClickable(String selector) {
        waitForClickable(getPage().locator(selector));
    }

    @Step("Element tıklanabilirlik bekleme (Locator)")
    default void waitForClickable(Locator locator) {
        getLogger().info("Waiting for element to be clickable...");
        try {
            locator.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE)
                    .setTimeout(ConfigManager.get().timeout()));

            if (!locator.isEnabled()) {
                throw new RuntimeException("Element is visible but NOT enabled/clickable!");
            }
        } catch (Exception e) {
            getLogger().error("Wait for clickable failed!");
            throw e;
        }
    }

    @Step("Ağ işlemi bitiş bekleme")
    default void waitForNetworkIdle() {
        getLogger().info("Waiting for network to be idle...");
        getPage().waitForLoadState(LoadState.NETWORKIDLE);
    }

    @Step("DOM yüklenme bekleme")
    default void waitForReady() {
        getLogger().info("Waiting for DOM content to be loaded...");
        getPage().waitForLoadState(LoadState.DOMCONTENTLOADED);
    }

    @Step("URL bekleme: {urlPattern}")
    default void waitForUrl(String urlPattern) {
        getLogger().info("Waiting for URL to match: {}", urlPattern);
        getPage().waitForURL(urlPattern);
    }

    @Step("{seconds} saniye bekleniyor")
    default void waitForSeconds(int seconds) {
        getLogger().warn("Using hard wait: {} seconds", seconds);
        getPage().waitForTimeout(seconds * 1000.0);
    }

    @Step("{millis} milisaniye bekleniyor")
    default void waitForMillis(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
