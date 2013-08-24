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

/**
 * Contains the view which show a tab for each patch for a certain garden
 * Has a description area for the patch and an Add-button which produces
 * a Dialog instance for creating a new plant.
 * @author Magnus
 */
public class GardenActivity extends SherlockFragmentActivity 
					implements NewPlantDialogFragment.NewPlantDialogListener{

	public static final String EXTRA_GARDEN = "nu.vill.myplants.GARDEN"; //identifier for bundled data
	public static final String TAG = "GardenActivity"; // Log tag
	private TabsAdapter tabsAdapter;
	private ActionBar actionBar;
	private String url;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ViewPager viewPager = new ViewPager(this);
		viewPager.setId(R.id.pager);
		setContentView(viewPager);
			
		actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayHomeAsUpEnabled(true);

		tabsAdapter = new TabsAdapter(this, viewPager);
		// get the right patch
		url = getString(R.string.patches_url) + getIntent().getStringExtra(EXTRA_GARDEN);
		
	} 
	
	/**
	 * Updates the tabs
	 */
	@Override
	protected void onResume() {
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
	 * Creates a tab for each patch
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
			
			try { // Add tabs for each patch in this garden
				JSONArray jArray = new JSONArray(backgroundResult); // Create JSON array with patches
				
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
	 * Catch clicks from the NewPlantDialog
	 */
	@Override
	public void onSaveNewPlant(Plant plant) {
		new NewPlantCreator().execute(plant);
		
	}

	//TODO Change to REST POST method in conjunction with the Connection class
	/**
	 * Creates the new plant on the server asynchronously from the main thread
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
				String newPlantId = new Connection().query(url);
			return newPlantId;
		}

		@Override
		protected void onPostExecute(String newPlantId) {
			// Reports to user that a new plant has been created
			toast( getString(R.string.new_plant_toast) + " (id:" + newPlantId + ")");			
			super.onPostExecute(newPlantId);
		}
	}

	/**
	 * Short method within activity's context that simplify toasting	
	 * @param text
	 */
	private void toast(String text){
		Toast.makeText(this, "new plant id = " + text,
				Toast.LENGTH_SHORT).show();
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
