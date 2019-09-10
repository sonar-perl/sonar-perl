package com.github.sonarperl.cpd;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import com.github.sonarperl.PerlLanguage;
import com.github.sonarperl.PerlVisitorContext;
import com.github.sonarperl.TestPerlVisitorRunner;
import com.github.sonarperl.TestUtils;
import org.junit.Test;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
import org.sonar.api.batch.sensor.cpd.internal.TokensLine;
import org.sonar.api.batch.sensor.internal.SensorContextTester;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

public class PerlCpdAnalyzerTest {

    private static final String BASE_DIR = "src/test/resources/cpd";
    private SensorContextTester context = SensorContextTester.create(new File(BASE_DIR));
    private PerlCpdAnalyzer cpdAnalyzer = new PerlCpdAnalyzer(context);

    @Test
    public void code_chunks() {
        DefaultInputFile inputFile = inputFile("code_chunks.pl");
        PerlVisitorContext visitorContext = TestPerlVisitorRunner.createContext(inputFile.path().toFile());
        cpdAnalyzer.pushCpdTokens(inputFile, visitorContext);

        List<TokensLine> lines = context.cpdTokens("moduleKey:code_chunks.pl");
        assertThat(lines).isNotNull().hasSize(9);
        TokensLine line1 = lines.get(0);
        assertThat(line1.getStartLine()).isEqualTo(2);
        assertThat(line1.getEndLine()).isEqualTo(2);
        assertThat(line1.getStartUnit()).isEqualTo(1);
        assertThat(line1.getEndUnit()).isEqualTo(1);
        List<String> values = lines.stream().map(TokensLine::getValue).collect(Collectors.toList());
        assertThat(values).containsExactly(
                "00000",
                "1111L",
                "0x10000L",
                "0X1111",
                "0b1111L",
                "0o12345",
                "u\"lala\"",
                "U\"lala\"",
                "r\"lala\"",
                "R\"lala\"",
                "print",
                "a=[1,",
                "2,",
                "]",
                "deffoo():pass",
                "classbar(object):pass",
                "deffoo2(x,y,z,):",
                // \n materializes DEDENT
                "pass\n",
                "defbar(*baz):",
                // \n materializes DEDENT
                "foo(3,4,5)\n",
                "defbar2(**baz):",
                // \n materializes DEDENT
                "yield;\n",
                "items=[]",
                "(itemforiteminitems)",
                "[itemforiteminitems]",
                "ifitemisnotNone:",
                "pass\n",
                "ifitemnotinitems:",
                "pass\n");
    }

    private DefaultInputFile inputFile(String fileName) {
        File file = new File(BASE_DIR, fileName);

        DefaultInputFile inputFile = TestInputFileBuilder.create("moduleKey", file.getName())
                .setModuleBaseDir(Paths.get(BASE_DIR))
                .setCharset(UTF_8)
                .setType(InputFile.Type.MAIN)
                .setLanguage(PerlLanguage.KEY)
                .initMetadata(TestUtils.fileContent(file, StandardCharsets.UTF_8))
                .build();

        context.fileSystem().add(inputFile);

        return inputFile;
    }
}

