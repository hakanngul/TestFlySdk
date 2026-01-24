package com.testfly.sdk.actions;

import com.microsoft.playwright.Page;
import org.apache.logging.log4j.Logger;
public interface IPageContext {
    Page getPage();
    Logger getLogger();
}
