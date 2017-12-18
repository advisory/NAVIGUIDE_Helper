package eab.naviguide.common.helper.base;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import com.advisory.common.automation.framework.config.AutomationConfig;
import com.advisory.common.automation.framework.config.AutomationConstants;
import com.advisory.common.automation.framework.helper.NavigationHelper;
import com.advisory.common.automation.framework.util.SessionUtility;

import eab.naviguide.common.helper.webdriver.WebDriverManagerFactory;

public class TestAutomation {
	/** The test class start time. */
	private long testClassStartTime;
	
	/** The test class end time. */
	private long testClassEndTime;
	
	/** The current test class. */
	private String currentTestClass;
	
	/**
	 * Test setup.
	 */
	@BeforeClass
	public void testSetup() {
		tearUp();
	}	
		
	/**
	 * Tear up.
	 */
	public void tearUp() {
		testClassStartTime = System.currentTimeMillis();
		currentTestClass = this.getClass().getName();
		Thread.currentThread().setName(currentTestClass);
		SessionUtility.setValue(AutomationConstants.WEB_DRIVER_KEY+currentTestClass, 
				new WebDriverManagerFactory().createWebDriver(AutomationConfig.getWebDriverType()));
		SessionUtility.setValue(AutomationConstants.RUN_NAME,AutomationConfig.getRunName());
		SessionUtility.setValue(AutomationConstants.MEMBER,AutomationConfig.getMemberName());
		SessionUtility.setValue(AutomationConstants.USER_ID,AutomationConfig.getUserId());
		SessionUtility.setValue(AutomationConstants.ENVIORNMENT,"Current Enviornment");
		SessionUtility.setValue(AutomationConstants.REPORT_NAME,currentTestClass);
		SessionUtility.setValue(AutomationConstants.EXPECTED_RESULT,"N/A");
		SessionUtility.setValue(AutomationConstants.ACTUAL_RESULT,"N/A");
		System.out.println("Starting Test Class "+currentTestClass);
		String implicit_wait = AutomationConfig.getProperty("automation.ie.implicit.wait");
		if (implicit_wait==null)
			implicit_wait = "0";
		if (AutomationConfig.getProperty("automation.web.driver").equalsIgnoreCase("Internet_Explorer")) {
			NavigationHelper.getWebDriver().manage().timeouts().implicitlyWait(implicit_wait.equals(null) ? 0 : Long.parseLong(implicit_wait), TimeUnit.SECONDS);
		}
	}

	/**
	 * Tear down.
	 */
	public void tearDown(){
		WebDriver webDriver = (WebDriver)SessionUtility.getValue(AutomationConstants.WEB_DRIVER_KEY+Thread.currentThread().getName());
		if (webDriver != null){
			try {
				webDriver.quit();
			} catch (Exception exception) {
				throw new RuntimeException("Exception occured while quiting driver",exception);
			}
		}
	testClassEndTime = System.currentTimeMillis();
	SessionUtility.setValue(AutomationConstants.RUN_NAME,"");
	SessionUtility.setValue(AutomationConstants.MEMBER,"");
	SessionUtility.setValue(AutomationConstants.USER_ID,"");
	SessionUtility.setValue(AutomationConstants.ENVIORNMENT,"");
	SessionUtility.setValue(AutomationConstants.REPORT_NAME,"");
	SessionUtility.setValue(AutomationConstants.EXPECTED_RESULT,"");
	SessionUtility.setValue(AutomationConstants.ACTUAL_RESULT,"");

	System.out.println(currentTestClass + " complete" + " test(s) run; total time in milliseconds: "
			+ Long.toString(testClassEndTime - testClassStartTime));
	}
	
	/**
	 * Test reset.
	 */
	@AfterClass
	public void testReset() {
		tearDown();
	}
}
