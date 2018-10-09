package quanye.org.chatapp.customview;

import android.content.Context;
import android.widget.Toast;

/**
 * 只显示一次的Toast工具类
 *
 * @author Quanyec
 */
public class MToast {

    private static Toast toast;

    public static void showShortTimeToast(Context context, CharSequence msg) {
        if (toast == null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }

    public static void showLongTimeToast(Context context, CharSequence msg) {
        if (toast == null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }


    public static void showShortTimeToast(Context context, int msgId) {
        if (toast == null) {
            toast = Toast.makeText(context, msgId, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msgId);
        }
        toast.show();
    }


    public static void showLongTimeToast(Context context, int msgId) {
        if (toast == null) {
            toast = Toast.makeText(context, msgId, Toast.LENGTH_LONG);
        } else {
            toast.setText(msgId);
        }
        toast.show();
    }
}
