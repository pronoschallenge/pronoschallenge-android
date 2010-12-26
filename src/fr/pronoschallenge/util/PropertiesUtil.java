package fr.pronoschallenge.util;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;
import fr.pronoschallenge.PronosChallenge;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: thomas
 * Date: 26/12/10
 * Time: 11:21
 */
public class PropertiesUtil {

    private static Properties properties;

    public static void loadProperties(AssetManager assetManager) {
        // Read from the /assets directory
        try {
            InputStream inputStream = assetManager.open("env.properties");
            Properties properties = new Properties();
            properties.load(inputStream);
        } catch (IOException e) {
            Log.i("PronosChallenge", "Failed to open env.properties file");
        }

    }

    public static String getProperty(AssetManager assetManager, String key) {
        if(properties == null) {
            loadProperties(assetManager);
        }

        return properties.getProperty(key);
    }

}
