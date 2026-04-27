package com.testfly.sdk.core;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;

import java.util.HashMap;
import java.util.Map;

public class BrowserFactory {

    private static final Map<String, BrowserLaunchStrategy> strategies = new HashMap<>();

    private BrowserFactory() {
    }

    static {
        register("chrome", (playwright, headless) -> {
            if (!headless) {
                return playwright.chromium().launch(
                    new BrowserType.LaunchOptions().setHeadless(false).setChannel("chrome"));
            }
            return playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(true));
        });
        register("chromium", (playwright, headless) ->
            playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(headless)));
        register("firefox", (playwright, headless) ->
            playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(headless)));
        register("webkit", (playwright, headless) ->
            playwright.webkit().launch(new BrowserType.LaunchOptions().setHeadless(headless)));
    }

    public static void register(String name, BrowserLaunchStrategy strategy) {
        strategies.put(name.toLowerCase(), strategy);
    }

    public static Browser launch(Playwright playwright, String browserType, boolean headless) {
        BrowserLaunchStrategy strategy = strategies.get(browserType.toLowerCase());
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported browser type: " + browserType +
                ". Registered: " + strategies.keySet());
        }
        return strategy.launch(playwright, headless);
    }

    @FunctionalInterface
    public interface BrowserLaunchStrategy {
        Browser launch(Playwright playwright, boolean headless);
    }
}
