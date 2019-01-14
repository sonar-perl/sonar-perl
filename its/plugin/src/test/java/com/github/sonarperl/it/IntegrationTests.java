package com.github.sonarperl.it;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Suite;

import com.sonar.orchestrator.Orchestrator;
import com.sonar.orchestrator.OrchestratorBuilder;
import com.sonar.orchestrator.locator.FileLocation;

@RunWith(Suite.class)
@Suite.SuiteClasses({ 
    ProjectMetricsIntegrationTest.class,
    TestMetricsIntegrationJUnit1Test.class,
    TestMetricsIntegrationJUnit2Test.class,
    TestMetricsIntegrationTest.class,
    PerlCriticIntegrationTest.class
})
public class IntegrationTests {

    private static Collection<Orchestrator> ORCHESTRATORS = new ArrayList<>();

    @ClassRule
    public static RuleChain RESOURCES = RuleChain.emptyRuleChain();

    static {
        try {
            for (Orchestrator orchestrator : new Orchestrator[]{
                orchestratorBuilderFor("5.6").build(),
                orchestratorBuilderFor("6.7").build(),
                orchestratorBuilderFor("7.0").build(),
                orchestratorBuilderFor("7.5").build()}
            ) {
                register(orchestrator);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    private static void register(Orchestrator orchestrator) {
        ORCHESTRATORS.add(orchestrator);
        RESOURCES = RESOURCES.around(orchestrator);
    }

    @Parameters
    public static final Collection<Orchestrator> orchestrators() {
        return ORCHESTRATORS;
    }

    private static OrchestratorBuilder orchestratorBuilderFor(String version) {
        FileLocation sonarPluginJar = FileLocation.byWildcardMavenFilename(new File("../../sonar-perl-plugin/build/libs"),
                "sonar-perl-plugin-*-all.jar");
        OrchestratorBuilder orchestratorBuilder = Orchestrator.builderEnv()
                .setSonarVersion(version)
                .addPlugin(sonarPluginJar);
        return orchestratorBuilder;
    }

}
