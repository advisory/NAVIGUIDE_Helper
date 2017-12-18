package eab.naviguide.common.helper.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class UtilHelper {
	public static String runBashCommands(String[] command) {
		String line = null;
		try {
			Process runCommand = Runtime.getRuntime().exec(command);
			runCommand.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(runCommand.getInputStream()));
			line = reader.readLine();
			System.out.println("Done running Bash command!");
			return line;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return line;
	}

}
