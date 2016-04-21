package com.epages.sonar.perl;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.resources.Project;

public class GlobalSensor implements Sensor {

    private static final Logger log = LoggerFactory.getLogger(GlobalSensor.class);

    private final FileSystem fs;

    public GlobalSensor(FileSystem fs) {
        this.fs = fs;
    }

    @Override
    public boolean shouldExecuteOnProject(Project project) {
        return fs.hasFiles(fs.predicates().hasLanguage("perl"));
    }

    @Override
    public void analyse(Project module, SensorContext context) {
        for (InputFile inputFile : fs.inputFiles(fs.predicates().hasLanguage("perl"))) {
            this.analyseFile(inputFile, context);
        }
    }

    private void analyseFile(InputFile inputFile, SensorContext context) {
        log.debug("Analysing file {}", inputFile);
        File file = inputFile.file();
        context.saveMeasure(inputFile, new Measure<String>(CoreMetrics.NCLOC, (double) countLinesOfCode(file)));
    }

    private long countLinesOfCode(File file) {
        try (Stream<String> lines = Files.lines(file.toPath(), StandardCharsets.ISO_8859_1)) {
            return lines.filter(line -> !line.matches("^\\s*#")).count();
        } catch (IOException e) {
            log.error(String.format("Error during analysis of file '%s': '%s'", file.getAbsoluteFile(),
                    e.getMessage()), e);
        }
        return 0L;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
