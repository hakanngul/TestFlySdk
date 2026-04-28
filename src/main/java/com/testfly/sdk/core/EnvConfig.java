package com.testfly.sdk.core;

import org.aeonbits.owner.Config;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({
        "classpath:${env}.properties",
        "classpath:config.properties"
})
public interface EnvConfig extends Config {

    @Key("browser")
    @DefaultValue("chrome")
    String browser();

    @Key("headless")
    @DefaultValue("false")
    boolean isHeadless();

    @Key("timeout")
    @DefaultValue("10000")
    int timeout();

    @Key("headful.timeout")
    @DefaultValue("30000")
    int headfulTimeout();

    @Key("base.url")
    @DefaultValue("https://example.com")
    String baseUrl();

    @Key("environment")
    @DefaultValue("dev")
    String environment();

    @Key("record.video")
    @DefaultValue("false")
    boolean isRecordVideo();

    @Key("api.timeout.connect")
    @DefaultValue("30000")
    int apiConnectTimeout();

    @Key("api.timeout.read")
    @DefaultValue("30000")
    int apiReadTimeout();

    @Key("api.retry.max")
    @DefaultValue("3")
    int apiMaxRetries();

    @Key("api.retry.delay")
    @DefaultValue("1000")
    int apiRetryDelay();

    @Key("trace.enabled")
    @DefaultValue("true")
    boolean isTraceEnabled();

    @Key("trace.path")
    @DefaultValue("target/traces")
    String tracePath();

    @Key("retry.enabled")
    @DefaultValue("true")
    boolean isRetryEnabled();

    @Key("retry.max")
    @DefaultValue("2")
    int retryMax();

    @Key("parallel")
    @DefaultValue("true")
    boolean isParallel();

    @Key("thread.count")
    @DefaultValue("4")
    int threadCount();
}
