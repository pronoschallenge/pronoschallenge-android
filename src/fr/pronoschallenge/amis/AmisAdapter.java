package fr.pronoschallenge.amis;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import fr.pronoschallenge.R;


public class AmisAdapter extends ArrayAdapter<AmisPalmaresEntry> {

	String mUser;
	
    public AmisAdapter(Context context, int textViewResourceId,
                         List<AmisPalmaresEntry> objects, String mUser) {
        super(context, textViewResourceId, objects);
        this.mUser = mUser;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater li = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = li.inflate(R.layout.amis_liste_item, null);
        }
        AmisPalmaresEntry palmaresEntry = getItem(position);
        
        if (palmaresEntry != null) {        	
            TextView pseudoTextView = (TextView) view.findViewById(R.id.amisPseudo);
        	TextView placeGeneralTextView = (TextView) view.findViewById(R.id.amisPlaceGeneral);
        	TextView placeHourraTextView = (TextView) view.findViewById(R.id.amisPlaceHourra);
        	TextView placeMixteTextView = (TextView) view.findViewById(R.id.amisPlaceMixte);
            
            pseudoTextView.setText(palmaresEntry.getPseudo());
            pseudoTextView.setVisibility(View.VISIBLE);
            
            LinearLayout amisLayout = (LinearLayout)view.findViewById(R.id.amisListeItemLayout);
            if (palmaresEntry.getPseudo().toUpperCase().compareTo(mUser.toUpperCase()) == 0) {            	
            	amisLayout.setBackgroundColor(Color.WHITE);
            	pseudoTextView.setTextColor(Color.BLACK);
            	placeGeneralTextView.setTextColor(Color.BLACK);
            	placeHourraTextView.setTextColor(Color.BLACK);
            	placeMixteTextView.setTextColor(Color.BLACK);
            } else {
            	amisLayout.setBackgroundColor(Color.BLACK);
            	pseudoTextView.setTextColor(Color.GRAY);
            	placeGeneralTextView.setTextColor(Color.GRAY);
            	placeHourraTextView.setTextColor(Color.GRAY);
            	placeMixteTextView.setTextColor(Color.GRAY);
            }
            
            for (AmisPalmaresDetailEntry palmaresDetailEntry : palmaresEntry.getPalmaresDetail()) {
            	if (palmaresDetailEntry.getTypeChamp().equals("general")) {                    
                    placeGeneralTextView.setText(palmaresDetailEntry.getNumPlace().toString());
            	} else if (palmaresDetailEntry.getTypeChamp().equals("hourra")) {                    
                    placeHourraTextView.setText(palmaresDetailEntry.getNumPlace().toString());
            	} else if (palmaresDetailEntry.getTypeChamp().equals("mixte")) {                    
                    placeMixteTextView.setText(palmaresDetailEntry.getNumPlace().toString());
            	}
            }
            	
        }

        return view;
    }
}
