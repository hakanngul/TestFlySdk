package com.testfly.sdk.web.actions;


import com.microsoft.playwright.Page;
import io.qameta.allure.Step;
import java.nio.file.Paths;

public interface ScreenActions extends PageContext {

    @Step("Ekran görüntüsü alınıyor: {name}")
    default String takeScreenshot(String name) {
        String path = "target/screenshots/" + name + "_" + System.currentTimeMillis() + ".png";
        getPage().screenshot(new Page.ScreenshotOptions().setPath(Paths.get(path)));
        getLogger().info("Screenshot saved to: {}", path);
        return path;
    }

    @Step("Element ekran görüntüsü alınıyor: {name}")
    default void takeElementScreenshot(com.microsoft.playwright.Locator locator, String name) {
        String path = "target/screenshots/elements/" + name + ".png";
        locator.screenshot(new com.microsoft.playwright.Locator.ScreenshotOptions().setPath(Paths.get(path)));
        getLogger().info("Element screenshot saved to: {}", path);
    }

    @Step("Ekran görüntüsü byte dizisi olarak alınıyor")
    default byte[] getScreenshotAsBytes() {
        return getPage().screenshot();
    }
}
