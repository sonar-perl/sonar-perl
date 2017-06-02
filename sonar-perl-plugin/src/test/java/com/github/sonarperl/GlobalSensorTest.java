package com.github.sonarperl;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.junit.Test;
import org.sonar.api.batch.fs.InputFile.Type;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.fs.internal.FileMetadata;
import org.sonar.api.batch.sensor.internal.DefaultSensorDescriptor;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.internal.google.common.base.Charsets;
import org.sonar.api.measures.CoreMetrics;

import com.github.sonarperl.GlobalSensor;
import com.github.sonarperl.PerlLanguage;

public class GlobalSensorTest {

    private final File baseDir = new File("src/test/resources");
    private final SensorContextTester context = SensorContextTester.create(baseDir);

    @Test
    public void sensor_descriptor() {
      DefaultSensorDescriptor descriptor = new DefaultSensorDescriptor();

      createSensor().describe(descriptor);
      assertThat(descriptor.name()).isEqualTo("Perl Sensor");
      assertThat(descriptor.languages()).containsOnly("perl");
      assertThat(descriptor.type()).isEqualTo(Type.MAIN);
    }

    @Test
    public void should_execute_on_simple_project() {
        String relativePath = "basic/lib/Sample/Project.pm";
        inputFile(relativePath);
        createSensor().execute(context);

        String key = "moduleKey:" + relativePath;
        assertThat(context.measure(key, CoreMetrics.NCLOC).value()).isEqualTo(15);
        assertThat(context.measure(key, CoreMetrics.COMMENT_LINES).value()).isEqualTo(26);
        assertThat(context.measure(key, CoreMetrics.CLASSES).value()).isEqualTo(1);
        assertThat(context.measure(key, CoreMetrics.FUNCTIONS).value()).isEqualTo(3);
    }

    @Test
    public void should_execute_on_edge_cases() {
        String relativePath = "basic/lib/Sample/EdgeCases.pm";
        inputFile(relativePath);
        createSensor().execute(context);

        String key = "moduleKey:" + relativePath;
        assertThat(context.measure(key, CoreMetrics.CLASSES).value()).isEqualTo(1);
        assertThat(context.measure(key, CoreMetrics.FUNCTIONS).value()).isEqualTo(3);
    }

    @Test
    public void should_execute_illustrating_ignored_subs_and_existing_parse_problem() {
        String relativePath = "basic/lib/Sample/Ignored.pm";
        inputFile(relativePath);
        createSensor().execute(context);

        String key = "moduleKey:" + relativePath;
        assertThat(context.measure(key, CoreMetrics.CLASSES).value()).isEqualTo(0);
        assertThat(context.measure(key, CoreMetrics.FUNCTIONS).value()).isEqualTo(0);
    }

    private GlobalSensor createSensor() {
        return new GlobalSensor();
    }

    private DefaultInputFile inputFile(String relativePath) {
      DefaultInputFile inputFile = new DefaultInputFile("moduleKey", relativePath)
        .setModuleBaseDir(baseDir.toPath())
        .setType(Type.MAIN)
        .setLanguage(PerlLanguage.KEY);

      context.fileSystem().add(inputFile);

      return inputFile.initMetadata(new FileMetadata().readMetadata(inputFile.file(), Charsets.UTF_8));
    }

}
