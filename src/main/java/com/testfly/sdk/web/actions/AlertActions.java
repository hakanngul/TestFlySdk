package com.testfly.sdk.web.actions;

import io.qameta.allure.Step;

public interface AlertActions extends PageContext {

    @Step("Bir sonraki alert kabul ediliyor")
    default void acceptNextAlert() {
        getLogger().info("Setting up listener to accept next alert");
        getPage().onceDialog(dialog -> {
            getLogger().info("Alert accepted: {}", dialog.message());
            dialog.accept();
        });
    }

    @Step("Bir sonraki alert reddediliyor")
    default void dismissNextAlert() {
        getPage().onceDialog(dialog -> {
            getLogger().info("Alert dismissed: {}", dialog.message());
            dialog.dismiss();
        });
    }
}
