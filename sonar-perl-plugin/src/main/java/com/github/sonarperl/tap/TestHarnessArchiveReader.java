package com.github.sonarperl.tap;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.github.sonarperl.tap.TestHarnessReport.Test;
import com.github.sonarperl.tap.TestHarnessReport.TestDetail;
import com.github.sonarperl.tap.TestHarnessReport.TestDetail.TestDetailBuilder;
import com.github.sonarperl.tap.TestHarnessReport.TestHarnessReportBuilder;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import java.io.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class TestHarnessArchiveReader {

    private static final Logger log = Loggers.get(TestHarnessArchiveReader.class);


    public Optional<TestHarnessReport> read(File file) throws IOException {

        TestHarnessReport.TestHarnessReportBuilder builder = TestHarnessReport.builder();
        try (InputStream in = openArchiveFile(file);
            ArchiveInputStream archive = new ArchiveStreamFactory("UTF-8").createArchiveInputStream(in)) {
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
        br.lines().forEach(line -> TapLineReader.invoke(detailBuilder, line));
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
