package com.github.sonarperl.tap;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Optional;

import org.junit.Test;

import com.github.sonarperl.tap.TestHarnessReport.TestDetail;

public class TestHarnessJUnitReaderTest {

    // TAP::Formatter::JUnit
    @Test
    public void should_analyze_testreport() throws Exception {
        TestHarnessJUnitReader reader = new TestHarnessJUnitReader();
        Optional<TestHarnessReport> report = reader.read(sampleFile1());
        assertThat(report).isNotEmpty();

        TestHarnessReport r = report.get();
        assertThat(r.getTests()).hasSize(1);
        TestHarnessReport.Test t = r.getTests().get(0);
        assertThat(t.getFilePath()).isEqualTo("Project_t");
        assertThat(r.getTestDetails()).hasSize(1);
        TestDetail d = r.getTestDetails().get(0);
        assertThat(d.getFilePath()).isEqualTo("Project.t"); // shouldn't this be t/Project.t?
        assertThat(d.getFailed()).isEqualTo(1);
        assertThat(d.getNumberOfTests()).isEqualTo(2);
    }

    private File sampleFile1() throws URISyntaxException {
        return Paths.get(ClassLoader.getSystemResource("tap_junit1/junit_reports/t").toURI()).toFile();
    }

    // TAP::Harness::JUnit
    @Test
    public void should_analyze_testreport2() throws Exception {
        TestHarnessJUnitReader reader = new TestHarnessJUnitReader();
        Optional<TestHarnessReport> report = reader.read(sampleFile2());
        assertThat(report).isNotEmpty();

        TestHarnessReport r = report.get();
        assertThat(r.getTests()).hasSize(1);
        TestHarnessReport.Test t = r.getTests().get(0);
        assertThat(t.getFilePath()).isEqualTo("t.Project_t");
        assertThat(r.getTestDetails()).hasSize(1);
        TestDetail d = r.getTestDetails().get(0);
        assertThat(d.getFilePath()).isEqualTo("t/Project.t");
        assertThat(d.getFailed()).isEqualTo(1);
        assertThat(d.getNumberOfTests()).isEqualTo(2);
    }

    private File sampleFile2() throws URISyntaxException {
        return Paths.get(ClassLoader.getSystemResource("tap_junit2/junit_output.xml").toURI()).toFile();
    }
}
