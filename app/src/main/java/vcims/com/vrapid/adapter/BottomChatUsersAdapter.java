package vcims.com.vrapid.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import vcims.com.vrapid.R;
import vcims.com.vrapid.models.ChatModel;

public class BottomChatUsersAdapter extends RecyclerView.Adapter<BottomChatUsersAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ChatModel> users;
    private OnAdapterEvent listener;

    public BottomChatUsersAdapter(Context context, ArrayList<ChatModel> users) {
        this.context = context;
        this.users = users;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.bottom_chat_custom_user_row, parent, false);
        ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(holder.getAdapterPosition());
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ChatModel chatModel = users.get(position);
        holder.getBottomChatUserTV().setText(chatModel.getName());
        listener.onBindLastViewHolder(position, holder);

    }

    public void setAdapterListener(OnAdapterEvent listener) {
        this.listener = listener;
    }

    public void add(ChatModel model) {
        users.add(model);
        notifyDataSetChanged();
    }

    public Boolean doesUserExist(int userId) {
        for (int i = 0; i < users.size(); i++) {
            if (userId == users.get(i).getUserId()) {
                return true;
            }
        }
        return false;
    }

    public void remove(int userId) {
        //Todo: Remove this user
        int indexToRemove = -1;
        for (int i = 0; i < users.size(); i++) {
            if (userId == users.get(i).getUserId()) {
                indexToRemove = i;
                break;
            }
        }
        if (indexToRemove != -1) {
            users.remove(indexToRemove);
            notifyDataSetChanged();
        }
    }

    public void removeAll() {
        users.clear();
        notifyDataSetChanged();
    }

    public int getUserId(int position) {
        return users.get(position).getUserId();
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public interface OnAdapterEvent {
        void onBindLastViewHolder(int position, ViewHolder holder);

        void onItemClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView bottomChatUserTV, newMessageNotiTV;
        private LinearLayout bottomChatUserLL;

        public ViewHolder(View itemView) {
            super(itemView);
            bottomChatUserTV = itemView.findViewById(R.id.bottomChatUserTV);
            newMessageNotiTV = itemView.findViewById(R.id.newMessageNotiTV);
            bottomChatUserLL = itemView.findViewById(R.id.bottomChatUserLL);
        }

        public TextView getBottomChatUserTV() {
            return bottomChatUserTV;
        }

        public void setBottomChatUserTV(TextView bottomChatUserTV) {
            this.bottomChatUserTV = bottomChatUserTV;
        }

        public TextView getNewMessageNotiTV() {
            return newMessageNotiTV;
        }

        public void setNewMessageNotiTV(TextView newMessageNotiTV) {
            this.newMessageNotiTV = newMessageNotiTV;
        }

        public LinearLayout getBottomChatUserLL() {
            return bottomChatUserLL;
        }

        public void setBottomChatUserLL(LinearLayout bottomChatUserLL) {
            this.bottomChatUserLL = bottomChatUserLL;
        }
    }
}
