package apertx.bonchcheck;
import android.app.*;
import android.os.*;
import java.io.*;
import java.net.*;
import android.widget.*;

public class ProfileActivity extends Activity {
	StringBuilder sb;
	String[] stats;

	@Override
	protected void onCreate(Bundle b0) {
		super.onCreate(b0);
		final ListView list = new ListView(this);
		setContentView(list);
		new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						HttpURLConnection http = (HttpURLConnection)new URL("https://lk.sut.ru/project/cabinet/forms/wifi.php").openConnection();
						http.setRequestProperty("Cookie", getSharedPreferences("data", MODE_PRIVATE).getString("miden", ""));
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
					} catch (IOException e) {}
					String str = sb.toString();
					stats = str.split("<th>");
					list.post(new Runnable() {
							@Override
							public void run() {
								list.setAdapter(new ArrayAdapter<String>(ProfileActivity.this, android.R.layout.simple_list_item_1, stats));
							}
						});
				}
			}).start();
	}
}
