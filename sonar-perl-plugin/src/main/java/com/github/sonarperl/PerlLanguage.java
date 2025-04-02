package com.github.sonarperl;

import org.apache.commons.lang3.StringUtils;
import org.sonar.api.config.Configuration;
import org.sonar.api.resources.AbstractLanguage;

import java.util.Arrays;
import java.util.List;

/**
 * This class defines the perl language.
 */
public final class PerlLanguage extends AbstractLanguage {

    public static final String NAME = "Perl";
    public static final String KEY = "perl";
    private final Configuration config;

    public static final List<String> FILE_SUFFIXES = List.of(".pl", ".pm", ".t");
    public static final String FILE_SUFFIXES_KEY = "sonar.perl.file.suffixes";

    public PerlLanguage(Configuration config) {
        super(KEY, NAME);
        this.config = config;
    }

    public String[] getFileSuffixes() {
        final List<String> providedFilesSuffixes = Arrays.stream(config.getStringArray(FILE_SUFFIXES_KEY))
                .map(String::trim)
                .filter(StringUtils::isNotBlank).toList();
        final List<String> filesSuffixes = providedFilesSuffixes.isEmpty() ? FILE_SUFFIXES : providedFilesSuffixes;
        return filesSuffixes.toArray(new String[0]);
    }

    /**
     * Allows to know if the given file name has a valid suffix.
     *
     * @param fileName
     *            String representing the file name
     * @return boolean <code>true</code> if the file name's suffix is known,
     *         <code>false</code> any other way
     */
    public boolean hasValidSuffixes(String fileName) {
        String pathLowerCase = fileName.toLowerCase();
        for (String suffix : getFileSuffixes()) {
            if (pathLowerCase.endsWith(suffix.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

}
