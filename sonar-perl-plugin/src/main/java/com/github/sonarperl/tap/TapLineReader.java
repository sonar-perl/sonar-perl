package com.github.sonarperl.tap;


import com.github.sonarperl.tap.TestHarnessReport.TestDetail.TestDetailBuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TapLineReader {

    private static final Matcher tapNumberOfTests = Pattern.compile("^1\\.\\.(\\d+).*").matcher("");

    public static void invoke(TestDetailBuilder detailBuilder, String line) {
        if (line.startsWith("ok ")) {
            detailBuilder.ok();
        } else if (line.startsWith("not ok ")) {
            detailBuilder.failed();
        } else if (line.startsWith("1..")) {
            Matcher m = tapNumberOfTests.reset(line);
            if (m.matches()) {
                detailBuilder.total(Integer.valueOf(m.group(1)));
            }
        }
    }
}
