/* ===========================================================
 * AFreeChart : a free chart library for Android(tm) platform.
 *              (based on JFreeChart and JCommon)
 * ===========================================================
 *
 * (C) Copyright 2010, by ICOMSYSTECH Co.,Ltd.
 * (C) Copyright 2000-2008, by Object Refinery Limited and Contributors.
 *
 * Project Info:
 *    AFreeChart: http://code.google.com/p/afreechart/
 *    JFreeChart: http://www.jfree.org/jfreechart/index.html
 *    JCommon   : http://www.jfree.org/jcommon/index.html
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * [Android is a trademark of Google Inc.]
 *
 * -----------------
 * AnnotationDemo01Activity.java
 * -----------------
 * (C) Copyright 2011, by ICOMSYSTECH Co.,Ltd.
 *
 * Original Author:  Yamakami Souichirou (for ICOMSYSTECH Co.,Ltd);
 * Contributor(s):   -;
 *
 * Changes
 * -------
 * 18-Oct-2011 : Added new sample code (SY);
 */

package fr.pronoschallenge.afreechart;

import fr.pronoschallenge.R;
import fr.pronoschallenge.rest.QueryBuilder;
import fr.pronoschallenge.rest.RestClient;
import greendroid.app.GDActivity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;

/**
 * AFreeChartAnnotationActivity
 */
public class AFreeChartAnnotationActivity extends GDActivity {

	private String itemName;
	private int mode;
	
	static final int MODE_PROFIL = 0;
	static final int MODE_CLUB = 1;
	
    /**
     * Called when the activity is starting.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	
        Bundle objetbunble  = this.getIntent().getExtras();        
        setItemName((String) objetbunble.get("item"));
        setMode((Integer) objetbunble.get("mode"));
        
        new EvolutionTask(this).execute(getItemName());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setTitle(getString(R.string.title_evolution));

    }
    
	
	// Evolution classement utilisateur
	private List<EvolutionClassementEntry> getEvolutionProfil(String userName) {
		List<EvolutionClassementEntry> evolutionEntries = new ArrayList<EvolutionClassementEntry>();

		String strEvolution = RestClient.get(new QueryBuilder(this.getAssets(), "/rest/profilEvolution/" + userName + "/").getUri());

		try {
			// A Simple JSONObject Creation
	        JSONObject json = new JSONObject(strEvolution);

	        // A Simple JSONObject Parsing
	        JSONArray evolutionArray = json.getJSONArray("profilEvolution");
	        for (int i = 0; i < evolutionArray.length(); i++)
	        {
	        	JSONObject jsonPalmaresEntry = evolutionArray.getJSONObject(i);

	        	EvolutionClassementEntry evolutionEntry = new EvolutionClassementEntry();
	        	evolutionEntry.setTypeChamp(jsonPalmaresEntry.getString("type"));
	        	// Recherche des journées du championnat adéquat
	        	while (jsonPalmaresEntry.getString("type").equals(evolutionEntry.getTypeChamp()) && i < evolutionArray.length()) {
	        		EvolutionClassementDetailEntry evolutionDetail = new EvolutionClassementDetailEntry();
        			evolutionDetail.setNumPlace(Integer.valueOf(jsonPalmaresEntry.getString("place")));
        			evolutionDetail.setNumJournee(Integer.valueOf(jsonPalmaresEntry.getString("jour")));
                    evolutionEntry.setEvolutionDetail(evolutionDetail);
                    i++;
                    if (i < evolutionArray.length()) {
                    	jsonPalmaresEntry = evolutionArray.getJSONObject(i);
                    }
	        	}
	        	// Repositionnement sur l'entité JSON adéquate
	        	if (i < evolutionArray.length()) {
	        		i--;
	        	}
                evolutionEntries.add(evolutionEntry);
	        }

		} catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

		return evolutionEntries;
	}    
	   
	
		// Evolution classement utilisateur
		private List<EvolutionClassementEntry> getEvolutionClub(String clubName) {
			List<EvolutionClassementEntry> evolutionEntries = new ArrayList<EvolutionClassementEntry>();

			String strEvolution = RestClient.get(new QueryBuilder(this.getAssets(), "/rest/evolutionClassementL1/" + clubName + "/").getUri());

			try {
				// A Simple JSONObject Creation
		        JSONObject json = new JSONObject(strEvolution);

		        // A Simple JSONObject Parsing
		        JSONArray evolutionArray = json.getJSONArray("evolutionClassementL1");
	        	EvolutionClassementEntry evolutionEntry = new EvolutionClassementEntry();
	        	evolutionEntry.setTypeChamp("Ligue 1");		        
		        for (int i = 0; i < evolutionArray.length(); i++)
		        {
		        	JSONObject jsonPalmaresEntry = evolutionArray.getJSONObject(i);
		        	
		        	EvolutionClassementDetailEntry evolutionDetail = new EvolutionClassementDetailEntry();
	        		evolutionDetail.setNumPlace(Integer.valueOf(jsonPalmaresEntry.getString("place")));
	        		evolutionDetail.setNumJournee(Integer.valueOf(jsonPalmaresEntry.getString("jour")));
	                evolutionEntry.setEvolutionDetail(evolutionDetail);
		        }
                evolutionEntries.add(evolutionEntry);

			} catch (JSONException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }

			return evolutionEntries;
		}    

	public int getMode() {
		return mode;
	}


	public void setMode(int mode) {
		this.mode = mode;
	}


	public String getItemName() {
		return itemName;
	}


	public void setItemName(String itemName) {
		this.itemName = itemName;
	}


	private class EvolutionTask extends AsyncTask<String, Void, Boolean> {

    	private Activity activity;
        private List<EvolutionClassementEntry> evolutionEntries;
        private ProgressDialog dialog;

        private EvolutionTask(Activity activity) {
        	this.activity = activity;
            dialog = new ProgressDialog(activity);
        }

        protected void onPreExecute() {
            this.dialog.setMessage("Chargement");
            this.dialog.show();
        }

        @Override
        protected Boolean doInBackground(final String... args) {
        	if (getMode() == MODE_PROFIL) {
        		evolutionEntries = getEvolutionProfil(args[0]);
        	} else {
        		evolutionEntries = getEvolutionClub(args[0]);
        	}
            return true;
        }
       
        @Override
        protected void onPostExecute(final Boolean success) {

        	AFreeChartAnnotationView mView = new AFreeChartAnnotationView((AFreeChartAnnotationActivity) activity, evolutionEntries, getMode());
            setActionBarContentView(mView);

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            super.onPostExecute(success);
        }
        	
    }	
	
}
