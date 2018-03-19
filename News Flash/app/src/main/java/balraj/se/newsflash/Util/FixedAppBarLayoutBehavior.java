package balraj.se.newsflash.Util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.util.AttributeSet;

/**
 * Created by balra on 20-03-2018.
 */

public class FixedAppBarLayoutBehavior extends AppBarLayout.Behavior {

    public FixedAppBarLayoutBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);

        setDragCallback(new DragCallback() {
            @Override public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                return false;
            }
        });
    }
}