package fr.pronoschallenge;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ListView;
import fr.pronoschallenge.rest.QueryBuilder;
import fr.pronoschallenge.rest.RestClient;
import greendroid.app.GDActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class PronosActivity extends GDActivity {

	private ListView pronosListView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.pronos);
		
		// Obtain handles to UI objects
		pronosListView = (ListView) findViewById(R.id.pronoList);
	}
	
	
	@Override
	protected void onStart() {
		setTitle(getString(R.string.title_pronos));

        AsyncTask task = new PronosTask(this).execute("tomtom");

		super.onStart();
	}

	private List<PronoEntry> getPronos(String userName) {
		List<PronoEntry> pronoEntries = new ArrayList<PronoEntry>();

		String strPronos = RestClient.get(new QueryBuilder(this.getAssets(), "/rest/pronos/" + userName).getUri());

        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

		try {
			// A Simple JSONObject Creation
	        JSONObject json = new JSONObject(strPronos);

	        // A Simple JSONObject Parsing
	        JSONArray pronosArray = json.getJSONArray("pronos");
	        for(int i=0;i<pronosArray.length();i++)
	        {
	        	JSONObject jsonPronoEntry = pronosArray.getJSONObject(i);

	        	PronoEntry pronoEntry = new PronoEntry();
	        	pronoEntry.setId(jsonPronoEntry.getInt("id"));
                try {
                    pronoEntry.setDate(formatter.parse(jsonPronoEntry.getString("date")));
                } catch(ParseException pe) {
                    pronoEntry.setDate(null);
                }
	        	pronoEntry.setEquipeDom(jsonPronoEntry.getString("equipe_dom"));
                pronoEntry.setEquipeExt(jsonPronoEntry.getString("equipe_ext"));
	        	pronoEntry.setProno(jsonPronoEntry.getString("prono"));
	        	pronoEntries.add(pronoEntry);
	        }

		} catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

		return pronoEntries;
	}

    private class PronosTask extends AsyncTask<String, Void, Boolean> {

        final private PronosActivity activity;
        private List<PronoEntry> pronoEntries;
        private ProgressDialog dialog;

        private PronosTask(PronosActivity activity) {
            this.activity = activity;
            dialog = new ProgressDialog(activity);
        }

        protected void onPreExecute() {
            this.dialog.setMessage("Chargement");
            this.dialog.show();
        }

        @Override
        protected Boolean doInBackground(final String... args) {

            pronoEntries = activity.getPronos(args[0]);

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            PronosAdapter adapter = new PronosAdapter(activity,	R.layout.pronos_item, pronoEntries);
            pronosListView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            super.onPostExecute(success);
        }
    }
}