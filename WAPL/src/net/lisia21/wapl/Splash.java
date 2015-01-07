package net.lisia21.wapl;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class Splash extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		initialize();
	}

	private void initialize() {
		Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				finish();
				overridePendingTransition(android.R.anim.slide_in_left,
						android.R.anim.slide_out_right);
			}
		};

		handler.sendEmptyMessageDelayed(0, 2500);
	}
}
