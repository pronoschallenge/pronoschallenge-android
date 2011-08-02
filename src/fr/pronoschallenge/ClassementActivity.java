package fr.pronoschallenge;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;
import fr.pronoschallenge.rest.QueryBuilder;
import fr.pronoschallenge.rest.RestClient;
import fr.pronoschallenge.util.NetworkUtil;
import greendroid.app.GDActivity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.widget.ListView;

public class ClassementActivity extends GDActivity {

	final static String CLASSEMENT_TYPE_GENERAL = "general";
	final static String CLASSEMENT_TYPE_HOURRA = "hourra";
	final static String CLASSEMENT_TYPE_MIXTE = "mixte";
	
	private String classementType;
	private ListView classementListView;
    private TextView messageTextView;

    private AlertDialog dialog;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.classement);
		
		// Obtain handles to UI objects
		classementListView = (ListView) findViewById(R.id.classementList);
        messageTextView = (TextView) findViewById(R.id.classementMessage);
	}
	
	
	@Override
	protected void onStart() {
		classementType = (String) this.getIntent().getExtras().get("fr.pronoschallenge.ClassementType");

		if(NetworkUtil.isConnected(this.getApplicationContext())) {
            setTitle(getString(R.string.title_classement) + " " + this.getIntent().getExtras().get("fr.pronoschallenge.ClassementTitle"));

            AsyncTask task = new ClassementTask(this).execute(classementType);
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

	private List<ClassementEntry> getClassement(String type) {
		List<ClassementEntry> classementEntries = new ArrayList<ClassementEntry>();

		String strClassement = RestClient.get(new QueryBuilder(this.getAssets(), "/rest/classement/" + type).getUri());

		try {
			// A Simple JSONObject Creation
	        JSONObject json = new JSONObject(strClassement);

	        // A Simple JSONObject Parsing
	        JSONArray classementArray = json.getJSONArray("classement");
	        for(int i=0;i<classementArray.length();i++)
	        {
	        	JSONObject jsonClassementEntry = classementArray.getJSONObject(i);

	        	ClassementEntry classementEntry = new ClassementEntry();
	        	classementEntry.setPlace(jsonClassementEntry.getInt("place"));
	        	classementEntry.setPseudo(jsonClassementEntry.getString("pseudo"));
	        	classementEntry.setPoints(jsonClassementEntry.getDouble("points"));
	        	classementEntries.add(classementEntry);
	        }

		} catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

		return classementEntries;
	}

	public String getClassementType() {
		return classementType;
	}

	public void setClassementType(String classementType) {
		this.classementType = classementType;
	}

    private class ClassementTask extends AsyncTask<String, Void, Boolean> {

        final private ClassementActivity activity;
        private List<ClassementEntry> classementEntries;
        private ProgressDialog dialog;

        private ClassementTask(ClassementActivity activity) {
            this.activity = activity;
            dialog = new ProgressDialog(activity);
        }

        protected void onPreExecute() {
            this.dialog.setMessage("Chargement");
            this.dialog.show();
        }

        @Override
        protected Boolean doInBackground(final String... args) {

            classementEntries = activity.getClassement(args[0]);

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if(classementEntries.size() > 0) {
                ClassementAdapter adapter = new ClassementAdapter(activity,	R.layout.classement_item, classementEntries);
                classementListView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                activity.messageTextView.setVisibility(View.INVISIBLE);
                activity.classementListView.setVisibility(View.VISIBLE);
            } else {
                activity.classementListView.setVisibility(View.INVISIBLE);
                activity.messageTextView.setText("Classement non disponible");
                activity.messageTextView.setVisibility(View.VISIBLE);
            }
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            super.onPostExecute(success);
        }
    }
}