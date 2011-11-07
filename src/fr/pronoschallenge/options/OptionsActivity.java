package fr.pronoschallenge.options;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import fr.pronoschallenge.R;
import fr.pronoschallenge.auth.LoginActivity;
import fr.pronoschallenge.rest.QueryBuilder;
import fr.pronoschallenge.rest.RestClient;
import fr.pronoschallenge.util.NetworkUtil;
import greendroid.app.GDActivity;
import greendroid.app.GDApplication;
import greendroid.app.GDTabActivity;
import greendroid.util.Config;
import greendroid.widget.ActionBar;
import greendroid.widget.ActionBarItem;
import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by IntelliJ IDEA.
 * User: Thomas Delhoménie
 * Date: 02/11/11
 * Time: 22:01
 */
public class OptionsActivity extends PreferenceActivity {

    private static final String LOG_TAG = OptionsActivity.class.getSimpleName();

    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String userName = PreferenceManager.getDefaultSharedPreferences(this).getString("username", null);
        String password = PreferenceManager.getDefaultSharedPreferences(this).getString("password", null);
        if(userName == null || password == null) {
            startActivityForResult(new Intent(this, LoginActivity.class), 1);
        }

        setTitle(getString(R.string.title));

        addPreferencesFromResource(R.xml.options);
        setContentView(R.layout.options);

        actionBar = (ActionBar) findViewById(R.id.gd_action_bar);
        actionBar.setOnActionBarListener(mActionBarListener);
        actionBar.setTitle(getString(R.string.title_options));

        findPreference("strategie").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                new UpdateStrategieTask(OptionsActivity.this).execute((String) newValue);
                return true;
            }
        });


        if(NetworkUtil.isConnected(this.getApplicationContext())) {
            new GetStrategieTask(this).execute(userName);
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

    }

    /**
     * Copied from GDActivity
     * @return
     */
    public GDApplication getGDApplication() {
        return (GDApplication) getApplication();
    }

    /**
     * Copied from GDActivity
     * @return
     */
    private ActionBar.OnActionBarListener mActionBarListener = new ActionBar.OnActionBarListener() {
        public void onActionBarItemClicked(int position) {
            if (position == ActionBar.OnActionBarListener.HOME_ITEM) {

                final GDApplication app = getGDApplication();
                final Class<?> klass = app.getHomeActivityClass();
                if (klass != null && !klass.equals(OptionsActivity.this.getClass())) {
                    if (Config.GD_INFO_LOGS_ENABLED) {
                        Log.i(LOG_TAG, "Going back to the home activity");
                    }
                    Intent homeIntent = new Intent(OptionsActivity.this, klass);
                    homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(homeIntent);
                }

            } else {
                if (!onHandleActionBarItemClick(actionBar.getItem(position), position)) {
                    if (Config.GD_WARNING_LOGS_ENABLED) {
                        Log.w(LOG_TAG, "Click on item at position " + position + " dropped down to the floor");
                    }
                }
            }
        }
    };

    /**
     * Copied from GDActivity
     * @return
     */
    public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
        return false;
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

    /**
     * Récupération de la stratégie de l'utilisateur
     * @return
     */
    private String getStrategie() {
        String userName = PreferenceManager.getDefaultSharedPreferences(this).getString("username", null);
        String password = PreferenceManager.getDefaultSharedPreferences(this).getString("password", null);
        String strategie = RestClient.get(new QueryBuilder(this.getAssets(), "/rest/strategie/" + userName).getUri(), userName, password);
        return strategie;
    }

    /**
     * Modification de la stratégie de l'utilisateur
     * @param strategie
     * @return
     */
    private void updateStrategie(String strategie) {
        String userName = PreferenceManager.getDefaultSharedPreferences(this).getString("username", null);
        String password = PreferenceManager.getDefaultSharedPreferences(this).getString("password", null);
        HttpResponse response = RestClient.postData(new QueryBuilder(this.getAssets(), "/rest/strategie/" + userName).getUri(), strategie, userName, password);
    }

    /**
     * Tache permettant de récupérer la stratégie de l'utilisateur
     */
    private class GetStrategieTask extends AsyncTask<String, Void, Boolean> {

        final private OptionsActivity activity;
        private ProgressDialog dialog;
        private String strategie;

        private GetStrategieTask(OptionsActivity activity) {
            this.activity = activity;
            dialog = new ProgressDialog(activity);
        }

        protected void onPreExecute() {
            this.dialog.setMessage("Chargement");
            this.dialog.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                String jsonStrategie = activity.getStrategie();
                JSONObject jsonStrategieObject = new JSONObject(jsonStrategie);
                strategie = jsonStrategieObject.getString("strategie");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {

            SharedPreferences.Editor editor = findPreference("strategie").getEditor();
            editor.putString("strategie", strategie);
            editor.commit();

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            super.onPostExecute(success);
        }
    }

     /**
      * Tache permettant de modifier la stratégie de l'utilisateur
      */
     private class UpdateStrategieTask extends AsyncTask<String, Void, Boolean> {

         final private OptionsActivity activity;
         private ProgressDialog dialog;
         private String strategie;

         private UpdateStrategieTask(OptionsActivity activity) {
             this.activity = activity;
             dialog = new ProgressDialog(activity);
         }

         protected void onPreExecute() {
             this.dialog.setMessage("Chargement");
             this.dialog.show();
         }

         @Override
         protected Boolean doInBackground(String... strings) {
             activity.updateStrategie(strings[0]);

             return true;
         }

         @Override
         protected void onPostExecute(Boolean success) {
             if (dialog.isShowing()) {
                 dialog.dismiss();
             }
             super.onPostExecute(success);
         }
     }

}
