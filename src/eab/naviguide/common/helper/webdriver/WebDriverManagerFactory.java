package eab.naviguide.common.helper.webdriver;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

import com.advisory.common.automation.framework.config.AutomationConstants;
import io.github.bonigarcia.wdm.WebDriverManager;

public class WebDriverManagerFactory {
	/**
	 * Creates a new WebDriver object.
	 *
	 * @param webDriverType
	 *            the web driver type
	 * @return the web driver
	 */
	public WebDriver createWebDriver(String webDriverType) {
		WebDriver webDriver = null;
		Class<? extends WebDriver> driverClass = null;

		try {
			if (AutomationConstants.INTERNET_EXPLORER.equalsIgnoreCase(webDriverType)) {
				driverClass = InternetExplorerDriver.class;
			} else if (AutomationConstants.FIREFOX.equalsIgnoreCase(webDriverType)) {
				driverClass = FirefoxDriver.class;
			} else if (AutomationConstants.CHROME.equalsIgnoreCase(webDriverType)) {
				driverClass = ChromeDriver.class;				
			} else if (AutomationConstants.SAFARI.equalsIgnoreCase(webDriverType)) {
				driverClass = SafariDriver.class;
			}
			WebDriverManager.getInstance(driverClass).setup();
			webDriver = driverClass.newInstance();
			webDriver.manage().window().maximize();
			webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		} catch (InstantiationException instantiationException) {
			throw new RuntimeException("InstantiationException in createWebDriver", instantiationException);
		} catch (IllegalAccessException illegalAccessException) {
			throw new RuntimeException("IllegalAccessException in createWebDriver", illegalAccessException);
		}
		return webDriver;
	}
}
