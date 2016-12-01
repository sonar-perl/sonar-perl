package com.github.otrosien.sonar.perl.critic;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

class PerlCriticAnalysisResultsParser {

    private static final Logger log = Loggers.get(PerlCriticAnalysisResultsParser.class);

    public List<PerlCriticViolation> parse(final File file) throws IOException {
        PerlCriticIssuesLoaderSensor.log.info("Parsing file {}", file.getAbsolutePath());

        // TODO: support run-away lines sometimes produced by perlcritc.
        try (Stream<String> lines = Files.lines(file.toPath())) {
            return lines //
                    .map(this::parseLine) //
                    .filter(Optional::isPresent) //
                    .map(Optional::get)
                    .collect(Collectors.toList());
        }
    }

    // see https://github.com/jploski/epic-ide/blob/b70f1c5ccb3e528f3a8c727b04dc633439f1d35a/org.epic.perleditor/src/org/epic/perleditor/editors/util/SourceCritic.java
    private Optional<PerlCriticViolation> parseLine(String line) {
        if(line.endsWith("OK")) {
            return Optional.empty();
        }
        if(! line.endsWith("~||~")) {
            log.warn("Invalid line '{}'", line);
            return Optional.empty();
        }
        String[] tmp = line.replace("~||~", "").split("~\\|~");

        // handle cases where a line returned from critic doesn't have all 7
        // expected fields
        if (tmp.length != 7) {
            log.warn("Invalid line. Did not find 7 fields delimited by '~|~' in '{}'", line);
            return Optional.empty();
        }

        // @see org.epic.perleditor.editors.util.SourceCritic
        return Optional.of(new PerlCriticViolation(tmp[6], tmp[4], tmp[0], "".equals(tmp[2]) ? -1 : Integer.parseInt(tmp[2])));
    }

}