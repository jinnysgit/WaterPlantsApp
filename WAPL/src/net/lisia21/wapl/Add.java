package net.lisia21.wapl;

import java.net.URI;
import java.util.ArrayList;

import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Add extends Activity {

	URI url;

	Button add;
	EditText getSerialNumber;

	String phoneNumber, serialNumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add);

		Intent getInt = getIntent();
		phoneNumber = getInt.getStringExtra("phoneNumber");

		add = (Button) findViewById(R.id.add);
		add.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				
				getSerialNumber = (EditText) findViewById(R.id.serialNumber);
				serialNumber = getSerialNumber.getText().toString();
				
				Toast.makeText(getApplicationContext(), serialNumber+" is enrolled",
						Toast.LENGTH_SHORT).show();

				try {
					url = new URI("http://203.247.166.59/home");

					new Thread() {
						public void run() {
							try {

								HttpClient httpclient = new DefaultHttpClient();
								HttpPost httpPost = new HttpPost(url);
								ArrayList<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();

								nameValuePairs.add(new BasicNameValuePair(
										"phone_number", phoneNumber));
								nameValuePairs.add(new BasicNameValuePair(
										"serial_number", serialNumber));

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

			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

}
