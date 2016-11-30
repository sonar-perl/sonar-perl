package com.github.otrosien.sonar.perl;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
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

    enum LineType {
        COMMENT,
        CODE;
    }

    @Override
    public void describe(SensorDescriptor descriptor) {
      descriptor
        .onlyOnLanguage(PerlLanguage.KEY)
        .name("Perl Sensor")
        .onlyOnFileType(Type.MAIN);
    }

    @Override
    public void execute(SensorContext context) {
        FileSystem fileSystem = context.fileSystem();

        FilePredicate mainFilePredicate = fileSystem.predicates().and(
          fileSystem.predicates().hasType(InputFile.Type.MAIN),
          fileSystem.predicates().hasLanguage(PerlLanguage.KEY));

        ArrayList<InputFile> inputFiles = Lists.newArrayList(fileSystem.inputFiles(mainFilePredicate));

        for (InputFile inputFile : inputFiles) {
            this.analyseFile(inputFile, context);
        }
    }

    private void analyseFile(InputFile inputFile, SensorContext context) {
        log.debug("Analysing file {}", inputFile);
        File file = inputFile.file();
        Map<LineType, AtomicInteger> lines = countLines(file);
        context.<Integer>newMeasure().on(inputFile)
            .withValue(lines.get(LineType.CODE).get())
            .forMetric(CoreMetrics.NCLOC).save();
        context.<Integer>newMeasure().on(inputFile)
            .withValue(lines.get(LineType.COMMENT).get())
            .forMetric(CoreMetrics.COMMENT_LINES).save();
    }

    private Map<LineType,AtomicInteger> countLines(File file) {

        final AtomicInteger currentIsComment = new AtomicInteger();
        Map<LineType, AtomicInteger> counters = new EnumMap<>(LineType.class);
        counters.put(LineType.CODE, new AtomicInteger());
        counters.put(LineType.COMMENT, new AtomicInteger());

        try (Stream<String> lines = Files.lines(file.toPath(), StandardCharsets.ISO_8859_1)) {
            lines
            .filter(line -> !line.matches("^\\s*$"))
            .forEach(line -> {
                if (line.matches("^=(pod|head1|head2|head3|head4|head|over|item|back|begin|end|for|encoding)\\b.*")) {
                    currentIsComment.getAndSet(1);
                    counters.get(LineType.COMMENT).getAndIncrement();
                } else if (line.matches("^=cut\\b.*")) {
                    currentIsComment.getAndSet(0);
                    counters.get(LineType.COMMENT).getAndIncrement();
                } else if (line.matches("\\s*\\#.*")) {
                    counters.get(LineType.COMMENT).getAndIncrement();
                } else {
                    if(currentIsComment.get() == 1) {
                        counters.get(LineType.COMMENT).getAndIncrement();
                    } else {
                        counters.get(LineType.CODE).getAndIncrement();
                    }
                }
            });
        } catch (IOException e) {
            log.error(String.format("Error during analysis of file '%s': '%s'", file.getAbsoluteFile(),
                    e.getMessage()), e);
        }
        return counters;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
