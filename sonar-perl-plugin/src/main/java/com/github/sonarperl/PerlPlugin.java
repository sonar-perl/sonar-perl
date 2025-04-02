package com.github.sonarperl;

import com.github.sonarperl.critic.PerlCriticIssuesLoaderSensor;
import com.github.sonarperl.critic.PerlCriticProperties;
import com.github.sonarperl.critic.PerlCriticRulesDefinition;
import com.github.sonarperl.critic.SonarWayProfile;
import com.github.sonarperl.tap.TestHarnessArchiveProperties;
import com.github.sonarperl.tap.TestHarnessLoaderSensor;
import org.sonar.api.Plugin;
import org.sonar.api.PropertyType;
import org.sonar.api.config.PropertyDefinition;

public class PerlPlugin implements Plugin {
    public static final String CATEGORY_NAME = "Community Perl";

    public void define(Context context) {
        context.addExtensions(
                PerlLanguage.class,
                PerlSquidSensor.class,
                PerlCriticRulesDefinition.class, 
                SonarWayProfile.class,
                GlobalSensor.class,
                PerlCriticIssuesLoaderSensor.class,
                TestHarnessLoaderSensor.class
        );

        context.addExtension(PropertyDefinition.builder(PerlLanguage.FILE_SUFFIXES_KEY)
                .name("File Suffixes")
                .description("Comma-separated list of suffixes for files to analyze.")
                .defaultValue(String.join(",", PerlLanguage.FILE_SUFFIXES))
                .category(CATEGORY_NAME)
                .subCategory("General")
                .type(PropertyType.STRING)
                .multiValues(true)
                .build());
        context.addExtension(PropertyDefinition.builder(PerlCriticProperties.PERLCRITIC_REPORT_PATH_KEY)
                .name("PerlCritic Report Location")
                .description("Location of perlcritic report file. Can be generated using this command-line: perlcritic --quiet --verbose \"%f~|~%s~|~%l~|~%c~|~%m~|~%e~|~%p~||~%n\"")
                .defaultValue(PerlCriticProperties.PERLCRITIC_REPORT_PATH_DEFAULT)
                .category(CATEGORY_NAME)
                .subCategory("Perl::Critic")
                .type(PropertyType.STRING)
                .build());
        context.addExtension(PropertyDefinition.builder(TestHarnessArchiveProperties.HARNESS_ARCHIVE_PATH_KEY)
                .name("Test::Harness Archive Location")
                .description("Location of Test::Harness::Archive report file. Can be generated using this command-line: prove -t -a testReport.tgz")
                .defaultValue(TestHarnessArchiveProperties.HARNESS_ARCHIVE_PATH_DEFAULT)
                .category(CATEGORY_NAME)
                .subCategory("Test::Harness")
                .type(PropertyType.STRING)
                .build());
        
    }

}
