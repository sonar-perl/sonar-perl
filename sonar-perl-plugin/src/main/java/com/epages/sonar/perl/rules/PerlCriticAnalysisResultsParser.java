package com.epages.sonar.perl.rules;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

class PerlCriticAnalysisResultsParser {

    private static final Logger log = Loggers.get(PerlCriticAnalysisResultsParser.class);

    public List<PerlCriticViolation> parse(final File file) throws IOException {
        PerlCriticIssuesLoaderSensor.log.info("Parsing file {}", file.getAbsolutePath());

        try (Stream<String> lines = Files.lines(file.toPath())) {
            return lines //
                    .map(line -> parseLine(line)) //
                    .filter(line -> line != null) //
                    .collect(Collectors.toList());
        }
    }

    // see https://github.com/jploski/epic-ide/blob/b70f1c5ccb3e528f3a8c727b04dc633439f1d35a/org.epic.perleditor/src/org/epic/perleditor/editors/util/SourceCritic.java
    private PerlCriticViolation parseLine(String line) {
        String[] tmp = line.split("~\\|~");

        // handle cases where a line returned from critic doesn't have all 7
        // expected fields
        if (tmp.length != 7) {
            log.warn("Cannot parse line '{}'", line);
            return null;
        }

        // @see org.epic.perleditor.editors.util.SourceCritic
        return new PerlCriticViolation(tmp[6], tmp[4], tmp[0], "".equals(tmp[2]) ? -1 : Integer.parseInt(tmp[2]));
    }

}