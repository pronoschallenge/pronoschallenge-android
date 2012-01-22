package fr.pronoschallenge.amis;

import fr.pronoschallenge.R;
import fr.pronoschallenge.amis.ajout.AmisAjoutActivity;
import fr.pronoschallenge.auth.LoginActivity;
import fr.pronoschallenge.classement.ClassementQuickAction;
import fr.pronoschallenge.profil.ProfilActivity;
import fr.pronoschallenge.rest.QueryBuilder;
import fr.pronoschallenge.rest.RestClient;
import fr.pronoschallenge.util.NetworkUtil;
import greendroid.app.GDActivity;
import greendroid.widget.ActionBarItem;
import greendroid.widget.NormalActionBarItem;
import greendroid.widget.QuickActionGrid;
import greendroid.widget.QuickActionWidget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


public class AmisActivity extends GDActivity {
	
	final static String VUE_PLACE = "place";
	final static String VUE_EVOLUTION = "evol";
	final static String VUE_POINT = "point";
	final static String TRI_PSEUDO = "pseudo";
	final static String TRI_GENERAL = "general";
	final static String TRI_HOURRA = "hourra";
	final static String TRI_MIXTE = "mixte";
	
	private String strProfilPseudo;
	private String strTri;
	private Boolean bolTriAscendant;
	private String strVue; 
    private LinearLayout amisLayout;
    
	private ListView amisPalmaresListView;
    private TextView amisPalmaresMessage;
    private TextView pseudoTextView;
    private TextView generalTextView;
    private TextView hourraTextView;
    private TextView mixteTextView;
    private QuickActionWidget vueQuickActionGrid;

    private List<AmisPalmaresEntry> amisPalmaresEntriesNonTriee;
    
    private AlertDialog dialog;
    private String mUser;

    
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

        String userName = PreferenceManager.getDefaultSharedPreferences(this).getString("username", null);
        String password = PreferenceManager.getDefaultSharedPreferences(this).getString("password", null);
        if(userName == null || password == null) {
            startActivityForResult(new Intent(this, LoginActivity.class), 1);
        }
        mUser = userName;

		setActionBarContentView(R.layout.amis_liste);
    	
        strProfilPseudo = userName;
        strTri = TRI_PSEUDO;
        bolTriAscendant = true;
        strVue = VUE_POINT;
		
		// Obtain handles to UI objects
        amisPalmaresListView = (ListView) findViewById(R.id.amisPalmaresList);
        amisPalmaresMessage = (TextView) findViewById(R.id.amisPalmaresMessage);
        amisLayout = (LinearLayout) findViewById(R.id.amisLayout);
        
        // Entête de la liste view permettant de trier la liste
        pseudoTextView = (TextView) findViewById(R.id.amisEntetePseudo);
        pseudoTextView.setOnClickListener(new EnteteOnClickListener(TRI_PSEUDO, this));        	
        generalTextView = (TextView) findViewById(R.id.amisEnteteGeneral);
        generalTextView.setOnClickListener(new EnteteOnClickListener(TRI_GENERAL, this));
        hourraTextView = (TextView) findViewById(R.id.amisEnteteHourra);
        hourraTextView.setOnClickListener(new EnteteOnClickListener(TRI_HOURRA, this));
        mixteTextView = (TextView) findViewById(R.id.amisEnteteMixte);
        mixteTextView.setOnClickListener(new EnteteOnClickListener(TRI_MIXTE, this));


        // menu pour switcher entre les classements
        vueQuickActionGrid = new QuickActionGrid(this);
        vueQuickActionGrid.addQuickAction(new ClassementQuickAction(this, null, R.string.type_vue_classement));
        vueQuickActionGrid.addQuickAction(new ClassementQuickAction(this, null, R.string.type_vue_point));
        vueQuickActionGrid.addQuickAction(new ClassementQuickAction(this, null, R.string.type_vue_topflop));

