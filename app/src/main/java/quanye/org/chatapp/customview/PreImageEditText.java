package quanye.org.chatapp.customview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.ImageView;

import quanye.org.chatapp.R;

public class PreImageEditText extends android.support.v7.widget.AppCompatImageView {


    private Drawable preImg;
    private Context ctx;


    public PreImageEditText(Context context) {
        super(context);
        this.ctx = context;
        init();
    }

    public PreImageEditText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.ctx = context;
        init();
    }

    public PreImageEditText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.ctx = context;
        init();
    }

    private void init() {
    }


}
