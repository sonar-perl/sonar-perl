package com.github.sonarperl;

import org.sonar.api.batch.fs.FilePredicates;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PerlSquidSensor implements Sensor {

    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor
                .onlyOnLanguage(PerlLanguage.KEY)
                .name("Perl Squid Sensor");
    }

    @Override
    public void execute(SensorContext context) {
        FilePredicates p = context.fileSystem().predicates();
        Iterable<InputFile> it = context.fileSystem().inputFiles(p.hasLanguage(PerlLanguage.KEY));
        List<InputFile> list = new ArrayList<>();
        it.forEach(list::add);
        List<InputFile> inputFiles = Collections.unmodifiableList(list);

        PerlScanner scanner = new PerlScanner(context, inputFiles);
        scanner.scanFiles();
    }
}
