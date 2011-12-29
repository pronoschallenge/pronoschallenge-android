package fr.pronoschallenge.stat.match;

import fr.pronoschallenge.R;
import fr.pronoschallenge.classement.club.ClassementClubEntry;
import fr.pronoschallenge.rest.QueryBuilder;
import fr.pronoschallenge.rest.RestClient;
import fr.pronoschallenge.util.NetworkUtil;
import greendroid.app.GDActivity;
import greendroid.widget.AsyncImageView;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

public class StatMatchActivity extends GDActivity {

	private String nomClubDomicile;
	private String nomClubExterieur;
	private int intIdMatch;
	
	private AsyncImageView statMatchLogoDomAsyncImageView;
	private TextView statMatchEquipeDomTextView;
	private TextView statMatchPlaceDomTextView;
	private ListView statMatchSerieListViewDom;
	
	private AsyncImageView statMatchLogoExtAsyncImageView;	
	private TextView statMatchEquipeExtTextView;
	private TextView statMatchPlaceExtTextView;
	private ListView statMatchSerieListViewExt;
	
	private TextView messageStatMatchSerieTextView;
	
	//private AsyncImageView statMatchLogoCoteDomAsyncImageView;
	private TextView statMatchCoteDomTextView;
	
	//private AsyncImageView statMatchLogoCoteNulAsyncImageView;
	private TextView statMatchCoteNulTextView;
	
	//private AsyncImageView statMatchLogoCoteExtAsyncImageView;
	private TextView statMatchCoteExtTextView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setActionBarContentView(R.layout.stat_match);
    	
    	//On r√©cup√®re l'objet Bundle envoy√© par l'autre Activity
        Bundle objetbunble  = this.getIntent().getExtras();
        nomClubDomicile = (String) objetbunble.get("clubDomicile");
        nomClubExterieur = (String) objetbunble.get("clubExterieur");
        String strIdMatch = (String) objetbunble.get("idMatch");
        intIdMatch = Integer.parseInt(strIdMatch);
        
		// Obtain handles to UI objects
        statMatchLogoDomAsyncImageView = (AsyncImageView) findViewById(R.id.statMatchLogoDom);
        statMatchEquipeDomTextView = (TextView) findViewById(R.id.statMatchEquipeDom);
        statMatchPlaceDomTextView = (TextView) findViewById(R.id.statMatchPlaceDom);
		statMatchSerieListViewDom = (ListView) findViewById(R.id.statMatchSerieListDom);
		
        statMatchLogoExtAsyncImageView = (AsyncImageView) findViewById(R.id.statMatchLogoExt);
        statMatchEquipeExtTextView = (TextView) findViewById(R.id.statMatchEquipeExt);
        statMatchPlaceExtTextView = (TextView) findViewById(R.id.statMatchPlaceExt);
		statMatchSerieListViewExt = (ListView) findViewById(R.id.statMatchSerieListExt);
		
        messageStatMatchSerieTextView = (TextView) findViewById(R.id.statMatchSerieMessage);
        
        //statMatchLogoCoteDomAsyncImageView = (AsyncImageView) findViewById(R.id.statMatchLogoCoteDom);
        statMatchCoteDomTextView = (TextView) findViewById(R.id.statMatchCoteDom);
        
        //statMatchLogoCoteNulAsyncImageView = (AsyncImageView) findViewById(R.id.statMatchLogoCoteNul);
        statMatchCoteNulTextView = (TextView) findViewById(R.id.statMatchCoteNul);
        
        //statMatchLogoCoteExtAsyncImageView = (AsyncImageView) findViewById(R.id.statMatchLogoCoteExt);
        statMatchCoteExtTextView = (TextView) findViewById(R.id.statMatchCoteExt);

