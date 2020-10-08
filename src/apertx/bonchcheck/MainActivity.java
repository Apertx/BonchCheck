package apertx.bonchcheck;

import android.app.*;
import android.os.*;
import java.net.*;
import java.io.*;
import android.widget.*;
import android.text.*;
import android.view.*;
import android.content.*;
import android.view.View.*;

public class MainActivity extends Activity {
	final int MENU_PROFILE = 17;
	final int MENU_LOGIN = 18;
	final int MENU_BONCHNET = 19;
	final int MENU_PRIVACY = 20;
	final int MENU_LICENSE = 21;
	final int MENU_EXIT = 22;

	SharedPreferences prefs;
	String sid;
	String user;
	String pass;
	String sb;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prefs = getSharedPreferences("data", MODE_PRIVATE);
		user = prefs.getString("user", null);
		pass = prefs.getString("pass", null);
		if (user == null || pass == null)
			startActivityForResult(new Intent(this, LoginActivity.class), 20);
		final Button butt = new Button(this);
		butt.setBackgroundColor(getResources().getColor(R.color.bonch));
		butt.setText(R.string.check);
		butt.setPadding(16, 64, 16, 64);
		butt.setOnClickListener(new OnClickListener() {
				@Override public void onClick(View p0) {
					butt.setEnabled(false);
					new Thread(new Runnable() {
							@Override public void run() {
								sid = BonchAPI.login(user, pass);
								sb = BonchAPI.getResponse(sid, Endpoint.rasp);
								int indexFOpen = sb.indexOf("open_zan(raspisanie");
								int indexOpen = sb.indexOf("open_zan");
								if (indexOpen != indexFOpen) {
									String rasp = sb.substring(indexOpen + 9, sb.indexOf(',', indexOpen));
									String week = sb.substring(sb.indexOf(',', indexOpen) + 2, sb.indexOf(')', indexOpen));
									sb = BonchAPI.postResponse(sid, Endpoint.rasp, new StringBuilder().append("open=1&rasp=").append(rasp).append("&week=").append(week).toString());
								} else
									sb = getString(R.string.no_check);
								runOnUiThread(new Runnable() {
										@Override public void run() {
											Toast.makeText(MainActivity.this, sb, 0).show();
											butt.setEnabled(true);
										}
									});
							}
						}).start();
				}
			});
		LinearLayout ll = new LinearLayout(this);
		ll.setGravity(Gravity.CENTER);
		ll.addView(butt);
		setContentView(ll);
	}

	@Override public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_PROFILE, 0, R.string.profile);
		menu.add(0, MENU_LOGIN, 0, R.string.login_pc);
		//menu.add(0, MENU_BONCHNET, 0, "Войти в БончНет (BonchNet)");
		menu.add(0, MENU_PRIVACY, 0, R.string.conf);
		menu.add(0, MENU_LICENSE, 0, R.string.license);
		menu.add(0, MENU_EXIT, 0, R.string.exit);
		return super.onCreateOptionsMenu(menu);
	}

	@Override public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
			case MENU_PROFILE:
				startActivity(new Intent(this, ProfileActivity.class));
				break;
			case MENU_LOGIN:
				startActivity(new Intent(this, LoginActivity.class));
				break;
			case MENU_BONCHNET:
				item.setEnabled(false);
				break;
			case MENU_PRIVACY:
				new AlertDialog.Builder(this).setTitle(R.string.conf).setMessage(R.string.conf_text).setPositiveButton("Согласен", null).show();
				break;
			case MENU_LICENSE:
				new AlertDialog.Builder(this).setTitle(R.string.license).setMessage(R.string.license_text).setPositiveButton(":3", null).show();
				break;
			case MENU_EXIT:
				finish();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK)
			switch (requestCode) {
				case 20:
					user = data.getStringExtra("user");
					pass = data.getStringExtra("pass");
					prefs.edit().putString("user", user).putString("pass", pass).putString("wifi", data.getStringExtra("wifi")).commit();
					break;
			} else finish();
	}
}
