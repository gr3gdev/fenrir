package io.github.gr3gdev.benchmark;

import io.github.gr3gdev.benchmark.test.data.Framework;
import io.github.gr3gdev.benchmark.test.data.Report;
import io.github.gr3gdev.benchmark.test.utils.ReportUtils;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestPlan;

import java.io.IOException;
import java.net.http.HttpClient;
import java.nio.file.Files;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;

@SuppressWarnings("unused")
public class BeforeAfterSuiteListener implements TestExecutionListener {

    @Override
    public void testPlanExecutionStarted(TestPlan testPlan) {
        if (!ReportUtils.REPORT_DIR.exists()) {
            try {
                Files.createDirectory(ReportUtils.REPORT_DIR.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        TestSuite.client = HttpClient.newBuilder()
                .connectTimeout(Duration.of(10, ChronoUnit.SECONDS))
                .build();

        TestSuite.reports = new HashMap<>();
        Arrays.stream(Framework.values())
                .forEach(f -> TestSuite.reports.put(f, new Report()));
    }

    @Override
    public void testPlanExecutionFinished(TestPlan testPlan) {
        // After all tests
        TestSuite.client.close();
        ReportUtils.report();
    }
}
