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
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class StatMatchActivity extends GDActivity {

	private String nomClubDomicile;
	private String nomClubExterieur;
	private int intIdMatch;
	private int vueStat;
	static int vueCote = 1;
	static int vueClassement = 2;
	
	private AsyncImageView statMatchLogoDomAsyncImageView;
	private TextView statMatchEquipeDomTextView;
	private TextView statMatchPlaceDomTextView;
	private ListView statMatchSerieListViewDom;
	
	private AsyncImageView statMatchLogoExtAsyncImageView;	
	private TextView statMatchEquipeExtTextView;
	private TextView statMatchPlaceExtTextView;
	private ListView statMatchSerieListViewExt;
	
	private TextView messageStatMatchSerieTextView;
	
	private TextView statMatchCoteDomTextView;
	private TextView statMatchCoteNulTextView;
	private TextView statMatchCoteExtTextView;
	
	private TextView statMatchFormeDom1TextView;
	private TextView statMatchFormeDomNTextView;
	private TextView statMatchFormeDom2TextView;
	private TextView statMatchFormeExt1TextView;
	private TextView statMatchFormeExtNTextView;
	private TextView statMatchFormeExt2TextView;
	private LinearLayout statMatchFormeLayout;
	private LinearLayout statMatchCoteLayout;
	private LinearLayout statMatchDerniersMatchLayout;

	private ListView statMatchClassementListView;
	private LinearLayout statMatchClassementLayout;
	
	private LinearLayout statMatchConfrontationLayout;
	private TextView statMatchConfrontationTitreTextView;	
	private TextView statMatchConfrontation1TextView;
	private TextView statMatchConfrontationNTextView;
	private TextView statMatchConfrontation2TextView;
	private TextView statMatchConfrontationMessageTextView;	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setActionBarContentView(R.layout.stat_match);
    	
    	//On rÃ©cupÃ¨re l'objet Bundle envoyÃ© par l'autre Activity
        Bundle objetbunble  = this.getIntent().getExtras();
        nomClubDomicile = (String) objetbunble.get("clubDomicile");
        nomClubExterieur = (String) objetbunble.get("clubExterieur");
        String strIdMatch = (String) objetbunble.get("idMatch");
        intIdMatch = Integer.parseInt(strIdMatch);
        vueStat = vueClassement;
        
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
        
        statMatchCoteDomTextView = (TextView) findViewById(R.id.statMatchCoteDom);
        statMatchCoteNulTextView = (TextView) findViewById(R.id.statMatchCoteNul);
        statMatchCoteExtTextView = (TextView) findViewById(R.id.statMatchCoteExt);

        statMatchFormeDom1TextView = (TextView) findViewById(R.id.statMatchFormeDom1);
        statMatchFormeDomNTextView = (TextView) findViewById(R.id.statMatchFormeDomN);
        statMatchFormeDom2TextView = (TextView) findViewById(R.id.statMatchFormeDom2);
        statMatchFormeExt1TextView = (TextView) findViewById(R.id.statMatchFormeExt1);
        statMatchFormeExtNTextView = (TextView) findViewById(R.id.statMatchFormeExtN);
        statMatchFormeExt2TextView = (TextView) findViewById(R.id.statMatchFormeExt2);
        statMatchFormeLayout = (LinearLayout) findViewById(R.id.statMatchFormeLayout);
        statMatchCoteLayout = (LinearLayout) findViewById(R.id.statMatchCoteLayout);
        statMatchDerniersMatchLayout = (LinearLayout) findViewById(R.id.statMatchDerniersMatchLayout);

        statMatchClassementLayout = (LinearLayout) findViewById(R.id.statMatchClassementLayout);
        statMatchClassementListView = (ListView) findViewById(R.id.statMatchClassementList);

        statMatchConfrontationLayout = (LinearLayout) findViewById(R.id.statMatchConfrontationLayout);
        statMatchConfrontationTitreTextView = (TextView) findViewById(R.id.statMatchConfrontationTitre);
        statMatchConfrontation1TextView = (TextView) findViewById(R.id.statMatchConfrontation1);
        statMatchConfrontationNTextView = (TextView) findViewById(R.id.statMatchConfrontationN);
        statMatchConfrontation2TextView = (TextView) findViewById(R.id.statMatchConfrontation2);
        statMatchConfrontationMessageTextView = (TextView) findViewById(R.id.statMatchConfrontationMessage);
        
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
		
		afficherPage();
	}

	// Affichage de la page selon la vue souhaitée
	private void afficherPage() {
		
		new InfoClubTask(this).execute("");
		
		if (vueStat == vueCote) {
			new CoteMatchTask(this).execute("");
			new StatMatchTask(this).execute("");
		} else if (vueStat == vueClassement) {
			new ConfrontationTask(this).execute("");
		}
	}
	
	
	// Série en cours d'un club
	private List<StatMatchSerieEntry> getMatchSerie(String nomClub) {
		List<StatMatchSerieEntry> statMatchSerieEntries = new ArrayList<StatMatchSerieEntry>();

		String strMatchSerie = RestClient.get(new QueryBuilder(this.getAssets(), "/rest/serieClub/" + nomClub + "/?nbMatch=5").getUri());

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
	
	
	// Série en cours d'un club
	private List<StatMatchSerieEntry> getConfrontation(String nomClubDom, String nomClubExt) {
		List<StatMatchSerieEntry> statMatchSerieEntries = new ArrayList<StatMatchSerieEntry>();

		String strMatchSerie = RestClient.get(new QueryBuilder(this.getAssets(), "/rest/confrontationClub/" + nomClubDom + "/?clubAdverse=" + nomClubExt).getUri());

		try {
			// A Simple JSONObject Creation
	        JSONObject json = new JSONObject(strMatchSerie);

	        // A Simple JSONObject Parsing
	        JSONArray matchSerieArray = json.getJSONArray("confrontationClub");
	        for(int i = 0; i < matchSerieArray.length(); i++) {
	        	JSONObject jsonSerieEntry = matchSerieArray.getJSONObject(i);

	        	StatMatchSerieEntry statMatchSerieEntry = new StatMatchSerieEntry();	        	
	        	statMatchSerieEntry.setButDom(jsonSerieEntry.getInt("butDom"));
	        	statMatchSerieEntry.setButExt(jsonSerieEntry.getInt("butExt"));
        		statMatchSerieEntry.setNomClubDom(jsonSerieEntry.getString("clubDom"));
	        	statMatchSerieEntry.setNomClubExt(jsonSerieEntry.getString("clubExt"));	        	
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
	        	infoClubEntry.setPoints(jsonInfoClubEntry.getInt("points"));
	        	infoClubEntry.setMatchJoue(jsonInfoClubEntry.getInt("j"));
	        	infoClubEntry.setMatchGagne(jsonInfoClubEntry.getInt("g"));
	        	infoClubEntry.setMatchNul(jsonInfoClubEntry.getInt("n"));
	        	infoClubEntry.setMatchPerdu(jsonInfoClubEntry.getInt("p"));
	        	infoClubEntry.setButsPour(jsonInfoClubEntry.getInt("bp"));
	        	infoClubEntry.setButsContre(jsonInfoClubEntry.getInt("bc"));
	        	infoClubEntry.setDiff(jsonInfoClubEntry.getInt("diff"));
	        	infoClubEntry.setMatchGagneDom(jsonInfoClubEntry.getInt("domg"));
	        	infoClubEntry.setMatchNulDom(jsonInfoClubEntry.getInt("domn"));
	        	infoClubEntry.setMatchPerduDom(jsonInfoClubEntry.getInt("domp"));
	        	infoClubEntry.setMatchGagneExt(jsonInfoClubEntry.getInt("extg"));
	        	infoClubEntry.setMatchNulExt(jsonInfoClubEntry.getInt("extn"));
	        	infoClubEntry.setMatchPerduExt(jsonInfoClubEntry.getInt("extp"));
	        	infoClubEntry.setUrlLogo(jsonInfoClubEntry.getString("url_logo"));
	        }

		} catch (JSONException e) {
            e.printStackTrace();
        }

		return infoClubEntry;
	}
	
	
	// Derniers matchs effectués par les 2 clubs
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
        	
        	statMatchDerniersMatchLayout.setVisibility(View.VISIBLE);
        	
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
                messageStatMatchSerieTextView.setText("Série non disponible");
                messageStatMatchSerieTextView.setVisibility(View.VISIBLE);
            }
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            super.onPostExecute(success);
        }
    }	
	
	
	// Derniers matchs effectués par les 2 clubs
    private class ConfrontationTask extends AsyncTask<String, Void, Boolean> {

        private List<StatMatchSerieEntry> statMatchSerieEntries;
        private ProgressDialog dialog;

        private ConfrontationTask(StatMatchActivity activity) {
            dialog = new ProgressDialog(activity);
        }

        protected void onPreExecute() {
            this.dialog.setMessage("Chargement");
            this.dialog.show();
        }

        @Override
        protected Boolean doInBackground(final String... args) {
            statMatchSerieEntries = getConfrontation(nomClubDomicile, nomClubExterieur);
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
        	double nbMatchJoue = statMatchSerieEntries.size();
        	double nbMatchG = 0, nbMatchN = 0, nbMatchP = 0;
        	String titreCompl;

        	android.view.Display display = ((android.view.WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        	int intLargeur = display.getWidth() - 10;        	
        	
        	statMatchConfrontationLayout.setVisibility(View.VISIBLE);
        	statMatchConfrontation1TextView.setBackgroundColor(Color.GREEN);
        	statMatchConfrontation1TextView.setTextColor(Color.BLACK);
        	statMatchConfrontationNTextView.setBackgroundColor(Color.YELLOW);
        	statMatchConfrontationNTextView.setTextColor(Color.BLACK);
        	statMatchConfrontation2TextView.setBackgroundColor(Color.RED);
        	statMatchConfrontation2TextView.setTextColor(Color.BLACK);
        	
            if(nbMatchJoue > 0) {
            	for (StatMatchSerieEntry statMatchSerie : statMatchSerieEntries) {
            		if (statMatchSerie.getButDom() == statMatchSerie.getButExt()) {
            			nbMatchN += 1;
            		} else if ((statMatchSerie.getButDom() > statMatchSerie.getButExt() && statMatchSerie.getNomClubDom().compareTo(nomClubDomicile) == 0)
            				|| (statMatchSerie.getButExt() > statMatchSerie.getButDom() && statMatchSerie.getNomClubExt().compareTo(nomClubDomicile) == 0)) {
            			nbMatchG += 1;
            		} else {
            			nbMatchP += 1;
            		}
            	}
            	statMatchConfrontation1TextView.setWidth((int) ((nbMatchG / nbMatchJoue) * intLargeur));
            	statMatchConfrontationNTextView.setWidth((int) ((nbMatchN / nbMatchJoue) * intLargeur));
            	statMatchConfrontation2TextView.setWidth((int) ((nbMatchP / nbMatchJoue) * intLargeur));
            	if (nbMatchJoue > 1) {
            		titreCompl = "s)";
            	} else {
            		titreCompl = ")";
            	}
            	statMatchConfrontationTitreTextView.setText("Confrontations (" + String.valueOf((int) nbMatchJoue) + " match" + titreCompl);
            } else {
            	statMatchConfrontation1TextView.setVisibility(View.GONE);
            	statMatchConfrontationNTextView.setVisibility(View.GONE);
            	statMatchConfrontation2TextView.setVisibility(View.GONE);
                statMatchConfrontationMessageTextView.setText("Historique non disponible");
                statMatchConfrontationMessageTextView.setVisibility(View.VISIBLE);
            }
        
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            super.onPostExecute(success);
        }
    }	
	
	
	// affiche la forme domicile/extérieur sur la saison pour les 2 clubs
    private void afficherForme(ClassementClubEntry infoClubEntryDom, ClassementClubEntry infoClubEntryExt) {
    	android.view.Display display = ((android.view.WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();    	
    	// Moitié de la largeur de l'écran - 2x(largeur min d'une valeur forme) 
    	int intLargeur = (display.getWidth() / 2) - 10;
    	double intMatchJoue;
    	
    	statMatchFormeLayout.setVisibility(View.VISIBLE);
    	statMatchFormeDom1TextView.setBackgroundColor(Color.GREEN);
    	statMatchFormeDom1TextView.setTextColor(Color.BLACK);
    	statMatchFormeDomNTextView.setBackgroundColor(Color.YELLOW);
    	statMatchFormeDomNTextView.setTextColor(Color.BLACK);
    	statMatchFormeDom2TextView.setBackgroundColor(Color.RED);
    	statMatchFormeDom2TextView.setTextColor(Color.BLACK);
    	statMatchFormeExt1TextView.setBackgroundColor(Color.GREEN);
    	statMatchFormeExt1TextView.setTextColor(Color.BLACK);
    	statMatchFormeExtNTextView.setBackgroundColor(Color.YELLOW);
    	statMatchFormeExtNTextView.setTextColor(Color.BLACK);
    	statMatchFormeExt2TextView.setBackgroundColor(Color.RED);
    	statMatchFormeExt2TextView.setTextColor(Color.BLACK);
    	
    	intMatchJoue = infoClubEntryDom.getMatchGagneDom() + infoClubEntryDom.getMatchNulDom() + infoClubEntryDom.getMatchPerduDom();
    	statMatchFormeDom1TextView.setWidth((int) (((double) infoClubEntryDom.getMatchGagneDom() / intMatchJoue) * intLargeur));
    	statMatchFormeDomNTextView.setWidth((int) (((double) infoClubEntryDom.getMatchNulDom() / intMatchJoue) * intLargeur));
    	statMatchFormeDom2TextView.setWidth((int) (((double) infoClubEntryDom.getMatchPerduDom() / intMatchJoue) * intLargeur));

    	intMatchJoue = infoClubEntryExt.getMatchGagneExt() + infoClubEntryExt.getMatchNulExt() + infoClubEntryExt.getMatchPerduExt();
    	statMatchFormeExt1TextView.setWidth((int) (((double)infoClubEntryExt.getMatchGagneExt() / intMatchJoue) * intLargeur));
    	statMatchFormeExtNTextView.setWidth((int) (((double) infoClubEntryExt.getMatchNulExt() / intMatchJoue) * intLargeur));
    	statMatchFormeExt2TextView.setWidth((int) (((double) infoClubEntryExt.getMatchPerduExt() / intMatchJoue) * intLargeur));
    }	
	
	
	// affiche le classement détaillé des 2 clubs
    private void afficherClassementDetail(StatMatchActivity activity, ClassementClubEntry infoClubEntryDom, ClassementClubEntry infoClubEntryExt) {
        List<StatMatchClassementEntry> StatMatchClassementEntries = new ArrayList<StatMatchClassementEntry>();
        StatMatchClassementEntry statMatchClassementEntry;
        
    	statMatchClassementLayout.setVisibility(View.VISIBLE);
    	
    	statMatchClassementEntry = new StatMatchClassementEntry();
    	statMatchClassementEntry.setTypeInfo("Pts");
    	statMatchClassementEntry.setInfoDom(String.valueOf(infoClubEntryDom.getPoints()));
    	statMatchClassementEntry.setInfoExt(String.valueOf(infoClubEntryExt.getPoints()));
    	StatMatchClassementEntries.add(statMatchClassementEntry);

    	statMatchClassementEntry = new StatMatchClassementEntry();
    	statMatchClassementEntry.setTypeInfo("J");
    	statMatchClassementEntry.setInfoDom(String.valueOf(infoClubEntryDom.getMatchJoue()));
    	statMatchClassementEntry.setInfoExt(String.valueOf(infoClubEntryExt.getMatchJoue()));
    	StatMatchClassementEntries.add(statMatchClassementEntry);

    	statMatchClassementEntry = new StatMatchClassementEntry();
    	statMatchClassementEntry.setTypeInfo("G");
    	statMatchClassementEntry.setInfoDom(String.valueOf(infoClubEntryDom.getMatchGagne()));
    	statMatchClassementEntry.setInfoExt(String.valueOf(infoClubEntryExt.getMatchGagne()));
    	StatMatchClassementEntries.add(statMatchClassementEntry);

    	statMatchClassementEntry = new StatMatchClassementEntry();
    	statMatchClassementEntry.setTypeInfo("N");
    	statMatchClassementEntry.setInfoDom(String.valueOf(infoClubEntryDom.getMatchNul()));
    	statMatchClassementEntry.setInfoExt(String.valueOf(infoClubEntryExt.getMatchNul()));
    	StatMatchClassementEntries.add(statMatchClassementEntry);

    	statMatchClassementEntry = new StatMatchClassementEntry();
    	statMatchClassementEntry.setTypeInfo("P");
    	statMatchClassementEntry.setInfoDom(String.valueOf(infoClubEntryDom.getMatchPerdu()));
    	statMatchClassementEntry.setInfoExt(String.valueOf(infoClubEntryExt.getMatchPerdu()));
    	StatMatchClassementEntries.add(statMatchClassementEntry);

    	statMatchClassementEntry = new StatMatchClassementEntry();
    	statMatchClassementEntry.setTypeInfo("BP");
    	statMatchClassementEntry.setInfoDom(String.valueOf(infoClubEntryDom.getButsPour()));
    	statMatchClassementEntry.setInfoExt(String.valueOf(infoClubEntryExt.getButsPour()));
    	StatMatchClassementEntries.add(statMatchClassementEntry);

    	statMatchClassementEntry = new StatMatchClassementEntry();
    	statMatchClassementEntry.setTypeInfo("BC");
    	statMatchClassementEntry.setInfoDom(String.valueOf(infoClubEntryDom.getButsContre()));
    	statMatchClassementEntry.setInfoExt(String.valueOf(infoClubEntryExt.getButsContre()));
    	StatMatchClassementEntries.add(statMatchClassementEntry);
    	
		StatMatchClassementAdapter adapter = new StatMatchClassementAdapter(activity, R.layout.stat_match_classement_item, StatMatchClassementEntries);
	    statMatchClassementListView.setAdapter(adapter);
	    adapter.notifyDataSetChanged();

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
                	statMatchCoteDomTextView.setText(String.valueOf(coteMatchEntry.getCote()));
                	tabcote[0] = coteMatchEntry.getCote();
	        	} else if (coteMatchEntry.getTypeMatch().compareTo("N") == 0) {
                	statMatchCoteNulTextView.setText(String.valueOf(coteMatchEntry.getCote()));
                	tabcote[1] = coteMatchEntry.getCote();
	        	} else if (coteMatchEntry.getTypeMatch().compareTo("2") == 0) {
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
	        
	        statMatchCoteLayout.setVisibility(View.VISIBLE);
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
        private StatMatchActivity activity;

        private InfoClubTask(StatMatchActivity activity) {
        	this.activity = activity;
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

        	if (vueStat == vueCote) {
        		afficherForme(infoClubEntryDom, infoClubEntryExt);
        	} else if (vueStat == vueClassement) {
        		afficherClassementDetail(activity, infoClubEntryDom, infoClubEntryExt);
        	}
        	
        	if (dialog.isShowing()) {
                dialog.dismiss();
            }
            super.onPostExecute(success);
        }
    }

}
