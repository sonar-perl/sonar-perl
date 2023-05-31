package com.github.sonarperl.tap;

import com.github.sonarperl.tap.TestHarnessReport.Test;
import com.github.sonarperl.tap.TestHarnessReport.TestDetail;
import com.github.sonarperl.tap.TestHarnessReport.TestDetail.TestDetailBuilder;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

/*
A difficulty with JUnit reports is that we need to recover test file path
info.

There are mainly two Perl modules that supports generating JUnit XML,
one is TAP::Harness::JUnit, the other is TAP::Formatter::JUnit.

For TAP::Formatter::JUnit, we would support if it dumps to individual
files under a directory via setting PERL_TEST_HARNESS_DUMP_TAP.  The junit
xml files are named by appending .junit.xml to the t/xxx.t form.

For TAP::Harness::JUnit, we would assume its namemangle is "none" or
"perl".  We cannot support "hudson" mode as we can't safely recover test
file path in this case.  Actually even "perl" mode is not 100% safe
although it should work well in most cases. 
*/

public class TestHarnessJUnitReader {

    private static final Logger log = Loggers.get(TestHarnessJUnitReader.class);

    public Optional<TestHarnessReport> read(File file) throws IOException {
        Path path = Paths.get(file.getPath());
        TestHarnessReport.TestHarnessReportBuilder builder = TestHarnessReport.builder();

        if (path.toFile().isDirectory()) { // for TAP::Formatter::JUnit
            log.info("Looking for JUnit reports under path {}", path.toString());

            try (Stream<Path> stream = Files.walk(path)) {
                return stream
                    .filter(p -> p.toFile().exists())
                    .filter(p -> p.toString().endsWith(".junit.xml"))
                    .map(p -> readReport(builder, p, path))
                    .reduce(Boolean::logicalOr)
                    .map(b -> builder.build());
            }
        } else {      // for TAP::Harness::JUnit
            return Optional.of(path)
                .filter(p -> readReport(builder, p, p))
                .map(p -> builder.build());
        }
    }

    private boolean readReport( TestHarnessReport.TestHarnessReportBuilder builder,
                            Path path, 
                            Path reportRootPath ) {
        String reportPath = path.toString();
        log.info("Reading JUnit report from file {}", reportPath);

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setAttribute(XMLConstants.FEATURE_SECURE_PROCESSING, Boolean.TRUE);

        try {
            InputSource is = new InputSource(
                                    Files.newBufferedReader(path, StandardCharsets.UTF_8));
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(is);

            NodeList testsuites = doc.getElementsByTagName("testsuite");

            for (int i = 0; i < testsuites.getLength(); i++) {
                Element testsuite = (Element)(testsuites.item(i));
                String tsname = testsuite.getAttribute("name");
                String filepath;
                if (reportPath.endsWith(".t.junit.xml") && !reportRootPath.equals(path)) {
                    // assume TAP::Formatter::JUnit with PERL_TEST_HARNESS_DUMP_TAP
                    filepath = reportRootPath.relativize(path)
                                    .toString()
                                    .replaceAll("\\.junit\\.xml$", "");
                } else {
                    // assume TAP::Harness::JUnit
                    filepath = tsname;
                    if (!tsname.endsWith(".t")) {
                        // assume namemangle is "perl"
                        filepath = tsname.replace('.', '/').replaceAll("_t$", ".t");
                    }
                }

                builder.addTest(
                    new Test(String.valueOf(tsname),
                             BigDecimal.ZERO,
                             new BigDecimal((String) testsuite.getAttribute("time"))));

                TestDetailBuilder detailBuilder = TestDetail.builder();
                detailBuilder.filePath(filepath);

                NodeList outnodes = testsuite.getElementsByTagName("system-out");
                if (outnodes.getLength() > 0) {
                    String systemout = outnodes.item(0).getTextContent();
                    BufferedReader br = new BufferedReader(new StringReader(systemout));
                    br.lines().forEach(line -> TapLineReader.invoke(detailBuilder, line));

                    TestDetail detail = detailBuilder.build();
                    if (detail.getNumberOfTests() > 0) {
                        builder.addTestDetail(detail);
                        continue;
                    }
                }

                log.info("Did not recognize TAP or tests skipped completely: {}", filepath);
            }
            return true;

        } catch (IOException e) {
            log.error("IO exception: ", e);
        } catch (SAXException e) {
            log.error("Failed parsing JUnit report: ", e);
        } catch (ParserConfigurationException e) {
            log.error("Parser configuration exception: ", e);
        }
        return false;
    }
}
