package com.github.sonarperl;


import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

public class TestUtils {

    public static String fileContent(File file, Charset charset) {
        try {
            return new String(Files.readAllBytes(file.toPath()), charset);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read " + file, e);
        }
    }

}
