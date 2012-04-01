package fr.pronoschallenge.stat.saison;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import fr.pronoschallenge.R;
import fr.pronoschallenge.classement.ClassementActivity;
import fr.pronoschallenge.profil.ProfilPagedViewActivity;


public class StatSaisonAdapter extends ArrayAdapter<StatSaisonDetailEntry> {
	
	final int TYPE_EVOL = 0;
	final int TYPE_POINT = 1;
	final int TYPE_SERIE = 2;
	
	private boolean affichClassement;
	private boolean affichJournee;
	private int type;
	
    public StatSaisonAdapter(Context context, int textViewResourceId,
                         List<StatSaisonDetailEntry> objects, boolean affichClassement, boolean affichJournee, int type) {
        super(context, textViewResourceId, objects);
    	this.affichClassement = affichClassement;
    	this.affichJournee = affichJournee;
    	this.type = type;
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
            
        	if (type == TYPE_SERIE) {

            	if (position == 0) {
            		classementTextView.setText("OK");
            	} else if (position == 1) {
            		classementTextView.setText("KO");
            	}
        		
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

        	// Clic sur un entête de classement
            	final int typeClassement = position;
            	classementTextView.setOnClickListener(new View.OnClickListener() {					
					public void onClick(View v) {
		            	CharSequence[] classementItemsLast = {v.getContext().getString(R.string.type_classement_general_last), v.getContext().getString(R.string.type_classement_hourra_last)};
		            	CharSequence[] classementItemsClass = {v.getContext().getString(R.string.type_classement_general), v.getContext().getString(R.string.type_classement_hourra), v.getContext().getString(R.string.type_classement_mixte)};
		            	final CharSequence[] classementItems;
		            	if (type == TYPE_EVOL) {
		            		classementItems = classementItemsClass;	
		            	} else {
		            		classementItems = classementItemsLast;
		            	}            	
		            	String[] classementTypesLast = {ClassementActivity.CLASSEMENT_TYPE_GENERAL_LAST, ClassementActivity.CLASSEMENT_TYPE_HOURRA_LAST};
		            	String[] classementTypesClass = {ClassementActivity.CLASSEMENT_TYPE_GENERAL, ClassementActivity.CLASSEMENT_TYPE_HOURRA, ClassementActivity.CLASSEMENT_TYPE_MIXTE};
		                final String[] classementTypes;
		            	if (type == TYPE_EVOL) {
		            		classementTypes = classementTypesClass;	
		            	} else {
		            		classementTypes = classementTypesLast;
		            	}			            Intent classementIntent = new Intent();
				    	classementIntent.setClassName("fr.pronoschallenge", "fr.pronoschallenge.classement.ClassementActivity");
				    	classementIntent.putExtra("fr.pronoschallenge.classement.ClassementType", classementTypes[typeClassement]);
				    	classementIntent.putExtra("fr.pronoschallenge.classement.ClassementTitle", classementItems[typeClassement]);
		    			v.getContext().startActivity(classementIntent);						
					}
				});
            	
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

    	// Clic sur un pseudo
        	pseudoTextView.setText(statEntry.getPseudo());
        	final String pseudo = statEntry.getPseudo();
        	pseudoTextView.setOnClickListener(new View.OnClickListener() {					
				public void onClick(View v) {						
	    			Bundle objetbunble = new Bundle();
					objetbunble.putString("pseudo", pseudo);
	    			Intent intent = new Intent(v.getContext(), ProfilPagedViewActivity.class);
	    			intent.putExtras(objetbunble);
	    			v.getContext().startActivity(intent);						
				}
			});        	
            	
        }

        return view;
    }
}
