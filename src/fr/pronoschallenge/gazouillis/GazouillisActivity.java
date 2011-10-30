package fr.pronoschallenge.gazouillis;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import fr.pronoschallenge.R;
import fr.pronoschallenge.rest.QueryBuilder;
import fr.pronoschallenge.rest.RestClient;
import fr.pronoschallenge.util.NetworkUtil;
import greendroid.app.GDActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GazouillisActivity extends GDActivity {

	private ListView gazouillisListView;
    private TextView messageTextView;
    private Button plusButton;

    private AlertDialog dialog;

    private int debut = 0;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.gazouillis);
		
		// Obtain handles to UI objects
		gazouillisListView = (ListView) findViewById(R.id.gazouillisList);
        messageTextView = (TextView) findViewById(R.id.gazouilliMessage);

        plusButton = new Button(this);
        plusButton.setText(R.string.button_gazouillis_plus);
        plusButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                fetchMoreGazouillis(v);
            }
        });

        gazouillisListView.addFooterView(plusButton);
	}
	
	
	@Override
	protected void onStart() {
		if(NetworkUtil.isConnected(this.getApplicationContext())) {
            setTitle(getString(R.string.title_gazouillis));

            AsyncTask task = new GazouillisTask(this).execute(String.valueOf(debut));
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Connexion Internet indisponible")
                .setPositiveButton("Fermer", new DialogInterface.OnClickListener() {
                                       public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            finish();
                                       }
                                   });
            dialog = builder.create();
            dialog.show();
        }

		super.onStart();
	}

    public void fetchMoreGazouillis(View view) {
        debut += 10;

        AsyncTask task = new GazouillisTask(this).execute(String.valueOf(debut));
    }

    /**
     * Get the gazouillis by calling the REST service
     * @param debut
     * @return
     */
	private List<GazouillisEntry> getGazouillis(String debut) {
		List<GazouillisEntry> gazouillisEntries = new ArrayList<GazouillisEntry>();

		String strGazouillis = RestClient.get(new QueryBuilder(this.getAssets(), "/rest/gazouillis/" + debut + "/10").getUri());

		try {
			// A Simple JSONObject Creation
	        JSONObject json = new JSONObject(strGazouillis);

	        // A Simple JSONObject Parsing
	        JSONArray gazouillisArray = json.getJSONArray("gazouillis");
	        for(int i=0;i<gazouillisArray.length();i++)
	        {
	        	JSONObject jsonGazouilliEntry = gazouillisArray.getJSONObject(i);

	        	GazouillisEntry gazouillisEntry = new GazouillisEntry();
	        	gazouillisEntry.setContenu(jsonGazouilliEntry.getString("contenu"));
                gazouillisEntry.setUrlAvatar(jsonGazouilliEntry.getString("url_avatar"));
                gazouillisEntry.setIdMembre(jsonGazouilliEntry.getString("id_membre"));
                gazouillisEntry.setPseudo(jsonGazouilliEntry.getString("pseudo"));

                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    gazouillisEntry.setDate(dateFormat.parse(jsonGazouilliEntry.getString("date")));
                } catch (ParseException pe) {
                    pe.printStackTrace();
                }

                gazouillisEntries.add(gazouillisEntry);
	        }

		} catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

		return gazouillisEntries;
	}

    private class GazouillisTask extends AsyncTask<String, Void, Boolean> {

        final private GazouillisActivity activity;
        private List<GazouillisEntry> gazouillisEntries;
        private ProgressDialog dialog;

        private GazouillisTask(GazouillisActivity activity) {
            this.activity = activity;
            dialog = new ProgressDialog(activity);
        }

        protected void onPreExecute() {
            this.dialog.setMessage("Chargement");
            this.dialog.show();
        }

        @Override
        protected Boolean doInBackground(final String... args) {

            gazouillisEntries = activity.getGazouillis(args[0]);

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if(gazouillisEntries.size() > 0) {
                // Récupération de l'adapter
                // Une fois qu'un header ou un footer a été ajouté à la liste,
                // l'adpater d'origine est wrappé par un HeaderViewListAdapter.
                // Il faut donc différencier les 2 cas.
                GazouillisAdapter adapter = null;
                if(gazouillisListView.getAdapter() instanceof HeaderViewListAdapter) {
                    adapter = (GazouillisAdapter) ((HeaderViewListAdapter) gazouillisListView.getAdapter()).getWrappedAdapter();
                } else {
                    adapter = (GazouillisAdapter) gazouillisListView.getAdapter();
                }
                if(adapter == null) {
                    adapter = new GazouillisAdapter(activity, R.layout.gazouillis_item, gazouillisEntries);
                    gazouillisListView.setAdapter(adapter);
                    activity.messageTextView.setVisibility(View.GONE);
                    activity.gazouillisListView.setVisibility(View.VISIBLE);
                    activity.plusButton.setVisibility(View.VISIBLE);
                    //gazouillisListView.addFooterView(plusButton);
                } else {
                    for(GazouillisEntry gazouilli : gazouillisEntries) {
                        adapter.add(gazouilli);
                    }
                }
                adapter.notifyDataSetChanged();
            } else {
                activity.gazouillisListView.setVisibility(View.GONE);
                activity.plusButton.setVisibility(View.GONE);
                activity.messageTextView.setText("Gazouillis non disponible");
                activity.messageTextView.setVisibility(View.VISIBLE);
            }
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            super.onPostExecute(success);
        }
    }
}