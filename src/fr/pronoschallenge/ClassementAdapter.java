package fr.pronoschallenge;

import java.text.DecimalFormat;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ClassementAdapter extends ArrayAdapter<ClassementEntry> {

	public ClassementAdapter(Context context, int textViewResourceId,
			List<ClassementEntry> objects) {
		super(context, textViewResourceId, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
        
        if (view == null)
        {            
            LayoutInflater li = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //le layout représentant la ligne dans le listView
            view = li.inflate(R.layout.classement_item, null); 
        }         
        ClassementEntry classementEntry = getItem(position);
        if (classementEntry != null)
        {
            TextView classementEntryPlace = (TextView)view.findViewById(R.id.classementEntryPlace);  
            classementEntryPlace.setText(String.valueOf(classementEntry.getPlace()));
            
            TextView classementEntryPseudo = (TextView)view.findViewById(R.id.classementEntryPseudo);  
            classementEntryPseudo.setText(classementEntry.getPseudo());
            
            TextView classementEntryPoints = (TextView)view.findViewById(R.id.classementEntryPoints);  
            String points = null;
            if(((ClassementActivity)this.getContext()).getClassementType().equals("mixte"))
            {
            	points = String.valueOf(new DecimalFormat("0.00").format(classementEntry.getPoints()));
            }
            else
            {
            	points = String.valueOf((int)(classementEntry.getPoints()));
            }
            classementEntryPoints.setText(points);
        }
        
     return view;

	}

}
