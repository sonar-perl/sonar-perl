package com.github.sonarperl.it;

import com.sonar.orchestrator.build.SonarScanner;

public class TestSonarScanner {

	public static SonarScanner create() {
    	return
    			SonarScanner.create()
                .setProperty("sonar.login", "admin")
                .setProperty("sonar.password", "admin")
                .setEnvironmentVariable("SONARQUBE_SCANNER_PARAMS", "{}");
	}
}
