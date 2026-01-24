package com.testfly.sdk.actions;



import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.ElementState;
import io.qameta.allure.Step;

import java.util.List;

public interface IElementChecks extends IPageContext {

    // --- 1. GÖRÜNÜRLÜK VE VARLIK KONTROLLERİ ---

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
        // Elementin DOM'da olup olmadığını (görünür olmasa bile) kontrol eder
        return getPage().locator(selector).count() > 0;
    }

    // --- 2. DURUM KONTROLLERİ (STATE) ---

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

    // --- 3. İÇERİK VE DEĞER KONTROLLERİ ---

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

    // --- 4. LİSTE VE SAYI KONTROLLERİ ---

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

    // --- 5. GELİŞMİŞ DURUM BEKLEME (POLLING OLMADAN DURUM SORGULAMA) ---

    /**
     * Elementin belirli bir state'e gelip gelmediğini anlık kontrol eder.
     * Playwright'ın dahili bekleme mekanizmasını kullanır.
     */
    @Step("Element hazır olma kontrolü (Locator)")
    default boolean isReady(Locator locator) {
        try {
            // Elementin etkileşime hazır (visible, enabled, stable) olup olmadığını döner
            locator.elementHandle().waitForElementState(ElementState.VISIBLE);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}