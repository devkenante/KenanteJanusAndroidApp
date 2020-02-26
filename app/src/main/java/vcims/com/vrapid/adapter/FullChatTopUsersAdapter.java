package vcims.com.vrapid.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import vcims.com.vrapid.R;
import vcims.com.vrapid.models.ChatModel;

public class FullChatTopUsersAdapter extends RecyclerView.Adapter<FullChatTopUsersAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ChatModel> users;
    private OnAdapterEvents listener;
    private Boolean isChatFragment;

    public FullChatTopUsersAdapter(Context context, ArrayList<ChatModel> users, Boolean isChatFragment){
        this.context = context;
        this.users = users;
        this.isChatFragment = isChatFragment;
    }

    public void setAdapterListener(OnAdapterEvents listener){
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.full_chat_top_users_row, parent, false);
        ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onUserItemClick(holder.getAdapterPosition());
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.getTriangleIV().setVisibility(View.INVISIBLE);
        ChatModel model = users.get(position);
        holder.fullChatTopUserNameTV.setText(model.getName());
        listener.onBindLastViewHolder(position, holder);
    }

    public void addUser(ChatModel model){
        if(users == null)
            return;
        users.add(model);
        notifyItemInserted(users.size() - 1);
    }

    public void removeUser(int index){
        if(users == null)
            return;
        users.remove(index);
        notifyDataSetChanged();
    }

    public int getIndexOfItem(int integer){
        if(users == null)
            return -1;
        for(int i = 0; i < users.size(); i++){
            if(users.get(i).getUserId() == integer)
                return i;
        }
        return -1;
    }

    public Boolean doesUserExist(int userId){
        if(users == null)
            return false;
        for(int i = 0; i < users.size(); i++){
            if(users.get(i).getUserId() == userId)
                return true;
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public interface OnAdapterEvents{
        void onBindLastViewHolder(int position, ViewHolder holder);
        void onUserItemClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView fullChatTopUserNameTV;
        ImageView triangleIV;

        public ViewHolder(View itemView) {
            super(itemView);
            fullChatTopUserNameTV = itemView.findViewById(R.id.fullChatTopUserNameTV);
            triangleIV = itemView.findViewById(R.id.triangleIV);
        }

        public TextView getFullChatTopUserNameTV() {
            return fullChatTopUserNameTV;
        }

        public void setFullChatTopUserNameTV(TextView fullChatTopUserNameTV) {
            this.fullChatTopUserNameTV = fullChatTopUserNameTV;
        }

        public ImageView getTriangleIV() {
            return triangleIV;
        }

        public void setTriangleIV(ImageView triangleIV) {
            this.triangleIV = triangleIV;
        }
    }
}
