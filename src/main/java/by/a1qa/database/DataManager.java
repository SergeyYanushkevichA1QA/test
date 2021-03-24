package by.a1qa.database;

import aquality.selenium.browser.AqualityServices;
import by.a1qa.service.FileUtils;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;

public class DataManager {

    public static void setTestProject(int projectId, String testName, String testMethod, String testEnv) {
        try {
            Statement statement = ConnectionManager.getConnection().createStatement();
            statement.execute(Query.addTest(projectId, testName, testMethod, testEnv));
        } catch (SQLException e) {
            AqualityServices.getLogger().error(e.getMessage());
        }
    }

    public static int getTestId(int projectId, String testName) {
        try {
            Statement statement = ConnectionManager.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(Query.getTestId(projectId, testName));
            if(resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            AqualityServices.getLogger().error(e.getMessage());
        }
        return 0;
    }


    public static void setLogTest(String log, int testId) {
        try {
            Statement statement = ConnectionManager.getConnection().createStatement();
            statement.execute(Query.addLog(log, testId));
        } catch (SQLException e) {
            AqualityServices.getLogger().error(e.getMessage());
        }
    }

    public static void setAttachmentTest(String path, String type, int testId) {
        try {
            Statement statement = ConnectionManager.getConnection().createStatement();
            statement.execute(Query.addAttachment(Base64.getEncoder().encodeToString(FileUtils.getContentAsArray(path)), type, testId));
        } catch (SQLException e) {
            AqualityServices.getLogger().error(e.getMessage());
        }
    }

}
