/*
 * Copyright (C) 2011 Cyril Mottier (http://www.cyrilmottier.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.pronoschallenge.club;

import fr.pronoschallenge.R;
import fr.pronoschallenge.util.NetworkUtil;
import greendroid.app.GDActivity;
import greendroid.widget.PageIndicator;
import greendroid.widget.PagedView;
import greendroid.widget.PagedView.OnPagedViewChangeListener;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Window;

public class ClubPagedViewActivity extends GDActivity {

    private static final int PAGE_COUNT = 2;
	private String nomClub;

    private PageIndicator mPageIndicatorOther;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setActionBarContentView(R.layout.club_paged_view);
    	
    	//On récupère l'objet Bundle envoyé par l'autre Activity
        Bundle objetbunble  = this.getIntent().getExtras();
        setNomClub((String) objetbunble.get("club"));
        
		if(NetworkUtil.isConnected(this.getApplicationContext())) {
            setTitle("Information " + nomClub);
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

        final PagedView pagedView = (PagedView) findViewById(R.id.club_paged_view);
        pagedView.setOnPageChangeListener(mOnPagedViewChangedListener);
        pagedView.setAdapter(new ClubPagedViewAdapter(this));
        
        mPageIndicatorOther = (PageIndicator) findViewById(R.id.club_page_indicator_other);
        mPageIndicatorOther.setDotCount(PAGE_COUNT);
        
        setActivePage(pagedView.getCurrentPage());
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

    public String getNomClub() {
		return nomClub;
	}

	public void setNomClub(String nomClub) {
		this.nomClub = nomClub;
	}

}
