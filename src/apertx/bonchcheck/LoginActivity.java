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
	String sid;
	boolean user_ok;
	boolean pass_ok;
	int result;
	String sb;

	@Override protected void onCreate(Bundle b0) {
		super.onCreate(b0);
		setContentView(R.layout.login);
		((TextView)findViewById(R.id.login_text)).setText(R.string.email);
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
									sid = BonchAPI.login(auth_user.getText().toString(), auth_pass.getText().toString());
									sb = BonchAPI.getResponse(sid, Endpoint.wifi);
									if (sid != null) {
										int index_wifi = sb.indexOf("Ваш логин:") + 14;
										setResult(RESULT_OK, new Intent().putExtra("user", auth_user.getText().toString()).putExtra("pass", auth_pass.getText().toString()).putExtra("wifi", sb.substring(index_wifi, sb.indexOf('<', index_wifi))));
										finish();
									} else
										runOnUiThread(new Runnable() {
												@Override
												public void run() {
													Toast.makeText(LoginActivity.this, getString(R.string.auth_err), 0).show();
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
		menu.add(0, 11, 0, R.string.conf);
		menu.add(0, 12, 0, R.string.license);
		menu.add(0, 13, 0, R.string.exit);
		return super.onCreateOptionsMenu(menu);
	}

	@Override public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case 11:
				new AlertDialog.Builder(this).setTitle(R.string.conf).setMessage(R.string.conf_text).setPositiveButton("Согласен", null).show();
				break;
			case 12:
				new AlertDialog.Builder(this).setTitle(R.string.license).setMessage(R.string.license_text).setPositiveButton(":3", null).show();
				break;
			case 13:
				finish();
				break;
		}
		return super.onOptionsItemSelected(item);
	}
}
