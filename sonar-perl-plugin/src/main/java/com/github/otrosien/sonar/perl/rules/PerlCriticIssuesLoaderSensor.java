package com.github.otrosien.sonar.perl.rules;

import static java.lang.String.format;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.config.Settings;
import org.sonar.api.issue.Issuable;
import org.sonar.api.issue.Issue;
import org.sonar.api.resources.Project;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import com.google.common.base.Strings;

public class PerlCriticIssuesLoaderSensor implements Sensor {

    static final Logger log = Loggers.get(PerlCriticIssuesLoaderSensor.class);

    protected final Settings settings;
    protected final FileSystem fileSystem;
    protected final RuleFinder ruleFinder;
    protected final ResourcePerspectives perspectives;

    /**
     * Use of IoC to get Settings, FileSystem, RuleFinder and
     * ResourcePerspectives
     */
    public PerlCriticIssuesLoaderSensor(final Settings settings, final FileSystem fileSystem,
            final RuleFinder ruleFinder, final ResourcePerspectives perspectives) {
        this.settings = settings;
        this.fileSystem = fileSystem;
        this.ruleFinder = ruleFinder;
        this.perspectives = perspectives;
    }

    @Override
    public boolean shouldExecuteOnProject(final Project project) {
        return !Strings.isNullOrEmpty(getReportPath());
    }

    protected String reportPathKey() {
        return PerlCritic.PERLCRITIC_REPORT_PATH_KEY;
    }

    protected String getReportPath() {
        String reportPath = settings.getString(reportPathKey());
        log.info("Configured report path: {}", reportPath);
        if (!Strings.isNullOrEmpty(reportPath)) {
            return reportPath;
        } else {
            return null;
        }
    }

    @Override
    public void analyse(final Project project, final SensorContext context) {
        String reportPath = getReportPath();
        File analysisResultsFile = new File(reportPath);
        if(! analysisResultsFile.exists()) {
            log.info("PerlCritic Analysis Results '{}' does not exist. Skipping...", analysisResultsFile.getPath());
        }
        try {
            parseAndSaveResults(analysisResultsFile);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to parse the provided PerlCritic report file", e);
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
            log.error("Not able to find an InputFile with path '{}'", violation.getFilePath());
        }
    }

    private boolean saveIssue(InputFile inputFile, int line, String externalRuleKey, String message) {
        RuleKey rule = RuleKey.of(PerlCriticRulesDefinition.getRepositoryKeyForLanguage(inputFile.language()),
                externalRuleKey);

        log.debug("Now saving an issue of type {}", rule);
        Issuable issuable = perspectives.as(Issuable.class, inputFile);
        boolean result = false;
        if (issuable != null) {
            log.debug("Issuable is not null: {}", issuable.toString());
            Issuable.IssueBuilder issueBuilder = issuable.newIssueBuilder().ruleKey(rule).message(message);
            if (line > 0) {
                log.debug("Line is > 0");
                issueBuilder = issueBuilder.line(line);
            }
            Issue issue = issueBuilder.build();
            log.debug("Issue == null? " + (issue == null));
            try {
                result = issuable.addIssue(issue);
                log.debug("after addIssue: result={}", result);
            } catch (org.sonar.api.utils.MessageException me) {
                log.error(format("Can't add issue on file %s at line %d.", inputFile.absolutePath(), line), me);
            }

        } else {
            log.debug("Can't find an Issuable corresponding to InputFile:" + inputFile.absolutePath());
        }
        return result;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