		if(NetworkUtil.isConnected(this.getApplicationContext())) {
            setTitle(getString(R.string.title_stat));
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
		
		new InfoClubTask(this).execute("");
		new CoteMatchTask(this).execute("");
		new StatMatchTask(this).execute("");
	}

	
	// SÈrie en cours d'un club
	private List<StatMatchSerieEntry> getMatchSerie(String nomClub) {
		List<StatMatchSerieEntry> statMatchSerieEntries = new ArrayList<StatMatchSerieEntry>();

		String strMatchSerie = RestClient.get(new QueryBuilder(this.getAssets(), "/rest/serieClub/" + nomClub + "/?nbMatch=6").getUri());

		try {
			// A Simple JSONObject Creation
	        JSONObject json = new JSONObject(strMatchSerie);

	        // A Simple JSONObject Parsing
	        JSONArray matchSerieArray = json.getJSONArray("serieClub");
	        for(int i = 0; i < matchSerieArray.length(); i++) {
	        	JSONObject jsonSerieEntry = matchSerieArray.getJSONObject(i);

	        	StatMatchSerieEntry statMatchSerieEntry = new StatMatchSerieEntry();	        	
	        	statMatchSerieEntry.setButDom(jsonSerieEntry.getInt("butDom"));
	        	statMatchSerieEntry.setButExt(jsonSerieEntry.getInt("butExt"));
	        	statMatchSerieEntry.setMatchDomExt(jsonSerieEntry.getString("type"));
        		statMatchSerieEntry.setNomClubDom(jsonSerieEntry.getString("clubDom"));
	        	statMatchSerieEntry.setNomClubExt(jsonSerieEntry.getString("clubExt"));
	        	if (statMatchSerieEntry.getMatchDomExt().compareTo("D") == 0) {
	        		if (statMatchSerieEntry.getButDom() == statMatchSerieEntry.getButExt()) { 
	        			statMatchSerieEntry.setTypeResultat("N");
	        		} else if (statMatchSerieEntry.getButDom() > statMatchSerieEntry.getButExt()) {
	        			statMatchSerieEntry.setTypeResultat("V");
	        		} else {
	        			statMatchSerieEntry.setTypeResultat("D");
	        		}
	        	} else {
	        		if (statMatchSerieEntry.getButDom() == statMatchSerieEntry.getButExt()) { 
	        			statMatchSerieEntry.setTypeResultat("N");
	        		} else if (statMatchSerieEntry.getButDom() > statMatchSerieEntry.getButExt()) {
	        			statMatchSerieEntry.setTypeResultat("D");
	        		} else {
	        			statMatchSerieEntry.setTypeResultat("V");
	        		}
	        	}
	        	
	        	statMatchSerieEntries.add(statMatchSerieEntry);
	        }

		} catch (JSONException e) {
            e.printStackTrace();
        }

		return statMatchSerieEntries;
	}

	
	// Cote d'un match
	private List<CoteMatchEntry> getCoteMatch(int idMatch) {
		List<CoteMatchEntry> coteMatchEntries = new ArrayList<CoteMatchEntry>();

		String strCoteMatch = RestClient.get(new QueryBuilder(this.getAssets(), "/rest/coteMatch/" + String.valueOf(idMatch) + "/").getUri());

		try {
			// A Simple JSONObject Creation
	        JSONObject json = new JSONObject(strCoteMatch);

	        // A Simple JSONObject Parsing
	        JSONArray coteMatchArray = json.getJSONArray("coteMatch");
	        for(int i = 0; i < coteMatchArray.length(); i++) {
	        	JSONObject jsonCoteMatchEntry = coteMatchArray.getJSONObject(i);

	        	CoteMatchEntry coteMatchEntry = new CoteMatchEntry();	        	
	        	coteMatchEntry.setTypeMatch(jsonCoteMatchEntry.getString("type"));
	        	coteMatchEntry.setCote(jsonCoteMatchEntry.getInt("cote"));
	        	coteMatchEntry.setUrlLogo(jsonCoteMatchEntry.getString("url_logo"));
	        	
	        	coteMatchEntries.add(coteMatchEntry);
	        }

		} catch (JSONException e) {
            e.printStackTrace();
        }

		return coteMatchEntries;
	}

	
	// Information d'un club
	private ClassementClubEntry getInfoClub(String nomClub) {
		
		ClassementClubEntry infoClubEntry = new ClassementClubEntry(); 

		String strInfoClub = RestClient.get(new QueryBuilder(this.getAssets(), "/rest/infoClub/" + nomClub + "/").getUri());

		try {
			// A Simple JSONObject Creation
	        JSONObject json = new JSONObject(strInfoClub);

	        // A Simple JSONObject Parsing
	        JSONArray matchSerieArray = json.getJSONArray("infoClub");
	        for(int i=0;i<matchSerieArray.length();i++) {
	        	JSONObject jsonInfoClubEntry = matchSerieArray.getJSONObject(i);

	        	infoClubEntry.setClub(jsonInfoClubEntry.getString("club"));
	        	infoClubEntry.setPlace(jsonInfoClubEntry.getInt("place"));
	        	infoClubEntry.setUrlLogo(jsonInfoClubEntry.getString("url_logo"));
	        }

		} catch (JSONException e) {
            e.printStackTrace();
        }

		return infoClubEntry;
	}
	
	
	
