package com.github.sonarperl;



import com.github.sonarperl.parser.PerlParser;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.impl.Parser;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class TestPerlVisitorRunner {

    private TestPerlVisitorRunner() {
    }

    public static void scanFile(File file, PerlVisitor... visitors) {
        PerlVisitorContext context = createContext(file);
        for (PerlVisitor visitor : visitors) {
            visitor.scanFile(context);
        }
    }

    public static PerlVisitorContext createContext(File file) {
        Parser<Grammar> parser = PerlParser.create(new PerlConfiguration(StandardCharsets.UTF_8));
        TestPerlFile perlFile = new TestPerlFile(file);
        AstNode rootTree = parser.parse(perlFile.content());
        return new PerlVisitorContext(rootTree, perlFile);
    }

    private static class TestPerlFile implements PerlFile {

        private final File file;

        public TestPerlFile(File file) {
            this.file = file;
        }

        @Override
        public String content() {
            try {
                return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new IllegalStateException("Cannot read " + file, e);
            }
        }

        @Override
        public String fileName() {
            return file.getName();
        }

    }

}

