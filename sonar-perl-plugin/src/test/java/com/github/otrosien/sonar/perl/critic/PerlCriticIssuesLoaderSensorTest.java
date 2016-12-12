package com.github.otrosien.sonar.perl.critic;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.fs.InputFile.Type;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.fs.internal.FileMetadata;
import org.sonar.api.batch.rule.internal.ActiveRulesBuilder;
import org.sonar.api.batch.sensor.internal.DefaultSensorDescriptor;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.config.Settings;
import org.sonar.api.internal.google.common.base.Charsets;
import org.sonar.api.rule.RuleKey;

import com.github.otrosien.sonar.perl.PerlLanguage;

public class PerlCriticIssuesLoaderSensorTest {

    private final File baseDir = new File("src/test/resources/basic");
    private final SensorContextTester context = SensorContextTester.create(baseDir);
    private Settings settings = new Settings();

    @Before
    public void setActiveRules() {
        context.setActiveRules(
                new ActiveRulesBuilder()
                .create(RuleKey.of("PerlCritic", "TestingAndDebugging::RequireUseStrict")).activate()
                .build()
        );
    }
    
    @Test
    public void sensor_descriptor() {
      DefaultSensorDescriptor descriptor = new DefaultSensorDescriptor();

      createSensor().describe(descriptor);
      assertThat(descriptor.name()).isEqualTo("PerlCritic Sensor");
      assertThat(descriptor.languages()).containsOnly("perl");
      assertThat(descriptor.type()).isEqualTo(Type.MAIN);
    }

    @Test
    public void should_execute_on_simple_project() {
        String relativePath = "lib/Sample/Project.pm";
        inputFile(relativePath);
        createSensor().execute(context);
        assertThat(context.allIssues()).hasSize(3);
    }

    @Test
    public void should_not_throw_on_report_file_not_found() {
        String relativePath = "lib/Sample/Project.pm";
        inputFile(relativePath);
        settings.setProperty(PerlCritic.PERLCRITIC_REPORT_PATH_KEY, "src/test/resources/basic/perlcritic_nonexistant_report.txt");
        new PerlCriticIssuesLoaderSensor(settings).execute(context);
    }

    private PerlCriticIssuesLoaderSensor createSensor() {
        settings.setProperty(PerlCritic.PERLCRITIC_REPORT_PATH_KEY, "src/test/resources/basic/perlcritic_report.txt");
        return new PerlCriticIssuesLoaderSensor(settings);
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
