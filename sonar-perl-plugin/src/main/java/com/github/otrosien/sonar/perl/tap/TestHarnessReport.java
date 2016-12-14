package com.github.otrosien.sonar.perl.tap;

import static java.util.Collections.unmodifiableList;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@FieldDefaults(makeFinal=true, level=AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class TestHarnessReport {

    BigDecimal startTime;
    BigDecimal endTime;
    List<Test> tests;

    @FieldDefaults(makeFinal=true, level=AccessLevel.PRIVATE)
    @AllArgsConstructor
    @Getter
    public static class Test {
        @NonNull
        String filePath;
        @NonNull
        BigDecimal startTime;
        @NonNull
        BigDecimal endTime;

        public long getDuration() {
            return endTime.subtract(startTime).multiply(new BigDecimal("1000")).longValue();
        }
    }

    @FieldDefaults(level=AccessLevel.PRIVATE)
    public static class TestHarnessReportBuilder {
        BigDecimal startTime;
        BigDecimal endTime;
        List<Test> tests = new ArrayList<>();

        TestHarnessReportBuilder() {
        }

        public TestHarnessReportBuilder startTime(final BigDecimal startTime) {
            this.startTime = startTime;
            return this;
        }

        public TestHarnessReportBuilder endTime(final BigDecimal endTime) {
            this.endTime = endTime;
            return this;
        }

        public TestHarnessReportBuilder addTest(final Test test) {
            this.tests.add(test);
            return this;
        }

        public TestHarnessReport build() {
            java.util.List<Test> tests = unmodifiableList(this.tests);
            return new TestHarnessReport(startTime, endTime, tests);
        }

    }

    public static TestHarnessReportBuilder builder() {
        return new TestHarnessReportBuilder();
    }

}
