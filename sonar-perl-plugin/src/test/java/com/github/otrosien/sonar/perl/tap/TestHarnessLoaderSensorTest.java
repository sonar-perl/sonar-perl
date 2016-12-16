package com.github.otrosien.sonar.perl.tap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.sonar.api.measures.CoreMetrics.SKIPPED_TESTS;
import static org.sonar.api.measures.CoreMetrics.TESTS;
import static org.sonar.api.measures.CoreMetrics.*;

import java.io.File;

import org.junit.Test;
import org.sonar.api.batch.fs.InputFile.Type;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.fs.internal.FileMetadata;
import org.sonar.api.batch.sensor.internal.DefaultSensorDescriptor;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.internal.google.common.base.Charsets;

import com.github.otrosien.sonar.perl.PerlLanguage;

public class TestHarnessLoaderSensorTest {

    private final File baseDir = new File("src/test/resources/tap");
    private final SensorContextTester context = SensorContextTester.create(baseDir);

    @Test
    public void sensor_descriptor() {
      DefaultSensorDescriptor descriptor = new DefaultSensorDescriptor();

      createSensor().describe(descriptor);
      assertThat(descriptor.name()).isEqualTo("Perl Test::Harness Sensor");
      assertThat(descriptor.languages()).containsOnly("perl");
      assertThat(descriptor.type()).isEqualTo(Type.TEST);
    }

    @Test
    public void should_execute_on_simple_project() {
        String relativePath = "t/Project.t";
        inputFile(relativePath);
        createSensor().execute(context);
        assertThat(context.measure("moduleKey:t/Project.t", TEST_EXECUTION_TIME).value()).isEqualTo(26L);
        assertThat(context.measure("moduleKey:t/Project.t", TESTS).value()).isEqualTo(2);
        assertThat(context.measure("moduleKey:t/Project.t", TEST_FAILURES).value()).isEqualTo(1);
        assertThat(context.measure("moduleKey:t/Project.t", SKIPPED_TESTS).value()).isEqualTo(0);
    }

    private TestHarnessLoaderSensor createSensor() {
        context.settings().setProperty(TestHarnessArchiveProperties.HARNESS_ARCHIVE_PATH_KEY, "src/test/resources/tap/testReport.tgz");
        return new TestHarnessLoaderSensor();
    }

    private DefaultInputFile inputFile(String relativePath) {
      DefaultInputFile inputFile = new DefaultInputFile("moduleKey", relativePath)
        .setModuleBaseDir(baseDir.toPath())
        .setType(Type.TEST)
        .setLanguage(PerlLanguage.KEY);

      context.fileSystem().add(inputFile);

      return inputFile.initMetadata(new FileMetadata().readMetadata(inputFile.file(), Charsets.UTF_8));
    }
}
