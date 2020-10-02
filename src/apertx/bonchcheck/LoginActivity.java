package apertx.bonchcheck;

import android.app.*;
import android.os.*;
import android.text.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import java.io.*;
import java.net.*;
import android.content.*;

public class LoginActivity extends Activity {
	String miden;
	boolean user_ok;
	boolean pass_ok;
	int result;
	StringBuilder sb;

	@Override protected void onCreate(Bundle b0) {
		super.onCreate(b0);
		setContentView(R.layout.login);
		((TextView)findViewById(R.id.login_text)).setText("E-mail");
		final Button butt = findViewById(R.id.login_butt);
		butt.setEnabled(false);
		final EditText auth_user = findViewById(R.id.login_auth_user);
		auth_user.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		auth_user.addTextChangedListener(new TextWatcher() {
				@Override public void beforeTextChanged(CharSequence p0, int p1, int p2, int p3) {}
				@Override public void onTextChanged(CharSequence p0, int p1, int p2, int p3) {
					String str = p0.toString();
					int index_dog = str.indexOf('@');
					int index_dot = str.indexOf('.', index_dog);
					user_ok = ((index_dog > 0) && (index_dot > index_dog + 1) && (index_dot != str.length() - 1));
					butt.setEnabled(user_ok && pass_ok);
				}
				@Override public void afterTextChanged(Editable p0) {}
			});
		final EditText auth_pass = findViewById(R.id.login_auth_pass);
		auth_pass.addTextChangedListener(new TextWatcher() {
				@Override public void beforeTextChanged(CharSequence p0, int p1, int p2, int p3) {}
				@Override public void onTextChanged(CharSequence p0, int p1, int p2, int p3) {
					pass_ok = (p0.length() > 0);
					butt.setEnabled(user_ok && pass_ok);
				}
				@Override public void afterTextChanged(Editable p0) {}
			});
		butt.setOnClickListener(new OnClickListener() {
				@Override public void onClick(View p0) {
					if (butt.isEnabled()) {
						butt.setEnabled(false);
						new Thread(new Runnable() {
								@Override public void run() {
									try {
										HttpURLConnection http = (HttpURLConnection)new URL("https://lk.sut.ru/cabinet/").openConnection();
										http.connect();
										http.getInputStream().read();
										http.disconnect();
										miden = http.getHeaderField(8).substring(0, http.getHeaderField(8).indexOf(';'));

										http = (HttpURLConnection)new URL("https://lk.sut.ru/cabinet/lib/autentificationok.php").openConnection();
										http.setRequestMethod("POST");
										http.setDoOutput(true);
										http.setRequestProperty("Cookie", miden);
										String post = new StringBuilder().append("users=").append(auth_user.getText()).append("&parole=").append(auth_pass.getText()).toString();
										OutputStream os = http.getOutputStream();
										os.write(post.getBytes());
										os.close();
										http.connect();
										result = http.getInputStream().read();
										http.disconnect();

										http = (HttpURLConnection)new URL("https://lk.sut.ru/project/cabinet/forms/wifi.php").openConnection();
										http.setRequestProperty("Cookie", miden);
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
									if (result == 49) {
										String str = sb.toString();
										int index_wifi = str.indexOf("Ваш логин:") + 14;
										String g = str.substring(index_wifi);
										setResult(RESULT_OK, new Intent().putExtra("miden", miden).putExtra("users", auth_user.getText().toString()).putExtra("parole", auth_pass.getText().toString()).putExtra("wifi", str.substring(index_wifi, str.indexOf('<', index_wifi))));
										finish();
									} else
										runOnUiThread(new Runnable() {
												@Override
												public void run() {
													Toast.makeText(LoginActivity.this, "Не удалось войти", 1).show();
													butt.setEnabled(true);
												}
											});
								}
							}).start();
					}
				}
			});
	}

	@Override public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 10, 0, "Войти в БончНет (BonchNet)");
		menu.add(0, 11, 0, "Политика конфиденциальности");
		menu.add(0, 12, 0, "Лицензия");
		menu.add(0, 13, 0, "Выйти");
		return super.onCreateOptionsMenu(menu);
	}

	@Override public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case 10:
				startActivity(new Intent(this, LoginActivity.class));
				break;
			case 11:
				new AlertDialog.Builder(this).setTitle("Политика конфиденциальности").setMessage("Твои личные данный в безопасности, потому что всем пофиг на них, как и на тебя").setPositiveButton("Согласен", null).show();
				break;
			case 12:
				new AlertDialog.Builder(this).setTitle("Лицензия").setMessage("DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE\nVersion 2, December 2004\n\nCopyright (C) 2004 Sam Hocevar <sam@hocevar.net>\n\nEveryone is permitted to copy and distribute verbatim or modified\ncopies of this license document, and changing it is allowed as long\nas the name is changed.\n\nDO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE\nTERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION\n\n0. You just DO WHAT THE FUCK YOU WANT TO.").setPositiveButton(":3", null).show();
				break;
			case 13:
				finish();
				break;
		}
		return super.onOptionsItemSelected(item);
	}
}
