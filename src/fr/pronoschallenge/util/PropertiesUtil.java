package fr.pronoschallenge.util;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;
import fr.pronoschallenge.PronosChallenge;

import java.io.*;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: thomas
 * Date: 26/12/10
 * Time: 11:21
 */
public class PropertiesUtil {

    private static Properties properties = null;

    public static void loadProperties(AssetManager assetManager) {
        // Read from the /assets directory
        try {
            InputStream inputStream = assetManager.open("env.conf");
            properties = new Properties();
            properties.load(inputStream);
            inputStream.close();
        } catch (Exception e) {
            Log.i("PronosChallenge", "Failed to open env.conf file");
            e.printStackTrace();
        }

    }

    public static String getProperty(AssetManager assetManager, String key) {
        if(properties == null) {
            loadProperties(assetManager);
        }

        return properties.getProperty(key);
    }



    public static String convertStreamToString(InputStream is)
            throws IOException {
        /*
         * To convert the InputStream to String we use the
         * Reader.read(char[] buffer) method. We iterate until the
         * Reader return -1 which means there's no more data to
         * read. We use the StringWriter class to produce the string.
         */
        if (is != null) {
            Writer writer = new StringWriter();

            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(
                        new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }
            return writer.toString();
        } else {
            return "";
        }
    }

}
