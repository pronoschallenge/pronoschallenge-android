package fr.pronoschallenge;

import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import fr.pronoschallenge.rest.QueryBuilder;
import fr.pronoschallenge.rest.RestClient;
import greendroid.widget.PagedAdapter;
import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class PronosPagesAdapter extends PagedAdapter {

    private Context context;
    private List<List<PronoEntry>> pronos;

    public PronosPagesAdapter(Context context, int textViewResourceId,
                              List<List<PronoEntry>> objects) {
        //super(context, textViewResourceId, objects);
        super();

        this.context = context;
        this.pronos = objects;
    }

    @Override
    public int getCount() {
        return pronos.size();
    }

    @Override
    public Object getItem(int position) {
        return pronos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //le layout représentant la ligne dans le listView
            view = li.inflate(R.layout.pronos_page_item, null);
        }

        ListView pronoPageListView = (ListView) view.findViewById(R.id.pronoPageList);

        PronosAdapter adapter = new PronosAdapter(context, R.layout.pronos_item, pronos.get(position));
        pronoPageListView.setAdapter(adapter);

        return view;

    }
}
