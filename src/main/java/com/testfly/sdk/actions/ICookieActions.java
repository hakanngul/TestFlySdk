package com.testfly.sdk.actions;

import com.microsoft.playwright.options.Cookie;
import io.qameta.allure.Step;
import java.util.List;

public interface ICookieActions extends IPageContext {

    @Step("Tüm çerezler siliniyor")
    default void deleteAllCookies() {
        getLogger().info("Deleting all cookies");
        getPage().context().clearCookies();
    }

    @Step("Çerezler alınıyor")
    default List<Cookie> getCookies() {
        return getPage().context().cookies();
    }

    @Step("Çerezler ekleniyor: {cookies.size()} adet")
    default void addCookies(List<Cookie> cookies) {
        getPage().context().addCookies(cookies);
    }
}