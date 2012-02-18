package fr.pronoschallenge.stat.match;

import fr.pronoschallenge.R;
import fr.pronoschallenge.classement.club.ClassementClubEntry;
import fr.pronoschallenge.rest.QueryBuilder;
import fr.pronoschallenge.rest.RestClient;
import greendroid.widget.AsyncImageView;
import greendroid.widget.PagedAdapter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class StatMatchPagedViewAdapter extends PagedAdapter {

	static int PAGE_COUNT = 2;
	static int NUM_PAGE_COTE = 0;
	static int NUM_PAGE_CLASSEMENT = 1;	
	
	private StatMatchPagedViewActivity statMatchActivity;
    private int currentPage;

    public StatMatchPagedViewAdapter(StatMatchPagedViewActivity statMatchActivity) {
    	super();
    	this.statMatchActivity = statMatchActivity;
    }
	
	private void afficherPage(int numPage, View convertView) {

		LinearLayout statMatchConfrontationLayout = (LinearLayout) convertView.findViewById(R.id.statMatchConfrontationLayout);
		LinearLayout statMatchConfrontationDomLayout = (LinearLayout) convertView.findViewById(R.id.statMatchConfrontationDomLayout);
		LinearLayout statMatchClassementLayout = (LinearLayout) convertView.findViewById(R.id.statMatchClassementLayout);
		LinearLayout statMatchCoteLayout = (LinearLayout) convertView.findViewById(R.id.statMatchCoteLayout);
		LinearLayout statMatchFormeLayout = (LinearLayout) convertView.findViewById(R.id.statMatchFormeLayout);
		LinearLayout statMatchDerniersMatchLayout = (LinearLayout) convertView.findViewById(R.id.statMatchDerniersMatchLayout);		
		
		statMatchCoteLayout.setVisibility(View.GONE);
		statMatchFormeLayout.setVisibility(View.GONE);
		statMatchDerniersMatchLayout.setVisibility(View.GONE);
		statMatchConfrontationLayout.setVisibility(View.GONE);
		statMatchConfrontationDomLayout.setVisibility(View.GONE);
		statMatchClassementLayout.setVisibility(View.GONE);
		
		currentPage = numPage;
		new InfoClubTask(convertView).execute("");
		
		if (currentPage == NUM_PAGE_COTE) {
			new CoteMatchTask(convertView).execute("");
			new StatMatchTask(convertView).execute("");
		} else if (currentPage == NUM_PAGE_CLASSEMENT) {
			new ConfrontationTask(convertView).execute("");
		}
	}
	
	
	// S�rie en cours d'un club
	private List<StatMatchSerieEntry> getMatchSerie(String nomClub) {
		List<StatMatchSerieEntry> statMatchSerieEntries = new ArrayList<StatMatchSerieEntry>();

		String strMatchSerie = RestClient.get(new QueryBuilder(statMatchActivity.getAssets(), "/rest/serieClub/" + nomClub + "/?nbMatch=5").getUri());

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
	
	
	// S�rie en cours d'un club
	private List<StatMatchSerieEntry> getConfrontation(String nomClubDom, String nomClubExt) {
		List<StatMatchSerieEntry> statMatchSerieEntries = new ArrayList<StatMatchSerieEntry>();

		String strMatchSerie = RestClient.get(new QueryBuilder(statMatchActivity.getAssets(), "/rest/confrontationClub/" + nomClubDom + "/?clubAdverse=" + nomClubExt).getUri());

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

		String strCoteMatch = RestClient.get(new QueryBuilder(statMatchActivity.getAssets(), "/rest/coteMatch/" + String.valueOf(idMatch) + "/").getUri());

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

		String strInfoClub = RestClient.get(new QueryBuilder(statMatchActivity.getAssets(), "/rest/infoClub/" + nomClub + "/").getUri());

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

        private List<StatMatchSerieEntry> statMatchSerieEntriesDom;
        private List<StatMatchSerieEntry> statMatchSerieEntriesExt;
        private ProgressDialog dialog;
        private View convertView;

        private StatMatchTask(View convertView) {
        	this.convertView = convertView;
            dialog = new ProgressDialog(statMatchActivity);
        }

        protected void onPreExecute() {
            this.dialog.setMessage("Chargement");
            this.dialog.show();
        }

        @Override
        protected Boolean doInBackground(final String... args) {

            statMatchSerieEntriesDom = getMatchSerie(statMatchActivity.getNomClubDomicile());
            statMatchSerieEntriesExt = getMatchSerie(statMatchActivity.getNomClubExterieur());

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            
        	LinearLayout statMatchDerniersMatchLayout = (LinearLayout) convertView.findViewById(R.id.statMatchDerniersMatchLayout);
        	ListView statMatchSerieListViewDom = (ListView) convertView.findViewById(R.id.statMatchSerieListDom);;
        	TextView messageStatMatchSerieTextView = (TextView) convertView.findViewById(R.id.statMatchSerieMessage);
        	ListView statMatchSerieListViewExt = (ListView) convertView.findViewById(R.id.statMatchSerieListExt);
        	
        	statMatchDerniersMatchLayout.setVisibility(View.VISIBLE);
        	
            if(statMatchSerieEntriesDom.size() > 0) {
                StatMatchSerieAdapter adapter = new StatMatchSerieAdapter(convertView.getContext(), R.layout.stat_match_serie_item, statMatchSerieEntriesDom);
                statMatchSerieListViewDom.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                messageStatMatchSerieTextView.setVisibility(View.GONE);
                statMatchSerieListViewDom.setVisibility(View.VISIBLE);
            }
            
            if(statMatchSerieEntriesExt.size() > 0) {
                StatMatchSerieAdapter adapter = new StatMatchSerieAdapter(convertView.getContext(), R.layout.stat_match_serie_item, statMatchSerieEntriesExt);
                statMatchSerieListViewExt.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                messageStatMatchSerieTextView.setVisibility(View.GONE);
                statMatchSerieListViewExt.setVisibility(View.VISIBLE);
            }
            
            if(statMatchSerieEntriesDom.size() == 0 && statMatchSerieEntriesExt.size() == 0) {
                statMatchSerieListViewDom.setVisibility(View.GONE);
                statMatchSerieListViewExt.setVisibility(View.GONE);
                messageStatMatchSerieTextView.setText("S�rie non disponible");
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
        private View convertView;
     
        private ConfrontationTask(View convertView) {
        	this.convertView = convertView;
            dialog = new ProgressDialog(statMatchActivity);
        }

        protected void onPreExecute() {
            this.dialog.setMessage("Chargement");
            this.dialog.show();
        }

        @Override
        protected Boolean doInBackground(final String... args) {
            statMatchSerieEntries = getConfrontation(statMatchActivity.getNomClubDomicile(), statMatchActivity.getNomClubExterieur());
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
        	double nbMatchJoue = statMatchSerieEntries.size();
        	double nbMatchG = 0, nbMatchN = 0, nbMatchP = 0;
        	double nbMatchJoueDom = 0, nbMatchDomG = 0, nbMatchDomN = 0, nbMatchDomP = 0;
        	String titreCompl;

        	LinearLayout statMatchConfrontationLayout = (LinearLayout) convertView.findViewById(R.id.statMatchConfrontationLayout);
        	TextView statMatchConfrontationTitreTextView = (TextView) convertView.findViewById(R.id.statMatchConfrontationTitre);	
        	TextView statMatchConfrontation1TextView = (TextView) convertView.findViewById(R.id.statMatchConfrontation1);
        	TextView statMatchConfrontationNTextView = (TextView) convertView.findViewById(R.id.statMatchConfrontationN);
        	TextView statMatchConfrontation2TextView = (TextView) convertView.findViewById(R.id.statMatchConfrontation2);
        	TextView statMatchConfrontationMessageTextView = (TextView) convertView.findViewById(R.id.statMatchConfrontationMessage);	

        	LinearLayout statMatchConfrontationDomLayout = (LinearLayout) convertView.findViewById(R.id.statMatchConfrontationDomLayout);
        	TextView statMatchConfrontationTitreDomTextView = (TextView) convertView.findViewById(R.id.statMatchConfrontationDomTitre);	
        	TextView statMatchConfrontation1DomTextView = (TextView) convertView.findViewById(R.id.statMatchConfrontationDom1);
        	TextView statMatchConfrontationNDomTextView = (TextView) convertView.findViewById(R.id.statMatchConfrontationDomN);
        	TextView statMatchConfrontation2DomTextView = (TextView) convertView.findViewById(R.id.statMatchConfrontationDom2);
        	TextView statMatchConfrontationMessageDomTextView = (TextView) convertView.findViewById(R.id.statMatchConfrontationDomMessage);	        	
        	
        	android.view.Display display = ((android.view.WindowManager)statMatchActivity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        	int intLargeur = display.getWidth() - 10;        	
        	
        	statMatchConfrontationLayout.setVisibility(View.VISIBLE);
        	statMatchConfrontation1TextView.setBackgroundResource(R.drawable.tendance_vert);
        	statMatchConfrontation1TextView.setTextColor(Color.BLACK);
        	statMatchConfrontationNTextView.setBackgroundResource(R.drawable.tendance_orange);
        	statMatchConfrontationNTextView.setTextColor(Color.BLACK);
        	statMatchConfrontation2TextView.setBackgroundResource(R.drawable.tendance_rouge);
        	statMatchConfrontation2TextView.setTextColor(Color.BLACK);

        	statMatchConfrontationDomLayout.setVisibility(View.VISIBLE);
        	statMatchConfrontation1DomTextView.setBackgroundResource(R.drawable.tendance_vert);
        	statMatchConfrontation1DomTextView.setTextColor(Color.BLACK);
        	statMatchConfrontationNDomTextView.setBackgroundResource(R.drawable.tendance_orange);
        	statMatchConfrontationNDomTextView.setTextColor(Color.BLACK);
        	statMatchConfrontation2DomTextView.setBackgroundResource(R.drawable.tendance_rouge);
        	statMatchConfrontation2DomTextView.setTextColor(Color.BLACK);
        	
            if(nbMatchJoue > 0) {
            	for (StatMatchSerieEntry statMatchSerie : statMatchSerieEntries) {
            		if (statMatchSerie.getButDom() == statMatchSerie.getButExt()) {
            			nbMatchN += 1;
            			if (statMatchSerie.getNomClubDom().compareTo(statMatchActivity.getNomClubDomicile()) == 0) {
            				nbMatchJoueDom += 1;
            				nbMatchDomN += 1;
            			}
            		} else if ((statMatchSerie.getButDom() > statMatchSerie.getButExt() && statMatchSerie.getNomClubDom().compareTo(statMatchActivity.getNomClubDomicile()) == 0)
            				|| (statMatchSerie.getButExt() > statMatchSerie.getButDom() && statMatchSerie.getNomClubExt().compareTo(statMatchActivity.getNomClubDomicile()) == 0)) {
            			nbMatchG += 1;
            			if (statMatchSerie.getNomClubDom().compareTo(statMatchActivity.getNomClubDomicile()) == 0) {
            				nbMatchJoueDom += 1;
            				nbMatchDomG += 1;
            			}
            		} else {
            			nbMatchP += 1;
            			if (statMatchSerie.getNomClubDom().compareTo(statMatchActivity.getNomClubDomicile()) == 0) {
            				nbMatchJoueDom += 1;
            				nbMatchDomP += 1;
            			}            			
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
            	statMatchConfrontationTitreTextView.setText("Confrontation globale (" + String.valueOf((int) nbMatchJoue) + " match" + titreCompl);
            	if (nbMatchJoueDom > 0) {
	            	statMatchConfrontation1DomTextView.setWidth((int) ((nbMatchDomG / nbMatchJoueDom) * intLargeur));
	            	statMatchConfrontationNDomTextView.setWidth((int) ((nbMatchDomN / nbMatchJoueDom) * intLargeur));
	            	statMatchConfrontation2DomTextView.setWidth((int) ((nbMatchDomP / nbMatchJoueDom) * intLargeur));
	            	if (nbMatchJoueDom > 1) {
	            		titreCompl = "s)";
	            	} else {
	            		titreCompl = ")";
	            	}
	            	statMatchConfrontationTitreDomTextView.setText("Confrontation à " + statMatchActivity.getNomClubDomicile() + " (" + String.valueOf((int) nbMatchJoueDom) + " match" + titreCompl);
            	}
            } else { 
            	statMatchConfrontation1TextView.setVisibility(View.GONE);
            	statMatchConfrontationNTextView.setVisibility(View.GONE);
            	statMatchConfrontation2TextView.setVisibility(View.GONE);
            	statMatchConfrontationTitreTextView.setText("Confrontation globale");
                statMatchConfrontationMessageTextView.setText("Historique non disponible");
                statMatchConfrontationMessageTextView.setVisibility(View.VISIBLE);
            }
            if (nbMatchJoueDom == 0) {
            	statMatchConfrontation1DomTextView.setVisibility(View.GONE);
            	statMatchConfrontationNDomTextView.setVisibility(View.GONE);
            	statMatchConfrontation2DomTextView.setVisibility(View.GONE);
            	statMatchConfrontationTitreDomTextView.setText("Confrontation à " + statMatchActivity.getNomClubDomicile());
                statMatchConfrontationMessageDomTextView.setText("Historique non disponible");
                statMatchConfrontationMessageDomTextView.setVisibility(View.VISIBLE);            	
            }
        
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            super.onPostExecute(success);
        }
    }	
	
	
	// affiche la forme domicile/extérieur sur la saison pour les 2 clubs
    private void afficherForme(View convertView, ClassementClubEntry infoClubEntryDom, ClassementClubEntry infoClubEntryExt) {

    	TextView statMatchFormeDom1TextView = (TextView) convertView.findViewById(R.id.statMatchFormeDom1);
    	TextView statMatchFormeDomNTextView = (TextView) convertView.findViewById(R.id.statMatchFormeDomN);
    	TextView statMatchFormeDom2TextView = (TextView) convertView.findViewById(R.id.statMatchFormeDom2);
    	TextView statMatchFormeExt1TextView = (TextView) convertView.findViewById(R.id.statMatchFormeExt1);
    	TextView statMatchFormeExtNTextView = (TextView) convertView.findViewById(R.id.statMatchFormeExtN);
    	TextView statMatchFormeExt2TextView = (TextView) convertView.findViewById(R.id.statMatchFormeExt2);
    	LinearLayout statMatchFormeLayout = (LinearLayout) convertView.findViewById(R.id.statMatchFormeLayout);
    	
    	android.view.Display display = ((android.view.WindowManager)statMatchActivity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();    	
    	// Moitié de la largeur de l'écran - 2x(largeur min d'une valeur forme) 
    	int intLargeur = (display.getWidth() / 2) - 10;
    	double intMatchJoue;
    	
    	statMatchFormeLayout.setVisibility(View.VISIBLE);
    	statMatchFormeDom1TextView.setBackgroundResource(R.drawable.tendance_vert);
    	statMatchFormeDom1TextView.setTextColor(Color.BLACK);
    	statMatchFormeDomNTextView.setBackgroundResource(R.drawable.tendance_orange);
    	statMatchFormeDomNTextView.setTextColor(Color.BLACK);
    	statMatchFormeDom2TextView.setBackgroundResource(R.drawable.tendance_rouge);
    	statMatchFormeDom2TextView.setTextColor(Color.BLACK);
    	statMatchFormeExt1TextView.setBackgroundResource(R.drawable.tendance_vert);
    	statMatchFormeExt1TextView.setTextColor(Color.BLACK);
    	statMatchFormeExtNTextView.setBackgroundResource(R.drawable.tendance_orange);
    	statMatchFormeExtNTextView.setTextColor(Color.BLACK);
    	statMatchFormeExt2TextView.setBackgroundResource(R.drawable.tendance_rouge);
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
    private void afficherClassementDetail(View convertView, ClassementClubEntry infoClubEntryDom, ClassementClubEntry infoClubEntryExt) {
        List<StatMatchClassementEntry> StatMatchClassementEntries = new ArrayList<StatMatchClassementEntry>();
        StatMatchClassementEntry statMatchClassementEntry;

        LinearLayout statMatchClassementLayout = (LinearLayout) convertView.findViewById(R.id.statMatchClassementLayout);
        ListView statMatchClassementListView = (ListView) convertView.findViewById(R.id.statMatchClassementList);
        
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
    	
		StatMatchClassementAdapter adapter = new StatMatchClassementAdapter(convertView.getContext(), R.layout.stat_match_classement_item, StatMatchClassementEntries);
	    statMatchClassementListView.setAdapter(adapter);
	    adapter.notifyDataSetChanged();

    }	
    
    
	// Cotes du match
    private class CoteMatchTask extends AsyncTask<String, Void, Boolean> {

        private List<CoteMatchEntry> coteMatchEntries;
        private ProgressDialog dialog;
        private View convertView;

        private CoteMatchTask(View convertView) {
        	this.convertView = convertView; 
            dialog = new ProgressDialog(statMatchActivity);
        }

        protected void onPreExecute() {
            this.dialog.setMessage("Chargement");
            this.dialog.show();
        }

        @Override
        protected Boolean doInBackground(final String... args) {

            coteMatchEntries = getCoteMatch(Integer.parseInt(statMatchActivity.getIdMatch()));

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
        	
        	TextView statMatchCoteDomTextView = (TextView) convertView.findViewById(R.id.statMatchCoteDom);
        	TextView statMatchCoteNulTextView = (TextView) convertView.findViewById(R.id.statMatchCoteNul);
        	TextView statMatchCoteExtTextView = (TextView) convertView.findViewById(R.id.statMatchCoteExt);
        	LinearLayout statMatchCoteLayout = (LinearLayout) convertView.findViewById(R.id.statMatchCoteLayout);
        	
        	int tabcote[] = new int [3];
        	int sommeCote = 0; 
        	
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
	        	sommeCote += coteMatchEntry.getCote();
	        }
	        
	        statMatchCoteDomTextView.setBackgroundResource(R.drawable.tendance_vert);
        	statMatchCoteDomTextView.setTextColor(Color.BLACK);
        	statMatchCoteNulTextView.setBackgroundResource(R.drawable.tendance_orange);
        	statMatchCoteNulTextView.setTextColor(Color.BLACK);
        	statMatchCoteExtTextView.setBackgroundResource(R.drawable.tendance_rouge);
        	statMatchCoteExtTextView.setTextColor(Color.BLACK);
	        
        	android.view.Display display = ((android.view.WindowManager)statMatchActivity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();    	
        	int intLargeur = display.getWidth() - 10;
        	statMatchCoteDomTextView.setWidth((int) (((double) tabcote[0] / sommeCote) * intLargeur));
        	statMatchCoteNulTextView.setWidth((int) (((double) tabcote[1] / sommeCote) * intLargeur));
        	statMatchCoteExtTextView.setWidth((int) (((double) tabcote[2] / sommeCote) * intLargeur));
        	
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
        private View convertView;

        private InfoClubTask(View convertView) {
        	this.convertView = convertView;
            dialog = new ProgressDialog(statMatchActivity);
        }

        protected void onPreExecute() {
            this.dialog.setMessage("Chargement");
            this.dialog.show();
        }

        @Override
        protected Boolean doInBackground(final String... args) {

            infoClubEntryDom = getInfoClub(statMatchActivity.getNomClubDomicile());
            infoClubEntryExt = getInfoClub(statMatchActivity.getNomClubExterieur());

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
        	
        	AsyncImageView statMatchLogoDomAsyncImageView = (AsyncImageView) convertView.findViewById(R.id.statMatchLogoDom);
        	TextView statMatchEquipeDomTextView = (TextView) convertView.findViewById(R.id.statMatchEquipeDom);
        	TextView statMatchPlaceDomTextView = (TextView) convertView.findViewById(R.id.statMatchPlaceDom);
            AsyncImageView statMatchLogoExtAsyncImageView = (AsyncImageView) convertView.findViewById(R.id.statMatchLogoExt);
            TextView statMatchEquipeExtTextView = (TextView) convertView.findViewById(R.id.statMatchEquipeExt);
            TextView statMatchPlaceExtTextView = (TextView) convertView.findViewById(R.id.statMatchPlaceExt);
        	
        	statMatchLogoDomAsyncImageView.setUrl(infoClubEntryDom.getUrlLogo());
        	statMatchEquipeDomTextView.setText(infoClubEntryDom.getClub());
        	statMatchPlaceDomTextView.setText("(" + String.valueOf(infoClubEntryDom.getPlace()) + ")");
            
        	statMatchLogoExtAsyncImageView.setUrl(infoClubEntryExt.getUrlLogo());
        	statMatchEquipeExtTextView.setText(infoClubEntryExt.getClub());
        	statMatchPlaceExtTextView.setText("(" + String.valueOf(infoClubEntryExt.getPlace()) + ")");       	

        	if (currentPage == NUM_PAGE_COTE) {
        		afficherForme(convertView, infoClubEntryDom, infoClubEntryExt);
        	} else if (currentPage == NUM_PAGE_CLASSEMENT) {
        		afficherClassementDetail(convertView, infoClubEntryDom, infoClubEntryExt);
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
            convertView = statMatchActivity.getLayoutInflater().inflate(R.layout.stat_match, parent, false);
        }

        afficherPage(position, convertView);

        return convertView;

	}
}
