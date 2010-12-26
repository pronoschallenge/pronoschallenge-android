package fr.pronoschallenge;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import fr.pronoschallenge.util.AppUtil;

public class PronosChallenge extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		findViewById(R.id.button_classements).setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		    	showDialog(0);
		    }
		});
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		final CharSequence[] classementItems = {"Général", "Hourra", "Mixte"};
		final String[] classementTypes = {ClassementActivity.CLASSEMENT_TYPE_GENERAL, ClassementActivity.CLASSEMENT_TYPE_HOURRA, ClassementActivity.CLASSEMENT_TYPE_MIXTE};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.title_select_classement));
		builder.setItems(classementItems, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
		    	Intent classementIntent = new Intent();
		    	classementIntent.setClassName("fr.pronoschallenge", "fr.pronoschallenge.ClassementActivity");
		    	classementIntent.putExtra("fr.pronoschallenge.ClassementType", classementTypes[item]);
		    	classementIntent.putExtra("fr.pronoschallenge.ClassementTitle", classementItems[item]);
		        startActivity(classementIntent);
		    }
		});
		AlertDialog alert = builder.create();
		
		return alert;
	}

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem accountMenu = menu.add(Menu.NONE, 1, Menu.NONE, "Compte");
        accountMenu.setIcon(AppUtil.resizeImage(this, R.drawable.account_menu_icon, 32, 32));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case 1 :
                Intent loginIntent = new Intent();
                loginIntent.setClassName("fr.pronoschallenge", "fr.pronoschallenge.auth.LoginActivity");
                startActivity(loginIntent);

            default :
        }


        return super.onOptionsItemSelected(item);
    }
}