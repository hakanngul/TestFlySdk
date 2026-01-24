package com.testfly.sdk.actions;

import com.microsoft.playwright.options.SelectOption;
import io.qameta.allure.Step;

public interface IFormActions extends IPageContext {

    // --- DROPDOWN (SELECT) İŞLEMLERİ ---

    @Step("Seçim yapılıyor: '{selector}' -> Metin: '{text}'")
    default void selectByText(String selector, String text) {
        getLogger().info("Selecting option '{}' from: {}", text, selector);
        getPage().locator(selector).selectOption(new SelectOption().setLabel(text));
    }

    @Step("Seçim yapılıyor: '{selector}' -> Değer: '{value}'")
    default void selectByValue(String selector, String value) {
        getLogger().info("Selecting value '{}' from: {}", value, selector);
        getPage().locator(selector).selectOption(value);
    }

    @Step("Seçim yapılıyor: '{selector}' -> İndeks: {index}")
    default void selectByIndex(String selector, int index) {
        getLogger().info("Selecting index '{}' from: {}", index, selector);
        getPage().locator(selector).selectOption(new SelectOption().setIndex(index));
    }

    // --- CHECKBOX & RADIO BUTTON İŞLEMLERİ ---

    @Step("Seçim yapılıyor: '{selector}'")
    default void check(String selector) {
        getLogger().info("Checking checkbox/radio: {}", selector);
        // Playwright check() metodu element zaten seçiliyse tekrar tıklamaz (Safe)
        getPage().locator(selector).check();
    }

    @Step("Seçim kaldırılıyor: '{selector}'")
    default void uncheck(String selector) {
        getLogger().info("Unchecking checkbox: {}", selector);
        getPage().locator(selector).uncheck();
    }

    /**
     * Elementin o anki seçili olma durumuna göre aksiyon alır.
     * @param shouldBeChecked true ise check eder, false ise uncheck eder.
     */
    @Step("Checkbox ayarlanıyor: '{selector}' -> Durum: {shouldBeChecked}")
    default void setCheckbox(String selector, boolean shouldBeChecked) {
        getLogger().info("Setting checkbox {} to {}", selector, shouldBeChecked);
        getPage().locator(selector).setChecked(shouldBeChecked);
    }
}