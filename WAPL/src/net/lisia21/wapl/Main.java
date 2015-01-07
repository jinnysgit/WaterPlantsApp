package net.lisia21.wapl;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

@SuppressLint("NewApi")
public class Main extends Activity implements OnClickListener {

	Button add, remove;
	ListView plantsList;
	ImageView plantImage;

	String phoneNumber, serialNumber;
	private ArrayList<String> arrayList;
	private ArrayList<String> serialBox;
	private ArrayAdapter<String> adapter;

	Document doc = null;

	GetXMLTask task;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		add = (Button) findViewById(R.id.add);
		remove = (Button) findViewById(R.id.remove);

		arrayList = new ArrayList<String>();
		serialBox = new ArrayList<String>();
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, arrayList);

		plantsList = (ListView) findViewById(R.id.plantList);
		plantImage = (ImageView) findViewById(R.id.plants);

		Intent getIntent = getIntent();
		phoneNumber = getIntent.getStringExtra("phoneNumber");

		add.setOnClickListener(this);
		remove.setOnClickListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		
		String xmlUrl = "http://203.247.166.59/" + phoneNumber + "/data.xml";

		task = new GetXMLTask(this);
		task.execute(xmlUrl);
		
		startService(new Intent(this, UpdateService.class));

	}

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.add:
			stopService(new Intent(this, UpdateService.class));
			Intent addIntent = new Intent(getApplicationContext(), Add.class);
			addIntent.putExtra("phoneNumber", phoneNumber);
			startActivity(addIntent);
			break;

		case R.id.remove:
			stopService(new Intent(this, UpdateService.class));
			Intent removeIntent = new Intent(getApplicationContext(),
					Remove.class);
			removeIntent.putExtra("phoneNumber", phoneNumber);
			removeIntent.putExtra("plantsList", serialBox);
			startActivity(removeIntent);
			break;
		}
	}

	@SuppressLint("NewApi")
	private class GetXMLTask extends AsyncTask<String, Void, Document> {
		private Activity context;

		public GetXMLTask(Activity context) {
			this.context = context;
		}

		@Override
		protected Document doInBackground(String... urls) {
			URL url;

			try {
				url = new URL(urls[0]);

				DocumentBuilderFactory dbf = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();

				doc = db.parse(new InputSource(url.openStream()));
				doc.getDocumentElement().normalize();

			} catch (Exception e) {
				Log.w("Connection", "ConnectionError");
			}

			return doc;
		}

		@Override
		protected void onPostExecute(Document doc) {
			String s = "";
			arrayList.clear();
			serialBox.clear();

			if (doc.getElementsByTagName("user") != null) {

				if (doc.getElementsByTagName("raspberry").getLength() == 0) {
					plantImage.setVisibility(plantImage.VISIBLE);
					plantsList.setVisibility(plantsList.GONE);

				} else {
					plantImage.setVisibility(plantImage.GONE);
					plantsList.setVisibility(plantsList.VISIBLE);

					NodeList nodeList = doc.getElementsByTagName("raspberry");

					for (int i = 0; i < nodeList.getLength(); i++) {

						Node node = nodeList.item(i);
						Element fstElmnt = (Element) node;

						NodeList serialList = fstElmnt
								.getElementsByTagName("serial");

						s = "" + (i + 1) + ". Serial No. ";

						Element serialElement = (Element) serialList.item(0);
						serialList = serialElement.getChildNodes();
						serialBox.add(((Node) serialList.item(0))
								.getNodeValue());

						s += " " + ((Node) serialList.item(0)).getNodeValue()
								+ "\n";

						NodeList tempList = fstElmnt
								.getElementsByTagName("temperature");

						Element tempElement = (Element) tempList.item(0);
						tempList = tempElement.getChildNodes();

						s += "   - temperature :  "
								+ ((Node) tempList.item(0)).getNodeValue()
								+ "\n";

						NodeList humidList = fstElmnt
								.getElementsByTagName("humidity");

						Element humidElement = (Element) humidList.item(0);
						humidList = humidElement.getChildNodes();

						s += "   - humidity :  "
								+ ((Node) humidList.item(0)).getNodeValue()
								+ "\n";

						NodeList moistureList = fstElmnt
								.getElementsByTagName("moisture");

						Element moistureElement = (Element) moistureList
								.item(0);
						moistureList = moistureElement.getChildNodes();

						s += "   - moisture :  "
								+ ((Node) moistureList.item(0)).getNodeValue()
								+ "\n";

						arrayList.add(s);
					}
				}
			}
			plantsList.setAdapter(adapter);

		}
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
