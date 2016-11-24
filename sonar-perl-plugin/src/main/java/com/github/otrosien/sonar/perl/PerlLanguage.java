package com.github.otrosien.sonar.perl;

import java.util.ArrayList;
import java.util.List;

import org.sonar.api.config.Settings;
import org.sonar.api.resources.AbstractLanguage;

/**
 * This class defines the perl language.
 */
public final class PerlLanguage extends AbstractLanguage {

    public static final String NAME = "Perl";
    public static final String KEY = "perl";

    private final Settings settings;

    public PerlLanguage(Settings settings) {
        super(KEY, NAME);
        this.settings = settings;
    }

    @Override
    public String[] getFileSuffixes() {
        String[] suffixes = filterEmptyStrings(settings.getStringArray(PerlPlugin.FILE_SUFFIXES_KEY));
        if (suffixes.length == 0) {
            suffixes = PerlPlugin.DEFAULT_FILE_SUFFIXES.split(",\\s*");
        }
        return suffixes;
    }

    private String[] filterEmptyStrings(String[] stringArray) {
        List<String> nonEmptyStrings = new ArrayList<>();
        for (String string : stringArray) {
            if (! "".equals(string.trim())) {
                nonEmptyStrings.add(string.trim());
            }
        }
        return nonEmptyStrings.toArray(new String[nonEmptyStrings.size()]);
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
            if (pathLowerCase.endsWith("." + suffix.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

}
