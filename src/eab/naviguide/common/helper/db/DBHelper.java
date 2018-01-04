package eab.naviguide.common.helper.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.internal.BuildInfo;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.advisory.common.automation.framework.config.AutomationConfig;
import com.advisory.common.automation.framework.config.AutomationConstants;
import com.advisory.common.automation.framework.helper.DatabaseHelper;
import com.advisory.common.automation.framework.helper.GeneralHelper;
import com.advisory.common.automation.framework.helper.NavigationHelper;
import com.advisory.common.automation.framework.util.SessionUtility;

import eab.naviguide.common.helper.util.UtilHelper;

public class DBHelper {
	
	public static void connectToDB() throws IOException {
		//System.out.println("Checking DB connection :\n");
		boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
		if (isWindows) {
			boolean runCommand = false;
			String sshFilePath = (System.getProperty("user.dir") + "\\TestData\\ssh\\greendale").replace("\\", "/");

			// run the Unix "netstat" to check port 54321 is in use  
			String[] commandPortInUse = { "C:\\Program Files\\Git\\bin\\bash.exe", "-c",
					"netstat -aon | grep 127.0.0.1:54321 &" };
			/*ProcessBuilder processBuilder = new ProcessBuilder(command);
			Process p = processBuilder.start();*/
			Process p = Runtime.getRuntime().exec(commandPortInUse);

			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

			BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

			// read the output from the command
			String s = stdInput.readLine();
			if (s != null && s.contains("127.0.0.1:54321")) {
				//System.out.println("DB connection is already established :\n");
				runCommand = true;
			}

			/*while ((s = stdInput.readLine()) != null) {
				System.out.println(s);
				runCommand = true;
			}*/

			while ((s = stdError.readLine()) != null) {
				System.out.println(s);
			}
			//"ssh -N -L 54321:greendale-qa.ctwppe9uq1vi.us-east-1.rds.amazonaws.com:5432 greendale@10.111.84.135 -i C:/Ranjit/Greendale &"
			String[] commandQADBConnect = { "C:\\Program Files\\Git\\bin\\bash.exe", "-c",
					"ssh -N -L 54321:greendale-qa.ctwppe9uq1vi.us-east-1.rds.amazonaws.com:5432 greendale@10.111.84.135 -i "
							+ sshFilePath + " &" };
			if (!runCommand) {
				//System.out.println("Executing DB connection command :\n");
				p = Runtime.getRuntime().exec(commandQADBConnect);

				/*stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
				
				while ((s = stdInput.readLine()) != null) {
					System.out.println(s);
				}*/
			}
		} else {
			//Check if port 54321 is being used.
			if (UtilHelper.runBashCommands(new String[] { "bash", "-c", "lsof -i :54321" }) == null) {
				//System.out.println("Executing DB connection command :\n");
				UtilHelper.runBashCommands(new String[] { "bash", "-c",
						"ssh -N -L 54321:greendale-qa.ctwppe9uq1vi.us-east-1.rds.amazonaws.com:5432 greendale@10.111.84.135 -i "
								+ "~/.ssh/greendale &" });
				while (UtilHelper.runBashCommands(new String[] { "bash", "-c", "lsof -i :54321" }) == null)
					NavigationHelper.sleep(500);
			}
		}
	}

	public static void assertStringValues(String testCaseName, String expectedValue, String actualValue,
			long executionTime) {
		System.out.println("Expect : " + expectedValue);
		System.out.println("Actual : " + actualValue);
		if (actualValue.equals(expectedValue))
			logExecutionStatus("Pass", testCaseName, expectedValue, actualValue, executionTime);
		else
			logExecutionStatus("Fail", testCaseName, expectedValue, actualValue, executionTime);
	}

	public static void assertBooleanValues(String testCaseName, Boolean expectedBooleanValue,
			Boolean actualBooleanValue) {
		if (expectedBooleanValue == actualBooleanValue)
			logExecutionStatus("Pass", testCaseName, expectedBooleanValue.toString(), actualBooleanValue.toString(), 0);
		else
			logExecutionStatus("Fail", testCaseName, expectedBooleanValue.toString(), actualBooleanValue.toString(), 0);
	}

	public static void assertStringArrayList(String testCaseName, ArrayList<String> expectedValue, ArrayList<String> actualValue, long executionTime) {
		//System.out.println("expectedValue : " + expectedValue);
		//System.out.println("actualValue   : " + actualValue);
		if (GeneralHelper.compareArrayList(expectedValue, actualValue))
			DatabaseHelper.logExecutionStatus("Pass", testCaseName, expectedValue.toString(), actualValue.toString(), executionTime);
		else
			DatabaseHelper.logExecutionStatus("Fail", testCaseName, expectedValue.toString(), actualValue.toString(), executionTime);
	}

	public static void logExecutionStatus(String result, String testStep, String expectedValue, String actualValue,
			long responseTime) {
		StringBuilder query = new StringBuilder();
		expectedValue = expectedValue.replaceAll("'", "^");
		actualValue = actualValue.replaceAll("'", "^");
		BuildInfo b = new BuildInfo();
		Capabilities caps = ((RemoteWebDriver) NavigationHelper.getWebDriver()).getCapabilities();
		String browserName = caps.getBrowserName();
		String browserVersion = caps.getVersion();

		try {
			query.append("Insert into " + AutomationConfig.getProduct()
					+ "_PORTAL (PRODUCT_NAME,RUN_TYPE,RUN_NAME,RUN_JAVA_VERSION,RUN_MACHINE_NAME,RUN_USER_NAME,RUN_USER_DIR,RUN_OS_NAME,RUN_BROWSER_NAME,RUN_BROWSER_VERSION,RUN_JAVASCRIPT_ENABLED,RUN_APP_BUILD_NUMBER,RUN_APP_URL,RUN_SELENIUM_VERSION,"
					+ "RUN_SELENIUM_BUILD,RUN_FRAMEWORK_VERSION,RUN_RESULTS_VERSION,TEST_NAME,STEP_NAME,STEP_STATUS,STEP_PRIORITY,STEP_EXECUTION_TIME,STEP_EXPECTED_VALUE,STEP_ACTUAL_VALUE,RUN_DASHBOASR_STATUS) values ('"
					+ AutomationConfig.getProperty("automation.product") + "','webdriver','"
					+ AutomationConfig.getProperty("automation.run.name") + "','" + System.getProperty("java.version")
					+ "','" + InetAddress.getLocalHost().getHostName() + "','" + System.getProperty("user.name") + "','"
					+ System.getProperty("user.dir") + "','" + System.getProperty("os.name") + "','" + browserName
					+ "','" + browserVersion + "','','" + AutomationConfig.getProperty("automation.app.build.no")
					+ "','" + AutomationConfig.getProperty("automation.server.url") + "','" + b.getReleaseLabel()
					+ "','" + b.getBuildRevision() + "','" + GeneralHelper.getFrameworkVersion() + "','','"
					+ SessionUtility.getValue(AutomationConstants.REPORT_NAME) + "','" + testStep + "','" + result
					+ "',''	,sysdate,'" + expectedValue + "','" + actualValue + "','null')");

		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		DatabaseHelper.insertQuery(AutomationConfig.getProperty("automation.dBDriver"),
				AutomationConfig.getProperty("automation.automationdBDetails"), query.toString());
	}
}

