package com.github.sonarperl.toolkit;

import com.github.sonarperl.api.PerlKeyword;
import org.sonar.colorizer.KeywordsTokenizer;
import org.sonar.colorizer.Tokenizer;
import org.sonar.sslr.toolkit.Toolkit;

import java.util.List;

public final class PerlToolkit {
    private PerlToolkit() {}


    public static void main(String[] args) {
        Toolkit toolkit = new Toolkit("SSLR :: Perl :: Toolkit", new PerlConfigurationModel());
        toolkit.run();
    }


    public static List<Tokenizer> getPerlTokenizers() {
        return List.of(
                new KeywordsTokenizer("<span class=\"k\">", "</span>", PerlKeyword.keywordValues()));
    }

}
