package fr.pronoschallenge.gazouillis;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import fr.pronoschallenge.R;
import greendroid.widget.AsyncImageView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class GazouillisAdapter extends ArrayAdapter<GazouillisEntry> {

	public GazouillisAdapter(Context context, int textViewResourceId,
                             List<GazouillisEntry> objects) {
		super(context, textViewResourceId, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;

        if (view == null)
        {
            LayoutInflater li = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //le layout représentant la ligne dans le listView
            view = li.inflate(R.layout.gazouillis_item, null);
        }
        GazouillisEntry gazouillisEntry = getItem(position);
        if (gazouillisEntry != null)
        {
            // Contenu
            TextView gazouillisEntryContenuTextView = (TextView)view.findViewById(R.id.gazouilliEntryContenu);
            gazouillisEntryContenuTextView.setText(gazouillisEntry.getContenu());
            // Avatar
            AsyncImageView gazouillisEntryAvatarView = (AsyncImageView)view.findViewById(R.id.gazouilliEntryAvatar);
            gazouillisEntryAvatarView.setUrl(gazouillisEntry.getUrlAvatar());
            // Membre
            TextView gazouillisEntryMembreTextView = (TextView)view.findViewById(R.id.gazouilliEntryMembre);
            gazouillisEntryMembreTextView.setText(gazouillisEntry.getPseudo());
            // Date
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:MM");
            TextView gazouillisEntryDateTextView = (TextView)view.findViewById(R.id.gazouilliEntryDate);
            gazouillisEntryDateTextView.setText(dateFormat.format(gazouillisEntry.getDate()));
        }
        
     return view;

	}

}
