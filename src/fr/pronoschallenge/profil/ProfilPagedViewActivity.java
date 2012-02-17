package fr.pronoschallenge.profil;

import fr.pronoschallenge.R;
import fr.pronoschallenge.amis.AmisActivity;
import fr.pronoschallenge.auth.LoginActivity;
import fr.pronoschallenge.options.OptionsActivity;
import fr.pronoschallenge.util.NetworkUtil;
import greendroid.app.GDActivity;
import greendroid.widget.ActionBarItem;
import greendroid.widget.NormalActionBarItem;
import greendroid.widget.PageIndicator;
import greendroid.widget.PagedView;
import greendroid.widget.PagedView.OnPagedViewChangeListener;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Window;

public class ProfilPagedViewActivity extends GDActivity {

	private static final String MODE_VISU = "1";	
	private static final String MODE_GESTION = "2";
    private static final int PAGE_COUNT = 2;
    
	private String profilPseudo;
	private String modeAffichage;

    private PageIndicator mPageIndicatorOther;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setActionBarContentView(R.layout.profil_paged_view);
    	
    	//On récupère l'objet Bundle envoyé par l'autre Activity
        Bundle objetbunble  = this.getIntent().getExtras();        
        
        profilPseudo = (String) objetbunble.get("pseudo");
        if (profilPseudo == null) {
	        String userName = PreferenceManager.getDefaultSharedPreferences(this).getString("username", null);
	        String password = PreferenceManager.getDefaultSharedPreferences(this).getString("password", null);
	        if(userName == null || password == null) {
	            startActivityForResult(new Intent(this, LoginActivity.class), 1);
	        }
	        profilPseudo = userName;
        }
	        
        modeAffichage = (String) objetbunble.get("mode");
        if (modeAffichage == null) {
        	modeAffichage = MODE_VISU;
        }

        // En mode gestion, on rend visible la barre d'outil
        if (modeAffichage.compareTo(MODE_GESTION) == 0) {            

            ActionBarItem itemAmi = getActionBar().newActionBarItem(NormalActionBarItem.class);
            itemAmi.setDrawable(R.drawable.mes_amis);
            getActionBar().addItem(itemAmi);

            ActionBarItem itemOption = getActionBar().newActionBarItem(NormalActionBarItem.class);
            itemOption.setDrawable(R.drawable.options);
            getActionBar().addItem(itemOption);            
        	
        }        
        
		if(NetworkUtil.isConnected(this.getApplicationContext())) {
            setTitle(getString(R.string.title_profil));
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

        final PagedView pagedView = (PagedView) findViewById(R.id.profil_paged_view);
        pagedView.setOnPageChangeListener(mOnPagedViewChangedListener);
        pagedView.setAdapter(new ProfilPagedViewAdapter(this));
        
        mPageIndicatorOther = (PageIndicator) findViewById(R.id.profil_page_indicator_other);
        mPageIndicatorOther.setDotCount(PAGE_COUNT);
        
        setActivePage(pagedView.getCurrentPage());
    }


    @Override
    public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
        switch (position) {
        // Mes amis
        case 0:
			Intent amiIntent = new Intent(ProfilPagedViewActivity.this, AmisActivity.class);					
			startActivity(amiIntent);
			break;
        // Options
        case 1:
			Intent optionIntent = new Intent(ProfilPagedViewActivity.this, OptionsActivity.class);					
			startActivity(optionIntent);
			break;
        }
        return true;
    }	
    
    
	private void setActivePage(int page) {
        mPageIndicatorOther.setActiveDot(page);
    }
    
    private OnPagedViewChangeListener mOnPagedViewChangedListener = new OnPagedViewChangeListener() {

        public void onStopTracking(PagedView pagedView) {
        }

        public void onStartTracking(PagedView pagedView) {
        }

        public void onPageChanged(PagedView pagedView, int previousPage, int newPage) {
            setActivePage(newPage);
        }
    };
    
    public String getProfilPseudo() {
		return profilPseudo;
	}

	public void setProfilPseudo(String profilPseudo) {
		this.profilPseudo = profilPseudo;
	}

	public String getModeAffichage() {
		return modeAffichage;
	}

	public void setModeAffichage(String modeAffichage) {
		this.modeAffichage = modeAffichage;
	}

}
