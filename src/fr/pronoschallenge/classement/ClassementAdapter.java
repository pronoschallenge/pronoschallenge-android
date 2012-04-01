package fr.pronoschallenge.classement;

import java.text.DecimalFormat;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import fr.pronoschallenge.R;

public class ClassementAdapter extends ArrayAdapter<ClassementEntry> {

    public int pointsPrecedents;
    public double pointsMixtePrecedents;
    String mUser;

	public ClassementAdapter(Context context, int textViewResourceId,
			List<ClassementEntry> objects, String mUser) {
		super(context, textViewResourceId, objects);

		this.mUser = mUser;
        pointsPrecedents = 0;
        pointsMixtePrecedents = 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
        
        if (view == null) {
            LayoutInflater li = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //le layout représentant la ligne dans le listView
            view = li.inflate(R.layout.classement_item, null); 
        }
        ClassementEntry classementEntry = getItem(position);
        if (classementEntry != null) {
            TextView classementEntryPseudo = (TextView)view.findViewById(R.id.classementEntryPseudo);  
            classementEntryPseudo.setText(classementEntry.getPseudo());
            
            ImageView classementEntryEtoile = (ImageView)view.findViewById(R.id.classementEntryEtoile);
            classementEntryEtoile.setVisibility(View.VISIBLE);
            switch (classementEntry.getPlacePrec()) {
			case 1:
				classementEntryEtoile.setImageResource(R.drawable.etoile_1);
				break;
			case 2:
				classementEntryEtoile.setImageResource(R.drawable.etoile_2);
				break;
			case 3:
				classementEntryEtoile.setImageResource(R.drawable.etoile_3);
				break;
			default:
				classementEntryEtoile.setVisibility(View.GONE);
				break;
			}

            int place = classementEntry.getPlace();
            String placeAffichee = "";

            TextView classementEntryPoints = (TextView)view.findViewById(R.id.classementEntryPoints);  
            String pointsAffiches;

            double points = classementEntry.getPoints();
            if(points != pointsMixtePrecedents) {
                placeAffichee = String.valueOf(place);
                pointsMixtePrecedents = points;
            }

            if(((ClassementActivity)this.getContext()).getClassementType().equals("mixte")) {
                pointsAffiches = String.valueOf(new DecimalFormat("0.00").format(points));
            } else {
                pointsAffiches = String.valueOf((int)(points));
            }
            classementEntryPoints.setText(pointsAffiches);

            TextView classementEntryPlace = (TextView)view.findViewById(R.id.classementEntryPlace);
            classementEntryPlace.setText(placeAffichee);
            
            RelativeLayout classementLayout = (RelativeLayout)view.findViewById(R.id.classementRelativeLayout);
            if (classementEntry.getPseudo().toUpperCase().compareTo(mUser.toUpperCase()) == 0) {            	
            	classementLayout.setBackgroundColor(Color.WHITE);
            	classementEntryPseudo.setTextColor(Color.BLACK);
            	classementEntryPoints.setTextColor(Color.BLACK);
            	classementEntryPlace.setTextColor(Color.BLACK);
            } else {
            	classementLayout.setBackgroundColor(Color.BLACK);
            	classementEntryPseudo.setTextColor(Color.GRAY);
            	classementEntryPoints.setTextColor(Color.GRAY);
            	classementEntryPlace.setTextColor(Color.GRAY);            		
            }
            
        }
        
     return view;

	}

}
