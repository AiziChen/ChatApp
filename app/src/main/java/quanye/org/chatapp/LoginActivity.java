package quanye.org.chatapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import quanye.org.chatapp.base.BaseActivity;
import quanye.org.chatapp.customview.MToast;
import quanye.org.chatapp.customview.PasswordTextView;
import quanye.org.chatapp.tools.$;

/**
 * 登录界面Activity
 *
 * @author Quanyec
 */
public class LoginActivity extends BaseActivity {

    public static final String PREFERENCES_NAME = LoginActivity.class.getPackage().getName() + ".preferences_name";
    public static final String USER_NAME = "user_name";
    public static final String USER_PASSWORD = "user_password";
    public final static String USER_TOKEN = "user_token";
    public final static String URL = "http://www.quanye.xyz:8080";

    private EditText etUserName;
    private EditText etPasswd;
    private Button btnLogin;
    private EditText etValidate;
    private PasswordTextView pwtvValidate;
    private CheckBox cbRemember;

    public static OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setTitle(R.string.user_login);

        etUserName = findViewById(R.id.etUserName);
        etPasswd = findViewById(R.id.etPasswd);
        btnLogin = findViewById(R.id.btnLogin);
        etValidate = findViewById(R.id.etValidate);
        pwtvValidate = findViewById(R.id.pwtvValidate);
        cbRemember = findViewById(R.id.cbRemember);

        // 若读取的USER_TOKEN和USER_NAME有内容，则直接进入聊天界面
        String userToken = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE).getString(USER_TOKEN, "");
        String userName = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE).getString(USER_NAME, "");
        if (!userToken.isEmpty() && !userName.isEmpty()) {
            ChatActivity.startAction(this, userName);
            finish();
            return;
        }

        initListener();

        resetData();
    }


    private void initListener() {
        cbRemember.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                SharedPreferences.Editor editor = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE).edit();
                editor.putString(USER_NAME, etUserName.getText().toString());
                editor.putString(USER_PASSWORD, etPasswd.getText().toString());
                editor.apply();
            } else {
                SharedPreferences.Editor editor = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE).edit();
                editor.putString(USER_NAME, "");
                editor.putString(USER_PASSWORD, "");
                editor.apply();
            }
        });
    }


    private void resetData() {
        String userName = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE).getString(USER_NAME, "");
        String password = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE).getString(USER_PASSWORD, "");
        etUserName.setText(userName);
        etPasswd.setText(password);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.login_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_register) {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * 登录
     * # from btnLogin
     *
     * @param view
     */
    public void onSubmit(View view) {

        if (!$.isNetWorkConnected(this)) {
            MToast.showShortTimeToast(this, "登录失败，请检查网络连接状态");
            return;
        }

        String userName = etUserName.getText().toString();
        String passwd = etPasswd.getText().toString();
        String icode = etValidate.getText().toString();
        if (userName.equals("") || passwd.equals("")) {
            MToast.showShortTimeToast(this, "用户名或密码不能为空");
            return;
        }

        String tcode = pwtvValidate.getValidateCode();
        if (!icode.equals(tcode)) {
            MToast.showShortTimeToast(this, "验证码错误");
            pwtvValidate.updateValidateCode();
            return;
        }


        new LoginAsyncTask().execute(userName, passwd);
    }


    /**
     * 验证登录的异步请求
     */
    private class LoginAsyncTask extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            String userName = strings[0];
            String passwd = strings[1];
            FormBody formBody = new FormBody.Builder()
                    .add("userName", userName)
                    .add("passwd", passwd)
                    .build();
            Request request = new Request.Builder()
                    .url(URL + "/login")
                    .post(formBody)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                ResponseBody body = response.body();
                if (body != null && body.string().equals("true")) {
                    LoginActivity.this.saveUserToken();
                    LoginActivity.this.saveUserName(userName);
                    return true;
                } else {
                    runOnUiThread(() -> MToast.showLongTimeToast(LoginActivity.this, "登录失败，用户密码或密码不正确"));
                    return false;
                }
            } catch (IOException e) {
                runOnUiThread(() -> MToast.showLongTimeToast(LoginActivity.this, "服务器连接失败"));
                return false;
            }
        }

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = ProgressDialog.show(LoginActivity.this, "", "登录中...", true, false);
            pd.show();
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            pd.dismiss();
            if (success) {
                String userName = etUserName.getText().toString();
                MToast.showShortTimeToast(LoginActivity.this, userName + "，欢迎您");
                ChatActivity.startAction(LoginActivity.this, userName);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Save Server USER_TOKEN to local preferences
     */
    private void saveUserToken() {
        // 读取token，若已经包含某个token，则说明已经登录过了，直接跳转到ChatActivity
        String tokenText = $.requestNoArgsContent(URL + "/getUserToken");
        if (tokenText != null) {
            getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE).edit().putString(USER_TOKEN, tokenText).apply();
        }
    }

    private void saveUserName(String userName) {
        getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE).edit().putString(USER_NAME, userName).apply();
    }


    public static void startAction(Activity activity) {
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
    }
}
