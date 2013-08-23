package nu.vill.myplants;

import nu.vill.myplants.model.Garden;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

public class MainAdapter extends ArrayAdapter<Garden>{

	private static final String TAG = "MainAdapter";
	Garden[] gardens;
	Context context;
	LayoutInflater layoutInflater;
	int layout;
	
	public MainAdapter(Context c, int layout, Garden[] gardens){
		super(c, layout, gardens);
		this.gardens = gardens;
		this.context = c;
		layoutInflater = (LayoutInflater)
				c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.layout = layout;
	}
	
	@Override
	public int getCount() {
		return gardens.length;
	}

	@Override
	public Garden getItem(int position) {
		return gardens[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View currentView = convertView;
		if (currentView == null)
			currentView = layoutInflater.inflate(layout, parent,false);

		Log.i(TAG,"position=" + position);
		Log.i(TAG, "currentView=" + currentView.toString());
		Log.i(TAG,"parent=" + parent.toString());		
		
		TextView gardenName = (TextView) currentView.findViewById(R.id.textPlant);
		gardenName.setText(gardens[position].getName());
		TextView gardenDesc = (TextView) currentView.findViewById(R.id.textPlant2);
		gardenDesc.setText(gardens[position].getDesc());
		return currentView;
	}
}
