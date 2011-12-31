package fr.pronoschallenge.stat.match;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import fr.pronoschallenge.R;

public class StatMatchClassementAdapter extends ArrayAdapter<StatMatchClassementEntry> {

	public StatMatchClassementAdapter(Context context, int textViewResourceId,
			List<StatMatchClassementEntry> objects) {
		super(context, textViewResourceId, objects);

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
        
        if (view == null)
        {
            LayoutInflater li = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = li.inflate(R.layout.stat_match_classement_item, null); 
        }
        StatMatchClassementEntry statMatchClassementEntry = getItem(position);
        if (statMatchClassementEntry != null)
        {
            TextView statMatchClassementDom = (TextView)view.findViewById(R.id.statMatchClassementDom);
            statMatchClassementDom.setText(statMatchClassementEntry.getInfoDom());
            TextView statMatchClassementTexte = (TextView)view.findViewById(R.id.statMatchClassementTexte);
            statMatchClassementTexte.setText(statMatchClassementEntry.getTypeInfo());
            TextView statMatchClassementExt = (TextView)view.findViewById(R.id.statMatchClassementExt);
            statMatchClassementExt.setText(statMatchClassementEntry.getInfoExt());
        }
        
     return view;

	}

}
