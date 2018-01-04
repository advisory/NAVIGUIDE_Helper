package eab.naviguide.common.helper.util;

import org.apache.http.HttpHeaders;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class APIHelper {

	public static JSONObject get_Api_Payload(String url, String token) {

		try {
			BufferedReader reader = null;
			StringBuilder sb = null;

			URL url_obj = new URL(url);

			HttpURLConnection connection = (HttpURLConnection) url_obj.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Authorization", token);
			connection.addRequestProperty("Accept", "application/json");
			connection.setReadTimeout(15 * 1000);
			connection.connect();

			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			JSONObject jsonOBJ = new JSONObject(sb.toString());
			return jsonOBJ;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void post_api_payload(String URL, String payload) {

		String Token = "Token 4d462e9ec5d6e785e3296892debaffca24508237";

		try {
			PostMethod postMethod = new PostMethod(URL);
			postMethod.addRequestHeader(HttpHeaders.ACCEPT, "application/json");
			postMethod.addRequestHeader(HttpHeaders.AUTHORIZATION, Token);
			StringRequestEntity requestEntity = new StringRequestEntity("[" + payload + "]", "application/json",
					"UTF-8");
			postMethod.setRequestEntity(requestEntity);
			// int statuscode = client.executeMethod(postMethod);
			// System.out.println(statuscode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
