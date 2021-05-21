/*
 * The MIT License
 *
 * Copyright (c) 2004-2010, Sun Microsystems, Inc., Tom Huybrechts, Yahoo!, Inc., Seiji Sogabe
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package hudson.tasks.junit;

import edu.hm.hafner.echarts.ChartModelConfiguration;
import edu.hm.hafner.echarts.JacksonFacade;
import edu.hm.hafner.echarts.LinesChartModel;

import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.bind.JavaScriptMethod;
import hudson.tasks.test.TestObject;
import hudson.tasks.test.TestObjectIterable;
import hudson.tasks.test.TestResultDurationChart;
import hudson.tasks.test.TestResultTrendChart;

import io.jenkins.plugins.junit.storage.TestResultImpl;

/**
 * History of {@link hudson.tasks.test.TestObject} over time.
 *
 * @since 1.320
 */
@Restricted(NoExternalUse.class)
public class History {
    private static final JacksonFacade JACKSON_FACADE = new JacksonFacade();
    private static final String EMPTY_CONFIGURATION = "{}";
    private final TestObject testObject;

    public History(final TestObject testObject) {
        this.testObject = testObject;
    }

    @SuppressWarnings("unused") // Called by jelly view
    public TestObject getTestObject() {
        return testObject;
    }

    @SuppressWarnings("unused") // Called by jelly view
    public boolean historyAvailable() {
        if (testObject instanceof hudson.tasks.junit.TestResult) {
            TestResultImpl pluggableStorage = ((hudson.tasks.junit.TestResult) testObject).getPluggableStorage();
            if (pluggableStorage != null) {
                return pluggableStorage.getCountOfBuildsWithTestResults() > 1;
            }
        }

        return testObject.getRun().getParent().getBuilds().size() > 1;
    }

    @Deprecated
    @SuppressWarnings("unused") // Called by jelly view
    public String getTestResultTrend() {
        return getTestResultTrend(EMPTY_CONFIGURATION);
    }

    @JavaScriptMethod
    @SuppressWarnings("unused") // Called by jelly view
    public String getTestResultTrend(final String configuration) {
        return JACKSON_FACADE.toJson(createTestResultTrend(ChartModelConfiguration.fromJson(configuration)));
    }

    private LinesChartModel createTestResultTrend(
            final ChartModelConfiguration chartModelConfiguration) {
        if (testObject instanceof hudson.tasks.junit.TestResult) {
            TestResultImpl pluggableStorage = ((hudson.tasks.junit.TestResult) testObject).getPluggableStorage();
            if (pluggableStorage != null) {
                return new TestResultTrendChart().create(pluggableStorage.getTrendTestResultSummary());
            }
        }

        return new TestResultTrendChart().createFromTestObject(createBuildHistory(testObject), chartModelConfiguration);
    }

    @Deprecated
    @SuppressWarnings("unused") // Called by jelly view
    public String getTestDurationTrend() {
        return getTestDurationTrend(EMPTY_CONFIGURATION);
    }

    @JavaScriptMethod
    @SuppressWarnings("unused") // Called by jelly view
    public String getTestDurationTrend(final String configuration) {
        return JACKSON_FACADE.toJson(createTestDurationResultTrend(ChartModelConfiguration.fromJson(configuration)));
    }

    private LinesChartModel createTestDurationResultTrend(
            final ChartModelConfiguration chartModelConfiguration) {
        if (testObject instanceof hudson.tasks.junit.TestResult) {
            TestResultImpl pluggableStorage = ((hudson.tasks.junit.TestResult) testObject).getPluggableStorage();
            if (pluggableStorage != null) {
                return new TestResultDurationChart().create(pluggableStorage.getTestDurationResultSummary());
            }
        }

        return new TestResultDurationChart().create(createBuildHistory(testObject), chartModelConfiguration);
    }

    private TestObjectIterable createBuildHistory(final TestObject testObject) {
        return new TestObjectIterable(testObject);
    }

    @SuppressWarnings("unused") // Called by jelly view
    public static int asInt(final String s, final int defaultValue) {
        if (s == null) return defaultValue;
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
