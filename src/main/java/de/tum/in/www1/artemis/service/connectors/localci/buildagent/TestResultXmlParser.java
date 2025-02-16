package de.tum.in.www1.artemis.service.connectors.localci.buildagent;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

import de.tum.in.www1.artemis.service.connectors.localci.dto.BuildResult;

class TestResultXmlParser {

    private static final XmlMapper mapper = new XmlMapper();

    /**
     * Parses the test result file and extracts failed and successful tests.
     *
     * @param testResultFileString The content of the test result file as a String.
     * @param failedTests          A list of failed tests. This list will be populated by the method.
     * @param successfulTests      A list of successful tests. This list will be populated by the method.
     * @throws IOException If an I/O error occurs while reading the test result file.
     */
    static void processTestResultFile(String testResultFileString, List<BuildResult.LocalCITestJobDTO> failedTests, List<BuildResult.LocalCITestJobDTO> successfulTests)
            throws IOException {
        TestSuite testSuite = mapper.readValue(testResultFileString, TestSuite.class);

        if (testSuite.testCases() != null) {
            processTestSuite(testSuite, failedTests, successfulTests);
        }
        else {
            TestSuites suites = mapper.readValue(testResultFileString, TestSuites.class);
            if (suites.testsuites() == null) {
                return;
            }

            for (TestSuite suite : suites.testsuites()) {
                processTestSuite(suite, failedTests, successfulTests);
            }
        }
    }

    private static void processTestSuite(TestSuite testSuite, List<BuildResult.LocalCITestJobDTO> failedTests, List<BuildResult.LocalCITestJobDTO> successfulTests) {
        for (TestCase testCase : testSuite.testCases()) {
            Failure failure = testCase.extractFailure();
            if (failure != null) {
                failedTests.add(new BuildResult.LocalCITestJobDTO(testCase.name(), List.of(failure.extractMessage())));
            }
            else {
                successfulTests.add(new BuildResult.LocalCITestJobDTO(testCase.name(), List.of()));
            }
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record TestSuites(@JacksonXmlElementWrapper(useWrapping = false) @JacksonXmlProperty(localName = "testsuite") List<TestSuite> testsuites) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record TestSuite(@JacksonXmlElementWrapper(useWrapping = false) @JacksonXmlProperty(localName = "testcase") List<TestCase> testCases) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record TestCase(@JacksonXmlProperty(isAttribute = true, localName = "name") String name, @JacksonXmlProperty(localName = "failure") Failure failure,
            @JacksonXmlProperty(localName = "error") Failure error) {

        private Failure extractFailure() {
            return failure != null ? failure : error;
        }
    }

    // Due to issues with Jackson this currently cannot be a record.
    // See https://github.com/FasterXML/jackson-module-kotlin/issues/138#issuecomment-1062725140
    @JsonIgnoreProperties(ignoreUnknown = true)
    static final class Failure {

        private String message;

        private String detailedMessage;

        private String extractMessage() {
            return message != null ? message : detailedMessage;
        }

        @JacksonXmlProperty(isAttribute = true, localName = "message")
        public void setMessage(String message) {
            this.message = message;
        }

        @JacksonXmlText
        public void setDetailedMessage(String detailedMessage) {
            this.detailedMessage = detailedMessage;
        }
    }
}
