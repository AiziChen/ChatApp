package quanye.org.chatapp.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import quanye.org.chatapp.domain.Message;

/**
 * 聊天客户端Service，因为需要后台接收信息，因此不能采用IntentService来处理耗时任务
 *
 * @author Quanyec
 */
public class ChatService extends Service {

    private static final String TAG = "ChatService";
    private static final String HOST = "www.quanye.xyz";
    public static final int MESSAGE_MAX_SIZE = 1024;

    private ChatListener chatListener;
    private ChatBinder binder = new ChatBinder();
    private static Socket socket;
    private static ClientThread clientThread;
    private static Gson gson = new Gson();

    // Chat message List
    public static List<Message> msgList = new ArrayList<>();

    public interface ChatListener {
        void onMessage(Message msg);
    }

    public class ChatBinder extends Binder {
        public void onChatListener(ChatListener listener) {
            ChatService.this.chatListener = listener;
        }

        public void sendMessage(Message message) {
            if (socket != null) {
                new Thread(() -> ChatService.this.sendMessage(message)).start();
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (socket == null) {
            new Thread(() -> {
                try {
                    socket = new Socket(HOST, 3000);
                    // 监听服务器输入流的线程
                    clientThread = new ClientThread(socket);
                    clientThread.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }


    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (clientThread != null) {
                clientThread.interrupt();
            }
            if (socket != null) {
                socket.close();
                socket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 发送网络数据
     *
     * @param message
     */
    private void sendMessage(Message message) {
        try {
            PrintStream ps = new PrintStream(socket.getOutputStream(), true, "UTF-8");
            ps.println(gson.toJson(message));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 服务器端监听线程
     */
    private class ClientThread extends Thread {
        private Socket socket;
        private BufferedReader br;

        ClientThread(Socket socket) throws IOException {
            this.socket = socket;
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }

        @Override
        public void run() {
            try {
                int hasRead = 0;
                char[] chars = new char[MESSAGE_MAX_SIZE];
                while ((hasRead = br.read(chars)) > 0) {
                    String messageJson = new String(chars, 0, hasRead);
                    try {
                        Message message = gson.fromJson(messageJson, Message.class);
                        if (message != null) {
                            msgList.add(message);
                            chatListener.onMessage(message);
                        }
                    }  catch (JsonSyntaxException e) {
                        Message message = new Message("系统", "用户发送过来的信息格式有误");
                        msgList.add(message);
                        chatListener.onMessage(message);
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            String messageJson = "{userName: 'david', content: '23'}";
            Log.i(TAG, messageJson);
            Message message = gson.fromJson(messageJson, Message.class);
            Log.i(TAG, message.toString());
            msgList.add(message);
            chatListener.onMessage(message);
        }
    }

}
