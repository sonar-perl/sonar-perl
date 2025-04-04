package com.github.sonarperl.it;

import com.sonar.orchestrator.build.SonarScanner;
import com.sonar.orchestrator.junit4.OrchestratorRule;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.io.File;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class PerlCriticIntegrationTest {

    private static final String PROJECT_KEY = "critic";

    @ClassRule
    public static TestRule RESOURCES = IntegrationTests.RESOURCES;

    @Parameters
    public static Collection<OrchestratorRule> orchestrators() {
        return IntegrationTests.orchestrators();
    }

    private SonarScanner scanner(OrchestratorRule orchestrator) {
        return TestSonarScanner.create(orchestrator)
                .setProjectDir(new File("projects/critic"))
                .setProjectKey(PROJECT_KEY)
                .setProjectName(PROJECT_KEY)
                .setProjectVersion("1.0-SNAPSHOT")
                .setProperty("sonar.clover.reportPath", "perlcritic_report.txt")
                .setSourceDirs("lib");
    }

    private final TestSonarClient wsClient;

    public PerlCriticIntegrationTest(OrchestratorRule orchestrator) {
        orchestrator.executeBuild(scanner(orchestrator));
        wsClient = new TestSonarClient(orchestrator, PROJECT_KEY);
    }

    @Test
    public void parse_report() {
        assertThat(wsClient.issueCount("BLOCKER", "PerlCritic:TestingAndDebugging::RequireUseStrict"))
        .isEqualTo(1);
    }

}