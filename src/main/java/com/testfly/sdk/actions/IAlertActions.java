package com.testfly.sdk.actions;

import io.qameta.allure.Step;

public interface IAlertActions extends IPageContext {

    /**
     * Bir sonraki gelecek olan Alert'i kabul eder (OK/Evet).
     */
    @Step("Bir sonraki alert kabul ediliyor")
    default void acceptNextAlert() {
        getLogger().info("Setting up listener to accept next alert");
        getPage().onceDialog(dialog -> {
            getLogger().info("Alert accepted: {}", dialog.message());
            dialog.accept();
        });
    }

    /**
     * Bir sonraki gelecek olan Alert'i reddeder (Cancel/Hayır).
     */
    @Step("Bir sonraki alert reddediliyor")
    default void dismissNextAlert() {
        getPage().onceDialog(dialog -> {
            getLogger().info("Alert dismissed: {}", dialog.message());
            dialog.dismiss();
        });
    }
}