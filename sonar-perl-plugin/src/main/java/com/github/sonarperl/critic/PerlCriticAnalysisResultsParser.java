package com.github.sonarperl.critic;

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
        log.info("Parsing file {}", file.getAbsolutePath());

        try (Stream<String> lines = Files.lines(file.toPath())) {
            return lines //
                    .map(this::parseLine) //
                    .filter(Optional::isPresent) //
                    .map(Optional::get)
                    .collect(Collectors.toList());
        }
    }

    /**
     * Parse Perl::Critic report file. Format inspired by EPIC
     * 
     *  // 0 = file
     *  // 1 = severity
     *  // 2 = line
     *  // 3 = column
     *  // 4 = message
     *  // 5 = PBP
     *  // 6 = policy
     *
     *  // %f~|~%s~|~%l~|~%c~|~%m~|~%e~|~%p~||~%n
     *
     * See {@link https://github.com/jploski/epic-ide/blob/b70f1c5ccb3e528f3a8c727b04dc633439f1d35a/org.epic.perleditor/src/org/epic/perleditor/editors/util/SourceCritic.java}
     */
    private Optional<PerlCriticViolation> parseLine(String line) {
        if(line.endsWith("OK")) {
            return Optional.empty();
        }
        if(! line.endsWith("~||~")) {
            log.warn("Invalid line '{}'", line);
            return Optional.empty();
        }
        String[] fields = line.replace("~||~", "").split("~\\|~");

        // TODO handle cases where a line returned from critic doesn't have all 7
        // expected fields, e.g. when dealing with messages containing newlines.
        if (fields.length != 7) {
            log.warn("Invalid line. Did not find 7 fields delimited by '~|~' in '{}'", line);
            return Optional.empty();
        }

        // @see org.epic.perleditor.editors.util.SourceCritic
        return Optional.of(new PerlCriticViolation(fields[6], fields[4], fields[0], "".equals(fields[2]) ? -1 : Integer.parseInt(fields[2])));
    }

}