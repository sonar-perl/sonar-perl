package com.github.otrosien.sonar.perl;

import java.util.Arrays;

import org.sonar.api.Plugin;
import org.sonar.api.Properties;
import org.sonar.api.Property;

import com.github.otrosien.sonar.perl.colorizer.PerlCodeColorizer;
import com.github.otrosien.sonar.perl.rules.PerlCritic;
import com.github.otrosien.sonar.perl.rules.PerlCriticIssuesLoaderSensor;
import com.github.otrosien.sonar.perl.rules.PerlCriticProfile;
import com.github.otrosien.sonar.perl.rules.PerlCriticRulesDefinition;

@Properties({ //
    @Property( //
    key = PerlPlugin.FILE_SUFFIXES_KEY, //
    name = "File Suffixes", //
    description = "Comma-separated list of suffixes for files to analyze.", //
    defaultValue = PerlPlugin.DEFAULT_FILE_SUFFIXES), //
    @Property( //
    key = PerlCritic.PERLCRITIC_REPORT_PATH_KEY, //
    name = "Perlcritic Report Location", //
    description = "Location of perlcritic report file. Needs to be generated using these command-line flags: --quiet --verbose \"%f~|~%s~|~%l~|~%c~|~%m~|~%e~|~%p~||~%n\"", //
    defaultValue = PerlCritic.PERLCRITIC_REPORT_PATH_DEFAULT ) //
})
public class PerlPlugin implements Plugin {

    public static final String FILE_SUFFIXES_KEY = "com.github.otrosien.sonar.perl.suffixes";

    public static final String DEFAULT_FILE_SUFFIXES = "pm,pl,t";

    @Override
    public void define(Context context) {
        context.addExtensions(Arrays.asList(
                PerlLanguage.class,
                PerlCriticRulesDefinition.class, 
                PerlCriticProfile.class,
                PerlCodeColorizer.class,
                GlobalSensor.class,
                PerlCriticIssuesLoaderSensor.class
        ));
    }

}
