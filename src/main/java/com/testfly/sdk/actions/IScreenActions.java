package com.testfly.sdk.actions;


import com.microsoft.playwright.Page;
import io.qameta.allure.Step;
import java.nio.file.Paths;

public interface IScreenActions extends IPageContext {

    /**
     * Sayfanın o anki görüntüsünü belirtilen isimle kaydeder.
     */
    @Step("Ekran görüntüsü alınıyor: {name}")
    default String takeScreenshot(String name) {
        String path = "target/screenshots/" + name + "_" + System.currentTimeMillis() + ".png";
        getPage().screenshot(new Page.ScreenshotOptions().setPath(Paths.get(path)));
        getLogger().info("Screenshot saved to: {}", path);
        return path;
    }

    /**
     * Sadece belirli bir elementin (Locator) görüntüsünü alır.
     */
    @Step("Element ekran görüntüsü alınıyor: {name}")
    default void takeElementScreenshot(com.microsoft.playwright.Locator locator, String name) {
        String path = "target/screenshots/elements/" + name + ".png";
        locator.screenshot(new com.microsoft.playwright.Locator.ScreenshotOptions().setPath(Paths.get(path)));
        getLogger().info("Element screenshot saved to: {}", path);
    }

    /**
     * Raporlama araçlarına (Allure vb.) gömmek için byte dizisi döner.
     */
    @Step("Ekran görüntüsü byte dizisi olarak alınıyor")
    default byte[] getScreenshotAsBytes() {
        return getPage().screenshot();
    }
}