package com.testfly.sdk.web.actions;



import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.ElementState;
import io.qameta.allure.Step;

import java.util.List;

public interface ElementChecks extends PageContext {

    @Step("Element görünürlük kontrolü: '{selector}'")
    default boolean isVisible(String selector) {
        return getPage().locator(selector).isVisible();
    }

    @Step("Element görünürlük kontrolü (Locator)")
    default boolean isVisible(Locator locator) {
        return locator.isVisible();
    }

    @Step("Element gizlilik kontrolü: '{selector}'")
    default boolean isHidden(String selector) {
        return getPage().locator(selector).isHidden();
    }

    @Step("Element varlık kontrolü (DOM): '{selector}'")
    default boolean isPresent(String selector) {
        return getPage().locator(selector).count() > 0;
    }

    @Step("Element aktiflik kontrolü: '{selector}'")
    default boolean isEnabled(String selector) {
        return getPage().locator(selector).isEnabled();
    }

    @Step("Element aktiflik kontrolü (Locator)")
    default boolean isEnabled(Locator locator) {
        return locator.isEnabled();
    }

    @Step("Element devre dışı kontrolü: '{selector}'")
    default boolean isDisabled(String selector) {
        return getPage().locator(selector).isDisabled();
    }

    @Step("Element düzenlenebilirlik kontrolü: '{selector}'")
    default boolean isEditable(String selector) {
        return getPage().locator(selector).isEditable();
    }

    @Step("Element seçili olma kontrolü: '{selector}'")
    default boolean isChecked(String selector) {
        return getPage().locator(selector).isChecked();
    }

    @Step("Metin içeriği kontrolü: '{selector}' -> Beklenen: '{expectedText}'")
    default boolean hasText(String selector, String expectedText) {
        String actualText = getPage().locator(selector).textContent();
        return actualText != null && actualText.contains(expectedText);
    }

    @Step("Değer kontrolü: '{selector}' -> Beklenen: '{expectedValue}'")
    default boolean hasValue(String selector, String expectedValue) {
        return getPage().locator(selector).inputValue().equals(expectedValue);
    }

    @Step("Özellik kontrolü: '{selector}' -> {attributeName} = '{expectedValue}'")
    default boolean hasAttribute(String selector, String attributeName, String expectedValue) {
        String attr = getPage().locator(selector).getAttribute(attributeName);
        return attr != null && attr.equals(expectedValue);
    }

    @Step("CSS sınıf kontrolü: '{selector}' -> {className}")
    default boolean hasClass(String selector, String className) {
        String classAttr = getPage().locator(selector).getAttribute("class");
        return classAttr != null && classAttr.contains(className);
    }

    @Step("Element sayısı: '{selector}'")
    default int getCount(String selector) {
        return getPage().locator(selector).count();
    }

    @Step("Liste boşluk kontrolü: '{selector}'")
    default boolean isListEmpty(String selector) {
        return getCount(selector) == 0;
    }

    @Step("Tüm metinler alınıyor: '{selector}'")
    default List<String> getAllTexts(String selector) {
        return getPage().locator(selector).allTextContents();
    }

    @Step("Element hazır olma kontrolü (Locator)")
    default boolean isReady(Locator locator) {
        try {
            locator.elementHandle().waitForElementState(ElementState.VISIBLE);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