	// Derniers matchs effectuÈs par les 2 clubs
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

            statMatchSerieEntriesDom = getMatchSerie(nomClubDomicile);
            statMatchSerieEntriesExt = getMatchSerie(nomClubExterieur);

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
        	
            if(statMatchSerieEntriesDom.size() > 0) {
                StatMatchSerieAdapter adapter = new StatMatchSerieAdapter(activity,	R.layout.stat_match_serie_item, statMatchSerieEntriesDom);
                statMatchSerieListViewDom.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                messageStatMatchSerieTextView.setVisibility(View.GONE);
                statMatchSerieListViewDom.setVisibility(View.VISIBLE);
            }
            
            if(statMatchSerieEntriesExt.size() > 0) {
                StatMatchSerieAdapter adapter = new StatMatchSerieAdapter(activity,	R.layout.stat_match_serie_item, statMatchSerieEntriesExt);
                statMatchSerieListViewExt.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                activity.messageStatMatchSerieTextView.setVisibility(View.GONE);
                activity.statMatchSerieListViewExt.setVisibility(View.VISIBLE);
            }
            
            if(statMatchSerieEntriesDom.size() == 0 && statMatchSerieEntriesExt.size() == 0) {
                statMatchSerieListViewDom.setVisibility(View.GONE);
                statMatchSerieListViewExt.setVisibility(View.GONE);
                messageStatMatchSerieTextView.setText("SÈrie non disponible");
                messageStatMatchSerieTextView.setVisibility(View.VISIBLE);
            }
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            super.onPostExecute(success);
        }
    }	

	
	// Cotes du match
    private class CoteMatchTask extends AsyncTask<String, Void, Boolean> {

        private List<CoteMatchEntry> coteMatchEntries;
        private ProgressDialog dialog;

        private CoteMatchTask(StatMatchActivity activity) {
            dialog = new ProgressDialog(activity);
        }

        protected void onPreExecute() {
            this.dialog.setMessage("Chargement");
            this.dialog.show();
        }

        @Override
        protected Boolean doInBackground(final String... args) {

            coteMatchEntries = getCoteMatch(intIdMatch);

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
        	
        	int tabcote[] = new int [3];
        	
	        for(CoteMatchEntry coteMatchEntry : coteMatchEntries) {
	        	if (coteMatchEntry.getTypeMatch().compareTo("1") == 0) {
                	//statMatchLogoCoteDomAsyncImageView.setUrl(coteMatchEntry.getUrlLogo());
                	statMatchCoteDomTextView.setText(String.valueOf(coteMatchEntry.getCote()));
                	tabcote[0] = coteMatchEntry.getCote();
	        	} else if (coteMatchEntry.getTypeMatch().compareTo("N") == 0) {
                	//statMatchLogoCoteNulAsyncImageView.setUrl(coteMatchEntry.getUrlLogo());
                	statMatchCoteNulTextView.setText(String.valueOf(coteMatchEntry.getCote()));
                	tabcote[1] = coteMatchEntry.getCote();
	        	} else if (coteMatchEntry.getTypeMatch().compareTo("2") == 0) {
                	//statMatchLogoCoteExtAsyncImageView.setUrl(coteMatchEntry.getUrlLogo());
                	statMatchCoteExtTextView.setText(String.valueOf(coteMatchEntry.getCote()));
                	tabcote[2] = coteMatchEntry.getCote();
	        	}
	        }
	        
	        // Affectations des couleurs de fond pour les cotes
	        if (tabcote[0] >= tabcote[1] && tabcote[0] >= tabcote[2]) {
	        	statMatchCoteDomTextView.setBackgroundColor(Color.RED);
	        }
	        if (tabcote[1] >= tabcote[0] && tabcote[1] >= tabcote[2]) {
	        	statMatchCoteNulTextView.setBackgroundColor(Color.RED);
	        }
	        if (tabcote[2] >= tabcote[0] && tabcote[2] >= tabcote[1]) {
	        	statMatchCoteExtTextView.setBackgroundColor(Color.RED);
	        }

	        if (tabcote[0] <= tabcote[1] && tabcote[0] <= tabcote[2]) {
	        	statMatchCoteDomTextView.setBackgroundColor(Color.GREEN);
	        	statMatchCoteDomTextView.setTextColor(Color.BLACK);
	        } 
	        if (tabcote[1] <= tabcote[0] && tabcote[1] <= tabcote[2]) {
	        	statMatchCoteNulTextView.setBackgroundColor(Color.GREEN);
	        	statMatchCoteNulTextView.setTextColor(Color.BLACK);
	        } 
	        if (tabcote[2] <= tabcote[0] && tabcote[2] <= tabcote[1]) {
	        	statMatchCoteExtTextView.setBackgroundColor(Color.GREEN);
	        	statMatchCoteExtTextView.setTextColor(Color.BLACK);
	        }
	        
	        
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            super.onPostExecute(success);
        }
    }	
    
    

    
    // Informations des 2 clubs
    private class InfoClubTask extends AsyncTask<String, Void, Boolean> {

        private ClassementClubEntry infoClubEntryDom;
        private ClassementClubEntry infoClubEntryExt;
        private ProgressDialog dialog;

        private InfoClubTask(StatMatchActivity activity) {
            dialog = new ProgressDialog(activity);
        }

        protected void onPreExecute() {
            this.dialog.setMessage("Chargement");
            this.dialog.show();
        }

        @Override
        protected Boolean doInBackground(final String... args) {

            infoClubEntryDom = getInfoClub(nomClubDomicile);
            infoClubEntryExt = getInfoClub(nomClubExterieur);

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
        	
        	statMatchLogoDomAsyncImageView.setUrl(infoClubEntryDom.getUrlLogo());
        	statMatchEquipeDomTextView.setText(infoClubEntryDom.getClub());
        	statMatchPlaceDomTextView.setText("(" + String.valueOf(infoClubEntryDom.getPlace()) + ")");
            
        	statMatchLogoExtAsyncImageView.setUrl(infoClubEntryExt.getUrlLogo());
        	statMatchEquipeExtTextView.setText(infoClubEntryExt.getClub());
        	statMatchPlaceExtTextView.setText("(" + String.valueOf(infoClubEntryExt.getPlace()) + ")");       	

        	if (dialog.isShowing()) {
                dialog.dismiss();
            }
            super.onPostExecute(success);
        }
    }

}
