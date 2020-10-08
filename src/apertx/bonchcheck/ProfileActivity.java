package apertx.bonchcheck;
import android.app.*;
import android.os.*;
import java.io.*;
import java.net.*;
import android.widget.*;
import android.content.*;

public class ProfileActivity extends Activity {
	String sid;
	String user;
	String pass;
	String sb;
	String[] stats;

	@Override
	protected void onCreate(Bundle b0) {
		super.onCreate(b0);
		SharedPreferences prefs = getSharedPreferences("data", MODE_PRIVATE);
		user = prefs.getString("user", null);
		pass = prefs.getString("pass", null);
		final ListView list = new ListView(this);
		setContentView(list);
		new Thread(new Runnable() {
				@Override
				public void run() {
					sid = BonchAPI.login(user, pass);
					sb = BonchAPI.getResponse(sid, Endpoint.profile);
					stats = sb.substring(sb.indexOf("<th>") + 4).split("<th>");
					for (int i = 0; i < stats.length; i += 1)
						stats[i] = stats[i].replace("</th><td>", " ").substring(0, stats[i].indexOf("</td") - 8);
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
