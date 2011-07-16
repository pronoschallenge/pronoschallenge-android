package fr.pronoschallenge;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

public class PronosAdapter extends ArrayAdapter<PronoEntry> {

	public PronosAdapter(Context context, int textViewResourceId,
                         List<PronoEntry> objects) {
		super(context, textViewResourceId, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
        
        if (view == null)
        {            
            LayoutInflater li = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //le layout représentant la ligne dans le listView
            view = li.inflate(R.layout.pronos_item, null);
        }         
        PronoEntry pronoEntry = getItem(position);
        if (pronoEntry != null)
        {
            TextView pronoEntryClubDom = (TextView)view.findViewById(R.id.pronoEntryEquipeDom);
            pronoEntryClubDom.setText(pronoEntry.getEquipeDom());

            final Button buttonProno1 = (Button)view.findViewById(R.id.buttonProno1);
            final Button buttonPronoN = (Button)view.findViewById(R.id.buttonPronoN);
            final Button buttonProno2 = (Button)view.findViewById(R.id.buttonProno2);

            // si le match a déjà été pronostiqué, sélectionne le bouton correspondant
            String prono = pronoEntry.getProno();
            if(prono.equals("1")) {
                buttonProno1.setSelected(true);
            } else if(prono.equals("N")) {
                buttonPronoN.setSelected(true);
            } else if(prono.equals("2")) {
                buttonProno2.setSelected(true);
            }

            if(pronoEntry.getDate().before(new Date())) {
                buttonProno1.setEnabled(false);
                buttonPronoN.setEnabled(false);
                buttonProno2.setEnabled(false);

                buttonProno1.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View button) {
                        if (button.isSelected()){
                            button.setSelected(false);
                        } else {
                            button.setSelected(true);
                            buttonPronoN.setSelected(false);
                            buttonProno2.setSelected(false);
                        }
                    }

                });

                 buttonPronoN.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View button) {
                        if (button.isSelected()){
                            button.setSelected(false);
                        } else {
                            button.setSelected(true);
                            buttonProno1.setSelected(false);
                            buttonProno2.setSelected(false);
                        }
                    }

                });

                buttonProno2.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View button) {
                        if (button.isSelected()){
                            button.setSelected(false);
                        } else {
                            button.setSelected(true);
                            buttonProno1.setSelected(false);
                            buttonPronoN.setSelected(false);
                        }
                    }

                });
            }

            TextView pronoEntryClubExt = (TextView)view.findViewById(R.id.pronoEntryEquipeExt);
            pronoEntryClubExt.setText(pronoEntry.getEquipeExt());
        }
        
        return view;

	}

}
