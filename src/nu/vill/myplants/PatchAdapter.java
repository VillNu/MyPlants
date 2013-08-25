/*
 * PatchAdapter.java
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
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

/**
 * Populates a patch with its plants
 * @author Magnus
 */
public class PatchAdapter extends BaseAdapter{

	private Plant[] plants;
	private LayoutInflater layoutInflater;
	
	public PatchAdapter(Context c, Plant[] plants){
 		this.plants = plants;
		this.layoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		return plants.length;
	}

	@Override
	public Object getItem(int position) {
		return plants[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) convertView = 
				layoutInflater.inflate(R.layout.list_item, parent,false);
		TextView plantName = (TextView) convertView.findViewById(R.id.textPlant);
		plantName.setText(plants[position].getName());
		TextView plantDesc = (TextView) convertView.findViewById(R.id.textPlant2);
		plantDesc.setText(plants[position].getDesc());
		return convertView;
	}
}
