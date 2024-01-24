package io.github.gr3gdev.benchmark.test;

import io.github.gr3gdev.benchmark.test.data.Framework;

public class QuarkusTest extends AbstractTest {
    @Override
    protected Framework getFramework() {
        return Framework.QUARKUS;
    }
}
