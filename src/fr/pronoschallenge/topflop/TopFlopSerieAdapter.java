package fr.pronoschallenge.topflop;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import fr.pronoschallenge.R;

public class TopFlopSerieAdapter extends ArrayAdapter<TopFlopEntry> {


	public TopFlopSerieAdapter(Context context, int textViewResourceId,
			List<TopFlopEntry> objects) {
		super(context, textViewResourceId, objects);

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
        
        if (view == null)
        {
            LayoutInflater li = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = li.inflate(R.layout.top_flop_item, null); 
        }
        TopFlopEntry topFlopEntry = getItem(position);
        if (topFlopEntry != null)
        {
            TextView evolutionTextView = (TextView)view.findViewById(R.id.topFlopItemEvolution);
            evolutionTextView.setText(topFlopEntry.getEvolution());
            TextView pseudoTextView = (TextView)view.findViewById(R.id.topFlopItemPseudo);
            pseudoTextView.setText(topFlopEntry.getPseudo());            
        }
        
     return view;

	}

}
