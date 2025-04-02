package com.github.sonarperl.it;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import com.sonar.orchestrator.Orchestrator;
import com.sonar.orchestrator.OrchestratorBuilder;
import com.sonar.orchestrator.config.Configuration;
import com.sonar.orchestrator.container.Edition;
import com.sonar.orchestrator.junit4.OrchestratorRule;
import com.sonar.orchestrator.junit4.OrchestratorRuleBuilder;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Suite;

import com.sonar.orchestrator.locator.FileLocation;

import static com.sonar.orchestrator.junit4.OrchestratorRule.builder;

@RunWith(Suite.class)
@Suite.SuiteClasses({ 
    ProjectMetricsIntegrationTest.class,
    TestMetricsIntegrationJUnit1Test.class,
    TestMetricsIntegrationJUnit2Test.class,
    TestMetricsIntegrationTest.class,
    PerlCriticIntegrationTest.class
})
public class IntegrationTests {

    private static Collection<OrchestratorRule> ORCHESTRATORS = new ArrayList<>(2);

    @ClassRule
    public static RuleChain RESOURCES = RuleChain.emptyRuleChain();

    static {
        try {
            for (OrchestratorRule orchestratorRule : new OrchestratorRule[]{
                    // LTA
                    orchestratorBuilderFor("LATEST_RELEASE[25.1]").build(),
                    // Latest Release
                    orchestratorBuilderFor("LATEST_RELEASE[25.3]").build(),
            }
            ) {
                register(orchestratorRule);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    private static void register(OrchestratorRule orchestratorRule) {
        ORCHESTRATORS.add(orchestratorRule);
        RESOURCES = RESOURCES.around(orchestratorRule);
    }

    @Parameters
    public static final Collection<OrchestratorRule> orchestrators() {
        return ORCHESTRATORS;
    }

    private static OrchestratorRuleBuilder orchestratorBuilderFor(String version) {
        FileLocation sonarPluginJar = FileLocation.byWildcardMavenFilename(new File("../../sonar-perl-plugin/build/libs"),
                "sonar-perl-plugin-*-all.jar");

        return builder(Configuration.createEnv())
                .setSonarVersion(version)
                .setEdition(Edition.COMMUNITY)
                .defaultForceAuthentication()
                .addPlugin(sonarPluginJar);

    }


}
