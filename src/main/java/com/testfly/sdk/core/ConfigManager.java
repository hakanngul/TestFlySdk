package com.testfly.sdk.core;

import org.aeonbits.owner.ConfigCache;

public class ConfigManager {
    public static EnvConfig get() {
        return ConfigCache.getOrCreate(EnvConfig.class);
    }
}
