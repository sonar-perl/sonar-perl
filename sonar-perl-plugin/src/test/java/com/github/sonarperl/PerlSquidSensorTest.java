package com.github.sonarperl;


import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.InputFile.Type;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
import org.sonar.api.batch.sensor.internal.DefaultSensorDescriptor;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.measures.FileLinesContext;
import org.sonar.api.measures.FileLinesContextFactory;
import org.sonar.api.utils.log.LogTester;

import java.io.File;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PerlSquidSensorTest {

    private final File baseDir = new File("src/test/resources/com/github/sonarperl/plugins/perl/squid-sensor").getAbsoluteFile();

    private SensorContextTester context;

    @org.junit.Rule
    public LogTester logTester = new LogTester();

    @Before
    public void init() {
        context = SensorContextTester.create(baseDir);
    }

    @Test
    public void sensor_descriptor() {
        DefaultSensorDescriptor descriptor = new DefaultSensorDescriptor();
        sensor().describe(descriptor);

        assertThat(descriptor.name()).isEqualTo("Perl Squid Sensor");
        assertThat(descriptor.languages()).containsOnly("perl");
    }

    @Test
    public void test_execute() {
        inputFile("file1.pl");

        sensor().execute(context);

        String key = "moduleKey:file1.pl";
        String msg = "number of TypeOfText for the highlighting of keyword 'sub'";
        assertThat(context.highlightingTypeAt(key, 9, 2)).as(msg).hasSize(1);
        assertThat(context.allAnalysisErrors()).isEmpty();
    }

    private PerlSquidSensor sensor() {
        FileLinesContextFactory fileLinesContextFactory = mock(FileLinesContextFactory.class);
        FileLinesContext fileLinesContext = mock(FileLinesContext.class);
        when(fileLinesContextFactory.createFor(Mockito.any(InputFile.class))).thenReturn(fileLinesContext);
        return new PerlSquidSensor();
    }

    private InputFile inputFile(String name) {
        DefaultInputFile inputFile =  TestInputFileBuilder.create("moduleKey", name)
                .setModuleBaseDir(baseDir.toPath())
                .setCharset(StandardCharsets.UTF_8)
                .setType(Type.MAIN)
                .setLanguage(PerlLanguage.KEY)
                .initMetadata(TestUtils.fileContent(new File(baseDir, name), StandardCharsets.UTF_8))
                .build();
        context.fileSystem().add(inputFile);
        return inputFile;
    }

}

