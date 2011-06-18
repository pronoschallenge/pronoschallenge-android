package fr.pronoschallenge;

import fr.pronoschallenge.util.AppUtil;
import greendroid.app.GDActivity;
import greendroid.widget.ActionBar.Type;
import greendroid.widget.QuickAction;
import greendroid.widget.QuickActionGrid;
import greendroid.widget.QuickActionWidget;
import greendroid.widget.QuickActionWidget.OnQuickActionClickListener;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class PronosChallengeActivity extends GDActivity {
	
	private QuickActionWidget classementQuickActionGrid;
	
	public PronosChallengeActivity() {
		super(Type.Normal);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//addActionBarItem(getActionBar().newActionBarItem(NormalActionBarItem.class));
		
		setActionBarContentView(R.layout.main);

		findViewById(R.id.button_classements).setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View v) {
						classementQuickActionGrid.show(v);
					}
				});
		
        classementQuickActionGrid = new QuickActionGrid(this);
        classementQuickActionGrid.addQuickAction(new ClassementQuickAction(this, null, R.string.type_classement_general));
        classementQuickActionGrid.addQuickAction(new ClassementQuickAction(this, null, R.string.type_classement_hourra));
        classementQuickActionGrid.addQuickAction(new ClassementQuickAction(this, null, R.string.type_classement_mixte));

        classementQuickActionGrid.setOnQuickActionClickListener(mActionListener);		
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

    private OnQuickActionClickListener mActionListener = new OnQuickActionClickListener() {
        public void onQuickActionClicked(QuickActionWidget widget, int position) {
    		final CharSequence[] classementItems = {"Général", "Hourra", "Mixte"};
    		final String[] classementTypes = {ClassementActivity.CLASSEMENT_TYPE_GENERAL, ClassementActivity.CLASSEMENT_TYPE_HOURRA, ClassementActivity.CLASSEMENT_TYPE_MIXTE};
            Intent classementIntent = new Intent();
	    	classementIntent.setClassName("fr.pronoschallenge", "fr.pronoschallenge.ClassementActivity");
	    	classementIntent.putExtra("fr.pronoschallenge.ClassementType", classementTypes[position]);
	    	classementIntent.putExtra("fr.pronoschallenge.ClassementTitle", classementItems[position]);
	        startActivity(classementIntent);
        }
    };
	
    private static class ClassementQuickAction extends QuickAction {
        
        private static final ColorFilter BLACK_CF = new LightingColorFilter(Color.BLACK, Color.BLACK);

        public ClassementQuickAction(Context ctx, Drawable drawable, int titleId) {
            super(ctx, drawable, titleId);
        }
        
        public ClassementQuickAction(Context ctx, int drawableId, int titleId) {
            super(ctx, buildDrawable(ctx, drawableId), titleId);
        }
        
        private static Drawable buildDrawable(Context ctx, int drawableId) {
            Drawable d = ctx.getResources().getDrawable(drawableId);
            d.setColorFilter(BLACK_CF);
            return d;
        }
        
    }
}