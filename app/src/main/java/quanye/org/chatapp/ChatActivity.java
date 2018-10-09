package quanye.org.chatapp;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import quanye.org.chatapp.base.BaseActivity;
import quanye.org.chatapp.customview.MToast;
import quanye.org.chatapp.customview.MessageAdapter;
import quanye.org.chatapp.domain.Message;
import quanye.org.chatapp.service.ChatService;

import static quanye.org.chatapp.LoginActivity.PREFERENCES_NAME;

/**
 * 聊天界面Activity
 *
 * @author Quqnyec
 */
public class ChatActivity extends BaseActivity {

    private static final String TAG = "ChatActivity";

    private String userName;
    private ListView listView;
    private EditText etContent;
    private Button btnSend;
    private MessageAdapter adapter;
    private List<Message> messageList = new ArrayList<>();
    private Intent chatServiceIntent;

    private ChatService.ChatBinder binder;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (ChatService.ChatBinder) service;
            // 处理被动接收到的数据
            binder.onChatListener((final Message msg) -> {
                // 更新listView列表
                runOnUiThread(() -> {
                    if (msg != null) {
                        messageList.add(msg);
                        adapter.notifyDataSetChanged();
                        listView.setSelection(listView.getBottom());
                        etContent.setText("");
                        btnSend.setEnabled(true);
                        btnSend.setText(R.string.send);     // 显示“发送”
                    } else {
                        MToast.showShortTimeToast(ChatActivity.this, R.string.send_failure);
                    }
                });
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        if (intent != null) {
            userName = intent.getStringExtra(LoginActivity.USER_NAME);
        } else {
            MToast.showShortTimeToast(this, "读取用户名出错");
            return;
        }

        etContent = findViewById(R.id.etContent);
        btnSend = findViewById(R.id.btnSend);
        listView = findViewById(R.id.listView);

        adapter = new MessageAdapter(this, messageList);
        listView.setAdapter(adapter);

        chatServiceIntent = new Intent(this, ChatService.class);
        startService(chatServiceIntent);
        bindService(chatServiceIntent, serviceConnection, BIND_AUTO_CREATE);

        // 处理发送
        btnSend.setOnClickListener(v -> {
            String content = etContent.getText().toString();
            if (!content.trim().isEmpty()) {
                btnSend.setText(R.string.sending);  // 显示“发送中...”
                binder.sendMessage(new Message(userName, content));
                btnSend.setEnabled(false);
            } else if (content.length() > ChatService.MESSAGE_MAX_SIZE) {
                MToast.showShortTimeToast(ChatActivity.this, "文本超过最大限制");
            } else {
                MToast.showShortTimeToast(ChatActivity.this, R.string.content_dont_empty);
            }
        });

        loadHistoryMessageList();
    }

    private void loadHistoryMessageList() {
        messageList.addAll(ChatService.msgList);
        if (ChatService.msgList.size() > 0) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (binder != null && binder.isBinderAlive()) {
            unbindService(serviceConnection);
        }
    }

    public static void startAction(LoginActivity activity, String userName) {
        Intent intent = new Intent(activity, ChatActivity.class);
        intent.putExtra(LoginActivity.USER_NAME, userName);
        activity.startActivity(intent);
        activity.finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_clean_talkList) {
            ChatService.msgList.clear();
            messageList.clear();
            adapter.notifyDataSetChanged();
            MToast.showShortTimeToast(this, "消息已清空");
        }
        if (item.getItemId() == R.id.menu_logout) {
            // 清空USER_TOKEN
            getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE).edit().putString(LoginActivity.USER_TOKEN, "").apply();
            new LogoutAsyncTask().execute();
        }
        return super.onOptionsItemSelected(item);
    }


    private class LogoutAsyncTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(LoginActivity.URL + "/logout")
                    .build();
            try {
                client.newCall(request).execute();
                return true;
            } catch (IOException e) {
                return false;
            }
        }

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = ProgressDialog.show(ChatActivity.this, "", "正在登出", false, true);
            pd.show();
        }

        @Override
        protected void onPostExecute(Boolean isLogout) {
            super.onPostExecute(isLogout);
            pd.dismiss();
            if (!isLogout) {
                MToast.showShortTimeToast(ChatActivity.this, "登出失败");
            } else {
                LoginActivity.startAction(ChatActivity.this);
                ChatActivity.this.finish();
                stopService(chatServiceIntent);
            }
        }
    }
}
