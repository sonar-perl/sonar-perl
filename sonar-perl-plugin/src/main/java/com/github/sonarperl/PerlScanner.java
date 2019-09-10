package com.github.sonarperl;


import com.github.sonarperl.cpd.PerlCpdAnalyzer;
import com.github.sonarperl.highlighter.PerlHighlighter;
import com.github.sonarperl.parser.PerlParser;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.RecognitionException;
import com.sonar.sslr.impl.Parser;
import java.util.List;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

public class PerlScanner {

    private static final Logger LOG = Loggers.get(PerlScanner.class);

    private final SensorContext context;
    private final Parser<Grammar> parser;
    private final List<InputFile> inputFiles;
    private final PerlCpdAnalyzer perlCpdAnalyzer;

    public PerlScanner(SensorContext context, List<InputFile> inputFiles) {
        this.context = context;
        this.inputFiles = inputFiles;
        this.parser = PerlParser.create(new PerlConfiguration(context.fileSystem().encoding()));
        this.perlCpdAnalyzer = new PerlCpdAnalyzer(context);
    }

    public void scanFiles() {
        for (InputFile perlFile : inputFiles) {
            if (context.isCancelled()) {
                return;
            }
            scanFile(perlFile);
        }
    }

    private void scanFile(InputFile inputFile) {
        PerlFile perlFile = SonarQubePerlFile.create(inputFile);
        PerlVisitorContext visitorContext;
        try {
            visitorContext = new PerlVisitorContext(parser.parse(perlFile.content()), perlFile);

        } catch (IllegalStateException e) {
            LOG.error("Unable to read file: " + perlFile.fileName(), e);
            return;

        } catch (RecognitionException e) {
            visitorContext = new PerlVisitorContext(perlFile, e);
            LOG.error("Unable to parse file: " + inputFile.toString());
            LOG.error(e.getMessage());
            int line = e.getLine() > 0 ? e.getLine() : 1;
            context.newAnalysisError()
                    .onFile(inputFile)
                    .at(inputFile.newPointer(line, 0))
                    .message(e.getMessage())
                    .save();
        }
        new PerlHighlighter(context, inputFile).scanFile(visitorContext);
        perlCpdAnalyzer.pushCpdTokens(inputFile, visitorContext);
    }
}
