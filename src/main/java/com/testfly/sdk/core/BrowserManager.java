package com.testfly.sdk.core;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;
import com.testfly.sdk.exceptions.BrowserInitializationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class BrowserManager {

    private static final Logger logger = LogManager.getLogger(BrowserManager.class);
    private static final String VIDEO_DIR = "target/videos";

    private static final ThreadLocal<Browser> browserThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> contextThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<Page> pageThreadLocal = new ThreadLocal<>();

    private BrowserManager() {
    }

    public static void initializeDriver(String browserType) {
        try {
            logger.info("=== Initializing Browser ===");

            browserThreadLocal.remove();
            contextThreadLocal.remove();
            pageThreadLocal.remove();

            Playwright playwright = PlaywrightManager.getOrCreate();

            Browser browser = createBrowser(playwright, browserType);
            browserThreadLocal.set(browser);

            boolean recordVideo = ConfigManager.get().isRecordVideo();
            BrowserContext context = createContext(browser, recordVideo);
            contextThreadLocal.set(context);

            Page page = context.newPage();

            int defaultTimeout = ConfigManager.get().isHeadless() ? ConfigManager.get().timeout()
                    : ConfigManager.get().headfulTimeout();

            page.setDefaultTimeout(defaultTimeout);
            page.setDefaultNavigationTimeout(defaultTimeout);

            pageThreadLocal.set(page);

            logger.info("Browser initialized successfully:");
            logger.info("  Browser: {}", browserThreadLocal.get() != null ? "OK" : "NULL");
            logger.info("  Context: {}", contextThreadLocal.get() != null ? "OK" : "NULL");
            logger.info("  Page: {}", pageThreadLocal.get() != null ? "OK" : "NULL");
            logger.info("  Video Recording: {}", recordVideo ? "ON" : "OFF");
            logger.info("  Default Timeout: {}ms", defaultTimeout);
            logger.info("  Thread ID: {}", Thread.currentThread().threadId());
        } catch (Exception e) {
            logger.error("Error initializing browser: {}", e.getMessage(), e);
            throw new BrowserInitializationException("Failed to initialize browser", e);
        }
    }

    private static Browser createBrowser(Playwright playwright, String browserType) {
        boolean headless = ConfigManager.get().isHeadless();
        logger.info("Launching browser: {} (headless: {})", browserType, headless);
        return BrowserFactory.launch(playwright, browserType, headless);
    }

    private static BrowserContext createContext(Browser browser, boolean recordVideo) {
        if (recordVideo) {
            try {
                Files.createDirectories(Paths.get(VIDEO_DIR));
            } catch (Exception e) {
                logger.warn("Could not create video directory: {}", e.getMessage());
            }

            logger.info("Video recording enabled → dir: {}", VIDEO_DIR);
            return browser.newContext(new Browser.NewContextOptions()
                    .setRecordVideoDir(Paths.get(VIDEO_DIR))
                    .setRecordVideoSize(1280, 720));
        }
        return browser.newContext();
    }

    public static Page getPage() {
        return pageThreadLocal.get();
    }

    public static BrowserContext getContext() {
        return contextThreadLocal.get();
    }

    public static Browser getBrowser() {
        return browserThreadLocal.get();
    }

    /**
     * Saves the video recording for a failed test scenario.
     * Must be called BEFORE quitDriver().
     * The page is closed here to finalize the video file.
     *
     * @param testName name of the failed test (used for file naming)
     * @return Optional path to the saved video file
     */
    public static Optional<Path> saveVideo(String testName) {
        if (!ConfigManager.get().isRecordVideo()) {
            return Optional.empty();
        }

        Page page = pageThreadLocal.get();
        if (page == null || page.video() == null) {
            logger.debug("No video available to save");
            return Optional.empty();
        }

        try {
            // Get video path BEFORE closing the page
            Path sourceVideoPath = page.video().path();
            logger.info("Video source path: {}", sourceVideoPath);

            // Close page to finalize video recording
            page.close();
            pageThreadLocal.remove();

            // Wait for video file to be fully written (file size must stabilize)
            Path stablePath = waitForVideoFile(sourceVideoPath);
            if (stablePath == null) {
                logger.warn("Video file did not stabilize in time");
                return Optional.empty();
            }

            String safeName = testName.replaceAll("[^a-zA-Z0-9._-]", "_");
            String timestamp = String.valueOf(System.currentTimeMillis());

            // Rename to meaningful name
            Path renamedPath = Paths.get(VIDEO_DIR, safeName + "_" + timestamp + ".webm");
            Files.move(stablePath, renamedPath);
            logger.info("Video file renamed: {} ({} bytes)", renamedPath.getFileName(), Files.size(renamedPath));

            // Convert .webm to .mp4 using ffmpeg
            Path mp4Path = Paths.get(VIDEO_DIR, safeName + "_" + timestamp + ".mp4");
            Path convertedPath = convertToMp4(renamedPath, mp4Path);

            if (convertedPath != null) {
                logger.info("Video saved as MP4: {}", convertedPath.getFileName());
                return Optional.of(convertedPath);
            } else {
                logger.warn("MP4 conversion failed, keeping WEBM: {}", renamedPath.getFileName());
                return Optional.of(renamedPath);
            }
        } catch (Exception e) {
            logger.warn("Failed to save video for '{}': {}", testName, e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Waits for the video file to be fully written by checking file size stability.
     * Playwright writes the video asynchronously after page close.
     *
     * @param videoPath path to the video file
     * @return the stable path, or null if timeout
     */
    private static Path waitForVideoFile(Path videoPath) {
        if (videoPath == null) {
            return null;
        }

        try {
            // Wait for file to exist (max 10 seconds)
            long deadline = System.currentTimeMillis() + 10_000;
            while (!Files.exists(videoPath) && System.currentTimeMillis() < deadline) {
                Thread.sleep(200);
            }

            if (!Files.exists(videoPath)) {
                logger.warn("Video file never appeared: {}", videoPath);
                return null;
            }

            // Wait for file size to stabilize (must be same size for 1 second)
            long previousSize = -1;
            int stableCount = 0;
            while (System.currentTimeMillis() < deadline) {
                long currentSize = Files.size(videoPath);
                if (currentSize == previousSize && currentSize > 0) {
                    stableCount++;
                    if (stableCount >= 5) { // 1 second of stable size (5 x 200ms)
                        logger.info("Video file stabilized: {} bytes", currentSize);
                        return videoPath;
                    }
                } else {
                    stableCount = 0;
                }
                previousSize = currentSize;
                Thread.sleep(200);
            }

            // Return file even if not fully stabilized (timeout)
            long finalSize = Files.size(videoPath);
            logger.warn("Video file wait timeout, size: {} bytes", finalSize);
            return finalSize > 0 ? videoPath : null;

        } catch (Exception e) {
            logger.warn("Error waiting for video file: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Converts a .webm video file to .mp4 using ffmpeg.
     *
     * @param webmPath source .webm file path
     * @param mp4Path  target .mp4 file path
     * @return Path to the .mp4 file, or null if conversion failed
     */
    private static Path convertToMp4(Path webmPath, Path mp4Path) {
        try {
            logger.info("Converting video: {} → {}", webmPath.getFileName(), mp4Path.getFileName());

            // Use shell to run ffmpeg — macOS SIP blocks DYLD vars from Java child
            // processes
            String ffmpegCmd = String.format(
                    "/opt/homebrew/bin/ffmpeg -y -i '%s' -c:v libx264 -preset fast -crf 28 -movflags +faststart -an '%s'",
                    webmPath.toAbsolutePath(), mp4Path.toAbsolutePath());
            ProcessBuilder pb = new ProcessBuilder("/bin/zsh", "-c", ffmpegCmd);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            // Must consume output stream, otherwise process may block/hang
            StringBuilder ffmpegOutput = new StringBuilder();
            try (var reader = new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    ffmpegOutput.append(line).append("\n");
                }
            }

            int exitCode = process.waitFor();

            if (exitCode == 0 && Files.exists(mp4Path)) {
                // Delete original .webm after successful conversion
                Files.deleteIfExists(webmPath);
                logger.info("✅ Video converted to MP4: {} ({} bytes)",
                        mp4Path.getFileName(), Files.size(mp4Path));
                return mp4Path;
            } else {
                logger.error("❌ ffmpeg exited with code {} for {}", exitCode, webmPath.getFileName());
                logger.error("ffmpeg output:\n{}", ffmpegOutput);
                return null;
            }
        } catch (Exception e) {
            logger.error("❌ ffmpeg conversion failed: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Discards the video recording for a passed test scenario.
     * Must be called BEFORE quitDriver().
     */
    public static void discardVideo() {
        if (!ConfigManager.get().isRecordVideo()) {
            return;
        }

        Page page = pageThreadLocal.get();
        if (page == null || page.video() == null) {
            return;
        }

        try {
            // Close page to finalize video file
            page.close();
            pageThreadLocal.remove();

            Path videoPath = page.video().path();
            if (videoPath != null && Files.exists(videoPath)) {
                Files.deleteIfExists(videoPath);
                logger.debug("Video discarded for passed test");
            }
        } catch (Exception e) {
            logger.debug("Could not discard video: {}", e.getMessage());
        }
    }

    public static void quitDriver() {
        try {
            Page page = pageThreadLocal.get();
            if (page != null && !page.isClosed()) {
                page.close();
            }
            pageThreadLocal.remove();

            BrowserContext context = contextThreadLocal.get();
            if (context != null) {
                context.close();
                contextThreadLocal.remove();
            }

            Browser browser = browserThreadLocal.get();
            if (browser != null) {
                browser.close();
                browserThreadLocal.remove();
            }
        } catch (Exception e) {
            logger.error("Error quitting browser: {}", e.getMessage(), e);
        }
    }

    public static void startTracing() {
        if (!ConfigManager.get().isTraceEnabled()) {
            return;
        }
        BrowserContext context = contextThreadLocal.get();
        if (context != null) {
            context.tracing().start(new Tracing.StartOptions()
                    .setScreenshots(true)
                    .setSnapshots(true)
                    .setSources(true));
            logger.info("Trace recording started for thread: {}", Thread.currentThread().threadId());
        }
    }

    public static void stopTracing(String testName) {
        if (!ConfigManager.get().isTraceEnabled()) {
            return;
        }
        BrowserContext context = contextThreadLocal.get();
        if (context != null) {
            try {
                String tracePath = ConfigManager.get().tracePath();
                Files.createDirectories(Paths.get(tracePath));

                String safeName = testName.replaceAll("[^a-zA-Z0-9._-]", "_");
                String filePath = tracePath + "/" + safeName + "_" + System.currentTimeMillis() + ".zip";

                context.tracing().stop(new Tracing.StopOptions()
                        .setPath(Paths.get(filePath)));
                logger.info("Trace saved: {}", filePath);
            } catch (Exception e) {
                logger.warn("Failed to save trace for '{}': {}", testName, e.getMessage());
            }
        }
    }

    public static void stopAndDiscardTracing() {
        if (!ConfigManager.get().isTraceEnabled()) {
            return;
        }
        BrowserContext context = contextThreadLocal.get();
        if (context != null) {
            try {
                context.tracing().stop();
                logger.debug("Trace discarded for thread: {}", Thread.currentThread().threadId());
            } catch (Exception e) {
                logger.debug("Failed to discard trace: {}", e.getMessage());
            }
        }
    }

    public static void clearAll() {
        browserThreadLocal.remove();
        contextThreadLocal.remove();
        pageThreadLocal.remove();
    }
}