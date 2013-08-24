/*
 * MainAdapter.java
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

import nu.vill.myplants.model.Garden;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

/**
 * Populates a list of Garden objects for the Main activity.
 * @author Magnus
 */
public class MainAdapter extends ArrayAdapter<Garden>{

	//private static final String TAG = "MainAdapter"; // Log tag

	LayoutInflater layoutInflater; // into which context to inflate
	int layout; // the layout
	Garden[] gardens; // the data set
	
	/**
	 * Sets up the inflater, the data set and the layout
	 * @param c the containing activity
	 * @param layout the XML layout for each view
	 * @param gardens the data set
	 */
	public MainAdapter(Context c, int layout, Garden[] gardens){
		super(c, layout, gardens);
		layoutInflater = (LayoutInflater)
				c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.layout = layout;
		this.gardens = gardens;
	}
	
	/**
	 * 
	 */
	@Override
	public int getCount() {
		return gardens.length;
	}

	/**
	 * 
	 */
	@Override
	public Garden getItem(int position) {
		return gardens[position];
	}

	/**
	 * 
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * Returns an inflated view displaying data for the given garden
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View currentView = convertView;
		if (currentView == null) currentView = layoutInflater.inflate(layout, parent,false);
		TextView gardenName = (TextView) currentView.findViewById(R.id.textPlant);
		gardenName.setText(gardens[position].getName());
		TextView gardenDesc = (TextView) currentView.findViewById(R.id.textPlant2);
		gardenDesc.setText(gardens[position].getDesc());
		return currentView;
	}
}
