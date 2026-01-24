package com.testfly.sdk.actions;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.testfly.sdk.config.ConfigManager;
import io.qameta.allure.Step;

public interface IWaitActions extends IPageContext {

    // --- 1. ELEMENT VISIBILITY (GÖRÜNÜRLÜK) ---

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

    // --- 2. ELEMENT HIDDEN (KAYBOLMA) ---

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

    // --- 3. CLICKABILITY (TIKLANABİLİRLİK) ---

    @Step("Element tıklanabilirlik bekleme: '{selector}'")
    default void waitForClickable(String selector) {
        waitForClickable(getPage().locator(selector));
    }

    /**
     * Elementin tıklanabilir (Visible + Enabled + Stable) olmasını bekler.
     */
    @Step("Element tıklanabilirlik bekleme (Locator)")
    default void waitForClickable(Locator locator) {
        getLogger().info("Waiting for element to be clickable...");
        try {
            // Görünür olmasını bekle
            locator.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE)
                    .setTimeout(ConfigManager.get().timeout()));

            // Tıklanabilir (Enabled) kontrolü
            if (!locator.isEnabled()) {
                throw new RuntimeException("Element is visible but NOT enabled/clickable!");
            }
        } catch (Exception e) {
            getLogger().error("Wait for clickable failed!");
            throw e;
        }
    }

    // --- 4. PAGE & NETWORK STATES (SAYFA VE AĞ DURUMLARI) ---

    /**
     * Sayfadaki tüm network trafiği durana kadar bekler. (Enterprise favorisi)
     */
    @Step("Ağ işlemi bitiş bekleme")
    default void waitForNetworkIdle() {
        getLogger().info("Waiting for network to be idle...");
        getPage().waitForLoadState(LoadState.NETWORKIDLE);
    }

    /**
     * DOM içeriğinin tamamen yüklendiğinden emin olur.
     */
    @Step("DOM yüklenme bekleme")
    default void waitForReady() {
        getLogger().info("Waiting for DOM content to be loaded...");
        getPage().waitForLoadState(LoadState.DOMCONTENTLOADED);
    }

    /**
     * Belirli bir URL regex veya string gelene kadar bekler.
     */
    @Step("URL bekleme: {urlPattern}")
    default void waitForUrl(String urlPattern) {
        getLogger().info("Waiting for URL to match: {}", urlPattern);
        getPage().waitForURL(urlPattern);
    }

    // --- 5. TIMEOUTS (ZAMANSAL BEKLEMELER) ---

    /**
     * Playwright tabanlı akıllı bekleme (Önerilen)
     */
    @Step("{seconds} saniye bekleniyor")
    default void waitForSeconds(int seconds) {
        getLogger().warn("Using hard wait: {} seconds", seconds);
        getPage().waitForTimeout(seconds * 1000.0);
    }

    /**
     * Java tabanlı statik bekleme (Sadece Thread işlemleri için)
     */
    @Step("{millis} milisaniye bekleniyor")
    default void waitForMillis(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}