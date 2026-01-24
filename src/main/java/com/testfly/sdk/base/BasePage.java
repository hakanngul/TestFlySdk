package com.testfly.sdk.base;

import com.microsoft.playwright.Page;
import com.testfly.sdk.actions.*;
import com.testfly.sdk.manager.DriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Paths;

public abstract class BasePage implements IWebActions
{

    protected Page page;
    protected Logger logger;

    public BasePage() {
        this.page = DriverManager.getPage();
        this.logger = LogManager.getLogger(this.getClass());
    }

    // INTERFACE INTEGRATION
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