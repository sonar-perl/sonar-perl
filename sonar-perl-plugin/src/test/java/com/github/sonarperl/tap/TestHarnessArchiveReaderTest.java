package com.github.sonarperl.tap;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Optional;

import org.junit.Test;

import com.github.sonarperl.tap.TestHarnessArchiveReader;
import com.github.sonarperl.tap.TestHarnessReport;

public class TestHarnessArchiveReaderTest {

    @Test
    public void should_analyze_testreport() throws Exception {
        TestHarnessArchiveReader reader = new TestHarnessArchiveReader();
        TestHarnessReport report = reader.read(sampleFile()).get();
        assertThat(report.getStartTime()).isEqualTo(new BigDecimal("1481788318"));
        assertThat(report.getEndTime()).isEqualTo(new BigDecimal("1481788318"));
        assertThat(report.getTests()).hasSize(1);
        assertThat(report.getTestDetails()).hasSize(1);

        TestHarnessReport.Test test0 = report.getTests().get(0);
        assertThat(test0.getFilePath()).isEqualTo("t/Project.t");
        assertThat(test0.getStartTime()).isEqualTo(new BigDecimal("1481788318.51594"));
        assertThat(test0.getEndTime()).isEqualTo(new BigDecimal("1481788318.54274"));

        TestHarnessReport.TestDetail testDetail0 = report.getTestDetails().get(0);
        assertThat(testDetail0.getFilePath()).isEqualTo("t/Project.t");
        assertThat(testDetail0.getPassed()).isEqualTo(1);
        assertThat(testDetail0.getFailed()).isEqualTo(1);
        assertThat(testDetail0.getNumberOfTests()).isEqualTo(2);
        assertThat(testDetail0.getSkipped()).isZero();
    }

    @Test
    public void should_not_throw_on_damaged_testreport() throws Exception {
        TestHarnessArchiveReader reader = new TestHarnessArchiveReader();
        Optional<TestHarnessReport> report = reader.read(brokenFile());
        assertThat(report).isEmpty();
    }

    @Test
    public void should_not_throw_on_tar_report() throws Exception {
        TestHarnessArchiveReader reader = new TestHarnessArchiveReader();
        Optional<TestHarnessReport> report = reader.read(tarFile());
        assertThat(report).isNotEmpty();
    }

    private File sampleFile() throws URISyntaxException {
        return Paths.get(ClassLoader.getSystemResource("tap/testReport.tgz").toURI()).toFile();
    }

    private File brokenFile() throws URISyntaxException {
        return Paths.get(ClassLoader.getSystemResource("tap/testReport_broken.tgz").toURI()).toFile();
    }

    private File tarFile() throws URISyntaxException {
        return Paths.get(ClassLoader.getSystemResource("tap/testReport.tar").toURI()).toFile();
    }

}
