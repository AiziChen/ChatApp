package quanye.org.chatapp;

import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.HashMap;
import java.util.Map;

import quanye.org.chatapp.base.BaseActivity;
import quanye.org.chatapp.customview.MToast;
import quanye.org.chatapp.tools.$;

/**
 * 注册界面Activity
 *
 * @author Quanyec
 */
public class RegisterActivity extends BaseActivity {

    private EditText etUserName;
    private EditText etPassword;
    private EditText etAge;
    private EditText etRemark;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();
    }

    private void initView() {
        etUserName = findViewById(R.id.etUserName);
        etPassword = findViewById(R.id.etPasswd);
        etAge = findViewById(R.id.etAge);
        etRemark = findViewById(R.id.etRemark);
        btnRegister = findViewById(R.id.btnRegister);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    /**
     * handler
     *
     * @param view
     */
    public void onRegister(View view) {
        String userName = etUserName.getText().toString();
        String password = etPassword.getText().toString();
        String ageStr = etAge.getText().toString();
        if (userName.isEmpty()) {
            MToast.showLongTimeToast(this, "用户名不能为空");
            return;
        }
        if (password.isEmpty()) {
            MToast.showLongTimeToast(this, "密码不能为空");
            return;
        }
        if (password.length() < 8 || password.length() > 16) {
            MToast.showLongTimeToast(this, "密码必需大于8位，小于16位");
            return;
        }
        if (!ageStr.isEmpty()) {
            int age = Integer.parseInt(ageStr);
            if (age > 200 || age < 3) {
                MToast.showLongTimeToast(this, "年龄不能小于3，且不能大于200");
                return;
            }
        }

        new RegisterAsyncTask().execute();
    }


    private class RegisterAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            Map<String, String> args = new HashMap<>();
            args.put("userName", etUserName.getText().toString());
            args.put("passwd", etPassword.getText().toString());
            args.put("age", etAge.getText().toString());
            args.put("remark", etRemark.getText().toString());
            String responseText = $.requestArgs(LoginActivity.URL + "/register", args);
            if (responseText == null) {
                return Boolean.toString(false);
            } else {
                return responseText;
            }
        }

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = ProgressDialog.show(RegisterActivity.this, "", "注册中...", true, false);
        }

        @Override
        protected void onPostExecute(String responseText) {
            super.onPostExecute(responseText);
            pd.dismiss();
            switch (responseText) {
                case "true":
                    MToast.showShortTimeToast(RegisterActivity.this, "注册成功");
                    finish();
                    break;
                case "multi":
                    MToast.showShortTimeToast(RegisterActivity.this, "注册失败，已存在该用户名");
                    break;
                default:
                    MToast.showShortTimeToast(RegisterActivity.this, "注册失败，请检查网络状态");
                    break;
            }
        }
    }
}
