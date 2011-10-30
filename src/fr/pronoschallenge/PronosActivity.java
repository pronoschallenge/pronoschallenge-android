package fr.pronoschallenge;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ListView;
import fr.pronoschallenge.auth.LoginActivity;
import fr.pronoschallenge.rest.QueryBuilder;
import fr.pronoschallenge.rest.RestClient;
import fr.pronoschallenge.util.NetworkUtil;
import greendroid.app.GDActivity;
import greendroid.widget.ActionBar;
import greendroid.widget.ActionBarItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class PronosActivity extends GDActivity {

	private ListView pronosListView;

    private AlertDialog dialog;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        String userName = PreferenceManager.getDefaultSharedPreferences(this).getString("username", null);
        String password = PreferenceManager.getDefaultSharedPreferences(this).getString("password", null);
        if(userName == null || password == null) {
            startActivityForResult(new Intent(this, LoginActivity.class), 1);
        }

		setActionBarContentView(R.layout.pronos);
		
		// Obtain handles to UI objects
		pronosListView = (ListView) findViewById(R.id.pronoList);
	}
	
	
	@Override
	protected void onStart() {
		setTitle(getString(R.string.title_pronos));

        if(NetworkUtil.isConnected(this.getApplicationContext())) {
            String userName = PreferenceManager.getDefaultSharedPreferences(this).getString("username", null);

            AsyncTask task = new PronosTask(this).execute(userName);
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1) {
            String userName = PreferenceManager.getDefaultSharedPreferences(this).getString("username", null);
            String password = PreferenceManager.getDefaultSharedPreferences(this).getString("password", null);
            if(userName == null || password == null) {
                finish();
            }
        }
    }

	private List<PronoEntry> getPronos(String userName) {
		List<PronoEntry> pronoEntries = new ArrayList<PronoEntry>();

		String strPronos = RestClient.get(new QueryBuilder(this.getAssets(), "/rest/pronos/" + userName).getUri());

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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