package com.github.otrosien.sonar.perl.tap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.github.otrosien.sonar.perl.tap.TestHarnessReport.Test;
import com.github.otrosien.sonar.perl.tap.TestHarnessReport.TestDetail;
import com.github.otrosien.sonar.perl.tap.TestHarnessReport.TestDetail.TestDetailBuilder;
import com.github.otrosien.sonar.perl.tap.TestHarnessReport.TestHarnessReportBuilder;

public class TestHarnessArchiveReader {

    private static final Logger log = Loggers.get(TestHarnessArchiveReader.class);

    private Pattern tapNumberOfTests = Pattern.compile("^1\\.\\.(\\d+).*");

    public Optional<TestHarnessReport> read(File file) throws IOException {

        TestHarnessReport.TestHarnessReportBuilder builder = TestHarnessReport.builder();
        try (FileInputStream s = new FileInputStream(file)) {
            TarArchiveInputStream tarInput = new TarArchiveInputStream(new GzipCompressorInputStream(s));
            TarArchiveEntry currentEntry = tarInput.getNextTarEntry();
            while (currentEntry != null) {
                if ("meta.yml".equals(currentEntry.getName())) {
                    log.info("Reading file entry {} from archive.", currentEntry.getName());
                    readMetaYaml(builder, tarInput);
                } else {
                    readTap(builder, tarInput);
                }
                currentEntry = tarInput.getNextTarEntry();
            }
            return Optional.of(builder.build());
        } catch (NumberFormatException e) {
            log.error("Unable to parse report.", e);
        }
        return Optional.empty();
    }

    private void readTap(TestHarnessReportBuilder builder, TarArchiveInputStream tarInput) {
        BufferedReader br =  new BufferedReader(new InputStreamReader(tarInput));
        TestDetailBuilder detailBuilder = TestDetail.builder();
        detailBuilder.filePath(tarInput.getCurrentEntry().getName());
        br.lines().forEach(line -> {
            if (line.startsWith("ok ")) {
                detailBuilder.ok();
            } else if (line.startsWith("not ok ")) {
                detailBuilder.failed();
            } else if (line.startsWith("1..")) {
                Matcher m = tapNumberOfTests.matcher(line);
                if (m.matches()) {
                    detailBuilder.total(Integer.valueOf(m.group(1)));
                }
            }
        });
        TestDetail detail = detailBuilder.build();
        if(detail.getNumberOfTests() > 0) {
            builder.addTestDetail(detail);
        } else {
            log.info("Did not recognize TAP or test skipped completely: " + detail.getFilePath());
        }
    }

    @SuppressWarnings("unchecked")
    private void readMetaYaml(TestHarnessReport.TestHarnessReportBuilder builder, TarArchiveInputStream tarInput)
            throws YamlException {
        BufferedReader br =  new BufferedReader(new InputStreamReader(tarInput));
        YamlReader reader = new YamlReader(br);
        Map<String, Object> object = (Map<String, Object>) reader.read(Map.class);
        builder.startTime(getFromMap(object, "start_time"));
        builder.endTime(getFromMap(object, "stop_time"));
        for (Map<String, Object> fileAttr : (List<Map<String, Object>>) object.get("file_attributes")) {
            builder.addTest(new Test((String) fileAttr.get("description"),
                    new BigDecimal((String) fileAttr.get("start_time")),
                    new BigDecimal((String) fileAttr.get("end_time"))));
        }
    }

    private BigDecimal getFromMap(Map<String, Object> map, String key) {
        Object value = map.get(key);
        Objects.requireNonNull(value, String.format("YAML property missing: %s", key));
        return new BigDecimal(String.valueOf(value));
    }
}
