package fr.pronoschallenge;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import fr.pronoschallenge.rest.QueryBuilder;
import fr.pronoschallenge.rest.RestClient;
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

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.classement);
		
		// Obtain handles to UI objects
		classementListView = (ListView) findViewById(R.id.classementList);
	}
	
	
	@Override
	protected void onStart() {
		classementType = (String) this.getIntent().getExtras().get("fr.pronoschallenge.ClassementType");

		//classementTitleTextView.setText(getString(R.string.title_classement) + " " + this.getIntent().getExtras().get("fr.pronoschallenge.ClassementTitle"));
		setTitle(getString(R.string.title_classement) + " " + this.getIntent().getExtras().get("fr.pronoschallenge.ClassementTitle"));

        AsyncTask task = new ClassementTask(this).execute(classementType);

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
            ClassementAdapter adapter = new ClassementAdapter(activity,	R.layout.classement_item, classementEntries);
            classementListView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            super.onPostExecute(success);
        }
    }
}