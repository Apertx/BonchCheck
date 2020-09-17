package app.template;

import android.app.*;
import android.os.*;
import java.net.*;
import java.io.*;
import android.widget.*;
import android.text.*;

public class MainActivity extends Activity {
	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ScrollView sv = new ScrollView(this);
		final TextView text = new TextView(this);
		sv.addView(text);
		final StringBuilder sb = new StringBuilder();
		setContentView(sv);
		new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						HttpURLConnection http = (HttpURLConnection)new URL("https://lk.sut.ru/cabinet/").openConnection();
						http.connect();
						http.getInputStream().read();
						http.disconnect();
						String miden = http.getHeaderField(8).substring(0, http.getHeaderField(8).indexOf(';'));

						http = (HttpURLConnection)new URL("https://lk.sut.ru/cabinet/lib/autentification.php").openConnection();
						http.setRequestMethod("POST");
						http.setDoOutput(true);
						http.setRequestProperty("Cookie", miden);
						String post = "users=XXX&parole=XXX";
						OutputStream os = http.getOutputStream();
						os.write(post.getBytes());
						os.close();
						http.connect();
						http.getInputStream().read();
						http.disconnect();

						http = (HttpURLConnection)new URL("https://lk.sut.ru/project/cabinet/forms/raspisanie.php").openConnection();
						http.setRequestProperty("Cookie", miden);
						http.connect();
						InputStream is = http.getInputStream();
						BufferedReader br = new BufferedReader(new InputStreamReader(is));
						String str;
						while ((str = br.readLine()) != null)
							sb.append(str);
						br.close();
						is.close();
						http.disconnect();

						http = (HttpURLConnection)new URL("https://lk.sut.ru/cabinet/?login=no").openConnection();
						http.setRequestProperty("Cookie", miden);
						http.connect();
						http.getInputStream().read();
						http.disconnect();
					} catch (IOException e) {}
					text.post(new Runnable() {
							@Override
							public void run() {
								text.setText(sb.toString());
							}
						});
				}
			}).start();
	}
}
