package com.github.sonarperl.it;

import com.sonar.orchestrator.build.SonarScanner;
import com.sonar.orchestrator.junit4.OrchestratorRule;

public class TestSonarScanner {

	public static SonarScanner create(OrchestratorRule orchestrator) {
    	return
    			SonarScanner.create()
				.setProperty("sonar.token", orchestrator.getDefaultAdminToken())
                .setEnvironmentVariable("SONARQUBE_SCANNER_PARAMS", "{}");
	}
}
