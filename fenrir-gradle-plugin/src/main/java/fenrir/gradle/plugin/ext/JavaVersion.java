package fenrir.gradle.plugin.ext;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum JavaVersion {
    VERSION_11("11"), VERSION_17("17"), VERSION_19("19"), VERSION_21("21");

    private final String version;
}
