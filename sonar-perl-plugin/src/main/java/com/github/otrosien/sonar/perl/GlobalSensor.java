package com.github.otrosien.sonar.perl;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.stream.Stream;

import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.InputFile.Type;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import com.google.common.collect.Lists;

public class GlobalSensor implements Sensor {

    private static final Logger log = Loggers.get(GlobalSensor.class);

    private FileSystem fileSystem;

    @Override
    public void describe(SensorDescriptor descriptor) {
      descriptor
        .onlyOnLanguage(PerlLanguage.KEY)
        .name("Perl sensor")
        .onlyOnFileType(Type.MAIN);
    }

    @Override
    public void execute(SensorContext context) {
        this.fileSystem = context.fileSystem();

        FilePredicate mainFilePredicate = this.fileSystem.predicates().and(
          this.fileSystem.predicates().hasType(InputFile.Type.MAIN),
          this.fileSystem.predicates().hasLanguage(PerlLanguage.KEY));

        ArrayList<InputFile> inputFiles = Lists.newArrayList(fileSystem.inputFiles(mainFilePredicate));

        for (InputFile inputFile : inputFiles) {
            this.analyseFile(inputFile, context);
        }
    }

    private void analyseFile(InputFile inputFile, SensorContext context) {
        log.debug("Analysing file {}", inputFile);
        File file = inputFile.file();
        context.<Integer>newMeasure().on(inputFile).withValue(countLinesOfCode(file)).forMetric(CoreMetrics.NCLOC);
    }

    private Integer countLinesOfCode(File file) {
        try (Stream<String> lines = Files.lines(file.toPath(), StandardCharsets.ISO_8859_1)) {
            return Math.toIntExact(lines.filter(line -> !line.matches("^\\s*#") && !line.matches("^\\s*$")).count());
        } catch (IOException e) {
            log.error(String.format("Error during analysis of file '%s': '%s'", file.getAbsoluteFile(),
                    e.getMessage()), e);
        }
        return 0;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
