package com.epages.sonar.perl;

import java.util.Arrays;
import java.util.List;

import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.api.SonarPlugin;

import com.epages.sonar.perl.rules.PerlLintProfile;
import com.epages.sonar.perl.rules.PerlLintRulesDefinition;

@Properties({ //
        @Property( //
        key = PerlPlugin.FILE_SUFFIXES_KEY, //
        name = "File Suffixes", //
        description = "Comma-separated list of suffixes for files to analyze.", //
        defaultValue = PerlPlugin.DEFAULT_FILE_SUFFIXES) //
})
public class PerlPlugin extends SonarPlugin {

    public static final String FILE_SUFFIXES_KEY = "com.epages.sonar.perl.suffixes";

    public static final String DEFAULT_FILE_SUFFIXES = "pm";

    @SuppressWarnings("rawtypes")
    @Override
    public List getExtensions() {
        return Arrays.asList(
                PerlLanguage.class,
                PerlLintRulesDefinition.class, 
                PerlLintProfile.class
        );
    }

}
