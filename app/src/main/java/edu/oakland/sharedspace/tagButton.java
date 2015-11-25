package edu.oakland.sharedspace;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Joseph on 11/24/15.
 */
public class TagButton extends ImageView implements View.OnClickListener {

    private boolean isActive;
    private int colorActive, colorDeactive;
    private Paint paint;

    public TagButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnClickListener(this);
        this.setSoundEffectsEnabled(false);

        paint = new Paint();

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.TagButton,
                0, 0);

        try {
            colorActive = a.getInteger(R.styleable.TagButton_colorActive, 0);
            colorDeactive = a.getInteger(R.styleable.TagButton_colorDeactive, 0);
        } finally {
            a.recycle();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        int viewWidthHalf = this.getMeasuredWidth()/2;
        int viewHeightHalf = this.getMeasuredHeight()/2;

        int radius = ((viewWidthHalf < viewHeightHalf) ? viewWidthHalf : viewHeightHalf);


        paint.setStyle(Style.FILL);
        paint.setAntiAlias(true);
        if(isActive){
            paint.setColor(colorActive);
        }else{
            paint.setColor(colorDeactive);
        }
        canvas.drawCircle(viewWidthHalf, viewHeightHalf, radius, paint);

        super.draw(canvas);
    }

    @Override
    public void onClick(View v) {
        isActive = !isActive;

        //Redraw
        invalidate();
        requestLayout();
    }
}
