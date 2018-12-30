package com.github.sonarperl;

import java.io.IOException;
import org.sonar.api.batch.fs.InputFile;

public abstract class SonarQubePerlFile implements PerlFile {

    private final InputFile inputFile;

    private SonarQubePerlFile(InputFile inputFile) {
        this.inputFile = inputFile;
    }

    public static PerlFile create(InputFile inputFile) {
        return new Sq62File(inputFile);
    }

    @Override
    public String fileName() {
        return inputFile.filename();
    }

    public InputFile inputFile() {
        return inputFile;
    }

    private static class Sq62File extends SonarQubePerlFile {

        public Sq62File(InputFile inputFile) {
            super(inputFile);
        }

        @Override
        public String content() {
            try {
                return inputFile().contents();
            } catch (IOException e) {
                throw new IllegalStateException("Could not read content of input file " + inputFile(), e);
            }
        }

    }

}

