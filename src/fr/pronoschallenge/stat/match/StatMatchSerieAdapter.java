package fr.pronoschallenge.stat.match;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import fr.pronoschallenge.R;

public class StatMatchSerieAdapter extends ArrayAdapter<StatMatchSerieEntry> {


	public StatMatchSerieAdapter(Context context, int textViewResourceId,
			List<StatMatchSerieEntry> objects) {
		super(context, textViewResourceId, objects);

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
        
        if (view == null)
        {
            LayoutInflater li = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = li.inflate(R.layout.stat_match_serie_item, null); 
        }
        StatMatchSerieEntry statMatchSerieEntry = getItem(position);
        if (statMatchSerieEntry != null)
        {
            TextView statMatchSerieDomicile = (TextView)view.findViewById(R.id.statMatchSerieResultat);
            statMatchSerieDomicile.setText(String.valueOf(statMatchSerieEntry.getButDom()) + "-" + String.valueOf(statMatchSerieEntry.getButExt()) + statMatchSerieEntry.getNomClubAdverse());

        }
        
     return view;

	}

}