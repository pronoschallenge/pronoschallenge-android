package fr.pronoschallenge.pronos;

import fr.pronoschallenge.auth.LoginActivity;
import fr.pronoschallenge.rest.QueryBuilder;
import fr.pronoschallenge.rest.RestClient;
import fr.pronoschallenge.util.NetworkUtil;
import greendroid.app.GDActivity;
import greendroid.widget.PagedView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.cyrilmottier.android.greendroid.R;

public class PronosActivity extends GDActivity {

	private PagedView pronosListView;

    private String userName;
    private String password;

    private List<List<PronoEntry>> pagedPronoEntries;

    private int currentPage = 0;
    private boolean nextPronosLoaded = false;
    private boolean previousPronosLoaded = false;
	
	private ImageView loaderView;
	private Animation rotateAnimation;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
        setTitle(getString(R.string.title_pronos));

        userName = PreferenceManager.getDefaultSharedPreferences(this).getString("username", null);
        password = PreferenceManager.getDefaultSharedPreferences(this).getString("password", null);
        if(userName == null || password == null) {
            startActivityForResult(new Intent(this, LoginActivity.class), 1);
        }

		setActionBarContentView(R.layout.pronos);
		
		// Obtain handles to UI objects
		pronosListView = (PagedView) findViewById(R.id.pronoList);
        pronosListView.setOnPageChangeListener(mOnPagedViewChangedListener);
		
		// Animation pour l'icone de chargement de la barre d'action
        rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.gd_rotate_loading);
        rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
            }
        });		
		
        // Icone de chargement de la barre d'action
        loaderView = new ImageView(this);
        final LinearLayout.LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
        loaderView.setLayoutParams(lp);
        loaderView.setImageResource(R.drawable.loading);
        loaderView.setPadding(10, 0, 10, 0);
        this.getActionBar().addView(loaderView);
        loaderView.setVisibility(View.GONE);		

        if(NetworkUtil.isConnected(this.getApplicationContext())) {
            new PronosTask(this).execute(userName);
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1) {
            userName = PreferenceManager.getDefaultSharedPreferences(this).getString("username", null);
            password = PreferenceManager.getDefaultSharedPreferences(this).getString("password", null);
            if(userName == null || password == null) {
                finish();
            }
        }
    }

    /**
     * Show the loading animation in the action bar
     */
    public void showLoader() {
        loaderView.clearAnimation();
        loaderView.startAnimation(rotateAnimation);
        loaderView.setVisibility(View.VISIBLE);
    }

    /**
     * Hide the loading animation in the action bar
     */
    public void hideLoader() {
        loaderView.setVisibility(View.GONE);
        loaderView.clearAnimation();
    }    
    
    private PagedView.OnPagedViewChangeListener mOnPagedViewChangedListener = new PagedView.OnPagedViewChangeListener() {

        public void onStopTracking(PagedView pagedView) {
        }

        public void onStartTracking(PagedView pagedView) {
        }

        public void onPageChanged(PagedView pagedView, int previousPage, int newPage) {
            if((newPage == 0 && !previousPronosLoaded)
                        || (newPage == PronosActivity.this.pagedPronoEntries.size()-1 && !nextPronosLoaded)) {
                new PronosTask(PronosActivity.this).execute(userName);
            }
        }
    };

    public PagedView getPronosListView() {
        return pronosListView;
    }

    private String getPronosJSON(String userName, String mode) {
        return RestClient.get(new QueryBuilder(this.getAssets(), "/rest/pronos/" + userName + "/?mode=" + mode).getUri());
    }

	private List<PronoEntry> getPronos(String strJSON) {
		List<PronoEntry> pronoEntries = new LinkedList<PronoEntry>();

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
			// A Simple JSONObject Creation
	        JSONObject json = new JSONObject(strJSON);

	        // Matchs non joués
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

	private List<PronoEntry> getPronosJoues(String strJSON) {
		List<PronoEntry> pronoEntries = new LinkedList<PronoEntry>();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
			// A Simple JSONObject Creation
	        JSONObject json = new JSONObject(strJSON);

	        // Matchs non joués
	        JSONArray pronosArray = json.getJSONArray("pronos_joues");
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
                pronoEntry.setButsDom(Integer.valueOf(jsonPronoEntry.getString("buts_dom")));
                pronoEntry.setButsExt(Integer.valueOf(jsonPronoEntry.getString("buts_ext")));
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

            if(pagedPronoEntries == null || pagedPronoEntries.isEmpty()) {

                String pronosJSON = activity.getPronosJSON(args[0], "default");

                pagedPronoEntries = new LinkedList<List<PronoEntry>>();

                List<PronoEntry> pronoEntries = activity.getPronos(pronosJSON);
                List<PronoEntry> pagePronos = new ArrayList<PronoEntry>();
                for(PronoEntry prono : pronoEntries) {
                    pagePronos.add(prono);
                }

                pagedPronoEntries.add(pagePronos);

                pagedPronoEntries.add(new ArrayList<PronoEntry>());
                ((LinkedList<List<PronoEntry>>)pagedPronoEntries).addFirst(new ArrayList<PronoEntry>());

                currentPage = 1;
            } else if(activity.getPronosListView().getCurrentPage() == 0) {

                pagedPronoEntries.remove(0);
                currentPage = 0;

                String pronosJSON = activity.getPronosJSON(args[0], "previous");

                List<PronoEntry> pronoJouesEntries = activity.getPronosJoues(pronosJSON);
                List<PronoEntry> pagePronos = new ArrayList<PronoEntry>();
                int count = 0;
                for(PronoEntry prono : pronoJouesEntries) {
                    if(count > 0 && count%10 == 0) {
                        ((LinkedList<List<PronoEntry>>)pagedPronoEntries).addFirst(pagePronos);
                        pagePronos = new ArrayList<PronoEntry>();
                        currentPage++;
                    }

                    pagePronos.add(prono);
                    count++;
                }

                if(pronoJouesEntries.size() > 0) {
                    ((LinkedList<List<PronoEntry>>)pagedPronoEntries).addFirst(pagePronos);
                    currentPage++;
                }

                currentPage--;

                previousPronosLoaded = true;
            } else {

                pagedPronoEntries.remove(pagedPronoEntries.size()-1);

                String pronosJSON = activity.getPronosJSON(args[0], "next");

                List<PronoEntry> pronoEntries = activity.getPronos(pronosJSON);
                List<PronoEntry> pagePronos = new ArrayList<PronoEntry>();
                int count = 0;
                for(PronoEntry prono : pronoEntries) {
                    if(count > 0 && count%10 == 0) {
                        // on ne prend pas la 1ere page puisqu'on l'a déjà
                        if(count > 10) {
                            // ajout de la page de 10 pronos à faire
                            pagedPronoEntries.add(pagePronos);
                        }
                        pagePronos = new ArrayList<PronoEntry>();
                    }

                    pagePronos.add(prono);
                    count++;
                }

                pagedPronoEntries.add(pagePronos);

                if(currentPage == 1) {
                    currentPage = currentPage + 1;
                } else {
                    currentPage = currentPage + 2;
                }

                nextPronosLoaded = true;
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            PronosPagesAdapter adapter = new PronosPagesAdapter(activity, R.layout.pronos_page_item, pagedPronoEntries);
            pronosListView.setAdapter(adapter);
            // on se place sur la 1ere page avec des matchs non joués
            activity.getPronosListView().scrollToPage(currentPage);
            adapter.notifyDataSetChanged();

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            super.onPostExecute(success);
        }
    }
}
