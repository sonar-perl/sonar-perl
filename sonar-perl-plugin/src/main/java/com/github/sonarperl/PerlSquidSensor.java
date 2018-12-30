package com.github.sonarperl;

import org.sonar.api.batch.fs.FilePredicates;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.rule.CheckFactory;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.issue.NoSonarFilter;
import org.sonar.api.measures.FileLinesContextFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PerlSquidSensor implements Sensor {

    private final FileLinesContextFactory fileLinesContextFactory;
    private final NoSonarFilter noSonarFilter;

    public PerlSquidSensor(FileLinesContextFactory fileLinesContextFactory, CheckFactory checkFactory, NoSonarFilter noSonarFilter) {
        this.fileLinesContextFactory = fileLinesContextFactory;
        this.noSonarFilter = noSonarFilter;
    }

    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor
                .onlyOnLanguage(PerlLanguage.KEY)
                .name("Perl Squid Sensor")
                .onlyOnFileType(InputFile.Type.MAIN);
    }

    @Override
    public void execute(SensorContext context) {
        FilePredicates p = context.fileSystem().predicates();
        Iterable<InputFile> it = context.fileSystem().inputFiles(p.and(p.hasType(InputFile.Type.MAIN), p.hasLanguage(PerlLanguage.KEY)));
        List<InputFile> list = new ArrayList<>();
        it.forEach(list::add);
        List<InputFile> inputFiles = Collections.unmodifiableList(list);

        PerlScanner scanner = new PerlScanner(context, inputFiles);
        scanner.scanFiles();
    }
}
