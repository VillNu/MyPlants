package nu.vill.myplants;

import java.net.URLEncoder;
import nu.vill.data.Connection;
import nu.vill.myplants.model.Plant;

import org.json.JSONArray;
import org.json.JSONObject;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.rufflez.swipeytabs.TabsAdapter;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Toast;

public class GardenActivity extends SherlockFragmentActivity 
					implements NewPlantDialogFragment.NewPlantDialogListener{
	
	public static final String EXTRA_GARDEN = "nu.vill.myplants.GARDEN";
	public static final String TAG = "GardenActivity";
	private ViewPager viewPager;
	private TabsAdapter tabsAdapter;
	private ActionBar actionBar;
	private String url;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		viewPager = new ViewPager(this);
		viewPager.setId(R.id.pager);
		setContentView(viewPager);
		//setContentView(R.layout.activity_garden);
			
		actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayHomeAsUpEnabled(true);
		//actionBar.setDisplayShowHomeEnabled(true);
		//actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);

		tabsAdapter = new TabsAdapter(this, viewPager);
		// get the right patch
		url = getString(R.string.patches_url) + getIntent().getStringExtra(EXTRA_GARDEN);
		
		new GardenDownloader().execute(url);
		
/*		Bundle gardenInfo = new Bundle();
		gardenInfo.putInt("id", 1);
		gardenInfo.putString("desc", "descRipTion");
		for (int i = 0; i < 4; i++){
			
			tabsAdapter.addTab(actionBar.newTab().setText("tabb"), PatchFragment.class, gardenInfo);
		}
*/
	} 
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	
/* Not needed when ActionBarSherlock is used
	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 *//*
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar(ActionBar aBar) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}
*/
/* Not needed when ActionBarSherlock is used
 * 	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.garden, menu);
		return true;
	}
*/
	

	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
		finish();
		return true;
	}
		

/* Not needed when ActionBarSherlock is used
 *	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
*/
	/**
	 * Creates a tab for each patch
	 * @author Magnus
	 *
	 */
	private class GardenDownloader extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... urls) {
			// Get the patches from the server
			return new Connection().query(urls[0]);
		}
		
		@Override
		protected void onPostExecute(String backgroundResult) {
			super.onPostExecute(backgroundResult);

			try { // add tabs for each patch in this garden
				JSONArray jArray = new JSONArray(backgroundResult);
				if (tabsAdapter.getCount() != jArray.length()){
					for (int i = 0; i < jArray.length(); i++){
						JSONObject json = jArray.getJSONObject(i);
						Bundle gardenInfo = new Bundle();
						gardenInfo.putInt("id", Integer.parseInt(json.getString("id")));
						gardenInfo.putString("desc", json.getString("desc"));
						String name = json.getString("name");												
						tabsAdapter.addTab(actionBar.newTab().setText(name),
											PatchFragment.class, gardenInfo);
					}
				}
			}catch(Exception e){
				Log.e(TAG,"Excpetion: " + e);
			}		
		}
	}	
	
	/**
	 * Catch clicks from the NewPlantDialog
	 */
	@Override
	public void onSaveNewPlant(Plant plant) {
		new NewPlantCreator().execute(plant);
		
	}

	/**
	 * Ugly way of sending a new plant to the server
	 * @author Magnus
	 *
	 */
	private class NewPlantCreator extends AsyncTask<Plant,Void,String>{

		@Override
		protected String doInBackground(Plant... plants) {
			String name = plants[0].getName();
			String desc = plants[0].getDesc();
			try {// encode the URL for a http GET-call
				name = URLEncoder.encode(name,"UTF-8");
				desc = URLEncoder.encode(desc,"UTF-8");
			}catch(Exception e){};
			String url = getString(R.string.new_plant_url) + 
										"name=" + name + 
										"&description=" + desc +  
										"&patch_id=" + plants[0].getPatch();
		
				String newPlantId = new Connection().query(url);
			
			return newPlantId;
		}

		@Override
		protected void onPostExecute(String newPlantId) {
			toast(newPlantId);
			
			super.onPostExecute(newPlantId);
		}
	}
		
	private void toast(String text){
		Toast.makeText(this, "new plant id = " + text,
				Toast.LENGTH_SHORT).show();
	}
}
