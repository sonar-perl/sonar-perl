package com.github.sonarperl.it;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.Collection;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.sonar.wsclient.SonarClient;
import org.sonar.wsclient.issue.IssueQuery;

import com.sonar.orchestrator.Orchestrator;
import com.sonar.orchestrator.build.SonarScanner;

@RunWith(Parameterized.class)
public class PerlCriticIntegrationTest {

    private static final String PROJECT_KEY = "critic";

    @ClassRule
    public static TestRule RESOURCES = IntegrationTests.RESOURCES;

    @Parameters
    public static Collection<Orchestrator> orchestrators() {
        return IntegrationTests.orchestrators();
    }

    private static final SonarScanner build;

    static {
        build = SonarScanner.create()
                .setProjectDir(new File("projects/critic"))
                .setProjectKey(PROJECT_KEY)
                .setProjectName(PROJECT_KEY)
                .setProjectVersion("1.0-SNAPSHOT")
                .setProperty("sonar.clover.reportPath", "perlcritic_report.txt")
                .setSourceDirs("lib");
    }

    private final SonarClient wsClient;

    public PerlCriticIntegrationTest(Orchestrator orchestrator) {
        orchestrator.executeBuild(build);
        wsClient = orchestrator.getServer().wsClient();
    }

    @Test
    public void parse_report() {
        assertThat(wsClient.issueClient().find(IssueQuery.create().severities("BLOCKER").rules("PerlCritic:TestingAndDebugging::RequireUseStrict")).list())
        .hasSize(1);
    }

}