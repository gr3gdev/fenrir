package io.github.gr3gdev.benchmark;

import io.github.gr3gdev.benchmark.test.FenrirTest;
import io.github.gr3gdev.benchmark.test.QuarkusTest;
import io.github.gr3gdev.benchmark.test.SpringTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        SpringTest.class,
        QuarkusTest.class,
        FenrirTest.class
})
public class TestSuite {

}
