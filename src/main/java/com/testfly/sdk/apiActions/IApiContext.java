package com.testfly.sdk.apiActions;

import com.microsoft.playwright.APIRequestContext;
import org.apache.logging.log4j.Logger;

public interface IApiContext {
    APIRequestContext getRequestContext();
    Logger getLogger();
    String getBaseUrl();
}