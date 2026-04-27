package com.testfly.sdk.web.actions;

import com.microsoft.playwright.Page;
import org.apache.logging.log4j.Logger;

public interface WebActions extends
        PageContext,
        NavigationActions,
        MouseActions,
        KeyboardActions,
        ElementChecks,
        ScreenActions,
        FormActions,
        WaitActions,
        AlertActions,
        CookieActions,
        JavaScriptActions {
}
