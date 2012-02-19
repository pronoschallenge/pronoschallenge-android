package fr.pronoschallenge.club;

import fr.pronoschallenge.R;
import fr.pronoschallenge.classement.club.ClassementClubActivity;
import fr.pronoschallenge.classement.club.ClassementClubAdapter;
import fr.pronoschallenge.classement.club.ClassementClubEntry;
import fr.pronoschallenge.rest.QueryBuilder;
import fr.pronoschallenge.rest.RestClient;
import greendroid.widget.AsyncImageView;
import greendroid.widget.PagedAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ClubPagedViewAdapter extends PagedAdapter {

	static int PAGE_COUNT = 2;
	static int NUM_PAGE_CLASSEMENT = 0;
	static int NUM_PAGE_RESULTAT = 1;	
	
	private ClubPagedViewActivity clubActivity;
    private int currentPage;

    public ClubPagedViewAdapter(ClubPagedViewActivity clubActivity) {
    	super();
    	this.clubActivity = clubActivity;
    }
	
	private void afficherPage(int numPage, View convertView) {

		LinearLayout clubClassementEnteteLayout = (LinearLayout) convertView.findViewById(R.id.clubClassementEnteteLayout);
		ListView clubClassementList = (ListView) convertView.findViewById(R.id.clubClassementList);
		LinearLayout clubEvolutionEnteteLayout = (LinearLayout) convertView.findViewById(R.id.clubEvolutionEnteteLayout);
		LinearLayout clubResultatLayout = (LinearLayout) convertView.findViewById(R.id.clubResultatLayout);
		
		clubClassementEnteteLayout.setVisibility(View.GONE);
		clubClassementList.setVisibility(View.GONE);		
		clubEvolutionEnteteLayout.setVisibility(View.GONE);
		clubResultatLayout.setVisibility(View.GONE);
		
		currentPage = numPage;
				
		if (currentPage == NUM_PAGE_CLASSEMENT) {
			new InfoClubTask(convertView).execute("");
			new ClassementClubTask(convertView).execute("");
		} else if (currentPage == NUM_PAGE_RESULTAT) {
			new CalendrierTask(convertView).execute("");
		}
	}
	
	// Information d'un club
	private ClassementClubEntry getInfoClub(String nomClub) {
		
		ClassementClubEntry infoClubEntry = new ClassementClubEntry(); 

		String strInfoClub = RestClient.get(new QueryBuilder(clubActivity.getAssets(), "/rest/infoClub/" + nomClub + "/").getUri());

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
	
	
	// Calendrier d'un club
	private List<ClubCalendrierEntry> getCalendrier() {
		List<ClubCalendrierEntry> clubCalendrierEntries = new ArrayList<ClubCalendrierEntry>();

		String strClubCalendrier = RestClient.get(new QueryBuilder(clubActivity.getAssets(), "/rest/calendrierClub/" + clubActivity.getNomClub()).getUri());

		try {
			// A Simple JSONObject Creation
	        JSONObject json = new JSONObject(strClubCalendrier);

	        // A Simple JSONObject Parsing
	        JSONArray calendrierArray = json.getJSONArray("calendrierClub");
	        for(int i = 0; i < calendrierArray.length(); i++) {
	        	JSONObject jsonSerieEntry = calendrierArray.getJSONObject(i);

	        	ClubCalendrierEntry clubCalendrierEntry = new ClubCalendrierEntry();	        	
	        	clubCalendrierEntry.setButDom(jsonSerieEntry.getString("butDom"));
	        	clubCalendrierEntry.setButExt(jsonSerieEntry.getString("butExt"));
	        	clubCalendrierEntry.setMatchDomExt(jsonSerieEntry.getString("type"));
        		clubCalendrierEntry.setNomClubDom(jsonSerieEntry.getString("clubDom"));
	        	clubCalendrierEntry.setNomClubExt(jsonSerieEntry.getString("clubExt"));
	        	if (clubCalendrierEntry.getButDom().compareTo("null") == 0) {
	        		clubCalendrierEntry.setTypeResultat("");
	        	} else {
		        	if (clubCalendrierEntry.getMatchDomExt().compareTo("D") == 0) {
		        		if (Integer.parseInt(clubCalendrierEntry.getButDom()) == Integer.parseInt(clubCalendrierEntry.getButExt())) { 
		        			clubCalendrierEntry.setTypeResultat("N");
		        		} else if (Integer.parseInt(clubCalendrierEntry.getButDom()) > Integer.parseInt(clubCalendrierEntry.getButExt())) {
		        			clubCalendrierEntry.setTypeResultat("V");
		        		} else {
		        			clubCalendrierEntry.setTypeResultat("D");
		        		}
		        	} else {
		        		if (Integer.parseInt(clubCalendrierEntry.getButDom()) == Integer.parseInt(clubCalendrierEntry.getButExt())) { 
		        			clubCalendrierEntry.setTypeResultat("N");
		        		} else if (Integer.parseInt(clubCalendrierEntry.getButDom()) > Integer.parseInt(clubCalendrierEntry.getButExt())) {
		        			clubCalendrierEntry.setTypeResultat("D");
		        		} else {
		        			clubCalendrierEntry.setTypeResultat("V");
		        		}
		        	}	        		
	        	}
	        	clubCalendrierEntry.setNumJournee(jsonSerieEntry.getInt("numJour"));
	        	
	        	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        	try {
					clubCalendrierEntry.setDateMatch(formatter.parse(jsonSerieEntry.getString("date")));
				} catch (ParseException e) {
					clubCalendrierEntry.setDateMatch(null);
				}
	        	
	        	clubCalendrierEntries.add(clubCalendrierEntry);
	        }

		} catch (JSONException e) {
            e.printStackTrace();
        }

		return clubCalendrierEntries;
	}	
	
	// Calendrier du club
    private class CalendrierTask extends AsyncTask<String, Void, Boolean> {

        private List<ClubCalendrierEntry> clubCalendrierEntries;
        private ProgressDialog dialog;
        private View convertView;

        private CalendrierTask(View convertView) {
        	this.convertView = convertView;
            dialog = new ProgressDialog(clubActivity);
        }

        protected void onPreExecute() {
            this.dialog.setMessage("Chargement");
            this.dialog.show();
        }

        @Override
        protected Boolean doInBackground(final String... args) {
            clubCalendrierEntries = getCalendrier();
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            
        	LinearLayout clubResultatLayout = (LinearLayout) convertView.findViewById(R.id.clubResultatLayout);
        	ListView clubResultatList = (ListView) convertView.findViewById(R.id.clubResultatList);;
        	
        	clubResultatLayout.setVisibility(View.VISIBLE);
        	
            if(clubCalendrierEntries.size() > 0) {
                ClubCalendrierAdapter adapter = new ClubCalendrierAdapter(convertView.getContext(), R.layout.club_calendrier_item, clubCalendrierEntries);
                clubResultatList.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                clubResultatList.setVisibility(View.VISIBLE);
            }

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            super.onPostExecute(success);
        }
    }	

    
    // Informations du club
    private class InfoClubTask extends AsyncTask<String, Void, Boolean> {

        private ClassementClubEntry infoClubEntry;
        private ProgressDialog dialog;
        private View convertView;

        private InfoClubTask(View convertView) {
        	this.convertView = convertView;
            dialog = new ProgressDialog(clubActivity);
        }

        protected void onPreExecute() {
            this.dialog.setMessage("Chargement");
            this.dialog.show();
        }

        @Override
        protected Boolean doInBackground(final String... args) {

            infoClubEntry = getInfoClub(clubActivity.getNomClub());

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
        	
        	AsyncImageView clubLogoAsyncImageView = (AsyncImageView) convertView.findViewById(R.id.clubLogo);
        	TextView clubNomTextView = (TextView) convertView.findViewById(R.id.clubNom);
        	
        	clubLogoAsyncImageView.setUrl(infoClubEntry.getUrlLogo());
        	clubNomTextView.setText(infoClubEntry.getClub());
        	
        	if (dialog.isShowing()) {
                dialog.dismiss();
            }
            super.onPostExecute(success);
        }
    }

	
    private class ClassementClubTask extends AsyncTask<String, Void, Boolean> {

    	private View convertView;
        private List<ClassementClubEntry> classementClubEntries;
        private ProgressDialog dialog;

        private ClassementClubTask(View convertView) {
            this.convertView = convertView;
            dialog = new ProgressDialog(clubActivity);
        }

        protected void onPreExecute() {
            this.dialog.setMessage("Chargement");
            this.dialog.show();
        }

        @Override
        protected Boolean doInBackground(final String... args) {
            classementClubEntries = ClassementClubActivity.getclassementClub(clubActivity.getNomClub(), clubActivity);
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
        	LinearLayout clubClassementEnteteLayout = (LinearLayout) convertView.findViewById(R.id.clubClassementEnteteLayout);
            clubClassementEnteteLayout.setVisibility(View.VISIBLE);

        	ListView clubClassementList = (ListView) convertView.findViewById(R.id.clubClassementList);;

        	if(classementClubEntries.size() > 0) {
                ClassementClubAdapter adapter = new ClassementClubAdapter(clubActivity,	R.layout.classement_club_item, classementClubEntries);
                clubClassementList.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                clubClassementList.setVisibility(View.VISIBLE);
            } else {
            	clubClassementList.setVisibility(View.GONE);
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
            convertView = clubActivity.getLayoutInflater().inflate(R.layout.club, parent, false);
        }

        afficherPage(position, convertView);

        return convertView;

	}
}
