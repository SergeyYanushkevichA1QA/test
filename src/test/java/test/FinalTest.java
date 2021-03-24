package test;

import aquality.selenium.browser.AqualityServices;
import aquality.selenium.browser.Browser;
import aquality.selenium.core.utilities.ISettingsFile;
import aquality.selenium.core.utilities.JsonSettingsFile;
import by.a1qa.database.ConnectionManager;
import by.a1qa.database.DataManager;
import by.a1qa.entity.ImageType;
import by.a1qa.entity.TestsResponse;
import by.a1qa.forms.AuthorizationForm;
import by.a1qa.forms.ProjectForm;
import by.a1qa.forms.ProjectsForm;
import by.a1qa.forms.TestPage;
import by.a1qa.service.*;
import by.a1qa.testRail.TestRailManager;
import org.apache.commons.httpclient.HttpStatus;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


import java.io.IOException;

import java.util.List;


public class FinalTest {
    private static final AuthorizationForm authForm = new AuthorizationForm();
    private static final ProjectsForm projectsForm = new ProjectsForm();
    private static final ProjectForm projectForm = new ProjectForm();
    private final Browser browser = AqualityServices.getBrowser();
    private static ISettingsFile environment = new JsonSettingsFile("settings.json");
    private static final String url = environment.getValue("/url").toString();

    @BeforeClass
    public void setup() {
        browser.maximize();
        browser.goTo(AuthorizationUtils.authorization(url, TestData.getWebUsername(), TestData.getWebPassword()));
        browser.waitForPageToLoad();
        Assert.assertTrue(projectsForm.atPage(), "Projects page didn't open");
    }


    @Test
    public void FinalTest() throws IOException {
        Integer variant = 1;
        String token = APIUtils.tokenPost(variant.toString());
        Assert.assertNotNull(token, "Checking response token");
        CookieUtils.addCookie(TestData.getCookie(token));
        browser.refresh();
        Assert.assertEquals(variant.toString(), StringUtils.getNumberVersion(projectsForm.getVersion()), "Checking number of version");
        String project = "Nexage";
        String id = StringUtils.getProjectId(projectsForm.getIdOfProject(project));
        projectsForm.clickProjectButton(project);
        TestsResponse testsResponse = APIUtils.postTests(id);
        AqualityServices.getLogger().info("Checking status code");
        Assert.assertEquals(testsResponse.getStatusCode(), HttpStatus.SC_OK);
        AqualityServices.getLogger().info("Status code is " + testsResponse.getStatusCode());
        AqualityServices.getLogger().info("Checking json");
        Assert.assertTrue(testsResponse.isJSON());
        List<by.a1qa.models.Test> pageTestsAPI = testsResponse.getObject();
        int numberOfProjects = 20;
        List<by.a1qa.models.Test> sortedTestsAPIList = ListUtils.getSortedProjectList(pageTestsAPI, numberOfProjects);
        List<by.a1qa.models.Test> pageTests = projectForm.getTests();
        Assert.assertEquals(sortedTestsAPIList, pageTests, "Tests received from the page isn't equal to tests received from a api");
        projectForm.getMenuForm().clickHomeButton();
        Assert.assertTrue(projectsForm.state().waitForDisplayed(), "Failed to return the projects page");
        projectsForm.clickAddButton();
        String newProject = "MyProject";
        Assert.assertTrue(projectsForm.waitNewProjectFormForDisplayed(), "Add project form isn't displayed");
        projectsForm.getNewProjectForm().setProjectName(newProject);
        projectsForm.getNewProjectForm().clickSaveProject();
        Assert.assertEquals(projectsForm.getNewProjectForm().getAlertText(), "Project " + newProject + " saved");
        browser.executeScript("closePopUp()");
        Assert.assertTrue(projectsForm.waitNewProjectFormForNotDisplayed(), "Add project form is displayed");
        browser.refresh();
        Assert.assertTrue(projectsForm.isProjectDisplayed(newProject), "Saved project isn't displayed in projects list");
        String newProjectId = StringUtils.getProjectId(projectsForm.getIdOfProject(newProject));
        projectsForm.clickProjectButton(newProject);
        String testName = "newTest";  String testMethod = "Test"; String testEnv = "localhost";
        DataManager.setTestProject(Integer.parseInt(newProjectId), testName, testMethod, testEnv);
        int testId = DataManager.getTestId(Integer.parseInt(newProjectId), testName);
        String logs = FileUtils.getLogContent();
        DataManager.setLogTest(logs, testId);
        String screenshotPath = ScreenshotUtils.makeScreenshot(environment.getValue("/pathForScreenFold").toString());
        DataManager.setAttachmentTest(screenshotPath, ImageType.IMAGEPNG.getType(), testId);
        ConnectionManager.closeConnection();
        projectForm.waitTestForDisplayed(testName);
        by.a1qa.models.Test newTest = projectForm.getTest(testName);
        Assert.assertEquals(newTest.getName(), testName, "Test name isn't correct");
        Assert.assertEquals(newTest.getMethod(), testMethod, "Test method isn't correct");
        projectForm.clickTestNameButton(testName);
        TestPage testPage = new TestPage();
        Assert.assertTrue(testPage.state().waitForDisplayed(), "Test page isn't displayed");
        testPage.getTest();
        Assert.assertEquals(testPage.getTestName(), testName, "Test name isn't correct");
        Assert.assertEquals(testPage.getMethodName(), testMethod, "Test method isn't correct");
        Assert.assertEquals(testPage.getEnvironment(), testEnv, "Test env isn't correct");
        /*Assert.assertEquals(testPage.getDecodedImageContent(), FileUtils.getContentAsArray(screenshotPath),
              "Actual image isn't equal to expected (uploaded) image");*/
    }

    @AfterClass
    public void tearDown() {
        browser.quit();
    }
}
