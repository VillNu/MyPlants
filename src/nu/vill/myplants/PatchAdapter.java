package nu.vill.myplants;

import nu.vill.myplants.model.Plant;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

public class PatchAdapter extends BaseAdapter{

	Plant[] plants;
	Context context;
	LayoutInflater layoutInflater;
	
	public PatchAdapter(Context c, Plant[] plants){
		this.plants = plants;
		this.context = c;
		layoutInflater = (LayoutInflater)
				c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
		if (convertView == null)
			convertView = layoutInflater.inflate(R.layout.list_item, parent,false);
		TextView plantName = (TextView) convertView.findViewById(R.id.textPlant);
		plantName.setText(plants[position].getName());
		TextView plantDesc = (TextView) convertView.findViewById(R.id.textPlant2);
		plantDesc.setText(plants[position].getDesc());
		return convertView;
	}
}
