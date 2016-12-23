package com.github.otrosien.sonar.perl.tap;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
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
        try (InputStream in = openArchiveFile(file)) {
            ArchiveInputStream archive = new ArchiveStreamFactory("UTF-8").createArchiveInputStream(in);
            ArchiveEntry currentEntry = archive.getNextEntry();
            while (currentEntry != null) {
                if ("meta.yml".equals(currentEntry.getName())) {
                    log.info("Reading file entry {} from archive.", currentEntry.getName());
                    readMetaYaml(builder, archive);
                } else {
                    readTap(builder, archive, currentEntry);
                }
                currentEntry = archive.getNextEntry();
            }
            return Optional.of(builder.build());
        } catch (ArchiveException e) {
            log.error("Unable to read archive.", e);
        } catch (CompressorException e) {
            log.error("Unable to decompress archive.", e);
        } catch (NumberFormatException e) {
            log.error("Unable to parse report.", e);
        }
        return Optional.empty();
    }

    private InputStream openArchiveFile(File file) throws CompressorException, FileNotFoundException {
        BufferedInputStream s = new BufferedInputStream(new FileInputStream(file));
        if (file.getName().matches(".*(gz|bz2|zip|xz|lzma)$")) {
            return new BufferedInputStream(new CompressorStreamFactory().createCompressorInputStream(s));
        } else {
            return s;
        }
    }

    private void readTap(TestHarnessReportBuilder builder, ArchiveInputStream archive, ArchiveEntry entry) {
        BufferedReader br = new BufferedReader(new InputStreamReader(archive));
        TestDetailBuilder detailBuilder = TestDetail.builder();
        detailBuilder.filePath(entry.getName());
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
        if (detail.getNumberOfTests() > 0) {
            builder.addTestDetail(detail);
        } else {
            log.info("Did not recognize TAP or tests skipped completely: " + detail.getFilePath());
        }
    }

    @SuppressWarnings("unchecked")
    private void readMetaYaml(TestHarnessReport.TestHarnessReportBuilder builder, ArchiveInputStream archive) throws YamlException {
        BufferedReader br = new BufferedReader(new InputStreamReader(archive));
        YamlReader reader = new YamlReader(br);
        Map<String, Object> object = (Map<String, Object>) reader.read(Map.class);
        builder.startTime(getFromMap(object, "start_time"));
        builder.endTime(getFromMap(object, "stop_time"));
        for (Map<String, Object> fileAttr : (List<Map<String, Object>>) object.get("file_attributes")) {
            builder.addTest(
                    new Test((String) fileAttr.get("description"), new BigDecimal((String) fileAttr.get("start_time")),
                            new BigDecimal((String) fileAttr.get("end_time"))));
        }
    }

    private BigDecimal getFromMap(Map<String, Object> map, String key) {
        Object value = map.get(key);
        Objects.requireNonNull(value, String.format("YAML property missing: %s", key));
        return new BigDecimal(String.valueOf(value));
    }
}
