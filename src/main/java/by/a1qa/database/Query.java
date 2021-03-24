package by.a1qa.database;

import java.sql.Blob;
import java.util.List;

public class Query {
    private static int session_id = 1;
    private static int is_exception = 1;

    public static String addTest(int projectId, String testName, String testMethod, String testEnv) {
        return "insert into test (test.name,test.method_name,test.env,test.project_id, test.session_id)" +
                "values ('"+ testName +"','"+ testMethod +"','"+testEnv+"',"+ projectId + "," + session_id +");";
    }

    public static String getTestId(int projectId, String testName) {
        return "select test.id from test  where test.project_id=" + projectId + " AND test.name='" + testName +"';";
    }

    public static String addLog(String logContent, int testId) {
        return "insert into log(log.content, log.is_exception, log.test_id) value('"+logContent+"'," + is_exception + "," + testId +");";
    }

    public static String addAttachment(String decodedImage, String type, int testId) {
        return "insert into attachment(attachment.content, attachment.content_type, attachment.test_id)"
                +"values ('"+decodedImage+"', '"+type+"', "+testId+")";
    }

}
