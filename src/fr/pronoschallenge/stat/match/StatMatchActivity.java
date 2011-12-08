package fr.pronoschallenge.stat.match;

import fr.pronoschallenge.R;
import fr.pronoschallenge.rest.QueryBuilder;
import fr.pronoschallenge.rest.RestClient;
import fr.pronoschallenge.util.NetworkUtil;
import greendroid.app.GDActivity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class StatMatchActivity extends GDActivity {

	private String nomClubDomicile;
	private String nomClubExterieur;
	
	private ListView statMatchSerieListViewDom;
	private ListView statMatchSerieListViewExt;
	private TextView messageStatMatchSerieTextView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.stat_match);
    	
    	//On récupère l'objet Bundle envoyé par l'autre Activity
        Bundle objetbunble  = this.getIntent().getExtras();
        nomClubDomicile = (String) objetbunble.get("clubDomicile");
        nomClubExterieur = (String) objetbunble.get("clubExterieur");
        
		// Obtain handles to UI objects
		statMatchSerieListViewDom = (ListView) findViewById(R.id.statMatchSerieListDom);
		statMatchSerieListViewExt = (ListView) findViewById(R.id.statMatchSerieListExt);
        messageStatMatchSerieTextView = (TextView) findViewById(R.id.statMatchSerieMessage);

		if(NetworkUtil.isConnected(this.getApplicationContext())) {
            setTitle(getString(R.string.title_classement_club));
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Connexion Internet indisponible")
                .setPositiveButton("Fermer", new DialogInterface.OnClickListener() {
                                       public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            finish();
                                       }
                                   });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
		
		new StatMatchTask(this).execute("");
	}

	private List<StatMatchSerieEntry> getMatchSerie(String nomClub) {
		List<StatMatchSerieEntry> statMatchSerieEntries = new ArrayList<StatMatchSerieEntry>();

		String strMatchSerie = RestClient.get(new QueryBuilder(this.getAssets(), "/rest/serieClub/" + nomClub + "/").getUri());

		try {
			// A Simple JSONObject Creation
	        JSONObject json = new JSONObject(strMatchSerie);

	        // A Simple JSONObject Parsing
	        JSONArray matchSerieArray = json.getJSONArray("serieClub");
	        for(int i=0;i<matchSerieArray.length();i++) {
	        	JSONObject jsonSerieEntry = matchSerieArray.getJSONObject(i);

	        	StatMatchSerieEntry statMatchSerieEntry = new StatMatchSerieEntry();	        	
	        	statMatchSerieEntry.setButDom(jsonSerieEntry.getInt("butDom"));
	        	statMatchSerieEntry.setButExt(jsonSerieEntry.getInt("butExt"));
	        	if (nomClub.compareTo(jsonSerieEntry.getString("clubDom")) == 0) {
	        		statMatchSerieEntry.setNomClubAdverse(jsonSerieEntry.getString("clubDom"));
	        		statMatchSerieEntry.setMatchDomExt("D");
	        	} else {
	        		statMatchSerieEntry.setNomClubAdverse(jsonSerieEntry.getString("clubExt"));
	        		statMatchSerieEntry.setMatchDomExt("E");
	        	}
	        	
	        	statMatchSerieEntries.add(statMatchSerieEntry);
	        }

		} catch (JSONException e) {
            e.printStackTrace();
        }

		return statMatchSerieEntries;
	}

    private class StatMatchTask extends AsyncTask<String, Void, Boolean> {

        final private StatMatchActivity activity;
        private List<StatMatchSerieEntry> statMatchSerieEntriesDom;
        private List<StatMatchSerieEntry> statMatchSerieEntriesExt;
        private ProgressDialog dialog;

        private StatMatchTask(StatMatchActivity activity) {
            this.activity = activity;
            dialog = new ProgressDialog(activity);
        }

        protected void onPreExecute() {
            this.dialog.setMessage("Chargement");
            this.dialog.show();
        }

        @Override
        protected Boolean doInBackground(final String... args) {

            statMatchSerieEntriesDom = activity.getMatchSerie(nomClubDomicile);
            statMatchSerieEntriesExt = activity.getMatchSerie(nomClubExterieur);

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if(statMatchSerieEntriesDom.size() > 0) {
                StatMatchSerieAdapter adapter = new StatMatchSerieAdapter(activity,	R.layout.stat_match_serie_item, statMatchSerieEntriesDom);
                statMatchSerieListViewDom.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                activity.messageStatMatchSerieTextView.setVisibility(View.GONE);
                activity.statMatchSerieListViewDom.setVisibility(View.VISIBLE);
            }
            if(statMatchSerieEntriesExt.size() > 0) {
                StatMatchSerieAdapter adapter = new StatMatchSerieAdapter(activity,	R.layout.stat_match_serie_item, statMatchSerieEntriesExt);
                statMatchSerieListViewExt.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                activity.messageStatMatchSerieTextView.setVisibility(View.GONE);
                activity.statMatchSerieListViewExt.setVisibility(View.VISIBLE);
            }
            if(statMatchSerieEntriesDom.size() == 0 && statMatchSerieEntriesExt.size() == 0) {
                activity.statMatchSerieListViewDom.setVisibility(View.GONE);
                activity.statMatchSerieListViewExt.setVisibility(View.GONE);
                activity.messageStatMatchSerieTextView.setText("Serie non disponible");
                activity.messageStatMatchSerieTextView.setVisibility(View.VISIBLE);
            }
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            super.onPostExecute(success);
        }
    }
}
