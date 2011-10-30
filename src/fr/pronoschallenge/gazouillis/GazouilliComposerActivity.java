package fr.pronoschallenge.gazouillis;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import fr.pronoschallenge.R;
import fr.pronoschallenge.rest.QueryBuilder;
import fr.pronoschallenge.rest.RestClient;
import greendroid.app.GDActivity;
import org.apache.http.HttpResponse;

import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: Thomas Delhoménie
 * Date: 30/10/11
 * Time: 15:45
 */
public class GazouilliComposerActivity extends GDActivity {
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(getString(R.string.title_gazouilli));

        setActionBarContentView(R.layout.gazouilli_composer);

        editText = (EditText) findViewById(R.id.composeGazouilli);
    }

    public void sendGazouilli(View view) {
        String contenu =  editText.getText().toString();

        if ("".equals(contenu)) {
            Toast toast = Toast.makeText(view.getContext(), "Gazouilli vide...", 4);
            toast.show();

            return;
        } else if(contenu.length() > 140) {
            Toast toast = Toast.makeText(view.getContext(), "Erreur lors de l'ajout du gazouilli : le message fait plus de 140 caractères", 4);
            toast.show();

            return;
        }

        String userName = PreferenceManager.getDefaultSharedPreferences(view.getContext()).getString("username", null);
        String password = PreferenceManager.getDefaultSharedPreferences(view.getContext()).getString("password", null);
        String url = new QueryBuilder(view.getContext().getAssets(), "/rest/gazouilli/").getUri();


        HttpResponse response = RestClient.postData(url, contenu, userName, password);

        if (response.getStatusLine().getStatusCode() != 200) {
            Toast toast = Toast.makeText(view.getContext(), "Erreur lors de l'ajout du gazouilli : " + response.getStatusLine().getStatusCode(), 4);
            toast.show();

            return;
        } else {
            try {
                String message = RestClient.convertStreamToString(response.getEntity().getContent());
                if(message.length() > 0) {
                    Toast toast = Toast.makeText(view.getContext(), "Erreur lors de l'ajout du gazouilli : " + message, 4);
                    toast.show();
                } else {
                    this.finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
