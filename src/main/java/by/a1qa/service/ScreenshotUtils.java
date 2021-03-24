package by.a1qa.service;

import aquality.selenium.browser.AqualityServices;
import aquality.selenium.core.logging.Logger;
import by.a1qa.entity.ImageType;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ScreenshotUtils {
    private static final String screenPathFormat = "%s/%s%s";

    public static String makeScreenshot(String saveFolderPath) {
        String screenshotPath = null;
        try {
            screenshotPath = String.format(screenPathFormat, saveFolderPath,
                    RandomStringUtils.randomAlphanumeric(10), ImageType.PNG.getType());
            Files.write(Paths.get(screenshotPath), AqualityServices.getBrowser().getScreenshot());
        } catch (IOException e) {
            AqualityServices.getLogger().warn("Can't create file");
        }
        return screenshotPath;
    }

    public static byte[] getDecodedImage(String path){
        return Base64.decodeBase64(path.getBytes());
    }



}
