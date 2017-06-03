package com.github.sonarperl.it;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

import org.junit.ClassRule;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Suite;

import com.sonar.orchestrator.Orchestrator;
import com.sonar.orchestrator.OrchestratorBuilder;
import com.sonar.orchestrator.locator.FileLocation;

@RunWith(Suite.class)
@Suite.SuiteClasses({ ProjectMetricsIntegrationTest.class, TestMetricsIntegrationJUnit1Test.class,
        TestMetricsIntegrationJUnit2Test.class, TestMetricsIntegrationTest.class, })
public class IntegrationTests {

    @ClassRule
    public static Orchestrator ORCHESTRATOR_LOW;

    @ClassRule
    public static Orchestrator ORCHESTRATOR_HIGH;

    static {
        OrchestratorBuilder orchestratorBuilder1 = Orchestrator.builderEnv()
                .setSonarVersion("5.6")
                .addPlugin(FileLocation.byWildcardMavenFilename(new File("../../sonar-perl-plugin/build/libs"),
                        "sonar-perl-plugin-*.jar"));
        ORCHESTRATOR_LOW = orchestratorBuilder1.build();

        OrchestratorBuilder orchestratorBuilder2 = Orchestrator.builderEnv()
                .setSonarVersion("6.2")
                .addPlugin(FileLocation.byWildcardMavenFilename(new File("../../sonar-perl-plugin/build/libs"),
                        "sonar-perl-plugin-*.jar"));
        ORCHESTRATOR_HIGH = orchestratorBuilder2.build();

    }

    public static TestRule RESOURCES = RuleChain.emptyRuleChain()
            .around(ORCHESTRATOR_HIGH)
            .around(ORCHESTRATOR_LOW);

    @Parameters
    public static final Collection<Orchestrator> orchestrators() {
        return Arrays.asList(ORCHESTRATOR_LOW
                , ORCHESTRATOR_HIGH
                );
    }

}
