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
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Remove extends Activity {

	URI url;

	private ArrayList<String> getPlantsList;
	private ArrayAdapter<String> adapter;

	ListView plantsList;
	TextView exp;

	String serialNumber, phoneNumber;

	Intent getInt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_remove);

		getInt = getIntent();

		if (getPlantsList != null) {
			getPlantsList.clear();
		}

		plantsList = (ListView) findViewById(R.id.plantList);
		exp = (TextView) findViewById(R.id.exp);

		phoneNumber = getInt.getStringExtra("phoneNumber");

	}

	@Override
	protected void onResume() {
		super.onResume();

		getPlantsList = (ArrayList<String>) getInt
				.getSerializableExtra("plantsList");

		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, getPlantsList);

		plantsList.setAdapter(adapter);

		plantsList
				.setOnItemLongClickListener(new ListViewItemLongClickListener());

	}

	private class ListViewItemLongClickListener implements
			AdapterView.OnItemLongClickListener {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			final int selectedPos = position;
			serialNumber = getPlantsList.get(position);

			AlertDialog.Builder alertDlg = new AlertDialog.Builder(
					view.getContext());
			alertDlg.setTitle("Destroy");

			alertDlg.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

							try {
								url = new URI(
										"http://203.247.166.59/home/destroy");

								getPlantsList.remove(selectedPos);

								Toast.makeText(getApplicationContext(),
										serialNumber + " removed.",
										Toast.LENGTH_SHORT).show();

								new Thread() {
									public void run() {
										try {

											HttpClient httpclient = new DefaultHttpClient();
											HttpPost httpPost = new HttpPost(
													url);
											ArrayList<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();

											nameValuePairs
													.add(new BasicNameValuePair(
															"phone_number",
															phoneNumber));
											nameValuePairs
													.add(new BasicNameValuePair(
															"serial_number",
															serialNumber));

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

							adapter.notifyDataSetChanged();
							dialog.dismiss();
						}

					});

			alertDlg.setNegativeButton("No",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});

			alertDlg.setMessage(String.format(
					"Do you want to delete this item?",
					getPlantsList.get(position)));
			alertDlg.show();
			return false;
		}
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
