/*
 * Connection.java
 * 
 * Copyright (C) 2013 Magnus Duberg 
 *
 * Licensed under the Want Now License, Version 0.q (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 *
 *      http://vill.nu/licenses/WANT-NOW-LICENSE-0.q
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package nu.vill.myplants;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import nu.vill.data.Connection;
import nu.vill.myplants.model.Garden;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


public class MainActivity extends SherlockActivity {

	protected static final int GARDEN_ID = 0;
	public static final String EXTRA_GARDEN_ID = "nu.vill.myplants.GARDEN_ID";
	public static final String EXTRA_GARDEN_NAME = "nu.vill.myplants.GARDEN_NAME";
	private static final String TAG = "MainActivity";
	ArrayList<Garden> gardens = new ArrayList<Garden>();
	ArrayAdapter<Garden> gardensAdapter;
	ListView listView;
	AsyncTask<?,?,?> gardenDownload;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);				
		setContentView(R.layout.activity_main);		

		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);

		listView = (ListView) findViewById(R.id.listGardens);
		listView.setOnItemClickListener(new OnItemClickListener(){		
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int pos,
					long id) {
				openGardenActivity((Garden) adapter.getItemAtPosition(pos));				
			}
		});

		updateList();
	}


	/**
	 * Does nothing since its state is recovered from the server
	 */
	@Override
	protected void onResume() {
		Log.v(TAG,"MainActivity resumed (task id:"+this.getTaskId()+")");
		super.onResume();	
	}

	/**
	 * Cancels unnecessary updates of this Activity (the list of gardens)
	 */
	@Override
	protected void onDestroy() {
		gardenDownload.cancel(true);
		Log.v(TAG,"MainActivity destroyed (task id:"+this.getTaskId()+")");
		super.onDestroy();
	}
	
	
	/**
	 * Updates the list of gardens off the background thread and keeps a
	 * reference to it so it can be killed onDestroy
	 */
	private void updateList(){
		gardenDownload = new ListOfGardensDownloader().execute(getString(R.string.gardens_url));
	}

	/**
	 * Opens activity to manage the garden with the given id
	 * @param garden
	 */
	private void openGardenActivity(Garden garden){
		Intent intent = new Intent(this, GardenActivity.class);
		intent.putExtra(EXTRA_GARDEN_ID, garden.getId());
		intent.putExtra(EXTRA_GARDEN_NAME, garden.getName());
		startActivity(intent);
	}

	/**
	 * Makes toasting shorter
	 * @param message
	 */
	protected void toast(String message){
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

	/**
	 * Downloads gardens from server off the main thread and updates the list of gardens
	 * the String passed to the  must be well formed URL and the response from the server must contain
	 * some gardens formatted as an JSON object
	 * @author Magnus
	 */
	private class ListOfGardensDownloader extends AsyncTask<String, Void, String> {
		
		@Override
		protected String doInBackground(String... urls) {
			return new Connection().query(urls[0]); // downloads the gardens
		}
		
		@Override
		protected void onPostExecute(String backgroundResult) {
			super.onPostExecute(backgroundResult);
			updateListViewAdapter(strJasonsToGardenArray(backgroundResult));
		}
	}

	/**
	 * Updates the list with the given gardens
	 * @param gardens array of Garden objects
	 */
	private void updateListViewAdapter(Garden[] gardens){
		gardensAdapter = new MainAdapter(this, R.layout.list_item, gardens);
		listView.setAdapter(gardensAdapter);	
	}

	/**
	 * Converts a JSON formatted string of gardens to a Garden array
	 * @param strJasons JSON formatted string containing gardens 
	 * @return array of Garden objects
	 */
	public static Garden[] strJasonsToGardenArray(String strJasons){
		Garden[] gardenArray = null;
		try {
		JSONArray jArray = new JSONArray(strJasons);
		gardenArray = new Garden[jArray.length()];
		for (int i = 0; i < jArray.length(); i++){
			JSONObject json = jArray.getJSONObject(i);
			json.length();
			gardenArray[i] = new Garden(json.getInt("id"),
					json.getString("name"),
					json.getString("desc"));
		}
		}catch (Exception e){
			Log.e("log_tag", "Error : " + e.toString());
		}
		return gardenArray;
	}

	
	/**
	 * Potentially useful for coming function "View plant web info"
	 * @param webURL
	 */
	void openWebPage(String webURL){
		/*temp*/	if(webURL.length() < 1)	webURL = "http://sv.wikipedia.org/w/index.php?search=" + "Orkid�";

		// Build the Intent
		Uri webpage = Uri.parse(webURL);
		Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);

		// Verify it resolves
		PackageManager packageManager = getPackageManager();
		List<ResolveInfo> activities = packageManager.queryIntentActivities(webIntent, 0);
		boolean isIntentSafe = activities.size() > 0;

		// Start an activity if it's safe
		if (isIntentSafe) {
			startActivity(webIntent);
		}else{
			// ChooseThere was no program to receive the Intent
			String title = getResources().getText(R.string.chooser_title).toString();
			// Create and start the chooser
			Intent chooser = Intent.createChooser(new Intent(Intent.ACTION_ALL_APPS), title);
			startActivity(chooser);
		}
	}

	/* Not needed when ActionBarSherlock is used	
	@Override	public boolean onCreateOptionsMenu(Menu menu) 	 */
}


