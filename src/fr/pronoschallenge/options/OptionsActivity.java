package fr.pronoschallenge.options;

import android.content.Intent;
import android.os.Bundle;
import greendroid.app.GDTabActivity;

/**
 * Created by IntelliJ IDEA.
 * User: Thomas Delhoménie
 * Date: 02/11/11
 * Time: 22:01
 */
public class OptionsActivity extends GDTabActivity {
@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Options");

        Intent intent = new Intent(this, ProfilActivity.class);
        addTab("profilTab", "Profil", intent);

        intent = new Intent(this, FiltreActivity.class);
        addTab("filtreTab", "Filtre", intent);

        intent = new Intent(this, StrategieActivity.class);
        addTab("strategieTab", "Strategie", intent);
    }
}
