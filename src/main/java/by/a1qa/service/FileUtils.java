package by.a1qa.service;

import aquality.selenium.browser.AqualityServices;
import aquality.selenium.core.utilities.ISettingsFile;
import aquality.selenium.core.utilities.JsonSettingsFile;
import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class FileUtils {
    private static ISettingsFile environment = new JsonSettingsFile("settings.json");

    public static String getLogContent() {
        String content = null;
        try {
            content = Files.readString(Paths.get(environment.getValue("/pathForLogs").toString()));
        } catch (IOException e) {
            AqualityServices.getLogger().debug("Failed to read all lines from the file", e);
        }
        return content;
    }

    public static byte[] getContentAsArray(String filePath) {
        byte[] content = null;
        try {
            content = Files.readAllBytes(Paths.get(filePath));
        } catch (IOException e) {
            AqualityServices.getLogger().debug("Failed to read all lines from the file", e);
        }
        return content;
    }
}
