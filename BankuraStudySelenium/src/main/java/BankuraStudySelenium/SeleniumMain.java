package BankuraStudySelenium;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

/**
 * Class For Study Selenium.
 * @author bankura
 *
 */
public class SeleniumMain {

    //private static WebDriver mydriver = null;

    public static String IMAGE_SAVE_PATH = "C://develop/data/image/";

    /**
     * Main method.
     *
     * [References]
     *  https://www.deep-rain.com/programming/java/927
     *  https://qiita.com/11295/items/0ab572452e9af7bac255
     *  https://www.seleniumqref.com/api/java/time_set/Java_implicitlyWait.html
     *  https://www.it-mure.jp.net/ja/java/selenium%E3%81%A7%E3%83%87%E3%83%95%E3%82%A9%E3%83%AB%E3%83%88%E3%81%AE%E3%83%80%E3%82%A6%E3%83%B3%E3%83%AD%E3%83%BC%E3%83%89%E3%83%87%E3%82%A3%E3%83%AC%E3%82%AF%E3%83%88%E3%83%AA%E3%82%92%E8%A8%AD%E5%AE%9A%E3%81%99%E3%82%8B%E6%96%B9%E6%B3%95chrome-capabilities%EF%BC%9F/822761644/
     *
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("START");

        // ドライバへのパスを記述
        //System.setProperty("webdriver.chrome.driver", "selenium/driver/chrome/chromedriver.exe");
        //WebDriver driver = new ChromeDriver();

        WebDriver driver = null;

        try {
            driver = getRemoteWebdriver("selenium/driver/chrome/chromedriver.exe");

            // 要素が見つからない場合最大で10秒間待つよう指定
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

            // googleにページ遷移
            driver.get("http://www.google.co.jp");

            System.out.println(driver.getCurrentUrl());
            System.out.println(driver.getTitle());

            WebElement elm = driver.findElement(By.name("q"));
            elm.sendKeys("天気");

            driver.findElement(By.name("btnK")).submit();
            System.out.println(driver.findElement(By.id("result-stats")).getText());

            long ypos = getWindowYPosition(driver);
            System.out.println("ウィンドウ縦位置： " + ypos);

            scrollEndPage(driver);
            System.out.println("ウィンドウ縦位置： " + getWindowYPosition(driver));

            scrollStartPage(driver);
            System.out.println("ウィンドウ縦位置： " + getWindowYPosition(driver));

            /*
            driver.findElement(By.className("class-name"));         // クラス名で検索
            driver.findElement(By.cssSelector(".class-name"));      // CSSセレクタで検索
            driver.findElement(By.id("idName"));                    // IDで検索
            driver.findElement(By.name("password"));                // nameで検索
            driver.findElement(By.linkText("Join US"));             // リンクの文字列で検索（完全一致）
            driver.findElement(By.partialLinkText("Join US"));      // リンクの文字列で検索（部分一致）
            */

            long cnt = 1;
            while (true) {
                File file = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                System.out.println(file.getAbsolutePath());
                file.renameTo(new File(IMAGE_SAVE_PATH + "test" + cnt + ".png"));
                scrollNextPageBySpace(driver);
                Thread.sleep(100);

                if (ypos == getWindowYPosition(driver))  break;

                ypos =  getWindowYPosition(driver);
                System.out.println("ウィンドウ縦位置： " + ypos);
                cnt = cnt + 1;
            }
            //driver.wait(5000);
            Thread.sleep(5000);


        } catch (Exception e) {
            System.out.println("ERROR");
            e.printStackTrace();

        } finally {
            if (driver != null) {
                driver.close();
                driver.quit();
            }
            System.out.println("END");
        }
    }

    private static DesiredCapabilities getChromeDesiredCapabilities(boolean isHeadless) {
        ChromeOptions options = new ChromeOptions();

        options.addArguments("--start-maximized");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--enable-panels");
        //options.addArguments("user-agent=Type user agent here");                // headlessでのCAPTCHA回避に必須
        options.addArguments("--disable-blink-features=AutomationControlled");  // headlessじゃなければこれだけでもいけた

        HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
        chromePrefs.put("profile.default_content_settings.popups", 0);
        chromePrefs.put("download.default_directory", "selenium/");
        chromePrefs.put("download.directory_upgrade", true);
        chromePrefs.put("download.extensions_to_open", "");
        chromePrefs.put("download.prompt_for_download", false);
        options.setExperimentalOption("prefs", chromePrefs);

        options.setHeadless(isHeadless);

        DesiredCapabilities cap = DesiredCapabilities.chrome();
        cap.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
        cap.setCapability(ChromeOptions.CAPABILITY, options);

        return cap;
    }

    private static ChromeDriverService getDriverService(String driverExePath) {
        ChromeDriverService service = new ChromeDriverService.Builder()
                .usingDriverExecutable(new File(driverExePath))
                .usingAnyFreePort()
                .build();
        return service;
    }

    private static ChromeDriverService startDriverService(String driverExePath) throws IOException {
        ChromeDriverService service = getDriverService(driverExePath);
        service.start();
        return service;
    }

    private static WebDriver getRemoteWebdriver(String driverExePath) throws IOException {
        return getRemoteWebdriver(driverExePath, false);
    }
    private static WebDriver getRemoteWebdriver(String driverExePath, boolean isHeadless) throws IOException {
        WebDriver driver = new RemoteWebDriver(startDriverService(driverExePath).getUrl(), getChromeDesiredCapabilities(isHeadless));
        return driver;
    }

    private static JavascriptExecutor getJavascriptExecutor(WebDriver driver) {
        return (JavascriptExecutor) driver;
    }
    private static Object executeScript(WebDriver driver, String scriptsTxt) {
        return getJavascriptExecutor(driver).executeScript(scriptsTxt);
    }
    private static long getWindowYPosition(WebDriver driver) {
        return (long) executeScript(driver, "return window.pageYOffset;");
    }

    private static void scrollEndPage(WebDriver driver) {
        executeScript(driver, "window.scrollTo(0, document.body.scrollHeight);");
    }
    private static void scrollStartPage(WebDriver driver) {
        executeScript(driver, "window.scrollTo(0, 0);");
    }

    private static void scrollNextPageBySpace(WebDriver driver) {
        driver.findElement(By.tagName("body")).sendKeys(Keys.chord(Keys.SPACE));
    }
}
