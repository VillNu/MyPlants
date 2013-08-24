/*
 * PatchFragment.java
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

import nu.vill.data.Connection;
import nu.vill.myplants.model.Plant;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class PatchFragment extends SherlockFragment {
	
	private static final String TAG = "TabFragment";
	private int patchId;
	private ListView listView;
	private static AsyncTask<String,Void,String> asyncListLoader;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState){
		View view = inflater.inflate(nu.vill.myplants.R.layout.fragment_patch,
				container, false);
		
		// Set the description for each Patch(tab) (shown above list of plants)
		((TextView) view.findViewById(nu.vill.myplants.R.id.patch_title))
				.setText(getArguments().getString("desc"));
		patchId = getArguments().getInt("id");
		listView = (ListView) view.findViewById(R.id.listViewPlants);

		// Update the Listview (list of plants) asynchronously
		updateList();
		
		// Configure the "Add new plant"-button
		Button addPlantButton = (Button) view.findViewById(R.id.buttonAddPlant);
		addPlantButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {//Starting a new plant dialog
				showNewPlantDialog();
			}
		});
		
		return view;
	}	
	
	public void showNewPlantDialog() {
		DialogFragment newPlantDialog = new NewPlantDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("patch_id", patchId); // tell the dialog which patch
		newPlantDialog.setArguments(bundle); // new plants belong to
		newPlantDialog.show(getFragmentManager(), "NewPlantDialog");
	}

	@Override
	public void onDestroy() {
		Log.d(TAG,"destroying fragment for patch " +  this.getArguments().getInt("id"));
		asyncListLoader.cancel(true);// stop list loading to prevent nullpointerexceptions
		super.onDestroy();
	}

	/**
	 * Update the list of plants asynchronously
	 */
	private void updateList(){
		// Asynchronously download the plants of this patch
		asyncListLoader = new PatchDownloader()
							.execute(getString(R.string.plants_url) + patchId);
	}

	private class PatchDownloader extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... urls) {
			// Downloads the plants in this patch
			return new Connection().query(urls[0]);
		}

		@Override
		protected void onPostExecute(String backgroundResult) {
			super.onPostExecute(backgroundResult);
			// update the list by setting a new adapter
			if (getSherlockActivity() != null){// check its still visible
				listView.setAdapter(new PatchAdapter(getSherlockActivity(),
						strJasonsToPlantArray(backgroundResult)));
			}
		}
	}

	/**
	 * Converts a JSON-string to an array of Plant objects
	 * @param String (of JSON objects)
	 * @return Plant[]
	 */
	public static Plant[] strJasonsToPlantArray(String strJasons){
	Log.d(TAG, "Converting " + strJasons + " to array..");
	
		if (strJasons.equals("null"))
			return new Plant[]{}; // return empty array
		
		try {
			JSONArray jArray = new JSONArray(strJasons);
			Plant[] plantArray = new Plant[jArray.length()];
			for (int i = 0; i < jArray.length(); i++){
				JSONObject json = jArray.getJSONObject(i);
				json.length();
				plantArray[i] = new Plant(json.getInt("id"),
						json.getString("name"),
						json.getString("description"),
						json.getInt("patch_id"));
			}
			return plantArray; // return array with plants
		}catch (Exception e){
			Log.e(TAG, "Error : " + e.toString());
			return new Plant[]{}; // return empty array
		}		
	}

	

}
