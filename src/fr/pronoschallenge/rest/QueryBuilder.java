package fr.pronoschallenge.rest;

import android.content.res.AssetManager;
import fr.pronoschallenge.util.PropertiesUtil;

/**
 * Created by IntelliJ IDEA.
 * User: thomas
 * Date: 26/12/10
 * Time: 11:40
 * To change this template use File | Settings | File Templates.
 */
public class QueryBuilder {

    private StringBuilder uri = new StringBuilder(80);

    public QueryBuilder(AssetManager assetManager, String resourceUri) {
        String host = PropertiesUtil.getProperty(assetManager, "host");
        String context = PropertiesUtil.getProperty(assetManager, "context");

        uri.append("http://").append(host).append(context).append(resourceUri);
    }

    public String getUri() {
        return uri.toString();
    }
}
