package io.github.gr3gdev.benchmark;

import io.github.gr3gdev.benchmark.test.FenrirTest;
import io.github.gr3gdev.benchmark.test.QuarkusTest;
import io.github.gr3gdev.benchmark.test.SpringTest;
import io.github.gr3gdev.benchmark.test.data.Framework;
import io.github.gr3gdev.benchmark.test.data.Report;
import io.github.gr3gdev.bench.data.Request;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.Map;

@Suite
@SelectClasses({
        SpringTest.class,
        QuarkusTest.class,
        FenrirTest.class
})
public class TestSuite {
    public static Report report;
    public static HttpClient client;
    public static Map<Framework, Map<Request.Data, HttpResponse<String>>> responses;
}
