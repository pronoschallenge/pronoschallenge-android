package fr.pronoschallenge.club;

import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import fr.pronoschallenge.R;

public class ClubCalendrierAdapter extends ArrayAdapter<ClubCalendrierEntry> {


	public ClubCalendrierAdapter(Context context, int textViewResourceId,
			List<ClubCalendrierEntry> objects) {
		super(context, textViewResourceId, objects);

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
        
        if (view == null)
        {
            LayoutInflater li = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = li.inflate(R.layout.club_calendrier_item, null); 
        }
        ClubCalendrierEntry clubCalendrierEntry = getItem(position);
        if (clubCalendrierEntry != null)
        {
            TextView clubCalendrierJournee = (TextView)view.findViewById(R.id.clubCalendrierJournee);
            clubCalendrierJournee.setText(String.valueOf(clubCalendrierEntry.getNumJournee()));

            TextView clubCalendrierDate = (TextView)view.findViewById(R.id.clubCalendrierDate);
            SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yy");
            clubCalendrierDate.setText(formater.format(clubCalendrierEntry.getDateMatch()));

            TextView clubCalendrierResultat = (TextView)view.findViewById(R.id.clubCalendrierResultat);
            if (clubCalendrierEntry.getButDom().compareTo("null") == 0) {
            	clubCalendrierResultat.setText("-");
            	clubCalendrierResultat.setTextColor(Color.WHITE);
            } else {
                clubCalendrierResultat.setText(clubCalendrierEntry.getButDom() + "-" + clubCalendrierEntry.getButExt());               
                if (clubCalendrierEntry.getTypeResultat().compareTo("D") == 0) {
                	clubCalendrierResultat.setTextColor(Color.rgb(255, 64, 64));
                } else if (clubCalendrierEntry.getTypeResultat().compareTo("V") == 0) {
                	clubCalendrierResultat.setTextColor(Color.rgb(50, 205, 50));
                } else {
                	clubCalendrierResultat.setTextColor(Color.rgb(255, 185, 15));
                }            	
            }
            
            TextView clubCalendrierDom = (TextView)view.findViewById(R.id.clubCalendrierDom);
            clubCalendrierDom.setText(clubCalendrierEntry.getNomClubDom());
            
            TextView clubCalendrierExt = (TextView)view.findViewById(R.id.clubCalendrierExt);
            clubCalendrierExt.setText(clubCalendrierEntry.getNomClubExt());
            
        }
        
     return view;

	}

}