        vueQuickActionGrid.setOnQuickActionClickListener(new QuickActionWidget.OnQuickActionClickListener() {
            public void onQuickActionClicked(QuickActionWidget widget, int position) {
                final CharSequence[] vueItems = {getString(R.string.type_vue_classement), getString(R.string.type_vue_point), getString(R.string.type_vue_topflop)};
                AmisActivity amisActivity = (AmisActivity) vueQuickActionGrid.getContentView().getContext();
                amisActivity.setTitle(getString(R.string.title_liste_amis) + " - " + vueItems[position]);
                switch (position) {
                case 0:
                	strVue = VUE_PLACE;
                	amisPalmaresEntriesNonTriee = null;
                    getActionBar().getItem(0).setDrawable(R.drawable.coupe);;
                	new AmisTask(amisActivity).execute(strProfilPseudo);
                    break;
                case 1:
                	strVue = VUE_POINT;
                	amisPalmaresEntriesNonTriee = null;
                	getActionBar().getItem(0).setDrawable(R.drawable.classement);
                	new AmisTask(amisActivity).execute(strProfilPseudo);
                	break;            	
                case 2:
                	strVue = VUE_EVOLUTION;
                	amisPalmaresEntriesNonTriee = null;
                	getActionBar().getItem(0).setDrawable(R.drawable.top_flop);
                	new AmisTask(amisActivity).execute(strProfilPseudo);
                	break;            	
                }
            }
        });

        // Ajout de l'item dans la barre de menu pour afficher la vue des amis (classement, points, top flop)
        ActionBarItem itemVue = getActionBar().newActionBarItem(NormalActionBarItem.class);
        itemVue.setDrawable(R.drawable.coupe);
        getActionBar().addItem(itemVue);
        
        // Ajout de l'item dans la barre de menu pour ajouter un ami
        ActionBarItem itemAjoutAmi = getActionBar().newActionBarItem(NormalActionBarItem.class);
        itemAjoutAmi.setDrawable(R.drawable.user_add);
        getActionBar().addItem(itemAjoutAmi);

