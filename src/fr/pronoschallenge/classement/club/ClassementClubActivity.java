package fr.pronoschallenge.classement.club;

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

public class ClassementClubActivity extends GDActivity {
	
	private ListView classementClubListView;
    private TextView messageClubTextView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.classement_club);
		
		// Obtain handles to UI objects
		classementClubListView = (ListView) findViewById(R.id.classementClubList);
        messageClubTextView = (TextView) findViewById(R.id.classementClubMessage);

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
		
		new ClassementClubTask(this).execute("");
	}

	private List<ClassementClubEntry> getclassementClub() {
		List<ClassementClubEntry> classementClubEntries = new ArrayList<ClassementClubEntry>();

		String strClassement = RestClient.get(new QueryBuilder(this.getAssets(), "/rest/classementL1/").getUri());

		try {
			// A Simple JSONObject Creation
	        JSONObject json = new JSONObject(strClassement);

	        // A Simple JSONObject Parsing
	        JSONArray classementArray = json.getJSONArray("classementL1");
	        int intPlace = 0;        
	        int intPtsPrecedent = 0;
	        int intDiffPrecedent = 0;
	        for(int i=0;i<classementArray.length();i++)
	        {
	        	JSONObject jsonClassementEntry = classementArray.getJSONObject(i);

	        	ClassementClubEntry classementClubEntry = new ClassementClubEntry();	        	
	        	classementClubEntry.setClub(jsonClassementEntry.getString("club"));
	        	classementClubEntry.setPoints(jsonClassementEntry.getInt("points"));
	        	classementClubEntry.setDiff(jsonClassementEntry.getInt("diff"));
	        	classementClubEntry.setMatchJoue(jsonClassementEntry.getInt("joues"));
	        	classementClubEntry.setUrlLogo(jsonClassementEntry.getString("url_logo"));
	        	
	        	if (classementClubEntry.getPoints() != intPtsPrecedent || classementClubEntry.getDiff() != intDiffPrecedent) {
	        		intPlace = i+1;	        		
	        	} 
	        	classementClubEntry.setPlace(intPlace);
	        	
	        	classementClubEntries.add(classementClubEntry);
	        	
	        	intPtsPrecedent = classementClubEntry.getPoints();
	        	intDiffPrecedent = classementClubEntry.getDiff();
	        }

		} catch (JSONException e) {
            e.printStackTrace();
        }

		return classementClubEntries;
	}

    private class ClassementClubTask extends AsyncTask<String, Void, Boolean> {

        final private ClassementClubActivity activity;
        private List<ClassementClubEntry> classementClubEntries;
        private ProgressDialog dialog;

        private ClassementClubTask(ClassementClubActivity activity) {
            this.activity = activity;
            dialog = new ProgressDialog(activity);
        }

        protected void onPreExecute() {
            this.dialog.setMessage("Chargement");
            this.dialog.show();
        }

        @Override
        protected Boolean doInBackground(final String... args) {

            classementClubEntries = activity.getclassementClub();

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if(classementClubEntries.size() > 0) {
                ClassementClubAdapter adapter = new ClassementClubAdapter(activity,	R.layout.classement_club_item, classementClubEntries);
                classementClubListView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                activity.messageClubTextView.setVisibility(View.GONE);
                activity.classementClubListView.setVisibility(View.VISIBLE);
            } else {
                activity.classementClubListView.setVisibility(View.GONE);
                activity.messageClubTextView.setText("Classement non disponible");
                activity.messageClubTextView.setVisibility(View.VISIBLE);
            }
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            super.onPostExecute(success);
        }
    }
}
