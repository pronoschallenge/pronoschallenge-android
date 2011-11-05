package fr.pronoschallenge;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import greendroid.widget.QuickAction;

/**
* Created by IntelliJ IDEA.
* User: Thomas Delhoménie
* Date: 05/11/11
* Time: 14:23
*/
class ClassementQuickAction extends QuickAction {

    private static final ColorFilter BLACK_CF = new LightingColorFilter(Color.BLACK, Color.BLACK);

    public ClassementQuickAction(Context ctx, Drawable drawable, int titleId) {
        super(ctx, drawable, titleId);
    }

    public ClassementQuickAction(Context ctx, int drawableId, int titleId) {
        super(ctx, buildDrawable(ctx, drawableId), titleId);
    }

    private static Drawable buildDrawable(Context ctx, int drawableId) {
        Drawable d = ctx.getResources().getDrawable(drawableId);
        d.setColorFilter(BLACK_CF);
        return d;
    }

}
