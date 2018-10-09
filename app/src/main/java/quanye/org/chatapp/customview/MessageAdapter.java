package quanye.org.chatapp.customview;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import me.wangyuwei.flipshare.FlipShareView;
import me.wangyuwei.flipshare.ShareItem;
import quanye.org.chatapp.R;
import quanye.org.chatapp.domain.Message;
import quanye.org.chatapp.tools.$;

/**
 * @author Quanyec
 */
public class MessageAdapter extends BaseAdapter {

    private Activity activity;
    private List<Message> messageList;
    private LayoutInflater inflater;

    public MessageAdapter(Activity activity, List<Message> messageList) {
        super();
        this.activity = activity;
        this.messageList = messageList;
        this.inflater = LayoutInflater.from(activity);
    }

    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public Object getItem(int position) {
        return messageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.message_item, null);

            holder.itemLayout = convertView.findViewById(R.id.itemLayout);
            holder.userName = convertView.findViewById(R.id.tv_userName);
            holder.message = convertView.findViewById(R.id.tv_message);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.userName.setText(messageList.get(position).getUserName());
        holder.message.setText(messageList.get(position).getContent());
        holder.itemLayout.setOnLongClickListener(v -> {
            FlipShareView shareView = new FlipShareView.Builder(activity, holder.message)
                    .addItem(new ShareItem("复制", Color.WHITE, Color.BLACK))
                    .create();
            shareView.setOnFlipClickListener(new FlipShareView.OnFlipClickListener() {
                @Override
                public void onItemClick(int position) {
                    $.copyToClipBoard(activity, holder.message.getText().toString());
                    MToast.showShortTimeToast(activity, "复制成功");
                }
                @Override
                public void dismiss() {}
            });
            return false;
        });

        return convertView;
    }


    private class ViewHolder {
        public ConstraintLayout itemLayout;
        public TextView userName;
        public TextView message;
    }
}
