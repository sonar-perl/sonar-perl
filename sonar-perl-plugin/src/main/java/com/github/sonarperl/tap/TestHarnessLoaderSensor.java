package com.github.sonarperl.tap;

import com.github.sonarperl.PerlLanguage;
import com.github.sonarperl.tap.TestHarnessReport.Test;
import com.github.sonarperl.tap.TestHarnessReport.TestDetail;
import org.sonar.api.batch.fs.FilePredicates;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.InputFile.Type;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class TestHarnessLoaderSensor implements Sensor {

    private static final Logger log = Loggers.get(TestHarnessLoaderSensor.class);

    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor
        .onlyOnLanguage(PerlLanguage.KEY)
        .name("Perl Test::Harness Sensor")
        .onlyOnFileType(Type.TEST);
    }

    private Optional<String> getArchiveReportPath(SensorContext context) {
        Optional<String> reportPath = context.config().get(TestHarnessArchiveProperties.HARNESS_ARCHIVE_PATH_KEY);
        log.info("Configured archive report path: {}", reportPath);
        return reportPath;
    }
    private Optional<String> getJUnitReportPath(SensorContext context) {
        Optional<String> reportPath = context.config().get(TestHarnessJUnitProperties.HARNESS_JUNIT_PATH_KEY);
        log.info("Configured junit report path: {}", reportPath);
        return reportPath;
    }

    @Override
    public void execute(SensorContext context) {

        Optional<String> archiveReportPath = getArchiveReportPath(context);
        Optional<File> archiveReportFile = archiveReportPath
            .map(File::new)
            .filter(File::exists);

        Optional<String> junitReportPath = getJUnitReportPath(context);
        Optional<File> junitReportFile = junitReportPath
            .map(File::new)
            .filter(File::exists);

        if(archiveReportFile.isPresent()) {
            try {
                Optional<TestHarnessReport> report = new TestHarnessArchiveReader().read(archiveReportFile.get());
                report.ifPresent(r -> new TestHarnessLoaderSensorExecutor(context).saveTestReportMeasures(r)); // NOSONAR
            } catch (IOException e) {
                log.error("Error reading Test::Harness::Archive report.", e);
            }
        }
        else if (junitReportFile.isPresent()) {
            try {
                Optional<TestHarnessReport> report = new TestHarnessJUnitReader().read(junitReportFile.get());
                report.ifPresent(r -> new TestHarnessLoaderSensorExecutor(context).saveTestReportMeasures(r)); // NOSONAR
            } catch (IOException e) {
                log.error("Error reading Test::Harness::JUnit report.", e);
            }
        }
        else {
            log.info("None of Test::Harness::Archive ({}) or Test::Harness::JUnit ({}) report files exist. Skipping...", archiveReportPath.orElse(""), junitReportPath.orElse(""));
        }
    }

    static class TestHarnessLoaderSensorExecutor {
        private final SensorContext context;
        private final FileSystem fileSystem;
        private final FilePredicates filePredicates;

        public TestHarnessLoaderSensorExecutor(SensorContext context) {
            this.context = context;
            this.fileSystem = context.fileSystem();
            this.filePredicates = this.fileSystem.predicates();
        }
    
        void saveTestReportMeasures(TestHarnessReport fileReport) {
            for(Test t : fileReport.getTests()) {
                InputFile testFile = getUnitTestInputFile(t.getFilePath());
                if(testFile != null) {
                    context.<Long>newMeasure().on(testFile).withValue(t.getDuration()).forMetric(CoreMetrics.TEST_EXECUTION_TIME).save();
                }
            }
            for(TestDetail d : fileReport.getTestDetails()) {
                InputFile testFile = getUnitTestInputFile(d.getFilePath());
                if(testFile != null) {
                    context.<Integer>newMeasure().on(testFile).withValue(d.getNumberOfTests()).forMetric(CoreMetrics.TESTS).save();
                    context.<Integer>newMeasure().on(testFile).withValue(d.getFailed()).forMetric(CoreMetrics.TEST_FAILURES).save();
                    context.<Integer>newMeasure().on(testFile).withValue(d.getSkipped()).forMetric(CoreMetrics.SKIPPED_TESTS).save();
                }
            }
        }

        /**
         * Gets the file pointed by the report.
         *
         * @param filePath the unit test report
         */
        private InputFile getUnitTestInputFile(String filePath) {
          return fileSystem.inputFile(fileSystem.predicates().and(
            filePredicates.hasPath(filePath),
            filePredicates.hasType(InputFile.Type.TEST),
            filePredicates.hasLanguage(PerlLanguage.KEY)));
        }
    }

}
