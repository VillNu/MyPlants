/*
 * NewPlantDialogFragment.java
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

import nu.vill.myplants.model.Plant;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class NewPlantDialogFragment extends SherlockDialogFragment{
	
	// define how to report back to the parent
	public interface NewPlantDialogListener{
		public void onSaveNewPlant(Plant plant);
	}

	private static final String TAG = "NewPlantDialogFragment";
	
	private NewPlantDialogListener listener;
	private View view;
	
	/**
	 * Necessary empty constructor
	 */
	public NewPlantDialogFragment() {}
	
	// Make sure activity hosts callback method
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		
		try{listener = (NewPlantDialogListener) activity;}
		catch(ClassCastException e){
			throw new ClassCastException(activity.toString() + 
					" must implement NewPlantDialogListener");
		}
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Activity activity = getSherlockActivity();
		LayoutInflater inflater = activity.getLayoutInflater();
		view = inflater.inflate(R.layout.fragment_new_plant, null);  //null viktigt
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setView(view);
		builder.setTitle(R.string.new_plant_title);
		builder.setPositiveButton(R.string.new_plant_positive_button,
										new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				String name = ((EditText) view.findViewById(R.id.newPlantName))
														.getText().toString();
				String desc = ((EditText) view.findViewById(R.id.newPlantDesc))
														.getText().toString();
				int patch_id = getArguments().getInt("patch_id");
				listener.onSaveNewPlant(new Plant(name,desc, patch_id));
			}
		});
		builder.setNegativeButton(R.string.new_plant_negative_button,
										new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});		
		Log.d(TAG,"New " + builder.toString() + " created. in activity " + activity.toString());
		return builder.create(); // returns an AlertDialog
	}
	
	
	
	
	
}
