package fr.pronoschallenge.stat.saison;

import fr.pronoschallenge.R;
import fr.pronoschallenge.auth.LoginActivity;
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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


public class StatSaisonActivity extends GDActivity {
	
	public static final String TYPE_HISTO = "0";
	public static final String TYPE_DERN_JOUR = "1";
	
	private ListView statEvolTopListView;
	private ListView statEvolFlopListView;
	private ListView statPointTopListView;
	private ListView statPointFlopListView;
	private ListView statSerieListView;
	
    private AlertDialog dialog;
    private String typeStat;

    
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

        String userName = PreferenceManager.getDefaultSharedPreferences(this).getString("username", null);
        String password = PreferenceManager.getDefaultSharedPreferences(this).getString("password", null);
        if(userName == null || password == null) {
            startActivityForResult(new Intent(this, LoginActivity.class), 1);
        }
        typeStat = TYPE_DERN_JOUR;

		setActionBarContentView(R.layout.stat_saison);
    	
		// Obtain handles to UI objects
        statEvolTopListView = (ListView) findViewById(R.id.statEvolTopList);
        statEvolFlopListView = (ListView) findViewById(R.id.statEvolFlopList);
        statPointTopListView = (ListView) findViewById(R.id.statPointTopList);
        statPointFlopListView = (ListView) findViewById(R.id.statPointFlopList);
        statSerieListView = (ListView) findViewById(R.id.statSerieList);

	}
	
	
	@Override
	protected void onStart() {
		if(NetworkUtil.isConnected(this.getApplicationContext())) {
			String title;
			title = getString(R.string.title_stat_saison) + " - ";
			if (typeStat.compareTo(TYPE_DERN_JOUR) == 0) {
				title += "Dern. Journée";
			} else {
				title += "Saison";
			}				
			setTitle(title);
            new StatTask(this).execute(typeStat);
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

	
	// Afficher la répartition des pronostiques et des résultats
	private void afficheRepartition(StatSaisonEntry statSaisonPronos, StatSaisonEntry statSaisonResultats) {
    	double nbMatchJoue;
    	double nbMatchG, nbMatchN, nbMatchP;

    	LinearLayout statSaisonRepartLayout = (LinearLayout) findViewById(R.id.statSaisonRepartLayout);
    	TextView statSaisonRepartTitre = (TextView) findViewById(R.id.statSaisonRepartTitre);
    	
    	TextView statSaisonPronoDom = (TextView) findViewById(R.id.statSaisonPronoDom);
    	TextView statSaisonPronoNul = (TextView) findViewById(R.id.statSaisonPronoNul);
    	TextView statSaisonPronoExt = (TextView) findViewById(R.id.statSaisonPronoExt);

    	TextView statSaisonResultDom = (TextView) findViewById(R.id.statSaisonResultDom);
    	TextView statSaisonResultNul = (TextView) findViewById(R.id.statSaisonResultNul);
    	TextView statSaisonResultExt = (TextView) findViewById(R.id.statSaisonResultExt);
    	
    	android.view.Display display = ((android.view.WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
    	int intLargeur = display.getWidth() - 10;        	
    	
    	
    	// Répartition des résultats
    	nbMatchG = 0;
    	nbMatchN = 0;
    	nbMatchP = 0;
    	
    	for (StatSaisonDetailEntry statRepart : statSaisonResultats.getStatDetail()) {
			if (statRepart.getPseudo().compareTo("1") == 0) {
				nbMatchG = Double.parseDouble(statRepart.getResultat());
			} else if (statRepart.getPseudo().compareTo("N") == 0) {
				nbMatchN = Double.parseDouble(statRepart.getResultat());
			} else if (statRepart.getPseudo().compareTo("2") == 0) {
				nbMatchP = Double.parseDouble(statRepart.getResultat());
			}
		}
    	
    	nbMatchJoue = nbMatchG + nbMatchN + nbMatchP;
    	
        if(nbMatchJoue > 0) {
        	statSaisonResultDom.setWidth((int) ((nbMatchG / nbMatchJoue) * intLargeur));
        	statSaisonResultNul.setWidth((int) ((nbMatchN / nbMatchJoue) * intLargeur));
        	statSaisonResultExt.setWidth((int) ((nbMatchP / nbMatchJoue) * intLargeur));
        	statSaisonRepartTitre.setText("Pronos / Résultats (" + String.valueOf((int) nbMatchJoue) + " m)");
        }

    	
    	// Répartition des pronostics
    	nbMatchG = 0;
    	nbMatchN = 0;
    	nbMatchP = 0;

    	for (StatSaisonDetailEntry statRepart : statSaisonPronos.getStatDetail()) {
			if (statRepart.getPseudo().compareTo("1") == 0) {
				nbMatchG = Double.parseDouble(statRepart.getResultat());
			} else if (statRepart.getPseudo().compareTo("N") == 0) {
				nbMatchN = Double.parseDouble(statRepart.getResultat());
			} else if (statRepart.getPseudo().compareTo("2") == 0) {
				nbMatchP = Double.parseDouble(statRepart.getResultat());
			}
		}
    	
    	nbMatchJoue = nbMatchG + nbMatchN + nbMatchP;
    	
        if(nbMatchJoue > 0) {
        	statSaisonPronoDom.setWidth((int) ((nbMatchG / nbMatchJoue) * intLargeur));
        	statSaisonPronoNul.setWidth((int) ((nbMatchN / nbMatchJoue) * intLargeur));
        	statSaisonPronoExt.setWidth((int) ((nbMatchP / nbMatchJoue) * intLargeur));
        	statSaisonRepartLayout.setVisibility(View.VISIBLE);
        }        
        
	}
	
	
	private List<StatSaisonEntry> getStat(String typeStat) {

		List<StatSaisonEntry> statSaisonList = new ArrayList<StatSaisonEntry>();
		String strStat = RestClient.get(new QueryBuilder(this.getAssets(), "/rest/statistique/" + typeStat).getUri());
		
		try {
			// A Simple JSONObject Creation
	        JSONObject json = new JSONObject(strStat);

	        // A Simple JSONObject Parsing
	        JSONArray statArray = json.getJSONArray("statistique");
	        for (int i = 0; i < statArray.length(); i++)
	        {
	        	JSONObject jsonStatEntry = statArray.getJSONObject(i);

	        	StatSaisonEntry statSaisonEntry = new StatSaisonEntry();
	        	statSaisonEntry.setType(jsonStatEntry.getString("stat"));
	        	// Recherche des championnats liÃ©s Ã  la saison
	        	while (jsonStatEntry.getString("stat").equals(statSaisonEntry.getType()) && i < statArray.length()) {
	        		StatSaisonDetailEntry statDetail = new StatSaisonDetailEntry();
	        		if (jsonStatEntry.getString("pseudo").compareTo("null") == 0) {
	        			statDetail.setClassement("");
	                    statDetail.setPseudo("");
	                    statDetail.setResultat("");
	                    statDetail.setJournee("");	        			
	        		} else {
	        			statDetail.setClassement(jsonStatEntry.getString("quoi"));
	                    statDetail.setPseudo(jsonStatEntry.getString("pseudo"));
	                    statDetail.setResultat(jsonStatEntry.getString("result"));
	                    statDetail.setJournee(jsonStatEntry.getString("quand"));	        			
	        		}
                    statSaisonEntry.setStatDetail(statDetail);
                    // EntitÃ© JSON suivante
                    i++;
                    if (i < statArray.length()) {
                    	jsonStatEntry = statArray.getJSONObject(i);
                    }
	        	}
	        	// Repositionnement sur l'entitÃ© JSON adÃ©quate
	        	if (i < statArray.length()) {
	        		i--;
	        	}
	        	statSaisonList.add(statSaisonEntry);
	        }

		} catch (JSONException e) {
            e.printStackTrace();
        }

		return statSaisonList;
	}


    private class StatTask extends AsyncTask<String, Void, Boolean> {

        final private StatSaisonActivity activity;
        private ProgressDialog dialog;
        private StatSaisonEntry statEvolTop;
        private StatSaisonEntry statEvolFlop;
        private StatSaisonEntry statPointTop;
        private StatSaisonEntry statPointFlop;
        private StatSaisonEntry statSerie;
        private StatSaisonEntry statRepartPronos;
        private StatSaisonEntry statRepartResultats;

        private StatTask(StatSaisonActivity activity) {
            this.activity = activity;
            dialog = new ProgressDialog(activity);
        }

        protected void onPreExecute() {
            this.dialog.setMessage("Chargement");
            this.dialog.show();
        }

        @Override
        protected Boolean doInBackground(final String... args) {
        	List<StatSaisonEntry> statEntries = activity.getStat(args[0]);
        	statEvolTop = statEntries.get(0);
        	statEvolFlop = statEntries.get(1);
        	statPointTop = statEntries.get(2);
        	statPointFlop = statEntries.get(3);
        	statSerie = statEntries.get(4);
        	for (StatSaisonDetailEntry statSaisonEntry : statEntries.get(5).getStatDetail()) {
        		statSerie.setStatDetail(statSaisonEntry);
			}
        	statRepartPronos = statEntries.get(6);
        	statRepartResultats = statEntries.get(7);
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
        	// Message liste vide
            if (! statEvolTop.getStatDetail().isEmpty()) {
	            StatSaisonAdapter adapterEvolTop = new StatSaisonAdapter(activity,	R.layout.stat_saison_item, statEvolTop.getStatDetail(), true, false, false);
	            statEvolTopListView.setAdapter(adapterEvolTop);
	            adapterEvolTop.notifyDataSetChanged();	            
	            StatSaisonAdapter adapterEvolFlop = new StatSaisonAdapter(activity, R.layout.stat_saison_item, statEvolFlop.getStatDetail(), false, false, false);
	            statEvolFlopListView.setAdapter(adapterEvolFlop);
	            adapterEvolFlop.notifyDataSetChanged();	            
	            StatSaisonAdapter adapterPointTop = new StatSaisonAdapter(activity,	R.layout.stat_saison_item, statPointTop.getStatDetail(), true, false, false);
	            statPointTopListView.setAdapter(adapterPointTop);
	            adapterPointTop.notifyDataSetChanged();	            
	            StatSaisonAdapter adapterPointFlop = new StatSaisonAdapter(activity, R.layout.stat_saison_item, statPointFlop.getStatDetail(), false, false, false);
	            statPointFlopListView.setAdapter(adapterPointFlop);
	            adapterPointFlop.notifyDataSetChanged();	            
	            StatSaisonAdapter adapterSerie = new StatSaisonAdapter(activity, R.layout.stat_saison_item, statSerie.getStatDetail(), false, false, true);
	            statSerieListView.setAdapter(adapterSerie);
	            adapterSerie.notifyDataSetChanged();
	            afficheRepartition(statRepartPronos, statRepartResultats);
            }

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            super.onPostExecute(success);
        }
        	
    }
}