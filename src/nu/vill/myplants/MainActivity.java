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
	public static final String EXTRA_GARDEN = "nu.vill.myplants.GARDEN";
	private static final String TAG = "MainActivity";
	ArrayList<Garden> gardens = new ArrayList<Garden>();
	ArrayAdapter<Garden> gardensAdapter;
	ListView listView;

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
				Garden garden = (Garden) adapter.getItemAtPosition(pos);
				toast("You have pressed id " + id + "\nGarden id: "
						+ garden.getId());
				openGardenActivity(String.valueOf(garden.getId()));				
			}
		});

		updateList();
	}

	@Override
	protected void onResume() {
		Log.i(TAG,"MainActivity resumed (task id:"+this.getTaskId()+")");
		super.onResume();	
	}

	@Override
	protected void onDestroy() {
		Log.i(TAG,"MainActivity destroyed (task id:"+this.getTaskId()+")");
		super.onDestroy();
	}
	
	
	
	/* Not needed when ActionBarSherlock is used	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	 */

	/* (non-Javadoc)
	 * @see com.actionbarsherlock.app.SherlockActivity#onDestroy()
	 */

	private void updateList(){
		new ListOfGardensDownloader().execute(getString(R.string.gardens_url));
	}

	private void openGardenActivity(String garden_id){
		Intent intent = new Intent(this, GardenActivity.class);
		intent.putExtra(EXTRA_GARDEN, garden_id);
		startActivity(intent);
	}


	protected void toast(String message){
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

	private class ListOfGardensDownloader extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute(){		}

		@Override
		protected String doInBackground(String... urls) {
			return new Connection().query(urls[0]);
		}

		@Override
		protected void onPostExecute(String backgroundResult) {
			super.onPostExecute(backgroundResult);
			updateListViewAdapter(strJasonsToGardenArray(backgroundResult));
		}
	}

	private void updateListViewAdapter(Garden[] gardens){
		gardensAdapter = new MainAdapter(this, R.layout.list_item, gardens);
		listView.setAdapter(gardensAdapter);	
	}


	public static Garden[] strJasonsToGardenArray(String strJasons){

		Garden[] gardenArray = null;
		try { Log.d(TAG, "Converting to array..");
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


	void openWebPage(String webURL){
		/*temp*/	if(webURL.length() < 1)	webURL = "http://sv.wikipedia.org/w/index.php?search=" + "Orkidé";

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

}


