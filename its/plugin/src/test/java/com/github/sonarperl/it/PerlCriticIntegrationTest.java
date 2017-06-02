package com.github.sonarperl.it;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.sonar.wsclient.SonarClient;
import org.sonar.wsclient.issue.IssueQuery;

import com.sonar.orchestrator.Orchestrator;
import com.sonar.orchestrator.build.SonarScanner;
import com.sonar.orchestrator.locator.FileLocation;

public class PerlCriticIntegrationTest {

    private static final String PROJECT_KEY = "critic";

    @ClassRule
    public static Orchestrator orchestrator = Orchestrator.builderEnv()
      .setSonarVersion("6.1")
      .addPlugin(FileLocation.byWildcardMavenFilename(new File("../../sonar-perl-plugin/build/libs"), "sonar-perl-plugin-*.jar"))
      .build();

    private static SonarClient wsClient;

    @BeforeClass
    public static void startServer() {
        SonarScanner build = SonarScanner.create()
                .setProjectDir(new File("projects/critic"))
                .setProjectKey(PROJECT_KEY)
                .setProjectName(PROJECT_KEY)
                .setProjectVersion("1.0-SNAPSHOT")
                .setProperty("sonar.clover.reportPath", "perlcritic_report.txt")
                .setSourceDirs("lib");
        orchestrator.executeBuild(build);

        wsClient = orchestrator.getServer().wsClient();
    }

    @Test
    public void parse_report() {
        assertThat(wsClient.issueClient().find(IssueQuery.create().severities("BLOCKER").rules("PerlCritic:TestingAndDebugging::RequireUseStrict")).list())
        .hasSize(1);
    }

}