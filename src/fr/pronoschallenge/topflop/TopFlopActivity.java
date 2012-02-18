package fr.pronoschallenge.topflop;

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
import android.view.Window;
import android.widget.ListView;

public class TopFlopActivity extends GDActivity {

	private final static String CLASSEMENT_MIXTE = "mixte";
	private final static String CLASSEMENT_GENERAL = "general";
	private final static String CLASSEMENT_HOURRA = "hourra";
	private final static String EVOLUTION_TOP = "0";
	private final static String EVOLUTION_FLOP = "1";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setActionBarContentView(R.layout.top_flop);
        
		if(NetworkUtil.isConnected(this.getApplicationContext())) {
            setTitle(getString(R.string.title_top_flop));
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
		
		new TopFlopTask(this, CLASSEMENT_MIXTE).execute("");
		new TopFlopTask(this, CLASSEMENT_GENERAL).execute("");
		new TopFlopTask(this, CLASSEMENT_HOURRA).execute("");
	}
	
	
	// Top ou flop pour un championnat (3 users en résultat)
	private List<TopFlopEntry> getTopFlop(String typeChamp, String typeFlop) {
		List<TopFlopEntry> topFlopEntries = new ArrayList<TopFlopEntry>();

		String strTopFlop = RestClient.get(new QueryBuilder(this.getAssets(), "/rest/topFlop/" + typeChamp + "/?typeEvol=" + typeFlop + "&nbUser=3").getUri());

		try {
			// A Simple JSONObject Creation
	        JSONObject json = new JSONObject(strTopFlop);

	        // A Simple JSONObject Parsing
	        JSONArray topFlopArray = json.getJSONArray("topFlop");
	        for(int i = 0; i < topFlopArray.length(); i++) {
	        	JSONObject jsonTopFlopEntry = topFlopArray.getJSONObject(i);

	        	TopFlopEntry topFlopEntry = new TopFlopEntry();
	        	if (Integer.parseInt(jsonTopFlopEntry.getString("evol")) > 0) {
	        		topFlopEntry.setEvolution("+" + jsonTopFlopEntry.getString("evol"));
	        	} else {
	        		topFlopEntry.setEvolution(jsonTopFlopEntry.getString("evol"));	
	        	}	        	
	        	topFlopEntry.setPseudo(jsonTopFlopEntry.getString("pseudo"));
	        	
	        	topFlopEntries.add(topFlopEntry);
	        }

		} catch (JSONException e) {
            e.printStackTrace();
        }

		return topFlopEntries;
	}

	
	
	// Derniers matchs effectués par les 2 clubs
    private class TopFlopTask extends AsyncTask<String, Void, Boolean> {

        private List<TopFlopEntry> evolutionEntriesTop;
        private List<TopFlopEntry> evolutionEntriesFlop;
        private ProgressDialog dialog;
        private TopFlopActivity activity;
        private String typeChamp;

        private TopFlopTask(TopFlopActivity activity, String typeChamp) {
        	this.activity = activity;
        	this.typeChamp = typeChamp;
            dialog = new ProgressDialog(activity);
        }

        protected void onPreExecute() {
            this.dialog.setMessage("Chargement");
            this.dialog.show();
        }

        @Override
        protected Boolean doInBackground(final String... args) {

        	evolutionEntriesTop = getTopFlop(typeChamp, EVOLUTION_TOP);
        	evolutionEntriesFlop = getTopFlop(typeChamp, EVOLUTION_FLOP);

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            
        	ListView evolutionListViewTop;
        	ListView evolutionListViewFlop;
        	
        	if (typeChamp.compareTo(CLASSEMENT_MIXTE) == 0) {
            	evolutionListViewTop = (ListView) activity.findViewById(R.id.evolutionMixteListTop);
            	evolutionListViewFlop = (ListView) activity.findViewById(R.id.evolutionMixteListFlop);
        	} else if (typeChamp.compareTo(CLASSEMENT_GENERAL) == 0) {
            	evolutionListViewTop = (ListView) activity.findViewById(R.id.evolutionGeneralListTop);
            	evolutionListViewFlop = (ListView) activity.findViewById(R.id.evolutionGeneralListFlop);        		
        	} else {
            	evolutionListViewTop = (ListView) activity.findViewById(R.id.evolutionHourraListTop);
            	evolutionListViewFlop = (ListView) activity.findViewById(R.id.evolutionHourraListFlop);        		
        	}
        	
            if(evolutionEntriesTop.size() > 0) {
                TopFlopSerieAdapter adapter = new TopFlopSerieAdapter(activity, R.layout.top_flop_item, evolutionEntriesTop);
                evolutionListViewTop.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        	
            if(evolutionEntriesFlop.size() > 0) {
                TopFlopSerieAdapter adapter = new TopFlopSerieAdapter(activity, R.layout.top_flop_item, evolutionEntriesFlop);
                evolutionListViewFlop.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            super.onPostExecute(success);
        }
    }		
	
}
