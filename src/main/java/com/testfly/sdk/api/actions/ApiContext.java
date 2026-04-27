package com.testfly.sdk.api.actions;

import com.microsoft.playwright.APIRequestContext;
import org.apache.logging.log4j.Logger;

public interface ApiContext {
    APIRequestContext getRequestContext();
    Logger getLogger();
    String getBaseUrl();
}
