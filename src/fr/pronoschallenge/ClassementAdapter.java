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

    public int pointsPrecedents;
    public double pointsMixtePrecedents;

	public ClassementAdapter(Context context, int textViewResourceId,
			List<ClassementEntry> objects) {
		super(context, textViewResourceId, objects);

        pointsPrecedents = 0;
        pointsMixtePrecedents = 0;
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
            TextView classementEntryPseudo = (TextView)view.findViewById(R.id.classementEntryPseudo);  
            classementEntryPseudo.setText(classementEntry.getPseudo());

            int place = classementEntry.getPlace();
            String placeAffichee = "";

            TextView classementEntryPoints = (TextView)view.findViewById(R.id.classementEntryPoints);  
            String pointsAffiches = null;

            double points = classementEntry.getPoints();
            if(points != pointsMixtePrecedents)
            {
                placeAffichee = String.valueOf(place);
                pointsMixtePrecedents = points;
            }

            if(((ClassementActivity)this.getContext()).getClassementType().equals("mixte"))
            {

                pointsAffiches = String.valueOf(new DecimalFormat("0.00").format(points));
            }
            else
            {
                pointsAffiches = String.valueOf((int)(points));
            }
            classementEntryPoints.setText(pointsAffiches);

            TextView classementEntryPlace = (TextView)view.findViewById(R.id.classementEntryPlace);
            classementEntryPlace.setText(placeAffichee);
        }
        
     return view;

	}

}
