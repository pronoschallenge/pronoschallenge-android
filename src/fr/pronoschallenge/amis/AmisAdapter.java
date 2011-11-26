package fr.pronoschallenge.amis;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.*;

import fr.pronoschallenge.R;


public class AmisAdapter extends ArrayAdapter<AmisPalmaresEntry> {

    public AmisAdapter(Context context, int textViewResourceId,
                         List<AmisPalmaresEntry> objects) {
        super(context, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater li = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //le layout représentant la ligne dans le listView
            view = li.inflate(R.layout.amis_liste_item, null);
        }
        AmisPalmaresEntry palmaresEntry = getItem(position);
        
        if (palmaresEntry != null) {        	
            TextView pseudoTextView = (TextView) view.findViewById(R.id.amisPseudo);
            pseudoTextView.setText(palmaresEntry.getPseudo());
            pseudoTextView.setVisibility(View.VISIBLE);
            
            for (AmisPalmaresDetailEntry palmaresDetailEntry : palmaresEntry.getPalmaresDetail()) {
            	if (palmaresDetailEntry.getTypeChamp().equals("general")) {
                    TextView placeGeneralTextView = (TextView) view.findViewById(R.id.amisPlaceGeneral);
                    placeGeneralTextView.setText(palmaresDetailEntry.getNumPlace().toString());
            	} else if (palmaresDetailEntry.getTypeChamp().equals("hourra")) {
                    TextView placeHourraTextView = (TextView) view.findViewById(R.id.amisPlaceHourra);
                    placeHourraTextView.setText(palmaresDetailEntry.getNumPlace().toString());
            	} else if (palmaresDetailEntry.getTypeChamp().equals("mixte")) {
                    TextView placeMixteTextView = (TextView) view.findViewById(R.id.amisPlaceMixte);
                    placeMixteTextView.setText(palmaresDetailEntry.getNumPlace().toString());
            	}
            }
            	
        }

        return view;
    }
}
