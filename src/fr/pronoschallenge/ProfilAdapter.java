package fr.pronoschallenge;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.*;

public class ProfilAdapter extends ArrayAdapter<PalmaresEntry> {

    public ProfilAdapter(Context context, int textViewResourceId,
                         List<PalmaresEntry> objects) {
        super(context, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater li = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //le layout représentant la ligne dans le listView
            view = li.inflate(R.layout.profil_item, null);
        }
        PalmaresEntry palmaresEntry = getItem(position);
        
        if (palmaresEntry != null) {        	
            TextView palmaresSaison = (TextView) view.findViewById(R.id.profilSaison);
            palmaresSaison.setText(palmaresEntry.getNomSaison());
            palmaresSaison.setVisibility(View.VISIBLE);
            
            for (PalmaresDetailEntry palmaresDetailEntry : palmaresEntry.getPalmaresDetail()) {
            	if (palmaresDetailEntry.getTypeChamp().equals("general")) {
                    TextView palmaresClassementGeneral = (TextView) view.findViewById(R.id.profilChampGeneral);
                    palmaresClassementGeneral.setText(palmaresDetailEntry.getTypeChamp());
                    palmaresClassementGeneral.setVisibility(View.VISIBLE);
                    TextView palmaresPlaceGeneral = (TextView) view.findViewById(R.id.profilPlaceGeneral);
                    palmaresPlaceGeneral.setText(palmaresDetailEntry.getNumPlace());
                    palmaresPlaceGeneral.setVisibility(View.VISIBLE);            		
            	} else if (palmaresDetailEntry.getTypeChamp().equals("hourra")) {
                    TextView palmaresClassementHourra = (TextView) view.findViewById(R.id.profilChampHourra);
                    palmaresClassementHourra.setText(palmaresDetailEntry.getTypeChamp());
                    palmaresClassementHourra.setVisibility(View.VISIBLE);
                    TextView palmaresPlaceHourra = (TextView) view.findViewById(R.id.profilPlaceHourra);
                    palmaresPlaceHourra.setText(palmaresDetailEntry.getNumPlace());
                    palmaresPlaceHourra.setVisibility(View.VISIBLE);            		
            	}  else if (palmaresDetailEntry.getTypeChamp().equals("mixte")) {
                    TextView palmaresClassementMixte = (TextView) view.findViewById(R.id.profilChampMixte);
                    palmaresClassementMixte.setText(palmaresDetailEntry.getTypeChamp());
                    palmaresClassementMixte.setVisibility(View.VISIBLE);
                    TextView palmaresPlaceMixte = (TextView) view.findViewById(R.id.profilPlaceMixte);
                    palmaresPlaceMixte.setText(palmaresDetailEntry.getNumPlace());
                    palmaresPlaceMixte.setVisibility(View.VISIBLE);            		
            	}
            }
            	
        }

        return view;
    }
}
