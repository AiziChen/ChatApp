package quanye.org.chatapp.tools;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 工具类
 * @author Quanyec
 */
public class $ {

    private static OkHttpClient client;


    /**
     * 请求无参数的Http请求
     * @param url  URL
     * @return  返回内容
     */
    @Nullable
    public static String requestNoArgsContent(String url) {
        if (client == null) {
            client = new OkHttpClient();
        }
        Request request = new Request.Builder().url(url).build();
        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            return null;
        }
    }


    /**
     * 请求带参数的HTTP请求
     * @param url  URL
     * @param args  Map类型的参数
     * @return  请求返回的内容
     */
    @Nullable
    public static String requestArgs(String url, Map<String, String> args) {
        if (client == null) {
            client = new OkHttpClient();
        }
        FormBody.Builder builder = new FormBody.Builder();
        for (String key : args.keySet()) {
            builder.add(key, args.get(key));
        }
        FormBody formBody = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            return null;
        }
    }


    public static boolean isNetWorkConnected(@NotNull Context c) {
        ConnectivityManager manager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null) {
            return info.isAvailable() && info.isConnectedOrConnecting();
        } else {
            return false;
        }
    }


    public static void copyToClipBoard(Activity activity, String text) {
        ClipboardManager clipboardManager = (ClipboardManager)activity.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setText(text);
    }
}
