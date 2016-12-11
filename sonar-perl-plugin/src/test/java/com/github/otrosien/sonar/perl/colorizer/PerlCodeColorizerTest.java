package com.github.otrosien.sonar.perl.colorizer;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.sonar.api.internal.apachecommons.io.IOUtils;
import org.sonar.colorizer.HtmlOptions;
import org.sonar.colorizer.HtmlRenderer;
import org.sonar.colorizer.Tokenizer;

@SuppressWarnings("deprecation")
public class PerlCodeColorizerTest {

    @Test
    public void perlToHtml() throws IOException {
        Reader perl = readFile("/colorizer/Color.pm");
        Reader expected = readFile("/colorizer/expected.html");
        String html = toHtml(perl, new PerlCodeColorizer().getTokenizers());
        assertThat(html).contains(IOUtils.toString(expected));
    }

    public String toHtml(Reader code, List<Tokenizer> tokenizers) {
        return new HtmlRenderer(HtmlOptions.DEFAULT).render(code, tokenizers);
    }

    /**
     * @return Reader for specified file with EOL normalized to specified one.
     */
    private Reader readFile(String path, String eol) throws IOException {
        String s = IOUtils.readLines(getClass().getResourceAsStream(path)).
        stream().collect(Collectors.joining(eol));

        return new StringReader(s);
    }

    /**
     * @return Reader for specified file with EOL normalized to LF.
     */
    private Reader readFile(String path) throws IOException {
        return readFile(path, IOUtils.LINE_SEPARATOR_UNIX);
    }

}
