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
	String miden;
	String users;
	String parole;
	StringBuilder sb;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prefs = getSharedPreferences("data", MODE_PRIVATE);
		miden = prefs.getString("miden", "null");
		users = prefs.getString("users", "null");
		parole = prefs.getString("parole", "null");
		if (!prefs.contains("miden") || !prefs.contains("users") || !prefs.contains("parole"))
			startActivityForResult(new Intent(this, LoginActivity.class), 20);
		final Button butt = new Button(this);
		butt.setBackgroundColor(0xffffb834);
		butt.setText("Отметиться на паре");
		butt.setOnClickListener(new OnClickListener() {
				@Override public void onClick(View p0) {
					if (butt.isEnabled()) {
						butt.setEnabled(false);
						new Thread(new Runnable() {
								@Override public void run() {
									try {
										HttpURLConnection http = (HttpURLConnection)new URL("https://lk.sut.ru/lib/autentificationok.php").openConnection();
										http.setRequestMethod("POST");
										http.setDoOutput(true);
										http.setRequestProperty("Cookie", miden);
										String post = new StringBuilder().append("users=").append(users).append("&parole=").append(parole).toString();
										OutputStream os = http.getOutputStream();
										os.write(post.getBytes());
										os.close();
										http.connect();
										http.getInputStream().read();
										http.disconnect();

										http = (HttpURLConnection)new URL("https://lk.sut.ru/project/cabinet/forms/raspisanie.php").openConnection();
										http.setRequestMethod("POST");
										http.setDoOutput(true);
										http.setRequestProperty("Cookie", miden);
										post = new StringBuilder().append("open=1&rasp=").append("").append("&week=").append("").toString();
										os = http.getOutputStream();
										//os.write(post.getBytes());
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
									} catch (IOException e) {}
									runOnUiThread(new Runnable() {
											@Override
											public void run() {
												Toast.makeText(MainActivity.this, sb.toString(), 1).show();
												butt.setEnabled(true);
											}
										});
								}
							}).start();
					} else
						Toast.makeText(MainActivity.this, "Хватит нажимать", 0).show();
				}
			});
			LinearLayout ll = new LinearLayout(this);
			ll.setGravity(Gravity.CENTER);
			ll.addView(butt);
			setContentView(ll);
	}

	@Override public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_PROFILE, 0, "Мой профиль");
		menu.add(0, MENU_LOGIN, 0, "Залогиниться");
		menu.add(0, MENU_BONCHNET, 0, "Войти в БончНет (BonchNet)");
		menu.add(0, MENU_PRIVACY, 0, "Политика конфиденциальности");
		menu.add(0, MENU_LICENSE, 0, "Лицензия");
		menu.add(0, MENU_EXIT, 0, "Выйти");
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
										Toast.makeText(MainActivity.this, "Добро пожаловать в БончНет, самую андеграунд сеть района", 0).show();
										item.setEnabled(false);
									}
								});
						}
					}).start();
				break;
			case MENU_PRIVACY:
				new AlertDialog.Builder(this).setTitle("Политика конфиденциальности").setMessage("Твои личные данный в безопасности, потому что всем пофиг на них, как и на тебя").setPositiveButton("Согласен", null).show();
				break;
			case MENU_LICENSE:
				new AlertDialog.Builder(this).setTitle("Лицензия").setMessage("DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE\nVersion 2, December 2004\n\nCopyright (C) 2004 Sam Hocevar <sam@hocevar.net>\n\nEveryone is permitted to copy and distribute verbatim or modified\ncopies of this license document, and changing it is allowed as long\nas the name is changed.\n\nDO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE\nTERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION\n\n0. You just DO WHAT THE FUCK YOU WANT TO.").setPositiveButton(":3", null).show();
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
					miden = data.getStringExtra("miden");
					users = data.getStringExtra("users");
					parole = data.getStringExtra("parole");
					prefs.edit().putString("miden", miden).putString("users", users).putString("parole", parole).putString("wifi", data.getStringExtra("wifi")).commit();
					break;
			} else finish();
	}
}
