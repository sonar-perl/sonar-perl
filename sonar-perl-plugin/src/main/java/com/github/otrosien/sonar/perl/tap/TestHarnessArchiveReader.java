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

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import com.esotericsoftware.yamlbeans.YamlReader;
import com.github.otrosien.sonar.perl.tap.TestHarnessReport.Test;

public class TestHarnessArchiveReader {

    private static final Logger log = Loggers.get(TestHarnessArchiveReader.class);

    @SuppressWarnings("unchecked")
    public Optional<TestHarnessReport> read(File file) throws IOException {

        try (FileInputStream s = new FileInputStream(file)) {
            TarArchiveInputStream tarInput = new TarArchiveInputStream(new GzipCompressorInputStream(s));
            TarArchiveEntry currentEntry = tarInput.getNextTarEntry();
            TestHarnessReport report = null;
            while (currentEntry != null) {
                if ("meta.yml".equals(currentEntry.getName())) {
                    TestHarnessReport.TestHarnessReportBuilder builder = TestHarnessReport.builder();
                    log.info("Reading file entry {} from archive.", currentEntry.getName());
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
                    report = builder.build();
                    break;
                }
                currentEntry = tarInput.getNextTarEntry();
            }
            return Optional.ofNullable(report);
        } catch (NumberFormatException e) {
            log.error("Unable to parse report.", e);
        }
        return Optional.empty();
    }

    private BigDecimal getFromMap(Map<String, Object> map, String key) {
        Object value = map.get(key);
        Objects.requireNonNull(value, String.format("YAML property missing: %s", key));
        return new BigDecimal(String.valueOf(value));
    }
}
