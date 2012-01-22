package fr.pronoschallenge.amis.ajout;

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


public class AmisAjoutActivity extends GDActivity {
	
	private String profilPseudo;
    private LinearLayout amisAjoutLayout;
    
	private ListView amisAjoutListView;
    private TextView amisAjoutMessage;
    
    private static final String SECTIONS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  
    private AlertDialog dialog;

    
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

		setActionBarContentView(R.layout.amis_ajout_liste);
    	
        profilPseudo = userName;
		
		// Obtain handles to UI objects
        amisAjoutListView = (ListView) findViewById(R.id.ajoutAmisList);
        amisAjoutListView.setTextFilterEnabled(true);
        amisAjoutMessage = (TextView) findViewById(R.id.ajoutAmisMessage);
        amisAjoutLayout = (LinearLayout) findViewById(R.id.ajoutAmisLayout);
      
	}

	
	@Override
	protected void onStart() {
		if(NetworkUtil.isConnected(this.getApplicationContext())) {
			setTitle(getString(R.string.title_liste_amis) + " - " + getString(R.string.title_liste_amis_ajout));
            new AmisTask(this).execute(profilPseudo);
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


	private List<AmisAjoutEntry> getAmisAjoutListe(String userName) {
		// Chargement de la liste des utilisateurs non amis

		List<AmisAjoutEntry> amisAjoutListe = new ArrayList<AmisAjoutEntry>();
		String strListeAjoutAmis = RestClient.get(new QueryBuilder(this.getAssets(), "/rest/listeAmis/" + userName + "/?type='0'").getUri());
		
		try {
			// A Simple JSONObject Creation
	        JSONObject json = new JSONObject(strListeAjoutAmis);

	        // A Simple JSONObject Parsing
	        JSONArray amisAjoutArray = json.getJSONArray("listeAmis");
	        for (int i = 0; i < amisAjoutArray.length(); i++)
	        {
	        	JSONObject jsonAmisAjoutEntry = amisAjoutArray.getJSONObject(i);

	        	AmisAjoutEntry amisAjoutEntry = new AmisAjoutEntry();
	        	amisAjoutEntry.setPseudo(jsonAmisAjoutEntry.getString("pseudo"));
	        	amisAjoutEntry.setNom(jsonAmisAjoutEntry.getString("nom"));
	        	amisAjoutEntry.setPrenom(jsonAmisAjoutEntry.getString("prenom"));
	        	amisAjoutEntry.setAmi(jsonAmisAjoutEntry.getString("ami"));
	        	amisAjoutListe.add(amisAjoutEntry);
	        }

		} catch (JSONException e) {
            e.printStackTrace();
        }
		
		return amisAjoutListe;
	}


    private class AmisTask extends AsyncTask<String, Void, Boolean> {

        final private AmisAjoutActivity activity;
        private ProgressDialog dialog;
        private List<AmisAjoutEntry> amisAjoutListe;

        private AmisTask(AmisAjoutActivity activity) {
            this.activity = activity;
            dialog = new ProgressDialog(activity);
        }

        protected void onPreExecute() {
            this.dialog.setMessage("Chargement");
            this.dialog.show();
        }

        @Override
        protected Boolean doInBackground(final String... args) {
        	amisAjoutListe = activity.getAmisAjoutListe(args[0]);
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
        	// Message liste vide
            if (amisAjoutListe.isEmpty()) {
            	activity.amisAjoutMessage.setVisibility(View.VISIBLE);
            } else {
            	// Affichage de la liste
            	activity.amisAjoutMessage.setVisibility(View.GONE);
            	amisAjoutListView.setFastScrollEnabled(true);
	            AmisAjoutAdapter adapter = new AmisAjoutAdapter(activity, R.layout.amis_ajout_liste_item, amisAjoutListe);
	            amisAjoutListView.setAdapter(adapter);
	            adapter.notifyDataSetChanged();	            
            }

            activity.amisAjoutLayout.setVisibility(View.VISIBLE);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            super.onPostExecute(success);
        }
        	
    }
}