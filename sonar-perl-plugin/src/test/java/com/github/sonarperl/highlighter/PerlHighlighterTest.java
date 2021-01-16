package com.github.sonarperl.highlighter;

import com.github.sonarperl.TestPerlVisitorRunner;
import com.github.sonarperl.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
import org.sonar.api.batch.sensor.highlighting.TypeOfText;
import org.sonar.api.batch.sensor.internal.SensorContextTester;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PerlHighlighterTest {

    private SensorContextTester context;

    private File file;

    @Before
    public void scanFile() {
        String dir = "src/test/resources/com/github/sonarperl/plugins/perl";

        file = new File(dir, "/perlHighlighter.pl");
        DefaultInputFile inputFile = new TestInputFileBuilder("moduleKey", file.getName())
                .initMetadata(TestUtils.fileContent(file, StandardCharsets.UTF_8))
                .build();

        context = SensorContextTester.create(new File(dir));
        context.fileSystem().add(inputFile);

        PerlHighlighter perlHighlighter = new PerlHighlighter(context, inputFile);
        TestPerlVisitorRunner.scanFile(file, perlHighlighter);
    }


    @Test
    public void keyword() {
        // sub
        checkOnRange(7, 0, 3, TypeOfText.KEYWORD);
    }

    @Test
    public void stringLiteral() {
        // "some string"
        checkOnRange(5, 5, 13, TypeOfText.STRING);
    }

    @Test
    public void comment() {
        // # this is a comment
        checkOnRange(3, 0, 19, TypeOfText.COMMENT);
    }

    /**
     * Checks the highlighting of a range of columns. The first column of a line has index 0.
     * The range is the columns of the token.
     */
    private void checkOnRange(int line, int firstColumn, int length, TypeOfText expectedTypeOfText) {
        // check that every column of the token is highlighted (and with the expected type)
        for (int column = firstColumn; column < firstColumn + length; column++) {
            checkInternal(line, column, "", expectedTypeOfText);
        }

        // check that the column before the token is not highlighted
        if (firstColumn != 0) {
            checkInternal(line, firstColumn - 1, " (= before the token)", null);
        }

        // check that the column after the token is not highlighted
        checkInternal(line, firstColumn + length, " (= after the token)", null);
    }


    /**
     * Checks the highlighting of one column. The first column of a line has index 0.
     */
    private void check(int line, int column, TypeOfText expectedTypeOfText) {
        checkInternal(line, column, "", expectedTypeOfText);
    }

    private void checkInternal(int line, int column, String messageComplement, TypeOfText expectedTypeOfText) {
        String componentKey = "moduleKey:" + file.getName();
        List<TypeOfText> foundTypeOfTexts = context.highlightingTypeAt(componentKey, line, column);

        int expectedNumberOfTypeOfText = expectedTypeOfText == null ? 0 : 1;
        String message = "number of TypeOfTexts at line " + line + " and column " + column + messageComplement;
        assertThat(foundTypeOfTexts).as(message).hasSize(expectedNumberOfTypeOfText);
        if (expectedNumberOfTypeOfText > 0) {
            message = "found TypeOfTexts at line " + line + " and column " + column + messageComplement;
            assertThat(foundTypeOfTexts.get(0)).as(message).isEqualTo(expectedTypeOfText);
        }
    }




}
