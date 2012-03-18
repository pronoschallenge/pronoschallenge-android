package fr.pronoschallenge.classement.club;

import fr.pronoschallenge.R;
import fr.pronoschallenge.club.ClubPagedViewActivity;
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
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class ClassementClubActivity extends GDActivity {
	
	private ListView classementClubListView;
    private TextView messageClubTextView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setActionBarContentView(R.layout.classement_club);
		
		// Obtain handles to UI objects
		classementClubListView = (ListView) findViewById(R.id.classementClubList);
        messageClubTextView = (TextView) findViewById(R.id.classementClubMessage);
        
        classementClubListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
			    int position, long id) {
				ClassementClubEntry classementClubEntry = (ClassementClubEntry) classementClubListView.getItemAtPosition(position);
					Bundle objetbunble = new Bundle();
					objetbunble.putString("club", classementClubEntry.getClub());
					Intent intent = new Intent(ClassementClubActivity.this,	ClubPagedViewActivity.class);
					intent.putExtras(objetbunble);
					startActivity(intent);
			}
        });
        
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

    // Classement limité au : 1er / dernier / club en cours / clubs immédiatement au dessus et au dessous
	public static List<ClassementClubEntry> getclassementClub(String nomClub, Context context) {
		List<ClassementClubEntry> classementClubEntries = new ArrayList<ClassementClubEntry>();

		String strClassement = RestClient.get(new QueryBuilder(context.getAssets(), "/rest/classementL1/").getUri());

		try {
			// A Simple JSONObject Creation
	        JSONObject json = new JSONObject(strClassement);

	        // A Simple JSONObject Parsing
	        JSONArray classementArray = json.getJSONArray("classementL1");
	        int intPlace = 0;        
	        ClassementClubEntry classementClubEntryPrec = new ClassementClubEntry();
	        boolean bolAfficheNull = false;
	        for(int i=0;i<classementArray.length();i++)
	        {
	        	JSONObject jsonClassementEntry = classementArray.getJSONObject(i);

	        	ClassementClubEntry classementClubEntry = new ClassementClubEntry();	        	
	        	classementClubEntry.setClub(jsonClassementEntry.getString("club"));
	        	classementClubEntry.setPoints(jsonClassementEntry.getInt("points"));
	        	classementClubEntry.setDiff(jsonClassementEntry.getInt("diff"));
	        	classementClubEntry.setMatchJoue(jsonClassementEntry.getInt("joues"));
	        	classementClubEntry.setUrlLogo(jsonClassementEntry.getString("url_logo"));
	        	
	        	if (classementClubEntry.getPoints() != classementClubEntryPrec.getPoints() 
	        			|| classementClubEntry.getDiff() != classementClubEntryPrec.getDiff()) {
	        		intPlace = i+1;	        		
	        	}
	        	classementClubEntry.setPlace(intPlace);
	        	
	        	if (nomClub.compareTo("") == 0) {
	        		classementClubEntries.add(classementClubEntry);
	        	}
	        	else {
		        	if (i == 0 || i == classementArray.length() - 1 || classementClubEntryPrec.getClub().compareTo(nomClub) == 0) {
		        		classementClubEntries.add(classementClubEntry);
		        		bolAfficheNull = true;
		        	} else if (classementClubEntry.getClub().compareTo(nomClub) == 0) {
		        		if (i != 1) {
		        			classementClubEntries.add(classementClubEntryPrec);
		        		}
		        		classementClubEntries.add(classementClubEntry);
		        		bolAfficheNull = true;
		        	} else if (bolAfficheNull) {
		        		ClassementClubEntry classementClubEntryNull = new ClassementClubEntry();
		        		classementClubEntryNull.setClub("...");
		        		classementClubEntries.add(classementClubEntryNull);
		        		bolAfficheNull = false;
		        	}	        		
	        	}
	        	classementClubEntryPrec = classementClubEntry;

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

            classementClubEntries = ClassementClubActivity.getclassementClub("", activity);

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
