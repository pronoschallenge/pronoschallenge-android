package fr.pronoschallenge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import fr.pronoschallenge.rest.QueryBuilder;
import fr.pronoschallenge.rest.RestClient;

public class PronosAdapter extends ArrayAdapter<PronoEntry> {

    public PronosAdapter(Context context, int textViewResourceId,
                         List<PronoEntry> objects) {
        super(context, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater li = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //le layout représentant la ligne dans le listView
            view = li.inflate(R.layout.pronos_item, null);
        }
        PronoEntry pronoEntry = getItem(position);
        if (pronoEntry != null) {
            TextView pronoEntryClubDom = (TextView) view.findViewById(R.id.pronoEntryEquipeDom);
            pronoEntryClubDom.setText(pronoEntry.getEquipeDom());

            int id = pronoEntry.getId();

            Button buttonProno1 = (Button) view.findViewById(R.id.buttonProno1);
            buttonProno1.setSelected(false);
            buttonProno1.setTag(R.id.idProno, id);
            buttonProno1.setTag(R.id.valueProno, "1");
            Button buttonPronoN = (Button) view.findViewById(R.id.buttonPronoN);
            buttonPronoN.setSelected(false);
            buttonPronoN.setTag(R.id.idProno, id);
            buttonPronoN.setTag(R.id.valueProno, "N");
            Button buttonProno2 = (Button) view.findViewById(R.id.buttonProno2);
            buttonProno2.setSelected(false);
            buttonProno2.setTag(R.id.idProno, id);
            buttonProno2.setTag(R.id.valueProno, "2");

            // si le match a déjà été pronostiqué, on sélectionne le bouton correspondant
            String prono = pronoEntry.getProno();
            if (prono.equals("1")) {
                buttonProno1.setSelected(true);
            } else if (prono.equals("N")) {
                buttonPronoN.setSelected(true);
            } else if (prono.equals("2")) {
                buttonProno2.setSelected(true);
            }

            if (pronoEntry.getDate().before(new Date())) {
                buttonProno1.setEnabled(false);
                buttonPronoN.setEnabled(false);
                buttonProno2.setEnabled(false);
            } else {
                buttonProno1.setOnClickListener(new PronosButtonsOnClickListener((Activity) this.getContext(), new ArrayList<View>(Arrays.asList(buttonPronoN, buttonProno2))));
                buttonPronoN.setOnClickListener(new PronosButtonsOnClickListener((Activity) this.getContext(), new ArrayList<View>(Arrays.asList(buttonProno1, buttonProno2))));
                buttonProno2.setOnClickListener(new PronosButtonsOnClickListener((Activity) this.getContext(), new ArrayList<View>(Arrays.asList(buttonProno1, buttonPronoN))));
            }

            TextView pronoEntryClubExt = (TextView) view.findViewById(R.id.pronoEntryEquipeExt);
            pronoEntryClubExt.setText(pronoEntry.getEquipeExt());
        }

        return view;

    }

    class PronosButtonsOnClickListener implements View.OnClickListener {

        Activity activity;
        List<View> othersButtons = null;

        PronosButtonsOnClickListener(Activity activity, List<View> othersButtons) {
            this.activity = activity;
            this.othersButtons = othersButtons;
        }

        public void onClick(View button) {
            // mise à jour des éléments graphiques
            String valueProno;
            if (button.isSelected()) {
                button.setSelected(false);
                valueProno = "0";
            } else {
                button.setSelected(true);
                valueProno = (String) button.getTag(R.id.valueProno);
            }

            for (View otherButton : othersButtons) {
                otherButton.setSelected(false);
            }

            // lancement de la tâche de mise à jour de pronos
            new PronosTask(activity, button, othersButtons).execute(valueProno);
        }
    }

    class PronosTask extends AsyncTask<String, Void, Boolean> {

        private Activity activity;
        private View button;
        private int nbOfProcess;

        PronosTask(Activity activity, View button, List<View> othersButtons) {
            this.activity = activity;
            this.button = button;
            this.nbOfProcess = 0;
        }

        @Override
        protected void onPreExecute() {
            if(nbOfProcess++ == 0) {
                //((GDActivity) activity).getActionBar().showLoader();
            	((PronosActivity)activity).showLoader();
            }

            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(--nbOfProcess == 0) {
                //((GDActivity) activity).getActionBar().hideLoader();
                ((PronosActivity)activity).hideLoader();
            }
            super.onPostExecute(aBoolean);
        }

        @Override
        protected Boolean doInBackground(String... args) {
            String valueProno = args[0];

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id", button.getTag(R.id.idProno));
                jsonObject.put("prono", valueProno);
            } catch (JSONException je) {
                je.printStackTrace();
            }
            JSONArray jsonDataArray = new JSONArray();
            jsonDataArray.put(jsonObject);

            String userName = PreferenceManager.getDefaultSharedPreferences(button.getContext()).getString("username", null);
            String password = PreferenceManager.getDefaultSharedPreferences(button.getContext()).getString("password", null);
            String url = new QueryBuilder(button.getContext().getAssets(), "/rest/pronos/" + userName).getUri();
            HttpResponse response = RestClient.postData(url, jsonDataArray.toString(), userName, password);

            if (response.getStatusLine().getStatusCode() != 200) {
                Toast toast = Toast.makeText(button.getContext(), "Erreur lors de la mise à jour de vos pronostics : " + response.getStatusLine().getStatusCode(), 4);
                toast.show();
            } else {
                try {
                    String message = RestClient.convertStreamToString(response.getEntity().getContent());
                    if(message.length() > 0) {
                        Toast toast = Toast.makeText(button.getContext(), "Erreur lors de la mise à jour de vos pronostics : " + message, 4);
                        toast.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return true;
        }
    }
}
