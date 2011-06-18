package fr.pronoschallenge;

import android.graphics.Bitmap;

/**
 * Created by IntelliJ IDEA.
 * User: thomas
 * Date: 18/06/11
 * Time: 15:49
 * To change this template use File | Settings | File Templates.
 */
public class HomeMenuItem {
    private String name;
    private Bitmap image;

    public HomeMenuItem(String name, Bitmap image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
