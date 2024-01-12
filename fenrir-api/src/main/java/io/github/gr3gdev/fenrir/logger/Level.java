package io.github.gr3gdev.fenrir.logger;

public class Level extends java.util.logging.Level {


    private static final String defaultBundle = "sun.util.logging.resources.logging";

    public static final java.util.logging.Level ERROR = new Level("ERROR", 1000, defaultBundle);
    public static final java.util.logging.Level WARN = new Level("WARN", 900, defaultBundle);
    public static final java.util.logging.Level DEBUG = new Level("DEBUG", 600, defaultBundle);

    protected Level(String name, int value, String resourceBundleName) {
        super(name, value, resourceBundleName);
    }
}
