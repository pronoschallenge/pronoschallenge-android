package fr.pronoschallenge;

import android.graphics.*;
import android.view.*;
import android.widget.*;
import fr.pronoschallenge.util.AppUtil;
import greendroid.app.GDActivity;
import greendroid.widget.ActionBar.Type;
import greendroid.widget.QuickAction;
import greendroid.widget.QuickActionGrid;
import greendroid.widget.QuickActionWidget;
import greendroid.widget.QuickActionWidget.OnQuickActionClickListener;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class PronosChallengeActivity extends GDActivity {
	
	private QuickActionWidget classementQuickActionGrid;

    private List icons = new ArrayList();
	
	public PronosChallengeActivity() {
		super(Type.Normal);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//addActionBarItem(getActionBar().newActionBarItem(NormalActionBarItem.class));

		setActionBarContentView(R.layout.main);

        GridView gridView = (GridView) findViewById(R.id.gridView);
        gridView.setAdapter(new HomeMenuItemsAdapter());

        icons.add(new HomeMenuItem(getString(R.string.button_pronos), BitmapFactory.decodeResource(getResources(), R.drawable.pronos)));
        icons.add(new HomeMenuItem(getString(R.string.button_classements), BitmapFactory.decodeResource(getResources(), R.drawable.classements)));
        icons.add(new HomeMenuItem(getString(R.string.button_gazouillis), BitmapFactory.decodeResource(getResources(), R.drawable.gazouillis)));
        icons.add(new HomeMenuItem(getString(R.string.button_options), BitmapFactory.decodeResource(getResources(), R.drawable.options)));
		
        classementQuickActionGrid = new QuickActionGrid(this);
        classementQuickActionGrid.addQuickAction(new ClassementQuickAction(this, null, R.string.type_classement_general));
        classementQuickActionGrid.addQuickAction(new ClassementQuickAction(this, null, R.string.type_classement_hourra));
        classementQuickActionGrid.addQuickAction(new ClassementQuickAction(this, null, R.string.type_classement_mixte));

        classementQuickActionGrid.setOnQuickActionClickListener(mActionListener);		
	}

    public class HomeMenuItemsAdapter extends BaseAdapter {

        public int getCount() {
            return icons.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View view, ViewGroup viewGroup) {
            View menuItemView;

            HomeMenuItem homeMenuItem = (HomeMenuItem) icons.get(position);

            if(view == null) {
                LayoutInflater li = getLayoutInflater();
                menuItemView = li.inflate(R.layout.home_menu_item, null);
            } else {
                menuItemView = view;
            }

            ImageView imageView = (ImageView) menuItemView.findViewById(R.id.icon_image);
            imageView.setImageBitmap(homeMenuItem.getImage());
            TextView textView = (TextView) menuItemView.findViewById(R.id.icon_text);
            textView.setText(homeMenuItem.getName());

            if(homeMenuItem.getName().equals(getString(R.string.button_classements))) {
                menuItemView.setOnClickListener(
                    new View.OnClickListener() {
                        public void onClick(View v) {
                            classementQuickActionGrid.show(v);
                        }
                    });
            } else if(homeMenuItem.getName().equals(getString(R.string.button_pronos))) {
                menuItemView.setOnClickListener(
                    new View.OnClickListener() {
                        public void onClick(View v) {
                            Intent pronosIntent = new Intent();
                            pronosIntent.setClassName("fr.pronoschallenge", "fr.pronoschallenge.PronosActivity");
                            startActivity(pronosIntent);
                        }
                    });
            } else if(homeMenuItem.getName().equals(getString(R.string.button_gazouillis))) {
                menuItemView.setOnClickListener(
                    new View.OnClickListener() {
                        public void onClick(View v) {
                            Intent gazouillisIntent = new Intent();
                            gazouillisIntent.setClassName("fr.pronoschallenge", "fr.pronoschallenge.gazouillis.GazouillisActivity");
                            startActivity(gazouillisIntent);
                        }
                    });
            }

            return menuItemView;
        }
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