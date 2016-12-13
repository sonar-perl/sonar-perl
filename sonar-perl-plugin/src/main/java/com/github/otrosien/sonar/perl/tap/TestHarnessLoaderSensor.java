package com.github.otrosien.sonar.perl.tap;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

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

import com.github.otrosien.sonar.perl.PerlLanguage;
import com.github.otrosien.sonar.perl.tap.TestHarnessReport.Test;

public class TestHarnessLoaderSensor implements Sensor {

    private static final Logger log = Loggers.get(TestHarnessLoaderSensor.class);

    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor
        .onlyOnLanguage(PerlLanguage.KEY)
        .name("Perl Test::Harness Sensor")
        .onlyOnFileType(Type.TEST);
    }

    private Optional<String> getReportPath(SensorContext context) {
        String reportPath = context.settings().getString(TestHarnessArchive.HARNESS_ARCHIVE_PATH_KEY);
        log.info("Configured report path: {}", reportPath);
        return Optional.ofNullable(reportPath);
    }

    @Override
    public void execute(SensorContext context) {

        Optional<String> reportPath = getReportPath(context);
        Optional<File> reportFile = reportPath
            .map(File::new)
            .filter(File::exists);

        if(reportFile.isPresent()) {
            try {
                Optional<TestHarnessReport> report = new TestHarnessArchiveReader().read(reportFile.get());
                report.ifPresent(r -> new TestHarnessLoaderSensorExecutor(context).saveTestReportMeasures(r));
            } catch (IOException e) {
                log.error("Error reading Test::Harness::Archive report.", e);
            }
        } else {
            log.info("Test::Harness::Archive report file '{}' does not exist. Skipping...", reportPath.orElse(""));
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
                InputFile testFile = getUnitTestInputFile(t);
                context.<Long>newMeasure().on(testFile).withValue((long)t.getDuration()).forMetric(CoreMetrics.TEST_EXECUTION_TIME).save();
            }
        }

        /**
         * Gets the file pointed by the report.
         *
         * @param report the unit test report
         */
        private InputFile getUnitTestInputFile(TestHarnessReport.Test test) {
          return fileSystem.inputFile(fileSystem.predicates().and(
            filePredicates.hasPath(test.getFilePath()),
            filePredicates.hasType(InputFile.Type.TEST),
            filePredicates.hasLanguage(PerlLanguage.KEY)));
        }
    }

}
