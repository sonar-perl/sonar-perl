package com.github.otrosien.sonar.perl.rules;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;

import com.github.otrosien.sonar.perl.rules.PerlCriticAnalysisResultsParser;
import com.github.otrosien.sonar.perl.rules.PerlCriticViolation;

public class PerlCriticAnalysisResultsParserTest {

    @Test
    public void should_parse_sample_file() throws IOException, URISyntaxException {
        PerlCriticAnalysisResultsParser parser = new PerlCriticAnalysisResultsParser();
        List<PerlCriticViolation> parseResult = parser.parse(sampleFile());

        assertThat(parseResult, notNullValue());
        assertThat(parseResult.size(), is(2));

        PerlCriticViolation v = parseResult.get(0);
        assertThat(v.getFilePath(), is("lib/Sample/Project.pm"));
        assertThat(v.getLine(), is(3));
        assertThat(v.getType(), is("TestingAndDebugging::RequireUseStrict"));
        assertThat(v.getDescription(), is("Code before strictures are enabled"));
    }

    private File sampleFile() throws URISyntaxException {
        return Paths.get(ClassLoader.getSystemResource("sample_perlcritic_report.txt").toURI()).toFile();
    }

}