        // Actions sur la liste des amis
        amisPalmaresListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
			    int position, long id) {
					//on récupère la HashMap contenant les infos de notre item (pseudo)
					AmisPalmaresEntry amisPalmaresEntry = (AmisPalmaresEntry) amisPalmaresListView.getItemAtPosition(position);					
					//On crée un objet Bundle, c'est ce qui va nous permettre d'envoyer des données à l'autre Activity
					Bundle objetbunble = new Bundle();		 
					//Cela fonctionne plus ou moins comme une HashMap, on entre une clef et sa valeur en face
					objetbunble.putString("pseudo", amisPalmaresEntry.getPseudo());							
					// On met en place le passage entre les deux activités sur ce Listener (activité départ, activité arrivée)
					Intent intent = new Intent(AmisActivity.this, ProfilActivity.class);					
					//On affecte à l'Intent le Bundle que l'on a créé
					intent.putExtras(objetbunble);					
					//On démarre la nouvelle Activity en indiquant qu'on pourra revenir à l'activity classement
					int intRecharge = 0;
					startActivityForResult(intent, intRecharge);
					if (intRecharge == RESULT_OK) reStart();
			}
        });        

	}
	
	
	@Override
	protected void onStart() {
		if(NetworkUtil.isConnected(this.getApplicationContext())) {
			setTitle(getString(R.string.title_liste_amis) + " - " + getString(R.string.type_vue_classement));
            new AmisTask(this).execute(strProfilPseudo, strTri);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Connexion Internet indisponible")
                .setPositiveButton("Fermer", new DialogInterface.OnClickListener() {
                                       public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            finish();
                                       }
                                   });
            dialog = builder.create();
            dialog.show();
        }

		super.onStart();
	}

    @Override
    public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
        switch (position) {
        // Changement de liste
        case 0:
            vueQuickActionGrid.show(item.getItemView());
            break;
        // Ajout d'un ami
        case 1:
			Bundle objetbunble = new Bundle();		 
			objetbunble.putString("pseudo", strProfilPseudo);
			Intent intent = new Intent(AmisActivity.this,AmisAjoutActivity.class);					
			intent.putExtras(objetbunble);					
			startActivityForResult(intent, 0);            
        }
        return true;
    }

    private void reStart() {
    	this.onRestart();
    }
    
    // fonction qui tri la liste des amis selon un param�tre (pseudo, classement g�n�ral ...)
    public List<AmisPalmaresEntry> triAmis() {

    	// La liste est triée par défaut sur le pseudo 
		if (strTri.compareTo(TRI_PSEUDO) == 0 && bolTriAscendant) {
			return amisPalmaresEntriesNonTriee;
		// TRI PAR PSEUDO DESCENDANT
		} else {
			List<AmisPalmaresEntry> listeAmisTriee = new ArrayList<AmisPalmaresEntry>();
			if (strTri.compareTo(TRI_PSEUDO) == 0) {
				listeAmisTriee = amisPalmaresEntriesNonTriee;
				Collections.reverse(listeAmisTriee);
			// TRI PAR PLACE (classement, top/flop) ASCENDANT / DESCENDANT	
			} else {
				List<Integer> ma_liste = new ArrayList<Integer>();
				// Parcours des amis et stockage du classement souhaité
				for (AmisPalmaresEntry palmaresEntry : amisPalmaresEntriesNonTriee) {
					for (AmisPalmaresDetailEntry palmaresDetail : palmaresEntry.getPalmaresDetail()) {
						if (palmaresDetail.getTypeChamp().compareTo(strTri) == 0) {
							ma_liste.add(palmaresDetail.getNumPlace());
						}
					}
				}
				// Tri ascendant du classement				
				if (bolTriAscendant) {
					Collections.sort(ma_liste);	
				} else {
					Comparator<Object> comparator = Collections.reverseOrder();
					Collections.sort(ma_liste, comparator);
				}				
				Integer numPlacePrecedente = 0;
				// Reparcours de la liste pour attribuer tous les éléments affichés
				for (Integer numPlace : ma_liste) {
					if (numPlace.compareTo(numPlacePrecedente) != 0) {
						for (AmisPalmaresEntry palmaresEntry : amisPalmaresEntriesNonTriee) {
							for (AmisPalmaresDetailEntry palmaresDetail : palmaresEntry.getPalmaresDetail()) {
								if (palmaresDetail.getTypeChamp().compareTo(strTri) == 0 && palmaresDetail.getNumPlace().compareTo(numPlace) == 0) {
									listeAmisTriee.add(palmaresEntry);
								}
							}
						}
					}
					numPlacePrecedente = numPlace;
				}				
			}
			return listeAmisTriee;
		}
    }
	
	// Clic sur un entête = filtre des amis sur ce critère
    class EnteteOnClickListener implements View.OnClickListener {

        String strTri = null;
        AmisActivity activity;

        EnteteOnClickListener(String strTri, AmisActivity activity) {
            this.strTri = strTri;
            this.activity = activity;
        }

        public void onClick(View button) {
            // Actualisation du tri de la liste des amis
        	bolTriAscendant = !bolTriAscendant;
        	activity.strTri = strTri;
            new AmisTask(activity).execute(activity.strProfilPseudo);
        }
    }

	
	private List<AmisPalmaresEntry> getAmisPalmares(String userName) {
		// Chargement de la liste des amis uniquement la première fois (pour ne pas faire un chargement à chaque filtre)
		if (amisPalmaresEntriesNonTriee == null) {
	
			amisPalmaresEntriesNonTriee = new ArrayList<AmisPalmaresEntry>();
			
			// Liste des evolutions des amis
			if (strVue.compareTo(VUE_EVOLUTION) == 0) {
				String strListeAmis = RestClient.get(new QueryBuilder(this.getAssets(), "/rest/evolutionClassement/" + userName + "/?filtre='1'").getUri());
				
				try {
					// A Simple JSONObject Creation
			        JSONObject json = new JSONObject(strListeAmis);
		
			        // A Simple JSONObject Parsing
			        JSONArray palmaresArray = json.getJSONArray("evolutionClassement");
			        for (int i = 0; i < palmaresArray.length(); i++)
			        {
			        	JSONObject jsonPalmaresEntry = palmaresArray.getJSONObject(i);
		
			        	AmisPalmaresEntry palmaresEntry = new AmisPalmaresEntry();
			        	palmaresEntry.setPseudo(jsonPalmaresEntry.getString("pseudo"));
			        	// Recherche des championnats liés à la saison
			        	while (jsonPalmaresEntry.getString("pseudo").equals(palmaresEntry.getPseudo()) && i < palmaresArray.length()) {
			        		AmisPalmaresDetailEntry palmaresDetail = new AmisPalmaresDetailEntry();
		        			palmaresDetail.setTypeChamp(jsonPalmaresEntry.getString("type"));
		                    palmaresDetail.setNumPlace(jsonPalmaresEntry.getInt("evol"));
		                    palmaresEntry.setPalmaresDetail(palmaresDetail);
		                    // Entité JSON suivante
		                    i++;
		                    if (i < palmaresArray.length()) {
		                    	jsonPalmaresEntry = palmaresArray.getJSONObject(i);
		                    }
			        	}
			        	// Repositionnement sur l'entité JSON adéquate
			        	if (i < palmaresArray.length()) {
			        		i--;
			        	}
		                amisPalmaresEntriesNonTriee.add(palmaresEntry);
			        }
		
				} catch (JSONException e) {
		            e.printStackTrace();
		        }
				
			} else {
				String strListeAmis = RestClient.get(new QueryBuilder(this.getAssets(), "/rest/listeAmis/" + userName + "/").getUri());
				
				try {
					// A Simple JSONObject Creation
			        JSONObject json = new JSONObject(strListeAmis);
		
			        // A Simple JSONObject Parsing
			        JSONArray palmaresArray = json.getJSONArray("listeAmis");
			        for (int i = 0; i < palmaresArray.length(); i++)
			        {
			        	JSONObject jsonPalmaresEntry = palmaresArray.getJSONObject(i);
		
			        	AmisPalmaresEntry palmaresEntry = new AmisPalmaresEntry();
			        	palmaresEntry.setPseudo(jsonPalmaresEntry.getString("pseudo"));
			        	// Recherche des championnats liés à la saison
			        	while (jsonPalmaresEntry.getString("pseudo").equals(palmaresEntry.getPseudo()) && i < palmaresArray.length()) {
			        		AmisPalmaresDetailEntry palmaresDetail = new AmisPalmaresDetailEntry();
		        			palmaresDetail.setTypeChamp(jsonPalmaresEntry.getString("type"));
		        			if (strVue.compareTo(VUE_PLACE) == 0) {
		        				palmaresDetail.setNumPlace(jsonPalmaresEntry.getInt("place"));
		        			} else {
			                    palmaresDetail.setNumPlace(jsonPalmaresEntry.getInt("point"));		        				
		        			}
		                    palmaresEntry.setPalmaresDetail(palmaresDetail);
		                    // Entité JSON suivante
		                    i++;
		                    if (i < palmaresArray.length()) {
		                    	jsonPalmaresEntry = palmaresArray.getJSONObject(i);
		                    }
			        	}
			        	// Repositionnement sur l'entité JSON adéquate
			        	if (i < palmaresArray.length()) {
			        		i--;
			        	}
		                amisPalmaresEntriesNonTriee.add(palmaresEntry);
			        }
		
				} catch (JSONException e) {
		            e.printStackTrace();
		        }
				
			}
		}
		
		// Retourne amisPalmaresEntriesNonTriee triée sur le tri souhaité
		return triAmis();
	}


    private class AmisTask extends AsyncTask<String, Void, Boolean> {

        final private AmisActivity activity;
        private ProgressDialog dialog;
        private List<AmisPalmaresEntry> amisPalmaresEntries;

        private AmisTask(AmisActivity activity) {
            this.activity = activity;
            dialog = new ProgressDialog(activity);
        }

        protected void onPreExecute() {
            this.dialog.setMessage("Chargement");
            this.dialog.show();
        }

        @Override
        protected Boolean doInBackground(final String... args) {
            amisPalmaresEntries = activity.getAmisPalmares(args[0]);
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
        	// Message liste vide
            if (amisPalmaresEntries.isEmpty()) {
            	activity.amisPalmaresMessage.setVisibility(View.VISIBLE);
            } else {
            	// Affichage de la liste
            	activity.amisPalmaresMessage.setVisibility(View.GONE);
	            AmisAdapter adapter = new AmisAdapter(activity,	R.layout.amis_liste_item, amisPalmaresEntries, mUser);
	            amisPalmaresListView.setAdapter(adapter);
	            adapter.notifyDataSetChanged();	            
            }

            activity.amisLayout.setVisibility(View.VISIBLE);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            super.onPostExecute(success);
        }
        	
    }
}