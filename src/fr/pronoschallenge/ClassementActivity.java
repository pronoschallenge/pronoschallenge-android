package fr.pronoschallenge;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import fr.pronoschallenge.rest.RestClient;

public class ClassementActivity extends Activity {

	private ListView classementList;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.classement);

		// Obtain handles to UI objects
		classementList = (ListView) findViewById(R.id.classementList);

		classementList.setAdapter(new ClassementAdapter(this,
				R.layout.classement_item, getClassement()));
	}
	
	private List<ClassementEntry> getClassement() {
		List<ClassementEntry> classementEntries = new ArrayList<ClassementEntry>();
		
		String strClassement = RestClient.exec("http://www.pronoschallenge.fr/rest/classement/mixte");
        
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
	        	classementEntry.setPoints(jsonClassementEntry.getInt("points"));
	        	classementEntries.add(classementEntry);
	        }
	
		} catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		
		return classementEntries;
	}
}