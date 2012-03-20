package fr.pronoschallenge.stat.saison;

import java.util.List;

import android.content.Context;
import android.opengl.Visibility;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import fr.pronoschallenge.R;


public class StatSaisonAdapter extends ArrayAdapter<StatSaisonDetailEntry> {
	
	private boolean affichClassement;
	private boolean affichJournee;
	private boolean isSerie;
	
    public StatSaisonAdapter(Context context, int textViewResourceId,
                         List<StatSaisonDetailEntry> objects, boolean affichClassement, boolean affichJournee, boolean isSerie) {
        super(context, textViewResourceId, objects);
    	this.affichClassement = affichClassement;
    	this.affichJournee = affichJournee;
    	this.isSerie = isSerie;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater li = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = li.inflate(R.layout.stat_saison_item, null);
        }
        StatSaisonDetailEntry statEntry = getItem(position);
        
        if (statEntry != null) {        	
            TextView classementTextView = (TextView) view.findViewById(R.id.statSaisonClassement);
        	TextView pseudoTextView = (TextView) view.findViewById(R.id.statSaisonPseudo);
        	TextView resultatTextView = (TextView) view.findViewById(R.id.statSaisonResultat);
        	TextView journeeTextView = (TextView) view.findViewById(R.id.statSaisonJournee);
        	TextView equipeTextView = (TextView) view.findViewById(R.id.statSaisonEquipe);
            
        	if (isSerie == true) {

            	if (position == 0) {
            		classementTextView.setText("OK");
            	} else if (position == 1) {
            		classementTextView.setText("KO");
            	}
        		
            	pseudoTextView.setText(statEntry.getPseudo());
            	journeeTextView.setText(statEntry.getJournee());
            	equipeTextView.setText(statEntry.getResultat());
            	classementTextView.setVisibility(View.VISIBLE);
            	equipeTextView.setVisibility(View.VISIBLE);
            	journeeTextView.setVisibility(View.VISIBLE);
            	
        	} else {
        		
            	if (position == 0) {
            		classementTextView.setText("Gen");	
            	} else if (position == 1) {
            		classementTextView.setText("Hou");
            	} else {
            		classementTextView.setText("Mix");
            	}
            	
            	pseudoTextView.setText(statEntry.getPseudo());
            	resultatTextView.setText(statEntry.getResultat());
            	journeeTextView.setText(statEntry.getJournee());
            	resultatTextView.setVisibility(View.VISIBLE);
            	
            	if (affichClassement == true) {
            		classementTextView.setVisibility(View.VISIBLE);
            	}
            	
            	if (affichJournee == true) {
            		journeeTextView.setVisibility(View.VISIBLE);
            	}
        		
        	}
            	
        }

        return view;
    }
}
