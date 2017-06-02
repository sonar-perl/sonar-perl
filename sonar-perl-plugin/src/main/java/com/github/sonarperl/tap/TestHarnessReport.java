package com.github.sonarperl.tap;

import static java.util.Collections.unmodifiableList;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TestHarnessReport {

    private final BigDecimal startTime;
    private final BigDecimal endTime;
    private final List<Test> tests;
    private final List<TestDetail> testDetails;

    public TestHarnessReport(BigDecimal startTime, BigDecimal endTime, List<Test> tests, List<TestDetail> testDetails) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.tests = tests;
        this.testDetails = testDetails;
    }

    public BigDecimal getStartTime() {
        return startTime;
    }

    public BigDecimal getEndTime() {
        return endTime;
    }

    public List<Test> getTests() {
        return tests;
    }

    public List<TestDetail> getTestDetails() {
        return testDetails;
    }

    public static class Test {
        private final String filePath;
        private final BigDecimal startTime;
        private final BigDecimal endTime;

        public Test(String filePath, BigDecimal startTime, BigDecimal endTime) {
            this.filePath = Objects.requireNonNull(filePath);
            this.startTime = Objects.requireNonNull(startTime);
            this.endTime = Objects.requireNonNull(endTime);
        }

        public String getFilePath() {
            return filePath;
        }

        public BigDecimal getStartTime() {
            return startTime;
        }

        public BigDecimal getEndTime() {
            return endTime;
        }

        public long getDuration() {
            return endTime.subtract(startTime).multiply(new BigDecimal("1000")).longValue();
        }
    }

    public static class TestDetail {
        private final String filePath;
        private final int numberOfTests;
        private final int passed;
        private final int failed;

        public TestDetail(String filePath, int numberOfTests, int passed, int failed) {
            this.filePath = Objects.requireNonNull(filePath);
            this.numberOfTests = numberOfTests;
            this.passed = passed;
            this.failed = failed;
        }

        public String getFilePath() {
            return filePath;
        }

        public int getNumberOfTests() {
            return numberOfTests;
        }

        public int getPassed() {
            return passed;
        }

        public int getFailed() {
            return failed;
        }

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

    public static class TestHarnessReportBuilder {
        private BigDecimal startTime;
        private BigDecimal endTime;
        private List<Test> tests = new ArrayList<>();
        private List<TestDetail> testDetails = new ArrayList<>();

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
