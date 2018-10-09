package quanye.org.chatapp.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.Random;

/**
 * Custom TextView - The passwordTextView
 *
 * @author QuanyeChen
 */
public class PasswordTextView extends android.support.v7.widget.AppCompatTextView {

    private Paint paint = new Paint();
    private Rect bound = new Rect();
    private Random random = new Random();

    private String validateCode = randomText();

    public PasswordTextView(Context context) {
        this(context, null);
    }

    public PasswordTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PasswordTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        paint.getTextBounds(getText().toString(), 0, getText().length(), bound);
        setText(validateCode);

        this.setOnClickListener(v -> {
            updateValidateCode();
        });
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode != MeasureSpec.EXACTLY) {
            paint.setTextSize(getTextSize());
            paint.getTextBounds(getText().toString(), 0, getText().length(), bound);
            float textWidth = bound.width();
            widthSize = (int) (getPaddingLeft() + textWidth + getPaddingRight());
        }

        if (heightMode != MeasureSpec.EXACTLY) {
            paint.setTextSize(getTextSize());
            paint.getTextBounds(getText().toString(), 0, getText().length(), bound);
            float textHeight = bound.height();
            heightSize = (int) (getPaddingTop() + textHeight + getPaddingBottom());
        }

        setMeasuredDimension(widthSize, heightSize);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        paint.setColor(Color.BLACK);
        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), paint);

        int x = getWidth() / 2 - bound.width() / 2;
        int y = getHeight() / 2 + bound.height() / 2;
        for (int i = 0; i < getText().length(); ++i) {
            String s = getText().subSequence(i, i + 1).toString();
            int red = random.nextInt(125) + 100;
            int green = random.nextInt(125) + 100;
            int blue = random.nextInt(125) + 100;
            int color = Color.rgb(red, blue, green);
            paint.setColor(color);
            canvas.rotate(random.nextInt(3));
            canvas.drawText(s, x + (i * bound.width() / 4), y, paint);
        }
    }


    /**
     * 随机获取的数字验证码
     * @return 验证码
     */
    private String randomText() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; ++i) {
            int r = random.nextInt(9);
            sb.append(r);
        }

        return sb.toString();
    }


    public void updateValidateCode() {
        validateCode = randomText();
        setText(validateCode);
        postInvalidate();
    }


    public String getValidateCode() {
        return validateCode;
    }
}
