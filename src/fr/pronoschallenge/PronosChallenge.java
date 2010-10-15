package fr.pronoschallenge;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class PronosChallenge extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		findViewById(R.id.button_classements).setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		    	Intent classementIntent = new Intent();
		    	classementIntent.setClassName("fr.pronoschallenge", "fr.pronoschallenge.ClassementActivity");
		    	classementIntent.putExtra("fr.pronoschallenge.ClassementType", "mixte");
		        startActivity(classementIntent);
		    }
		});
	}	
}