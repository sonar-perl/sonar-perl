package com.github.sonarperl;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
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

public class GlobalSensor implements Sensor {

    private static final Logger log = Loggers.get(GlobalSensor.class);

    enum CounterType {
        COMMENT,
        CODE,
        CLASS,
        FUNCTION;
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

        List<InputFile> inputFiles = new ArrayList<>();
        fileSystem.inputFiles(mainFilePredicate).forEach(inputFiles::add);

        for (InputFile inputFile : inputFiles) {
            this.analyseFile(inputFile, fileSystem.encoding(), context);
        }
    }

    private void analyseFile(InputFile inputFile, Charset charset, SensorContext context) {
        log.debug("Analysing file {}", inputFile);
        File file = inputFile.file();
        Map<CounterType, AtomicInteger> lines = countLines(file, charset);

        context.<Integer>newMeasure().on(inputFile)
            .withValue(lines.get(CounterType.CODE).get())
            .forMetric(CoreMetrics.NCLOC).save();

        context.<Integer>newMeasure().on(inputFile)
        .withValue(lines.get(CounterType.COMMENT).get())
        .forMetric(CoreMetrics.COMMENT_LINES).save();

        context.<Integer>newMeasure().on(inputFile)
        .withValue(lines.get(CounterType.CLASS).get())
        .forMetric(CoreMetrics.CLASSES).save();

        context.<Integer>newMeasure().on(inputFile)
        .withValue(lines.get(CounterType.FUNCTION).get())
        .forMetric(CoreMetrics.FUNCTIONS).save();
    }

    private Map<CounterType,AtomicInteger> countLines(File file, Charset charset) {

        final AtomicInteger currentIsComment = new AtomicInteger();
        Map<CounterType, AtomicInteger> counters = new EnumMap<>(CounterType.class);
        counters.put(CounterType.CODE, new AtomicInteger());
        counters.put(CounterType.COMMENT, new AtomicInteger());
        counters.put(CounterType.CLASS, new AtomicInteger());
        counters.put(CounterType.FUNCTION, new AtomicInteger());

        try (Stream<String> lines = Files.lines(file.toPath(), charset)) {
            lines
            .filter(line -> !line.matches("^\\s*$"))
            .forEach(line -> {
                if (line.matches("^=(pod|head1|head2|head3|head4|head|over|item|back|begin|end|for|encoding)\\b.*")) {
                    currentIsComment.getAndSet(1);
                    counters.get(CounterType.COMMENT).getAndIncrement();
                } else if (line.matches("^=cut\\b.*")) {
                    currentIsComment.getAndSet(0);
                    counters.get(CounterType.COMMENT).getAndIncrement();
                } else if (line.matches("\\s*\\#.*")) {
                    counters.get(CounterType.COMMENT).getAndIncrement();
                } else {
                    if(currentIsComment.get() == 1) {
                        counters.get(CounterType.COMMENT).getAndIncrement();
                    } else {
                        counters.get(CounterType.CODE).getAndIncrement();
                        /* matches
                         sub xy { -- simple case
                         sub ($a, $b) { -- sub with signature // NOSONAR
                         sub xy  -- (limitation of eventually having opening brace on next line)
                         ---------------
                         does not match:
                         sub xy; (prototype)

                         requires sub to start on a new line, as this probably reduces some other false positives and
                         can be safely assumed for > 99% of the existing perl code.
                         */
                        if(line.matches("^\\s*sub\\s+\\S+\\([^)]*\\)\\s*;.*")) {
                            // prototype. skip.
                        } else if(line.matches("^\\s*sub\\s+[^{]+.*")) {
                            counters.get(CounterType.FUNCTION).getAndIncrement();
                        } else if(line.matches("\\s*package\\b.*")) {
                            // perl doesn't have any syntax for classes, only packages,
                            // and so we assume a package declaration introduces a class
                            // see http://perldoc.perl.org/perlmod.html#Perl-Classes
                            counters.get(CounterType.CLASS).getAndIncrement();
                        }
                    }
                }
            });
        } catch (IOException e) {
            log.error(String.format("Error during analysis of file '%s': '%s'", file.getAbsoluteFile(),
                    e.getMessage()), e);
        }
        return counters;
    }

}
