package net.soradigital.suaraku.helper;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

public class GridHelper {
    private Context context;
    public GridHelper(Context context){
        this.context = context;
    }
    public int dpToPx(int dp) {
        Resources r = context.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}
