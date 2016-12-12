package com.github.otrosien.sonar.perl.critic;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.InputFile.Type;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.config.Settings;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import com.github.otrosien.sonar.perl.PerlLanguage;

public class PerlCriticIssuesLoaderSensor implements org.sonar.api.batch.sensor.Sensor {

    private static final Logger log = Loggers.get(PerlCriticIssuesLoaderSensor.class);

    private FileSystem fileSystem;
    private SensorContext context;

    @Override
    public void describe(SensorDescriptor descriptor) {
      descriptor
        .onlyOnLanguage(PerlLanguage.KEY)
        .name("PerlCritic Sensor")
        .onlyOnFileType(Type.MAIN);
    }

    private Optional<String> getReportPath(SensorContext context) {
        String reportPath = context.settings().getString(PerlCritic.PERLCRITIC_REPORT_PATH_KEY);
        log.info("Configured report path: {}", reportPath);
        return Optional.ofNullable(reportPath);
    }

    @Override
    public void execute(SensorContext context) {

        Optional<String> reportPath = getReportPath(context);
        Optional<File> reportFile = reportPath
            .map(File::new)
            .filter(File::exists);

        try {
            if(reportFile.isPresent()) {
                this.context = context;
                this.fileSystem = context.fileSystem();
                parseAndSaveResults(reportFile.get());
            } else {
                log.info("PerlCritic report file '{}' does not exist. Skipping...", reportPath.orElse(""));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to parse the provided PerlCritic report file", e);
        } finally {
            this.context = null;
            this.fileSystem = null;
        }
    }

    protected void parseAndSaveResults(final File file) throws IOException {
        log.info("Parsing PerlCritic Analysis Results");
        PerlCriticAnalysisResultsParser parser = new PerlCriticAnalysisResultsParser();
        List<PerlCriticViolation> parseResult = parser.parse(file);
        log.info("Found {} PerlCritic violations.", parseResult.size());
        for (PerlCriticViolation error : parseResult) {
            getResourceAndSaveIssue(error);
        }
    }

    private void getResourceAndSaveIssue(PerlCriticViolation violation) {
        log.debug(violation.toString());

        InputFile inputFile = fileSystem
                .inputFile(fileSystem.predicates().hasRelativePath(violation.getFilePath()));

        if (inputFile != null) {
            saveIssue(inputFile, violation.getLine(), violation.getType(), violation.getDescription());
        } else {
            log.warn("Not able to find an InputFile with path '{}'", violation.getFilePath());
        }
    }

    private void saveIssue(InputFile inputFile, int line, String externalRuleKey, String message) {
        RuleKey rule = RuleKey.of(PerlCriticRulesDefinition.getRepositoryKey(), externalRuleKey);

        if(this.context.activeRules().find(rule) == null) {
            log.info("Ignoring unknown or deactivated issue of type {}", rule);
            return;
        }

        log.debug("Saving an issue of type {} on file {}", rule, inputFile);

        NewIssue issue = this.context.newIssue().forRule(rule);
        NewIssueLocation location = issue.newLocation()
                .message(message)
                .on(inputFile);

        if(line > 0) {
            location.at(inputFile.selectLine(line));
        }
    
        issue.at(location);
        issue.save();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
