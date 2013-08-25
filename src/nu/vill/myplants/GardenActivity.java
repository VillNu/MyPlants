/*
 * GardenActivity.java
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

import java.net.URLEncoder;

import nu.vill.data.Connection;
import nu.vill.myplants.model.Plant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.rufflez.swipeytabs.TabsAdapter;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Toast;


// TODO Change from Ruffalos FragmentPagerAdapter to a FragmentStatePagerAdapter for memory conservation reasons
/**
 * This activity and its instance of TabsAdapter is based on example code by Andrew Ruffolo.
 * It uses ActionbarSherlock with tabs and Pageviewer for swiping between the tabs/fragments.
 * The tabs/fragments contains the patches for a certain garden.
 * Each fragment has a description area for the patch and an "Add plant"-button which produces
 * a dialog user input for creating a new plant.
 * @author Magnus
 */
public class GardenActivity extends SherlockFragmentActivity 
					implements NewPlantDialogFragment.NewPlantDialogListener{

	// Constants
	public static final String EXTRA_GARDEN_ID = "nu.vill.myplants.GARDEN_ID"; //identifier for bundled data
	public static final String EXTRA_GARDEN_NAME = "nu.vill.myplants.GARDEN_NAME"; //identifier for bundled data
	public static final String TAG = "GardenActivity"; // Log tag
	
	// 
	private TabsAdapter tabsAdapter; // holds the fragments for 
	private ActionBar actionBar;
	private String url;

	/**
	 * Sets up 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Setup the action bar (backward compatibility provided by the
		// actionbarsherlock package
		actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(getIntent().getStringExtra(EXTRA_GARDEN_NAME));

		// Sets the activity viewpager and its adapter (other ways of doing it?)
		ViewPager viewPager = new ViewPager(this);
		viewPager.setId(R.id.pager);
		setContentView(viewPager);
		tabsAdapter = new TabsAdapter(this, viewPager);

		// Get the right patch
		url = getString(R.string.patches_url) + getIntent().getIntExtra(EXTRA_GARDEN_ID, 0);
	} 
	
	/**
	 * Updates the tabs
	 */
	@Override
	protected void onResume() {
		Log.v(TAG,"Resumed");
		new GardenDownloader().execute(url);		
		super.onResume();
	}

	/**
	 * Finishes this Activity when user presses the Up button
	 */
	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
		finish();
		return true;
	}
	
	/**
	 * Creates tabs for the patches in the garden with the specified garden id
	 * @author Magnus
	 */
	private class GardenDownloader extends AsyncTask<String, Void, String> {

		/**
		 * Downloads the patches for this garden from url, off the main thread 
		 */
		@Override
		protected String doInBackground(String... urls) {
			return new Connection().query(urls[0]); // Get the patches from the server
		}
		
		/**
		 * Updates the tabs 
		 */
		@Override
		protected void onPostExecute(String backgroundResult) {
			super.onPostExecute(backgroundResult);
			//TODO Question. Can the reference to tabsAdapter and actionBar leak the Activity?
			try { // Add tabs for each patch in this garden
				JSONArray jArray = new JSONArray(backgroundResult); // Create JSON array with patches
				Log.v(TAG,"Counted "+jArray.length()+" patches.");
				if (tabsAdapter.getCount() != jArray.length()) { // Check if tabs are already created
					for (int i = 0; i < jArray.length(); i++){ // Work through the array of patches
						JSONObject json = jArray.getJSONObject(i); // Extract a patch
						Bundle patchInfo = new Bundle(); // Create a Bundle to hold patch information
						patchInfo.putInt("id", Integer.parseInt(json.getString("id"))); // Add patch id
						patchInfo.putString("desc", json.getString("desc")); // Add patch description
						tabsAdapter.addTab(actionBar.newTab() // create new tab
								.setText(json.getString("name")), // tab title = patch name
								PatchFragment.class, patchInfo); // tab layout
					}
				}else {
					Log.v(TAG,"No need to add tabs, all are already created.");
				}
			}catch(JSONException e){
				Log.w(TAG,"JSONExcpetion: " + e);
			}		
		}
	}	
	
	/**
	 * Implementation of the NewPlantDialog listener
	 * Is called when user clicks Save-button
	 */
	@Override
	public void onSaveNewPlant(Plant plant) {
		// Create new plant asynchronously off the main thread
		new NewPlantCreator().execute(plant);
	}

	//TODO Change to REST POST method in conjunction with the Connection class
	/**
	 * Creates the new plant on the server asynchronously off the main thread
	 * @author Magnus
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
				String newPlantId = new Connection().query(url); // server creation
				Log.v(TAG,"Created plant with id " + newPlantId);
			return newPlantId;
		}

		@Override
		protected void onPostExecute(String newPlantId) {
			// Reports to user whether a new plant has been created
			// The toast should ideally be executed before the onPostExecute,
			// because onPostExecute is avoided when canceled. But Toast needs the activity
			// to still be active (on the main thread)
			toast(getString(R.string.new_plant_toast)+" (id:"+newPlantId+")",false);
			// TODO Update the fragment with the new plant (bypassing server?)
			super.onPostExecute(newPlantId);
		}
	}

	/**
	 * Short method within activity's context that simplify toasting	
	 * @param text The text to be shown
	 * @param short_duration if false shown for long duration
	 */
	private void toast(String text, boolean short_duration){
		int length = short_duration ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG;
		Toast.makeText(this, text, length).show();
	}
	
	
/*	
	Not needed when ActionBarSherlock is used
	private void setupActionBar(ActionBar aBar);

	Not needed when ActionBarSherlock is used
  	@Override	public boolean onCreateOptionsMenu(Menu menu);	

	Not needed when ActionBarSherlock is used
	@Override	public boolean onOptionsItemSelected(MenuItem item) {
*/
}
