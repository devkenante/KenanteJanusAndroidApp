package vcims.com.vrapid.adapter;

import android.app.Dialog;
import android.content.Context;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.varenia.kenante_chat.core.KenanteAttachment;
import com.varenia.kenante_chat.core.KenanteChatMessage;

import java.util.ArrayList;

import vcims.com.vrapid.R;
import vcims.com.vrapid.database.DBHelper;

public class FullChatAdapter extends RecyclerView.Adapter<FullChatAdapter.ViewHolder> {

    public int currentUserId;
    private Context context;
    private ArrayList<KenanteChatMessage> messages;
    private OnChatEvents listener;
    private DBHelper db;

    public FullChatAdapter(Context context, ArrayList<KenanteChatMessage> messages, int currentUserId, DBHelper db) {
        this.context = context;
        this.messages = messages;
        this.currentUserId = currentUserId;
        this.db = db;
        setHasStableIds(true);
    }

    public void setChatListener(OnChatEvents listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.full_chat_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.fullChatImageIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onChatItemClick(1, viewHolder.getAdapterPosition(), viewHolder);
            }
        });
        viewHolder.docCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onChatItemClick(2, viewHolder.getAdapterPosition(), viewHolder);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.getFullChatImageIV().setVisibility(View.VISIBLE);
        holder.getFullChatMessageTV().setVisibility(View.VISIBLE);
        holder.getDocCV().setVisibility(View.GONE);
        //holder.getAttachmentPB().setVisibility(View.GONE);
        //holder.getAttachmentDownloadedIV().setVisibility(View.GONE);
        KenanteChatMessage chatMessage = messages.get(position);
        int userId = chatMessage.getSenderId();
        setPosition(userId, holder, chatMessage);
        holder.fullChatImageIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog enlargedImage = new Dialog(context);
                ImageView image = new ImageView(context);
                image.setScaleType(ImageView.ScaleType.FIT_XY);
                image.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                if (chatMessage.getAttachments().size() > 0)
                    for (KenanteAttachment attachment : chatMessage.getAttachments()) {
                        String url = attachment.getUrl();
                        Picasso.with(context).load(url).into(image);
                    }
                enlargedImage.setContentView(image);
                enlargedImage.show();
            }
        });
    }

    private void setPosition(int user, ViewHolder holder, KenanteChatMessage message) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        if (user == currentUserId) {
            params.gravity = Gravity.END;
            if (!message.getMessage().equals("") && !message.getMessage().equals("null")) {
                holder.getFullChatMessageTV().setLayoutParams(params);
                holder.getFullChatMessageTV().setText(message.getMessage());
                holder.getFullChatImageIV().setVisibility(View.GONE);
                holder.getDocCV().setVisibility(View.GONE);
            } else if (message.getAttachments().size() > 0) {
                LinearLayout.LayoutParams docParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                docParams.gravity = Gravity.END;
                holder.getDocCV().setLayoutParams(docParams);
                params.width = holder.fullChatImageIV.getLayoutParams().width;
                params.height = holder.fullChatImageIV.getLayoutParams().height;
                holder.getFullChatImageIV().setLayoutParams(params);
                for (KenanteAttachment attachment : message.getAttachments()) {
                    String url = attachment.getUrl();
                    if (attachment.getExtension().equals("doc") || attachment.getExtension().equals("docx") || attachment.getExtension().equals("pdf")
                            || attachment.getExtension().equals("ppt") || attachment.getExtension().equals("pptx")) {
                        holder.getDocCV().setVisibility(View.VISIBLE);
                        holder.getFullChatImageIV().setVisibility(View.GONE);
                        holder.getDocFileTV().setText(attachment.getName());
                        //Todo: Uncomment this code
                        /*String localUri = db.getAttachmentLocalURI(attachment.getName());
                        if (!localUri.equals("")) {
                            holder.getAttachmentDownloadedIV().setVisibility(View.VISIBLE);
                            holder.setFileUrl(localUri);
                        }*/
                        holder.getAttachmentDownloadedIV().setVisibility(View.VISIBLE);
                    } else if(attachment.getExtension().equals("png")||attachment.getExtension().equals("jpg")||attachment.getExtension().equals("jpeg")){
                        holder.getDocCV().setVisibility(View.GONE);
                        Picasso.with(context).load(url).into(holder.getFullChatImageIV());
                    }
                }
                holder.getFullChatMessageTV().setVisibility(View.GONE);
            }
        } else {
            //Append User Type also
            params.gravity = Gravity.START;
            if (!message.getMessage().equals("") && !message.getMessage().equals("null")) {
                holder.getFullChatMessageTV().setText(message.getMessage());
                holder.getFullChatMessageTV().setLayoutParams(params);
                holder.getFullChatImageIV().setVisibility(View.GONE);
            } else if (message.getAttachments().size() > 0) {
                params.width = holder.fullChatImageIV.getLayoutParams().width;
                params.height = holder.fullChatImageIV.getLayoutParams().height;
                holder.getFullChatImageIV().setLayoutParams(params);
                LinearLayout.LayoutParams docParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                docParams.gravity = Gravity.START;
                holder.getDocCV().setLayoutParams(docParams);
                for (KenanteAttachment attachment : message.getAttachments()) {
                    String url = attachment.getUrl();
                    if (attachment.getExtension().equals("doc") || attachment.getExtension().equals("docx") || attachment.getExtension().equals("pdf")
                            || attachment.getExtension().equals("ppt") || attachment.getExtension().equals("pptx")) {
                        holder.getDocCV().setVisibility(View.VISIBLE);
                        holder.getFullChatImageIV().setVisibility(View.GONE);
                        holder.getDocFileTV().setText(attachment.getName());
                        //Todo: Uncomment this code
                        /*String localUri = db.getAttachmentLocalURI(attachment.getName());
                        if (!localUri.equals("")) {
                            holder.getAttachmentDownloadedIV().setVisibility(View.VISIBLE);
                            holder.setFileUrl(localUri);
                        }*/
                        holder.getAttachmentDownloadedIV().setVisibility(View.VISIBLE);
                    } else if(attachment.getExtension().equals("png")||attachment.getExtension().equals("jpg")||attachment.getExtension().equals("jpeg")){
                        holder.getDocCV().setVisibility(View.GONE);
                        Picasso.with(context).load(url).into(holder.getFullChatImageIV());
                    }
                }
                holder.getFullChatMessageTV().setVisibility(View.GONE);
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

    public KenanteChatMessage getChatItem(int position) {
        if (messages.size() != 0)
            return messages.get(position);
        else
            return null;
    }

    public void addMessages(ArrayList<KenanteChatMessage> chatMessages) {
        if (messages == null || chatMessages == null)
            return;
        this.messages = chatMessages;
        notifyDataSetChanged();
    }

    public void addItem(KenanteChatMessage message) {
        if (messages == null || message == null)
            return;
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    public void removeAllItems() {
        if (messages == null)
            return;
        messages.clear();
        notifyDataSetChanged();
    }

    public interface OnChatEvents {
        void onChatItemClick(int type, int position, ViewHolder holder);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView fullChatMessageTV, docFileTV;
        private ImageView fullChatImageIV, attachmentDownloadedIV;
        private ProgressBar attachmentPB;
        private CardView docCV;
        private String fileUrl;

        public ViewHolder(View itemView) {
            super(itemView);
            fullChatMessageTV = itemView.findViewById(R.id.fullChatMessageTV);
            fullChatImageIV = itemView.findViewById(R.id.fullChatImageIV);
            docCV = itemView.findViewById(R.id.docCV);
            docFileTV = itemView.findViewById(R.id.docFileTV);
            attachmentDownloadedIV = itemView.findViewById(R.id.attachmentDownloadedIV);
            attachmentPB = itemView.findViewById(R.id.attachmentPB);
        }

        public TextView getFullChatMessageTV() {
            return fullChatMessageTV;
        }

        public void setFullChatMessageTV(TextView fullChatMessageTV) {
            this.fullChatMessageTV = fullChatMessageTV;
        }

        public TextView getDocFileTV() {
            return docFileTV;
        }

        public void setDocFileTV(TextView docFileTV) {
            this.docFileTV = docFileTV;
        }

        public ImageView getFullChatImageIV() {
            return fullChatImageIV;
        }

        public void setFullChatImageIV(ImageView fullChatImageIV) {
            this.fullChatImageIV = fullChatImageIV;
        }

        public ImageView getAttachmentDownloadedIV() {
            return attachmentDownloadedIV;
        }

        public void setAttachmentDownloadedIV(ImageView attachmentDownloadedIV) {
            this.attachmentDownloadedIV = attachmentDownloadedIV;
        }

        public ProgressBar getAttachmentPB() {
            return attachmentPB;
        }

        public void setAttachmentPB(ProgressBar attachmentPB) {
            this.attachmentPB = attachmentPB;
        }

        public CardView getDocCV() {
            return docCV;
        }

        public void setDocCV(CardView docCV) {
            this.docCV = docCV;
        }

        public String getFileUrl() {
            return fileUrl;
        }

        public void setFileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
        }
    }

}
