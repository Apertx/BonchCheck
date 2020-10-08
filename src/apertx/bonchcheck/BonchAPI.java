package apertx.bonchcheck;

import java.io.*;
import java.net.*;

public class BonchAPI {
	static String login(String user, String pass) {
		String sid = null;
		try {
			HttpURLConnection http = (HttpURLConnection)new URL("https://lk.sut.ru/cabinet/").openConnection();
			http.connect();
			http.getInputStream().read();
			http.disconnect();
			sid = http.getHeaderField(8).substring(0, http.getHeaderField(8).indexOf(';'));
			http = (HttpURLConnection)new URL(Endpoint.auth).openConnection();
			http.setRequestMethod("POST");
			http.setDoOutput(true);
			http.setRequestProperty("Cookie", sid);
			String post = new StringBuilder().append("users=").append(user).append("&parole=").append(pass).toString();
			OutputStream os = http.getOutputStream();
			os.write(post.getBytes());
			os.close();
			http.connect();
			int result = http.getInputStream().read();
			http.disconnect();
			if (result != 49)
				sid = null;
		} catch (Exception e) {}
		return sid;
	}

	static String getResponse(String sid, String endpoint) {
		StringBuilder sb = new StringBuilder();
		try {
			HttpURLConnection http = (HttpURLConnection)new URL(endpoint).openConnection();
			http.setRequestProperty("Cookie", sid);
			http.connect();
			InputStream is = http.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "Windows-1251"));
			String str;
			while ((str = br.readLine()) != null)
				sb.append(str);
			br.close();
			is.close();
			http.disconnect();
		} catch (Exception e) {}
		return sb.toString();
	}

	static String postResponse(String sid, String endpoint, String post) {
		StringBuilder sb = new StringBuilder();
		try {
			HttpURLConnection http = (HttpURLConnection)new URL(Endpoint.rasp).openConnection();
			http.setRequestMethod("POST");
			http.setDoOutput(true);
			http.setRequestProperty("Cookie", sid);
			OutputStream os = http.getOutputStream();
			os.write(post.getBytes());
			os.close();
			http.connect();
			InputStream is = http.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "Windows-1251"));
			sb = new StringBuilder();
			String str;
			while ((str = br.readLine()) != null)
				sb.append(str);
			br.close();
			is.close();
			http.disconnect();
		} catch (Exception e) {}
		return sb.toString();
	}
}
