package vcims.com.vrapid.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.varenia.kenante_chat.core.KenanteAttachment;
import com.varenia.kenante_chat.core.KenanteChatMessage;

import java.util.List;

import vcims.com.vrapid.R;

public class NewChatAdapter extends RecyclerView.Adapter<NewChatAdapter.ViewHolder> {

    Context context;
    List<KenanteChatMessage> messages;
    int currentUserId;

    public NewChatAdapter(Context context, List<KenanteChatMessage> messages, int currentUserId) {
        this.context = context;
        this.messages = messages;
        this.currentUserId = currentUserId;
    }

    @Override
    public NewChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.full_chat_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NewChatAdapter.ViewHolder holder, int position) {
        KenanteChatMessage chatMessage = messages.get(position);
        holder.fullChatImageIV.setVisibility(View.VISIBLE);
        holder.fullChatMessageTV.setVisibility(View.VISIBLE);
        int userId = chatMessage.getSenderId();
        setPosition(userId, holder.fullChatMessageTV, holder.fullChatImageIV, chatMessage);

        /*holder.fullChatImageIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog enlargedImage = new Dialog(context);
                ImageView image = new ImageView(context);
                image.setScaleType(ImageView.ScaleType.FIT_XY);
                image.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                if(chatMessage.getAttachments()!=null)
                    for (QBAttachment attachment : chatMessage.getAttachments()) {
                        String url = attachment.getUrl();
                        Picasso.with(context).load(url).into(image);
                    }
                enlargedImage.setContentView(image);
                enlargedImage.show();
            }
        });*/
    }

    private void setPosition(int user, TextView textView, ImageView imageView, KenanteChatMessage message) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        if (user == currentUserId) {
            params.gravity = Gravity.END;
            if (!message.getMessage().equals("") && !message.getMessage().equals("null")) {
                textView.setLayoutParams(params);
                textView.setText(message.getMessage());
                imageView.setVisibility(View.GONE);
            }
            else if(message.getAttachments() != null && message.getAttachments().size() > 0){
                params.width = imageView.getLayoutParams().width;
                params.height = imageView.getLayoutParams().height;
                imageView.setLayoutParams(params);
                for (KenanteAttachment attachment : message.getAttachments()) {
                    String url = attachment.getUrl();
                    Picasso.with(context).load(url).into(imageView);
                }
                textView.setVisibility(View.GONE);
            }
        } else {
            //Append User Type also
            params.gravity = Gravity.START;
            if (!message.getMessage().equals("") && !message.getMessage().equals("null")) {
                textView.setText(message.getMessage());
                textView.setLayoutParams(params);
                imageView.setVisibility(View.GONE);
            }
            else if(message.getAttachments() != null && message.getAttachments().size() > 0){
                params.width = imageView.getLayoutParams().width;
                params.height = imageView.getLayoutParams().height;
                imageView.setLayoutParams(params);
                for (KenanteAttachment attachment : message.getAttachments()) {
                    String url = attachment.getUrl();
                    Picasso.with(context).load(url).into(imageView);
                }
                textView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void addItem(KenanteChatMessage message) {
        if(messages==null || message == null)
            return;
        if (messages.size() == 0)
            messages.add(message);
        else {
            //Getting first item
            KenanteChatMessage firstMessage;
            if (messages.size() == 1)
                firstMessage = messages.get(0);
            else
                firstMessage = messages.get(1);
            messages.clear();
            messages.add(firstMessage);
            messages.add(message);
        }
        notifyDataSetChanged();
    }

    public void removeAll(){
        if(messages==null)
            return;
        messages.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<KenanteChatMessage> message) {
        if(messages==null || message == null)
            return;
        messages.addAll(0, message);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView fullChatMessageTV;
        private ImageView fullChatImageIV;

        public ViewHolder(View itemView) {
            super(itemView);
            fullChatMessageTV = itemView.findViewById(R.id.fullChatMessageTV);
            fullChatImageIV = itemView.findViewById(R.id.fullChatImageIV);
        }
    }
}
