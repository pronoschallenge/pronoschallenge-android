package fr.pronoschallenge.profil;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import fr.pronoschallenge.R;
import fr.pronoschallenge.rest.QueryBuilder;
import fr.pronoschallenge.rest.RestClient;
import fr.pronoschallenge.util.AppUtil;
import fr.pronoschallenge.util.NetworkUtil;
import greendroid.app.GDActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ProfilActivity extends GDActivity {
	
	private String profilPseudo;
	private ImageView avatarImageView;
    private TextView pseudoTextView;
    private TextView nomPrenomTextView;
    private TextView adresseTextView;
    private ImageView logoImageView;
    private TextView messageTextView;
    private LinearLayout profilLayout;
    
	private ListView palmaresListView;
    private TextView messagePalmares;
	
    private AlertDialog dialog;

    
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		//on lui associe le layout profil.xml
		setActionBarContentView(R.layout.profil);
    	
    	//On récupère l'objet Bundle envoyé par l'autre Activity
        Bundle objetbunble  = this.getIntent().getExtras();
        profilPseudo = (String) objetbunble.get("pseudo");
		
		// Obtain handles to UI objects
		avatarImageView = (ImageView) findViewById(R.id.profilAvatar);
		pseudoTextView = (TextView) findViewById(R.id.profilPseudo);
		nomPrenomTextView = (TextView) findViewById(R.id.profilNomPrenom);
		adresseTextView = (TextView) findViewById(R.id.profilAdresse);
		logoImageView = (ImageView) findViewById(R.id.profilLogo);
        messageTextView = (TextView) findViewById(R.id.profilMessage);
        palmaresListView = (ListView) findViewById(R.id.palmaresList);
        messagePalmares = (TextView) findViewById(R.id.palmaresMessage);
        profilLayout = (LinearLayout) findViewById(R.id.profilLayout);
	}
	
	
	@Override
	protected void onStart() {
		if(NetworkUtil.isConnected(this.getApplicationContext())) {
			setTitle(getString(R.string.title_profil));
            new ProfilTask(this).execute(profilPseudo);
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

	
	private ProfilEntry getProfil(String userName) {
		ProfilEntry profilEntry = new ProfilEntry();

		String strProfil = RestClient.get(new QueryBuilder(this.getAssets(), "/rest/profil/" + userName + "/").getUri());

		// Propriété non présente dans le JSON
		profilEntry.setPseudo(userName);
		
		try {
			// A Simple JSONObject Creation
	        JSONObject json = new JSONObject(strProfil);
	        JSONObject jsonProfilEntry = json.getJSONObject("profil");
	        
	        // Propriétés de l'objet JSON
	        profilEntry.setId_membre(jsonProfilEntry.getInt("id_membre"));	        
	        profilEntry.setUrl_avatar(jsonProfilEntry.getString("url_avatar"));
	        profilEntry.setNom(jsonProfilEntry.getString("nom"));
	        profilEntry.setPrenom(jsonProfilEntry.getString("prenom"));
	        profilEntry.setClub_favori(jsonProfilEntry.getString("club_favori"));
	        profilEntry.setVille(jsonProfilEntry.getString("ville"));
	        profilEntry.setDepartement(jsonProfilEntry.getString("departement"));
	        profilEntry.setUrl_logo(jsonProfilEntry.getString("url_logo"));

		} catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

		return profilEntry;
	}

	
	private List<PalmaresEntry> getPalmares(String userName) {
		List<PalmaresEntry> palmaresEntries = new ArrayList<PalmaresEntry>();

		String strPalmares = RestClient.get(new QueryBuilder(this.getAssets(), "/rest/palmares/" + userName + "/").getUri());

		try {
			// A Simple JSONObject Creation
	        JSONObject json = new JSONObject(strPalmares);

	        // A Simple JSONObject Parsing
	        JSONArray palmaresArray = json.getJSONArray("palmares");
	        for (int i = 0; i < palmaresArray.length(); i++)
	        {
	        	JSONObject jsonPalmaresEntry = palmaresArray.getJSONObject(i);

	        	PalmaresEntry palmaresEntry = new PalmaresEntry();
	        	palmaresEntry.setNomSaison(jsonPalmaresEntry.getString("nomSaison"));
	        	// Recherche des championnats li�s � la saison
	        	while (jsonPalmaresEntry.getString("nomSaison").equals(palmaresEntry.getNomSaison()) && i < palmaresArray.length()) {
	        		PalmaresDetailEntry palmaresDetail = new PalmaresDetailEntry();
        			palmaresDetail.setTypeChamp(jsonPalmaresEntry.getString("typeChamp"));
                    palmaresDetail.setNumPlace(jsonPalmaresEntry.getString("numPlace"));
                    palmaresEntry.setPalmaresDetail(palmaresDetail);
                    // Entité JSON suivante
                    i++;
                    if (i < palmaresArray.length()) {
                    	jsonPalmaresEntry = palmaresArray.getJSONObject(i);
                    }
	        	}
	        	// Repositionnement sur l'entité JSON adéquate
	        	if (i < palmaresArray.length()) {
	        		i--;
	        	}
                palmaresEntries.add(palmaresEntry);
	        }

		} catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

		return palmaresEntries;
	}


    private class ProfilTask extends AsyncTask<String, Void, Boolean> {

        final private ProfilActivity activity;
        private ProfilEntry profilEntry;
        private List<PalmaresEntry> palmaresEntries;
        private ProgressDialog dialog;

        private ProfilTask(ProfilActivity activity) {
            this.activity = activity;
            dialog = new ProgressDialog(activity);
        }

        protected void onPreExecute() {
            this.dialog.setMessage("Chargement");
            this.dialog.show();
        }

        @Override
        protected Boolean doInBackground(final String... args) {
            profilEntry = activity.getProfil(args[0]);
            palmaresEntries = activity.getPalmares(args[0]);
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if(profilEntry != null) {
            	// Chargement des données de l'activity
                activity.pseudoTextView.setText(profilEntry.getPseudo());
                Bitmap bitmapAvatar = AppUtil.downloadImage(profilEntry.getUrl_avatar());
                if (bitmapAvatar != null) {
                	activity.avatarImageView.setImageBitmap(bitmapAvatar);	
                }
                activity.nomPrenomTextView.setText((String) (profilEntry.getNom() + " " + profilEntry.getPrenom()).trim());
                if (profilEntry.getDepartement() != "") {
                	activity.adresseTextView.setText((String) (profilEntry.getVille() + " (" + profilEntry.getDepartement() + ")").trim());	
                } else {
                	activity.adresseTextView.setText((String) (profilEntry.getVille()).trim());
                }                
                bitmapAvatar = AppUtil.downloadImage(profilEntry.getUrl_logo());
                if (bitmapAvatar != null) {
                	activity.logoImageView.setImageBitmap(bitmapAvatar);	
                } 
                activity.pseudoTextView.setVisibility(View.VISIBLE);
                activity.avatarImageView.setVisibility(View.VISIBLE);
                activity.nomPrenomTextView.setVisibility(View.VISIBLE);
                activity.adresseTextView.setVisibility(View.VISIBLE);
                activity.logoImageView.setVisibility(View.VISIBLE);
                activity.messageTextView.setVisibility(View.GONE);
            } else {
                activity.pseudoTextView.setVisibility(View.GONE);
                activity.avatarImageView.setVisibility(View.GONE);
                activity.nomPrenomTextView.setVisibility(View.GONE);
                activity.adresseTextView.setVisibility(View.GONE);
                activity.logoImageView.setVisibility(View.GONE);
                activity.messageTextView.setVisibility(View.VISIBLE);
            }
            
            if (palmaresEntries.isEmpty()) {
            	activity.messagePalmares.setVisibility(View.VISIBLE);
            } else {
	            ProfilAdapter adapter = new ProfilAdapter(activity,	R.layout.profil_item, palmaresEntries);
	            palmaresListView.setAdapter(adapter);
	            adapter.notifyDataSetChanged();	            
            }

            activity.profilLayout.setVisibility(View.VISIBLE);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            super.onPostExecute(success);
        }
        	
    }
}