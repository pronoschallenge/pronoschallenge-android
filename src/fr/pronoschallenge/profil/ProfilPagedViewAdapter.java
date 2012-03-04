package fr.pronoschallenge.profil;

import fr.pronoschallenge.R;
import fr.pronoschallenge.rest.QueryBuilder;
import fr.pronoschallenge.rest.RestClient;
import fr.pronoschallenge.util.AppUtil;
import greendroid.widget.PagedAdapter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ProfilPagedViewAdapter extends PagedAdapter {

	static int PAGE_COUNT = 2;
	static int NUM_PAGE_STAT = 0;
	static int NUM_PAGE_HISTO = 1;
	
	private ProfilPagedViewActivity profilActivity;
    private int currentPage;

    public ProfilPagedViewAdapter(ProfilPagedViewActivity profilActivity) {
    	super();
    	this.profilActivity = profilActivity;
    }
	
	private void afficherPage(int numPage, View convertView) {

		LinearLayout profilPalmaresEnteteLayout = (LinearLayout) convertView.findViewById(R.id.profilPalmaresEnteteLayout);
		LinearLayout profilPalmaresListLayout = (LinearLayout) convertView.findViewById(R.id.profilPalmaresListLayout);
		LinearLayout profilStatEnteteLayout1 = (LinearLayout) convertView.findViewById(R.id.profilStatEnteteLayout1);
		LinearLayout profilStatEnteteLayout2 = (LinearLayout) convertView.findViewById(R.id.profilStatEnteteLayout2);
		LinearLayout profilStatListLayout = (LinearLayout) convertView.findViewById(R.id.profilStatListLayout);
		
		profilPalmaresEnteteLayout.setVisibility(View.GONE);
		profilPalmaresListLayout.setVisibility(View.GONE);
		profilStatEnteteLayout1.setVisibility(View.GONE);
		profilStatEnteteLayout2.setVisibility(View.GONE);
		profilStatListLayout.setVisibility(View.GONE);
		
		currentPage = numPage;
		new ProfilTask(convertView).execute(profilActivity.getProfilPseudo());
		
		if (currentPage == NUM_PAGE_STAT) {
			new StatistiqueTask(convertView).execute(profilActivity.getProfilPseudo());
		} else if (currentPage == NUM_PAGE_HISTO) {
			new PalmaresTask(convertView).execute(profilActivity.getProfilPseudo());
		} 
	}

	// Informations gÈnÈrales du profil
	private ProfilEntry getProfil(String userName) {
		ProfilEntry profilEntry = new ProfilEntry();

		String strProfil = RestClient.get(new QueryBuilder(profilActivity.getAssets(), "/rest/profil/" + userName + "/").getUri());

		// Propri√©t√© non pr√©sente dans le JSON
		profilEntry.setPseudo(userName);
		
		try {
			// A Simple JSONObject Creation
	        JSONObject json = new JSONObject(strProfil);
	        JSONObject jsonProfilEntry = json.getJSONObject("profil");
	        
	        // Propri√©t√©s de l'objet JSON
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

	
	// PalmarËs (historique)
	private List<PalmaresEntry> getPalmares(String userName) {
		List<PalmaresEntry> palmaresEntries = new ArrayList<PalmaresEntry>();

		String strPalmares = RestClient.get(new QueryBuilder(profilActivity.getAssets(), "/rest/palmares/" + userName + "/").getUri());

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
	        	// Recherche des championnats liÔøΩs ÔøΩ la saison
	        	while (jsonPalmaresEntry.getString("nomSaison").equals(palmaresEntry.getNomSaison()) && i < palmaresArray.length()) {
	        		PalmaresDetailEntry palmaresDetail = new PalmaresDetailEntry();
        			palmaresDetail.setTypeChamp(jsonPalmaresEntry.getString("typeChamp"));
                    palmaresDetail.setNumPlace(jsonPalmaresEntry.getString("numPlace"));
                    palmaresEntry.setPalmaresDetail(palmaresDetail);
                    // Entit√© JSON suivante
                    i++;
                    if (i < palmaresArray.length()) {
                    	jsonPalmaresEntry = palmaresArray.getJSONObject(i);
                    }
	        	}
	        	// Repositionnement sur l'entit√© JSON ad√©quate
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

	
	// Statistique (classement, derniËre journÈe, Èvolution)
	private List<ProfilStatEntry> getStatistique(String userName) {
		List<ProfilStatEntry> statEntries = new ArrayList<ProfilStatEntry>();

		String strStat = RestClient.get(new QueryBuilder(profilActivity.getAssets(), "/rest/profilStat/" + userName + "/").getUri());

		try {
			// A Simple JSONObject Creation
	        JSONObject json = new JSONObject(strStat);

	        // A Simple JSONObject Parsing
	        JSONArray palmaresArray = json.getJSONArray("profilStat");
	        for (int i = 0; i < palmaresArray.length(); i++)
	        {
	        	JSONObject jsonPalmaresEntry = palmaresArray.getJSONObject(i);

	        	ProfilStatEntry profilStatEntry = new ProfilStatEntry();
	        	profilStatEntry.setTypeChamp(jsonPalmaresEntry.getString("type"));
	        	profilStatEntry.setNumPlace(jsonPalmaresEntry.getString("place"));
	        	profilStatEntry.setNbPoints(jsonPalmaresEntry.getString("points"));
	        	profilStatEntry.setEvolution(jsonPalmaresEntry.getString("evolution"));
	        	statEntries.add(profilStatEntry);
	        }

		} catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

		return statEntries;
	}


    private class ProfilTask extends AsyncTask<String, Void, Boolean> {

    	private View convertView;
        private ProfilEntry profilEntry;
        private ProgressDialog dialog;

        private ProfilTask(View convertView) {
        	this.convertView = convertView;
            dialog = new ProgressDialog(profilActivity);
        }

        protected void onPreExecute() {
            this.dialog.setMessage("Chargement");
            this.dialog.show();
        }

        @Override
        protected Boolean doInBackground(final String... args) {
            profilEntry = getProfil(args[0]);
            return true;
        }
       
        @Override
        protected void onPostExecute(final Boolean success) {
            if(profilEntry != null) {
            	// Chargement des donn√©es de l'activity
            	TextView pseudoTextView = (TextView) convertView.findViewById(R.id.profilPseudo);
                pseudoTextView.setText(profilEntry.getPseudo());
            	ImageView avatarImageView = (ImageView) convertView.findViewById(R.id.profilAvatar);
                Bitmap bitmapAvatar = AppUtil.downloadImage(profilEntry.getUrl_avatar());
                if (bitmapAvatar != null) {
                	avatarImageView.setImageBitmap(bitmapAvatar);	
                }
                avatarImageView.setVisibility(View.VISIBLE);
                TextView nomPrenomTextView = (TextView) convertView.findViewById(R.id.profilNomPrenom);
                nomPrenomTextView.setText((String) (profilEntry.getNom() + " " + profilEntry.getPrenom()).trim());
                TextView adresseTextView = (TextView) convertView.findViewById(R.id.profilAdresse);
                if (profilEntry.getDepartement() != "") {
                	adresseTextView.setText((String) (profilEntry.getVille() + " (" + profilEntry.getDepartement() + ")").trim());	
                } else {
                	adresseTextView.setText((String) (profilEntry.getVille()).trim());
                }                
                bitmapAvatar = AppUtil.downloadImage(profilEntry.getUrl_logo());
                if (bitmapAvatar != null) {
                	ImageView logoImageView = (ImageView) convertView.findViewById(R.id.profilLogo);
                	logoImageView.setImageBitmap(bitmapAvatar);	
                } 
            }

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            super.onPostExecute(success);
        }
        	
    }


    private class PalmaresTask extends AsyncTask<String, Void, Boolean> {

    	private View convertView;
        private List<PalmaresEntry> palmaresEntries;
        private ProgressDialog dialog;

        private PalmaresTask(View convertView) {
        	this.convertView = convertView;
            dialog = new ProgressDialog(profilActivity);
        }

        protected void onPreExecute() {
            this.dialog.setMessage("Chargement");
            this.dialog.show();
        }

        @Override
        protected Boolean doInBackground(final String... args) {
            palmaresEntries = getPalmares(args[0]);
            return true;
        }
       
        @Override
        protected void onPostExecute(final Boolean success) {
    		LinearLayout profilPalmaresEnteteLayout = (LinearLayout) convertView.findViewById(R.id.profilPalmaresEnteteLayout);
    		LinearLayout profilPalmaresListLayout = (LinearLayout) convertView.findViewById(R.id.profilPalmaresListLayout);		
    		profilPalmaresEnteteLayout.setVisibility(View.VISIBLE);
    		profilPalmaresListLayout.setVisibility(View.VISIBLE);
        	
            if (palmaresEntries.isEmpty()) {
            	TextView messagePalmares = (TextView) convertView.findViewById(R.id.palmaresMessage);
            	messagePalmares.setVisibility(View.VISIBLE);
            } else {
	            ProfilAdapter adapter = new ProfilAdapter(profilActivity, R.layout.profil_item, palmaresEntries);
	            ListView palmaresListView = (ListView) convertView.findViewById(R.id.palmaresList);
	            palmaresListView.setAdapter(adapter);
	            adapter.notifyDataSetChanged();	            
            }

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            super.onPostExecute(success);
        }
        	
    }


    private class StatistiqueTask extends AsyncTask<String, Void, Boolean> {

    	private View convertView;
        private List<ProfilStatEntry> statEntries;
        private ProgressDialog dialog;

        private StatistiqueTask(View convertView) {
        	this.convertView = convertView;
            dialog = new ProgressDialog(profilActivity);
        }

        protected void onPreExecute() {
            this.dialog.setMessage("Chargement");
            this.dialog.show();
        }

        @Override
        protected Boolean doInBackground(final String... args) {
        	statEntries = getStatistique(args[0]);
            return true;
        }
       
        @Override
        protected void onPostExecute(final Boolean success) {
    		LinearLayout profilStatEnteteLayout1 = (LinearLayout) convertView.findViewById(R.id.profilStatEnteteLayout1);
    		LinearLayout profilStatEnteteLayout2 = (LinearLayout) convertView.findViewById(R.id.profilStatEnteteLayout2);
    		LinearLayout profilStatListLayout = (LinearLayout) convertView.findViewById(R.id.profilStatListLayout);		
    		profilStatEnteteLayout1.setVisibility(View.VISIBLE);
    		profilStatEnteteLayout2.setVisibility(View.VISIBLE);
    		profilStatListLayout.setVisibility(View.VISIBLE);
        	
            if (! statEntries.isEmpty()) {
	            ProfilStatAdapter adapter = new ProfilStatAdapter(profilActivity, R.layout.profil_stat_item, statEntries);
	            ListView profilStatListView = (ListView) convertView.findViewById(R.id.profilStatList);
	            profilStatListView.setAdapter(adapter);
	            adapter.notifyDataSetChanged();	            
            }

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            super.onPostExecute(success);
        }
        	
    }
    
    
	@Override
	public int getCount() {
		return PAGE_COUNT;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = profilActivity.getLayoutInflater().inflate(R.layout.profil, parent, false);
        }

        afficherPage(position, convertView);

        return convertView;

	}
}
