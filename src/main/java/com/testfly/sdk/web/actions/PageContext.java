package com.testfly.sdk.web.actions;

import com.microsoft.playwright.Page;
import org.apache.logging.log4j.Logger;

public interface PageContext {
    Page getPage();
    Logger getLogger();
}
