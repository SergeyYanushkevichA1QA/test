package by.a1qa.forms;

import aquality.selenium.browser.AqualityServices;
import aquality.selenium.elements.ElementType;
import aquality.selenium.elements.interfaces.ILink;
import aquality.selenium.elements.interfaces.ITextBox;
import aquality.selenium.forms.Form;
import by.a1qa.entity.TestStatus;
import by.a1qa.models.Test;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ProjectForm extends Form {
    private final MenuForm menuForm = new MenuForm();
    private static final String projectNameSeparator = "Home";
    private static final String dateTimeFormat = "yyyy-MM-dd HH:mm:ss.S";
    private static final String testLoc = "tr";
    private static final String testNameLoc = "td:nth-child(1)";
    private static final String testNameButtonLoc = "a";
    private static final String testMethodLoc = "td:nth-child(2)";
    private static final String testResultLoc = "td:nth-child(3)";
    private static final String testStartTimeLoc = "td:nth-child(4)";
    private static final String testEndTimeLoc = "td:nth-child(5)";

    private final ITextBox headerBox = AqualityServices.getElementFactory().getTextBox(By.xpath("//ol[@class='breadcrumb']"),
            "Header text box");
    private final ITextBox testsBox = AqualityServices.getElementFactory().getTextBox(By.xpath("//div[@class='panel panel-default']//table[@class='table']"), "Tests box");

    public ProjectForm() {
        super(By.xpath("//canvas[@class='flot-base']"), "Project page");
    }


    public MenuForm getMenuForm() {
        return menuForm;
    }



    public String getHeaderText() {
        return headerBox.getText();
    }

    public String getProjectName() {
        return StringUtils.substringAfter(getHeaderText(), projectNameSeparator).trim();
    }

    public List<Test> getTests() {
        return getTestsList(getTestsBoxes());
    }

    public Test getTest(String testName) {
        return getTest(getTestsBoxes(), testName);
    }

    private List<Test> getTestsList(List<ITextBox> testsBoxes) {
        List<Test> tests = new ArrayList<Test>(testsBoxes.size());
        for (ITextBox testBox : testsBoxes) {
            tests.add(createTest(testBox));
        }
        return tests;
    }

    private Test getTest(List<ITextBox> testsBoxes, String testName) {
        Test test = null;
        for (ITextBox testBox : testsBoxes) {
            if (getTestName(testBox).equals(testName)) {
                test = createTest(testBox);
            }
        }
        return test;
    }

    private Test createTest(ITextBox testBox) {
        Test test = new Test();
        test.setName(getTestName(testBox));
        test.setMethod(getTestMethod(testBox));
        test.setStatus(getTestResult(testBox));
        test.setStartTime(getTestStartTime(testBox));
        test.setEndTime(getTestEndTime(testBox));
        return test;
    }

    private List<ITextBox> getTestsBoxes() {
        List<ITextBox> testBoxes = testsBox.findChildElements(By.cssSelector(testLoc), ElementType.TEXTBOX);
        testBoxes.remove(0);
        testBoxes.removeIf(elem -> Objects.isNull(elem));
        return testBoxes;
    }

    private ITextBox getTestBox(List<ITextBox> testsBoxes, String testName) {
        ITextBox testBox = null;
        for (ITextBox box : testsBoxes) {
            if (getTestName(box).equals(testName)) {
                testBox = box;
            }
        }
        return testBox;
    }


    private String getTestName(ITextBox testBox) {
        return testBox.findChildElement(By.cssSelector(testNameLoc), ElementType.TEXTBOX).getText();
    }

    private String getTestMethod(ITextBox testBox) {
        return testBox.findChildElement(By.cssSelector(testMethodLoc), ElementType.TEXTBOX).getText();
    }

    private String getTestResult(ITextBox testBox) {
        String testResult = testBox.findChildElement(By.cssSelector(testResultLoc), ElementType.TEXTBOX).getText();
        return testResult.equals(TestStatus.UNFINISHED.getDescription()) ? (TestStatus.UNFINISHED.getStatusUpperCase())
                : testResult.equals(TestStatus.PASSED.getDescription()) ? (TestStatus.PASSED.getStatusUpperCase())
                : testResult.equals(TestStatus.FAILED.getDescription()) ? (TestStatus.FAILED.getStatusUpperCase())
                : testResult.equals(TestStatus.SKIPPED.getDescription())
                ? TestStatus.SKIPPED.getStatusUpperCase() : testResult.equals(TestStatus.IN_PROGRESS.getDescription())
                ? TestStatus.IN_PROGRESS.getDescription()
                : null;
    }

    private LocalDateTime getTestStartTime(ITextBox testBox) {
        return getTimeBySelector(testBox, testStartTimeLoc);
    }

    private LocalDateTime getTestEndTime(ITextBox testBox) {
        return getTimeBySelector(testBox, testEndTimeLoc);
    }

    private LocalDateTime getTimeBySelector(ITextBox testBox, String locator) {
        LocalDateTime dateTime = null;
        String time = testBox.findChildElement(By.cssSelector(locator), ElementType.TEXTBOX).getText();
        if (Objects.nonNull(time) && !time.isBlank()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormat);
            dateTime = LocalDateTime.parse(time, formatter);
        }
        return dateTime;
    }


    public void clickTestNameButton(String testName) {
        getTestBox(getTestsBoxes(), testName).findChildElement(By.cssSelector(testNameLoc), ElementType.TEXTBOX)
                .findChildElement(By.cssSelector(testNameButtonLoc), ElementType.BUTTON).click();
    }

    public boolean waitTestForDisplayed(String testName) {
        try {
            AqualityServices.getConditionalWait()
                    .waitForTrue(() -> Objects.nonNull(getTestBox(getTestsBoxes(), testName)));
        } catch (TimeoutException | java.util.concurrent.TimeoutException e) {
            e.printStackTrace();
        }
        return getTestBox(getTestsBoxes(), testName).state().waitForDisplayed();
    }
}
