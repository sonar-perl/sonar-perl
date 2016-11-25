package com.github.otrosien.sonar.perl.critic;

public class PerlCritic {

    public static final String PERLCRITIC_REPORT_PATH_KEY = "sonar.perlcritic.reportPath";

    public static final String PERLCRITIC_REPORT_PATH_DEFAULT = "perlcritic_report.txt";

    private PerlCritic() {
        throw new UnsupportedOperationException("Do not instanciate.");
    }

}
