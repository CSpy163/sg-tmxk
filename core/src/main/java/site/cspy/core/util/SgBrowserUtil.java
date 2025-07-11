package site.cspy.core.util;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class SgBrowserUtil {

    /**
     * 保存浏览器截图
     *
     * @param url        url
     * @param domId      dom id
     * @param targetFile 目标文件
     * @throws IOException io exception
     */
//    public static void saveCaptureTo(String url, String domId, File targetFile) throws IOException {
//        //        ChromeOptions options = new ChromeOptions();
//        //        options.addArguments("--headless");
//        //        WebDriver driver = new ChromeDriver(options);
//
//        FirefoxOptions options = new FirefoxOptions();
//        options.addArguments("--headless");
//        WebDriver driver = new FirefoxDriver(options);
//
//        driver.get(url);
//        File screenShot = new WebDriverWait(driver, Duration.of(10, ChronoUnit.SECONDS)).until(
//                webDriver -> {
//                    WebElement element = webDriver.findElement(By.id(domId));
//                    Dimension size = element.getSize();
//                    webDriver.manage().window().setSize(new Dimension(size.getWidth(), size.getHeight()));
//                    return element;
//                }).getScreenshotAs(OutputType.FILE);
//        Files.copy(screenShot.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
//        log.info("网页截图完成，保存到 {}", targetFile.getAbsolutePath());
//        driver.quit();
//    }

}
