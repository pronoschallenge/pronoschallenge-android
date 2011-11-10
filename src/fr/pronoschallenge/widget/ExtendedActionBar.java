package fr.pronoschallenge.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import fr.pronoschallenge.R;
import greendroid.widget.ActionBar;

/**
 * Created by IntelliJ IDEA.
 * User: Thomas Delhoménie
 * Date: 02/11/11
 * Time: 23:18
 */
public class ExtendedActionBar extends ActionBar {

    public ExtendedActionBar(Context context) {
        super(context);
    }

    public ExtendedActionBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ExtendedActionBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void showLoader() {

    }

    public void hideLoader() {

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ImageView loader = new ImageView(this.getContext());
        loader.setImageResource(R.drawable.loading);
        addView(loader);
    }
}
