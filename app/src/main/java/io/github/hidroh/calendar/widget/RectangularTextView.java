package io.github.hidroh.calendar.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by ABofanov on 11.02.2019.
 */

public class RectangularTextView extends android.support.v7.widget.AppCompatTextView {

    public RectangularTextView(Context context) {
        super(context);
    }

    public RectangularTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RectangularTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, w, oldw, oldh);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
