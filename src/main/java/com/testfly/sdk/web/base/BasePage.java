package com.testfly.sdk.web.base;

import com.microsoft.playwright.Page;
import com.testfly.sdk.web.actions.WebActions;
import com.testfly.sdk.core.BrowserManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Paths;

public abstract class BasePage implements WebActions
{

    protected Page page;
    protected Logger logger;

    public BasePage() {
        this.page = BrowserManager.getPage();
        this.logger = LogManager.getLogger(this.getClass());
    }

    @Override
    public Page getPage() {
        return this.page;
    }

    @Override
    public Logger getLogger() {
        return this.logger;
    }

    protected void uploadFile(String selector, String filePath) {
        logger.info("Uploading file: {}", filePath);
        page.locator(selector).setInputFiles(Paths.get(filePath));
    }
}
