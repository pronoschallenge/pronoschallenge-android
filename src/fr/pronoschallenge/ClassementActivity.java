package fr.pronoschallenge;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import fr.pronoschallenge.rest.QueryBuilder;
import fr.pronoschallenge.rest.RestClient;

public class ClassementActivity extends Activity {

	final static String CLASSEMENT_TYPE_GENERAL = "general";
	final static String CLASSEMENT_TYPE_HOURRA = "hourra";
	final static String CLASSEMENT_TYPE_MIXTE = "mixte";
	
	private String classementType;
	private TextView classementTitleTextView;
	private ListView classementListView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.classement);
		
		// Obtain handles to UI objects
		classementTitleTextView = (TextView) findViewById(R.id.classementTitle);
		classementListView = (ListView) findViewById(R.id.classementList);
	}
	
	
	@Override
	protected void onStart() {
		classementType = (String) this.getIntent().getExtras().get("fr.pronoschallenge.ClassementType");

		classementTitleTextView.setText(getString(R.string.title_classement) + " " + this.getIntent().getExtras().get("fr.pronoschallenge.ClassementTitle"));
		
		classementListView.setAdapter(new ClassementAdapter(this,
				R.layout.classement_item, getClassement(classementType)));
		
		super.onStart();
	}
	
	private List<ClassementEntry> getClassement(String type) {
		List<ClassementEntry> classementEntries = new ArrayList<ClassementEntry>();

		String strClassement = RestClient.exec(new QueryBuilder(this.getAssets(), "/rest/classement/" + type).getUri());

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
}