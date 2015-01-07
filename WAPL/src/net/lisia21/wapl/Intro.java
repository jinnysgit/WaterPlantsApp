package net.lisia21.wapl;

import java.net.URI;
import java.util.ArrayList;

import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Intro extends Activity {

	TextView number;
	Button login;
	String phoneNumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_intro);
		startActivity(new Intent(this, Splash.class));

		number = (TextView) findViewById(R.id.number);
		login = (Button) findViewById(R.id.login);

		TelephonyManager systemService = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		phoneNumber = systemService.getLine1Number();
		phoneNumber = "0"
				+ phoneNumber.substring(phoneNumber.length() - 10,
						phoneNumber.length());

		number.setText(phoneNumber);

		login.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {

				try {
					final URI tempurl = new URI(
							"http://203.247.166.59/home");

					new Thread() {
						public void run() {
							try {

								HttpClient httpclient = new DefaultHttpClient();
								HttpPost httpPost = new HttpPost(tempurl);
								ArrayList<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();

								nameValuePairs.add(new BasicNameValuePair(
										"phone_number", phoneNumber));

								httpPost.setEntity(new UrlEncodedFormEntity(
										nameValuePairs, "utf-8"));
								httpclient.execute(httpPost);

							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}.start();

				} catch (Exception e) {
					e.printStackTrace();
				}

				Intent runIntent = new Intent(getApplicationContext(),
						Main.class);
				runIntent.putExtra("phoneNumber", phoneNumber);
				startActivity(runIntent);
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

		// When a user presses the back button on his or her phone,
		// alert dialogue box will appear
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			vibe.vibrate(200);
			new AlertDialog.Builder(this)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle(" 종 료 ")
					.setMessage(" 종료 하시겠습니까? ")
					.setPositiveButton(" Yes ",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									moveTaskToBack(true);
									android.os.Process
											.killProcess(android.os.Process
													.myPid());

								}
							}).setNegativeButton(" No ", null).show();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}
}
