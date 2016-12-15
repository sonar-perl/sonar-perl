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
    List<TestDetail> testDetails;

    // found neither test summary nor test details.
    public boolean isEmpty() {
        return tests.isEmpty() && testDetails.isEmpty();
    }

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

    @FieldDefaults(makeFinal=true, level=AccessLevel.PRIVATE)
    @AllArgsConstructor
    @Getter
    public static class TestDetail {
        @NonNull
        String filePath;
        int numberOfTests;
        int passed;
        int failed;

        public int getSkipped() {
            return numberOfTests - passed - failed;
        }

        public static class TestDetailBuilder {
            String filePath;
            int numberOfTests;
            int passed;
            int failed;

            public TestDetailBuilder ok() {
                passed++;
                return this;
            }
            public TestDetailBuilder failed() {
                failed++;
                return this;
            }
            public TestDetailBuilder total(int numberOfTests) {
                this.numberOfTests = numberOfTests;
                return this;
            }
            public TestDetailBuilder filePath(String filePath) {
                this.filePath = filePath;
                return this;
            }
            public TestDetail build() {
                return new TestDetail(filePath, numberOfTests, passed, failed);
            }
        }
        public static TestDetailBuilder builder() {
            return new TestDetailBuilder();
        }
    }
    @FieldDefaults(level=AccessLevel.PRIVATE)
    public static class TestHarnessReportBuilder {
        BigDecimal startTime;
        BigDecimal endTime;
        List<Test> tests = new ArrayList<>();
        List<TestDetail> testDetails = new ArrayList<>();

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

        public TestHarnessReportBuilder addTestDetail(final TestDetail testDetail) {
            this.testDetails.add(testDetail);
            return this;
        }

        public TestHarnessReport build() {
            return new TestHarnessReport(startTime, endTime,
                    unmodifiableList(this.tests),
                    unmodifiableList(this.testDetails));
        }

    }

    public static TestHarnessReportBuilder builder() {
        return new TestHarnessReportBuilder();
    }

}
