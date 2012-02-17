package fr.pronoschallenge.profil;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import fr.pronoschallenge.R;


public class ProfilStatAdapter extends ArrayAdapter<ProfilStatEntry> {

    public ProfilStatAdapter(Context context, int textViewResourceId,
                         List<ProfilStatEntry> objects) {
        super(context, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater li = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //le layout représentant la ligne dans le listView
            view = li.inflate(R.layout.profil_stat_item, null);
        }
        ProfilStatEntry profilStatEntry = getItem(position);
        
        if (profilStatEntry != null) {        	
            TextView profilStatType = (TextView) view.findViewById(R.id.profilStatType);
            profilStatType.setText(profilStatEntry.getTypeChamp());
            TextView profilStatPlace = (TextView) view.findViewById(R.id.profilStatPlace);
            profilStatPlace.setText(profilStatEntry.getNumPlace());            
            TextView profilStatPoints = (TextView) view.findViewById(R.id.profilStatPoints);
            profilStatPoints.setText(profilStatEntry.getNbPoints());            
            TextView profilStatEvolution = (TextView) view.findViewById(R.id.profilStatEvolution);            
            ImageView profilStatImage = (ImageView) view.findViewById(R.id.profilStatEvolutionImage);
            int evolution = Integer.parseInt(profilStatEntry.getEvolution());
            if (evolution < 0) {
            	profilStatImage.setImageResource(R.drawable.evol_flop);
            	profilStatEvolution.setText(profilStatEntry.getEvolution());
            } else if (evolution > 0) {
            	profilStatImage.setImageResource(R.drawable.evol_top);
            	profilStatEvolution.setText("+" + profilStatEntry.getEvolution());
            } else {
            	profilStatImage.setImageResource(R.drawable.evol_stable);
            	profilStatEvolution.setText("");
            }

        }

        return view;
    }
}
