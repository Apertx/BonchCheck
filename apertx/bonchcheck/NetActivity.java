package apertx.bonchcheck;

import android.app.*;
import android.os.*;
import java.io.*;
import java.net.*;
import android.widget.*;
import android.view.View.*;
import android.view.*;
import android.text.*;

public class NetActivity extends Activity {
	boolean user_ok;
	boolean pass_ok;

	@Override protected void onCreate(Bundle b0) {
		super.onCreate(b0);
		final StringBuilder sb = new StringBuilder();
		new Thread(new Runnable() {
				@Override public void run() {
					try {
						HttpURLConnection http = (HttpURLConnection)new URL("https://wifi.itut.ru:8003/index.php?zone=cpzone").openConnection();
						http.setRequestMethod("POST");
						http.setDoOutput(true);
						String post = "redirurl=&auth_user=kuznecov.ik&auth_pass=Bonch910";
						OutputStream os = http.getOutputStream();
						os.write(post.getBytes());
						os.close();
						http.connect();
						InputStream is = http.getInputStream();
						BufferedReader br = new BufferedReader(new InputStreamReader(is));
						String str;
						while ((str = br.readLine()) != null)
							sb.append(str);
						br.close();
						is.close();
						http.disconnect();
					} catch (IOException e) {}
					runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(NetActivity.this, "Добро пожаловать в БончНет, самую андеграунд сеть района", 0).show();
							}
						});
				}
			}).start();
	}
}
