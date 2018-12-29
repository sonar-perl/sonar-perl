package com.github.sonarperl.parser;

import com.github.sonarperl.PerlConfiguration;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.impl.Parser;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class PerlParserTest {

    private final Parser<Grammar> parser = PerlParser.create(new PerlConfiguration(StandardCharsets.UTF_8));

    @Test
    public void test() {
        Collection<File> files = listFiles();
        for (File file : files) {
            parser.parse(file);
        }
    }

    private static Collection<File> listFiles() {
        File dir = new File("src/test/resources/parser/");
        return FileUtils.listFiles(dir, new String[]{"pl"}, true);
    }

}
